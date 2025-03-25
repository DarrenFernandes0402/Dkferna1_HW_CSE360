package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * RoleSelectPage class represents the user interface for the role-selection
 * page. This page displays only if a user has multiple roles and allows the
 * user to select which role they which to play.
 */
public class RoleSelectPage {
	private final DatabaseHelper databaseHelper;
	String selectedRole;
	User user;

	public RoleSelectPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		user = databaseHelper.currentUser;
	}

	/**
	 * Displays the role-selection page. *
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };

		// Create Next and Back buttons
		Button nextButton = new Button("Next");
		nextButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		Button quitButton = new Button("Back");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

		// Set nextButton to default to allow Enter button to activate
		nextButton.setDefaultButton(true);

		// Create combobox for selecting roles
		ComboBox<String> comboBox = new ComboBox<>();

		// Add roles text to comboBox
		comboBox.getItems().addAll(user.getRoles());

		// Set default comboBox text
		comboBox.setPromptText("Please select a role");
		comboBox.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;"
				+ "-fx-prompt-text-fill: black;");

		// ComboBox allows selection of roles and deactivates Next button until
		// selection
		// is made
		comboBox.setOnAction(a -> {
			selectedRole = comboBox.getValue();
			if (selectedRole != null) {
				nextButton.setDisable(false);
			} else {
				nextButton.setDisable(true);
			}
		});

		// Next button to proceed with selection. Is inactive unless selection is made
		// in ComboBox. Based on role selected, go to that role's home page.
		nextButton.setOnAction(a -> {
			// Set currentRole in databaseHelper
			databaseHelper.currentUser.setCurrentRole(selectedRole);

			// Create new stage to get rid of transparency for following pages
			// Stage newStage = new Stage();
			// newStage.initStyle(StageStyle.DECORATED);

			// Direct user to role's home page based on user selection
			switch (selectedRole) {
			case "Admin":
				new AdminHomePage(databaseHelper).show(primaryStage, user);
				break;
			case "Student":
				new StudentHomePage(databaseHelper).show(primaryStage);
				break;
			case "Instructor":
				new InstructorHomePage(databaseHelper).show(primaryStage);
				break;
			case "Staff":
				new StaffHomePage(databaseHelper).show(primaryStage);
				break;
			case "Reviewer":
				new ReviewerHomePage(databaseHelper).show(primaryStage);
				break;
			}

			// Close primaryStage after moving forward
			// primaryStage.close();

		});

		// Quit button to return to User Login Screen
		quitButton.setOnAction(a -> {
			// Call UserLoginPage
			new UserLoginPage(databaseHelper).show(primaryStage);
		});

		// Center the text in the dropdown list of the comboBox
		comboBox.setCellFactory(a -> new ListCell<String>() {
			@Override
			protected void updateItem(String role, boolean flag) {
				super.updateItem(role, flag);

				// If not empty, capitalize first letter and center text
				if (!flag && role != null) {
					setText(role.substring(0, 1).toUpperCase() + role.substring(1));
					setAlignment(Pos.CENTER);
				}
			}
		});

		// Center the text in the comboBox selection
		comboBox.setButtonCell(new ListCell<String>() {
			@Override
			protected void updateItem(String role, boolean flag) {
				super.updateItem(role, flag);

				// If not empty, capitalize first letter and center text
				if (!flag && role != null) {
					setText(role.substring(0, 1).toUpperCase() + role.substring(1));
					setAlignment(Pos.CENTER);
				}
			}
		});

		// Label to display title to user
		Label prompt = new Label("Role-Select");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		// Create layout for buttons and combobox
		VBox layout = new VBox(10);
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

		HBox layoutH = new HBox(10);
		layoutH.setStyle("-fx-padding: 20;");
		layoutH.setAlignment(Pos.CENTER);

		// Attach nextButton and quitButton to the same container
		layoutH.getChildren().addAll(nextButton, quitButton);

		// Attach buttons and combobox to the same container
		layout.getChildren().addAll(prompt, comboBox, layoutH);

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

		titleBar.setOnMousePressed(a -> {
			offsetX[0] = a.getSceneX()/* - primaryStage.getX() */;
			offsetY[0] = a.getSceneY()/* - primaryStage.getY() */;
		});

		titleBar.setOnMouseDragged(a -> {
			primaryStage.setX(a.getScreenX() - offsetX[0]);
			primaryStage.setY(a.getScreenY() - offsetY[0]);
		});

		titleBar.setMinHeight(35);
		titleBar.setMaxHeight(35);

		titleBar.setMaxWidth(Double.MAX_VALUE);

		// Spacer to push the titleBar to the top
		VBox spacer1 = new VBox();
		spacer1.setAlignment(Pos.BOTTOM_CENTER);
		VBox.setVgrow(spacer1, Priority.ALWAYS);

		VBox titleBox = new VBox(titleBar, spacer1);
		titleBox.setAlignment(Pos.CENTER);
		titleBox.setMaxWidth(Double.MAX_VALUE);

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

		// Create scene to hold UI objects
		Scene scene = new Scene(root, 400, 300);
		scene.setFill(Color.TRANSPARENT);

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.setMaxWidth(Double.MAX_VALUE);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
