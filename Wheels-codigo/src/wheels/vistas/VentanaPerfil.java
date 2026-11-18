package wheels.vistas;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
        JPanel cabeza = new JPanel(null);
        cabeza.setBounds(0, 0, Estilo.ANCHO, 190);
        cabeza.setBackground(Estilo.NAVY);

        JLabel foto = Estilo.avatar(u.getIniciales(), (Estilo.ANCHO - 76) / 2, 30, 76);
        cabeza.add(foto);

        JLabel nombre = new JLabel(u.getNombre(), SwingConstants.CENTER);
        nombre.setBounds(0, 118, Estilo.ANCHO, 24);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 17));
        nombre.setForeground(Estilo.BLANCO);
        cabeza.add(nombre);

        JLabel datos = new JLabel(u.getUniversidad() + "  -  " + u.getCarrera(), SwingConstants.CENTER);
        datos.setBounds(0, 144, Estilo.ANCHO, 18);
        datos.setFont(Estilo.PEQUENA);
        datos.setForeground(new java.awt.Color(159, 178, 204));
        cabeza.add(datos);

        lienzo.add(cabeza);

        // ---------- Informacion basica ----------
        JPanel info = Estilo.tarjeta(20, 205, 340, 120);
        int y = 12;
        y = fila(info, "Correo", u.getEmail(), y);
        y = fila(info, "Universidad", u.getUniversidad(), y);
        y = fila(info, "Viajes solicitados", String.valueOf(cuentaSolicitudes()), y);
        lienzo.add(info);

        // ---------- Vehiculo ----------
        Vehiculo auto = u.getVehiculo();
        JPanel pAuto = Estilo.tarjeta(20, 340, 340, 72);
        pAuto.add(Estilo.texto("Mi vehiculo", 14, 10, 200, Estilo.SUBTITULO, Estilo.TEXTO));
        pAuto.add(Estilo.texto(auto == null ? "Aun no has registrado un vehiculo" : auto.toString(),
                14, 30, 310, Estilo.PEQUENA, Estilo.GRIS));

        JButton irVehiculo = Estilo.botonPequeno(
                auto == null ? "Registrar vehiculo" : "Editar vehiculo", 200, 38, 126);
        irVehiculo.addActionListener(e -> irA(new VentanaRegistrarVehiculo()));
        pAuto.add(irVehiculo);
        lienzo.add(pAuto);

        // ---------- Acciones ----------
        JButton editar = Estilo.botonOscuro("Editar perfil", 20, 430, 340);
        editar.addActionListener(e -> editarPerfil());
        lienzo.add(editar);

        JButton salir = Estilo.botonBlanco("Cerrar sesion", 20, 478, 340);
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
        return y + 32;
    }

    private int cuentaSolicitudes() {
        int total = 0;
        for (wheels.modelo.Solicitud s : BaseDatos.solicitudes) {
            if (s.getPasajero().getId() == BaseDatos.usuarioActual.getId()) {
                total++;
            }
        }
        return total;
    }

    /** Edicion sencilla del perfil usando ventanas de dialogo. */
    private void editarPerfil() {
        Usuario u = BaseDatos.usuarioActual;

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nombre completo:", u.getNombre());
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            return;
        }

        String nuevaCarrera = (String) JOptionPane.showInputDialog(this, "Carrera:", "Wheels",
                JOptionPane.QUESTION_MESSAGE, null, BaseDatos.CARRERAS, u.getCarrera());
        if (nuevaCarrera == null) {
            return;
        }

        u.setNombre(nuevoNombre.trim());
        u.setCarrera(nuevaCarrera);

        JOptionPane.showMessageDialog(this, "Perfil actualizado.",
                "Wheels", JOptionPane.INFORMATION_MESSAGE);

        irA(new VentanaPerfil());   // recarga la pantalla con los datos nuevos
    }
}
