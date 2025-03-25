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

public class CreateMessagePage {
    private final DatabaseHelper databaseHelper;
    private final int recipientID;
    private User recipient;

    public CreateMessagePage(DatabaseHelper databaseHelper, int recipientID) {
        this.databaseHelper = databaseHelper;
        this.recipientID = recipientID;
        try {
            this.recipient = databaseHelper.getUser(recipientID);
        } catch (SQLException e) {
            e.printStackTrace();
            this.recipient = null;
        }
    }

    public void show(Stage primaryStage) {
        // Label showing recipient username
        Label recipientLabel = new Label("To: " + (recipient != null ? recipient.getUsername() : "Unknown"));
        recipientLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Subject Input Field 
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter Subject");
        subjectField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        subjectField.setPrefWidth(500); 

        // Message TextArea
        TextArea messageField = new TextArea();
        messageField.setPromptText("Type your message here...");
        messageField.setStyle("-fx-font-size: 14px; -fx-border-color: gray;");
        messageField.setPrefHeight(200);
        messageField.setPrefWidth(500); 
        messageField.setWrapText(true);

        // ScrollPane for Message Content
        ScrollPane scrollPane = new ScrollPane(messageField);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Send Button
        Button submitButton = new Button("Send");
        submitButton.setPrefWidth(100);
        submitButton.setPadding(new Insets(5, 15, 5, 15));
        
        // Cancel Button
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");
        cancelButton.setPrefWidth(100);
        cancelButton.setPadding(new Insets(5, 15, 5, 15));

        // Button Box 
        HBox buttonBox = new HBox(10, submitButton, cancelButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Event Handlers
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
                Message newMessage = new Message(senderID, recipientID, subject, messageText);
                databaseHelper.qaHelper.createMessage(newMessage);
                showAlert("Success", "Your message has been sent successfully.");
                primaryStage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "An error occurred while sending the message.");
            }
        });

        cancelButton.setOnAction(e -> primaryStage.close());

        // Layout
        VBox contentLayout = new VBox(10, recipientLabel, subjectField, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Scene
        Scene scene = new Scene(contentLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("New Message to: " + (recipient != null ? recipient.getUsername() : "Unknown"));
        primaryStage.show();
    }
    
    // pop up alert showing message sent successfully
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
