package ac.artemis.core.v4.utils.blocks;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.anticheat.api.material.NMSMaterial;

/**
 * @author Ghast
 * @since 2/18/2020
 */
public class BlocksUtil {
    public static float getSlipperiness(Block block) {
        if (block == null) return 0.6F;
        switch (NMSMaterial.matchXMaterial(block.getType())) {
            case SLIME_BLOCK:
                return 0.8F;
            case ICE:
            case PACKED_ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
                return 0.98F;
            default:
                return 0.6F;
        }
    }
}
