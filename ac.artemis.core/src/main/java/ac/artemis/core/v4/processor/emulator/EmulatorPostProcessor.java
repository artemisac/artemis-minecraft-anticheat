package ac.artemis.core.v4.processor.emulator;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;

public class EmulatorPostProcessor extends AbstractHandler {
    public EmulatorPostProcessor(final PlayerData data) {
        super("Emulator [0x02]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (isNullLocation() || isNullRotation() || isNullMovement()) return;
        if (packet instanceof PacketPlayClientFlying) {
            postUpdate();
            data.prediction.setConfirmingVelocity(false);
            data.prediction.setVelocityTicks(data.prediction.getVelocityTicks() + 1);

            if (data.entity.isJumping()) {
                data.entity.setJumpTicks(10);
            }

            if (packet instanceof PacketPlayClientPosition) {
                data.entity.setPosition(
                        data.prediction.getX(),
                        data.prediction.getY(),
                        data.prediction.getZ()
                );
            }
        }
    }

    private void postUpdate() {
        this.data.entity.setPreviousGround(data.entity.isOnGround());
        //this.data.entity.setLastSprinting(data.entity.isSprinting());
        //this.data.entity.onGround = data.user.isOnFakeGround();
        //this.data.entity.onLadder = data.movement.isOnLadder();
        this.data.entity.setNoClip(data.getPlayer().getGameMode().name().equalsIgnoreCase("SPECTATOR"));
        this.data.entity.setLastPositionPrevious(data.movement.lastMovement);
        this.data.entity.setLastRotationPrevious(data.movement.lastMovement);
        //this.data.entity.getGhostBlocks().clear();
        this.data.entity.getTags().clear();
        //this.data.world.clear();
        //this.data.entity.usingItem = data.user.isUsingItem();
    }

    private boolean preCheck() {
        //System.out.println("DATA IS NULL ");
        return data == null || data.movement == null;
    }

    public boolean isNullLocation() {
        return (preCheck() || data.movement.getLocation() == null || data.movement.getLastLocation() == null);
    }

    public boolean isNullVelocity() {
        return (preCheck() || data.movement.getVelocity() == null || data.movement.getLastVelocity() == null);
    }

    public boolean isNullRotation() {
        return (preCheck() || data.movement.getRotation() == null || data.movement.getLastRotation() == null);
    }

    public boolean isNullMovement() {
        return (preCheck() || data.movement.getMovement() == null || data.movement.getLastMovement() == null);
    }
}
