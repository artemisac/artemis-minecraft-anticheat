package ac.artemis.core.v4.check.exempt.type;

import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.protocol.ProtocolVersion;
import lombok.Getter;

import java.util.function.Function;

public enum ExemptType {
    /**
     * Exempt when the data is receiving velocity
     */
    VELOCITY(playerData -> playerData.getPrediction().getVelocityTicks() < 3),

    /**
     * Exempt when the data got teleported
     */
    TELEPORT(playerData -> playerData.getMovement().getTeleportTicks() > 4 || System.currentTimeMillis() - playerData.getMovement().getLastTeleportOutbound() < 250L),

    /**
     * Exempt if the data did not move
     */
    MOVEMENT(playerData -> playerData.getMovement().getLastLocation() != null && playerData.getMovement().getLastLocation().distanceSquare(playerData.getMovement().getLocation()) < 9.0E-4D),

    /**
     * Exempt if the player collides with ground
     */
    GROUND(playerData -> playerData.getMovement().isOnGround()),

    /**
     * Exempt if the data is in the void
     */
    VOID(playerData -> playerData.getMovement().getLocation() != null && playerData.getMovement().getLocation().getY() < 4),

    /**
     * Exempt if the data is placing or digging a block
     */
    INTERACT(playerData -> playerData.getUser().isDigging() || System.currentTimeMillis() - playerData.getUser().getLastPlace() < 100L || playerData.getUser().isFakeDigging() || System.currentTimeMillis() - playerData.getUser().getLastDig() < 200L),

    /**
     * Exempt if the data is in a vehicle or interacting with one
     */
    VEHICLE(playerData -> playerData.getPrediction().isInVehicle() || playerData.getCollision().isCollidesBoat() || playerData.getCollision().isHasLeftVehicle()),

    /**
     * World change to prevent weird teleport changes
     */
    WORLD(playerData -> playerData.getMovement().getLastLocation() != null && playerData.getMovement().getLastLocation().getWorld().v() != playerData.getPlayer().getWorld().v()),

    /**
     * Factors which may impact the velocity of a data
     */
    STEPABLE(playerData -> playerData.getMovement().isOnStair() || playerData.getMovement().isOnSlab()),

    STAIRS(playerData -> playerData.getMovement().isOnStair()),

    /**
     * Factors which may impact the speed of a player in water
     */
    STRIDER(playerData -> playerData.getPlayer().getInventory().getBoots() != null && playerData.getPlayer().getInventory().getBoots().getEnchants().values().stream().anyMatch(i -> i == 8)),

    /**
     * Exempt if the player has the ability to fucking fly
     */
    FLIGHT(playerData -> playerData.getPlayer().isFlying() || playerData.getPlayer().isAllowedFlight()),

    /**
     * Exempt if the player can somehow walk on liquids
     */
    LIQUID_WALK(playerData -> playerData.getPlayer().isFlying()
            || playerData.user.isOnLilyPad()
            || playerData.movement.isOnIce()),

    /**
     * Exempt if the player collides with liquids
     */
    LIQUID(playerData -> playerData.collision.getCollidingMaterials1().contains(NMSMaterial.WATER)
            || playerData.collision.getCollidingMaterials1().contains(NMSMaterial.LAVA)),

    /**
     * Exempt if the player is on a piston
     */
    PISTON(playerData -> playerData.collision.getCollidingMaterials1().contains(NMSMaterial.PISTON)
            || playerData.collision.getCollidingMaterials1().contains(NMSMaterial.MOVING_PISTON)
            || playerData.collision.getCollidingMaterials1().contains(NMSMaterial.PISTON_HEAD)
    ),

    /**
     * Exempt if the player's version is in shitty 1.9-1.15.2 - NFP stands for No Flying Packet
     */
    NFPGAY(playerData -> playerData.getVersion().isOrAbove(ProtocolVersion.V1_9) && playerData.getVersion().isOrBelow(ProtocolVersion.V1_15_2)),

    /**
     * Exempt if the player has delayed flying packets
     */
    LAGGING(playerData -> playerData.getExemptManager().isExempt(ExemptType.NFPGAY) ? !TimeUtil.elapsed(playerData.movement.getLastDelayedFlyingPacket(), 250L) && !playerData.movement.isMoving() : !TimeUtil.elapsed(playerData.movement.getLastDelayedFlyingPacket(), 250L)),

    /**
     * Returns true if the tps of the server is too low to ensure check stability
     */
    TPS(playerData -> NMSManager.getInms().getTps() < 18),

    /**
     * Returns true if the player is placing a block
     */
    PLACING(playerData -> playerData.user.isPlaced()),

    /**
     * Exempt if the player is on slimes
     */
    SLIME(playerData -> playerData.getMovement().isSlimeVelocity()
            || playerData.collision.getCollidingMaterials1().contains(NMSMaterial.SLIME_BLOCK)),

    /**
     * Exempt if the player just only joined
     */
    JOIN(playerData -> !TimeUtil.hasExpired(playerData.user.getJoin(), 10)),

    /**
     * Exempt if the player collides with another entity
     */
    COLLIDE_ENTITY(playerData -> playerData.collision.isCollidesCollideable()),

    /**
     * Exempt if data is respawning
     */
    RESPAWN(playerData -> !TimeUtil.hasExpired(playerData.user.getLastRespawn(), 3) || playerData.movement.getRespawnTicks() > 0),

    /**
     * Exempt if the data is on snow
     */
    SNOW(playerData -> playerData.getCollision().getCollidingMaterials1().contains(NMSMaterial.SNOW)),

    /**
     * Exempt if the data is using an item
     */
    ITEM(playerData -> playerData.getEntity().isUsingItem()),

    /**
     * Exempt if the data is under a block
     */
    UNDERBLOCK(playerData -> playerData.getMovement().isUnderBlock()),

    /**
     * Exempt if the data is on a ladder
     */
    LADDER(playerData -> playerData.getMovement().isOnLadder()),

    /**
     * Exempt if the data is on a ladder
     */
    WEB(playerData -> playerData.getMovement().isInWeb()),

    /**
     * Exempt if the data is in a combat
     */
    COMBAT(playerData -> !TimeUtil.hasExpired(playerData.getCombat().getLastAttack(), 2)),

    /**
     * Exempt if the data is in a combat
     */
    NOT_COMBAT(playerData -> TimeUtil.hasExpired(playerData.getCombat().getLastAttack(), 3)),

    /**
     * Exempt if the data is collided horizontally
     */
    COLLIDED_HORIZONTALLY(playerData -> playerData.entity.isCollidedHorizontally()),

    /**
     * Exempt if the data is collided vertically
     */
    COLLIDED_VERTICALLY(playerData -> playerData.entity.isCollidedVertically()),

    ZERO_ZERO_THREE(playerData -> !playerData.prediction.isPos() || !playerData.prediction.isLastPos() || !playerData.prediction.isLastLastPos()),

    /**
     * Exempt if data is not in a valid gamemode
     */
    GAMEMODE(playerData -> !playerData.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !playerData.getPlayer().getGameMode().name().toUpperCase().equalsIgnoreCase("ADVENTURE"));

    @Getter
    private final Function<PlayerData, Boolean> function;

    ExemptType(final Function<PlayerData, Boolean> function) {
        this.function = function;
    }
}
