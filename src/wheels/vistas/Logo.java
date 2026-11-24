package wheels.vistas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 * Identidad grafica de Wheels.
 * Isotipo: una "W" trazada como una ruta, cuyos dos vertices inferiores
 * son ruedas (color turquesa), sobre un cuadrado redondeado azul noche.
 * Todo es vectorial: se ve nitido en cualquier tamano.
 */
public final class Logo {

    private Logo() { }

    /** Dibuja el isotipo en (x, y) con el tamano indicado. */
    public static void pintarIsotipo(Graphics2D g0, float x, float y, float s) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        g.translate(x, y);
        RoundRectangle2D fondo = new RoundRectangle2D.Float(0, 0, s, s, s * 0.56f, s * 0.56f);
        g.setPaint(new GradientPaint(0, 0, new Color(0x17306B), s, s, Estilo.NAVY));
        g.fill(fondo);
        // brillo sutil arriba
        g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 34), 0, s * 0.6f, new Color(255, 255, 255, 0)));
        g.fill(fondo);

        Path2D w = new Path2D.Float();
        w.moveTo(s * 0.19f, s * 0.29f);
        w.lineTo(s * 0.355f, s * 0.70f);
        w.lineTo(s * 0.50f, s * 0.40f);
        w.lineTo(s * 0.645f, s * 0.70f);
        w.lineTo(s * 0.81f, s * 0.29f);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(s * 0.105f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(w);

        float r = s * 0.105f;
        for (float cx : new float[]{0.355f, 0.645f}) {
            g.setColor(Estilo.NAVY);
            g.fill(new Ellipse2D.Float(s * cx - r - s * 0.03f, s * 0.70f - r - s * 0.03f, (r + s * 0.03f) * 2, (r + s * 0.03f) * 2));
            g.setColor(Estilo.TEAL);
            g.fill(new Ellipse2D.Float(s * cx - r, s * 0.70f - r, r * 2, r * 2));
            g.setColor(Estilo.NAVY);
            float ri = r * 0.38f;
            g.fill(new Ellipse2D.Float(s * cx - ri, s * 0.70f - ri, ri * 2, ri * 2));
        }
        g.dispose();
    }

    /** Isotipo + palabra "Wheels" como componente. */
    public static class Marca extends ComponenteAccesible {
        private final int tam;
        private final Color colorTexto;
        private final boolean conTexto;

        public Marca(int tam, Color colorTexto, boolean conTexto) {
            this.tam = tam;
            this.colorTexto = colorTexto;
            this.conTexto = conTexto;
            setOpaque(false);
            getAccessibleContext().setAccessibleName("Wheels");
        }

        private Font fuente() { return Estilo.fuente(tam * 0.62f, Estilo.NEGRITA); }

        @Override public Dimension getPreferredSize() {
            if (!conTexto) return new Dimension(tam, tam);
            FontMetrics fm = getFontMetrics(fuente());
            return new Dimension(tam + Math.round(tam * 0.32f) + fm.stringWidth("Wheels") + 2, tam);
        }
        @Override public Dimension getMinimumSize() { return getPreferredSize(); }
        @Override public Dimension getMaximumSize() { return getPreferredSize(); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Estilo.suavizar(g);
            pintarIsotipo(g, 0, 0, tam);
            if (conTexto) {
                g.setFont(fuente());
                g.setColor(colorTexto);
                FontMetrics fm = g.getFontMetrics();
                float y = (tam - fm.getHeight()) / 2f + fm.getAscent();
                g.drawString("Wheels", tam + tam * 0.32f, y);
            }
            g.dispose();
        }
    }

    /** Iconos de la ventana en varios tamanos (barra de tareas, Alt+Tab...). */
    public static List<Image> iconosVentana() {
        List<Image> lista = new ArrayList<>();
        for (int s : new int[]{16, 20, 24, 32, 40, 48, 64, 128, 256}) {
            lista.add(imagen(s));
        }
        return lista;
    }

    public static BufferedImage imagen(int s) {
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        pintarIsotipo(g, 0, 0, s);
        g.dispose();
        return img;
    }

    /**
     * Decoracion de fondo para paneles de la marca: una ruta punteada con
     * paradas y ondas suaves. intensidad = 1 normal, mayor = mas visible.
     */
    public static void pintarRutaDecorativa(Graphics2D g0, int w, int h, float intensidad) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int a = Math.min(255, Math.round(22 * intensidad));
        // ondas concentricas
        g.setColor(new Color(20, 184, 166, Math.min(255, Math.round(30 * intensidad))));
        g.fill(new Ellipse2D.Float(w - h * 0.55f, -h * 0.35f, h * 0.9f, h * 0.9f));
        g.setColor(new Color(255, 255, 255, Math.round(a * 0.6f)));
        g.fill(new Ellipse2D.Float(-h * 0.25f, h * 0.62f, h * 0.6f, h * 0.6f));

        // ruta punteada
        Path2D ruta = new Path2D.Float();
        // la ruta corre por el borde inferior y sube por la derecha, lejos del texto
        ruta.moveTo(-20, h * 0.97f);
        ruta.curveTo(w * 0.40f, h * 1.02f, w * 0.70f, h * 0.92f, w * 0.88f, h * 0.70f);
        ruta.curveTo(w * 0.98f, h * 0.56f, w * 0.96f, h * 0.30f, w + 20, h * 0.18f);
        g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{2f, 9f}, 0f));
        g.setColor(new Color(255, 255, 255, Math.min(255, a * 2)));
        g.draw(ruta);

        // paradas
        float[][] paradas = {{w * 0.88f, h * 0.70f}};
        for (float[] p : paradas) {
            g.setColor(new Color(20, 184, 166, Math.min(255, Math.round(150 * intensidad))));
            g.fill(new Ellipse2D.Float(p[0] - 6, p[1] - 6, 12, 12));
            g.setColor(new Color(255, 255, 255, Math.min(255, Math.round(200 * intensidad))));
            g.fill(new Ellipse2D.Float(p[0] - 2.5f, p[1] - 2.5f, 5, 5));
        }
        g.dispose();
    }
}
