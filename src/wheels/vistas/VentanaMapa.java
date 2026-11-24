package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JButton;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA - Mapa de la ruta del viaje con sus paradas. */
public class VentanaMapa extends VentanaBase {

    public VentanaMapa() {
        super("Mapa de la ruta");
        final Viaje viaje = BaseDatos.viajeSeleccionado;
        final PanelMapa mapa = new PanelMapa(viaje);

        JButton gmaps = Estilo.boton("Abrir en Google Maps", Estilo.TipoBoton.ACENTO, Estilo.Tamano.NORMAL, Iconos.Tipo.NAVEGACION);
        gmaps.addActionListener(e -> Utilidades.abrirNavegador(this, mapa.enlaceGoogleMaps()));
        encabezado("Ruta del viaje", viaje.getRuta(), true, gmaps);
        Diseno.Columna c = crearCuerpo();

        Diseno.Rejilla dos = new Diseno.Rejilla(320, 2, 20, 20).igualarAlto(false).conCorte(820);
        // en ventana ancha el mapa ocupa mas espacio que la lista
        Estilo.Tarjeta tm = Estilo.Tarjeta.conColumna(0);
        tm.conRelleno(10, 10);
        mapa.setPreferredSize(new Dimension(300, 380));
        tm.columna().agregar(mapa);
        dos.add(tm);

        Estilo.Tarjeta lista = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna l = lista.columna();
        l.agregar(Estilo.texto("Paradas", Estilo.h3(), Estilo.TEXTO));
        l.agregar(parada("A", Estilo.TEAL_OSCURO, viaje.getOrigen(), "Salida · " + viaje.getHora()));
        ArrayList<String[]> puntos = BaseDatos.puntosRecogida(viaje.getId());
        int n = 1;
        for (String[] p : puntos) {
            String hora = p[2] != null && p[2].length() >= 5 ? p[2].substring(0, 5) : "";
            l.agregar(parada(String.valueOf(n++), Estilo.AMBAR, p[0], p[1] + (hora.isEmpty() ? "" : " · " + hora)));
        }
        l.agregar(parada("B", Estilo.NAVY, viaje.getDestino(), "Llegada"));
        if (puntos.isEmpty()) {
            l.agregar(new Estilo.Parrafo("Este viaje no tiene puntos de recogida intermedios: el conductor sale directo al destino.",
                    Estilo.pequena(), Estilo.GRIS));
        }
        if (!mapa.getDesconocidos().isEmpty()) {
            Estilo.Aviso a = new Estilo.Aviso();
            a.mostrar("No reconocemos " + String.join(", ", mapa.getDesconocidos())
                    + " en el mapa sin conexión. Usa Google Maps para ver la ubicación exacta.", "alerta");
            l.agregar(a);
        }
        dos.add(lista);
        c.agregar(dos);
    }

    @Override public String seccion() { return INICIO; }

    private PanelAcceso.FilaIcono parada(String letra, java.awt.Color color, String titulo, String detalle) {
        Estilo.Pastilla p = new Estilo.Pastilla(letra, color, Estilo.BLANCO, null);
        p.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        p.setPreferredSize(new Dimension(28, 28));
        p.setFont(Estilo.fuente(12, Estilo.NEGRITA));
        Diseno.Columna t = new Diseno.Columna(100000, 0, 0).conSeparacion(2);
        t.agregar(Estilo.texto(titulo, Estilo.cuerpoSemi(), Estilo.TEXTO));
        t.agregar(new Estilo.Parrafo(detalle, Estilo.pequena(), Estilo.GRIS));
        return new PanelAcceso.FilaIcono(p, t);
    }
}
