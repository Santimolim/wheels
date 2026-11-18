package entidades;

/**
 * Entidad de la tabla pickup_points
 *
 * @author prestamo
 */
public class PickupPoint {

    private int id;
    private int tripId;
    private String name;
    private String address;
    private String pickupTime;
    private String createdAt;

    public PickupPoint() {
    }

    public PickupPoint(int id, int tripId, String name, String address, String pickupTime, String createdAt) {
        this.id = id;
        this.tripId = tripId;
        this.name = name;
        this.address = address;
        this.pickupTime = pickupTime;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PickupPoint{" + "id=" + id + ", tripId=" + tripId + ", name=" + name + ", address=" + address + ", pickupTime=" + pickupTime + ", createdAt=" + createdAt + '}';
    }
}
