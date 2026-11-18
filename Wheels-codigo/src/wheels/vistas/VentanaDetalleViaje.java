package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/** PANTALLA 6 - Trip Details. */
public class VentanaDetalleViaje extends VentanaBase {

    public VentanaDetalleViaje() {
        super("Detalle del viaje");

        final Viaje viaje = BaseDatos.viajeSeleccionado;
        Vehiculo auto = viaje.getVehiculo();

        lienzo.add(Estilo.encabezado("Detalle del viaje", "Revisa antes de solicitar"));

        JButton volver = Estilo.enlace("< Volver", 280, 20, 80);
        volver.setForeground(Estilo.BLANCO);
        volver.addActionListener(e -> irA(new VentanaViajesDisponibles()));
        lienzo.add(volver);

        // ---------- Conductor ----------
        JPanel pConductor = Estilo.tarjeta(20, 95, 340, 70);
        pConductor.add(Estilo.avatar(viaje.getConductor().getIniciales(), 14, 14, 42));
        pConductor.add(Estilo.texto(viaje.getConductor().getNombre(), 66, 14, 260,
                Estilo.SUBTITULO, Estilo.TEXTO));
        pConductor.add(Estilo.texto(viaje.getConductor().getUniversidad(), 66, 32, 260,
                Estilo.PEQUENA, Estilo.GRIS));
        pConductor.add(Estilo.texto(viaje.getConductor().getCarrera(), 66, 46, 260,
                Estilo.PEQUENA, Estilo.GRIS));
        lienzo.add(pConductor);

        // ---------- Datos del viaje ----------
        JPanel pDatos = Estilo.tarjeta(20, 178, 340, 300);

        int y = 14;
        y = fila(pDatos, "Vehiculo",  auto.getMarca() + " " + auto.getModelo(), y);
        y = fila(pDatos, "Color",     auto.getColor(), y);
        y = fila(pDatos, "Placa",     auto.getPlaca(), y);
        y = fila(pDatos, "Origen",    viaje.getOrigen(), y);
        y = fila(pDatos, "Destino",   viaje.getDestino(), y);
        y = fila(pDatos, "Fecha",     viaje.getFecha(), y);
        y = fila(pDatos, "Hora",      viaje.getHora(), y);
        y = fila(pDatos, "Puestos disponibles",
                viaje.getPuestosDisponibles() + " de " + auto.getPuestos(), y);
        y = fila(pDatos, "Precio por puesto", viaje.getPrecioTexto(), y);

        lienzo.add(pDatos);

        // ---------- Boton principal ----------
        JButton solicitar = Estilo.botonPrimario("Request Seat", 20, 500, 340);
        solicitar.addActionListener(e -> {
            if (BaseDatos.yaSolicito(viaje)) {
                JOptionPane.showMessageDialog(this,
                        "Ya enviaste una solicitud para este viaje.",
                        "Wheels", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            irA(new VentanaConfirmarSolicitud());
        });
        lienzo.add(solicitar);

        JButton atras = Estilo.botonBlanco("Volver a la lista", 20, 550, 340);
        atras.addActionListener(e -> irA(new VentanaViajesDisponibles()));
        lienzo.add(atras);
    }

    /** Dibuja una fila "etiqueta ....... valor" y devuelve la siguiente posicion Y. */
    private int fila(JPanel panel, String etiqueta, String valor, int y) {
        panel.add(Estilo.texto(etiqueta, 14, y, 180, Estilo.NORMAL, Estilo.GRIS));
        javax.swing.JLabel v = Estilo.texto(valor, 160, y, 166, Estilo.SUBTITULO, Estilo.TEXTO);
        v.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        panel.add(v);
        panel.add(Estilo.separador(14, y + 26, 312));
        return y + 32;
    }
}
