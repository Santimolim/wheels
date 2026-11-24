package wheels.datos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import wheels.modelo.Solicitud;
import wheels.modelo.Usuario;
import wheels.modelo.Vehiculo;
import wheels.modelo.Viaje;

/**
 * Acceso a la base de datos MySQL "wheels".
 * Todo es "static" para que cualquier pantalla pueda leer y escribir
 * sin pasar objetos de una pantalla a otra. No hay datos en memoria:
 * cada metodo consulta o modifica directamente las tablas.
 *
 * Las lecturas de viajes y solicitudes usan JOIN para traer conductor,
 * universidad y vehiculo en una sola consulta (antes era una consulta por fila).
 * Las operaciones que tocan varias tablas (aceptar, cancelar) usan transacciones.
 */
public class BaseDatos {

    /** Usuario que tiene la sesion abierta en este momento. */
    public static Usuario usuarioActual = null;

    /** Resultado de la ultima busqueda hecha en la pantalla Inicio. */
    public static ArrayList<Viaje> resultadosBusqueda = new ArrayList<>();
    /** Texto de la ultima busqueda, para mostrarlo en la pantalla de resultados. */
    public static String descripcionBusqueda = "";

    /** Viaje que el usuario esta mirando en este momento. */
    public static Viaje viajeSeleccionado = null;

    /** Solicitud del viaje que esta en curso (pantalla Viaje en curso). */
    public static Solicitud solicitudEnCurso = null;

    /** Momento en que se inicio cada viaje en esta sesion (id solicitud -> milisegundos). */
    private static final Map<Integer, Long> inicioViajes = new HashMap<>();

    public static final String[] CARRERAS = {
        "Administración de Empresas", "Arquitectura", "Comunicación Social", "Contaduría Pública",
        "Derecho", "Diseño", "Economía", "Enfermería", "Ingeniería Civil", "Ingeniería de Sistemas",
        "Ingeniería de Software", "Ingeniería Electrónica", "Ingeniería Industrial", "Ingeniería Mecánica",
        "Medicina", "Psicología"
    };

    /** Quien muestra los errores de base de datos (la interfaz lo reemplaza al arrancar). */
    public static Consumer<String> manejadorError = msg -> System.err.println("Error de base de datos: " + msg);

    private static final DateTimeFormatter FORMATO_AHORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ------------------------------------------------------------------
    //  Utilidades
    // ------------------------------------------------------------------

    private static Connection cn() throws SQLException {
        Connection c = Conexion.getInstancia().conectar();
        if (c == null) {
            throw new SQLException("Sin conexión con MySQL (" + Conexion.getInstancia().getDescripcion()
                    + "). Verifica que MySQL esté iniciado en XAMPP.");
        }
        return c;
    }

    /** Fecha y hora actual en el formato que usa la BD: AAAA-MM-DD HH:MM:SS */
    public static String ahora() {
        return LocalDateTime.now().format(FORMATO_AHORA);
    }

    public static String hoy() {
        return LocalDate.now().toString();
    }

    private static void error(SQLException e) {
        System.err.println("Error de base de datos: " + e.getMessage());
        manejadorError.accept(e.getMessage());
    }

    /** Escapa los comodines de LIKE para que el texto se busque literalmente. */
    private static String like(String texto) {
        return "%" + texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }

    /** Prueba la conexion al arrancar la aplicacion. */
    public static boolean probarConexion() {
        return Conexion.getInstancia().conectar() != null;
    }

    private interface Trabajo {
        void ejecutar(Connection c) throws SQLException;
    }

    /** Ejecuta varias sentencias como una sola transaccion (todo o nada). */
    private static void transaccion(Trabajo t) throws SQLException {
        Connection c = cn();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            t.ejecutar(c);
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
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
                rs.getInt("university_id"), rs.getString("uni"), rs.getString("career"));
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

