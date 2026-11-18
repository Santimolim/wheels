package wheels.modelo;

/** Solicitud de un puesto hecha por un pasajero sobre un viaje. */
public class Solicitud {

    public static final String PENDIENTE  = "Pendiente";
    public static final String ACEPTADA   = "Aceptada";
    public static final String COMPLETADA = "Completada";

    private int id;
    private Viaje viaje;
    private Usuario pasajero;
    private String estado;

    public Solicitud(int id, Viaje viaje, Usuario pasajero) {
        this.id = id;
        this.viaje = viaje;
        this.pasajero = pasajero;
        this.estado = PENDIENTE;
    }

    public int getId() { return id; }
    public Viaje getViaje() { return viaje; }
    public Usuario getPasajero() { return pasajero; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    /** El conductor acepta: cambia el estado y ocupa un puesto del viaje. */
    public void aceptar() {
        this.estado = ACEPTADA;
        viaje.ocuparPuesto();
    }

    public void completar() {
        this.estado = COMPLETADA;
    }
}
