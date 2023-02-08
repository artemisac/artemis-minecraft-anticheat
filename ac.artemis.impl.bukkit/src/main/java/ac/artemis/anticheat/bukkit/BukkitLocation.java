package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.world.Chunk;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;

public class BukkitLocation extends AbstractWrapper<org.bukkit.Location> implements Location {
    public BukkitLocation(org.bukkit.Location wrapper) {
        super(wrapper);
    }

    @Override
    public double getX() {
        return wrapper.getX();
    }

    @Override
    public double getY() {
        return wrapper.getY();
    }

    @Override
    public double getZ() {
        return wrapper.getZ();
    }

    @Override
    public float getYaw() {
        return wrapper.getYaw();
    }

    @Override
    public float getPitch() {
        return wrapper.getPitch();
    }

    @Override
    public int getBlockX() {
        return wrapper.getBlockX();
    }

    @Override
    public int getBlockY() {
        return wrapper.getBlockY();
    }

    @Override
    public int getBlockZ() {
        return wrapper.getBlockZ();
    }

    @Override
    public Location subtract(int x, int y, int z) {
        wrapper.subtract(x, y, z);
        return this;
    }

    @Override
    public Location add(int x, int y, int z) {
        wrapper.add(x, y, z);
        return this;
    }

    @Override
    public Block getBlock() {
        return new BukkitBlock(wrapper.getBlock());
    }

    @Override
    public Chunk getChunk() {
        return new BukkitChunk(wrapper.getChunk());
    }

    @Override
    public World getWorld() {
        return new BukkitWorld(wrapper.getWorld());
    }
}
