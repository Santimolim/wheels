package wheels.vistas;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA 5 - Viajes disponibles: resultados de la busqueda, con orden. */
public class VentanaViajesDisponibles extends VentanaBase {

    private final Diseno.Rejilla rejilla = new Diseno.Rejilla(300, 3, 16, 16);
    private final ArrayList<Viaje> viajes;

    public VentanaViajesDisponibles() {
        super("Viajes disponibles");
        viajes = new ArrayList<>(BaseDatos.resultadosBusqueda);
        int n = viajes.size();

        encabezado("Viajes disponibles", BaseDatos.descripcionBusqueda, true);
        Diseno.Columna c = crearCuerpo();

        if (n == 0) {
            JButton modificar = Estilo.boton("Modificar búsqueda", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.BUSCAR);
            modificar.addActionListener(e -> volver());
            Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
            t.columna().agregar(Estilo.vacio(Iconos.Tipo.BUSCAR, "No encontramos viajes",
                    "No hay viajes disponibles con esos datos. Prueba con otra fecha o escribe solo una parte "
                    + "del lugar (por ejemplo, \"Chía\" en vez de la dirección completa).", modificar));
            c.agregar(t);
            return;
        }

        JPanel barra = Estilo.panel(new BorderLayout());
        barra.add(Estilo.texto(n + (n == 1 ? " viaje encontrado" : " viajes encontrados"), Estilo.h3(), Estilo.TEXTO),
                BorderLayout.WEST);
        JComboBox<String> orden = Estilo.combo(new String[]{"Salida más pronto", "Menor precio", "Más puestos libres"});
        orden.setPreferredSize(new java.awt.Dimension(210, 38));
        orden.getAccessibleContext().setAccessibleName("Ordenar resultados");
        orden.addActionListener(e -> ordenar(orden.getSelectedIndex()));
        JPanel der = Estilo.panel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        javax.swing.JLabel lo = Estilo.texto("Ordenar por", Estilo.pequena(), Estilo.GRIS);
        lo.setIcon(Iconos.de(Iconos.Tipo.ORDENAR, 15, Estilo.GRIS));
        der.add(lo);
        der.add(orden);
        barra.add(der, BorderLayout.EAST);
        c.agregar(barra);
        c.espacio(16);
        c.agregar(rejilla);
        ordenar(0);
    }

    @Override public String seccion() { return INICIO; }

    private void ordenar(int criterio) {
        Comparator<Viaje> cmp;
        switch (criterio) {
            case 1:  cmp = Comparator.comparingInt(Viaje::getPrecio); break;
            case 2:  cmp = Comparator.comparingInt(Viaje::getPuestosDisponibles).reversed(); break;
            default: cmp = Comparator.comparing((Viaje v) -> v.getFecha() + v.getHora()); break;
        }
        viajes.sort(cmp);
        rejilla.removeAll();
        for (Viaje v : viajes) rejilla.add(new TarjetaViaje(v));
        rejilla.revalidate();
        cuerpo.revalidate();
        repaint();
    }
}
