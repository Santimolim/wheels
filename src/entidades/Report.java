package entidades;

/**
 * Entidad de la tabla reports
 *
 * @author prestamo
 */
public class Report {

    private int id;
    private int reporterId;
    private int reportedUserId;
    private int tripId;
    private String reason;
    private String description;
    private String status;
    private String createdAt;

    public Report() {
    }

    public Report(int id, int reporterId, int reportedUserId, int tripId, String reason, String description, String status, String createdAt) {
        this.id = id;
        this.reporterId = reporterId;
        this.reportedUserId = reportedUserId;
        this.tripId = tripId;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public int getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(int reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return "Report{" + "id=" + id + ", reporterId=" + reporterId + ", reportedUserId=" + reportedUserId + ", tripId=" + tripId + ", reason=" + reason + ", description=" + description + ", status=" + status + ", createdAt=" + createdAt + '}';
    }
}
