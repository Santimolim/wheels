package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/** PANTALLA 6 - Detalle del viaje: recorrido, mapa, precio, conductor, vehiculo y solicitud de puesto. */
public class VentanaDetalleViaje extends VentanaBase {

    private final Viaje viaje;

    public VentanaDetalleViaje() {
        super("Detalle del viaje");
        Viaje sel = BaseDatos.viajeSeleccionado;
        Viaje fresco = BaseDatos.buscarViaje(sel.getId());   // datos actualizados (puestos, estado)
        viaje = fresco != null ? fresco : sel;
        BaseDatos.viajeSeleccionado = viaje;

        encabezado(viaje.getOrigen() + " → " + viaje.getDestino(),
                viaje.getFechaAmigable() + " · salida " + viaje.getHora(), true);
        Diseno.Columna c = crearCuerpo();

        // Lo mas importante primero: precio y solicitud, luego el recorrido y quien conduce
        c.conSeparacion(16);
        c.agregar(tarjetaReserva());
        c.agregar(tarjetaRecorrido());
        c.agregar(tarjetaMapa());
        c.agregar(tarjetaConductor());
        c.agregar(tarjetaVehiculo());
        c.agregar(tarjetaPreferencias());
    }

    @Override public String seccion() { return INICIO; }

    // ------------------------------------------------------------------

    private Estilo.Tarjeta tarjetaRecorrido() {
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna col = t.columna();
        col.agregar(Estilo.tituloSeccion("Recorrido", new Estilo.Pastilla(viaje.getFechaAmigable(),
                Estilo.TEAL_SUAVE, Estilo.TEAL_OSCURO, Iconos.Tipo.CALENDARIO)));
        col.espacio(16);
        ArrayList<String[]> paradas = BaseDatos.puntosRecogida(viaje.getId());
        col.agregar(paso(Iconos.Tipo.ORIGEN, Estilo.TEAL_OSCURO, viaje.getOrigen(), "Salida a las " + viaje.getHora(), true));
        for (String[] p : paradas) {
            String hora = p[2] != null && p[2].length() >= 5 ? p[2].substring(0, 5) : "";
            col.agregar(paso(Iconos.Tipo.UBICACION, Estilo.AMBAR_TEXTO, p[0],
                    (hora.isEmpty() ? "" : "Recogida " + hora + " · ") + p[1], true));
        }
        col.agregar(paso(Iconos.Tipo.UBICACION, Estilo.NAVY, viaje.getDestino(), "Llegada", false));
        return t;
    }

