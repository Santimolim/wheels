package wheels.vistas;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import wheels.datos.BaseDatos;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;

/** PANTALLA 13 - Profile. */
public class VentanaPerfil extends VentanaBase {

    public VentanaPerfil() {
        super("Perfil");

        Usuario u = BaseDatos.usuarioActual;

        // ---------- Encabezado con foto ----------
        JPanel cabeza = new Estilo.PanelDegradado();
        cabeza.setBounds(0, 0, Estilo.ANCHO, 180);

        cabeza.add(Estilo.avatar(u.getIniciales(), (Estilo.ANCHO - 76) / 2, 22, 76));

        JLabel nombre = new JLabel(u.getNombre(), SwingConstants.CENTER);
        nombre.setBounds(0, 104, Estilo.ANCHO, 24);
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 17));
        nombre.setForeground(Estilo.BLANCO);
        cabeza.add(nombre);

        JLabel datos = new JLabel(u.getUniversidad() + "  -  " + u.getCarrera(), SwingConstants.CENTER);
        datos.setBounds(0, 128, Estilo.ANCHO, 18);
        datos.setFont(Estilo.PEQUENA);
        datos.setForeground(new java.awt.Color(170, 190, 216));
        cabeza.add(datos);

        cabeza.add(Estilo.pastilla(Estilo.estrellas(u.getCalificacion()) + " calificacion",
                (Estilo.ANCHO - 120) / 2, 150, 120, Estilo.TEAL, Estilo.NAVY));
        lienzo.add(cabeza);

        // ---------- Informacion basica ----------
        JPanel info = Estilo.tarjeta(20, 190, 340, 172);
        int y = 12;
        y = fila(info, "Correo", u.getEmail(), y);
        y = fila(info, "Celular", u.getTelefono().isEmpty() ? "-" : u.getTelefono(), y);
        y = fila(info, "Semestre", String.valueOf(u.getSemestre()), y);
        y = fila(info, "Viajes solicitados", String.valueOf(BaseDatos.contarSolicitudes()), y);
        y = fila(info, "Viajes publicados", String.valueOf(BaseDatos.contarViajesPublicados()), y);
        lienzo.add(info);

        // ---------- Vehiculo ----------
        Vehiculo auto = u.getVehiculo();
        JPanel pAuto = Estilo.tarjeta(20, 370, 340, 72);
        pAuto.add(Estilo.texto("Mi vehiculo", 14, 10, 200, Estilo.SUBTITULO, Estilo.TEXTO));
        pAuto.add(Estilo.texto(auto == null ? "Aun no has registrado un vehiculo" : auto.toString(),
                14, 30, 190, Estilo.PEQUENA, Estilo.GRIS));

        JButton irVehiculo = Estilo.botonPequeno(
                auto == null ? "Registrar vehiculo" : "Editar vehiculo", 200, 22, 126);
        irVehiculo.addActionListener(e -> irA(new VentanaRegistrarVehiculo()));
        pAuto.add(irVehiculo);
        lienzo.add(pAuto);

        // ---------- Acciones ----------
        JButton notif = Estilo.botonBlanco("Notificaciones (" + BaseDatos.notificacionesSinLeer() + " nuevas)", 20, 452, 340);
        notif.addActionListener(e -> irA(new VentanaNotificaciones()));
        lienzo.add(notif);

        JButton editar = Estilo.botonOscuro("Editar perfil", 20, 498, 340);
        editar.addActionListener(e -> editarPerfil());
        lienzo.add(editar);

        JButton salir = Estilo.botonBlanco("Cerrar sesion", 20, 544, 340);
        salir.setForeground(Estilo.ROJO);
        salir.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Seguro que quieres cerrar sesion?",
                    "Wheels", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                BaseDatos.cerrarSesion();
                irA(new VentanaLogin());
            }
        });
        lienzo.add(salir);

        agregarBarraInferior("Profile");
    }

    private int fila(JPanel panel, String etiqueta, String valor, int y) {
        panel.add(Estilo.texto(etiqueta, 14, y, 140, Estilo.PEQUENA, Estilo.GRIS));
        JLabel v = Estilo.texto(valor, 120, y - 2, 206, Estilo.NORMAL, Estilo.TEXTO);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(v);
        panel.add(Estilo.separador(14, y + 22, 312));
        return y + 31;
    }

    /** Edicion del perfil: se guarda en la tabla users. */
    private void editarPerfil() {
        Usuario u = BaseDatos.usuarioActual;

        JTextField nom = new JTextField(u.getNombre());
        JTextField tel = new JTextField(u.getTelefono());
        javax.swing.JComboBox<String> car = new javax.swing.JComboBox<>(BaseDatos.CARRERAS);
        car.setEditable(true);
        car.setSelectedItem(u.getCarrera());
        Object[] campos = {"Nombre completo:", nom, "Celular:", tel, "Carrera:", car};

        int r = JOptionPane.showConfirmDialog(this, campos, "Editar perfil",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String n = nom.getText().trim();
        String c = car.getSelectedItem() == null ? "" : car.getSelectedItem().toString().trim();
        if (n.isEmpty() || c.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y carrera son obligatorios.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (BaseDatos.actualizarPerfil(u, n, c, tel.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Perfil actualizado.",
                    "Wheels", JOptionPane.INFORMATION_MESSAGE);
            irA(new VentanaPerfil());   // recarga la pantalla con los datos nuevos
        }
    }
}
