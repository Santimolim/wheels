package wheels.vistas;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;

/** PANTALLA 9 - My Trips. Pendientes, aceptados e historial. */
public class VentanaMisViajes extends VentanaBase {

    private JPanel lista;
    private JScrollPane scroll;
    private JButton bPendientes, bAceptados, bHistorial;
    private String estadoActual = Solicitud.PENDIENTE;

    public VentanaMisViajes() {
        super("Mis viajes");

        lienzo.add(Estilo.encabezado("Mis viajes", "Solicitudes y viajes realizados"));

        bPendientes = pestana("Pendientes", 20, Solicitud.PENDIENTE);
        bAceptados  = pestana("Aceptados", 130, Solicitud.ACEPTADA);
        bHistorial  = pestana("Historial", 240, Solicitud.COMPLETADA);
        lienzo.add(bPendientes);
        lienzo.add(bAceptados);
        lienzo.add(bHistorial);

        lista = new JPanel(null);
        lista.setBackground(Estilo.FONDO);

        scroll = new JScrollPane(lista);
        scroll.setBounds(20, 140, 340, 485);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        lienzo.add(scroll);

        mostrar(Solicitud.PENDIENTE);

        agregarBarraInferior("Trips");
    }

    private JButton pestana(String texto, int x, final String estado) {
        JButton b = new JButton(texto);
        b.setBounds(x, 95, 105, 32);
        b.setFont(Estilo.PEQUENA);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.addActionListener(e -> mostrar(estado));
        return b;
    }

    /** Vuelve a llenar la lista con las solicitudes del estado indicado. */
    private void mostrar(String estado) {
        estadoActual = estado;
        pintarPestanas();

        lista.removeAll();

        ArrayList<Solicitud> datos = BaseDatos.solicitudesPorEstado(estado);

        int y = 0;
        if (datos.isEmpty()) {
            lista.add(Estilo.texto("No tienes viajes en esta seccion.", 0, 10, 300,
                    Estilo.NORMAL, Estilo.GRIS));
            y = 60;
        } else {
            for (Solicitud s : datos) {
                JPanel t = tarjeta(s);
                t.setBounds(0, y, 300, 130);
                lista.add(t);
                y += 140;
            }
        }

        lista.setPreferredSize(new Dimension(300, y));
        lista.revalidate();
        lista.repaint();
    }

    private void pintarPestanas() {
        pintar(bPendientes, estadoActual.equals(Solicitud.PENDIENTE));
        pintar(bAceptados,  estadoActual.equals(Solicitud.ACEPTADA));
        pintar(bHistorial,  estadoActual.equals(Solicitud.COMPLETADA));
    }

    private void pintar(JButton b, boolean activo) {
        b.setBackground(activo ? Estilo.NAVY : Estilo.LINEA);
        b.setForeground(activo ? Estilo.BLANCO : Estilo.GRIS);
    }

    /** Tarjeta de una solicitud. */
    private JPanel tarjeta(final Solicitud s) {
        JPanel p = new JPanel(null);
        p.setBackground(Estilo.BLANCO);
        p.setBorder(javax.swing.BorderFactory.createLineBorder(Estilo.LINEA, 1));
        p.setPreferredSize(new Dimension(300, 130));
        p.setMaximumSize(new Dimension(300, 130));

        p.add(Estilo.avatar(s.getViaje().getConductor().getIniciales(), 12, 12, 36));
        p.add(Estilo.texto(s.getViaje().getConductor().getNombre(), 58, 12, 160,
                Estilo.SUBTITULO, Estilo.TEXTO));
        p.add(Estilo.texto(s.getViaje().getRuta(), 58, 32, 240, Estilo.PEQUENA, Estilo.GRIS));
        p.add(Estilo.estado(s.getEstado(), 206, 12));

        p.add(Estilo.separador(12, 62, 276));
        p.add(Estilo.texto(s.getViaje().getFecha() + "  -  " + s.getViaje().getHora(),
                12, 70, 200, Estilo.NORMAL, Estilo.GRIS));
        JLabel precio = Estilo.texto(s.getViaje().getPrecioTexto(), 170, 68, 118,
                Estilo.SUBTITULO, Estilo.NAVY);
        precio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        p.add(precio);

        if (s.getEstado().equals(Solicitud.PENDIENTE)) {
            JButton aceptar = Estilo.botonPequeno("Simular aceptacion", 12, 94, 150);
            aceptar.setBackground(Estilo.VERDE);
            aceptar.addActionListener(e -> {
                s.aceptar();
                mostrar(Solicitud.ACEPTADA);
            });
            p.add(aceptar);
        }

        if (s.getEstado().equals(Solicitud.ACEPTADA)) {
            JButton completar = Estilo.botonPequeno("Marcar como realizado", 12, 94, 170);
            completar.addActionListener(e -> {
                s.completar();
                mostrar(Solicitud.COMPLETADA);
            });
            p.add(completar);
        }

        return p;
    }
}
