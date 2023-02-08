package ac.artemis.anticheat.engine.v2.jump;

import ac.artemis.anticheat.engine.v2.ArtemisData;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.modal.Motion;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Jump {
    private ArtemisData data;
    private Motion motion;
    private AttributeMap attributes;

    public boolean isSprinting() {
        return attributes.poll(EntityAttributes.SPRINT);
    }

    public boolean isWater() {
        return attributes.poll(EntityAttributes.WATER);
    }

    public boolean isLava() {
        return attributes.poll(EntityAttributes.LAVA);
    }

    public float getYaw() {
        return attributes.poll(EntityAttributes.YAW);
    }
}
