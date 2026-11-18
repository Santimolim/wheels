package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;

/** PANTALLA 2 - Log In. */
public class VentanaLogin extends VentanaBase {

    private JTextField txtEmail;
    private JPasswordField txtPassword;

    public VentanaLogin() {
        super("Iniciar sesion");

        JLabel logo = new JLabel("W", SwingConstants.CENTER);
        logo.setBounds(160, 50, 60, 60);
        logo.setOpaque(true);
        logo.setBackground(Estilo.TEAL);
        logo.setForeground(Estilo.NAVY);
        logo.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 32));
        lienzo.add(logo);

        JLabel t = new JLabel("Bienvenido de nuevo", SwingConstants.CENTER);
        t.setBounds(0, 130, Estilo.ANCHO, 28);
        t.setFont(Estilo.TITULO);
        t.setForeground(Estilo.TEXTO);
        lienzo.add(t);

        JLabel s = new JLabel("Ingresa con tu correo institucional", SwingConstants.CENTER);
        s.setBounds(0, 158, Estilo.ANCHO, 20);
        s.setFont(Estilo.PEQUENA);
        s.setForeground(Estilo.GRIS);
        lienzo.add(s);

        lienzo.add(Estilo.etiqueta("Correo institucional", 30, 210));
        txtEmail = Estilo.campo(30, 232, 320);
        lienzo.add(txtEmail);

        lienzo.add(Estilo.etiqueta("Contrasena", 30, 280));
        txtPassword = Estilo.campoClave(30, 302, 320);
        lienzo.add(txtPassword);

        JButton entrar = Estilo.botonPrimario("Log In", 30, 365, 320);
        entrar.addActionListener(e -> iniciarSesion());
        lienzo.add(entrar);

        JButton crear = Estilo.enlace("No tienes cuenta? Crear cuenta", 30, 420, 320);
        crear.addActionListener(e -> irA(new VentanaRegistro()));
        lienzo.add(crear);

        txtPassword.addActionListener(e -> iniciarSesion());   // Enter para entrar

        JLabel estado = new JLabel("Conectado a la base de datos Wheels", SwingConstants.CENTER);
        estado.setBounds(30, 620, 320, 20);
        estado.setFont(Estilo.PEQUENA);
        estado.setForeground(Estilo.VERDE);
        lienzo.add(estado);
    }

    private void iniciarSesion() {
        String email = txtEmail.getText().trim();
        String clave = new String(txtPassword.getPassword());

        if (email.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes llenar los dos campos.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@") || !email.contains(".edu")) {
            JOptionPane.showMessageDialog(this,
                    "Debes usar un correo institucional (que contenga .edu).",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = BaseDatos.validarLogin(email, clave);

        if (u == null) {
            JOptionPane.showMessageDialog(this, "Correo o contrasena incorrectos.",
                    "Wheels", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BaseDatos.usuarioActual = u;
        irA(new VentanaHome());
    }
}
