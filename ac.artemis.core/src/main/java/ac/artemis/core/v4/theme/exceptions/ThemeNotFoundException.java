package ac.artemis.core.v4.theme.exceptions;

public class ThemeNotFoundException extends RuntimeException {
    public ThemeNotFoundException(String message) {
        super("Theme " + message + " was not found! Please make sure it is the correct name of the file!");
    }
}
