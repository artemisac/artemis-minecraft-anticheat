package ac.artemis.core.v4.check.exceptions;

/**
 * @author Ghast
 * @since 17-Mar-20
 */
public class InvalidSettingValueCast extends RuntimeException {
    public InvalidSettingValueCast() {
        super("Value is not what you are trying to cast it for!");
    }

    public InvalidSettingValueCast(String message, String cast) {
        super("Value " + message + " is not a(n) " + cast + " !");
    }
}
