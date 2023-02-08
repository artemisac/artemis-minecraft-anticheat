package ac.artemis.anticheat.engine.v1.block;

import ac.artemis.anticheat.engine.v1.Bntity;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import lombok.Getter;

@Getter
public class WrappedBlock {
    private final NMSMaterial material;

    public WrappedBlock(NMSMaterial material) {
        this.material = material;
    }

    public void onFallenUpon(Bntity physics, Location position, float fallDistance) {
        physics.fall(fallDistance, 1.0f);
    }

    public void onLanded(Bntity entity) {
        entity.motionY = 0.0D;
    }

    public void onEntityCollidedWithBlock(Bntity physics, Location position) {
        // ┌(͝°͜ʖ͡°)=ε/̵͇̿̿/’̿’̿ ̿
    }

    public void onEntityCollidedWithBlockState(Bntity physics, Location position) {
        // ┌(͝°͜ʖ͡°)=ε/̵͇̿̿/’̿’̿ ̿
    }

    public Point modifyAcceleration(World world, NaivePoint blockPos, Bntity bntity, Point vector){
        return vector;
    }

    public static WrappedBlock getBlock(NMSMaterial material) {
        switch (material) {
            case SLIME_BLOCK:
                return Blocks.SLIME;
            case SOUL_SAND:
                return Blocks.SOUL_SAND;
            case COBWEB:
                return Blocks.WEB;
            case WATER:
            case LAVA:
                return Blocks.LIQUID;
            default:
                return new WrappedBlock(material);
        }
    }
}
