package wheels.modelo;

/**
 * Representa a un estudiante registrado en Wheels.
 * Un mismo usuario puede ser pasajero y conductor.
 */
public class Usuario {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private String universidad;
    private String carrera;
    private Vehiculo vehiculo;   // null si todavia no ha registrado vehiculo

    public Usuario(int id, String nombre, String email, String password,
                   String universidad, String carrera) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.universidad = universidad;
        this.carrera = carrera;
        this.vehiculo = null;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getUniversidad() { return universidad; }
    public String getCarrera() { return carrera; }
    public Vehiculo getVehiculo() { return vehiculo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    /** Devuelve las iniciales para mostrarlas como foto de perfil. */
    public String getIniciales() {
        String[] partes = nombre.trim().split(" ");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        return nombre.substring(0, 1).toUpperCase();
    }

    public boolean tieneVehiculo() {
        return vehiculo != null;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
