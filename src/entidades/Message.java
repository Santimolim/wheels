package entidades;

/**
 * Entidad de la tabla messages
 *
 * @author prestamo
 */
public class Message {

    private int id;
    private int senderId;
    private int receiverId;
    private int tripId;
    private String message;
    private int isRead;
    private String sentAt;

    public Message() {
    }

    public Message(int id, int senderId, int receiverId, int tripId, String message, int isRead, String sentAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.tripId = tripId;
        this.message = message;
        this.isRead = isRead;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", senderId=" + senderId + ", receiverId=" + receiverId + ", tripId=" + tripId + ", message=" + message + ", isRead=" + isRead + ", sentAt=" + sentAt + '}';
    }
}
