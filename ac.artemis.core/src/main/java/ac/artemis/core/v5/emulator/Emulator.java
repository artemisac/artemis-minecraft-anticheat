package ac.artemis.core.v5.emulator;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.emulator.datawatcher.DataWatcher;
import ac.artemis.core.v4.emulator.entity.utils.PlayerCapabilities;
import ac.artemis.core.v4.emulator.entity.utils.PlayerControls;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherReader;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerExplosion;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;

import java.util.List;

/**
 * The interface all Emulators must use to create an... emulator. Yes.
 * This should provide motion, sprinting status, jump status, offsets
 * etc...
 */
public interface Emulator {
    /**
     * Gets the PlayerData of the loaded Emulator
     *
     * @return PlayerData object of the instance
     */
    PlayerData getData();

    /**
     * Gets the Emulator position as a PlayerMovement object.
     *
     * @return PlayerMovement object of the current position
     * @see PlayerMovement
     */
    PlayerMovement toMovement();

    /**
     * Gets the BoundingBox of the emulated entity
     *
     * @return BoundingBox object of the current entity
     */
    BoundingBox getEntityBoundingBox();

    /**
     * Sets the BoundingBox of the current emulated entity
     *
     * @param entityBoundingBoxIn BoundingBox object
     */
    void setEntityBoundingBox(BoundingBox entityBoundingBoxIn);

    /**
     * Gets ghost blocks.
     *
     * @return the ghost blocks
     */
    @Deprecated
    List<Block> getGhostBlocks();

    /**
     * Gets jump boost amplifier.
     *
     * @param player the player
     * @return the jump boost amplifier
     */
    @Deprecated
    int getJumpBoostAmplifier(Player player);

    /**
     * Gets step height.
     *
     * @return the step height
     */
    float getStepHeight();

    /**
     * Is chunk loaded boolean.
     *
     * @return the boolean
     */
    boolean isChunkLoaded();

    /**
     * Is flying boolean.
     *
     * @return the boolean
     */
    boolean isFlying();

    /**
     * Is in web boolean.
     *
     * @return the boolean
     */
    boolean isInWeb();

    /**
     * Is sneaking boolean.
     *
     * @return the boolean
     */
    boolean isSneaking();

    /**
     * Is sprinting boolean.
     *
     * @return the boolean
     */
    boolean isSprinting();

    /**
     * Is attribute sprinting boolean.
     *
     * @return the boolean
     */
    boolean isAttributeSprinting();

    /**
     * Is last sprinting boolean.
     *
     * @return the boolean
     */
    boolean isLastSprinting();

    /**
     * Is using item boolean.
     *
     * @return the boolean
     */
    boolean isUsingItem();

    /**
     * Is collided horizontally boolean.
     *
     * @return the boolean
     */
    boolean isCollidedHorizontally();

    /**
     * Is collided vertically boolean.
     *
     * @return the boolean
     */
    boolean isCollidedVertically();

    /**
     * Is was pos boolean.
     *
     * @return the boolean
     */
    boolean isWasPos();

    /**
     * Sets was pos.
     *
     * @param value the value
     */
    void setWasPos(boolean value);

    /**
     * Gets motion x.
     *
     * @return the motion x
     */
    double getMotionX();

    /**
     * Gets motion y.
     *
     * @return the motion y
     */
    double getMotionY();

    /**
     * Gets motion z.
     *
     * @return the motion z
     */
    double getMotionZ();

    /**
     * Gets max distance.
     *
     * @return the max distance
     */
    double getMaxDistance();

    /**
     * Sets max distance.
     *
     * @param value the value
     */
    void setMaxDistance(double value);

    /**
     * Gets max motion x.
     *
     * @return the max motion x
     */
    double getMaxMotionX();

    /**
     * Gets max motion y.
     *
     * @return the max motion y
     */
    double getMaxMotionY();

    /**
     * Gets max motion z.
     *
     * @return the max motion z
     */
    double getMaxMotionZ();

    /**
     * Gets rotation yaw.
     *
     * @return the rotation yaw
     */
    float getRotationYaw();

    /**
     * Gets rotation pitch.
     *
     * @return the rotation pitch
     */
    float getRotationPitch();

    /**
     * Is jumping boolean.
     *
     * @return the boolean
     */
    boolean isJumping();

    /**
     * On update.
     */
    void onUpdate();

    /**
     * Sets sprinting.
     *
     * @param sprinting the sprinting
     */
    void setSprinting(boolean sprinting);

    /**
     * Sets last sprinting.
     *
     * @param sprinting the sprinting
     */
    void setLastSprinting(boolean sprinting);

    /**
     * Sets sneaking.
     *
     * @param sneaking the sneaking
     */
    void setSneaking(boolean sneaking);

    /**
     * Sets chunk loaded.
     *
     * @param value the value
     */
    void setChunkLoaded(final boolean value);

    /**
     * Sets ground.
     *
     * @param value the value
     */
    void setGround(final boolean value);

    /**
     * Sets jump ticks.
     *
     * @param ticks the ticks
     */
    void setJumpTicks(final int ticks);

    /**
     * Attack target entity with current item.
     *
     * @param entityIn the entity in
     */
    void attackTargetEntityWithCurrentItem(Entity entityIn);

    /**
     * Gets capabilities.
     *
     * @return the capabilities
     */
    PlayerCapabilities getCapabilities();

