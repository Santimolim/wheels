package entidades;

/**
 * Entidad de la tabla vehicles
 *
 * @author prestamo
 */
public class Vehicle {

    private int id;
    private int userId;
    private String brand;
    private String model;
    private String color;
    private String plate;
    private int year;
    private int seats;

    public Vehicle() {
    }

    public Vehicle(int id, int userId, String brand, String model, String color, String plate, int year, int seats) {
        this.id = id;
        this.userId = userId;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.plate = plate;
        this.year = year;
        this.seats = seats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "id=" + id + ", userId=" + userId + ", brand=" + brand + ", model=" + model + ", color=" + color + ", plate=" + plate + ", year=" + year + ", seats=" + seats + '}';
    }
}
