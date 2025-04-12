package tests;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import application.*;
import databasePart1.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This class the testing of the Question and Answers functionalities
 * using a database connection
 * @author Darren Fernandes
 */
public class RequestToBeAReviewerTest{
	/**
	 * default constructor
	 */
	private static DatabaseHelper databaseHelper = new DatabaseHelper();
	public RequestToBeAReviewerTest(){}
	/**
	 * This class sets up the database and ensures that each time the test is called, the 
	 * database is set back up from scratch. This ensures that each test is run without any issues 
	 * and that there is nothing interfering with the test, as well as ensures that each test is run on 
	 * the unedited version of the database and its contents. It also registers a user and a request from scratch
	 */
	@Before
    public void setUpDatabase() {
        try {
				databaseHelper.connectToDatabase();
				List<String> roles = Arrays.asList("Admin", "Student", "Instructor");
		        User user = new User(100, "Admin", "Admin", "Admin", "Admin", roles, false);
		        databaseHelper.register(user);
		        databaseHelper.currentUser = user;
		        Request request = new Request("This is a test request", user);
		        databaseHelper.register("This is a test request");
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }
	
	/**
	 * This is the first test for the request class. It just checks if there exists a request equal to the one 
	 * that was registered in the before class. It is successful if they are both equal, and also 
	 * catches exceptions, here the database and nullpointer exceptions are possible
	 */
	@Test
	public void testCreatingRequest() {
		try {
			assertEquals("This is a test request", databaseHelper.getAllRequests().get(0).getRequest());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred in test 1: " + e.getMessage());
		}
	}
	/**
	 * This test checks who all have permission to see a review. At this point, without any manipulation
	 * only the instructor should be able to see the request, meaning that getting all the requests should show us that the 
	 * instructor's variable requestTOF should be true and the Admin's variable requestATOF should be false
	 */
	@Test
	public void testSeeingRequest() {
		try {
			assertFalse("Expected requestTOF to be true", databaseHelper.getAllRequests().get(0).getRequestTOF());
			assertTrue("Expected requestTOF to be true", databaseHelper.getAllRequests().get(0).getRequestATOF());
		}
		catch(Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
	}
	
	/**
	 * This test checks the permissions again but if they were updated to be visible to only the admin. In this case
	 * only the admin's visibility should be true, and the the instructor's is false
	 */
	@Test
	public void InstructorAccept() {
		try {
			databaseHelper.updateRequestStatus("Admin", true, true);
			assertTrue("Expected requestTOF to be false" , databaseHelper.getAllRequestsForAdmin().get(0).getRequestTOF());
			assertTrue("Expected requestATOF to be true", databaseHelper.getAllRequestsForAdmin().get(0).getRequestATOF());
		}
		catch(Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
	}
	
	/**
	 * When the instructor declines a request, it should not longer appear on the instructor's side
	 */
	@Test
	public void InstructorDecline() {
		try {
			databaseHelper.updateRequestStatus("Admin", false, false);
			assertFalse("Expected requestTOF to be false", databaseHelper.getAllRequestsA().get(0).getRequestTOF());
			assertFalse("Expected requestATOF to be true", databaseHelper.getAllRequestsA().get(0).getRequestATOF());
		}
		catch(Exception e) {
            e.printStackTrace();
            fail("Exception occurred: " + e.getMessage());
        }
	}
	/**
	 * This is what does happen when the request is accepted, which is it is deleted from the database. 
	 * If this is true, then there should be nothing in the database, meaning when we try and get the requests,
	 * the table should be empty.
	 */
	@Test
	public void DeleteRequestTest() {
	    try {
	        
	        databaseHelper.deleteRequest("Admin");
	        assertTrue("Expected request to be deleted", databaseHelper.getAllRequestsA().isEmpty());
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        fail("Exception occurred in DeleteRequestTest: " + e.getMessage());
	    }
	}

}