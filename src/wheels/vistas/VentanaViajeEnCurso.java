package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/**
 * PANTALLA - Viaje en curso.
 * Muestra el mapa con la posicion ESTIMADA del carro segun el tiempo transcurrido
 * desde que se inicio el viaje (Wheels no usa GPS), la hora estimada de llegada,
 * los datos del conductor, chat, compartir el viaje y el BOTON DE PANICO (SOS).
 */
public class VentanaViajeEnCurso extends VentanaBase {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("h:mm a");

    private final Solicitud solicitud;
    private final Viaje viaje;
    private final PanelMapa mapa;
    private final long inicio;
    private final double minutosTotales;
    private final boolean duracionEstimada;
    private final JLabel lblEstado, lblEta, lblRestante;
    private final JProgressBar barra;
    private final Estilo.Tarjeta alerta;
    private Timer timerViaje, timerAlerta;
    private boolean alertaActiva = false;
    private boolean llego = false;

    public VentanaViajeEnCurso() {
        super("Viaje en curso");
        solicitud = BaseDatos.solicitudEnCurso;
        viaje = solicitud.getViaje();
        BaseDatos.viajeSeleccionado = viaje;
        Usuario conductor = viaje.getConductor();
        mapa = new PanelMapa(viaje);
        inicio = BaseDatos.inicioViaje(solicitud);
        double km = mapa.distanciaKm();
        duracionEstimada = km <= 0;
        minutosTotales = km > 0 ? Math.max(8, km / 28.0 * 60) : 35;   // ~28 km/h promedio en ciudad

        encabezado("Viaje en curso", viaje.getRuta(), true);
        Diseno.Columna c = crearCuerpo().conSeparacion(16);

        // ---------- Alerta SOS (oculta hasta activarla) ----------
        alerta = crearAlerta();
        c.agregar(alerta);

        Diseno.Rejilla dos = new Diseno.Rejilla(360, 2, 20, 20).igualarAlto(false).conCorte(820);

        // ---------- Mapa ----------
        Estilo.Tarjeta tm = Estilo.Tarjeta.conColumna(0);
        tm.conRelleno(10, 10);
        mapa.setPreferredSize(new Dimension(300, 300));
        tm.columna().agregar(mapa);
        // el mapa va debajo del estado (se agrega mas abajo)

        Diseno.Columna der = new Diseno.Columna(100000, 0, 0).conSeparacion(16);

        // ---------- Estado / llegada ----------
        Estilo.Tarjeta est = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna e = est.columna();
        lblEstado = Estilo.texto("En camino", Estilo.h2(), Estilo.TEXTO);
        lblEstado.setIcon(Iconos.de(Iconos.Tipo.NAVEGACION, 20, Estilo.TEAL_OSCURO));
        lblEstado.setIconTextGap(10);
        e.agregar(lblEstado);
        e.espacio(14);
        JPanel tiempos = Estilo.panel(new GridLayout(1, 2, 12, 0));
        lblEta = Estilo.texto("--:--", Estilo.fuente(22, Estilo.NEGRITA), Estilo.NAVY);
        lblRestante = Estilo.texto("--", Estilo.fuente(22, Estilo.NEGRITA), Estilo.NAVY);
        tiempos.add(dato("Llegada estimada", lblEta));
        tiempos.add(dato("Tiempo restante", lblRestante));
        e.agregar(tiempos);
        e.espacio(16);
        barra = new JProgressBar(0, 1000);
        barra.setForeground(Estilo.TEAL);
        barra.setBackground(Estilo.HOVER);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(100, 8));
        barra.getAccessibleContext().setAccessibleName("Progreso estimado del viaje");
        e.agregar(barra);
        e.espacio(12);
        e.agregar(new Estilo.Parrafo(duracionEstimada
                ? "Estimación de 35 min basada en el tiempo transcurrido: no reconocemos todos los lugares de la ruta y Wheels no usa GPS."
                : "Progreso estimado según el tiempo transcurrido desde que iniciaste el viaje. Wheels no usa GPS.",
                Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS));
        der.agregar(est);
        der.agregar(tm);

        // ---------- Conductor ----------
        Estilo.Tarjeta pc = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna p = pc.columna();
        JPanel fila = Estilo.panel(new BorderLayout(12, 0));
        fila.add(new Estilo.Avatar(conductor.getIniciales(), conductor.getEmail(), 46), BorderLayout.WEST);
        JPanel txt = Estilo.panel(new GridLayout(2, 1));
        txt.add(Estilo.texto(conductor.getNombre(), Estilo.h3(), Estilo.TEXTO));
        txt.add(Estilo.texto(viaje.getVehiculo().getNombre() + " · " + viaje.getVehiculo().getColor().toLowerCase(),
                Estilo.pequena(), Estilo.GRIS));
        fila.add(txt, BorderLayout.CENTER);
        JPanel pl = Estilo.panel(new java.awt.GridBagLayout());
        pl.add(Utilidades.placa(viaje.getVehiculo().getPlaca()));
        fila.add(pl, BorderLayout.EAST);
        p.agregar(fila);
        if (!conductor.getTelefono().isEmpty()) {
            p.espacio(12);
            JPanel tel = Estilo.panel(new BorderLayout());
            tel.add(Estilo.dato(Iconos.Tipo.TELEFONO, conductor.getTelefono()), BorderLayout.WEST);
            JButton copiar = Estilo.enlace("Copiar número");
            copiar.addActionListener(ev -> {
                Utilidades.copiar(conductor.getTelefono());
                toast("Número copiado", Dialogos.Tono.EXITO);
            });
            tel.add(copiar, BorderLayout.EAST);
            p.agregar(tel);
        }
        der.agregar(pc);

