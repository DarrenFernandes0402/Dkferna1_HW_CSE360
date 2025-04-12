package databasePart1;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import application.Question;
import application.Answer;
import application.Review;
import application.Message;
import tests.*;
import application.User;

/**
 * <p>
 * Database helper for the Question, Answer and Review database tables.
 * </p>
 * <p>
 * A helper class to initialize and manage the SQL tables for all Questions, Answers and Reviews.
 * QAHelper1 contains all functions necessary to Create, Read, Update and Delete data
 * from each table along with many more to help maintain the database.
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
 * </p>
 * 
 * @version 0.00 2025-02-28 - Initial baseline
 * <p>
 * 0.01 2025-03-31 - Added Javadoc comments
 * </p>
 *
 */
public class QAHelper1 {
	private DatabaseHelper databaseHelper;

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	/**
	 * The database connection
	 */
	public Connection connection = null;	
	
	/**
	 * Statement that will be used in SQL queries
	 */
	public Statement statement = null;

	/**
	 * Constructor
	 * 
	 * @param databaseHelper Database helper for the user database
	 * 
	 */
	public QAHelper1(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	/**
	 * 
	 * Initializes the connection to the database
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to QA database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * 
	 * Create the tables that will be used to store the data
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void createTables() throws SQLException {
		// Create the question database
		String questionTable = "CREATE TABLE IF NOT EXISTS cse360question ("
				+ "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " + "title VARCHAR(255), " + "text TEXT DEFAULT NULL, "
				+ "author INT, " + "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP, " + "answer_id VARCHAR(MAX) DEFAULT NULL, "
				+ "preferred_answer INT DEFAULT NULL)";
		statement.execute(questionTable);

		// Create the answer database
		String answerTable = "CREATE TABLE IF NOT EXISTS cse360answer ("
				+ "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " + "text TEXT DEFAULT NULL, " + "author INT, "
				+ "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " + "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ "answer_id VARCHAR(MAX) DEFAULT NULL)";
		statement.execute(answerTable);

		String messageTable = "CREATE TABLE IF NOT EXISTS cse360message ("
				+ "messageid INT AUTO_INCREMENT PRIMARY KEY, " + "referenceId INT, " + "referenceType VARCHAR(20), "
				+ "senderid INT, " + "recipientid INT, " + "subject TEXT, " + "message TEXT, "
				+ "createdon TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updatedon TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
		statement.execute(messageTable);

		String answerViewsTable = "CREATE TABLE IF NOT EXISTS cse360answerviews (" + "answer_id INT NOT NULL, "
				+ "user_id INT NOT NULL, " + "is_read BOOLEAN DEFAULT FALSE, " + "PRIMARY KEY (answer_id, user_id))";
		statement.execute(answerViewsTable);

		// Create the review database
		String reviewTable = "CREATE TABLE IF NOT EXISTS cse360review ("
				+ "id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, " + "forQuestion BOOLEAN NOT NULL, "
				+ "relatedId INT NOT NULL, " + "text TEXT NOT NULL, " + "author INT, "
				+ "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " + "updated_on DATETIME DEFAULT CURRENT_TIMESTAMP, "
				+ "vote INT DEFAULT 0)";
		statement.execute(reviewTable);

	}

	/**
	 * Creates an auxiliary table tracking which answers each student has read
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void createAnswerViewsTable() throws SQLException {
		// *** REMOVED RECURSIVE CALL TO createAnswerViewsTable() ***

	}

	/**
	 * This helps us keep track of how many 'unread' answers remain for each
	 * question or user.
	 * 
	 * @param answerId 			The answer id of the answer you wish to mark read
	 * @param userId 			The user id of the user that is reading the answer
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void markAnswerAsRead(int answerId, int userId) throws SQLException {
		// Check if record exists
		String checkQuery = "SELECT * FROM cse360answerviews WHERE answer_id = ? AND user_id = ?";
		try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
			checkStmt.setInt(1, answerId);
			checkStmt.setInt(2, userId);
			ResultSet rs = checkStmt.executeQuery();
			if (!rs.next()) {
				// Insert new record
				String insertQuery = "INSERT INTO cse360answerviews (answer_id, user_id, is_read) VALUES (?, ?, TRUE)";
				try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
					insertStmt.setInt(1, answerId);
					insertStmt.setInt(2, userId);
					insertStmt.executeUpdate();
				}
			} else {
				// If record is found but is_read is FALSE, update it
				boolean isRead = rs.getBoolean("is_read");
				if (!isRead) {
					String updateQuery = "UPDATE cse360answerviews SET is_read = TRUE WHERE answer_id = ? AND user_id = ?";
					try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
						updateStmt.setInt(1, answerId);
						updateStmt.setInt(2, userId);
						updateStmt.executeUpdate();
					}
				}
			}
		}
	}

	/**
	 * Returns a boolean that indicates of a answer has been marked read by the user.
	 * 
	 * @param answerId 			The answer id of the answer you wish to check if is read
	 * @param userId 			The user id of the user that you want to check to see if they have read the answer
	 * 
	 * @return 					A boolean indicating whether the function was successfully performed
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public boolean isAnswerMarkedAsRead(int answerId, int userId) throws SQLException {
		String query = "SELECT is_read FROM cse360answerviews WHERE answer_id = ? AND user_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerId);
			pstmt.setInt(2, userId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean("is_read"); // Returns true if the answer is already marked as read
			}
		}
		return false;
	}

	/**
	 * Check if the database is empty - Only checks the question database at the
	 * moment.
	 * 
	 * @return 						A boolean indicating whether the function was successfully performed
	 * 
	 * @throws SQLException 		In case the database throws an error
	 * 
	 */
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360question";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	/**
	 * Registers a new question in the database.
	 * 
	 * @param question 			The question object you wish to register in the database
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void registerQuestion(Question question) throws SQLException {
		String insertQuestion = "INSERT INTO cse360question (title, text, author) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertQuestion)) {
			pstmt.setString(1, question.getTitle());
			pstmt.setString(2, question.getText());
			pstmt.setInt(3, question.getAuthorId());
			pstmt.executeUpdate();
		}
		System.out.println("Question registered successfully.");
	}

	/**
	 * Registers a new answer in the database.
	 * 
	 * @param answer		 The answer object you wish to relate to a question
	 * @param relatedID 	 The id of the question you wish to relate the passed answer object to
	 * 
	 * @throws SQLException  In case the database throws an error
	 * 
	 */
	public void registerAnswerWithQuestion(Answer answer, int relatedID) throws SQLException {
		String insertAnswer = "INSERT INTO cse360answer (text, author) VALUES (?, ?)";

		// Prevent duplicates
		if (isDuplicateAnswer(relatedID, answer.getText())) {
			System.out.println("Duplicate answer detected. Answer not inserted.");
			return;
		}

		try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getAuthorId());
			pstmt.executeUpdate();

