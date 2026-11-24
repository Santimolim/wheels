package wheels.vistas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;

/**
 * Layouts adaptables (responsive) de Wheels.
 *  - Columna: apila componentes con un ancho maximo, centrada en la ventana.
 *  - Rejilla: reparte tarjetas en 1, 2 o 3 columnas segun el ancho disponible.
 *  - FilaFlexible: pone elementos en fila y salta de linea cuando no caben.
 * Asi la interfaz se reorganiza sola cuando la ventana cambia de tamano.
 */
public final class Diseno {

    private Diseno() { }

    /** Componente cuyo alto depende del ancho que se le de (texto que salta de linea, rejillas...). */
    public interface AltoSegunAncho {
        int altoPara(int ancho);
    }

    static int alto(Component c, int ancho) {
        if (!c.isVisible()) return 0;
        if (c instanceof AltoSegunAncho) return ((AltoSegunAncho) c).altoPara(ancho);
        return c.getPreferredSize().height;
    }

    /** Espacio vertical fijo. */
    public static JComponent espacio(int alto) {
        JComponent c = new JComponent() { };
        c.setPreferredSize(new Dimension(1, alto));
        return c;
    }

    // ==================================================================
    //  Columna
    // ==================================================================

    /**
     * Columna centrada con ancho maximo. Si esta dentro de un JScrollPane
     * se ajusta al ancho visible (sin barra horizontal) y crece hacia abajo.
     */
    public static class Columna extends JPanel implements Scrollable, AltoSegunAncho {
        private int anchoMax;
        private final int margenX;
        private final int margenY;
        private int separacion = 0;

        public Columna(int anchoMax, int margenX, int margenY) {
            this.anchoMax = anchoMax;
            this.margenX = margenX;
            this.margenY = margenY;
            setOpaque(false);
            setLayout(new Layout());
        }

        private boolean centrarVertical = false;

        /** Espacio automatico entre todos los elementos. */
        public Columna conSeparacion(int s) { separacion = s; return this; }

        /** Si sobra alto, el contenido se centra verticalmente. */
        public Columna centrada() { centrarVertical = true; return this; }
        public void setAnchoMax(int a) { anchoMax = a; revalidate(); }

        public Component agregar(Component c) { add(c); return c; }
        public void espacio(int alto) { add(Diseno.espacio(alto)); }

        private int anchoReferencia() {
            if (getParent() instanceof JViewport) return getParent().getWidth();
            return getWidth() > 0 ? getWidth() : anchoMax + margenX * 2;
        }

        private int anchoContenido(int total) {
            return Math.max(0, Math.min(anchoMax, total - margenX * 2));
        }

        @Override public int altoPara(int ancho) {
            int w = anchoContenido(ancho);
            int h = margenY * 2;
            int n = 0;
            for (Component c : getComponents()) {
                if (!c.isVisible()) continue;
                if (n++ > 0) h += separacion;
                h += alto(c, w);
            }
            return h;
        }

        @Override public Dimension getPreferredSize() {
            int ref = anchoReferencia();
            return new Dimension(Math.min(ref, anchoMax + margenX * 2), altoPara(ref));
        }

        @Override public Dimension getMinimumSize() { return new Dimension(0, 0); }

        private class Layout implements LayoutManager {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension preferredLayoutSize(Container p) { return getPreferredSize(); }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(0, 0); }
            @Override public void layoutContainer(Container p) {
                int w = anchoContenido(p.getWidth());
                int x = (p.getWidth() - w) / 2;
                int y = margenY;
                if (centrarVertical) {
                    int total = altoPara(p.getWidth());
                    if (p.getHeight() > total) y += (p.getHeight() - total) / 2;
                }
                int n = 0;
                for (Component c : p.getComponents()) {
                    if (!c.isVisible()) continue;
                    if (n++ > 0) y += separacion;
                    int h = alto(c, w);
                    c.setBounds(x, y, w, h);
                    y += h;
                }
            }
        }

