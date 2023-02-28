package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.block.BlockFace;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

public class BukkitBlock extends AbstractWrapper<org.bukkit.block.Block> implements Block {
    public BukkitBlock(org.bukkit.block.Block wrapper) {
        super(wrapper);
    }

    @Override
    public Material getType() {
        return wrapper.getType() == null ? null : new BukkitMaterial(wrapper.getType());
    }

    @Override
    public int getX() {
        return wrapper.getX();
    }

    @Override
    public int getY() {
        return wrapper.getY();
    }

    @Override
    public int getZ() {
        return wrapper.getZ();
    }

    @Override
    public boolean isLiquid() {
        return wrapper.isLiquid();
    }

    @Override
    public Block getRelative(BlockFace blockFace) {
        final org.bukkit.block.Block block = wrapper.getRelative(org.bukkit.block.BlockFace.values()[blockFace.ordinal()]);
        return block == null ? null : new BukkitBlock(block);
    }

    @Override
    public World getWorld() {
        return wrapper.getWorld() == null ? null : new BukkitWorld(wrapper.getWorld());
    }

    @Override
    public Location getLocation() {
        return wrapper.getLocation() == null ? null : new BukkitLocation(wrapper.getLocation());
    }

    @Override
    public byte getData() {
        return wrapper.getData();
    }
}
