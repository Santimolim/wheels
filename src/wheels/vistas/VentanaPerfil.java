package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.datos.Conexion;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;

/** PANTALLA 13 - Perfil: datos personales, estadisticas, vehiculo, seguridad y cierre de sesion. */
public class VentanaPerfil extends VentanaBase {

    public VentanaPerfil() {
        super("Mi perfil");
        Usuario u = BaseDatos.usuarioActual;
        Diseno.Columna c = crearCuerpo(880);

        // ---------- Cabecera ----------
        Cabecera cab = new Cabecera();
        JPanel av = Estilo.panel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        av.add(new Estilo.Avatar(u.getIniciales(), u.getEmail(), 84).conAnillo());
        cab.columna.agregar(av);
        cab.columna.espacio(12);
        Estilo.Parrafo nombre = new Estilo.Parrafo(u.getNombre(), Estilo.fuente(22, Estilo.NEGRITA), Estilo.BLANCO);
        nombre.centrar();
        cab.columna.agregar(nombre);
        cab.columna.espacio(4);
        Estilo.Parrafo carrera = new Estilo.Parrafo(u.getCarrera() + " · " + u.getSemestre() + "° semestre",
                Estilo.cuerpo(), new Color(0xC7D2FE));
        carrera.centrar();
        cab.columna.agregar(carrera);
        cab.columna.espacio(4);
        JLabel uni = Estilo.texto(u.getUniversidad(), Estilo.pequena(), new Color(0xA5B4CF));
        uni.setIcon(Iconos.de(Iconos.Tipo.UNIVERSIDAD, 15, new Color(0xA5B4CF)));
        uni.setIconTextGap(6);
        uni.setHorizontalAlignment(JLabel.CENTER);
        cab.columna.agregar(uni);
        cab.columna.espacio(20);
        Diseno.Rejilla stats = new Diseno.Rejilla(90, 3, 8, 8);
        stats.add(estadistica("★ " + u.getCalificacion(), "Calificación"));
        stats.add(estadistica(String.valueOf(BaseDatos.contarSolicitudes()), "Realizados"));
        stats.add(estadistica(String.valueOf(BaseDatos.contarViajesPublicados()), "Publicados"));
        cab.columna.agregar(stats);
        c.agregar(cab);
        c.espacio(20);

        Diseno.Rejilla dos = new Diseno.Rejilla(320, 2, 20, 20).igualarAlto(false);

        // ---------- Informacion personal ----------
        Estilo.Tarjeta info = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna i = info.columna();
        JButton editar = Estilo.boton("Editar", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.EDITAR);
        editar.addActionListener(e -> editarPerfil());
        i.agregar(Estilo.tituloSeccion("Información personal", editar));
        i.agregar(Estilo.dato(Iconos.Tipo.CORREO, u.getEmail()));
        i.agregar(Estilo.dato(Iconos.Tipo.TELEFONO, u.getTelefono().isEmpty() ? "Sin celular registrado" : u.getTelefono()));
        i.agregar(Estilo.dato(Iconos.Tipo.UNIVERSIDAD, u.getUniversidad()));
        i.agregar(Estilo.dato(Iconos.Tipo.PERFIL, u.getCarrera() + " · " + u.getSemestre() + "° semestre"));
        dos.add(info);

        Diseno.Columna der = new Diseno.Columna(100000, 0, 0).conSeparacion(20);

        // ---------- Vehiculo ----------
        Vehiculo auto = u.getVehiculo();
        Estilo.Tarjeta pAuto = Estilo.Tarjeta.conColumna(14);
        Diseno.Columna a = pAuto.columna();
        JButton irVehiculo = Estilo.boton(auto == null ? "Registrar" : "Editar", Estilo.TipoBoton.SECUNDARIO,
                Estilo.Tamano.PEQUENO, auto == null ? Iconos.Tipo.MAS : Iconos.Tipo.EDITAR);
        irVehiculo.addActionListener(e -> irA(() -> new VentanaRegistrarVehiculo(false)));
        a.agregar(Estilo.tituloSeccion("Mi vehículo", irVehiculo));
        if (auto == null) {
            a.agregar(new Estilo.Parrafo("Aún no registras un vehículo. Regístralo para poder publicar viajes como conductor.",
                    Estilo.pequena(), Estilo.GRIS));
        } else {
            JPanel fv = Estilo.panel(new BorderLayout(12, 0));
            fv.add(new Estilo.IconoCirculo(Iconos.Tipo.CARRO, Estilo.HOVER, Estilo.NAVY, 44), BorderLayout.WEST);
            JPanel tv = Estilo.panel(new GridLayout(2, 1));
            tv.add(Estilo.texto(auto.getNombre(), Estilo.cuerpoSemi(), Estilo.TEXTO));
            tv.add(Estilo.texto("Color " + auto.getColor().toLowerCase() + " · " + auto.getPuestos() + " puestos",
                    Estilo.pequena(), Estilo.GRIS));
            fv.add(tv, BorderLayout.CENTER);
            JPanel pl = Estilo.panel(new java.awt.GridBagLayout());
            pl.add(Utilidades.placa(auto.getPlaca()));
            fv.add(pl, BorderLayout.EAST);
            a.agregar(fv);
        }
        der.agregar(pAuto);

        // ---------- Cuenta y seguridad ----------
        Estilo.Tarjeta seg = Estilo.Tarjeta.conColumna(10);
        Diseno.Columna s = seg.columna();
        s.agregar(Estilo.texto("Cuenta y seguridad", Estilo.h3(), Estilo.TEXTO));
        s.espacio(4);
        JButton clave = Estilo.boton("Cambiar contraseña", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.NORMAL, Iconos.Tipo.CANDADO);
        clave.setHorizontalAlignment(JButton.LEFT);
        clave.addActionListener(e -> cambiarClave());
        s.agregar(clave);
        JButton salir = Estilo.boton("Cerrar sesión", Estilo.TipoBoton.PELIGRO_SUAVE, Estilo.Tamano.NORMAL, Iconos.Tipo.SALIR);
        salir.setHorizontalAlignment(JButton.LEFT);
        salir.addActionListener(e -> Aplicacion.cerrarSesion());
        s.agregar(salir);
        der.agregar(seg);
        dos.add(der);
        c.agregar(dos);

        c.espacio(24);
        JLabel version = Estilo.texto("Wheels " + wheels.Wheels.VERSION + " · Base de datos " + Conexion.getInstancia().getDescripcion(),
                Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS_CLARO);
        version.setHorizontalAlignment(JLabel.CENTER);
        c.agregar(version);
    }

