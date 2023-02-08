package ac.artemis.core.v4.utils.blocks;

import ac.artemis.anticheat.api.material.NMSMaterial;

/**
 * @author Ghast
 * @since 23-Mar-20
 */

public enum ObjectBoundingEnum {
    BOAT(1.375, 0.6, NMSMaterial.OAK_BOAT, NMSMaterial.ACACIA_BOAT, NMSMaterial.BIRCH_BOAT,
            NMSMaterial.DARK_OAK_BOAT, NMSMaterial.JUNGLE_BOAT, NMSMaterial.SPRUCE_BOAT),
    MINECART(0.98, 0.7, NMSMaterial.MINECART, NMSMaterial.HOPPER_MINECART, NMSMaterial.CHEST_MINECART,
            NMSMaterial.COMMAND_BLOCK_MINECART, NMSMaterial.FURNACE_MINECART, NMSMaterial.TNT_MINECART),
    TNT_PRIMED(0.98, 0.98, NMSMaterial.TNT),
    ENDER_CRYSTAL(2.0, 2.0, NMSMaterial.END_CRYSTAL),

    ;

    private double xz, y;
    private NMSMaterial[] type;

    ObjectBoundingEnum(double xz, double y, NMSMaterial... mats) {
        this.xz = xz;
        this.y = y;
        this.type = mats;
    }
}
