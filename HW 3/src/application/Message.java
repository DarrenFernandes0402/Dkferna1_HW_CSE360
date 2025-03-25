package application;

import java.sql.SQLException;
import java.time.LocalDateTime;

import databasePart1.DatabaseHelper;

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
		
	// Constructors
    public Message() {
    }
    
    public Message(int senderID, int recipientID, String subject, String message) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.subject = subject;
        this.message = message;
    }

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
    
    public Message(int referenceID, String referenceType, int senderID, int recipientID, String subject, String message) {
		this.referenceID = referenceID;
		this.referenceType = referenceType;
		this.senderID = senderID;
		this.recipientID = recipientID;
		this.subject = subject;
		this.message = message;
	}
    
	public Message(int messageID, int referenceID, String referenceType, int senderID, int recipientID, String subject,
			String message) {
		this.messageID = messageID;
		this.referenceID = referenceID;
		this.referenceType = referenceType;
		this.senderID = senderID;
		this.recipientID = recipientID;
		this.subject = subject;
		this.message = message;
	}

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
    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(int referenceID) {
        this.referenceID = referenceID;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }
    
    public User getSender() {
        return sender;
    }

    public int getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(int recipientID) {
        this.recipientID = recipientID;
    }
    
    public User getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String toString() {
        return String.format(
            "MESSAGE:\n" +
            "Message ID: %d\n" +
            "Reference ID: %d\n" +
            "Reference Type: %s\n" +
            "Sender ID: %d\n" +
            "Recipient ID: %d\n" +
            "Subject: %s\n" +
            "Message: %s\n"
//            + "Created On: %s\n" +
//            "Updated On: %s"
            ,
            messageID, referenceID, referenceType, senderID, recipientID, subject, message
//            , createdOn, updatedOn
        );
        }
}
