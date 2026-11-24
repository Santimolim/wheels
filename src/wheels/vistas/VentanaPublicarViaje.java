package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/** PANTALLA 11 - Publicar viaje (con vista previa en vivo). */
public class VentanaPublicarViaje extends VentanaBase {

    private final JTextField txtOrigen, txtDestino;
    private final JComboBox<String> cmbFecha, cmbHora, cmbPuestos, cmbCharla;
    private final JSpinner spPrecio;
    private final JCheckBox chkMusica, chkMascotas, chkEquipaje;
    private final Estilo.Campo cOrigen, cDestino, cHora;
    private final Estilo.Aviso aviso = new Estilo.Aviso();
    private final ArrayList<LocalDate> fechas = new ArrayList<>();
    private final JPanel vistaPrevia = Estilo.panel(new BorderLayout());
    private final Vehiculo auto;

    /** Si el usuario no tiene vehiculo, primero lo lleva a registrarlo. */
    public static VentanaBase crear() {
        if (BaseDatos.usuarioActual.tieneVehiculo()) return new VentanaPublicarViaje();
        return new VentanaRegistrarVehiculo(true);
    }

    public VentanaPublicarViaje() {
        super("Publicar viaje");
        Usuario u = BaseDatos.usuarioActual;
        auto = u.getVehiculo();

        encabezado("Publicar un viaje", "Comparte los puestos libres de tu carro", false);
        Diseno.Columna c = crearCuerpo();

        Diseno.Rejilla dos = new Diseno.Rejilla(340, 2, 24, 20).igualarAlto(false).conCorte(860);

        // ---------- Formulario ----------
        Estilo.Tarjeta form = Estilo.Tarjeta.conColumna(4);
        form.conRelleno(22, 24);
        Diseno.Columna f = form.columna();
        f.agregar(aviso);

        f.agregar(Estilo.texto("Recorrido", Estilo.h3(), Estilo.TEXTO));
        f.espacio(10);
        Diseno.Rejilla ruta = new Diseno.Rejilla(200, 2, 14, 0);
        txtOrigen = Estilo.campo("Ej: Centro Chía", Iconos.Tipo.ORIGEN);
        txtDestino = Estilo.campo("Ej: Universidad de La Sabana", Iconos.Tipo.UBICACION);
        cOrigen = new Estilo.Campo("Desde", txtOrigen, null);
        cDestino = new Estilo.Campo("Hacia", txtDestino, null);
        ruta.add(cOrigen);
        ruta.add(cDestino);
        f.agregar(ruta);

        Diseno.Rejilla cuando = new Diseno.Rejilla(130, 4, 14, 0);
        cmbFecha = Estilo.combo(opcionesFecha());
        cmbFecha.setSelectedIndex(1);
        cmbHora = Estilo.combo(opcionesHora());
        cmbHora.setSelectedItem("07:00");
        cHora = new Estilo.Campo("Hora de salida", cmbHora, null);
        String[] puestos = new String[Math.max(1, auto.getPuestos())];
        for (int i = 0; i < puestos.length; i++) puestos[i] = String.valueOf(i + 1);
        cmbPuestos = Estilo.combo(puestos);
        cmbPuestos.setSelectedIndex(Math.min(2, puestos.length - 1));
        spPrecio = new JSpinner(new SpinnerNumberModel(5000, 0, 50000, 500));
        spPrecio.setPreferredSize(new java.awt.Dimension(120, 42));
        spPrecio.setFont(Estilo.cuerpo());
        spPrecio.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc:12");
        JSpinner.NumberEditor ed = new JSpinner.NumberEditor(spPrecio, "$#,##0");
        spPrecio.setEditor(ed);
        cuando.add(new Estilo.Campo("Fecha", cmbFecha, null));
        cuando.add(cHora);
        cuando.add(new Estilo.Campo("Puestos", cmbPuestos, "Máx. " + auto.getPuestos()));
        cuando.add(new Estilo.Campo("Precio por puesto", spPrecio, "En pesos · 0 = gratis"));
        f.agregar(cuando);

        f.espacio(10);
        f.agregar(Estilo.separador());
        f.espacio(16);
        f.agregar(Estilo.texto("Preferencias", Estilo.h3(), Estilo.TEXTO));
        f.espacio(10);
        Diseno.FilaFlexible prefs = new Diseno.FilaFlexible(18, 8);
        chkMusica = check("Música", true);
        chkMascotas = check("Mascotas", false);
        chkEquipaje = check("Equipaje", true);
        prefs.add(chkMusica);
        prefs.add(chkMascotas);
        prefs.add(chkEquipaje);
        f.agregar(prefs);
        f.espacio(12);
        cmbCharla = Estilo.combo(new String[]{"Charla normal", "Viaje silencioso", "Conversador"});
        f.agregar(new Estilo.Campo("Ambiente del viaje", cmbCharla, null));

        f.espacio(6);
        f.agregar(Estilo.separador());
        f.espacio(16);
        JPanel veh = Estilo.panel(new BorderLayout(12, 0));
        veh.add(new Estilo.IconoCirculo(Iconos.Tipo.CARRO, Estilo.HOVER, Estilo.NAVY, 42), BorderLayout.WEST);
        JPanel vt = Estilo.panel(new java.awt.GridLayout(2, 1));
        vt.add(Estilo.texto(auto.getNombre(), Estilo.cuerpoSemi(), Estilo.TEXTO));
        vt.add(Estilo.texto("Color " + auto.getColor().toLowerCase() + " · placa " + auto.getPlaca(), Estilo.pequena(), Estilo.GRIS));
        veh.add(vt, BorderLayout.CENTER);
        JButton cambiar = Estilo.enlace("Editar vehículo");
        cambiar.addActionListener(e -> irA(() -> new VentanaRegistrarVehiculo(false)));
        veh.add(cambiar, BorderLayout.EAST);
        f.agregar(veh);
        f.espacio(20);

        JButton publicar = Estilo.boton("Publicar viaje", Estilo.TipoBoton.ACENTO, Estilo.Tamano.GRANDE, Iconos.Tipo.PUBLICAR);
        publicar.addActionListener(e -> publicar());
        f.agregar(publicar);
        dos.add(form);

        // ---------- Vista previa ----------
        Diseno.Columna prev = new Diseno.Columna(100000, 0, 0).conSeparacion(12);
        prev.agregar(Estilo.texto("Vista previa", Estilo.h3(), Estilo.TEXTO));
        prev.agregar(Estilo.texto("Así verán tu viaje los demás estudiantes.", Estilo.pequena(), Estilo.GRIS));
        vistaPrevia.setPreferredSize(new java.awt.Dimension(300, 196));
        prev.agregar(vistaPrevia);
        Estilo.Aviso consejo = new Estilo.Aviso();
        consejo.mostrar("Escribe lugares conocidos (por ejemplo \"Portal Norte\" o \"Centro Chía\") "
                + "para que la ruta se dibuje en el mapa.", "info");
        prev.agregar(consejo);
        dos.add(prev);
        c.agregar(dos);

        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarVista(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarVista(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarVista(); }
        };
        txtOrigen.getDocument().addDocumentListener(dl);
        txtDestino.getDocument().addDocumentListener(dl);
        cmbFecha.addActionListener(e -> actualizarVista());
        cmbHora.addActionListener(e -> actualizarVista());
        cmbPuestos.addActionListener(e -> actualizarVista());
        spPrecio.addChangeListener(e -> actualizarVista());
        actualizarVista();
    }

