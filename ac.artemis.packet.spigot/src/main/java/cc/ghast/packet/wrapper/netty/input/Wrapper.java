package cc.ghast.packet.wrapper.netty.input;

import lombok.Data;

/**
 * @author Ghast
 * @since 18/10/2020
 * ArtemisPacket © 2020
 */

@Data
public class Wrapper<T> {
    private final T value;
}
