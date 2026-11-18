package entidades;

/**
 * Entidad de la tabla trip_preferences
 *
 * @author prestamo
 */
public class TripPreference {

    private int id;
    private int tripId;
    private int musicAllowed;
    private int petsAllowed;
    private int luggageAllowed;
    private int smokingAllowed;
    private String conversationPreference;

    public TripPreference() {
    }

    public TripPreference(int id, int tripId, int musicAllowed, int petsAllowed, int luggageAllowed, int smokingAllowed, String conversationPreference) {
        this.id = id;
        this.tripId = tripId;
        this.musicAllowed = musicAllowed;
        this.petsAllowed = petsAllowed;
        this.luggageAllowed = luggageAllowed;
        this.smokingAllowed = smokingAllowed;
        this.conversationPreference = conversationPreference;
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

    public int getMusicAllowed() {
        return musicAllowed;
    }

    public void setMusicAllowed(int musicAllowed) {
        this.musicAllowed = musicAllowed;
    }

    public int getPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(int petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public int getLuggageAllowed() {
        return luggageAllowed;
    }

    public void setLuggageAllowed(int luggageAllowed) {
        this.luggageAllowed = luggageAllowed;
    }

    public int getSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(int smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public String getConversationPreference() {
        return conversationPreference;
    }

    public void setConversationPreference(String conversationPreference) {
        this.conversationPreference = conversationPreference;
    }

    @Override
    public String toString() {
        return "TripPreference{" + "id=" + id + ", tripId=" + tripId + ", musicAllowed=" + musicAllowed + ", petsAllowed=" + petsAllowed + ", luggageAllowed=" + luggageAllowed + ", smokingAllowed=" + smokingAllowed + ", conversationPreference=" + conversationPreference + '}';
    }
}
