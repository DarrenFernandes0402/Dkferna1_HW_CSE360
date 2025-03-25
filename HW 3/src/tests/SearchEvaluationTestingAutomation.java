package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.*;
import databasePart1.*;

public class SearchEvaluationTestingAutomation {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	static int numPassed = 0;
	static int numFailed = 0;
	static int size;
	private static Question searchResult; // will hold the question object that is at the top of the returned list
	private static Question question;
	private static Answer answer;
	private static List<Question> questions;
	private static List<Answer> answers;
	private static String deco = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	// This class is a test bed to test the searchQuestionDatabase() method within
	// the QAHelper1 class.
	// This is a series of six total tests that will test searching the title and
	// the text of questions and display the results of those tests in the console
	// at the end.

	// *******Very Important, Be sure to visit DatabaseHelper.java lines 61-71 and
	// make sure that both databases are set to reset and to populate once

	public static void main(String[] args) {
		// Initialize the test environment

		// Connect to the database
		try {
			databaseHelper.connectToDatabase();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		// Test Case 1
		// Checks the size of the database to make sure all questions expected are there
		System.out.println(deco + "\nStarting TEST 1\n");
		performTestCase(1, true);
		System.out.println("\nTEST 1 has completed\n" + deco);

		// Test Case 2
		// Checks the search function with a title input against expected the expected question
		System.out.println(deco + "\nStarting TEST 2\n");
		performTestCase(2, false);
		System.out.println("\nTEST 2 has completed\n" + deco);

		// Test Case 3
		// Checks the search function with a title input against expected the expected question
		System.out.println(deco + "\nStarting TEST 3\n");
		performTestCase(3, false);
		System.out.println("\nTEST 3 has completed\n" + deco);

		// Test Case 4
		// Checks the search function with a text input against expected the expected question
		System.out.println(deco + "\nStarting TEST 4\n");
		performTestCase(4, true);
		System.out.println("\nTEST 4 has completed\n" + deco);

		// Test Case 5
		// Checks the search function with a text input against expected the expected question
		System.out.println(deco + "\nStarting TEST 5\n");
		performTestCase(5, true);
		System.out.println("\nTEST 5 has completed\n" + deco);

		// Test Case 6
		// Checks the search function with a text input against expected the expected question
		System.out.println(deco + "\nStarting TEST 6\n");
		performTestCase(6, false);
		System.out.println("\nTEST 6 has completed\n" + deco);

		// Display the results to the user
		displayResults();
	}

	private static void performTestCase(int testNum, boolean flag) {

		// Perform appropriate test according to testNum
		switch (testNum) {

		// Test the question database to make sure it contains all expected questions
				case 1:
					try {
						// Retrieve question from the database
						questions = databaseHelper.qaHelper.getAllQuestions();

					} catch (SQLException e) {
						e.printStackTrace();
						System.err.println("Error calling .getAllQuestions() in TEST 1");
						;
						return;
					}

					// Check if question expected is equal to the question found
					if (questions.size() == 23 && flag == true) {
						System.out.println("\n*** TEST PASSED ***");
						// Increment numPassed if it passes
						numPassed++;
					} else {
						// Display error message if it fails
						System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
								+ "\nbut actually has: " + searchResult.getId());
						numFailed++;
					}					
					break;
		
		// Test the search for an existing question title
		case 2:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(14);
				// Retrieve first question from the list of question returned by a search
				searchResult = databaseHelper.qaHelper
						.searchQuestionDatabase("standup meetings all team members required").get(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error calling .getQuestion() in TEST 2");
				;
				return;
			}

			// Check if question expected is equal to the question found
			if (question.getId() == searchResult.getId() && flag == false) {
				System.out.println("\n*** TEST PASSED ***");
				// Increment numPassed if it passes
				numPassed++;
			} else {
				// Display error message if it fails
				System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
						+ "\nbut actually has: " + searchResult.getId());
				numFailed++;
			}
			break;

		// Test the search for an existing question title
		case 3:
			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(23);
				// Retrieve first question from the list of question returned by a search
				searchResult = databaseHelper.qaHelper
						.searchQuestionDatabase("regarding some confusion about the student user stories to be implemented").get(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error calling .getQuestion() in TEST 3");
				;
				return;
			}

			// Check if question expected is equal to the question found
			if (question.getId() == searchResult.getId() && flag == false) {
				System.out.println("\n*** TEST PASSED ***");
				// Increment numPassed if it passes
				numPassed++;
			} else {
				// Display error message if it fails
				System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
						+ "\nbut actually has: " + searchResult.getId());
				numFailed++;
			}			
			break;

		// Test the search for an existing question text
		case 4:
			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(4);
				// Retrieve first question from the list of question returned by a search
				searchResult = databaseHelper.qaHelper.searchQuestionDatabase(
						"are we copying every file from hw1 and creating a new folder? how are we going to create a new user interface allowing for questions")
						.get(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error calling .getQuestion() in TEST 4");
				;
				return;
			}

			// Check if question expected is equal to the question found
			if (question.getId() == searchResult.getId() && flag == true) {
				System.out.println("\n*** TEST PASSED ***");
				// Increment numPassed if it passes
				numPassed++;
			} else {
				// Display error message if it fails
				System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
						+ "\nbut actually has: " + searchResult.getId());
				numFailed++;
			}		
			break;

		// Test the search for an existing question text
		case 5:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(9);
				// Retrieve first question from the list of question returned by a search
				searchResult = databaseHelper.qaHelper.searchQuestionDatabase("trouble viewing the architecture and design documents").get(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error calling .getQuestion() in TEST 5");
				;
				return;
			}

			// Check if question expected is equal to the question found
			if (question.getId() == searchResult.getId() && flag == true) {
				System.out.println("\n*** TEST PASSED ***");
				// Increment numPassed if it passes
				numPassed++;
			} else {
				// Display error message if it fails
				System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
						+ "\nbut actually has: " + searchResult.getId());
				numFailed++;
			}
			break;

		// Test the search for an existing question text
		case 6:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(21);
				// Retrieve first question from the list of question returned by a search
				searchResult = databaseHelper.qaHelper
						.searchQuestionDatabase("Hi, I emailed my grader about their github").get(0);
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Error calling .getQuestion() in TEST 6");
				;
				return;
			}

			// Check if question expected is equal to the question found
			if (question.getId() == searchResult.getId() && flag == false) {
				System.out.println("\n*** TEST PASSED ***");
				// Increment numPassed if it passes
				numPassed++;
			} else {
				// Display error message if it fails
				System.out.println("\n*FAILED: Question object is expected to contain question id: " + question.getId()
						+ "\nbut actually has: " + searchResult.getId());
				numFailed++;
			}
			break;
		}
		return;

	}

	private static void displayResults() {
		System.out.println(deco + "\n\nTesting has successfully completed. Out of " + (numPassed + numFailed)
				+ " total tests, the results are...\n\n");

		System.out.println("The number of tests that passed is: 	" + numPassed + "\n\n");

		System.out.println("The number of tests that failed is: 	" + numFailed + "\n\n");

		System.out.println("  -Thank you\n" + deco);
	}

}
