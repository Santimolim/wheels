package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Pantallas de acceso (login y registro): en ventana ancha muestra a la
 * izquierda el panel de la marca y a la derecha el formulario; en ventana
 * angosta solo el formulario, con el logo arriba.
 */
class PanelAcceso extends JPanel {

    static final int CORTE = Integer.MAX_VALUE;   // formato celular: el panel de la marca no se muestra

    private final JComponent marca;
    private final JScrollPane formulario;
    private final List<Component> soloAngosto = new ArrayList<>();

    PanelAcceso(Diseno.Columna form) {
        setOpaque(true);
        setBackground(Estilo.BLANCO);
        marca = crearPanelMarca();
        formulario = new JScrollPane(form, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formulario.setBorder(null);
        formulario.getViewport().setBackground(Estilo.BLANCO);
        formulario.getVerticalScrollBar().setUnitIncrement(24);
        add(marca);
        add(formulario);
        setLayout(new Layout());
    }

    /** Componentes que solo se ven cuando el panel de la marca esta oculto (ej. el logo). */
    void soloEnAngosto(Component c) { soloAngosto.add(c); }

    private class Layout implements LayoutManager {
        @Override public void addLayoutComponent(String n, Component c) { }
        @Override public void removeLayoutComponent(Component c) { }
        @Override public Dimension preferredLayoutSize(Container p) { return new Dimension(1000, 700); }
        @Override public Dimension minimumLayoutSize(Container p) { return new Dimension(0, 0); }
        @Override public void layoutContainer(Container p) {
            int w = p.getWidth(), h = p.getHeight();
            boolean ancho = w >= CORTE;
            boolean cambio = marca.isVisible() != ancho;
            marca.setVisible(ancho);
            for (Component c : soloAngosto) c.setVisible(!ancho);
            if (ancho) {
                int mw = Math.max(380, Math.min(620, (int) (w * 0.44)));
                marca.setBounds(0, 0, mw, h);
                formulario.setBounds(mw, 0, w - mw, h);
            } else {
                formulario.setBounds(0, 0, w, h);
            }
            if (cambio) formulario.getViewport().getView().revalidate();
        }
    }

    /** Panel azul con el logo, el mensaje principal y los beneficios. */
    static JComponent crearPanelMarca() {
        Estilo.PanelMarca fondo = new Estilo.PanelMarca(new BorderLayout(), 0);
        Diseno.Columna c = new Diseno.Columna(440, 48, 48).centrada();
        c.agregar(filaIzquierda(new Logo.Marca(42, Estilo.BLANCO, true)));
        c.espacio(56);
        c.agregar(new Estilo.Parrafo("Comparte el camino\na la universidad.", Estilo.fuente(32, Estilo.NEGRITA), Estilo.BLANCO));
        c.espacio(14);
        c.agregar(new Estilo.Parrafo("Encuentra compañeros que van hacia el mismo campus, "
                + "divide los gastos del viaje y llega acompañado.", Estilo.fuente(15, Estilo.REGULAR), new Color(0xC7D2FE)));
        c.espacio(40);
        c.agregar(beneficio(Iconos.Tipo.AHORRO, "Ahorra en cada trayecto",
                "Paga solo tu puesto. El precio lo fija el conductor al publicar."));
        c.espacio(20);
        c.agregar(beneficio(Iconos.Tipo.UNIVERSIDAD, "Solo estudiantes",
                "El registro exige un correo institucional (.edu)."));
        c.espacio(20);
        c.agregar(beneficio(Iconos.Tipo.ESCUDO, "Viaja tranquilo",
                "Chat con el conductor, calificaciones y botón SOS durante el viaje."));
        fondo.add(c, BorderLayout.CENTER);
        return fondo;
    }

    /** Boton de flecha para volver a la pantalla anterior (bienvenida). */
    static JPanel botonVolver() {
        javax.swing.JButton v = Estilo.botonIcono(Iconos.Tipo.FLECHA_IZQ, "Volver", Estilo.TEXTO, 40);
        v.addActionListener(e -> Aplicacion.volver());
        JPanel p = filaIzquierda(v);
        p.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, -10, 0, 0));
        return p;
    }

    static JPanel filaIzquierda(JComponent c) {
        JPanel p = Estilo.panel(new BorderLayout());
        p.add(c, BorderLayout.WEST);
        return p;
    }

    static JComponent beneficio(Iconos.Tipo icono, String titulo, String descripcion) {
        Diseno.Columna textos = new Diseno.Columna(10000, 0, 0).conSeparacion(3);
        textos.agregar(Estilo.texto(titulo, Estilo.fuente(15, Estilo.SEMI), Estilo.BLANCO));
        textos.agregar(new Estilo.Parrafo(descripcion, Estilo.fuente(13.5f, Estilo.REGULAR), new Color(0xA5B4CF)));
        return new FilaIcono(new Estilo.IconoCirculo(icono, new Color(255, 255, 255, 26), Estilo.TEAL, 42), textos);
    }

    /** Icono a la izquierda y columna de texto a la derecha que ajusta su alto. */
    static class FilaIcono extends JPanel implements Diseno.AltoSegunAncho {
        private final JComponent icono;
        private final Diseno.Columna textos;
        FilaIcono(JComponent icono, Diseno.Columna textos) {
            super(null);
            setOpaque(false);
            this.icono = icono;
            this.textos = textos;
            add(icono);
            add(textos);
        }
        @Override public int altoPara(int ancho) {
            java.awt.Insets in = getInsets();
            int w = ancho - in.left - in.right;
            return in.top + in.bottom
                    + Math.max(icono.getPreferredSize().height, textos.altoPara(w - icono.getPreferredSize().width - 16));
        }
        @Override public void doLayout() {
            java.awt.Insets in = getInsets();
            Dimension d = icono.getPreferredSize();
            icono.setBounds(in.left, in.top, d.width, d.height);
            int x = in.left + d.width + 16;
            textos.setBounds(x, in.top, getWidth() - x - in.right, getHeight() - in.top - in.bottom);
        }
    }
}
