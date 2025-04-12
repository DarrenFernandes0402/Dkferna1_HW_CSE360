package application;

import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Represents a review submitted by a user for a question or an answer.
 * <p>
 * A Review includes details such as whether it is for a question or not,
 * the related content ID, the review text, author information, creation and
 * update timestamps, message count, and vote count. This class provides multiple
 * constructors to support different initialization scenarios as well as methods to
 * generate formatted display strings.
 * </p>
 * 
 * @author CSE 360 Team 8
 */
public class Review {
    private Integer id;
    private Boolean forQuestion;
    private Integer relatedId;
    private String text;
    private Integer authorId;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private User author;
    private String authorName;
    private Integer messageCount;
    private Integer voteCount;

    /**
     * Constructs a Review with all fields.
     * <p>
     * This constructor is mainly used when retrieving reviews from the database,
     * such as in the getAllQuestions() method in QAHelper.
     * </p>
     *
     * @param id           the review ID
     * @param forQuestion  true if the review is for a question, false otherwise
     * @param relatedId    the ID of the related content (question or answer)
     * @param text         the review text
     * @param authorId     the ID of the review author
     * @param createdOn    the creation timestamp of the review
     * @param updatedOn    the last updated timestamp of the review
     * @param author       the User object representing the author
     * @param authorName   the name of the author
     * @param messageCount the number of messages associated with the review
     * @param voteCount    the current vote count for the review
     */
    public Review(Integer id, Boolean forQuestion, Integer relatedId, String text, Integer authorId, LocalDateTime createdOn,
            LocalDateTime updatedOn, User author, String authorName, Integer messageCount, Integer voteCount) {
        this.id = id;
        this.forQuestion = forQuestion;
        this.relatedId = relatedId;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.author = author;
        this.authorName = authorName;
        this.messageCount = messageCount;
        this.voteCount = voteCount;
    }
    
    /**
     * Constructs a Review with minimal fields for a review that is not yet stored in the database.
     *
     * @param forQuestion true if the review is for a question, false if for an answer
     * @param relatedId   the ID of the related content (question or answer)
     * @param text        the review text
     * @param authorId    the ID of the review author
     * @param author      the User object representing the author
     * @param authorName  the name of the author
     */
    public Review(Boolean forQuestion, Integer relatedId, String text, Integer authorId, User author, String authorName) {
        this.id = null;
        this.forQuestion = forQuestion;
        this.relatedId = relatedId;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = null;
        this.updatedOn = null;
        this.author = author;
        this.authorName = authorName;
    }
    
    /**
     * Constructs a Review with minimal fields without author information.
     *
     * @param forQuestion true if the review is for a question, false if for an answer
     * @param relatedId   the ID of the related content
     * @param text        the review text
     * @param authorId    the ID of the review author
     */
    public Review(Boolean forQuestion, Integer relatedId, String text, Integer authorId) {
        this.id = null;
        this.forQuestion = forQuestion;
        this.relatedId = relatedId;
        this.text = text;
        this.authorId = authorId;
        this.createdOn = null;
        this.updatedOn = null;
    }
    
    /**
     * Default constructor.
     */
    public Review() {
        this.id = null;
        this.forQuestion = null;
        this.relatedId = null;
        this.text = null;
        this.authorId = null;
        this.createdOn = null;
        this.updatedOn = null;
        this.voteCount = null;
    }

    // Getters

    /**
     * Returns the review ID.
     *
     * @return the review ID.
     */
    public Integer getId() {
        return id;
    }
    
    /**
     * Returns the vote count of the review.
     *
     * @return the vote count.
     */
    public Integer getVoteCount() {
        return voteCount;
    }
    
    /**
     * Returns the message count associated with the review.
     *
     * @return the message count.
     */
    public Integer getMessageCount() {
        return messageCount;
    }
    
    /**
     * Returns whether the review is for a question.
     *
     * @return true if the review is for a question, false otherwise.
     */
    public Boolean getForQuestion() {
        return forQuestion;
    }

