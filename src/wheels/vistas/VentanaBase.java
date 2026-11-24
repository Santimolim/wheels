package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * Clase padre de todas las pantallas de Wheels (herencia).
 * Cada pantalla es un panel que se muestra dentro de la ventana unica
 * (Aplicacion). Aqui se arma una sola vez el encabezado, el area con
 * desplazamiento y la navegacion, para no repetir ese codigo.
 */
public class VentanaBase extends JPanel {

    // Secciones de la navegacion principal
    public static final String INICIO = "inicio";
    public static final String VIAJES = "viajes";
    public static final String PUBLICAR = "publicar";
    public static final String AVISOS = "avisos";
    public static final String PERFIL = "perfil";

    /** Ancho maximo del contenido para que se lea bien en pantallas grandes. */
    public static final int ANCHO_CONTENIDO = 1080;

    private final String titulo;
    protected Diseno.Columna cuerpo;
    protected JScrollPane desplazamiento;

    public VentanaBase(String titulo) {
        super(new BorderLayout());
        this.titulo = titulo;
        setBackground(Estilo.FONDO);
    }

    public String getTitulo() { return titulo; }

    /** Seccion del menu que se marca como activa; null = pantalla sin menu (login, bienvenida). */
    public String seccion() { return null; }

    /** true = pantalla principal (reinicia el historial de "Volver"). */
    public boolean esRaiz() { return false; }

    /** Se llama justo despues de mostrar la pantalla (por ejemplo, para poner el foco). */
    public void alMostrar() { }

    /** Se llama al salir de la pantalla: detener temporizadores, etc. */
    public void alSalir() { }

    // ------------------------------------------------------------------
    //  Estructura
    // ------------------------------------------------------------------

    /** Crea el area de contenido con desplazamiento vertical y ancho maximo. */
    protected Diseno.Columna crearCuerpo(int anchoMax) {
        cuerpo = new Diseno.Columna(anchoMax, Estilo.MARGEN, 24);
        desplazamiento = new JScrollPane(cuerpo,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        desplazamiento.setBorder(null);
        desplazamiento.setOpaque(false);
        desplazamiento.getViewport().setBackground(Estilo.FONDO);
        desplazamiento.getVerticalScrollBar().setUnitIncrement(24);
        add(desplazamiento, BorderLayout.CENTER);
        return cuerpo;
    }

    protected Diseno.Columna crearCuerpo() {
        return crearCuerpo(ANCHO_CONTENIDO);
    }

    /**
     * Encabezado fijo de la pantalla: boton volver (opcional), titulo,
     * subtitulo y acciones a la derecha.
     */
    protected JPanel encabezado(String titulo, String subtitulo, boolean conVolver, JComponent... acciones) {
        Encabezado e = new Encabezado(titulo, subtitulo, conVolver, acciones);
        add(e, BorderLayout.NORTH);
        return e;
    }

    /** Barra blanca superior. El contenido se alinea con el cuerpo (mismo ancho maximo). */
    private static final class Encabezado extends JPanel {
        Encabezado(String titulo, String subtitulo, boolean conVolver, JComponent... acciones) {
            super(new AnchoMaximoLayout(ANCHO_CONTENIDO, Estilo.MARGEN));
            setBackground(Estilo.BLANCO);
            setBorder(BorderFactory.createEmptyBorder(18, 0, 16, 0));

            JPanel fila = Estilo.panel(new BorderLayout(14, 0));
            if (conVolver) {
                JButton v = Estilo.botonIcono(Iconos.Tipo.FLECHA_IZQ, "Volver (Alt + ←)", Estilo.TEXTO, 40);
                v.addActionListener(ev -> Aplicacion.volver());
                fila.add(v, BorderLayout.WEST);
            }
            JPanel textos = Estilo.panel(new BorderLayout(0, 2));
            JLabel t = Estilo.texto(titulo, Estilo.fuente(21, Estilo.NEGRITA), Estilo.TEXTO);
            textos.add(t, BorderLayout.NORTH);
            if (subtitulo != null && !subtitulo.isEmpty()) {
                textos.add(Estilo.texto(subtitulo, Estilo.pequena(), Estilo.GRIS), BorderLayout.SOUTH);
            }
            fila.add(textos, BorderLayout.CENTER);
            if (acciones.length > 0) {
                JPanel der = Estilo.panel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
                for (JComponent a : acciones) der.add(a);
                JPanel centrar = Estilo.panel(new java.awt.GridBagLayout());
                centrar.add(der);
                fila.add(centrar, BorderLayout.EAST);
            }
            add(fila);
        }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Estilo.LINEA);
            g.fillRect(0, getHeight() - 1, getWidth(), 1);
        }
    }

    /** Layout de un solo hijo: lo centra con un ancho maximo (como la Columna). */
    static final class AnchoMaximoLayout implements java.awt.LayoutManager {
        private final int max, margen;
        AnchoMaximoLayout(int max, int margen) { this.max = max; this.margen = margen; }
        @Override public void addLayoutComponent(String n, Component c) { }
        @Override public void removeLayoutComponent(Component c) { }
        @Override public Dimension preferredLayoutSize(java.awt.Container p) {
            Insets in = p.getInsets();
            Component c = p.getComponentCount() > 0 ? p.getComponent(0) : null;
            int h = c == null ? 0 : c.getPreferredSize().height;
            return new Dimension(max, h + in.top + in.bottom);
        }
        @Override public Dimension minimumLayoutSize(java.awt.Container p) { return preferredLayoutSize(p); }
        @Override public void layoutContainer(java.awt.Container p) {
            if (p.getComponentCount() == 0) return;
            Insets in = p.getInsets();
            int w = Math.min(max, p.getWidth() - margen * 2);
            Component c = p.getComponent(0);
            c.setBounds((p.getWidth() - w) / 2, in.top, w, p.getHeight() - in.top - in.bottom);
        }
    }

    // ------------------------------------------------------------------
    //  Navegacion (atajos para las pantallas hijas)
    // ------------------------------------------------------------------

    protected void irA(Supplier<VentanaBase> siguiente) { Aplicacion.ir(siguiente); }

    protected void irRaiz(Supplier<VentanaBase> siguiente) { Aplicacion.irRaiz(siguiente); }

    protected void volver() { Aplicacion.volver(); }

    protected void toast(String msg, Dialogos.Tono tono) { Aplicacion.toast(msg, tono); }

    /** Vuelve al inicio del desplazamiento (por ejemplo al cambiar de pestana). */
    protected void subir() {
        if (desplazamiento != null) desplazamiento.getVerticalScrollBar().setValue(0);
    }
}
