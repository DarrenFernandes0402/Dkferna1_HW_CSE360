package tests;

import application.Answer;
import application.Message;
import databasePart1.DatabaseHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.*;
public class StaffTest {

	/**
	 * This is a test class to try and test functionality of the 
	 * staff user page 
	 */
	private static DatabaseHelper db;

	/**
	 * Connects to the database once before all tests.
	 */
	@Before
	public void setup() {
		db = new DatabaseHelper();
		try {
			db.connectToDatabase();
		} catch (SQLException e) {
			fail("Failed to connect to the database.");
		}
	}
	
	/**
	 * This test tests if there are messages in the database
	 * It passes if there are messages in the database
	 * @throws SQLException
	 */
	@Test
	public void testMessagesExistInDatabase() throws SQLException {
		List<Message> after = db.qaHelper.getAllMessages();
		int count = after.size();
		assertTrue(count > 0);
	}
	
	/**
	 * This is a test for a database method that checks if it is possible for the 
	 * staff to see all the user's messages
	 * @throws SQLException
	 */
	@Test
	public void testSeeAllMessages() throws SQLException{
		List<Message> messages = db.qaHelper.retrieveMessagesFromUser(1);
		assertEquals("This is a test", messages.get(0).getMessage());
	}
	
	/**
	 * An important thing that the staff can do is ban and unban a user.
	 * This is a test to see if the user can be banned.
	 */
	@Test
	public void testBanUser() {
		db.banUser("xXAnthonyXx");
		assert(db.isUserBanned("xXAnthonyXx"));
	}
	
	/**
	 * A staff member can make mistakes and ban a user mistakenly, or sort it out outside 
	 * of our system, and can the unban the user, this test tests that. 
	 */
	@Test
	public void testUnBanUser() {
		db.unbanUser("xXAnthonyXx");
		assert(!db.isUserBanned("xXAnthonyXx"));
	}
	
	/**
	 * The user needs to see all the questions, but also all the answers that a person sends
	 * incase there are any answers that are inappropriate.
	 */
	@Test
	public void testGetAllAnswers() {
		try {
	        List<Answer> answers = db.qaHelper.getAllAnswersForQuestion(1);
	        assertEquals("Wrong number of answers", 2, answers.size());
	        assertEquals("Oh well another test happened", answers.get(1).getText());
	    } catch (SQLException e) {
	        fail("Case 5 is not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}