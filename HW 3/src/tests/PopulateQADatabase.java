package tests;

import java.sql.SQLException;

import application.Answer;
import application.Question;
import databasePart1.QAHelper1;

public class PopulateQADatabase {
	private final QAHelper1 qaHelper;
	private Question question;
	private Answer answer;

	public PopulateQADatabase(QAHelper1 qaHelper) {
		this.qaHelper = qaHelper;
	}

	public void execute() {

		System.out.println("This process will populate the question, answer, and relation databases.\n");

		// Populate Question 1
		question = new Question("User Stories for HW2",
				"Where are the user stories for HW2 located? Are they the same ones we were working on for TP1?", 1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 1");
			e.printStackTrace();
		}

		// Populate Answer 1
		answer = new Answer(
				"There is just one \"Student Question and Answer System - Initial User Stories\" for now.  You find them in the module entitled: \"Individual Homeworks and Team Projects\".",
				404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 1); // Register answer in relation to question id 1 for the
															// relation table

			answer = qaHelper.getAnswer(answer.getText());
			qaHelper.setPreferredAnswer(1, answer.getId()); // Sets preferred answer for question id 1
			
		} catch (SQLException e) {
			System.out.println("Error registering/setting preferred answer 1");
			e.printStackTrace();
		}

		// Populate Question 2
		question = new Question("Question regarding deletion",
				"Let me preface this with the fact that I understand D stands for delete. However, none of the stories listed on canvas include any mention of deletion, CRU operations. If we are expected to create a subset of these user stories, where does the deletion come from? Are we supposed to write our own user stories for deletion - violating the subset operation?",
				1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 2");
			e.printStackTrace();
		}

