package wheels.datos;

import java.util.ArrayList;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/**
 * Guarda toda la informacion de la aplicacion en memoria, usando ArrayList.
 * Todo es "static" para que cualquier ventana pueda leer y escribir los
 * mismos datos sin tener que pasarlos de una ventana a otra.
 */
public class BaseDatos {

    public static ArrayList<Usuario> usuarios = new ArrayList<>();
    public static ArrayList<Viaje> viajes = new ArrayList<>();
    public static ArrayList<Solicitud> solicitudes = new ArrayList<>();

    /** Usuario que tiene la sesion abierta en este momento. */
    public static Usuario usuarioActual = null;

    /** Resultado de la ultima busqueda hecha en la pantalla Home. */
    public static ArrayList<Viaje> resultadosBusqueda = new ArrayList<>();

    /** Viaje que el usuario esta mirando en este momento. */
    public static Viaje viajeSeleccionado = null;

    private static int contadorUsuarios = 0;
    private static int contadorViajes = 0;
    private static int contadorSolicitudes = 0;

    public static final String[] UNIVERSIDADES = {
        "Universidad Nacional", "Universidad de Antioquia",
        "Universidad EAFIT", "Universidad Pontificia Bolivariana"
    };

    public static final String[] CARRERAS = {
        "Ingenieria de Sistemas", "Ingenieria Industrial",
        "Ingenieria Civil", "Administracion de Empresas",
        "Contaduria", "Psicologia"
    };

    // ------------------------------------------------------------------
    //  Datos de ejemplo
    // ------------------------------------------------------------------

    /** Crea usuarios y viajes de prueba. Se llama una sola vez al arrancar. */
    public static void cargarDatosDePrueba() {

        Usuario u1 = crearUsuario("Santiago Molina Mora", "santiago.molina@unal.edu.co",
                "1234", "Universidad Nacional", "Ingenieria de Sistemas");

        Usuario u2 = crearUsuario("Mariana Rios", "mariana.rios@unal.edu.co",
                "1234", "Universidad Nacional", "Ingenieria Industrial");
        u2.setVehiculo(new Vehiculo("Chevrolet", "Onix", "Gris", "KJR 482", 4));

        Usuario u3 = crearUsuario("Andres Castro", "andres.castro@unal.edu.co",
                "1234", "Universidad Nacional", "Ingenieria Civil");
        u3.setVehiculo(new Vehiculo("Renault", "Logan", "Blanco", "HTP 109", 4));

        Usuario u4 = crearUsuario("Laura Torres", "laura.torres@udea.edu.co",
                "1234", "Universidad de Antioquia", "Psicologia");
        u4.setVehiculo(new Vehiculo("Mazda", "2", "Rojo", "FGZ 730", 3));

        crearViaje(u2, "Laureles", "Campus Norte", "14/09/2026", "07:30 AM", 2, 6000);
        crearViaje(u3, "Estadio",  "Campus Norte", "14/09/2026", "08:00 AM", 3, 5500);
        crearViaje(u4, "Envigado", "Campus Norte", "14/09/2026", "06:30 AM", 2, 7000);
        crearViaje(u2, "Campus Norte", "Laureles",  "14/09/2026", "06:00 PM", 3, 6000);
    }

    // ------------------------------------------------------------------
    //  Usuarios
    // ------------------------------------------------------------------

    public static Usuario crearUsuario(String nombre, String email, String password,
                                       String universidad, String carrera) {
        contadorUsuarios++;
        Usuario u = new Usuario(contadorUsuarios, nombre, email, password, universidad, carrera);
        usuarios.add(u);
        return u;
    }

    /** Devuelve el usuario si el correo y la clave coinciden; si no, null. */
    public static Usuario validarLogin(String email, String password) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static boolean existeEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    //  Viajes
    // ------------------------------------------------------------------

    public static Viaje crearViaje(Usuario conductor, String origen, String destino,
                                   String fecha, String hora, int puestos, double precio) {
        contadorViajes++;
        Viaje v = new Viaje(contadorViajes, conductor, origen, destino, fecha, hora, puestos, precio);
        viajes.add(v);
        return v;
    }

    /**
     * Busca viajes. Si un campo viene vacio, ese campo no se tiene en cuenta.
     * No muestra los viajes publicados por el propio usuario.
     */
    public static ArrayList<Viaje> buscarViajes(String origen, String destino, String fecha) {
        ArrayList<Viaje> encontrados = new ArrayList<>();

        for (Viaje v : viajes) {

            if (usuarioActual != null && v.getConductor().getId() == usuarioActual.getId()) {
                continue;   // no me muestro mis propios viajes
            }
            if (v.getPuestosDisponibles() <= 0) {
                continue;   // viaje lleno
            }

            boolean coincide = true;

            if (!origen.isEmpty() && !v.getOrigen().toLowerCase().contains(origen.toLowerCase())) {
                coincide = false;
            }
            if (!destino.isEmpty() && !v.getDestino().toLowerCase().contains(destino.toLowerCase())) {
                coincide = false;
            }
            if (!fecha.isEmpty() && !v.getFecha().equals(fecha)) {
                coincide = false;
            }

            if (coincide) {
                encontrados.add(v);
            }
        }
        return encontrados;
    }

    /** Viajes publicados por el usuario que tiene la sesion abierta. */
    public static ArrayList<Viaje> viajesDelUsuario() {
        ArrayList<Viaje> mios = new ArrayList<>();
        for (Viaje v : viajes) {
            if (v.getConductor().getId() == usuarioActual.getId()) {
                mios.add(v);
            }
        }
        return mios;
    }

    // ------------------------------------------------------------------
    //  Solicitudes
    // ------------------------------------------------------------------

    public static Solicitud crearSolicitud(Viaje viaje, Usuario pasajero) {
        contadorSolicitudes++;
        Solicitud s = new Solicitud(contadorSolicitudes, viaje, pasajero);
        solicitudes.add(s);
        return s;
    }

    /** Solicitudes del usuario actual filtradas por estado. */
    public static ArrayList<Solicitud> solicitudesPorEstado(String estado) {
        ArrayList<Solicitud> lista = new ArrayList<>();
        for (Solicitud s : solicitudes) {
            if (s.getPasajero().getId() == usuarioActual.getId()
                    && s.getEstado().equals(estado)) {
                lista.add(s);
            }
        }
        return lista;
    }

    /** Evita que el usuario solicite dos veces el mismo viaje. */
    public static boolean yaSolicito(Viaje viaje) {
        for (Solicitud s : solicitudes) {
            if (s.getPasajero().getId() == usuarioActual.getId()
                    && s.getViaje().getId() == viaje.getId()) {
                return true;
            }
        }
        return false;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        resultadosBusqueda.clear();
        viajeSeleccionado = null;
    }
}
