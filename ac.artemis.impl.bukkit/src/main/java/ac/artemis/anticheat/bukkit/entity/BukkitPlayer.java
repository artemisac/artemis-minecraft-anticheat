package ac.artemis.anticheat.bukkit.entity;

import ac.artemis.anticheat.bukkit.BukkitEffect;
import ac.artemis.anticheat.bukkit.BukkitInventory;
import ac.artemis.anticheat.bukkit.BukkitWorld;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.inventory.Inventory;
import ac.artemis.packet.minecraft.potion.Effect;
import ac.artemis.packet.minecraft.world.World;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class BukkitPlayer extends BukkitHuman<org.bukkit.entity.Player> implements Player {
    public BukkitPlayer(org.bukkit.entity.Player wrapper) {
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
    public int getEntityId() {
        return wrapper.getEntityId();
    }

    @Override
    public String getName() {
        return wrapper.getName();
    }

    @Override
    public GameMode getGameMode() {
        return GameMode.values()[wrapper.getGameMode().ordinal()];
    }

    @Override
    public Inventory getInventory() {
        // TODO: Add caching
        return new BukkitInventory(wrapper.getInventory());
    }

    @Override
    public boolean hasPermission(String permission) {
        return wrapper.hasPermission(permission);
    }

    @Override
    public void sendJsonMessage(String s) {
        wrapper.spigot().sendMessage(TextComponent.fromLegacyText(s));
    }

    @Override
    public void sendMessage(String message) {
        wrapper.sendMessage(message);
    }

    @Override
    public void kickPlayer(String reason) {
        wrapper.kickPlayer(reason);
    }

    @Override
    public int getFoodLevel() {
        return wrapper.getFoodLevel();
    }

    @Override
    public boolean isSleeping() {
        return wrapper.isSleeping();
    }

    @Override
    public boolean isSneaking() {
        return wrapper.isSneaking();
    }

    @Override
    public boolean isAllowedFlight() {
        return wrapper.getAllowFlight();
    }

    @Override
    public boolean isFlying() {
        return wrapper.isFlying();
    }

    @Override
    public boolean isInvulnerable() {
        return wrapper.isInvulnerable();
    }

    @Override
    public boolean isInsideVehicle() {
        return wrapper.isInsideVehicle();
    }

    @Override
    public boolean isOp() {
        return wrapper.isOp();
    }

    @Override
    public float getFlySpeed() {
        return wrapper.getFlySpeed();
    }

    @Override
    public float getWalkSpeed() {
        return wrapper.getWalkSpeed();
    }

    @Override
    public float getEyeHeight() {
        return (float) wrapper.getEyeHeight();
    }

    @Override
    public boolean hasMetadata(String s) {
        return wrapper.hasMetadata(s);
    }
}
