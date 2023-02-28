package cc.ghast.packet.wrapper.codec;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BytePool {
    private final byte[] data;
    private final int var;
}
