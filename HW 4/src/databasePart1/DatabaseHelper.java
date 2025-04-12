package databasePart1;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import application.Answer;
import application.Question;
import application.Request;
import application.User;
import tests.*;

/*******
 * <p> Title: DatabaseHelper Class. </p>
 * 
 * <p> Description: A Database Helper class that holds all of our methods taking data in and out of the database. </p>
 * 
 * <p> Copyright: CSE360 Team 8 @ 2025 </p>
 * 
 * @author CSE360 Team 8
 * 
 * @version 1.00	2025-03-24 Implementing the database methods
 * 
 */

public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	/**
	 * The database connection
	 */
	private Connection connection = null;
	/**
	 * The Statement used to process SQL Queries
	 */
	private Statement statement = null;
	/**
	 * The User that is currently logged in. 
	 */
	public User currentUser;

	// Create QAHelper object
	/**
	 * A QAHelper object to access the QA database methods. 
	 */
	public final QAHelper1 qaHelper;

	// Initialize new QAHelper object2
	/**
	 * A default constructor to initialize the database. 
	 */
	public DatabaseHelper() {
		qaHelper = new QAHelper1(this);
	}
	
	/**
	 * Sets up the main database and QA databases for testing.
	 * Set to drop the tables and repopulate the database depending on the flags set. 
	 * There are helper methods that will populate both databases reliably .
	 *
	 * @throws SQLException if anything fails while setting up the DB.
	 */
	public void connectToDatabase() throws SQLException {
		try {
			qaHelper.connectToDatabase();

			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to User database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			statement = connection.createStatement();
			/*------------------------------------------------------------------------------------------------*/
			/* You can use this command to clear the databases and restart from fresh. */

			boolean resetUserDatabase = false; // Set to true if you want to reset the User Database
			boolean resetQADatabase = false; // Set to true if you want to reset the QA Database

			int a = 0; // Set this to 1 if you wish to populate User Database(0 or 1)
			int b = 0; // Set this to the number of times you want to populate the QA
						// Database(0 or greater)

			/*------------------------------------------------------------------------------------------------*/

			// If set to true, then clear User Database
			if (resetUserDatabase) {
				statement.execute("DROP ALL OBJECTS");
				System.out.println("User Database cleared successfully.");
			}

			// If set to true, then clear QA Database
			if (resetQADatabase) {
				qaHelper.statement.execute("DROP ALL OBJECTS");
				System.out.println("Question/Answer Database cleared successfully.");
			}

			// Create tables for Users Database
			this.createTables();

			// Populate the User Database if set to above
			for (int i = 0; i < a; i++) {
				new PopulateUserDatabase(this).execute();
			}

			// Create tables for QA Database
			qaHelper.createTables();

			// Populate the QA Database if set to above
			for (int n = 0; n < b; n++) {
				new PopulateQADatabase(qaHelper).execute();
			}

		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	/**
	 * A setup function for when the tables have been reset.
	 * Sets up the User, RequestReviewer and InvitationCodes tables. 
	 * Will not create any tables if they already exist. 
	 * @throws SQLException if there is an error when creating tables.
	 */
	// Create the tables for the User Database
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" 
	+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " 
				+ "name VARCHAR(255), " 
				+ "password VARCHAR(255), "
				+ "email VARCHAR(255), "																					
				+ "roles VARCHAR(70), " 
				+ "reviewers VARCHAR(300), " 
				+ "otp BOOLEAN DEFAULT FALSE, "
				+ "banned BOOLEAN DEFAULT FALSE)";
		statement.execute(userTable);
		
		// Create the table for the reviewer request
		String requestReviewerTable = "CREATE TABLE IF NOT EXISTS cse360request (" 
                + "id INT AUTO_INCREMENT PRIMARY KEY, "
                + "request VARCHAR(500), "
                + "userName VARCHAR(255), "
                + "requestTOF BOOLEAN DEFAULT FALSE, "
		+ "requestATOF BOOLEAN DEFAULT FALSE, "
                + "notes VARCHAR(2000), "
                + "status VARCHAR(50), "
                + "originalId INT "
                + ")";
        statement.execute(requestReviewerTable);
		
		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" 
				+ "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE," 
				+ "generatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
		statement.execute(invitationCodesTable);

	}

	
	/**
	 * This is a helper method that will check if the database is empty 
	 * All it checks for is if there is any data in the User table.
	 * @return boolean for if the database is empty 
	 * @throws SQLException if it can not find the User table.
	 */
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	/**
 	* Creates a new request with the status 'OPEN' and no notes.
 	* The request is associated with the specified user and has originalId set to 0.
 	*
 	* @param requestText the content of the request
 	* @param userName the username of the person creating the request
 	* @throws SQLException if a database access error occurs
 	*/
    public void createNewRequest(String requestText, String userName) throws SQLException {
        String sql = "INSERT INTO cse360request (request, userName, requestTOF, notes, status, originalId) "
                   + "VALUES (?, ?, false, '', 'OPEN', 0)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, requestText);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        }
    }

	/**
 	* Retrieves all requests that are currently open or reopened.
 	*
 	* @return a list of open or reopened Request objects
 	* @throws SQLException if a database access error occurs
 	*/
    public List<Request> getAllOpenRequests() throws SQLException {
        String sql = "SELECT * FROM cse360request WHERE status='OPEN' OR status='REOPENED'";
        List<Request> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Request r = buildRequestFromResultSet(rs);
                list.add(r);
            }
        }
        return list;
    }
	/**
 	* Retrieves all requests that have been marked as closed.
 	*
 	* @return a list of closed Request objects
 	* @throws SQLException if a database access error occurs
 	*/
    public List<Request> getAllClosedRequests() throws SQLException {
        String sql = "SELECT * FROM cse360request WHERE status='CLOSED'";
        List<Request> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Request r = buildRequestFromResultSet(rs);
                list.add(r);
            }
        }
        return list;
    }
	
	/**
 	* Builds a Request object from a given ResultSet row.
 	*
 	* @param rs the ResultSet positioned at the current row
 	* @return a Request object with the extracted data
 	* @throws SQLException if a database access error occurs
 	*/
    private Request buildRequestFromResultSet(ResultSet rs) throws SQLException {
    	int id = rs.getInt("id");
    	String req = rs.getString("request");
    	String userName = rs.getString("userName");
    	boolean requestTOF = rs.getBoolean("requestTOF");
    	boolean requestATOF = rs.getBoolean("requestATOF");  // Added
    	String notes = rs.getString("notes");
    	String status = rs.getString("status");
    	int originalId = rs.getInt("originalId");

    	User user = getUser(userName);
    	Request r = new Request(id, req, user, requestTOF, requestATOF, notes, status, originalId);

        return r;
    }

	/**
 	* Closes a request by updating its status to 'CLOSED' and appending an optional note.
 	*
 	* @param requestId the ID of the request to close
 	* @param noteToAppend an optional note to append to the request's notes
 	* @throws SQLException if a database access error occurs
 	*/
    public void closeRequest(int requestId, String noteToAppend) throws SQLException {
        // 1) Retrieve existing notes
        Request r = getRequestById(requestId);
        String newNotes = r.getNotes() == null ? "" : r.getNotes();
        if (!noteToAppend.isEmpty()) {
            // Add semicolon delimiter
            newNotes += (newNotes.isEmpty() ? "" : ";") + noteToAppend;
        }

        // 2) Update status, notes
        String sql = "UPDATE cse360request SET status='CLOSED', notes=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newNotes);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }

	/**
 	* Reopens a closed request by creating a new request row with status 'REOPENED'.
 	* The new request references the original via originalId and contains merged notes.
 	*
 	* @param oldRequestId the ID of the closed request to be reopened
 	* @param updatedDescription the new request text for the reopened request
 	* @param additionalNote an optional note to append
 	* @param reopenedBy the user who is reopening the request
 	* @throws SQLException if the original request is not closed or if a database access error occurs
 	*/
    public void reopenRequest(int oldRequestId, String updatedDescription, String additionalNote, String reopenedBy) throws SQLException {
        // Retrieve the old request
        Request oldReq = getRequestById(oldRequestId);
        if (!"CLOSED".equalsIgnoreCase(oldReq.getStatus())) {
            throw new SQLException("Cannot reopen because the original is not closed.");
        }

        // Merge old notes with the new note
        String combinedNotes = oldReq.getNotes() == null ? "" : oldReq.getNotes();
        if (!additionalNote.isEmpty()) {
            combinedNotes += (combinedNotes.isEmpty() ? "" : ";") + additionalNote;
        }

        // Update the existing row with new request text, notes, and status.
        // Change 'REOPENED' to 'OPEN' if that's your intended status.
        String sql = "UPDATE cse360request SET request = ?, userName = ?, notes = ?, status = 'REOPENED' WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, updatedDescription);
            pstmt.setString(2, reopenedBy);
            pstmt.setString(3, combinedNotes);
            pstmt.setInt(4, oldRequestId);
            pstmt.executeUpdate();
        }
    }
	
	/**
 	* Adds a note to an existing request without changing its status.
 	*
 	* @param requestId the ID of the request to add a note to
 	* @param note the note to be added
 	* @throws SQLException if a database access error occurs
 	*/
    public void addNoteToRequest(int requestId, String note) throws SQLException {
        Request existing = getRequestById(requestId);
        String existingNotes = existing.getNotes() == null ? "" : existing.getNotes();
        if (!note.isEmpty()) {
            existingNotes += (existingNotes.isEmpty() ? "" : ";") + note;
        }
        String sql = "UPDATE cse360request SET notes=? WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, existingNotes);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        }
    }

	/**
 	* Retrieves a single request by its ID.
 	*
 	* @param requestId the ID of the request to retrieve
 	* @return the Request object with the given ID, or null if not found
 	* @throws SQLException if a database access error occurs
 	*/
    public Request getRequestById(int requestId) throws SQLException {
        String sql = "SELECT * FROM cse360request WHERE id=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return buildRequestFromResultSet(rs);
            }
        }
        return null;
    }

	
	
	////REVIEWER IDS

	/**
	 * Gets all of the reviewers that have been associated with the user. The user
	 * sets these in the GUI. 
	 * @param userId the id for the user requesting their Reviewer list
	 * @return Map of Users and Integers for the reviewers and their weights  
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public Map<User, Integer> getAllReviewersForUser(int userId) throws SQLException {
		String query = "SELECT reviewers FROM cse360users WHERE id = ?";            // selecting all of the rows in the database
		Map<User, Integer> reviewers = new HashMap<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				reviewers = getReviewersMap(rs.getString("reviewers"));
			}
		}
		return reviewers;
	}
	
	/**
	 * Function that will add a Reviewer ID to the User's list of Reviewers. 
	 * @param userId the id for the user adding a reviewer
	 * @param newReviewer The User object that represents the Reviewer to be added
	 * @param weight The selected weight value the User has given to the Reviewer
	 * @return boolean 
	 */
	public boolean addReviewer(int userId, User newReviewer, int weight)  {
		String query = "SELECT reviewers FROM cse360users WHERE id = ?";            // selecting all of the rows in the database
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {   // if the user is found
				Map<User, Integer> reviewers = getReviewersMap(rs.getString("reviewers"));
				//User newReviewer = getUser(reviewerId);
				
				reviewers.put(newReviewer, weight);
				
				updateReviewers(reviewers, userId);
			}
			return true;
		} catch (SQLException e) {
			System.out.println("ADDREVIEWER: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Function that will allow the User to update their list of reviewers completely.
	 * Will update the whole list of reviewers, you should get current list of 
	 * Reviewers first.   
	 * @param reviewers Map of the Reviewers to be updated and their weights
	 * @param userId the id for the user updating their Reviewer list
	 * @return boolean
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public boolean updateReviewers(Map<User, Integer> reviewers, int userId) throws SQLException {
		String query = "UPDATE cse360users SET reviewers = ? WHERE id = ?";            // selecting all of the rows in the database

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, putReviewerMapToString(reviewers));
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
			return true;
		}
	}
	
	/**
	 * Allows the User to update an individual Reviewer's weights in their
	 * list of Reviewers.   
	 * @param userId the id for the user updating their Reviewer list
	 * @param reviewer The Reviewer object that will be updated. 
	 * @param newWeight The new weight that will be assigned to the reviewer.
	 * @return boolean
	 */
	public boolean updateReviewerWeight(int userId, User reviewer, int newWeight)  {
		try {
			Map<User, Integer> reviewers = getAllReviewersForUser(userId);
			
			reviewers.put(reviewer, newWeight);
			
			updateReviewers(reviewers, userId);
			return true;
		} catch (SQLException e) {
			System.out.println("UPDATEREVIEWERWEIGHT: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Allows the User to remove an individual Reviewer from their Reviewer's list. 
	 * @param userId the id for the user requesting their Reviewer list
	 * @param reviewer the Reviewer that will be updated. 
	 * @return boolean
	 */
	public boolean removeReviewer(int userId, User reviewer) {
		String query = "SELECT reviewers FROM cse360users WHERE id = ?";            // selecting all of the rows in the database
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {   // if the user is found
				Map<User, Integer> reviewers = getReviewersMap(rs.getString("reviewers"));
				//User newReviewer = getUser(reviewerId);
				
				reviewers.remove(reviewer);
				
				updateReviewers(reviewers, userId);
			}
			return true;
		}
		catch (SQLException e) {
			System.out.println("REMOVEREVIEWER: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Allows a new User to be registered to the database. This adds a new row 
	 * to the User table.   
	 * @param user the User object that will be added.
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, name, password, email, roles, otp) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) { // added new values to set
																					// corresponding to tables
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getName());
			pstmt.setString(3, user.getPassword());
			pstmt.setString(4, user.getEmail());
			pstmt.setString(5, rolesSerial(user.getRoles()));
			pstmt.setBoolean(6, user.getOTPFlag());
			pstmt.executeUpdate();
		}
	}
	
	/**
	 * A method for creating a request to become a Reviewer. This request will then 
	 * show up to the Admin to be approved. 
	 * @param request a message detailing why the user should be a reviewer. 
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public void register(String request) throws SQLException {
		String insertRequest = "INSERT INTO cse360request (request, userName, requestTOF, requestATOF) VALUES (?, ?, ?, ?)";
		if (currentUser == null || currentUser.getUsername() == null) {
	        throw new IllegalStateException("Current user is not set.");
	    }
		try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)){
			pstmt.setString(1,  request);
			pstmt.setString(2,  currentUser.getUsername());
			pstmt.setBoolean(3, false);
			pstmt.setBoolean(4,  true);
			pstmt.executeUpdate();
		}
		catch (SQLIntegrityConstraintViolationException e) {
	        System.out.println("User already has a request registered.");
	    }
	}

	/**
	 * This allows a User to have their roles updated. An Admin has to request 
	 * new roles to be added or removed.   
	 * @param username the username of the User to be updated
	 * @param roles a comma separated string that contains the roles to be updated. 
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public void updateRoles(String username, String roles) throws SQLException {
		String insertUser = "UPDATE cse360users SET roles = ? WHERE username = ?"; // updating the roles for a user, add
																					// or remove

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, roles);
			pstmt.setString(2, username);
			pstmt.executeUpdate();
			this.currentUser.getRoles().add(roles);
		}
	}

	/**
	 * Will update the password that is stored in the database for this 
	 * user.  
	 * @param username the username for the password to be updated. 
	 * @param password the new password to be updated. 
	 */
	public void updatePassword(String username, String password) {
		String insertUser = "UPDATE cse360users SET password = ? WHERE username = ?"; // able to update password for OTP

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, password);
			pstmt.setString(2, username);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("COULD NOT UPDATE PASSWORD: " + e.getMessage());
		}
	}

	/**
	 * Allows the persistence of a One Time Password flag to be stored in 
	 * the database. Used when an admin has sent the user a One Time 
	 * Password.  
	 * @param username the username for the User that has requested the One Time Password.
	 * @param flag a boolean for the new One Time password flag 
	 */
	public void updateOTPFlag(String username, boolean flag) {
		String insertUser = "UPDATE cse360users SET otp = ? WHERE username = ?"; // able to update if the user is using
																					// a OTP

		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setBoolean(1, flag);
			pstmt.setString(2, username);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("COULD NOT UPDATE OTP: " + e.getMessage());
		}
	}

	/**
	 * Returns the User object that is associated with the username. 
	 * @param username the username for the User to be retrieved. 
	 * @return User
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public User getUser(String username) throws SQLException {
		String query = "SELECT * FROM cse360users AS c WHERE c.username = ?	"; // getting all of the fields of a user
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				if (name == null || name.isEmpty()) {
					name = "User";
				}
				String password = rs.getString("password");
				String email = rs.getString("email");
				List<String> roles = rolesDeserial(rs.getString("roles"));
				boolean otp = rs.getBoolean("otp");
				return new User(id, username, name, password, email, roles, otp);
			}
		}
		return null;
	}

	/**
	 * Returns the User object that is associated with the ID. 
	 * @param id the id for the User requested. 
	 * @return User  
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public User getUser(int id) throws SQLException {
		String query = "SELECT * FROM cse360users AS c WHERE c.id = ?	"; // getting all of the fields of a user
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				String username = rs.getString("userName");
				String name = rs.getString("name");
				if (name == null || name.isEmpty()) {
					name = "User";
				}
				String password = rs.getString("password");
				String email = rs.getString("email");
				List<String> roles = rolesDeserial(rs.getString("roles"));
				boolean otp = rs.getBoolean("otp");
				return new User(id, username, name, password, email, roles, otp);
			}
		}
		return null;
	}

	/**
	 * Allows a role to be added to a User. Calls the updateRoles function above. 
	 * @param username the username of the user adding a role to 
	 * @param newRole the new role to be added 
	 * @return boolean for if the role got added   
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public boolean addRoles(String username, String newRole) throws SQLException { // able to add roles based on
																					// username
		String query = "SELECT * FROM cse360users AS c WHERE c.username = ?	";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) { // if the user is found
				List<String> roles = rolesDeserial(rs.getString("roles")); // deserialize string into list of roles
				if (!roles.contains(newRole)) { // make sure to not duplicate roles
					roles.add(newRole); // add new roles

					String rolesString = rolesSerial(roles); // serialize list of roles into a string
					try {
						updateRoles(username, rolesString); // calling updateRoles for reusability
						return true;
					} catch (SQLException e) {
						System.out.println(e.getMessage() + "\n" + "ERROR IN ADDROLES/REGISTER");
						return false;
					}
				} else {
					System.out.println("ADDROLES: User already has this role");
					return false;
				}
			}
			System.out.println("ADDROLES: User was not found");
			return false;
		}
	}

	/**
 	* Bans a user by setting their 'banned' status to TRUE in the database.
 	*
 	* @param username the username of the user to ban
 	* @return true if the user was successfully banned; false otherwise
 	*/
	public boolean banUser(String username) {
	    String sql = "UPDATE cse360users SET banned = TRUE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;  // Return true if at least one row was affected
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
 	* Unbans a user by setting their 'banned' status to FALSE in the database.
 	*
 	* @param username the username of the user to unban
 	* @return true if the user was successfully unbanned; false otherwise
 	*/
	public boolean unbanUser(String username) {
	    String sql = "UPDATE cse360users SET banned = FALSE WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;  // Return true if at least one row was affected
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	/**
 	* Checks if a user is currently banned.
 	*
 	* @param username the username to check
 	* @return true if the user is banned; false otherwise or if an error occurs
 	*/
	public boolean isUserBanned(String username) {
	    String sql = "SELECT banned FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setString(1, username);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getBoolean("banned");  // Return true if banned, false otherwise
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;  // Default to not banned if an error occurs
	}

	/**
	 * This function will remove a role form the user who's username is passed in. 
	 * This will call the updateRoles function from above. 
	 * @param username the username of the user that will have roles removed. 
	 * @param newRole a string containing the role that will be removed.  
	 * @return boolean
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public boolean removeRoles(String username, String newRole) throws SQLException {
		String query = "SELECT * FROM cse360users AS c WHERE c.username = ?	";

		if (!newRole.equalsIgnoreCase("admin") && currentUser.getRoles().size() > 1) { // make sure that you are not  
																						// deleting the only admin's
																						// roles

			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, username);
				ResultSet rs = pstmt.executeQuery();

				if (rs.next()) {
					List<String> roles = rolesDeserial(rs.getString("roles")); // get list of deserialized roles

					if (roles.contains(newRole)) { // make sure that user has this role
						roles.remove(roles.indexOf(newRole)); // possible to delete last role

						String rolesString = rolesSerial(roles); // serialize roles into a string
						try {
							updateRoles(username, rolesString); // reusing updateRoles method
							return true;
						} catch (SQLException e) {
							System.out.println(e.getMessage() + "\n" + "ERROR IN REMOVEROLES/REGISTER");
							return false;
						}
					} else {
						System.out.println("REMOVEROLES: User does not have this role");
						return false;
					}
				} else {
					System.out.println("REMOVEROLES: User was not found");
				}
			}
		}
		System.out.println("REMOVEROLES: You cannot remove your own roles");
		return false;
	}

	/**
	 * This function will delete a request to be a reviewer from the 
	 * current user that is logged in. Can be used if the user no longer 
	 * wants to be a reviewer after submitting their request.   
	 * @param username the id for the user requesting their Reviewer list
	 * @return boolean
	 */
	public boolean deleteRequest(String username) {
		String query = "DELETE FROM cse360request as c WHERE c.username = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);

			if (pstmt.executeUpdate() > 0) {
				System.out.println("DELETEREQUEST: REQUEST successfully deleted");
				return true;
			}
			System.out.println("DELETEREQUEST: REQUEST was not found");
			return false;
		} catch (SQLException e) {
			System.err.println("DELETEREQUEST: SQL Error - " + e.getMessage());
			return false;
		}
}


	/**
	 * This function will allow a user to be deleted from the database and 
	 * no longer be able to log in. You are not able to delete your own account 
	 * if you are logged in.   
	 * @param username the username of the user that will have their account deleted. 
	 * @return boolean
	 */
	public boolean deleteUser(String username) {
		if (!username.equals(currentUser.getUsername())) { // make sure the the correct user is getting deleted

			String query = "DELETE FROM cse360users AS c WHERE c.username = ?"; // delete the correct user row from
																				// database
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setString(1, username);

				if (pstmt.executeUpdate() > 0) {
					System.out.println("DELETEUSER: User successfully deleted");
					return true;
				}
				System.out.println("DELETEUSER: User was not found");
				return false;
			} catch (SQLException e) {
				System.err.println("DELETEUSER: SQL Error - " + e.getMessage());
				return false;
			}
		}
		System.out.println("DELETEUSER: You cannot delete yourself");
		return false;
	}

	/**
	 * This function will retrieve all of the users that are currently in 
	 * the database. Will create User objects from the rows that are returned.  
	 * @return a List of Users that are in the database. 
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public List<User> getAllUsers() throws SQLException {
		String query = "SELECT * FROM cse360users"; // selecting all of the rows in the database
		List<User> users = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username"); // for each row, get all of the user attributes
				String name = rs.getString("name");
				if (name == null || name.isEmpty()) {
					name = "User";
				}
				String password = rs.getString("password");
				String email = rs.getString("email");
				List<String> roles = rolesDeserial(rs.getString("roles"));
				boolean otp = rs.getBoolean("otp");
				User user = new User(id, username, name, password, email, roles, otp); // create new user with all of
																						// the attributes
				users.add(user); // add new user to the list of users
			}
		}
		return users;
	}

	/**
	 * Method to retrieve all requests from the database
	 * 
	 * @return					A List of Request objects representing all requests in the database
	 * @throws SQLException		SQL exception throw
	 */
	public List<Request> getAllRequestsA() throws SQLException {
		String query = "SELECT userName, request, requestTOF, requestATOF FROM cse360request";
		List<Request> requests = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String userName = rs.getString("userName");
	            String requestText = rs.getString("request");
	            boolean requestTOF = rs.getBoolean("requestTOF");
	            boolean requestATOF = rs.getBoolean("requestATOF");
	            User user = getUser(userName);
	            
	            requests.add(new Request(requestText, user, requestTOF, requestATOF));
			}
		}
		return requests;
	}

	/**
	 * Gets all of the requests for reviewers that Users have submitted.
	 * Used to display to an Admin in order for them to approve/deny the request. 
	 * @return a List of Requests that are in the database. 
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	public List<Request> getAllRequests() throws SQLException {
	    String query = "SELECT id, userName, request, requestTOF, requestATOF, notes, status, originalId FROM cse360request";
	    List<Request> requests = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int id = rs.getInt("id");
	            String userName = rs.getString("userName");
	            String requestText = rs.getString("request");
	            boolean requestTOF = rs.getBoolean("requestTOF");
	            boolean requestATOF = rs.getBoolean("requestATOF");
	            String notes = rs.getString("notes");
	            String status = rs.getString("status");
	            int originalId = rs.getInt("originalId");

	            User user = getUser(userName);
	            requests.add(new Request(id, requestText, user, requestTOF, requestATOF, notes, status, originalId));
	        }
	    }

	    return requests;
	}
	/**
 	* Retrieves all reviewer requests from the database.
 	* <p>
 	* This method executes a SQL query to select all rows from the
 	* {@code cse360request} table where the column {@code requestATOF} is true,
 	* indicating that the request is intended for reviewer operations.
 	* Each retrieved row is converted into a {@code Request} object using the
 	* {@link #buildRequestFromResultSet(ResultSet)} method.
 	* </p>
 	*
 	* @return a list of {@code Request} objects representing all reviewer requests.
 	* @throws SQLException if a database access error occurs or the SQL query fails.
 	*/
	public List<Request> getAllReviewerRequests() throws SQLException {
	    String sql = "SELECT * FROM cse360request WHERE requestATOF = true";
	    List<Request> reviewerRequests = new ArrayList<>();
	    try (PreparedStatement pstmt = connection.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {
	        while (rs.next()) {
	            Request r = buildRequestFromResultSet(rs);
	            reviewerRequests.add(r);
	        }
	    }
	    return reviewerRequests;
	}
	
	/**
	 * This method updates the request by the user, typically only for making it visible to the 
	 * admin, and removing it from the Instructors view
	 * @param userName the username for the request being updated
	 * @param requestTOF a boolean to tell if the request is still active. 
	 * @param requestATOF a boolean to tell if the request has been accepted. 
	 * @throws SQLException if there is an error in accessing the column.
	 */
	public void updateRequestStatus(String userName, boolean requestTOF, boolean requestATOF) throws SQLException {
	    String query = "UPDATE cse360request SET requestTOF = ?, requestATOF = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, requestTOF);
	        pstmt.setBoolean(2, requestATOF);
	        pstmt.setString(3, userName);
	        pstmt.executeUpdate();
	    }
	}
	
	/**
	 * This method gets all the requests that the admin should see.
	 * @return a List of Requests that are in the database. 
	 * @throws SQLException if there is an error in accessing the column.
	 */
	public List<Request> getAllRequestsForAdmin() throws SQLException {
		String query = "SELECT userName, request, requestTOF, requestATOF FROM cse360request WHERE requestATOF = TRUE";
		List<Request> requests = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String userName = rs.getString("userName");
	            String requestText = rs.getString("request");
	            boolean requestTOF = rs.getBoolean("requestTOF");
	            boolean requestATOF = rs.getBoolean("requestATOF");
	            User user = getUser(userName);
	            
	            requests.add(new Request(requestText, user, requestTOF, requestATOF));
			}
		}
		return requests;
	}

	/**
	 * This method returns all of the Users in the database that 
	 * have a specific role assigned to them. 
	 * @param role the role that the query will return Users for. 
	 * @return a List of Users in the database that have this role.  
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	// Retrieves all users with a specified role
	public List<User> getAllUsersWithRole(String role) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE roles LIKE ?";
		List<User> users = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, "%" + role + "%");

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String username = rs.getString("username"); // for each row, get all of the user attributes
				String name = rs.getString("name");
				if (name == null || name.isEmpty()) {
					name = "User";
				}
				String password = rs.getString("password");
				String email = rs.getString("email");
				List<String> roles = rolesDeserial(rs.getString("roles"));
				boolean otp = rs.getBoolean("otp");

				if (roles.contains(role)) {
					User user = new User(id, username, name, password, email, roles, otp); // create new user

					users.add(user); // add new user to the list of users
				}
			}
		}
		return users;
	}

	/**
	 * This method allows a User to login if the username and password
	 * match what is in the database.  
	 * @param username the username that the user is logging in with
	 * @param password the password that the user is logging in with
	 * @return User  
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	// Validates a user's login credentials.
	public User login(String username, String password) throws SQLException {
	    String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND password <> ''";
	    if (connection == null) {
	        connectToDatabase();
	    }

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, password);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                boolean isBanned = rs.getBoolean("banned");

	                if (isBanned) {
	                    System.out.println("User is banned and cannot log in.");
	                    return null;  // Prevent login for banned users
	                }

	                currentUser = getUser(username);
	                boolean otp = rs.getBoolean("otp");
	                String storedPW = rs.getString("password");

	                if (storedPW.isEmpty()) {
	                    return null;
	                }
	                if (otp) {
	                    // Reset password after OTP login
	                    String updateQuery = "UPDATE cse360users SET password = '' WHERE userName = ?";
	                    try (PreparedStatement updatepstmt = connection.prepareStatement(updateQuery)) {
	                        updatepstmt.setString(1, username);
	                        updatepstmt.executeUpdate();
	                    }
	                }
	                return currentUser;
	            }
	            return null;
	        }
	    }
	}

	/**
	 * This method checks if the User with the given username 
	 * is in the database.  
	 * @param username the username being checked if it exists. 
	 * @return boolean 
	 */
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String username) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?"; // make sure that user exists
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				// If the count is greater than 0, the user exists
				return rs.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // If an error occurs, assume user doesn't exist
	}

	/**
	 * This method returns all of the roles that the User with
	 * the given username has. 
	 * @param username the username to get the roles from. 
	 * @return a List of Strings that represent the roles the user has.   
	 */
	// Retrieves the role of a user from the database using their UserName.
	public List<String> getUserRole(String username) {
		String query = "SELECT roles FROM cse360users WHERE userName = ?"; // getting all of the roles for a user
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rolesDeserial(rs.getString("roles")); // Return the role if user exists, deserializing into a
																// list
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null; // If no user exists or an error occurs
	}

	/**
	 * Generates a Invitation Code that will be used to allow 
	 * a new User to register their login. 
	 * @return String 
	 */
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
		String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
		String query = "INSERT INTO InvitationCodes (code, generatedDate) VALUES (?, CURRENT_TIMESTAMP)"; // added
																											// timeStamp
																											// for
																											// invalidation
		System.out.println(code);
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * Generates a random One Time Password that the user can user to login
	 * and reset their password.  
	 * @return String 
	 */
	public String generateOneTimePassword() {
		String special = "~`!@#$%^&*()_-+{}[]|:,.?/"; // same list of special characters in PasswordEvaluator
		String lower = "abcdefghijklmnopqrstuvwxyz"; // alphabet in lowercase
		String upper = lower.toUpperCase(); // using lowercase alphabet to uppercase
		String OTP = "";
		Random random = new Random();
		int rand = 0;

		for (int i = 0; i < 6; i++) { // first 6 characters will be only chars
			rand = random.nextInt(26); // generating a random number from 0-25 (random is exclusive)

			if (i % 2 == 0) { // for each even character, character will be lowercase
				OTP = OTP + lower.charAt(rand);
			} else { // for each odd character,character will be uppercase
				OTP = OTP + upper.charAt(rand);
			}
		}

		rand = random.nextInt(9); // reset random to only be 0-9
		OTP = OTP + rand; // add random number to OTP
		rand = random.nextInt(special.length()); // reset random to special char length (random exclusive)
		OTP = OTP + special.charAt(rand); // add random special character to OTP
		return OTP;
	}

	/**
	 *   Validates the previously generated Invitation code that 
	 *   exists in the database. 
	 * @param code the invitation code being used 
	 * @return boolean
	 */
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE AND generatedDate >= DATEADD('MINUTE', -15, CURRENT_TIMESTAMP)";
		// If expiration date is changed, make sure to update expiration label in
		// InvitationPage with new time limit
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Mark the code as used
				markInvitationCodeAsUsed(code);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * This marks the previously used Invitation code so that it 
	 * will not be able to be used again. 
	 * @param code The invitation code that has been used. 
	 * @throws SQLException if there is an error in accessing the column. 
	 */
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
		String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?"; // update Invitation Code to true when
																					// used
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, code);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This helper method decodes the comma separated string in the 
	 * database and turns it into a List of String for easier use.  
	 * @param roles the comma separated string being decoded. 
	 * @return List<String> 
	 */
	private List<String> rolesDeserial(String roles) { // Deserializing from String to List<Roles> for easier logic
		if (roles == null || roles == "") {
			return new ArrayList<>(); // if roles is empty or null, return empty list, was returning 1 comma before
										// this
		} else {
			return new ArrayList<>(Arrays.asList(roles.split(","))); // return mutable list that is split on commmas
																		// from database
		}
	}

	/**
	 *  This helper method is used to encode a List of roles into a 
	 *  comma separated string to be stored by the database.
	 * @param roles the List of roles that will be encoded. 
	 * @return String
	 */
	private String rolesSerial(List<String> roles) { // serializing a List<Roles> into a string to be stored in database
		if (roles == null || roles.isEmpty()) { // make sure that the list is not empty or null
			return "";
		} else {
			return String.join(",", roles); // joining each Role in the list to be comma separated string
		}
	}

	/**
	 * This helper method is used to encode a Map of User's and Integers
	 * into a string with custom delimiters to be stored by the database.
	 * @param map the map of User's and their Integer weights. 
	 * @return String   
	 */
	private String putReviewerMapToString(Map<User, Integer> map) {
		String mapToString = "";
		if (map.isEmpty()) {
			return "";
			
		}
		else {
			for (Map.Entry<User, Integer> m: map.entrySet()) {
				mapToString = mapToString + "! ";
				
				mapToString = mapToString + m.getKey().getUsername();
				
				mapToString = mapToString + " " + m.getValue() + " ";
				
			}
		}
		return mapToString;
	}
	

	/**
	 * This helper method is used to decode a delimited String 
	 * into a Map of Users and Integers. 
	 * @param json the delimited string to be decoded. 
	 * @return Map<User, Integer>  
	 */
	private Map<User, Integer> getReviewersMap(String json) {   /// what it will be stored as ! userId weight ! userId weight 
		Map<User, Integer> ret = new HashMap<>();
		if (json == null || json.isEmpty()) {        // make sure that the list is not empty or null
			return new HashMap<>();
		} else {  
			
			String [] split = json.split("!");
			for (String s : split) {
				if (s.trim().isEmpty()) continue;
				
				String [] split2 = s.split(" ");
			
				try {
					User user = getUser(split2[1]);
					ret.put(user, Integer.parseInt(split2[2]));	
				}
				
				catch (SQLException e) {
					System.out.println("GETREVIEWERSMAP: COULD NOT GET USER" + e.getMessage());
				}
			}
			
			
			}
			return ret;
		}
	

	/**
	 * Sets the user logged in to have the most current role 
	 * for this class to stay updated. 
	 * @param role the role to be set as the current role 
	 */
	public void setUserCurrentRole(String role) { // public setter for when user picks their role
		currentUser.setCurrentRole(role);
	}

	/**
	 * Closes the connection to the database. . 
	 */
	// Closes the database connection and statement.
	public void closeConnection() {
		try {
			if (statement != null)
				statement.close();
		} catch (SQLException se2) {
			se2.printStackTrace();
		}
		try {
			if (connection != null)
				connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

//	/*******
//	 * <p> Title: QAHelper Class. </p>
//	 * 
//	 * <p> Description: A Database Helper class that holds all of our methods taking data in and out of the database for questions and answers. </p>
//	 * 
//	 * <p> Copyright: CSE360 Team 8 @ 2025 </p>
//	 * 
//	 * @author CSE360 Team 8
//	 * 
//	 * @version 1.00	2025-03-24 Implementing the QAHelper methods
//	 * 
//	 */
//	public class QAHelper {
//
//		// Initialize connection to database
//		/**
//		 * 
//		 * Initializes the connection to the database
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public void connectToDatabase() throws SQLException {
//			qaHelper.connectToDatabase();
//		}
//
//		/*
//		 * // Create the tables that will be used to store the info // I don't believe
//		 * we need this part private void createTables() throws SQLException {
//		 * qaHelper.createTables(); }
//		 */
//		// Check if the database is empty
//		/**
//		 * Check if the database is empty - Only checks the question database at the
//		 * moment.
//		 * 
//		 * @return 						A boolean indicating whether the function was successfully performed
//		 * 
//		 * @throws SQLException 		In case the database throws an error
//		 * 
//		 */
//		public boolean isDatabaseEmpty() throws SQLException {
//			return qaHelper.isDatabaseEmpty();
//		}
//
//		// Registers a new user in the database.
//		/**
//		 * Registers a new question in the database.
//		 * 
//		 * @param question 			The question object you wish to register in the database
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public void registerQuestion(Question question) throws SQLException {
//			qaHelper.registerQuestion(question);
//		}
//
//		// Registers a new question in the database.
//		/**
//		 * Registers a new answer in the database.
//		 * 
//		 * @param answer		 The answer object you wish to relate to a question
//		 * @param relatedID 	 The id of the question you wish to relate the passed answer object to
//		 * 
//		 * @throws SQLException  In case the database throws an error
//		 * 
//		 */
//		public void registerAnswerWithQuestion(Answer answer, int questionID) throws SQLException {
//			qaHelper.registerAnswerWithQuestion(answer, questionID);
//		}
//
//		// Registers a new user in the database.
//		/**
//		 * Registers a new answer in the database.
//		 * 
//		 * @param answer 		The answer object you wish to register
//		 * @param relatedID 	The answer id you wish to relate to the passed answer object
//		 * 
//		 * @throws SQLException In case the database throws an error
//		 * 
//		 */
//		public void registerAnswerWithAnswer(Answer answer, int relatedID) throws SQLException {
//			qaHelper.registerAnswerWithAnswer(answer, relatedID);
//		}
//
//		// Deletes a question row from the SQL table
//		/**
//		 * Deletes a question row from the SQL table
//		 * 
//		 * @param id 	The id of the question you wish to delete
//		 * 
//		 * @return 		A boolean indicating whether the function was successfully performed
//		 * 
//		 */
//		public boolean deleteQuestion(int id) {
//			return qaHelper.deleteQuestion(id);
//		}
//
//		// Deletes a question row from the SQL table
//		/**
//		 * Deletes a question row from the SQL table
//		 * 
//		 * @param id 		The id of the answer you wish to delete
//		 * 
//		 * @return 			A boolean indicating whether the function was successfully performed
//		 * 
//		 */
//		public boolean deleteAnswer(int id) {
//			return qaHelper.deleteAnswer(id);
//		}
//
//		// Add a relation to the question database
//		/**
//		 * Add a relation to the question database
//		 * 
//		 * @param questionID 	The id of the question you want to relate to
//		 * @param answerID 		The id of the answer you wish to relate to a question
//		 * 
//		 */
//		public void addRelationToQuestion(int questionID, int answerID) {
//			qaHelper.addRelationToQuestion(questionID, answerID);
//		}
//
//		// Add a relation to the answer database
//		/**
//		 * Add a relation to the answer database
//		 * 
//		 * @param answerID 		The id of the answer you wish to relate to
//		 * @param relatedID 	The id of the answer you wish to relate
//		 * 
//		 */
//		public void addRelationToAnswer(int questionID, int answerID) {
//			qaHelper.addRelationToAnswer(questionID, answerID);
//		}
//
//		// Delete a relation from the relation database
//		/**
//		 * Delete a relation from the relation database
//		 * 
//		 * @param questionID 	The id of the question you wish to delete a relation from
//		 * @param answerID 		The id of the answer you wish to remove the relation of
//		 * 
//		 * @return 				A boolean indicating whether the function was successfully performed
//		 * 
//		 */
//		public boolean deleteRelation(int questionID, int answerID) {
//			return qaHelper.deleteRelation(questionID, answerID);
//		}
//
//		// Get a question object with a provided question id
//		/**
//		 * Get a question object with a provided question id
//		 * 
//		 * @param questionID 			The id of the question you wish to search for
//		 * 
//		 * @return 						A question object representing the question you were searching for
//		 * 
//		 * @throws SQLException 		In case the database throws an error
//		 * 
//		 */
//		public Question getQuestion(Integer questionID) throws SQLException {
//			return qaHelper.getQuestion(questionID);
//		}
//
//		// Get a question object with a provided question title
//		/**
//		 * Get a question object with a provided question title
//		 * 
//		 * @param questionTitle 		The title of the question you are searching for
//		 * 
//		 * @return 						A question object representing the question you were searching for
//		 * 
//		 * @throws SQLException 		In case the database throws an error
//		 * 
//		 */
//		public Question getQuestion(String questionTitle) throws SQLException {
//			return qaHelper.getQuestion(questionTitle);
//		}
//
//		// Get an answer object with a provided answer id
//		/**
//		 * Get an answer object with a provided answer id
//		 * 
//		 * @param answerID 			The id of the answer you are searching for
//		 * 
//		 * @return 					An answer object representing the answer you were searching for
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public Answer getAnswer(Integer answerID) throws SQLException {
//			return qaHelper.getAnswer(answerID);
//		}
//
//		// Retrieve all questions from the question database
//		
//		/**
//		 * Retrieve all questions from the question database
//		 * 
//		 * @return 					A List of question objects representing the entire question database
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Question> getAllQuestions() throws SQLException {
//			return qaHelper.getAllQuestions();
//		}
//
//		// Retrieve all questions from question database that don't have answers
//
//		/**
//		 * Returns a list of all questions that have no potential answers
//		 * 
//		 * @return 					A List of question objects representing all unanswered questions
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Question> getAllUnansweredQuestions() throws SQLException {
//			return qaHelper.getAllUnansweredQuestions();
//		}
//
//		// Retrieve all questions from question database that have answers
//		/**
//		 * Retrieve all questions that have potential answers
//		 * 
//		 * @return 					A List of question objects representing all answered questions
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Question> getAllAnsweredQuestions() throws SQLException {
//			return qaHelper.getAllAnsweredQuestions();
//		}
//
//		// Retrieve all answers from the answer database
//		/**
//		 * Retrieve all answers from the answer database
//		 * 
//		 * @return 					A List of answer objects representing all answers in the answer database
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Answer> getAllAnswers() throws SQLException {
//			return qaHelper.getAllAnswers();
//		}
//
//		// Retrieve all of the answers that are associated with a given question id from
//		// the answer database
//		/**
//		 * Retrieve all of the answers that are associated with a given question id from
//		 * the question database.
//		 * 
//		 * @param questionID 		The id of the question you are working with
//		 * 
//		 * @return 					A List of answer objects representing all answers for the passed question
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Answer> getAllAnswersForQuestion(int questionID) throws SQLException {
//			return qaHelper.getAllAnswersForQuestion(questionID);
//		}
//
//		// Retrieve all of the answers that are associated with a given answer id from
//		// the answer database
//		/**
//		 * Retrieve all of the answers that are associated with a given answer id from
//		 * the answer database
//		 * 
//		 * @param answerID 			The id of the answer you are working with
//		 * 
//		 * @return 					A List of answer objects representing all answers related to the passed answer
//		 * 
//		 * @throws SQLException 	In case the database throws an error
//		 * 
//		 */
//		public List<Answer> getAllAnswersForAnswer(int answerID) throws SQLException {
//			return qaHelper.getAllAnswersForAnswer(answerID);
//		}
//
//		// Method used to convert the text of a question into a list of unique words
//		// without special characters for comparison to others
//		/**
//		 * Method used to convert the text of a question into a list of unique words
//		 * without special characters for comparison to others
//		 * 
//		 * @param text 				The text you wish to deserialize
//		 * 
//		 * @return 					The deserialized text
//		 * 
//		 */
//		public List<String> textDeserial(String text) {
//			return qaHelper.textDeserial(text);
//		}
//
//		// Update the contents of a question object with those of pass question object
//		/**
//		 * Update the contents of a question object with those of the passed question
//		 * object
//		 * 
//		 * @param question 			The question object you are working with
//		 * 
//		 */
//		public void updateQuestion(Question question) {
//			qaHelper.updateQuestion(question);
//		}
//
//		// Update the contents of a answer object with those of pass answer object
//		/**
//		 * Update the contents of a answer object with those of pass answer object
//		 * 
//		 * @param answer 			The answer object you are working with
//		 * 
//		 */
//		public void updateAnswer(Answer answer) {
//			qaHelper.updateAnswer(answer);
//		}
//	}
}
