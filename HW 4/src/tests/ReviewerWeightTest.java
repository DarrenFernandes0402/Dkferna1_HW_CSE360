package tests;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import application.User;
import databasePart1.DatabaseHelper;
import tests.PopulateUserDatabase;

public class ReviewerWeightTest {

    private static DatabaseHelper dbHelper;
    private static User testUser;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Initialize the DatabaseHelper and connect to the database.
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();

        // Run your populate file to set up the user database.
        new PopulateUserDatabase(dbHelper).execute();
        // Retrieve a known user as the test user. "Kapierc8" is user #1 in your PopulateUserDatabase.
        testUser = dbHelper.getUser("Kapierc8");
        if (testUser == null) {
            throw new IllegalStateException("Could not find user 'Kapierc8' after population!");
        }
        
        // Clear the reviewer's list for the test user.
        dbHelper.updateReviewers(new java.util.HashMap<>(), testUser.getUserId());
    }

    @Before
    public void setUp() throws SQLException {
        // Reset the reviewer's list before each test.
        dbHelper.updateReviewers(new java.util.HashMap<>(), testUser.getUserId());
    }
    
    // Helper method to retrieve a reviewer's weight from a map using the reviewer's user ID.
    private Integer getWeightForReviewer(Map<User, Integer> reviewerMap, int reviewerId) {
        Optional<Integer> weight = reviewerMap.entrySet()
                .stream()
                .filter(e -> e.getKey().getUserId() == reviewerId)
                .map(e -> e.getValue())
                .findFirst();
        return weight.orElse(null);
    }
    
    /**
     * setReviewerWeight:
     * Verifies that adding a reviewer sets the weight correctly.
     */
    @Test
    public void setReviewerWeight() throws SQLException {
        // Retrieve a reviewer from the populated users.
        User reviewer = dbHelper.getUser("cespina3");
        if (reviewer == null) {
            throw new IllegalStateException("Could not find user 'cespina3' after population!");
        }
        int weight = 3;
        
        // Add the reviewer with the specified weight.
        boolean added = dbHelper.addReviewer(testUser.getUserId(), reviewer, weight);
        assertTrue("Reviewer should be added successfully", added);
        
        // Retrieve the reviewer's map for the test user.
        Map<User, Integer> reviewers = dbHelper.getAllReviewersForUser(testUser.getUserId());
        Integer storedWeight = getWeightForReviewer(reviewers, reviewer.getUserId());
        
        assertNotNull("Reviewer should be present in the map", storedWeight);
        assertEquals("Weight should be set correctly", Integer.valueOf(weight), storedWeight);
    }
    
    /**
     * changeReviewerWeight:
     * Verifies that updating a reviewer's weight changes it as expected.
     */
    @Test
    public void changeReviewerWeight() throws SQLException {
        // Retrieve a reviewer from the populated users.
        // For this test, we'll use the user with username "xXAnthonyXx" (User 3 in PopulateUserDatabase).
        User reviewer = dbHelper.getUser("xXAnthonyXx");
        if (reviewer == null) {
            throw new IllegalStateException("Could not find user 'xXAnthonyXx' after population!");
        }
        
        // Set an initial weight.
        int initialWeight = 2;
        dbHelper.addReviewer(testUser.getUserId(), reviewer, initialWeight);
        
        // Now update the weight to a new value.
        int newWeight = 6;
        boolean updated = dbHelper.updateReviewerWeight(testUser.getUserId(), reviewer, newWeight);
        assertTrue("Reviewer weight should update successfully", updated);
        
        // Retrieve the reviewer's map for the test user.
        Map<User, Integer> reviewers = dbHelper.getAllReviewersForUser(testUser.getUserId());
        Integer storedWeight = getWeightForReviewer(reviewers, reviewer.getUserId());
        assertNotNull("Reviewer should be present in the map", storedWeight);
        assertEquals("Reviewer weight should be updated", Integer.valueOf(newWeight), storedWeight);
    }
    /**
     * testMultipleReviewersWithEqualWeight:
     * Verifies that when multiple reviewers are added with the same weight,
     * they are both stored correctly.
     */
    @Test
    public void testMultipleReviewersWithEqualWeight() throws SQLException {
        // Retrieve two reviewers from the populated database.
        // In PopulateUserDatabase, "xXMarkusXx" is user with id 6 and "ShadowX" is user with id 8.
        User reviewer1 = dbHelper.getUser("xXMarkusXx");
        User reviewer2 = dbHelper.getUser("ShadowX");
        if (reviewer1 == null || reviewer2 == null) {
            throw new IllegalStateException("Could not find reviewers 'xXMarkusXx' or 'ShadowX' after population!");
        }
        
        int equalWeight = 4;
        // Add both reviewers with the same weight.
        boolean added1 = dbHelper.addReviewer(testUser.getUserId(), reviewer1, equalWeight);
        boolean added2 = dbHelper.addReviewer(testUser.getUserId(), reviewer2, equalWeight);
        
        assertTrue("Reviewer1 should be added successfully", added1);
        assertTrue("Reviewer2 should be added successfully", added2);
        
        // Retrieve the reviewer map for the test user.
        Map<User, Integer> reviewers = dbHelper.getAllReviewersForUser(testUser.getUserId());
        
        // Use the helper method to get the stored weight for each reviewer.
        Integer weight1 = getWeightForReviewer(reviewers, reviewer1.getUserId());
        Integer weight2 = getWeightForReviewer(reviewers, reviewer2.getUserId());
        
        assertNotNull("Reviewer1 should be present in the map", weight1);
        assertNotNull("Reviewer2 should be present in the map", weight2);
        assertEquals("Reviewer1 weight should be equal to " + equalWeight, Integer.valueOf(equalWeight), weight1);
        assertEquals("Reviewer2 weight should be equal to " + equalWeight, Integer.valueOf(equalWeight), weight2);
    }
    /**
     * testReviewerWeightBoundary:
     * Verifies that the system correctly stores the minimum and maximum allowed weights.
     */
    @Test
    public void testReviewerWeightBoundary() throws SQLException {
        // Retrieve two reviewers from the populated database.
        // Adjust the usernames as needed based on your PopulateUserDatabase.
        User reviewerMin = dbHelper.getUser("cespina3");       // Using as an example for min weight.
        User reviewerMax = dbHelper.getUser("xXAnthonyXx");       // Using as an example for max weight.
        
        if (reviewerMin == null || reviewerMax == null) {
            throw new IllegalStateException("Could not retrieve the necessary reviewers from the database.");
        }
        
        // Define boundary values.
        int minWeight = 0;
        int maxWeight = 10;
        
        // Add the reviewers with boundary weights.
        boolean addedMin = dbHelper.addReviewer(testUser.getUserId(), reviewerMin, minWeight);
        boolean addedMax = dbHelper.addReviewer(testUser.getUserId(), reviewerMax, maxWeight);
        
        assertTrue("Reviewer with minimum weight should be added successfully", addedMin);
        assertTrue("Reviewer with maximum weight should be added successfully", addedMax);
        
        // Retrieve the reviewer map for the test user.
        Map<User, Integer> reviewerMap = dbHelper.getAllReviewersForUser(testUser.getUserId());
        
        // Use helper method to get the stored weight.
        Integer storedMin = getWeightForReviewer(reviewerMap, reviewerMin.getUserId());
        Integer storedMax = getWeightForReviewer(reviewerMap, reviewerMax.getUserId());
        
        assertNotNull("Reviewer for min weight should be present in the map", storedMin);
        assertNotNull("Reviewer for max weight should be present in the map", storedMax);
        assertEquals("Stored minimum weight should be " + minWeight, Integer.valueOf(minWeight), storedMin);
        assertEquals("Stored maximum weight should be " + maxWeight, Integer.valueOf(maxWeight), storedMax);
    }
    /**
     * testReviewerRemovalAndReAddition:
     * Verifies that after removing a reviewer, re-adding them with a new weight results in the updated weight
     * being stored correctly.
     */
    @Test
    public void testReviewerRemovalAndReAddition() throws SQLException {
        // Retrieve a reviewer from the populated database.
        // For this test, we'll use the user with username "xXMarkusXx".
        User reviewer = dbHelper.getUser("xXMarkusXx");
        if (reviewer == null) {
            throw new IllegalStateException("Could not find user 'xXMarkusXx' after population!");
        }
        
        // Add the reviewer with an initial weight.
        int initialWeight = 3;
        boolean added = dbHelper.addReviewer(testUser.getUserId(), reviewer, initialWeight);
        assertTrue("Reviewer should be added successfully", added);
        
        // Verify that the reviewer is present with the initial weight.
        Map<User, Integer> reviewersMap = dbHelper.getAllReviewersForUser(testUser.getUserId());
        Integer storedWeight = getWeightForReviewer(reviewersMap, reviewer.getUserId());
        assertNotNull("Reviewer should be present after initial add", storedWeight);
        assertEquals("Initial weight should match", Integer.valueOf(initialWeight), storedWeight);
        
        // Remove the reviewer.
        boolean removed = dbHelper.removeReviewer(testUser.getUserId(), reviewer);
        assertTrue("Reviewer should be removed successfully", removed);
        
        // Verify that the reviewer is no longer present.
        reviewersMap = dbHelper.getAllReviewersForUser(testUser.getUserId());
        storedWeight = getWeightForReviewer(reviewersMap, reviewer.getUserId());
        assertNull("Reviewer should not be present after removal", storedWeight);
        
        // Re-add the same reviewer with a new weight.
        int newWeight = 6;
        boolean reAdded = dbHelper.addReviewer(testUser.getUserId(), reviewer, newWeight);
        assertTrue("Reviewer should be re-added successfully", reAdded);
        
        // Verify that the reviewer is present with the new weight.
        reviewersMap = dbHelper.getAllReviewersForUser(testUser.getUserId());
        storedWeight = getWeightForReviewer(reviewersMap, reviewer.getUserId());
        assertNotNull("Reviewer should be present after re-addition", storedWeight);
        assertEquals("Re-added reviewer weight should match new weight", Integer.valueOf(newWeight), storedWeight);
    }



}
