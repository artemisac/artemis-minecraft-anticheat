package ac.artemis.packet.api.test;

import ac.artemis.packet.minecraft.PotionEffectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PotionEffectTypeTest {

    @Test
    public void testInit() {
        PotionEffectType type = PotionEffectType.CONFUSION;
    }

    @Test
    public void testId() {
        PotionEffectType type = PotionEffectType.CONFUSION;
        assertEquals(9, type.getId());
    }

    @Test
    public void testReverseId() {
        PotionEffectType type = PotionEffectType.getFromId(9);
        assertEquals(PotionEffectType.CONFUSION, type);
    }
}
