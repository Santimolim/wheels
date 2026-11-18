package wheels.vistas;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA 5 - Available Trips. Lista de resultados de la busqueda. */
public class VentanaViajesDisponibles extends VentanaBase {

    public VentanaViajesDisponibles() {
        super("Viajes disponibles");

        int cantidad = BaseDatos.resultadosBusqueda.size();

        lienzo.add(Estilo.encabezado("Viajes disponibles",
                cantidad + (cantidad == 1 ? " resultado" : " resultados")));

        agregarVolverEncabezado();

        // Panel que contiene todas las tarjetas, una debajo de otra.
        // Usamos layout null y vamos bajando la posicion Y en cada vuelta del for.
        JPanel lista = new JPanel(null);
        lista.setBackground(Estilo.FONDO);

        int y = 0;
        for (Viaje v : BaseDatos.resultadosBusqueda) {
            TarjetaViaje t = new TarjetaViaje(this, v, 300);
            t.setBounds(0, y, 300, TarjetaViaje.ALTO);
            lista.add(t);
            y += TarjetaViaje.ALTO + 10;
        }

        if (cantidad == 0) {
            JLabel vacio = Estilo.texto("No hay viajes que coincidan con tu busqueda.",
                    0, 10, 300, Estilo.NORMAL, Estilo.GRIS);
            lista.add(vacio);
            y = 60;
        }

        // La altura total del panel depende de cuantas tarjetas haya
        lista.setPreferredSize(new Dimension(300, y));

        // El JScrollPane permite desplazar la lista si hay muchos viajes
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBounds(20, 95, 340, 530);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Estilo.FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        lienzo.add(scroll);

        agregarBarraInferior("Trips");
    }
}
