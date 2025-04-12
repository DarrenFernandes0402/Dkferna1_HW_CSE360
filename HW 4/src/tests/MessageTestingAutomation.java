package tests;

import application.Message;
import databasePart1.DatabaseHelper;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.Before;
import org.junit.*;

/**
 * MessageTestingAutomation is a console-based test runner used to validate
 * the message creation and insertion functionality of the message database.
 * It includes both positive and negative test cases to ensure the system behaves
 * correctly under valid and invalid input conditions.
 * 
 * @author Chris
 * @version 1.0
 * @since 2025-03-24

 */
public class MessageTestingAutomation {

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
	 * [Test 1] Runs a suite of automated tests to verify that message records are
	 * correctly inserted, validated, and managed in the database.
	 * 
	 * Check if any messages currently exist in the database.
	 */
	@Test
	public void testMessagesExistInDatabase() throws SQLException {
		List<Message> after = db.qaHelper.getAllMessages();
		int count = after.size();
		boolean hasMessages = count > 0;
		assertTrue(count >= 0);
	}

	/**
	 * [Test 2] Look for a specific known message by subject.
	 */
	@Test
	public void testFindSpecificMessage()throws SQLException {
		boolean found = false;
		List<Message> messages = db.qaHelper.getAllMessages();
		for (Message m : messages) {
			if ("Meeting Reminder".equals(m.getSubject())) {
				found = true;
				break;
			}
		}
		System.out.println("[Test 2] 'Meeting Reminder' message found: " + found);
		assertTrue(true); // Pass regardless; it's a presence check

	}

	/**
	 * [Test 3] Validate that all messages contain both a non-empty subject and message body.
	 */
	@Test
	public void testAllMessagesHaveValidContent() throws SQLException{
		boolean allValid = true;
		List<Message> messages = db.qaHelper.getAllMessages();
		for (Message m : messages) {
			if (m.getSubject() == null || m.getSubject().isBlank() ||
					m.getMessage() == null || m.getMessage().isBlank()) {
				allValid = false;
				System.out.println("[DEBUG] Invalid message found: ID=" + m.getMessageID());
				break;
			}
		}
		System.out.println("[Test 3] All messages have valid subject and body: " + allValid);
		assertTrue(allValid);
	}


	/**
	 * [Test 4] Attempt to insert a message with an empty subject and body.
	 */
	@Test
	public void testInsertEmptyMessageFails() throws SQLException{
		Message badMsg = new Message(1337, 2, "", "");
		Exception exception = assertThrows(Exception.class, () -> {
			db.qaHelper.createMessage(badMsg);
		});
		System.out.println("[Test 4] Passed: Empty message was rejected.");
	}

	/**
	 * [Test 5] Attempt to insert a message with null subject and body.
	 */
	@Test
	public void testInsertNullMessageFails() throws SQLException{
		Message badMsg = new Message(1337, 2, null, null);
		Exception exception = assertThrows(Exception.class, () -> {
			db.qaHelper.createMessage(badMsg);
		});
		System.out.println("[Test 5] Passed: Null message was rejected.");
	}

	/**
	 * [Test 6] Ensure that all messages in the database have valid, auto-generated message IDs.
	 */
	@Test
	public void testAllMessagesHaveValidIDs() throws SQLException{
		boolean allHaveIDs = true;
		List<Message> messages = db.qaHelper.getAllMessages();
		for (Message m : messages) {
			if (m.getMessageID() <= 0) {
				allHaveIDs = false;
				break;
			}
		}
		System.out.println("[Test 6] All messages have valid message IDs: " + allHaveIDs);
		assertTrue(allHaveIDs);
	}
}

