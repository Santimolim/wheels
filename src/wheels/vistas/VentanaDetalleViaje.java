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
        boolean esMio = viaje.getConductor().getId() == BaseDatos.usuarioActual.getId();

        lienzo.add(Estilo.encabezado("Detalle del viaje", "Revisa antes de solicitar"));

        agregarVolverEncabezado();

        // ---------- Conductor ----------
        JPanel pConductor = Estilo.tarjeta(20, 92, 340, 72);
        pConductor.add(Estilo.avatar(viaje.getConductor().getIniciales(), 14, 14, 42));
        pConductor.add(Estilo.texto(viaje.getConductor().getNombre(), 66, 12, 190,
                Estilo.SUBTITULO, Estilo.TEXTO));
        pConductor.add(Estilo.pastilla(Estilo.estrellas(viaje.getConductor().getCalificacion()),
                264, 14, 62, new java.awt.Color(254, 243, 199), new java.awt.Color(180, 83, 9)));
        pConductor.add(Estilo.texto(viaje.getConductor().getUniversidad(), 66, 30, 260,
                Estilo.PEQUENA, Estilo.GRIS));
        pConductor.add(Estilo.texto(viaje.getConductor().getCarrera(), 66, 44, 260,
                Estilo.PEQUENA, Estilo.GRIS));
        lienzo.add(pConductor);

        // ---------- Datos del viaje ----------
        JPanel pDatos = Estilo.tarjeta(20, 172, 340, 330);

        int y = 12;
        y = fila(pDatos, "Vehiculo",  auto.getMarca() + " " + auto.getModelo() + " " + auto.getAnio(), y);
        y = fila(pDatos, "Color / Placa", auto.getColor() + "  -  " + auto.getPlaca(), y);
        y = fila(pDatos, "Origen",    viaje.getOrigen(), y);
        y = fila(pDatos, "Destino",   viaje.getDestino(), y);
        y = fila(pDatos, "Fecha",     viaje.getFecha(), y);
        y = fila(pDatos, "Hora",      viaje.getHora(), y);
        y = fila(pDatos, "Puestos disponibles",
                viaje.getPuestosDisponibles() + " de " + auto.getPuestos(), y);
        y = fila(pDatos, "Paradas", BaseDatos.puntosRecogida(viaje.getId()).size() + " puntos de recogida", y);
        y = fila(pDatos, "Precio por puesto", viaje.getPrecioTexto(), y);
        pDatos.add(Estilo.texto(BaseDatos.preferencias(viaje.getId()), 14, y, 312,
                Estilo.PEQUENA, Estilo.AZUL));

        lienzo.add(pDatos);

        // ---------- Botones ----------
        JButton mapa = Estilo.botonOscuro("Ver ruta en el mapa", 20, 510, 340);
        mapa.addActionListener(e -> irA(new VentanaMapa()));
        lienzo.add(mapa);

        JButton solicitar = Estilo.botonPrimario(esMio ? "Este es tu viaje" : "Request Seat", 20, 556, 340);
        solicitar.setEnabled(!esMio);
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

        JButton atras = Estilo.botonBlanco("Volver", 20, 606, 340);
        atras.addActionListener(e -> volver());
        lienzo.add(atras);
    }

    /** Dibuja una fila "etiqueta ....... valor" y devuelve la siguiente posicion Y. */
    private int fila(JPanel panel, String etiqueta, String valor, int y) {
        panel.add(Estilo.texto(etiqueta, 14, y, 150, Estilo.NORMAL, Estilo.GRIS));
        javax.swing.JLabel v = Estilo.texto(valor, 140, y, 186, Estilo.SUBTITULO, Estilo.TEXTO);
        v.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        panel.add(v);
        panel.add(Estilo.separador(14, y + 26, 312));
        return y + 32;
    }
}
