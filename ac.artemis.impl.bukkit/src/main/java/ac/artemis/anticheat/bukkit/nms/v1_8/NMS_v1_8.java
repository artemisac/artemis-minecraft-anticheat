package ac.artemis.anticheat.bukkit.nms.v1_8;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.bukkit.entity.BukkitEntity;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.Vec3d;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 23-Mar-20
 */
public class NMS_v1_8 implements INMS {
    @Override
    public BoundingBox getBoundingBoxBlock(
            final ac.artemis.packet.minecraft.block.Block block
    ) {
        final BlockPosition blockPosition = new BlockPosition(
                block.getX(),
                block.getY(),
                block.getZ()
        );

        final CraftWorld craftServer = (CraftWorld) block.getWorld().v();
        final WorldServer world = craftServer.getHandle();

        final IBlockData iBlockData = world.getType(blockPosition);
        final AxisAlignedBB bb = iBlockData.getBlock().a(
                world,
                blockPosition,
                iBlockData
        );

        if (bb == null) {
            return ReachEntity.ZERO_AABB;
        }

        return new BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
    }

    @Override
    public List<BoundingBox> getCollidingBoxes(
            final ac.artemis.packet.minecraft.entity.Entity player,
            final BoundingBox bb
    ) {
        final CraftEntity craftEntity = (CraftEntity) player.v();
        final Entity player1 = craftEntity.getHandle();
        final World world = player1.getWorld();

        final int minX = MathHelper.floor(bb.getMinX());
        final int maxX = MathHelper.floor(bb.getMaxX() + 1.0D);
        final int minZ = MathHelper.floor(bb.getMinZ());
        final int maxZ = MathHelper.floor(bb.getMaxZ() + 1.0D);

        for(int chunkx = minX >> 4; chunkx <= maxX - 1 >> 4; ++chunkx) {
            for (int chunkz = minZ >> 4; chunkz <= maxZ - 1 >> 4; ++chunkz) {
                if (!player.getWorld().isChunkLoaded(chunkx, chunkz)) {
                    return new ArrayList<>();
                }
            }
        }

        final List<AxisAlignedBB> axises = world.getCubes(
                player1,
                new AxisAlignedBB(
                        bb.minX,
                        bb.minY,
                        bb.minZ,
                        bb.maxX,
                        bb.maxY,
                        bb.maxZ
                )
        );
        final List<BoundingBox> boxes = new ArrayList<>();

        for (AxisAlignedBB it : axises) {
            boxes.add(new BoundingBox(it.a, it.b, it.c, it.d, it.e, it.f));
        }
        return boxes;
    }

    @Override
    public List<BoundingBox> getCollidingBoxes(
            final ac.artemis.packet.minecraft.entity.Entity player,
            BoundingBox bb,
            List<ac.artemis.packet.minecraft.block.Block> ghostBlocks
    ) {
        final CraftEntity craftEntity = (CraftEntity) player.v();
        final Entity player1 = craftEntity.getHandle();

        final World world = player1.getWorld();
        final AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.maxY,
                bb.maxZ
        );

        final int minX = MathHelper.floor(bb.getMinX());
        final int maxX = MathHelper.floor(bb.getMaxX() + 1.0D);
        final int minZ = MathHelper.floor(bb.getMinZ());
        final int maxZ = MathHelper.floor(bb.getMaxZ() + 1.0D);

        for(int chunkx = minX >> 4; chunkx <= maxX - 1 >> 4; ++chunkx) {

            for (int chunkz = minZ >> 4; chunkz <= maxZ - 1 >> 4; ++chunkz) {
                if (!player.getWorld().isChunkLoaded(chunkx, chunkz)) {
                    return new ArrayList<>();
                }
            }
        }

