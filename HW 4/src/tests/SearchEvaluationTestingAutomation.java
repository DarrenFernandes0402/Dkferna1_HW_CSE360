package tests;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import application.Question;
import databasePart1.DatabaseHelper;

/**
 * <p>
 * Test of the searchQuestionDatabase method.
 * </p>
 * <p>
 * A test of the searchQuestionDatabase method that uses content of
 * questions to find the question it came from in an effort to find similar
 * questions that already exist in the database.
 * </p>
 * <p>
 * The searchQuestionDatabase method is a method contained within the QAHelper1
 * class. This method compares a list of strings that is generated from the
 * input text to a list of strings that is contained within each question
 * containing every word in that question in order to find and rank questions by
 * similarity to the input text.
 * </p>
 * @author Kyle Pierce
 * <p>
 * Zachary Chalmers
 * <p>
 * Chris Espinal
 * <p>
 * Darren Fernandes
 * <p>
 * Dara Gafoor
 * <p>
 * Joseph Morgan
 * 
 * @version 0.00 2025-02-28 - Initial baseline
 * <p>
 * 0.01 2025-03-20 - Added Junit support and Javadoc comments
 * </p>
 *
 */
public class SearchEvaluationTestingAutomation {
	/**
	 * 
	 * Variables to hold various data necessary for each test to be performed.
	 */
	private static final DatabaseHelper databaseHelper = new DatabaseHelper(); // Object to access database
	static int numPassed = 0; 												   // Number of tests passed
	static int numFailed = 0; 												   // Number of tests failed
	private static Question searchResult; 									   // Holds the first(most similar) question object
	private static Question question; 										   // Holds the question we expect to find
	private static List<Question> questions; 								   // Holds the question database
	private static String deco = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	/**
	 * Default constructor
	 */
	public SearchEvaluationTestingAutomation() {
	}

	/**
	 * This is the start of the test program. This method handles and coordinates the
	 * execution of the tests and the display of the results to the user.
	 * 
	 * @param args Command-line arguments(Not used here)
	 */
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
		// Checks the search function with a title input against expected the expected
		// question
		System.out.println(deco + "\nStarting TEST 2\n");
		performTestCase(2, false);
		System.out.println("\nTEST 2 has completed\n" + deco);

		// Test Case 3
		// Checks the search function with a title input against expected the expected
		// question
		System.out.println(deco + "\nStarting TEST 3\n");
		performTestCase(3, false);
		System.out.println("\nTEST 3 has completed\n" + deco);

		// Test Case 4
		// Checks the search function with a text input against expected the expected
		// question
		System.out.println(deco + "\nStarting TEST 4\n");
		performTestCase(4, true);
		System.out.println("\nTEST 4 has completed\n" + deco);

		// Test Case 5
		// Checks the search function with a text input against expected the expected
		// question
		System.out.println(deco + "\nStarting TEST 5\n");
		performTestCase(5, true);
		System.out.println("\nTEST 5 has completed\n" + deco);

		// Test Case 6
		// Checks the search function with a text input against expected the expected
		// question
		System.out.println(deco + "\nStarting TEST 6\n");
		performTestCase(6, false);
		System.out.println("\nTEST 6 has completed\n" + deco);

