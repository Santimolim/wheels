package wheels.modelo;

/** Viaje publicado por un estudiante conductor. */
public class Viaje {

    private int id;
    private Usuario conductor;
    private String origen;
    private String destino;
    private String fecha;
    private String hora;
    private int puestosDisponibles;
    private double precio;

    public Viaje(int id, Usuario conductor, String origen, String destino,
                 String fecha, String hora, int puestosDisponibles, double precio) {
        this.id = id;
        this.conductor = conductor;
        this.origen = origen;
        this.destino = destino;
        this.fecha = fecha;
        this.hora = hora;
        this.puestosDisponibles = puestosDisponibles;
        this.precio = precio;
    }

    public int getId() { return id; }
    public Usuario getConductor() { return conductor; }
    public String getOrigen() { return origen; }
    public String getDestino() { return destino; }
    public String getFecha() { return fecha; }
    public String getHora() { return hora; }
    public int getPuestosDisponibles() { return puestosDisponibles; }
    public double getPrecio() { return precio; }

    public void setPuestosDisponibles(int p) { this.puestosDisponibles = p; }

    /** Descuenta un puesto cuando una solicitud es aceptada. */
    public void ocuparPuesto() {
        if (puestosDisponibles > 0) {
            puestosDisponibles--;
        }
    }

    public Vehiculo getVehiculo() {
        return conductor.getVehiculo();
    }

    /** Precio formateado para mostrar en pantalla. */
    public String getPrecioTexto() {
        return "$" + String.format("%,.0f", precio).replace(",", ".");
    }

    public String getRuta() {
        return origen + "  ->  " + destino;
    }

    @Override
    public String toString() {
        return getRuta() + " (" + fecha + " " + hora + ")";
    }
}
