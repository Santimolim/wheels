package wheels.vistas;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;

/**
 * PANTALLA 9 - My Trips.
 * Como pasajero: pendientes, aceptados (con boton Iniciar viaje) e historial.
 * Como conductor: solicitudes recibidas para aceptar o rechazar.
 */
public class VentanaMisViajes extends VentanaBase {

    private static final String CONDUCTOR = "Conductor";

    private JPanel lista;
    private JButton bPendientes, bAceptados, bHistorial, bConductor;
    private String estadoActual;

    public VentanaMisViajes() {
        this(Solicitud.PENDIENTE);
    }

    public VentanaMisViajes(String pestanaInicial) {
        super("Mis viajes");

        lienzo.add(Estilo.encabezado("Mis viajes", "Tus solicitudes y viajes en tiempo real"));

        bPendientes = pestana("Pendientes", 12, Solicitud.PENDIENTE);
        bAceptados  = pestana("Aceptados", 101, Solicitud.ACEPTADA);
        bHistorial  = pestana("Historial", 190, Solicitud.COMPLETADA);
        bConductor  = pestana("Conductor", 279, CONDUCTOR);
        lienzo.add(bPendientes);
        lienzo.add(bAceptados);
        lienzo.add(bHistorial);
        lienzo.add(bConductor);

        lista = new JPanel(null);
        lista.setBackground(Estilo.FONDO);

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBounds(20, 140, 345, 495);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        lienzo.add(scroll);

        mostrar(pestanaInicial);

        agregarBarraInferior("Trips");
    }

    private JButton pestana(String texto, int x, final String estado) {
        JButton b = Estilo.botonPequeno(texto, x, 95, 85);
        b.setBounds(x, 95, 85, 32);
        b.addActionListener(e -> mostrar(estado));
        return b;
    }

    /** Vuelve a llenar la lista con las solicitudes del estado indicado. */
    private void mostrar(String estado) {
        estadoActual = estado;
        pintarPestanas();

        lista.removeAll();

        ArrayList<Solicitud> datos;
        if (estado.equals(CONDUCTOR)) {
            datos = BaseDatos.solicitudesRecibidas();
        } else if (estado.equals(Solicitud.ACEPTADA)) {
            // los viajes en curso tambien se muestran en Aceptados (primero)
            datos = BaseDatos.solicitudesPorEstado(Solicitud.EN_CURSO);
            datos.addAll(BaseDatos.solicitudesPorEstado(Solicitud.ACEPTADA));
        } else {
            datos = BaseDatos.solicitudesPorEstado(estado);
        }

        int y = 0;
        if (datos.isEmpty()) {
            String msg = estado.equals(CONDUCTOR)
                    ? "No tienes solicitudes nuevas en tus viajes."
                    : "No tienes viajes en esta seccion.";
            lista.add(Estilo.texto(msg, 0, 10, 320, Estilo.NORMAL, Estilo.GRIS));
            y = 60;
        } else {
            for (Solicitud s : datos) {
                JPanel t = estado.equals(CONDUCTOR) ? tarjetaConductor(s) : tarjeta(s);
                t.setBounds(0, y, 325, 136);
                lista.add(t);
                y += 146;
            }
        }

        lista.setPreferredSize(new Dimension(325, y));
        lista.revalidate();
        lista.repaint();
    }

    private void pintarPestanas() {
        pintar(bPendientes, estadoActual.equals(Solicitud.PENDIENTE));
        pintar(bAceptados,  estadoActual.equals(Solicitud.ACEPTADA));
        pintar(bHistorial,  estadoActual.equals(Solicitud.COMPLETADA));
        pintar(bConductor,  estadoActual.equals(CONDUCTOR));
    }

    private void pintar(JButton b, boolean activo) {
        b.setBackground(activo ? Estilo.NAVY : Estilo.LINEA);
        b.setForeground(activo ? Estilo.BLANCO : Estilo.GRIS);
    }

