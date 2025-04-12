package application;

import databasePart1.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * InvitePage class represents the page where an admin can generate an
 * invitation code. The invitation code is displayed upon clicking a button.
 */
public class InvitationPage {

    /**
     * The DatabaseHelper instance used for database operations.
     */
    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a new InvitationPage with the specified DatabaseHelper.
     *
     * @param databaseHelper the DatabaseHelper used for generating invitation codes
     */
    public InvitationPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Invite Page in the provided primary stage.
     * <p>
     * This page allows the admin to generate an invitation code by clicking a button.
     * The generated code is displayed along with an expiration message. Additional
     * buttons allow navigation back to the login screen or to the admin home page.
     * </p>
     *
     * @param primaryStage the primary stage where the Invite Page will be displayed
     */
    public void show(Stage primaryStage) {
        double[] offsetX = { 0 };
        double[] offsetY = { 0 };

        // Label to display the title of the page.
        Label userLabel = new Label("Invite");
        userLabel.setStyle("-fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold;");

        // Button to generate the invitation code.
        Button showCodeButton = new Button("Generate Invitation Code");
        showCodeButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");
        // Set showCodeButton as default so that pressing Enter activates it.
        showCodeButton.setDefaultButton(true);

        // Button to return to the login screen.
        Button quitButton = new Button("Back to Login");
        quitButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Button to navigate to the admin home page.
        Button homeButton = new Button("Admin Home");
        homeButton.setStyle(
                "-fx-text-fill: black; -fx-font-weight: bold; -fx-border-color: black, gray; -fx-border-width: 2, 1;"
                        + "-fx-border-radius: 6, 5; -fx-border-inset: 0, 4;");

        // Label to display the generated invitation code.
        Label inviteCodeLabel = new Label("");
        inviteCodeLabel.setAlignment(Pos.CENTER);
        inviteCodeLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-style: italic; -fx-text-fill: black; -fx-font-weight: bold; -fx-translate-y: 8");

        // Generate invitation code when the button is clicked.
        showCodeButton.setOnAction(a -> {
            String invitationCode = databaseHelper.generateInvitationCode();
            inviteCodeLabel.setText(invitationCode + " - Code will expire in 15 minutes.");
        });

        // Navigate back to the login page.
        quitButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        // Navigate to the admin home page.
        homeButton.setOnAction(a -> {
            new AdminHomePage(databaseHelper).show(primaryStage, databaseHelper.currentUser);
        });

        // Set up the main layout.
        VBox layout = new VBox(10);
        layout.setMinSize(400, 220);
        layout.setMaxSize(400, 220);
        layout.setStyle("-fx-padding: 20; -fx-background-color: derive(gray, 80%); -fx-background-radius: 100;"
                + "-fx-background-insets: 4; -fx-border-color: gray, gray, black;"
                + "-fx-border-width: 2, 2, 1; -fx-border-radius: 100, 100, 100;" + "-fx-border-insets: 0, 2, 4");
        layout.setAlignment(Pos.CENTER);

        // HBox for navigation buttons.
        HBox hbox = new HBox(5);
        hbox.setStyle(" -fx-padding: 20;");
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(homeButton, quitButton);

        // Add components to the layout.
        layout.getChildren().addAll(userLabel, showCodeButton, inviteCodeLabel, hbox);

        // Enable window dragging by tracking mouse pressed and dragged events.
        layout.setOnMousePressed(a -> {
            offsetX[0] = a.getSceneX();
            offsetY[0] = a.getSceneY();
        });
        layout.setOnMouseDragged(a -> {
            primaryStage.setX(a.getScreenX() - offsetX[0]);
            primaryStage.setY(a.getScreenY() - offsetY[0]);
        });

        // Set up window control buttons (close, maximize, minimize).
        Button closeButton = new Button("X");
        closeButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        closeButton.setMinSize(25, 25);
        closeButton.setMaxSize(25, 25);
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

        Button maxButton = new Button("🗖");
        maxButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        maxButton.setMinSize(25, 25);
        maxButton.setMaxSize(25, 25);
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

        Button minButton = new Button("_");
        minButton.setStyle(
                "-fx-background-color: transparent; -fx-background-insets: 0; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 12px;"
                        + "-fx-font-weight: bold; -fx-padding: 0;");
        minButton.setMinSize(25, 25);
        minButton.setMaxSize(25, 25);
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
        minButton.setOnAction(a -> {
            primaryStage.setIconified(true);
        });

        // Container for the window control buttons.
        HBox buttonBar = new HBox(5, minButton, maxButton, closeButton);
        buttonBar.setAlignment(Pos.TOP_RIGHT);
        buttonBar.setPadding(new Insets(0));
        
        // Spacer to push the button bar to the far right.
        HBox spacer = new HBox(buttonBar);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox titleBar = new HBox(spacer, buttonBar);
        titleBar.setMinHeight(35);
        titleBar.setMaxHeight(35);
        titleBar.setMaxWidth(600);
        
        // Spacer to push the titleBar to the top.
        VBox spacer1 = new VBox();
        spacer1.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(spacer1, Priority.ALWAYS);
        VBox titleBox = new VBox(titleBar, spacer1);
        titleBox.setAlignment(Pos.CENTER);

        // StackPane to control layout sizing.
        StackPane root = new StackPane(titleBox, layout);
        root.setStyle("-fx-background-color: transparent;");
        root.setPadding(new Insets(0));
        
        titleBox.prefWidthProperty().bind(root.widthProperty());
        titleBox.prefHeightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 940, 400);
        scene.setFill(Color.TRANSPARENT);

        // Remove any icons from the title bar.
        primaryStage.getIcons().clear();

        // Set the scene and show the stage.
        primaryStage.setScene(scene);
        primaryStage.setTitle("");
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.centerOnScreen();
    }
}
