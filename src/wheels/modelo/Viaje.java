package wheels.modelo;

/** Viaje publicado por un estudiante conductor (tabla trips). */
public class Viaje {

    private int id;
    private Usuario conductor;
    private Vehiculo vehiculo;
    private String origen;
    private String destino;
    private String fecha;   // AAAA-MM-DD
    private String hora;    // HH:MM:SS
    private int puestosDisponibles;
    private int precio;
    private String estado;  // available, full, cancelled

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
    public String getEstado() { return estado; }
    public int getPuestosDisponibles() { return puestosDisponibles; }
    public int getPrecio() { return precio; }

    /** Hora sin segundos: 07:00:00 -> 07:00 */
    public String getHora() {
        if (hora != null && hora.length() == 8) {
            return hora.substring(0, 5);
        }
        return hora;
    }

    public void setPuestosDisponibles(int p) { this.puestosDisponibles = p; }

    public Vehiculo getVehiculo() {
        return vehiculo != null ? vehiculo : conductor.getVehiculo();
    }

    /** Precio formateado para mostrar en pantalla. */
    public String getPrecioTexto() {
        return "$" + String.format("%,d", precio).replace(",", ".");
    }

    public String getRuta() {
        return origen + "  ->  " + destino;
    }

    @Override
    public String toString() {
        return getRuta() + " (" + fecha + " " + getHora() + ")";
    }
}
