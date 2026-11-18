package wheels.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/**
 * Acceso a la base de datos MySQL "wheels".
 * Todo es "static" para que cualquier ventana pueda leer y escribir
 * sin pasar objetos de una ventana a otra. Ya NO hay datos en memoria:
 * cada metodo consulta o modifica directamente las tablas.
 */
public class BaseDatos {

    /** Usuario que tiene la sesion abierta en este momento. */
    public static Usuario usuarioActual = null;

    /** Resultado de la ultima busqueda hecha en la pantalla Home. */
    public static ArrayList<Viaje> resultadosBusqueda = new ArrayList<>();

    /** Viaje que el usuario esta mirando en este momento. */
    public static Viaje viajeSeleccionado = null;

    /** Solicitud del viaje que esta en curso (pantalla Viaje en curso). */
    public static Solicitud solicitudEnCurso = null;

    public static final String[] CARRERAS = {
        "Ingenieria de Sistemas", "Ingenieria de Software", "Ingenieria Industrial",
        "Ingenieria Civil", "Ingenieria Electronica", "Administracion de Empresas",
        "Contaduria", "Psicologia", "Derecho", "Medicina", "Arquitectura", "Comunicacion"
    };

    // ------------------------------------------------------------------
    //  Utilidades
    // ------------------------------------------------------------------

    private static Connection cn() throws SQLException {
        Connection c = Conexion.getInstancia().conectar();
        if (c == null) {
            throw new SQLException("Sin conexion a MySQL (puerto 3307). Inicia MySQL en XAMPP.");
        }
        return c;
    }

    /** Fecha y hora actual en el formato que usa la BD: AAAA-MM-DD HH:MM:SS */
    public static String ahora() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private static void error(SQLException e) {
        System.out.println("Error de base de datos: " + e.getMessage());
        javax.swing.JOptionPane.showMessageDialog(null,
                "Error de base de datos:\n" + e.getMessage(),
                "Wheels", javax.swing.JOptionPane.ERROR_MESSAGE);
    }

    /** Prueba la conexion al arrancar la aplicacion. */
    public static boolean probarConexion() {
        return Conexion.getInstancia().conectar() != null;
    }

    // ------------------------------------------------------------------
    //  Universidades
    // ------------------------------------------------------------------

