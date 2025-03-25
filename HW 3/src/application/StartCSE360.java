package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;

public class StartCSE360 extends Application {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		
		// Initalizes tranparency in stage
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		try {
			databaseHelper.connectToDatabase(); // Connect to the database
			if (databaseHelper.isDatabaseEmpty()) {

				new AdminSetupPage(databaseHelper).show(primaryStage);
			} else {
				new UserLoginPage(databaseHelper).show(primaryStage);

			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		primaryStage.setTitle("");
	}
}
