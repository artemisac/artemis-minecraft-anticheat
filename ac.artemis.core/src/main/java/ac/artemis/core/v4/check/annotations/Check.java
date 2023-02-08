package ac.artemis.core.v4.check.annotations;

import ac.artemis.anticheat.api.check.type.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Check {
    Type type() default Type.UNKNOWN;

    String var() default "A";

    String[] alias() default "Heuristic:Gen.A";

    int threshold() default 15;

    boolean enabled() default true;

    boolean bannable() default true;
}