    /** Devuelve filas {id, nombre}. */
    public static ArrayList<String[]> universidades() {
        ArrayList<String[]> lista = new ArrayList<>();
        try (Statement st = cn().createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name FROM universities ORDER BY name")) {
            while (rs.next()) {
                lista.add(new String[]{String.valueOf(rs.getInt(1)), rs.getString(2)});
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    // ------------------------------------------------------------------
    //  Usuarios
    // ------------------------------------------------------------------

    private static final String SQL_USUARIO =
            "SELECT u.*, un.name AS uni FROM users u JOIN universities un ON un.id = u.university_id ";

    private static Usuario leerUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(rs.getInt("id"), rs.getString("name"), rs.getString("email"),
                rs.getString("password"), rs.getInt("university_id"), rs.getString("uni"),
                rs.getString("career"));
        u.setSemestre(rs.getInt("semester"));
        u.setTelefono(rs.getString("phone"));
        u.setRol(rs.getString("role"));
        u.setCalificacion(rs.getString("rating"));
        return u;
    }

    public static Usuario buscarUsuario(int id) {
        try (PreparedStatement ps = cn().prepareStatement(SQL_USUARIO + "WHERE u.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = leerUsuario(rs);
                    u.setVehiculo(vehiculoDe(u.getId()));
                    return u;
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    /** Devuelve el usuario si el correo y la clave coinciden; si no, null. */
    public static Usuario validarLogin(String email, String password) {
        try (PreparedStatement ps = cn().prepareStatement(
                SQL_USUARIO + "WHERE LOWER(u.email) = LOWER(?) AND u.password = ?")) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = leerUsuario(rs);
                    u.setVehiculo(vehiculoDe(u.getId()));
                    return u;
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    public static boolean existeEmail(String email) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT 1 FROM users WHERE LOWER(email) = LOWER(?)")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            error(e);
            return true;
        }
    }

    public static Usuario crearUsuario(String nombre, String email, String password,
                                       int universidadId, String carrera, int semestre, String telefono) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO users (university_id, name, email, password, career, semester, phone, role, rating, created_at) "
                + "VALUES (?,?,?,?,?,?,?,'student','5.0',?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, universidadId);
            ps.setString(2, nombre);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, carrera);
            ps.setInt(6, semestre);
            ps.setString(7, telefono);
            ps.setString(8, ahora());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return buscarUsuario(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    public static boolean actualizarPerfil(Usuario u, String nombre, String carrera, String telefono) {
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE users SET name = ?, career = ?, phone = ? WHERE id = ?")) {
            ps.setString(1, nombre);
            ps.setString(2, carrera);
            ps.setString(3, telefono);
            ps.setInt(4, u.getId());
            ps.executeUpdate();
            u.setNombre(nombre);
            u.setCarrera(carrera);
            u.setTelefono(telefono);
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    // ------------------------------------------------------------------
    //  Vehiculos
    // ------------------------------------------------------------------

    public static Vehiculo vehiculoDe(int usuarioId) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT * FROM vehicles WHERE user_id = ? ORDER BY id LIMIT 1")) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Vehiculo(rs.getInt("id"), rs.getString("brand"), rs.getString("model"),
                            rs.getString("color"), rs.getString("plate"), rs.getInt("year"), rs.getInt("seats"));
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    /** Inserta o actualiza el vehiculo del usuario actual. */
    public static boolean guardarVehiculo(String marca, String modelo, String color,
                                          String placa, int anio, int puestos) {
        Vehiculo actual = usuarioActual.getVehiculo();
        try {
            if (actual == null) {
                try (PreparedStatement ps = cn().prepareStatement(
                        "INSERT INTO vehicles (user_id, brand, model, color, plate, year, seats) VALUES (?,?,?,?,?,?,?)")) {
                    ps.setInt(1, usuarioActual.getId());
                    ps.setString(2, marca);
                    ps.setString(3, modelo);
                    ps.setString(4, color);
                    ps.setString(5, placa);
                    ps.setInt(6, anio);
                    ps.setInt(7, puestos);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = cn().prepareStatement(
                        "UPDATE vehicles SET brand=?, model=?, color=?, plate=?, year=?, seats=? WHERE id=?")) {
                    ps.setString(1, marca);
                    ps.setString(2, modelo);
                    ps.setString(3, color);
                    ps.setString(4, placa);
                    ps.setInt(5, anio);
                    ps.setInt(6, puestos);
                    ps.setInt(7, actual.getId());
                    ps.executeUpdate();
                }
            }
            usuarioActual.setVehiculo(vehiculoDe(usuarioActual.getId()));
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                javax.swing.JOptionPane.showMessageDialog(null, "Esa placa ya esta registrada.",
                        "Wheels", javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {
                error(e);
            }
            return false;
        }
    }

    // ------------------------------------------------------------------
    //  Viajes
    // ------------------------------------------------------------------

    /** Lee viajes con una consulta que devuelve filas de la tabla trips. */
    private static ArrayList<Viaje> leerViajes(PreparedStatement ps) throws SQLException {
        ArrayList<Viaje> lista = new ArrayList<>();
        HashMap<Integer, Usuario> cache = new HashMap<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leerViaje(rs, cache));
            }
        }
        return lista;
    }

    private static Viaje leerViaje(ResultSet rs, HashMap<Integer, Usuario> cache) throws SQLException {
        int idConductor = rs.getInt("driver_id");
        Usuario conductor = cache.get(idConductor);
        if (conductor == null) {
            conductor = buscarUsuario(idConductor);
            cache.put(idConductor, conductor);
        }
        Vehiculo v = conductor.getVehiculo();
        return new Viaje(rs.getInt("id"), conductor, v, rs.getString("origin"),
                rs.getString("destination"), rs.getString("departure_date"),
                rs.getString("departure_time"), rs.getInt("available_seats"),
                rs.getInt("price"), rs.getString("status"));
    }

    public static Viaje buscarViaje(int id) {
        try (PreparedStatement ps = cn().prepareStatement("SELECT * FROM trips WHERE id = ?")) {
            ps.setInt(1, id);
            ArrayList<Viaje> l = leerViajes(ps);
            return l.isEmpty() ? null : l.get(0);
        } catch (SQLException e) {
            error(e);
            return null;
        }
    }

    public static Viaje crearViaje(Usuario conductor, String origen, String destino,
                                   String fecha, String hora, int puestos, int precio) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO trips (driver_id, vehicle_id, origin, destination, departure_date, departure_time, "
                + "available_seats, price, status, created_at) VALUES (?,?,?,?,?,?,?,?,'available',?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conductor.getId());
            ps.setInt(2, conductor.getVehiculo().getId());
            ps.setString(3, origen);
            ps.setString(4, destino);
            ps.setString(5, fecha);
            ps.setString(6, hora);
            ps.setInt(7, puestos);
            ps.setInt(8, precio);
            ps.setString(9, ahora());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // preferencias por defecto del viaje
                    try (PreparedStatement p2 = cn().prepareStatement(
                            "INSERT INTO trip_preferences (trip_id) VALUES (?)")) {
                        p2.setInt(1, id);
                        p2.executeUpdate();
                    }
                    return buscarViaje(id);
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    /**
     * Busca viajes disponibles. Si un campo viene vacio, no se tiene en cuenta.
     * No muestra los viajes publicados por el propio usuario.
     */
    public static ArrayList<Viaje> buscarViajes(String origen, String destino, String fecha) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT * FROM trips WHERE status = 'available' AND available_seats > 0 AND driver_id <> ? "
                + "AND origin LIKE ? AND destination LIKE ? AND departure_date LIKE ? "
                + "ORDER BY departure_date, departure_time")) {
            ps.setInt(1, usuarioActual == null ? -1 : usuarioActual.getId());
            ps.setString(2, "%" + origen + "%");
            ps.setString(3, "%" + destino + "%");
            ps.setString(4, "%" + fecha + "%");
            return leerViajes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Viajes publicados por el usuario que tiene la sesion abierta. */
    public static ArrayList<Viaje> viajesDelUsuario() {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT * FROM trips WHERE driver_id = ? ORDER BY departure_date DESC")) {
            ps.setInt(1, usuarioActual.getId());
            return leerViajes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Puntos de recogida del viaje: filas {nombre, direccion, hora}. */
    public static ArrayList<String[]> puntosRecogida(int viajeId) {
        ArrayList<String[]> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT name, address, pickup_time FROM pickup_points WHERE trip_id = ? ORDER BY pickup_time")) {
            ps.setInt(1, viajeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3)});
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    /** Preferencias del viaje en texto: "Musica, Equipaje, Conversacion normal". */
    public static String preferencias(int viajeId) {
        try (PreparedStatement ps = cn().prepareStatement("SELECT * FROM trip_preferences WHERE trip_id = ?")) {
            ps.setInt(1, viajeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    if (rs.getInt("music_allowed") == 1)   sb.append("Musica  ");
                    if (rs.getInt("pets_allowed") == 1)    sb.append("Mascotas  ");
                    if (rs.getInt("luggage_allowed") == 1) sb.append("Equipaje  ");
                    if (rs.getInt("smoking_allowed") == 1) sb.append("Fumar  ");
                    String c = rs.getString("conversation_preference");
                    if ("quiet".equals(c))      sb.append("Silencioso");
                    else if ("talkative".equals(c)) sb.append("Conversador");
                    else sb.append("Charla normal");
                    return sb.toString().trim();
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return "Sin preferencias";
    }

    // ------------------------------------------------------------------
    //  Solicitudes
    // ------------------------------------------------------------------

    private static ArrayList<Solicitud> leerSolicitudes(PreparedStatement ps) throws SQLException {
        ArrayList<Solicitud> lista = new ArrayList<>();
        HashMap<Integer, Viaje> viajes = new HashMap<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idViaje = rs.getInt("trip_id");
                Viaje v = viajes.get(idViaje);
                if (v == null) {
                    v = buscarViaje(idViaje);
                    viajes.put(idViaje, v);
                }
                Usuario pasajero = rs.getInt("passenger_id") == usuarioActual.getId()
                        ? usuarioActual : buscarUsuario(rs.getInt("passenger_id"));
                lista.add(new Solicitud(rs.getInt("id"), v, pasajero, Solicitud.desdeBD(rs.getString("status"))));
            }
        }
        return lista;
    }

    public static Solicitud crearSolicitud(Viaje viaje, Usuario pasajero) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO trip_requests (trip_id, passenger_id, status, requested_at) VALUES (?,?,'pending',?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, viaje.getId());
            ps.setInt(2, pasajero.getId());
            ps.setString(3, ahora());
            ps.executeUpdate();
            notificar(viaje.getConductor().getId(), "Nueva solicitud de viaje",
                    pasajero.getNombre() + " solicito un cupo en tu viaje " + viaje.getRuta() + ".", "trip_request");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Solicitud(rs.getInt(1), viaje, pasajero, Solicitud.PENDIENTE);
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    /** Solicitudes del usuario actual (como pasajero) filtradas por estado. */
    public static ArrayList<Solicitud> solicitudesPorEstado(String estado) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT * FROM trip_requests WHERE passenger_id = ? AND status = ? ORDER BY id DESC")) {
            ps.setInt(1, usuarioActual.getId());
            ps.setString(2, Solicitud.haciaBD(estado));
            return leerSolicitudes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Solicitudes que otros pasajeros hicieron a los viajes del usuario actual (conductor). */
    public static ArrayList<Solicitud> solicitudesRecibidas() {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT r.* FROM trip_requests r JOIN trips t ON t.id = r.trip_id "
                + "WHERE t.driver_id = ? AND r.status IN ('pending','accepted','in_progress') "
                + "ORDER BY FIELD(r.status,'pending','in_progress','accepted'), r.id DESC")) {
            ps.setInt(1, usuarioActual.getId());
            return leerSolicitudes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    public static int contarSolicitudes() {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT COUNT(*) FROM trip_requests WHERE passenger_id = ?")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            error(e);
            return 0;
        }
    }

    public static int contarViajesPublicados() {
        try (PreparedStatement ps = cn().prepareStatement("SELECT COUNT(*) FROM trips WHERE driver_id = ?")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            error(e);
            return 0;
        }
    }

    /** Evita que el usuario solicite dos veces el mismo viaje. */
    public static boolean yaSolicito(Viaje viaje) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT 1 FROM trip_requests WHERE trip_id = ? AND passenger_id = ?")) {
            ps.setInt(1, viaje.getId());
            ps.setInt(2, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    /** Cambia el estado de una solicitud y aplica los efectos sobre el viaje. */
    public static boolean cambiarEstado(Solicitud s, String nuevoEstado) {
        try {
            try (PreparedStatement ps = cn().prepareStatement("UPDATE trip_requests SET status = ? WHERE id = ?")) {
                ps.setString(1, Solicitud.haciaBD(nuevoEstado));
                ps.setInt(2, s.getId());
                ps.executeUpdate();
            }
            Viaje v = s.getViaje();
            if (nuevoEstado.equals(Solicitud.ACEPTADA) && s.getEstado().equals(Solicitud.PENDIENTE)) {
                try (PreparedStatement ps = cn().prepareStatement(
                        "UPDATE trips SET available_seats = GREATEST(available_seats - 1, 0), "
                        + "status = IF(available_seats <= 0, 'full', status) WHERE id = ?")) {
                    ps.setInt(1, v.getId());
                    ps.executeUpdate();
                }
                notificar(s.getPasajero().getId(), "Solicitud aceptada",
                        v.getConductor().getPrimerNombre() + " acepto tu solicitud para " + v.getRuta() + ".",
                        "request_accepted");
            }
            if (nuevoEstado.equals(Solicitud.RECHAZADA)) {
                notificar(s.getPasajero().getId(), "Solicitud rechazada",
                        "Tu solicitud para " + v.getRuta() + " no fue aceptada.", "request_rejected");
            }
            if (nuevoEstado.equals(Solicitud.EN_CURSO)) {
                notificar(v.getConductor().getId(), "Viaje iniciado",
                        s.getPasajero().getNombre() + " inicio el viaje " + v.getRuta() + ".", "trip_started");
            }
            s.setEstado(nuevoEstado);
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    // ------------------------------------------------------------------
    //  Mensajes
    // ------------------------------------------------------------------

    /** Conversacion entre el usuario actual y otro usuario en un viaje: filas {remitente, texto, hora}. */
    public static ArrayList<String[]> mensajes(int otroId, int viajeId) {
        ArrayList<String[]> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT m.sender_id, m.message, m.sent_at, u.name FROM messages m JOIN users u ON u.id = m.sender_id "
                + "WHERE m.trip_id = ? AND ((m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?)) "
                + "ORDER BY m.id")) {
            ps.setInt(1, viajeId);
            ps.setInt(2, usuarioActual.getId());
            ps.setInt(3, otroId);
            ps.setInt(4, otroId);
            ps.setInt(5, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String quien = rs.getInt(1) == usuarioActual.getId() ? "Yo" : rs.getString(4).split(" ")[0];
                    String hora = rs.getString(3) == null ? "" : rs.getString(3).substring(11, 16);
                    lista.add(new String[]{quien, rs.getString(2), hora});
                }
            }
            try (PreparedStatement p2 = cn().prepareStatement(
                    "UPDATE messages SET is_read = 1 WHERE receiver_id = ? AND sender_id = ? AND trip_id = ?")) {
                p2.setInt(1, usuarioActual.getId());
                p2.setInt(2, otroId);
                p2.setInt(3, viajeId);
                p2.executeUpdate();
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    public static void enviarMensaje(int receptorId, int viajeId, String texto) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO messages (sender_id, receiver_id, trip_id, message, is_read, sent_at) VALUES (?,?,?,?,0,?)")) {
            ps.setInt(1, usuarioActual.getId());
            ps.setInt(2, receptorId);
            ps.setInt(3, viajeId);
            ps.setString(4, texto);
            ps.setString(5, ahora());
            ps.executeUpdate();
            notificar(receptorId, "Nuevo mensaje",
                    "Recibiste un nuevo mensaje de " + usuarioActual.getNombre() + ".", "new_message");
        } catch (SQLException e) {
            error(e);
        }
    }

    // ------------------------------------------------------------------
    //  Notificaciones
    // ------------------------------------------------------------------

    public static void notificar(int usuarioId, String titulo, String mensaje, String tipo) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES (?,?,?,?,0,?)")) {
            ps.setInt(1, usuarioId);
            ps.setString(2, titulo);
            ps.setString(3, mensaje);
            ps.setString(4, tipo);
            ps.setString(5, ahora());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("No se pudo crear la notificacion: " + e.getMessage());
        }
    }