    @Override public String seccion() { return PUBLICAR; }
    @Override public boolean esRaiz() { return true; }
    @Override public void alMostrar() { txtOrigen.requestFocusInWindow(); }

    private JCheckBox check(String txt, boolean sel) {
        JCheckBox c = new JCheckBox(txt, sel);
        c.setFont(Estilo.cuerpo());
        c.setForeground(Estilo.TEXTO2);
        c.setOpaque(false);
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    private String[] opcionesFecha() {
        Locale es = Locale.forLanguageTag("es-CO");
        String[] t = new String[30];
        for (int i = 0; i < 30; i++) {
            LocalDate d = LocalDate.now().plusDays(i);
            fechas.add(d);
            String s = i == 0 ? "Hoy" : i == 1 ? "Mañana" : d.format(DateTimeFormatter.ofPattern("EEE d MMM", es)).replace(".", "");
            t[i] = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        }
        return t;
    }

    private static String[] opcionesHora() {
        ArrayList<String> h = new ArrayList<>();
        for (int m = 5 * 60; m <= 22 * 60 + 45; m += 15) h.add(String.format("%02d:%02d", m / 60, m % 60));
        return h.toArray(new String[0]);
    }

    private Viaje viajeEnFormulario() {
        String o = txtOrigen.getText().trim(), d = txtDestino.getText().trim();
        return new Viaje(0, BaseDatos.usuarioActual, auto, o.isEmpty() ? "Origen" : o, d.isEmpty() ? "Destino" : d,
                fechas.get(cmbFecha.getSelectedIndex()).toString(), cmbHora.getSelectedItem() + ":00",
                Integer.parseInt(cmbPuestos.getSelectedItem().toString()), ((Number) spPrecio.getValue()).intValue(),
                Viaje.DISPONIBLE);
    }

    private void actualizarVista() {
        vistaPrevia.removeAll();
        vistaPrevia.add(new TarjetaViaje(viajeEnFormulario(), false), BorderLayout.CENTER);
        vistaPrevia.revalidate();
        vistaPrevia.repaint();
    }

    private void publicar() {
        aviso.ocultar();
        cOrigen.setError(null);
        cDestino.setError(null);
        cHora.setError(null);
        try {
            spPrecio.commitEdit();
        } catch (java.text.ParseException ex) {
            aviso.mostrar("El precio no es válido. Usa solo números, por ejemplo 5000.", "error");
            revalidate();
            return;
        }
        String origen = txtOrigen.getText().trim().replaceAll("\\s+", " ");
        String destino = txtDestino.getText().trim().replaceAll("\\s+", " ");
        LocalDate fecha = fechas.get(cmbFecha.getSelectedIndex());
        LocalTime hora = LocalTime.parse(cmbHora.getSelectedItem().toString());

        boolean ok = true;
        if (origen.length() < 3) { cOrigen.setError("Indica desde dónde sales."); ok = false; }
        if (destino.length() < 3) { cDestino.setError("Indica hacia dónde vas."); ok = false; }
        if (ok && PanelMapa.normalizar(origen).equals(PanelMapa.normalizar(destino))) {
            cDestino.setError("El destino debe ser distinto al origen."); ok = false;
        }
        if (origen.length() > 150 || destino.length() > 150) { aviso.mostrar("El nombre del lugar es demasiado largo.", "error"); ok = false; }
        if (LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now().plusMinutes(10))) {
            cHora.setError("Elige una hora al menos 10 min en el futuro."); ok = false;
        }
        if (!ok) {
            revalidate();
            repaint();
            return;
        }
        String charla = cmbCharla.getSelectedIndex() == 1 ? "quiet" : cmbCharla.getSelectedIndex() == 2 ? "talkative" : "normal";
        Viaje v = BaseDatos.crearViaje(BaseDatos.usuarioActual, origen, destino, fecha.toString(), hora + ":00",
                Integer.parseInt(cmbPuestos.getSelectedItem().toString()), ((Number) spPrecio.getValue()).intValue(),
                chkMusica.isSelected(), chkMascotas.isSelected(), chkEquipaje.isSelected(), charla);
        if (v == null) return;
        BaseDatos.viajeSeleccionado = v;
        JComponent detalle = new TarjetaViaje(v, false);
        irA(() -> new VentanaExito("¡Viaje publicado!",
                "Tu viaje ya es visible para otros estudiantes. Te avisaremos en Notificaciones cuando alguien solicite un puesto.",
                detalle, "Ver mis viajes publicados", () -> Aplicacion.irRaiz(() -> new VentanaMisViajes(VentanaMisViajes.PUBLICADOS)),
                "Publicar otro", () -> Aplicacion.irRaiz(VentanaPublicarViaje::new)));
    }
}
