package application;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a question posted by a user along with its metadata.
 * <p>
 * A Question contains an ID, title, text, author information, creation and update timestamps,
 * a list of components, the ID of the preferred answer, and related IDs. It provides multiple
 * constructors to handle different initialization scenarios, as well as methods to generate
 * display strings for use in the UI.
 * </p>
 */
public class Question {
    private Integer id;
    private String title;
    private String text;
    private Integer authorId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private List<String> comp;
    private int preferredAnswer;
    private User author;
    private String authorName;
    private List<String> relatedId;

    /**
     * Constructs a Question with all details including related IDs.
     *
     * @param id              the unique question ID
     * @param title           the title of the question
     * @param text            the body text of the question
     * @param authorId        the ID of the author
     * @param createdOn       the creation timestamp
     * @param updatedOn       the last updated timestamp
     * @param comp            a list of components associated with the question
     * @param preferredAnswer the ID of the preferred answer
     * @param author          the User object representing the author
     * @param authorName      the name of the author
     * @param relatedId       a list of related IDs
     */
    public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
            LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author, String authorName, List<String> relatedId) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.comp = comp;
        this.preferredAnswer = preferredAnswer;
        this.author = author;
        this.authorName = authorName;
        this.relatedId = relatedId;
    }
    
    /**
     * Constructs a Question without related IDs.
     *
     * @param id              the unique question ID
     * @param title           the title of the question
     * @param text            the body text of the question
     * @param authorId        the ID of the author
     * @param createdOn       the creation timestamp
     * @param updatedOn       the last updated timestamp
     * @param comp            a list of components
     * @param preferredAnswer the ID of the preferred answer
     * @param author          the User object representing the author
     * @param authorName      the name of the author
     */
    public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
            LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author, String authorName) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.comp = comp;
        this.preferredAnswer = preferredAnswer;
        this.author = author;
        this.authorName = authorName;
    }

    /**
     * Constructs a Question without the author name.
     *
     * @param id              the unique question ID
     * @param title           the title of the question
     * @param text            the body text of the question
     * @param authorId        the ID of the author
     * @param createdOn       the creation timestamp
     * @param updatedOn       the last updated timestamp
     * @param comp            a list of components
     * @param preferredAnswer the ID of the preferred answer
     * @param author          the User object representing the author
     */
    public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
            LocalDateTime updatedOn, List<String> comp, int preferredAnswer, User author) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.comp = comp;
        this.preferredAnswer = preferredAnswer;
        this.author = author;
    }

    /**
     * Constructs a Question without components and preferred answer.
     *
     * @param id        the unique question ID
     * @param title     the title of the question
     * @param text      the body text of the question
     * @param authorId  the ID of the author
     * @param createdOn the creation timestamp
     * @param updatedOn the last updated timestamp
     * @param comp      a list of components
     */
    public Question(Integer id, String title, String text, Integer authorId, LocalDateTime createdOn,
            LocalDateTime updatedOn, List<String> comp) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.comp = comp;
    }
    
    /**
     * Constructs a Question with a title, text, and author ID.
     *
     * @param title    the title of the question
     * @param text     the body text of the question
     * @param authorId the ID of the author
     */
    public Question(String title, String text, Integer authorId) {
        this.title = title;
        this.text = text;
        this.authorId = authorId;
    }

    /**
     * Constructs a Question with a title and text.
     *
     * @param title the title of the question
     * @param text  the body text of the question
     */
    public Question(String title, String text) {
        this.title = title;
        this.text = text;
    }

    /**
     * Default constructor for Question.
     */
    public Question() {
        this.id = 0;
        this.title = "";
        this.text = "";
        this.authorId = 0;
        this.createdOn = null;
        this.updatedOn = null;
        this.comp = null;
        this.preferredAnswer = 0;
        this.author = null;
    }

    // Getters

    /**
     * Returns the unique question ID.
     *
     * @return the question ID.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Returns the title of the question.
     *
     * @return the question title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the body text of the question.
     *
     * @return the question text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the ID of the author.
     *
     * @return the author ID.
     */
    public Integer getAuthorId() {
        return authorId;
    }

    /**
     * Returns the creation timestamp of the question.
     *
     * @return the creation timestamp.
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Returns the last updated timestamp of the question.
     *
     * @return the updated timestamp.
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Returns the list of components associated with the question.
     *
     * @return a list of components.
     */
    public List<String> getComp() {
        return comp;
    }

    /**
     * Returns the preferred answer ID.
     *
     * @return the preferred answer ID.
     */
    public int getPreferredAnswer() {
        return preferredAnswer;
    }
    
    /**
     * Returns the author as a User object.
     *
     * @return the author.
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Returns the author's name.
     *
     * @return the author name.
     */
    public String getAuthorName() {
        return authorName;
    }
    
    /**
     * Returns the list of related IDs.
     *
     * @return the list of related IDs.
     */
    public List<String> getRelatedId() {
        return relatedId;
    }
    
    // Setters

    /**
     * Sets the unique question ID.
     *
     * @param id the question ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Sets the title of the question.
     *
     * @param title the question title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the body text of the question.
     *
     * @param text the question text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the author ID.
     *
     * @param authorId the author ID to set.
     */
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdOn the creation timestamp.
     */
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * Sets the last updated timestamp.
     *
     * @param updatedOn the updated timestamp.
     */
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Sets the list of components.
     *
     * @param comp the list of components.
     */
    public void setComp(List<String> comp) {
        this.comp = comp;
    }

    /**
     * Sets the preferred answer ID.
     *
     * @param preferredAnswer the preferred answer ID.
     */
    public void setPreferredAnswer(int preferredAnswer) {
        this.preferredAnswer = preferredAnswer;
    }
    
    /**
     * Sets the author.
     *
     * @param author the User object representing the author.
     */
    public void setAuthor(User author) {
        this.author = author;
    }
    
    /**
     * Sets the author's name.
     *
     * @param authorName the author name.
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    /**
     * Sets the list of related IDs.
     *
     * @param relatedId the list of related IDs.
     */
    public void setRelatedId(List<String> relatedId) {
        this.relatedId = relatedId;
    }

    /**
     * Returns a string representation of the question for debugging purposes.
     *
     * @return a formatted string with question details.
     */
    @Override
    public String toString() {
        return String.format(
                "\nQUESTIONID:\n\t%s\nTitle:\n\t%s\nText:\n\t%s\nRelatedIds:\n\t%s\nAuthorId:\n\t%s\nAuthor Name:\n\t%s  \nCreated On:\n\t%s\nUpdated On:\n\t%s\nPreferred Answer Id:\n\t%s",
                id, title, text, relatedId, authorId, authorName, createdOn, updatedOn, preferredAnswer);
    }

    /**
     * Returns a formatted display string for the question, including the title, author, and days since creation.
     *
     * @return a display string.
     */
    public String toDisplay() {
        String displayAuthor;
        int daysSinceCreated = getDaysSinceCreated();

        // Return empty string if title is empty.
        if (title.equals("")) {
            return "";
        } else {
            // If the author is not set, default to "User".
            if (author == null) {
                displayAuthor = "User";
            } else {
                displayAuthor = authorName;
            }
            return String.format("%s\n%s             	         				      	%sd", title, displayAuthor, daysSinceCreated);
        }
    }

    /**
     * Returns a detailed formatted display string for the question, including the ID, title, text,
     * related IDs, author, and days since creation.
     *
     * @return a detailed display string.
     */
    public String toDisplayWithText() {
        String displayAuthor;
        int daysSinceCreated = getDaysSinceCreated();
        
        if (relatedId == null || relatedId.isEmpty()) {
            relatedId = List.of("");
        }

        if (title.equals("")) {
            return "";
        } else {
            if (author == null) {
                displayAuthor = "User";
            } else {
                displayAuthor = author.getName();
            }
            return String.format("Question Id: %s\n\n%s\n\n%s\n\nRelated Ids: %s\n\n%s           															    %sd", 
                    id, title, text, relatedId, displayAuthor, daysSinceCreated);
        }
    }
    
    /**
     * Calculates the number of days since the question was created.
     *
     * @return the number of days between the creation date and now, or 0 if the creation date is null.
     */
    public int getDaysSinceCreated() {
        if (createdOn == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(createdOn, LocalDateTime.now());
    }
}