        // ---------- Acciones ----------
        JPanel acciones = Estilo.panel(new GridLayout(1, 3, 10, 0));
        JButton chat = Estilo.boton("Chat", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.CHAT);
        chat.addActionListener(ev -> DialogoChat.abrir(this, conductor, viaje));
        JButton compartir = Estilo.boton("Compartir", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.COMPARTIR);
        compartir.setToolTipText("Copia los datos del viaje para enviarlos a un contacto de confianza");
        compartir.addActionListener(ev -> {
            Utilidades.copiar(textoCompartir());
            toast("Datos del viaje copiados. Pégalos en WhatsApp o SMS.", Dialogos.Tono.EXITO);
        });
        JButton maps = Estilo.boton("Mapa", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.MAPA);
        maps.setToolTipText("Abrir la ruta en Google Maps");
        maps.addActionListener(ev -> Utilidades.abrirNavegador(this, mapa.enlaceGoogleMaps()));
        acciones.add(chat);
        acciones.add(compartir);
        acciones.add(maps);
        der.agregar(acciones);

        JButton sos = Estilo.boton("SOS · Botón de pánico", Estilo.TipoBoton.PELIGRO, Estilo.Tamano.GRANDE, Iconos.Tipo.SOS);
        sos.setToolTipText("Registra una alerta urgente y avisa a los administradores de Wheels");
        sos.addActionListener(ev -> panico());
        der.agregar(sos);

        JButton finalizar = Estilo.boton("Finalizar viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, Iconos.Tipo.CHECK);
        finalizar.addActionListener(ev -> finalizar());
        der.agregar(finalizar);

        dos.add(der);
        c.agregar(dos);

        actualizar();
        timerViaje = new Timer(1000, ev -> actualizar());
        timerViaje.start();
    }

    @Override public String seccion() { return VIAJES; }

    @Override public void alSalir() { detener(); }

    private JPanel dato(String etiqueta, JLabel valor) {
        JPanel p = Estilo.panel(new BorderLayout(0, 2));
        p.add(Estilo.texto(etiqueta, Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS), BorderLayout.NORTH);
        p.add(valor, BorderLayout.CENTER);
        return p;
    }

    /** Recalcula el progreso con el reloj real. */
    private void actualizar() {
        double transcurridos = (System.currentTimeMillis() - inicio) / 60000.0;
        double progreso = Math.min(1, transcurridos / minutosTotales);
        mapa.setProgreso(progreso);
        barra.setValue((int) (progreso * 1000));
        long llegada = inicio + (long) (minutosTotales * 60000);
        lblEta.setText(HORA.format(Instant.ofEpochMilli(llegada).atZone(ZoneId.systemDefault())).toLowerCase());
        int restantes = (int) Math.ceil(minutosTotales - transcurridos);
        if (progreso >= 1) {
            if (!llego) {
                llego = true;
                lblEstado.setText("Deberías estar llegando");
                lblEstado.setIcon(Iconos.de(Iconos.Tipo.UBICACION, 20, Estilo.VERDE));
                lblRestante.setText("0 min");
            }
        } else {
            lblRestante.setText(restantes + " min");
        }
    }

    private void detener() {
        if (timerViaje != null) timerViaje.stop();
        if (timerAlerta != null) timerAlerta.stop();
    }

    private String textoCompartir() {
        long llegada = inicio + (long) (minutosTotales * 60000);
        return "Voy en un viaje de Wheels: " + viaje.getOrigen() + " → " + viaje.getDestino()
                + ". Conductor: " + viaje.getConductor().getNombre()
                + ", " + viaje.getVehiculo().getNombre() + " color " + viaje.getVehiculo().getColor().toLowerCase()
                + ", placa " + viaje.getVehiculo().getPlaca()
                + ". Llegada estimada: " + HORA.format(Instant.ofEpochMilli(llegada).atZone(ZoneId.systemDefault())).toLowerCase()
                + ". Ruta: " + mapa.enlaceGoogleMaps();
    }

    // ------------------------------------------------------------------
    //  BOTON DE PANICO
    // ------------------------------------------------------------------

