package ac.artemis.core.v4.dependency.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ghast
 * @since 10-Nov-19
 * Ghast CC © 2019
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dependency {
    /**
     * @return String value of the name to be displayed
     */
    String name();

    /**
     * @return String value of the version of the dependency
     */
    String version();

    /**
     * @return String value of the URL of the hosted dependency
     */
    String url();

    /**
     * @return Boolean value of whether or not to download the dependency
     */
    boolean download() default false;
}
