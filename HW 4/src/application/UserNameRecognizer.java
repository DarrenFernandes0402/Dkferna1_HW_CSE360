package application;

/**
 * The {@code UserNameRecognizer} class validates a username using a finite state machine (FSM)
 * approach. The FSM ensures that the username starts with an alphabetic character (A-Z or a-z)
 * and that subsequent characters are either alphanumeric or one of the following symbols: period ('.'),
 * minus ('-'), or underscore ('_'). The FSM also enforces that the username length is between 4 and 16 characters.
 * <p>
 * If the input is valid, an empty string is returned; otherwise, a detailed error message is provided.
 * </p>
 * 
 * Copyright: Lynn Robert Carter © 2024
 * 
 * @author Lynn Robert Carter
 */
public class UserNameRecognizer {
    /**
     * The error message generated during username validation.
     */
    public static String userNameRecognizerErrorMessage = "";
    
    /**
     * The username input string being processed.
     */
    public static String userNameRecognizerInput = "";
    
    /**
     * The index at which an error was detected (or -1 if no error).
     */
    public static int userNameRecognizerIndexofError = -1;
    
    /**
     * The current state of the finite state machine.
     */
    private static int state = 0;
    
    /**
     * The next state value for the FSM transition.
     */
    private static int nextState = 0;
    
    /**
     * Flag indicating if the current state is a final (accepting) state.
     */
    private static boolean finalState = false;
    
    /**
     * The entire input string being processed.
     */
    private static String inputLine = "";
    
    /**
     * The current character in the input line.
     */
    private static char currentChar;
    
    /**
     * The index of the current character in the input line.
     */
    private static int currentCharNdx;
    
    /**
     * Flag that indicates whether the FSM is currently running.
     */
    private static boolean running;
    
    /**
     * The number of characters processed so far.
     * The username length must not exceed 16 characters.
     */
    private static int userNameSize = 0;

    /**
     * Displays debugging information for the current state of the FSM.
     * <p>
     * The method prints the current state, whether it is final, the current character (if available),
     * the next state, and the number of characters processed.
     * </p>
     */
    private static void displayDebuggingInfo() {
        if (currentCharNdx >= inputLine.length())
            // Display state with no current character info
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "None");
        else
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "  " + currentChar + " "
                    + ((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + nextState
                    + "     " + userNameSize);
    }

    /**
     * Advances the FSM to the next character in the input string.
     * <p>
     * Increments the current character index and updates the current character.
     * If the end of the input is reached, sets the current character to a blank and stops the FSM.
     * </p>
     */
    private static void moveToNextCharacter() {
        currentCharNdx++;
        if (currentCharNdx < inputLine.length())
            currentChar = inputLine.charAt(currentCharNdx);
        else {
            currentChar = ' ';
            running = false;
        }
    }

    /**
     * Validates the input username using a finite state machine (FSM) approach.
     * <p>
     * The FSM enforces that the username starts with an alphabetic character (A-Z or a-z)
     * and that subsequent characters are either alphanumeric or one of the allowed symbols (period, minus, or underscore).
     * Additionally, it verifies that the username length is between 4 and 16 characters.
     * If the username is valid, an empty string is returned; otherwise, a detailed error message is provided.
     * </p>
     *
     * @param input the input string representing the username to validate
     * @return an empty string if the username is valid; otherwise, an error message describing the issue
     */
    public static String checkForValidUserName(String input) {
        // Check if input is empty.
        if (input.length() <= 0) {
            userNameRecognizerIndexofError = 0;
            return "*** ERROR *** The username is empty!!";
        }

        // Initialize FSM variables.
        state = 0;
        inputLine = input;
        currentCharNdx = 0;
        currentChar = input.charAt(0);

        userNameRecognizerInput = input;
        running = true;
        nextState = -1;
        System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

        // Initialize username size counter.
        userNameSize = 0;

        // Process the input with the FSM.
        while (running) {
            switch (state) {
                case 0:
                    // In state 0, the first character must be a letter (A-Z or a-z).
                    if ((currentChar >= 'A' && currentChar <= 'Z') ||
                        (currentChar >= 'a' && currentChar <= 'z')) {
                        nextState = 1;
                        userNameSize++;
                    } else {
                        running = false;
                    }
                    break;

                case 1:
                    // In state 1, allow alphanumeric characters; also allow period, minus, or underscore which transition to state 2.
                    if ((currentChar >= 'A' && currentChar <= 'Z') ||
                        (currentChar >= 'a' && currentChar <= 'z') ||
                        (currentChar >= '0' && currentChar <= '9')) {
                        nextState = 1;
                        userNameSize++;
                    } else if ((currentChar == '.') ||
                               (currentChar == '-') ||
                               (currentChar == '_')) {
                        nextState = 2;
                        userNameSize++;
                    } else {
                        running = false;
                    }
                    if (userNameSize > 16)
                        running = false;
                    break;

                case 2:
                    // In state 2, after a period, minus, or underscore, only alphanumeric characters are allowed.
                    if ((currentChar >= 'A' && currentChar <= 'Z') ||
                        (currentChar >= 'a' && currentChar <= 'z') ||
                        (currentChar >= '0' && currentChar <= '9')) {
                        nextState = 1;
                        userNameSize++;
                    } else {
                        running = false;
                    }
                    if (userNameSize > 16)
                        running = false;
                    break;
            }

            if (running) {
                displayDebuggingInfo();
                moveToNextCharacter();
                state = nextState;
                if (state == 1)
                    finalState = true;
                nextState = -1;
            }
        }
        displayDebuggingInfo();

        System.out.println("The loop has ended.");

        // Set the error index.
        userNameRecognizerIndexofError = currentCharNdx;
        userNameRecognizerErrorMessage = "*** ERROR *** ";

        // Determine error message based on final state.
        switch (state) {
            case 0:
                userNameRecognizerErrorMessage += "A UserName must start with A-Z or a-z.\n";
                return userNameRecognizerErrorMessage;

            case 1:
                if (userNameSize < 4) {
                    userNameRecognizerErrorMessage += "A UserName must have at least 4 characters.\n";
                    return userNameRecognizerErrorMessage;
                } else if (userNameSize > 16) {
                    userNameRecognizerErrorMessage += "A UserName must have no more than 16 characters.\n";
                    return userNameRecognizerErrorMessage;
                } else if (currentCharNdx < input.length()) {
                    userNameRecognizerErrorMessage += "A UserName character may only contain the characters A-Z, a-z, 0-9 or period, minus, or underscore.\n";
                    return userNameRecognizerErrorMessage;
                } else {
                    userNameRecognizerIndexofError = -1;
                    userNameRecognizerErrorMessage = "";
                    return userNameRecognizerErrorMessage;
                }

            case 2:
                userNameRecognizerErrorMessage += "A UserName character after a period, minus or underscore must be A-Z, a-z, or 0-9.\n";
                return userNameRecognizerErrorMessage;

            default:
                userNameRecognizerErrorMessage += "State outside of valid range.";
                return userNameRecognizerErrorMessage;
        }
    }
}
