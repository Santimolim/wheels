package wheels.vistas;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA - Mapa de la ruta del viaje con sus puntos de recogida. */
public class VentanaMapa extends VentanaBase {

    public VentanaMapa() {
        super("Mapa de la ruta");
        final Viaje viaje = BaseDatos.viajeSeleccionado;

        lienzo.add(Estilo.encabezado("Ruta del viaje", viaje.getRuta()));

        agregarVolverEncabezado();

        final PanelMapa mapa = new PanelMapa(viaje);
        mapa.setBounds(12, 92, 356, 330);
        lienzo.add(mapa);

        // Lista de paradas
        ArrayList<String[]> puntos = BaseDatos.puntosRecogida(viaje.getId());
        JPanel paradas = Estilo.tarjeta(12, 432, 356, 160);
        int y = 10;
        paradas.add(Estilo.pastilla("A", 12, y, 22, Estilo.VERDE, Estilo.BLANCO));
        paradas.add(Estilo.texto("Salida: " + viaje.getOrigen() + "  -  " + viaje.getHora(),
                42, y, 300, Estilo.NORMAL, Estilo.TEXTO));
        y += 26;
        int n = 1;
        for (String[] p : puntos) {
            if (y > 100) break;
            paradas.add(Estilo.pastilla(String.valueOf(n++), 12, y, 22, Estilo.AMBAR, Estilo.BLANCO));
            paradas.add(Estilo.texto(p[0] + "  -  " + p[2].substring(0, 5), 42, y - 2, 300,
                    Estilo.NORMAL, Estilo.TEXTO));
            paradas.add(Estilo.texto(p[1], 42, y + 14, 300, Estilo.PEQUENA, Estilo.GRIS));
            y += 34;
        }
        paradas.add(Estilo.pastilla("B", 12, y, 22, Estilo.ROJO, Estilo.BLANCO));
        paradas.add(Estilo.texto("Llegada: " + viaje.getDestino(), 42, y, 300, Estilo.NORMAL, Estilo.TEXTO));
        if (puntos.isEmpty()) {
            paradas.add(Estilo.texto("Este viaje no tiene puntos de recogida registrados.",
                    12, y + 30, 330, Estilo.PEQUENA, Estilo.GRIS));
        }
        lienzo.add(paradas);

        JButton maps = Estilo.botonPrimario("Abrir en Google Maps", 12, 604, 356);
        maps.addActionListener(e -> VentanaViajeEnCurso.abrirNavegador(mapa.enlaceGoogleMaps()));
        lienzo.add(maps);
    }
}
