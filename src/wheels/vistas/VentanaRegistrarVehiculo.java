package wheels.vistas;

import java.time.Year;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Vehiculo;

/** PANTALLA 10 - Registrar o editar el vehiculo del usuario. */
public class VentanaRegistrarVehiculo extends VentanaBase {

    private final boolean paraPublicar;
    private final JTextField txtMarca, txtModelo, txtColor, txtPlaca;
    private final JComboBox<String> cmbPuestos, cmbAnio;
    private final Estilo.Campo cMarca, cModelo, cColor, cPlaca;
    private final Estilo.Aviso aviso = new Estilo.Aviso();
    private final boolean nuevo;

    /** paraPublicar = true cuando viene del boton Publicar sin tener vehiculo. */
    public VentanaRegistrarVehiculo(boolean paraPublicar) {
        super("Mi vehículo");
        this.paraPublicar = paraPublicar;
        Vehiculo actual = BaseDatos.usuarioActual.getVehiculo();
        nuevo = actual == null;

        encabezado(nuevo ? "Registra tu vehículo" : "Editar vehículo",
                nuevo ? "Solo lo haces una vez para poder publicar viajes" : actual.getNombre() + " · " + actual.getPlaca(),
                !paraPublicar);
        Diseno.Columna c = crearCuerpo(640);

        if (paraPublicar) {
            Estilo.Aviso a = new Estilo.Aviso();
            a.mostrar("Para publicar un viaje primero necesitamos los datos de tu carro. "
                    + "Los pasajeros los verán para reconocerte en el punto de encuentro.", "info");
            c.agregar(a);
            c.espacio(16);
        }

        Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(4);
        t.conRelleno(22, 24);
        Diseno.Columna f = t.columna();
        f.agregar(aviso);

        Diseno.Rejilla r1 = new Diseno.Rejilla(200, 2, 14, 0);
        txtMarca = Estilo.campo("Ej: Mazda", Iconos.Tipo.CARRO);
        txtModelo = Estilo.campo("Ej: 3 Touring", null);
        cMarca = new Estilo.Campo("Marca", txtMarca, null);
        cModelo = new Estilo.Campo("Modelo", txtModelo, null);
        r1.add(cMarca);
        r1.add(cModelo);
        f.agregar(r1);

        Diseno.Rejilla r2 = new Diseno.Rejilla(200, 2, 14, 0);
        txtColor = Estilo.campo("Ej: Gris", null);
        txtPlaca = Estilo.campo("ABC123", null);
        txtPlaca.setFont(Estilo.fuente(14, Estilo.SEMI));
        ((javax.swing.text.AbstractDocument) txtPlaca.getDocument()).setDocumentFilter(new FiltroPlaca());
        cColor = new Estilo.Campo("Color", txtColor, null);
        cPlaca = new Estilo.Campo("Placa", txtPlaca, "3 letras y 3 números (motos: ABC12D)");
        r2.add(cColor);
        r2.add(cPlaca);
        f.agregar(r2);

        Diseno.Rejilla r3 = new Diseno.Rejilla(200, 2, 14, 0);
        String[] anios = new String[30];
        int este = Year.now().getValue();
        for (int i = 0; i < anios.length; i++) anios[i] = String.valueOf(este + 1 - i);
        cmbAnio = Estilo.combo(anios);
        cmbAnio.setSelectedItem(String.valueOf(este - 3));
        cmbPuestos = Estilo.combo(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        cmbPuestos.setSelectedItem("4");
        r3.add(new Estilo.Campo("Año del modelo", cmbAnio, null));
        r3.add(new Estilo.Campo("Puestos para pasajeros", cmbPuestos, "Sin contar el del conductor"));
        f.agregar(r3);
        f.espacio(14);

        if (!nuevo) {
            txtMarca.setText(actual.getMarca());
            txtModelo.setText(actual.getModelo());
            txtColor.setText(actual.getColor());
            txtPlaca.setText(actual.getPlaca());
            cmbPuestos.setSelectedItem(String.valueOf(actual.getPuestos()));
            cmbAnio.setSelectedItem(String.valueOf(actual.getAnio()));
        }

        JButton guardar = Estilo.boton(nuevo ? "Guardar y continuar" : "Guardar cambios",
                Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, Iconos.Tipo.CHECK);
        guardar.addActionListener(e -> guardar());
        f.agregar(guardar);
        c.agregar(t);
    }

    @Override public String seccion() { return paraPublicar ? PUBLICAR : PERFIL; }
    @Override public boolean esRaiz() { return paraPublicar; }
    @Override public void alMostrar() { txtMarca.requestFocusInWindow(); }

    private void guardar() {
        aviso.ocultar();
        for (Estilo.Campo c : new Estilo.Campo[]{cMarca, cModelo, cColor, cPlaca}) c.setError(null);
        String marca  = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color  = txtColor.getText().trim();
        String placa  = txtPlaca.getText().trim().toUpperCase();

        boolean ok = true;
        if (marca.isEmpty() || marca.length() > 50) { cMarca.setError("Escribe la marca."); ok = false; }
        if (modelo.isEmpty() || modelo.length() > 50) { cModelo.setError("Escribe el modelo."); ok = false; }
        if (color.isEmpty() || color.length() > 30) { cColor.setError("Escribe el color."); ok = false; }
        if (!placa.matches("[A-Z]{3}\\d{3}") && !placa.matches("[A-Z]{3}\\d{2}[A-Z]")) {
            cPlaca.setError("Formato no válido. Ej: ABC123 o ABC12D."); ok = false;
        }
        if (!ok) {
            revalidate();
            repaint();
            return;
        }

        String error = BaseDatos.guardarVehiculo(capitalizar(marca), modelo, capitalizar(color), placa,
                Integer.parseInt(cmbAnio.getSelectedItem().toString()),
                Integer.parseInt(cmbPuestos.getSelectedItem().toString()));
        if (error != null) {
            if (error.contains("placa")) cPlaca.setError(error); else aviso.mostrar(error, "error");
            revalidate();
            return;
        }
        toast(nuevo ? "Vehículo registrado" : "Vehículo actualizado", Dialogos.Tono.EXITO);
        if (paraPublicar) irRaiz(VentanaPublicarViaje::new);
        else volver();
    }

    private static String capitalizar(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /** Solo deja escribir letras y numeros en la placa, en mayuscula y maximo 6 caracteres. */
    private static final class FiltroPlaca extends javax.swing.text.DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String s, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            replace(fb, off, 0, s, a);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String s, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            String limpio = s == null ? "" : s.toUpperCase().replaceAll("[^A-Z0-9]", "");
            int cabe = 6 - (fb.getDocument().getLength() - len);
            if (limpio.length() > cabe) limpio = limpio.substring(0, Math.max(0, cabe));
            super.replace(fb, off, len, limpio, a);
        }
    }
}
