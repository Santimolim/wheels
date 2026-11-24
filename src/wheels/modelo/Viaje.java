package wheels.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/** Viaje publicado por un estudiante conductor (tabla trips). */
public class Viaje {

    public static final String DISPONIBLE = "available";
    public static final String LLENO      = "full";
    public static final String CANCELADO  = "cancelled";

    private static final Locale ES = Locale.forLanguageTag("es-CO");

    private final int id;
    private final Usuario conductor;
    private final Vehiculo vehiculo;
    private final String origen;
    private final String destino;
    private final String fecha;   // AAAA-MM-DD
    private final String hora;    // HH:MM:SS
    private int puestosDisponibles;
    private final int precio;
    private String estado;        // available, full, cancelled

    public Viaje(int id, Usuario conductor, Vehiculo vehiculo, String origen, String destino,
                 String fecha, String hora, int puestosDisponibles, int precio, String estado) {
        this.id = id;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.hora = hora;
        this.puestosDisponibles = puestosDisponibles;
        this.precio = precio;
        this.estado = estado;
    }

    public int getId() { return id; }
    public Usuario getConductor() { return conductor; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getFecha() { return fecha; }
    public String getEstado() { return estado == null ? DISPONIBLE : estado; }
    public int getPuestosDisponibles() { return puestosDisponibles; }
    public int getPrecio() { return precio; }

    public void setPuestosDisponibles(int p) { this.puestosDisponibles = p; }
    public void setEstado(String estado) { this.estado = estado; }

    public boolean estaCancelado() { return CANCELADO.equals(estado); }

    /** Hora sin segundos: 07:00:00 -> 07:00 */
    public String getHora() {
        if (hora != null && hora.length() == 8) {
            return hora.substring(0, 5);
        }
        return hora;
    }

    public Vehiculo getVehiculo() {
        return vehiculo != null ? vehiculo : conductor.getVehiculo();
    }

    /** Precio formateado para mostrar en pantalla: $6.000 */
    public String getPrecioTexto() {
        return precioTexto(precio);
    }

    public static String precioTexto(int valor) {
        return valor == 0 ? "Gratis" : "$" + String.format("%,d", valor).replace(",", ".");
    }

    public String getRuta() {
        return origen + " → " + destino;
    }

    /** "Hoy", "Mañana" o "mié 25 nov". */
    public String getFechaAmigable() {
        try {
            LocalDate d = LocalDate.parse(fecha);
            LocalDate hoy = LocalDate.now();
            if (d.equals(hoy)) return "Hoy";
            if (d.equals(hoy.plusDays(1))) return "Mañana";
            String dia = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, ES).replace(".", "");
            String txt = dia + " " + d.format(DateTimeFormatter.ofPattern("d MMM", ES)).replace(".", "");
            if (d.getYear() != hoy.getYear()) txt += " " + d.getYear();
            return txt;
        } catch (RuntimeException e) {
            return fecha;
        }
    }

    /** true si la fecha del viaje ya paso. */
    public boolean yaPaso() {
        try {
            return LocalDate.parse(fecha).isBefore(LocalDate.now());
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getRuta() + " (" + fecha + " " + getHora() + ")";
    }
}
