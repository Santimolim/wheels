package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/**
 * PANTALLA 9 - Mis viajes.
 * Como pasajero: proximos (pendientes, aceptados, en curso) e historial.
 * Como conductor: solicitudes recibidas (aceptar/rechazar) y viajes publicados (cancelar).
 */
public class VentanaMisViajes extends VentanaBase {

    public static final String PROXIMOS = "proximos";
    public static final String HISTORIAL = "historial";
    public static final String SOLICITUDES = "solicitudes";
    public static final String PUBLICADOS = "publicados";

    private final Diseno.Columna contenido = new Diseno.Columna(100000, 0, 0);
    private final Diseno.FilaFlexible pestanas = new Diseno.FilaFlexible(8, 8);
    private String actual;

    public VentanaMisViajes() {
        this(PROXIMOS);
    }

    public VentanaMisViajes(String pestanaInicial) {
        super("Mis viajes");
        encabezado("Mis viajes", "Como pasajero y como conductor", false);
        Diseno.Columna c = crearCuerpo();
        c.agregar(pestanas);
        c.espacio(20);
        c.agregar(contenido);
        mostrar(pestanaInicial);
    }

    @Override public String seccion() { return VIAJES; }
    @Override public boolean esRaiz() { return true; }

    private void pintarPestanas() {
        pestanas.removeAll();
        int pendientes = BaseDatos.solicitudesPendientesRecibidas();
        pestanas.add(pestana(PROXIMOS, "Próximos", Iconos.Tipo.RELOJ, 0));
        pestanas.add(pestana(HISTORIAL, "Historial", Iconos.Tipo.CHECK, 0));
        pestanas.add(pestana(SOLICITUDES, "Solicitudes", Iconos.Tipo.PERSONAS, pendientes));
        pestanas.add(pestana(PUBLICADOS, "Publicados", Iconos.Tipo.VOLANTE, 0));
    }

    private JButton pestana(String id, String texto, Iconos.Tipo icono, int contador) {
        boolean activa = id.equals(actual);
        JButton b = Estilo.boton(texto + (contador > 0 ? "  " + contador : ""),
                activa ? Estilo.TipoBoton.PRIMARIO : Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, icono);
        b.getAccessibleContext().setAccessibleName(texto + (activa ? ", seleccionada" : ""));
        b.addActionListener(e -> mostrar(id));
        return b;
    }

