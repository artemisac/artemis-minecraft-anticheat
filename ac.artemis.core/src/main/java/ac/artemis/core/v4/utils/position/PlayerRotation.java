package ac.artemis.core.v4.utils.position;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.utils.maths.MathUtil;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

@Getter
public class PlayerRotation extends SimpleRotation {
    private final long timestamp;
    private final Player player;

    public PlayerRotation(Player player, float yaw, float pitch, long timestamp) {
        super(MathUtil.normalizeYaw(yaw), pitch);
        this.timestamp = timestamp;
        this.player = player;
    }

    public float getDeltaYaw(SimpleRotation two) {
        return (float) MathUtil.distanceBetweenAngles(yaw, two.getYaw());
    }

    public float getDeltaPitch(SimpleRotation two) {
        return (float) MathUtil.distanceBetweenAngles(pitch, two.getPitch());
    }


}
