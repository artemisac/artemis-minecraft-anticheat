package ac.artemis.core.v4.check.exceptions;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
public class InvalidCheckNameException extends RuntimeException {
    public InvalidCheckNameException() {
        super("No check name found");
    }

    public InvalidCheckNameException(String s) {
        super("Check name " + s + " is not a valid check name");
    }
}