    /** Un paso del recorrido con su icono y una linea que lo une con el siguiente. */
    private JPanel paso(Iconos.Tipo icono, Color color, String titulo, String detalle, boolean conLinea) {
        Diseno.Columna txt = new Diseno.Columna(100000, 0, 0).conSeparacion(2);
        txt.agregar(Estilo.texto(titulo, Estilo.cuerpoSemi(), Estilo.TEXTO));
        txt.agregar(new Estilo.Parrafo(detalle, Estilo.pequena(), Estilo.GRIS));
        JPanel marcador = new JPanel(null) {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                Estilo.suavizar(g2);
                if (conLinea) {
                    g2.setColor(Estilo.LINEA_FUERTE);
                    g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND,
                            1f, new float[]{1f, 5f}, 0f));
                    g2.drawLine(15, 30, 15, getHeight() + 2);
                }
                g2.setColor(Estilo.mezclar(color, Color.WHITE, 0.86f));
                g2.fillOval(3, 0, 24, 24);
                Iconos.de(icono, 14, color).paintIcon(this, g2, 8, 5);
                g2.dispose();
            }
        };
        marcador.setOpaque(false);
        marcador.setPreferredSize(new Dimension(30, 24));
        return new PanelAcceso.FilaIcono(marcador, txt) {
            @Override public int altoPara(int ancho) {
                return Math.max(24, txt.altoPara(ancho - 46)) + (conLinea ? 16 : 0);
            }
            @Override public void doLayout() {
                marcador.setBounds(0, 0, 30, getHeight());
                txt.setBounds(46, 2, getWidth() - 46, getHeight());
            }
        };
    }

    private Estilo.Tarjeta tarjetaMapa() {
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        t.conRelleno(10, 10);
        Diseno.Columna col = t.columna();
        PanelMapa mapa = new PanelMapa(viaje);
        mapa.setPreferredSize(new Dimension(300, 260));
        mapa.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        mapa.setToolTipText("Ver mapa completo");
        mapa.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { irA(VentanaMapa::new); }
        });
        col.agregar(mapa);
        col.espacio(10);
        JPanel botones = Estilo.panel(new GridLayout(1, 2, 10, 0));
        JButton ver = Estilo.boton("Ver mapa", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.MAPA);
        ver.addActionListener(e -> irA(VentanaMapa::new));
        JButton gmaps = Estilo.boton("Google Maps", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.NAVEGACION);
        gmaps.addActionListener(e -> Utilidades.abrirNavegador(this, mapa.enlaceGoogleMaps()));
        botones.add(ver);
        botones.add(gmaps);
        col.agregar(botones);
        return t;
    }

    private Estilo.Tarjeta tarjetaPreferencias() {
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna col = t.columna();
        col.agregar(Estilo.texto("Preferencias del conductor", Estilo.h3(), Estilo.TEXTO));
        col.espacio(12);
        Diseno.FilaFlexible chips = new Diseno.FilaFlexible(8, 8);
        ArrayList<String> prefs = BaseDatos.preferencias(viaje.getId());
        for (String p : prefs) {
            Iconos.Tipo ic = p.startsWith("Música") ? Iconos.Tipo.MUSICA : p.startsWith("Mascotas") ? Iconos.Tipo.MASCOTA
                    : p.startsWith("Equipaje") ? Iconos.Tipo.MALETA : Iconos.Tipo.CHAT;
            chips.add(new Estilo.Pastilla(p, Estilo.HOVER, Estilo.TEXTO2, ic));
        }
        if (prefs.isEmpty()) chips.add(Estilo.texto("El conductor no indicó preferencias.", Estilo.pequena(), Estilo.GRIS));
        col.agregar(chips);
        return t;
    }

    private Estilo.Tarjeta tarjetaReserva() {
        Usuario yo = BaseDatos.usuarioActual;
        boolean esMio = viaje.getConductor().getId() == yo.getId();
        String estado = esMio ? null : BaseDatos.estadoSolicitud(viaje);
        Vehiculo auto = viaje.getVehiculo();

        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna col = t.columna();
        JPanel precio = Estilo.panel(new BorderLayout());
        JPanel p1 = Estilo.panel(new BorderLayout(0, 0));
        p1.add(Estilo.texto(viaje.getPrecioTexto(), Estilo.fuente(30, Estilo.NEGRITA), Estilo.NAVY), BorderLayout.NORTH);
        p1.add(Estilo.texto("por puesto", Estilo.pequena(), Estilo.GRIS), BorderLayout.SOUTH);
        precio.add(p1, BorderLayout.WEST);
        if (estado != null) {
            JPanel e = Estilo.panel(new java.awt.GridBagLayout());
            e.add(Estilo.estado(estado));
            precio.add(e, BorderLayout.EAST);
        }
        col.agregar(precio);
        col.espacio(18);

        int libres = viaje.getPuestosDisponibles();
        int total = Math.max(libres, auto.getPuestos());
        JPanel fp = Estilo.panel(new BorderLayout());
        fp.add(Estilo.texto(libres + " de " + total + " puestos libres", Estilo.pequenaSemi(), Estilo.TEXTO2), BorderLayout.WEST);
        col.agregar(fp);
        col.espacio(8);
        JProgressBar barra = new JProgressBar(0, Math.max(1, total));
        barra.setValue(total - libres);
        barra.setForeground(libres <= 1 ? Estilo.AMBAR : Estilo.TEAL);
        barra.setBackground(Estilo.HOVER);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(100, 8));
        barra.getAccessibleContext().setAccessibleName(libres + " puestos libres de " + total);
        col.agregar(barra);
        col.espacio(20);

        Estilo.Boton accion;
        if (esMio) {
            accion = Estilo.boton("Ver solicitudes de este viaje", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, Iconos.Tipo.PERSONAS);
            accion.addActionListener(e -> irRaiz(() -> new VentanaMisViajes(VentanaMisViajes.SOLICITUDES)));
            col.agregar(accion);
            col.espacio(10);
            col.agregar(nota("Este viaje lo publicaste tú."));
        } else if (estado != null) {
            accion = Estilo.boton("Ver en Mis viajes", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, Iconos.Tipo.RUTA);
            accion.addActionListener(e -> irRaiz(VentanaMisViajes::new));
            col.agregar(accion);
            col.espacio(10);
            col.agregar(nota("Ya enviaste una solicitud para este viaje (" + estado.toLowerCase() + ")."));
        } else {
            String motivo = viaje.estaCancelado() ? "El conductor canceló este viaje."
                    : viaje.yaPaso() ? "Este viaje ya salió."
                    : (libres <= 0 || Viaje.LLENO.equals(viaje.getEstado())) ? "Ya no quedan puestos libres." : null;
            accion = Estilo.boton(motivo == null ? "Solicitar puesto" : "No disponible",
                    Estilo.TipoBoton.ACENTO, Estilo.Tamano.GRANDE, motivo == null ? Iconos.Tipo.CHECK : null);
            accion.setEnabled(motivo == null);
            accion.addActionListener(e -> solicitar());
            col.agregar(accion);
            col.espacio(10);
            col.agregar(nota(motivo != null ? motivo : "No pagas nada ahora. El conductor debe aceptar tu solicitud."));
        }
        if (!esMio) {
            col.espacio(10);
            JButton chat = Estilo.boton("Escribir a " + viaje.getConductor().getPrimerNombre(),
                    Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.CHAT);
            chat.addActionListener(e -> DialogoChat.abrir(this, viaje.getConductor(), viaje));
            col.agregar(chat);
        }
        return t;
    }

    private Estilo.Parrafo nota(String txt) {
        Estilo.Parrafo p = new Estilo.Parrafo(txt, Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS);
        p.centrar();
        return p;
    }

    private Estilo.Tarjeta tarjetaConductor() {
        Usuario d = viaje.getConductor();
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna col = t.columna();
        col.agregar(Estilo.texto("Conductor", Estilo.mini(), Estilo.GRIS));
        col.espacio(12);
        JPanel fila = Estilo.panel(new BorderLayout(14, 0));
        fila.add(new Estilo.Avatar(d.getIniciales(), d.getEmail(), 52), BorderLayout.WEST);
        JPanel txt = Estilo.panel(new GridLayout(2, 1, 0, 2));
        txt.add(Estilo.texto(d.getNombre(), Estilo.h3(), Estilo.TEXTO));
        txt.add(Estilo.texto(d.getCarrera() + " · " + d.getSemestre() + "° semestre", Estilo.pequena(), Estilo.GRIS));
        fila.add(txt, BorderLayout.CENTER);
        JPanel cal = Estilo.panel(new java.awt.GridBagLayout());
        cal.add(Estilo.calificacion(d.getCalificacion()));
        fila.add(cal, BorderLayout.EAST);
        col.agregar(fila);
        col.espacio(14);
        col.agregar(Estilo.dato(Iconos.Tipo.UNIVERSIDAD, d.getUniversidad()));
        return t;
    }

    private Estilo.Tarjeta tarjetaVehiculo() {
        Vehiculo v = viaje.getVehiculo();
        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
        Diseno.Columna col = t.columna();
        col.agregar(Estilo.texto("Vehículo", Estilo.mini(), Estilo.GRIS));
        col.espacio(12);
        JPanel fila = Estilo.panel(new BorderLayout(14, 0));
        fila.add(new Estilo.IconoCirculo(Iconos.Tipo.CARRO, Estilo.HOVER, Estilo.NAVY, 48), BorderLayout.WEST);
        JPanel txt = Estilo.panel(new GridLayout(2, 1, 0, 2));
        txt.add(Estilo.texto(v.getNombre(), Estilo.h3(), Estilo.TEXTO));
        txt.add(Estilo.texto("Color " + v.getColor().toLowerCase() + " · " + v.getPuestos() + " puestos", Estilo.pequena(), Estilo.GRIS));
        fila.add(txt, BorderLayout.CENTER);
        JPanel placa = Estilo.panel(new java.awt.GridBagLayout());
        placa.add(Utilidades.placa(v.getPlaca()));
        fila.add(placa, BorderLayout.EAST);
        col.agregar(fila);
        return t;
    }

    // ------------------------------------------------------------------

    private void solicitar() {
        Diseno.Columna resumen = new Diseno.Columna(400, 0, 0).conSeparacion(10);
        resumen.agregar(Utilidades.filaDato("Ruta", viaje.getRuta()));
        resumen.agregar(Utilidades.filaDato("Salida", viaje.getFechaAmigable() + " · " + viaje.getHora()));
        resumen.agregar(Utilidades.filaDato("Conductor", viaje.getConductor().getNombre()));
        resumen.agregar(Estilo.separador());
        resumen.agregar(Utilidades.filaDato("Total por tu puesto", viaje.getPrecioTexto()));

        int r = new Dialogos.Constructor(this, "Confirma tu solicitud")
                .mensaje(viaje.getConductor().getPrimerNombre() + " recibirá tu solicitud y debe aceptarla para reservar tu puesto. "
                        + "Te avisaremos en Notificaciones.")
                .contenido(resumen)
                .boton("Cancelar", Estilo.TipoBoton.SECUNDARIO, null)
                .botonPrincipal("Enviar solicitud", Estilo.TipoBoton.ACENTO, null)
                .mostrar();
        if (r != 1) return;

        String error = BaseDatos.crearSolicitud(viaje, BaseDatos.usuarioActual);
        if (error != null) {
            Dialogos.info(this, "No se pudo enviar", error, Dialogos.Tono.ALERTA);
            Aplicacion.recargar();
            return;
        }
        final Viaje v = viaje;
        irA(() -> new VentanaExito("Solicitud enviada",
                v.getConductor().getPrimerNombre() + " recibió tu solicitud para " + v.getRuta()
                + ". Te avisaremos cuando la acepte; puedes seguir su estado en Mis viajes.",
                "Ver mis viajes", () -> Aplicacion.irRaiz(VentanaMisViajes::new),
                "Volver al inicio", () -> Aplicacion.irRaiz(VentanaHome::new)));
    }
}
