package wheels.vistas;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.text.View;
import wheels.modelo.Solicitud;

/**
 * Sistema de diseno de Wheels: colores, tipografia y "fabrica" de componentes.
 * Todas las pantallas usan estos metodos para verse iguales.
 * Se apoya en FlatLaf (look and feel moderno) y en la fuente Inter.
 */
public final class Estilo {

    private Estilo() { }

    // ---------------- Colores de la marca ----------------
    public static final Color NAVY        = new Color(0x0B1B3F);
    public static final Color NAVY2       = new Color(0x17306B);
    public static final Color NAVY_HOVER  = new Color(0x1A2D5C);
    public static final Color TEAL        = new Color(0x14B8A6);
    public static final Color TEAL_OSCURO = new Color(0x0F766E);
    public static final Color TEAL_SUAVE  = new Color(0xE3F6F3);

    // ---------------- Neutros ----------------
    public static final Color FONDO       = new Color(0xF4F6FA);
    public static final Color BLANCO      = Color.WHITE;
    public static final Color TEXTO       = new Color(0x0F172A);
    public static final Color TEXTO2      = new Color(0x334155);
    public static final Color GRIS        = new Color(0x64748B);
    public static final Color GRIS_CLARO  = new Color(0x94A3B8);
    public static final Color LINEA       = new Color(0xE5E9F0);
    public static final Color LINEA_FUERTE = new Color(0xD3DAE5);
    public static final Color HOVER       = new Color(0xF1F4F9);

    // ---------------- Estados ----------------
    public static final Color VERDE       = new Color(0x16A34A);
    public static final Color VERDE_SUAVE = new Color(0xDCFCE7);
    public static final Color VERDE_TEXTO = new Color(0x15803D);
    public static final Color AMBAR       = new Color(0xF59E0B);
    public static final Color AMBAR_SUAVE = new Color(0xFEF3C7);
    public static final Color AMBAR_TEXTO = new Color(0xB45309);
    public static final Color ROJO        = new Color(0xDC2626);
    public static final Color ROJO_HOVER  = new Color(0xB91C1C);
    public static final Color ROJO_SUAVE  = new Color(0xFEE2E2);
    public static final Color ROJO_TEXTO  = new Color(0xB91C1C);
    public static final Color AZUL        = new Color(0x2563EB);
    public static final Color AZUL_SUAVE  = new Color(0xDBEAFE);
    public static final Color AZUL_TEXTO  = new Color(0x1D4ED8);

    // ---------------- Espaciado ----------------
    public static final int RADIO = 14;
    public static final int RADIO_CAMPO = 12;
    public static final int MARGEN = 24;

    // ---------------- Tipografia ----------------
    public static final int REGULAR = 400, SEMI = 600, NEGRITA = 700;
    private static final Map<String, Font> FUENTES = new HashMap<>();

    public static Font fuente(float tam, int peso) {
        String clave = tam + "/" + peso;
        return FUENTES.computeIfAbsent(clave, k -> {
            if (peso >= NEGRITA) return new Font(FlatInterFont.FAMILY, Font.BOLD, 1).deriveFont(tam);
            if (peso >= SEMI) return new Font(FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 1).deriveFont(tam);
            return new Font(FlatInterFont.FAMILY, Font.PLAIN, 1).deriveFont(tam);
        });
    }

    public static Font display()   { return fuente(30, NEGRITA); }
    public static Font h1()        { return fuente(24, NEGRITA); }
    public static Font h2()        { return fuente(18, SEMI); }
    public static Font h3()        { return fuente(15, SEMI); }
    public static Font cuerpo()    { return fuente(14, REGULAR); }
    public static Font cuerpoSemi(){ return fuente(14, SEMI); }
    public static Font pequena()   { return fuente(12.5f, REGULAR); }
    public static Font pequenaSemi(){ return fuente(12.5f, SEMI); }
    public static Font mini()      { return fuente(11.5f, SEMI); }

