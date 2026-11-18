package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Vehiculo;

/** PANTALLA 10 - Register Vehicle. */
public class VentanaRegistrarVehiculo extends VentanaBase {

    private JTextField txtMarca, txtModelo, txtColor, txtPlaca;
    private JComboBox<String> cmbPuestos, cmbAnio;

    public VentanaRegistrarVehiculo() {
        super("Registrar vehiculo");

        JButton volver = Estilo.enlace("< Volver", 20, 16, 80);
        volver.addActionListener(e -> volver());
        lienzo.add(volver);

        lienzo.add(Estilo.titulo("Registrar vehiculo", 20, 48));
        lienzo.add(Estilo.texto("Necesario una sola vez para poder publicar viajes.",
                20, 78, 340, Estilo.PEQUENA, Estilo.GRIS));

        lienzo.add(Estilo.etiqueta("Marca", 20, 116));
        txtMarca = Estilo.campo(20, 136, 340);
        lienzo.add(txtMarca);

        lienzo.add(Estilo.etiqueta("Modelo", 20, 182));
        txtModelo = Estilo.campo(20, 202, 340);
        lienzo.add(txtModelo);

        lienzo.add(Estilo.etiqueta("Color", 20, 248));
        txtColor = Estilo.campo(20, 268, 340);
        lienzo.add(txtColor);

        lienzo.add(Estilo.etiqueta("Placa", 20, 314));
        txtPlaca = Estilo.campo(20, 334, 340);
        lienzo.add(txtPlaca);

        lienzo.add(Estilo.etiqueta("Puestos", 20, 380));
        cmbPuestos = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6"});
        cmbPuestos.setBounds(20, 400, 165, 34);
        cmbPuestos.setFont(Estilo.NORMAL);
        cmbPuestos.setBackground(Estilo.BLANCO);
        cmbPuestos.setSelectedIndex(3);
        lienzo.add(cmbPuestos);

        lienzo.add(Estilo.etiqueta("Modelo (ano)", 195, 380));
        String[] anios = new String[20];
        int actualAnio = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int i = 0; i < 20; i++) anios[i] = String.valueOf(actualAnio + 1 - i);
        cmbAnio = new JComboBox<>(anios);
        cmbAnio.setBounds(195, 400, 165, 34);
        cmbAnio.setFont(Estilo.NORMAL);
        cmbAnio.setBackground(Estilo.BLANCO);
        cmbAnio.setSelectedIndex(3);
        lienzo.add(cmbAnio);

        // Si ya tiene vehiculo, se muestran sus datos para poder editarlos
        Vehiculo actual = BaseDatos.usuarioActual.getVehiculo();
        if (actual != null) {
            txtMarca.setText(actual.getMarca());
            txtModelo.setText(actual.getModelo());
            txtColor.setText(actual.getColor());
            txtPlaca.setText(actual.getPlaca());
            cmbPuestos.setSelectedItem(String.valueOf(actual.getPuestos()));
            cmbAnio.setSelectedItem(String.valueOf(actual.getAnio()));
        }

        JButton guardar = Estilo.botonPrimario("Save Vehicle", 20, 470, 340);
        guardar.addActionListener(e -> guardar());
        lienzo.add(guardar);

        JButton cancelar = Estilo.botonBlanco("Cancelar", 20, 522, 340);
        cancelar.addActionListener(e -> volver());
        lienzo.add(cancelar);
    }

    private void guardar() {
        String marca  = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color  = txtColor.getText().trim();
        String placa  = txtPlaca.getText().trim().toUpperCase();

        if (marca.isEmpty() || modelo.isEmpty() || color.isEmpty() || placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes llenar todos los campos.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (placa.length() < 5) {
            JOptionPane.showMessageDialog(this, "La placa no parece valida.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int puestos = Integer.parseInt(cmbPuestos.getSelectedItem().toString());
        int anio = Integer.parseInt(cmbAnio.getSelectedItem().toString());

        if (!BaseDatos.guardarVehiculo(marca, modelo, color, placa, anio, puestos)) {
            return;
        }

        JOptionPane.showMessageDialog(this, "Vehiculo guardado correctamente.",
                "Wheels", JOptionPane.INFORMATION_MESSAGE);

        volver();
    }
}
