package wheels.vistas;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import wheels.datos.BaseDatos;
import wheels.modelo.Vehiculo;

/** PANTALLA 11 - Publish Trip. */
public class VentanaPublicarViaje extends VentanaBase {

    private JTextField txtOrigen, txtDestino, txtFecha, txtHora, txtPrecio;
    private JComboBox<String> cmbPuestos;

    public VentanaPublicarViaje() {
        super("Publicar viaje");

        Vehiculo auto = BaseDatos.usuarioActual.getVehiculo();

        lienzo.add(Estilo.encabezado("Publicar viaje", "Ofrece tus puestos libres"));

        lienzo.add(Estilo.etiqueta("Origen", 20, 92));
        txtOrigen = Estilo.campo(20, 110, 340);
        lienzo.add(txtOrigen);

        lienzo.add(Estilo.etiqueta("Destino", 20, 152));
        txtDestino = Estilo.campo(20, 170, 340);
        lienzo.add(txtDestino);

        lienzo.add(Estilo.etiqueta("Fecha (dd/mm/aaaa)", 20, 212));
        txtFecha = Estilo.campo(20, 230, 165);
        txtFecha.setText("14/09/2026");
        lienzo.add(txtFecha);

        lienzo.add(Estilo.etiqueta("Hora", 195, 212));
        txtHora = Estilo.campo(195, 230, 165);
        txtHora.setText("07:30 AM");
        lienzo.add(txtHora);

        lienzo.add(Estilo.etiqueta("Puestos", 20, 272));
        cmbPuestos = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6"});
        cmbPuestos.setBounds(20, 290, 165, 34);
        cmbPuestos.setFont(Estilo.NORMAL);
        cmbPuestos.setBackground(Estilo.BLANCO);
        cmbPuestos.setSelectedIndex(2);
        lienzo.add(cmbPuestos);

        lienzo.add(Estilo.etiqueta("Precio por puesto", 195, 272));
        txtPrecio = Estilo.campo(195, 290, 165);
        txtPrecio.setText("6000");
        lienzo.add(txtPrecio);

        lienzo.add(Estilo.etiqueta("Vehiculo", 20, 332));
        JTextField vehiculo = Estilo.campo(20, 350, 340);
        vehiculo.setText(auto.toString());
        vehiculo.setEditable(false);
        vehiculo.setBackground(Estilo.LINEA);
        lienzo.add(vehiculo);

        JButton publicar = Estilo.botonPrimario("Publish Trip", 20, 420, 340);
        publicar.addActionListener(e -> publicar());
        lienzo.add(publicar);

        JButton cambiar = Estilo.botonBlanco("Editar mi vehiculo", 20, 472, 340);
        cambiar.addActionListener(e -> irA(new VentanaRegistrarVehiculo()));
        lienzo.add(cambiar);

        agregarBarraInferior("Publish");
    }

    private void publicar() {
        String origen  = txtOrigen.getText().trim();
        String destino = txtDestino.getText().trim();
        String fecha   = txtFecha.getText().trim();
        String hora    = txtHora.getText().trim();
        String precioT = txtPrecio.getText().trim();

        if (origen.isEmpty() || destino.isEmpty() || fecha.isEmpty()
                || hora.isEmpty() || precioT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes llenar todos los campos.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origen.equalsIgnoreCase(destino)) {
            JOptionPane.showMessageDialog(this, "El origen y el destino no pueden ser iguales.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioT);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un numero. Ejemplo: 6000",
                    "Wheels", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precio < 0) {
            JOptionPane.showMessageDialog(this, "El precio no puede ser negativo.",
                    "Wheels", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int puestos = Integer.parseInt(cmbPuestos.getSelectedItem().toString());

        BaseDatos.viajeSeleccionado = BaseDatos.crearViaje(
                BaseDatos.usuarioActual, origen, destino, fecha, hora, puestos, precio);

        irA(new VentanaViajePublicado());
    }
}
