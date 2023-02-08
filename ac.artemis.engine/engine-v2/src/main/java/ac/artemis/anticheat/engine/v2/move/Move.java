package ac.artemis.anticheat.engine.v2.move;

import ac.artemis.core.v5.utils.bounding.BoundingBox;
import lombok.Data;
import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.attributes.AttributeKey;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.modal.Motion;

/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */

@Data
public class Move {
    private Motion motion;
    private BoundingBox boundingBox;
    private AttributeMap attributeMap;

    public Move(Motion motion, BoundingBox boundingBox, AttributeMap attributeMap) {
        this.motion = motion;
        this.boundingBox = boundingBox;
        this.attributeMap = attributeMap;
    }

    public <T> T poll(AttributeKey key) {
        return attributeMap.poll(key);
    }

    public boolean isGround() {
        return poll(EntityAttributes.GROUND);
    }

    public boolean isSneaking() {
        return poll(EntityAttributes.SNEAK);
    }

    public boolean isWeb() {
        return poll(EntityAttributes.WEB);
    }

    public boolean isNoClip(ArtemisData data) {
        // String comparison for safe 1.7 porting **in case** you decide to switch the version
        return data.getPlayer().getPlayer().getGameMode().name().equalsIgnoreCase("SPECTATOR");
    }

    public float getStepHeight() {
        // Hardcoded to save performance, reference in here in case you want to add it
        // To attributes
        return 0.6F;
    }

}
