package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * The {@code RoleSelectPage} class represents the user interface for the role-selection page.
 * <p>
 * This page is shown only when a user has multiple roles and allows the user to choose which role
 * they wish to assume. Once a role is selected from the dropdown, the user can click the "Next" button
 * to proceed to the corresponding home page. The "Back" button returns the user to the login page.
 * The UI also supports custom window controls (minimize, maximize, close) and window dragging.
 * </p>
 */
public class RoleSelectPage {
    private final DatabaseHelper databaseHelper;
    String selectedRole;
    User user;

    /**
     * Constructs a new {@code RoleSelectPage} with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the {@code DatabaseHelper} instance used for retrieving user and role information.
     */
    public RoleSelectPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        user = databaseHelper.currentUser;
    }

    /**
     * Displays the role-selection page on the provided stage.
     * <p>
     * This method creates a UI with a ComboBox populated with the user's roles,
     * a "Next" button (enabled when a role is selected), and a "Back" button to return to the login page.
     * It also includes custom window controls (minimize, maximize, close) and enables window dragging.
     * When a role is selected and "Next" is pressed, the application navigates to the appropriate home page.
     * </p>
     *
     * @param primaryStage the primary {@code Stage} on which the role-selection UI is displayed.
     */
    public void show(Stage primaryStage) {
        double[] offsetX = {0};
        double[] offsetY = {0};

        // Create "Next" and "Back" buttons.
        Button nextButton = new Button("Next");
        nextButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        nextButton.setDefaultButton(true);

        Button quitButton = new Button("Back");
        quitButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Create a ComboBox for role selection and populate it with the current user's roles.
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(user.getRoles());
        comboBox.setPromptText("Please select a role");
        comboBox.setStyle("-fx-text-fill: black; -fx-font-weight: bold;-fx-border-color: black, gray;"
                + "-fx-border-width: 2, 1; -fx-border-radius: 3, 1; -fx-border-inset: 0, 4;"
                + "-fx-prompt-text-fill: black;");

        // Enable the Next button only when a role is selected.
        comboBox.setOnAction(a -> {
            selectedRole = comboBox.getValue();
            nextButton.setDisable(selectedRole == null);
        });

        // When Next is pressed, set the current role and navigate to the appropriate home page.
        nextButton.setOnAction(a -> {
            databaseHelper.currentUser.setCurrentRole(selectedRole);
            switch (selectedRole) {
                case "Admin":
                    new AdminHomePage(databaseHelper).show(primaryStage, user);
                    break;
                case "Student":
                    new StudentHomePage(databaseHelper).show(primaryStage);
                    break;
                case "Instructor":
                    new InstructorHomePage(databaseHelper).show(primaryStage);
                    break;
                case "Staff":
                    new StaffHomePage(databaseHelper).show(primaryStage);
                    break;
                case "Reviewer":
                    new ReviewerHomePage(databaseHelper).show(primaryStage);
                    break;
            }
        });

        // The Back button returns to the User Login Screen.
        quitButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Center and format the ComboBox items.
        comboBox.setCellFactory(a -> new ListCell<String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (!empty && role != null) {
                    setText(role.substring(0, 1).toUpperCase() + role.substring(1));
                    setAlignment(Pos.CENTER);
                }
            }
        });
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (!empty && role != null) {
                    setText(role.substring(0, 1).toUpperCase() + role.substring(1));
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Label for the page title.
        Label prompt = new Label("Role-Select");
        prompt.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");
        prompt.setAlignment(Pos.CENTER);

        // Main layout container.
        VBox layout = new VBox(10);
        layout.setMinSize(400, 220);
        layout.setMaxSize(400, 220);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");
        layout.setAlignment(Pos.CENTER);

        // Enable dragging the window by clicking on the layout.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Container for the Next and Back buttons.
        HBox layoutH = new HBox(10);
        layoutH.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layoutH.setAlignment(Pos.CENTER);
        layoutH.getChildren().addAll(nextButton, quitButton);

        // Assemble the main layout.
        layout.getChildren().addAll(prompt, comboBox, layoutH);

        // Create custom window control buttons (close, maximize, minimize).
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);
        closeButton.setOnMouseEntered(a -> {
            closeButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        closeButton.setOnMouseExited(a -> {
            closeButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        closeButton.setOnAction(a -> primaryStage.close());

        Button maxButton = new Button("🗖");
        maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);
        maxButton.setOnMouseEntered(a -> {
            maxButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        maxButton.setOnMouseExited(a -> {
            maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        maxButton.setOnAction(a -> primaryStage.setMaximized(!primaryStage.isMaximized()));

        Button minButton = new Button("_");
        minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);
        minButton.setOnMouseEntered(a -> {
            minButton.setStyle("-fx-background-color: gray; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        minButton.setOnMouseExited(a -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        minButton.setOnAction(a -> primaryStage.setIconified(true));

        // Container for window control buttons.
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));

        // Spacer to push the buttonBar to the far right.
        HBox spacer = new HBox(buttonBar);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Title bar for window dragging.
        HBox titleBar = new HBox(spacer, buttonBar);
        titleBar.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        titleBar.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });
        titleBar.setMinHeight(35);
        titleBar.setMaxHeight(35);
        titleBar.setMaxWidth(Double.MAX_VALUE);

        // Spacer to push the titleBar to the top.
        VBox spacer1 = new VBox();
        spacer1.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(spacer1, Priority.ALWAYS);

        VBox titleBox = new VBox(titleBar, spacer1);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setMaxWidth(Double.MAX_VALUE);

        titleBar.setAlignment(Pos.TOP_CENTER);
        spacer.setAlignment(Pos.TOP_LEFT);
        buttonBar.setAlignment(Pos.TOP_RIGHT);

        // Assemble the main layout.
        StackPane root = new StackPane(titleBox, layout);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));

        titleBox.prefWidthProperty().bind(root.widthProperty());
        titleBox.prefHeightProperty().bind(root.heightProperty());

        // Remove any icons from the title bar.
        primaryStage.getIcons().clear();

        Scene scene = new Scene(root, 400, 300);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