    /** Notificaciones del usuario actual: filas {titulo, mensaje, fecha, leida(0/1), tipo}. */
    public static ArrayList<String[]> notificaciones() {
        ArrayList<String[]> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT title, message, created_at, is_read, type FROM notifications WHERE user_id = ? ORDER BY id DESC LIMIT 30")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3),
                        String.valueOf(rs.getInt(4)), rs.getString(5)});
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    public static int notificacionesSinLeer() {
        if (usuarioActual == null) return 0;
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    public static void marcarNotificacionesLeidas() {
        try (PreparedStatement ps = cn().prepareStatement("UPDATE notifications SET is_read = 1 WHERE user_id = ?")) {
            ps.setInt(1, usuarioActual.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            error(e);
        }
    }

    // ------------------------------------------------------------------
    //  Calificaciones y reportes
    // ------------------------------------------------------------------

    public static void calificar(Viaje viaje, int calificadoId, int puntaje, String comentario) {
        try {
            try (PreparedStatement ps = cn().prepareStatement(
                    "INSERT INTO ratings (trip_id, reviewer_id, reviewed_user_id, score, comment, created_at) VALUES (?,?,?,?,?,?)")) {
                ps.setInt(1, viaje.getId());
                ps.setInt(2, usuarioActual.getId());
                ps.setInt(3, calificadoId);
                ps.setInt(4, puntaje);
                ps.setString(5, comentario);
                ps.setString(6, ahora());
                ps.executeUpdate();
            }
            // Recalcula el promedio del usuario calificado
            try (PreparedStatement ps = cn().prepareStatement(
                    "UPDATE users SET rating = (SELECT FORMAT(AVG(score),1) FROM ratings WHERE reviewed_user_id = ?) WHERE id = ?")) {
                ps.setInt(1, calificadoId);
                ps.setInt(2, calificadoId);
                ps.executeUpdate();
            }
            notificar(calificadoId, "Nueva calificacion",
                    "Recibiste " + puntaje + " estrellas de " + usuarioActual.getNombre() + ".", "rating");
        } catch (SQLException e) {
            error(e);
        }
    }

    /**
     * BOTON DE PANICO: guarda un reporte urgente y notifica a los administradores
     * y al conductor. Devuelve el id del reporte (o -1 si fallo).
     */
    public static int activarPanico(Viaje viaje, String ubicacion) {
        int idReporte = -1;
        String descripcion = "ALERTA SOS activada por " + usuarioActual.getNombre()
                + " (tel " + usuarioActual.getTelefono() + ") en el viaje " + viaje.getRuta()
                + ". Vehiculo " + viaje.getVehiculo() + ". Ubicacion aproximada: " + ubicacion
                + ". Hora: " + ahora();
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO reports (reporter_id, reported_user_id, trip_id, reason, description, status, created_at) "
                + "VALUES (?,?,?,'BOTON DE PANICO',?,'pending',?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioActual.getId());
            ps.setInt(2, viaje.getConductor().getId());
            ps.setInt(3, viaje.getId());
            ps.setString(4, descripcion.length() > 500 ? descripcion.substring(0, 500) : descripcion);
            ps.setString(5, ahora());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) idReporte = rs.getInt(1);
            }
            try (Statement st = cn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id FROM users WHERE role = 'admin'")) {
                ArrayList<Integer> admins = new ArrayList<>();
                while (rs.next()) admins.add(rs.getInt(1));
                for (int id : admins) {
                    notificar(id, "ALERTA SOS - Reporte #" + idReporte, descripcion, "panic");
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return idReporte;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        resultadosBusqueda.clear();
        viajeSeleccionado = null;
        solicitudEnCurso = null;
    }
}
