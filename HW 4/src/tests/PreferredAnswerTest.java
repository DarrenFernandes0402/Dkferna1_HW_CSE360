package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import application.Question;

public class PreferredAnswerTest {

    private Question question;

    @Before
    public void setUp() {
        // Create a dummy question with id, title, text, authorId, and timestamps.
        question = new Question(1, "Test Question", "This is a test question", 100, 
                                  LocalDateTime.now(), LocalDateTime.now(), null);
        // Set valid answer IDs that represent valid answers for this question.
        List<String> validIds = Arrays.asList("101", "102", "103");
        question.setRelatedId(validIds);
        // Initially, no preferred answer is set (0 means none)
        question.setPreferredAnswer(0);
    }
    
    // Helper method that replicates the validation logic.
    // Returns an error message if the input is invalid, or null if valid.
    private String validatePreferredAnswerInput(String input, Question question) {
        if (input.trim().isEmpty()) {
            return "Input cannot be empty.";
        }
        try {
            int enteredId = Integer.parseInt(input.trim());
            if (question.getRelatedId() == null || 
                !question.getRelatedId().contains(String.valueOf(enteredId))) {
                return "Please enter a valid answer ID for this question.";
            }
        } catch (NumberFormatException e) {
            return "Please enter only numbers.";
        }
        return null;
    }
    
    // Test 1: Input is empty.
    @Test
    public void testEmptyInput() {
        String input = "";
        String error = validatePreferredAnswerInput(input, question);
        assertEquals("Input cannot be empty.", error);
    }
    
    // Test 2: Input is non-numeric.
    @Test
    public void testNonNumericInput() {
        String input = "abc";
        String error = validatePreferredAnswerInput(input, question);
        assertEquals("Please enter only numbers.", error);
    }
    
    // Test 3: Input is numeric but not in the valid list.
    @Test
    public void testInvalidAnswerId() {
        String input = "105"; // Not one of "101", "102", "103"
        String error = validatePreferredAnswerInput(input, question);
        assertEquals("Please enter a valid answer ID for this question.", error);
    }
    
    // Test 4: Valid input updates the preferred answer.
    @Test
    public void testValidPreferredAnswerUpdate() {
        String input = "102";
        String error = validatePreferredAnswerInput(input, question);
        assertNull(error);
        int enteredId = Integer.parseInt(input.trim());
        // Update the preferred answer.
        question.setPreferredAnswer(enteredId);
        assertEquals(102, question.getPreferredAnswer());
    }
    
    // Test 5: Updating the preferred answer after it has been set.
    @Test
    public void testUpdatePreferredAnswerChange() {
        // First, set an initial preferred answer.
        String initialInput = "101";
        String error = validatePreferredAnswerInput(initialInput, question);
        assertNull(error);
        question.setPreferredAnswer(Integer.parseInt(initialInput.trim()));
        assertEquals(101, question.getPreferredAnswer());
        
        // Now update the preferred answer to a different valid value.
        String newInput = "103";
        error = validatePreferredAnswerInput(newInput, question);
        assertNull(error);
        question.setPreferredAnswer(Integer.parseInt(newInput.trim()));
        assertEquals(103, question.getPreferredAnswer());
    }
}
