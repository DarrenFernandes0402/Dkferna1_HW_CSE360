package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class handles the page where the student can request to become a reviewer
 * @author Darren Fernandes
 */
public class AskToBeAReviewer {
	private final DatabaseHelper databaseHelper;
	public AskToBeAReviewer(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	/**
	 * This is the actual display and button handling of the page, including displaying things like the
	 * box to enter your requests, submitting your request, entering your request in the database, and 
	 * taking you back to the student page
	 * @param primaryStage 	The currently active stage
	 */
	public void show(Stage primaryStage) {
			// Label to display the welcome message for the student
			Label userLabel = new Label("Tell Us why you want to be a reviewer in the box below.");
			userLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");
	
			// Input fields for account info
	
			TextArea request = new TextArea();
			request.setPromptText("Tell us why you want to be a reviewer");
			request.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
					+ "-fx-border-width: 2, 1; -fx-border-radius: 5, 1; -fx-border-inset: 0, 4;");
			request.setMaxWidth(300);
			request.setPrefHeight(100);
			request.setWrapText(true);
	
	
			// Button to register account
			Button setupButton = new Button("Request");
			setupButton.setStyle(
					"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
							+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
			setupButton.setDefaultButton(true);
			
			Button cancel = new Button("Cancel");
			cancel.setStyle(
					"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
							+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
			
			// Label to display error messages
			Label errorLabel = new Label();
			errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
	
			setupButton.setOnAction(a -> {
				
				// Retrieve user input
				String requestA = request.getText();		
	
				if (requestA.isEmpty() || requestA.length() <= 30 || requestA.length()>=500) {
					errorLabel.setText("Enter a reequest with atleast 30 characters and less than 500 characters");
					return;
				}
				errorLabel.setText("");
				try {
					databaseHelper.register(requestA);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				primaryStage.close();
				new StudentHomePage(databaseHelper).show(primaryStage);
				return;
			});
			
			cancel.setOnAction(a-> {
				primaryStage.close();
				new StudentHomePage(databaseHelper).show(primaryStage);
			});
	
			VBox layout = new VBox(10, userLabel, request, setupButton, cancel,
					errorLabel);
			layout.setMinSize(500, 340);
			layout.setMaxSize(500, 340);
			layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
					+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
					+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
			layout.setAlignment(Pos.CENTER);
			
			// StackPane to control layout sizing
				StackPane root = new StackPane(layout);
	
			// Removes icon from title bar in alert window		
			primaryStage.getIcons().clear();
			
			primaryStage.setScene(new Scene(root, 940, 400));
			primaryStage.setTitle("");
			primaryStage.show();
	}
}