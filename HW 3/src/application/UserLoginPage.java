package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their
 * accounts. It validates the user's credentials and navigates to the
 * appropriate page upon successful login.
 */
public class UserLoginPage {
	private final DatabaseHelper databaseHelper;

	public UserLoginPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	// Method to include direction to the necessary role pages
	private void roleHomePage(User user, Stage newStage) {

		// Check if user has any roles, if so, send to that page
		if (user.getRoles().size() > 0) {
			switch (user.getRoles().get(0).toLowerCase()) {
			case "admin":
				databaseHelper.setUserCurrentRole("admin");
				new AdminHomePage(databaseHelper).show(newStage, user);
				break;

			case "student":
				databaseHelper.setUserCurrentRole("student");
				new StudentHomePage(databaseHelper).show(newStage);
				break;

			case "instructor":
				databaseHelper.setUserCurrentRole("instructor");
				new InstructorHomePage(databaseHelper).show(newStage);
				break;

			case "staff":
				databaseHelper.setUserCurrentRole("staff");
				new StaffHomePage(databaseHelper).show(newStage);
				break;

			case "reviewer":
				databaseHelper.setUserCurrentRole("reviewer");
				new ReviewerHomePage(databaseHelper).show(newStage);
				break;

			default:
				databaseHelper.setUserCurrentRole("user");
				new UserHomePage(databaseHelper).show(newStage);
				break;
			}
			// Else, send them to the UserHomePage
		} else {
			databaseHelper.setUserCurrentRole("user");
			new UserHomePage(databaseHelper).show(newStage);
		}
	}

	public void show(Stage primaryStage) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };

		// Input field for the user's userName, password
		TextField userNameField = new TextField();
		userNameField.setPromptText("Enter Username");
		userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		userNameField.setMaxWidth(200);
		userNameField.setAlignment(Pos.CENTER);

		PasswordField passwordField = new PasswordField();
		passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		passwordField.setPromptText("Enter Password");
		passwordField.setMaxWidth(200);
		passwordField.setAlignment(Pos.CENTER);

		// Label to display title to user
		Label prompt = new Label("Login");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

		// Button to login
		Button loginButton = new Button("Login");
		loginButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Button to register a new user
		Button setupButton = new Button("New User");
		setupButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Set login button as default to allow pressing Enter to activate
		loginButton.setDefaultButton(true);

		loginButton.setOnAction(a -> {
			// Retrieve user inputs
			String userName = userNameField.getText();
			String password = passwordField.getText();

			String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);
			String passwordValidate = PasswordEvaluator.evaluatePassword(password);

			if (!userNameValidate.isEmpty()) {
				errorLabel.setText("Invalid username");
				return;
			}

			if (!passwordValidate.isEmpty()) {
				errorLabel.setText("Invalid password");
				return;
			}

			try {
				User user = databaseHelper.login(userName, password);

				if (user == null) {
					errorLabel.setText("Error loggin in. Contact an Administrator.");
					return;
				}

				if (user.getOTPFlag()) {
					new NewPasswordPage(databaseHelper).show(primaryStage, user);
					return;
				}

				// If user has more than one role, send them to RoleSelectPage otherwise call
				// roleHomePage() method
				if (user.getRoles().size() > 1) {
					new RoleSelectPage(databaseHelper).show(primaryStage);//
					return;
				} else {
					roleHomePage(user, primaryStage);

					return;
				}

			} catch (SQLException e) {
				System.err.println("Database error: " + e.getMessage());
				e.printStackTrace();
			}
		});

		// Button to register a new account
		setupButton.setOnAction(a -> {

			new SetupAccountPage(databaseHelper).show(primaryStage);
		});

		HBox hbox = new HBox(5, loginButton, setupButton);
		hbox.setAlignment(Pos.CENTER);

		// Container to hold UI elements in a nice border
		VBox layout = new VBox(10);
		layout.setMinSize(400, 220);
		layout.setMaxSize(400, 220);
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		layout.getChildren().addAll(prompt, userNameField, passwordField, hbox, errorLabel);
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

		primaryStage.setOnShown(a -> {
			userNameField.requestFocus();
		});

		Scene scene = new Scene(root, 400, 300);
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.setMaxWidth(Double.MAX_VALUE);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
