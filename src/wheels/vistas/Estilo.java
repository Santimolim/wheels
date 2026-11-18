package wheels.vistas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Colores, fuentes y "fabrica" de componentes.
 * Todas las ventanas usan estos metodos para verse iguales.
 * Los botones, tarjetas y avatares se dibujan redondeados.
 */
public class Estilo {

    // ---------------- Colores de la marca Wheels ----------------
    public static final Color NAVY   = new Color(11, 26, 58);
    public static final Color NAVY2  = new Color(24, 52, 105);
    public static final Color TEAL   = new Color(44, 201, 196);
    public static final Color AZUL   = new Color(30, 123, 224);
    public static final Color FONDO  = new Color(242, 245, 250);
    public static final Color BLANCO = Color.WHITE;
    public static final Color TEXTO  = new Color(15, 27, 51);
    public static final Color GRIS   = new Color(122, 134, 153);
    public static final Color LINEA  = new Color(225, 231, 240);
    public static final Color VERDE  = new Color(22, 163, 74);
    public static final Color AMBAR  = new Color(245, 158, 11);
    public static final Color ROJO   = new Color(220, 38, 38);

    // ---------------- Fuentes ----------------
    private static final String FAMILIA = "Segoe UI";
    public static final Font TITULO_GRANDE = new Font(FAMILIA, Font.BOLD, 26);
    public static final Font TITULO        = new Font(FAMILIA, Font.BOLD, 18);
    public static final Font SUBTITULO     = new Font(FAMILIA, Font.BOLD, 14);
    public static final Font NORMAL        = new Font(FAMILIA, Font.PLAIN, 13);
    public static final Font PEQUENA       = new Font(FAMILIA, Font.PLAIN, 11);
    public static final Font ETIQUETA      = new Font(FAMILIA, Font.BOLD, 11);

    // ---------------- Medidas de la ventana ----------------
    public static final int ANCHO = 380;
    public static final int ALTO  = 700;

    static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    // ==================================================================
    //  Etiquetas de texto
    // ==================================================================

    public static JLabel texto(String txt, int x, int y, int ancho, Font fuente, Color color) {
        JLabel l = new JLabel(txt);
        l.setBounds(x, y, ancho, fuente.getSize() + 10);
        l.setFont(fuente);
        l.setForeground(color);
        return l;
    }

    public static JLabel titulo(String txt, int x, int y) {
        return texto(txt, x, y, ANCHO - x - 20, TITULO, TEXTO);
    }

    public static JLabel etiqueta(String txt, int x, int y) {
        return texto(txt.toUpperCase(), x, y, 260, ETIQUETA, GRIS);
    }

    // ==================================================================
    //  Campos de texto
    // ==================================================================

