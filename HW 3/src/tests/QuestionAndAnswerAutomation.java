package tests;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.*;
import databasePart1.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * This class automates the testing of Question and Answer functionalities
 * using a database connection.
 * @author Darren Fernandes
 */
public class QuestionAndAnswerAutomation {
	/**
	 * default constructor
	 */
	public QuestionAndAnswerAutomation() {}
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	static int numPassed = 0;
	static int numFailed = 0;
	static int size;
	private static String testString = "This is the request";
	
	/**
	 * This class sets up the database and ensures that each time the test is called, the 
	 * database is set back up from scratch. This ensures that each test is run without any issues 
	 * and that there is nothing interfering with the test, as well as ensures that each test is run on 
	 * the unedited version of the database and its contents
	 */
	@BeforeClass
    public static void setUpDatabase() {
        try {
            databaseHelper.connectToDatabase();
        } catch (SQLException e) {
            fail("Database connection failed: " + e.getMessage());
        }
    }

	/**
	 * This test will run several methods within the question and answer database and to try and ensure that the
	 * methods in there work correctly. The first method it tests is the getQuestion, which returns the entire question
	 * object based on the question number, in this case, 1. The next method is the getText method. This method returns 
	 * the question as a string. Since this is a test, we have pre-populated the fields with information we already know. 
	 * If the getText method as well as the getQuestion method both work correctly, it should return the text we specified i.e.
	 * "Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?". If it does return this, 
	 * we know that the test is working fine. however, if it does not, then the function is not working and needs to be checked.
	 * However, it can also fail when trying to connect to the database. In this case, it should enter the catch block, which 
	 * will let us know that we have used this already tested code wrong, and need to adjust how we call it
	 */
	@Test
	public void testSetQuestion() {

	    try {
	        String question1 = databaseHelper.qaHelper.getQuestion(1).getText();
	        assertEquals("Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?", question1);
	    } catch (SQLException e) {
	        fail("Case 1 not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * This test will also run several methods within the question and answer database and to try and ensure that the
	 * methods in there work correctly. The first method it tests is the getQuestion, which returns the entire question
	 * object based on the question number, in this case, 1. this time, we do not return the text, but the object itself
	 * When the object does return, we use the setText method. This method is a setter in the Question class. When the question
	 * is set, we then try and update the database. This update should update the question in the database.
	 * We then call the method from the last test, and compare the string we get to the one we tried to set it as.
	 * If it does set it correctly, The test passes. If it does not, we fail the test.  However, it can also fail when trying 
	 * to connect to the database. In this case, it should enter the catch block, which will let us know that we have used this 
	 * already tested code wrong, and need to adjust how we call it.
	 */
	@Test
	public void testUpdateQuestion() {
		try {
	        Question question = databaseHelper.qaHelper.getQuestion(1);
	        question.setText("It works");
	        databaseHelper.qaHelper.updateQuestion(question);
	        assertEquals("It works", databaseHelper.qaHelper.getQuestion(1).getText());

	        question.setText("Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?");
	        databaseHelper.qaHelper.updateQuestion(question);
	    } catch (SQLException e) {
	        fail("Test 2 is not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * This test will run several methods within the question and answer database and to try and ensure that the
	 * methods in there work correctly. The first method it tests is the getAnswer, which returns the entire answer
	 * object based on the answer number, in this case, 3. The next method is the getText method. This method returns 
	 * the answer as a string. Since this is a test, we have pre-populated the fields with information we already know. 
	 * If the getText method as well as the getAnswer method both work correctly, it should return the text we specified i.e.
	 * "Makes sense to me.". If it does return this, we know that the test is working fine. however, if it does not, then the 
	 * function is not working and needs to be checked.	 * However, it can also fail when trying to connect to the database. 
	 * In this case, it should enter the catch block, which will let us know that we have used this already tested code wrong, 
	 * and need to adjust how we call it.
	 */
	@Test
	public void testGetAnswer() {
		try {
	        Answer answer = databaseHelper.qaHelper.getAnswer(3);
	        assertEquals("Makes sense to me.", answer.getText());
	    } catch (SQLException e) {
	        fail("Case 3 is not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * This test will also run several methods within the question and answer database and to try and ensure that the
	 * methods in there work correctly. The first method it tests is the getAnswer, which returns the entire answer
	 * object based on the answer number, in this case, 3. this time, we do not return the text, but the object itself
	 * When the object does return, we use the setText method. This method is a setter in the Question class. When the answer
	 * is set, we then try and update the database. This update should update the answer in the database.
	 * We then call the method from the last test, and compare the string we get to the one we tried to set it as.
	 * If it does set it correctly, The test passes. If it does not, we fail the test.  However, it can also fail when trying 
	 * to connect to the database. In this case, it should enter the catch block, which will let us know that we have used this 
	 * already tested code wrong, and need to adjust how we call it.
	 */
	@Test
	public void testUpdateAnswer() {
		try {
	        Answer answer = databaseHelper.qaHelper.getAnswer(3);
	        answer.setText("It works!");
	        databaseHelper.qaHelper.updateAnswer(answer);
	        assertEquals("It works!", databaseHelper.qaHelper.getAnswer(3).getText());

	        answer.setText("Makes sense to me.");
	        databaseHelper.qaHelper.updateAnswer(answer);
	    } catch (SQLException e) {
	        fail("Case 4 is not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * This is a test to test our ability to get all the answers from the database. We know how we stored them along with the 
	 * number of answers, what the answers are, and their related id's. With this information, we test a few things in there
	 * that we expect, and see if our list has the required things. The first thing we do is ofcourse connect to the database,
	 * which should be done without issues, but is placed in a try catch block just incase. We then get all the answers from
	 * the database and store it in a list for a particular question, 1 in this case. We then compare the number of answers 
	 * in the list, 2, versus the number found. We also check the text of the field and check if that also works out to the same
	 * text expected, "Oh well another test happened", in this case. If both of these are fine, then the test passes and we exit 
	 * out successfully
	 */
	@Test
	public void testGetAllAnswers() {
		try {
	        List<Answer> answers = databaseHelper.qaHelper.getAllAnswersForQuestion(1);
	        assertEquals("Wrong number of answers", 2, answers.size());
	        assertEquals("Oh well another test happened", answers.get(1).getText());
	    } catch (SQLException e) {
	        fail("Case 5 is not working due to SQLException: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
}