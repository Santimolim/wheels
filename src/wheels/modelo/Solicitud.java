package wheels.modelo;

/** Solicitud de un puesto hecha por un pasajero sobre un viaje (tabla trip_requests). */
public class Solicitud {

    // Texto que se muestra en pantalla
    public static final String PENDIENTE  = "Pendiente";
    public static final String ACEPTADA   = "Aceptada";
    public static final String EN_CURSO   = "En curso";
    public static final String COMPLETADA = "Completada";
    public static final String RECHAZADA  = "Rechazada";
    public static final String CANCELADA  = "Cancelada";

    private final int id;
    private final Viaje viaje;
    private final Usuario pasajero;
    private String estado;
    private final String fechaSolicitud;

    public Solicitud(int id, Viaje viaje, Usuario pasajero, String estado, String fechaSolicitud) {
        this.id = id;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.estado = estado;
        this.fechaSolicitud = fechaSolicitud;
    }

    public int getId() { return id; }
    public Viaje getViaje() { return viaje; }
    public Usuario getPasajero() { return pasajero; }
    public String getEstado() { return estado; }
    public String getFechaSolicitud() { return fechaSolicitud == null ? "" : fechaSolicitud; }

    public void setEstado(String estado) { this.estado = estado; }

    public boolean esActiva() {
        return estado.equals(ACEPTADA) || estado.equals(EN_CURSO);
    }

    /** Convierte el valor guardado en la BD (pending, accepted...) al texto de pantalla. */
    public static String desdeBD(String s) {
        if (s == null) return PENDIENTE;
        switch (s) {
            case "accepted":    return ACEPTADA;
            case "in_progress": return EN_CURSO;
            case "completed":   return COMPLETADA;
            case "rejected":    return RECHAZADA;
            case "cancelled":   return CANCELADA;
            default:            return PENDIENTE;
        }
    }

    /** Convierte el texto de pantalla al valor que se guarda en la BD. */
    public static String haciaBD(String estado) {
        switch (estado) {
            case ACEPTADA:   return "accepted";
            case EN_CURSO:   return "in_progress";
            case COMPLETADA: return "completed";
            case RECHAZADA:  return "rejected";
            case CANCELADA:  return "cancelled";
            default:         return "pending";
        }
    }
}
