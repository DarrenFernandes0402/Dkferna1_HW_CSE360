package application;

public class EmailValidator {
	/**
	 * Copyright: Lynn Robert Carter © 2024
	 * 
	 * @author Lynn Robert Carter
	 **/

	public static String emailRecognizerErrorMessage = ""; // The error message text
	public static String emailRecognizerInput = ""; // The input being processed
	public static int emailRecognizerIndexofError = -1; // The index of error location
	private static int state = 0; // The current state value
	private static int nextState = 0; // The next state value
	private static boolean finalState = false; // Is this state a final state?
	private static String inputLine = ""; // The input line
	private static char currentChar; // The current character in the line
	private static int currentCharNdx; // The index of the current character
	private static boolean running; // The flag that specifies if the FSM is
									// running
	private static int nameSize = 0; // A numeric value may not exceed 50 characters

	// Private method to display debugging data
	private static void displayDebuggingInfo() {
		// Display the current state of the FSM as part of an execution trace
		if (currentCharNdx >= inputLine.length())
			// display the line with the current state numbers aligned
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
					+ ((finalState) ? "       F   " : "           ") + "None");
		else
			System.out.println(((state > 99) ? " " : (state > 9) ? "  " : "   ") + state
					+ ((finalState) ? "       F   " : "           ") + "  " + currentChar + " "
					+ ((nextState > 99) ? "" : (nextState > 9) || (nextState == -1) ? "   " : "    ") + nextState
					+ "     " + nameSize);
	}

	// Private method to move to the next character within the limits of the input
	// line
	private static void moveToNextCharacter() {
		currentCharNdx++;
		if (currentCharNdx < inputLine.length()) {
			currentChar = inputLine.charAt(currentCharNdx);
		} else {
			currentChar = ' ';
			running = false;
		}
	}

	/**********
	 * This method is a mechanical transformation of a Finite State Machine diagram
	 * into a Java method.
	 * 
	 * @param input The input string for the Finite State Machine
	 * @return An output string that is empty if every things is okay or it is a
	 *         String with a helpful description of the error
	 */
	public static String checkForValidEmail(String input) {
		// Check to ensure that there is input to process
		if (input.length() <= 0) {
			emailRecognizerIndexofError = 0; // Error at first character;
			return "*** ERROR *** The name is empty!!";
		}

		// The local variables used to perform the Finite State Machine simulation
		state = 0; // This is the FSM state number
		inputLine = input; // Save the reference to the input line as a global
		currentCharNdx = 0; // The index of the current character
		currentChar = input.charAt(currentCharNdx); // The current character from above indexed position
		//nextChar = input.charAt(nextCharIndex);
		// The Finite State Machines continues until the end of the input is reached or
		// at some
		// state the current character does not match any valid transition to a next
		// state

		emailRecognizerInput = input; // Save a copy of the input
		running = true; // Start the loop
		nextState = -1; // There is no next state
		System.out.println("\nCurrent Final Input  Next  Date\nState   State Char  State  Size");

		// This is the place where semantic actions for a transition to the initial
		// state occur

		nameSize = 0; // Initialize the Name size

		// The Finite State Machines continues until the end of the input is reached or
		// at some
		// state the current character does not match any valid transition to a next
		// state
		while (running) {
			// The switch statement takes the execution to the code for the current state,
			// where
			// that code sees whether or not the current character is valid to transition to
			// a
			// next state
			switch (state) {
			case 0:
				// State 0 has 1 valid transition that is addressed by an if statement.

				// The current character is checked against A-Z, ', -. If any are matched
				// the FSM goes to state 1

				// A-Z, ', - -> State 1
				if (currentChar == '@' && nameSize > 0 && nameSize < 65) {
					nextState = 1;
					// Count the character
					nameSize++;
				}

				else if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z')
						|| (currentChar >= '0') && (currentChar <= '9') || ("!#$%&'*+-/=?^_`{|}~.".indexOf(currentChar) != -1)) { // Check for A-Z, ', -
					nextState = 0;
					// Count the character
					nameSize++;

					// This only occurs once, so there is no need to check for the size getting
					// too large.
				}
				// If it is none of those characters, the FSM halts
				else {
					running = false;
				}

				// The execution of this state is finished
				break;

			case 1:
				if ((currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z')
						|| (currentChar >= '0') && (currentChar <= '9') || (currentChar == '.')) {

					nextState = 1;
					nameSize++;

				}
				// If it is none of those characters, the FSM halts
				else
					running = false;

				// The execution of this state is finished
				// If the size is larger than 50, the loop must stop
				if (nameSize > 50)
					running = false;
				break;
				
				
				
			default:
				running = false;
				break;
				
			}

			if (running) {
				displayDebuggingInfo();
				// When the processing of a state has finished, the FSM proceeds to the next
				// character in the input and if there is one, it fetches that character and
				// updates the currentChar. If there is no next character the currentChar is
				// set to a blank.
				moveToNextCharacter();

				// Move to the next state
				state = nextState;

				// Is the new state a final state? If so, signal this fact.
				if (state == 2)
					finalState = true;

				// Ensure that one of the cases sets this to a valid value
				nextState = -1;
			}
			// Should the FSM get here, the loop starts again

		}
		displayDebuggingInfo();

		System.out.println("The loop has ended.");

		// When the FSM halts, we must determine if the situation is an error or not.
		// That depends
		// of the current state of the FSM and whether or not the whole string has been
		// consumed.
		// This switch directs the execution to separate code for each of the FSM states
		// and that
		// makes it possible for this code to display a very specific error message to
		// improve the
		// user experience.
		emailRecognizerIndexofError = currentCharNdx; // Set index of a possible error;
		emailRecognizerErrorMessage = "*** ERROR *** ";

		// The following code is a slight variation to support just console output.
		switch (state) {
		case 0:
			// State 0 is not a final state, so we can return a very specific error message
			System.out.println("name size: " + nameSize);
			if (nameSize < 2) {
				// Name is too small
				emailRecognizerErrorMessage += "The email is not long enough to be valid.\n";
				return emailRecognizerErrorMessage;
			} else if (nameSize > 64){
				emailRecognizerErrorMessage += "The email local part (before the @) must be less than 65 characters.\n";
				return emailRecognizerErrorMessage;
			}
			else 
			emailRecognizerErrorMessage += "An email may only contain A-Z, a-z, 0-9 or special characters before the @. It must also contain an @ before the domain.\n";
			return emailRecognizerErrorMessage;

		case 1:
			// State 1 is a final state. Check to see if the Name length is valid. If so
			// we
			// we must ensure the whole string has been consumed.
			System.out.println("name size: " + nameSize);
			if (nameSize < 2) {
				// Name is too small
				emailRecognizerErrorMessage += "The email is not long enough to be valid.\n";
				return emailRecognizerErrorMessage;
			} else if (nameSize > 50) {
				// Name is too long
				emailRecognizerErrorMessage += "An email must have no more than 50 characters.\n";
				return emailRecognizerErrorMessage;
			} else if (currentCharNdx < input.length()) {
				// There are characters remaining in the input, so the input is not valid
				emailRecognizerErrorMessage += "An email may only contain the characters A-Z, a-z, a @ or a period.\n";
				return emailRecognizerErrorMessage;
			} else {
				// Name is valid
				emailRecognizerIndexofError = -1;
				emailRecognizerErrorMessage = "";
				return emailRecognizerErrorMessage;
			}

		default:
			// This is for the case where we have a state that is outside of the valid
			// range.
			// This should not happen
			emailRecognizerErrorMessage += "State outside of valid range.";
			return emailRecognizerErrorMessage;
		}
	}
}
