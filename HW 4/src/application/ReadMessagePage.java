package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * The {@code ReadMessagePage} class displays the details of a specific message,
 * including the sender, subject, reference information, and message content.
 * It also provides a button for replying to the message.
 */
public class ReadMessagePage {
    
    /**
     * The {@code DatabaseHelper} instance used for fetching user information.
     */
    private final DatabaseHelper databaseHelper;
    
    /**
     * The {@code Message} to be displayed.
     */
    private final Message message;
    
    /**
     * The {@code User} object representing the sender of the message.
     */
    private User sender;

    /**
     * Constructs a new {@code ReadMessagePage} using the given database helper and message.
     * It attempts to load the sender's user information from the database.
     *
     * @param databaseHelper the database helper used to fetch sender information
     * @param message        the message to be displayed
     */
    public ReadMessagePage(DatabaseHelper databaseHelper, Message message) {
        this.databaseHelper = databaseHelper;
        this.message = message;
        try {
            this.sender = databaseHelper.getUser(message.getSenderID());
        } catch (SQLException e) {
            e.printStackTrace();
            this.sender = null;
        }
    }

    /**
     * Displays the message details on the provided JavaFX stage.
     * <p>
     * The view includes:
     * <ul>
     *   <li>A label showing the sender's username.</li>
     *   <li>A label displaying the message subject.</li>
     *   <li>A label with reference information (if available).</li>
     *   <li>A non-editable, scrollable text area for the message content.</li>
     *   <li>A "Reply" button which opens a new window to reply to the message.</li>
     * </ul>
     *
     * @param stage the JavaFX stage to render the message view on
     */
    public void show(Stage stage) {
        // Sender Label displays the sender's username.
        Label senderLabel = new Label("From: " + (sender != null ? sender.getUsername() : "Unknown"));
        senderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Subject Label displays the message subject.
        Label subjectLabel = new Label("Subject: " + message.getSubject());
        subjectLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");
        
        // Reference Info Label displays reference information if available.
        String refType = message.getReferenceType();
        String refDisplay = "Reference: None";
        if (refType != null && message.getReferenceID() > 0) {
            refDisplay = "Reference: " + refType + message.getReferenceID();
        }
        Label referenceLabel = new Label(refDisplay);
        referenceLabel.setStyle("-fx-font-size: 13px; -fx-font-style: italic; -fx-text-fill: #666;");

        // Message Content Area is a non-editable, scrollable text area.
        TextArea messageTextArea = new TextArea(message.getMessage());
        messageTextArea.setWrapText(true);
        messageTextArea.setEditable(false);
        messageTextArea.setPrefHeight(250);
        messageTextArea.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(messageTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Reply Button opens a new stage to reply to the message.
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold; -fx-padding: 5px 15px;");
        replyButton.setOnAction(event -> {
            if (sender != null) {
                Stage replyStage = new Stage();
                int recipientId = sender.getUserId();
                int referenceId = message.getMessageID();
                String referenceType = "Message";
                new CreateMessagePage(databaseHelper, recipientId, referenceId, referenceType).show(replyStage);
            }
        });

        // Button Box contains the reply button.
        HBox buttonBox = new HBox(replyButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Layout container that arranges all components vertically.
        VBox contentLayout = new VBox(10, senderLabel, subjectLabel, referenceLabel, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Create the scene with the layout and display it.
        Scene scene = new Scene(contentLayout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Message ID: " + message.getMessageID());
        stage.show();
    }
}
