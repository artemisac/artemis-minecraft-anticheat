package ac.artemis.core.v4.check.annotations;

import ac.artemis.core.v4.check.enums.CheckSettings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Setting {
    CheckSettings type();

    String defaultValue() default "1";

    boolean released() default true;
}
