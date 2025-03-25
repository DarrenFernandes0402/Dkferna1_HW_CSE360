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
import application.Message;
import tests.*;

import application.User;

// This is a database class used to create, read, update, and delete SQL databases among other managing functions in order
// to maintain a database of questions and answers and to perform various functions to them.

public class QAHelper1 {
	private DatabaseHelper databaseHelper;

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/QADatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	public Connection connection = null;
	public Statement statement = null;

	public QAHelper1(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}

	// Initialize connection to database
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

	// Create the tables that will be used to store the info
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
				+ "messageid INT AUTO_INCREMENT PRIMARY KEY, "
//		        + "referenceid INT, "
//		        + "referencetype VARCHAR(20), "
				+ "senderid INT, " + "recipientid INT, " + "subject TEXT, " + "message TEXT, "
				+ "createdon TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
				+ "updatedon TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)";
		statement.execute(messageTable);

		String answerViewsTable = "CREATE TABLE IF NOT EXISTS cse360answerviews (" + "answer_id INT NOT NULL, "
				+ "user_id INT NOT NULL, " + "is_read BOOLEAN DEFAULT FALSE, " + "PRIMARY KEY (answer_id, user_id))";

		statement.execute(answerViewsTable);

	}

	// Creates an auxiliary table tracking which answers each student has read
	public void createAnswerViewsTable() throws SQLException {
		// *** REMOVED RECURSIVE CALL TO createAnswerViewsTable() ***

	}

	// This helps us keep track of how many 'unread' answers remain for each
	// question or user.
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

	// Check if the database is empty - Only checks the question database at the
	// moment
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360question";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new question in the database.
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

	// Registers a new answer in the database.
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

	// Registers a new answer in the database.
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
	
	public void setPreferredAnswer(int questionId, int answerId) throws SQLException {
		String updateQuery = "UPDATE cse360question SET preferred_answer = ? WHERE id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
			pstmt.setInt(1, answerId);
			pstmt.setInt(2, questionId);
			pstmt.executeUpdate();
		}
		System.out.println("Preferred answer set for question ID: " + questionId);
	}


	// Checks if answer text is found within questions related answers
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

	// Deletes a question row from the SQL table
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

	// Deletes a question row from the SQL table
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

	// Add a relation to the question database
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

	// Add a relation to the answer database
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

	// Delete a relation from the relation database
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

	// Get a question object with a provided question id
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

	// Get a question object with a provided question title
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

	// Get an answer object with a provided answer id
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

	// Get an answer object with a provided answer text
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

	// Retrieve all questions from the question database
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

	// Retrieves how many answers a given student has not yet read across all
	// questions
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
		
	    List<Integer> answerIds = Arrays.stream(answerIdStr.split(",\\s*"))
	            .map(Integer::parseInt)
	            .collect(Collectors.toList());

	    if (answerIds.isEmpty()) {
	        result.put("unread", unreadAnswers);
	        result.put("read", readAnswers);
	        return result;
	    }

	    // dynamic SQL query
	    String placeholders = answerIds.stream().map(id -> "?").collect(Collectors.joining(", "));
	    String query = "SELECT a.*, " +
	                   "CASE WHEN av.is_read IS NULL OR av.is_read = FALSE THEN 'unread' ELSE 'read' END AS read_status " +
	                   "FROM cse360answer a " +
	                   "LEFT JOIN cse360answerviews av ON a.id = av.answer_id AND av.user_id = ? " +
	                   "WHERE a.id IN (" + placeholders + ")";

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

	// Returns a list of all questions that have no potential answers
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

	// Returns a list of all questions that are still "unresolved." Interpreted here
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

	// Returns a list of the current user's unresolved questions
	public List<Question> getAllUnresolvedQuestionsForUser(int userId) throws SQLException {
	    String query = "SELECT q.* FROM cse360question q " +
	                   "WHERE (q.preferred_answer IS NULL OR q.preferred_answer = 0) " +
	                   "AND q.author = ?";

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

	            // Create Question object without unread count (since we now get unread answers dynamically)
	            Question q = new Question(id, title, text, authorId, createdOn, updatedOn, 
	                                      textDeserial(title + text), preferredAnswer, author, authorName, relatedId);
	            
	            questions.add(q);
	        }
	    }
	    return questions;
	}
	
	// Retrieves only those answers that are not the chosen preferred answer
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

	// Retrieve all questions that have potential answers
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

	// Retrieve all answers from the answer database
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

	// Retrieve all of the answers that are associated with a given question id from
	// the question database
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

	// Retrieve all of the answers that are associated with a given answer id from
	// the answer database
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

	// Method used to convert the text of a question into a list of unique words
	// without special characters for comparison to others
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

	// Update a question object with a preferred answer id
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

	// Update the contents of a question object with those of pass question object
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

	// Update the contents of a answer object with those of pass answer object
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

	// Search the question database for similar question title + text
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

	// Registers a new private message in the database.
	public void createMessage(Message message) throws SQLException {
		String insertMessage = "INSERT INTO cse360message (senderid, recipientid, subject, message) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertMessage, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setInt(1, message.getSenderID());
			pstmt.setInt(2, message.getRecipientID());
			pstmt.setString(3, message.getSubject());
			pstmt.setString(4, message.getMessage());
			pstmt.executeUpdate();

			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					int messageID = generatedKeys.getInt(1);
					// Set the messageID in the Message object if necessary
					message.setMessageID(messageID);
				}
			}
		}
	}

	public boolean deleteMessage(int messageID) throws SQLException {
		String query = "DELETE FROM cse360message WHERE messageid = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, messageID);
			int affectedRows = pstmt.executeUpdate();
			return affectedRows > 0;
		}
	}

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

	                Message message = new Message(databaseHelper, messageID, senderID, recipientID, subject, content);
	                messages.add(message);
	            }
	        }
	    }
	    return messages;
	}
	
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

	                Message message = new Message(databaseHelper, messageID, senderID, recipientID, subject, content);
	                messages.add(message);
	            }
	        }
	    }
	    return messages;
	}
}
