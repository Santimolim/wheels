package wheels.vistas;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA 4 - Home. Buscador de viajes. */
public class VentanaHome extends VentanaBase {

    private JTextField txtOrigen;
    private JTextField txtDestino;
    private JTextField txtFecha;

    public VentanaHome() {
        super("Inicio");

        JPanel cab = Estilo.encabezado("Hola, " + primerNombre(),
                "A donde vas hoy?  " + Estilo.estrellas(BaseDatos.usuarioActual.getCalificacion()));
        lienzo.add(cab);

        // Campana de notificaciones con contador
        int sinLeer = BaseDatos.notificacionesSinLeer();
        JButton campana = Estilo.botonPequeno(sinLeer > 0 ? "Avisos (" + sinLeer + ")" : "Avisos", 262, 14, 98);
        campana.setBackground(sinLeer > 0 ? Estilo.AMBAR : Estilo.NAVY2);
        campana.addActionListener(e -> irA(new VentanaNotificaciones()));
        cab.add(campana);

        // ---------- Tarjeta del buscador ----------
        JPanel buscador = Estilo.tarjeta(20, 62, 340, 250);

        buscador.add(Estilo.etiqueta("Origen", 16, 12));
        txtOrigen = Estilo.campo(16, 32, 308);
        txtOrigen.setToolTipText("Ej: Bogota, Chia, Cajica");
        buscador.add(txtOrigen);

        buscador.add(Estilo.etiqueta("Destino", 16, 76));
        txtDestino = Estilo.campo(16, 96, 308);
        txtDestino.setToolTipText("Ej: Universidad de La Sabana");
        buscador.add(txtDestino);

        buscador.add(Estilo.etiqueta("Fecha (aaaa-mm-dd, opcional)", 16, 140));
        txtFecha = Estilo.campo(16, 160, 308);
        buscador.add(txtFecha);

        JButton buscar = Estilo.botonPrimario("Search Trips", 16, 200, 308);
        buscar.addActionListener(e -> buscar());
        buscador.add(buscar);

        lienzo.add(buscador);

        // ---------- Viajes publicados ----------
        lienzo.add(Estilo.texto("Viajes publicados", 20, 320, 220, Estilo.SUBTITULO, Estilo.TEXTO));

        JButton verTodos = Estilo.enlace("Ver todos", 262, 320, 98);
        verTodos.addActionListener(e -> {
            BaseDatos.resultadosBusqueda = BaseDatos.buscarViajes("", "", "");
            irA(new VentanaViajesDisponibles());
        });
        lienzo.add(verTodos);

        ArrayList<Viaje> todos = BaseDatos.buscarViajes("", "", "");
        int y = 346;
        int mostrados = 0;
        for (Viaje v : todos) {
            if (mostrados >= 2) {
                break;
            }
            TarjetaViaje t = new TarjetaViaje(this, v, 340);
            t.setBounds(20, y, 340, TarjetaViaje.ALTO);
            lienzo.add(t);
            y += TarjetaViaje.ALTO + 10;
            mostrados++;
        }

        if (mostrados == 0) {
            JLabel vacio = Estilo.texto("Todavia no hay viajes publicados.", 20, 360, 320,
                    Estilo.NORMAL, Estilo.GRIS);
            lienzo.add(vacio);
        }

        agregarBarraInferior("Home");
    }

    private String primerNombre() {
        return BaseDatos.usuarioActual.getPrimerNombre();
    }

    private void buscar() {
        String origen  = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();
        String fecha   = txtFecha.getText().trim();

        BaseDatos.resultadosBusqueda = BaseDatos.buscarViajes(origen, destino, fecha);

        if (BaseDatos.resultadosBusqueda.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se encontraron viajes con esos datos.",
                    "Wheels", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        irA(new VentanaViajesDisponibles());
    }
}
