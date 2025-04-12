package application;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import databasePart1.DatabaseHelper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * The {@code InstructorRequest} class is responsible for displaying all user-submitted
 * requests to the instructor in a tabular format using JavaFX. This includes the username,
 * request message, current status, and optional notes.
 * <p>
 * The instructor can view this information and navigate back to the instructor's home page.
 * Additionally, the instructor can reopen a CLOSED request, providing additional notes.
 * </p>
 * 
 * Updated to update an existing request (rather than creating a new one) when a CLOSED request is reopened,
 * and to allow the instructor to input additional notes during the reopen process.
 * 
 * @author CSE 360 Team 8
 */
public class InstructorRequest {

    private final DatabaseHelper databaseHelper;

    /**
     * Constructs an InstructorRequest with a given {@code DatabaseHelper} instance.
     *
     * @param databaseHelper the database helper used to fetch requests
     */
    public InstructorRequest(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the JavaFX window showing all requests in a {@code TableView}.
     * Each row displays the user's name, request content, status, and notes.
     * The instructor can click "Back" to return to the home page.
     *
     * @param primaryStage the primary JavaFX stage to show the request window
     */
    public void show(Stage primaryStage) {
        TableView<Request> tableView = new TableView<>();

        // Username column
        TableColumn<Request, String> usernames = new TableColumn<>("Username");
        usernames.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getUser().getUsername()));

        // Request column
        TableColumn<Request, String> requestCol = new TableColumn<>("Request");
        requestCol.setCellValueFactory(new PropertyValueFactory<>("request"));
        requestCol.setPrefWidth(350);

        // Status column
        TableColumn<Request, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getStatus()));
        statusCol.setPrefWidth(100);

        // Notes column
        TableColumn<Request, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getNotes()));
        notesCol.setPrefWidth(200);
        
        // Reopen button column
        TableColumn<Request, Void> reopenCol = new TableColumn<>("Reopen");
        reopenCol.setPrefWidth(100);  // Ensure the column is visible.

        /**
         * Creates a TableCell that displays a "Reopen" button if the request status is CLOSED.
         * When clicked, it prompts the instructor for additional notes and then reopens the request.
         */
        Callback<TableColumn<Request, Void>, TableCell<Request, Void>> cellFactory =
                new Callback<TableColumn<Request, Void>, TableCell<Request, Void>>() {
                    @Override
                    public TableCell<Request, Void> call(final TableColumn<Request, Void> param) {
                        return new TableCell<Request, Void>() {

                            private final Button btn = new Button("Reopen");

                            {
                                btn.setOnAction((ActionEvent event) -> {
                                    Request req = getTableView().getItems().get(getIndex());
                                    if ("CLOSED".equalsIgnoreCase(req.getStatus())) {
                                        // Prompt the instructor for additional notes when reopening a request.
                                        TextInputDialog dialog = new TextInputDialog();
                                        dialog.setTitle("Reopen Request");
                                        dialog.setHeaderText("Reopen Request");
                                        dialog.setContentText("Enter additional notes:");
                                        Optional<String> result = dialog.showAndWait();
                                        
                                        // Only proceed if the instructor entered notes (or left it blank intentionally)
                                        if (result.isPresent()) {
                                            String additionalNote = result.get().trim();
                                            try {
                                                String updatedDescription = req.getRequest();
                                                String reopenedBy = req.getUser().getUsername();
                                                // Update the current request in the database
                                                databaseHelper.reopenRequest(req.getId(), updatedDescription, additionalNote, reopenedBy);
                                                
                                                // Update the current request object instead of removing it:
                                                req.setStatus("OPEN");
                                                req.setNotes(additionalNote);
                                                getTableView().refresh();
                                                
                                                System.out.println("Request " + req.getId() + " reopened.");
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                                alert.setTitle("Error");
                                                alert.setHeaderText("Reopen Request Failed");
                                                alert.setContentText(e.getMessage());
                                                alert.showAndWait();
                                            }
                                        }
                                    }
                                });
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    Request req = getTableView().getItems().get(getIndex());
                                    // Show the button only if the status is CLOSED.
                                    if ("CLOSED".equalsIgnoreCase(req.getStatus())) {
                                        setGraphic(btn);
                                    } else {
                                        setGraphic(null);
                                    }
                                }
                            }
                        };
                    }
                };
        reopenCol.setCellFactory(cellFactory);

        tableView.getColumns().addAll(usernames, requestCol, statusCol, notesCol, reopenCol);

        ObservableList<Request> data = FXCollections.observableArrayList();

        try {
            List<Request> userRequests = databaseHelper.getAllRequests();
            data.addAll(userRequests);
        } catch (SQLException e) {
            e.printStackTrace(); // Consider logging in production
        }

        tableView.setItems(data);

        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        backButton.setOnAction(e -> {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
        });

        HBox hbox = new HBox(5, backButton);
        VBox root = new VBox(tableView, hbox);
        Scene scene = new Scene(root, 900, 400);
        primaryStage.setTitle("My Requests");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
