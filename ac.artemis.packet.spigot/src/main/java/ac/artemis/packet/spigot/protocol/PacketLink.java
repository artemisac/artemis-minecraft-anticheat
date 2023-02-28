package ac.artemis.packet.spigot.protocol;

import ac.artemis.packet.wrapper.Packet;

import javax.lang.model.element.Element;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketLink {
    Class<? extends Packet> value();

}
