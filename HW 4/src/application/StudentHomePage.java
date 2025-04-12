package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * StudentHomePage provides the user interface for students to submit questions,
 * view answers, and interact with reviewers. It displays a list of questions,
 * supports creating new questions, editing existing ones, and shows related answers
 * and reviews.
 */

public class StudentHomePage {

	private final DatabaseHelper databaseHelper;
	private Question question;
	private Answer answer;
	private Review review;
	private List<Question> questions;
	private List<Answer> answers;
	private ObservableList<QATableRow> resultsObservableList = FXCollections.observableArrayList();
	private TableView<Question> qTable;
	private List<Question> sortedList;
	private boolean updatingAnswer = false;
	private boolean updatingQuestion = false;

	private Answer tempAnswer;

	private TableView<QATableRow> resultsTable;

	private int indent = 0;
	
	/**
	 * Constructs a StudentHomePage instance with the specified DatabaseHelper.
	 *
	 * @param databaseHelper the helper object for database operations.
	 */

	public StudentHomePage(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/**
	 * Builds and displays the student home page.
	 *
	 * <p>This method initializes UI components including input fields for the question title and body,
	 * tables to display questions and their related answers/reviews, and buttons for various actions.
	 * It also sets up event handlers for submitting, editing, deleting, and filtering questions.</p>
	 *
	 * @param primaryStage the main stage on which to display the student home page.
	 */

	public void show(Stage primaryStage) {
		double[] offsetX = { 0 };
		double[] offsetY = { 0 };

		try {
			questions = databaseHelper.qaHelper.getAllQuestions();
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
		Label topLabel = new Label("Student Home Page");
		topLabel.setStyle(
				"-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-color: derive(gray, -20%)");

		VBox topLabelBox = new VBox(topLabel);
		topLabelBox.setStyle(" -fx-background-color: derive(gray, -20%)");

		// Label to display title of area to the user
		Label prompt = new Label("Entry Box");
		prompt.setStyle("-fx-text-fill: black; -fx-font-size: 16px; -fx-font-weight: bold;");

		// Input box for a title
		TextArea titleField = new TextArea();
		titleField.setPromptText("Enter a title for your question.");
		// Styling for the titleField
		titleField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		titleField.setMaxWidth(600);
		titleField.setPrefWidth(600);
		titleField.setMaxHeight(60);
		titleField.setPrefHeight(60);

		// Input box for body of question
		TextArea inputField = new TextArea();
		inputField.setPromptText("Ask your question in more detail");
		// Styling for the inputField
		inputField.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
				+ "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
		inputField.setMaxWidth(600);
		inputField.setPrefWidth(600);
		inputField.setMaxHeight(900);
		inputField.setPrefHeight(900);
		inputField.setWrapText(true);

		// Button to submit question text in input fields to database
		Button submitButton = new Button("Submit Question");
		submitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to return to the login screen
		Button quitButton = new Button("Back to login");
		quitButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		// Button to find reviewers for your questions
		Button findReviewerButton = new Button("Find Reviewer For Question");
		findReviewerButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
		
		Button manageReviewersButton = new Button("Manage my Reviewers");
		manageReviewersButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

		Button askToBeAReviewer = new Button("Ask to be a reviewer");
		askToBeAReviewer.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

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
		Button newQuestionButton = new Button("New");
		newQuestionButton.setStyle(
				"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
		newQuestionButton.setMinWidth(60);
		
		// Button to open the ui to submit a new question
		Button questionCloseButton = new Button("Close");
		questionCloseButton.setStyle(
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

		// Hbox to hold and position the title
		HBox topTitleBox = new HBox(135, submitButton, questionCloseButton);
		// Set alignment of box
		questionCloseButton.setAlignment(Pos.TOP_RIGHT);
		submitButton.setAlignment(Pos.TOP_LEFT);

		// Container to hold the two input boxes for questions and their title together
		VBox questionInputBox = new VBox(5, topTitleBox, titleField, inputField);
		questionInputBox.setStyle("-fx-background-color: derive(gray, 80%)");

		VBox submitBox = new VBox(questionInputBox);

		TableColumn<QATableRow, String> contentColumn = new TableColumn<>("Results");
		contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

		// Add cell factory to deal with text runoff and disable horizontal scrolling
		contentColumn.setCellFactory(a -> new TableCell<QATableRow, String>() {
			private final TextArea replyArea = new TextArea();
			private final Button submitReplyButton = new Button("Submit");
			private final Button MessageButton = new Button("Message User");
			private final Button markAsReadButton = new Button("Mark As Read");
			HBox buttonBox = new HBox(10, submitReplyButton, MessageButton);
			private final VBox replyBox = new VBox(5, buttonBox, replyArea);
			private final VBox cellContent = new VBox(5);
			private final HBox cellBox = new HBox();

			{
				submitReplyButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
				replyBox.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
				replyBox.setStyle("-fx-padding: 1px;");
				
				MessageButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
				replyBox.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
				replyBox.setStyle("-fx-padding: 1px;");

				markAsReadButton.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
				replyBox.setStyle(
						"-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");
				replyBox.setStyle("-fx-padding: 1px;");

				// Set prompt text for replyArea
				replyArea.setPromptText("Enter your answer here...");
				replyArea.setPrefRowCount(3);
				
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
				});

				submitReplyButton.setOnAction(a -> {
					String inputText = replyArea.getText().trim();
					QATableRow.RowType rowType = getTableView().getItems().get(getIndex()).getType();

					if (!inputText.isEmpty()) {
						try {

							if (rowType == QATableRow.RowType.QUESTION) {
								databaseHelper.qaHelper.registerAnswerWithQuestion(
										new Answer(inputText, databaseHelper.currentUser.getUserId()),
										getTableView().getItems().get(getIndex()).getAnswerId());
								replyArea.clear();
							} else {
								databaseHelper.qaHelper.registerAnswerWithAnswer(
										new Answer(inputText, databaseHelper.currentUser.getUserId()),
										getTableView().getItems().get(getIndex()).getAnswerId());
								replyArea.clear();
							}

							// Retrieve your new answer from the database
							tempAnswer = databaseHelper.qaHelper.getAnswer(inputText);

						} catch (SQLException e) {
							e.printStackTrace();
							System.err
							.println("Error trying to register answer in results table via submitReplyButton");
						}

						// Update the table after submitting new answer
						updateResultsTableForQuestion(qTable.getSelectionModel().getSelectedItem());

						// Select the next row down which is the new answer - Doesn't work if the text
						// of the answer is the same
						resultsTable.getSelectionModel()
								.select(new QATableRow(QATableRow.RowType.ANSWER, tempAnswer.toDisplay(),
										tempAnswer.getId(), tempAnswer.getAuthorId(), tempAnswer.getRelatedId()));

					}
				});			
				cellContent.getChildren().addAll(replyBox);
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

					
					boolean isQuestionAuthor = row.getType() == QATableRow.RowType.QUESTION &&
						row.getAuthorId().equals(databaseHelper.currentUser.getUserId());
					if (isQuestionAuthor) {
						if (!buttonBox.getChildren().contains(markAsReadButton)) {
							buttonBox.getChildren().add(markAsReadButton);
						}
						
					} else {
						buttonBox.getChildren().remove(markAsReadButton);
						
					} if (!buttonBox.getChildren().contains(markAsReadButton) && !row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						buttonBox.getChildren().add(markAsReadButton);
					} try {
						boolean isRead = databaseHelper.qaHelper.isAnswerMarkedAsRead(row.getAnswerId(), databaseHelper.currentUser.getUserId());
						if (isRead) {
							markAsReadButton.setDisable(true);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					markAsReadButton.setOnAction(a -> {
						try {
							databaseHelper.qaHelper.markAnswerAsRead(row.getAnswerId(), databaseHelper.currentUser.getUserId());
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
						//spacer.setStyle("-fx-border-color: black;");
						spacer.setMinSize(50, 5);
						spacer.setMaxSize(50, 5);
						cellBox.getChildren().add(spacer);

						// Make sure you're at least on third row
						if (getIndex() > 1) {

							// Check if row item is in the relatedId list for the row item above
							if (previousRow.getRelatedId() != null
									&& previousRow.getRelatedId().contains(currentRow.getAnswerId().toString())) {

								// Trying to get compounding indentation to work here. private variable indent
								// already exists at class level

							} else {
								// Reset indent counter on an unrelated row
								indent = 0;
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
									qTable.getSelectionModel().select(databaseHelper.qaHelper.getQuestion(
											databaseHelper.qaHelper.getReview(currentRow.getReviewId()).getRelatedId()));
								} else {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper
													.getQuestionForAnswer(
															databaseHelper.qaHelper
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
									qTable.getSelectionModel().select(databaseHelper.qaHelper.getQuestion(
											databaseHelper.qaHelper.getReview(currentRow.getReviewId()).getRelatedId()));
								} else {
									// Set qTable to previous question
									qTable.getSelectionModel()
											.select(databaseHelper.qaHelper
													.getQuestionForAnswer(
															databaseHelper.qaHelper
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

					// Check if the currentUser matches the author of the answer in the cell
					if (row.getType() == QATableRow.RowType.ANSWER && row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						// Buttons to edit and delete the answer
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
							// Set submit button text
							submitButton.setText("Update Answer");

							// Show the submitBox
							submitBox.setVisible(true);
							submitBox.setManaged(true);

							// Set updating flag to true
							updatingAnswer = true;

							try {
								// Set answer to current row object
								answer = databaseHelper.qaHelper.getAnswer(row.getAnswerId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println("Error trying to get answer in results table via editButton");
							}

							// Set inputField to existing answer text to update
							inputField.setText(answer.getText());

						});

						deleteButton.setOnAction(a -> {
							// Delete selected answer
							databaseHelper.qaHelper.deleteAnswer(row.getAnswerId());

							try {
								// Retrieve an updated list of questions from the database
								question = qTable.getSelectionModel().getSelectedItem();
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update answers object via getALLUsers() in resultsTable");
							}

							// Refresh contents of tables manually
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);

							// Set qTable to previous question
							qTable.getSelectionModel().select(question);

						});
						
						// Check if the answer is already marked as read
						try {
					        boolean isRead = databaseHelper.qaHelper.isAnswerMarkedAsRead(row.getAnswerId(), databaseHelper.currentUser.getUserId());
					        if (isRead) {
					            markAsReadButton.setDisable(true); 
					        }
						} catch (SQLException e) {
					        e.printStackTrace();
						}
						
						markAsReadButton.setOnAction(a -> {
					        try {
					            databaseHelper.qaHelper.markAnswerAsRead(row.getAnswerId(), databaseHelper.currentUser.getUserId()); 
					            markAsReadButton.setDisable(true); // Disable button after clicking
					        } catch (SQLException e) {
					            e.printStackTrace();
					        }
						});

						HBox buttonBox = new HBox(1, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);

						cellContent.getChildren().addAll(displayLabel, buttonBox);

						setGraphic(cellBox);
						setText(null);
						// Check if row is a question and the author matches current user
					} else if (row.getType() == QATableRow.RowType.QUESTION && row.getAuthorId() != null
							&& row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
						// Buttons to edit and delete the question
						Button editButton = new Button("Edit");
						Button deleteButton = new Button("Delete");
						Button setPreferredAnswerButton = new Button("Set Prefered Answer"); //joe
						// Styling for buttons
						editButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");
						deleteButton.setStyle(
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");
						setPreferredAnswerButton.setStyle( //joe
								"-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
										+ "-fx-font-weight: bold; -fx-padding: 1px;");

						setPreferredAnswerButton.setOnAction(a -> { // joe
						    // Debug: Print when the setPreferredAnswerButton is pressed
						    System.out.println("SetPreferredAnswerButton pressed for question id: " 
						        + question.getId() + " | Current preferredAnswer: " + question.getPreferredAnswer());

						    // Create a pop-up for the user to input the preferred answer Id
						    Stage preferredAnswerBox = new Stage();

						    Label promptLabel = new Label("Enter the Answer ID of the preferred answer:");
						    TextField answerIdField = new TextField();
						    answerIdField.setPrefWidth(50);
						    Label errorLabel = new Label("");
						    errorLabel.setStyle("-fx-text-fill: red;");
						    Button confirmButton = new Button("Confirm");

						    // Attach functionality to the confirm button with input validation
						    confirmButton.setOnAction(event -> {
						        // Clear any previous error messages
						        errorLabel.setText("");
						        
						        // First check if the input is empty
						        String inputText = answerIdField.getText().trim();
						        if (inputText.isEmpty()) {
						            errorLabel.setText("Input cannot be empty.");
						            return;
						        }
						        
						        try {
						     
						        	
						            int enteredId = Integer.parseInt(inputText);
						            
						            Question newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());

						            // Validate that the entered ID exists among valid answer IDs for this question.
						            if (newQuestion.getRelatedId() == null || !newQuestion.getRelatedId().contains(String.valueOf(enteredId))) {
						                errorLabel.setText("Please enter a valid answer ID for this question.");
						                return;
						            }

						            // Update the in-memory question with the preferred answer
						            newQuestion.setPreferredAnswer(enteredId);

						            // Update the database with the new preferred answer
						            // (Assumes you have added an updatePreferredAnswer method in QAHelper1.)
						            databaseHelper.qaHelper.updatePreferredAnswer(newQuestion);

						            // Close the popup once the update is complete
						            preferredAnswerBox.close();
						            updateResultsTableForQuestion(newQuestion);
						        } catch (NumberFormatException ex) {
						            errorLabel.setText("Please enter only numbers.");
						        } 
						        catch (SQLException ex) {
                                    errorLabel.setText("Could not get the question.");
                                }						      
						    });

						    // Build the layout including the error label
						    VBox layout = new VBox(10, promptLabel, answerIdField, errorLabel, confirmButton);
						    layout.setAlignment(Pos.CENTER);

						    Scene scene = new Scene(layout, 500, 300);
						    preferredAnswerBox.setScene(scene);
						    preferredAnswerBox.setTitle("Set Preferred Answer");
						    preferredAnswerBox.show();
						});


						editButton.setOnAction(a -> {
							// Set submit button text
							submitButton.setText("Update Question");

							// Show the submitBox
							submitBox.setVisible(true);
							submitBox.setManaged(true);

							// Set updating flag to true
							updatingQuestion = true;

							try {
								// Set answer to current row object
								question = databaseHelper.qaHelper.getQuestion(row.getQuestionId());
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println("Error trying to get question in results table via editButton");
							}

							// Set titleField to existing answer text to update
							titleField.setText(question.getTitle());

							// Set inputField to existing answer text to update
							inputField.setText(question.getText());
						});

						deleteButton.setOnAction(a -> {
							// Delete selected question
							databaseHelper.qaHelper.deleteQuestion(row.getQuestionId());

							try {
								// Retrieve an updated list of questions from the database
								questions = databaseHelper.qaHelper.getAllQuestions();
							} catch (SQLException e) {
								e.printStackTrace();
								System.err.println(
										"Error trying update answers object via getALLUsers() in resultsTable");
							}

							// Refresh contents of tables manually
							questionObservableList.clear();
							questionObservableList.addAll(questions);
							qTable.setItems(questionObservableList);

							// Set qTable to previous question
							qTable.getSelectionModel().select(question);

						});

						HBox buttonBox = new HBox(1,setPreferredAnswerButton, editButton, deleteButton);
						buttonBox.setAlignment(Pos.BOTTOM_RIGHT);

						displayLabel.setAlignment(Pos.CENTER_LEFT);

						cellContent.getChildren().addAll(displayLabel, buttonBox);

						setGraphic(cellBox);
						setText(null);
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
					cellContent.getChildren().add(replyBox);
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

		// Create filter button to adjust question table database view
		ToggleGroup filter = new ToggleGroup();

		RadioButton allButton = new RadioButton("All");
		allButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		allButton.setToggleGroup(filter);
		allButton.setSelected(true);

		RadioButton unansweredButton = new RadioButton("Unanswered");
		unansweredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		unansweredButton.setToggleGroup(filter);
		;

		RadioButton answeredButton = new RadioButton("Answered");
		answeredButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		answeredButton.setToggleGroup(filter);

		RadioButton reviewersButton = new RadioButton("My Reviewers");
		reviewersButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
		reviewersButton.setToggleGroup(filter);
		
		FlowPane filterBox = new FlowPane(30, 10, allButton, unansweredButton, answeredButton, reviewersButton);		
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
					} else if (selection.equalsIgnoreCase("My Reviewers")) {
						questions = getReviewersQuestions();
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
		HBox titleBox2 = new HBox(20, newQuestionButton, filterBox);
		HBox.setHgrow(filterBox, Priority.ALWAYS);
		filterBox.setMaxWidth(Region.USE_COMPUTED_SIZE);
		titleBox2.setAlignment(Pos.CENTER);
		titleBox2.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(titleBox2, Priority.ALWAYS);
		// Container to hold the table
		VBox questionDB = new VBox(10, titleBox2, qTable);

		// Set height of table to adjust to container
		qTable.prefHeightProperty().bind(questionDB.heightProperty());

		// Label to display error messages
		Label errorLabel = new Label();
		errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
		errorLabel.setTranslateY(22);
		
		askToBeAReviewer.setOnAction(a ->{
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			new AskToBeAReviewer(databaseHelper).show(newStage);
		});

		
		if (databaseHelper.currentUser.getRoles().contains("Reviewer")) {
			askToBeAReviewer.setVisible(false);
			askToBeAReviewer.setManaged(false);
		} else {
			askToBeAReviewer.setVisible(true);
			askToBeAReviewer.setManaged(true);
		}

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

		// On mouse click on the searchTable, navigate to the selected question on the qTable
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

				// Deselect filter buttons after sending custom search to table
				allButton.setSelected(false);
				unansweredButton.setSelected(false);
				answeredButton.setSelected(false);
			}
		});

		// Button to submit an answer or question from the input fields
		submitButton.setOnAction(a -> {
			String titleInput = titleField.getText();
			String textInput = inputField.getText();

			// Check if textInput is empty or null
			if (textInput == null || textInput.trim().isEmpty()) {
				errorLabel.setText("Error, question-body field is blank.");
				return;
			}
			try {
				if (!updatingQuestion && !updatingAnswer) {

					// Check if titleInput is empty or null
					if (titleInput == null || titleInput.trim().isEmpty()) {
						errorLabel.setText("Error, question-title field is blank.");
						return;
					}

					// Check if inputs are empty or null
					Question newQuestion;
					Question newSelection = qTable.getSelectionModel().getSelectedItem();

					if (newSelection != null) {
						// Update the currently selected question
						newSelection.setTitle(titleInput);
						newSelection.setText(textInput);
						databaseHelper.qaHelper.updateQuestion(newSelection);
						newQuestion = newSelection;
					} else {
						// Register a new question in the database
						newQuestion = new Question(titleInput, textInput, databaseHelper.currentUser.getUserId());
						databaseHelper.qaHelper.registerQuestion(newQuestion);

						// Retrieve full question object for new question
						newQuestion = databaseHelper.qaHelper.getQuestion(newQuestion.getTitle());
					}

					// Clear input fields for new inputs
					titleField.clear();
					inputField.clear();

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					qTable.getSelectionModel().select(newQuestion);

				} else if (updatingAnswer) {

					// Add updated text to answer object
					answer.setText(textInput);

					// Update answer
					databaseHelper.qaHelper.updateAnswer(answer);

					// Retrieve an updated list of questions from the database
					questions = databaseHelper.qaHelper.getAllQuestions();

					// Refresh contents of tables manually
					questionObservableList.clear();
					questionObservableList.addAll(questions);
					qTable.setItems(questionObservableList);

					// Set qTable selection to previous question
					qTable.getSelectionModel().select(question);

				} else if (updatingQuestion) {

					// Add updated title to question object
					question.setTitle(titleInput);

					// Add updated text to question object
					question.setText(textInput);

					// Update answer
					databaseHelper.qaHelper.updateQuestion(question);

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
				System.err.println("Error trying to register new question into database via submit button");
				;
				return;
			}

			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);

			// Set both updating flags to false
			updatingQuestion = false;
			updatingAnswer = false;

		});

		findReviewerButton.setOnAction(a -> {

			// Create a new stage in order to popup new window and keep this one
			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			new FindReviewerForQuestionPage(databaseHelper).show(newStage);

		});
		
		manageReviewersButton.setOnAction(a -> {
			// Create a new stage in order to popup new window and keep this one

			Stage newStage = new Stage();
			newStage.initStyle(StageStyle.TRANSPARENT);

			// Close the existing stage
			primaryStage.close();

			new ReviewerListPage(newStage, databaseHelper).show(databaseHelper.currentUser);
		});

		// Add listeners for the textArea title field
		titleField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
				// aTable.getSelectionModel().clearSelection();
			}
		});

		// Add listeners for the textArea input field
		inputField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
			if (isFocused) {
				qTable.getSelectionModel().clearSelection();
				// aTable.getSelectionModel().clearSelection();
			}
		});

		// Add listeners for table selections to read selected objects from tables
		qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				// Clear selections on the other tables
				// aTable.getSelectionModel().clearSelection();

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
				submitButton.setText("Update Question");

				// Update results table
				updateResultsTableForQuestion(newSelection);
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

		HBox reviewerButtonBox = new HBox(findReviewerButton);
		reviewerButtonBox.setAlignment(Pos.BOTTOM_RIGHT);
		
		HBox manageReviewerButtonBox = new HBox(manageReviewersButton);
		manageReviewerButtonBox.setAlignment(Pos.BOTTOM_RIGHT);

		HBox quitButtonBox = new HBox(quitButton);
		quitButtonBox.setAlignment(Pos.BOTTOM_LEFT);
		
		HBox askToBeAReviewerBox = new HBox(askToBeAReviewer);
		askToBeAReviewerBox.setAlignment(Pos.BOTTOM_CENTER);
		
		Button viewUnresolvedBtn = createViewUnresolvedButton();
		Button viewAllUnresolvedBtn = createViewAllUnresolvedButton();
		
		HBox buttonBox2 = new HBox(10, viewUnresolvedBtn, viewAllUnresolvedBtn);

		HBox buttonBox1 = new HBox(10, quitButtonBox, reviewerButtonBox, manageReviewerButtonBox, inboxButton, buttonBox2, askToBeAReviewerBox);
		quitButton.setAlignment(Pos.BOTTOM_LEFT);
		findReviewerButton.setAlignment(Pos.BOTTOM_RIGHT);		

		VBox vbox1 = new VBox(10, vbox, buttonBox1);
		vbox1.setAlignment(Pos.CENTER);

		// Hide submitBox unit needed
		submitBox.setVisible(false);
		submitBox.setManaged(false);

		newQuestionButton.setOnAction(a -> {
			// Set submit button text
			submitButton.setText("Submit Question");

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

		questionCloseButton.setOnAction(a -> {
			// Hide submitBox
			submitBox.setVisible(false);
			submitBox.setManaged(false);
		});

		StackPane root3 = new StackPane(questionDB, submitBox);
		
		HBox hbox1 = new HBox(10, root3, vbox1);

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
		contentColumn.prefWidthProperty().bind(vbox1.widthProperty().subtract(19));
		detailsColumn.prefWidthProperty().bind(questionDB.widthProperty().subtract(19));
		searchTable.prefWidthProperty().bind(vbox.widthProperty());
		searchTable.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		searchBox.prefWidthProperty().bind(vbox.widthProperty());
		searchBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		submitBox.prefWidthProperty().bind(qTable.widthProperty());
		submitBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
		questionInputBox.prefWidthProperty().bind(qTable.widthProperty());
		questionInputBox.prefHeightProperty().bind(layout.heightProperty().subtract(50));
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
	 * Updates the results table with the latest answers and reviews for the given question.
	 *
	 * <p>This method retrieves the updated question, its answers, and reviews from the database,
	 * then repopulates the observable list that backs the results table.</p>
	 *
	 * @param question the question whose related answers and reviews should be displayed.
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
	 * Displays a pop-up window listing unresolved questions for the current student.
	 *
	 * <p>This method retrieves unresolved questions from the database specific to the current user
	 * and presents them in a new stage. Double-clicking a question opens its potential answers.</p>
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
			myUnresolvedList = databaseHelper.qaHelper.getAllUnresolvedQuestionsForUser(
				databaseHelper.currentUser.getUserId()
			);
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
    
		// Set cell value factory to retrieve the title property from each Question object
		questCol.setCellValueFactory(cellData -> 
					     new SimpleStringProperty(cellData.getValue().getTitle())
					    );
		
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
	 * Displays a pop-up window listing potential (both unread and read) answers for the specified question.
	 *
	 * <p>This method creates a new stage with two tables: one for unread answers and one for read answers.
	 * It retrieves and shows the answers based on the current user's read status.</p>
	 *
	 * @param question the question for which to display potential answers.
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
	        Map<String, List<Answer>> answerMap = databaseHelper.qaHelper.getReadAndUnreadAnswers(
	            question.getId(), databaseHelper.currentUser.getUserId()
	        );

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
	    unreadCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getText())
	    );

	    TableColumn<Answer, String> readCol = new TableColumn<>("Read Answers");
	    readCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getText())
	    );

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
	 * Creates and returns a button that, when clicked, displays the current user's unresolved questions.
	 *
	 * @return a Button configured to show the unresolved questions for the current user.
	 */

	private Button createViewUnresolvedButton() {
	    Button viewUnresolvedBtn = new Button("View My Unresolved");
	    // Button text instructs that we are viewing the current user's unresolved questions

	    viewUnresolvedBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
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
	    // Button text indicates that this lists all unresolved questions (not just the current user's)

	    viewAllUnresolvedBtn.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
	    // Same style as other buttons associated

	    viewAllUnresolvedBtn.setOnAction(e -> {
	        showAllUnresolvedQuestions();
	        // When clicked, calls the method that displays every unresolved question
	    });
	    return viewAllUnresolvedBtn;
	    // Returns the new button for "View All Unresolved"
	}

	//Displays a pop-up window listing all unresolved questions for any user (User Story #2).
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

	    questCol.setCellValueFactory(cellData -> 
	        new SimpleStringProperty(cellData.getValue().getTitle())
	    );
	    // Binds the column to each Question's getTitle() method

	    TableColumn<Question, Number> potentialAnsCol = new TableColumn<>("Potential Answers");
	    // Column showing how many potential (unpreferred) answers each question has

	    potentialAnsCol.setCellValueFactory(cellData -> {
	        Question q = cellData.getValue();
	        int count = 0;
	        // Will track how many potential answers exist for this question

	        try {
	            List<Answer> potential = databaseHelper.qaHelper.getPotentialAnswersForQuestion(q.getId());
	            // Retrieves any answers that have not been chosen as the question's preferred answer
	            count = potential.size();
	            // Stores the number of those potential answers
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return new ReadOnlyObjectWrapper<>(count);
	        // Wraps the count in a ReadOnlyObjectWrapper so it can be displayed in the table cell
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
	 * Recursively processes and adds related answers to the results observable list.
	 *
	 * <p>This method retrieves answers that are related to the given parent answer and adds them
	 * to the results list, while removing them from the provided list to avoid duplication.</p>
	 *
	 * @param parentId the ID of the parent answer.
	 * @param answers  the list of answers to process.
	 * @return the remaining list of answers after processing related ones.
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
	
	/**
	 * Retrieves and sorts questions based on the reviewer's association with the current student.
	 *
	 * <p>This method gathers all answered questions and computes a score based on whether the answers
	 * were provided by reviewers associated with the current user. The questions are then sorted in
	 * descending order of the reviewer score.</p>
	 *
	 * @return a list of questions sorted by the maximum reviewer weight.
	 * @throws SQLException if a database access error occurs.
	 */

	private List<Question> getReviewersQuestions() throws SQLException {
		List<Question> allQuestions = databaseHelper.qaHelper.getAllAnsweredQuestions();
		List<Answer> allAnswers  = new ArrayList<>();
		for (Question q : allQuestions) {
			List<Answer> a = databaseHelper.qaHelper.getAllAnswersForQuestion(q.getId());
			allAnswers.addAll(a);
		}
		
		Map<User, Integer> myReviewers = databaseHelper.getAllReviewersForUser(databaseHelper.currentUser.getUserId()); 
		
		Map<Question, Integer> questionToReviewerScore = new HashMap<>();
		
		for (Question q : allQuestions) {
			List<Answer> answers = databaseHelper.qaHelper.getAllAnswersForQuestion(q.getId());

			for (Answer answer : answers) {
				User author = answer.getAuthor();
				Integer weight = myReviewers.get(author);

				if (weight != null) {
					questionToReviewerScore.merge(q, weight, Math::max);
				}
			}
		}
		
		
		return questionToReviewerScore.entrySet()
				.stream()
				.sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())) // descending
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}
	
}
