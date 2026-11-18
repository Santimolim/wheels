package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/** Chat entre el usuario actual y otro usuario dentro de un viaje (tabla messages). */
public class DialogoChat extends JDialog {

    private final Usuario otro;
    private final Viaje viaje;
    private final JEditorPane area;
    private final Timer refresco;

    public DialogoChat(JFrame padre, Usuario otro, Viaje viaje) {
        super(padre, "Chat con " + otro.getNombre(), true);
        this.otro = otro;
        this.viaje = viaje;

        JPanel raiz = new JPanel(new BorderLayout(0, 8));
        raiz.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        raiz.setBackground(Estilo.FONDO);

        area = new JEditorPane("text/html", "");
        area.setEditable(false);
        JScrollPane sc = new JScrollPane(area);
        sc.setPreferredSize(new Dimension(330, 360));
        sc.setBorder(BorderFactory.createLineBorder(Estilo.LINEA));
        raiz.add(sc, BorderLayout.CENTER);

        JPanel abajo = new JPanel(new BorderLayout(6, 0));
        abajo.setOpaque(false);
        final JTextField txt = new JTextField();
        txt.setFont(Estilo.NORMAL);
        txt.setPreferredSize(new Dimension(240, 36));
        abajo.add(txt, BorderLayout.CENTER);
        JButton enviar = Estilo.botonPrimario("Enviar", 0, 0, 80);
        enviar.setPreferredSize(new Dimension(80, 36));
        abajo.add(enviar, BorderLayout.EAST);
        raiz.add(abajo, BorderLayout.SOUTH);

        Runnable mandar = () -> {
            String t = txt.getText().trim();
            if (t.isEmpty()) return;
            BaseDatos.enviarMensaje(otro.getId(), viaje.getId(), t.length() > 500 ? t.substring(0, 500) : t);
            txt.setText("");
            cargar();
        };
        enviar.addActionListener(e -> mandar.run());
        txt.addActionListener(e -> mandar.run());

        // Mensajes rapidos
        JPanel rapidos = new JPanel(new java.awt.GridLayout(1, 3, 4, 0));
        rapidos.setOpaque(false);
        for (String r : new String[]{"Ya voy saliendo", "Estoy en el punto", "Llego en 5 min"}) {
            JButton b = Estilo.botonBlanco(r, 0, 0, 100);
            b.setFont(Estilo.PEQUENA);
            b.setPreferredSize(new Dimension(100, 30));
            b.addActionListener(e -> { txt.setText(r); mandar.run(); });
            rapidos.add(b);
        }
        raiz.add(rapidos, BorderLayout.NORTH);

        setContentPane(raiz);
        pack();
        setLocationRelativeTo(padre);
        cargar();

        // Revisa mensajes nuevos cada 5 segundos
        refresco = new Timer(5000, e -> cargar());
        refresco.start();
    }

    private void cargar() {
        StringBuilder sb = new StringBuilder("<html><body style='font-family:Segoe UI;font-size:11px;margin:6px'>");
        java.util.ArrayList<String[]> lista = BaseDatos.mensajes(otro.getId(), viaje.getId());
        if (lista.isEmpty()) {
            sb.append("<p style='color:#7a8699'>Aun no hay mensajes. Saluda a ")
              .append(otro.getPrimerNombre()).append("!</p>");
        }
        for (String[] m : lista) {
            boolean yo = m[0].equals("Yo");
            sb.append("<div style='margin:4px 0;text-align:").append(yo ? "right" : "left").append("'>")
              .append("<span style='background:").append(yo ? "#1e7be0;color:white" : "#e4e9f0;color:#0f1b33")
              .append(";padding:4px'>&nbsp;").append(escapar(m[1])).append("&nbsp;</span><br>")
              .append("<span style='color:#7a8699;font-size:9px'>").append(m[0]).append(" ").append(m[2])
              .append("</span></div>");
        }
        sb.append("</body></html>");
        area.setText(sb.toString());
        area.setCaretPosition(area.getDocument().getLength());
    }

    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    public void dispose() {
        refresco.stop();
        super.dispose();
    }
}