    /**
     * Sets item in use.
     *
     * @param stack    the stack
     * @param hand     the hand
     * @param duration the duration
     */
    void setItemInUse(ItemStack stack, PlayerEnums.Hand hand, int duration);

    /**
     * Gets world.
     *
     * @return the world
     */
    ArtemisWorld getWorld();

    /**
     * Gets item in hand.
     *
     * @return the item in hand
     */
    PlayerEnums.Hand getItemInHand();

    /**
     * Clear item in use.
     */
    void clearItemInUse();

    /**
     * Stop using item.
     */
    void stopUsingItem();

    /**
     * Sets health.
     *
     * @param health the health
     */
    void setHealth(float health);

    /**
     * On death.
     *
     * @param cause the cause
     */
    void onDeath(DamageSource cause);

    /**
     * On item use finish.
     */
    void onItemUseFinish();

    /**
     * Reset world.
     */
    void resetWorld();

    /**
     * Prepare to spawn.
     */
    void prepareToSpawn();

    /**
     * Sets velocity.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    void setVelocity(double x, double y, double z);

    /**
     * Add potion effect.
     *
     * @param potionEffectIn the potion effect in
     */
    void addPotionEffect(PotionEffect potionEffectIn);

    /**
     * Remove potion effect.
     *
     * @param potionId the potion id
     */
    void removePotionEffect(int potionId);

    /**
     * Handle player teleport.
     *
     * @param packet the packet
     */
    void handlePacket(GPacketPlayServerEntityTeleport packet);

    /**
     * Handle entity properties.
     *
     * @param packet the packet
     */
    void handlePacket(GPacketPlayServerUpdateAttributes packet);

    /**
     * Handle player pos look.
     *
     * @param packet the packet
     */
    void handlePacket(GPacketPlayServerPosition packet);

    /**
     * Gets data watcher.
     *
     * @return the data watcher
     */
    DataWatcher getDataWatcher();

    /**
     * Gets data watcher factory.
     *
     * @return the data watcher factory
     */
    DataWatcherReader getDataWatcherFactory();

    /**
     * Handle explosion.
     *
     * @param packet the packet
     */
    void handleExplosion(GPacketPlayServerExplosion packet);

    /**
     * Gets last position previous.
     *
     * @return the last position previous
     */
    PlayerMovement getLastPositionPrevious();

    /**
     * Sets last position previous.
     *
     * @param playerMovementIn the player movement in
     */
    void setLastPositionPrevious(PlayerMovement playerMovementIn);

    /**
     * Gets last rotation previous.
     *
     * @return the last rotation previous
     */
    PlayerMovement getLastRotationPrevious();

    /**
     * Sets last rotation previous.
     *
     * @param playerMovementIn the player movement in
     */
    void setLastRotationPrevious(PlayerMovement playerMovementIn);

    /**
     * Is previous ground boolean.
     *
     * @return the boolean
     */
    boolean isPreviousGround();

    /**
     * Sets previous ground.
     *
     * @param previousGroundIn the previous ground in
     */
    void setPreviousGround(boolean previousGroundIn);

    /**
     * Is on ground boolean.
     *
     * @return the boolean
     */
    boolean isOnGround();

    /**
     * Is no clip boolean.
     *
     * @return the boolean
     */
    boolean isNoClip();

    /**
     * Gets position.
     *
     * @return the position
     */
    Point getPosition();

    /**
     * Gets motion.
     *
     * @return the motion
     */
    Velocity getMotion();

    /**
     * Gets player controls.
     *
     * @return the player controls
     */
    PlayerControls getPlayerControls();

    /**
     * Is in lava boolean.
     *
     * @return the boolean
     */
    boolean isInLava();

    /**
     * Is in water boolean.
     *
     * @return the boolean
     */
    boolean isInWater();

    /**
     * Gets move strafing.
     *
     * @return the move strafing
     */
    float getMoveStrafing();

    /**
     * Gets move forward.
     *
     * @return the move forward
     */
    float getMoveForward();

    /**
     * Gets fall distance.
     *
     * @return the fall distance
     */
    float getFallDistance();

    /**
     * Gets jump ticks.
     *
     * @return the jump ticks
     */
    int getJumpTicks();

    /**
     * Gets ai move speed.
     *
     * @return the ai move speed
     */
    double getAIMoveSpeed();

    /**
     * Gets on ladder.
     *
     * @return the on ladder
     */
//ServersideAttributeMap getAttributeMap();
    boolean isOnLadder();

    /**
     * Is potion active boolean.
     *
     * @param potionIn the potion in
     * @return the boolean
     */
    boolean isPotionActive(Potion potionIn);

    /**
     * Gets active potion effect.
     *
     * @param potionIn the potion in
     * @return the active potion effect
     */
    PotionEffect getActivePotionEffect(Potion potionIn);

    /**
     * Sets no clip.
     *
     * @param noClipIn the no clip in
     */
    void setNoClip(boolean noClipIn);

    /**
     * Sets rotation.
     *
     * @param yaw   the yaw
     * @param pitch the pitch
     */
    void setRotation(float yaw, float pitch);

    /**
     * Sets position.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    void setPosition(double x, double y, double z);

    /**
     * Sets server position.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    void setServerPosition(double x, double y, double z);

    /**
     * Read tags string.
     *
     * @return the string
     */
    String readTags();

    /**
     * Gets tags.
     *
     * @return the tags
     */
    List<String> getTags();


}
