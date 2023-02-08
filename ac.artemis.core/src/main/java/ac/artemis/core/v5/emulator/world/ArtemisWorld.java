package ac.artemis.core.v5.emulator.world;

import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.emulator.block.Block;

public interface ArtemisWorld {
    NMSMaterial getMaterialAt(final int x, final int y, final int z);
    Block getBlockAt(final int x, final int y, final int z);
    void updateMaterialAt(final Material material, final int x, final int y, final int z);
    void updateMaterialAt(final Block block, final int x, final int y, final int z);
    boolean isLoaded(final int x, final int z);
    boolean isLoaded(final int minX, final int maxX, final int minZ, final int maxZ);

    Block cache(final int x, final int y, final int z);
    World getBukkitWorld();
}
