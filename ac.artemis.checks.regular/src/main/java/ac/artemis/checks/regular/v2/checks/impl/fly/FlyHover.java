package ac.artemis.checks.regular.v2.checks.impl.fly;

import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.NMS;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.material.Material;

import java.util.*;

@Check(type = Type.FLY, var = "Hover")
@NMS
@Setback
public class FlyHover extends ComplexPositionCheck implements TeleportHandler {

    private int airTicks = 0, buffer = 0;
    private double lastDeltaY = 0.0;

    private static final Collection<NMSMaterial> invalidMaterials = Arrays.asList(
            NMSMaterial.ANVIL,
            NMSMaterial.ENCHANTING_TABLE,
            NMSMaterial.OAK_BOAT
    );

    public FlyHover(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handlePosition(final PlayerPosition from, final PlayerPosition to) {
        final Set<Material> collisions = NMSManager
                .getInms()
                .getCollidingBlocks(to.getBox(), to.getWorld());

        final boolean invalid = data.getUser().isOnFakeGround()
                || data.getPlayer().getGameMode() == GameMode.CREATIVE
                || data.getPlayer().isAllowedFlight()
                || data.getMovement().isOnSlime()
                || data.getMovement().isInLiquid()
                || data.getMovement().isOnLadder()
                || data.getMovement().isUnderBlock()
                || data.collision.getCollidingMaterials1().contains(NMSMaterial.PISTON)
                || data.collision.getCollidingMaterials1().contains(NMSMaterial.MOVING_PISTON)
                || data.collision.getCollidingMaterials1().contains(NMSMaterial.PISTON_HEAD)
                || collisions.stream().anyMatch(invalidMaterials::contains)
                || (from.distanceXZ(to) < 0.1 && data.user.isOnGround())
                || data.movement.isInVehiclePacket()
                || this.isExempt(ExemptType.JOIN, ExemptType.VELOCITY);

        if (!invalid) {
            final double deltaY = to.getY() - from.getY();
            final double velocityXYZ = Math.abs(data.entity.getMotionY() - deltaY);

            if (++airTicks > 8 && deltaY >= 0.0 && deltaY < 0.01 && velocityXYZ < 1E-5) {
                final double horizontalDistance = MathUtil.vectorDistance(from, to);
                final double acceleration = Math.abs(deltaY - lastDeltaY);

                if (acceleration < 0.0003 && horizontalDistance > 0.12) {
                    if (++buffer > 5) {
                        log("a=" + acceleration + " readNBTNMS=" + horizontalDistance);
                    }
                } else {
                    buffer = 0;
                }
            }

            this.debug("airTicks=" + airTicks + " deltaY=" + deltaY + " velocityY=" + velocityXYZ);
            this.lastDeltaY = deltaY;
        } else {
            this.debug("Invalid");
        }
    }

    @Override
    public void handle(ModifiableFlyingLocation confirmedLocation) {
        this.airTicks = 0;
    }
}
