package ac.artemis.core.v4.utils.lists;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class UnfinishedFeatureException extends RuntimeException {
    public UnfinishedFeatureException(String s) {
        super("Feature " + s + " is not finished. Please do not use it for the time being.");
    }
}
