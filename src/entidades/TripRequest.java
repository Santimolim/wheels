package entidades;

/**
 * Entidad de la tabla trip_requests
 *
 * @author prestamo
 */
public class TripRequest {

    private int id;
    private int tripId;
    private int passengerId;
    private String status;
    private String requestedAt;

    public TripRequest() {
    }

    public TripRequest(int id, int tripId, int passengerId, String status, String requestedAt) {
        this.id = id;
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.status = status;
        this.requestedAt = requestedAt;
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

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }

    @Override
    public String toString() {
        return "TripRequest{" + "id=" + id + ", tripId=" + tripId + ", passengerId=" + passengerId + ", status=" + status + ", requestedAt=" + requestedAt + '}';
    }
}
