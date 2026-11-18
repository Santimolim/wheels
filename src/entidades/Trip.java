package entidades;

/**
 * Entidad de la tabla trips
 *
 * @author prestamo
 */
public class Trip {

    private int id;
    private int driverId;
    private int vehicleId;
    private String origin;
    private String destination;
    private String departureDate;
    private String departureTime;
    private int availableSeats;
    private int price;
    private String status;
    private String createdAt;

    public Trip() {
    }

    public Trip(int id, int driverId, int vehicleId, String origin, String destination, String departureDate, String departureTime, int availableSeats, int price, String status, String createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Trip{" + "id=" + id + ", driverId=" + driverId + ", vehicleId=" + vehicleId + ", origin=" + origin + ", destination=" + destination + ", departureDate=" + departureDate + ", departureTime=" + departureTime + ", availableSeats=" + availableSeats + ", price=" + price + ", status=" + status + ", createdAt=" + createdAt + '}';
    }
}
