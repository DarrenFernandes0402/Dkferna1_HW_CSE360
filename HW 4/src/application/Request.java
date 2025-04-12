package application;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a Request made by a {@link User}, containing the request message,
 * status, approval flags, and optional notes. Requests can be marked by an instructor
 * or admin, closed or reopened, and may include references to prior versions.
 * <p>
 * This class supports the following features:
 * <ul>
 *   <li>Tracking request content and the user who submitted it</li>
 *   <li>Status management: OPEN, CLOSED, REOPENED</li>
 *   <li>Notes appended in a semicolon-delimited format</li>
 *   <li>Flags to track instructor and admin approval</li>
 *   <li>An original request ID to support reopened requests</li>
 * </ul>
 * <p>
 * Use this class when retrieving, displaying, or modifying user-submitted requests
 * in instructor/admin interfaces.
 * </p>
 * 
 * @author CSE 360 Team 8
 */
public class Request {
    private int id;
    private StringProperty request;
    private User user;
    private boolean requestTOF;
    private boolean requestATOF;
    private String notes;
    private String status;
    private int originalId;

    /**
     * Constructs a new Request with default flags and status.
     * This constructor is used when creating a request that is not yet stored in the database.
     *
     * @param request the request message
     * @param user    the user who submitted the request
     */
    public Request(String request, User user) {
        this.id = -1;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = false;
        this.requestATOF = false;
        this.notes = "";
        this.status = "OPEN";
        this.originalId = -1;
    }

    /**
     * Constructs a Request using all fields (used for DB retrieval).
     *
     * @param id           the request ID
     * @param request      the request message
     * @param user         the submitting user
     * @param requestTOF   the instructor approval flag
     * @param requestATOF  the admin approval flag
     * @param notes        semicolon-delimited notes
     * @param status       the current status (e.g., "OPEN", "CLOSED")
     * @param originalId   the original request ID if the request was reopened
     */
    public Request(int id, String request, User user, boolean requestTOF, boolean requestATOF, 
                   String notes, String status, int originalId) {
        this.id = id;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = requestTOF;
        this.requestATOF = requestATOF;
        this.notes = notes;
        this.status = status;
        this.originalId = originalId;
    }

    /**
     * Constructs a Request with approval flags pre-set.
     *
     * @param request     the request message
     * @param user        the user who submitted the request
     * @param requestTOF  the instructor approval flag
     * @param requestATOF the admin approval flag
     */
    public Request(String request, User user, boolean requestTOF, boolean requestATOF) {
        this.id = -1;
        this.request = new SimpleStringProperty(request);
        this.user = user;
        this.requestTOF = requestTOF;
        this.requestATOF = requestATOF;
        this.notes = "";
        this.status = "OPEN";
        this.originalId = -1;
    }

    // Getters and Setters

    /**
     * Returns the unique identifier for this request.
     * 
     * @return the request ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the request text as a JavaFX {@code StringProperty}.
     * 
     * @return the StringProperty representing the request message
     */
    public StringProperty requestProperty() {
        return request;
    }

    /**
     * Returns the request text as a string.
     * 
     * @return the request content
     */
    public String getRequest() {
        return request.get();
    }

    /**
     * Sets the request message.
     * 
     * @param newRequest the updated request content
     */
    public void setRequest(String newRequest) {
        this.request.set(newRequest);
    }

    /**
     * Returns the {@code User} who submitted this request.
     * 
     * @return the user object
     */
    public User getUser() {
        return user;
    }

    /**
     * Returns the username associated with the request.
     * 
     * @return the username, or an empty string if no user is set
     */
    public String getUserName() {
        return (user != null) ? user.getUsername() : "";
    }

    /**
     * Returns whether the instructor has acted on this request.
     * 
     * @return true if the instructor approval flag is set, false otherwise
     */
    public boolean getRequestTOF() {
        return requestTOF;
    }

    /**
     * Sets the instructor approval flag.
     * 
     * @param requestTOF true if the instructor has acted on the request
     */
    public void setRequestTOF(boolean requestTOF) {
        this.requestTOF = requestTOF;
    }

    /**
     * Returns whether the admin has acted on this request.
     * 
     * @return true if the admin approval flag is set, false otherwise
     */
    public boolean getRequestATOF() {
        return requestATOF;
    }

    /**
     * Sets the admin approval flag.
     * 
     * @param requestATOF true if the admin has acted on the request
     */
    public void setRequestATOF(boolean requestATOF) {
        this.requestATOF = requestATOF;
    }

    /**
     * Returns the semicolon-delimited notes associated with this request.
     * 
     * @return the notes string
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes for this request.
     * 
     * @param notes a semicolon-delimited string of notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the current status of the request (e.g., "OPEN", "CLOSED").
     * 
     * @return the status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the request.
     * 
     * @param status the new status string
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns the original request ID if the request was reopened.
     * 
     * @return the original request ID
     */
    public int getOriginalId() {
        return originalId;
    }

    /**
     * Sets the original request ID for reopened requests.
     * 
     * @param originalId the original request ID
     */
    public void setOriginalId(int originalId) {
        this.originalId = originalId;
    }

    /**
     * Appends a note to the existing notes, using a semicolon as a delimiter.
     * 
     * @param note the note to be added
     */
    public void addNote(String note) {
        if (note == null || note.isEmpty()) return;
        if (this.notes == null || this.notes.isEmpty()) {
            this.notes = note;
        } else {
            this.notes += ";" + note;
        }
    }
}
