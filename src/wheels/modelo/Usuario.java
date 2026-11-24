package wheels.modelo;

/**
 * Representa a un estudiante registrado en Wheels (tabla users).
 * Un mismo usuario puede ser pasajero y conductor.
 * Por seguridad, la contrasena (cifrada) no se guarda en este objeto.
 */
public class Usuario {

    private final int id;
    private String nombre;
    private String email;
    private final int universidadId;
    private String universidad;
    private String carrera;
    private int semestre;
    private String telefono;
    private String rol;
    private String calificacion;
    private Vehiculo vehiculo;   // null si todavia no ha registrado vehiculo

    public Usuario(int id, String nombre, String email, int universidadId, String universidad, String carrera) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.universidadId = universidadId;
        this.universidad = universidad;
        this.carrera = carrera;
        this.semestre = 1;
        this.rol = "student";
        this.calificacion = "5.0";
        this.vehiculo = null;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public int getUniversidadId() { return universidadId; }
    public String getUniversidad() { return universidad; }
    public String getCarrera() { return carrera; }
    public int getSemestre() { return semestre; }
    public String getTelefono() { return telefono == null ? "" : telefono; }
    public String getRol() { return rol; }
    public String getCalificacion() { return calificacion == null || calificacion.isEmpty() ? "5.0" : calificacion; }
    public Vehiculo getVehiculo() { return vehiculo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public void setSemestre(int semestre) { this.semestre = semestre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setRol(String rol) { this.rol = rol; }
    public void setCalificacion(String calificacion) { this.calificacion = calificacion; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    /** Devuelve las iniciales para mostrarlas como foto de perfil. */
    public String getIniciales() {
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2 && !partes[1].isEmpty()) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        return nombre.isEmpty() ? "?" : nombre.substring(0, 1).toUpperCase();
    }

    public String getPrimerNombre() {
        return nombre.trim().split("\\s+")[0];
    }

    public boolean tieneVehiculo() {
        return vehiculo != null;
    }

    public boolean esAdmin() {
        return "admin".equalsIgnoreCase(rol);
    }

    @Override
    public String toString() {
        return nombre;
    }
}
