package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.View;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/** Chat entre el usuario actual y otro usuario dentro de un viaje (tabla messages). */
public class DialogoChat extends JDialog {

    private static final int ANCHO = 440;
    private final Usuario otro;
    private final Viaje viaje;
    private final Diseno.Columna lista = new Diseno.Columna(100000, 16, 16).conSeparacion(6);
    private final JScrollPane scroll;
    private final JTextField txt;
    private final Timer refresco;
    private int cantidad = -1;

    public static void abrir(Component padre, Usuario otro, Viaje viaje) {
        DialogoChat d = new DialogoChat(SwingUtilities.getWindowAncestor(padre), otro, viaje);
        Aplicacion.oscurecer(true);
        try {
            d.setVisible(true);
        } finally {
            Aplicacion.oscurecer(false);
            Aplicacion.actualizarInsignias();
        }
    }

    private DialogoChat(Window padre, Usuario otro, Viaje viaje) {
        super(padre, "Chat con " + otro.getNombre(), ModalityType.APPLICATION_MODAL);
        this.otro = otro;
        this.viaje = viaje;
        setUndecorated(true);

        JPanel raiz = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Estilo.suavizar(g2);
                g2.setColor(Estilo.FONDO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                g2.dispose();
            }
        };
        raiz.setOpaque(false);

