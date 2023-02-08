package ac.artemis.core.v5.emulator.block.impl;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.wrapper.Wrapper;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.utils.cache.Cache;
import ac.artemis.core.v5.utils.cache.TimedCache;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.Collections;
import java.util.List;

/**
 * @author Ghast
 * @since 19/02/2021
 * Artemis © 2021
 */
public class BlockDoor extends Block {
    private Wrapper<Hinge> hinge = new Wrapper<>(Hinge.LEFT);
    private Half half;
    private Wrapper<Boolean> open = new Wrapper<>(false);
    private Wrapper<Boolean> powered = new Wrapper<>(false);
    private Wrapper<Boolean> flag1 = new Wrapper<>(false);

    private final Cache<BoundingBox> cachedBox = new TimedCache<>(5000);

    public BlockDoor(final NaivePoint location, final EnumFacing direction) {
        super(NMSMaterial.OAK_DOOR, location, direction);
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        if (!cachedBox.isReset()) {
            return Collections.singletonList(cachedBox.get());
        }

        // Todo - Make proper updating for bukkit blocks
        final ac.artemis.packet.minecraft.block.Block bukkitBlock = BlockUtil.getBlockAsync(world.getBukkitWorld(), location.getX(), location.getY(), location.getZ());

        if (bukkitBlock != null) this.readData(bukkitBlock.getData());

        if (half == Half.LOWER) {
            final NaivePoint up = location.getRelative(BlockFace.UP);
            final Block block = world.getBlockAt(up.getX(), up.getY(), up.getZ());

            if (block instanceof BlockDoor) {
                final BlockDoor door = (BlockDoor) block;
                hinge = door.getHinge();
                powered = door.powered;
            }
        } else {
            final NaivePoint down = location.getRelative(BlockFace.DOWN);
            final Block block = world.getBlockAt(down.getX(), down.getY(), down.getZ());

            if (block instanceof BlockDoor) {
                final BlockDoor door = (BlockDoor) block;
                direction = door.getDirection();
                open = door.open;
            }
        }
        // Actual collisions
        final BoundingBox boundingBox;

        final float f = 0.1875F;

        if (open.get()) {
            switch (direction) {
                case EAST: {
                    if (!flag1.get()) {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    } else {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    }
                    break;
                }

                case SOUTH: {
                    if (!flag1.get()) {
                        boundingBox = getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    } else {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    }
                    break;
                }

                case WEST: {
                    if (!flag1.get()) {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    } else {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    }
                    break;
                }

                default: {
                    if (!flag1.get()) {
                        boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    } else {
                        boundingBox = getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    }
                    break;
                }
            }
        } else {
            switch (direction) {
                case EAST:
                    boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                    break;
                case SOUTH:
                    boundingBox = getFromPoint(location, 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                    break;
                case WEST:
                    boundingBox = getFromPoint(location, 1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                    break;
                default:
                    boundingBox = getFromPoint(location, 0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                    break;
            }
        }

        cachedBox.set(boundingBox);
        return Collections.singletonList(boundingBox);
    }

    public Wrapper<Hinge> getHinge() {
        return hinge;
    }

    public BlockDoor setHinge(final Hinge hinge) {
        this.hinge.set(hinge);
        return this;
    }

    public Half getHalf() {
        return half;
    }

    public BlockDoor setHalf(final Half half) {
        this.half = half;
        return this;
    }

    public boolean isOpen() {
        return open.get();
    }

    public BlockDoor setOpen(final boolean open) {
        this.open.set(open);
        return this;
    }

    public boolean isPowered() {
        return powered.get();
    }

    public BlockDoor setPowered(final boolean powered) {
        this.powered.set(powered);
        return this;
    }

    public boolean isFlag1() {
        return flag1.get();
    }

    public BlockDoor setFlag1(final boolean flag1) {
        this.flag1.set(flag1);
        return this;
    }

    @Override
    public void readData(final int data) {
        if ((data & 8) > 0) {
            this.setHalf(Half.UPPER);
            this.setHinge((data & 1) > 0 ? Hinge.RIGHT : Hinge.LEFT);
            this.setPowered((data & 2) > 0);
        } else {
            this.setHalf(Half.LOWER);
            this.setDirection(EnumFacing.getHorizontal(data & 3).rotateYCCW());
            this.setOpen((data & 4) > 0);
        }
    }

    public enum Hinge {
        LEFT, RIGHT;

        public String getName() {
            return this == Hinge.LEFT ? "left" : "right";
        }
    }

    public enum Half {

        UPPER, LOWER;

        public String getName() {
            return this == Half.UPPER ? "upper" : "lower";
        }
    }
}
