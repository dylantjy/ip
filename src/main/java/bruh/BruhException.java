package bruh;

/**
 * Custom checked exception for the Bruh application.
 * <p>
 * Used to signal domain-specific errors such as invalid user input
 * or storage/processing failures.
 */
public class BruhException extends Exception {

        /**
         * Creates a new {@code BruhException} with the given detail message.
         *
         * @param message detail message explaining the cause of the error
         */
        public BruhException(String message) {
                 super(message);
         }
 }

