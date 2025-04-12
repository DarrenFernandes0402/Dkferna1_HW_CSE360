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
import javafx.scene.control.Alert;
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
import javafx.scene.control.TextInputDialog;
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
 * InstructorHomePage is responsible for displaying the instructor's home page 
 * interface. This page allows instructors to view and manage questions, reviews, 
 * and send requests to the Admin. It makes use of JavaFX for the UI components 
 * and interacts with the database via the provided DatabaseHelper instance.
 * <p>
 * The page supports functionalities such as:
 * <ul>
 *   <li>Viewing questions and reviews</li>
 *   <li>Searching questions</li>
 *   <li>Submitting and updating reviews</li>
 *   <li>Viewing unresolved questions</li>
 *   <li>Sending requests to the Admin</li>
 * </ul>
 * 
 * @author CSE 360 Team 8
 */
public class InstructorHomePage {

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

    private Answer tempAnswer;

    private TableView<QATableRow> resultsTable;

    private int indent = 1;
    
    /**
     * Constructs an InstructorHomePage instance with the specified DatabaseHelper.
     *
     * @param databaseHelper the DatabaseHelper instance used for all database operations
     */
    public InstructorHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    /**
     * Displays the Instructor Home Page on the provided primary stage.
     * This method initializes and arranges all UI components, sets up event
     * handlers, and manages the layout for the page.
     *
     * @param primaryStage the primary stage on which the UI will be displayed
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
        Label topLabel = new Label("Instructor Home Page");
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

        Button reviewerRequestButton = new Button("View Reviewer Requests");
        reviewerRequestButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black;");
        reviewerRequestButton.setOnAction(e -> {
            Stage stage = new Stage();
            new InstructorReviewerRequest(databaseHelper).show(stage);
        });
        
        Button requestsButton = new Button("View/Manage Admin Requests");
        requestsButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
        requestsButton.setOnAction(e -> showRequestsWindow());
        
        // Button to send a request to Admin
        Button sendRequestButton = new Button("Send Request to Admin");
        sendRequestButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
        sendRequestButton.setOnAction(e -> showRequestDialog());

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

        // Button to open the ui to submit a new question
        Button reviewCloseButton = new Button("Close");
        reviewCloseButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");

        // Table display of the question database
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

        // HBox to position the title
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

        // Table display of the reviews
        TableView<Review> rTable = new TableView<>();

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
        ObservableList<Review> reviewObservableList = FXCollections.observableArrayList(reviews);
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

        // Add columns to the rTable
        rTable.getColumns().addAll(reviewDetailsColumn);

        // Listener to dynamically hide the title bar of the reviews table
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
                            break;
                        case "My Reviews":
                            reviews = databaseHelper.qaHelper.getMyReviews();
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

        // HBox to hold and position the review filter controls
        HBox reviewTitleBox = new HBox(reviewFilterBox);
        reviewTitleBox.setStyle("-fx-background-color: derive(gray, 60%)");
        reviewCloseButton.setAlignment(Pos.TOP_RIGHT);
        submitButton.setAlignment(Pos.TOP_LEFT);

        // Container to hold the input box for reviews
        VBox rTableVBox = new VBox(5, rTable);
        rTableVBox.setStyle("-fx-background-color: derive(gray, 60%)");

        VBox rTableBox = new VBox(5, reviewTitleBox, rTableVBox);
        rTableBox.setStyle("-fx-background-color: derive(gray, 60%)");

        // HBox to hold and position the submit and close buttons for reviews
        HBox topTitleBox = new HBox(135, submitButton, reviewCloseButton);
        reviewCloseButton.setAlignment(Pos.TOP_RIGHT);
        submitButton.setAlignment(Pos.TOP_LEFT);

        // Container to hold the review input box
        VBox reviewInputBox = new VBox(5, topTitleBox, inputField);
        reviewInputBox.setStyle("-fx-background-color: derive(gray, 60%)");

        VBox submitBox = new VBox(reviewInputBox);

        TableColumn<QATableRow, String> contentColumn = new TableColumn<>("Results");
        contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getText()));

        // Add cell factory to deal with text wrapping and styling for the results table
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
                });
            }
            @Override
            protected void updateItem(String item, boolean flag) {
                super.updateItem(item, flag);
                // Clear container
                cellContent.getChildren().clear();
                cellBox.getChildren().clear();
                setGraphic(null);
                if (flag && item == null) {
                    setText(null);
                } else {
                    QATableRow row = getTableView().getItems().get(getIndex());
                    Label displayLabel = new Label(item);
                    displayLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
                    displayLabel.setWrapText(true);
                    displayLabel.maxWidthProperty().bind(contentColumn.widthProperty().subtract(50));
                    displayLabel.setPrefHeight(225);
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
                    if (getIndex() > 0) {
                        QATableRow currentRow = getTableView().getItems().get(getIndex());
                        QATableRow previousRow = getTableView().getItems().get(getIndex() - 1);
                        Region spacer = new Region();
                        spacer.setMinSize(25, 5);
                        spacer.setMaxSize(25, 5);
                        cellBox.getChildren().add(0, spacer);
                        if (getIndex() > 1) {
                            if (previousRow.getRelatedId() != null
                                    && previousRow.getRelatedId().contains(currentRow.getAnswerId().toString())) {
                                // Uncomment to adjust indentation if needed
                                // spacer.setMinSize(25 * indent, 5);
                                // spacer.setMaxSize(25 * indent, 5);
                                // indent += 1;
                            } else {
                                indent = 1;
                            }
                        }
                    }
                    if (row.getType() == QATableRow.RowType.REVIEW) {
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
                        
                        Button upVoteButton = new Button("\uD83D\uDC4D UpVote");
                        upVoteButton.setStyle(
                                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
                        Button downVoteButton = new Button("\uD83D\uDC4E DownVote");
                        downVoteButton.setStyle(
                                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1px;");
                        HBox voteBox = new HBox(5, upVoteButton, downVoteButton);
                        upVoteButton.setOnAction(a -> {
                            QATableRow currentRow = getTableView().getItems().get(getIndex());
                            databaseHelper.qaHelper.registerVoteForReview(currentRow.getReviewId(), 1);
                            try {
                                review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
                                questions = databaseHelper.qaHelper.getAllQuestions();
                                questionObservableList.clear();
                                questionObservableList.addAll(questions);
                                qTable.setItems(questionObservableList);
                                if (review.getForQuestion()) {
                                    qTable.getSelectionModel().select(databaseHelper.qaHelper.getQuestion(
                                            databaseHelper.qaHelper.getReview(currentRow.getReviewId()).getRelatedId()));
                                } else {
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
                        downVoteButton.setOnAction(a -> {
                            QATableRow currentRow = getTableView().getItems().get(getIndex());
                            databaseHelper.qaHelper.registerVoteForReview(currentRow.getReviewId(), -1);
                            try {
                                review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
                                questions = databaseHelper.qaHelper.getAllQuestions();
                                questionObservableList.clear();
                                questionObservableList.addAll(questions);
                                qTable.setItems(questionObservableList);
                                if (review.getForQuestion()) {
                                    qTable.getSelectionModel().select(databaseHelper.qaHelper.getQuestion(
                                            databaseHelper.qaHelper.getReview(currentRow.getReviewId()).getRelatedId()));
                                } else {
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
                    if (row.getType() == QATableRow.RowType.REVIEW && row.getAuthorId() != null
                            && row.getAuthorId().equals(databaseHelper.currentUser.getUserId())) {
                        Button editButton = new Button("Edit");
                        Button deleteButton = new Button("Delete");
                        editButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                                        + "-fx-font-weight: bold; -fx-padding: 1px;");
                        deleteButton.setStyle(
                                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                                        + "-fx-font-weight: bold; -fx-padding: 1px;");
                        editButton.setOnAction(a -> {
                            QATableRow currentRow = getTableView().getItems().get(getIndex());
                            submitButton.setText("Update Review");
                            updatingReview = true;
                            submitBox.setVisible(true);
                            submitBox.setManaged(true);
                            try {
                                review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
                            } catch (SQLException e) {
                                e.printStackTrace();
                                System.err.println("Error trying to get review in results table via editButton");
                            }
                            inputField.setText(review.getText());
                        });
                        deleteButton.setOnAction(a -> {
                            QATableRow currentRow = getTableView().getItems().get(getIndex());
                            try {
                                review = databaseHelper.qaHelper.getReview(currentRow.getReviewId());
                                questions = databaseHelper.qaHelper.getAllQuestions();
                                Question question = databaseHelper.qaHelper.getQuestion(review.getRelatedId());
                            } catch (SQLException e) {
                                e.printStackTrace();
                                System.err.println(
                                        "Error trying update questions object via getALLQuestions() in resultsTable");
                            }
                            databaseHelper.qaHelper.deleteReview(currentRow.getReviewId());
                            questionObservableList.clear();
                            questionObservableList.addAll(questions);
                            qTable.setItems(questionObservableList);
                            qTable.getSelectionModel().select(question);
                        });
                        HBox buttonBox = new HBox(1, editButton, deleteButton);
                        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
                        displayLabel.setAlignment(Pos.CENTER_LEFT);
                        cellContent.getChildren().addAll(displayLabel, buttonBox);
                        setGraphic(cellBox);
                        setText(null);
                    } else if (row.getType() == QATableRow.RowType.QUESTION) {
                        try {
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

        // Create and place a placeholder for when resultsTable is empty
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

        // HBox to position the filter button and new review button
        HBox titleBox2 = new HBox(70, newReviewButton, filterBox);
        titleBox2.setAlignment(Pos.CENTER);

        // Container to hold the question table
        VBox questionDB = new VBox(5, titleBox2, qTable);

        // Bind table heights to container sizes
        qTable.prefHeightProperty().bind(questionDB.heightProperty());

        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 20px;");
        errorLabel.setTranslateY(22);

        // "Back to login" button action
        quitButton.setOnAction(a -> {
            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.close();
            new UserLoginPage(databaseHelper).show(newStage);
        });

        // Create a search input field
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setStyle(
                "-fx-alignment: center-left; -fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1;");
        searchField.setPrefWidth(250);

        ObservableList<Question> searchObservableList = FXCollections.observableArrayList(questions);

        TableView<Question> searchTable = new TableView<>();
        searchTable.setItems(searchObservableList);
        searchTable.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width:  1;");

        HBox searchBox = new HBox(searchTable);

        TableColumn<Question, String> searchColumn = new TableColumn<>();
        searchColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().toDisplayWithText()));

        searchTable.setRowFactory(a -> new TableRow<Question>() {
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
        
        TableColumn<QATableRow, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(tc -> new TableCell<>() {
            private final ComboBox<String> actionCombo = new ComboBox<>();

            {
                actionCombo.getItems().addAll("Select Action", "Edit", "Delete");
                actionCombo.setValue("Select Action");
                actionCombo.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
                actionCombo.setOnAction(e -> {
                    QATableRow row = getTableView().getItems().get(getIndex());
                    String action = actionCombo.getValue();
                    if ("Edit".equals(action)) {
                        submitButton.setText("Update Review");
                        updatingReview = true;
                        submitBox.setVisible(true);
                        submitBox.setManaged(true);
                        try {
                            review = databaseHelper.qaHelper.getReview(row.getReviewId());
                            inputField.setText(review.getText());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    } else if ("Delete".equals(action)) {
                        try {
                            if (row.getType() == QATableRow.RowType.REVIEW) {
                                databaseHelper.qaHelper.deleteReview(row.getReviewId());
                                questions = databaseHelper.qaHelper.getAllQuestions();
                                questionObservableList.setAll(questions);
                                qTable.setItems(questionObservableList);
                            } else if (row.getType() == QATableRow.RowType.QUESTION) {
                                // Insert deletion logic for questions if required
                            } else if (row.getType() == QATableRow.RowType.ANSWER) {
                                // Insert deletion logic for answers if required
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    actionCombo.setValue("Select Action");
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionCombo);
                }
            }
        });
        resultsTable.getColumns().add(actionCol);

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
        searchTable.getColumns().add(searchColumn);

        searchTable.widthProperty().addListener((obs, oldVal, newVal) -> {
            Node titleBar = searchTable.lookup("TableHeaderRow");
            if (titleBar != null) {
                titleBar.setVisible(false);
                titleBar.setManaged(false);
                titleBar.setStyle("-fx-pref-height: 0; -fx-min-height: 0; -fx-max-height: 0;");
            }
        });
        searchBox.setVisible(false);
        searchBox.setManaged(false);

        searchField.setOnKeyReleased(a -> {
            String input = searchField.getText().trim();
            if (!input.isEmpty()) {
                sortedList = databaseHelper.qaHelper.searchQuestionDatabase(input);
                searchField.setMinWidth(searchTable.getWidth());
                searchBox.setVisible(true);
                searchBox.setManaged(true);
                if (sortedList != null && !sortedList.isEmpty()) {
                    searchObservableList.setAll(sortedList);
                    if (!searchBox.isVisible()) {
                        searchBox.setVisible(true);
                        searchBox.setManaged(true);
                    }
                }
            } else {
                searchObservableList.clear();
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                searchField.setMinWidth(250);
                searchField.setPrefWidth(250);
                searchField.setAlignment(Pos.TOP_RIGHT);
            }
        });

        searchField.setOnMouseClicked(a -> {
            qTable.getSelectionModel().clearSelection();
            searchBox.setVisible(true);
            searchBox.setManaged(true);
        });

        searchTable.setOnMouseClicked(a -> {
            Question selection = searchTable.getSelectionModel().getSelectedItem();
            if (selection != null) {
                questionObservableList.clear();
                questionObservableList.setAll(sortedList);
                qTable.setItems(questionObservableList);
                qTable.getSelectionModel().select(selection);
                filter.setValue(null);
            }
        });

        submitButton.setOnAction(a -> {
            question = new Question();
            review = new Review();
            String textInput = inputField.getText();
            Question qTableSelection = qTable.getSelectionModel().getSelectedItem();
            QATableRow resultsTableSelection = resultsTable.getSelectionModel().getSelectedItem();
            if (textInput == null || textInput.trim().isEmpty()) {
                errorLabel.setText("Error, review-body field is blank.");
                return;
            }
            try {
                if (!updatingReview) {
                    if (qTableSelection != null) {
                        review = new Review(true, qTableSelection.getId(), textInput,
                                databaseHelper.currentUser.getUserId());
                        databaseHelper.qaHelper.registerReview(review);
                        question = qTableSelection;
                    } else if (resultsTableSelection != null) {
                        if (resultsTableSelection.getType() == QATableRow.RowType.QUESTION) {
                            review = new Review(true, resultsTableSelection.getQuestionId(), textInput,
                                    databaseHelper.currentUser.getUserId());
                            databaseHelper.qaHelper.registerReview(review);
                            question = databaseHelper.qaHelper.getQuestion(resultsTableSelection.getQuestionId());
                        } else if (resultsTableSelection.getType() == QATableRow.RowType.ANSWER) {
                            review = new Review(
                                    true, databaseHelper.qaHelper
                                            .getQuestionForAnswer(resultsTableSelection.getAnswerId()).getId(),
                                    textInput, databaseHelper.currentUser.getUserId());
                            databaseHelper.qaHelper.registerReview(review);
                            question = databaseHelper.qaHelper
                                    .getQuestionForAnswer(resultsTableSelection.getAnswerId());
                        } else if (resultsTableSelection.getType() == QATableRow.RowType.REVIEW) {
                            review = new Review(true, resultsTableSelection.getQuestionId(), textInput,
                                    databaseHelper.currentUser.getUserId());
                            databaseHelper.qaHelper.registerReview(review);
                            question = databaseHelper.qaHelper.getQuestion(databaseHelper.qaHelper
                                    .getReview(resultsTableSelection.getReviewId()).getRelatedId());
                        }
                    }
                    inputField.clear();
                    questions = databaseHelper.qaHelper.getAllQuestions();
                    questionObservableList.clear();
                    questionObservableList.addAll(questions);
                    qTable.setItems(questionObservableList);
                    qTable.getSelectionModel().select(question);
                } else if (updatingReview) {
                    review.setText(textInput);
                    databaseHelper.qaHelper.updateReview(review);
                    question = databaseHelper.qaHelper.getQuestion(
                            databaseHelper.qaHelper.getReview(resultsTableSelection.getReviewId()).getRelatedId());
                    questions = databaseHelper.qaHelper.getAllQuestions();
                    questionObservableList.clear();
                    questionObservableList.addAll(questions);
                    qTable.setItems(questionObservableList);
                    qTable.getSelectionModel().select(question);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error trying to register new review into database via submit button");
                return;
            }
            submitBox.setVisible(false);
            submitBox.setManaged(false);
        });

        inputField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                qTable.getSelectionModel().clearSelection();
            }
        });

        qTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                searchField.setAlignment(Pos.TOP_RIGHT);
                searchField.setMinWidth(250);
                searchField.setPrefWidth(250);
                this.question = newSelection;
                submitButton.setText("Review");
                updateResultsTableForQuestion(newSelection);
            }
        });

        rTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                searchBox.setVisible(false);
                searchBox.setManaged(false);
                searchField.setAlignment(Pos.TOP_RIGHT);
                searchField.setMinWidth(250);
                searchField.setPrefWidth(250);
                submitButton.setText("Review");
                try {
                    if (newSelection.getForQuestion()) {
                        this.question = databaseHelper.qaHelper.getQuestion(newSelection.getRelatedId());
                    } else if (!newSelection.getForQuestion()) {
                        this.question = databaseHelper.qaHelper.getQuestionForAnswer(
                                databaseHelper.qaHelper.getAnswer(newSelection.getRelatedId()).getId());
                    }
                    updateResultsTableForQuestion(question);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        searchField.setAlignment(Pos.TOP_RIGHT);
        searchBox.setAlignment(Pos.BOTTOM_RIGHT);

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

        HBox buttonBox1 = new HBox(10, viewReviewsButton, quitButtonBox, inboxButton, buttonBox2, requestsButton, sendRequestButton, reviewerRequestButton);
        quitButton.setAlignment(Pos.BOTTOM_LEFT);

        VBox vbox1 = new VBox(10, vbox, buttonBox1);
        vbox1.setAlignment(Pos.CENTER);

        // Hide submitBox and rTableBox until needed
        submitBox.setVisible(false);
        submitBox.setManaged(false);
        rTableBox.setVisible(false);
        rTableBox.setManaged(false);

        newReviewButton.setOnAction(a -> {
            submitButton.setText("Submit Review");
            submitBox.setVisible(true);
            submitBox.setManaged(true);
        });

        inboxButton.setOnAction(a -> {
            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.TRANSPARENT);
            new Inbox(databaseHelper).show(newStage);
        });

        reviewCloseButton.setOnAction(a -> {
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

        // Window control buttons
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

        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);
        buttonBar.setAlignment(Pos.TOP_RIGHT);

        StackPane root = new StackPane(layout, buttonBar);
        root.setAlignment(buttonBar, Pos.TOP_RIGHT);
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

        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Updates the results table based on the specified question.
     * This method retrieves all answers and reviews related to the question,
     * then repopulates the results table accordingly.
     *
     * @param question the Question for which the results table should be updated
     */
    private void updateResultsTableForQuestion(Question question) {
        Answer duplicate = null;
        List<Review> reviews;
        resultsObservableList.clear();
        try {
            question = databaseHelper.qaHelper.getQuestion(question.getId());
            answers = databaseHelper.qaHelper.getAllAnswersForQuestion(question.getId());
            resultsObservableList.add(new QATableRow(QATableRow.RowType.QUESTION, question.toDisplayWithText(),
                    question.getId(), question.getAuthorId(), question.getRelatedId()));
            reviews = databaseHelper.qaHelper.getReviewsForQuestion(question.getId());
            if (!reviews.isEmpty()) {
                for (Review review : reviews) {
                    resultsObservableList.add(new QATableRow(QATableRow.RowType.REVIEW, review.toDisplayWithText(),
                            review.getId(), review.getAuthorId()));
                }
            }
            if (question.getPreferredAnswer() > 0) {
                answer = databaseHelper.qaHelper.getAnswer(question.getPreferredAnswer());
                duplicate = answer;
                answers.remove(answer);
                resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(), answer.getId(),
                        answer.getAuthorId(), answer.getRelatedId()));
                answers = addRelatedAnswers(answer.getId(), answers);
            }
            for (Answer answer : answers) {
                if (duplicate != null && !answer.getId().equals(duplicate.getId())) {
                    resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, answer.toDisplay(),
                            answer.getId(), answer.getAuthorId(), answer.getRelatedId()));
                }
                reviews = databaseHelper.qaHelper.getReviewsForAnswer(answer.getId());
                if (!reviews.isEmpty()) {
                    for (Review review : reviews) {
                        resultsObservableList.add(new QATableRow(QATableRow.RowType.REVIEW, review.toDisplayWithText(),
                                review.getId(), review.getAuthorId()));
                    }
                }
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
     * Displays a pop-up window showing the current instructor's unresolved questions.
     */
    private void showUnresolvedQuestionsForCurrentUser() {
        Stage stage = new Stage();
        stage.setTitle("My Unresolved Questions");
        Label heading = new Label("My Unresolved Questions");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TableView<Question> unresolvedTable = new TableView<>();
        unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
        unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        List<Question> myUnresolvedList;
        try {
            myUnresolvedList = databaseHelper.qaHelper
                    .getAllUnresolvedQuestionsForUser(databaseHelper.currentUser.getUserId());
        } catch (SQLException ex) {
            ex.printStackTrace();
            myUnresolvedList = new ArrayList<>();
        }
        ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(myUnresolvedList);
        unresolvedTable.setItems(unresolvedObs);
        TableColumn<Question, String> questCol = new TableColumn<>("Question Title");
        questCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        unresolvedTable.getColumns().add(questCol);
        unresolvedTable.setRowFactory(tv -> {
            TableRow<Question> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Question question = row.getItem();
                    showPotentialAnswersWindow(question);
                }
            });
            return row;
        });
        VBox layout = new VBox(10, heading, unresolvedTable);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(15, 15, 15, 15));
        Scene scene = new Scene(layout, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays a pop-up window showing both read and unread answers for the specified question.
     *
     * @param question the Question for which potential answers should be displayed
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
     * Creates and returns a Button for viewing the current user's unresolved questions.
     *
     * @return the Button configured to display the current user's unresolved questions
     */
    private Button createViewUnresolvedButton() {
        Button viewUnresolvedBtn = new Button("View My Unresolved");
        viewUnresolvedBtn.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
        viewUnresolvedBtn.setOnAction(e -> {
            showUnresolvedQuestionsForCurrentUser();
        });
        return viewUnresolvedBtn;
    }

    /**
     * Creates and returns a Button for viewing all unresolved questions.
     *
     * @return the Button configured to display all unresolved questions
     */
    private Button createViewAllUnresolvedButton() {
        Button viewAllUnresolvedBtn = new Button("View All Unresolved");
        viewAllUnresolvedBtn.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black; -fx-border-width: 1px;");
        viewAllUnresolvedBtn.setOnAction(e -> {
            showAllUnresolvedQuestions();
        });
        return viewAllUnresolvedBtn;
    }

    /**
     * Displays a pop-up window listing all unresolved questions for every user.
     */
    private void showAllUnresolvedQuestions() {
        Stage stage = new Stage();
        stage.setTitle("All Unresolved Questions");
        Label heading = new Label("All Unresolved Questions");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TableView<Question> unresolvedTable = new TableView<>();
        unresolvedTable.setStyle("-fx-border-color: black; -fx-text-fill: black; -fx-font-weight: bold;");
        unresolvedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        List<Question> allUnresolvedList;
        try {
            allUnresolvedList = databaseHelper.qaHelper.getAllUnresolvedQuestions();
        } catch (SQLException ex) {
            ex.printStackTrace();
            allUnresolvedList = new ArrayList<>();
        }
        ObservableList<Question> unresolvedObs = FXCollections.observableArrayList(allUnresolvedList);
        unresolvedTable.setItems(unresolvedObs);
        TableColumn<Question, String> questCol = new TableColumn<>("Question Title");
        questCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        TableColumn<Question, Number> potentialAnsCol = new TableColumn<>("Potential Answers");
        potentialAnsCol.setCellValueFactory(cellData -> {
            Question q = cellData.getValue();
            int count = 0;
            try {
                List<Answer> potential = databaseHelper.qaHelper.getPotentialAnswersForQuestion(q.getId());
                count = potential.size();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new ReadOnlyObjectWrapper<>(count);
        });
        unresolvedTable.getColumns().addAll(questCol, potentialAnsCol);
        unresolvedTable.setRowFactory(tv -> {
            TableRow<Question> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Question clickedQ = row.getItem();
                    showPotentialAnswersWindow(clickedQ);
                }
            });
            return row;
        });
        VBox layout = new VBox(10, heading, unresolvedTable);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(15, 15, 15, 15));
        Scene scene = new Scene(layout, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Recursively adds related answers for a given parent answer.
     * <p>
     * This method retrieves any answers that are related to the specified parent answer,
     * removes them from the given list, adds them to the results observable list, and
     * processes any nested related answers.
     * </p>
     *
     * @param parentId the ID of the parent answer
     * @param answers  the list of answers to process
     * @return the updated list of answers after removing processed related answers
     */
    private List<Answer> addRelatedAnswers(int parentId, List<Answer> answers) {
        try {
            List<Answer> relatedAnswers = databaseHelper.qaHelper.getAllAnswersForAnswer(parentId);
            for (Answer subAnswer : relatedAnswers) {
                answers.remove(subAnswer);
                resultsObservableList.add(new QATableRow(QATableRow.RowType.ANSWER, subAnswer.toDisplay(),
                        subAnswer.getId(), subAnswer.getAuthorId(), subAnswer.getRelatedId()));
                addRelatedAnswers(subAnswer.getId(), answers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error retrieving related answers in addRelatedAnswers()");
        }
        return answers;
    }
    
    /**
     * Opens a new window to display the instructor's requests.
     */
    private void showRequestsWindow() {
        Stage requestStage = new Stage();
        requestStage.initStyle(StageStyle.DECORATED);
        new InstructorRequest(databaseHelper).show(requestStage);
    }
    
    /**
     * Refreshes the specified table view with the latest requests belonging to the current instructor.
     *
     * @param tableView the TableView to be refreshed with request data
     */
    private void refreshTable(TableView<Request> tableView) {
        try {
            List<Request> openAndClosed = databaseHelper.getAllRequests();
            String myUserName = databaseHelper.currentUser.getUsername();
            openAndClosed.removeIf(r -> !r.getUserName().equalsIgnoreCase(myUserName));
            tableView.setItems(FXCollections.observableArrayList(openAndClosed));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Opens a dialog for the instructor to enter a request and submit it to the Admin.
     */
    private void showRequestDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send Request to Admin");
        dialog.setHeaderText("Enter your request for Admin:");
        dialog.setContentText("Request:");
        dialog.showAndWait().ifPresent(requestText -> {
            if (!requestText.trim().isEmpty()) {
                try {
                    databaseHelper.createNewRequest(requestText, databaseHelper.currentUser.getUsername());
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Request Sent");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Your request has been successfully sent to the Admin.");
                    successAlert.showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error Sending Request");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("There was an error sending your request. Please try again.");
                    errorAlert.showAndWait();
                }
            } else {
                Alert emptyAlert = new Alert(Alert.AlertType.WARNING);
                emptyAlert.setTitle("Empty Request");
                emptyAlert.setHeaderText(null);
                emptyAlert.setContentText("Request cannot be empty. Please enter a valid request.");
                emptyAlert.showAndWait();
            }
        });
    }
}
