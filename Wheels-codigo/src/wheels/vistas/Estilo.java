package wheels.vistas;

import java.awt.Color;
import java.awt.Font;
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
 */
public class Estilo {

    // ---------------- Colores de la marca Wheels ----------------
    public static final Color NAVY   = new Color(11, 26, 58);
    public static final Color TEAL   = new Color(44, 201, 196);
    public static final Color AZUL   = new Color(30, 123, 224);
    public static final Color FONDO  = new Color(245, 247, 250);
    public static final Color BLANCO = Color.WHITE;
    public static final Color TEXTO  = new Color(15, 27, 51);
    public static final Color GRIS   = new Color(122, 134, 153);
    public static final Color LINEA  = new Color(228, 233, 240);
    public static final Color VERDE  = new Color(34, 197, 94);
    public static final Color AMBAR  = new Color(245, 158, 11);

    // ---------------- Fuentes ----------------
    public static final Font TITULO_GRANDE = new Font("SansSerif", Font.BOLD, 26);
    public static final Font TITULO        = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBTITULO     = new Font("SansSerif", Font.BOLD, 14);
    public static final Font NORMAL        = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font PEQUENA       = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font ETIQUETA      = new Font("SansSerif", Font.BOLD, 11);

    // ---------------- Medidas de la ventana ----------------
    public static final int ANCHO = 380;
    public static final int ALTO  = 700;

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

    public static JTextField campo(int x, int y, int ancho) {
        JTextField t = new JTextField();
        t.setBounds(x, y, ancho, 34);
        t.setFont(NORMAL);
        t.setBackground(BLANCO);
        t.setForeground(TEXTO);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINEA, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        return t;
    }

    public static JPasswordField campoClave(int x, int y, int ancho) {
        JPasswordField t = new JPasswordField();
        t.setBounds(x, y, ancho, 34);
        t.setFont(NORMAL);
        t.setBackground(BLANCO);
        t.setForeground(TEXTO);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LINEA, 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        return t;
    }

    // ==================================================================
    //  Botones
    // ==================================================================

    private static JButton baseBoton(String txt, int x, int y, int ancho, int alto,
                                     Color fondo, Color letra) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, ancho, alto);
        b.setFont(SUBTITULO);
        b.setBackground(fondo);
        b.setForeground(letra);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
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

    /** Boton blanco con borde, para acciones de menor importancia. */
    public static JButton botonBlanco(String txt, int x, int y, int ancho) {
        JButton b = baseBoton(txt, x, y, ancho, 38, BLANCO, TEXTO);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createLineBorder(LINEA, 1));
        return b;
    }

    /** Boton pequeno turquesa, usado dentro de las tarjetas de viaje. */
    public static JButton botonPequeno(String txt, int x, int y, int ancho) {
        JButton b = baseBoton(txt, x, y, ancho, 28, NAVY, BLANCO);
        b.setFont(PEQUENA);
        return b;
    }

    /** Texto que parece un enlace. */
    public static JButton enlace(String txt, int x, int y, int ancho) {
        JButton b = new JButton(txt);
        b.setBounds(x, y, ancho, 24);
        b.setFont(PEQUENA);
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

    /** Barra superior azul oscuro con titulo y subtitulo. */
    public static JPanel encabezado(String titulo, String subtitulo) {
        JPanel p = new JPanel(null);
        p.setBounds(0, 0, ANCHO, 80);
        p.setBackground(NAVY);

        JLabel t = new JLabel(titulo);
        t.setBounds(20, 20, ANCHO - 40, 24);
        t.setFont(TITULO);
        t.setForeground(BLANCO);
        p.add(t);

        JLabel s = new JLabel(subtitulo);
        s.setBounds(20, 46, ANCHO - 40, 18);
        s.setFont(PEQUENA);
        s.setForeground(new Color(159, 178, 204));
        p.add(s);

        return p;
    }

    /** Tarjeta blanca con borde gris. */
    public static JPanel tarjeta(int x, int y, int ancho, int alto) {
        JPanel p = new JPanel(null);
        p.setBounds(x, y, ancho, alto);
        p.setBackground(BLANCO);
        p.setBorder(BorderFactory.createLineBorder(LINEA, 1));
        return p;
    }

    /** Circulo con las iniciales del usuario (hace de foto de perfil). */
    public static JLabel avatar(String iniciales, int x, int y, int tam) {
        JLabel l = new JLabel(iniciales, SwingConstants.CENTER);
        l.setBounds(x, y, tam, tam);
        l.setOpaque(true);
        l.setBackground(TEAL);
        l.setForeground(NAVY);
        l.setFont(new Font("SansSerif", Font.BOLD, tam / 3));
        return l;
    }

    /** Etiqueta de estado de una solicitud. */
    public static JLabel estado(String txt, int x, int y) {
        Color fondo = LINEA;
        Color letra = GRIS;
        if (txt.equals("Pendiente")) { fondo = new Color(254, 243, 199); letra = new Color(180, 83, 9); }
        if (txt.equals("Aceptada"))  { fondo = new Color(220, 252, 231); letra = new Color(21, 128, 61); }

        JLabel l = new JLabel(txt, SwingConstants.CENTER);
        l.setBounds(x, y, 80, 22);
        l.setOpaque(true);
        l.setBackground(fondo);
        l.setForeground(letra);
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        return l;
    }

    /** Linea separadora horizontal. */
    public static JPanel separador(int x, int y, int ancho) {
        JPanel p = new JPanel();
        p.setBounds(x, y, ancho, 1);
        p.setBackground(LINEA);
        return p;
    }
}
