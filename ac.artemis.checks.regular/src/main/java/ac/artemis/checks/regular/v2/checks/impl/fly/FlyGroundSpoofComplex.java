package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v5.collision.BlockCollisionProvider;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.packet.minecraft.material.Material;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 30-Mar-20
 */
@Setback
@Check(type = Type.FLY, var = "GroundSpoofComplex")
public class FlyGroundSpoofComplex extends ComplexPositionCheck {

    private float buffer;

    public FlyGroundSpoofComplex(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.SLIME,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.COLLIDE_ENTITY,
                ExemptType.LIQUID,
                ExemptType.LIQUID_WALK,
                ExemptType.RESPAWN,
                ExemptType.LADDER,
                ExemptType.WEB,
                ExemptType.UNDERBLOCK
        );

        // Invalid conditions which can false it I guess? I don't think so but oh well...
        if (exempt) {
            debug("Invalid conditions " + Arrays.toString(exemptTypes()));
            return;
        }

        // Get a bounding box then expand it
        final BoundingBox box = data.prediction.getLazyBox().expand(1.0D, 1.0D, 1.0D);

        // Check for NMS collisions
        final Set<Material> mats = BlockCollisionProvider.PROVIDER
                .getCollidingBlocks(box, data.getEntity())
                .stream()
                .map(e -> e.getMaterial().getMaterial())
                .collect(Collectors.toSet());

        final boolean flag = this.getPacket().isOnGround()
                && mats.size() == 1
                && NMSMaterial.matchNMSMaterial(mats.iterator().next()).equals(NMSMaterial.AIR);

        // Obvious ground spoof. No need for buffers
        if (flag) {
            if (buffer++ > 2) {
                this.log(
                        new Debug<>("collisions", Arrays.toString(mats.toArray()))
                );
            }
        } else {
            this.buffer = Math.max(0.F, buffer - 0.125F);
        }

        this.debug("collisions=" + Arrays.toString(mats.toArray()));
    }
}
