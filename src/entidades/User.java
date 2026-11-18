package entidades;

/**
 * Entidad de la tabla users
 *
 * @author prestamo
 */
public class User {

    private int id;
    private int universityId;
    private String name;
    private String email;
    private String password;
    private String career;
    private int semester;
    private String phone;
    private String profilePhoto;
    private String role;
    private String rating;
    private String createdAt;

    public User() {
    }

    public User(int id, int universityId, String name, String email, String password, String career, int semester, String phone, String profilePhoto, String role, String rating, String createdAt) {
        this.id = id;
        this.universityId = universityId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.career = career;
        this.semester = semester;
        this.phone = phone;
        this.profilePhoto = profilePhoto;
        this.role = role;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", universityId=" + universityId + ", name=" + name + ", email=" + email + ", password=" + password + ", career=" + career + ", semester=" + semester + ", phone=" + phone + ", profilePhoto=" + profilePhoto + ", role=" + role + ", rating=" + rating + ", createdAt=" + createdAt + '}';
    }
}
