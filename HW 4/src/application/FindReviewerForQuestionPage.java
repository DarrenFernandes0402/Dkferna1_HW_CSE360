package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;

/**
 * The {@code FindReviewerForQuestionPage} class provides a JavaFX-based user interface
 * for finding and assigning reviewers for questions.
 * <p>
 * This page displays a table of questions (with filtering options for all, unanswered, or answered)
 * and a table of potential reviewers (with filtering options for all or preferred). It allows the user
 * to select a question and a reviewer, then assign the reviewer to the question.
 * </p>
 */
public class FindReviewerForQuestionPage {
    
    /**
     * The DatabaseHelper instance used to perform database operations.
     */
    private final DatabaseHelper databaseHelper;
    
    /**
     * The currently selected question.
     */
    private Question question;
    
    /**
     * The list of questions to display in the question table.
     */
    private List<Question> questions;
    
    /**
     * The currently selected user.
     */
    private User user;
    
    /**
     * The list of reviewer users to display in the reviewer table.
     */
    private List<User> users;

    /**
     * Constructs a {@code FindReviewerForQuestionPage} with the specified DatabaseHelper.
     *
     * @param databaseHelper The DatabaseHelper instance for accessing question and reviewer data.
     */
    public FindReviewerForQuestionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Find Reviewer For Question page on the provided JavaFX Stage.
     * <p>
     * This method sets up the user interface layout including:
     * <ul>
     *   <li>Tables for displaying questions and reviewers.</li>
     *   <li>Filter options for adjusting the view of questions and reviewers.</li>
     *   <li>Buttons for assigning reviewers, closing the window, and window controls (minimize, maximize, close).</li>
     * </ul>
     * The method retrieves questions and reviewers from the database, binds them to the respective tables,
     * and configures event listeners to handle user actions.
     *
     * @param primaryStage The primary JavaFX stage on which the page will be displayed.
     */
    public void show(Stage primaryStage) {
        double[] offsetX = { 0 };
        double[] offsetY = { 0 };

        // Label to display page title to the user
        Label titleLabel = new Label("Find Reviewers For Your Question");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");

        // Button to return to the login screen
        Button quitButton = new Button("Close");
        quitButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");

        // Button to assign selected reviewer to selected question
        Button assignButton = new Button("Assign Reviewer");
        assignButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");

        quitButton.setOnAction(a -> {
            // Close the existing stage
            primaryStage.close();
        });

        // Table to display the question database
        TableView<Question> qTable = new TableView<>();
        // Styling for the table with a bold outline
        qTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");
        qTable.setMinWidth(300);
        qTable.setFixedCellSize(-1);

        qTable.setRowFactory(a -> new TableRow<Question>() {
            @Override
            protected void updateItem(Question item, boolean flag) {
                super.updateItem(item, flag);
                if (flag || item == null) {
                    setStyle("-fx-border-color: transparent;");
                } else {
                    setStyle(
                            "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
                }
            }
        });

        // Retrieve questions from the database if not already loaded
        if (questions == null || questions.isEmpty()) {
            try {
                questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error trying to update question table when initializing FindReviewerForQuestionPage");
                return;
            }
        }

        // Retrieve reviewers from the database if not already loaded
        if (users == null || users.isEmpty()) {
            try {
                users = databaseHelper.getAllReviewersForUser(databaseHelper.currentUser.getUserId()).entrySet()
                        .stream().sorted(Map.Entry.<User, Integer>comparingByValue(Comparator.reverseOrder()))
                        .map(Map.Entry::getKey).collect(Collectors.toList()).reversed();
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error trying to update reviewer table when initializing FindReviewerForQuestionPage");
                return;
            }
        }

        // Create an observable list of questions and assign to the table
        ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
        qTable.setItems(questionObservableList);

        TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
        detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

        // Add cell factory to enable text wrapping in the question details column
        detailsColumn.setCellFactory(a -> new TableCell<Question, String>() {
            private final Label textLabel = new Label();

            {
                textLabel.setWrapText(true);
                textLabel.setMaxWidth(Double.MAX_VALUE);
                textLabel.setStyle("-fx-padding: 5px;");
            }

            @Override
            protected void updateItem(String item, boolean flag) {
                super.updateItem(item, flag);
                setGraphic(flag || item == null ? null : textLabel);
                if (!flag && item != null) {
                    textLabel.setText(item);
                    textLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
                }
            }
        });

        // Add the details column to the questions table
        qTable.getColumns().addAll(detailsColumn);

        // Listener to dynamically hide the title bar of the Question Details table
        qTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node titleBar = qTable.lookup("TableHeaderRow");
            if (titleBar != null && titleBar.isVisible()) {
                titleBar.setVisible(false);
                titleBar.setManaged(false);
                titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
            }
        });