        final List<AxisAlignedBB> axises = new ArrayList<>();
        try {
            int i = MathHelper.floor(axisalignedbb.a);
            int j = MathHelper.floor(axisalignedbb.d + 1.0D);
            int k = MathHelper.floor(axisalignedbb.b);
            int l = MathHelper.floor(axisalignedbb.e + 1.0D);
            int i1 = MathHelper.floor(axisalignedbb.c);
            int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);
            WorldBorder worldborder = world.getWorldBorder();
            boolean flag = player1.aT();
            boolean flag1 = world.a(worldborder, player1);
            int ystart = Math.max(k - 1, 0);
            int j2;
            for(int chunkx = i >> 4; chunkx <= j - 1 >> 4; ++chunkx) {
                int cx = chunkx << 4;

                for(int chunkz = i1 >> 4; chunkz <= j1 - 1 >> 4; ++chunkz) {
                    if (world.getChunkIfLoaded(chunkx, chunkz) != null) {
                        j2 = chunkz << 4;
                        Chunk chunk = world.getChunkIfLoaded(chunkx, chunkz);
                        int xstart = Math.max(i, cx);
                        int xend = Math.min(j, cx + 16);
                        int zstart = Math.max(i1, j2);
                        int zend = Math.min(j1, j2 + 16);

                        for(int x = xstart; x < xend; ++x) {
                            for(int z = zstart; z < zend; ++z) {
                                for(int y = ystart; y < l; ++y) {
                                    BlockPosition blockposition = new BlockPosition(x, y, z);
                                    if (flag && flag1) {
                                        player1.h(false);
                                    } else if (!flag && !flag1) {
                                        player1.h(true);
                                    }

                                    IBlockData block;
                                    if (!world.getWorldBorder().a(blockposition) && flag1) {
                                        block = Blocks.STONE.getBlockData();
                                    } else {
                                        block = chunk.getBlockData(blockposition);
                                    }

                                    if (block != null) {
                                        block.getBlock().a(world, blockposition, block, axisalignedbb, axises, player1);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            double d0 = 0.25D;
            try {
                List list = world.getEntities(player1, axisalignedbb.grow(d0, d0, d0));

                for(j2 = 0; j2 < list.size(); ++j2) {
                    if (player1.passenger != list && player1.vehicle != list) {
                        AxisAlignedBB axisalignedbb1 = ((Entity)list.get(j2)).S();
                        if (axisalignedbb1 != null && axisalignedbb1.b(axisalignedbb)) {
                            axises.add(axisalignedbb1);
                        }

                        axisalignedbb1 = player1.j((Entity)list.get(j2));
                        if (axisalignedbb1 != null && axisalignedbb1.b(axisalignedbb)) {
                            axises.add(axisalignedbb1);
                        }
                    }
                }
            } catch (Throwable e){
            }

        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
        boolean flag1 = world.a(world.getWorldBorder(), player1);

        for (ac.artemis.packet.minecraft.block.Block e : ghostBlocks) {
            final AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(
                    e.getX(),
                    e.getY(),
                    e.getZ(),
                    e.getX() + 1.0F,
                    e.getY() + 1.0F,
                    e.getZ() + 1.0F
            );

            if (!axisalignedbb1.b(axisalignedbb))
                continue;
            axises.add(axisalignedbb1);
        }

        List<BoundingBox> boxes = new ArrayList<>();

        for (AxisAlignedBB it : axises) {
            boxes.add(new BoundingBox(it.a, it.b, it.c, it.d, it.e, it.f));
        }
        return boxes;
    }

    @Override
    public List<ac.artemis.packet.minecraft.entity.Entity> getEntitiesInAABBexcluding(
            final Player player,
            final BoundingBox bb,
            final java.util.function.Predicate<ac.artemis.packet.minecraft.entity.Entity> predicate
    ) {
        final CraftEntity craftEntity = (CraftEntity) player.v();
        final Entity player1 = craftEntity.getHandle();
        final World world = player1.world;

        final int minX = MathHelper.floor(bb.getMinX());
        final int maxX = MathHelper.floor(bb.getMaxX() + 1.0D);
        final int minZ = MathHelper.floor(bb.getMinZ());
        final int maxZ = MathHelper.floor(bb.getMaxZ() + 1.0D);

        for(int chunkx = minX >> 4; chunkx <= maxX - 1 >> 4; ++chunkx) {

            for (int chunkz = minZ >> 4; chunkz <= maxZ - 1 >> 4; ++chunkz) {
                if (!player.getWorld().isChunkLoaded(chunkx, chunkz)) {
                    return new ArrayList<>();
                }
            }
        }

        final List<Entity> axises = world.a(
                player1,
                new AxisAlignedBB(
                        bb.minX,
                        bb.minY,
                        bb.minZ,
                        bb.maxX,
                        bb.maxY,
                        bb.maxZ
                ),
                Entity::isAlive
        );
        final List<ac.artemis.packet.minecraft.entity.Entity> boxes = new ArrayList<>();
        for (Entity ax : axises){
            final org.bukkit.entity.Entity bukkitEntity = ax.getBukkitEntity();
            final ac.artemis.packet.minecraft.entity.Entity entity = BukkitEntity.of(bukkitEntity);

            if (predicate.test(entity)) {
                boxes.add(entity);
            }
        }

        return boxes;
    }

    @Override
    public BoundingBox getEntityBoundingBox(
            final ac.artemis.packet.minecraft.entity.Entity entity
    ) {
        final CraftEntity craftEntity = (CraftEntity) entity.v();
        final AxisAlignedBB bb = craftEntity.getHandle().getBoundingBox();

        return new BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
    }

    @Override
    public Set<Material> getCollidingBlocks(
            final BoundingBox bb,
            final ac.artemis.packet.minecraft.world.World world
    ) {
        return getCollidingMaterials(bb, world);
    }

    public Set<Material> getCollidingMaterials(
            final BoundingBox bb,
            final ac.artemis.packet.minecraft.world.World world) {
        final Set<Material> arraylist = new HashSet<>();

        final int minX = MathHelper.floor(bb.getMinX());
        final int maxX = MathHelper.floor(bb.getMaxX() + 1.0D);
        final int minZ = MathHelper.floor(bb.getMinZ());
        final int maxZ = MathHelper.floor(bb.getMaxZ() + 1.0D);

        for(int chunkx = minX >> 4; chunkx <= maxX - 1 >> 4; ++chunkx) {
            for (int chunkz = minZ >> 4; chunkz <= maxZ - 1 >> 4; ++chunkz) {
                if (!world.isChunkLoaded(chunkx, chunkz)) {
                    return new HashSet<>();
                }
            }
        }

        final int minY = MathHelper.floor(bb.minY);
        final int maxY = MathHelper.floor(bb.maxY + 1.0D);

        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                if (!BlockUtil.isLoaded(world, x, z)) {
                    continue;
                }

                for (int y = minY - 1; y < maxY; ++y) {
                    final Block block = BlockUtil.getBlockAsync(world, x, y, z);
                    if (block == null) continue;

                    arraylist.add(block.getType());
                }
            }
        }

        return arraylist;
    }

    @Override
    public Vec3d getMotion(Player player) {
        final CraftPlayer craftPlayer = (CraftPlayer) player.v();
        final EntityPlayer playerE = craftPlayer.getHandle();

        return new Vec3d(playerE.motX, playerE.motY, playerE.motZ);
    }

    @Override
    public ac.artemis.packet.minecraft.entity.Entity getEntity(
            final ac.artemis.packet.minecraft.world.World world,
            final int id
    ) {
        final CraftWorld craftWorld = world.v();
        final Entity entity = craftWorld.getHandle().a(id);

        return entity == null
                ? null
                : BukkitEntity.of(entity.getBukkitEntity());
    }

    @Override
    public double getTps() {
        return MinecraftServer.getServer().recentTps[0];
    }

    @Override
    public Point getModifiedAcceleration(
            final ac.artemis.packet.minecraft.world.World bbukkit,
            final cc.ghast.packet.wrapper.bukkit.BlockPosition block,
            final ac.artemis.packet.minecraft.entity.Entity entity,
            final Point point
    ) {
        final CraftWorld craftWorld = (CraftWorld) bbukkit.v();
        final World world = craftWorld.getHandle();

        if (!bbukkit.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
            return point;
        }

        final BlockPosition blockPosition = new BlockPosition(
                block.getX(),
                block.getY(),
                block.getZ()
        );
        final IBlockData blockData = world.getType(blockPosition);
        final CraftEntity craftEntity = (CraftEntity) entity.v();

        final Vec3D vec3D = new Vec3D(
                point.getX(),
                point.getY(),
                point.getZ()
        );
        final Vec3D fluid = blockData.getBlock().a(
                world,
                blockPosition,
                craftEntity.getHandle(),
                vec3D
        );

        return new Point(fluid.a, fluid.b, fluid.c);
    }
    
    @Override
    public List<BoundingBox> getCollidingEntities(
            final ac.artemis.packet.minecraft.entity.Entity bukkitEntity,
            final ac.artemis.packet.minecraft.world.World bworld,
            final BoundingBox bb
    ) {
        final CraftWorld craftWorld = (CraftWorld) bworld.v();
        final World world = craftWorld.getHandle();

        final Entity entity = bukkitEntity == null
                ? null
                : ((CraftEntity) bukkitEntity.v()).getHandle();
        final AxisAlignedBB aabb = AxisAlignedBB.a(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.maxX,
                bb.maxZ
        );

        final int minX = MathHelper.floor(bb.getMinX());
        final int maxX = MathHelper.floor(bb.getMaxX() + 1.0D);
        final int minZ = MathHelper.floor(bb.getMinZ());
        final int maxZ = MathHelper.floor(bb.getMaxZ() + 1.0D);

        for(int chunkx = minX >> 4; chunkx <= maxX - 1 >> 4; ++chunkx) {
            for (int chunkz = minZ >> 4; chunkz <= maxZ - 1 >> 4; ++chunkz) {
                if (!bworld.isChunkLoaded(chunkx, chunkz)) {
                    return new ArrayList<>();
                }
            }
        }

        final List<AxisAlignedBB> bbs = new ArrayList<>();
        try {
            world.getEntities(entity, aabb.grow(0.25D, 0.25D, 0.25D))
                    .forEach(f -> {
                        final Entity e = (Entity) f;
                        if (entity == null || (entity.passenger != e && entity.vehicle != e)) {
                            AxisAlignedBB axisalignedbb1 = e.S();
                            if (axisalignedbb1 != null && axisalignedbb1.b(aabb)) {
                                bbs.add(axisalignedbb1);
                            }

                            if (entity != null) {
                                axisalignedbb1 = entity.j(e);
                                if (axisalignedbb1 != null && axisalignedbb1.b(aabb)) {
                                    bbs.add(axisalignedbb1);
                                }
                            }
                        }
                    });
        } catch (Exception e) {

        }

        return bbs.stream()
                .map(e -> new BoundingBox(e.a, e.b, e.c, e.d, e.e, e.f))
                .collect(Collectors.toList());
    }

    @Override
    public boolean getReplaceAttributeBlock(
            final ac.artemis.packet.minecraft.world.World bbukit,
            final NaivePoint blockPos
    ) {
        final CraftWorld craftWorld = (CraftWorld) bbukit.v();
        final World world = craftWorld.getHandle();

        if (!bbukit.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return false;
        }

        final IBlockData block = world.getType(
                new BlockPosition(
                        blockPos.getX(),
                        blockPos.getY(),
                        blockPos.getZ()
                )
        );

        return block.getBlock().getMaterial().isReplaceable();
    }
}