			ResultSet newID = pstmt.getGeneratedKeys();
			if (newID.next()) {
				int answerID = newID.getInt(1);
				addRelationToQuestion(relatedID, answerID);
			}

		}
		System.out.println("Answer registered successfully.");
	}

	
	/**
	 * Registers a new answer in the database.
	 * 
	 * @param answer 		The answer object you wish to register
	 * @param relatedID 	The answer id you wish to relate to the passed answer object
	 * 
	 * @throws SQLException In case the database throws an error
	 * 
	 */
	public void registerAnswerWithAnswer(Answer answer, int relatedID) throws SQLException {
		String insertAnswer = "INSERT INTO cse360answer (text, author) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertAnswer, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getAuthorId());
			pstmt.executeUpdate();

			ResultSet newID = pstmt.getGeneratedKeys();

			if (newID.next()) {
				int answerID = newID.getInt(1);
				addRelationToAnswer(relatedID, answerID);
			}

		}
		System.out.println("Answer registered successfully.");
	}

	/**
	 * Sets the preferredAnswer variable for the provided question id to the provided answer id
	 * so that the answer id provided is the preferredAnswer of the provided question id.
	 * 
	 * @param questionId 	The id of the question you are setting the preferredAnswer of
	 * @param answerId 		The id of the answer you wish to set as the preferredAnswer
	 * 
	 * @throws SQLException In case the database throws an error
	 * 
	 */
	public void setPreferredAnswer(int questionId, int answerId) throws SQLException {
		String updateQuery = "UPDATE cse360question SET preferred_answer = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setInt(1, answerId);
			pstmt.setInt(2, questionId);
			pstmt.executeUpdate();
		}
		System.out.println("Preferred answer set for question ID: " + questionId);
	}

	/**
	 * Checks if answer text is found within questions related answers
	 * 
	 * @param questionID 	The id of the question you are working with
	 * @param answerText 	The text of the answer you wish to check for
	 * 
	 * @return 				A boolean indicating whether the function was successfully performed
	 * 
	 * @throws SQLException In case the database throws an error
	 * 
	 */
	public boolean isDuplicateAnswer(int questionID, String answerText) throws SQLException {
		// First, retrieve the answer IDs from the question
		String getAnswersQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(getAnswersQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIds = rs.getString("answer_id"); // e.g., "1, 4, 5, 6, 7"
				if (answerIds == null || answerIds.trim().isEmpty()) {
					return false; // No answers exist yet, so no duplicates
				}

				// Convert "1, 4, 5, 6, 7" into (1, 4, 5, 6, 7)
				String[] idsArray = answerIds.split(",\\s*"); // Split by comma & spaces
				List<Integer> answerIdList = new ArrayList<>();
				for (String id : idsArray) {
					try {
						answerIdList.add(Integer.parseInt(id.trim())); // Convert to int
					} catch (NumberFormatException e) {
						System.err.println("Invalid answer ID format: " + id);
					}
				}

				if (answerIdList.isEmpty()) {
					return false; // No valid IDs found
				}

				// Build SQL query dynamically
				StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM cse360answer WHERE text = ? AND id IN (");
				for (int i = 0; i < answerIdList.size(); i++) {
					sql.append("?");
					if (i < answerIdList.size() - 1) {
						sql.append(", ");
					}
				}
				sql.append(")");

				try (PreparedStatement checkStmt = connection.prepareStatement(sql.toString())) {
					checkStmt.setString(1, answerText.trim());
					for (int i = 0; i < answerIdList.size(); i++) {
						checkStmt.setInt(i + 2, answerIdList.get(i));
					}

					ResultSet checkRs = checkStmt.executeQuery();
					return checkRs.next() && checkRs.getInt(1) > 0;
				}
			}
		}
		return false; // Default case
	}

	/**
	 * Deletes a question row from the SQL table
	 * 
	 * @param id 	The id of the question you wish to delete
	 * 
	 * @return 		A boolean indicating whether the function was successfully performed
	 * 
	 */
	public boolean deleteQuestion(int id) {
		String query = "DELETE FROM cse360question AS c WHERE c.id = ?"; // delete the correct question row from
																			// database
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-QUESTION: Question successfully deleted");
				// Remove answers related to question
				List<Answer> answers = getAllAnswersForQuestion(id);
				for (Answer answer : answers) {
					deleteAnswer(answer.getId());
				}
				return true;
			}
			System.out.println("DELETE-QUESTION: Question was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETE-QUESTION: SQL Error - " + e.getMessage());
			return false;
		}
	}

	/**
	 * Deletes a question row from the SQL table
	 * 
	 * @param id 		The id of the answer you wish to delete
	 * 
	 * @return 			A boolean indicating whether the function was successfully performed
	 * 
	 */
	public boolean deleteAnswer(int id) {
		String query = "DELETE FROM cse360answer AS c WHERE c.id = ?"; // delete the correct answer row from database
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-ANSWER: Answer successfully deleted");
				return true;
			}
			System.out.println("DELETE-ANSWER: Answer was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETE-ANSWER: SQL Error - " + e.getMessage());
			return false;
		}
	}

	/**
	 * Add a relation to the question database
	 * 
	 * @param questionID 	The id of the question you want to relate to
	 * @param answerID 		The id of the answer you wish to relate to a question
	 * 
	 */
	public void addRelationToQuestion(int questionID, int answerID) {
		String selectQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
		String updateQuery = "UPDATE cse360question SET answer_id = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIDs = rs.getString("answer_id");
				String newAnswerIDs;

				// Check if null or empty
				if (answerIDs == null || answerIDs.trim().isEmpty()) {
					newAnswerIDs = String.valueOf(answerID);
				} else {
					// If it is not empty or null, then add a comma and space
					newAnswerIDs = answerIDs + ", " + answerID;
				}

				try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
					upstmt.setString(1, newAnswerIDs);
					upstmt.setInt(2, questionID);
					upstmt.executeUpdate();
				}
			} else {
				System.err.println("Could not find a question with id: " + questionID);
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage() + "\nERROR IN ADD-RELATION METHOD");
		}
	}

	/**
	 * Add a relation to the answer database
	 * 
	 * @param answerID 		The id of the answer you wish to relate to
	 * @param relatedID 	The id of the answer you wish to relate
	 * 
	 */
	public void addRelationToAnswer(int answerID, int relatedID) {
		String selectQuery = "SELECT answer_id FROM cse360answer WHERE id = ?";
		String updateQuery = "UPDATE cse360answer SET answer_id = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIDs = rs.getString("answer_id");
				String newAnswerIDs;

				// Check if null or empty
				if (answerIDs == null || answerIDs.trim().isEmpty()) {
					newAnswerIDs = String.valueOf(relatedID);
				} else {
					// If it is not empty or null, then add a comma and space
					newAnswerIDs = answerIDs + ", " + relatedID;
				}

				try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
					upstmt.setString(1, newAnswerIDs);
					upstmt.setInt(2, answerID);
					upstmt.executeUpdate();
				}
			} else {
				System.err.println("Could not find an answer with id: " + answerID);
			}

		} catch (SQLException e) {
			System.err.println(e.getMessage() + "\nERROR IN ADD-RELATION METHOD");
		}

	}

	/**
	 * Delete a relation from the relation database
	 * 
	 * @param questionID 	The id of the question you wish to delete a relation from
	 * @param answerID 		The id of the answer you wish to remove the relation of
	 * 
	 * @return 				A boolean indicating whether the function was successfully performed
	 * 
	 */
	public boolean deleteRelation(int questionID, int answerID) {
		String selectQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
		String updateQuery = "UPDATE cse360question SET answer_id = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(selectQuery)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIDs = rs.getString("answer_id");

				// Check if null or empty
				if (answerIDs == null || answerIDs.trim().isEmpty()) {
					System.err.println("Error: There are no answers related to this question.");
					return false;
				}

				// convert comma separated list into an array
				String[] answerIdArray = answerIDs.split(",\\s");
				List<String> answerIdList = new ArrayList<>(Arrays.asList(answerIdArray));

				boolean idFound = answerIdList.remove(String.valueOf(answerID));
				if (!idFound) {
					System.out.println("Could not find answer id relation in provided question id: " + questionID);
					return false;
				}

				// Put list back into comma separated list
				String newAnswerIDs = String.join(", ", answerIdList);

				try (PreparedStatement upstmt = connection.prepareStatement(updateQuery)) {
					upstmt.setString(1, newAnswerIDs.isEmpty() ? null : newAnswerIDs);
					upstmt.setInt(2, questionID);
					upstmt.executeUpdate();
					System.out.println("Answer ID: " + answerID
							+ " was successfully removed from relation with question id: " + questionID);
					return true;
				}
			} else {
				System.err.println("Could not find a question with id: " + questionID);
				return false;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage() + "\nERROR IN DELETE-RELATION METHOD");
			return false;
		}
	}

	/**
	 * Get a question object with a provided question id
	 * 
	 * @param questionID 			The id of the question you wish to search for
	 * 
	 * @return 						A question object representing the question you were searching for
	 * 
	 * @throws SQLException 		In case the database throws an error
	 * 
	 */
	public Question getQuestion(Integer questionID) throws SQLException {
		// Search the question database for a match to the question id
		String query = "SELECT * FROM cse360question AS c WHERE c.id = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				List<String> comp = textDeserial(title + " " + text);

				int preferredAnswer = rs.getInt("preferred_answer");

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, authorId, createdOn, updatedOn, comp, preferredAnswer,
						author, authorName, relatedId);

				// Return the question object
				return question;
			}
		}
		return null;
	}

	/**
	 * Get a question object with a provided question title
	 * 
	 * @param questionTitle 		The title of the question you are searching for
	 * 
	 * @return 						A question object representing the question you were searching for
	 * 
	 * @throws SQLException 		In case the database throws an error
	 * 
	 */
	public Question getQuestion(String questionTitle) throws SQLException {
		// Search the question database for a match to the question id
		String query = "SELECT * FROM cse360question AS c WHERE c.title = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, questionTitle);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				List<String> comp = textDeserial(title + " " + text);

				int preferredAnswer = rs.getInt("preferred_answer");

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, authorId, createdOn, updatedOn, comp, preferredAnswer,
						author, authorName, relatedId);

				// Return the question object
				return question;
			}
		}
		return null;
	}

	/**
	 * Get an answer object with a provided answer id
	 * 
	 * @param answerID 			The id of the answer you are searching for
	 * 
	 * @return 					An answer object representing the answer you were searching for
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Answer getAnswer(Integer answerID) throws SQLException {
		// Search the answer database for a match to the answer id
		String query = "SELECT * FROM cse360answer AS c WHERE c.id = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);

				// Return the answer object
				return answer;
			}
		}
		return null;
	}

	/**
	 * Get an answer object with a provided answer text
	 * 
	 * @param answerText 		The text of the answer you are searching for
	 * 
	 * @return 					An answer object representing the answer you were searching for
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Answer getAnswer(String answerText) throws SQLException {
		// Search the answer database for a match to the answer text
		String query = "SELECT * FROM cse360answer AS c WHERE c.text = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answerText);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);

				// Return the answer object
				return answer;
			}
		}
		return null;
	}

	/**
	 * Retrieve all questions from the question database
	 * 
	 * @return 					A List of question objects representing the entire question database
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllQuestions() throws SQLException {
		String query = "SELECT * FROM cse360question"; // selecting all of the rows in the database
		List<Question> questions = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				int preferredAnswer = rs.getInt("preferred_answer");

				List<String> comp = textDeserial(title + " " + text);

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, authorId, createdOn, updatedOn, comp, preferredAnswer,
						author, authorName, relatedId);

				// Add question object to the list questions
				questions.add(question);
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	/**
	 * Retrieves how many answers a given student has not yet read across all
	 * questions.
	 * 
	 * @param questionId 		The id of the question you are working with
	 * @param userId 			The id of the user you are working with
	 * 
	 * @return 					A hash map of all answers, read and unread, for the current user
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Map<String, List<Answer>> getReadAndUnreadAnswers(int questionId, int userId) throws SQLException {
		Map<String, List<Answer>> result = new HashMap<>();
		List<Answer> unreadAnswers = new ArrayList<>();
		List<Answer> readAnswers = new ArrayList<>();

		// Retrieve answer_id as a single string
		String getAnswerIdsQuery = "SELECT answer_id FROM cse360question WHERE id = ?";
		String answerIdStr = null;

		try (PreparedStatement stmt = connection.prepareStatement(getAnswerIdsQuery)) {
			stmt.setInt(1, questionId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				answerIdStr = rs.getString("answer_id");
			}
		}

		// If no answer IDs exist, return empty lists
		if (answerIdStr == null || answerIdStr.trim().isEmpty()) {
			result.put("unread", unreadAnswers);
			result.put("read", readAnswers);
			return result;
		}

		List<Integer> answerIds = Arrays.stream(answerIdStr.split(",\\s*")).map(Integer::parseInt)
				.collect(Collectors.toList());

		if (answerIds.isEmpty()) {
			result.put("unread", unreadAnswers);
			result.put("read", readAnswers);
			return result;
		}

		// dynamic SQL query
		String placeholders = answerIds.stream().map(id -> "?").collect(Collectors.joining(", "));
		String query = "SELECT a.*, "
				+ "CASE WHEN av.is_read IS NULL OR av.is_read = FALSE THEN 'unread' ELSE 'read' END AS read_status "
				+ "FROM cse360answer a " + "LEFT JOIN cse360answerviews av ON a.id = av.answer_id AND av.user_id = ? "
				+ "WHERE a.id IN (" + placeholders + ")";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			for (int i = 0; i < answerIds.size(); i++) {
				pstmt.setInt(i + 2, answerIds.get(i));
			}

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_on");
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_on");
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				String readStatus = rs.getString("read_status");

				Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, null, null, null);

				if ("unread".equals(readStatus)) {
					unreadAnswers.add(answer);
				} else {
					readAnswers.add(answer);
				}
			}
		}

		result.put("unread", unreadAnswers);
		result.put("read", readAnswers);
		return result;
	}

	/**
	 * Returns a list of all questions that have no potential answers
	 * 
	 * @return 					A List of question objects representing all unanswered questions
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllUnansweredQuestions() throws SQLException {
		String query = "SELECT * FROM cse360question WHERE answer_id IS NULL OR answer_id = ''"; // selecting all of the
																									// rows in the
																									// database
		List<Question> questions = new ArrayList<>(); // that don't have an answer id

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				int preferredAnswer = rs.getInt("preferred_answer");

				List<String> comp = textDeserial(title + " " + text);

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, authorId, createdOn, updatedOn, comp, preferredAnswer,
						author, authorName, relatedId);

				// Add question object to the list questions
				questions.add(question);
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	/**
	 * Returns a list of all questions that are still "unresolved." Interpreted here
	 * 
	 * @return 					A List of question objects representing all unresolved questions
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllUnresolvedQuestions() throws SQLException {
		// "preferred_answer" is the int column that can store a chosen "best" or
		// "accepted" answer
		// We treat null or 0 as “unresolved.”
		String query = "SELECT * FROM cse360question WHERE preferred_answer IS NULL OR preferred_answer = 0";
		List<Question> questions = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_on");
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_on");
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				int preferredAnswer = rs.getInt("preferred_answer");

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";
				if (author != null) {
					authorName = author.getName();
				}
				String answerIDs = rs.getString("answer_id");
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				Question q = new Question(id, title, text, authorId, createdOn, updatedOn, textDeserial(title + text),
						preferredAnswer, author, authorName, relatedId);
				questions.add(q);
			}
		}
		return questions;
	}

	/**
	 * Returns a list of the current user's unresolved questions
	 * 
	 * @param userId 			The id of the user you are working with
	 * 
	 * @return 					A List of question objects representing all unresolved questions for the passed user id
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllUnresolvedQuestionsForUser(int userId) throws SQLException {
		String query = "SELECT q.* FROM cse360question q "
				+ "WHERE (q.preferred_answer IS NULL OR q.preferred_answer = 0) " + "AND q.author = ?";

		List<Question> questions = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId); // For questions posted by the user
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_on");
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_on");
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				int preferredAnswer = rs.getInt("preferred_answer");

				User author = databaseHelper.getUser(authorId);
				String authorName = (author != null) ? author.getName() : "User";
				String answerIDs = rs.getString("answer_id");
				List<String> relatedId = (answerIDs != null) ? List.of(answerIDs.split(",\\s")) : null;

				// Create Question object without unread count (since we now get unread answers
				// dynamically)
				Question q = new Question(id, title, text, authorId, createdOn, updatedOn, textDeserial(title + text),
						preferredAnswer, author, authorName, relatedId);

				questions.add(q);
			}
		}
		return questions;
	}

	/**
	 * Retrieves only those answers that are not the chosen preferred answer
	 * 
	 * @param questionId 		The id of the question you are working with
	 * 
	 * @return 					A List of answer objects representing answers related to a question
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Answer> getPotentialAnswersForQuestion(int questionId) throws SQLException {
		Question q = getQuestion(questionId);
		if (q == null) {
			return new ArrayList<>();
		}

		int preferredId = q.getPreferredAnswer();

		// Get all answers for the question
		List<Answer> allAnswers = getAllAnswersForQuestion(questionId);
		List<Answer> potentialAnswers = new ArrayList<>();

		// Filter out the preferred answer
		for (Answer ans : allAnswers) {
			if (ans.getId() != preferredId) {
				potentialAnswers.add(ans);
			}
		}

		return potentialAnswers;
	}

	/**
	 * Retrieve all questions that have potential answers
	 * 
	 * @return 					A List of question objects representing all answered questions
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllAnsweredQuestions() throws SQLException {
		String query = "SELECT * FROM cse360question WHERE answer_id IS NOT NULL AND answer_id <> ''"; // selecting all
																										// of the rows
																										// in the
																										// database
		List<Question> questions = new ArrayList<>(); // that don't have an answer id

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title"); // for each row, get all of the user attributes
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;
				int preferredAnswer = rs.getInt("preferred_answer");

				List<String> comp = textDeserial(title + " " + text);

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new question object with the pulled info
				Question question = new Question(id, title, text, authorId, createdOn, updatedOn, comp, preferredAnswer,
						author, authorName, relatedId);

				// Add question object to the list questions
				questions.add(question);
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	/**
	 * Retrieve all answers from the answer database
	 * 
	 * @return 					A List of answer objects representing all answers in the answer database
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Answer> getAllAnswers() throws SQLException {
		String query = "SELECT * FROM cse360answer"; // selecting all of the rows in the database
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				// Pull the info from that row for one answer object
				int id = rs.getInt("id");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				String answerIDs = rs.getString("answer_id");
				// convert comma separated list into an array
				List<String> relatedId = answerIDs != null ? List.of(answerIDs.split(",\\s")) : null;

				// Create a new answer object with the pulled info
				Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName, relatedId);

				// Add the new answer object to the list of answer objects
				answers.add(answer);
			}
		}
		// Return the list of answer objects
		return answers;
	}

	/**
	 * Retrieve all of the answers that are associated with a given question id from
	 * the question database.
	 * 
	 * @param questionID 		The id of the question you are working with
	 * 
	 * @return 					A List of answer objects representing all answers for the passed question
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Answer> getAllAnswersForQuestion(int questionID) throws SQLException {
		String query = "SELECT answer_id FROM cse360question WHERE id = ?";
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIDs = rs.getString("answer_id");

				if (answerIDs == null || answerIDs.trim().isEmpty()) {
					// System.err.println("Error: There are no answers related to this question.");
					// // Debug
					return answers;
				}

				// convert comma separated list into an array
				String[] answerIdArray = answerIDs.split(",\\s");

				String temp = String.join(",", Collections.nCopies(answerIdArray.length, "?"));
				String newQuery = "SELECT id, text, author, created_On, updated_On, answer_id FROM cse360answer WHERE id IN ("
						+ temp + ")";
				try (PreparedStatement upstmt = connection.prepareStatement(newQuery)) {

					for (int i = 0; i < answerIdArray.length; i++) {
						upstmt.setInt(i + 1, Integer.parseInt(answerIdArray[i].trim()));
					}

					ResultSet newRs = upstmt.executeQuery();

					while (newRs.next()) {
						int id = newRs.getInt("id");
						String text = newRs.getString("text");
						int authorId = newRs.getInt("author");
						Timestamp created = newRs.getTimestamp("created_On");
						// Convert to LocalDateTime format
						LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
						Timestamp updated = newRs.getTimestamp("updated_On");
						// Convert to LocalDateTime format
						LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

						User author = databaseHelper.getUser(authorId);
						String authorName = "User";

						if (author != null) {
							authorName = author.getName();
						}

						String subAnswerIDs = newRs.getString("answer_id");
						// convert comma separated list into an array
						List<String> relatedId = subAnswerIDs != null ? List.of(subAnswerIDs.split(",\\s")) : null;

						// Create a new answer object with the pulled info
						Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName,
								relatedId);

						// Add the new answer object to the list of answer objects
						answers.add(answer);
					}
				}
			} else {
				System.err.println("Error. Question id, " + questionID + ", was not found");
			}
		}
		// Return the list of answer objects
		return answers;
	}

	/**
	 * Retrieve all of the answers that are associated with a given answer id from
	 * the answer database
	 * 
	 * @param answerID 			The id of the answer you are working with
	 * 
	 * @return 					A List of answer objects representing all answers related to the passed answer
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Answer> getAllAnswersForAnswer(int answerID) throws SQLException {
		String query = "SELECT answer_id FROM cse360answer WHERE id = ?";
		List<Answer> answers = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String answerIDs = rs.getString("answer_id");

				if (answerIDs == null || answerIDs.trim().isEmpty()) {
					// System.err.println("Error: There are no answers related to this answer."); //
					// Debug
					return answers;
				}

				// convert comma separated list into an array
				String[] answerIdArray = answerIDs.split(",\\s");

				String temp = String.join(",", Collections.nCopies(answerIdArray.length, "?"));
				String newQuery = "SELECT id, text, author, created_On, updated_On, answer_id FROM cse360answer WHERE id IN ("
						+ temp + ")";
				try (PreparedStatement upstmt = connection.prepareStatement(newQuery)) {

					for (int i = 0; i < answerIdArray.length; i++) {
						upstmt.setInt(i + 1, Integer.parseInt(answerIdArray[i].trim()));
					}

					ResultSet newRs = upstmt.executeQuery();

					while (newRs.next()) {
						int id = newRs.getInt("id");
						String text = newRs.getString("text");
						int authorId = newRs.getInt("author");
						Timestamp created = newRs.getTimestamp("created_On");
						// Convert to LocalDateTime format
						LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
						Timestamp updated = newRs.getTimestamp("updated_On");
						// Convert to LocalDateTime format
						LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

						User author = databaseHelper.getUser(authorId);
						String authorName = "User";

						if (author != null) {
							authorName = author.getName();
						}

						String subAnswerIDs = newRs.getString("answer_id");
						// convert comma separated list into an array
						List<String> relatedId = subAnswerIDs != null ? List.of(subAnswerIDs.split(",\\s")) : null;

						// Create a new answer object with the pulled info
						Answer answer = new Answer(id, text, authorId, createdOn, updatedOn, author, authorName,
								relatedId);

						// Add the new answer object to the list of answer objects
						answers.add(answer);
					}
				}
			} else {
				System.err.println("Error. Answer id, " + answerID + ", was not found");
			}
		}
		// Return the list of answer objects
		return answers;
	}

	/**
	 * Method used to convert the text of a question into a list of unique words
	 * without special characters for comparison to others
	 * 
	 * @param text 				The text you wish to deserialize
	 * 
	 * @return 					The deserialized text
	 * 
	 */
	public List<String> textDeserial(String text) {
		List<String> uniqueWords = new ArrayList<>();
		Set<String> existingWords = new HashSet<>();
		if (text == null || text.isEmpty()) {
			// If empty return new empty list
			return new ArrayList<>();
		} else {
			// Clean string of all but alpha-numeric characters and spaces
			String cleanText = text.replaceAll("[^a-zA-Z0-9\\s]", "").toLowerCase();

			// Split the string using the spaces
			String[] wordsArray = cleanText.split("\\s+");

			for (String word : wordsArray) {
				if (!existingWords.contains(word)) {
					uniqueWords.add(word);
					existingWords.add(word);
				}
			}

			// Convert back to list and return
			return new ArrayList<>(uniqueWords);
		}
	}

	/**
	 * Update a question object with a preferred answer id
	 * 
	 * @param question 			The question object you are working with
	 * 
	 */
	public void updatePreferredAnswer(Question question) {
		String query = "UPDATE cse360question SET preferred_answer = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, question.getPreferredAnswer());
			pstmt.setInt(2, question.getId());
			int updated = pstmt.executeUpdate();
			if (updated > 0) {
				System.out.println("Preferred answer updated.");
			} else {
				System.out.println("No matching question found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update question in updatePreferredAnswer method.");
		}
	}

	/**
	 * Update the contents of a question object with those of the passed question
	 * object
	 * 
	 * @param question 			The question object you are working with
	 * 
	 */
	public void updateQuestion(Question question) {
		String query = "UPDATE cse360question Set title = ?, text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, question.getTitle());
			pstmt.setString(2, question.getText());
			pstmt.setInt(3, question.getId());

			int updated = pstmt.executeUpdate();
			// Check if any changes were made
			if (updated > 0) {
				System.out.println("Question has been updated."); // Debug
			} else {
				System.out.println("No matching question was found."); // Debug
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update question in updateQuestion method.");
		}
	}

	/**
	 * Update the contents of a answer object with those of pass answer object
	 * 
	 * @param answer 			The answer object you are working with
	 * 
	 */
	public void updateAnswer(Answer answer) {
		String query = "UPDATE cse360answer Set text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getId());

			int updated = pstmt.executeUpdate();
			// Check if any changes were made
			if (updated > 0) {
				System.out.println("Answer has been updated."); // Debug
			} else {
				System.out.println("No matching answer was found."); // Debug
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update question in updateAnswer method.");
		}
	}

	/**
	 * Search the question database for similar question title + text
	 * 
	 * @param input 		A string containing the text you wish to search for
	 * 
	 * @return 				A List of question objects representing all questions relating to the searched text
	 * 
	 */
	public List<Question> searchQuestionDatabase(String input) {
		List<Question> questions;
		// Get list words from current text input string
		List<String> entry = databaseHelper.qaHelper.textDeserial(input);
		try {
			questions = databaseHelper.qaHelper.getAllQuestions();
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error trying to .getAllQuestions() within searchQuestionDatabase method");
			return null;
		}

		// Use Hashmap to sort
		Map<Question, Integer> similarity = new HashMap<>();

		for (Question question : questions) {
			// This will hold words already seen
			Set<String> existingWords = new HashSet<>();
			// Get list of words to compare from current question
			List<String> compList = question.getComp();
			int count = 0;

			// Count the matches
			for (String word : entry) {
				if (compList.contains(word) && !existingWords.contains(word)) {
					existingWords.add(word);
					count++;
				}
			}

			double score = ((double) count/* / compList.size() */); // Factoring in size doesn't seem reliable

			// Set initial threshold to add comp to map
			if (score > 0.05) {
				similarity.put(question, (int) score);
			}
		}

		// Sort based on similarity score
		List<Question> sortedList = similarity.entrySet().stream()
				.sorted(Map.Entry.<Question, Integer>comparingByValue().reversed()).map(Map.Entry::getKey)
				.collect(Collectors.toList());

		return sortedList;
	}

	/**
	 * Registers a new private message in the database.
	 * 
	 * @param message 			A message object you are working with
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void createMessage(Message message) throws SQLException {
	    String insertMessage = "INSERT INTO cse360message (senderid, recipientid, subject, message, referenceId, referenceType) VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS)) {
	        pstmt.setInt(1, message.getSenderID());
	        pstmt.setInt(2, message.getRecipientID());
	        pstmt.setString(3, message.getSubject());
	        pstmt.setString(4, message.getMessage());
	        pstmt.setInt(5, message.getReferenceID());
	        pstmt.setString(6, message.getReferenceType().substring(0,1).toLowerCase());
	        pstmt.executeUpdate();

	        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                int messageID = generatedKeys.getInt(1);
	                message.setMessageID(messageID);
	            }
	        }
	    }
	}

	/**
	 * Deletes a message from the cse360message table matching the provided message id
	 * 
	 * @param messageID 		The id of the message you with to delete
	 * 
	 * @return 					A boolean indicating whether the function was successfully performed
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public boolean deleteMessage(int messageID) throws SQLException {
		String query = "DELETE FROM cse360message WHERE messageid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, messageID);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		}
	}

	/**
	 * Returns a List of all messages in the cse360message table
	 * 
	 * @return 					A List of message objects representing all messages
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Message> getAllMessages() throws SQLException {
	    String query = "SELECT * FROM cse360message";
	    List<Message> messages = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            int messageID = rs.getInt("messageid");
	            int senderID = rs.getInt("senderid");
	            int recipientID = rs.getInt("recipientid");
	            String subject = rs.getString("subject");
	            String content = rs.getString("message");

	            Message message = new Message(databaseHelper, messageID, senderID, recipientID, subject, content);
	            messages.add(message);
	        }
	    }
	    return messages;
	}

	/**
	 * Returns a List of Message objects that have an author matching the passed
	 * user id.
	 * 
	 * @param id 				The id of the user you are working with
	 * 
	 * @return 					A List of message objects representing messages authored by the passed user id
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Message> retrieveMessagesByUserId(int id) throws SQLException {
	    String query = "SELECT * FROM cse360message WHERE recipientid = ?";
	    List<Message> messages = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int messageID = rs.getInt("messageid");
	                int senderID = rs.getInt("senderid");
	                int recipientID = rs.getInt("recipientid");
	                String subject = rs.getString("subject");
	                String content = rs.getString("message");
	                int referenceID = rs.getInt("referenceid");
	                String referenceType = rs.getString("referencetype");

	                Message message = new Message(databaseHelper, messageID, senderID, recipientID, subject, content);

	                message.setReferenceID(referenceID);
	                message.setReferenceType(referenceType);

	                messages.add(message);
	            }
	        }
	    }
	    return messages;
	}
	
	public List<Message> retrieveMessagesFromUser(int id) throws SQLException {
	    String query = "SELECT * FROM cse360message WHERE senderid = ?";
	    List<Message> messages = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                int messageID = rs.getInt("messageid");
	                int senderID = rs.getInt("senderid");
	                int recipientID = rs.getInt("recipientid");
	                String subject = rs.getString("subject");
	                String content = rs.getString("message");
	                int referenceID = rs.getInt("referenceid");
	                String referenceType = rs.getString("referencetype");

	                Message message = new Message(databaseHelper, messageID, senderID, recipientID, subject, content);

	                message.setReferenceID(referenceID);
	                message.setReferenceType(referenceType);

	                messages.add(message);
	            }
	        }
	    }
	    return messages;
	}

	/**
	 * Returns a List of Message objects that are related to the passed user id
	 * 
	 * @param id 				The id of the user you are working with
	 * 
	 * @return 					A List of message objects representing the messages related to the passed user id
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Message> retrieveMessagesRelatedToUserId(int id) throws SQLException {
		String query = "SELECT * FROM cse360message WHERE senderid = ? OR recipientid = ?";
		List<Message> messages = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.setInt(2, id);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					int messageID = rs.getInt("messageid");
					int senderID = rs.getInt("senderid");
					int recipientID = rs.getInt("recipientid");
					String subject = rs.getString("subject");
					String content = rs.getString("message");
					int referenceID = rs.getInt("referenceid");
	                String referenceType = rs.getString("referencetype");

					Message message = new Message(databaseHelper, referenceID, referenceType, messageID, senderID, recipientID, subject, content);
					messages.add(message);
				}
			}
		}
		return messages;
	}

	/**
	 * Returns a Question object related to a provided answer id
	 * 
	 * @param answerID 			The id of the answer you are working with
	 * 
	 * @return 					A question object representing the question you were searching for
	 * 
	 */
	public Question getQuestionForAnswer(int answerID) {
		List<Question> questions;
		try {
			// Retrieve the question database
			questions = getAllQuestions();
		} catch (SQLException e) {
			System.err.println("Error trying to retrieve question database in getAllReviewedByMeQuestions() method.");
			return null;
		}
		// Iterate through each question
		for (Question question : questions) {
			// Check if the current question object has the answerId we're looking for in
			// getRelatedId()
			if (question.getRelatedId().contains(String.valueOf(answerID))) {
				return question;
			}
		}
		System.out.println("Error: No question found relating to answerId in getQuestionForAnswer() method.");
		return null;
	}

	/**
	 * Registers a new review in the database.
	 * 
	 * @param review 			A review object of the review you are working with
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public void registerReview(Review review) throws SQLException {
		String insertReview = "INSERT INTO cse360review (forQuestion, relatedId, text, author) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertReview)) {
			pstmt.setBoolean(1, review.getForQuestion());
			pstmt.setInt(2, review.getRelatedId());
			pstmt.setString(3, review.getText());
			pstmt.setInt(4, review.getAuthorId());
			pstmt.executeUpdate();
		}
		System.out.println("Review registered successfully.");
	}

	/**
	 * Get a review object with a provided review id
	 * 
	 * @param reviewID 			The id of the review you are working with
	 * 
	 * @return 					A review object representing the review you were searching for
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Review getReview(Integer reviewID) throws SQLException {
		// Search the review database for a match to the review id
		String query = "SELECT * FROM cse360review WHERE id = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewID);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer messageCount = getMessageCount(id, 'r');

				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, messageCount, voteCount);

				// Return the review object
				return review;
			}
		}
		return null;
	}

	/**
	 * Get a review object with a provided review text
	 * 
	 * @param reviewText 		The text of the review you were looking for
	 * 
	 * @return 					A review object representing the review you were looking for
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Review getReview(String reviewText) throws SQLException {
		// Search the review database for a match to the review text
		String query = "SELECT * FROM cse360review AS c WHERE c.text = ?	";

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reviewText);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer messageCount = getMessageCount(id, 'r');
				
				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, messageCount, voteCount);

				// Return the review object
				return review;
			}
		}
		return null;
	}

	/**
	 * Update the contents of a review object with those of the passed review object
	 * 
	 * @param review 			A review object of the review you are working with
	 * 
	 */
	public void updateReview(Review review) {
		String query = "UPDATE cse360review Set text = ?, updated_on = CURRENT_TIMESTAMP WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, review.getText());
			pstmt.setInt(2, review.getId());

			int updated = pstmt.executeUpdate();
			// Check if any changes were made
			if (updated > 0) {
				System.out.println("Review has been updated."); // Debug
			} else {
				System.out.println("No matching review was found."); // Debug
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update review in updateReview method.");
		}
	}

	/**
	 * Deletes a review row from the SQL table
	 * 
	 * @param id 			The id of the review you wish to delete
	 * 
	 * @return 				A boolean indicating whether the function was successfully performed
	 * 
	 */
	public boolean deleteReview(int id) {
		String query = "DELETE FROM cse360review AS c WHERE c.id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);

			// Check if any matches were found and deleted
			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETE-REVIEW: Review successfully deleted");
				return true;
			}
			System.out.println("DELETE-REVIEW: Review was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETE-REVIEW: SQL Error - " + e.getMessage());
			return false;
		}
	}

	/**
	 * Retrieves all questions that have been reviewed or that have answers that
	 * have been reviewed
	 * 
	 * @return 					A List of question objects representing all questions with reviews
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllReviewedQuestions() throws SQLException {
		// Retrieve all review rows from the database
		String query = "SELECT * FROM cse360review ";
		List<Review> reviews = new ArrayList<>();
		List<Question> questions = new ArrayList<>();
		List<Integer> containedIn = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Boolean forQuestion = rs.getBoolean("forQuestion");
				Integer relatedId = rs.getInt("relatedId");

				// Check if review is for a question
				if (forQuestion) {
					// Check if its already contained in the list
					if (containedIn == null || !containedIn.contains(relatedId)) {
						questions.add(databaseHelper.qaHelper.getQuestion(relatedId));
						containedIn.add(relatedId);
					}
					// Or if its an answer
				} else {
					// Check if its already contained in the list
					if (containedIn == null
							|| !containedIn.contains(databaseHelper.qaHelper.getQuestionForAnswer(relatedId).getId())) {
						questions.add(databaseHelper.qaHelper.getQuestionForAnswer(relatedId));
						containedIn.add(databaseHelper.qaHelper.getQuestionForAnswer(relatedId).getId());
					}
				}
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	/**
	 * Retrieves all questions that have been reviewed by the currentUser or that
	 * have answers that have been reviewed by the currentUser
	 * 
	 * @return 					A List of question objects representing all questions reviewed by current user
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Question> getAllReviewedByMeQuestions() throws SQLException {
		// Retrieve all review rows from the database that match the currentUser id
		String query = "SELECT * FROM cse360review WHERE author = ?";
		List<Review> reviews = new ArrayList<>();
		List<Question> questions = new ArrayList<>();
		List<Integer> containedIn = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, databaseHelper.currentUser.getUserId());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Boolean forQuestion = rs.getBoolean("forQuestion");
				Integer relatedId = rs.getInt("relatedId");

				// Check if review is for a question
				if (forQuestion) {
					// Check if its already contained in the list
					if (containedIn == null || !containedIn.contains(relatedId)) {
						questions.add(databaseHelper.qaHelper.getQuestion(relatedId));
						containedIn.add(relatedId);
					}
					// Or if its for an answer
				} else {
					// Check if its already contained in the list
					if (containedIn == null
							|| !containedIn.contains(databaseHelper.qaHelper.getQuestionForAnswer(relatedId).getId())) {
						questions.add(databaseHelper.qaHelper.getQuestionForAnswer(relatedId));
						containedIn.add(databaseHelper.qaHelper.getQuestionForAnswer(relatedId).getId());
					}
				}
			}
		}
		// Return the assembled list of question objects
		return questions;
	}

	/**
	 * Retrieves a list of Review objects that are related to the provided question id
	 * 
	 * @param questionID 		The id of the question you are working with
	 * 
	 * @return 					A List of review objects representing all reviews for a passed question
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */	
	public List<Review> getReviewsForQuestion(int questionID) throws SQLException {
		String query = "SELECT * FROM cse360review WHERE forQuestion = true AND relatedId = ?";
		List<Review> reviews = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, questionID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer messageCount = getMessageCount(id, 'r');
				
				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, messageCount, voteCount);

				// Add the new answer object to the list of answer objects
				reviews.add(review);
			}
		}
		// Return the list of review objects
		return reviews;
	}

	/**
	 * Retrieves a list of Reviews that are related to the provided answer id
	 * 
	 * @param answerID 			The id of the answer you are working with
	 * 
	 * @return 					A List of review objects representing all reviews for the passed answer
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Review> getReviewsForAnswer(int answerID) throws SQLException {
		String query = "SELECT * FROM cse360review WHERE forQuestion = false AND relatedId = ?";
		List<Review> reviews = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, answerID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer count = getMessageCount(id, 'r');
				
				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, count, voteCount);

				// Add the new answer object to the list of answer objects
				reviews.add(review);
			}
		}
		// Return the list of review objects
		return reviews;
	}

	/**
	 * Retrieve all reviews from the review database
	 * 
	 * @return 					A List of review objects representing all reviews in the database
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Review> getAllReviews() throws SQLException {
		String query = "SELECT * FROM cse360review";
		List<Review> reviews = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer count = getMessageCount(id, 'r');
				
				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, count, voteCount);

				// Add the new answer object to the list of answer objects
				reviews.add(review);
			}
		}
		// Return the list of review objects
		return reviews;
	}

	/**
	 * Retrieve only reviews written by the current user from the review database
	 * 
	 * @return 					A List of review objects representing all reviews authored by the current user
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public List<Review> getMyReviews() throws SQLException {
		String query = "SELECT * FROM cse360review WHERE author = ?";
		List<Review> reviews = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, databaseHelper.currentUser.getUserId());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				Boolean forQuestion = rs.getBoolean("forQuestion"); // for each row, get all of the review attributes
				Integer relatedId = rs.getInt("relatedId");
				String text = rs.getString("text");
				int authorId = rs.getInt("author");
				Timestamp created = rs.getTimestamp("created_On");
				// Convert to LocalDateTime format
				LocalDateTime createdOn = created != null ? created.toLocalDateTime() : null;
				Timestamp updated = rs.getTimestamp("updated_On");
				// Convert to LocalDateTime format
				LocalDateTime updatedOn = updated != null ? updated.toLocalDateTime() : null;

				User author = databaseHelper.getUser(authorId);
				String authorName = "User";

				if (author != null) {
					authorName = author.getName();
				}

				// Get the message count for the review
				Integer count = getMessageCount(id, 'r');
				
				// Get the vote count for the review
				Integer voteCount = getVoteCountForReview(id);

				// Create a new review object with the pulled info
				Review review = new Review(id, forQuestion, relatedId, text, authorId, createdOn, updatedOn, author,
						authorName, count, voteCount);

				// Add the new answer object to the list of answer objects
				reviews.add(review);
			}
		}
		// Return the list of review objects
		return reviews;
	}

	/**
	 * Searches the message table database and returns the number of message
	 * relating to that object
	 * 
	 * @param referenceId 		The id of the object you are working with
	 * @param referenceType 	The type of object passed in the first parameter
	 * 
	 * @return 					An Integer representing the total messages
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Integer getMessageCount(Integer referenceId, char referenceType) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360message WHERE referenceId = ? AND referenceType = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, referenceId);
			pstmt.setString(2, String.valueOf(referenceType));
			ResultSet rs = pstmt.executeQuery();
			// Variable to store the count of how many messages match the parameters
			int count = 0;
			// Retrieve the count
			if (rs.next()) {
				count = rs.getInt(1);
			}

			// Return the count
			return count;
		}
	}

	/**
	 * Returns the total number of messages in the system for a given user.
	 * This includes all messages where the user is either the sender or the recipient.
	 *
	 * @param userId The user ID to search messages for.
	 * @return The total number of messages related to the user.
	 * @throws SQLException If a database access error occurs.
	 */
	public int getTotalMessageCountForUser(int userId) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM cse360message WHERE senderid = ? OR recipientid = ?";

	    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
	        stmt.setInt(1, userId);
	        stmt.setInt(2, userId);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt(1);
	            }
	        }
	    }
	    return 0;
	}

	/**
	 * Registers a new vote for a review object in the SQL table
	 * 
	 * @param reviewId 		The id of the review you are working with
	 * @param vote 			An integer representing an upvote or downvote for a review
	 * 
	 */
	public void registerVoteForReview(Integer reviewId, Integer vote) {
		String selectQuery = "SELECT vote FROM cse360review WHERE id = ?";
		String updateQuery = "UPDATE cse360review Set vote = ? WHERE id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(selectQuery);
				PreparedStatement ustmt = connection.prepareStatement(updateQuery)) {
			pstmt.setInt(1, reviewId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int currentVote = rs.getInt("vote");
				int newVote = currentVote + vote;
				
				// Debug
				System.out.println("Previous vote: " + currentVote + ", New vote: " + (currentVote + vote));

				ustmt.setInt(1, newVote);
				ustmt.setInt(2, reviewId);
				int updated = ustmt.executeUpdate();

				// Check if any changes were made
				if (updated > 0) {
					System.out.println("Review vote has been updated."); // Debug
				} else {
					System.out.println("Error: No matching review was found."); // Debug
				}
			} else {
				System.out.println("Error: Could not find review by that id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error trying to update review vote in registerVoteForReview() method.");
		}
	}

	/**
	 * Retrieves the count of reviews in the database written by a user
	 * 
	 * @param userId 			The id of the user you are working with
	 * 
	 * @return 					An Integer value representing the number of reviews for a user id
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Integer getReviewCountForReviewer(Integer userId) throws SQLException {
		String query = "SELECT COUNT(*) FROM cse360review WHERE author = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			// Variable to store the count of how many reviews match the parameters
			int count = 0;
			// Retrieve the count
			if (rs.next()) {
				count = rs.getInt(1);
			}

			// Return the count
			return count;
		}
	}

	/**
	 * Retrieves the sum of review votes for a user - votes are upvote/downvotes on
	 * reviews
	 * 
	 * @param userId 			The id of the user you are working with
	 * 
	 * @return 					An Integer value representing the total upvote/downvote count on reviews authored by a user id
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Integer getVoteCountForReviewer(Integer userId) throws SQLException {
		String query = "SELECT vote FROM cse360review WHERE author = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			// Variable to store the count of review votes matching the user
			int count = 0;
			// Retrieve the count
			if (rs.next()) {
				count += rs.getInt("vote");
			}

			// Return the count
			return count;
		}
	}

	/**
	 * Retrieves the vote value for a review - votes are upvote/downvotes on reviews
	 * 
	 * @param reviewId 			The id of the review you are working with
	 * 
	 * @return 					An Integer value representing the upvote/downvote total for a review
	 * 
	 * @throws SQLException 	In case the database throws an error
	 * 
	 */
	public Integer getVoteCountForReview(Integer reviewId) throws SQLException {
		String query = "SELECT vote FROM cse360review WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, reviewId);
			ResultSet rs = pstmt.executeQuery();
			// Variable to store the count of review votes matching the user
			int count = 0;
			// Retrieve the count
			if (rs.next()) {
				count = rs.getInt("vote");
			}

			// Return the count
			return count;
		}
	}

	/**
	 * Retrieves the reviewer rating using getReviewCountForReviewer() and
	 * getVoteCountForReviewer
	 * 
	 * @param userId 		The id of the user you are working with
	 * 
	 * @return 				An Integer value representing a reviewers total reviewer rating
	 * 
	 */
	public Integer getReviewerRating(Integer userId) {
		Integer reviewCount;
		Integer voteCount;
		try {
			reviewCount = getReviewCountForReviewer(userId);
			voteCount = getVoteCountForReviewer(userId);
		} catch (SQLException e) {
			System.err.println("Reviwer-Rating: SQL Error - " + e.getMessage());
			return 0;
		}

		if (reviewCount <= 0) {
			return 0;
		} else {
			return voteCount / reviewCount;
		}
	}
}
