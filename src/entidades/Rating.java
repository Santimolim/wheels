package entidades;

/**
 * Entidad de la tabla ratings
 *
 * @author prestamo
 */
public class Rating {

    private int id;
    private int tripId;
    private int reviewerId;
    private int reviewedUserId;
    private int score;
    private String comment;
    private String createdAt;

    public Rating() {
    }

    public Rating(int id, int tripId, int reviewerId, int reviewedUserId, int score, String comment, String createdAt) {
        this.id = id;
        this.tripId = tripId;
        this.reviewerId = reviewerId;
        this.reviewedUserId = reviewedUserId;
        this.score = score;
        this.comment = comment;
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

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(int reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Rating{" + "id=" + id + ", tripId=" + tripId + ", reviewerId=" + reviewerId + ", reviewedUserId=" + reviewedUserId + ", score=" + score + ", comment=" + comment + ", createdAt=" + createdAt + '}';
    }
}
