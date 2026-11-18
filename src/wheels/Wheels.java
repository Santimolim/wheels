package wheels;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import wheels.datos.BaseDatos;
import wheels.vistas.VentanaBienvenida;

/**
 * Clase principal del proyecto Wheels.
 * Verifica la conexion con MySQL (base "wheels") y muestra la bienvenida.
 * Todos los datos se leen y guardan en la base de datos.
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

        if (!BaseDatos.probarConexion()) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos 'wheels'.\n\n"
                    + "1. Abre XAMPP e inicia MySQL (puerto 3307).\n"
                    + "2. Verifica que la base 'wheels' exista (importa wheels.sql).\n"
                    + "3. Verifica que el JAR mysql-connector este en Libraries.",
                    "Wheels - Sin conexion", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new VentanaBienvenida().setVisible(true));
    }
}
