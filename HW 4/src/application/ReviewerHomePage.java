package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import application.QATableRow;
import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * ReviewerHomePage provides the user interface for reviewers.
 *
 * <p>This page allows reviewers to view questions that have been reviewed by them,
 * submit new reviews, update or delete existing reviews, and interact with questions
 * by marking answers as read or messaging users. It also supports filtering and searching
 * through questions and reviews.</p>
 */

public class ReviewerHomePage {

	private final DatabaseHelper databaseHelper;
	private Question question;
	private Answer answer;
	private Review review;
	private List<Review> reviews;
	private List<Question> questions;
	private List<Answer> answers;
	private ObservableList<QATableRow> resultsObservableList = FXCollections.observableArrayList();
	private TableView<Question> qTable;
	private List<Question> sortedList;
	private boolean updatingReview = false;
	private boolean reviewToggle = false;
	private String reviewFilterString = "";
	private Answer tempAnswer;
	ObservableList<Review> reviewObservableList;
	private TableView<QATableRow> resultsTable;
	TableView<Review> rTable;
	private int indent = 1;
	
	/**
	 * Constructs a ReviewerHomePage instance using the provided DatabaseHelper.
	 *
	 * @param databaseHelper the helper object used for database operations.
	 */

	public ReviewerHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * Displays the reviewer home page on the specified stage.
	 *
	 * <p>This method initializes the UI components, retrieves questions, answers,
	 * and reviews from the database, and sets up event handlers for user interactions
	 * including review submission, filtering, and navigation.</p>
	 *
	 * @param primaryStage the stage on which the reviewer home page is displayed.
	 */

