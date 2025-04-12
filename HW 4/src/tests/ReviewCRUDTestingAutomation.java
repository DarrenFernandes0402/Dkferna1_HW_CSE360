package tests;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import application.Review;
import databasePart1.DatabaseHelper;

/**
 * <p>
 * Test of the review CRUD methods.
 * </p>
 * <p>
 * A test of the review CRUD methods including creating, reading, updating and
 * deleting reviews to/from the review SQL table. The test will use preloaded
 * content to generate reviews in order to perform some of these functions.
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
 * @version 0.00 2025-04-01 - Initial baseline
 *
 */
public class ReviewCRUDTestingAutomation {
	/**
	 * 
	 * Variables to hold various data necessary for each test to be performed.
	 */
	private static final DatabaseHelper databaseHelper = new DatabaseHelper(); // Object to access database
	static int numPassed = 0; 												   // Number of tests passed
	static int numFailed = 0; 												   // Number of tests failed
	private static Review review; 										   // Holds the review we expect to find
	private static List<Review> reviews; 								   // Holds the review database
	private static String deco = "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

	/**
	 * Default constructor
	 */
	public ReviewCRUDTestingAutomation() {
	}

	/**
	 * Connects to the databaseHelper to initialize the testing environment.
	 * 
	 * @throws SQLException	In case the database throws an error
	 * 
	 */
	@Before
	public void setUp() throws SQLException {
		databaseHelper.connectToDatabase();
	}
	
	/**
	 * Test 26 - This method performs test 1 which will simply test the database size
	 * to ensure the expected number of reviews exists in the database to ensure
	 * that all reviews are accessible and able to be searched.
	 * 
	 */
	@Test
	public void testReviewDatabaseSize() {
		try {
			// Retrieve reviews from the database
			reviews = databaseHelper.qaHelper.getAllReviews();

		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getAllReviews() in JUNIT TEST 1");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(10, reviews.size());
	}

	/**
	 * Test 27 - This method performs test 2 which will test the delete
	 * function. It will delete review id #2 and then search for it.
	 * 
	 */
	@Test
	public void testDeleteReview() {
		try {
			// Delete review 2
			databaseHelper.qaHelper.deleteReview(2);
			// Retrieve review from the database
			review = databaseHelper.qaHelper.getReview(2);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getReview() in JUNIT TEST 2");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(null, review);
	}

	/**
	 * Test 28 - This method performs test 3 which will test the update
	 * function. This will update the text and then check to see if
	 * that text is reflected in the review database.
	 * 
	 */
	@Test
	public void testUpdateReview() {
		try {
			// Retrieve review 3 from the database
			review = databaseHelper.qaHelper.getReview(3);
			// Set the text on the review object for review 3
			review.setText("This is the new text for review 3");
			// Update Review 3 with some new text
			databaseHelper.qaHelper.updateReview(review);
			// Retrieve review from the database
			review = databaseHelper.qaHelper.getReview(3);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getReview() in JUNIT TEST 3");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(String.valueOf("This is the new text for review 3"), review.getText());
	}

	/**
	 * Test 29 - This method simply pulling another review from the database
	 * 
	 */
	@Test
	public void testSearchForExistingReview1() {
		try {
			// Retrieve review from the database
			review = databaseHelper.qaHelper.getReview(4);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getReview() in JUNIT TEST 4");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(String.valueOf("This is a test review for question 4"), review.getText());
	}

	/**
	 * Test 30 - This method simply pulling another review from the database
	 * 
	 */
	@Test
	public void testSearchForExistingReview2() {
		try {
			// Retrieve review from the database
			review = databaseHelper.qaHelper.getReview(5);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error calling .getReview() in JUNIT TEST 5");
			;
			return;
		}

		// Assertion statement to check
		assertEquals(String.valueOf("This is a test review for question 5"), review.getText());
	}
	}

