package ac.artemis.packet.generator.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Pair<X, Y> {
    X x;
    Y y;
}
