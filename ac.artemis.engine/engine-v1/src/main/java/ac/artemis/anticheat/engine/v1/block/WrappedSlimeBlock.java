package ac.artemis.anticheat.engine.v1.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.packet.minecraft.world.Location;

public class WrappedSlimeBlock extends WrappedBlock {
    public WrappedSlimeBlock() {
        super(NMSMaterial.SLIME_BLOCK);
    }

    @Override
    public void onFallenUpon(Bntity physics, Location position, float fallDistance) {
        if (physics.isSneaking()) {
            super.onFallenUpon(physics, position, fallDistance);
        } else {
            physics.fall(fallDistance, 0.0F);
        }
    }

    @Override
    public void onLanded(Bntity entity) {
        if (entity.isSneaking()) {
            super.onLanded(entity);
        } else if (entity.motionY < 0.0D) {
            entity.motionY = -entity.motionY;
        }
    }

    @Override
    public void onEntityCollidedWithBlock(Bntity entity, Location position) {
        if (Math.abs(entity.motionY) < 0.1D && !entity.isSneaking()) {
            double d0 = 0.4D + Math.abs(entity.motionY) * 0.2D;
            entity.motionX *= d0;
            entity.motionZ *= d0;
        }

        super.onEntityCollidedWithBlock(entity, position);
    }
}
