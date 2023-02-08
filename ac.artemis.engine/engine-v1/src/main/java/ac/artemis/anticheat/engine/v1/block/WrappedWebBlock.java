package ac.artemis.anticheat.engine.v1.block;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.packet.minecraft.world.Location;

public class WrappedWebBlock extends WrappedBlock {
    public WrappedWebBlock() {
        super(NMSMaterial.COBWEB);
    }

    @Override
    public void onEntityCollidedWithBlockState(Bntity physics, Location position) {
        physics.setInWeb();
    }
}
