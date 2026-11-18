package wheels.vistas;

import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;

/** PANTALLA 3 - Create Account. Guarda el usuario en la tabla users. */
public class VentanaRegistro extends VentanaBase {

    private JTextField txtNombre, txtEmail, txtTelefono;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbUniversidad, cmbCarrera, cmbSemestre;
    private final ArrayList<String[]> universidades;

    public VentanaRegistro() {
        super("Crear cuenta");

        JButton volver = Estilo.enlace("< Volver", 20, 16, 80);
        volver.addActionListener(e -> irA(new VentanaLogin()));
        lienzo.add(volver);

        lienzo.add(Estilo.titulo("Crear cuenta", 30, 44));
        lienzo.add(Estilo.texto("Solo para estudiantes activos", 30, 70, 300,
                Estilo.PEQUENA, Estilo.GRIS));

        lienzo.add(Estilo.etiqueta("Nombre completo", 30, 100));
        txtNombre = Estilo.campo(30, 118, 320);
        lienzo.add(txtNombre);

        lienzo.add(Estilo.etiqueta("Correo institucional", 30, 158));
        txtEmail = Estilo.campo(30, 176, 320);
        lienzo.add(txtEmail);

        lienzo.add(Estilo.etiqueta("Contrasena", 30, 216));
        txtPassword = Estilo.campoClave(30, 234, 155);
        lienzo.add(txtPassword);

        lienzo.add(Estilo.etiqueta("Celular", 195, 216));
        txtTelefono = Estilo.campo(195, 234, 155);
        lienzo.add(txtTelefono);

        lienzo.add(Estilo.etiqueta("Universidad", 30, 274));
        universidades = BaseDatos.universidades();
        cmbUniversidad = new JComboBox<>();
        for (String[] u : universidades) cmbUniversidad.addItem(u[1]);
        cmbUniversidad.setBounds(30, 292, 320, 34);
        cmbUniversidad.setFont(Estilo.NORMAL);
        cmbUniversidad.setBackground(Estilo.BLANCO);
        lienzo.add(cmbUniversidad);

        lienzo.add(Estilo.etiqueta("Carrera", 30, 332));
        cmbCarrera = new JComboBox<>(BaseDatos.CARRERAS);
        cmbCarrera.setBounds(30, 350, 220, 34);
        cmbCarrera.setFont(Estilo.NORMAL);
        cmbCarrera.setBackground(Estilo.BLANCO);
        lienzo.add(cmbCarrera);

        lienzo.add(Estilo.etiqueta("Semestre", 260, 332));
        String[] sem = new String[10];
        for (int i = 0; i < 10; i++) sem[i] = String.valueOf(i + 1);
        cmbSemestre = new JComboBox<>(sem);
        cmbSemestre.setBounds(260, 350, 90, 34);
        cmbSemestre.setFont(Estilo.NORMAL);
        cmbSemestre.setBackground(Estilo.BLANCO);
        lienzo.add(cmbSemestre);

        JButton crear = Estilo.botonPrimario("Create Account", 30, 410, 320);
        crear.addActionListener(e -> registrar());
        lienzo.add(crear);

        JButton ya = Estilo.enlace("Ya tienes cuenta? Inicia sesion", 30, 462, 320);
        ya.addActionListener(e -> irA(new VentanaLogin()));
        lienzo.add(ya);
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String email  = txtEmail.getText().trim();
        String clave  = new String(txtPassword.getPassword());
        String tel    = txtTelefono.getText().trim();

        if (nombre.isEmpty() || email.isEmpty() || clave.isEmpty() || tel.isEmpty()) {
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
        if (!tel.matches("[0-9]{7,15}")) {
            JOptionPane.showMessageDialog(this, "El celular solo debe tener numeros (7 a 15 digitos).",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (BaseDatos.existeEmail(email)) {
            JOptionPane.showMessageDialog(this, "Ese correo ya esta registrado.",
                    "Wheels", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cmbUniversidad.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "No hay universidades cargadas en la base de datos.",
                    "Wheels", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idUni = Integer.parseInt(universidades.get(cmbUniversidad.getSelectedIndex())[0]);
        Usuario nuevo = BaseDatos.crearUsuario(nombre, email, clave, idUni,
                cmbCarrera.getSelectedItem().toString(),
                Integer.parseInt(cmbSemestre.getSelectedItem().toString()), tel);
        if (nuevo == null) return;

        BaseDatos.usuarioActual = nuevo;

        JOptionPane.showMessageDialog(this, "Cuenta creada. Bienvenido a Wheels, " + nombre + "!",
                "Wheels", JOptionPane.INFORMATION_MESSAGE);

        irA(new VentanaHome());
    }
}
