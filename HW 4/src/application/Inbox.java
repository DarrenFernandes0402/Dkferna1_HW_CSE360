package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * The {@code Inbox} class displays the Inbox view for a user.
 * <p>
 * It shows all messages related to the current user in a table, allowing actions
 * such as reading, replying, and deleting messages.
 * </p>
 */
public class Inbox {
    
    /**
     * The helper used to interact with the database.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs the {@code Inbox} view with a given database helper.
     *
     * @param databaseHelper The helper used to interact with the database.
     */
    public Inbox(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Inbox window on the specified stage.
     * <p>
     * This method creates a table to display the messages, along with buttons to read,
     * reply, or delete a selected message. Custom window styling and controls (minimize,
     * maximize, close) are also applied, and the layout supports window dragging.
     * </p>
     *
     * @param primaryStage The stage where the inbox should be shown.
     */
    public void show(Stage primaryStage) {
        // Variables to track mouse offset for window dragging
        double[] offsetX = { 0 };
        double[] offsetY = { 0 };

        // Create a TableView to display messages
        TableView<Message> table = new TableView<>();
        List<Message> messageList = new ArrayList<>();

        // Retrieve messages for the current user from the database
        try {
            messageList = databaseHelper.qaHelper.retrieveMessagesByUserId(databaseHelper.currentUser.getUserId());
        } catch (SQLException e) {
            System.out.println("Error fetching messages from the database.");
            e.printStackTrace();
        }

        // TableColumn for Message ID
        TableColumn<Message, Integer> messageIdColumn = new TableColumn<>("MsgID");
        messageIdColumn.setCellValueFactory(new PropertyValueFactory<>("messageID"));
        messageIdColumn.setPrefWidth(50);

        // TableColumn for Sender Username
        TableColumn<Message, String> senderColumn = new TableColumn<>("From");
        senderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getSender() != null ? cellData.getValue().getSender().getUsername() : "Unknown"
        ));

        // TableColumn for Recipient Username (hidden)
        TableColumn<Message, String> recipientColumn = new TableColumn<>("To");
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getRecipient() != null ? cellData.getValue().getRecipient().getUsername() : "Unknown"
        ));
        recipientColumn.setVisible(false); // Hide column

        // TableColumn for Reference (combination of reference type and ID)
        TableColumn<Message, String> referenceColumn = new TableColumn<>("Reference");
        referenceColumn.setCellValueFactory(cellData -> {
            Message msg = cellData.getValue();
            String refDisplay = msg.getReferenceType() + msg.getReferenceID();
            if (msg.getReferenceType() == null || msg.getReferenceType().isBlank() || msg.getReferenceID() <= 0) {
                refDisplay = "-";
            }
            return new SimpleStringProperty(refDisplay);
        });
        referenceColumn.setPrefWidth(80);

        // TableColumn for Subject
        TableColumn<Message, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectColumn.setPrefWidth(175);

        // TableColumn for Message Content
        TableColumn<Message, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(618);

        // Set fixed row height for consistency
        table.setFixedCellSize(70);

        // Add all columns to the table
        table.getColumns().addAll(messageIdColumn, senderColumn, referenceColumn, recipientColumn, subjectColumn, messageColumn);

        // Populate table with messages
        ObservableList<Message> messageObservableList = FXCollections.observableArrayList(messageList);
        table.setItems(messageObservableList);

        // Create buttons for inbox actions
        Button readButton = new Button("Read");
        readButton.setStyle("-fx-text-fill: white; -fx-background-color: green; -fx-font-weight: bold;");
        
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold;");
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");

        // Disable buttons until a message is selected
        readButton.setDisable(true);
        replyButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable action buttons when a table row (message) is selected
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            readButton.setDisable(!hasSelection);
            replyButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        // Define action for the Read button
        readButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage detailsStage = new Stage();
                new ReadMessagePage(databaseHelper, selectedMessage).show(detailsStage);
            }
        });

        // Define action for the Reply button
        replyButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage replyStage = new Stage();
                int recipientId = selectedMessage.getSenderID();
                int referenceId = selectedMessage.getMessageID();
                String referenceType = "Message";
                new CreateMessagePage(databaseHelper, recipientId, referenceId, referenceType).show(replyStage);
            }
        });

        // Define action for the Delete button
        deleteButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Deletion");
                alert.setHeaderText("Are you sure you want to delete this message?");
                alert.setContentText("Message ID: " + selectedMessage.getMessageID());

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        if (databaseHelper.qaHelper.deleteMessage(selectedMessage.getMessageID())) {
                            table.getItems().remove(selectedMessage);
                            System.out.println("Message deleted: " + selectedMessage.getMessageID());
                        } else {
                            System.out.println("Failed to delete message.");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Layout for the action buttons
        HBox buttonBox = new HBox(10, readButton, replyButton, deleteButton);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        // Main layout container combining the message table and button box
        VBox layout = new VBox(10, table, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setMinSize(1300, 900);
        layout.setMaxSize(1300, 900);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");

        // Enable window dragging by tracking mouse press and drag events
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });

        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Create window control buttons (close, maximize, minimize)
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

        // Configure hover effects for window control buttons
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
        minButton.setOnAction(a -> primaryStage.setIconified(true));

        // Container for window control buttons
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);

        // Spacer to push the title bar to the top
        Region spacer = new Region();
        spacer.setMinHeight(26);
        spacer.setMaxHeight(26);

        // Layout container including spacer and main layout
        VBox layoutBox = new VBox(spacer, layout);
        layoutBox.setAlignment(Pos.CENTER);

        // StackPane to manage overall layout and position window controls
        StackPane root = new StackPane(layoutBox, buttonBar);
        root.setAlignment(buttonBar, Pos.TOP_RIGHT);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));

        Scene scene = new Scene(root, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inbox");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
