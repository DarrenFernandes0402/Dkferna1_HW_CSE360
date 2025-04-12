package application;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.sql.SQLException;

/**
 * The {@code UserLoginPage} class provides a login interface for users to access their accounts.
 * <p>
 * It validates the user's credentials and navigates to the appropriate page based on the user's role.
 * If the user's One-Time Password (OTP) flag is set, the page directs the user to a password reset page.
 * If the user has more than one role, they are prompted to select one via a role selection page.
 * Otherwise, the user is sent directly to their corresponding home page.
 * </p>
 */
public class UserLoginPage {
    /**
     * The {@code DatabaseHelper} used for all database operations.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a new {@code UserLoginPage} with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the DatabaseHelper instance for interacting with the database.
     */
    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Navigates the user to their home page based on their role.
     * <p>
     * If the user has one or more roles, the first role is used to direct the user to the corresponding home page.
     * If the user has no roles, they are sent to a generic user home page.
     * </p>
     *
     * @param user     the User whose home page should be displayed.
     * @param newStage the stage on which to display the home page.
     */
    private void roleHomePage(User user, Stage newStage) {
        // Check if the user has any roles.
        if (user.getRoles().size() > 0) {
            switch (user.getRoles().get(0).toLowerCase()) {
                case "admin":
                    databaseHelper.setUserCurrentRole("admin");
                    new AdminHomePage(databaseHelper).show(newStage, user);
                    break;
                case "student":
                    databaseHelper.setUserCurrentRole("student");
                    new StudentHomePage(databaseHelper).show(newStage);
                    break;
                case "instructor":
                    databaseHelper.setUserCurrentRole("instructor");
                    new InstructorHomePage(databaseHelper).show(newStage);
                    break;
                case "staff":
                    databaseHelper.setUserCurrentRole("staff");
                    new StaffHomePage(databaseHelper).show(newStage);
                    break;
                case "reviewer":
                    databaseHelper.setUserCurrentRole("reviewer");
                    new ReviewerHomePage(databaseHelper).show(newStage);
                    break;
                default:
                    databaseHelper.setUserCurrentRole("user");
                    new UserHomePage(databaseHelper).show(newStage);
                    break;
            }
        } else {
            databaseHelper.setUserCurrentRole("user");
            new UserHomePage(databaseHelper).show(newStage);
        }
    }

    /**
     * Displays the login page on the provided stage.
     * <p>
     * The login page includes input fields for username and password, along with "Login" and "New User"
     * buttons. The login process validates the input using {@code UserNameRecognizer} and {@code PasswordEvaluator}.
     * If the credentials are valid and the user is successfully authenticated, the user is navigated
     * to the appropriate home page based on their role.
     * </p>
     *
     * @param primaryStage the stage on which the login page will be displayed.
     */
    public void show(Stage primaryStage) {
        double[] offsetX = {0};
        double[] offsetY = {0};

        // Input field for username.
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Username");
        userNameField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        userNameField.setMaxWidth(200);
        userNameField.setAlignment(Pos.CENTER);

        // Password field for password.
        PasswordField passwordField = new PasswordField();
        passwordField.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;");
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(200);
        passwordField.setAlignment(Pos.CENTER);

        // Label for the login page title.
        Label prompt = new Label("Login");
        prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
        prompt.setAlignment(Pos.CENTER);

        // Label to display error messages.
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 12px;");

        // Button to trigger login.
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Button for new user registration.
        Button setupButton = new Button("New User");
        setupButton.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray;"
                + " -fx-border-width: 2, 1; -fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Set loginButton as default for Enter key activation.
        loginButton.setDefaultButton(true);

        // Action handler for login button.
        loginButton.setOnAction(a -> {
            // Retrieve user inputs.
            String userName = userNameField.getText();
            String password = passwordField.getText();

            String userNameValidate = UserNameRecognizer.checkForValidUserName(userName);
            String passwordValidate = PasswordEvaluator.evaluatePassword(password);

            if (!userNameValidate.isEmpty()) {
                errorLabel.setText("Invalid username");
                return;
            }

            if (!passwordValidate.isEmpty()) {
                errorLabel.setText("Invalid password");
                return;
            }

            try {
                User user = databaseHelper.login(userName, password);

                if (user == null) {
                    errorLabel.setText("Error logging in. Contact an Administrator.");
                    return;
                }

                if (user.getOTPFlag()) {
                    new NewPasswordPage(databaseHelper).show(primaryStage, user);
                    return;
                }

                // If user has more than one role, use RoleSelectPage; otherwise, navigate directly.
                if (user.getRoles().size() > 1) {
                    new RoleSelectPage(databaseHelper).show(primaryStage);
                    return;
                } else {
                    roleHomePage(user, primaryStage);
                    return;
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Action handler for new user registration.
        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });

        HBox hbox = new HBox(5, loginButton, setupButton);
        hbox.setAlignment(Pos.CENTER);

        // Main layout container.
        VBox layout = new VBox(10);
        layout.setMinSize(400, 220);
        layout.setMaxSize(400, 220);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + " -fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + " -fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");
        layout.getChildren().addAll(prompt, userNameField, passwordField, hbox, errorLabel);
        layout.setAlignment(Pos.CENTER);

        // Allow window dragging.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Window control buttons for close, maximize, and minimize.
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

        // Set alignment within the title bar.
        titleBar.setAlignment(Pos.TOP_CENTER);
        spacer.setAlignment(Pos.TOP_LEFT);
        buttonBar.setAlignment(Pos.TOP_RIGHT);

        // StackPane for overall layout.
        StackPane root = new StackPane(titleBox, layout);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));
        
        titleBox.prefWidthProperty().bind(root.widthProperty());
        titleBox.prefHeightProperty().bind(root.heightProperty());

        primaryStage.setOnShown(a -> {
            userNameField.requestFocus();
        });

        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
