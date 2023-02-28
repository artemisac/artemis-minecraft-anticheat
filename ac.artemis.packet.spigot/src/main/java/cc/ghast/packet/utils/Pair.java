package cc.ghast.packet.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Ghast
 * @since 19/08/2020
 * Artemis © 2020
 */

@AllArgsConstructor
@Data
public class Pair<K, V> {
    K k;
    V v;
}
