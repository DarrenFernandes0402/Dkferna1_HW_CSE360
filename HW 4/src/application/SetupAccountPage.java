package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import databasePart1.DatabaseHelper;

/**
 * The {@code SetupAccountPage} class handles the account setup process for new users.
 * <p>
 * Users provide their username, name, email, password, and a valid invitation code to register.
 * This class validates the input using FSM-based validators for username, name, password, and email.
 * If all validations pass and the invitation code is valid, a new user is registered in the database.
 * Upon successful registration, the user is directed to the login page.
 * </p>
 */
public class SetupAccountPage {

    /**
     * The {@code DatabaseHelper} instance used to perform all database operations.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a new {@code SetupAccountPage} with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the DatabaseHelper used for interacting with the database.
     */
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page on the provided stage.
     * <p>
     * The page provides input fields for username, name, email, password, and invitation code.
     * It validates each input and if valid, registers the new user in the database.
     * After a successful registration, the user is redirected to the login page.
     * </p>
     *
     * @param primaryStage the stage where the Setup Account page will be displayed.
     */
    public void show(Stage primaryStage) {
        double[] offsetX = {0};
        double[] offsetY = {0};

        // Title label.
        Label title = new Label("New User");
        title.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);

        // Input field for username.
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        userNameField.setMaxWidth(200);

        // Input field for full name.
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");
        nameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        nameField.setMaxWidth(200);

        // Input field for email.
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        emailField.setMaxWidth(200);

        // Password field.
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        passwordField.setMaxWidth(200);

        // Input field for invitation code.
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter Invitation Code");
        inviteCodeField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        inviteCodeField.setMaxWidth(200);

        // Label to display error messages.
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");
        errorLabel.setAlignment(Pos.CENTER);

        // Button to submit the account setup.
        Button setupButton = new Button("Setup");
        setupButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        setupButton.setDefaultButton(true);

        // Button to return to the login page.
        Button quitButton = new Button("Back to login");
        quitButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Quit button action: return to the login page.
        quitButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Action for the setup button.
        setupButton.setOnAction(a -> {
            // Retrieve user input.
            String userName = userNameField.getText();
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();

            // Validate input using FSM validators.
            String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);
            String nameValidate = NameValidator.checkForValidName(name);
            String passwordValidate = PasswordEvaluator.evaluatePassword(password);
            String emailValidate = EmailValidator.checkForValidEmail(email);

            if (!userNameValidate.isEmpty()) {
                errorLabel.setText(userNameValidate);
                return;
            }
            if (!nameValidate.isEmpty()) {
                errorLabel.setText(nameValidate);
                return;
            }
            if (!passwordValidate.isEmpty()) {
                errorLabel.setText(passwordValidate);
                return;
            }
            if (!emailValidate.isEmpty()) {
                errorLabel.setText(emailValidate);
                return;
            }

            try {
                // Check if the username already exists.
                if (!databaseHelper.doesUserExist(userName)) {
                    // Validate the invitation code.
                    if (databaseHelper.validateInvitationCode(code)) {
                        // Create a new user and register them.
                        List<String> roles = new ArrayList<>();
                        User user = new User(userName, name, password, email, roles, false);
                        databaseHelper.register(user);
                        // Navigate to the login page after successful registration.
                        new UserLoginPage(databaseHelper).show(primaryStage);
                    } else {
                        errorLabel.setText("Please enter a valid invitation code.");
                    }
                } else {
                    errorLabel.setText("This username is taken! Please use another to set up an account.");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        HBox hbox = new HBox(5, setupButton, quitButton);
        hbox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        hbox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10);
        layout.setMinSize(500, 380);
        layout.setMaxSize(500, 380);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + " -fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + " -fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");
        layout.getChildren().addAll(title, userNameField, nameField, emailField, passwordField, inviteCodeField, hbox, errorLabel);
        layout.setAlignment(Pos.CENTER);

        VBox.setMargin(layout, new Insets(0));
        VBox.setMargin(errorLabel, new Insets(0));
        VBox.setVgrow(hbox, Priority.NEVER);

        // Enable window dragging.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Window control buttons.
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);
        closeButton.setOnMouseEntered(a -> {
            closeButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            closeButton.setMinSize(25, 25);
            closeButton.setMaxSize(25, 25);
        });
        closeButton.setOnMouseExited(a -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            closeButton.setMinSize(25, 25);
            closeButton.setMaxSize(25, 25);
        });
        closeButton.setOnAction(a -> {
            primaryStage.close();
        });

        Button maxButton = new Button("🗖");
        maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);
        maxButton.setOnMouseEntered(a -> {
            maxButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            maxButton.setMinSize(25, 25);
            maxButton.setMaxSize(25, 25);
        });
        maxButton.setOnMouseExited(a -> {
            maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            maxButton.setMinSize(25, 25);
            maxButton.setMaxSize(25, 25);
        });
        maxButton.setOnAction(a -> {
            primaryStage.setMaximized(!primaryStage.isMaximized());
        });

        Button minButton = new Button("_");
        minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);
        minButton.setOnMouseEntered(a -> {
            minButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            minButton.setMinSize(25, 25);
            minButton.setMaxSize(25, 25);
        });
        minButton.setOnMouseExited(a -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; " +
                    "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
            minButton.setMinSize(25, 25);
            minButton.setMaxSize(25, 25);
        });
        minButton.setOnAction(a -> {
            primaryStage.setIconified(true);
        });

        // Container for window control buttons.
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);
        
        // Spacer to push the button bar to the far right.
        HBox spacer = new HBox(buttonBar);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox titleBar = new HBox(spacer, buttonBar);
        titleBar.setMinHeight(35);
        titleBar.setMaxHeight(35);
        titleBar.setMaxWidth(600);
        
        // Spacer to push the title bar to the top.
        VBox spacer1 = new VBox();
        spacer1.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        
        VBox titleBox = new VBox(titleBar, spacer1);
        titleBox.setAlignment(Pos.CENTER);
        
        titleBar.setAlignment(Pos.TOP_CENTER);
        spacer.setAlignment(Pos.TOP_LEFT);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        
        // StackPane to control overall layout.
        StackPane root = new StackPane(titleBox, layout);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));
        
        titleBox.prefWidthProperty().bind(root.widthProperty());
        titleBox.prefHeightProperty().bind(root.heightProperty());
        
        // Remove icons from the title bar.
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
