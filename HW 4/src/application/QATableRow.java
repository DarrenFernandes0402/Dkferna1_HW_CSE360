package application;

import java.util.List;

/**
 * The {@code QATableRow} class is a wrapper used for dynamically representing
 * rows in a table that displays questions, answers, and reviews.
 * <p>
 * It encapsulates the type of the row, the text content to be displayed,
 * the unique content ID, the author's ID (if applicable), and any related IDs.
 * </p>
 */
public class QATableRow {
    
    /**
     * Enum representing the type of row.
     */
    public enum RowType {
        /**
         * Represents a question row.
         */
        QUESTION, 
        /**
         * Represents an answer row.
         */
        ANSWER, 
        /**
         * Represents a review row.
         */
        REVIEW
    }

    private final RowType type;
    private final String text;
    private final Integer contentId;
    private final Integer authorId;
    private final List<String> relatedId;

    /**
     * Constructs a {@code QATableRow} with a row type, text content, a content ID,
     * and a list of related IDs.
     * <p>
     * This constructor is used when the author ID is not available.
     * </p>
     *
     * @param type      the type of row (QUESTION, ANSWER, or REVIEW)
     * @param text      the text content to display
     * @param contentId the unique content ID (e.g., question, answer, or review ID)
     * @param relatedId a list of related IDs
     */
    public QATableRow(RowType type, String text, Integer contentId, List<String> relatedId) {
        this.type = type;
        this.text = text;
        this.contentId = contentId;
        this.authorId = null;
        this.relatedId = relatedId;
    }

    /**
     * Constructs a {@code QATableRow} with a row type, text content, a content ID,
     * an author ID, and a list of related IDs.
     *
     * @param type      the type of row (QUESTION, ANSWER, or REVIEW)
     * @param text      the text content to display
     * @param contentId the unique content ID (e.g., question, answer, or review ID)
     * @param authorId  the ID of the author of the content
     * @param relatedId a list of related IDs
     */
    public QATableRow(RowType type, String text, Integer contentId, Integer authorId, List<String> relatedId) {
        this.type = type;
        this.text = text;
        this.contentId = contentId;
        this.authorId = authorId;
        this.relatedId = relatedId;
    }

    /**
     * Constructs a {@code QATableRow} with a row type, text content, a content ID,
     * and an author ID. No related IDs are provided.
     *
     * @param type      the type of row (QUESTION, ANSWER, or REVIEW)
     * @param text      the text content to display
     * @param contentId the unique content ID (e.g., question, answer, or review ID)
     * @param authorId  the ID of the author of the content
     */
    public QATableRow(RowType type, String text, Integer contentId, Integer authorId) {
        this.type = type;
        this.text = text;
        this.contentId = contentId;
        this.authorId = authorId;
        this.relatedId = null;
    }

    /**
     * Gets the question ID. In this implementation, the question ID is equivalent
     * to the content ID.
     *
     * @return the question ID.
     */
    public Integer getQuestionId() {
        return contentId;
    }

    /**
     * Gets the answer ID. In this implementation, the answer ID is equivalent
     * to the content ID.
     *
     * @return the answer ID.
     */
    public Integer getAnswerId() {
        return contentId;
    }

    /**
     * Gets the review ID. In this implementation, the review ID is equivalent
     * to the content ID.
     *
     * @return the review ID.
     */
    public Integer getReviewId() {
        return contentId;
    }

    /**
     * Gets the author ID associated with this row.
     *
     * @return the author ID, or null if not provided.
     */
    public Integer getAuthorId() {
        return authorId;
    }
    
    /**
     * Gets the unique content ID associated with this row.
     *
     * @return the content ID.
     */
    public Integer getContentId() {
        return contentId;
    }

    /**
     * Gets the text content of this row.
     *
     * @return the text to display.
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the row type.
     *
     * @return the type of the row (QUESTION, ANSWER, or REVIEW).
     */
    public RowType getType() {
        return type;
    }

    /**
     * Gets the list of related IDs.
     *
     * @return a list of related IDs, or null if none exist.
     */
    public List<String> getRelatedId() {
        return relatedId;
    }

    /**
     * Returns a string representation of the QATableRow.
     *
     * @return the text content of the row.
     */
    @Override
    public String toString() {
        return text;
    }
}
