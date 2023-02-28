package ac.artemis.packet.minecraft.entity.impl;

import ac.artemis.packet.minecraft.entity.Human;

public interface Player extends Human {

    boolean hasPermission(final String permission);

    void sendJsonMessage(final String message);

    void kickPlayer(String reason);

    int getFoodLevel();

    boolean isSleeping();

    boolean isSneaking();

    boolean isAllowedFlight();

    boolean isFlying();

    boolean isInvulnerable();

    boolean isInsideVehicle();

    boolean isOp();

    float getFlySpeed();

    float getWalkSpeed();

    float getEyeHeight();

    boolean hasMetadata(final String key);
}
