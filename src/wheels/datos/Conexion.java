package wheels.datos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexion unica (Singleton) a la base de datos MySQL "wheels".
 *
 * La configuracion NO esta fija en el codigo. Se toma, en este orden:
 *   1. Propiedades del sistema:  -Dwheels.db.port=3306
 *   2. Variables de entorno:     WHEELS_DB_PORT=3306
 *   3. Archivo wheels.properties (carpeta de trabajo, carpeta del .jar o ~/.wheels/)
 *   4. Valores por defecto:      localhost, puerto 3306 (o 3307 si falla), base wheels, root sin contrasena
 *
 * Claves disponibles: db.host, db.port, db.name, db.user, db.password
 */
public class Conexion {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private final String host;
    private String puerto;
    private final String[] puertos;   // puertos a probar, en orden
    private final String db;
    private final String usuario;
    private final String clave;

    private Connection cadena;
    private String ultimoError = "";
    private static Conexion instancia;

    private Conexion() {
        Properties archivo = leerArchivo();
        host    = valor("db.host", "localhost", archivo);
        db      = valor("db.name", "wheels", archivo);
        usuario = valor("db.user", "root", archivo);
        clave   = valor("db.password", "", archivo);
        // Si no se configuro un puerto, se prueba el de XAMPP por defecto (3306) y luego 3307
        String fijo = valor("db.port", null, archivo);
        puertos = fijo != null ? new String[]{fijo} : new String[]{"3306", "3307"};
        puerto = puertos[0];
    }

    private String url(String p) {
        return "jdbc:mysql://" + host + ":" + p + "/" + db
                + "?useSSL=false&serverTimezone=America/Bogota&allowPublicKeyRetrieval=true"
                + "&characterEncoding=UTF-8&connectTimeout=3000";
    }

    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /** Devuelve una conexion viva o null si MySQL no responde (ver getUltimoError). */
    public synchronized Connection conectar() {
        try {
            // isValid comprueba que la conexion siga viva (por ejemplo si MySQL se reinicio en XAMPP)
            if (cadena == null || cadena.isClosed() || !cadena.isValid(2)) {
                Class.forName(DRIVER);
                cadena = null;
                // primero el puerto que funciono la ultima vez, luego los demas
                java.util.LinkedHashSet<String> orden = new java.util.LinkedHashSet<>();
                orden.add(puerto);
                orden.addAll(java.util.Arrays.asList(puertos));
                for (String p : orden) {
                    try {
                        cadena = DriverManager.getConnection(url(p), usuario, clave);
                        puerto = p;
                        ultimoError = "";
                        break;
                    } catch (SQLException e) {
                        ultimoError = e.getMessage();
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            ultimoError = "No se encontró el driver de MySQL (mysql-connector-java en lib/).";
            cadena = null;
        } catch (SQLException e) {
            ultimoError = e.getMessage();
            cadena = null;
        }
        return cadena;
    }

    public synchronized void desconectar() {
        try {
            if (cadena != null && !cadena.isClosed()) {
                cadena.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
        cadena = null;
    }

    public String getUltimoError() { return ultimoError; }

    /** Texto corto para mostrar en pantalla: "localhost:3307 / wheels". */
    public String getDescripcion() { return host + ":" + puerto + " / " + db; }

    public String getPuerto() { return puerto; }

    // ------------------------------------------------------------------
    //  Lectura de la configuracion
    // ------------------------------------------------------------------

    private static String valor(String clave, String porDefecto, Properties archivo) {
        String v = System.getProperty("wheels." + clave);
        if (v == null) v = System.getenv("WHEELS_" + clave.replace('.', '_').toUpperCase());
        if (v == null) v = archivo.getProperty(clave);
        return v == null ? porDefecto : v.trim();
    }

    private static Properties leerArchivo() {
        Properties p = new Properties();
        for (File f : candidatos()) {
            if (f != null && f.isFile()) {
                try (InputStream in = new FileInputStream(f)) {
                    p.load(in);
                    return p;
                } catch (Exception e) {
                    System.err.println("No se pudo leer " + f + ": " + e.getMessage());
                }
            }
        }
        return p;
    }

    private static File[] candidatos() {
        File jarDir = null;
        try {
            File origen = new File(Conexion.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            jarDir = origen.isFile() ? origen.getParentFile() : origen;
        } catch (Exception ignorado) {
            // sin ubicacion del codigo: se usan las otras rutas
        }
        return new File[]{
            new File("wheels.properties"),
            jarDir == null ? null : new File(jarDir, "wheels.properties"),
            jarDir == null || jarDir.getParentFile() == null ? null : new File(jarDir.getParentFile(), "wheels.properties"),
            new File(System.getProperty("user.home"), ".wheels" + File.separator + "wheels.properties")
        };
    }
}
