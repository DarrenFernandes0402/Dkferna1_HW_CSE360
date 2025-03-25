package tests;

import databasePart1.DatabaseHelper;
import application.Message;
import application.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopulateMessageDatabase {
    private final DatabaseHelper databaseHelper;
    private Message message;
    private User user; 
    
    public PopulateMessageDatabase(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void execute() {
    	
    	try {
            databaseHelper.connectToDatabase();
            user = databaseHelper.getUser("cespina3");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
            return;
        }
    	
    	
    	Message m1 = new Message(1337, 2, "Meeting Reminder", "Don't forget about our meeting at 3 PM today.");
     	Message m2 = new Message(1337, 2, "Project Update", "I have pushed the latest changes to the repository.");
     	Message m3 = new Message(6721, 2, "Bug Found", "There is a bug in the login system. Please check it.");
        Message m4 = new Message(2894, 2, "Code Review", "Can you review my latest code submission?");
        Message m5 = new Message(5050, 2, "Feedback Request", "I would love to hear your feedback on my project.");
        Message m6 = new Message(9021, 2, "Team Coordination", "Let's sync up on our project deliverables.");
        Message m7 = new Message(7142, 2, "Issue Resolved", "The issue you reported has been resolved.");
        Message m8 =  new Message(404, 2, "System Update", "We've deployed a new system update.");
        Message m9 = new Message(404, 2, "New Feature", "Check out the new messaging feature!");
        Message m10 = new Message(404, 2, "Testing", "This is a test message for user 404.");
        Message m11 = new Message(404, 2, "Follow-up", "Following up on our previous discussion.");
        Message m12 = new Message(1, 2, "Welcome", "Welcome to the platform! Let us know if you need any help.");
        List<Message> messages = new ArrayList<>(Arrays.asList(m1,m2,m3,m4,m5,m6,m7,m9,m10,m11));
        
    	
    	
    	
        System.out.println("Populating test messages in the database.\n");
        try {
        	for (Message m : messages) {
        		databaseHelper.qaHelper.createMessage(m);
        	}
        	
        } catch (SQLException e) {
        	System.out.println("Error registering messages ");
			e.printStackTrace();
        }

        
         	
    }
    public static void main(String[] args) {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        PopulateMessageDatabase populateMessageDatabase = new PopulateMessageDatabase(databaseHelper);
        populateMessageDatabase.execute();
        System.out.println("Test messages have been populated and should be visible in the main program.");
    }
}