    /** Llena la lista segun la pestana elegida. */
    private void mostrar(String pestana) {
        actual = pestana;
        pintarPestanas();
        contenido.removeAll();

        Diseno.Rejilla rejilla = new Diseno.Rejilla(340, 2, 16, 16).igualarAlto(false);
        switch (pestana) {
            case HISTORIAL: {
                encabezar(nota("Viajes finalizados, solicitudes rechazadas y canceladas."));
                ArrayList<Solicitud> l = BaseDatos.solicitudesPorEstado(Solicitud.COMPLETADA, Solicitud.RECHAZADA, Solicitud.CANCELADA);
                Set<Integer> calificados = BaseDatos.viajesCalificados();
                for (Solicitud s : l) rejilla.add(tarjetaPasajero(s, calificados));
                if (l.isEmpty()) contenido.agregar(vacio(Iconos.Tipo.RUTA, "Tu historial está vacío",
                        "Cuando finalices un viaje aparecerá aquí para que puedas calificarlo.", false));
                break;
            }
            case SOLICITUDES: {
                encabezar(nota("Pasajeros que pidieron un puesto en tus viajes."));
                ArrayList<Solicitud> l = BaseDatos.solicitudesRecibidas();
                for (Solicitud s : l) rejilla.add(tarjetaConductor(s));
                if (l.isEmpty()) contenido.agregar(vacio(Iconos.Tipo.PERSONAS, "Sin solicitudes por ahora",
                        "Cuando alguien solicite un puesto en uno de tus viajes, podrás aceptarlo o rechazarlo aquí.", true));
                break;
            }
            case PUBLICADOS: {
                encabezar(nota("Viajes que ofreciste como conductor."));
                ArrayList<Viaje> l = BaseDatos.viajesDelUsuario();
                for (Viaje v : l) rejilla.add(tarjetaPublicado(v));
                if (l.isEmpty()) contenido.agregar(vacio(Iconos.Tipo.VOLANTE, "No has publicado viajes",
                        "¿Vas a la universidad en carro? Publica tus puestos libres y comparte los gastos.", true));
                break;
            }
            default: {
                encabezar(nota("Solicitudes pendientes, viajes aceptados y en curso."));
                ArrayList<Solicitud> l = BaseDatos.solicitudesPorEstado(Solicitud.EN_CURSO, Solicitud.ACEPTADA, Solicitud.PENDIENTE);
                for (Solicitud s : l) rejilla.add(tarjetaPasajero(s, null));
                if (l.isEmpty()) {
                    JButton buscar = Estilo.boton("Buscar un viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.BUSCAR);
                    buscar.addActionListener(e -> irRaiz(VentanaHome::new));
                    Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
                    t.columna().agregar(Estilo.vacio(Iconos.Tipo.CARRO, "No tienes viajes próximos",
                            "Busca un viaje hacia tu universidad y solicita un puesto.", buscar));
                    contenido.agregar(t);
                }
            }
        }
        if (rejilla.getComponentCount() > 0) contenido.agregar(rejilla);
        contenido.revalidate();
        if (cuerpo != null) cuerpo.revalidate();
        repaint();
        subir();
    }

    private void encabezar(JLabel nota) {
        contenido.agregar(nota);
        contenido.espacio(14);
    }

    private JLabel nota(String txt) {
        return Estilo.texto(txt, Estilo.pequena(), Estilo.GRIS);
    }

    private Estilo.Tarjeta vacio(Iconos.Tipo icono, String titulo, String desc, boolean conPublicar) {
        JButton b = null;
        if (conPublicar) {
            b = Estilo.boton("Publicar un viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.PUBLICAR);
            b.addActionListener(e -> Aplicacion.navegar(PUBLICAR));
        }
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        t.columna().agregar(Estilo.vacio(icono, titulo, desc, b));
        return t;
    }

    // ------------------------------------------------------------------
    //  Tarjetas
    // ------------------------------------------------------------------

    /** Cabecera de tarjeta: persona (avatar, nombre, detalle) y estado a la derecha. */
    private JPanel persona(Usuario u, String detalle, String estado) {
        JPanel p = Estilo.panel(new BorderLayout(12, 0));
        p.add(new Estilo.Avatar(u.getIniciales(), u.getEmail(), 40), BorderLayout.WEST);
        JPanel t = Estilo.panel(new GridLayout(2, 1));
        t.add(Estilo.texto(u.getNombre(), Estilo.cuerpoSemi(), Estilo.TEXTO));
        JLabel d = Estilo.texto(detalle, Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS);
        t.add(d);
        p.add(t, BorderLayout.CENTER);
        if (estado != null) {
            JPanel e = Estilo.panel(new java.awt.GridBagLayout());
            e.add(Estilo.estado(estado));
            p.add(e, BorderLayout.EAST);
        }
        return p;
    }

    private JPanel resumenViaje(Viaje v) {
        JPanel p = Estilo.panel(new BorderLayout(10, 6));
        p.add(new TarjetaViaje.RutaMini(v.getOrigen(), v.getDestino()), BorderLayout.CENTER);
        JPanel pie = Estilo.panel(new BorderLayout());
        JLabel cuando = Estilo.texto(v.getFechaAmigable() + " · " + v.getHora(), Estilo.pequenaSemi(), Estilo.TEXTO2);
        cuando.setIcon(Iconos.de(Iconos.Tipo.CALENDARIO, 15, Estilo.GRIS));
        cuando.setIconTextGap(6);
        pie.add(cuando, BorderLayout.WEST);
        pie.add(Estilo.texto(v.getPrecioTexto(), Estilo.fuente(16, Estilo.NEGRITA), Estilo.NAVY), BorderLayout.EAST);
        p.add(pie, BorderLayout.SOUTH);
        return p;
    }

    private Estilo.Tarjeta tarjetaPasajero(final Solicitud s, Set<Integer> calificados) {
        final Viaje v = s.getViaje();
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna c = t.columna();
        c.agregar(persona(v.getConductor(), "Conductor · ★ " + v.getConductor().getCalificacion()
                + " · " + v.getVehiculo().getMarca() + " " + v.getVehiculo().getModelo(), s.getEstado()));
        c.agregar(Estilo.separador());
        c.agregar(resumenViaje(v));

        Diseno.FilaFlexible acciones = new Diseno.FilaFlexible(8, 8);
        switch (s.getEstado()) {
            case Solicitud.EN_CURSO: {
                JButton b = Estilo.boton("Continuar viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.NAVEGACION);
                b.addActionListener(e -> { BaseDatos.solicitudEnCurso = s; irA(VentanaViajeEnCurso::new); });
                acciones.add(b);
                acciones.add(botonChat(v.getConductor(), v));
                break;
            }
            case Solicitud.ACEPTADA: {
                JButton b = Estilo.boton("Iniciar viaje", Estilo.TipoBoton.EXITO, Estilo.Tamano.PEQUENO, Iconos.Tipo.NAVEGACION);
                b.addActionListener(e -> iniciar(s));
                acciones.add(b);
                acciones.add(botonChat(v.getConductor(), v));
                acciones.add(botonCancelar(s));
                break;
            }
            case Solicitud.PENDIENTE:
                acciones.add(botonVer(v));
                acciones.add(botonChat(v.getConductor(), v));
                acciones.add(botonCancelar(s));
                break;
            case Solicitud.COMPLETADA:
                if (calificados != null && !calificados.contains(v.getId())) {
                    JButton b = Estilo.boton("Calificar", Estilo.TipoBoton.ACENTO, Estilo.Tamano.PEQUENO, Iconos.Tipo.ESTRELLA);
                    b.addActionListener(e -> {
                        if (DialogoCalificar.mostrar(this, v)) {
                            toast("¡Gracias por calificar!", Dialogos.Tono.EXITO);
                            mostrar(HISTORIAL);
                        }
                    });
                    acciones.add(b);
                } else {
                    acciones.add(new Estilo.Pastilla("Calificado", Estilo.AMBAR_SUAVE, Estilo.AMBAR_TEXTO, Iconos.Tipo.ESTRELLA_LLENA));
                }
                break;
            default:
                if (!v.yaPaso() && !v.estaCancelado()) acciones.add(botonVer(v));
                break;
        }
        if (acciones.getComponentCount() > 0) c.agregar(acciones);
        return t;
    }

    private Estilo.Tarjeta tarjetaConductor(final Solicitud s) {
        final Viaje v = s.getViaje();
        final Usuario p = s.getPasajero();
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna c = t.columna();
        c.agregar(persona(p, "Pasajero · ★ " + p.getCalificacion() + " · " + p.getCarrera(), s.getEstado()));
        c.agregar(Estilo.separador());
        c.agregar(resumenViaje(v));
        if (!s.getEstado().equals(Solicitud.PENDIENTE) && !p.getTelefono().isEmpty()) {
            c.agregar(Estilo.dato(Iconos.Tipo.TELEFONO, "Celular: " + p.getTelefono()));
        }
        Diseno.FilaFlexible acciones = new Diseno.FilaFlexible(8, 8);
        if (s.getEstado().equals(Solicitud.PENDIENTE)) {
            JButton aceptar = Estilo.boton("Aceptar", Estilo.TipoBoton.EXITO, Estilo.Tamano.PEQUENO, Iconos.Tipo.CHECK);
            aceptar.addActionListener(e -> {
                String error = BaseDatos.aceptarSolicitud(s);
                if (error != null) {
                    Dialogos.info(this, "No se pudo aceptar", error, Dialogos.Tono.ALERTA);
                } else {
                    toast("Aceptaste a " + p.getPrimerNombre() + ". Le avisamos.", Dialogos.Tono.EXITO);
                }
                mostrar(SOLICITUDES);
                Aplicacion.actualizarInsignias();
            });
            JButton rechazar = Estilo.boton("Rechazar", Estilo.TipoBoton.PELIGRO_SUAVE, Estilo.Tamano.PEQUENO, Iconos.Tipo.CERRAR);
            rechazar.addActionListener(e -> {
                if (!Dialogos.confirmar(this, "¿Rechazar a " + p.getPrimerNombre() + "?",
                        "Le avisaremos que su solicitud para " + v.getRuta() + " no fue aceptada.", "Rechazar", true)) return;
                if (BaseDatos.rechazarSolicitud(s)) toast("Solicitud rechazada", Dialogos.Tono.INFO);
                mostrar(SOLICITUDES);
                Aplicacion.actualizarInsignias();
            });
            acciones.add(aceptar);
            acciones.add(rechazar);
        }
        acciones.add(botonChat(p, v));
        c.agregar(acciones);
        return t;
    }

    private Estilo.Tarjeta tarjetaPublicado(final Viaje v) {
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna c = t.columna();
        boolean pasado = v.yaPaso();
        Estilo.Pastilla estado;
        if (v.estaCancelado()) estado = new Estilo.Pastilla("Cancelado", Estilo.HOVER, Estilo.GRIS, Iconos.Tipo.CERRAR);
        else if (pasado) estado = new Estilo.Pastilla("Realizado", Estilo.TEAL_SUAVE, Estilo.TEAL_OSCURO, Iconos.Tipo.CHECK);
        else if (Viaje.LLENO.equals(v.getEstado()) || v.getPuestosDisponibles() == 0)
            estado = new Estilo.Pastilla("Lleno", Estilo.AMBAR_SUAVE, Estilo.AMBAR_TEXTO, Iconos.Tipo.PERSONAS);
        else estado = new Estilo.Pastilla("Publicado", Estilo.VERDE_SUAVE, Estilo.VERDE_TEXTO, Iconos.Tipo.CHECK);

        JPanel arriba = Estilo.panel(new BorderLayout());
        int confirmados = BaseDatos.pasajerosConfirmados(v.getId());
        JLabel ocup = Estilo.texto(confirmados + (confirmados == 1 ? " pasajero confirmado" : " pasajeros confirmados")
                + " · " + v.getPuestosDisponibles() + " libres", Estilo.pequenaSemi(), Estilo.TEXTO2);
        ocup.setIcon(Iconos.de(Iconos.Tipo.PERSONAS, 16, Estilo.GRIS));
        ocup.setIconTextGap(6);
        arriba.add(ocup, BorderLayout.WEST);
        arriba.add(estado, BorderLayout.EAST);
        c.agregar(arriba);
        c.agregar(Estilo.separador());
        c.agregar(resumenViaje(v));

        Diseno.FilaFlexible acciones = new Diseno.FilaFlexible(8, 8);
        acciones.add(botonVer(v));
        if (!v.estaCancelado() && !pasado) {
            JButton cancelar = Estilo.boton("Cancelar viaje", Estilo.TipoBoton.PELIGRO_SUAVE, Estilo.Tamano.PEQUENO, Iconos.Tipo.BASURA);
            cancelar.addActionListener(e -> {
                if (!Dialogos.confirmar(this, "¿Cancelar este viaje?",
                        "Se cancelarán las solicitudes pendientes y aceptadas, y avisaremos a cada pasajero. "
                        + "Esta acción no se puede deshacer.", "Cancelar viaje", true)) return;
                String error = BaseDatos.cancelarViaje(v);
                if (error != null) Dialogos.info(this, "No se pudo cancelar", error, Dialogos.Tono.ALERTA);
                else toast("Viaje cancelado. Avisamos a los pasajeros.", Dialogos.Tono.INFO);
                mostrar(PUBLICADOS);
            });
            acciones.add(cancelar);
        }
        c.agregar(acciones);
        return t;
    }

    // ------------------------------------------------------------------
    //  Botones comunes y acciones
    // ------------------------------------------------------------------

    private JButton botonVer(Viaje v) {
        JButton b = Estilo.boton("Ver viaje", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.CHEVRON_DER);
        b.addActionListener(e -> { BaseDatos.viajeSeleccionado = v; irA(VentanaDetalleViaje::new); });
        return b;
    }

    private JButton botonChat(Usuario otro, Viaje v) {
        JButton b = Estilo.boton("Chat", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.CHAT);
        b.addActionListener(e -> DialogoChat.abrir(this, otro, v));
        return b;
    }

    private JButton botonCancelar(Solicitud s) {
        boolean aceptada = s.getEstado().equals(Solicitud.ACEPTADA);
        JButton b = Estilo.boton(aceptada ? "Cancelar puesto" : "Cancelar solicitud",
                Estilo.TipoBoton.PELIGRO_SUAVE, Estilo.Tamano.PEQUENO, null);
        b.addActionListener(e -> {
            if (!Dialogos.confirmar(this, aceptada ? "¿Cancelar tu puesto?" : "¿Cancelar la solicitud?",
                    (aceptada ? "Tu puesto quedará libre para otro estudiante" : "La solicitud se eliminará")
                    + " y avisaremos a " + s.getViaje().getConductor().getPrimerNombre() + ".",
                    aceptada ? "Cancelar puesto" : "Cancelar solicitud", true)) return;
            if (BaseDatos.cancelarSolicitud(s)) toast("Listo, cancelamos tu " + (aceptada ? "puesto" : "solicitud") + ".", Dialogos.Tono.INFO);
            mostrar(PROXIMOS);
        });
        return b;
    }

    private void iniciar(Solicitud s) {
        Viaje v = s.getViaje();
        String hoy = BaseDatos.hoy();
        if (v.getFecha().compareTo(hoy) > 0
                && !Dialogos.confirmar(this, "Este viaje es " + v.getFechaAmigable().toLowerCase(),
                        "El viaje está programado para el " + v.getFecha() + " a las " + v.getHora()
                        + ". ¿Quieres iniciarlo ahora de todas formas?", "Iniciar ahora", false)) {
            return;
        }
        if (!BaseDatos.iniciarViaje(s)) {
            Dialogos.info(this, "No se pudo iniciar", "La solicitud ya no está aceptada. Actualizamos la lista.", Dialogos.Tono.ALERTA);
            mostrar(PROXIMOS);
            return;
        }
        BaseDatos.solicitudEnCurso = s;
        irA(VentanaViajeEnCurso::new);
    }
}