        // ---- Scrollable: ocupa todo el ancho visible y desplaza en vertical ----
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return Math.max(48, r.height - 48); }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() {
            return getParent() instanceof JViewport && getParent().getHeight() > altoPara(getParent().getWidth());
        }
    }

    // ==================================================================
    //  Rejilla
    // ==================================================================

    /** Rejilla adaptable: tantas columnas como quepan con el ancho minimo indicado. */
    public static class Rejilla extends JPanel implements AltoSegunAncho {
        private final int anchoMinCol;
        private final int maxCols;
        private final int gapX;
        private final int gapY;
        private boolean igualarAlto = true;

        public Rejilla(int anchoMinCol, int maxCols, int gapX, int gapY) {
            this.anchoMinCol = anchoMinCol;
            this.maxCols = maxCols;
            this.gapX = gapX;
            this.gapY = gapY;
            setOpaque(false);
            setLayout(new Layout());
        }

        private int corte = 0;

        /** false = cada elemento conserva su propio alto (columnas de distinto largo). */
        public Rejilla igualarAlto(boolean b) { igualarAlto = b; return this; }

        /** Usa todas las columnas solo si el ancho llega a este valor; si no, una sola columna. */
        public Rejilla conCorte(int anchoMinimo) { corte = anchoMinimo; return this; }

        int columnas(int ancho) {
            if (corte > 0) return ancho >= corte ? maxCols : 1;
            int n = (ancho + gapX) / (anchoMinCol + gapX);
            return Math.max(1, Math.min(maxCols, n));
        }

        private int anchoCol(int ancho, int cols) {
            return (ancho - gapX * (cols - 1)) / cols;
        }

        private Component[] visibles() {
            return java.util.Arrays.stream(getComponents()).filter(Component::isVisible).toArray(Component[]::new);
        }

        @Override public int altoPara(int ancho) {
            Component[] cs = visibles();
            if (cs.length == 0) return 0;
            int cols = columnas(ancho);
            int cw = anchoCol(ancho, cols);
            int h = 0;
            for (int i = 0; i < cs.length; i += cols) {
                int fila = 0;
                for (int j = i; j < Math.min(i + cols, cs.length); j++) fila = Math.max(fila, alto(cs[j], cw));
                h += fila + (i > 0 ? gapY : 0);
            }
            return h;
        }

        @Override public Dimension getPreferredSize() {
            int w = getWidth() > 0 ? getWidth() : anchoMinCol;
            return new Dimension(w, altoPara(w));
        }

        private class Layout implements LayoutManager {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension preferredLayoutSize(Container p) { return getPreferredSize(); }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(0, 0); }
            @Override public void layoutContainer(Container p) {
                Component[] cs = visibles();
                int ancho = p.getWidth();
                int cols = columnas(ancho);
                int cw = anchoCol(ancho, cols);
                int y = 0;
                for (int i = 0; i < cs.length; i += cols) {
                    int fila = 0;
                    for (int j = i; j < Math.min(i + cols, cs.length); j++) fila = Math.max(fila, alto(cs[j], cw));
                    for (int j = i; j < Math.min(i + cols, cs.length); j++) {
                        int x = (j - i) * (cw + gapX);
                        int ultimo = (j - i == cols - 1) ? ancho - x : cw;   // la ultima columna absorbe el redondeo
                        cs[j].setBounds(x, y, ultimo, igualarAlto ? fila : alto(cs[j], cw));
                    }
                    y += fila + gapY;
                }
            }
        }
    }

    // ==================================================================
    //  Fila flexible
    // ==================================================================

    /** Elementos en fila que saltan a la siguiente linea si no caben (chips, botones). */
    public static class FilaFlexible extends JPanel implements AltoSegunAncho {
        private final int gapX;
        private final int gapY;
        private boolean alinearDerecha = false;

        public FilaFlexible(int gapX, int gapY) {
            this.gapX = gapX;
            this.gapY = gapY;
            setOpaque(false);
            setLayout(new Layout());
        }

        public FilaFlexible aLaDerecha() { alinearDerecha = true; return this; }

        @Override public int altoPara(int ancho) {
            int x = 0, y = 0, fila = 0;
            for (Component c : getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                if (x > 0 && x + d.width > ancho) { y += fila + gapY; x = 0; fila = 0; }
                x += d.width + gapX;
                fila = Math.max(fila, d.height);
            }
            return y + fila;
        }

        @Override public Dimension getPreferredSize() {
            int w = 0, h = 0;
            for (Component c : getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                w += d.width + (w > 0 ? gapX : 0);
                h = Math.max(h, d.height);
            }
            if (getWidth() > 0) return new Dimension(w, altoPara(getWidth()));
            return new Dimension(w, h);
        }

        private class Layout implements LayoutManager {
            @Override public void addLayoutComponent(String n, Component c) { }
            @Override public void removeLayoutComponent(Component c) { }
            @Override public Dimension preferredLayoutSize(Container p) { return getPreferredSize(); }
            @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(0, 0); }
            @Override public void layoutContainer(Container p) {
                Insets in = p.getInsets();
                int ancho = p.getWidth() - in.left - in.right;
                java.util.List<Component> linea = new java.util.ArrayList<>();
                int x = 0, y = in.top, fila = 0;
                for (Component c : p.getComponents()) {
                    if (!c.isVisible()) continue;
                    Dimension d = c.getPreferredSize();
                    if (x > 0 && x + d.width > ancho) {
                        colocar(linea, in.left, y, fila, ancho);
                        linea.clear();
                        y += fila + gapY; x = 0; fila = 0;
                    }
                    linea.add(c);
                    x += Math.min(d.width, ancho) + gapX;
                    fila = Math.max(fila, d.height);
                }
                colocar(linea, in.left, y, fila, ancho);
            }
            private void colocar(java.util.List<Component> linea, int x0, int y, int fila, int ancho) {
                int total = 0;
                for (Component c : linea) total += Math.min(c.getPreferredSize().width, ancho) + gapX;
                total -= gapX;
                int x = alinearDerecha ? x0 + Math.max(0, ancho - total) : x0;
                for (Component c : linea) {
                    Dimension d = c.getPreferredSize();
                    int w = Math.min(d.width, ancho);
                    c.setBounds(x, y + (fila - d.height) / 2, w, d.height);
                    x += w + gapX;
                }
            }
        }
    }
}
