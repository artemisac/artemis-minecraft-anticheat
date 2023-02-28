package cc.ghast.packet.wrapper.codec;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StringPool {
    private final String data;
    private final int var;
}
