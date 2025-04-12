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
import javafx.scene.control.ListCell;
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

/**
 * The {@code ReviewerListPage} class displays a page where an administrator
 * can view and manage reviewer assignments.
 * <p>
 * This page includes two tables:
 * <ul>
 *   <li>A table showing users available to become reviewers.</li>
 *   <li>A table showing the current reviewers for the logged-in user along with their assigned weight.</li>
 * </ul>
 * It also provides pop-up dialogs to add a reviewer (assign a weight) or to manage an existing reviewer
 * (reassign weight or remove the reviewer).
 */
public class ReviewerListPage {
    
    /**
     * The {@code DatabaseHelper} used for all database operations.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * The primary stage on which this page is displayed.
     */
    private Stage primaryStage; 

    /**
     * The main layout container for this page.
     */
    private VBox layout; 

    /**
     * A list that holds the current reviewers.
     */
    private List<User> myReviewers;
    
    /**
     * Constructs a new {@code ReviewerListPage} with the specified primary stage
     * and database helper.
     *
     * @param primaryStage the primary stage on which this page is shown.
     * @param databaseHelper the helper used to perform database operations.
     */
    public ReviewerListPage(Stage primaryStage, DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.primaryStage = primaryStage;
        myReviewers = new ArrayList<>();
    }

    /**
     * Displays the Reviewer List Page for the specified user.
     * <p>
     * This method creates two tables:
     * <ul>
     *   <li>The "Current Reviewers" table showing reviewers already assigned to the user.</li>
     *   <li>The "Reviewers Available" table showing users available to become reviewers.</li>
     * </ul>
     * It also includes a back button to return to the home page.
     *
     * @param user the user for whom reviewer information is displayed.
     */
    public void show(User user) {
        TableView<User> reviewerTable = createReviewerTable(user);
        TableView<User> userTable = createUserTable(user);

        Label userLabel = new Label("Reviewers Available");
        userLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        userLabel.setAlignment(Pos.CENTER);
        
        Label reviewerLabel = new Label("Current Reviewers");
        reviewerLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        reviewerLabel.setAlignment(Pos.CENTER);
        
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                + " -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        backButton.setAlignment(Pos.CENTER);
        backButton.setOnAction(a -> {
            // Create new stage to remove transparency if necessary.
            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.TRANSPARENT);
            // Close current stage and navigate to the Student Home Page.
            primaryStage.close();
            new StudentHomePage(databaseHelper).show(newStage);
        });

        HBox hbox = new HBox(5, backButton);
        hbox.setAlignment(Pos.CENTER);
        layout = new VBox(userLabel, userTable, reviewerLabel, reviewerTable);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + " -fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + " -fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");
        
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
        