    /**
     * Devuelve el usuario si el correo y la clave coinciden; si no, null.
     * Si la clave guardada estaba en texto plano (datos antiguos), se cifra en ese momento.
     */
    public static Usuario validarLogin(String email, String password) {
        try (PreparedStatement ps = cn().prepareStatement(SQL_USUARIO + "WHERE LOWER(u.email) = LOWER(?)")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    Seguridad.cifrar(password);   // mismo tiempo de respuesta exista o no el correo
                    return null;
                }
                String guardada = rs.getString("password");
                if (!Seguridad.verificar(password, guardada)) {
                    return null;
                }
                Usuario u = leerUsuario(rs);
                u.setVehiculo(vehiculoDe(u.getId()));
                if (!Seguridad.estaCifrada(guardada)) {
                    guardarClave(u.getId(), password);
                }
                return u;
            }
        } catch (SQLException e) {
            error(e);
        }
        return null;
    }

    private static void guardarClave(int usuarioId, String clave) throws SQLException {
        try (PreparedStatement ps = cn().prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
            ps.setString(1, Seguridad.cifrar(clave));
            ps.setInt(2, usuarioId);
            ps.executeUpdate();
        }
    }

    /** Cambia la contrasena del usuario actual. Devuelve false si la actual no coincide. */
    public static boolean cambiarClave(String actual, String nueva) {
        try (PreparedStatement ps = cn().prepareStatement("SELECT password FROM users WHERE id = ?")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !Seguridad.verificar(actual, rs.getString(1))) {
                    return false;
                }
            }
            guardarClave(usuarioActual.getId(), nueva);
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
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
            ps.setString(3, email.toLowerCase());
            ps.setString(4, Seguridad.cifrar(password));
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

    public static boolean actualizarPerfil(Usuario u, String nombre, String carrera, int semestre, String telefono) {
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE users SET name = ?, career = ?, semester = ?, phone = ? WHERE id = ?")) {
            ps.setString(1, nombre);
            ps.setString(2, carrera);
            ps.setInt(3, semestre);
            ps.setString(4, telefono);
            ps.setInt(5, u.getId());
            ps.executeUpdate();
            u.setNombre(nombre);
            u.setCarrera(carrera);
            u.setSemestre(semestre);
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

    /**
     * Inserta o actualiza el vehiculo del usuario actual.
     * Devuelve null si todo salio bien, o el mensaje de error para mostrarlo en el formulario.
     */
    public static String guardarVehiculo(String marca, String modelo, String color,
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
            return null;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 || String.valueOf(e.getMessage()).contains("Duplicate")) {
                return "Esa placa ya está registrada por otro usuario.";
            }
            error(e);
            return "No se pudo guardar el vehículo.";
        }
    }

    // ------------------------------------------------------------------
    //  Viajes
    // ------------------------------------------------------------------

    /** Columnas de un viaje con su conductor, universidad y vehiculo (una sola consulta). */
    private static final String SQL_VIAJE =
            "SELECT t.id AS t_id, t.origin, t.destination, t.departure_date, t.departure_time, "
            + "t.available_seats, t.price, t.status AS t_status, "
            + "d.id AS d_id, d.name AS d_name, d.email AS d_email, d.career AS d_career, d.semester AS d_semester, "
            + "d.phone AS d_phone, d.role AS d_role, d.rating AS d_rating, d.university_id AS d_uid, du.name AS d_uni, "
            + "v.id AS v_id, v.brand, v.model, v.color, v.plate, v.year, v.seats ";

    private static final String FROM_VIAJE =
            "FROM trips t JOIN users d ON d.id = t.driver_id "
            + "JOIN universities du ON du.id = d.university_id "
            + "JOIN vehicles v ON v.id = t.vehicle_id ";

    private static Viaje leerViaje(ResultSet rs) throws SQLException {
        Usuario conductor = new Usuario(rs.getInt("d_id"), rs.getString("d_name"), rs.getString("d_email"),
                rs.getInt("d_uid"), rs.getString("d_uni"), rs.getString("d_career"));
        conductor.setSemestre(rs.getInt("d_semester"));
        conductor.setTelefono(rs.getString("d_phone"));
        conductor.setRol(rs.getString("d_role"));
        conductor.setCalificacion(rs.getString("d_rating"));
        Vehiculo v = new Vehiculo(rs.getInt("v_id"), rs.getString("brand"), rs.getString("model"),
                rs.getString("color"), rs.getString("plate"), rs.getInt("year"), rs.getInt("seats"));
        conductor.setVehiculo(v);
        return new Viaje(rs.getInt("t_id"), conductor, v, rs.getString("origin"),
                rs.getString("destination"), rs.getString("departure_date"),
                rs.getString("departure_time"), rs.getInt("available_seats"),
                rs.getInt("price"), rs.getString("t_status"));
    }

    private static ArrayList<Viaje> leerViajes(PreparedStatement ps) throws SQLException {
        ArrayList<Viaje> lista = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(leerViaje(rs));
            }
        }
        return lista;
    }

    public static Viaje buscarViaje(int id) {
        try (PreparedStatement ps = cn().prepareStatement(SQL_VIAJE + FROM_VIAJE + "WHERE t.id = ?")) {
            ps.setInt(1, id);
            ArrayList<Viaje> l = leerViajes(ps);
            return l.isEmpty() ? null : l.get(0);
        } catch (SQLException e) {
            error(e);
            return null;
        }
    }

    public static Viaje crearViaje(Usuario conductor, String origen, String destino,
                                   String fecha, String hora, int puestos, int precio,
                                   boolean musica, boolean mascotas, boolean equipaje, String conversacion) {
        final int[] idNuevo = {-1};
        try {
            transaccion(c -> {
                try (PreparedStatement ps = c.prepareStatement(
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
                        if (rs.next()) idNuevo[0] = rs.getInt(1);
                    }
                }
                try (PreparedStatement p2 = c.prepareStatement(
                        "INSERT INTO trip_preferences (trip_id, music_allowed, pets_allowed, luggage_allowed, "
                        + "smoking_allowed, conversation_preference) VALUES (?,?,?,?,0,?)")) {
                    p2.setInt(1, idNuevo[0]);
                    p2.setInt(2, musica ? 1 : 0);
                    p2.setInt(3, mascotas ? 1 : 0);
                    p2.setInt(4, equipaje ? 1 : 0);
                    p2.setString(5, conversacion);
                    p2.executeUpdate();
                }
            });
            return buscarViaje(idNuevo[0]);
        } catch (SQLException e) {
            error(e);
            return null;
        }
    }

    /**
     * Busca viajes disponibles desde hoy en adelante. Si un campo viene vacio,
     * no se tiene en cuenta. No muestra los viajes publicados por el propio usuario.
     */
    public static ArrayList<Viaje> buscarViajes(String origen, String destino, String fecha) {
        String sql = SQL_VIAJE + FROM_VIAJE
                + "WHERE t.status = 'available' AND t.available_seats > 0 AND t.driver_id <> ? "
                + "AND t.departure_date >= ? AND t.origin LIKE ? AND t.destination LIKE ? "
                + (fecha.isEmpty() ? "" : "AND t.departure_date = ? ")
                + "ORDER BY t.departure_date, t.departure_time";
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
            ps.setInt(1, usuarioActual == null ? -1 : usuarioActual.getId());
            ps.setString(2, hoy());
            ps.setString(3, like(origen));
            ps.setString(4, like(destino));
            if (!fecha.isEmpty()) ps.setString(5, fecha);
            return leerViajes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Destinos con mas viajes disponibles desde hoy (para sugerencias rapidas). */
    public static ArrayList<String> destinosPopulares(int limite) {
        ArrayList<String> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT destination, COUNT(*) n FROM trips WHERE status = 'available' AND available_seats > 0 "
                + "AND departure_date >= ? AND driver_id <> ? GROUP BY destination ORDER BY n DESC, destination LIMIT ?")) {
            ps.setString(1, hoy());
            ps.setInt(2, usuarioActual == null ? -1 : usuarioActual.getId());
            ps.setInt(3, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(rs.getString(1));
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    /** Ids de los viajes que el usuario actual ya califico. */
    public static java.util.Set<Integer> viajesCalificados() {
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        try (PreparedStatement ps = cn().prepareStatement("SELECT trip_id FROM ratings WHERE reviewer_id = ?")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            error(e);
        }
        return ids;
    }

    /** Viajes publicados por el usuario actual, los proximos primero. */
    public static ArrayList<Viaje> viajesDelUsuario() {
        try (PreparedStatement ps = cn().prepareStatement(SQL_VIAJE + FROM_VIAJE
                + "WHERE t.driver_id = ? ORDER BY (t.departure_date < ?), t.departure_date, t.departure_time")) {
            ps.setInt(1, usuarioActual.getId());
            ps.setString(2, hoy());
            return leerViajes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Cantidad de pasajeros aceptados o en curso de un viaje. */
    public static int pasajerosConfirmados(int viajeId) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT COUNT(*) FROM trip_requests WHERE trip_id = ? AND status IN ('accepted','in_progress','completed')")) {
            ps.setInt(1, viajeId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            error(e);
            return 0;
        }
    }

    /**
     * El conductor cancela su viaje: se cancelan las solicitudes pendientes o aceptadas
     * y se avisa a cada pasajero. No se puede si algun pasajero ya inicio el viaje.
     * Devuelve null si se cancelo, o el motivo si no se pudo.
     */
    public static String cancelarViaje(Viaje v) {
        final List<Integer> avisar = new ArrayList<>();
        final String[] motivo = {null};
        try {
            transaccion(c -> {
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT 1 FROM trip_requests WHERE trip_id = ? AND status = 'in_progress'")) {
                    ps.setInt(1, v.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            motivo[0] = "Un pasajero ya inició este viaje; no se puede cancelar.";
                            return;
                        }
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT passenger_id FROM trip_requests WHERE trip_id = ? AND status IN ('pending','accepted')")) {
                    ps.setInt(1, v.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) avisar.add(rs.getInt(1));
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE trip_requests SET status = 'cancelled' WHERE trip_id = ? AND status IN ('pending','accepted')")) {
                    ps.setInt(1, v.getId());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE trips SET status = 'cancelled' WHERE id = ?")) {
                    ps.setInt(1, v.getId());
                    ps.executeUpdate();
                }
            });
        } catch (SQLException e) {
            error(e);
            return "No se pudo cancelar el viaje.";
        }
        if (motivo[0] != null) return motivo[0];
        v.setEstado(Viaje.CANCELADO);
        for (int id : avisar) {
            notificar(id, "Viaje cancelado", v.getConductor().getPrimerNombre() + " canceló el viaje "
                    + v.getRuta() + " del " + v.getFecha() + ".", "trip_cancelled");
        }
        return null;
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

    /** Preferencias del viaje como lista de textos: "Música", "Equipaje", "Charla normal". */
    public static ArrayList<String> preferencias(int viajeId) {
        ArrayList<String> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement("SELECT * FROM trip_preferences WHERE trip_id = ?")) {
            ps.setInt(1, viajeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (rs.getInt("music_allowed") == 1)   lista.add("Música");
                    if (rs.getInt("pets_allowed") == 1)    lista.add("Mascotas");
                    if (rs.getInt("luggage_allowed") == 1) lista.add("Equipaje");
                    if (rs.getInt("smoking_allowed") == 1) lista.add("Se permite fumar");
                    String c = rs.getString("conversation_preference");
                    if ("quiet".equals(c)) lista.add("Viaje silencioso");
                    else if ("talkative".equals(c)) lista.add("Conversador");
                    else lista.add("Charla normal");
                }
            }
        } catch (SQLException e) {
            error(e);
        }
        return lista;
    }

    // ------------------------------------------------------------------
    //  Solicitudes
    // ------------------------------------------------------------------

    private static final String SQL_SOLICITUD = SQL_VIAJE
            + ", r.id AS r_id, r.status AS r_status, r.requested_at, "
            + "p.id AS p_id, p.name AS p_name, p.email AS p_email, p.career AS p_career, p.semester AS p_semester, "
            + "p.phone AS p_phone, p.role AS p_role, p.rating AS p_rating, p.university_id AS p_uid, pu.name AS p_uni "
            + FROM_VIAJE
            + "JOIN trip_requests r ON r.trip_id = t.id "
            + "JOIN users p ON p.id = r.passenger_id "
            + "JOIN universities pu ON pu.id = p.university_id ";

    private static ArrayList<Solicitud> leerSolicitudes(PreparedStatement ps) throws SQLException {
        ArrayList<Solicitud> lista = new ArrayList<>();
        HashMap<Integer, Viaje> viajes = new HashMap<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int idViaje = rs.getInt("t_id");
                Viaje v = viajes.get(idViaje);
                if (v == null) {
                    v = leerViaje(rs);
                    viajes.put(idViaje, v);
                }
                Usuario pasajero;
                if (usuarioActual != null && rs.getInt("p_id") == usuarioActual.getId()) {
                    pasajero = usuarioActual;
                } else {
                    pasajero = new Usuario(rs.getInt("p_id"), rs.getString("p_name"), rs.getString("p_email"),
                            rs.getInt("p_uid"), rs.getString("p_uni"), rs.getString("p_career"));
                    pasajero.setSemestre(rs.getInt("p_semester"));
                    pasajero.setTelefono(rs.getString("p_phone"));
                    pasajero.setRol(rs.getString("p_role"));
                    pasajero.setCalificacion(rs.getString("p_rating"));
                }
                lista.add(new Solicitud(rs.getInt("r_id"), v, pasajero,
                        Solicitud.desdeBD(rs.getString("r_status")), rs.getString("requested_at")));
            }
        }
        return lista;
    }

    /**
     * Crea la solicitud de un puesto. Devuelve null si se creo, o el motivo si no se pudo
     * (el viaje ya no esta disponible, ya lo solicito, etc.).
     */
    public static String crearSolicitud(Viaje viaje, Usuario pasajero) {
        try {
            Viaje actual = buscarViaje(viaje.getId());
            if (actual == null || !Viaje.DISPONIBLE.equals(actual.getEstado()) || actual.getPuestosDisponibles() <= 0) {
                return "Este viaje ya no tiene puestos disponibles.";
            }
            if (actual.yaPaso()) {
                return "Este viaje ya salió.";
            }
            try (PreparedStatement ps = cn().prepareStatement(
                    "INSERT INTO trip_requests (trip_id, passenger_id, status, requested_at) VALUES (?,?,'pending',?)")) {
                ps.setInt(1, viaje.getId());
                ps.setInt(2, pasajero.getId());
                ps.setString(3, ahora());
                ps.executeUpdate();
            }
            notificar(viaje.getConductor().getId(), "Nueva solicitud de viaje",
                    pasajero.getNombre() + " solicitó un puesto en tu viaje " + viaje.getRuta() + ".", "trip_request");
            return null;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return "Ya enviaste una solicitud para este viaje.";
            }
            error(e);
            return "No se pudo enviar la solicitud.";
        }
    }

    /** Solicitudes del usuario actual (como pasajero) con alguno de los estados dados. */
    public static ArrayList<Solicitud> solicitudesPorEstado(String... estados) {
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < estados.length; i++) in.append(i == 0 ? "?" : ",?");
        try (PreparedStatement ps = cn().prepareStatement(SQL_SOLICITUD
                + "WHERE r.passenger_id = ? AND r.status IN (" + in + ") "
                + "ORDER BY FIELD(r.status,'in_progress','accepted','pending'), t.departure_date DESC, r.id DESC")) {
            ps.setInt(1, usuarioActual.getId());
            for (int i = 0; i < estados.length; i++) ps.setString(i + 2, Solicitud.haciaBD(estados[i]));
            return leerSolicitudes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    /** Solicitudes que otros pasajeros hicieron a los viajes del usuario actual (conductor). */
    public static ArrayList<Solicitud> solicitudesRecibidas() {
        try (PreparedStatement ps = cn().prepareStatement(SQL_SOLICITUD
                + "WHERE t.driver_id = ? AND t.status <> 'cancelled' AND r.status IN ('pending','accepted','in_progress') "
                + "ORDER BY FIELD(r.status,'pending','in_progress','accepted'), t.departure_date, r.id DESC")) {
            ps.setInt(1, usuarioActual.getId());
            return leerSolicitudes(ps);
        } catch (SQLException e) {
            error(e);
            return new ArrayList<>();
        }
    }

    public static int solicitudesPendientesRecibidas() {
        if (usuarioActual == null) return 0;
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT COUNT(*) FROM trip_requests r JOIN trips t ON t.id = r.trip_id "
                + "WHERE t.driver_id = ? AND t.status <> 'cancelled' AND r.status = 'pending'")) {
            ps.setInt(1, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    public static int contarSolicitudes() {
        return contar("SELECT COUNT(*) FROM trip_requests WHERE passenger_id = ? AND status = 'completed'");
    }

    public static int contarViajesPublicados() {
        return contar("SELECT COUNT(*) FROM trips WHERE driver_id = ? AND status <> 'cancelled'");
    }

    private static int contar(String sql) {
        try (PreparedStatement ps = cn().prepareStatement(sql)) {
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

    /** Estado de la solicitud del usuario actual en un viaje, o null si no la ha hecho. */
    public static String estadoSolicitud(Viaje viaje) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT status FROM trip_requests WHERE trip_id = ? AND passenger_id = ?")) {
            ps.setInt(1, viaje.getId());
            ps.setInt(2, usuarioActual.getId());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Solicitud.desdeBD(rs.getString(1)) : null;
            }
        } catch (SQLException e) {
            error(e);
            return null;
        }
    }

    /**
     * El conductor acepta una solicitud. Descuenta el puesto de forma atomica:
     * si dos solicitudes se aceptan a la vez, solo una obtiene el ultimo puesto.
     * Devuelve null si se acepto, o el motivo si no.
     */
    public static String aceptarSolicitud(Solicitud s) {
        final String[] motivo = {null};
        try {
            transaccion(c -> {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE trips SET available_seats = available_seats - 1, "
                        + "status = IF(available_seats = 0, 'full', status) "
                        + "WHERE id = ? AND available_seats > 0 AND status = 'available'")) {
                    ps.setInt(1, s.getViaje().getId());
                    if (ps.executeUpdate() == 0) {
                        motivo[0] = "Este viaje ya no tiene puestos libres.";
                        return;
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE trip_requests SET status = 'accepted' WHERE id = ? AND status = 'pending'")) {
                    ps.setInt(1, s.getId());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("La solicitud ya no está pendiente.");
                    }
                }
            });
        } catch (SQLException e) {
            return e.getMessage();
        }
        if (motivo[0] != null) return motivo[0];
        s.setEstado(Solicitud.ACEPTADA);
        Viaje v = s.getViaje();
        v.setPuestosDisponibles(Math.max(0, v.getPuestosDisponibles() - 1));
        notificar(s.getPasajero().getId(), "Solicitud aceptada",
                v.getConductor().getPrimerNombre() + " aceptó tu solicitud para " + v.getRuta()
                + " (" + v.getFecha() + " " + v.getHora() + ").", "request_accepted");
        return null;
    }

    public static boolean rechazarSolicitud(Solicitud s) {
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE trip_requests SET status = 'rejected' WHERE id = ? AND status = 'pending'")) {
            ps.setInt(1, s.getId());
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) {
            error(e);
            return false;
        }
        s.setEstado(Solicitud.RECHAZADA);
        notificar(s.getPasajero().getId(), "Solicitud no aceptada",
                "Tu solicitud para " + s.getViaje().getRuta() + " no fue aceptada.", "request_rejected");
        return true;
    }

    /**
     * El pasajero cancela su solicitud. Si estaba aceptada, el puesto vuelve al viaje.
     * La fila se elimina para que pueda volver a solicitar el mismo viaje si cambia de opinion.
     */
    public static boolean cancelarSolicitud(Solicitud s) {
        final boolean liberarPuesto = s.getEstado().equals(Solicitud.ACEPTADA);
        try {
            transaccion(c -> {
                try (PreparedStatement ps = c.prepareStatement(
                        "DELETE FROM trip_requests WHERE id = ? AND passenger_id = ? AND status IN ('pending','accepted')")) {
                    ps.setInt(1, s.getId());
                    ps.setInt(2, usuarioActual.getId());
                    if (ps.executeUpdate() == 0) throw new SQLException("La solicitud ya no se puede cancelar.");
                }
                if (liberarPuesto) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE trips SET available_seats = available_seats + 1, "
                            + "status = IF(status = 'full', 'available', status) WHERE id = ?")) {
                        ps.setInt(1, s.getViaje().getId());
                        ps.executeUpdate();
                    }
                }
            });
        } catch (SQLException e) {
            error(e);
            return false;
        }
        s.setEstado(Solicitud.CANCELADA);
        notificar(s.getViaje().getConductor().getId(), "Solicitud cancelada",
                s.getPasajero().getNombre() + " canceló su " + (liberarPuesto ? "puesto" : "solicitud")
                + " en el viaje " + s.getViaje().getRuta() + ".", "request_cancelled");
        return true;
    }

    /** El pasajero inicia el viaje (aceptada -> en curso). */
    public static boolean iniciarViaje(Solicitud s) {
        if (s.getEstado().equals(Solicitud.EN_CURSO)) {
            inicioViajes.putIfAbsent(s.getId(), System.currentTimeMillis());
            return true;
        }
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE trip_requests SET status = 'in_progress' WHERE id = ? AND status = 'accepted'")) {
            ps.setInt(1, s.getId());
            if (ps.executeUpdate() == 0) return false;
        } catch (SQLException e) {
            error(e);
            return false;
        }
        s.setEstado(Solicitud.EN_CURSO);
        inicioViajes.put(s.getId(), System.currentTimeMillis());
        notificar(s.getViaje().getConductor().getId(), "Viaje iniciado",
                s.getPasajero().getNombre() + " inició el viaje " + s.getViaje().getRuta() + ".", "trip_started");
        return true;
    }

    /** Milisegundos en que se inicio el viaje en esta sesion (o ahora si se inicio en otra). */
    public static long inicioViaje(Solicitud s) {
        return inicioViajes.computeIfAbsent(s.getId(), k -> System.currentTimeMillis());
    }

    public static boolean finalizarViaje(Solicitud s) {
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE trip_requests SET status = 'completed' WHERE id = ? AND status = 'in_progress'")) {
            ps.setInt(1, s.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            error(e);
            return false;
        }
        s.setEstado(Solicitud.COMPLETADA);
        inicioViajes.remove(s.getId());
        notificar(s.getViaje().getConductor().getId(), "Viaje finalizado",
                s.getPasajero().getNombre() + " finalizó el viaje " + s.getViaje().getRuta() + ".", "trip_completed");
        return true;
    }

    // ------------------------------------------------------------------
    //  Mensajes
    // ------------------------------------------------------------------

    /** Conversacion entre el usuario actual y otro usuario en un viaje: filas {remitente, texto, hora, esMio(1/0)}. */
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
                    boolean mio = rs.getInt(1) == usuarioActual.getId();
                    String quien = mio ? "Tú" : rs.getString(4).split(" ")[0];
                    String enviado = rs.getString(3);
                    String hora = enviado == null || enviado.length() < 16 ? "" : enviado.substring(11, 16);
                    lista.add(new String[]{quien, rs.getString(2), hora, mio ? "1" : "0"});
                }
            }
            try (PreparedStatement p2 = cn().prepareStatement(
                    "UPDATE messages SET is_read = 1 WHERE receiver_id = ? AND sender_id = ? AND trip_id = ? AND is_read = 0")) {
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

    public static boolean enviarMensaje(int receptorId, int viajeId, String texto) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO messages (sender_id, receiver_id, trip_id, message, is_read, sent_at) VALUES (?,?,?,?,0,?)")) {
            ps.setInt(1, usuarioActual.getId());
            ps.setInt(2, receptorId);
            ps.setInt(3, viajeId);
            ps.setString(4, texto);
            ps.setString(5, ahora());
            ps.executeUpdate();
            notificar(receptorId, "Nuevo mensaje",
                    usuarioActual.getPrimerNombre() + ": " + (texto.length() > 80 ? texto.substring(0, 80) + "…" : texto),
                    "new_message");
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    // ------------------------------------------------------------------
    //  Notificaciones
    // ------------------------------------------------------------------

    public static void notificar(int usuarioId, String titulo, String mensaje, String tipo) {
        try (PreparedStatement ps = cn().prepareStatement(
                "INSERT INTO notifications (user_id, title, message, type, is_read, created_at) VALUES (?,?,?,?,0,?)")) {
            ps.setInt(1, usuarioId);
            ps.setString(2, titulo.length() > 150 ? titulo.substring(0, 150) : titulo);
            ps.setString(3, mensaje.length() > 500 ? mensaje.substring(0, 500) : mensaje);
            ps.setString(4, tipo);
            ps.setString(5, ahora());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("No se pudo crear la notificacion: " + e.getMessage());
        }
    }

    /** Notificaciones del usuario actual: filas {titulo, mensaje, fecha, leida(0/1), tipo}. */
    public static ArrayList<String[]> notificaciones() {
        ArrayList<String[]> lista = new ArrayList<>();
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT title, message, created_at, is_read, type FROM notifications WHERE user_id = ? ORDER BY id DESC LIMIT 50")) {
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
        try (PreparedStatement ps = cn().prepareStatement(
                "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0")) {
            ps.setInt(1, usuarioActual.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            error(e);
        }
    }

    // ------------------------------------------------------------------
    //  Calificaciones y reportes
    // ------------------------------------------------------------------

    public static boolean yaCalifico(Viaje viaje, int calificadoId) {
        try (PreparedStatement ps = cn().prepareStatement(
                "SELECT 1 FROM ratings WHERE trip_id = ? AND reviewer_id = ? AND reviewed_user_id = ?")) {
            ps.setInt(1, viaje.getId());
            ps.setInt(2, usuarioActual.getId());
            ps.setInt(3, calificadoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            error(e);
            return true;
        }
    }

    public static boolean calificar(Viaje viaje, int calificadoId, int puntaje, String comentario) {
        try {
            transaccion(c -> {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO ratings (trip_id, reviewer_id, reviewed_user_id, score, comment, created_at) VALUES (?,?,?,?,?,?)")) {
                    ps.setInt(1, viaje.getId());
                    ps.setInt(2, usuarioActual.getId());
                    ps.setInt(3, calificadoId);
                    ps.setInt(4, puntaje);
                    ps.setString(5, comentario.isEmpty() ? null : comentario);
                    ps.setString(6, ahora());
                    ps.executeUpdate();
                }
                // Recalcula el promedio del usuario calificado
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE users SET rating = (SELECT FORMAT(AVG(score),1) FROM ratings WHERE reviewed_user_id = ?) WHERE id = ?")) {
                    ps.setInt(1, calificadoId);
                    ps.setInt(2, calificadoId);
                    ps.executeUpdate();
                }
            });
            notificar(calificadoId, "Nueva calificación",
                    "Recibiste " + puntaje + (puntaje == 1 ? " estrella" : " estrellas") + " de "
                    + usuarioActual.getNombre() + ".", "rating");
            return true;
        } catch (SQLException e) {
            error(e);
            return false;
        }
    }

    /**
     * BOTON DE PANICO: guarda un reporte urgente y notifica a los administradores.
     * Devuelve el id del reporte (o -1 si fallo).
     */
    public static int activarPanico(Viaje viaje, String ubicacion) {
        int idReporte = -1;
        String descripcion = "ALERTA SOS activada por " + usuarioActual.getNombre()
                + " (tel " + usuarioActual.getTelefono() + ") en el viaje " + viaje.getRuta()
                + ". Vehículo " + viaje.getVehiculo() + ". " + ubicacion + ". Hora: " + ahora();
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
            ArrayList<Integer> admins = new ArrayList<>();
            try (Statement st = cn().createStatement();
                 ResultSet rs = st.executeQuery("SELECT id FROM users WHERE role = 'admin'")) {
                while (rs.next()) admins.add(rs.getInt(1));
            }
            for (int id : admins) {
                notificar(id, "ALERTA SOS - Reporte #" + idReporte, descripcion, "panic");
            }
        } catch (SQLException e) {
            error(e);
        }
        return idReporte;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        resultadosBusqueda.clear();
        descripcionBusqueda = "";
        viajeSeleccionado = null;
        solicitudEnCurso = null;
        inicioViajes.clear();
    }
}