		// Populate Answer 2
		answer = new Answer(
				"What did we say about the requirements?  They change, they can be wrong, and there can be holes.  There can be conflicting requirements.\r\n"
						+ "\r\n" + "Can you delete questions and relies in Ed Discussions?",
				404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 2); // Register answer in relation to question id 2 for the
															// relation table
			answer = qaHelper.getAnswer(answer.getText());
			qaHelper.setPreferredAnswer(2, answer.getId()); // Sets preferred answer for question id 2
			
		} catch (SQLException e) {
			System.out.println("Error registering answer 2");
			e.printStackTrace();
		}

		// Populate Question 3
		question = new Question("How many UML classes do we need to make HW2?",
				"How many UML diagrams do we need to make for HW2, it is one per class right? So four in total?", 1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 3");
			e.printStackTrace();
		}

		// Populate Answer 3
		answer = new Answer("Makes sense to me.", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 3); // Register answer in relation to question id 3 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 3");
			e.printStackTrace();
		}

		// Populate Question 4
		question = new Question("Use HW1 files or create new files from scratch?",
				"Are we copying every file from HW1 and creating a new folder called 'HW2'? If not, then how are we going to create a new \"user interface\" allowing for questions to be asked and then responses to those answers?",
				1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 4");
			e.printStackTrace();
		}

		// Populate Answer 4
		answer = new Answer(
				"You do not need much of HW1 for HW2.  Copy what you believe is important, but read the HW2 requirements carefully.  HW2 is about getting four classes defined and basic CRUD operations implemented to set the stage for what you will need to do in Team Project Phase 2.",
				404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 4); // Register answer in relation to question id 4 for the
															// relation table
			answer = qaHelper.getAnswer(answer.getText());
			qaHelper.setPreferredAnswer(4, answer.getId()); // Sets preferred answer for question id 4
			
		} catch (SQLException e) {
			System.out.println("Error registering answer 4");
			e.printStackTrace();
		}

		// Populate Question 5
		question = new Question("Live Events to the beginning of the week", "Hi all, \r\n" + "\r\n"
				+ "Would it be possible to move Live Events to Monday or Tuesday for online students?\r\n" + "\r\n"
				+ "By having the Live Events on later in the week (after Wednesday), it makes it hard to get an opportunity to discuss with the professors/get more information before the soft deadline. \r\n"
				+ "\r\n"
				+ "Please like if moving Live Events to the beginning of the week would help you as an online student! \r\n"
				+ "\r\n" + "Thank you, \r\n" + "\r\n" + "Bob", 808);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 5");
			e.printStackTrace();
		}

		// Populate Answer 5
		answer = new Answer("The live event is Tuesday at 7 PM MST.", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 5); // Register answer in relation to question id 5 for the
															// relation table
			answer = qaHelper.getAnswer(answer.getText());
			qaHelper.setPreferredAnswer(5, answer.getId()); // Sets preferred answer for question id 5
			
		} catch (SQLException e) {
			System.out.println("Error registering answer 5");
			e.printStackTrace();
		}

		// Populate Question 6
		question = new Question("Hw 2- Input validation Idea",
				"I just wanted to double check as well as get other students opinions on this but is limiting the amount of characters a question/answer can be considered as a good input validation? I was thinking back in hw 1 when the password had to be a certain length and thought that it would be a good implementation of that concept since it seems like discussion forums such as ed discussion doesn't seem to have much of a limit on what you can type in. I feel that limiting the amount of characters would allow for students to write their answers/ questions in a concise way but is this too limiting and would it cause frustration for the potential users? ",
				1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 6");
			e.printStackTrace();
		}

		// Populate Answer 6
		answer = new Answer(
				"What happens with Ed Discussion of you try to paste a gigabyte of data into an input field?  Google Drives have upper limits.",
				404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 6); // Register answer in relation to question id 6 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 6");
			e.printStackTrace();
		}

		// Populate Question 7
		question = new Question("HW2 Is Astah required?",
				"Is Astah required for HW2 or can we use something like draw.io instead?", 1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 7");
			e.printStackTrace();
		}

		// Populate Answer 7
		answer = new Answer("Does the word \"Astah\" appear in the assignment?", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 7); // Register answer in relation to question id 7 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 7");
			e.printStackTrace();
		}

		// Populate Question 8
		question = new Question("Understanding of HW2", "Hi all, \r\n" + "\r\n"
				+ "Here is my understanding of HW2 from the Individual Homework 2 document on Canvas. \r\n" + "\r\n"
				+ "We are supposed to create a standalone program that can do CRUD operations and input validation for a question and answer system. You may implement this from Foundation Code, HW1, or your TP1 code. You pick a subset of user stories from \"Student\" that you think best cover what is required for CRUD/input validation - it's vague because it is up to your best judgement. \r\n"
				+ "\r\n"
				+ "CRUD stands for Create, Read, Update, Delete. Input validation is how you make sure your database is secure when allowing users to enter things directly into your database. If you're taking 365, think SQL injection and all the things that can go wrong there.\r\n"
				+ "\r\n"
				+ "Note that it is standalone. I don't think this means it isn't related to HW1 or our team projects -- Instead, I think it means it is a more simple adaptation, requiring only enough to get these basic functionalities in place and not worry about roles and their effect on what a user can do. It is to practice on a smaller scale and get a working idea of what we will implement, before we actually implement it into the team project. \r\n"
				+ "\r\n"
				+ "You should think like this too! It's just a mini version of the Q&A system we are developing.\r\n"
				+ "\r\n" + "In the document it says to create:\r\n" + "\r\n" + "a question class\r\n" + "\r\n"
				+ "an answer class\r\n" + "\r\n"
				+ "a questions class that supports storing all current questions as well as any subset of the questions (e.g., a subset reflecting the results of a search)\r\n"
				+ "\r\n"
				+ "an answers class that supports storing all potential answers to all stored questions and any subset of potential answers to a question (e.g., a subset reflecting the results of a search)\r\n"
				+ "\r\n" + "Lets look a little deeper here. \r\n" + "\r\n"
				+ "The question class represents a single question and all of the data associated with it (Think: how are questions displayed on EdDiscussion? What information do they have?) Same with the answer class, but for answers to questions. Think about that relationship.\r\n"
				+ "\r\n"
				+ "What about the last two? (TAs please correct me if I'm wrong) I think these are to manage a collection of questions/answers, maybe like a category of questions (on EdDiscussion, \"Assignments\" questions vs \"Exams\" questions) or answers related to a question (q: \"What is the meaning of life?\", answers: \"42\", \"Playing with my dog\", \"Making an impact\" (where the question and answer classes are just simple strings). What might that data look like? How might we interact with it? Remember we can have multiple collections, collections that are empty, have a few entries, or that 1000s of entries. Do some research on collections of data like this, it'll help a lot down the line.\r\n"
				+ "\r\n"
				+ "We are urged to discuss \"potential attributes\" for these classes with each other on EdDiscussion. Again, this is vague because it is up to you! Talk to others about these and learn from others' previous experiences.\r\n"
				+ "\r\n"
				+ "Documentation and automated testing is still important. Use JavaDoc comments for documentation, they're pretty cool. Automated testing is like in TP1 still. No need to worry about JUnit, if you don't want to. \r\n"
				+ "\r\n"
				+ "Read the deliverables and note theres a lot planning, documentation, diagrams, and testing in the deliverable PDF. And finally a screencast to explain and show off your work. K.I.S.S. (Keep it simple silly) method for the screencast, so you don't bore your grader :P \r\n"
				+ "\r\n" + "I think I got everything!\r\n" + "\r\n" + "So, to recap:\r\n" + "\r\n"
				+ "Create a standalone, mini question and answer system based off of a subset of the student user stories that best cover CRUD operations and input validation. \r\n"
				+ "\r\n" + "It can be started from Foundation Code, HW1, or TP1. \r\n" + "\r\n"
				+ "Make your own separate Github repository for this homework. Don't fork - If you fork, it will make the repository public and we don't want that! Better to download as a zip, open it, and initialize a new repository from it.\r\n"
				+ "\r\n" + "You need to code 4 new classes and write database operations for them (CRUD).\r\n" + "\r\n"
				+ "Their inputs should be validated to prevent saving bad data into our DB.\r\n" + "\r\n"
				+ "If you're Staff and notice something is wrong with my interpretation, please let me know!", 1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 8");
			e.printStackTrace();
		}

		// Populate Answer 8
		answer = new Answer("Very nice review!  Thank you.\r\n" + "\r\n"
				+ "Focus on the deliverables.  If it does not specify something, it is not required.\r\n" + "\r\n"
				+ "The goal is for you to create the four classes, the methods to make them work, the validation to ensure they are not given invalid data, and a set of tests to show they are working properly.\r\n"
				+ "\r\n" + "Right?", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 8); // Register answer in relation to question id 8 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 8");
			e.printStackTrace();
		}

		// Populate Question 9
		question = new Question("Document not showing on HW2", "Hi all, \r\n" + "\r\n"
				+ "I'm having trouble viewing the Architecture and Design Documents.png file on Individual Homework 2: https://canvas.asu.edu/courses/210566/assignments/5854806?module_item_id=15430181\r\n"
				+ "\r\n" + "Is it just my canvas or do others see this too?", 1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 9");
			e.printStackTrace();
		}

		// Populate Answer 9
		answer = new Answer("Can you see it now?", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 9); // Register answer in relation to question id 9 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 9");
			e.printStackTrace();
		}

		// Populate Question 10
		question = new Question("HW2",
				"For this assignment, it says you should make a standalone application. Does this mean that we cannot build upon homework 1? So we should make a completely different application without the login and admin and new users user stories?",
				1337);
		try {
			qaHelper.registerQuestion(question);
		} catch (SQLException e) {
			System.out.println("Error registering question 10");
			e.printStackTrace();
		}

		// Populate Answer 10
		answer = new Answer(
				"You may use parts of HW1 to produce HW2 if you wish, but HW2 is not a logical continuation of HW1.  You are not required to produce a user friendly user interface.  You are not required to implement roles.  Read the assignment deliverables carefully.",
				404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 10); // Register answer in relation to question id 10 for the
																// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 10");
			e.printStackTrace();
		}

		// Add additional Answer 11
		answer = new Answer("Thats a great response!!!", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 2); // Register answer in relation to question id 2 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 11");
			e.printStackTrace();
		}

		// Add additional Answer 12
		answer = new Answer("Thank you so much!!!", 1337);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 2); // Register answer in relation to question id 2 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 12");
			e.printStackTrace();
		}

		// Add additional Answer 13
		answer = new Answer("Woweeeeeeee", 1337);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 4); // Register answer in relation to question id 4 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 13");
			e.printStackTrace();
		}

		// Add additional Answer 14
		answer = new Answer("Well that raises some interesting questions.", 404);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 5); // Register answer in relation to question id 5 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 14");
			e.printStackTrace();
		}

		// Add additional Answer 15
		answer = new Answer("Oh well", 1919);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 9); // Register answer in relation to question id 9 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 15");
			e.printStackTrace();
		}

		// Add additional Answer 16
		answer = new Answer("Oh well another test happened", 1);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 1); // Register answer in relation to question id 9 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 16");
			e.printStackTrace();
		}

		// Add additional Answer 17
		answer = new Answer("Another one bites the dust.... ", 1);
		try {
			qaHelper.registerAnswerWithQuestion(answer, 2); // Register answer in relation to question id 9 for the
															// relation table
		} catch (SQLException e) {
			System.out.println("Error registering answer 17");
			e.printStackTrace();
		}

		// Add additional Question 11
		question = new Question("Student Can produce a new question",
				"My group is working on Team project two and we were wonder if for \"can produce a new question based on a previous question to address the feedback I have received and any new insights gained, so I am more likely to get an answer that resolves my issue.\" we need to make a follow up button?",
				1);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 18 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 18");
			e.printStackTrace();
		}

		// Add additional Question 12
		question = new Question("Question on TP2",
				"Are we supposed to implement the questions and answers into a JavaFX gui, or can it be in the console.\r\n"
						+ "\r\n"
						+ "I understand that we'll still need to show the testing the automated testing for the tp1 and the gui elements for the login pages to make sure the integration didn't break anything.",
				1);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 19 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 19");
			e.printStackTrace();
		}

		// Add additional Question 13
		question = new Question("Quiz 4 Answer Feedback",
				"I didn't have too many complaints about the first 3 quizzes, I thought they were straightforward enough and backed up by both the lectures and the textbook, but I am  looking at the quiz 4 answers now that they are released and think we are getting off track with the big picture here and getting too specific with what is being asked here:\r\n"
						+ "\r\n"
						+ "1. Consider the following software project requirement and then select the best process model to develop this software.\r\n"
						+ "\r\n"
						+ "A software development team is tasked with creating a complex and technical mobile application for a company. The client expects high-quality software that is free of defects. The team consists of experienced developers who are committed to continuous improvement and quality.\r\n"
						+ "\r\n"
						+ "I am assuming the answer here is XP Process model simply because it uses the key phrase 'high-quality' and 'continuous improvement', which seems a little specific, but I guess that is ok with me, at least compared to the next two.\r\n"
						+ "\r\n"
						+ "2. Consider the following software project requirements and then select the best process model to develop this software.\r\n"
						+ "\r\n"
						+ "A software development team is tasked with building a web application with multiple modules. The client expects to have a working version of the software as early as possible to gather feedback and make changes based on it.\r\n"
						+ "\r\n"
						+ "Here the answer is incremental process model. Why not XP? XP is 1. also an incremental model 2. committed to releasing versions quickly, I think even faster than any other model, and 3. gets feedback from an on-site customer. Seems like it fits the description just as well if not better than just incremental\r\n"
						+ "\r\n"
						+ "3. Which of the following are incremental process models (select all that apply)\r\n"
						+ "\r\n"
						+ "Here one of the correct choices is RAD (Rapid Application Development). Is this even in the textbook chapter? Is it in the slides? If I am wrong on this one sure, but at least tell me where to find it. I don't recall it being a major or minor topic of discussion.\r\n"
						+ "\r\n"
						+ "Last Note: If waterfall and incremental are going to be both specific models, but also classes or categories of models, I think this should be specified more when discussing them.",
				2);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 20 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 20");
			e.printStackTrace();
		}

		// Add additional Question 14
		question = new Question("Standup Meetings ALL Team Members Required?",
				"In order for my team to meet up 6 different times, there will have to be meetings where not all members can attend. Is it okay if 5/6 or even 4/6 members can not attend a meeting? Luckily we are able to have it so that each team member only misses one meeting rather than one member missing all 6. ",
				3);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 21 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 21");
			e.printStackTrace();
		}

		// Add additional Question 15
		question = new Question("Standup Meetings for TP2",
				"If we submit team phase 2 a week early, would it be fine if we only have 4 standup meetings (4 meetings for 2 weeks instead of 6 in 3 weeks)? ",
				4);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 22 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 22");
			e.printStackTrace();
		}

		// Add additional Question 16
		question = new Question("Comparing HW2 solutions",
				"Can we add our teammates as collaborators to our private repositories for individual HW2 so that we can compare our solutions for Task 1 in TP2?",
				5);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 23 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 23");
			e.printStackTrace();
		}

		// Add additional Question 17
		question = new Question("TP2 Phase 2 Task 5 GitHub",
				"In the Team Project 2 template, it says to include the URLs/screencasts for the team's solution for HW2. Just wanted to clarify if this is actually referring to the team's solution for Phase 2 (which includes all implemented Student stories)?",
				6);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 24 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 24");
			e.printStackTrace();
		}

		// Add additional Question 18
		question = new Question("TP2 Phase 2 Task 1",
				"Task 1 specifies that our team needs to \"provides evidence (e.g., screencast and console output) that the code has been tested and operates as required.\" We have chosen one of our member's HW2 to work off of, but can we use the screencast that our individual team member submitted for their HW2 assignment, or do we need to re-film a screencast for HW2 that includes our entire team presenting the individual HW2 implementation? \r\n"
						+ "\r\n"
						+ "Also, Task 1.4 of the phase 2 PDF says to include a \"URL and access to Source Code in GitHub.\" Is this referring to the HW2 submission by our individual team member, or the URL of our overall TP2 Github? ",
				7);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 25 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 25");
			e.printStackTrace();
		}

		// Add additional Question 19
		question = new Question("TP2 new features clarification",
				"I have a couple questions about the new features we need to add to TP2:\r\n" + "\r\n"
						+ "\"Students to ask for or suggest clarifications.\"\r\n" + "\r\n"
						+ "Should clarifications be requested through a dedicated button/feature, or should replying to an answer automatically count as a clarification request? Similar to how EdDiscussions allows threaded replies?\r\n"
						+ "\r\n"
						+ "\"Students to announce that a specific answer addressed the issue that prompted the initial question.\"\r\n"
						+ "\r\n"
						+ "Who has permission to mark an answer as correct? The original poster, or can other users/admin do it as well?",
				8);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 26 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 26");
			e.printStackTrace();
		}

		// Add additional Question 20
		question = new Question("Quiz 4 Answers", "Are correct answers being given for past quizzes?\r\n" + "\r\n"
				+ "For quiz 4, I have questions about the first 3 and would be curious to know what the correct answers are and how people supposedly arrived at them",
				9);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 27 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 27");
			e.printStackTrace();
		}

		// Add additional Question 21
		question = new Question("HW2 Github Privacy",
				"Hi, I emailed my grader about their github. I'm worried they won't get back to me today. Is it okay to submit everything including a link to the repository, and give them access as soon as they get back to me which may be after friday 11:59? I have everything else done.",
				11);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 28 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 28");
			e.printStackTrace();
		}

		// Add additional Question 22
		question = new Question("HW 2 GitHub Files",
				"I just realized that on my GitHub I uploaded class files instead of manually creating java files. I turned my HW2 in on time but am worried on if it's accessible, and if it isn't, am I able to change it so that I can receive credit. I also want to do this without turning it into a late submission.",
				10);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 29 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 29");
			e.printStackTrace();
		}

		// Add additional Question 23
		question = new Question("Regarding some of the Student User Stories", "Hello there!\r\n" + "\r\n"
				+ "I am posting this  regarding some confusion about the student user stories to be implemented. Some of the user stories have a reviewer feature like the ones below:\r\n"
				+ "\r\n"
				+ "As a student, I can read the reviews of potential answers to questions and add promising reviewers to my potential trusted reviewers list so I can establish and manage a list of trusted reviewers.\r\n"
				+ "\r\n"
				+ "As a student, I can establish and manage a list of reviewers I trust and assign each a weight, so my curated list of answers serves me well.\r\n"
				+ "\r\n"
				+ "As a student, I can see when a reviewer in my list of trusted reviewers updates a review, so I can quickly benefit from the update.\r\n"
				+ "\r\n"
				+ "As a student, I can provide private feedback to a reviewer of a potential answer so the reviewer can provide better reviews.\r\n"
				+ "\r\n"
				+ "As a student, I can request to be given the role of a reviewer. My classmates can benefit from my experience and reduce the number of potential answers they need to read. An instructor must approve this request, and the role is assigned upon approval.\r\n"
				+ "\r\n"
				+ " As a student, I can search to find answered questions, currently unanswered questions, and reviewers so I can more quickly get my questions answered and create an effective set of reviewers to curate the results of my searches. (Epic)\r\n"
				+ "\r\n" + "As this was mentioned in the assignment: \r\n" + "\r\n"
				+ "Do not include stories that involve users playing the role of reviewers, instructors, or staff members. You do not need to fully implement the provided Student User Stories in this phase. I apologize for the amount of text I had to include as I just wanted to be clear on what to implement for this phase as we woulld have been done with most of the work already.",
				12);
		try {
			qaHelper.registerQuestion(question); // Register answer in relation to question id 30 for the
													// relation table
		} catch (SQLException e) {
			System.out.println("Error registering question 30");
			e.printStackTrace();
		}
	}

}
