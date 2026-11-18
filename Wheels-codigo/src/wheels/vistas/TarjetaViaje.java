package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JLabel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/**
 * Tarjeta que muestra un viaje dentro de una lista.
 * Se usa en Available Trips y en el Home.
 */
public class TarjetaViaje extends javax.swing.JPanel {

    public static final int ALTO = 140;

    public TarjetaViaje(final VentanaBase ventana, final Viaje viaje, int ancho) {
        setLayout(null);
        setBackground(Estilo.BLANCO);
        setBorder(javax.swing.BorderFactory.createLineBorder(Estilo.LINEA, 1));
        setPreferredSize(new java.awt.Dimension(ancho, ALTO));
        setMaximumSize(new java.awt.Dimension(ancho, ALTO));

        add(Estilo.avatar(viaje.getConductor().getIniciales(), 12, 12, 36));

        JLabel nombre = Estilo.texto(viaje.getConductor().getNombre(), 58, 12, 200,
                Estilo.SUBTITULO, Estilo.TEXTO);
        add(nombre);

        JLabel uni = Estilo.texto(viaje.getConductor().getUniversidad(), 58, 30, 220,
                Estilo.PEQUENA, Estilo.GRIS);
        add(uni);

        JLabel origen = Estilo.texto("Origen:  " + viaje.getOrigen(), 12, 58, 260,
                Estilo.NORMAL, Estilo.TEXTO);
        add(origen);

        JLabel destino = Estilo.texto("Destino: " + viaje.getDestino(), 12, 76, 260,
                Estilo.NORMAL, Estilo.TEXTO);
        add(destino);

        JLabel datos = Estilo.texto(viaje.getFecha() + "  -  " + viaje.getHora()
                + "  -  " + viaje.getPuestosDisponibles() + " puestos",
                12, 96, 260, Estilo.PEQUENA, Estilo.GRIS);
        add(datos);

        JLabel precio = Estilo.texto(viaje.getPrecioTexto(), 12, 112, 120,
                Estilo.TITULO, Estilo.NAVY);
        add(precio);

        JButton ver = Estilo.botonPequeno("View Trip", ancho - 110, 108, 95);
        ver.addActionListener(e -> {
            BaseDatos.viajeSeleccionado = viaje;
            ventana.irA(new VentanaDetalleViaje());
        });
        add(ver);
    }
}
