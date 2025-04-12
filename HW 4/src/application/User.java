package application;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a user entity in the system.
 * <p>
 * The {@code User} class encapsulates the details of a user, including the username, password,
 * full name, email address, current active role, and a list of all roles assigned to the user.
 * It may also include a mapping of reviewer IDs and a flag indicating if a one-time password (OTP)
 * is active for the user.
 * </p>
 * 
 * @author CSE 360 Team 8
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
    
    /**
     * Flag indicating whether a one-time password is active for the user.
     */
    private boolean otp;

    /**
     * Constructs a new {@code User} with the specified id, username, name, password,
     * email, roles, and OTP flag.
     *
     * @param id       the unique identifier for the user
     * @param userName the username of the user
     * @param name     the full name of the user
     * @param password the user's password
     * @param email    the user's email address
     * @param roles    the list of roles assigned to the user
     * @param otp      true if a one-time password is active, false otherwise
     */
    public User(int id, String userName, String name, String password, String email, List<String> roles, boolean otp) {
        this.id = id;
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.otp = otp;
    }

    /**
     * Constructs a new {@code User} with the specified id, username, name, password,
     * email, roles, reviewer IDs, and OTP flag.
     *
     * @param id          the unique identifier for the user
     * @param userName    the username of the user
     * @param name        the full name of the user
     * @param password    the user's password
     * @param email       the user's email address
     * @param roles       the list of roles assigned to the user
     * @param reviewerIds a map of reviewer users to their associated IDs
     * @param otp         true if a one-time password is active, false otherwise
     */
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
    
    /**
     * Constructs a new {@code User} with the specified username, name, password,
     * email, roles, and OTP flag.
     *
     * @param userName the username of the user
     * @param name     the full name of the user
     * @param password the user's password
     * @param email    the user's email address
     * @param roles    the list of roles assigned to the user
     * @param otp      true if a one-time password is active, false otherwise
     */
    public User(String userName, String name, String password, String email, List<String> roles, boolean otp) {
        this.userName = userName;
        this.name = name;
        this.password = password;
        this.email = email;
        this.roles = roles;
        this.otp = otp;
    }

    /**
     * Constructs a new {@code User} with the specified username, name, password, email,
     * current role, roles, and OTP flag.
     * <p>
     * Note: This constructor is defined but not implemented.
     * </p>
     *
     * @param username    the username of the user
     * @param name        the full name of the user
     * @param password    the user's password
     * @param email       the user's email address
     * @param currentRole the user's current active role
     * @param roles       the list of roles assigned to the user
     * @param otp         true if a one-time password is active, false otherwise
     */
    public User(String username, String name, String password, String email, String currentRole, List<String> roles,
            boolean otp) {
        // Constructor intentionally left blank.
    }

    /**
     * Adds a role to the user's list of roles.
     *
     * @param role the role to add
     */
    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * Returns the user's unique identifier.
     *
     * @return the user ID.
     */
    public int getUserId() {
        return this.id;
    }

    /**
     * Returns the username.
     *
     * @return the username.
     */
    public String getUsername() {
        return this.userName;
    }

    /**
     * Returns the user's password.
     *
     * @return the password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Returns the full name of the user.
     *
     * @return the name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the user's current active role.
     *
     * @return the current role.
     */
    public String getCurrentRole() {
        return this.currentRole;
    }

    /**
     * Returns the list of roles assigned to the user.
     *
     * @return the list of roles.
     */
    public List<String> getRoles() {
        return this.roles;
    }
    
    /**
     * Returns the mapping of reviewer users to their associated IDs.
     *
     * @return the reviewer IDs map.
     */
    public Map<User, Integer> getReviewerIds() {
        return reviewerIds;
    }

    /**
     * Returns true if a one-time password is active for the user.
     *
     * @return true if OTP is active; false otherwise.
     */
    public boolean getOTPFlag() {
        return this.otp;
    }

    /**
     * Sets the user's password.
     *
     * @param password the new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user's current active role.
     *
     * @param role the new current role.
     */
    public void setCurrentRole(String role) {
        this.currentRole = role;
    }

    /**
     * Sets the mapping of reviewer IDs.
     *
     * @param reviewerIds the reviewer IDs map.
     */
    public void setReviewerIds(Map<User, Integer> reviewerIds) {
        this.reviewerIds = reviewerIds;
    }
    
    /**
     * Sets the OTP flag.
     *
     * @param flag true to activate OTP; false to deactivate.
     */
    public void setOTPFlag(boolean flag) {
        this.otp = flag;
    }

    /**
     * Returns a string representation of the user for debugging purposes.
     *
     * @return a formatted string with user details.
     */
    @Override
    public String toString() {
        return String.format("USER: \n Id: %s, Username: %s, Name: %s, Email: %s, Roles: %s", id, userName, name, email,
                roles);
    }

    /**
     * Returns a formatted display string for the user.
     * <p>
     * The display string includes the username, full name, email, and roles.
     * If the user's name is empty, an empty string is returned.
     * </p>
     *
     * @return a display string for the user.
     */
    public String toDisplay() {
        if (name.equals("")) {
            return "";
        } else {
            return String.format("%s\n%s\n%s               %s", userName, name, email, roles);
        }
    }

    /**
     * Returns the hash code value for this user.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, id, name, password, userName);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * Two users are considered equal if they have the same id, username, name, email, and password.
     *
     * @param obj the reference object with which to compare.
     * @return true if this object is equal to the obj argument; false otherwise.
     */
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
