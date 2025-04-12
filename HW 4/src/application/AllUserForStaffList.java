package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

/**
 * User list provides the administrative user interface for managing users
 * and performing tasks such as banning/unbanning users, getting user's messages and messaging the user.
 *
 * <p>This page displays a table of all users along with controls for performing
 * administrative actions. It utilizes a DatabaseHelper for all database interactions.</p>
 *
 * @author CSE-Online-Team8
 */

public class AllUserForStaffList {
	/**
	 * Displays the User list in the provided PrimaryStage.
	 * 
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	private final DatabaseHelper databaseHelper;
	
	/**
	 * Constructs an User list with the specified DatabaseHelper.
	 *
	 * @param databaseHelper The DatabaseHelper used to interact with the database.
	 */
	public AllUserForStaffList(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * Displays the user list page in the provided primary stage.
	 *This page gets all the users and their roles, as well as all their interactions with anyone
	 *Should the staff member also want to check the peron's messages, they can by clicking a button
	 *or can message a user or ban them.
	 *It works in conjunction with the Create messages page as well as the database helper method, such
	 *as register, add message, get messages from user., etc.
	 * @param primaryStage The primary stage where the scene will be displayed.
	 */
	public void show(Stage primaryStage) {
		TableView<User> table = new TableView<>();
		List<User> users = new ArrayList<>();
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };

		// Label to display title to user
		Label prompt = new Label("Welcome to the User List");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		try {
			users = databaseHelper.getAllUsers();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all users");
		}

		// setting up the table columns for both Column name
		// also setting up where it will get its data from the ObservableList
		// automatically finds the variables based on variable name

		TableColumn<User, String> usernames = new TableColumn<>("Username");
		usernames.setCellValueFactory(new PropertyValueFactory<>("username"));

		TableColumn<User, String> names = new TableColumn<>("Name");
		names.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<User, String> emails = new TableColumn<>("Email");
		emails.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<User, String> roles = new TableColumn<>("Roles");

		roles.setCellValueFactory(cellData -> {
			List<String> listOfRoles = cellData.getValue().getRoles();

			String displayRoles;
			if (listOfRoles == null || listOfRoles.isEmpty()) {
				displayRoles = "";
			} else {
				displayRoles = String.join(", ", listOfRoles);
			}
			return new SimpleStringProperty(displayRoles);
		});

		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails, roles);

		// list that takes the columns cellValueFactory values that correspond to user
		// variables
		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

