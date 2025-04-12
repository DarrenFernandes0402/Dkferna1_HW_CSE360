package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The {@code UserHomePage} class displays a simple welcome message for the user.
 * <p>
 * This page shows the current user's username and current role, and provides a logout
 * ("Back to login") button that returns the user to the login page. The layout supports
 * window dragging and includes custom window control buttons (close, maximize, minimize).
 * </p>
 */
public class UserHomePage {
    
    /**
     * The {@code DatabaseHelper} used for all database operations.
     */
    private final DatabaseHelper databaseHelper;
    
    /**
     * The current user.
     */
    private User user;

    /**
     * Constructs a new {@code UserHomePage} with the specified {@code DatabaseHelper}.
     *
     * @param databaseHelper the {@code DatabaseHelper} instance used for database interactions.
     */
    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        user = databaseHelper.currentUser;
    }

    /**
     * Displays the user home page on the given stage.
     * <p>
     * The page displays a welcome message including the user's username and role, and a
     * logout button to return to the login page. It also supports window dragging and includes
     * custom window control buttons in the top right corner.
     * </p>
     *
     * @param primaryStage the {@code Stage} on which the home page is displayed.
     */
    public void show(Stage primaryStage) {
        double[] offsetX = { 0 };
        double[] offsetY = { 0 };
        
        // Create the main layout container.
        VBox layout = new VBox(10);
        layout.setMinSize(940, 360);
        layout.setMaxSize(940, 360);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100; -fx-border-insets: 0, 2, 4");

        System.out.println("Successfully made it to the User Home Page");  // Debug message
        
        // Create a label displaying a welcome message with username and role.
        Label userLabel = new Label(
                "Hello, " + user.getUsername() + ". Your current role is : " + user.getCurrentRole());
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create a button to log out and return to the login screen.
        Button quitButton = new Button("Back to login");
        quitButton.setOnAction(event -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });
        
        // Enable window dragging by recording mouse offsets.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });
        
        // Create custom window control buttons.
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
        });
        maxButton.setOnMouseExited(a -> {
            maxButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
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
        });
        minButton.setOnMouseExited(a -> {
            minButton.setStyle("-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; "
                    + "-fx-text-fill: black; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 0;");
        });
        minButton.setOnAction(a -> {
            primaryStage.setIconified(true);
        });
        
        // Create a container for the window control buttons.
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        buttonBar.setMaxHeight(27);
        buttonBar.setMaxWidth(80);
        
        // Create a spacer region for layout purposes.
        Region spacer = new Region();
        spacer.setMinHeight(26);
        spacer.setMaxHeight(26);
        
        // Create a layout box to hold the main layout.
        VBox layoutBox = new VBox(spacer, layout);
        layoutBox.setAlignment(Pos.CENTER);
        
        // Assemble the root layout.
        StackPane root = new StackPane(layoutBox, buttonBar);
        root.setAlignment(buttonBar, Pos.TOP_RIGHT);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));
        
        layoutBox.prefWidthProperty().bind(root.widthProperty());
        layoutBox.prefHeightProperty().bind(root.heightProperty());
        
        // Add the welcome message and logout button to the main layout.
        layout.getChildren().addAll(userLabel, quitButton);
        layout.setAlignment(Pos.CENTER);
		
        Scene scene = new Scene(root, 940, 400);
        scene.setFill(Color.TRANSPARENT);
		
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
