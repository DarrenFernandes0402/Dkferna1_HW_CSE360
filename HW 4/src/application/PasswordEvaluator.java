package application;

/**
 * The {@code PasswordEvaluator} class provides functionality to validate a password
 * using a finite state machine (FSM) approach implemented as a directed graph.
 * <p>
 * The evaluator checks that the password contains at least one uppercase letter,
 * one lowercase letter, one numeric digit, one special character, and is at least 8 characters long.
 * If any requirement is not met, an error message is returned indicating which criteria were not satisfied.
 * </p>
 * <p>
 * Copyright: Lynn Robert Carter © 2024
 * </p>
 * 
 * @author Lynn Robert Carter
 */
public class PasswordEvaluator {
    
    /**
     * The error message generated during password validation.
     */
    public static String passwordErrorMessage = "";
    
    /**
     * The input password string being processed.
     */
    public static String passwordInput = "";
    
    /**
     * The index in the password where an error was detected (or -1 if no error).
     */
    public static int passwordIndexofError = -1;
    
    /**
     * Flag indicating if an uppercase letter was found in the password.
     */
    public static boolean foundUpperCase = false;
    
    /**
     * Flag indicating if a lowercase letter was found in the password.
     */
    public static boolean foundLowerCase = false;
    
    /**
     * Flag indicating if a numeric digit was found in the password.
     */
    public static boolean foundNumericDigit = false;
    
    /**
     * Flag indicating if a special character was found in the password.
     */
    public static boolean foundSpecialChar = false;
    
    /**
     * Flag indicating if the password is long enough (at least 8 characters).
     */
    public static boolean foundLongEnough = false;
    
    /**
     * The entire input password string.
     */
    private static String inputLine = "";
    
    /**
     * The current character being processed.
     */
    private static char currentChar;
    
    /**
     * The index of the current character in the input password.
     */
    private static int currentCharNdx;
    
    /**
     * Flag indicating whether the FSM is still running.
     */
    private static boolean running;
    
    /**
     * Counter for the length of the password processed so far.
     */
    private static int passwordSize; // Not used directly for validation, but can be useful for debugging.

    /**
     * Displays the current input state to the console.
     * <p>
     * This method prints the entire input line, then prints a line with a marker ("?")
     * at the position of the current character (indicating where an error may have been found).
     * It also prints the total password size, the current index, and the current character.
     * </p>
     */
    private static void displayInputState() {
        // Display the entire input line.
        System.out.println(inputLine);
        // Display a marker at the current character position.
        System.out.println(inputLine.substring(0, currentCharNdx) + "?");
        // Print debugging information about the password.
        System.out.println("The password size: " + inputLine.length() + "  |  The currentCharNdx: " + currentCharNdx
                + "  |  The currentChar: \"" + currentChar + "\"");
    }

    /**
     * Evaluates the input password and returns an empty string if the password meets
     * all the required criteria, or an error message detailing the missing criteria.
     * <p>
     * The method uses a directed graph (FSM) approach to process the password character by character.
     * It checks for uppercase letters, lowercase letters, numeric digits, and special characters,
     * and ensures that the password is at least 8 characters long.
     * </p>
     *
     * @param input The input password string to evaluate.
     * @return An empty string if the password is valid, or an error message describing the missing criteria.
     */
    public static String evaluatePassword(String input) {
        // Reset error message and index.
        passwordErrorMessage = "";
        passwordIndexofError = 0;
        // Store input for processing.
        inputLine = input;
        currentCharNdx = 0;

        // Check for an empty password.
        if (input.length() <= 0)
            return "*** ERROR *** The password is empty!";

        // Set up the first character for processing.
        currentChar = input.charAt(0);

        // Initialize flags and store a copy of the input.
        passwordInput = input;
        foundUpperCase = false;
        foundLowerCase = false;
        foundNumericDigit = false;
        foundSpecialChar = false;
        foundLongEnough = false;
        running = true;

        // Process each character until the end of the input is reached.
        while (running) {
            displayInputState();
            // Check for uppercase letters.
            if (currentChar >= 'A' && currentChar <= 'Z') {
                System.out.println("Upper case letter found");
                foundUpperCase = true;
            }
            // Check for lowercase letters.
            else if (currentChar >= 'a' && currentChar <= 'z') {
                System.out.println("Lower case letter found");
                foundLowerCase = true;
            }
            // Check for numeric digits.
            else if (currentChar >= '0' && currentChar <= '9') {
                System.out.println("Digit found");
                foundNumericDigit = true;
            }
            // Check for special characters.
            else if ("~`!@#$%^&*()_-+{}[]|:,.?/".indexOf(currentChar) >= 0) {
                System.out.println("Special character found");
                foundSpecialChar = true;
            }
            // If the character does not match any valid category, report an error.
            else {
                passwordIndexofError = currentCharNdx;
                return "*** ERROR *** An invalid character has been found!";
            }
            // Check if at least 8 characters have been processed.
            if (currentCharNdx >= 7) { // Index 7 corresponds to the 8th character.
                System.out.println("At least 8 characters found");
                foundLongEnough = true;
            }

            // Move to the next character.
            currentCharNdx++;
            if (currentCharNdx >= inputLine.length())
                running = false;
            else
                currentChar = input.charAt(currentCharNdx);

            System.out.println();
        }
        // Build error message if criteria are missing.
        String errMessage = "";
        if (!foundUpperCase)
            errMessage += "Password must have one UPPERCASE letter; ";

        if (!foundLowerCase)
            errMessage += "Password must have one LOWERCASE letter; ";

        if (!foundNumericDigit)
            errMessage += "Password must include a NUMBER; ";

        if (!foundSpecialChar)
            errMessage += "Password must include a SPECIAL CHARACTER; ";

        if (!foundLongEnough)
            errMessage += "Password must be AT LEAST 8 CHARACTERS; ";

        if (errMessage.equals(""))
            return "";

        passwordIndexofError = currentCharNdx;
        return "*** ERROR *** " + errMessage;
    }
}
