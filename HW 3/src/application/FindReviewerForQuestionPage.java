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

public class FindReviewerForQuestionPage {
	private final DatabaseHelper databaseHelper;
	private Question question;
	private List<Question> questions;
	private User user;
	private List<User> users;

	public FindReviewerForQuestionPage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

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

		// Table display of the question database
		// Create table to display the question database within
		TableView<Question> qTable = new TableView<>();
		// Styling for the table
		// Give qTable a bold outline
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

		if (questions == null || questions.isEmpty()) {
			try {
				questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
			} catch (SQLException e) {
				e.printStackTrace();
				System.err
						.println("Error trying to update question table when initializing FindReviewerForQuestionPage");
				return;
			}
		}

		if (users == null || users.isEmpty()) {
			try {
				users = databaseHelper.getAllReviewersForUser(databaseHelper.currentUser.getUserId()).entrySet()
						.stream().sorted(Map.Entry.<User, Integer>comparingByValue(Comparator.reverseOrder()))
						.map(Map.Entry::getKey).collect(Collectors.toList()).reversed();
			} catch (SQLException e) {
				e.printStackTrace();
				System.err
						.println("Error trying to update reviewer table when initializing FindReviewerForQuestionPage");
				return;
			}
		}

		// Create an observable list of questions and assign to the table
		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
		detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text wrapping
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

		// Add columns to the qTable
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

		// Table display of the users with the role of reviewer
		// Create table to display the reviewers to the user
		TableView<User> rTable = new TableView<>();
		// Styling for the table
		// Give resultsTable a bold outline
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

		// Create an observable list of questions and assign to the table
		ObservableList<User> reviewerObservableList = FXCollections.observableArrayList(users);
		rTable.setItems(reviewerObservableList);

		TableColumn<User, String> reviewerColumn = new TableColumn<>("Reviewers");
		reviewerColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text wrapping
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

		// Add columns to the qTable
		rTable.getColumns().addAll(reviewerColumn);

		// Listener to dynamically hide the title bar of the Question Details table
		rTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = rTable.lookup("TableHeaderRow");
			if (titleBar != null && titleBar.isVisible()) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Create filter button to adjust question table database view
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

			// Check if titleInput is empty or null
			if (qSelection == null || rSelection == null) {
				errorLabel.setText("Error, you must select a QUESTION AND a REVIEWER");
				return;
			} else {

				// Insert logic to assign a question to a reviewer

			}

		});

		// Create filter button to adjust reviewer table view
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
						// Retrive list of all reviewers
						users = databaseHelper.getAllUsersWithRole("Reviewer");
					} else if (selection.equalsIgnoreCase("Preferred")) {
						// Retrieve list of preferred reviewers for the current user (converts from map
						// to list)
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

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {

				// Store newSelection as question
				this.question = newSelection;
			} else {

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

		// Event to minimize the window
		minButton.setOnAction(a -> {
			primaryStage.setIconified(true);
		});

		// Container to hold the three buttons min, max, and close
		HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		buttonBar.setPadding(new Insets(0));
		buttonBar.setMaxHeight(27);
		buttonBar.setMaxWidth(80);

		// Spacer to push the titleBar to the top
		Region spacer = new Region();
		spacer.setMinHeight(26);
		spacer.setMaxHeight(26);

		VBox layoutBox = new VBox(spacer, layout);
		layoutBox.setAlignment(Pos.CENTER);

		// StackPane to control layout sizing
		StackPane root = new StackPane(layoutBox, buttonBar);
		root.setAlignment(buttonBar, Pos.TOP_RIGHT);
		root.setStyle("-fx-background-color: transparent;");
		root.setPadding(new Insets(0));

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

		// Adjust errorLabel downward just a bit
		errorLabel.setTranslateY(22);

		// Set height of table to adjust to container
		qTable.prefHeightProperty().bind(middleBox.heightProperty());
		rTable.prefHeightProperty().bind(middleBox.heightProperty());
		detailsColumn.prefWidthProperty().bind(questionBox.widthProperty().subtract(18));
		reviewerColumn.prefWidthProperty().bind(reviewerBox.widthProperty().subtract(21));

		Scene scene = new Scene(root, 1300, 930);
		scene.setFill(Color.TRANSPARENT);

		middleBox.prefWidthProperty().bind(root.widthProperty());

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.setMaxWidth(Double.MAX_VALUE);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
}
