package application;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Handles the display and management of reviewer requests submitted by instructors.
 * <p>
 * This class creates a JavaFX interface to show pending reviewer requests and 
 * allows the instructor to accept or decline each request.
 * When a request is accepted, the corresponding reviewer role is added and the
 * request is closed with an appropriate note. When declined, the request is deleted
 * from the database.
 * </p>
 */
public class InstructorReviewerRequest {

    /**
     * Helper instance for performing database operations.
     */
    private final DatabaseHelper databaseHelper;
    
    /**
     * Tracks processed request IDs without modifying the Request class.
     */
    private final HashSet<Integer> processedRequestIds = new HashSet<>();

    /**
     * Constructs a new InstructorReviewerRequest with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the helper for performing database operations.
     */
    public InstructorReviewerRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the reviewer requests in a JavaFX {@code TableView}. Provides options to
     * accept or decline each request. When a request is accepted, the corresponding reviewer
     * role is added and the request is closed with an appropriate note. When declined, the
     * request is removed from the database.
     *
     * @param primaryStage the primary stage for the JavaFX application.
     */
    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        // Username column showing the user's username.
        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUsername()));

        // Request column showing the request details.
        TableColumn<Request, String> requestCol = new TableColumn<>("Request");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("request"));
        requestCol.setPrefWidth(300);

        // Status column showing the current status of the request.
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));

        // Notes column showing any notes associated with the request.
        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));
        notesCol.setPrefWidth(200);

        // Accept column with a button to accept the request.
        TableColumn<Request, Void> accept = new TableColumn<>("Accept");
        accept.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button acceptButton = new Button("Accept");

            {
                acceptButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    String username = req.getUser().getUsername();
                    try {
                        System.out.println("Accepting request for: " + username);

                        // Update the request status to accepted (assumed to update status to "CLOSED")
                        databaseHelper.updateRequestStatus(username, false, true);

                        // Add the Reviewer role (capitalized to match AdminHomePage)
                        boolean success = databaseHelper.addRoles(username, "Reviewer");
                        System.out.println("Reviewer role added? " + success);

                        // Close the request with an appropriate note (assumed to set status to "CLOSED")
                        databaseHelper.closeRequest(req.getId(), "Instructor accepted reviewer request.");

                        // Refresh the table to show updated requests
                        refreshTable(getTableView());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request req = getTableView().getItems().get(getIndex());
                    // Debug print to check the request's status
                    System.out.println("Request ID: " + req.getId() + ", Status: " + req.getStatus());
                    // Show the button only if status is null or equals "OPEN" (ignoring case)
                    if (req.getStatus() == null || req.getStatus().equalsIgnoreCase("OPEN")) {
                        setGraphic(acceptButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Decline column with a button to decline the request.
        TableColumn<Request, Void> declineCol = new TableColumn<>("Decline");
        declineCol.setCellFactory(tc -> new TableCell<Request, Void>() {
            private final Button declineButton = new Button("Decline");

            {
                declineButton.setOnAction(e -> {
                    Request req = getTableView().getItems().get(getIndex());
                    // Delete the request from the database (or update its status accordingly)
                    databaseHelper.deleteRequest(req.getUserName());

                    // Refresh the table to show updated requests
                    refreshTable(getTableView());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Request req = getTableView().getItems().get(getIndex());
                    // Only show the button if status is null or equals "OPEN" (ignoring case)
                    if (req.getStatus() == null || req.getStatus().equalsIgnoreCase("OPEN")) {
                        setGraphic(declineButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Add all columns to the table view.
        tableView.getColumns().addAll(usernames, requestCol, statusCol, notesCol, accept, declineCol);
        refreshTable(tableView);

        // Back button to return to the instructor home page.
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        });
        
        VBox root = new VBox(10, tableView, new HBox(5, backButton));
        primaryStage.setTitle("Reviewer Requests");
        primaryStage.setScene(new Scene(root, 850, 450));
        primaryStage.show();
    }

    /**
     * Refreshes the TableView with the latest reviewer requests from the database.
     * Retrieves all reviewer requests and updates the table view's items.
     *
     * @param tableView the TableView to be refreshed.
     */
    private void refreshTable(TableView<Request> tableView) {
        try {
            List<Request> reviewerRequests = databaseHelper.getAllReviewerRequests();
            ObservableList<Request> data = FXCollections.observableArrayList(reviewerRequests);
            tableView.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
