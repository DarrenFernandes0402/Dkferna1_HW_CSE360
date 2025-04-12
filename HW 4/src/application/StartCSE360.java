package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.SQLException;
import databasePart1.DatabaseHelper;

/**
 * The {@code StartCSE360} class is the main entry point for the CSE360 application.
 * <p>
 * This class extends {@code Application} and is responsible for initializing the database
 * connection and launching the appropriate initial page. If the database is empty, the admin
 * setup page is shown; otherwise, the user login page is displayed.
 * </p>
 *
 * <p>
 * To run the application, the {@code main} method calls {@code launch(args)}, which in turn invokes
 * the {@code start} method.
 * </p>
 */
public class StartCSE360 extends Application {

    /**
     * A static instance of {@code DatabaseHelper} used for managing database operations.
     */
    private static final DatabaseHelper databaseHelper = new DatabaseHelper();

    /**
     * The main entry point for the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     * <p>
     * This method initializes the primary stage with a transparent style, connects to the database,
     * and displays either the admin setup page (if the database is empty) or the user login page.
     * </p>
     *
     * @param primaryStage the primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize transparency for the stage.
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        
        try {
            databaseHelper.connectToDatabase(); // Connect to the database.
            if (databaseHelper.isDatabaseEmpty()) {
                // If the database is empty, show the admin setup page.
                new AdminSetupPage(databaseHelper).show(primaryStage);
            } else {
                // Otherwise, show the user login page.
                new UserLoginPage(databaseHelper).show(primaryStage);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        primaryStage.setTitle("");
    }
}