    private static javax.swing.border.Border bordeCampo() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINEA, 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10));
    }

    public static JTextField campo(int x, int y, int ancho) {
        JTextField t = new JTextField();
        t.setBounds(x, y, ancho, 34);
        t.setFont(NORMAL);
        t.setBackground(BLANCO);
        t.setForeground(TEXTO);
        t.setBorder(bordeCampo());
        enfocar(t);
        return t;
    }

    public static JPasswordField campoClave(int x, int y, int ancho) {
        JPasswordField t = new JPasswordField();
        t.setBounds(x, y, ancho, 34);
        t.setFont(NORMAL);
        t.setBackground(BLANCO);
        t.setForeground(TEXTO);
        t.setBorder(bordeCampo());
        enfocar(t);
        return t;
    }

    /** Resalta el borde del campo en azul cuando tiene el foco. */
    private static void enfocar(final JTextField t) {
        t.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(AZUL, 2, true),
                        BorderFactory.createEmptyBorder(0, 9, 0, 9)));
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                t.setBorder(bordeCampo());
            }
        });
    }

    // ==================================================================
    //  Botones (redondeados, con efecto al pasar el mouse)
    // ==================================================================

    /** Boton dibujado a mano con esquinas redondeadas. */
    public static class Boton extends JButton {
        private boolean conBorde = false;
        private boolean encima = false;
        private final int radio;

        public Boton(String txt, int radio) {
            super(txt);
            this.radio = radio;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setMargin(new java.awt.Insets(0, 2, 0, 2));
            setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e)  { encima = false; repaint(); }
            });
        }

        public void setConBorde(boolean b) { conBorde = b; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            Color c = getBackground();
            if (getModel().isPressed()) c = c.darker();
            else if (encima) c = conBorde ? FONDO : mezclar(c, Color.WHITE, 0.12f);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            if (conBorde) {
                g2.setColor(LINEA);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static Color mezclar(Color a, Color b, float t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    private static JButton baseBoton(String txt, int x, int y, int ancho, int alto,
                                     Color fondo, Color letra) {
        Boton b = new Boton(txt, 14);
        b.setBounds(x, y, ancho, alto);
        b.setFont(SUBTITULO);
        b.setBackground(fondo);
        b.setForeground(letra);
        return b;
    }

    /** Boton principal de la pantalla (azul de marca). */
    public static JButton botonPrimario(String txt, int x, int y, int ancho) {
        return baseBoton(txt, x, y, ancho, 42, AZUL, BLANCO);
    }

    /** Boton secundario oscuro. */
    public static JButton botonOscuro(String txt, int x, int y, int ancho) {
        return baseBoton(txt, x, y, ancho, 38, NAVY, BLANCO);
    }

    /** Boton rojo para acciones de peligro (SOS, cancelar). */
    public static JButton botonPeligro(String txt, int x, int y, int ancho) {
        return baseBoton(txt, x, y, ancho, 42, ROJO, BLANCO);
    }

    /** Boton blanco con borde, para acciones de menor importancia. */
    public static JButton botonBlanco(String txt, int x, int y, int ancho) {
        Boton b = (Boton) baseBoton(txt, x, y, ancho, 38, BLANCO, TEXTO);
        b.setConBorde(true);
        return b;
    }

    /** Boton pequeno, usado dentro de las tarjetas. */
    public static JButton botonPequeno(String txt, int x, int y, int ancho) {
        JButton b = baseBoton(txt, x, y, ancho, 28, NAVY, BLANCO);
        b.setFont(ETIQUETA);
        return b;
    }

    /** Texto que parece un enlace. */
    public static JButton enlace(String txt, int x, int y, int ancho) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, ancho, 24);
        b.setFont(ETIQUETA);
        b.setForeground(AZUL);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    // ==================================================================
    //  Paneles
    // ==================================================================

    /** Panel con degradado azul oscuro (para encabezados). */
    public static class PanelDegradado extends JPanel {
        private Color colorFijo = null;
        public PanelDegradado() { super(null); setOpaque(true); }
        /** Si se da un color, el panel se pinta solido con ese color (alertas). */
        public void setColorFijo(Color c) { colorFijo = c; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            if (colorFijo != null) {
                g2.setColor(colorFijo);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                return;
            }
            g2.setPaint(new GradientPaint(0, 0, NAVY, getWidth(), getHeight(), NAVY2));
            g2.fillRect(0, 0, getWidth(), getHeight());
            // circulos decorativos
            g2.setColor(new Color(44, 201, 196, 35));
            g2.fillOval(getWidth() - 120, -60, 180, 180);
            g2.setColor(new Color(255, 255, 255, 15));
            g2.fillOval(getWidth() - 60, getHeight() - 40, 110, 110);
            g2.dispose();
        }
    }

    /** Barra superior con titulo y subtitulo. */
    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new PanelDegradado();
        p.setBounds(0, 0, ANCHO, 80);

        JLabel t = new JLabel(titulo);
        t.setBounds(20, 18, ANCHO - 40, 26);
        t.setFont(TITULO);
        t.setForeground(BLANCO);
        p.add(t);

        JLabel s = new JLabel(subtitulo);
        s.setBounds(20, 46, ANCHO - 40, 18);
        s.setFont(PEQUENA);
        s.setForeground(new Color(170, 190, 216));
        p.add(s);

        return p;
    }

    /** Tarjeta blanca redondeada con sombra suave. */
    public static class Tarjeta extends JPanel {
        public Tarjeta() { super(null); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            g2.setColor(new Color(15, 27, 51, 12));
            g2.fillRoundRect(1, 3, getWidth() - 2, getHeight() - 3, 18, 18);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 18, 18);
            g2.setColor(LINEA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 3, 18, 18);
            g2.dispose();
        }
    }

    public static JPanel tarjeta(int x, int y, int ancho, int alto) {
        JPanel p = new Tarjeta();
        p.setBounds(x, y, ancho, alto);
        p.setBackground(BLANCO);
        return p;
    }

    /** Circulo con las iniciales del usuario (hace de foto de perfil). */
    public static JLabel avatar(String iniciales, int x, int y, final int tam) {
        JLabel l = new JLabel(iniciales, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                suavizar(g2);
                g2.setPaint(new GradientPaint(0, 0, TEAL, tam, tam, new Color(30, 160, 200)));
                g2.fillOval(0, 0, tam - 1, tam - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setBounds(x, y, tam, tam);
        l.setForeground(NAVY);
        l.setFont(new Font(FAMILIA, Font.BOLD, tam / 3));
        return l;
    }

    /** Pastilla redondeada de color (estados, etiquetas). */
    public static JLabel pastilla(String txt, int x, int y, int ancho, final Color fondo, Color letra) {
        JLabel l = new JLabel(txt, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                suavizar(g2);
                g2.setColor(fondo);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setBounds(x, y, ancho, 22);
        l.setForeground(letra);
        l.setFont(new Font(FAMILIA, Font.BOLD, 10));
        return l;
    }

    /** Etiqueta de estado de una solicitud. */
    public static JLabel estado(String txt, int x, int y) {
        Color fondo = LINEA;
        Color letra = GRIS;
        if (txt.equals("Pendiente"))  { fondo = new Color(254, 243, 199); letra = new Color(180, 83, 9); }
        if (txt.equals("Aceptada"))   { fondo = new Color(220, 252, 231); letra = new Color(21, 128, 61); }
        if (txt.equals("En curso"))   { fondo = new Color(219, 234, 254); letra = new Color(29, 78, 216); }
        if (txt.equals("Rechazada"))  { fondo = new Color(254, 226, 226); letra = new Color(185, 28, 28); }
        return pastilla(txt, x, y, 80, fondo, letra);
    }

    /** Linea separadora horizontal. */
    public static JPanel separador(int x, int y, int ancho) {
        JPanel p = new JPanel();
        p.setBounds(x, y, ancho, 1);
        p.setBackground(LINEA);
        return p;
    }

    /** Estrellas en texto para una calificacion "4.8". */
    public static String estrellas(String calificacion) {
        return calificacion + " / 5";
    }

    static BasicStroke trazo(float w) {
        return new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }
}