    /** Tarjeta de una solicitud hecha por mi (pasajero). */
    private JPanel tarjeta(final Solicitud s) {
        JPanel p = Estilo.tarjeta(0, 0, 325, 136);

        p.add(Estilo.avatar(s.getViaje().getConductor().getIniciales(), 12, 12, 36));
        p.add(Estilo.texto(s.getViaje().getConductor().getNombre(), 58, 10, 170,
                Estilo.SUBTITULO, Estilo.TEXTO));
        p.add(Estilo.texto(s.getViaje().getRuta(), 58, 30, 250, Estilo.PEQUENA, Estilo.GRIS));
        p.add(Estilo.estado(s.getEstado(), 232, 12));

        p.add(Estilo.separador(12, 60, 301));
        p.add(Estilo.texto(s.getViaje().getFecha() + "  -  " + s.getViaje().getHora(),
                12, 68, 200, Estilo.NORMAL, Estilo.GRIS));
        JLabel precio = Estilo.texto(s.getViaje().getPrecioTexto(), 190, 66, 123,
                Estilo.SUBTITULO, Estilo.NAVY);
        precio.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        p.add(precio);

        if (s.getEstado().equals(Solicitud.ACEPTADA) || s.getEstado().equals(Solicitud.EN_CURSO)) {
            boolean enCurso = s.getEstado().equals(Solicitud.EN_CURSO);
            JButton iniciar = Estilo.botonPequeno(enCurso ? "Continuar viaje" : "Iniciar viaje", 12, 96, 120);
            iniciar.setBackground(Estilo.VERDE);
            iniciar.addActionListener(e -> {
                if (!enCurso) BaseDatos.cambiarEstado(s, Solicitud.EN_CURSO);
                BaseDatos.solicitudEnCurso = s;
                irA(new VentanaViajeEnCurso());
            });
            p.add(iniciar);

            JButton chat = Estilo.botonPequeno("Chat", 138, 96, 70);
            chat.addActionListener(e -> new DialogoChat(this, s.getViaje().getConductor(), s.getViaje()).setVisible(true));
            p.add(chat);
        }

        JButton mapa = Estilo.botonPequeno("Ver mapa", 214, 96, 99);
        mapa.setBackground(Estilo.AZUL);
        mapa.addActionListener(e -> {
            BaseDatos.viajeSeleccionado = s.getViaje();
            irA(new VentanaMapa());
        });
        p.add(mapa);

        return p;
    }

    /** Tarjeta de una solicitud que recibi en uno de mis viajes (conductor). */
    private JPanel tarjetaConductor(final Solicitud s) {
        JPanel p = Estilo.tarjeta(0, 0, 325, 136);

        p.add(Estilo.avatar(s.getPasajero().getIniciales(), 12, 12, 36));
        p.add(Estilo.texto(s.getPasajero().getNombre() + "  " + Estilo.estrellas(s.getPasajero().getCalificacion()),
                58, 10, 175, Estilo.SUBTITULO, Estilo.TEXTO));
        p.add(Estilo.texto(s.getPasajero().getCarrera(), 58, 30, 250, Estilo.PEQUENA, Estilo.GRIS));
        p.add(Estilo.estado(s.getEstado(), 232, 12));

        p.add(Estilo.separador(12, 60, 301));
        p.add(Estilo.texto(s.getViaje().getRuta(), 12, 66, 300, Estilo.NORMAL, Estilo.TEXTO));
        p.add(Estilo.texto(s.getViaje().getFecha() + "  -  " + s.getViaje().getHora() + "  -  "
                + s.getViaje().getPuestosDisponibles() + " puestos libres", 12, 82, 300, Estilo.PEQUENA, Estilo.GRIS));

        if (s.getEstado().equals(Solicitud.PENDIENTE)) {
            JButton aceptar = Estilo.botonPequeno("Aceptar", 12, 102, 95);
            aceptar.setBackground(Estilo.VERDE);
            aceptar.addActionListener(e -> {
                if (s.getViaje().getPuestosDisponibles() <= 0) {
                    JOptionPane.showMessageDialog(this, "Este viaje ya no tiene puestos libres.",
                            "Wheels", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                BaseDatos.cambiarEstado(s, Solicitud.ACEPTADA);
                mostrar(CONDUCTOR);
            });
            p.add(aceptar);

            JButton rechazar = Estilo.botonPequeno("Rechazar", 113, 102, 95);
            rechazar.setBackground(Estilo.ROJO);
            rechazar.addActionListener(e -> {
                BaseDatos.cambiarEstado(s, Solicitud.RECHAZADA);
                mostrar(CONDUCTOR);
            });
            p.add(rechazar);
        }

        JButton chat = Estilo.botonPequeno("Chat", 214, 102, 99);
        chat.addActionListener(e -> new DialogoChat(this, s.getPasajero(), s.getViaje()).setVisible(true));
        p.add(chat);

        return p;
    }
}