    /** Configura FlatLaf, la fuente Inter y los valores por defecto de los componentes. */
    public static void instalar() {
        FlatInterFont.install();
        FlatLaf.setPreferredFontFamily(FlatInterFont.FAMILY);
        FlatLaf.setPreferredSemiboldFontFamily(FlatInterFont.FAMILY_SEMIBOLD);
        Map<String, String> extra = new HashMap<>();
        extra.put("@accentColor", "#0F766E");
        extra.put("@background", "#F4F6FA");
        FlatLaf.setGlobalExtraDefaults(extra);
        FlatLightLaf.setup();

        UIManager.put("defaultFont", fuente(14, REGULAR));
        UIManager.put("Button.arc", RADIO_CAMPO);
        UIManager.put("Component.arc", RADIO_CAMPO);
        UIManager.put("TextComponent.arc", RADIO_CAMPO);
        UIManager.put("CheckBox.arc", 6);
        UIManager.put("Component.focusWidth", 2);
        UIManager.put("Component.innerFocusWidth", 0);
        UIManager.put("Component.borderColor", LINEA_FUERTE);
        UIManager.put("Component.focusedBorderColor", TEAL_OSCURO);
        UIManager.put("Component.focusColor", new Color(20, 184, 166, 70));
        UIManager.put("Component.arrowType", "chevron");
        UIManager.put("TextComponent.selectAllOnFocusPolicy", "never");
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.width", 11);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.track", new Color(0, 0, 0, 0));
        UIManager.put("ScrollPane.smoothScrolling", true);
        UIManager.put("Panel.background", FONDO);
        UIManager.put("TitlePane.unifiedBackground", false);
        UIManager.put("TitlePane.background", BLANCO);
        UIManager.put("TitlePane.inactiveBackground", BLANCO);
        UIManager.put("ToolTip.background", NAVY);
        UIManager.put("ToolTip.foreground", BLANCO);
        UIManager.put("ToolTip.border", BorderFactory.createEmptyBorder(6, 10, 6, 10));
        UIManager.put("PopupMenu.arc", 12);
        UIManager.put("ComboBox.selectionBackground", TEAL_SUAVE);
        UIManager.put("ComboBox.selectionForeground", TEXTO);
        UIManager.put("ComboBox.popupBackground", BLANCO);
        UIManager.put("List.selectionBackground", TEAL_SUAVE);
        UIManager.put("List.selectionForeground", TEXTO);
        UIManager.put("Spinner.buttonStyle", "button");
        UIManager.put("ProgressBar.arc", 999);
        UIManager.put("TextField.background", BLANCO);
        UIManager.put("PasswordField.background", BLANCO);
        UIManager.put("FormattedTextField.background", BLANCO);
        UIManager.put("TextArea.background", BLANCO);
        UIManager.put("ComboBox.background", BLANCO);
        UIManager.put("Spinner.background", BLANCO);
        UIManager.put("PasswordField.showRevealButton", true);
        UIManager.put("OptionPane.background", BLANCO);
    }

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static BasicStroke trazo(float w) {
        return new BasicStroke(w, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    public static Color mezclar(Color a, Color b, float t) {
        return new Color((int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    public static Color alfa(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ==================================================================
    //  Textos
    // ==================================================================

    public static JLabel texto(String txt, Font f, Color c) {
        JLabel l = new JLabel(txt);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }

    public static JLabel titulo(String txt)    { return texto(txt, h1(), TEXTO); }
    public static JLabel subtitulo(String txt) { return texto(txt, h2(), TEXTO); }
    public static JLabel secundario(String txt){ return texto(txt, cuerpo(), GRIS); }

    /** Texto con icono a la izquierda: "[icono] Universidad de La Sabana". */
    public static JLabel dato(Iconos.Tipo icono, String txt) {
        JLabel l = texto(txt, cuerpo(), TEXTO2);
        l.setIcon(Iconos.de(icono, 16, GRIS));
        l.setIconTextGap(8);
        return l;
    }

    /**
     * Parrafo que se ajusta al ancho disponible (salto de linea automatico).
     * Calcula su alto segun el ancho, para usarse dentro de Diseno.Columna.
     */
    public static class Parrafo extends JLabel implements Diseno.AltoSegunAncho {
        private String crudo;
        public Parrafo(String txt, Font f, Color c) {
            setFont(f);
            setForeground(c);
            setVerticalAlignment(SwingConstants.TOP);
            setTexto(txt);
        }
        public final void setTexto(String txt) {
            crudo = txt;
            setText("<html>" + escaparHtml(txt).replace("\n", "<br>") + "</html>");
        }
        public String getTexto() { return crudo; }
        public void centrar() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setText("<html><div style='text-align:center'>" + escaparHtml(crudo).replace("\n", "<br>") + "</div></html>");
        }
        @Override public int altoPara(int ancho) {
            View v = (View) getClientProperty("html");
            if (v == null) return getPreferredSize().height;
            v.setSize(Math.max(1, ancho), 0);
            return (int) Math.ceil(v.getPreferredSpan(View.Y_AXIS)) + 2;
        }
    }

    public static String escaparHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ==================================================================
    //  Botones
    // ==================================================================

    public enum TipoBoton { PRIMARIO, ACENTO, SECUNDARIO, FANTASMA, PELIGRO, PELIGRO_SUAVE, EXITO, CLARO }
    public enum Tamano { GRANDE(46, 15), NORMAL(40, 14), PEQUENO(34, 13);
        final int alto; final float letra;
        Tamano(int alto, float letra) { this.alto = alto; this.letra = letra; }
    }

    /** Boton con altura fija segun su tamano (el ancho lo decide el layout). */
    public static class Boton extends JButton {
        private final Tamano tamano;
        public Boton(String txt, Tamano tamano) {
            super(txt);
            this.tamano = tamano;
        }
        @Override public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, tamano.alto);
        }
        @Override public Dimension getMinimumSize() {
            return new Dimension(super.getMinimumSize().width, tamano.alto);
        }
        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, tamano.alto);
        }
    }

    public static Boton boton(String txt, TipoBoton tipo) {
        return boton(txt, tipo, Tamano.NORMAL, null);
    }

    public static Boton boton(String txt, TipoBoton tipo, Tamano tam, Iconos.Tipo icono) {
        Boton b = new Boton(txt, tam);
        b.setFont(fuente(tam.letra, SEMI));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.setIconTextGap(8);
        Color fondo, letra, hover, presion, borde;
        switch (tipo) {
            case ACENTO:        fondo = TEAL_OSCURO; letra = BLANCO; hover = new Color(0x0D6961); presion = new Color(0x0B5952); borde = fondo; break;
            case SECUNDARIO:    fondo = BLANCO; letra = TEXTO; hover = HOVER; presion = LINEA; borde = LINEA_FUERTE; break;
            case FANTASMA:      fondo = alfa(BLANCO, 0); letra = TEAL_OSCURO; hover = TEAL_SUAVE; presion = new Color(0xCFEFEA); borde = null; break;
            case PELIGRO:       fondo = ROJO; letra = BLANCO; hover = ROJO_HOVER; presion = new Color(0x991B1B); borde = fondo; break;
            case PELIGRO_SUAVE: fondo = ROJO_SUAVE; letra = ROJO_TEXTO; hover = new Color(0xFECACA); presion = new Color(0xFCA5A5); borde = fondo; break;
            case EXITO:         fondo = VERDE; letra = BLANCO; hover = new Color(0x15803D); presion = new Color(0x166534); borde = fondo; break;
            case CLARO:         fondo = alfa(BLANCO, 28); letra = BLANCO; hover = alfa(BLANCO, 48); presion = alfa(BLANCO, 64); borde = alfa(BLANCO, 60); break;
            default:            fondo = NAVY; letra = BLANCO; hover = NAVY_HOVER; presion = NAVY2; borde = fondo; break;
        }
        String estilo = "arc:" + RADIO_CAMPO + ";focusWidth:0;innerFocusWidth:0;"
                + "margin:0,16,0,16;"
                + "background:" + hexA(fondo) + ";foreground:" + hex(letra) + ";"
                + "hoverBackground:" + hexA(hover) + ";pressedBackground:" + hexA(presion) + ";"
                + "focusedBackground:" + hexA(fondo) + ";"
                + "disabledBackground:#e9edf3;disabledText:#94a3b8;"
                + (borde == null ? "borderWidth:0;" : "borderWidth:1;borderColor:" + hexA(borde)
                    + ";hoverBorderColor:" + hexA(tipo == TipoBoton.SECUNDARIO ? GRIS_CLARO : borde)
                    + ";focusedBorderColor:" + hexA(tipo == TipoBoton.SECUNDARIO ? TEAL_OSCURO : borde)
                    + ";disabledBorderColor:#e9edf3;");
        b.putClientProperty(FlatClientProperties.STYLE, estilo);
        if (tipo == TipoBoton.FANTASMA) b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        if (icono != null) {
            b.setIcon(Iconos.de(icono, tam == Tamano.PEQUENO ? 15 : 17, letra));
            b.setDisabledIcon(Iconos.de(icono, tam == Tamano.PEQUENO ? 15 : 17, GRIS_CLARO));
        }
        return b;
    }

    private static String hexA(Color c) {
        return c.getAlpha() == 255 ? hex(c) : String.format("#%02x%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    /** Boton cuadrado solo con icono (volver, cerrar, enviar...). */
    public static JButton botonIcono(Iconos.Tipo icono, String ayuda, Color color, int tam) {
        JButton b = new JButton(Iconos.de(icono, 20, color)) {
            @Override public Dimension getPreferredSize() { return new Dimension(tam, tam); }
            @Override public Dimension getMinimumSize() { return getPreferredSize(); }
            @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        };
        b.setToolTipText(ayuda);
        b.getAccessibleContext().setAccessibleName(ayuda);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON);
        b.putClientProperty(FlatClientProperties.STYLE, "arc:999;hoverBackground:#e9eef5;pressedBackground:#dde4ee;margin:0,0,0,0");
        return b;
    }

    /** Texto clicable con aspecto de enlace. */
    public static JButton enlace(String txt) {
        JButton b = new JButton(txt);
        b.setFont(pequenaSemi());
        b.setForeground(TEAL_OSCURO);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFocusPainted(false);
        b.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        b.putClientProperty(FlatClientProperties.STYLE, "margin:2,4,2,4;arc:8;hoverBackground:#e3f6f3;pressedBackground:#cfefea");
        return b;
    }

    // ==================================================================
    //  Campos de formulario
    // ==================================================================

    private static final String ESTILO_CAMPO =
            "arc:" + RADIO_CAMPO + ";margin:0,12,0,12;borderColor:#d3dae5;focusedBorderColor:#0f766e;"
            + "background:#ffffff";

    public static JTextField campo(String placeholder, Iconos.Tipo icono) {
        JTextField t = new JTextField() {
            @Override public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 42); }
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 42); }
        };
        prepararCampo(t, placeholder, icono);
        return t;
    }

    public static JPasswordField campoClave(String placeholder) {
        JPasswordField t = new JPasswordField() {
            @Override public Dimension getPreferredSize() { return new Dimension(super.getPreferredSize().width, 42); }
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 42); }
        };
        prepararCampo(t, placeholder, Iconos.Tipo.CANDADO);
        return t;
    }

    static void prepararCampo(JTextField t, String placeholder, Iconos.Tipo icono) {
        t.setFont(cuerpo());
        t.setForeground(TEXTO);
        t.putClientProperty(FlatClientProperties.STYLE, ESTILO_CAMPO);
        if (placeholder != null) t.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        if (icono != null) {
            t.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, Iconos.de(icono, 17, GRIS_CLARO));
        }
        // al escribir se quita el borde rojo de error
        t.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void limpiar() { t.putClientProperty(FlatClientProperties.OUTLINE, null); }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });
    }

    public static <T> JComboBox<T> combo(T[] opciones) {
        JComboBox<T> c = new JComboBox<T>(opciones) {
            @Override public Dimension getPreferredSize() { return new Dimension(Math.min(super.getPreferredSize().width, 260), 42); }
            @Override public Dimension getMinimumSize() { return new Dimension(80, 42); }
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 42); }
        };
        c.setFont(cuerpo());
        c.setMaximumRowCount(10);
        c.putClientProperty(FlatClientProperties.STYLE, "arc:" + RADIO_CAMPO
                + ";padding:0,10,0,6;borderColor:#d3dae5;focusedBorderColor:#0f766e;"
                + "background:#ffffff;buttonBackground:#ffffff;buttonEditableBackground:#ffffff");
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    /**
     * Campo de formulario completo: etiqueta arriba, control en medio y
     * mensaje de ayuda/error abajo. El error se muestra en rojo en el mismo lugar.
     */
    public static class Campo extends JPanel {
        private final JComponent control;
        private final JLabel ayuda;
        private final String textoAyuda;
        private JLabel etiqueta;

        /** Colores claros para usar el campo sobre un fondo oscuro. */
        public Campo sobreOscuro() {
            etiqueta.setForeground(new Color(0xC7D2FE));
            ayuda.setForeground(new Color(0xA5B4CF));
            return this;
        }

        /** Sin linea de ayuda/error debajo (campos que nunca muestran error). */
        public Campo compacto() {
            ayuda.setVisible(false);
            return this;
        }

        public Campo(String etiqueta, JComponent control, String textoAyuda) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            this.control = control;
            this.textoAyuda = textoAyuda == null ? "" : textoAyuda;
            JLabel l = texto(etiqueta, pequenaSemi(), TEXTO2);
            l.setAlignmentX(LEFT_ALIGNMENT);
            l.setLabelFor(control);
            this.etiqueta = l;
            add(l);
            add(Box.createVerticalStrut(6));
            control.setAlignmentX(LEFT_ALIGNMENT);
            add(control);
            ayuda = texto(this.textoAyuda.isEmpty() ? " " : this.textoAyuda, fuente(12, REGULAR), GRIS);
            ayuda.setAlignmentX(LEFT_ALIGNMENT);
            ayuda.setBorder(BorderFactory.createEmptyBorder(5, 2, 0, 0));
            add(ayuda);
        }

        public JComponent getControl() { return control; }

        public void setError(String msg) {
            control.putClientProperty(FlatClientProperties.OUTLINE, msg == null ? null : FlatClientProperties.OUTLINE_ERROR);
            ayuda.setText(msg == null ? (textoAyuda.isEmpty() ? " " : textoAyuda) : msg);
            ayuda.setForeground(msg == null ? GRIS : ROJO_TEXTO);
            ayuda.setIcon(msg == null ? null : Iconos.de(Iconos.Tipo.ALERTA, 13, ROJO_TEXTO));
            ayuda.setIconTextGap(5);
        }

        @Override public Dimension getMaximumSize() {
            return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
        }
    }

    // ==================================================================
    //  Paneles, tarjetas y decoracion
    // ==================================================================

    /** Panel transparente con el layout indicado. */
    public static JPanel panel(LayoutManager l) {
        JPanel p = new JPanel(l);
        p.setOpaque(false);
        return p;
    }

    /** Fila horizontal de componentes alineados a la izquierda. */
    public static JPanel fila(int espacio, Component... cs) {
        JPanel p = panel(new FlowLayout(FlowLayout.LEFT, espacio, 0));
        p.setBorder(BorderFactory.createEmptyBorder(0, -espacio, 0, 0));
        for (Component c : cs) p.add(c);
        return p;
    }

    /** Tarjeta blanca redondeada con borde suave y sombra ligera. */
    public static class Tarjeta extends JPanel implements Diseno.AltoSegunAncho {
        private Color fondo = BLANCO;
        private Color borde = LINEA;
        private boolean clicable = false;
        private boolean encima = false;
        private int radio = RADIO + 2;
        private Diseno.Columna contenido;

        public Tarjeta(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        }
        public Tarjeta() { this(new BorderLayout()); }

        /** Crea dentro de la tarjeta una columna que ajusta su alto al ancho. */
        public static Tarjeta conColumna(int separacion) {
            Tarjeta t = new Tarjeta(new BorderLayout());
            t.contenido = new Diseno.Columna(Integer.MAX_VALUE / 4, 0, 0).conSeparacion(separacion);
            t.add(t.contenido, BorderLayout.CENTER);
            return t;
        }

        public Diseno.Columna columna() { return contenido; }

        @Override public int altoPara(int ancho) {
            if (contenido == null) return getPreferredSize().height;
            Insets in = getInsets();
            return in.top + in.bottom + 3 + contenido.altoPara(ancho - in.left - in.right);
        }

        public Tarjeta conFondo(Color c) { fondo = c; repaint(); return this; }
        public Tarjeta conBorde(Color c) { borde = c; repaint(); return this; }
        public Tarjeta conRadio(int r) { radio = r; return this; }
        public Tarjeta conRelleno(int v, int h) { setBorder(BorderFactory.createEmptyBorder(v, h, v, h)); return this; }

        /** Hace que toda la tarjeta responda al clic, con efecto al pasar el mouse. */
        public Tarjeta alHacerClic(Runnable accion) {
            clicable = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) {
                    if (!contains(e.getPoint())) { encima = false; repaint(); }
                }
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) accion.run();
                }
            });
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            int w = getWidth(), h = getHeight();
            // sombra
            g2.setColor(new Color(15, 23, 42, encima ? 16 : 8));
            g2.fillRoundRect(0, 2, w, h - 2, radio + 2, radio + 2);
            if (encima) {
                g2.setColor(new Color(15, 23, 42, 8));
                g2.fillRoundRect(1, 4, w - 2, h - 4, radio + 2, radio + 2);
            }
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, w - 1, h - 3, radio, radio);
            g2.setColor(encima && clicable ? LINEA_FUERTE : borde);
            g2.drawRoundRect(0, 0, w - 1, h - 3, radio, radio);
            g2.dispose();
        }
    }

    /** Panel con degradado azul de la marca (encabezados destacados). */
    public static class PanelMarca extends JPanel {
        private Color colorFijo = null;
        private final int radio;
        public PanelMarca(LayoutManager l, int radio) {
            super(l);
            this.radio = radio;
            setOpaque(false);
        }
        public void setColorFijo(Color c) { colorFijo = c; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            int w = getWidth(), h = getHeight();
            if (radio > 0) g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, radio, radio));
            if (colorFijo != null) {
                g2.setColor(colorFijo);
                g2.fillRect(0, 0, w, h);
            } else {
                g2.setPaint(new GradientPaint(0, 0, NAVY, w, h, NAVY2));
                g2.fillRect(0, 0, w, h);
                Logo.pintarRutaDecorativa(g2, w, h, 1f);
            }
            g2.dispose();
        }
    }

    /** Pastilla redondeada de color (estados, etiquetas). */
    public static class Pastilla extends JLabel {
        private Color fondo;
        public Pastilla(String txt, Color fondo, Color letra, Iconos.Tipo icono) {
            super(txt);
            this.fondo = fondo;
            setForeground(letra);
            setFont(fuente(12, SEMI));
            setBorder(BorderFactory.createEmptyBorder(4, icono == null ? 10 : 8, 4, 10));
            if (icono != null) {
                setIcon(Iconos.de(icono, 13, letra));
                setIconTextGap(5);
            }
        }
        public void setFondo(Color c) { fondo = c; repaint(); }
        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static Pastilla pastilla(String txt, Color fondo, Color letra) {
        return new Pastilla(txt, fondo, letra, null);
    }

    /** Etiqueta de estado de una solicitud con su color. */
    public static Pastilla estado(String estado) {
        switch (estado) {
            case Solicitud.PENDIENTE:  return new Pastilla(estado, AMBAR_SUAVE, AMBAR_TEXTO, Iconos.Tipo.RELOJ);
            case Solicitud.ACEPTADA:   return new Pastilla(estado, VERDE_SUAVE, VERDE_TEXTO, Iconos.Tipo.CHECK);
            case Solicitud.EN_CURSO:   return new Pastilla(estado, AZUL_SUAVE, AZUL_TEXTO, Iconos.Tipo.NAVEGACION);
            case Solicitud.COMPLETADA: return new Pastilla(estado, TEAL_SUAVE, TEAL_OSCURO, Iconos.Tipo.CHECK);
            case Solicitud.RECHAZADA:  return new Pastilla(estado, ROJO_SUAVE, ROJO_TEXTO, Iconos.Tipo.CERRAR);
            case Solicitud.CANCELADA:  return new Pastilla(estado, HOVER, GRIS, Iconos.Tipo.CERRAR);
            default:                   return new Pastilla(estado, HOVER, GRIS, null);
        }
    }

    /** Calificacion con estrella: "★ 4.8". */
    public static Pastilla calificacion(String valor) {
        return new Pastilla(valor, AMBAR_SUAVE, AMBAR_TEXTO, Iconos.Tipo.ESTRELLA_LLENA);
    }

    /** Circulo con las iniciales del usuario (hace de foto de perfil). Color estable por nombre. */
    public static class Avatar extends ComponenteAccesible {
        private static final Color[][] PALETA = {
            {new Color(0x14B8A6), new Color(0x0E7490)}, {new Color(0x6366F1), new Color(0x4338CA)},
            {new Color(0xF59E0B), new Color(0xEA580C)}, {new Color(0xEC4899), new Color(0xBE185D)},
            {new Color(0x22C55E), new Color(0x15803D)}, {new Color(0x3B82F6), new Color(0x1D4ED8)},
            {new Color(0x8B5CF6), new Color(0x6D28D9)}, {new Color(0x06B6D4), new Color(0x0369A1)},
        };
        private final String iniciales;
        private final int tam;
        private final Color[] colores;
        private boolean anillo = false;

        public Avatar(String iniciales, String semilla, int tam) {
            this.iniciales = iniciales;
            this.tam = tam;
            this.colores = PALETA[Math.floorMod(semilla == null ? 0 : semilla.hashCode(), PALETA.length)];
            setOpaque(false);
            getAccessibleContext().setAccessibleName("Avatar " + iniciales);
        }
        public Avatar conAnillo() { anillo = true; return this; }
        @Override public Dimension getPreferredSize() { return new Dimension(tam, tam); }
        @Override public Dimension getMinimumSize() { return getPreferredSize(); }
        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            int d = Math.min(getWidth(), getHeight());
            if (anillo) {
                g2.setColor(alfa(BLANCO, 70));
                g2.fillOval(0, 0, d, d);
                g2.translate(4, 4);
                d -= 8;
            }
            g2.setPaint(new GradientPaint(0, 0, colores[0], d, d, colores[1]));
            g2.fillOval(0, 0, d, d);
            g2.setColor(BLANCO);
            g2.setFont(fuente(Math.max(11, d * 0.38f), SEMI));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            g2.drawString(iniciales, (d - fm.stringWidth(iniciales)) / 2f, (d - fm.getHeight()) / 2f + fm.getAscent());
            g2.dispose();
        }
    }

    /** Icono dentro de un circulo de color suave. */
    public static class IconoCirculo extends ComponenteAccesible {
        private final Icon icono;
        private Color fondo;
        private final int tam;
        public IconoCirculo(Iconos.Tipo tipo, Color fondo, Color color, int tam) {
            this.icono = Iconos.de(tipo, Math.round(tam * 0.5f), color);
            this.fondo = fondo;
            this.tam = tam;
            setOpaque(false);
        }
        public void setFondo(Color c) { fondo = c; repaint(); }
        @Override public Dimension getPreferredSize() { return new Dimension(tam, tam); }
        @Override public Dimension getMinimumSize() { return getPreferredSize(); }
        @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            suavizar(g2);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, tam, tam, tam * 2 / 3, tam * 2 / 3);
            icono.paintIcon(this, g2, (tam - icono.getIconWidth()) / 2, (tam - icono.getIconHeight()) / 2);
            g2.dispose();
        }
    }

    /** Linea separadora horizontal. */
    public static JComponent separador() {
        JComponent s = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(LINEA);
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(10, 1); }
            @Override public Dimension getMaximumSize() { return new Dimension(Integer.MAX_VALUE, 1); }
        };
        return s;
    }

    /** Encabezado de una seccion: titulo a la izquierda y una accion opcional a la derecha. */
    public static JPanel tituloSeccion(String titulo, JComponent accion) {
        JPanel p = panel(new BorderLayout());
        p.add(texto(titulo, h3(), TEXTO), BorderLayout.WEST);
        if (accion != null) p.add(accion, BorderLayout.EAST);
        return p;
    }

    /** Aviso en linea (informativo, exito, advertencia o error). */
    public static class Aviso extends Tarjeta implements Diseno.AltoSegunAncho {
        private final Parrafo mensaje;
        private final JLabel icono;
        public Aviso() {
            super(new BorderLayout(12, 0));
            conRelleno(12, 14);
            icono = new JLabel();
            icono.setVerticalAlignment(SwingConstants.TOP);
            add(icono, BorderLayout.WEST);
            mensaje = new Parrafo("", pequena(), TEXTO2);
            add(mensaje, BorderLayout.CENTER);
            setVisible(false);
        }
        public void mostrar(String msg, String tipo) {
            Color fondo, color;
            Iconos.Tipo ic;
            switch (tipo) {
                case "error":  fondo = ROJO_SUAVE;  color = ROJO_TEXTO;  ic = Iconos.Tipo.ALERTA; break;
                case "exito":  fondo = VERDE_SUAVE; color = VERDE_TEXTO; ic = Iconos.Tipo.CHECK; break;
                case "alerta": fondo = AMBAR_SUAVE; color = AMBAR_TEXTO; ic = Iconos.Tipo.ALERTA; break;
                default:       fondo = AZUL_SUAVE;  color = AZUL_TEXTO;  ic = Iconos.Tipo.INFO; break;
            }
            conFondo(fondo);
            conBorde(fondo);
            icono.setIcon(Iconos.de(ic, 18, color));
            mensaje.setForeground(mezclar(color, TEXTO, 0.35f));
            mensaje.setTexto(msg);
            setVisible(true);
            revalidate();
            repaint();
        }
        public void ocultar() { setVisible(false); revalidate(); }
        @Override public int altoPara(int ancho) {
            if (!isVisible()) return 0;
            return Math.max(mensaje.altoPara(ancho - 28 - 30), 18) + 24 + 3;
        }
    }

    /** Estado vacio: icono, titulo, descripcion y accion opcional. */
    public static JPanel vacio(Iconos.Tipo icono, String titulo, String descripcion, JButton accion) {
        Diseno.Columna c = new Diseno.Columna(420, 0, 40);
        JPanel ic = panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ic.add(new IconoCirculo(icono, TEAL_SUAVE, TEAL_OSCURO, 64));
        c.agregar(ic);
        c.espacio(16);
        JLabel t = texto(titulo, h2(), TEXTO);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        c.agregar(t);
        c.espacio(6);
        Parrafo d = new Parrafo(descripcion, cuerpo(), GRIS);
        d.centrar();
        c.agregar(d);
        if (accion != null) {
            c.espacio(18);
            JPanel b = panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            b.add(accion);
            c.agregar(b);
        }
        return c;
    }

    /** Fija el ancho preferido de un componente (por ejemplo un boton). */
    public static <T extends JComponent> T ancho(T c, int ancho) {
        Dimension d = c.getPreferredSize();
        c.setPreferredSize(new Dimension(ancho, d.height));
        return c;
    }
}
