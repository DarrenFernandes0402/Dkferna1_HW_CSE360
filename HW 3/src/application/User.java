package application;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The User class represents a user entity in the system. It contains the user's
 * details such as userName, password, and role.
 */
public class User {
	private int id;
	private String userName;
	private String name;
	private String password;
	private String email;
	private String currentRole;
	private List<String> roles;
	
	private Map<User, Integer> reviewerIds;
	
	// Flag will be true if one-time password is active on user
	private boolean otp;

	// Constructor to initialize a new User object with userName, password, and
	// role.
	public User(int id, String userName, String name, String password, String email, List<String> roles, boolean otp) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.otp = otp;
	}

	public User(int id, String userName, String name, String password, String email, List<String> roles, Map<User, Integer> reviewerIds, boolean otp) {
		this.id = id;
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.reviewerIds = reviewerIds;
		this.otp = otp;
	}
	
	public User(String userName, String name, String password, String email, List<String> roles, boolean otp) {
		this.userName = userName;
		this.name = name;
		this.password = password;
		this.email = email;
		this.roles = roles;
		this.otp = otp;
	}

	public User(String username, String name, String password, String email, String currentRole, List<String> roles,
			boolean otp) {

	}

	public void addRole(String role) {
		roles.add(role);
	}

	public int getUserId() {
		return this.id;
	}

	public String getUsername() {
		return this.userName;
	}

	public String getPassword() {
		return this.password;
	}

	public String getEmail() {
		return this.email;
	}

	public String getName() {
		return this.name;
	}

	public String getCurrentRole() {
		return this.currentRole;
	}

	public List<String> getRoles() {
		return this.roles;
	}
	

	public Map<User, Integer> getReviewerIds() {
		return reviewerIds;
	}

	
	public boolean getOTPFlag() {
		return this.otp;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setCurrentRole(String role) {
		this.currentRole = role;
	}

	public void setReviewerIds(Map<User, Integer> reviewerIds) {
		this.reviewerIds = reviewerIds;
	}
	
	public void setOTPFlag(boolean flag) {
		this.otp = flag;
	}

	public String toString() {
		return String.format("USER: \n Id: %s, Username: %s, Name: %s, Email: %s, Roles: %s", id, userName, name, email,
				roles);
	}

	public String toDisplay() {

		// If name is empty then return an empty string
		if (name == "") {
			return "";
		} else {
			return String.format("%s\n%s\n%s               %s", userName, name, email, roles);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, id, name, password, userName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(email, other.email) && id == other.id && Objects.equals(name, other.name)
				&& Objects.equals(password, other.password) && Objects.equals(userName, other.userName);
	}
	
	
	
	
}
