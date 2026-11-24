package wheels.vistas;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;

/** PANTALLA 3 - Crear cuenta. Guarda el usuario en la tabla users (contrasena cifrada). */
public class VentanaRegistro extends VentanaBase {

    private final JTextField txtNombre, txtEmail, txtTelefono;
    private final JPasswordField txtPassword, txtConfirmar;
    private final JComboBox<String> cmbUniversidad, cmbCarrera, cmbSemestre;
    private final Estilo.Campo cNombre, cEmail, cClave, cConfirmar, cTelefono, cUniversidad, cCarrera;
    private final ArrayList<String[]> universidades;
    private final Estilo.Aviso aviso;
    private final Estilo.Boton crear;

    public VentanaRegistro() {
        super("Crear cuenta");

        Diseno.Columna f = new Diseno.Columna(480, 28, 36).centrada();
        PanelAcceso panel = new PanelAcceso(f);

        f.agregar(PanelAcceso.botonVolver());
        f.espacio(12);
        JPanel logo = PanelAcceso.filaIzquierda(new Logo.Marca(34, Estilo.NAVY, true));
        f.agregar(logo);
        VentanaLogin.JComponentEspacio sep = new VentanaLogin.JComponentEspacio(28);
        f.agregar(sep);
        panel.soloEnAngosto(logo);
        panel.soloEnAngosto(sep);

        f.agregar(Estilo.texto("Crea tu cuenta", Estilo.h1(), Estilo.TEXTO));
        f.espacio(6);
        f.agregar(new Estilo.Parrafo("Únete con tu correo institucional. Solo toma un minuto.", Estilo.cuerpo(), Estilo.GRIS));
        f.espacio(22);

        aviso = new Estilo.Aviso();
        f.agregar(aviso);
        f.espacio(4);

        txtNombre = Estilo.campo("Nombre y apellido", Iconos.Tipo.PERFIL);
        cNombre = new Estilo.Campo("Nombre completo", txtNombre, null);
        f.agregar(cNombre);

        txtEmail = Estilo.campo("nombre@universidad.edu.co", Iconos.Tipo.CORREO);
        cEmail = new Estilo.Campo("Correo institucional", txtEmail, "Debe terminar en .edu o .edu.co");
        f.agregar(cEmail);

        Diseno.Rejilla claves = new Diseno.Rejilla(190, 2, 14, 0);
        txtPassword = Estilo.campoClave("Mínimo 8 caracteres");
        cClave = new Estilo.Campo("Contraseña", txtPassword, "Usa letras y números");
        txtConfirmar = Estilo.campoClave("Repite la contraseña");
        cConfirmar = new Estilo.Campo("Confirmar contraseña", txtConfirmar, null);
        claves.add(cClave);
        claves.add(cConfirmar);
        f.agregar(claves);

        Diseno.Rejilla datos = new Diseno.Rejilla(190, 2, 14, 0);
        txtTelefono = Estilo.campo("3001234567", Iconos.Tipo.TELEFONO);
        cTelefono = new Estilo.Campo("Celular", txtTelefono, "Lo verá tu conductor o pasajero");
        String[] sem = new String[12];
        for (int i = 0; i < sem.length; i++) sem[i] = (i + 1) + "° semestre";
        cmbSemestre = Estilo.combo(sem);
        datos.add(cTelefono);
        datos.add(new Estilo.Campo("Semestre", cmbSemestre, null));
        f.agregar(datos);

        universidades = BaseDatos.universidades();
        String[] nombres = new String[universidades.size()];
        for (int i = 0; i < nombres.length; i++) nombres[i] = universidades.get(i)[1];
        cmbUniversidad = Estilo.combo(nombres);
        cUniversidad = new Estilo.Campo("Universidad", cmbUniversidad, null);
        f.agregar(cUniversidad);

        cmbCarrera = Estilo.combo(BaseDatos.CARRERAS);
        cmbCarrera.setEditable(true);
        cmbCarrera.setSelectedItem("");
        cCarrera = new Estilo.Campo("Carrera", cmbCarrera, "Elige de la lista o escríbela");
        f.agregar(cCarrera);
        f.espacio(14);

        crear = Estilo.boton("Crear cuenta", Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, null);
        crear.addActionListener(e -> registrar());
        f.agregar(crear);
        f.espacio(18);

        JPanel ya = Estilo.panel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 4, 0));
        ya.add(Estilo.texto("¿Ya tienes cuenta?", Estilo.pequena(), Estilo.GRIS));
        JButton link = Estilo.enlace("Inicia sesión");
        link.addActionListener(e -> irA(VentanaLogin::new));
        ya.add(link);
        f.agregar(ya);

        add(panel, BorderLayout.CENTER);
    }

    @Override public void alMostrar() { txtNombre.requestFocusInWindow(); }

    private void registrar() {
        aviso.ocultar();
        for (Estilo.Campo c : new Estilo.Campo[]{cNombre, cEmail, cClave, cConfirmar, cTelefono, cUniversidad, cCarrera}) {
            c.setError(null);
        }
        final String nombre = txtNombre.getText().trim().replaceAll("\\s+", " ");
        final String email  = txtEmail.getText().trim().toLowerCase();
        final String clave  = new String(txtPassword.getPassword());
        final String clave2 = new String(txtConfirmar.getPassword());
        final String tel    = txtTelefono.getText().trim().replaceAll("[\\s-]", "");
        final String carrera = cmbCarrera.getEditor().getItem() == null ? "" : cmbCarrera.getEditor().getItem().toString().trim();

        boolean ok = true;
        if (nombre.length() < 3 || !nombre.contains(" ")) { cNombre.setError("Escribe tu nombre y apellido."); ok = false; }
        if (!email.matches(VentanaLogin.REGEX_CORREO)) { cEmail.setError("Usa un correo institucional válido (.edu o .edu.co)."); ok = false; }
        if (clave.length() < 8 || !clave.matches(".*[A-Za-z].*") || !clave.matches(".*\\d.*")) {
            cClave.setError("Mínimo 8 caracteres, con letras y números."); ok = false;
        }
        if (!clave.equals(clave2)) { cConfirmar.setError("Las contraseñas no coinciden."); ok = false; }
        if (!tel.matches("3\\d{9}") && !tel.matches("\\d{7,10}")) { cTelefono.setError("Número no válido (ej. 3001234567)."); ok = false; }
        if (cmbUniversidad.getSelectedIndex() < 0) { cUniversidad.setError("No hay universidades en la base de datos."); ok = false; }
        if (carrera.isEmpty()) { cCarrera.setError("Indica tu carrera."); ok = false; }
        if (carrera.length() > 150 || nombre.length() > 100) { aviso.mostrar("Algún dato es demasiado largo.", "error"); ok = false; }
        if (!ok) {
            revalidate();
            repaint();
            return;
        }

        final int idUni = Integer.parseInt(universidades.get(cmbUniversidad.getSelectedIndex())[0]);
        final int semestre = cmbSemestre.getSelectedIndex() + 1;
        crear.setEnabled(false);
        crear.setText("Creando cuenta…");
        new SwingWorker<Object, Void>() {
            @Override protected Object doInBackground() {
                if (BaseDatos.existeEmail(email)) return "existe";
                return BaseDatos.crearUsuario(nombre, email, clave, idUni, carrera, semestre, tel);
            }
            @Override protected void done() {
                crear.setEnabled(true);
                crear.setText("Crear cuenta");
                Object r;
                try { r = get(); } catch (Exception ex) { r = null; }
                if ("existe".equals(r)) {
                    cEmail.setError("Ese correo ya tiene una cuenta. Inicia sesión.");
                    revalidate();
                    return;
                }
                if (!(r instanceof Usuario)) {
                    aviso.mostrar("No se pudo crear la cuenta. Inténtalo de nuevo.", "error");
                    revalidate();
                    return;
                }
                BaseDatos.usuarioActual = (Usuario) r;
                irRaiz(VentanaHome::new);
                toast("Cuenta creada. ¡Bienvenido a Wheels, " + ((Usuario) r).getPrimerNombre() + "!", Dialogos.Tono.EXITO);
            }
        }.execute();
    }
}
