package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ReviewerListPage {
	
	private final DatabaseHelper databaseHelper;

	private Stage primaryStage; 
	private VBox layout; 
	private List<User> myReviewers;
	
	
	
	public ReviewerListPage(Stage primaryStage, DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.primaryStage = primaryStage;
		myReviewers = new ArrayList<>();
	}

	public void show( User user) {
		TableView<User> reviewerTable = createReviewerTable(user);
		
		TableView<User> userTable = createUserTable(user);

		Label userLabel = new Label("Reviewers Available");
		userLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		userLabel.setAlignment(Pos.CENTER);
		
		Label reviewerLabel = new Label("Current Reviewers");
		reviewerLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		reviewerLabel.setAlignment(Pos.CENTER);
		
		Button backButton = new Button("Back");
		backButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
						+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
		backButton.setAlignment(Pos.CENTER);
		backButton.setOnAction(a -> {

			// Create new stage to get rid of transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new StudentHomePage(databaseHelper).show(newStage);
		});

		HBox hbox = new HBox(5, backButton);
		hbox.setAlignment(Pos.CENTER);
		layout = new VBox(userLabel, userTable, reviewerLabel, reviewerTable);
		layout.setAlignment(Pos.CENTER);
		
		layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
				+ "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
				+ "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
		
		HBox buttonBar = createButtonBar();
		
		Region spacer = new Region();
		spacer.setMinHeight(26);
		spacer.setMaxHeight(26);
		
		layout.getChildren().addAll(hbox);
		
		VBox layoutBox = new VBox(spacer, layout);
		layoutBox.setAlignment(Pos.CENTER);
		
		StackPane root = new StackPane(layoutBox, buttonBar);
		root.setAlignment(buttonBar, Pos.TOP_RIGHT);
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0));
		
		
		Scene scene = new Scene(root, 1900, 1000);
		scene.setFill(Color.TRANSPARENT);
		
		// Removes icon from title bar in alert window
		primaryStage.getIcons().clear();

		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.show();

	}

	public void displayAddPopup(User user, User newReviewer) {
	        Stage popupStage = new Stage();
	        popupStage.initModality(Modality.APPLICATION_MODAL); // Blocks interaction with the main window
	        popupStage.setTitle("User Information");

	        // User Information
	        Label nameLabel = new Label("Name: " + newReviewer.getName());
	        Label emailLabel = new Label("Email: " + newReviewer.getEmail());

	        // Dropdown for assigning weight (1-6)
	        ComboBox<Integer> weightDropdown = new ComboBox<>();
	        weightDropdown.getItems().addAll(1, 2, 3, 4, 5);
	        weightDropdown.setValue(1); // Default selection

	        // Submit button
	        Button submitButton = new Button("Assign Weight");
	        submitButton.setOnAction(e -> {
	        	
	            int weight = weightDropdown.getValue();
	            
	            databaseHelper.addReviewer(user.getUserId(), newReviewer, weight);
	            
	            System.out.println("Assigned weight to " + user.getName() + ": " + weight);
	            popupStage.close(); // Close the popup
	            new ReviewerListPage(primaryStage, databaseHelper).show(user);
	        });

	        VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, submitButton);
	        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

	        Scene scene = new Scene(layout, 300, 200);
	        popupStage.setScene(scene);
	        popupStage.showAndWait(); // Blocks execution until popup is closed
	}
	
	
	public void displayManagePopup(User user, User reviewer) {
		Stage popupStage = new Stage();
	    popupStage.initModality(Modality.APPLICATION_MODAL);
	    popupStage.setTitle("Manage User");

	    // User Information Labels
	    Label nameLabel = new Label("Name: " + reviewer.getName());
	    Label emailLabel = new Label("Email: " + reviewer.getEmail());

	    // Dropdown for reassigning weight (1-6)
	    ComboBox<Integer> weightDropdown = new ComboBox<>();
	    weightDropdown.getItems().addAll(1, 2, 3, 4, 5, 6);
	    weightDropdown.setValue(1); // Default selection

	    // Reassign Weight Button
	    Button reassignButton = new Button("Reassign Weight");
	    reassignButton.setOnAction(e -> {
	        int weight = weightDropdown.getValue();
	        databaseHelper.updateReviewerWeight(user.getUserId(), reviewer, weight);
	        System.out.println("Reassigned weight to " + user.getName() + ": " + weight);
	        popupStage.close();
	        new ReviewerListPage(primaryStage, databaseHelper).show(user);
	    });

	    // Delete User Button
	    Button deleteButton = new Button("Delete Reviewer");
	    deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Red delete button for emphasis
	    deleteButton.setOnAction(e -> {
	        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
	                "Are you sure you want to remove " + user.getName() + "as a reviewer?", 
	                ButtonType.YES, ButtonType.NO);
	        confirmation.showAndWait().ifPresent(response -> {
	            if (response == ButtonType.YES) {
	                databaseHelper.removeReviewer(user.getUserId(), reviewer);
	                System.out.println("Removed Reviewer: " + user.getName());
	                popupStage.close();
	                new ReviewerListPage(primaryStage, databaseHelper).show(user);
	            }
	        });
	    });

	    // Layout
	    VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, reassignButton, deleteButton);
	    layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

	    Scene scene = new Scene(layout, 300, 250);
	    popupStage.setScene(scene);
	    popupStage.showAndWait();
    }
	
	public TableView<User> createUserTable(User user) {
		TableView<User> table = new TableView<>();
		List<User> users = new ArrayList<>();

		try {
			
			///// for debugging, use the getAllUsers()
			///// for actual use the getAllUsersWithRole
			
			users = databaseHelper.getAllUsersWithRole("Reviewer");
			//users = databaseHelper.getAllUsers();
			
			if (users.contains(databaseHelper.currentUser)) {
				users.remove(databaseHelper.currentUser);
			}
			
			for (User u : myReviewers) {
				if (users.contains(u)) {
					users.remove(u);
				}
				
			}
			
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

		
		
		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails);

		System.out.println("USERS:" + users.toString()); // debug

		
		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

		
		TableColumn<User, Void> addReviewer = new TableColumn<>("Add Reviewer");
		addReviewer.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Add Reviewer");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User newReviewer = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					displayAddPopup(user, newReviewer);
					
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {  // generic update method to update tableView
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(button);
				}
			}
		});
		
	
		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");

		table.getColumns().add(addReviewer);
		return table;

	}

	public TableView<User> createReviewerTable(User user) {
		TableView<User> table = new TableView<>();
		Map<User, Integer> reviewersPlaceholder = new HashMap<>();
				
		// Label to display title to user
		Label prompt = new Label("Welcome, Administrator!");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
		prompt.setAlignment(Pos.CENTER);

		try {
			reviewersPlaceholder = databaseHelper.getAllReviewersForUser(user.getUserId());
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all users" + e.getMessage());
		}
		
		final Map<User, Integer> finalReviewers = reviewersPlaceholder;
		
		
		// setting up the table columns for both Column name 
		// also setting up where it will get its data from the ObservableList
		// automatically finds the variables based on variable name 
		
		TableColumn<User, String> usernames = new TableColumn<>("Username");
		usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
		

		TableColumn<User, String> names = new TableColumn<>("Name");
		names.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<User, String> emails = new TableColumn<>("Email");
		emails.setCellValueFactory(new PropertyValueFactory<>("email"));

		TableColumn<User, Integer> weights = new TableColumn<>("Weight");
		weights.setCellValueFactory(cellData -> {
            User reviewer = cellData.getValue();
            Integer weight = finalReviewers.getOrDefault(reviewer, 0);            
            return new SimpleObjectProperty<>(weight); 
        });

        TableColumn<User, Void> manageReviewer = new TableColumn<>("Manage Reviewer");
        manageReviewer.setCellFactory(tc -> new TableCell<>() {
			private final Button button = new Button("Manage Reviewer");

			{
				button.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
								+ "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

				button.setOnAction(event -> {
					User newReviewer = getTableView().getItems().get(getIndex());  // getting the row index that the button was pressed on, i.e. the user

					displayManagePopup(user, newReviewer);
					
				});
			}

			@Override
			protected void updateItem(Void item, boolean empty) {  // generic update method to update tableView
				super.updateItem(item, empty);
				if (empty) {
					setGraphic(null);
				} else {
					setGraphic(button);
				}
			}
		});
		// Add columns to the table
		table.getColumns().addAll(usernames, names, emails, weights);

		System.out.println("REVIEWERS:" + finalReviewers.toString()); // debug
		

		List<User> users = new ArrayList<>(finalReviewers.keySet());
		myReviewers = users; 
		
		ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
		table.setItems(userObservableList);

	
		table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");

		table.getColumns().add(manageReviewer);
		return table;

	}
	
	public HBox createButtonBar() {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };
		
		Button quitButton = new Button("Back to login");
		quitButton.setOnAction(event -> {

			new UserLoginPage(databaseHelper).show(primaryStage);
		});
		
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
		return buttonBar;
	}
	
}