        // ---------- Encabezado ----------
        JPanel cab = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Estilo.suavizar(g2);
                g2.setColor(Estilo.BLANCO);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 22, 22, 22);
                g2.setColor(Estilo.LINEA);
                g2.fillRect(0, getHeight() - 1, getWidth(), 1);
                g2.dispose();
            }
        };
        cab.setOpaque(false);
        cab.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 12));
        cab.add(new Estilo.Avatar(otro.getIniciales(), otro.getEmail(), 42), BorderLayout.WEST);
        JPanel textos = Estilo.panel(new java.awt.GridLayout(2, 1));
        textos.add(Estilo.texto(otro.getNombre(), Estilo.h3(), Estilo.TEXTO));
        JLabel ruta = Estilo.texto(viaje.getRuta() + " · " + viaje.getFechaAmigable(), Estilo.pequena(), Estilo.GRIS);
        textos.add(ruta);
        cab.add(textos, BorderLayout.CENTER);
        JButton cerrar = Estilo.botonIcono(Iconos.Tipo.CERRAR, "Cerrar chat (Esc)", Estilo.GRIS, 36);
        cerrar.addActionListener(e -> dispose());
        JPanel c2 = Estilo.panel(new java.awt.GridBagLayout());
        c2.add(cerrar);
        cab.add(c2, BorderLayout.EAST);
        raiz.add(cab, BorderLayout.NORTH);

        // ---------- Mensajes ----------
        scroll = new JScrollPane(lista, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        raiz.add(scroll, BorderLayout.CENTER);

        // ---------- Escribir ----------
        JPanel abajo = new JPanel(new BorderLayout(0, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Estilo.suavizar(g2);
                g2.setColor(Estilo.BLANCO);
                g2.fillRoundRect(0, -22, getWidth(), getHeight() + 22, 22, 22);
                g2.setColor(Estilo.LINEA);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        abajo.setOpaque(false);
        abajo.setBorder(BorderFactory.createEmptyBorder(12, 14, 14, 14));

        txt = Estilo.campo("Escribe un mensaje…", null);
        Diseno.FilaFlexible rapidos = new Diseno.FilaFlexible(6, 6);
        for (String r : new String[]{"Ya voy saliendo", "Estoy en el punto", "Llego en 5 min", "¡Gracias!"}) {
            JButton b = Estilo.boton(r, Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, null);
            b.setFont(Estilo.fuente(12, Estilo.SEMI));
            b.addActionListener(e -> enviar(r));
            rapidos.add(b);
        }
        JPanel rapCont = Estilo.panel(new BorderLayout());
        rapCont.add(rapidos, BorderLayout.CENTER);
        rapCont.setPreferredSize(new Dimension(ANCHO - 28, rapidos.altoPara(ANCHO - 28)));
        abajo.add(rapCont, BorderLayout.NORTH);

        JPanel fila = Estilo.panel(new BorderLayout(8, 0));
        fila.add(txt, BorderLayout.CENTER);
        JButton enviar = Estilo.boton("", Estilo.TipoBoton.ACENTO, Estilo.Tamano.NORMAL, Iconos.Tipo.ENVIAR);
        enviar.setPreferredSize(new Dimension(48, 42));
        enviar.setToolTipText("Enviar (Enter)");
        enviar.getAccessibleContext().setAccessibleName("Enviar mensaje");
        enviar.addActionListener(e -> enviar(txt.getText()));
        fila.add(enviar, BorderLayout.EAST);
        abajo.add(fila, BorderLayout.SOUTH);
        raiz.add(abajo, BorderLayout.SOUTH);

        txt.addActionListener(e -> enviar(txt.getText()));
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        setContentPane(raiz);
        int alto = padre == null ? 620 : Math.min(640, padre.getHeight() - 40);
        int ancho = padre == null ? ANCHO : Math.min(ANCHO, padre.getWidth() - 24);
        setSize(ancho, Math.max(420, alto));
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 22, 22));
        setLocationRelativeTo(padre);

        cargar();
        // Revisa mensajes nuevos cada 4 segundos (solo redibuja si llego algo)
        refresco = new Timer(4000, e -> cargar());
        refresco.start();
        SwingUtilities.invokeLater(txt::requestFocusInWindow);
    }

    private void enviar(String texto) {
        String t = texto.trim();
        if (t.isEmpty()) return;
        if (t.length() > 500) t = t.substring(0, 500);
        if (BaseDatos.enviarMensaje(otro.getId(), viaje.getId(), t)) {
            txt.setText("");
            cargar();
        }
        txt.requestFocusInWindow();
    }

    private void cargar() {
        ArrayList<String[]> mensajes = BaseDatos.mensajes(otro.getId(), viaje.getId());
        if (mensajes.size() == cantidad) return;
        cantidad = mensajes.size();
        lista.removeAll();
        if (mensajes.isEmpty()) {
            lista.agregar(Estilo.vacio(Iconos.Tipo.CHAT, "Inicia la conversación",
                    "Saluda a " + otro.getPrimerNombre() + " o coordina el punto de encuentro.", null));
        }
        String ultimoAutor = "";
        for (String[] m : mensajes) {
            boolean mio = "1".equals(m[3]);
            if (!m[0].equals(ultimoAutor)) lista.espacio(6);
            lista.agregar(new Burbuja(m[1], m[2], mio));
            ultimoAutor = m[0];
        }
        lista.revalidate();
        lista.repaint();
        SwingUtilities.invokeLater(() -> {
            scroll.validate();
            scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
        });
    }

    @Override
    public void dispose() {
        if (refresco != null) refresco.stop();
        super.dispose();
    }

    /** Burbuja de mensaje: a la derecha (mio, color de la marca) o a la izquierda (del otro). */
    private static final class Burbuja extends JPanel implements Diseno.AltoSegunAncho {
        private final Estilo.Parrafo texto;
        private final JLabel hora;
        private final boolean mio;

        Burbuja(String mensaje, String hora, boolean mio) {
            super(null);
            setOpaque(false);
            this.mio = mio;
            texto = new Estilo.Parrafo(mensaje, Estilo.cuerpo(), mio ? Color.WHITE : Estilo.TEXTO);
            this.hora = Estilo.texto(hora, Estilo.fuente(11, Estilo.REGULAR), mio ? new Color(0xCCFBF1) : Estilo.GRIS_CLARO);
            add(texto);
            add(this.hora);
        }

        private int anchoTexto(int anchoTotal) {
            int max = (int) (anchoTotal * 0.78) - 28;
            View v = (View) texto.getClientProperty("html");
            int natural = v == null ? texto.getPreferredSize().width : (int) Math.ceil(v.getPreferredSpan(View.X_AXIS)) + 2;
            return Math.max(Math.min(natural, max), hora.getPreferredSize().width);
        }

        @Override public int altoPara(int ancho) {
            return texto.altoPara(anchoTexto(ancho)) + 16 + hora.getPreferredSize().height + 2;
        }

        @Override public void doLayout() {
            int tw = anchoTexto(getWidth());
            int bw = tw + 28;
            int x = mio ? getWidth() - bw : 0;
            int th = texto.altoPara(tw);
            texto.setBounds(x + 14, 8, tw, th);
            int hw = hora.getPreferredSize().width;
            hora.setBounds(x + bw - 14 - hw, 8 + th, hw, hora.getPreferredSize().height);
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.suavizar(g2);
            int bw = anchoTexto(getWidth()) + 28;
            int x = mio ? getWidth() - bw : 0;
            g2.setColor(mio ? Estilo.TEAL_OSCURO : Estilo.BLANCO);
            g2.fillRoundRect(x, 0, bw, getHeight(), 18, 18);
            // esquina recta del lado del autor
            g2.fillRect(mio ? x + bw - 10 : x, getHeight() - 10, 10, 10);
            if (!mio) {
                g2.setColor(Estilo.LINEA);
                g2.drawRoundRect(x, 0, bw - 1, getHeight() - 1, 18, 18);
            }
            g2.dispose();
        }
    }
}
