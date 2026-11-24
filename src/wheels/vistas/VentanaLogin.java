package wheels.vistas;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.util.prefs.Preferences;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import wheels.datos.BaseDatos;
import wheels.datos.Conexion;
import wheels.modelo.Usuario;

/** PANTALLA 2 - Iniciar sesion con correo institucional y contrasena. */
public class VentanaLogin extends VentanaBase {

    static final String REGEX_CORREO = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.edu(\\.[A-Za-z]{2})?$";
    private static final Preferences PREFS = Preferences.userNodeForPackage(VentanaLogin.class);

    private final JTextField txtEmail;
    private final JPasswordField txtPassword;
    private final Estilo.Campo campoEmail, campoClave;
    private final JCheckBox recordar;
    private final Estilo.Aviso aviso;
    private final Estilo.Boton entrar;

    public VentanaLogin() {
        super("Iniciar sesión");

        Diseno.Columna f = new Diseno.Columna(400, 28, 40).centrada();
        PanelAcceso panel = new PanelAcceso(f);

        f.agregar(PanelAcceso.botonVolver());
        f.espacio(12);
        JPanel logo = PanelAcceso.filaIzquierda(new Logo.Marca(38, Estilo.NAVY, true));
        f.agregar(logo);
        JComponentEspacio sep = new JComponentEspacio(36);
        f.agregar(sep);
        panel.soloEnAngosto(logo);
        panel.soloEnAngosto(sep);

        f.agregar(Estilo.texto("Bienvenido de nuevo", Estilo.h1(), Estilo.TEXTO));
        f.espacio(6);
        f.agregar(new Estilo.Parrafo("Ingresa con tu correo institucional para continuar.", Estilo.cuerpo(), Estilo.GRIS));
        f.espacio(24);

        aviso = new Estilo.Aviso();
        f.agregar(aviso);
        f.espacio(4);

        txtEmail = Estilo.campo("nombre@universidad.edu.co", Iconos.Tipo.CORREO);
        txtEmail.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        campoEmail = new Estilo.Campo("Correo institucional", txtEmail, null);
        f.agregar(campoEmail);
        f.espacio(6);

        txtPassword = Estilo.campoClave("Tu contraseña");
        campoClave = new Estilo.Campo("Contraseña", txtPassword, null);
        f.agregar(campoClave);
        f.espacio(2);

        recordar = new JCheckBox("Recordar mi correo en este equipo");
        recordar.setFont(Estilo.pequena());
        recordar.setForeground(Estilo.TEXTO2);
        recordar.setOpaque(false);
        recordar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        f.agregar(recordar);
        f.espacio(22);

        entrar = Estilo.boton("Iniciar sesión", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, null);
        entrar.addActionListener(e -> iniciarSesion());
        f.agregar(entrar);
        f.espacio(20);

        JPanel crear = Estilo.panel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 0));
        crear.add(Estilo.texto("¿Aún no tienes cuenta?", Estilo.pequena(), Estilo.GRIS));
        JButton link = Estilo.enlace("Crea una gratis");
        link.addActionListener(e -> irA(VentanaRegistro::new));
        crear.add(link);
        f.agregar(crear);
        f.espacio(36);

        boolean conectado = BaseDatos.probarConexion();
        JLabel estado = Estilo.texto(conectado
                ? "Conectado · " + Conexion.getInstancia().getDescripcion()
                : "Sin conexión con la base de datos", Estilo.fuente(12, Estilo.REGULAR),
                conectado ? Estilo.VERDE_TEXTO : Estilo.ROJO_TEXTO);
        estado.setIcon(new PuntoEstado(conectado ? Estilo.VERDE : Estilo.ROJO));
        estado.setIconTextGap(8);
        estado.setHorizontalAlignment(JLabel.CENTER);
        f.agregar(estado);

        txtEmail.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> iniciarSesion());

        String guardado = PREFS.get("correo", "");
        if (!guardado.isEmpty()) {
            txtEmail.setText(guardado);
            recordar.setSelected(true);
        }

        add(panel, BorderLayout.CENTER);
    }

    @Override public boolean esRaiz() { return false; }

    @Override public void alMostrar() {
        if (txtEmail.getText().isEmpty()) txtEmail.requestFocusInWindow();
        else txtPassword.requestFocusInWindow();
    }

    private void iniciarSesion() {
        aviso.ocultar();
        campoEmail.setError(null);
        campoClave.setError(null);
        final String email = txtEmail.getText().trim();
        final String clave = new String(txtPassword.getPassword());

        boolean ok = true;
        if (email.isEmpty()) {
            campoEmail.setError("Escribe tu correo institucional.");
            ok = false;
        } else if (!email.matches(REGEX_CORREO)) {
            campoEmail.setError("Usa tu correo institucional (termina en .edu o .edu.co).");
            ok = false;
        }
        if (clave.isEmpty()) {
            campoClave.setError("Escribe tu contraseña.");
            ok = false;
        }
        if (!ok) {
            cuerpoRevalidar();
            return;
        }

        entrar.setEnabled(false);
        entrar.setText("Ingresando…");
        new SwingWorker<Usuario, Void>() {
            @Override protected Usuario doInBackground() { return BaseDatos.validarLogin(email, clave); }
            @Override protected void done() {
                entrar.setEnabled(true);
                entrar.setText("Iniciar sesión");
                Usuario u;
                try {
                    u = get();
                } catch (Exception ex) {
                    u = null;
                }
                if (u == null) {
                    aviso.mostrar("Correo o contraseña incorrectos. Revisa los datos e inténtalo de nuevo.", "error");
                    txtPassword.selectAll();
                    txtPassword.requestFocusInWindow();
                    cuerpoRevalidar();
                    return;
                }
                if (recordar.isSelected()) PREFS.put("correo", email); else PREFS.remove("correo");
                BaseDatos.usuarioActual = u;
                irRaiz(VentanaHome::new);
                toast("¡Hola, " + u.getPrimerNombre() + "!", Dialogos.Tono.EXITO);
            }
        }.execute();
    }

    private void cuerpoRevalidar() {
        revalidate();
        repaint();
    }

    /** Espacio vertical que se puede ocultar (solo visible en ventana angosta). */
    static final class JComponentEspacio extends javax.swing.JComponent {
        JComponentEspacio(int alto) { setPreferredSize(new java.awt.Dimension(1, alto)); }
    }

    /** Punto de color para el estado de la conexion. */
    static final class PuntoEstado implements javax.swing.Icon {
        private final java.awt.Color color;
        PuntoEstado(java.awt.Color c) { color = c; }
        @Override public int getIconWidth() { return 8; }
        @Override public int getIconHeight() { return 8; }
        @Override public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            Estilo.suavizar(g2);
            g2.setColor(color);
            g2.fillOval(x, y, 8, 8);
            g2.dispose();
        }
    }
}