        // Clear icons from the title bar.
        primaryStage.getIcons().clear();

        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.show();
    }

    /**
     * Displays a pop-up window that allows the administrator to assign a weight to a new reviewer.
     * <p>
     * The pop-up shows the new reviewer's name and email, and provides a dropdown to select a weight.
     * Upon submission, the reviewer is added with the selected weight and the main page is refreshed.
     * </p>
     *
     * @param user the user for whom the reviewer is being added.
     * @param newReviewer the user to be added as a reviewer.
     */
    public void displayAddPopup(User user, User newReviewer) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL); // Block interaction with main window.
        popupStage.setTitle("User Information");

        // Display new reviewer's information.
        Label nameLabel = new Label("Name: " + newReviewer.getName());
        Label emailLabel = new Label("Email: " + newReviewer.getEmail());

        // Dropdown for assigning weight (1-5).
        ComboBox<Integer> weightDropdown = new ComboBox<>();
        weightDropdown.getItems().addAll(1, 2, 3, 4, 5);
        weightDropdown.setValue(1);

        // Submit button to assign weight.
        Button submitButton = new Button("Assign Weight");
        submitButton.setOnAction(e -> {
            int weight = weightDropdown.getValue();
            databaseHelper.addReviewer(user.getUserId(), newReviewer, weight);
            System.out.println("Assigned weight to " + user.getName() + ": " + weight);
            popupStage.close();
            new ReviewerListPage(primaryStage, databaseHelper).show(user);
        });

        VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, submitButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Displays a pop-up window that allows the administrator to manage an existing reviewer.
     * <p>
     * This pop-up allows reassigning the reviewer's weight or deleting the reviewer. It shows the reviewer's
     * name and email, and provides a dropdown for weight reassignment along with buttons for reassigning or deleting.
     * </p>
     *
     * @param user the current user (administrator).
     * @param reviewer the reviewer to manage.
     */
    public void displayManagePopup(User user, User reviewer) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Manage User");

        // Display reviewer information.
        Label nameLabel = new Label("Name: " + reviewer.getName());
        Label emailLabel = new Label("Email: " + reviewer.getEmail());

        // Dropdown for reassigning weight (1-6).
        ComboBox<Integer> weightDropdown = new ComboBox<>();
        weightDropdown.getItems().addAll(1, 2, 3, 4, 5, 6);
        weightDropdown.setValue(1);

        // Button to reassign weight.
        Button reassignButton = new Button("Reassign Weight");
        reassignButton.setOnAction(e -> {
            int weight = weightDropdown.getValue();
            databaseHelper.updateReviewerWeight(user.getUserId(), reviewer, weight);
            System.out.println("Reassigned weight to " + user.getName() + ": " + weight);
            popupStage.close();
            new ReviewerListPage(primaryStage, databaseHelper).show(user);
        });

        // Button to delete the reviewer.
        Button deleteButton = new Button("Delete Reviewer");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Are you sure you want to remove " + user.getName() + " as a reviewer?", 
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

        VBox layout = new VBox(10, nameLabel, emailLabel, weightDropdown, reassignButton, deleteButton);
        layout.setStyle("-fx-padding: 20px; -fx-alignment: center;");
        Scene scene = new Scene(layout, 300, 250);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
    
    /**
     * Creates and returns a TableView displaying users available to be added as reviewers.
     * <p>
     * This table lists users (with the "Reviewer" role) excluding the current user and any
     * users already in the current reviewer's list.
     * </p>
     *
     * @param user the current user.
     * @return a {@code TableView<User>} displaying available reviewers.
     */
    public TableView<User> createUserTable(User user) {
        TableView<User> table = new TableView<>();
        List<User> users = new ArrayList<>();

        try {
            // Retrieve all users with the Reviewer role.
            users = databaseHelper.getAllUsersWithRole("Reviewer");
            if (users.contains(databaseHelper.currentUser)) {
                users.remove(databaseHelper.currentUser);
            }
            // Remove users already added as reviewers.
            for (User u : myReviewers) {
                if (users.contains(u)) {
                    users.remove(u);
                }
            }
        } catch (SQLException e) {
            System.out.println("Should never reach here, can't get all users");
        }
        
        // Set up table columns.
        TableColumn<User, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<User, String> names = new TableColumn<>("Name");
        names.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<User, String> emails = new TableColumn<>("Email");
        emails.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(usernames, names, emails);
        System.out.println("USERS:" + users.toString()); // Debug output.
        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        table.setItems(userObservableList);

        // Add a column with a button to add a reviewer.
        TableColumn<User, Void> addReviewer = new TableColumn<>("Add Reviewer");
        addReviewer.setCellFactory(tc -> new TableCell<>() {
            private final Button button = new Button("Add Reviewer");

            {
                button.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
                button.setOnAction(event -> {
                    User newReviewer = getTableView().getItems().get(getIndex());
                    displayAddPopup(user, newReviewer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
        
        table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");
        table.getColumns().add(addReviewer);
        return table;
    }

    /**
     * Creates and returns a TableView displaying the current reviewers assigned to the user.
     * <p>
     * This table shows the reviewers along with their assigned weight and provides a button for
     * managing each reviewer.
     * </p>
     *
     * @param user the current user.
     * @return a {@code TableView<User>} displaying current reviewers.
     */
    public TableView<User> createReviewerTable(User user) {
        TableView<User> table = new TableView<>();
        Map<User, Integer> reviewersPlaceholder = new HashMap<>();
                
        // Title label for debugging (not added to the table UI).
        Label prompt = new Label("Welcome, Administrator!");
        prompt.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        prompt.setAlignment(Pos.CENTER);

        try {
            reviewersPlaceholder = databaseHelper.getAllReviewersForUser(user.getUserId());
        } catch (SQLException e) {
            System.out.println("Should never reach here, can't get all users" + e.getMessage());
        }
        
        final Map<User, Integer> finalReviewers = reviewersPlaceholder;
        
        // Set up table columns.
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

        // Column for managing a reviewer.
        TableColumn<User, Void> manageReviewer = new TableColumn<>("Manage Reviewer");
        manageReviewer.setCellFactory(tc -> new TableCell<>() {
            private final Button button = new Button("Manage Reviewer");

            {
                button.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
                button.setOnAction(event -> {
                    User newReviewer = getTableView().getItems().get(getIndex());
                    displayManagePopup(user, newReviewer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : button);
            }
        });
        table.getColumns().addAll(usernames, names, emails, weights);
        System.out.println("REVIEWERS:" + finalReviewers.toString()); // Debug output.
        List<User> users = new ArrayList<>(finalReviewers.keySet());
        myReviewers = users;
        ObservableList<User> userObservableList = FXCollections.observableArrayList(users);
        table.setItems(userObservableList);
        table.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 12px;");
        table.getColumns().add(manageReviewer);
        return table;
    }
    
    /**
     * Creates and returns an HBox containing window control buttons.
     * <p>
     * This method creates the "Back to login" button, along with custom window control
     * buttons (close, maximize, and minimize) that support window dragging.
     * </p>
     *
     * @return an {@code HBox} containing the window control buttons.
     */
    public HBox createButtonBar() {
        double[] offsetX = {0};
        double[] offsetY = {0};
        
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

        // Create window control buttons.
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);
        closeButton.setOnMouseEntered(a -> {
            closeButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        closeButton.setOnMouseExited(a -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        closeButton.setOnAction(a -> {
            primaryStage.close();
        });

        Button maxButton = new Button("🗖");
        maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);
        maxButton.setOnMouseEntered(a -> {
            maxButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        maxButton.setOnMouseExited(a -> {
            maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        maxButton.setOnAction(a -> {
            primaryStage.setMaximized(!primaryStage.isMaximized());
        });

        Button minButton = new Button("_");
        minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);
        minButton.setOnMouseEntered(a -> {
            minButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        minButton.setOnMouseExited(a -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        minButton.setOnAction(a -> {
            primaryStage.setIconified(true);
        });

        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);
        return buttonBar;
    }
}
