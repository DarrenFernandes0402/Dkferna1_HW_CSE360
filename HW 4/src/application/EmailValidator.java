package application;

/**
 * The {@code EmailValidator} class provides methods to validate email addresses using a
 * Finite State Machine (FSM) approach. The FSM processes the input string character by character,
 * transitioning through states and tracking debugging information.
 * <p>
 * If the input is valid, the method returns an empty string; otherwise, it returns an error message
 * describing the validation failure.
 * </p>
 * <p>
 * Copyright: Lynn Robert Carter © 2024
 * </p>
 *
 * @author Lynn Robert Carter
 */
public class EmailValidator {
    
    /**
     * The error message text after validation.
     */
    public static String emailRecognizerErrorMessage = "";
    
    /**
     * A copy of the input string being processed.
     */
    public static String emailRecognizerInput = "";
    
    /**
     * The index where the error occurred, if any.
     */
    public static int emailRecognizerIndexofError = -1;
    
    /**
     * The current state of the FSM.
     */
    private static int state = 0;
    
    /**
     * The next state of the FSM after processing the current character.
     */
    private static int nextState = 0;
    
    /**
     * Flag indicating whether the current state is a final (accepting) state.
     */
    private static boolean finalState = false;
    
    /**
     * The input string being processed.
     */
    private static String inputLine = "";
    
    /**
     * The current character in the input string.
     */
    private static char currentChar;
    
    /**
     * The index of the current character in the input string.
     */
    private static int currentCharNdx;
    
    /**
     * Flag that specifies whether the FSM is running.
     */
    private static boolean running;
    
    /**
     * The count of characters processed in the local part of the email.
     * A numeric value may not exceed 50 characters.
     */
    private static int nameSize = 0;
    
    /**
     * Displays the debugging information for the current state of the FSM.
     * <p>
     * This method prints details such as the current state, whether it's a final state,
     * the current character being processed, the next state, and the current name size.
     * </p>
     */
    private static void displayDebuggingInfo() {
        // If the current character index exceeds the input length, display without current character info.
        if (currentCharNdx >= inputLine.length())
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "None");
        else
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "  " + currentChar + " "
                    + ((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + nextState
                    + "     " + nameSize);
    }
    
    /**
     * Moves to the next character in the input string.
     * <p>
     * This method increments the current character index and updates the current character.
     * If the end of the input is reached, it sets the current character to a blank and stops the FSM.
     * </p>
     */
    private static void moveToNextCharacter() {
        currentCharNdx++;
        if (currentCharNdx < inputLine.length()) {
            currentChar = inputLine.charAt(currentCharNdx);
        } else {
            currentChar = ' ';
            running = false;
        }
    }
    
    /**
     * Validates the given email input using a Finite State Machine (FSM) approach.
     * <p>
     * The method processes the input string character by character, transitioning between states
     * based on valid email characters. Debugging information is printed during processing.
     * </p>
     *
     * @param input the email string to validate
     * @return an empty string if the email is valid, or an error message describing the issue
     */
    public static String checkForValidEmail(String input) {
        // Check to ensure that there is input to process
        if (input.length() <= 0) {
            emailRecognizerIndexofError = 0; // Error at first character
            return "*** ERROR *** The name is empty!!";
        }

        // Initialize FSM variables.
        state = 0; // Current FSM state
        inputLine = input; // Save reference to the input line
        currentCharNdx = 0; // Set starting index
        currentChar = input.charAt(currentCharNdx); // Get the first character
        
        emailRecognizerInput = input; // Save a copy of the input
        running = true; // Start the FSM loop
        nextState = -1; // Initialize next state
        
        System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");
        
        // Initialize the name size counter.
        nameSize = 0;
        
        // Process the input using the FSM until the end of the input or an invalid transition occurs.
        while (running) {
            switch (state) {
                case 0:
                    // In state 0, check for allowed characters or transition to state 1 when '@' is encountered.
                    if (currentChar == '@' && nameSize > 0 && nameSize < 65) {
                        nextState = 1;
                        nameSize++; // Count the character
                    } else if ((currentChar >= 'A' && currentChar <= 'Z') || 
                               (currentChar >= 'a' && currentChar <= 'z') ||
                               (currentChar >= '0' && currentChar <= '9') || 
                               ("!#$%&'*+-/=?^_`{|}~.".indexOf(currentChar) != -1)) {
                        nextState = 0;
                        nameSize++; // Count the character
                    } else {
                        running = false; // Halt if the character is invalid
                    }
                    break;
    
                case 1:
                    // In state 1, only letters, digits, and periods are allowed.
                    if ((currentChar >= 'A' && currentChar <= 'Z') || 
                        (currentChar >= 'a' && currentChar <= 'z') ||
                        (currentChar >= '0' && currentChar <= '9') || 
                        (currentChar == '.')) {
                        nextState = 1;
                        nameSize++;
                    } else {
                        running = false;
                    }
                    // Ensure that the name size does not exceed 50 characters.
                    if (nameSize > 50)
                        running = false;
                    break;
    
                default:
                    running = false;
                    break;
            }
    
            if (running) {
                displayDebuggingInfo();
                moveToNextCharacter();
                state = nextState; // Transition to the next state
                
                // If the new state is the designated final state, mark it.
                if (state == 2)
                    finalState = true;
    
                // Reset next state for the next iteration.
                nextState = -1;
            }
        }
        displayDebuggingInfo();
    
        System.out.println("The loop has ended.");
    
        // Determine and construct the error message based on the final state and remaining input.
        emailRecognizerIndexofError = currentCharNdx;
        emailRecognizerErrorMessage = "*** ERROR *** ";
    
        switch (state) {
            case 0:
                // Error handling for state 0 (non-final state).
                System.out.println("name size: " + nameSize);
                if (nameSize < 2) {
                    emailRecognizerErrorMessage += "The email is not long enough to be valid.\n";
                    return emailRecognizerErrorMessage;
                } else if (nameSize > 64) {
                    emailRecognizerErrorMessage += "The email local part (before the @) must be less than 65 characters.\n";
                    return emailRecognizerErrorMessage;
                } else {
                    emailRecognizerErrorMessage += "An email may only contain A-Z, a-z, 0-9 or special characters before the @. It must also contain an @ before the domain.\n";
                    return emailRecognizerErrorMessage;
                }
    
            case 1:
                // Error handling for state 1 (final state) and additional validations.
                System.out.println("name size: " + nameSize);
                if (nameSize < 2) {
                    emailRecognizerErrorMessage += "The email is not long enough to be valid.\n";
                    return emailRecognizerErrorMessage;
                } else if (nameSize > 50) {
                    emailRecognizerErrorMessage += "An email must have no more than 50 characters.\n";
                    return emailRecognizerErrorMessage;
                } else if (currentCharNdx < input.length()) {
                    emailRecognizerErrorMessage += "An email may only contain the characters A-Z, a-z, a @ or a period.\n";
                    return emailRecognizerErrorMessage;
                } else {
                    // The email is valid.
                    emailRecognizerIndexofError = -1;
                    emailRecognizerErrorMessage = "";
                    return emailRecognizerErrorMessage;
                }
    
            default:
                // Fallback for unexpected state values.
                emailRecognizerErrorMessage += "State outside of valid range.";
                return emailRecognizerErrorMessage;
        }
    }
}
