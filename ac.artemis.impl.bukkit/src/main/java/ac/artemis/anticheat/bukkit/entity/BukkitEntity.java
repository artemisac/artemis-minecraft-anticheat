package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.anticheat.bukkit.BukkitLocation;
import ac.artemis.anticheat.bukkit.BukkitWorld;
import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.EntityType;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import org.bukkit.entity.*;

import java.util.UUID;

public class BukkitEntity<T extends org.bukkit.entity.Entity> extends AbstractWrapper<T> implements Entity {
    protected BukkitEntity(T wrapper) {
        super(wrapper);
    }

    @Override
    public UUID getUniqueId() {
        return wrapper.getUniqueId();
    }

    @Override
    public World getWorld() {
        return new BukkitWorld(wrapper.getWorld());
    }

    @Override
    public Location getLocation() {
        return new BukkitLocation(wrapper.getLocation());
    }

    @Override
    public EntityType getType() {
        return EntityType.values()[wrapper.getType().ordinal()];
    }

    @Override
    public int getEntityId() {
        return wrapper.getEntityId();
    }

    @Override
    public boolean isDead() {
        return wrapper.isDead();
    }

    @Override
    public void teleport(Location location) {
        wrapper.teleport((org.bukkit.Location) location.v());
    }

    @Override
    public void sendMessage(String s) {
        wrapper.sendMessage(s);
    }

    public static BukkitEntity<?> of(final org.bukkit.entity.Entity entity) {
        switch (entity.getType()) {
            default:
                return new BukkitEntity<>(entity);
            case PLAYER:
                return new BukkitPlayer((Player) entity);
            case ARROW:
                return new BukkitArrow((Arrow) entity);
            case MINECART:
            case MINECART_CHEST:
            case MINECART_COMMAND:
            case MINECART_FURNACE:
            case MINECART_HOPPER:
            case MINECART_MOB_SPAWNER:
            case MINECART_TNT:
                return new BukkitMinecart((Minecart) entity);
            case FIREWORK:
                return new BukkitFirework((Firework) entity);
            case ZOMBIE:
                return new BukkitZombie((Zombie) entity);
            case SKELETON:
            case WITHER_SKELETON:
                return new BukkitSkeleton((Skeleton) entity);
            case SILVERFISH:
                return new BukkitSilverfish((Silverfish) entity);
            case HORSE:
                return new BukkitHorse((Vehicle) entity);
            case SPIDER:
            case CAVE_SPIDER:
                return new BukkitSpider((LivingEntity) entity);
            case ENDERMITE:
                return new BukkitEndermite((Endermite) entity);
            case WITHER:
                return new BukkitWither((Wither) entity);
            case ARMOR_STAND:
                return new BukkitArmorStand((ArmorStand) entity);
        }
    }
}
