package wheels.modelo;

/** Vehiculo registrado por un estudiante conductor. */
public class Vehiculo {

    private String marca;
    private String modelo;
    private String color;
    private String placa;
    private int puestos;

    public Vehiculo(String marca, String modelo, String color, String placa, int puestos) {
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.placa = placa;
        this.puestos = puestos;
    }

    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public String getPlaca() { return placa; }
    public int getPuestos() { return puestos; }

    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public void setColor(String color) { this.color = color; }
    public void setPlaca(String placa) { this.placa = placa; }
    public void setPuestos(int puestos) { this.puestos = puestos; }

    @Override
    public String toString() {
        return marca + " " + modelo + " - " + placa;
    }
}
