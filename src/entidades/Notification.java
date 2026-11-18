package entidades;

/**
 * Entidad de la tabla notifications
 *
 * @author prestamo
 */
public class Notification {

    private int id;
    private int userId;
    private String title;
    private String message;
    private String type;
    private int isRead;
    private String createdAt;

    public Notification() {
    }

    public Notification(int id, int userId, String title, String message, String type, int isRead, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" + "id=" + id + ", userId=" + userId + ", title=" + title + ", message=" + message + ", type=" + type + ", isRead=" + isRead + ", createdAt=" + createdAt + '}';
    }
}
