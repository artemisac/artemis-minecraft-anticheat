package ac.artemis.anticheat.bukkit.nms.v1_7;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.bukkit.entity.BukkitEntity;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.Vec3d;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ghast
 * @since 23-Mar-20
 */
public class NMS_v1_7_10 implements INMS {
    @Override
    public BoundingBox getBoundingBoxBlock(
            final ac.artemis.packet.minecraft.block.Block block
    ) {
        final CraftWorld craftWorld = (CraftWorld) block.getWorld().v();
        final WorldServer world = craftWorld.getHandle();

        final net.minecraft.server.v1_7_R4.Block iBlockData = world.getType(
                block.getX(),
                block.getY(),
                block.getZ()
        );
        final AxisAlignedBB bb = iBlockData.a(
                world,
                block.getX(),
                block.getY(),
                block.getZ()
        );

        return new BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
    }

    @Override
    public List<BoundingBox> getCollidingBoxes(
            final ac.artemis.packet.minecraft.entity.Entity player,
            final BoundingBox bb
    ) {
        final CraftEntity craftPlayer = (CraftEntity) player.v();
        final Entity player1 = craftPlayer.getHandle();
        final World world = player1.world;

        if (!world.isLoaded(
                MathHelper.floor(player1.locX),
                MathHelper.floor(player1.locY),
                MathHelper.floor(player1.locZ)))
            return new ArrayList<>();

        final AxisAlignedBB axisalignedbb = AxisAlignedBB.a(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.maxY,
                bb.maxZ
        );
        final List<AxisAlignedBB> axises = new ArrayList<>();

        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        // Spigot start
        int ystart = Math.max((k - 1), 0);
        for ( int chunkx = ( i >> 4 ); chunkx <= ( ( j - 1 ) >> 4 ); chunkx++ )
        {
            int cx = chunkx << 4;
            for ( int chunkz = ( i1 >> 4 ); chunkz <= ( ( j1 - 1 ) >> 4 ); chunkz++ )
            {
                Chunk chunk = world.getChunkIfLoaded( chunkx, chunkz );
                if ( chunk == null )
                {
                    continue;
                    // PaperSpigot end
                }
                int cz = chunkz << 4;
                // Compute ranges within chunk
                int xstart = Math.max(i, cx);
                int xend = Math.min(j, (cx + 16));
                int zstart = Math.max(i1, cz);
                int zend = Math.min(j1, (cz + 16));
                // Loop through blocks within chunk
                for ( int x = xstart; x < xend; x++ )
                {
                    for ( int z = zstart; z < zend; z++ )
                    {
                        for ( int y = ystart; y < l; y++ )
                        {
                            net.minecraft.server.v1_7_R4.Block block = chunk.getType(x - cx, y, z - cz );
                            if ( block != null )
                            {
                                // PaperSpigot start - FallingBlocks and TNT collide with specific non-collidable blocks
                                block.a( world, x, y, z, axisalignedbb, axises, player1 );
                                // PaperSpigot end
                            }
                        }
                    }
                }
            }
        }
        // Spigot end

        double d0 = 0.25D;
        List<Entity> list = world.getEntities(player1, axisalignedbb.grow(d0, d0, d0));

        for (Object o : list) {
            AxisAlignedBB axisalignedbb1 = ((Entity) o).J();

            if (axisalignedbb1 != null && axisalignedbb1.b(axisalignedbb)) {
                axises.add(axisalignedbb1);
            }

            axisalignedbb1 = player1.h((Entity) o);
            if (axisalignedbb1 != null && axisalignedbb1.b(axisalignedbb)) {
                axises.add(axisalignedbb1);
            }
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
        final List<Entity> axises = world.getEntities(
                player1,
                AxisAlignedBB.a(
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
            final org.bukkit.entity.Entity entityBukkit = ax.getBukkitEntity();
            final ac.artemis.packet.minecraft.entity.Entity entity = BukkitEntity.of(entityBukkit);
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
        final AxisAlignedBB bb = craftEntity.getHandle().boundingBox;

        return new BoundingBox(bb.a, bb.b, bb.c, bb.d, bb.e, bb.f);
    }

    @Override
    public Vec3d getMotion(final Player player) {
        final CraftPlayer craftPlayer = (CraftPlayer) player.v();
        final EntityPlayer playerE = craftPlayer.getHandle();

        return new Vec3d(playerE.motX, playerE.motY, playerE.motZ);
    }

    public Set<Material> getDistinctBlocks(AxisAlignedBB axisalignedbb, World world) {
        Set<Material> arraylist = new HashSet<>();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.floor(axisalignedbb.d + 1.0D);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.floor(axisalignedbb.e + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.floor(axisalignedbb.f + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = i1; l1 < j1; ++l1) {
                if (world.isLoaded(k1, 64, l1)) {
                    for (int i2 = k - 1; i2 < l; ++i2) {
                        net.minecraft.server.v1_7_R4.Block iblockdata;

                        if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
                            iblockdata = world.getType(k1, i2, l1);
                        } else {
                            iblockdata = Blocks.BEDROCK;
                        }
                        NMSMaterial.matchNMSMaterial(net.minecraft.server.v1_7_R4.Block
                                .getId(iblockdata), (byte) 0).ifPresent(e -> {
                            Material material = e.parseMaterial();
                            if (material != null && !arraylist.contains(material)) arraylist.add(material);
                        });
                    }
                }
            }
        }

        return arraylist;
    }

    @Override
    public Set<Material> getCollidingBlocks(
            final BoundingBox bb,
            final ac.artemis.packet.minecraft.world.World world
    ) {
        final CraftWorld craftWorld = (CraftWorld) world.v();
        final WorldServer worldServer = craftWorld.getHandle();

        final AxisAlignedBB axisAlignedBB = AxisAlignedBB.a(
                bb.minX,
                bb.minY,
                bb.minZ,
                bb.maxX,
                bb.maxY,
                bb.maxZ
        );

        return getDistinctBlocks(axisAlignedBB, worldServer);
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

        final net.minecraft.server.v1_7_R4.Block blockData = world.getType(
                block.getX(),
                block.getY(),
                block.getZ()
        );

        if (!(blockData instanceof BlockFluids))
            return point;

        final Vec3D vec3D = Vec3D.a(point.getX(), point.getY(), point.getZ());
        blockData.a(
                world,
                block.getX(),
                block.getY(),
                block.getZ(),
                ((CraftEntity) entity.v()).getHandle(),
                vec3D
        );

        return new Point(vec3D.a, vec3D.b, vec3D.c);
    }

    @Override
    public ac.artemis.packet.minecraft.entity.Entity getEntity(
            final ac.artemis.packet.minecraft.world.World world,
            final int id
    ) {
        final CraftWorld craftWorld = (CraftWorld) world.v();
        final Entity entity = craftWorld.getHandle().getEntity(id);

        return entity == null
                ? null
                : BukkitEntity.of(entity.getBukkitEntity());
    }

    @Override
    public double getTps() {
        return MinecraftServer.getServer().recentTps[0];
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
        final List<AxisAlignedBB> bbs = new ArrayList<>();

        world.getEntities(entity, aabb.grow(0.25D, 0.25D, 0.25D))
                .forEach(f -> {
                    final Entity e = (Entity) f;
                    if (entity == null || (entity.passenger != e && entity.vehicle != e)) {
                        AxisAlignedBB axisalignedbb1 = e.J();
                        if (axisalignedbb1 != null && axisalignedbb1.b(aabb)) {
                            bbs.add(axisalignedbb1);
                        }

                        if (entity != null) {
                            axisalignedbb1 = entity.h(e);
                            if (axisalignedbb1 != null && axisalignedbb1.b(aabb)) {
                                bbs.add(axisalignedbb1);
                            }
                        }
                    }
                });

        return bbs.stream()
                .map(e -> new BoundingBox(e.a, e.b, e.c, e.d, e.e, e.f))
                .collect(Collectors.toList());
    }

    @Override
    public boolean getReplaceAttributeBlock(
            final ac.artemis.packet.minecraft.world.World bbukit,
            final NaivePoint blockPos
    ) {
        final World world = ((CraftWorld) bbukit).getHandle();
        final net.minecraft.server.v1_7_R4.Block block = world.getType(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ()
        );

        return block.getMaterial().isReplaceable();
    }
}


