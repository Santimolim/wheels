package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA 7 - Request Confirmation. */
public class VentanaConfirmarSolicitud extends VentanaBase {

    public VentanaConfirmarSolicitud() {
        super("Confirmar solicitud");

        final Viaje viaje = BaseDatos.viajeSeleccionado;

        JLabel t = new JLabel("Confirma tu solicitud", SwingConstants.CENTER);
        t.setBounds(0, 110, Estilo.ANCHO, 30);
        t.setFont(Estilo.TITULO);
        t.setForeground(Estilo.TEXTO);
        lienzo.add(t);

        JLabel s = new JLabel("<html><div style='text-align:center;'>Se enviara una solicitud a "
                + viaje.getConductor().getNombre().split(" ")[0]
                + ".<br>Debe aceptarla para reservar tu puesto.</div></html>", SwingConstants.CENTER);
        s.setBounds(30, 145, 320, 48);
        s.setFont(Estilo.NORMAL);
        s.setForeground(Estilo.GRIS);
        lienzo.add(s);

        JPanel resumen = Estilo.tarjeta(20, 215, 340, 190);
        resumen.add(Estilo.avatar(viaje.getConductor().getIniciales(), 14, 14, 40));
        resumen.add(Estilo.texto(viaje.getConductor().getNombre(), 64, 16, 250,
                Estilo.SUBTITULO, Estilo.TEXTO));
        resumen.add(Estilo.texto(viaje.getVehiculo().toString(), 64, 34, 250,
                Estilo.PEQUENA, Estilo.GRIS));
        resumen.add(Estilo.separador(14, 66, 312));
        resumen.add(Estilo.texto("Origen:  " + viaje.getOrigen(), 14, 78, 300,
                Estilo.NORMAL, Estilo.TEXTO));
        resumen.add(Estilo.texto("Destino: " + viaje.getDestino(), 14, 98, 300,
                Estilo.NORMAL, Estilo.TEXTO));
        resumen.add(Estilo.texto(viaje.getFecha() + "  -  " + viaje.getHora(), 14, 120, 300,
                Estilo.NORMAL, Estilo.GRIS));
        resumen.add(Estilo.separador(14, 146, 312));
        resumen.add(Estilo.texto("Total a pagar", 14, 156, 160, Estilo.NORMAL, Estilo.GRIS));
        JLabel precio = Estilo.texto(viaje.getPrecioTexto(), 180, 152, 146, Estilo.TITULO, Estilo.AZUL);
        precio.setHorizontalAlignment(SwingConstants.RIGHT);
        resumen.add(precio);
        lienzo.add(resumen);

        JButton confirmar = Estilo.botonPrimario("Confirm Request", 20, 440, 340);
        confirmar.addActionListener(e -> {
            BaseDatos.crearSolicitud(viaje, BaseDatos.usuarioActual);
            irA(new VentanaSolicitudEnviada());
        });
        lienzo.add(confirmar);

        JButton cancelar = Estilo.botonBlanco("Cancel", 20, 492, 340);
        cancelar.addActionListener(e -> irA(new VentanaDetalleViaje()));
        lienzo.add(cancelar);
    }
}
