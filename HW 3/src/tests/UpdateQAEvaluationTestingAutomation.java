package tests;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import application.*;
import databasePart1.*;

public class UpdateQAEvaluationTestingAutomation {
	private static final DatabaseHelper databaseHelper = new DatabaseHelper();
	private static final QAHelper1 qaHelper = new QAHelper1(databaseHelper);
	static int numPassed = 0;
	static int numFailed = 0;
	static int size;
	private static Question question;
	private static Answer answer;
	private static List<Question> questions;
	private static List<Answer> answers;


	public static void main(String[] args) {
		// Initialize the test environment

		try {
			databaseHelper.connectToDatabase();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		try {
			questions = databaseHelper.qaHelper.getAllQuestions();
		}
		catch (SQLException e) {
			System.out.println("Could not get all questions");
		}
		
		question = questions.get(0);
		
		// Test Case 1
		// Adds a new answer to the question, checks size of answers
		System.out.println("Starting TEST 1\n");
		performTestCase(1, false);
		System.out.println("\nTEST 1 has completed\n");

		// Test Case 2
		// Edits the question the answer is associated with
		System.out.println("Starting TEST 2\n");
		performTestCase(2, false);
		System.out.println("\nTEST 2 has completed\n");

		// Test Case 3
		// Delete the answer after editing question, checks size of answers
		System.out.println("Starting TEST 3\n");
		performTestCase(3, true);
		System.out.println("\nTEST 3 has completed\n");

		// Test Case 4
		// Recreate new answer after editing question, check size of answers
		System.out.println("Starting TEST 4\n");
		performTestCase(4, true);
		System.out.println("\nTEST 4 has completed\n");

		// Test Case 5
		// Edits the new answer 
		System.out.println("Starting TEST 5\n");
		performTestCase(5, true);
		System.out.println("\nTEST 5 has completed\n");



		// Display the results of the series of tests
		displayResults();
	}

	private static void performTestCase(int testNum, boolean flag) {

		// Perform appropriate test according to testNum
		switch (testNum) {

		case 1:
			try {
				LocalDateTime time = LocalDateTime.now();
				Answer newAnswer = new Answer(2100, "Here is my test answer.", 21, time, time);
				
				databaseHelper.qaHelper.registerAnswerWithQuestion(newAnswer, question.getId());
				
				Question newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());
				newAnswer = databaseHelper.qaHelper.getAnswer(newAnswer.getText());
				
				if (newQuestion.getId() == question.getId()) {
					if (newQuestion.getRelatedId().size() - question.getRelatedId().size() == 1) {
						numPassed++;
						question = newQuestion;
						answer = newAnswer;
						break;
					}
					else {
						System.out.println("New answer was not added");
						numFailed++;
					}
				}
				else {
					System.out.println("Could not get the same question");
					numFailed++;
				}
				
			} catch(SQLException e) {
				System.out.println("Database exception" + e.getMessage());
				numFailed++;
			}
			
			break;

		case 2:
			try {
				String oldTitle = question.getTitle();
				Question newQuestion = question;
				newQuestion.setTitle("I have updated this question" + LocalDateTime.now().toString());

				databaseHelper.qaHelper.updateQuestion(newQuestion);

				newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());
				
				if (newQuestion.getId().equals(question.getId())) {
					if (!newQuestion.getTitle().equals(oldTitle)) {
						numPassed++;
						question = newQuestion;
						break;
					} else {
						System.out.println("Question was not edited correctly");
						numFailed++;
					}
				} else {
					System.out.println("Could not get the same question");
					numFailed++;
				}
			} catch (Exception e) {
				System.out.println("Database exception" + e.getMessage());
				numFailed++;
			}
			break;

		case 3:
			try {
				databaseHelper.qaHelper.deleteAnswer(answer.getId());
				databaseHelper.qaHelper.deleteRelation(question.getId(), answer.getId());
				
				Question newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());
				
				if (newQuestion.getId() == question.getId()) {
					if (question.getRelatedId().size() - newQuestion.getRelatedId().size() == 1) {
						numPassed++;
						break;
					}
					else {
						System.out.println("New answer was not deleted");
						numFailed++;
					}
				}
				else {
					System.out.println("Could not get the same question");
					numFailed++;
				}
				
			} catch(SQLException e) {
				System.out.println("Database exception" + e.getMessage());
				numFailed++;
			}
			break;

		case 4:
			try {
				LocalDateTime time = LocalDateTime.now();
				Answer newAnswer = answer;
				
				databaseHelper.qaHelper.registerAnswerWithQuestion(answer, question.getId());
				databaseHelper.qaHelper.addRelationToQuestion(question.getId(), newAnswer.getId());
				
				Question newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());
				
				if (newQuestion.getId() == question.getId()) {
					if (newQuestion.getRelatedId().size() - question.getRelatedId().size() == 1) {
						numPassed++;
						question = newQuestion;
						answer = newAnswer;
						break;
					}
					else {
						System.out.println("New answer was not added");
						numFailed++;
					}
				}
				else {
					System.out.println("Could not get the same question");
					numFailed++;
				}
				
			} catch(SQLException e) {
				System.out.println("Database exception" + e.getMessage());
				numFailed++;
			}
			
			break;

		case 5:
			try {

				Answer newAnswer = answer;
				newAnswer.setText("Updated answer");
				
				databaseHelper.qaHelper.updateAnswer(newAnswer);
				
				Question newQuestion = databaseHelper.qaHelper.getQuestion(question.getId());
				
				if (newQuestion.getId() == question.getId()) {
					if (newQuestion.getRelatedId().contains(answer.getId().toString())) {
						numPassed++;
						break;
					}
					else {
						System.out.println("New answer was not updated");
						numFailed++;
					}
				}
				else {
					System.out.println("Could not get the same question");
					numFailed++;
				}
				
			} catch(SQLException e) {
				System.out.println("Database exception" + e.getMessage());
				numFailed++;
			}
			
			break;
		}
		return;

	}

	private static void displayResults() {
		
		System.out.println("\n\nTesting has successfully completed. The results are...\n\n");
		
		System.out.println("The number of tests that passed is: 	" + numPassed + "\n\n");
		
		System.out.println("The number of tests that failed is: 	" + numFailed + "\n\n");
		
		System.out.println("Thank you");
	}

}