		TableColumn<User, Void> banColumn = new TableColumn<>("Ban/Unban User");
		banColumn.setCellFactory(tc -> new TableCell<>() {
		    private final Button button = new Button("Ban");

		    {
		    	button.setOnAction(event -> {
		    	    User user = getTableView().getItems().get(getIndex());
		    	    boolean isBanned = databaseHelper.isUserBanned(user.getUsername());

		    	    if (isBanned) {
		    	        if (databaseHelper.unbanUser(user.getUsername())) {
		    	            Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    	            alert.setTitle("User Unbanned");
		    	            alert.setContentText(user.getUsername() + " has been unbanned.");
		    	            alert.showAndWait();
		    	        } else {
		    	            // Inline alert for error
		    	            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
		    	            errorAlert.setTitle("Error");
		    	            errorAlert.setContentText("Failed to unban user.");
		    	            errorAlert.showAndWait();
		    	        }
		    	    } else {
		    	        if (databaseHelper.banUser(user.getUsername())) {
		    	            Alert alert = new Alert(Alert.AlertType.INFORMATION);
		    	            alert.setTitle("User Banned");
		    	            alert.setContentText(user.getUsername() + " has been banned.");
		    	            alert.showAndWait();
		    	        } else {
		    	            // Inline alert for error
		    	            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
		    	            errorAlert.setTitle("Error");
		    	            errorAlert.setContentText("Failed to ban user.");
		    	            errorAlert.showAndWait();
		    	        }
		    	    }
		    	});
		    }

		    @Override
		    protected void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty) {
		            setGraphic(null);
		        } else {
		            User user = getTableView().getItems().get(getIndex());
		            boolean isBanned = databaseHelper.isUserBanned(user.getUsername());
		            button.setText(isBanned ? "Unban" : "Ban");
		            setGraphic(button);
		        }
		    }
		});
		
		TableColumn<User, Void> viewMessage = new TableColumn<>("User's message");
		viewMessage.setCellFactory(tc -> new TableCell<>() {
		    private final Button button = new Button("View Messages");

		    {
		        button.setOnAction(event -> {
		            User user = getTableView().getItems().get(getIndex());
		            new AllMessagesByUser(databaseHelper).show(primaryStage, user);
		        });
		    }

		    @Override
		    protected void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty) {
		            setGraphic(null);
		        } else {
		            setGraphic(button);
		        }
		    }
		});
		
		TableColumn<User, Void> messageUser = new TableColumn<>("Message User");
		messageUser.setCellFactory(tc -> new TableCell<>() {
		    private final Button button = new Button("Message User");

		    {
		        button.setOnAction(event -> {
		            User user = getTableView().getItems().get(getIndex());
		            new CreateMessagePage(databaseHelper, user.getUserId(),1, "Message").show(primaryStage);
		        });
		    }

		    @Override
		    protected void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty) {
		            setGraphic(null);
		        } else {
		            setGraphic(button);
		        }
		    }
		});

		

		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");
		table.getColumns().addAll(banColumn,viewMessage, messageUser);
		HBox header = new HBox(5, prompt);
		header.setAlignment(Pos.CENTER);
		VBox vbox = new VBox(header, table);

		// Container to hold UI elements in a nice border
		VBox layout = new VBox(vbox);
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

		// Spacer to push buttonBar to the far right
		HBox spacer = new HBox(buttonBar);
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox titleBar = new HBox(spacer, buttonBar);

		titleBar.setOnMousePressed(a -> {
			offsetX[0] = a.getSceneX();
			offsetY[0] = a.getSceneY();
		});

		titleBar.setOnMouseDragged(a -> {
			primaryStage.setX(a.getScreenX() - offsetX[0]);
			primaryStage.setY(a.getScreenY() - offsetY[0]);
		});

		titleBar.setMinHeight(35);
		titleBar.setMaxHeight(35);

		titleBar.setMaxWidth(1600);

		// Spacer to push the titleBar to the top
		VBox spacer1 = new VBox();
		spacer1.setAlignment(Pos.BOTTOM_CENTER);
		VBox.setVgrow(spacer1, Priority.ALWAYS);
		spacer1.setMouseTransparent(true);
		spacer1.setVisible(false);
		
		VBox titleBox = new VBox(titleBar, spacer1);
		titleBox.setAlignment(Pos.CENTER);

		// Set position of container within titleBar
		titleBar.setAlignment(Pos.TOP_CENTER);
		spacer.setAlignment(Pos.TOP_LEFT);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		
		// Spacer to push content down a bit for titleBar to be accessible
		VBox layoutSpacer = new VBox();
		layoutSpacer.setMinHeight(50);
		layoutSpacer.setMaxHeight(50);		
		
		VBox layoutContainer = new VBox(titleBar, layout);
		
		layout.setMouseTransparent(false);		

		// StackPane to control layout sizing
		StackPane root = new StackPane(layoutContainer);
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0));
		
		titleBox.setMouseTransparent(true);

		titleBox.prefWidthProperty().bind(layout.widthProperty());
		titleBox.prefHeightProperty().bind(layout.heightProperty());
		table.prefWidthProperty().bind(layout.widthProperty().subtract(200));
		table.prefHeightProperty().bind(layout.heightProperty().subtract(200));
		layout.prefWidthProperty().bind(titleBox.widthProperty().subtract(50));
		layout.prefHeightProperty().bind(root.heightProperty().subtract(50));
		vbox.prefHeightProperty().bind(layout.heightProperty().subtract(100));

		Scene scene = new Scene(root, 1500, 1000);
		scene.setFill(Color.TRANSPARENT);

		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.setMaxWidth(Double.MAX_VALUE);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
