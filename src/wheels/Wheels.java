package wheels;

import javax.swing.SwingUtilities;
import wheels.datos.BaseDatos;
import wheels.datos.Conexion;
import wheels.vistas.Aplicacion;
import wheels.vistas.Dialogos;
import wheels.vistas.Estilo;
import wheels.vistas.VentanaBienvenida;

/**
 * Clase principal del proyecto Wheels.
 * Aplica el estilo visual, verifica la conexion con MySQL (base "wheels")
 * y abre la ventana con la pantalla de bienvenida.
 */
public class Wheels {

    public static final String VERSION = "2.0";

    public static void main(String[] args) {
        // Texto nitido y bordes de ventana integrados con el tema
        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        System.setProperty("flatlaf.menuBarEmbedded", "true");

        SwingUtilities.invokeLater(() -> {
            Estilo.instalar();
            if (!conectar()) {
                System.exit(1);
                return;
            }
            Aplicacion.iniciar(VentanaBienvenida::new);
        });
    }

    /** Intenta conectar; si falla explica como solucionarlo y permite reintentar. */
    private static boolean conectar() {
        while (!BaseDatos.probarConexion()) {
            Conexion cx = Conexion.getInstancia();
            int r = new Dialogos.Constructor(null, "No pudimos conectar con la base de datos")
                    .tono(Dialogos.Tono.PELIGRO)
                    .mensaje("Wheels necesita MySQL en " + cx.getDescripcion() + ".\n\n"
                            + "1. Abre XAMPP e inicia MySQL.\n"
                            + "2. Importa wheels.sql en phpMyAdmin si la base 'wheels' no existe.\n"
                            + "3. Si tu MySQL usa otro puerto, créalo en wheels.properties (db.port=3306).\n\n"
                            + "Detalle: " + cx.getUltimoError())
                    .ancho(520)
                    .boton("Salir", Estilo.TipoBoton.SECUNDARIO, null)
                    .botonPrincipal("Reintentar", Estilo.TipoBoton.PRIMARIO, null)
                    .mostrar();
            if (r != 1) return false;
        }
        return true;
    }
}