    @Override public String seccion() { return PERFIL; }
    @Override public boolean esRaiz() { return true; }

    private JPanel estadistica(String valor, String etiqueta) {
        JPanel p = new JPanel(new BorderLayout(0, 2)) {
            @Override protected void paintComponent(java.awt.Graphics gr) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) gr.create();
                Estilo.suavizar(g2);
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        JLabel v = Estilo.texto(valor, Estilo.fuente(20, Estilo.NEGRITA), Estilo.BLANCO);
        v.setHorizontalAlignment(JLabel.CENTER);
        JLabel e = Estilo.texto(etiqueta, Estilo.fuente(12, Estilo.REGULAR), new Color(0xA5B4CF));
        e.setHorizontalAlignment(JLabel.CENTER);
        p.add(v, BorderLayout.NORTH);
        p.add(e, BorderLayout.SOUTH);
        return p;
    }

    /** Edicion del perfil: se guarda en la tabla users. */
    private void editarPerfil() {
        Usuario u = BaseDatos.usuarioActual;
        JTextField nom = Estilo.campo("Nombre y apellido", Iconos.Tipo.PERFIL);
        nom.setText(u.getNombre());
        JTextField tel = Estilo.campo("3001234567", Iconos.Tipo.TELEFONO);
        tel.setText(u.getTelefono());
        JComboBox<String> car = Estilo.combo(BaseDatos.CARRERAS);
        car.setEditable(true);
        car.setSelectedItem(u.getCarrera());
        String[] sem = new String[12];
        for (int i = 0; i < sem.length; i++) sem[i] = (i + 1) + "° semestre";
        JComboBox<String> cmbSem = Estilo.combo(sem);
        cmbSem.setSelectedIndex(Math.max(0, Math.min(11, u.getSemestre() - 1)));

        Estilo.Campo cNom = new Estilo.Campo("Nombre completo", nom, null);
        Estilo.Campo cTel = new Estilo.Campo("Celular", tel, null);
        Estilo.Campo cCar = new Estilo.Campo("Carrera", car, null);
        Diseno.Columna form = new Diseno.Columna(420, 0, 0);
        form.agregar(cNom);
        form.agregar(cTel);
        form.agregar(cCar);
        form.agregar(new Estilo.Campo("Semestre", cmbSem, null));

        int r = new Dialogos.Constructor(this, "Editar perfil")
                .contenido(form)
                .boton("Cancelar", Estilo.TipoBoton.SECUNDARIO, null)
                .botonPrincipal("Guardar cambios", Estilo.TipoBoton.PRIMARIO, () -> {
                    boolean ok = true;
                    String n = nom.getText().trim();
                    String t = tel.getText().trim().replaceAll("[\\s-]", "");
                    String cr = car.getEditor().getItem() == null ? "" : car.getEditor().getItem().toString().trim();
                    cNom.setError(n.length() < 3 || !n.contains(" ") || n.length() > 100 ? "Escribe tu nombre y apellido." : null);
                    cTel.setError(!t.isEmpty() && !t.matches("\\d{7,10}") ? "Número no válido (ej. 3001234567)." : null);
                    cCar.setError(cr.isEmpty() || cr.length() > 150 ? "Indica tu carrera." : null);
                    if (n.length() < 3 || !n.contains(" ") || n.length() > 100) ok = false;
                    if (!t.isEmpty() && !t.matches("\\d{7,10}")) ok = false;
                    if (cr.isEmpty() || cr.length() > 150) ok = false;
                    form.revalidate();
                    return ok;
                })
                .mostrar();
        if (r != 1) return;

        String carrera = car.getEditor().getItem().toString().trim();
        if (BaseDatos.actualizarPerfil(u, nom.getText().trim().replaceAll("\\s+", " "), carrera,
                cmbSem.getSelectedIndex() + 1, tel.getText().trim().replaceAll("[\\s-]", ""))) {
            toast("Perfil actualizado", Dialogos.Tono.EXITO);
            Aplicacion.recargar();
        }
    }

