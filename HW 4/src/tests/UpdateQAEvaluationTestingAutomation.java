package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.*;

import application.*;
import databasePart1.DatabaseHelper;
import databasePart1.QAHelper1;

/*******
 * <p> Title: HW3Junit Class. </p>
 * 
 * <p> Description: A Junit testing class that is testing adding and deleting questions and answers from the database. </p>
 * 
 * <p> Copyright: Zachary Chalmers @ 2025 </p>
 * 
 * @author Zachary Chalmers
 * 
 * @version 1.00	2025-03-24 Creating and testing the tests
 * 
 */

public class UpdateQAEvaluationTestingAutomation {

	private static DatabaseHelper dbHelper;
	private static QAHelper1 qaHelper;
	private static Question question;
	private static Answer answer; 
	
	/**
	 * Default constructor to be used for javaDoc
	 */
	
	public UpdateQAEvaluationTestingAutomation() {
		super();
	}
	/**
	 * This setup function runs at the beginning of the test and sets up the 
	 * database, and gets the first question and answer to start the tests on. 
	 * Using the connectToDatabase(), we have it set to populate with test questions 
	 * and answers. 
	 * @throws SQLException if there is an error when setting up the database.
	 */
	
	@Before
	public void setup() throws SQLException {
		dbHelper = new DatabaseHelper();
		
		qaHelper = new QAHelper1(dbHelper);
		dbHelper.connectToDatabase();
		question = dbHelper.qaHelper.getAllQuestions().get(0);
		answer = dbHelper.qaHelper.getAnswer(Integer.parseInt(question.getRelatedId().get(0)));
	}
	
	/**
	 * This tests adding a new answer to the question and then verifies the size of 
	 * the questions related answers to ensure that it was added. 
	 * @throws SQLException if there is an error when getting the question or answer from the database. 
	 */
    @Test
    public void testAddAnswerToQuestion() throws SQLException {
    	LocalDateTime time = LocalDateTime.now();
        Answer newAnswer = new Answer(2100, "Here is my test answer.", 21, time, time);

        dbHelper.qaHelper.registerAnswerWithQuestion(newAnswer, question.getId());

        Question updatedQuestion = dbHelper.qaHelper.getQuestion(question.getId());

        assertEquals(question.getId(), updatedQuestion.getId());
        assertEquals(question.getRelatedId().size() + 1, updatedQuestion.getRelatedId().size());
    }
    
    /**
	 * This tests editing a question title and then verifies that the old
	 * question title does not equal the new question title. 
	 * @throws SQLException if there is an error when getting the question from the database.
	 */
    @Test
    public void testEditQuestionTitle() throws SQLException {
        String oldTitle = question.getTitle();
        question.setTitle("Updated title at " + LocalDateTime.now());

        dbHelper.qaHelper.updateQuestion(question);

        Question updatedQuestion = dbHelper.qaHelper.getQuestion(question.getId());

        assertEquals(question.getId(), updatedQuestion.getId());
        assertNotEquals(oldTitle, updatedQuestion.getTitle());
    }
    
    /**
	 * This tests deleting an answer from a question and then verifies
	 * that the size of the question's related answers is 1 less than the 
	 * old question's related answers. 
	 * @throws SQLException if there is an error when deleting the answer from the database.
	 */
    @Test
    public void testDeleteAnswerFromQuestion() throws SQLException {
    	Question oldQuestion = dbHelper.qaHelper.getQuestion(question.getId());
    	LocalDateTime time = LocalDateTime.now();
        Answer newAnswer = dbHelper.qaHelper.getAnswer(Integer.parseInt(oldQuestion.getRelatedId().get(0)));
  
        
    	dbHelper.qaHelper.deleteAnswer(newAnswer.getId());
    	dbHelper.qaHelper.deleteRelation(question.getId(), newAnswer.getId());

        Question updatedQuestion = dbHelper.qaHelper.getQuestion(question.getId());

        assertEquals(oldQuestion.getId(), updatedQuestion.getId());
        assertEquals(oldQuestion.getRelatedId().size() - 1, updatedQuestion.getRelatedId().size());
    }
    
    /**
	 * This tests re-adding a new answer to the question after editing the question title and 
	 * then verifies that the answer was added to the new question's related answers. 
	 * @throws SQLException if there is an error when getting the question from the database. 
	 */
    @Test
    public void testReAddAnswerAfterEdit() throws SQLException {
    	LocalDateTime time = LocalDateTime.now();
    	Answer newAnswer = new Answer(2100, "Here is my test answer.", 21, time, time);
    	Question oldQuestion = dbHelper.qaHelper.getQuestion(question.getId());
    	
    	
    	dbHelper.qaHelper.registerAnswerWithQuestion(newAnswer, question.getId());

        Question updatedQuestion = dbHelper.qaHelper.getQuestion(question.getId());

        assertEquals(oldQuestion.getId(), updatedQuestion.getId());
        assertEquals(oldQuestion.getRelatedId().size() + 1, updatedQuestion.getRelatedId().size());
    }
    
    /**
	 * This tests updating an answer that is associated with a question and then 
	 * verifies that the question now contains the new answer with its new text. 
	 * @throws SQLException if there is an error when getting the question from the database.
	 */
    @Test
    public void testUpdateAnswerText() throws SQLException {
    	
        answer.setText("Updated answer text");
        dbHelper.qaHelper.updateAnswer(answer);

        Question updatedQuestion = dbHelper.qaHelper.getQuestion(question.getId());

        assertEquals(question.getId(), updatedQuestion.getId());
        assertTrue(updatedQuestion.getRelatedId().contains(answer.getId().toString()));
    }
}
