package ac.artemis.anticheat.engine.v1.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.packet.minecraft.world.Location;

public class WrappedSoulSandBlock extends WrappedBlock {
    public WrappedSoulSandBlock() {
        super(NMSMaterial.SOUL_SAND);
    }

    @Override
    public void onEntityCollidedWithBlockState(Bntity physics, Location position) {
        physics.motionX *= 0.4D;
        physics.motionZ *= 0.4D;
    }
}