    private void cambiarClave() {
        JPasswordField actual = Estilo.campoClave("Tu contraseña actual");
        JPasswordField nueva = Estilo.campoClave("Mínimo 8 caracteres");
        JPasswordField repetir = Estilo.campoClave("Repite la nueva contraseña");
        Estilo.Campo cA = new Estilo.Campo("Contraseña actual", actual, null);
        Estilo.Campo cN = new Estilo.Campo("Nueva contraseña", nueva, "Usa letras y números");
        Estilo.Campo cR = new Estilo.Campo("Confirmar nueva contraseña", repetir, null);
        Diseno.Columna form = new Diseno.Columna(420, 0, 0);
        form.agregar(cA);
        form.agregar(cN);
        form.agregar(cR);

        int r = new Dialogos.Constructor(this, "Cambiar contraseña")
                .contenido(form)
                .boton("Cancelar", Estilo.TipoBoton.SECUNDARIO, null)
                .botonPrincipal("Cambiar contraseña", Estilo.TipoBoton.PRIMARIO, () -> {
                    String a = new String(actual.getPassword());
                    String n = new String(nueva.getPassword());
                    String rep = new String(repetir.getPassword());
                    cA.setError(null);
                    cN.setError(null);
                    cR.setError(null);
                    if (a.isEmpty()) { cA.setError("Escribe tu contraseña actual."); return false; }
                    if (n.length() < 8 || !n.matches(".*[A-Za-z].*") || !n.matches(".*\\d.*")) {
                        cN.setError("Mínimo 8 caracteres, con letras y números."); return false;
                    }
                    if (!n.equals(rep)) { cR.setError("Las contraseñas no coinciden."); return false; }
                    if (n.equals(a)) { cN.setError("Debe ser distinta a la actual."); return false; }
                    if (!BaseDatos.cambiarClave(a, n)) { cA.setError("La contraseña actual no es correcta."); return false; }
                    return true;
                })
                .mostrar();
        if (r == 1) toast("Contraseña actualizada", Dialogos.Tono.EXITO);
    }

    /** Cabecera azul con avatar, datos y estadisticas; su alto se ajusta al ancho. */
    private static final class Cabecera extends Estilo.PanelMarca implements Diseno.AltoSegunAncho {
        final Diseno.Columna columna = new Diseno.Columna(100000, 0, 0);
        Cabecera() {
            super(new BorderLayout(), 24);
            setBorder(BorderFactory.createEmptyBorder(26, 18, 18, 18));
            add(columna, BorderLayout.CENTER);
        }
        @Override public int altoPara(int ancho) {
            Insets in = getInsets();
            return in.top + in.bottom + columna.altoPara(ancho - in.left - in.right);
        }
    }
}
