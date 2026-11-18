package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;

/** PANTALLA 3 - Create Account. */
public class VentanaRegistro extends VentanaBase {

    private JTextField txtNombre;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbUniversidad;
    private JComboBox<String> cmbCarrera;

    public VentanaRegistro() {
        super("Crear cuenta");

        JButton volver = Estilo.enlace("< Volver", 20, 16, 80);
        volver.addActionListener(e -> irA(new VentanaLogin()));
        lienzo.add(volver);

        lienzo.add(Estilo.titulo("Crear cuenta", 30, 48));
        lienzo.add(Estilo.texto("Solo para estudiantes activos", 30, 76, 300,
                Estilo.PEQUENA, Estilo.GRIS));

        lienzo.add(Estilo.etiqueta("Nombre completo", 30, 110));
        txtNombre = Estilo.campo(30, 130, 320);
        lienzo.add(txtNombre);

        lienzo.add(Estilo.etiqueta("Correo institucional", 30, 176));
        txtEmail = Estilo.campo(30, 196, 320);
        lienzo.add(txtEmail);

        lienzo.add(Estilo.etiqueta("Contrasena", 30, 242));
        txtPassword = Estilo.campoClave(30, 262, 320);
        lienzo.add(txtPassword);

        lienzo.add(Estilo.etiqueta("Universidad", 30, 308));
        cmbUniversidad = new JComboBox<>(BaseDatos.UNIVERSIDADES);
        cmbUniversidad.setBounds(30, 328, 320, 34);
        cmbUniversidad.setFont(Estilo.NORMAL);
        cmbUniversidad.setBackground(Estilo.BLANCO);
        lienzo.add(cmbUniversidad);

        lienzo.add(Estilo.etiqueta("Carrera", 30, 374));
        cmbCarrera = new JComboBox<>(BaseDatos.CARRERAS);
        cmbCarrera.setBounds(30, 394, 320, 34);
        cmbCarrera.setFont(Estilo.NORMAL);
        cmbCarrera.setBackground(Estilo.BLANCO);
        lienzo.add(cmbCarrera);

        JButton crear = Estilo.botonPrimario("Create Account", 30, 460, 320);
        crear.addActionListener(e -> registrar());
        lienzo.add(crear);

        JButton ya = Estilo.enlace("Ya tienes cuenta? Inicia sesion", 30, 512, 320);
        ya.addActionListener(e -> irA(new VentanaLogin()));
        lienzo.add(ya);
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String email  = txtEmail.getText().trim();
        String clave  = new String(txtPassword.getPassword());

        if (nombre.isEmpty() || email.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes llenar todos los campos.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.contains("@") || !email.contains(".edu")) {
            JOptionPane.showMessageDialog(this,
                    "El correo debe ser institucional (que contenga .edu).",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (clave.length() < 4) {
            JOptionPane.showMessageDialog(this, "La contrasena debe tener al menos 4 caracteres.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (BaseDatos.existeEmail(email)) {
            JOptionPane.showMessageDialog(this, "Ese correo ya esta registrado.",
                    "Wheels", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario nuevo = BaseDatos.crearUsuario(nombre, email, clave,
                cmbUniversidad.getSelectedItem().toString(),
                cmbCarrera.getSelectedItem().toString());

        BaseDatos.usuarioActual = nuevo;

        JOptionPane.showMessageDialog(this, "Cuenta creada. Bienvenido a Wheels, " + nombre + "!",
                "Wheels", JOptionPane.INFORMATION_MESSAGE);

        irA(new VentanaHome());
    }
}
