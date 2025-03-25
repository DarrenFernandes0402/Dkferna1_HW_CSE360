package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class NewPasswordPage {
	private final DatabaseHelper databaseHelper;

	public NewPasswordPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	public void show(Stage primaryStage, User user) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };
		
		// Input field for the user's userName, password
		PasswordField passwordField = new PasswordField();
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setPromptText("Enter New Password");
		passwordField.setMaxWidth(200);
		passwordField.setAlignment(Pos.CENTER);

		PasswordField passwordValidField = new PasswordField();
		passwordValidField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordValidField.setPromptText("Confirm New Password");
		passwordValidField.setMaxWidth(200);
		passwordValidField.setAlignment(Pos.CENTER);

		// Label to display title to user
		Label prompt = new Label("Password Reset");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Label to display error messages
		Label errorLabel = new Label("");
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

		// Button to save password change
		Button saveChanges = new Button("Save");
		saveChanges.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Set saveChanges button as default to allow pressing Enter to activate
		saveChanges.setDefaultButton(true);

		saveChanges.setOnAction(a -> {
			// Retrieve user inputs
			String password = passwordField.getText();
			String passwordValid = passwordValidField.getText();

			String passwordValidate = PasswordEvaluator.evaluatePassword(password);

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText("***ERROR*** Invalid password");
				return;
			} else {
				if (passwordValid.equals(password)) {

					errorLabel.setText("***Success*** Password Changed");
					databaseHelper.updatePassword(user.getUsername(), password);

					databaseHelper.updateOTPFlag(user.getUsername(), false);

					new UserLoginPage(databaseHelper).show(primaryStage);
					return;
				}
			}
		});

		VBox layout = new VBox(10);
		layout.getChildren().addAll(prompt, passwordField, passwordValidField, saveChanges, errorLabel);
		layout.setMinSize(400, 220);
		layout.setMaxSize(400, 220);
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layout.setAlignment(Pos.CENTER);

		layout.setOnMousePressed(a -> {
			offsetX[0] = a.getSceneX();
			offsetY[0] = a.getSceneY();
		});

		layout.setOnMouseDragged(a -> {
			primaryStage.setX(a.getScreenX() - offsetX[0]);
			primaryStage.setY(a.getScreenY() - offsetY[0]);
		});

		// Container to hold the buttons and allow for click+drag
		// Button to replace X close button for transparent background
		Button closeButton = new Button("X");
		closeButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		closeButton.setMinSize(25, 25);
		closeButton.setMaxSize(25, 25);

		// Button to replace maximize button for transparent background
		Button maxButton = new Button("🗖");
		maxButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		maxButton.setMinSize(25, 25);
		maxButton.setMaxSize(25, 25);

		// Button to replace minimize button for transparent background
		Button minButton = new Button("_");
		minButton.setStyle(
				"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
						+ "-fx-font-weight: bold; -fx-padding: 0;");
		minButton.setMinSize(25, 25);
		minButton.setMaxSize(25, 25);

		// Set onAction events for button
		closeButton.setOnMouseEntered(a -> {
			closeButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});

		closeButton.setOnMouseExited(a -> {
			closeButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			closeButton.setMinSize(25, 25);
			closeButton.setMaxSize(25, 25);
		});

		closeButton.setOnAction(a -> {
			primaryStage.close();
		});

		// Set onAction events for button
		maxButton.setOnMouseEntered(a -> {
			maxButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});

		maxButton.setOnMouseExited(a -> {
			maxButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			maxButton.setMinSize(25, 25);
			maxButton.setMaxSize(25, 25);
		});

		maxButton.setOnAction(a -> {
			primaryStage.setMaximized(!primaryStage.isMaximized());
		});

		// Set onAction events for button
		minButton.setOnMouseEntered(a -> {
			minButton.setStyle(
					"-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: red; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});

		minButton.setOnMouseExited(a -> {
			minButton.setStyle(
					"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
							+ "-fx-font-weight: bold; -fx-padding: 0;");
			minButton.setMinSize(25, 25);
			minButton.setMaxSize(25, 25);
		});

		minButton.setOnAction(a -> {
			primaryStage.setIconified(true);
		});

		// Container to hold the three buttons min, max, and close
		HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		buttonBar.setPadding(new Insets(0));
		buttonBar.setMaxHeight(27);		
		buttonBar.setMaxWidth(80);
		
		// Spacer to push buttonBar to the far right
		HBox spacer = new HBox(buttonBar);
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox titleBar = new HBox(spacer, buttonBar);			

		titleBar.setMinHeight(35);
		titleBar.setMaxHeight(35);
		
		titleBar.setMaxWidth(600);	
		
		// Spacer to push the titleBar to the top
		VBox spacer1 = new VBox();
		spacer1.setAlignment(Pos.BOTTOM_CENTER);
		VBox.setVgrow(spacer1, Priority.ALWAYS);
		
		VBox titleBox = new VBox(titleBar, spacer1);
		titleBox.setAlignment(Pos.CENTER);

		// Set position of container within titleBar
		titleBar.setAlignment(Pos.TOP_CENTER);
		spacer.setAlignment(Pos.TOP_LEFT);
		buttonBar.setAlignment(Pos.TOP_RIGHT);

		// StackPane to control layout sizing
		StackPane root = new StackPane(titleBox, layout);
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0));
		
		titleBox.prefWidthProperty().bind(root.widthProperty());
		titleBox.prefHeightProperty().bind(root.heightProperty());

		// Removes icon from title bar in alert window		
		primaryStage.getIcons().clear();
		
		Scene scene = new Scene(root, 940, 400);
		scene.setFill(Color.TRANSPARENT);
		
		primaryStage.setScene(scene);		
		primaryStage.setTitle("");
		primaryStage.setMaxWidth(Double.MAX_VALUE);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
