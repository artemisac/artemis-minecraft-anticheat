package ac.artemis.core.v4.check.exceptions;

/**
 * @author Ghast
 * @since 15-Oct-19
 * Ghast CC © 2019
 */
public class CheckNotFoundException extends RuntimeException {
    public CheckNotFoundException() {
        super("Check not found! Contact an administrator.");
    }
}
