/*package databasePart1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.User;

public class DatabaseTests {

	private static final DatabaseHelper helper = new DatabaseHelper();

	private static final String username = "BLAH";
	private static final String name = "blah";
	private static final String password = "Password1!";
	private static final String email = "blah@gmail.com";
	
	
	
	// just checking if there is any errors or SQL syntax we have missed
	public static void main(String[] args) {
		testAddUser();
	}
	
	
	
	public static void testAddUser() {
		if (addUser()) {
			User user = getUser(username);
			
			assert(user.getUsername().equals(username));
			assert(user.getName().equals(name));
			assert(user.getPassword().equals(password));
			assert(user.getEmail().equals(email));
		}
	}
	
	
	public static User getUser(String username) {
		User user;
		try {
			user = helper.getUser(username);
			return user;
		}
		catch (SQLException e) {  
			System.out.println("ADDUSER: " + e.getMessage());
			return null;
		} 
		
	}
	
	
	public static boolean addUser() {
		List<String> roles = new ArrayList<>();
		roles.add("admin");
		User user = new User(username, name, password, email, roles, false);
		try {
			helper.register(user);
			return true;
		}
		catch (SQLException e) {  
			System.out.println("ADDUSER: " + e.getMessage());
			return false;
		}
	}
	
	
	
	
	
	
}


*/