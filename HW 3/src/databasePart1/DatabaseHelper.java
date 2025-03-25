package databasePart1;

import java.sql.Connection;
import java.sql.Statement;
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

/**
 * The DatabaseHelper class is responsible for managing the connection to the
 * database, performing operations such as user registration, login validation,
 * and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";

	// Database credentials
	static final String USER = "sa";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;

	public User currentUser;

	// Create QAHelper object
	public final QAHelper1 qaHelper;

	// Initialize new QAHelper object2
	public DatabaseHelper() {
		qaHelper = new QAHelper1(this);
	}

	public void connectToDatabase() throws SQLException {
		try {
			qaHelper.connectToDatabase();

			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to User database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);

			statement = connection.createStatement();
			/*------------------------------------------------------------------------------------------------*/
			/* You can use this command to clear the databases and restart from fresh. */

			boolean resetUserDatabase = true; // Set to true if you want to reset the User Database
			boolean resetQADatabase = true; // Set to true if you want to reset the QA Database

			int a = 1; // Set this to 1 if you wish to populate User Database(0 or 1)
			int b = 1; // Set this to the number of times you want to populate the QA
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

	// Create the tables for the User Database
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users (" + "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, " + "name VARCHAR(255), " + "password VARCHAR(255), " // added in
																										// username,
																										// name,
																										// password,
																										// email,
																										// OTPFlag
				+ "email VARCHAR(255), " + "roles VARCHAR(70), " + "reviewers VARCHAR(100), " + "otp BOOLEAN DEFAULT FALSE)"; // added in roles as a
																									// comma separated
																									// string
		statement.execute(userTable);
		
		// Create the table for the reviewer request
		String requestReviewerTable = "CREATE TABLE IF NOT EXISTS cse360request (" + "request VARCHAR(500), " 
				+ "userName VARCHAR(255) UNIQUE, " + "requestTOF BOOLEAN DEFAULT FALSE)";
		statement.execute(requestReviewerTable);

		// Create the invitation codes table
		String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes (" + "code VARCHAR(10) PRIMARY KEY, "
				+ "isUsed BOOLEAN DEFAULT FALSE," + "generatedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"; // new date
																											// variable
																											// for code
																											// timeout
		statement.execute(invitationCodesTable);

	}

	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	
	
	////REVIEWER IDS

	
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
	
	public boolean updateReviewers(Map<User, Integer> reviewers, int userId) throws SQLException {
		String query = "UPDATE cse360users SET reviewers = ? WHERE id = ?";            // selecting all of the rows in the database

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, putReviewerMapToString(reviewers));
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
			return true;
		}
	}
	
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
	
	public void register(String request) throws SQLException {
		String insertRequest = "INSERT INTO cse360request (request, userName, requestTOF) VALUES (?, ?, ?)";
		if (currentUser == null || currentUser.getUsername() == null) {
	        throw new IllegalStateException("Current user is not set.");
	    }
		try (PreparedStatement pstmt = connection.prepareStatement(insertRequest)){
			pstmt.setString(1,  request);
			pstmt.setString(2,  currentUser.getUsername());
			pstmt.setBoolean(3, true);
			pstmt.executeUpdate();
		}
		catch (SQLIntegrityConstraintViolationException e) {
	        System.out.println("User already has a request registered.");
	    }
	}


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
	
	public List<Request> getAllRequests() throws SQLException {
		String query = "SELECT userName, request, requestTOF FROM cse360request";
		List<Request> requests = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String userName = rs.getString("userName");
	            String requestText = rs.getString("request");
	            boolean requestTOF = rs.getBoolean("requestTOF");
	            
	            User user = getUser(userName);
	            
	            requests.add(new Request(requestText, user, requestTOF));
			}
		}
		return requests;
	}


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

	// Validates a user's login credentials.
	public User login(String username, String password) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND password <> ''";
		if (connection == null) { // connection has been becoming null for some reason
			connectToDatabase();
			System.out.println("CONNECTIONCONNECTIONCONNECTION: " + connection.toString()); // debug
		}

		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, username);
			pstmt.setString(2, password);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					// Set currentUser since successful login at this point
					currentUser = getUser(username);

					boolean otp = rs.getBoolean("otp");
					String storedPW = rs.getString("password");

					// If password is blank, login fails - password is set to blank after logging in
					// with a one-time password
					if (storedPW.isEmpty()) {
						System.out.println("Password is empty."); // Debug
						return null;
					}
					if (otp) {
						// Reset password to "" or blank
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

	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
		String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?"; // make sure that user exists
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {

			pstmt.setString(1, userName);
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

	// Retrieves the role of a user from the database using their UserName.
	public List<String> getUserRole(String userName) {
		String query = "SELECT roles FROM cse360users WHERE userName = ?"; // getting all of the roles for a user
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, userName);
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

	private List<String> rolesDeserial(String roles) { // Deserializing from String to List<Roles> for easier logic
		if (roles == null || roles == "") {
			return new ArrayList<>(); // if roles is empty or null, return empty list, was returning 1 comma before
										// this
		} else {
			return new ArrayList<>(Arrays.asList(roles.split(","))); // return mutable list that is split on commmas
																		// from database
		}
	}

	private String rolesSerial(List<String> roles) { // serializing a List<Roles> into a string to be stored in database
		if (roles == null || roles.isEmpty()) { // make sure that the list is not empty or null
			return "";
		} else {
			return String.join(",", roles); // joining each Role in the list to be comma separated string
		}
	}
	
	private List<Integer> reviewerDeserial(String reviewers) {  // Deserializing from String to List<Roles> for easier logic
		if (reviewers == null || reviewers == "") {
			return new ArrayList<>();                   // if roles is empty or null, return empty list, was returning 1 comma before this
		} else {
			List<Integer> reviewerIds = Arrays.asList(reviewers.split(",")).stream().map(r -> Integer.parseInt(r)).collect(Collectors.toList());
			return new ArrayList<>(reviewerIds);
		}
	}

	private String reviewerSerial(List<Integer> reviewers) {   // serializing a List<Roles> into a string to be stored in database
		if (reviewers == null || reviewers.isEmpty()) {        // make sure that the list is not empty or null
			return "";
		} else {  
			String serial = "";
			for (Integer i : reviewers) {
				serial = serial + i.toString() + ",";
			}
			return serial;
		}
	}
	
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
	

	public void setUserCurrentRole(String role) { // public setter for when user picks their role
		currentUser.setCurrentRole(role);
	}

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

	//
	public class QAHelper {

		// Initialize connection to database
		public void connectToDatabase() throws SQLException {
			qaHelper.connectToDatabase();
		}

		/*
		 * // Create the tables that will be used to store the info // I don't believe
		 * we need this part private void createTables() throws SQLException {
		 * qaHelper.createTables(); }
		 */

		// Check if the database is empty
		public boolean isDatabaseEmpty() throws SQLException {
			return qaHelper.isDatabaseEmpty();
		}

		// Registers a new user in the database.
		public void registerQuestion(Question question) throws SQLException {
			qaHelper.registerQuestion(question);
		}

		// Registers a new question in the database.
		public void registerAnswerWithQuestion(Answer answer, int questionID) throws SQLException {
			qaHelper.registerAnswerWithQuestion(answer, questionID);
		}

		// Registers a new user in the database.
		public void registerAnswerWithAnswer(Answer answer, int relatedID) throws SQLException {
			qaHelper.registerAnswerWithAnswer(answer, relatedID);
		}

		// Deletes a question row from the SQL table
		public boolean deleteQuestion(int id) {
			return qaHelper.deleteQuestion(id);
		}

		// Deletes a question row from the SQL table
		public boolean deleteAnswer(int id) {
			return qaHelper.deleteAnswer(id);
		}

		// Add a relation to the question database
		public void addRelationToQuestion(int questionID, int answerID) {
			qaHelper.addRelationToQuestion(questionID, answerID);
		}

		// Add a relation to the answer database
		public void addRelationToAnswer(int questionID, int answerID) {
			qaHelper.addRelationToAnswer(questionID, answerID);
		}

		// Delete a relation from the relation database
		public boolean deleteRelation(int questionID, int answerID) {
			return qaHelper.deleteRelation(questionID, answerID);
		}

		// Get a question object with a provided question id
		public Question getQuestion(Integer questionID) throws SQLException {
			return qaHelper.getQuestion(questionID);
		}

		// Get a question object with a provided question title
		public Question getQuestion(String questionTitle) throws SQLException {
			return qaHelper.getQuestion(questionTitle);
		}

		// Get an answer object with a provided answer id
		public Answer getAnswer(Integer answerID) throws SQLException {
			return qaHelper.getAnswer(answerID);
		}

		// Retrieve all questions from the question database
		public List<Question> getAllQuestions() throws SQLException {
			return qaHelper.getAllQuestions();
		}

		// Retrieve all questions from question database that don't have answers
		public List<Question> getAllUnansweredQuestions() throws SQLException {
			return qaHelper.getAllUnansweredQuestions();
		}

		// Retrieve all questions from question database that have answers
		public List<Question> getAllAnsweredQuestions() throws SQLException {
			return qaHelper.getAllAnsweredQuestions();
		}

		// Retrieve all answers from the answer database
		public List<Answer> getAllAnswers() throws SQLException {
			return qaHelper.getAllAnswers();
		}

		// Retrieve all of the answers that are associated with a given question id from
		// the answer database
		public List<Answer> getAllAnswersForQuestion(int questionID) throws SQLException {
			return qaHelper.getAllAnswersForQuestion(questionID);
		}

		// Retrieve all of the answers that are associated with a given answer id from
		// the answer database
		public List<Answer> getAllAnswersForAnswer(int answerID) throws SQLException {
			return qaHelper.getAllAnswersForAnswer(answerID);
		}

		// Method used to convert the text of a question into a list of unique words
		// without special characters for comparison to others
		public List<String> textDeserial(String text) {
			return qaHelper.textDeserial(text);
		}

		// Update the contents of a question object with those of pass question object
		public void updateQuestion(Question question) {
			qaHelper.updateQuestion(question);
		}

		// Update the contents of a answer object with those of pass answer object
		public void updateAnswer(Answer answer) {
			qaHelper.updateAnswer(answer);
		}
	}
}
