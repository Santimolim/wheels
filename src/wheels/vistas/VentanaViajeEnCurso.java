package wheels.vistas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import wheels.datos.BaseDatos;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Viaje;

/**
 * PANTALLA - Viaje en curso.
 * Muestra el mapa con el carro avanzando, tiempo estimado de llegada,
 * datos del conductor, chat, compartir ubicacion y BOTON DE PANICO (SOS).
 */
public class VentanaViajeEnCurso extends VentanaBase {

    private final Solicitud solicitud;
    private final Viaje viaje;
    private final PanelMapa mapa;
    private final JPanel cabecera;
    private final JLabel lblEstado, lblEta, lblVelocidad;
    private Timer timerViaje, timerAlerta;
    private double progreso = 0;
    private boolean alertaActiva = false;
    private boolean llego = false;

    public VentanaViajeEnCurso() {
        super("Viaje en curso");
        solicitud = BaseDatos.solicitudEnCurso;
        viaje = solicitud.getViaje();
        Usuario conductor = viaje.getConductor();

        // ---------- Cabecera ----------
        cabecera = Estilo.encabezado("Viaje en curso", viaje.getRuta());
        lienzo.add(cabecera);

        // ---------- Mapa ----------
        mapa = new PanelMapa(viaje);
        mapa.setBounds(12, 88, 356, 300);
        mapa.setProgreso(0);
        lienzo.add(mapa);

        // Boton SOS flotante sobre el mapa
        JButton sos = new Estilo.Boton("SOS", 60) {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                Estilo.suavizar(g2);
                g2.setColor(new Color(220, 38, 38, 70));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        sos.setBounds(298, 300, 62, 62);
        sos.setBackground(Estilo.ROJO);
        sos.setForeground(Color.WHITE);
        sos.setFont(new Font("Segoe UI", Font.BOLD, 15));
        sos.setToolTipText("Boton de panico: alerta a Wheels y comparte tu ubicacion");
        sos.addActionListener(e -> panico());
        lienzo.add(sos);
        lienzo.setComponentZOrder(sos, 0);

        // ---------- Estado / ETA ----------
        JPanel info = Estilo.tarjeta(12, 396, 356, 74);
        lblEstado = Estilo.texto("En camino", 14, 10, 200, Estilo.SUBTITULO, Estilo.TEXTO);
        info.add(lblEstado);
        lblEta = Estilo.texto("", 14, 32, 220, Estilo.NORMAL, Estilo.GRIS);
        info.add(lblEta);
        lblVelocidad = Estilo.texto("", 230, 10, 112, Estilo.TITULO, Estilo.AZUL);
        lblVelocidad.setHorizontalAlignment(SwingConstants.RIGHT);
        info.add(lblVelocidad);
        JLabel gps = Estilo.texto("GPS simulado", 230, 38, 112, Estilo.PEQUENA, Estilo.GRIS);
        gps.setHorizontalAlignment(SwingConstants.RIGHT);
        info.add(gps);
        lblEta.setText(String.format("Ruta de %.1f km", mapa.distanciaKm()));
        lienzo.add(info);

        // ---------- Conductor ----------
        JPanel pc = Estilo.tarjeta(12, 476, 356, 64);
        pc.add(Estilo.avatar(conductor.getIniciales(), 12, 12, 38));
        pc.add(Estilo.texto(conductor.getNombre() + "   " + Estilo.estrellas(conductor.getCalificacion()),
                60, 10, 290, Estilo.SUBTITULO, Estilo.TEXTO));
        pc.add(Estilo.texto(viaje.getVehiculo() + "  -  " + viaje.getVehiculo().getColor(),
                60, 30, 290, Estilo.PEQUENA, Estilo.GRIS));
        lienzo.add(pc);

        // ---------- Acciones ----------
        JButton chat = Estilo.botonBlanco("Chat", 12, 548, 112);
        chat.addActionListener(e -> new DialogoChat(this, conductor, viaje).setVisible(true));
        lienzo.add(chat);

        JButton compartir = Estilo.botonBlanco("Compartir", 134, 548, 112);
        compartir.addActionListener(e -> compartirUbicacion(true));
        lienzo.add(compartir);

        JButton maps = Estilo.botonBlanco("Google Maps", 256, 548, 112);
        maps.addActionListener(e -> abrirNavegador(mapa.enlaceGoogleMaps()));
        lienzo.add(maps);

        JButton finalizar = Estilo.botonPrimario("Finalizar viaje", 12, 596, 356);
        finalizar.addActionListener(e -> finalizar());
        lienzo.add(finalizar);

        JButton salir = Estilo.enlace("< Volver a Mis viajes (el viaje sigue en curso)", 12, 648, 356);
        salir.addActionListener(e -> { detener(); irA(new VentanaMisViajes()); });
        lienzo.add(salir);

        // ---------- Simulacion del recorrido ----------
        final double km = mapa.distanciaKm();
        final double minutosTotales = Math.max(8, km / 28.0 * 60);   // ~28 km/h en ciudad
        timerViaje = new Timer(100, e -> {
            if (llego) return;
            progreso = Math.min(1, progreso + 0.0025);   // ~40 s de recorrido en pantalla
            mapa.setProgreso(progreso);
            int restantes = (int) Math.ceil((1 - progreso) * minutosTotales);
            lblEta.setText(String.format("Llegada en %d min  -  %.1f km restantes", restantes, (1 - progreso) * km));
            lblVelocidad.setText((22 + (int) (Math.sin(progreso * 40) * 8 + 8)) + " km/h");
            if (progreso >= 1) {
                llego = true;
                lblEstado.setText("Has llegado a tu destino");
                lblEstado.setForeground(Estilo.VERDE);
                lblEta.setText(viaje.getDestino());
                lblVelocidad.setText("0 km/h");
            }
        });
        timerViaje.start();
    }

    private void detener() {
        if (timerViaje != null) timerViaje.stop();
        if (timerAlerta != null) timerAlerta.stop();
    }

    private String ubicacionTexto() {
        return "https://maps.google.com/?q=" + PanelMapa.coord(mapa.posicionActual());
    }

    private void compartirUbicacion(boolean avisar) {
        String txt = "Voy en un viaje Wheels: " + viaje.getRuta() + ". Conductor: "
                + viaje.getConductor().getNombre() + ", placa " + viaje.getVehiculo().getPlaca()
                + ". Mi ubicacion: " + ubicacionTexto();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt), null);
        if (avisar) {
            JOptionPane.showMessageDialog(this,
                    "Ubicacion copiada al portapapeles.\nPegala en WhatsApp o SMS para compartirla.",
                    "Compartir ubicacion", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    //  BOTON DE PANICO
    // ------------------------------------------------------------------

    private void panico() {
        if (alertaActiva) {
            mostrarPanelAlerta(-1);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this,
                "Activar ALERTA SOS?\n\n"
                + "- Se registra un reporte urgente en Wheels\n"
                + "- Se notifica a los administradores con tu ubicacion\n"
                + "- Se copia tu ubicacion para compartirla\n",
                "Boton de panico", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r != JOptionPane.YES_OPTION) return;

        alertaActiva = true;
        Toolkit.getDefaultToolkit().beep();
        int idReporte = BaseDatos.activarPanico(viaje, ubicacionTexto());
        compartirUbicacion(false);
        mapa.setAlerta(true);

        // La cabecera parpadea en rojo
        final Color[] colores = {Estilo.ROJO, new Color(127, 29, 29)};
        final int[] i = {0};
        ((JLabel) cabecera.getComponent(0)).setText("ALERTA SOS ACTIVA");
        ((JLabel) cabecera.getComponent(1)).setForeground(Color.WHITE);
        timerAlerta = new Timer(500, e -> {
            i[0]++;
            ((Estilo.PanelDegradado) cabecera).setColorFijo(colores[i[0] % 2]);
        });
        ((Estilo.PanelDegradado) cabecera).setColorFijo(Estilo.ROJO);
        timerAlerta.start();
        lblEstado.setText("Alerta SOS enviada");
        lblEstado.setForeground(Estilo.ROJO);

        mostrarPanelAlerta(idReporte);
    }

    private void mostrarPanelAlerta(int idReporte) {
        String msg = "<html><div style='width:260px'>"
                + "<h2 style='color:#dc2626;margin:0'>Alerta SOS enviada</h2>"
                + (idReporte > 0 ? "<p>Reporte de emergencia <b>#" + idReporte + "</b> registrado.</p>" : "")
                + "<p>Los administradores de Wheels fueron notificados con tu ubicacion. "
                + "Tu ubicacion ya esta copiada: pegala en WhatsApp a un contacto de confianza.</p>"
                + "<p><b>Lineas de emergencia en Colombia:</b><br>"
                + "Linea unica de emergencias: <b>123</b><br>"
                + "Policia (CAI): <b>112</b><br>Linea de la mujer: <b>155</b></p></div></html>";
        Object[] opciones = {"Estoy a salvo", "Seguir con alerta"};
        int r = JOptionPane.showOptionDialog(this, msg, "SOS", JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE, null, opciones, opciones[1]);
        if (r == 0) {
            alertaActiva = false;
            if (timerAlerta != null) timerAlerta.stop();
            mapa.setAlerta(false);
            BaseDatos.notificar(BaseDatos.usuarioActual.getId(), "Alerta SOS cerrada",
                    "Marcaste que estas a salvo en el viaje " + viaje.getRuta() + ".", "panic_closed");
            ((Estilo.PanelDegradado) cabecera).setColorFijo(Estilo.VERDE);
            ((JLabel) cabecera.getComponent(0)).setText("Estas a salvo - viaje en curso");
            lblEstado.setText(llego ? "Has llegado a tu destino" : "En camino");
            lblEstado.setForeground(Estilo.TEXTO);
        }
    }

    // ------------------------------------------------------------------
    //  Finalizar y calificar
    // ------------------------------------------------------------------

    private void finalizar() {
        if (!llego) {
            int r = JOptionPane.showConfirmDialog(this,
                    "Aun no llegas al destino. Finalizar el viaje de todas formas?",
                    "Wheels", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
        }
        detener();
        BaseDatos.cambiarEstado(solicitud, Solicitud.COMPLETADA);

        // Calificacion con estrellas
        JPanel p = new JPanel(null);
        p.setPreferredSize(new java.awt.Dimension(280, 120));
        p.add(Estilo.texto("Como estuvo tu viaje con " + viaje.getConductor().getPrimerNombre() + "?",
                0, 0, 280, Estilo.SUBTITULO, Estilo.TEXTO));
        final int[] puntaje = {5};
        final JButton[] estrellas = new JButton[5];
        for (int k = 0; k < 5; k++) {
            final int valor = k + 1;
            JButton b = new JButton("★");
            b.setBounds(k * 46, 28, 44, 40);
            b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 26));
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setForeground(Estilo.AMBAR);
            b.addActionListener(ev -> {
                puntaje[0] = valor;
                for (int j = 0; j < 5; j++) estrellas[j].setForeground(j < valor ? Estilo.AMBAR : Estilo.LINEA);
            });
            estrellas[k] = b;
            p.add(b);
        }
        JTextField comentario = Estilo.campo(0, 80, 280);
        comentario.setText("Excelente viaje");
        p.add(comentario);

        int r = JOptionPane.showConfirmDialog(this, p, "Califica tu viaje",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r == JOptionPane.OK_OPTION) {
            BaseDatos.calificar(viaje, viaje.getConductor().getId(), puntaje[0], comentario.getText().trim());
            JOptionPane.showMessageDialog(this, "Gracias por calificar. Viaje guardado en tu historial.",
                    "Wheels", JOptionPane.INFORMATION_MESSAGE);
        }
        BaseDatos.solicitudEnCurso = null;
        irA(new VentanaMisViajes(Solicitud.COMPLETADA));
    }

    @Override
    public void dispose() {
        detener();
        super.dispose();
    }

    static void abrirNavegador(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception ex) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(url), null);
            JOptionPane.showMessageDialog(null, "No se pudo abrir el navegador. Enlace copiado:\n" + url);
        }
    }
}
