package application;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Represents an answer in the system. An Answer object encapsulates
 * details such as its text, author, timestamps, and related IDs.
 */
public class Answer {
    private Integer id;
    private String text;
    private Integer authorId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private User author;
    private String authorName;
    private List<String> relatedId;

    /**
     * Constructs an Answer with all fields.
     *
     * @param id         the unique identifier of the answer
     * @param text       the text of the answer
     * @param authorId   the identifier of the answer's author
     * @param createdOn  the creation timestamp
     * @param updatedOn  the last update timestamp
     * @param author     the User object representing the author
     * @param authorName the name of the author
     * @param relatedId  the list of related IDs
     */
    public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn,
                  User author, String authorName, List<String> relatedId) {
        this.id = id;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.author = author;
        this.authorName = authorName;
        this.relatedId = relatedId;
    }

    /**
     * Constructs an Answer without the relatedId list.
     *
     * @param id         the unique identifier of the answer
     * @param text       the text of the answer
     * @param authorId   the identifier of the answer's author
     * @param createdOn  the creation timestamp
     * @param updatedOn  the last update timestamp
     * @param author     the User object representing the author
     * @param authorName the name of the author
     */
    public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn,
                  User author, String authorName) {
        this(id, text, authorId, createdOn, updatedOn, author, authorName, null);
    }

    /**
     * Constructs an Answer without the author name and relatedId.
     *
     * @param id        the unique identifier of the answer
     * @param text      the text of the answer
     * @param authorId  the identifier of the answer's author
     * @param createdOn the creation timestamp
     * @param updatedOn the last update timestamp
     * @param author    the User object representing the author
     */
    public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn,
                  User author) {
        this(id, text, authorId, createdOn, updatedOn, author, null, null);
    }

    /**
     * Constructs an Answer without author, author name, or relatedId.
     *
     * @param id        the unique identifier of the answer
     * @param text      the text of the answer
     * @param authorId  the identifier of the answer's author
     * @param createdOn the creation timestamp
     * @param updatedOn the last update timestamp
     */
    public Answer(Integer id, String text, Integer authorId, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this(id, text, authorId, createdOn, updatedOn, null, null, null);
    }

    /**
     * Constructs an Answer with only text, authorId, and relatedId.
     *
     * @param text      the text of the answer
     * @param authorId  the identifier of the answer's author
     * @param relatedId the list of related IDs
     */
    public Answer(String text, Integer authorId, List<String> relatedId) {
        this(null, text, authorId, null, null, null, null, relatedId);
    }

    /**
     * Constructs an Answer with only text and authorId.
     *
     * @param text     the text of the answer
     * @param authorId the identifier of the answer's author
     */
    public Answer(String text, Integer authorId) {
        this(text, authorId, null);
    }

    /**
     * Constructs an Answer with only text.
     *
     * @param text the text of the answer
     */
    public Answer(String text) {
        this(text, null, null);
    }

    // Getters

    /**
     * Returns the answer's unique identifier.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the text of the answer.
     *
     * @return the answer text
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the identifier of the author.
     *
     * @return the author's id
     */
    public Integer getAuthorId() {
        return authorId;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Returns the last updated timestamp.
     *
     * @return the update time
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Returns the User object representing the author.
     *
     * @return the author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Calculates the number of days since the answer was created.
     *
     * @return the number of days since creation; returns 0 if createdOn is null
     */
    public int getDaysSinceCreated() {
        if (createdOn == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(createdOn, LocalDateTime.now());
    }

    /**
     * Returns the name of the author.
     *
     * @return the author's name
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Returns the list of related IDs.
     *
     * @return the related IDs
     */
    public List<String> getRelatedId() {
        return relatedId;
    }

    // Setters

    /**
     * Sets the unique identifier of the answer.
     *
     * @param id the new id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the text of the answer.
     *
     * @param text the new text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the identifier of the author.
     *
     * @param authorId the new author id
     */
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdOn the new creation time
     */
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * Sets the last updated timestamp.
     *
     * @param updatedOn the new update time
     */
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Sets the User object representing the author.
     *
     * @param author the new author
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    /**
     * Sets the name of the author.
     *
     * @param authorName the new author name
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    /**
     * Sets the list of related IDs.
     *
     * @param relatedId the new list of related IDs
     */
    public void setRelatedId(List<String> relatedId) {
        this.relatedId = relatedId;
    }

    /**
     * Returns a formatted string representation of the Answer.
     *
     * @return a formatted string with answer details
     */
    public String toString() {
        return String.format(
                "\nANSWERID:\n\t%s\nText:\n\t\t%s\nRelatedIDs:\n\t\t%s\nAuthorId:\n\t  %s\nAuthor Name:\n\t  %s\nCreated On:\n\t%s\nUpdated On:\n\t%s\n",
                id, text, relatedId, authorId, authorName, createdOn, updatedOn);
    }

    /**
     * Returns a display-friendly string for the answer.
     *
     * @return a formatted string suitable for display; if the answer text is empty, returns an empty string.
     */
    public String toDisplay() {
        String displayAuthor;
        int daysSinceCreated = getDaysSinceCreated();

        if (relatedId == null || relatedId.isEmpty()) {
            relatedId = List.of("");
        }

        // If text is empty, return an empty string.
        if (text.equals("")) {
            return "";
        } else {
            // If author is null, default the display to "User".
            if (author == null) {
                displayAuthor = "User";
            } else {
                displayAuthor = getAuthorName();
            }

            return String.format("Answer Id: %s\n\n%s\nRelated Ids: %s\n\n%s            %sd",
                    id, text, relatedId, displayAuthor, daysSinceCreated);
        }
    }

	@Override
	public int hashCode() {
		return Objects.hash(author, createdOn, id, text);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Answer other = (Answer) obj;
		return Objects.equals(author, other.author) && Objects.equals(createdOn, other.createdOn)
				&& Objects.equals(id, other.id) && Objects.equals(text, other.text);
	}
    
    
    
}
