package entidades;

/**
 * Entidad de la tabla universities
 *
 * @author prestamo
 */
public class University {

    private int id;
    private String name;
    private String createdAt;

    public University() {
    }

    public University(int id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "University{" + "id=" + id + ", name=" + name + ", createdAt=" + createdAt + '}';
    }
}
