package wheels.modelo;

/** Vehiculo registrado por un estudiante conductor (tabla vehicles). */
public class Vehiculo {

    private int id;
    private String marca;
    private String modelo;
    private String color;
    private String placa;
    private int anio;
    private int puestos;

    public Vehiculo(int id, String marca, String modelo, String color, String placa, int anio, int puestos) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.placa = placa;
        this.anio = anio;
        this.puestos = puestos;
    }

    public int getId() { return id; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public String getPlaca() { return placa; }
    public int getAnio() { return anio; }
    public int getPuestos() { return puestos; }

    public void setId(int id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setColor(String color) { this.color = color; }
    public void setPlaca(String placa) { this.placa = placa; }
    public void setAnio(int anio) { this.anio = anio; }
    public void setPuestos(int puestos) { this.puestos = puestos; }

    /** "Mazda 3 2022" */
    public String getNombre() {
        return marca + " " + modelo + " " + anio;
    }

    @Override
    public String toString() {
        return marca + " " + modelo + " · " + placa;
    }
}
