package wheels.vistas;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.function.Supplier;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import wheels.datos.BaseDatos;
import wheels.datos.Conexion;

/**
 * Ventana unica de Wheels. Las pantallas (VentanaBase) se cambian dentro
 * de ella con una transicion suave, en lugar de abrir y cerrar ventanas.
 *
 * Formato de celular: ventana vertical de tamano fijo con la barra de
 * navegacion inferior (Inicio, Mis viajes, Publicar, Avisos, Perfil).
 */
public final class Aplicacion {

    public static final int ANCHO_CELULAR = 420;
    public static final int ALTO_CELULAR = 860;

    private static JFrame ventana;
    private static JPanel escena;
    private static BarraInferior inferior;
    private static Capa capa;
    private static VentanaBase actual;
    private static Supplier<VentanaBase> fabricaActual;
    private static final ArrayDeque<Supplier<VentanaBase>> historial = new ArrayDeque<>();
    private static boolean mostrandoError = false;
    private static final Preferences PREFS = Preferences.userNodeForPackage(Aplicacion.class);

    private Aplicacion() { }

    public static JFrame ventana() { return ventana; }

    /** Crea la ventana y muestra la primera pantalla. */
    public static void iniciar(Supplier<VentanaBase> primera) {
        ventana = new JFrame("Wheels");
        ventana.setIconImages(Logo.iconosVentana());
        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Estilo.FONDO);
        escena = new JPanel(new BorderLayout());
        escena.setBackground(Estilo.FONDO);
        inferior = new BarraInferior();
        raiz.add(escena, BorderLayout.CENTER);
        raiz.add(inferior, BorderLayout.SOUTH);
        ventana.setContentPane(raiz);

        capa = new Capa();
        ventana.setGlassPane(capa);

