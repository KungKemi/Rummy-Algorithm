package util;

/**
 * Exception thrown when a given playing card instance is not valid.
 */
public class IllegalCardException extends Exception {

    /**
     * Constructs a new IllegalCardException with no detail message or cause
     */
    public IllegalCardException() {
        super();
    }

    /**
     * Constructs a new IllegalCardException explaining the underlying cause
     * @param message detail message
     */
    public IllegalCardException(String message) {
        super(message);
    }
}