        // Table to display the reviewers
        TableView<User> rTable = new TableView<>();
        // Styling for the table with a bold outline
        rTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");
        rTable.setMinWidth(300);
        rTable.setFixedCellSize(-1);

        rTable.setRowFactory(a -> new TableRow<User>() {
            @Override
            protected void updateItem(User item, boolean flag) {
                super.updateItem(item, flag);
                if (flag || item == null) {
                    setStyle("-fx-border-color: transparent;");
                } else {
                    setStyle(
                            "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
                }
            }
        });

        // Create an observable list of reviewers and assign to the table
        ObservableList<User> reviewerObservableList = FXCollections.observableArrayList(users);
        rTable.setItems(reviewerObservableList);

        TableColumn<User, String> reviewerColumn = new TableColumn<>("Reviewers");
        reviewerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

        // Add cell factory to enable text wrapping in the reviewer column
        reviewerColumn.setCellFactory(a -> new TableCell<User, String>() {
            private final Label textLabel = new Label();

            {
                textLabel.setWrapText(true);
                textLabel.setMaxWidth(Double.MAX_VALUE);
                textLabel.setStyle("-fx-padding: 5px;");
            }

            @Override
            protected void updateItem(String item, boolean flag) {
                super.updateItem(item, flag);
                setGraphic(flag || item == null ? null : textLabel);
                if (!flag && item != null) {
                    textLabel.setText(item);
                    textLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
                }
            }
        });

        // Add the reviewer column to the reviewers table
        rTable.getColumns().addAll(reviewerColumn);

        // Listener to dynamically hide the title bar of the Reviewers table
        rTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node titleBar = rTable.lookup("TableHeaderRow");
            if (titleBar != null && titleBar.isVisible()) {
                titleBar.setVisible(false);
                titleBar.setManaged(false);
                titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
            }
        });

        // Create filter buttons for adjusting the question table view
        ToggleGroup filter = new ToggleGroup();

        RadioButton allButton = new RadioButton("All");
        allButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        allButton.setToggleGroup(filter);

        RadioButton unansweredButton = new RadioButton("Unanswered");
        unansweredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        unansweredButton.setToggleGroup(filter);
        unansweredButton.setSelected(true);

        RadioButton answeredButton = new RadioButton("Answered");
        answeredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        answeredButton.setToggleGroup(filter);

        HBox filterBox = new HBox(10, allButton, unansweredButton, answeredButton);
        filterBox.setAlignment(Pos.CENTER);

        filter.selectedToggleProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                RadioButton selected = (RadioButton) newSelection;
                String selection = selected.getText();

                try {
                    if (selection.equalsIgnoreCase("All")) {
                        questions = databaseHelper.qaHelper.getAllQuestions();
                    } else if (selection.equalsIgnoreCase("Unanswered")) {
                        questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
                    } else if (selection.equalsIgnoreCase("Answered")) {
                        questions = databaseHelper.qaHelper.getAllAnsweredQuestions();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("Error trying to update question table via radio buttons");
                    return;
                }
                questionObservableList.setAll(questions);
                qTable.setItems(questionObservableList);
                qTable.refresh();
            }
        });

        assignButton.setOnAction(a -> {
            Question qSelection = qTable.getSelectionModel().getSelectedItem();
            User rSelection = rTable.getSelectionModel().getSelectedItem();

            // Check if a question and reviewer have been selected
            if (qSelection == null || rSelection == null) {
                errorLabel.setText("Error, you must select a QUESTION AND a REVIEWER");
                return;
            } else {
                // Insert logic to assign a question to a reviewer
            }
        });

        // Create filter buttons for adjusting the reviewer table view
        ToggleGroup reviewerFilter = new ToggleGroup();

        RadioButton reviewerAllButton = new RadioButton("All");
        reviewerAllButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        reviewerAllButton.setToggleGroup(reviewerFilter);

        RadioButton reviewerPreferredButton = new RadioButton("Preferred");
        reviewerPreferredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
        reviewerPreferredButton.setToggleGroup(reviewerFilter);
        reviewerPreferredButton.setSelected(true);

        HBox reviewerFilterBox = new HBox(10, reviewerAllButton, reviewerPreferredButton);
        reviewerFilterBox.setAlignment(Pos.CENTER);

        reviewerFilter.selectedToggleProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                RadioButton selected = (RadioButton) newSelection;
                String selection = selected.getText();

                try {
                    if (selection.equalsIgnoreCase("All")) {
                        // Retrieve list of all reviewers
                        users = databaseHelper.getAllUsersWithRole("Reviewer");
                    } else if (selection.equalsIgnoreCase("Preferred")) {
                        // Retrieve list of preferred reviewers for the current user
                        users = databaseHelper.getAllReviewersForUser(databaseHelper.currentUser.getUserId()).entrySet()
                                .stream().sorted(Map.Entry.<User, Integer>comparingByValue(Comparator.reverseOrder()))
                                .map(Map.Entry::getKey).collect(Collectors.toList()).reversed();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("Error trying to update reviewer table via radio buttons");
                    return;
                }
                reviewerObservableList.setAll(users);
                rTable.setItems(reviewerObservableList);
                rTable.refresh();
            }
        });

        // Set selection to preferred by default
        reviewerFilter.selectToggle(reviewerPreferredButton);

        // Listen for question table selections and store the selected question
        qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.question = newSelection;
            }
        });

        HBox topBox = new HBox(titleLabel);

        VBox buttonBox = new VBox(assignButton);

        VBox questionBox = new VBox(5, filterBox, qTable);

        VBox reviewerBox = new VBox(5, reviewerFilterBox, rTable);

        HBox middleBox = new HBox(200, questionBox, buttonBox, reviewerBox);
        middleBox.setMinSize(1200, 775);
        middleBox.setMaxSize(1200, 775);

        VBox buttonBox2 = new VBox(5, quitButton, errorLabel);

        HBox bottomBox = new HBox(buttonBox2);

        VBox layout = new VBox(5, topBox, middleBox, bottomBox);
        layout.setMinSize(1300, 900);
        layout.setMaxSize(1300, 900);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");

        // Actions to allow window dragging
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });

        // Actions to allow window dragging
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Container for window control buttons (minimize, maximize, close)
        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);

        Button maxButton = new Button("🗖");
        maxButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);

        Button minButton = new Button("_");
        minButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);

        // Configure hover effects and actions for the window control buttons
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

        closeButton.setOnAction(a -> primaryStage.close());

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

        maxButton.setOnAction(a -> primaryStage.setMaximized(!primaryStage.isMaximized()));

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

        // Minimize the window when the minimize button is clicked
        minButton.setOnAction(a -> primaryStage.setIconified(true));

        // Container to hold the window control buttons
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);

        // Spacer to push the title bar to the top
        Region spacer = new Region();
        spacer.setMinHeight(26);
        spacer.setMaxHeight(26);

        VBox layoutBox = new VBox(spacer, layout);
        layoutBox.setAlignment(Pos.CENTER);

        // StackPane to control layout sizing and place the window control buttons
        StackPane root = new StackPane(layoutBox, buttonBar);
        root.setAlignment(buttonBar, Pos.TOP_RIGHT);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));

        // Align UI components
        questionBox.setAlignment(Pos.CENTER_LEFT);
        reviewerBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setAlignment(Pos.CENTER);
        quitButton.setAlignment(Pos.CENTER);
        titleLabel.setAlignment(Pos.CENTER);
        buttonBox2.setAlignment(Pos.CENTER);
        middleBox.setAlignment(Pos.CENTER);
        bottomBox.setAlignment(Pos.CENTER);
        layout.setAlignment(Pos.CENTER);
        topBox.setAlignment(Pos.CENTER);

        // Adjust errorLabel downward slightly
        errorLabel.setTranslateY(22);

        // Bind table heights and column widths to container sizes
        qTable.prefHeightProperty().bind(middleBox.heightProperty());
        rTable.prefHeightProperty().bind(middleBox.heightProperty());
        detailsColumn.prefWidthProperty().bind(questionBox.widthProperty().subtract(18));
        reviewerColumn.prefWidthProperty().bind(reviewerBox.widthProperty().subtract(21));

        Scene scene = new Scene(root, 1300, 930);
        scene.setFill(Color.TRANSPARENT);

        middleBox.prefWidthProperty().bind(root.widthProperty());

        // Set the scene to the primary stage and display the window
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
