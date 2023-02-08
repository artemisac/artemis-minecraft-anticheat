package ac.artemis.core.v4.check.annotations;

import ac.artemis.packet.protocol.ProtocolVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientVersion {
    ProtocolVersion[] version() default {ProtocolVersion.V1_7, ProtocolVersion.V1_7_10, ProtocolVersion.V1_8, ProtocolVersion.V1_8_5, ProtocolVersion.V1_8_9};
}