        ventana.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { salir(); }
        });
        ventana.getRootPane().registerKeyboardAction(e -> volver(),
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);

        BaseDatos.manejadorError = msg -> SwingUtilities.invokeLater(() -> {
            if (mostrandoError) return;
            mostrandoError = true;
            try {
                Dialogos.error(ventana, "Problema con la base de datos", msg);
            } finally {
                mostrandoError = false;
            }
        });

        restaurarTamano();
        mostrar(primera, false);
        ventana.setVisible(true);

        // Los contadores (avisos, solicitudes) se actualizan solos cada 20 segundos
        Timer t = new Timer(20_000, e -> actualizarInsignias());
        t.start();
    }

    /** Ventana con formato de celular (vertical), ajustada al alto de la pantalla. */
    private static void restaurarTamano() {
        Rectangle pantalla = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int h = Math.min(ALTO_CELULAR, pantalla.height - 20);
        ventana.setSize(ANCHO_CELULAR, Math.max(600, h));
        ventana.setResizable(false);
        int x = PREFS.getInt("x", Integer.MIN_VALUE), y = PREFS.getInt("y", Integer.MIN_VALUE);
        if (x != Integer.MIN_VALUE && pantalla.contains(x + 40, y + 40)) ventana.setLocation(x, y);
        else ventana.setLocationRelativeTo(null);
    }

    private static void salir() {
        PREFS.putInt("x", ventana.getX());
        PREFS.putInt("y", ventana.getY());
        if (actual != null) actual.alSalir();
        Conexion.getInstancia().desconectar();
        ventana.dispose();
        System.exit(0);
    }

    // ------------------------------------------------------------------
    //  Navegacion
    // ------------------------------------------------------------------

    /** Abre una pantalla y guarda la actual en el historial (para "Volver"). */
    public static void ir(Supplier<VentanaBase> fabrica) {
        if (fabricaActual != null) historial.push(fabricaActual);
        mostrar(fabrica, true);
    }

    /** Abre una pantalla principal: reinicia el historial. */
    public static void irRaiz(Supplier<VentanaBase> fabrica) {
        historial.clear();
        mostrar(fabrica, true);
    }

    /** Regresa a la pantalla anterior (se vuelve a construir con datos frescos). */
    public static void volver() {
        if (!historial.isEmpty()) {
            mostrar(historial.pop(), true);
        } else if (BaseDatos.usuarioActual != null) {
            if (!(actual instanceof VentanaHome)) mostrar(VentanaHome::new, true);
        } else if (!(actual instanceof VentanaBienvenida)) {
            mostrar(VentanaBienvenida::new, true);
        }
    }

    /** Vuelve a construir la pantalla actual (despues de cambiar datos). */
    public static void recargar() {
        if (fabricaActual != null) mostrar(fabricaActual, false);
    }

    private static void mostrar(Supplier<VentanaBase> fabrica, boolean animar) {
        BufferedImage antes = null;
        if (animar && actual != null && escena.getWidth() > 0 && escena.getHeight() > 0) {
            antes = new BufferedImage(escena.getWidth(), escena.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = antes.createGraphics();
            escena.paint(g);
            g.dispose();
        }
        VentanaBase nueva = fabrica.get();
        if (actual != null) actual.alSalir();
        if (nueva.esRaiz()) historial.clear();
        actual = nueva;
        fabricaActual = fabrica;

        escena.removeAll();
        escena.add(nueva, BorderLayout.CENTER);
        ventana.setTitle("Wheels · " + nueva.getTitulo());
        actualizarNavegacion();
        actualizarInsignias();
        escena.revalidate();
        escena.repaint();
        nueva.alMostrar();
        if (antes != null) capa.transicion(antes, SwingUtilities.convertPoint(escena, 0, 0, capa));
    }

    /** Muestra la barra inferior solo en las pantallas con sesion iniciada. */
    static void actualizarNavegacion() {
        if (ventana == null) return;
        boolean conMenu = actual != null && actual.seccion() != null && BaseDatos.usuarioActual != null;
        inferior.setVisible(conMenu);
        if (conMenu) inferior.marcar(actual.seccion());
        ventana.getContentPane().revalidate();
    }

    /** Actualiza los numeritos de avisos sin leer y solicitudes pendientes. */
    public static void actualizarInsignias() {
        if (BaseDatos.usuarioActual == null || inferior == null) return;
        inferior.insignias(BaseDatos.notificacionesSinLeer(), BaseDatos.solicitudesPendientesRecibidas());
    }

    /** Acciones de los botones de la barra de navegacion inferior. */
    static void navegar(String seccion) {
        switch (seccion) {
            case VentanaBase.INICIO:   irRaiz(VentanaHome::new); break;
            case VentanaBase.VIAJES:   irRaiz(VentanaMisViajes::new); break;
            case VentanaBase.PUBLICAR: irRaiz(VentanaPublicarViaje::crear); break;
            case VentanaBase.AVISOS:   irRaiz(VentanaNotificaciones::new); break;
            case VentanaBase.PERFIL:   irRaiz(VentanaPerfil::new); break;
            default: break;
        }
    }

    /** Pide confirmacion y cierra la sesion. */
    static void cerrarSesion() {
        if (Dialogos.confirmar(ventana, "¿Cerrar sesión?",
                "Tendrás que volver a ingresar tu correo y contraseña.", "Cerrar sesión", true)) {
            BaseDatos.cerrarSesion();
            irRaiz(VentanaLogin::new);
            toast("Sesión cerrada", Dialogos.Tono.INFO);
        }
    }

    // ------------------------------------------------------------------
    //  Avisos flotantes (toast) y fondo oscuro de dialogos
    // ------------------------------------------------------------------

    public static void toast(String mensaje, Dialogos.Tono tono) {
        if (ventana == null) return;
        Toast.mostrar(ventana.getLayeredPane(), mensaje, tono,
                inferior.isVisible() ? inferior.getHeight() : 0);
    }

    static void oscurecer(boolean si) {
        if (capa != null) capa.oscurecer(si);
    }

    /**
     * Capa transparente sobre la ventana: dibuja la transicion entre
     * pantallas (la anterior se desvanece) y el fondo oscuro de los dialogos.
     */
    private static final class Capa extends JComponent {
        private BufferedImage imagen;
        private java.awt.Point origen = new java.awt.Point();
        private float alfaImagen = 0f;
        private float alfaOscuro = 0f;
        private Timer animacion;

        Capa() {
            setOpaque(false);
        }

        void transicion(BufferedImage antes, java.awt.Point en) {
            if (animacion != null) animacion.stop();
            imagen = antes;
            origen = en;
            alfaImagen = 1f;
            setVisible(true);
            final long inicio = System.currentTimeMillis();
            animacion = new Timer(15, e -> {
                float t = Math.min(1f, (System.currentTimeMillis() - inicio) / 200f);
                alfaImagen = 1f - (1f - (1f - t) * (1f - t));   // ease-out
                if (t >= 1f) {
                    ((Timer) e.getSource()).stop();
                    imagen = null;
                    if (alfaOscuro == 0f) setVisible(false);
                }
                repaint();
            });
            animacion.start();
        }

        void oscurecer(boolean si) {
            alfaOscuro = si ? 0.38f : 0f;
            setVisible(si || imagen != null);
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (imagen != null && alfaImagen > 0f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfaImagen));
                g2.drawImage(imagen, origen.x, origen.y, null);
            }
            if (alfaOscuro > 0f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alfaOscuro));
                g2.setColor(new Color(8, 15, 35));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }
    }

    /** Aviso flotante que aparece abajo y se desvanece solo a los 3 segundos. */
    private static final class Toast extends JComponent {
        private static Toast visible;
        private final String mensaje;
        private final Color fondo;
        private final javax.swing.Icon icono;
        private float alfa = 0f;

        private Toast(String mensaje, Dialogos.Tono tono) {
            this.mensaje = mensaje;
            Color c;
            Iconos.Tipo t;
            switch (tono) {
                case EXITO:   c = Estilo.TEAL; t = Iconos.Tipo.CHECK; break;
                case ALERTA:  c = Estilo.AMBAR; t = Iconos.Tipo.ALERTA; break;
                case PELIGRO: c = new Color(0xF87171); t = Iconos.Tipo.ALERTA; break;
                default:      c = new Color(0x93C5FD); t = Iconos.Tipo.INFO; break;
            }
            fondo = Estilo.NAVY;
            icono = Iconos.de(t, 18, c);
            setFont(Estilo.cuerpoSemi());
        }

        static void mostrar(JLayeredPane capa, String mensaje, Dialogos.Tono tono, int margenInferior) {
            if (visible != null) {
                capa.remove(visible);
                capa.repaint();
            }
            Toast t = new Toast(mensaje, tono);
            visible = t;
            java.awt.FontMetrics fm = t.getFontMetrics(t.getFont());
            int w = Math.min(capa.getWidth() - 32, fm.stringWidth(mensaje) + 18 + 12 + 44);
            int h = 48;
            t.setBounds((capa.getWidth() - w) / 2, capa.getHeight() - h - 24 - margenInferior, w, h);
            capa.add(t, JLayeredPane.POPUP_LAYER);
            final long inicio = System.currentTimeMillis();
            Timer anim = new Timer(16, null);
            anim.addActionListener(e -> {
                long ms = System.currentTimeMillis() - inicio;
                if (ms < 180) t.alfa = ms / 180f;
                else if (ms < 3000) t.alfa = 1f;
                else if (ms < 3300) t.alfa = 1f - (ms - 3000) / 300f;
                else {
                    anim.stop();
                    capa.remove(t);
                    if (visible == t) visible = null;
                    capa.repaint(t.getBounds());
                    return;
                }
                t.repaint();
            });
            anim.start();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.suavizar(g2);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alfa))));
            int desplazar = Math.round((1f - Math.min(1f, alfa)) * 8);
            g2.translate(0, desplazar);
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(0, 3, getWidth(), getHeight() - 3, 16, 16);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 3, 16, 16);
            icono.paintIcon(this, g2, 16, (getHeight() - 3 - 18) / 2);
            g2.setColor(Color.WHITE);
            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String txt = mensaje;
            int disponible = getWidth() - 16 - 18 - 12 - 16;
            while (fm.stringWidth(txt) > disponible && txt.length() > 4) txt = txt.substring(0, txt.length() - 2);
            if (!txt.equals(mensaje)) txt = txt.substring(0, txt.length() - 1) + "…";
            g2.drawString(txt, 16 + 18 + 12, (getHeight() - 3 - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
