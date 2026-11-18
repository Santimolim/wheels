package wheels.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private final String DRIVER   = "com.mysql.cj.jdbc.Driver";
    private final String HOST     = "localhost";
    private final String PUERTO   = "3307";
    private final String DB       = "wheels";
    private final String USER     = "root";
    private final String PASSWORD = "";

    private final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + DB
            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

    private Connection cadena;
    private static Conexion instancia;

    private Conexion() {
        this.cadena = null;
    }

    public static Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection conectar() {
        try {
            // isValid comprueba que la conexion siga viva (por ejemplo si MySQL se reinicio en XAMPP)
            if (cadena == null || cadena.isClosed() || !cadena.isValid(2)) {
                Class.forName(DRIVER);
                cadena = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexion exitosa a la base de datos " + DB);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("No se encontro el driver de MySQL.");
            System.out.println("Agrega el JAR mysql-connector-j al proyecto.");
            cadena = null;
        } catch (SQLException e) {
            System.out.println("No se pudo conectar a MySQL en el puerto " + PUERTO);
            System.out.println("Detalle: " + e.getMessage());
            cadena = null;
        }
        return cadena;
    }

    public void desconectar() {
        try {
            if (cadena != null && !cadena.isClosed()) {
                cadena.close();
                cadena = null;
                System.out.println("Conexion cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    public boolean estaConectado() {
        try {
            return cadena != null && !cadena.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getCadena() {
        return cadena;
    }
}