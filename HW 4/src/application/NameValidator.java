package application;

/**
 * The {@code NameValidator} class provides functionality to validate names using a
 * Finite State Machine (FSM) approach. The FSM checks that a name starts with an uppercase letter (A-Z),
 * an apostrophe ('), or a minus (-), and that subsequent characters are lowercase letters (a-z),
 * apostrophes, or minuses. The validation ensures the name is non-empty, does not exceed 50 characters,
 * and contains only allowed characters.
 * <p>
 * Copyright: Lynn Robert Carter © 2024
 * </p>
 * 
 * @author Lynn Robert Carter
 */
public class NameValidator {
    /**
     * The error message text generated during validation, if any error occurs.
     */
    public static String nameRecognizerErrorMessage = "";
    
    /**
     * The input string being processed by the validator.
     */
    public static String nameRecognizerInput = "";
    
    /**
     * The index of the character where an error was detected (or -1 if no error).
     */
    public static int nameRecognizerIndexofError = -1;
    
    /**
     * The current state value of the FSM.
     */
    private static int state = 0;
    
    /**
     * The next state value for the FSM transition.
     */
    private static int nextState = 0;
    
    /**
     * Flag indicating whether the current state is a final (accepting) state.
     */
    private static boolean finalState = false;
    
    /**
     * The entire input string being processed by the FSM.
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
     * Flag that specifies if the FSM is currently running.
     */
    private static boolean running;
    
    /**
     * Counter for the number of valid characters processed in the name.
     * The maximum allowed number is 50.
     */
    private static int nameSize = 0;

    /**
     * Displays debugging information for the current state of the FSM.
     * <p>
     * The method prints details including the current state, whether it is a final state,
     * the current character (if any), the next state, and the number of characters processed.
     * </p>
     */
    private static void displayDebuggingInfo() {
        if (currentCharNdx >= inputLine.length())
            // Display state with no current character information
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "None");
        else
            System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
                    + ((finalState) ? "       F   " : "           ") + "  " + currentChar + " "
                    + ((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + nextState
                    + "     " + nameSize);
    }

    /**
     * Advances the FSM to the next character in the input string.
     * <p>
     * Increments the character index and updates the current character.
     * If the end of the input is reached, the current character is set to a blank and the FSM stops.
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
     * Validates the given name using a Finite State Machine (FSM) approach.
     * <p>
     * The FSM starts in an initial state and processes the input character by character,
     * transitioning between states based on allowed characters. If the entire string is
     * successfully processed and meets the size requirements, an empty string is returned.
     * Otherwise, a detailed error message is returned indicating the reason for failure.
     * </p>
     *
     * @param input the input string representing the name to validate
     * @return an empty string if the name is valid; otherwise, a string with an error description
     */
    public static String checkForValidName(String input) {
        // Check that input is not empty.
        if (input.length() <= 0) {
            nameRecognizerIndexofError = 0; // Error at the first character.
            return "*** ERROR *** The name field is empty!!";
        }

        // Initialize FSM variables.
        state = 0; // Set initial state.
        inputLine = input; // Store input.
        currentCharNdx = 0; // Reset character index.
        currentChar = input.charAt(0); // Get the first character.

        nameRecognizerInput = input; // Store a copy of the input.
        running = true; // Begin FSM execution.
        nextState = -1; // No next state yet.
        System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

        // Initialize the count for the name size.
        nameSize = 0;

        // Process input through FSM until halted.
        while (running) {
            switch (state) {
                case 0:
                    // In state 0, check if the first character is allowed (A-Z, apostrophe, or minus).
                    if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar == '\'') || (currentChar == '-')) {
                        nextState = 1;
                        nameSize++;
                    } else {
                        running = false;
                    }
                    break;

                case 1:
                    // In state 1, allow a-z, apostrophe, or minus.
                    if ((currentChar >= 'a' && currentChar <= 'z') || (currentChar == '\'') || (currentChar == '-')) {
                        nextState = 1;
                        nameSize++;
                    } else {
                        running = false;
                    }
                    // Stop if the name length exceeds 50 characters.
                    if (nameSize > 50)
                        running = false;
                    break;
            }

            if (running) {
                displayDebuggingInfo();
                moveToNextCharacter();
                state = nextState;
                // Mark state as final if it is state 1.
                if (state == 1)
                    finalState = true;
                nextState = -1;
            }
        }
        displayDebuggingInfo();

        System.out.println("The loop has ended.");

        // Determine the error message based on the final state and input consumption.
        nameRecognizerIndexofError = currentCharNdx;
        nameRecognizerErrorMessage = "*** ERROR *** ";

        switch (state) {
            case 0:
                // Error: invalid starting character.
                nameRecognizerErrorMessage += "A Name must start with A-Z, an apostrophe or a minus.\n";
                return nameRecognizerErrorMessage;

            case 1:
                // Final state: verify the overall length and that no extra characters remain.
                if (nameSize < 1) {
                    nameRecognizerErrorMessage += "A Name must have at least 1 character.\n";
                    return nameRecognizerErrorMessage;
                } else if (nameSize > 50) {
                    nameRecognizerErrorMessage += "A Name must have no more than 50 characters.\n";
                    return nameRecognizerErrorMessage;
                } else if (currentCharNdx < input.length()) {
                    nameRecognizerErrorMessage += "After the first character, a Name may only contain the characters a-z, an apostrophe or a minus.\n";
                    return nameRecognizerErrorMessage;
                } else {
                    // Valid name.
                    nameRecognizerIndexofError = -1;
                    nameRecognizerErrorMessage = "";
                    return nameRecognizerErrorMessage;
                }

            default:
                // Fallback for any undefined state.
                nameRecognizerErrorMessage += "State outside of valid range.";
                return nameRecognizerErrorMessage;
        }
    }
}
