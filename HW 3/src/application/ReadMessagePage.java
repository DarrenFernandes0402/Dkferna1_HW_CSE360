package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ReadMessagePage {
    private final DatabaseHelper databaseHelper;
    private final Message message;
    private User sender;

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

    public void show(Stage stage) {
        // Sender Label
        Label senderLabel = new Label("From: " + (sender != null ? sender.getUsername() : "Unknown"));
        senderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Subject Label
        Label subjectLabel = new Label("Subject: " + message.getSubject());
        subjectLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #555;");

        // Message Content Area
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

        // Reply Button
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold; -fx-padding: 5px 15px;");
        replyButton.setOnAction(event -> {
            if (sender != null) {
                Stage replyStage = new Stage();
                new CreateMessagePage(databaseHelper, sender.getUserId()).show(replyStage);
            }
        });

        // Button Box
        HBox buttonBox = new HBox(replyButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 20, 10, 20));

        // Layout
        VBox contentLayout = new VBox(10, senderLabel, subjectLabel, scrollPane, buttonBox);
        contentLayout.setPadding(new Insets(20));
        contentLayout.setAlignment(Pos.TOP_LEFT);
        contentLayout.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-border-width: 1px;");

        // Scene
        Scene scene = new Scene(contentLayout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Message ID: " + message.getMessageID());
        stage.show();
    }
}
