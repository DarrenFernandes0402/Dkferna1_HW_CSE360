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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/** 
 * The inbox class shows all user generated message that are related to the
 * target question.
 */

public class Inbox {
    private final DatabaseHelper databaseHelper;

    public Inbox(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	double[] offsetX = { 0 };
		double[] offsetY = { 0 };
		
        TableView<Message> table = new TableView<>();
        List<Message> messageList = new ArrayList<>();

        // Label to display title to user
        
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

        // TableColumn for Sender
        TableColumn<Message, String> senderColumn = new TableColumn<>("From");
        senderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getSender() != null ? cellData.getValue().getSender().getUsername() : "Unknown"
        ));

        // TableColumn for Recipient
        TableColumn<Message, String> recipientColumn = new TableColumn<>("To");
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
            cellData.getValue().getRecipient() != null ? cellData.getValue().getRecipient().getUsername() : "Unknown"
        ));
        recipientColumn.setVisible(false); // Hide column

        // TableColumn for Subject
        TableColumn<Message, String> subjectColumn = new TableColumn<>("Subject");
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectColumn.setPrefWidth(175);

        // TableColumn for Message Content
        TableColumn<Message, String> messageColumn = new TableColumn<>("Message");
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        messageColumn.setPrefWidth(618);

        // Fixed row height
        table.setFixedCellSize(70);  

        // Add columns to the table
        table.getColumns().addAll(messageIdColumn, senderColumn, recipientColumn, subjectColumn, messageColumn);

        ObservableList<Message> messageObservableList = FXCollections.observableArrayList(messageList);
        table.setItems(messageObservableList);

        // Buttons
        Button readButton = new Button("Read");
        readButton.setStyle("-fx-text-fill: white; -fx-background-color: green; -fx-font-weight: bold;");
        
        Button replyButton = new Button("Reply");
        replyButton.setStyle("-fx-text-fill: white; -fx-background-color: blue; -fx-font-weight: bold;");
        
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-text-fill: white; -fx-background-color: red; -fx-font-weight: bold;");

        // Disable buttons by default until a message is selected
        readButton.setDisable(true);
        replyButton.setDisable(true);
        deleteButton.setDisable(true);

        // Enable Buttons on Selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            readButton.setDisable(!hasSelection);
            replyButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });

        // Read Button Action
        readButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage detailsStage = new Stage();
                new ReadMessagePage(databaseHelper, selectedMessage).show(detailsStage);
            }
        });

        // Reply Button Action
        replyButton.setOnAction(event -> {
            Message selectedMessage = table.getSelectionModel().getSelectedItem();
            if (selectedMessage != null) {
                Stage replyStage = new Stage();
                new CreateMessagePage(databaseHelper, selectedMessage.getSenderID()).show(replyStage);
            }
        });

        // Delete Button Action
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

        // Layout
        HBox buttonBox = new HBox(10, readButton, replyButton, deleteButton);
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        VBox layout = new VBox(10, table, buttonBox);
        layout.setAlignment(Pos.CENTER);
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

        Scene scene = new Scene(root, 1300, 550);
        scene.setFill(Color.TRANSPARENT);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Inbox");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
