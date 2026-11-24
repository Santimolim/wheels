package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/** PANTALLA 4 - Inicio: buscador de viajes, proximo viaje, destinos populares y proximas salidas. */
public class VentanaHome extends VentanaBase {

    private final JTextField txtOrigen;
    private final JTextField txtDestino;
    private final JComboBox<String> cmbFecha;
    private final ArrayList<String> fechas = new ArrayList<>();

    public VentanaHome() {
        super("Inicio");
        Usuario u = BaseDatos.usuarioActual;
        Diseno.Columna c = crearCuerpo();
        c.agregar(encabezadoInicio());
        c.espacio(14);

        // ---------- Saludo y buscador ----------
        Hero hero = new Hero();
        Diseno.Columna h = hero.columna;
        String hoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.forLanguageTag("es-CO")));
        h.agregar(Estilo.texto(Character.toUpperCase(hoy.charAt(0)) + hoy.substring(1), Estilo.fuente(12.5f, Estilo.REGULAR),
                new Color(0xA5B4CF)));
        h.espacio(2);
        h.agregar(Estilo.texto(saludo() + ", " + u.getPrimerNombre(), Estilo.fuente(14, Estilo.SEMI), new Color(0x99F6E4)));
        h.espacio(4);
        h.agregar(Estilo.texto("¿A dónde vas hoy?", Estilo.fuente(25, Estilo.NEGRITA), Estilo.BLANCO));
        h.espacio(20);

        Diseno.Columna campos = new Diseno.Columna(100000, 0, 0).conSeparacion(12);
        txtOrigen = Estilo.campo("Ej: Chía, Calle 170", Iconos.Tipo.ORIGEN);
        txtDestino = Estilo.campo("Ej: Universidad de La Sabana", Iconos.Tipo.UBICACION);
        cmbFecha = Estilo.combo(opcionesFecha());
        campos.agregar(new Estilo.Campo("Desde", txtOrigen, null).sobreOscuro().compacto());
        campos.agregar(new Estilo.Campo("Hacia", txtDestino, null).sobreOscuro().compacto());
        campos.agregar(new Estilo.Campo("Cuándo", cmbFecha, null).sobreOscuro().compacto());
        campos.espacio(4);
        JButton buscar = Estilo.boton("Buscar viajes", Estilo.TipoBoton.ACENTO, Estilo.Tamano.GRANDE, Iconos.Tipo.BUSCAR);
        buscar.addActionListener(e -> buscar());
        campos.agregar(buscar);
        h.agregar(campos);
        txtOrigen.addActionListener(e -> buscar());
        txtDestino.addActionListener(e -> buscar());
        c.agregar(hero);

        // ---------- Proximo viaje (si tiene uno aceptado o en curso) ----------
        Solicitud proxima = null;
        for (Solicitud s : BaseDatos.solicitudesPorEstado(Solicitud.EN_CURSO, Solicitud.ACEPTADA)) {
            if (s.getEstado().equals(Solicitud.EN_CURSO) || !s.getViaje().yaPaso()) { proxima = s; break; }
        }
        if (proxima != null) {
            c.espacio(24);
            c.agregar(tarjetaProximo(proxima));
        }

        // ---------- Destinos populares ----------
        ArrayList<String> populares = BaseDatos.destinosPopulares(6);
        if (!populares.isEmpty()) {
            c.espacio(28);
            c.agregar(Estilo.tituloSeccion("Destinos populares", null));
            c.espacio(12);
            Diseno.FilaFlexible chips = new Diseno.FilaFlexible(8, 8);
            for (String d : populares) {
                JButton chip = Estilo.boton(d, Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.UBICACION);
                chip.setToolTipText("Buscar viajes hacia " + d);
                chip.addActionListener(e -> {
                    txtDestino.setText(d);
                    txtOrigen.setText("");
                    cmbFecha.setSelectedIndex(0);
                    buscar();
                });
                chips.add(chip);
            }
            c.agregar(chips);
        }

        // ---------- Proximas salidas ----------
        c.espacio(28);
        ArrayList<Viaje> proximos = BaseDatos.buscarViajes("", "", "");
        JButton verTodos = Estilo.enlace("Ver todos (" + proximos.size() + ")");
        verTodos.setIcon(Iconos.de(Iconos.Tipo.CHEVRON_DER, 14, Estilo.TEAL_OSCURO));
        verTodos.setHorizontalTextPosition(JButton.LEFT);
        verTodos.addActionListener(e -> {
            BaseDatos.resultadosBusqueda = proximos;
            BaseDatos.descripcionBusqueda = "Todos los viajes disponibles";
            irA(VentanaViajesDisponibles::new);
        });
        verTodos.setVisible(proximos.size() > 6);
        c.agregar(Estilo.tituloSeccion("Próximas salidas", verTodos));
        c.espacio(12);

        if (proximos.isEmpty()) {
            JButton publicar = Estilo.boton("Publicar un viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.PUBLICAR);
            publicar.addActionListener(e -> Aplicacion.navegar(PUBLICAR));
            Estilo.Tarjeta vacio = Estilo.Tarjeta.conColumna(0);
            vacio.columna().agregar(Estilo.vacio(Iconos.Tipo.CARRO, "Aún no hay viajes programados",
                    "Cuando otros estudiantes publiquen viajes desde hoy en adelante, aparecerán aquí. "
                    + "¿Tienes carro? Publica el tuyo.", publicar));
            c.agregar(vacio);
        } else {
            Diseno.Rejilla rejilla = new Diseno.Rejilla(300, 3, 16, 16);
            for (int i = 0; i < Math.min(6, proximos.size()); i++) rejilla.add(new TarjetaViaje(proximos.get(i)));
            c.agregar(rejilla);
        }
    }

    @Override public String seccion() { return INICIO; }
    @Override public boolean esRaiz() { return true; }

    /** Encabezado: logo y acceso a notificaciones (con la cantidad de nuevas). */
    private JPanel encabezadoInicio() {
        JPanel fila = Estilo.panel(new BorderLayout());
        fila.add(new Logo.Marca(30, Estilo.NAVY, true), BorderLayout.WEST);
        int sinLeer = BaseDatos.notificacionesSinLeer();
        String ayuda = sinLeer > 0 ? sinLeer + (sinLeer == 1 ? " notificación nueva" : " notificaciones nuevas") : "Notificaciones";
        JButton avisos = sinLeer > 0
                ? Estilo.boton(String.valueOf(sinLeer), Estilo.TipoBoton.PELIGRO_SUAVE, Estilo.Tamano.PEQUENO, Iconos.Tipo.CAMPANA)
                : Estilo.botonIcono(Iconos.Tipo.CAMPANA, ayuda, Estilo.TEXTO, 38);
        avisos.setToolTipText(ayuda);
        avisos.getAccessibleContext().setAccessibleName(ayuda);
        avisos.addActionListener(e -> Aplicacion.navegar(AVISOS));
        fila.add(avisos, BorderLayout.EAST);
        return fila;
    }

    private Estilo.Tarjeta tarjetaProximo(Solicitud s) {
        Viaje v = s.getViaje();
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(14);
        t.conFondo(new Color(0xF0FDFA)).conBorde(new Color(0x99F6E4));
        Diseno.Columna txt = new Diseno.Columna(10000, 0, 0).conSeparacion(3);
        txt.agregar(Estilo.texto(s.getEstado().equals(Solicitud.EN_CURSO) ? "Tienes un viaje en curso" : "Tu próximo viaje",
                Estilo.pequenaSemi(), Estilo.TEAL_OSCURO));
        txt.agregar(new Estilo.Parrafo(v.getRuta(), Estilo.h3(), Estilo.TEXTO));
        txt.agregar(Estilo.texto(v.getFechaAmigable() + " · " + v.getHora() + " · con " + v.getConductor().getPrimerNombre(),
                Estilo.pequena(), Estilo.GRIS));
        t.columna().agregar(new PanelAcceso.FilaIcono(
                new Estilo.IconoCirculo(Iconos.Tipo.CARRO, Estilo.BLANCO, Estilo.TEAL_OSCURO, 46), txt));
        JButton ver = Estilo.boton(s.getEstado().equals(Solicitud.EN_CURSO) ? "Continuar viaje" : "Ver en Mis viajes",
                Estilo.TipoBoton.ACENTO, Estilo.Tamano.NORMAL, null);
        ver.addActionListener(e -> {
            if (s.getEstado().equals(Solicitud.EN_CURSO)) {
                BaseDatos.solicitudEnCurso = s;
                irA(VentanaViajeEnCurso::new);
            } else {
                irRaiz(VentanaMisViajes::new);
            }
        });
        t.columna().agregar(ver);
        return t;
    }

    private String[] opcionesFecha() {
        Locale es = Locale.forLanguageTag("es-CO");
        ArrayList<String> textos = new ArrayList<>();
        textos.add("Cualquier fecha");
        fechas.add("");
        LocalDate d = LocalDate.now();
        for (int i = 0; i < 14; i++) {
            LocalDate x = d.plusDays(i);
            String t = i == 0 ? "Hoy" : i == 1 ? "Mañana"
                    : x.format(DateTimeFormatter.ofPattern("EEE d MMM", es)).replace(".", "");
            textos.add(Character.toUpperCase(t.charAt(0)) + t.substring(1));
            fechas.add(x.toString());
        }
        return textos.toArray(new String[0]);
    }

    private static String saludo() {
        int h = LocalTime.now().getHour();
        return h < 12 ? "Buenos días" : h < 19 ? "Buenas tardes" : "Buenas noches";
    }

    private void buscar() {
        String origen  = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();
        String fecha   = fechas.get(Math.max(0, cmbFecha.getSelectedIndex()));

        BaseDatos.resultadosBusqueda = BaseDatos.buscarViajes(origen, destino, fecha);
        StringBuilder d = new StringBuilder();
        if (!origen.isEmpty()) d.append("Desde ").append(origen);
        if (!destino.isEmpty()) d.append(d.length() > 0 ? " hacia " : "Hacia ").append(destino);
        if (!fecha.isEmpty()) d.append(d.length() > 0 ? " · " : "").append(cmbFecha.getSelectedItem());
        BaseDatos.descripcionBusqueda = d.length() == 0 ? "Todos los viajes disponibles" : d.toString();
        irA(VentanaViajesDisponibles::new);
    }

    /** Tarjeta azul del buscador; su alto se ajusta al ancho (los campos se reacomodan). */
    private static final class Hero extends Estilo.PanelMarca implements Diseno.AltoSegunAncho {
        final Diseno.Columna columna = new Diseno.Columna(100000, 0, 0);
        Hero() {
            super(new BorderLayout(), 24);
            setBorder(BorderFactory.createEmptyBorder(24, 22, 22, 22));
            add(columna, BorderLayout.CENTER);
        }
        @Override public int altoPara(int ancho) {
            Insets in = getInsets();
            return in.top + in.bottom + columna.altoPara(ancho - in.left - in.right);
        }
    }
}
