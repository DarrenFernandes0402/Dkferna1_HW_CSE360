package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Provides an administrative interface for managing user-submitted requests.
 * Admins can view, close requests with optional notes, and delete questions, answers, or reviews.
 * 
 * <p>Features include:
 * <ul>
 *     <li>Table display of all user requests</li>
 *     <li>Option to close open/reopened requests</li>
 *     <li>Deletion panel for database entries</li>
 *     <li>Navigation back to admin home</li>
 * </ul>
 *
 * @author CSE 360 Team 8
 */
public class AdminRequests {
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs an AdminRequests instance with the specified database helper.
     *
     * @param databaseHelper an instance of DatabaseHelper for DB operations
     */
    public AdminRequests(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Admin Requests interface on the provided JavaFX stage.
     *
     * @param primaryStage the main JavaFX window where the UI is rendered
     */
    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        // Username column
        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getUserName()));

        // Request text column
        TableColumn<Request, String> requestCol = new TableColumn<>("Request");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("request"));
        requestCol.setPrefWidth(300);

        // Status column
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));

        // Notes column
        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setPrefWidth(200);
        notesCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));

        // Close button column
        TableColumn<Request, Void> closeCol = new TableColumn<>("Close");
        closeCol.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button closeButton = new Button("Close Request");

            {
                closeButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    TextInputDialog dialog = new TextInputDialog("Add final admin notes...");
                    dialog.setTitle("Close Request");
                    dialog.setHeaderText("Optionally add notes before closing");
                    dialog.setContentText("Notes:");
                    dialog.showAndWait().ifPresent(note -> {
                        try {
                            databaseHelper.closeRequest(req.getId(), note);
                            refreshTableData(tableView);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request r = getTableView().getItems().get(getIndex());
                    if ("OPEN".equalsIgnoreCase(r.getStatus()) || "REOPENED".equalsIgnoreCase(r.getStatus())) {
                        setGraphic(closeButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        tableView.getColumns().addAll(usernames, requestCol, statusCol, notesCol, closeCol);

        // Delete controls
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Question", "Answer", "Review");
        typeComboBox.setPromptText("Select Type");

        TextField idInputField = new TextField();
        idInputField.setPromptText("Enter ID to delete");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            String type = typeComboBox.getValue();
            String idText = idInputField.getText();

            if (type == null || idText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please select a type and enter a valid ID.");
                return;
            }

            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid ID", "ID must be an integer.");
                return;
            }

            boolean result = false;
            switch (type.toLowerCase()) {
                case "question":
                    result = databaseHelper.qaHelper.deleteQuestion(id);
                    break;
                case "answer":
                    result = databaseHelper.qaHelper.deleteAnswer(id);
                    break;
                case "review":
                    result = databaseHelper.qaHelper.deleteReview(id);
                    break;
                default:
                    showAlert(Alert.AlertType.ERROR, "Invalid Type", "Unrecognized type selection.");
                    return;
            }

            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "Success", type + " with ID " + id + " was deleted successfully.");
                refreshTableData(tableView);
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to delete the specified item.");
            }
        });

        HBox deleteBox = new HBox(10, typeComboBox, idInputField, deleteButton);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(a -> {
            primaryStage.close();
            new AdminHomePage(databaseHelper).show(primaryStage, databaseHelper.currentUser);
        });

        HBox backBox = new HBox(5, backButton);
        backBox.setAlignment(Pos.CENTER);

        // Layout setup
        VBox root = new VBox(10, tableView, deleteBox, backBox);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Load data
        refreshTableData(tableView);

        Scene scene = new Scene(root, 900, 400);
        primaryStage.setTitle("Admin Requests");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Refreshes the table view with the latest requests from the database.
     *
     * @param tableView the table view to populate
     */
    private void refreshTableData(TableView<Request> tableView) {
        try {
            List<Request> userRequests = databaseHelper.getAllRequests();
            tableView.setItems(FXCollections.observableArrayList(userRequests));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a JavaFX alert popup with the given type, title, and message.
     *
     * @param alertType type of the alert (e.g., ERROR, INFORMATION)
     * @param title     title of the alert window
     * @param message   message to display in the alert
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
