package wheels.vistas;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wheels.datos.BaseDatos;

/** PANTALLA - Notificaciones del usuario (tabla notifications). */
public class VentanaNotificaciones extends VentanaBase {

    public VentanaNotificaciones() {
        super("Notificaciones");

        ArrayList<String[]> datos = BaseDatos.notificaciones();
        int sinLeer = BaseDatos.notificacionesSinLeer();

        lienzo.add(Estilo.encabezado("Notificaciones",
                sinLeer == 0 ? "Estas al dia" : sinLeer + " sin leer"));

        agregarVolverEncabezado();

        JPanel lista = new JPanel(null);
        lista.setBackground(Estilo.FONDO);
        int y = 0;
        for (String[] n : datos) {
            boolean leida = n[3].equals("1");
            boolean panico = n[4] != null && n[4].startsWith("panic");
            JPanel t = Estilo.tarjeta(0, y, 325, 78);
            Color punto = panico ? Estilo.ROJO : leida ? Estilo.LINEA : Estilo.AZUL;
            t.add(Estilo.pastilla("", 12, 14, 10, punto, punto));
            t.add(Estilo.texto(n[0], 30, 8, 280, Estilo.SUBTITULO, panico ? Estilo.ROJO : Estilo.TEXTO));
            JLabel msg = new JLabel("<html>" + n[1] + "</html>");
            msg.setBounds(30, 28, 285, 30);
            msg.setFont(Estilo.PEQUENA);
            msg.setForeground(Estilo.GRIS);
            msg.setVerticalAlignment(JLabel.TOP);
            t.add(msg);
            t.add(Estilo.texto(n[2] == null ? "" : n[2], 30, 56, 200, new java.awt.Font("Segoe UI", 0, 9), Estilo.GRIS));
            lista.add(t);
            y += 86;
        }
        if (datos.isEmpty()) {
            lista.add(Estilo.texto("No tienes notificaciones.", 0, 10, 300, Estilo.NORMAL, Estilo.GRIS));
            y = 40;
        }
        lista.setPreferredSize(new Dimension(325, y));

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBounds(20, 95, 345, 540);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        lienzo.add(scroll);

        BaseDatos.marcarNotificacionesLeidas();
        agregarBarraInferior("Home");
    }
}