    /**
     * Returns the review text.
     *
     * @return the review text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the ID of the review author.
     *
     * @return the author ID.
     */
    public Integer getAuthorId() {
        return authorId;
    }

    /**
     * Returns the creation timestamp of the review.
     *
     * @return the creation timestamp.
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Returns the last updated timestamp of the review.
     *
     * @return the last updated timestamp.
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }
    
    /**
     * Returns the author as a User object.
     *
     * @return the review author.
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Returns the author's name.
     *
     * @return the author's name.
     */
    public String getAuthorName() {
        return authorName;
    }
    
    /**
     * Returns the related content ID.
     *
     * @return the related content ID.
     */
    public Integer getRelatedId() {
        return relatedId;
    }
    
    /**
     * Calculates the number of days since the review was created.
     *
     * @return the number of days between the creation timestamp and now, or 0 if creation time is null.
     */
    public int getDaysSinceCreated() {
        if (createdOn == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(createdOn, LocalDateTime.now());
    }
    
    // Setters

    /**
     * Sets the review ID.
     *
     * @param id the review ID.
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /**
     * Sets the vote count.
     *
     * @param voteCount the vote count.
     */
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
    
    /**
     * Sets the message count.
     *
     * @param messageCount the message count.
     */
    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }
    
    /**
     * Sets whether the review is for a question.
     *
     * @param forQuestion true if the review is for a question, false otherwise.
     */
    public void setForQuestion(Boolean forQuestion) {
        this.forQuestion = forQuestion;
    }

    /**
     * Sets the review text.
     *
     * @param text the review text.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Sets the author ID.
     *
     * @param authorId the ID of the review author.
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
     * @param updatedOn the last updated timestamp.
     */
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }
    
    /**
     * Sets the review author.
     *
     * @param author the User object representing the author.
     */
    public void setAuthor(User author) {
        this.author = author;
    }
    
    /**
     * Sets the author's name.
     *
     * @param authorName the author's name.
     */
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    /**
     * Sets the related content ID.
     *
     * @param relatedId the related content ID.
     */
    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
    }

    /**
     * Returns a string representation of the review for debugging purposes.
     *
     * @return a formatted string containing key review details.
     */
    @Override
    public String toString() {
        return String.format(
                "\nQUESTIONID:\n\t%s\nTitle:\n\t%s\nText:\n\t%s\nRelatedIds:\n\t%s\nAuthorId:\n\t%s\nAuthor Name:\n\t%s  \nCreated On:\n\t%s\nUpdated On:\n\t%s",
                id, forQuestion, relatedId, text, authorId, authorName, createdOn, updatedOn);
    }

    /**
     * Returns a formatted display string for the review.
     * <p>
     * The string includes the review text, the message count (appended with "msg"),
     * the author's name, and the number of days since the review was created.
     * </p>
     *
     * @return a display string summarizing the review.
     */
    public String toDisplay() {
        String displayAuthor;
        int daysSinceCreated = getDaysSinceCreated();

        if (author == null) {
            displayAuthor = "User";
        } else {
            displayAuthor = authorName;
        }

        return String.format("%s             %smsg\n%s             	         				      	  %sd", 
                text, messageCount, displayAuthor, daysSinceCreated);
    }
    
    /**
     * Returns a detailed formatted display string for the review.
     * <p>
     * The string includes the review ID, text, rating (with a '+' prefix if positive),
     * the author's name, and the number of days since creation.
     * </p>
     *
     * @return a detailed display string for the review.
     */
    public String toDisplayWithText() {
        String displayAuthor;
        String voteValue;
        int daysSinceCreated = getDaysSinceCreated();
        // Prepend "+" if voteCount is positive.
        if (voteCount <= 0) {
            voteValue = String.valueOf(voteCount);
        } else {
            voteValue = "+" + String.valueOf(voteCount);
        }

        if (author == null) {
            displayAuthor = "User";
        } else {
            displayAuthor = author.getName();
        }

        return String.format("Review Id: %s\n\n%s           					    		Rating: %s\n\n%s           					    			 						%sd", 
                id, text, voteValue, displayAuthor, daysSinceCreated);
    }
}
