package ac.artemis.core.v4.utils.blocks;

import ac.artemis.packet.minecraft.entity.EntityType;
import lombok.AllArgsConstructor;

/**
 * @author Ghast
 * @since 23-Mar-20
 */

@AllArgsConstructor
public enum EntityBoundingEnum {
    BAT(0.5, 0.9, EntityType.BAT),
    BLAZE(0.6, 1.8, EntityType.BLAZE),
    CAVE_SPIDER(0.7, 0.5, EntityType.CAVE_SPIDER),
    CHICKEN(0.4, 0.7, EntityType.CHICKEN),
    COW(0.9, 1.4, EntityType.COW),
    CREEPER(0.6, 1.7, EntityType.CREEPER),
    //DONKEY(1.3964844, 1.6, EntityType.DO)
    ENDERMAN(0.6, 2.9, EntityType.ENDERMAN),
    ENDERMITE(0.4, 0.3, EntityType.ENDERMITE),
    GHAST(4, 4, EntityType.GHAST),
    GIANT(3.6, 10.8, EntityType.GIANT),
    HORSE(1.3964844, 1.6, EntityType.HORSE),
    MOOSHROOM(0.9, 1.4, EntityType.MUSHROOM_COW),
    OCELOT(0.6, 0.7, EntityType.OCELOT),
    PIG(0.9, 0.9, EntityType.PIG),
    PIG_ZOMBIE(0.6, 1.95, EntityType.PIG_ZOMBIE),
    RABBIT(0.4, 0.5, EntityType.RABBIT),
    SHEEP(0.9, 1.3, EntityType.SHEEP),
    SILVERFISH(0.4, 0.3, EntityType.SILVERFISH),
    SKELETON(0.6, 1.99, EntityType.SKELETON),
    //SKELETON_HORSE(1.3964844, EntityType.HORSE)
    SNOW_MAN(0.7, 1.9, EntityType.SNOWMAN),
    SPIDER(1.4, 0.9, EntityType.SPIDER),
    SQUID(0.8, 0.8, EntityType.SQUID),
    VILLAGER(0.6, 1.95, EntityType.VILLAGER),
    IRON_GOLEM(1.4, 2.7, EntityType.IRON_GOLEM),
    WITCH(0.6, 1.95, EntityType.WITCH),
    WITHER(0.9, 3.5, EntityType.WITHER),
    WOLF(0.6, 0.85, EntityType.WOLF),
    ZOMBIE(0.6, 1.95, EntityType.ZOMBIE),
    PLAYER(0.8, 1.8, EntityType.PLAYER);

    private double xz, y;
    private EntityType type;
}
