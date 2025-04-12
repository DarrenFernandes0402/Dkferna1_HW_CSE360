package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Page where a user can create and send a new message.
 * <p>
 * This page allows composing a new message with optional reference context
 * to a question, answer, or a previous message.
 * </p>
 */
public class CreateMessagePage {
    
    /**
     * Helper object for database access.
     */
    private final DatabaseHelper databaseHelper;
    
    /**
     * ID of the recipient user.
     */
    private final int recipientID;
    
    /**
     * The recipient {@code User} object fetched from the database.
     */
    private User recipient;
    
    /**
     * Optional ID of the referenced item (e.g., question, answer, message).
     */
    private int referenceID;
    
    /**
     * The type of the referenced item ("Question", "Answer", "Message", etc.).
     */
    private String referenceType;

    /**
     * Constructs a {@code CreateMessagePage} for a specific recipient with optional reference context.
     *
     * @param databaseHelper Helper object for database access
     * @param recipientID    ID of the recipient user
     * @param referenceID    Optional ID of the item being referenced
     * @param referenceType  Type of referenced item ("Question", "Answer", "Message", etc.)
     */
    public CreateMessagePage(DatabaseHelper databaseHelper, int recipientID, Integer referenceID, String referenceType) {
        this.databaseHelper = databaseHelper;
        this.recipientID = recipientID;
        this.referenceID = referenceID;
        this.referenceType = referenceType;

        try {
            this.recipient = databaseHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.recipient = null;
        }
    }

    /**
     * Displays the message composition window on the specified JavaFX stage.
     *
     * @param primaryStage The JavaFX stage to show the user interface on.
     */
    public void show(Stage primaryStage) {
        // Create a label showing the recipient's username.
        Label recipientLabel = new Label("To: " + (recipient != null ? recipient.getUsername() : "Unknown"));
        recipientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create a label to display reference information if available.
        Label referenceLabel;
        if (referenceType != null && referenceID > 0) {
            referenceLabel = new Label("Reference: " + referenceType + referenceID);
            referenceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");
        } else {
            referenceLabel = new Label("Reference: None");
            referenceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
        }

        // Create subject input field.
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter Subject");
        subjectField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        subjectField.setPrefWidth(500);

        // Create message text area.
        TextArea messageField = new TextArea();
        messageField.setPromptText("Type your message here...");
        messageField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        messageField.setPrefHeight(200);
        messageField.setPrefWidth(500);
        messageField.setWrapText(true);

        // Create a scroll pane for the message content.
        ScrollPane scrollPane = new ScrollPane(messageField);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Create the send button.
        Button submitButton = new Button("Send");
        submitButton.setPrefWidth(100);
        submitButton.setPadding(new Insets(5, 15, 5, 15));
        
        // Create the cancel button.
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");
        cancelButton.setPrefWidth(100);
        cancelButton.setPadding(new Insets(5, 15, 5, 15));

        // Group the buttons in a horizontal box.
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Set up event handler for the send button.
        submitButton.setOnAction(a -> {
            String subject = subjectField.getText().trim();
            String messageText = messageField.getText().trim();
            User currentUser = databaseHelper.currentUser;
            int senderID = currentUser.getUserId();

            if (subject.isEmpty() || messageText.isEmpty()) {
                showAlert("Error", "Subject and message cannot be empty.");
                return;
            }

            try {
                Message newMessage = new Message(referenceID, referenceType, senderID, recipientID, subject, messageText);
                databaseHelper.qaHelper.createMessage(newMessage);
                showAlert("Success", "Your message has been sent successfully.");
                primaryStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while sending the message.");
            }
        });

        // Set up event handler for the cancel button.
        cancelButton.setOnAction(e -> primaryStage.close());

        // Create the main layout and add UI components.
        VBox contentLayout = new VBox(10, recipientLabel, referenceLabel, subjectField, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Create and set the scene.
        Scene scene = new Scene(contentLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("New Message to: " + (recipient != null ? recipient.getUsername() : "Unknown"));
        primaryStage.show();
    }
    
    /**
     * Displays an alert dialog to the user.
     *
     * @param title   Title of the alert window.
     * @param message Content text of the alert message.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
