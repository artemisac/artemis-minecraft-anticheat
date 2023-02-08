package ac.artemis.core.v4.nms.minecraft;

import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.bounding.Vec3d;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;

import cc.ghast.packet.wrapper.bukkit.BlockPosition;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author Ghast
 * @since 23-Mar-20
 */
public interface INMS {
    @Deprecated
    BoundingBox getBoundingBoxBlock(Block block);

    @Deprecated
    List<BoundingBox> getCollidingBoxes(Entity player, BoundingBox playerPosition);

    @Deprecated
    default List<BoundingBox> getCollidingBoxes(Entity player, BoundingBox playerPosition, List<Block> ghostBlocks) {
        return getCollidingBoxes(player, playerPosition);
    }

    BoundingBox getEntityBoundingBox(Entity entity);

    Vec3d getMotion(Player player);

    Set<Material> getCollidingBlocks(BoundingBox bb, World world);

    List<Entity> getEntitiesInAABBexcluding(Player player, BoundingBox boundingBox, Predicate<Entity> predicate);

    Point getModifiedAcceleration(World world, BlockPosition blockPosition, Entity entity, Point point);

    Entity getEntity(World world, int id);

    double getTps();

    List<BoundingBox> getCollidingEntities(final Entity entity, final World world, final BoundingBox boundingBox);

    boolean getReplaceAttributeBlock(final World bbukit, final NaivePoint blockPos);
}
