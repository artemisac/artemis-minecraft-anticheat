package ac.artemis.checks.regular.v2.checks.impl.aim;

import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.SimpleRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import ac.artemis.core.v4.utils.position.SimpleRotation;
import ac.artemis.core.v5.utils.MathUtil;
import ac.artemis.core.v5.utils.minecraft.MathHelper;

import java.util.ArrayList;
import java.util.List;

@Check(type = Type.AIM, var = "Generic")
@Experimental
public class AimGeneric extends SimpleRotationCheck {
    private float lastDeltaYaw, lastDeltaPitch;
    private int buffer = 0;

    /*
    This check is used to patch generic client rotations, outside combat
     */

    public AimGeneric(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handleRotation(final SimpleRotation from, final SimpleRotation to) {
        final float deltaYaw = Math.abs(to.getYaw() - from.getYaw());
        final float deltaPitch = Math.abs(to.getPitch() - from.getPitch());

        final boolean cinematic = data.getCombat().isCinematic();

        final boolean flag = deltaYaw > 0.0 && deltaPitch > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f && !cinematic;

        if (flag) {
            final float rotationRound = Math.round(deltaYaw) + Math.round(deltaPitch);
            final float previousRotationRound = Math.round(lastDeltaYaw) + Math.round(lastDeltaPitch);

            if (rotationRound == previousRotationRound && Math.round(deltaYaw) == Math.round(lastDeltaYaw)) {
                if (++buffer > 10) {
                    this.log("r=" + rotationRound + " pr=" + previousRotationRound);
                }
            } else {
                buffer = 0;
            }

            this.lastDeltaYaw = deltaYaw;
            this.lastDeltaPitch = deltaPitch;
        }
    }
}
