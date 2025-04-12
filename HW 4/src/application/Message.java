package application;

import java.sql.SQLException;
import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;

/**
 * Represents a message exchanged between users in the system.
 * Each message can reference a question, answer, or another message.
 */
public class Message {
    private int messageID;
    private int referenceID;
    private String referenceType;
    private int senderID;
    private int recipientID;
    private String subject;
    private String message;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private User sender;
    private User recipient;
		
    /** Default constructor. */
    public Message() {
    }
    
    /**
     * Constructs a basic message without reference. Used for testing.
     *
     * @param senderID ID of the sender
     * @param recipientID ID of the recipient
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(int senderID, int recipientID, String subject, String message) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
    }
    
    /**
     * Constructs a message and retrieves sender and recipient User objects from the database.
     *
     * @param dbHelper Database helper for fetching user info
     * @param messageID Message ID
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message content
     */
    public Message(DatabaseHelper dbHelper, int messageID, int senderID, int recipientID, String subject, String message) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        try {
            this.sender = dbHelper.getUser(senderID);
            this.recipient = dbHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
            this.recipient = null;
        }
    }
    
    /**
     * Constructs a message with a reference and retrieves sender/recipient User objects.
     *
     * @param dbHelper Database helper for fetching user info
     * @param referenceID Referenced item ID (e.g., message, question, or answer)
     * @param referenceType Type of referenced item ("Message", "Question", "Answer")
     * @param messageID Message ID
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(DatabaseHelper dbHelper, int referenceID, String referenceType, int messageID, int senderID, int recipientID, String subject, String message) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.referenceID = referenceID;
        this.referenceType = referenceType;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        try {
            this.sender = dbHelper.getUser(senderID);
            this.recipient = dbHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
            this.recipient = null;
        }
    }
    
    /**
     * Constructs a message with reference details but without retrieving User objects.
     *
     * @param referenceID Referenced item ID
     * @param referenceType Type of referenced item
     * @param senderID Sender's user ID
     * @param recipientID Recipient's user ID
     * @param subject Subject of the message
     * @param message Message body
     */
    public Message(int referenceID, String referenceType, int senderID, int recipientID, String subject, String message) {
        this.referenceID = referenceID;
        this.referenceType = referenceType;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
    }
    
    /**
     * Constructs a message with full timestamp information.
     *
     * @param messageID Message ID
     * @param referenceID Reference ID
     * @param referenceType Reference type
     * @param senderID Sender ID
     * @param recipientID Recipient ID
     * @param subject Subject
     * @param message Message content
     * @param createdOn Time the message was created
     * @param updatedOn Time the message was last updated
     */
    public Message(int messageID, int referenceID, String referenceType, int senderID, int recipientID, 
                   String subject, String message, LocalDateTime createdOn, LocalDateTime updatedOn) {
        this.messageID = messageID;
        this.referenceID = referenceID;
        this.referenceType = referenceType;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }
	
    // Getters and Setters
    
    /**
     * Gets the unique message ID.
     * @return The unique ID of the message.
     */
    public int getMessageID() {
        return messageID;
    }

    /**
     * Sets the message ID.
     * @param messageID The unique ID to set for the message.
     */
    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    /**
     * Gets the referenced item's ID.
     * @return ID of the referenced item.
     */
    public int getReferenceID() {
        return referenceID;
    }

    /**
     * Sets the referenced item's ID.
     * @param referenceID The ID of the referenced item.
     */
    public void setReferenceID(int referenceID) {
        this.referenceID = referenceID;
    }

    /**
     * Gets the type of the referenced item.
     * @return Type of reference ("Message", "Question", "Answer").
     */
    public String getReferenceType() {
        return referenceType;
    }

    /**
     * Sets the type of the referenced item.
     * @param referenceType Type of reference ("Message", "Question", "Answer").
     */
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    /**
     * Gets the sender's user ID.
     * @return ID of the user who sent the message.
     */
    public int getSenderID() {
        return senderID;
    }

    /**
     * Sets the sender's user ID.
     * @param senderID ID of the user who sent the message.
     */
    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }
    
    /**
     * Gets the sender's user object.
     * @return User object of the sender.
     */
    public User getSender() {
        return sender;
    }

    /**
     * Gets the recipient's user ID.
     * @return ID of the message recipient.
     */
    public int getRecipientID() {
        return recipientID;
    }

    /**
     * Sets the recipient's user ID.
     * @param recipientID ID of the user receiving the message.
     */
    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }

    /**
     * Gets the recipient's user object.
     * @return User object of the recipient.
     */
    public User getRecipient() {
        return recipient;
    }

    /**
     * Gets the subject of the message.
     * @return Subject of the message.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject of the message.
     * @param subject Subject of the message.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the message body.
     * @return Content/body of the message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message body.
     * @param message Content/body of the message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the creation timestamp.
     * @return Timestamp of when the message was created.
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Sets the creation timestamp.
     * @param createdOn The date and time the message was created.
     */
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * Gets the last updated timestamp.
     * @return Timestamp of when the message was last updated.
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Sets the last updated timestamp.
     * @param updatedOn The date and time the message was last updated.
     */
    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    /**
     * Returns a string representation of the message for debugging or display.
     *
     * @return Formatted string representing key message attributes.
     */
    public String toString() {
        return String.format(
            "MESSAGE:\n" +
            "Message ID: %d\n" +
            "Reference ID: %d\n" +
            "Reference Type: %s\n" +
            "Sender ID: %d\n" +
            "Recipient ID: %d\n" +
            "Subject: %s\n" +
            "Message: %s\n",
            messageID, referenceID, referenceType, senderID, recipientID, subject, message
        );
    }
}
