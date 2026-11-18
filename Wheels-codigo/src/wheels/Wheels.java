package wheels;

import javax.swing.SwingUtilities;
import wheels.datos.BaseDatos;
import wheels.vistas.VentanaBienvenida;

/**
 * Clase principal del proyecto Wheels.
 * Aqui empieza la aplicacion: carga los datos de prueba
 * y muestra la pantalla de bienvenida.
 */
public class Wheels {

    public static void main(String[] args) {

        // Hace que la aplicacion se vea con el estilo del sistema operativo
        try {
            javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo aplicar el estilo del sistema.");
        }

        BaseDatos.cargarDatosDePrueba();

        SwingUtilities.invokeLater(() -> new VentanaBienvenida().setVisible(true));
    }
}