		// Display the results to the user
		displayResults();
	}

	/**
	 * This private method performs each test based on the test number provided. The
	 * result is then counted in the appropriate pass or fail variable.
	 * 
	 * @param testNum The test number to perform
	 * @param flag    The expected result of the test
	 */
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
				// Increment numFailed
				numFailed++;
			}
			break;

		// Test the search for an existing question title
		case 2:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(14);
				// Retrieve first question from the list of questions returned by a search
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
				// Increment numFailed
				numFailed++;
			}
			break;

		// Test the search for an existing question title
		case 3:
			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(23);
				// Retrieve first question from the list of questions returned by a search
				searchResult = databaseHelper.qaHelper.searchQuestionDatabase(
						"regarding some confusion about the student user stories to be implemented").get(0);
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
				// Increment numFailed
				numFailed++;
			}
			break;

		// Test the search for an existing question text
		case 4:
			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(4);
				// Retrieve first question from the list of questions returned by a search
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
				// Increment numFailed
				numFailed++;
			}
			break;

		// Test the search for an existing question text
		case 5:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(9);
				// Retrieve first question from the list of questions returned by a search
				searchResult = databaseHelper.qaHelper
						.searchQuestionDatabase("trouble viewing the architecture and design documents").get(0);
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
				// Increment numFailed
				numFailed++;
			}
			break;

		// Test the search for an existing question text
		case 6:

			try {
				// Retrieve question from the database
				question = databaseHelper.qaHelper.getQuestion(21);
				// Retrieve first question from the list of questions returned by a search
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
				// Increment numFailed
				numFailed++;
			}
			break;
		}
		// If program reaches here(It shouldn't), then test number doesn't exist, return
		return;

	}

	/**
	 * This private method displays results of the test to the user. It uses
	 * formatting in order to make the results easier to read and more
	 * understandable to the user.
	 * 
	 * @return A string containing results of the tests
	 */
	private static void displayResults() {
		// Begin display with a decorative banner and a statement
		System.out.println(deco + "\n\nTesting has successfully completed. Out of " + (numPassed + numFailed)
				+ " total tests, the results are...\n\n");

		// Display number of tests passed
		System.out.println("The number of tests that passed is: 	" + numPassed + "\n\n");

		// Display number of tests failed
		System.out.println("The number of tests that failed is: 	" + numFailed + "\n\n");

		// End with a thank you and decorative banner to close results
		System.out.println("  -Thank you\n" + deco);
	}

	// **********************************************************************************************
	// Junit tests that mirror each of the previous standard-format tests
	// **********************************************************************************************
	
	/**
	 * Connects to the databaseHelper to initialize the testing environment.
	 * 
	 * @throws SQLException	In case the database throws an error
	 */
	@Before
	public void setUp() throws SQLException {
		databaseHelper.connectToDatabase();
	}
	
	/**
	 * Test 1 - This method performs test 1 which will simply test the database size
	 * to ensure the expected number of questions exists in the database to ensure
	 * that all questions are accessible and able to be searched.
	 * 
	 */
	@Test
	public void testDatabaseSize() {
		try {
			// Retrieve question from the database
			questions = databaseHelper.qaHelper.getAllQuestions();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getAllQuestions() in JUNIT TEST 1");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(23, questions.size());
	}

	/**
	 * Test 2 - This method performs test 2 which will test the method,
	 * searchQuestionDatabase by searching using a partial existing title and expect
	 * to find that specific question
	 * 
	 */
	@Test
	public void testSearchForExistingTitle1() {
		try {
			// Retrieve question from the database
			question = databaseHelper.qaHelper.getQuestion(14);
			// Retrieve first question from the list of questions returned by a search
			searchResult = databaseHelper.qaHelper.searchQuestionDatabase("standup meetings all team members required")
					.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getQuestion() in JUNIT TEST 2");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(question.getId(), searchResult.getId());
	}

	/**
	 * Test 3 - This method performs test 3 which will test the method,
	 * searchQuestionDatabase by searching using a partial existing title and expect
	 * to find that specific question
	 * 
	 */
	@Test
	public void testSearchForExistingTitle2() {
		try {
			// Retrieve question from the database
			question = databaseHelper.qaHelper.getQuestion(23);
			// Retrieve first question from the list of questions returned by a search
			searchResult = databaseHelper.qaHelper
					.searchQuestionDatabase("regarding some confusion about the student user stories to be implemented")
					.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getQuestion() in JUNIT TEST 3");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(question.getId(), searchResult.getId());
	}

	/**
	 * Test 4 - This method performs test 4 which will test the method,
	 * searchQuestionDatabase by searching using a partial existing text and expect
	 * to find that specific question
	 * 
	 */
	@Test
	public void testSearchForExistingText1() {
		try {
			// Retrieve question from the database
			question = databaseHelper.qaHelper.getQuestion(4);
			// Retrieve first question from the list of questions returned by a search
			searchResult = databaseHelper.qaHelper.searchQuestionDatabase(
					"are we copying every file from hw1 and creating a new folder? how are we going to create a new user interface allowing for questions")
					.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getQuestion() in JUNIT TEST 4");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(question.getId(), searchResult.getId());
	}

	/**
	 * Test 5 - This method performs test 5 which will test the method,
	 * searchQuestionDatabase by searching using a partial existing text and expect
	 * to find that specific question
	 * 
	 */
	@Test
	public void testSearchForExistingText2() {
		try {
			// Retrieve question from the database
			question = databaseHelper.qaHelper.getQuestion(9);
			// Retrieve first question from the list of questions returned by a search
			searchResult = databaseHelper.qaHelper
					.searchQuestionDatabase("trouble viewing the architecture and design documents").get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getQuestion() in JUNIT TEST 5");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(question.getId(), searchResult.getId());
	}

	/**
	 * Test 6 - This method performs test 6 which will test the method,
	 * searchQuestionDatabase by searching using a partial existing text and expect
	 * to find that specific question
	 * 
	 */
	@Test
	public void testSearchForExistingText3() {
		try {
			// Retrieve question from the database
			question = databaseHelper.qaHelper.getQuestion(21);
			// Retrieve first question from the list of questions returned by a search
			searchResult = databaseHelper.qaHelper.searchQuestionDatabase("Hi, I emailed my grader about their github")
					.get(0);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getQuestion() in JUNIT TEST 6");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(question.getId(), searchResult.getId());
	}

	/**
	 * Test 7 - This method performs test 7 which will simply test the database size
	 * to ensure the expected number of questions exists in the database to ensure
	 * that all questions are accessible and able to be searched.
	 * 
	 */
	@Test
	public void testDatabaseSizeAgain() {
		try {
			// Retrieve question from the database
			questions = databaseHelper.qaHelper.getAllQuestions();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getAllQuestions() in JUNIT TEST 7");
			;
			return;
		}

		// Assertion statement to check
		assertNotEquals(22, questions.size());
	}
}
