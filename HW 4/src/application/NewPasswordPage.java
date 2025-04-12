package application;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The {@code NewPasswordPage} class provides a user interface for resetting a user's password.
 * <p>
 * The page includes two password fields for entering and confirming the new password. When the "Save"
 * button is clicked, the new password is validated using {@code PasswordEvaluator}. If valid and matching,
 * the password is updated in the database, the OTP flag is reset, and the user is redirected back to the
 * login page.
 * </p>
 */
public class NewPasswordPage {
    /**
     * The {@code DatabaseHelper} instance used for database operations such as updating the password.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a {@code NewPasswordPage} with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the DatabaseHelper used for password updates and related operations.
     */
    public NewPasswordPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the New Password Page on the provided stage.
     * <p>
     * The page includes input fields for entering and confirming the new password, along with labels for the title
     * and error messages. The "Save" button validates the passwords via {@code PasswordEvaluator}, updates the password,
     * resets the OTP flag, and navigates back to the login page if successful.
     * </p>
     *
     * @param primaryStage the primary stage where the page will be displayed.
     * @param user         the User object for which the password is being reset.
     */
    public void show(Stage primaryStage, User user) {
        double[] offsetX = {0};
        double[] offsetY = {0};

        // Create the password field for entering the new password.
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        passwordField.setPromptText("Enter New Password");
        passwordField.setMaxWidth(200);
        passwordField.setAlignment(Pos.CENTER);

        // Create the password field for confirming the new password.
        PasswordField passwordValidField = new PasswordField();
        passwordValidField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        passwordValidField.setPromptText("Confirm New Password");
        passwordValidField.setMaxWidth(200);
        passwordValidField.setAlignment(Pos.CENTER);

        // Label to display the title of the page.
        Label prompt = new Label("Password Reset");
        prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
        prompt.setAlignment(Pos.CENTER);

        // Label to display error or success messages.
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        // Button to save the new password.
        Button saveChanges = new Button("Save");
        saveChanges.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        // Set this button as the default so that pressing Enter triggers it.
        saveChanges.setDefaultButton(true);

        // Handle the "Save" button action.
        saveChanges.setOnAction(a -> {
            // Retrieve user inputs from both password fields.
            String password = passwordField.getText();
            String passwordValid = passwordValidField.getText();

            String passwordValidate = PasswordEvaluator.evaluatePassword(password);

            if (!passwordValidate.isEmpty()) {
                errorLabel.setText("***ERROR*** Invalid password");
                return;
            } else {
                if (passwordValid.equals(password)) {
                    errorLabel.setText("***Success*** Password Changed");
                    databaseHelper.updatePassword(user.getUsername(), password);
                    databaseHelper.updateOTPFlag(user.getUsername(), false);
                    new UserLoginPage(databaseHelper).show(primaryStage);
                    return;
                }
            }
        });

        // Set up the layout of the page.
        VBox layout = new VBox(10);
        layout.getChildren().addAll(prompt, passwordField, passwordValidField, saveChanges, errorLabel);
        layout.setMinSize(400, 220);
        layout.setMaxSize(400, 220);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");
        layout.setAlignment(Pos.CENTER);

        // Enable window dragging by tracking mouse press and drag events.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Create window control buttons for closing, maximizing, and minimizing the window.
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);
        closeButton.setOnMouseEntered(a -> {
            closeButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            closeButton.setMinSize(25, 25);
            closeButton.setMaxSize(25, 25);
        });
        closeButton.setOnMouseExited(a -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            closeButton.setMinSize(25, 25);
            closeButton.setMaxSize(25, 25);
        });
        closeButton.setOnAction(a -> {
            primaryStage.close();
        });

        Button maxButton = new Button("🗖");
        maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);
        maxButton.setOnMouseEntered(a -> {
            maxButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            maxButton.setMinSize(25, 25);
            maxButton.setMaxSize(25, 25);
        });
        maxButton.setOnMouseExited(a -> {
            maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            maxButton.setMinSize(25, 25);
            maxButton.setMaxSize(25, 25);
        });
        maxButton.setOnAction(a -> {
            primaryStage.setMaximized(!primaryStage.isMaximized());
        });

        Button minButton = new Button("_");
        minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);
        minButton.setOnMouseEntered(a -> {
            minButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            minButton.setMinSize(25, 25);
            minButton.setMaxSize(25, 25);
        });
        minButton.setOnMouseExited(a -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            minButton.setMinSize(25, 25);
            minButton.setMaxSize(25, 25);
        });
        minButton.setOnAction(a -> {
            primaryStage.setIconified(true);
        });

        // Create an HBox container for the window control buttons.
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);
        
        // Create a spacer to push the button bar to the far right.
        HBox spacer = new HBox(buttonBar);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleBar = new HBox(spacer, buttonBar);
        titleBar.setMinHeight(35);
        titleBar.setMaxHeight(35);
        titleBar.setMaxWidth(600);
        
        // Create a vertical spacer to push the title bar to the top.
        VBox spacer1 = new VBox();
        spacer1.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        
        VBox titleBox = new VBox(titleBar, spacer1);
        titleBox.setAlignment(Pos.CENTER);

        // Set alignment for elements in the title bar.
        titleBar.setAlignment(Pos.TOP_CENTER);
        spacer.setAlignment(Pos.TOP_LEFT);
        buttonBar.setAlignment(Pos.TOP_RIGHT);

        // Create a StackPane for overall layout.
        StackPane root = new StackPane(titleBox, layout);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));
        
        titleBox.prefWidthProperty().bind(root.widthProperty());
        titleBox.prefHeightProperty().bind(root.heightProperty());

        // Remove any icons from the window's title bar.
        primaryStage.getIcons().clear();
        
        Scene scene = new Scene(root, 940, 400);
        scene.setFill(Color.TRANSPARENT);
        
        primaryStage.setScene(scene);		
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