    private Estilo.Tarjeta crearAlerta() {
        Estilo.Tarjeta t = new Estilo.Tarjeta(new BorderLayout(14, 0)).conFondo(Estilo.ROJO).conBorde(Estilo.ROJO);
        t.setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));
        t.add(new Estilo.IconoCirculo(Iconos.Tipo.SOS, new Color(255, 255, 255, 40), Color.WHITE, 46), BorderLayout.WEST);
        JPanel txt = Estilo.panel(new GridLayout(2, 1, 0, 2));
        txt.add(Estilo.texto("Alerta SOS activa", Estilo.h3(), Color.WHITE));
        txt.add(Estilo.texto("Emergencias: 123 · Policía: 112 · Línea de la mujer: 155", Estilo.pequena(), new Color(0xFEE2E2)));
        t.add(txt, BorderLayout.CENTER);
        JButton salvo = Estilo.boton("Estoy a salvo", Estilo.TipoBoton.CLARO, Estilo.Tamano.NORMAL, Iconos.Tipo.CHECK);
        salvo.addActionListener(e -> cerrarAlerta());
        JPanel b = Estilo.panel(new java.awt.GridBagLayout());
        b.add(salvo);
        t.add(b, BorderLayout.EAST);
        t.setVisible(false);
        return t;
    }

    private void panico() {
        if (alertaActiva) {
            mostrarInfoAlerta(-1);
            return;
        }
        if (!Dialogos.confirmar(this, "¿Activar la alerta SOS?",
                "Se registrará un reporte urgente en Wheels, se notificará a los administradores con los datos "
                + "del viaje y se copiará un mensaje de emergencia para que lo envíes a un contacto de confianza.",
                "Activar alerta", true)) return;

        alertaActiva = true;
        java.awt.Toolkit.getDefaultToolkit().beep();
        double[] pos = mapa.posicionActual();
        String ubicacion = pos != null
                ? "Posición estimada en la ruta (sin GPS): https://maps.google.com/?q=" + PanelMapa.coord(pos)
                : "Ruta: " + mapa.enlaceGoogleMaps();
        int idReporte = BaseDatos.activarPanico(viaje, ubicacion);
        Utilidades.copiar("EMERGENCIA. " + textoCompartir());
        mapa.setAlerta(true);
        barra.setForeground(Estilo.ROJO);

        alerta.setVisible(true);
        final Color[] colores = {Estilo.ROJO, new Color(0xB91C1C)};
        final int[] i = {0};
        timerAlerta = new Timer(600, e -> {
            i[0]++;
            alerta.conFondo(colores[i[0] % 2]).conBorde(colores[i[0] % 2]);
        });
        timerAlerta.start();
        cuerpo.revalidate();
        subir();
        mostrarInfoAlerta(idReporte);
    }

    private void mostrarInfoAlerta(int idReporte) {
        Diseno.Columna lineas = new Diseno.Columna(400, 0, 0).conSeparacion(10);
        lineas.agregar(Utilidades.filaDato("Línea única de emergencias", "123"));
        lineas.agregar(Utilidades.filaDato("Policía (CAI)", "112"));
        lineas.agregar(Utilidades.filaDato("Línea de la mujer", "155"));
        new Dialogos.Constructor(this, "Alerta SOS enviada")
                .tono(Dialogos.Tono.PELIGRO)
                .mensaje((idReporte > 0 ? "Registramos el reporte de emergencia #" + idReporte + " y " : "")
                        + "avisamos a los administradores de Wheels. Copiamos un mensaje de emergencia con los datos "
                        + "del viaje: pégalo en WhatsApp o SMS a un contacto de confianza. Si estás en peligro, llama ya:")
                .contenido(lineas)
                .botonPrincipal("Entendido", Estilo.TipoBoton.PELIGRO, null)
                .mostrar();
    }

    private void cerrarAlerta() {
        if (!Dialogos.confirmar(this, "¿Estás a salvo?", "Cerraremos la alerta y quedará registrado que estás bien.",
                "Sí, estoy a salvo", false)) return;
        alertaActiva = false;
        if (timerAlerta != null) timerAlerta.stop();
        alerta.setVisible(false);
        mapa.setAlerta(false);
        barra.setForeground(Estilo.TEAL);
        BaseDatos.notificar(BaseDatos.usuarioActual.getId(), "Alerta SOS cerrada",
                "Marcaste que estás a salvo en el viaje " + viaje.getRuta() + ".", "panic_closed");
        cuerpo.revalidate();
        toast("Alerta cerrada. Nos alegra que estés bien.", Dialogos.Tono.EXITO);
    }

    // ------------------------------------------------------------------
    //  Finalizar y calificar
    // ------------------------------------------------------------------

    private void finalizar() {
        if (!llego && !Dialogos.confirmar(this, "¿Finalizar el viaje?",
                "Según el tiempo estimado aún no llegas al destino. ¿Quieres finalizarlo de todas formas?",
                "Finalizar viaje", false)) return;
        detener();
        if (!BaseDatos.finalizarViaje(solicitud)) {
            timerViaje.start();
            return;
        }
        BaseDatos.solicitudEnCurso = null;
        boolean califico = !BaseDatos.yaCalifico(viaje, viaje.getConductor().getId())
                && DialogoCalificar.mostrar(this, viaje);
        irRaiz(() -> new VentanaMisViajes(VentanaMisViajes.HISTORIAL));
        toast(califico ? "Viaje finalizado. ¡Gracias por calificar!" : "Viaje finalizado y guardado en tu historial.",
                Dialogos.Tono.EXITO);
    }
}