	public void show(Stage primaryStage) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };
		try {
			questions = databaseHelper.qaHelper.getAllReviewedByMeQuestions();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all QUESTIONS");
			e.printStackTrace();
		}

		try {
			answers = databaseHelper.qaHelper.getAllAnswers();
		} catch (SQLException e) {
			System.out.println("Should never reach here, can't get all ANSWERS");
			e.printStackTrace();
		}

		// Label to display title of the page to the user
		Label topLabel = new Label("Reviewer Home Page");
		topLabel.setStyle(
				"-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: derive(gray, -20%)");

		VBox topLabelBox = new VBox(topLabel);
		topLabelBox.setStyle(" -fx-background-color: derive(gray, -20%)");

		// Label to display title of area to the user
		Label prompt = new Label("Entry Box");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Input box for body of question
		TextArea inputField = new TextArea();
		inputField.setPromptText("Enter your review..");
		// Styling for the inputField
		inputField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		inputField.setMaxWidth(600);
		inputField.setPrefWidth(600);
		inputField.setMaxHeight(1000);
		inputField.setPrefHeight(1000);
		inputField.setWrapText(true);

		// Button to submit question text in input fields to database
		Button submitButton = new Button("Submit Review");
		submitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to view the rTable
		Button viewReviewsButton = new Button("View Reviews");
		viewReviewsButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to return to the login screen
		Button quitButton = new Button("Back to login");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to open inbox for private messages, also displays the number of messages a user has in their inbox
		int totalMessages = 0;
		try {
		    totalMessages = databaseHelper.qaHelper.getTotalMessageCountForUser(databaseHelper.currentUser.getUserId());
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		Button inboxButton = new Button("Inbox (" + totalMessages + ")");
		inboxButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to open the ui to submit a new question
		Button newReviewButton = new Button("Review");
		newReviewButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
		newReviewButton.setMinWidth(20);
		;

		// Button to open the ui to submit a new question
		Button reviewCloseButton = new Button("Close");
		reviewCloseButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Table display of the question database
		// Create table to display the question database within
		TableView<Question> qTable = new TableView<>();

		// Give qTable a bold outline
		qTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

		// Styling for the table
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
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px; -fx-table-cell-border-color: black;");
				}
			}
		});


		
		// Create an observable list of questions and assign to the table
		ObservableList<Question> questionObservableList = FXCollections.observableArrayList(questions);
		qTable.setItems(questionObservableList);

		TableColumn<Question, String> detailsColumn = new TableColumn<>("Question Details");
		detailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		detailsColumn.setCellFactory(a -> new TableCell<Question, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(false);
				textLabel.setEllipsisString("...");
				textLabel.setMaxWidth(Double.MAX_VALUE);
				textLabel.setStyle("-fx-text-overrun: ellipsis; -fx-padding: 2px;");
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

		// Label to display title to user
		Label prompt5 = new Label("Details");
		prompt5.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Hbox to position the title
		HBox titleBox5 = new HBox(prompt5);
		titleBox5.setAlignment(Pos.CENTER);

		resultsTable = new TableView<>();
		resultsTable.setItems(resultsObservableList);
		resultsTable.setRowFactory(a -> new TableRow<QATableRow>() {
			@Override
			protected void updateItem(QATableRow item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		// Table display of the question database
		// Create table to display the question database within
		rTable = new TableView<>();

		// Give rTable a bold outline
		rTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

		// Styling for the table
		rTable.setMinWidth(300);
		rTable.setFixedCellSize(-1);

		rTable.setRowFactory(a -> new TableRow<Review>() {
			@Override
			protected void updateItem(Review item, boolean flag) {
				super.updateItem(item, flag);
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px; -fx-table-cell-border-color: black;");
				}
			}
		});
		// Retrieve reviews from the database matching the current user as the author
		try {
			reviews = databaseHelper.qaHelper.getMyReviews();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Create an observable list of reviews and assign to the table
		reviewObservableList = FXCollections.observableArrayList(reviews);
		rTable.setItems(reviewObservableList);

		TableColumn<Review, String> reviewDetailsColumn = new TableColumn<>("Review Details");
		reviewDetailsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplay()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		reviewDetailsColumn.setCellFactory(a -> new TableCell<Review, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(false);
				textLabel.setEllipsisString("...");
				textLabel.setMaxWidth(Double.MAX_VALUE);
				textLabel.setStyle("-fx-text-overrun: ellipsis; -fx-padding: 2px;");
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
		rTable.getColumns().addAll(reviewDetailsColumn);

		// Listener to dynamically hide the title bar of the Question Details table
		rTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = rTable.lookup("TableHeaderRow");
			if (titleBar != null && titleBar.isVisible()) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});
		

		// Create filter comboBox to adjust review table database view
		ComboBox<String> reviewFilter = new ComboBox<>();
		reviewFilter.getItems().addAll("All Reviews", "My Reviews");
		reviewFilter.setValue("My Reviews");

		// Styling for comboBox
		reviewFilter.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		HBox reviewFilterBox = new HBox(10, reviewFilter);
		reviewFilterBox.setAlignment(Pos.CENTER_RIGHT);

		reviewFilter.valueProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				try {
					switch (newSelection) {
					case "All Reviews":
						reviews = databaseHelper.qaHelper.getAllReviews();
						reviewFilterString = "All Reviews";
						break;
					case "My Reviews":
						reviews = databaseHelper.qaHelper.getMyReviews();
						reviewFilterString = "My Reviews";
						break;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error trying to update review table via radio buttons");
					return;
				}
				reviewObservableList.setAll(reviews);
				rTable.setItems(reviewObservableList);
				rTable.refresh();
			}
		});

		primaryStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
			if (isNowFocused) {
				try {		    	
					if (reviewFilterString == "All Reviews") {
						reviews = databaseHelper.qaHelper.getAllReviews();
					}
					else if (reviewFilterString == "My Reviews") {
						reviews = databaseHelper.qaHelper.getMyReviews();
					}
					else {
						return;
					}
					reviewObservableList.setAll(reviews);
					rTable.setItems(reviewObservableList);
					rTable.refresh();
					System.out.println("Stage is now topmost / regained focus");
				} catch (SQLException e) {
					System.out.println("FOCUSED LISTENER FAIL");
				}
			} else {
				System.out.println("Stage lost focus");
			}
		});
		
		
		// Hbox to hold and position the title
		HBox reviewTitleBox = new HBox(reviewFilterBox);
		reviewTitleBox.setStyle("-fx-background-color: derive(gray, 60%)");
		// Set alignment of box
		reviewCloseButton.setAlignment(Pos.TOP_RIGHT);
		submitButton.setAlignment(Pos.TOP_LEFT);

		// Container to hold the two input boxes for questions and their title together
		VBox rTableVBox = new VBox(5, rTable);
		rTableVBox.setStyle("-fx-background-color: derive(gray, 60%)");

		VBox rTableBox = new VBox(5, reviewTitleBox, rTableVBox);
		rTableBox.setStyle("-fx-background-color: derive(gray, 60%)");

		// Hbox to hold and position the title
		HBox topTitleBox = new HBox(135, submitButton, reviewCloseButton);
		// Set alignment of box
		reviewCloseButton.setAlignment(Pos.TOP_RIGHT);
		submitButton.setAlignment(Pos.TOP_LEFT);

		// Container to hold the input boxes for reviews
		VBox reviewInputBox = new VBox(5, topTitleBox, inputField);
		reviewInputBox.setStyle("-fx-background-color: derive(gray, 60%)");

		VBox submitBox = new VBox(reviewInputBox);

		TableColumn<QATableRow, String> contentColumn = new TableColumn<>("Results");
		contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		contentColumn.setCellFactory(a -> new TableCell<QATableRow, String>() {
			private final Button MessageButton = new Button("Message User");
			private final Button markAsReadButton = new Button("Mark As Read");
			HBox buttonBox = new HBox(10, MessageButton);
			private final VBox cellContent = new VBox(5);
			private final HBox cellBox = new HBox();

			{
				MessageButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

				markAsReadButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

				MessageButton.setOnAction(a -> {
					Stage newStage = new Stage();
					QATableRow row = getTableView().getItems().get(getIndex());

					int recipientId = row.getAuthorId();
					int referenceId = 0;
					String referenceType = "";

					if (row.getType() == QATableRow.RowType.QUESTION) {
						referenceId = row.getQuestionId();
						referenceType = "Question";
					} else if (row.getType() == QATableRow.RowType.ANSWER) {
						referenceId = row.getAnswerId();
						referenceType = "Answer";
					} else if (row.getType() == QATableRow.RowType.REVIEW) {
						referenceId = row.getReviewId();
						referenceType = "Review";
					}

				    new CreateMessagePage(databaseHelper, recipientId, referenceId, referenceType).show(newStage);
				    
				    newStage.setOnHidden(e -> {
				        refreshReviewTable(); // This is a method you'll define to reload your data
				    
				    });
				});

				cellContent.getChildren().addAll(buttonBox);
				cellContent.setAlignment(Pos.CENTER_LEFT);
			}

			@Override
			protected void updateItem(String item, boolean flag) {
				super.updateItem(item, flag);

				// Clear container
				cellContent.getChildren().clear();
				cellBox.getChildren().clear();

				// variable to compound indents
				// int indent = 2;

				// Clear graphic
				setGraphic(null);
				if (flag && item == null) {
					setText(null);
				} else {

					// Get current QATableRow
					QATableRow row = getTableView().getItems().get(getIndex());
					// Create a label to hold the text
					Label displayLabel = new Label(item);
					displayLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
					displayLabel.setWrapText(true);

					displayLabel.maxWidthProperty().bind(contentColumn.widthProperty().subtract(50));

					// Set the preferred height of the cell
					displayLabel.setPrefHeight(225);

					// Flag to hold if row is a question and if currentUser is the author
					boolean isQuestionAuthor = row.getType() == QATableRow.RowType.QUESTION
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId());
					if (isQuestionAuthor) {
						if (!buttonBox.getChildren().contains(markAsReadButton)) {
							buttonBox.getChildren().add(markAsReadButton);
						}

					} else {
						buttonBox.getChildren().remove(markAsReadButton);

					}
					if (!buttonBox.getChildren().contains(markAsReadButton)
							&& !row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						buttonBox.getChildren().add(markAsReadButton);
					}
					try {
						boolean isRead = databaseHelper.qaHelper.isAnswerMarkedAsRead(row.getAnswerId(),
								databaseHelper.currentUser.getUserId());
						if (isRead) {
							markAsReadButton.setDisable(true);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					markAsReadButton.setOnAction(a -> {
						try {
							databaseHelper.qaHelper.markAnswerAsRead(row.getAnswerId(),
									databaseHelper.currentUser.getUserId());
							markAsReadButton.setDisable(true);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					});

					// Make sure you're on at least the second row
					if (getIndex() > 0) {
						QATableRow currentRow = getTableView().getItems().get(getIndex());
						QATableRow previousRow = getTableView().getItems().get(getIndex() - 1);

						// Add a spacer for any thread below the main question
						Region spacer = new Region();
						// Use to outline spacer when setting up indentation
						// spacer.setStyle("-fx-border-color: black;");
						spacer.setMinSize(25, 5);
						spacer.setMaxSize(25, 5);
						cellBox.getChildren().add(0, spacer);

						// Make sure you're at least on third row
						if (getIndex() > 1) {

							// Check if row item is in the relatedId list for the row item above
							if (previousRow.getRelatedId() != null
									&& previousRow.getRelatedId().contains(currentRow.getAnswerId().toString())) {

								// Trying to get compounding indentation to work here. private variable indent
								// already exists at class level
//								spacer.setMinSize(25 * indent, 5);
//								spacer.setMaxSize(25 * indent, 5);
//
//								indent += 1;

							} else {
								// Reset indent counter on an unrelated row
								indent = 1;
							}

						}
					}

					if (row.getType() == QATableRow.RowType.REVIEW) {
						// Label to identify review to the user
						Label reviewLabel = new Label("REVIEW");
						reviewLabel.setAlignment(Pos.CENTER);
						reviewLabel.setRotate(-90);
						reviewLabel.setMaxSize(200, 20);
						reviewLabel.setPrefSize(200, 20);
						reviewLabel.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 16px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px; -fx-letter-spacing: 4px;");

						cellBox.getChildren().add(0, reviewLabel);
						cellBox.setAlignment(Pos.CENTER_LEFT);

						// Button to open the upvote a review
						Button upVoteButton = new Button("\uD83D\uDC4D UpVote");
						upVoteButton.setStyle(
								"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

						// Button to open the downvote a review
						Button downVoteButton = new Button("\uD83D\uDC4E DownVote");
						downVoteButton.setStyle(
								"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

						// Container for the vote buttons
						HBox voteBox = new HBox(5, upVoteButton, downVoteButton);

						// Event to handle upvote button - add 1 to vote for review
						upVoteButton.setOnAction(a -> {
							// Get current QATableRow
							QATableRow currentRow = getTableView().getItems().get(getIndex());

							// Register a positive vote for the selected review
							databaseHelper.qaHelper.registerVoteForReview(currentRow.getReviewId(), 1);

							try {
								// Retrieve the review object in the cell
								review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());

								// Retrieve an updated list of questions from the database
								questions = databaseHelper.qaHelper.getAllQuestions();

								// Refresh contents of tables manually
								questionObservableList.clear();
								questionObservableList.addAll(questions);
								qTable.setItems(questionObservableList);

								if (review.getForQuestion()) {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper.getQuestion(databaseHelper.qaHelper
													.getReview(currentRow.getReviewId()).getRelatedId()));
								} else {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper.getQuestionForAnswer(databaseHelper.qaHelper
													.getAnswer(databaseHelper.qaHelper
															.getReview(currentRow.getReviewId()).getRelatedId())
													.getId()));
								}
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update questions object via getALLQuestions() in upVoteButton action");
							}

						});

						// Event to handle downVote button - subtract 1 from vote for review
						downVoteButton.setOnAction(a -> {
							// Get current QATableRow
							QATableRow currentRow = getTableView().getItems().get(getIndex());

							// Register a negative vote for the selected review
							databaseHelper.qaHelper.registerVoteForReview(currentRow.getReviewId(), -1);

							try {
								// Retrieve the review object in the cell
								review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());

								// Retrieve an updated list of questions from the database
								questions = databaseHelper.qaHelper.getAllQuestions();

								// Refresh contents of tables manually
								questionObservableList.clear();
								questionObservableList.addAll(questions);
								qTable.setItems(questionObservableList);

								if (review.getForQuestion()) {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper.getQuestion(databaseHelper.qaHelper
													.getReview(currentRow.getReviewId()).getRelatedId()));
								} else {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper.getQuestionForAnswer(databaseHelper.qaHelper
													.getAnswer(databaseHelper.qaHelper
															.getReview(currentRow.getReviewId()).getRelatedId())
													.getId()));
								}
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update questions object via getALLQuestions() in downVoteButton action");
							}

						});

						cellContent.getChildren().add(voteBox);
					}

					// Check if row is a Review type and if currentUser is author
					if (row.getType() == QATableRow.RowType.REVIEW && row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {

						// Buttons to edit and delete the question
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						// Styling for buttons
						editButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");

						editButton.setOnAction(a -> {
							// Get current QATableRow
							QATableRow currentRow = getTableView().getItems().get(getIndex());

							// Set submit button text
							submitButton.setText("Update Review");

							// Set updating flag to true
							updatingReview = true;

							// Show the submitBox
							submitBox.setVisible(true);
							submitBox.setManaged(true);

							try {
								// Set review to current row object
								// review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
								review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println("Error trying to get review in results table via editButton");
							}

							// Set inputField to existing answer text to update
							inputField.setText("**OLD\n" + review.getText() + "\n*EDIT:\n ");
						});

						deleteButton.setOnAction(a -> {
							// Get current QATableRow
							QATableRow currentRow = getTableView().getItems().get(getIndex());

							try {
								// Set review to current row object
								review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
								// Retrieve an updated list of questions from the database
								questions = databaseHelper.qaHelper.getAllQuestions();
								// Store related question object for review before deleting review to use later
								Question question = databaseHelper.qaHelper.getQuestion(review.getRelatedId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update questions object via getALLQuestions() in resultsTable");
							}

							// Delete selected review
							databaseHelper.qaHelper.deleteReview(currentRow.getReviewId());

							// Refresh contents of tables manually
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);

							// Set qTable to previous question
							qTable.getSelectionModel().select(question);

						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);

						cellContent.getChildren().addAll(displayLabel, buttonBox);

						setGraphic(cellBox);
						setText(null);

						// Check if row is a question
					} else if (row.getType() == QATableRow.RowType.QUESTION) {

						// This will make sure the first question in row 1 is saved to a question object
						// for updating an answer object later
						try {
							// Set question to current row object
							question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
						} catch (SQLException e) {
							e.printStackTrace();
							System.err.println("Error trying to get question in results table via editButton");
						}

						cellContent.getChildren().add(0, displayLabel);
						setGraphic(cellBox);
						setText(null);
					} else {
						cellContent.getChildren().add(0, displayLabel);
						setGraphic(cellBox);
						setText(null);
					}
					cellContent.getChildren().add(buttonBox);
					cellBox.getChildren().add(cellContent);

				}
			}
		});

		// Set columns to resultsTable
		resultsTable.getColumns().setAll(contentColumn);

		// Give resultsTable a bold outline
		resultsTable.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");

		// Create and place a placeholder for when resultsTable is empty(At the start of
		// the page)
		Label placeholderBox = new Label("◄ Question everything... ►");
		placeholderBox.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: derive(gray, 80%)");
		resultsTable.setPlaceholder(placeholderBox);

		// Hide header for resultsTable
		resultsTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = resultsTable.lookup("TableHeaderRow");
			if (titleBar != null) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Create filter comboBox to adjust question table database view
		ComboBox<String> filter = new ComboBox<>();
		filter.getItems().addAll("All", "Unanswered", "Answered", "Reviewed", "Reviewed By Me");
		filter.setValue("Reviewed By Me");

		// Styling for comboBox
		filter.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");

		HBox filterBox = new HBox(10, filter);
		filterBox.setAlignment(Pos.CENTER_RIGHT);

		filter.valueProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				try {
					switch (newSelection) {
					case "All":
						questions = databaseHelper.qaHelper.getAllQuestions();
						break;
					case "Unanswered":
						questions = databaseHelper.qaHelper.getAllUnansweredQuestions();
						break;
					case "Answered":
						questions = databaseHelper.qaHelper.getAllAnsweredQuestions();
						break;
					case "Reviewed":
						questions = databaseHelper.qaHelper.getAllReviewedQuestions();
						break;
					case "Reviewed By Me":
						questions = databaseHelper.qaHelper.getAllReviewedByMeQuestions();
						break;
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

		// Hbox to position the filter button
		HBox titleBox2 = new HBox(70, newReviewButton, filterBox);
		titleBox2.setAlignment(Pos.CENTER);

		// Container to hold the table
		VBox questionDB = new VBox(5, titleBox2, qTable);

		// Set height of table to adjust to container
		qTable.prefHeightProperty().bind(questionDB.heightProperty());

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
		errorLabel.setTranslateY(22);

		// "Back to login" button will bring user back to the login screen
		quitButton.setOnAction(a -> {

			// Create new stage to add transparency for following pages
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new UserLoginPage(databaseHelper).show(newStage);
		});

		// Create an inputField to initiate and type search
		TextField searchField = new TextField();
		searchField.setPromptText("Search questions...");
		searchField.setStyle(
				"-fx-alignment: center-left; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");
		searchField.setPrefWidth(250);

		// Create an observable list of questions and assign to the table
		ObservableList<Question> searchObservableList = FXCollections.observableArrayList(questions);

		// Create table to display the search results
		TableView<Question> searchTable = new TableView<>();
		searchTable.setItems(searchObservableList);
		searchTable.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

		// Create container to hold searchTable and allow for hiding and showing of it
		HBox searchBox = new HBox(searchTable);

		// Create a column for the searchTable that will hold the content
		TableColumn<Question, String> searchColumn = new TableColumn<>();
		searchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplayWithText()));

		// Style the searchTable with bold lines and black fonts etc..
		searchTable.setRowFactory(a -> new TableRow<Question>() {
			@Override
			protected void updateItem(Question item, boolean flag) {
				super.updateItem(item, flag);
				// If no item then no outline
				if (flag || item == null) {
					setStyle("-fx-border-color: transparent;");
				} else {
					setStyle(
							"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  2px; -fx-table-cell-border-color: black;");
				}
			}
		});

		// Add cell factory to deal with text wrapping and styling
		searchColumn.setCellFactory(a -> new TableCell<Question, String>() {
			private final Label textLabel = new Label();

			{
				textLabel.setWrapText(true);
				textLabel.setStyle("-fx-padding: 1px;");
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

		// Add searchColumn to searchTable
		searchTable.getColumns().add(searchColumn);

		// Hide header for searchTable
		searchTable.widthProperty().addListener((obs, oldVal, newVal) -> {
			Node titleBar = searchTable.lookup("TableHeaderRow");
			if (titleBar != null) {
				titleBar.setVisible(false);
				titleBar.setManaged(false);
				titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
			}
		});

		// Hide table until needed
		searchBox.setVisible(false);
		searchBox.setManaged(false);

		// Search question database for similar entries after/while typing
		searchField.setOnKeyReleased(a -> {
			String input = searchField.getText().trim();
			if (!input.isEmpty()) {

				// Search the database for the input string
				sortedList = databaseHelper.qaHelper.searchQuestionDatabase(input);

				searchField.setMinWidth(searchTable.getWidth());

				// Make table visible
				searchBox.setVisible(true);
				searchBox.setManaged(true);

				if (sortedList != null && !sortedList.isEmpty()) {

					// Set the observable list
					searchObservableList.setAll(sortedList);

					// Make table visible if it isn't
					if (!searchBox.isVisible()) {
						searchBox.setVisible(true);
						searchBox.setManaged(true);
					}
				}
			} else {
				// Clear searchObservableList and hide searchTable
				searchObservableList.clear();
				searchBox.setVisible(false);
				searchBox.setManaged(false);

				// Set searchField width to default
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);

				// Reset searchField position
				searchField.setAlignment(Pos.TOP_RIGHT);
			}
		});

		// On mouse click in the searchField, making the searchTable visible
		searchField.setOnMouseClicked(a -> {
			qTable.getSelectionModel().clearSelection();

			// Show searchTable
			searchBox.setVisible(true);
			searchBox.setManaged(true);
		});

		// On mouse click on the searchTable, navigate to the selected question on the
		// qTable
		searchTable.setOnMouseClicked(a -> {
			Question selection = searchTable.getSelectionModel().getSelectedItem();
			if (selection != null) {

				// Clear questionObservableList
				questionObservableList.clear();

				// Set the sortedList which has the recent search results saved to the
				// questionObservableList
				questionObservableList.setAll(sortedList);

				// Set the observable list to the qTable
				qTable.setItems(questionObservableList);

				// Select the choice on the qTable
				qTable.getSelectionModel().select(selection);

				// Deselect filter after sending custom search to table
				filter.setValue(null);
			}
		});

		// Button to submit a review from the input fields
		submitButton.setOnAction(a -> {
			question = new Question();

			if (review == null) {
				review = new Review();
			}

			String textInput = inputField.getText();

			// Store the selections of the tables
			Question qTableSelection = qTable.getSelectionModel().getSelectedItem();
			QATableRow resultsTableSelection = resultsTable.getSelectionModel().getSelectedItem();

			// Check if textInput is empty or null
			if (textInput == null || textInput.trim().isEmpty()) {
				errorLabel.setText("Error, review-body field is blank.");
				return;
			}

			try {
				if (!updatingReview) {

					if (qTableSelection != null) {
						// Register a review for the currently selected question from the qTable
						review = new Review(true, qTableSelection.getId(), textInput,
								databaseHelper.currentUser.getUserId());
						databaseHelper.qaHelper.registerReview(review);
						// Store the question you're working with for later
						question = qTableSelection;

					} else if (resultsTableSelection != null) {
						if (resultsTableSelection.getType() == QATableRow.RowType.QUESTION) {
							// Register a review for the currently selected question from the qTable
							review = new Review(true, resultsTableSelection.getQuestionId(), textInput,
									databaseHelper.currentUser.getUserId());
							databaseHelper.qaHelper.registerReview(review);

							// Store the question you're working with for later
							question = databaseHelper.qaHelper.getQuestion(resultsTableSelection.getQuestionId());

						} else if (resultsTableSelection.getType() == QATableRow.RowType.ANSWER) {
							// Register a review for the currently selected question from the qTable
							review = new Review(
									true, databaseHelper.qaHelper
											.getQuestionForAnswer(resultsTableSelection.getAnswerId()).getId(),
									textInput, databaseHelper.currentUser.getUserId());
							databaseHelper.qaHelper.registerReview(review);

							// Store the question you're working with for later
							question = databaseHelper.qaHelper
									.getQuestionForAnswer(resultsTableSelection.getAnswerId());
						} else if (resultsTableSelection.getType() == QATableRow.RowType.REVIEW) {
							// Register a review for the currently selected question from the qTable
							review = new Review(true, resultsTableSelection.getQuestionId(), textInput,
									databaseHelper.currentUser.getUserId());
							databaseHelper.qaHelper.registerReview(review);

							// Store the question you're working with for later
							question = databaseHelper.qaHelper.getQuestion(databaseHelper.qaHelper
									.getReview(resultsTableSelection.getReviewId()).getRelatedId());
						}
					}

					// Clear input field for new inputs
					inputField.clear();

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					qTable.getSelectionModel().select(question);

				} else if (updatingReview) {

					// Add updated text to review object
					review.setText(textInput);

					// Update review in the database
					databaseHelper.qaHelper.updateReview(review);

					// Store the question you're working with for later
					question = databaseHelper.qaHelper.getQuestion(
							databaseHelper.qaHelper.getReview(resultsTableSelection.getReviewId()).getRelatedId());

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					// Set qTable selection to previous selection
					qTable.getSelectionModel().select(question);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error trying to register new review into database via submit button");
				;
				return;
			}

			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);

		});

		// Add listeners for the textArea input field
		inputField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
			}
		});

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {

				// Hide the searchBox when using the qTable
				searchBox.setVisible(false);
				searchBox.setManaged(false);

				// Reset position of the searchField
				searchField.setAlignment(Pos.TOP_RIGHT);

				// Set searchField width back to default
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);

				this.question = newSelection;

				// Update text for buttons
				submitButton.setText("Review");

				// Update results table
				updateResultsTableForQuestion(newSelection);
			}
		});

		// Add listeners for table selections to read selected objects from tables
		rTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {

				// Hide the searchBox when using the qTable
				searchBox.setVisible(false);
				searchBox.setManaged(false);

				// Reset position of the searchField
				searchField.setAlignment(Pos.TOP_RIGHT);

				// Set searchField width back to default
				searchField.setMinWidth(250);
				searchField.setPrefWidth(250);

				// Update text for buttons
				submitButton.setText("Review");

				try {
					// Check if review is for a question
					if (newSelection.getForQuestion()) {

						this.question = databaseHelper.qaHelper.getQuestion(newSelection.getRelatedId());

						// Or its for an answer
					} else if (!newSelection.getForQuestion()) {
						this.question = databaseHelper.qaHelper.getQuestionForAnswer(
								databaseHelper.qaHelper.getAnswer(newSelection.getRelatedId()).getId());
					}

					// Update results table
					updateResultsTableForQuestion(question);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		searchField.setAlignment(Pos.TOP_RIGHT);
		searchBox.setAlignment(Pos.BOTTOM_RIGHT);

		// Use containers to position and hold many different UI components
		HBox hboxTop = new HBox(10, searchField);
		hboxTop.setAlignment(Pos.CENTER_RIGHT);

		VBox vboxTop = new VBox(10, hboxTop);
		vboxTop.setAlignment(Pos.CENTER_RIGHT);

		StackPane root2 = new StackPane(resultsTable, searchBox);

		VBox vbox = new VBox(10, vboxTop, root2);
		vbox.setAlignment(Pos.CENTER);

		HBox quitButtonBox = new HBox(quitButton);
		quitButtonBox.setAlignment(Pos.BOTTOM_LEFT);

		Button viewUnresolvedBtn = createViewUnresolvedButton();
		Button viewAllUnresolvedBtn = createViewAllUnresolvedButton();

		HBox buttonBox2 = new HBox(10, viewUnresolvedBtn, viewAllUnresolvedBtn);

		HBox buttonBox1 = new HBox(10, viewReviewsButton, quitButtonBox, inboxButton, buttonBox2);
		quitButton.setAlignment(Pos.BOTTOM_LEFT);

		VBox vbox1 = new VBox(10, vbox, buttonBox1);
		vbox1.setAlignment(Pos.CENTER);

		// Hide submitBox unless needed
		submitBox.setVisible(false);
		submitBox.setManaged(false);

		// Hide submitBox unless needed
		rTableBox.setVisible(false);
		rTableBox.setManaged(false);

		newReviewButton.setOnAction(a -> {
			// Set submit button text
			submitButton.setText("Submit Review");

			// Show submitBox
			submitBox.setVisible(true);
			submitBox.setManaged(true);

		});

		inboxButton.setOnAction(a -> {
			// Create a new stage in order to popup new window and keep this one
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);
			new Inbox(databaseHelper).show(newStage);
		});

		reviewCloseButton.setOnAction(a -> {
			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);
		});

		StackPane root3 = new StackPane(questionDB, submitBox, rTableBox);

		HBox hbox1 = new HBox(5, root3, vbox1);

		VBox layout = new VBox(topLabelBox, hbox1, errorLabel);

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

		viewReviewsButton.setOnAction(a -> {
			if (!reviewToggle) {
				// Show submitBox
				rTableBox.setVisible(true);
				rTableBox.setManaged(true);

				// Toggle the text
				viewReviewsButton.setText("View Questions");
				reviewFilterString = "My Reviews";
			} else {
				// Hide submitBox
				rTableBox.setVisible(false);
				rTableBox.setManaged(false);

				// Toggle the text
				viewReviewsButton.setText("View Reviews");
				reviewFilterString = "My Reviews";
			}
			reviewToggle = !reviewToggle;
		});

		// Container to hold the three buttons min, max, and close
		HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
		buttonBar.setAlignment(Pos.TOP_RIGHT);
		buttonBar.setPadding(new Insets(0));
		buttonBar.setMaxHeight(27);
		buttonBar.setMaxWidth(80);

		buttonBar.setAlignment(Pos.TOP_RIGHT);

		// StackPane to control layout sizing
		StackPane root = new StackPane(layout, buttonBar);
		root.setAlignment(buttonBar, Pos.TOP_RIGHT);
		root.setStyle("-fx-background-color: transparent;");
		root.setStyle("-fx-background-color: derive(gray, 60%);");
		root.setPadding(new Insets(0));

		Scene scene = new Scene(root, 1900, 1000);

		resultsTable.prefWidthProperty().bind(hbox1.widthProperty());
		resultsTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		qTable.prefWidthProperty().bind(root3.widthProperty());
		qTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		rTable.prefWidthProperty().bind(root3.widthProperty());
		rTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		contentColumn.prefWidthProperty().bind(vbox1.widthProperty().subtract(19));
		detailsColumn.prefWidthProperty().bind(questionDB.widthProperty().subtract(19));
		searchTable.prefWidthProperty().bind(vbox.widthProperty());
		searchTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		searchBox.prefWidthProperty().bind(vbox.widthProperty());
		searchBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		submitBox.prefWidthProperty().bind(qTable.widthProperty());
		submitBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		rTableBox.prefWidthProperty().bind(qTable.widthProperty());
		rTableBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		reviewInputBox.prefWidthProperty().bind(qTable.widthProperty());
		reviewInputBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		topLabelBox.prefWidthProperty().bind(layout.widthProperty());
		buttonBox1.prefWidthProperty().bind(resultsTable.widthProperty());

		// Set the scene to primary stage
		primaryStage.setScene(scene);
		primaryStage.setTitle("");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		primaryStage.show();
	}

	/**
	 * Updates the results table based on the selected question.
	 *
	 * <p>This method retrieves the current state of the specified question along with its
	 * associated answers and reviews, repopulating the observable list used by the results table
	 * and refreshing its display.</p>
	 *
	 * @param question the question for which the results table should be updated.
	 */

	private void updateResultsTableForQuestion(Question question) {
		Answer duplicate = null;
		List<Review> reviews;
		// Clear the observable list
		resultsObservableList.clear();

		// Retrieve an updated answer list of answers related to selected question from
		// the qtable
		try {
			question = databaseHelper.qaHelper.getQuestion(question.getId());
			answers = databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId());

			// Put the selected question in the first row
			resultsObservableList.add(new QATableRow(QATableRow.RowType.QUESTION, question.toDisplayWithText(),
					question.getId(), question.getAuthorId(), question.getRelatedId()));

			// Store a list of reviews for the question
			reviews = databaseHelper.qaHelper.getReviewsForQuestion(question.getId());
			// Check if list is empty
			if (!reviews.isEmpty()) {
				for (Review review : reviews) {
					// Put the selected review in the following rows
					resultsObservableList.add(new QATableRow(QATableRow.RowType.REVIEW, review.toDisplayWithText(),
							review.getId(), review.getAuthorId()));
				}
			}

			// Check if selected question has a preferred answer and put that in row 2 if so
			// Check if question has a preferred answer
			if (question.getPreferredAnswer() > 0) {
				// Retrieve the preferred answer object
				answer = databaseHelper.qaHelper.getAnswer(question.getPreferredAnswer());
				duplicate = answer;
				// Remove the preferred answer from the answers list so it is not duplicated
				answers.remove(answer);
				// Put the preferred answer in the second row
				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(), answer.getId(),
						answer.getAuthorId(), answer.getRelatedId()));

				// Recursively call addRelatedAnswers and store the list thats left
				answers = addRelatedAnswers(answer.getId(), answers);

			}

			// After that, if there are any, add each answer as its own row
			for (Answer answer : answers) {
				if (duplicate != null && !answer.getId().equals(duplicate.getId())) {
					// Add answer to the observable list
					resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(),
							answer.getId(), answer.getAuthorId(), answer.getRelatedId()));
				}

				// Store a list of reviews for the question
				reviews = databaseHelper.qaHelper.getReviewsForAnswer(answer.getId());
				// Check if list is empty
				if (!reviews.isEmpty()) {
					for (Review review : reviews) {
						// Put the selected review in the following rows
						resultsObservableList.add(new QATableRow(QATableRow.RowType.REVIEW, review.toDisplayWithText(),
								review.getId(), review.getAuthorId()));
					}
				}
				// Recursively call addRelatedAnswers and store the list thats left
				answers = addRelatedAnswers(answer.getId(), answers);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error trying to .getAllAnswers() within updateResultsTableForQuestion() method");
			return;
		}

		resultsTable.setItems(resultsObservableList);
		resultsTable.refresh();
	}

	/**
	 * Displays a pop-up window listing unresolved questions for the current reviewer.
	 *
	 * <p>This method retrieves unresolved questions from the database specific to the current user
	 * and displays them in a new stage. Double-clicking a question will open its potential answers window.</p>
	 */

	private void showUnresolvedQuestionsForCurrentUser() {
		// Create a new stage (window) to display unresolved questions
		Stage stage = new Stage();
		stage.setTitle("My Unresolved Questions");
		// Create a heading label with styling for emphasis
		Label heading = new Label("My Unresolved Questions");
		heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		// Create a TableView to display the list of unresolved questions
		TableView<Question> unresolvedTable = new TableView<>();
		unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
		unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Declare a list to store unresolved questions for the current user
		List<Question> myUnresolvedList;
		try {
			// Retrieve unresolved questions from the database for the logged-in user
			myUnresolvedList = databaseHelper.qaHelper
					.getAllUnresolvedQuestionsForUser(databaseHelper.currentUser.getUserId());
		} catch (SQLException ex) {
			// Handle SQL exceptions and initialize an empty list to avoid null issues
			ex.printStackTrace();
			myUnresolvedList = new ArrayList<>();
		}
		// Convert the retrieved list into an observable list for TableView
		ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(myUnresolvedList);
		unresolvedTable.setItems(unresolvedObs);
		// Define a column to display question titles
		TableColumn<Question, String> questCol = new TableColumn<>("Question Title");

		// Set cell value factory to retrieve the title property from each Question
		// object
		questCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

		// Add the column to the TableView
		unresolvedTable.getColumns().add(questCol);

		// Set a row factory to detect double-clicks on a question row
		unresolvedTable.setRowFactory(tv -> {
			TableRow<Question> row = new TableRow<>();
			row.setOnMouseClicked(event -> {

				// If the row is not empty and a double-click is detected
				if (!row.isEmpty() && event.getClickCount() == 2) {

					// Retrieve the selected question and open its potential answers window
					Question question = row.getItem();
					showPotentialAnswersWindow(question);
				}
			});
			return row; // Return the modified row
		});

		VBox layout = new VBox(10, heading, unresolvedTable);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(15, 15, 15, 15));
		Scene scene = new Scene(layout, 700, 400);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Displays a pop-up window listing potential answers (both unread and read) for the specified question.
	 *
	 * @param question the question for which to display the potential answers.
	 */

	private void showPotentialAnswersWindow(Question question) {
		Stage stage = new Stage();
		stage.setTitle("Answers for: " + question.getTitle());

		Label heading = new Label("Answers");
		heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		TableView<Answer> unreadAnswersTable = new TableView<>();
		TableView<Answer> readAnswersTable = new TableView<>();

		unreadAnswersTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
		readAnswersTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");

		unreadAnswersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		readAnswersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		List<Answer> unreadAnswers = new ArrayList<>();
		List<Answer> readAnswers = new ArrayList<>();

		try {
			Map<String, List<Answer>> answerMap = databaseHelper.qaHelper.getReadAndUnreadAnswers(question.getId(),
					databaseHelper.currentUser.getUserId());

			unreadAnswers = answerMap.getOrDefault("unread", new ArrayList<>());
			readAnswers = answerMap.getOrDefault("read", new ArrayList<>());
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		ObservableList<Answer> unreadObs = FXCollections.observableArrayList(unreadAnswers);
		ObservableList<Answer> readObs = FXCollections.observableArrayList(readAnswers);

		unreadAnswersTable.setItems(unreadObs);
		readAnswersTable.setItems(readObs);

		TableColumn<Answer, String> unreadCol = new TableColumn<>("Unread Answers");
		unreadCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		TableColumn<Answer, String> readCol = new TableColumn<>("Read Answers");
		readCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		unreadAnswersTable.getColumns().add(unreadCol);
		readAnswersTable.getColumns().add(readCol);

		VBox unreadBox = new VBox(new Label("Unread Answers"), unreadAnswersTable);
		VBox readBox = new VBox(new Label("Read Answers"), readAnswersTable);
		HBox answersBox = new HBox(20, unreadBox, readBox);
		answersBox.setAlignment(Pos.CENTER);

		VBox layout = new VBox(10, heading, answersBox);
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(15, 15, 15, 15));

		Scene scene = new Scene(layout, 700, 400);
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Creates and returns a button that, when clicked, displays unresolved questions for the current reviewer.
	 *
	 * @return a Button configured to show the current user's unresolved questions.
	 */

	private Button createViewUnresolvedButton() {
		Button viewUnresolvedBtn = new Button("View My Unresolved");
		// Button text instructs that we are viewing the current user's unresolved
		// questions

		viewUnresolvedBtn.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
		// Applies bold text and a black border, matching your UI design

		viewUnresolvedBtn.setOnAction(e -> {
			showUnresolvedQuestionsForCurrentUser();
			// When clicked, calls the method that displays the user's unresolved questions
		});
		return viewUnresolvedBtn;
		// Returns the newly-created button for "View My Unresolved"
	}

	/**
	 * Creates and returns a button that, when clicked, displays all unresolved questions.
	 *
	 * @return a Button configured to show all unresolved questions.
	 */

	private Button createViewAllUnresolvedButton() {
		Button viewAllUnresolvedBtn = new Button("View All Unresolved");
		// Button text indicates that this lists all unresolved questions (not just the
		// current user's)

		viewAllUnresolvedBtn.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
		// Same style as other buttons associated

		viewAllUnresolvedBtn.setOnAction(e -> {
			showAllUnresolvedQuestions();
			// When clicked, calls the method that displays every unresolved question
		});
		return viewAllUnresolvedBtn;
		// Returns the new button for "View All Unresolved"
	}

	// Displays a pop-up window listing all unresolved questions for any user (User
	// Story #2).
	private void showAllUnresolvedQuestions() {
		Stage stage = new Stage();
		// Creates a new Stage to list unresolved questions from all users

		stage.setTitle("All Unresolved Questions");
		// Sets the title to "All Unresolved Questions" for clarity

		Label heading = new Label("All Unresolved Questions");
		// Heading label that explains this list is for all users

		heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
		// Applies large, bold styling to the heading

		TableView<Question> unresolvedTable = new TableView<>();
		// A TableView to list all unresolved questions (regardless of user)

		unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
		// Matches your application's style: black border, bold text

		unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		// Automatically resizes columns to fit the table width

		List<Question> allUnresolvedList;
		// Will hold all unresolved questions for every user

		try {
			allUnresolvedList = databaseHelper.qaHelper.getAllUnresolvedQuestions();
			// Retrieves all unresolved questions, not filtered by user
		} catch (SQLException ex) {
			ex.printStackTrace();
			allUnresolvedList = new ArrayList<>();
			// If the DB call fails, initializes an empty list to prevent null usage
		}

		ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(allUnresolvedList);
		// Wraps the retrieved list in an ObservableList for display in the table

		unresolvedTable.setItems(unresolvedObs);
		// Sets the table's data source to our ObservableList

		TableColumn<Question, String> questCol = new TableColumn<>("Question Title");
		// Column to display the question title text

		questCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		// Binds the column to each Question's getTitle() method

		TableColumn<Question, Number> potentialAnsCol = new TableColumn<>("Potential Answers");
		// Column showing how many potential (unpreferred) answers each question has

		potentialAnsCol.setCellValueFactory(cellData -> {
			Question q = cellData.getValue();
			int count = 0;
			// Will track how many potential answers exist for this question

			try {
				List<Answer> potential = databaseHelper.qaHelper.getPotentialAnswersForQuestion(q.getId());
				// Retrieves any answers that have not been chosen as the question's preferred
				// answer
				count = potential.size();
				// Stores the number of those potential answers
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return new ReadOnlyObjectWrapper<>(count);
			// Wraps the count in a ReadOnlyObjectWrapper so it can be displayed in the
			// table cell
		});

		unresolvedTable.getColumns().addAll(questCol, potentialAnsCol);
		// Adds columns for question title and potential answers to the table

		// Allows double-clicking on a row to open that question's potential answers
		unresolvedTable.setRowFactory(tv -> {
			TableRow<Question> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					// Checks if we have a valid row and a double-click
					Question clickedQ = row.getItem();
					// Retrieves the Question from that row
					showPotentialAnswersWindow(clickedQ);
					// Opens a window showing potential answers for this question
				}
			});
			return row;
		});

		VBox layout = new VBox(10, heading, unresolvedTable);
		// A simple vertical box to hold the heading and the table

		layout.setAlignment(Pos.CENTER);
		// Centers the layout content

		layout.setPadding(new Insets(15, 15, 15, 15));
		// Adds padding of 15px on each side

		Scene scene = new Scene(layout, 700, 400);
		// Creates a scene sized 700x400 for the "All Unresolved" pop-up

		stage.setScene(scene);
		// Sets the newly created scene on the stage

		stage.show();
		// Displays this stage to the user
	}
	
	/**
	 * Refreshes the review table by reloading reviews based on the current review filter.
	 *
	 * <p>This method retrieves either all reviews or only the current user's reviews (depending on the filter),
	 * updates the observable list, and refreshes the review table display.</p>
	 */

	private void refreshReviewTable() {
	    try {
	        if ("All Reviews".equals(reviewFilterString)) {
	            reviews = databaseHelper.qaHelper.getAllReviews();
	        } else if ("My Reviews".equals(reviewFilterString)) {
	            reviews = databaseHelper.qaHelper.getMyReviews();
	        } else {
	            return;
	        }
	        reviewObservableList.setAll(reviews);
	        rTable.setItems(reviewObservableList);
	        rTable.refresh();
	    } catch (SQLException e) {
	        System.out.println("Failed to refresh review table");
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Recursively processes and adds related answers to the results observable list.
	 *
	 * <p>This method retrieves answers related to the specified parent answer, adds them to the results table,
	 * and removes them from the provided list to avoid duplication. It processes nested related answers recursively.</p>
	 *
	 * @param parentId the ID of the parent answer.
	 * @param answers  the list of answers to process.
	 * @return the modified list of answers after processing related answers.
	 */

	private List<Answer> addRelatedAnswers(int parentId, List<Answer> answers) {
		try {
			// Retrieve related answers
			List<Answer> relatedAnswers = databaseHelper.qaHelper.getAllAnswersForAnswer(parentId);

			// Iterate through each answer in relatedAnswers
			for (Answer subAnswer : relatedAnswers) {

				// Remove subAnswer from the list of answers
				answers.remove(subAnswer);

				resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, subAnswer.toDisplay(),
						subAnswer.getId(), subAnswer.getAuthorId(), subAnswer.getRelatedId()));

				// Recursively call the function to process nested related answers
				addRelatedAnswers(subAnswer.getId(), answers);

			}

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error retrieving related answers in addRelatedAnswers()");
		}
		// Return list of answers that is left
		return answers;
	}
}
