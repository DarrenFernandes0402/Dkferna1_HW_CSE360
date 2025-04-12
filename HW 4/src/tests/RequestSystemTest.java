package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.Request;
import application.User;
import databasePart1.DatabaseHelper;

import java.util.Arrays;

/**
 * This class tests the functionality of the request system, including
 * creating, closing, reopening requests, adding notes, and approval flags.
 */
public class RequestSystemTest {

    private DatabaseHelper db;
    private User instructor;
    private User admin;

    @Before
    public void setUp() throws Exception {
        db = new DatabaseHelper();
        db.connectToDatabase();

        // Register instructor
        instructor = new User(9991, "instructorUser", "Instructor", "pass", "inst@example.com",
                Arrays.asList("Instructor"), false);
        db.register(instructor);

        // Register admin
        admin = new User(9992, "adminUser", "Admin", "adminpass", "admin@example.com",
                Arrays.asList("Admin"), false);
        db.register(admin);

        db.currentUser = instructor;
    }

    /**
     * Test 1: Create a new request and verify default values.
     */
    @Test
    public void testCreateRequest() throws SQLException {
        String requestText = "Please fix classroom projector.";
        db.createNewRequest(requestText, instructor.getUsername());

        List<Request> all = db.getAllRequests();
        assertEquals(1, all.size());

        Request r = all.get(0);
        assertEquals("OPEN", r.getStatus());
        assertEquals(requestText, r.getRequest());
        assertEquals(instructor.getUsername(), r.getUserName());
        assertEquals(0, r.getOriginalId());
    }

    /**
     * Test 2: Close a request with a note and ensure note/status are set.
     */
    @Test
    public void testCloseRequestWithNote() throws SQLException {
        db.createNewRequest("Setup printer in lab", instructor.getUsername());
        Request request = db.getAllRequests().get(0);

        db.currentUser = admin;
        db.closeRequest(request.getId(), "Printer installed successfully.");

        Request closed = db.getRequestById(request.getId());
        assertEquals("CLOSED", closed.getStatus());
        assertTrue(closed.getNotes().contains("Printer installed successfully."));
    }

    /**
     * Test 3: Reopen a closed request, check merged notes and updated status.
     */
    @Test
    public void testReopenClosedRequest() throws SQLException {
        db.createNewRequest("Projector broken", instructor.getUsername());
        Request original = db.getAllRequests().get(0);

        db.currentUser = admin;
        db.closeRequest(original.getId(), "Replaced projector bulb.");

        db.currentUser = instructor;
        db.reopenRequest(original.getId(), "Still not working", "Seems like power issue", instructor.getUsername());

        Request reopened = db.getRequestById(original.getId());
        assertEquals("REOPENED", reopened.getStatus());
        assertEquals(original.getId(), reopened.getId()); // same row updated
        assertTrue(reopened.getNotes().contains("Replaced projector bulb."));
        assertTrue(reopened.getNotes().contains("Seems like power issue"));
        assertEquals("Still not working", reopened.getRequest());
    }

    /**
     * Test 4: Add a note without changing status.
     */
    @Test
    public void testAddNoteOnly() throws SQLException {
        db.createNewRequest("Need more whiteboard markers", instructor.getUsername());
        Request req = db.getAllRequests().get(0);

        db.addNoteToRequest(req.getId(), "Supplies ordered.");

        Request updated = db.getRequestById(req.getId());
        assertEquals("OPEN", updated.getStatus());
        assertTrue(updated.getNotes().contains("Supplies ordered."));
    }

    /**
     * Test 5: Manually modify approval flags and verify getter/setter behavior.
     */
    @Test
    public void testInstructorAndAdminApprovalFlags() throws SQLException {
        db.createNewRequest("Software update needed", instructor.getUsername());
        Request req = db.getAllRequests().get(0);

        req.setRequestTOF(true);
        req.setRequestATOF(true);

        assertTrue(req.getRequestTOF());
        assertTrue(req.getRequestATOF());

        req.setRequestTOF(false);
        req.setRequestATOF(false);

        assertFalse(req.getRequestTOF());
        assertFalse(req.getRequestATOF());
    }
}
