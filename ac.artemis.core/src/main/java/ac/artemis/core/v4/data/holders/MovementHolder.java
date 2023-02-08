package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.check.data.MouseFilter;
import ac.artemis.core.v4.check.exempt.ExemptManager;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v4.utils.lists.EvictingArrayList;
import ac.artemis.core.v4.utils.position.*;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.*;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.PlayerEnums;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Getter
@Setter
public class MovementHolder extends AbstractHolder {
    public MovementHolder(PlayerData data) {
        super(data);
    }

    public List<PlayerPosition> playerPositions = new EvictingArrayList<>(10);

    // Positions / Rotations
    public PlayerPosition location, lastLocation;
    public PlayerRotation rotation, lastRotation;
    public PlayerMovement movement, lastMovement;

    public boolean processedTeleport, processedEntityTeleport;

    // Ints (ticks)
    public int teleportTicks,
            respawnTicks,
            sprintTicks,
            standTicks,
            deathTicks,
            flyTicks,
            optifineTicks,
            maxPingTicks,
            fakeGroundTicks;

    // Longs (timings)
    /**
     * Pretty straight forward. Basically the last move packet which has had
     * a delay > 110ms
     *
     * @see GPacket.Client#POSITION
     * @see GPacket.Client#POSITION_LOOK
     * @see PacketPlayClientFlying#isPos()
     */
    public long lastDelayedMovePacket;

    /**
     * Same concept as lastDelayedMovePacket. Basically the last flying packet
     * with a delayt > 110ms
     *
     * @see GPacket.Client#FLYING
     * @see PacketPlayClientFlying
     */
    public long lastDelayedFlyingPacket;

    /**
     * Corresponds to the timing of the last move packet
     *
     * @see cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientPosition
     * @see cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientPositionLook
     * @see PacketPlayClientFlying#isPos()
     */
    public long lastMovePacket;

    /**
     * Corresponds to the timing of the last move packet
     *
     * @see PacketPlayClientFlying#isPos()
     */
    public long lastFlyingPacket;

    /**
     * Corresponds to the timestamp of the last GPacketPlayOutPosition packet sent.
     * Basically for teleportation
     *
     * @see GPacket.Server#POSITION
     * @see GPacketPlayServerPosition
     */
    public long lastTeleportOutbound;

    /**
     * Timestamp of the last velocity packet received.
     * TODO Move this to PlayerData#User
     *
     * @see PlayerData#user
     * @see GPacketPlayServerVelocity
     */
    public long lastVelocityPacket;


        /*
         __          __        _     _
         \ \        / /       | |   | |
          \ \  /\  / /__  _ __| | __| |
           \ \/  \/ / _ \| '__| |/ _` |
            \  /\  / (_) | |  | | (_| |
             \/  \/ \___/|_|  |_|\__,_|
         */

    /**
     * -- JUMP --
     * Corresponds to the last time deltaY > lastDeltaY by the jump value.
     * TODO check if user is on slab, ladder etc...
     */
    public long lastJump;

    /**
     * -- BLOCK --
     * Last time user was on a solid block (not a bad velocity one)
     * TODO Do the implementation, hella useful
     */
    public boolean onBlock;
    public long lastOnBlock;

    public void setOnBlock(boolean value, long timestamp) {
        this.onBlock = value;

        if (!value) return;
        this.lastOnBlock = timestamp;
    }

    // TODO COMPENSATOR WITH DELTAY!!
    public boolean isOnBlock() {
        return onBlock;
    }

    /**
     * -- MOVE CANCEL --
     * Last time a user's movement was cancelled. This is checked in
     * The GPacketPlayOutPosition if the lastMovement's distance to movement == 0
     * TODO Actually implement this
     */
    public long lastMoveCancel;

    public void setMoveCancel(boolean value, long timestamp) {
        if (!value) return;

        this.lastMoveCancel = timestamp;
    }


    /**
     * -- GROUND --
     * Corresponds to the last time a processor's bounding box collided with no element at
     * a 0.1 expansion radius.
     * TODO Check for liquids/Bad velocity items which do not make the processor onGround (eg: Water)
     */
    public boolean onGround, wasOnGround, onGroundCollision;
    public long lastOnGround;
    public PlayerEnums.AirType airType;

    public void setOnGround(boolean value, PlayerEnums.AirType type, long timestamp) {
        this.wasOnGround = this.onGround;
        this.onGround = value;
        this.airType = type;

        if (!value) return;

        this.lastOnGround = timestamp;
    }


    // TODO Lag compensation
    public boolean isOnGround() {
        return onGround;
    }

    public boolean isOnGroundCollision() {
        return onGroundCollision;
    }

    public void setOnGroundCollision(boolean onGroundCollision) {
        this.onGroundCollision = onGroundCollision;
    }

    /**
     * -- ICE --
     * Corresponds to the last time block#below contains the Material ICE / PACKED_ICE
     */
    public boolean wasOnIce, onIce;
    public long lastOnIce;

    public void setOnIce(boolean value, long timestamp) {
        this.wasOnIce = this.onIce;
        this.onIce = value;

        if (!value) return;
        this.lastOnIce = timestamp;
    }

    public boolean isStrider() {
        return data.exemptManager.isExempt(ExemptType.STRIDER);
    }


    // TODO Lag compensation
    public boolean isOnIce() {
        return onIce;
    }

    /**
     * -- SLIME --
     * Corresponds to the last time any sort of interaction with slime has occurred. Highly
     * recommended to not use this but to instead use the isSlimeVelocity value. This can be
     * however a source of a disabler/bypass, keep such in mind. When messing around with this,
     * make sure to properly compensate.
     */
    public boolean onSlime;
    public long lastSlime;

    public void setOnSlime(boolean value, long timestamp) {
        this.onSlime = value;

        if (!value) return;
        this.lastSlime = timestamp;
    }

    // TODO COMPENSATOR WITH DELTAY!!
    public boolean isOnSlime() {
        return onSlime;
    }

    /**
     * -- SOUL SAND --
     * Soul Sand has the characteristic of messing with a processor's Y coordinate. It isn't exactly
     * standard height and counts as a change in delta.
     */
    public boolean onSoulSand;
    public long lastOnSoulSand;

    public void setOnSoulSand(boolean value, long timestamp) {
        this.onSoulSand = value;

        if (!value) return;
        this.lastOnSoulSand = timestamp;
    }

    // TODO COMPENSATOR WITH DELTAY!!
    public boolean isOnSoulSand() {
        return onSoulSand;
    }

    /**
     * -- UNDER BLOCK --
     * The last value under block is counted when the block#above is not equal to AIR. This however can be
     * Counted as a liquid or any sort of absurd things.
     * TODO Compensate for liquids and bad velocity items, compensate for half slabs.
     */
    public boolean underBlock, wasUnderBlock, underBlockVelocity;
    public long lastUnderBlock;

    public void setUnderBlock(boolean value, long timestamp) {
        this.wasUnderBlock = this.underBlock;
        this.underBlock = value;
        this.underBlockVelocity = value;

        if (!value) return;
        this.lastUnderBlock = timestamp;
    }

    // TODO COMPENSATOR WITH DELTAY!!
    public boolean isUnderBlock() {
        return underBlockVelocity;
    }


    /**
     * -- STAIRS --
     * Corresponds to the last interaction with stairs. Due to the nature of stairs, it
     * can be annoying to deal with them. Current value checks for Block#below and Block#inside
     * TODO have better calculation for it
     */
    public boolean onStair;
    public long lastOnStairs;

    public void setOnStair(boolean value, long timestamp) {
        this.onStair = value;

        if (!value) return;
        this.lastOnStairs = timestamp;

    }

    // TODO Compensate with lag
    public boolean isOnStair() {
        return onStair;
    }


    /**
     * -- SLAB --
     * Corresponds to the last time the user was on a slab. This can impact the transition from a block
     * To another with different Y-Axis as the distance is below required to automatically adjust the height.
     * This can false a lot of checks if not checked for correctly
     */
    public boolean onSlab;
    public long lastOnSlab;

    public void setOnSlab(boolean value, long timestamp) {
        this.onSlab = value;

        if (!value) return;
        this.lastOnSlab = timestamp;
    }

    // TODO Compensate with ping by substracting lastOnSlab to ping as a requirement!
    public boolean isOnSlab() {
        return onSlab;
    }

    /**
     * -- TRAPDOOR [IN] --
     * Corresponds to the last time the user was literally in the trapdoor. This can be done by placing yourself
     * In the trapdoor block and then closing it. This will impact the motion of the user. To be careful with.
     * The value is checked with Block#above
     */
    public boolean inTrapdoor;
    public long lastInTrapdoor;

    public void setInTrapdoor(boolean value, long timestamp) {
        this.inTrapdoor = value;

        if (!value) return;
        this.lastInTrapdoor = timestamp;
    }

    // TODO Lag compensation
    public boolean isInTrapdoor() {
        return inTrapdoor;
    }


    /**
     * -- TRAPDOOR [ON] --
     * Corresponds to the last time the user was on a trapdoor. This is checked with Block#air (the block the user
     * Is on) as the trapdoor must be above ground facing downwards for it to impact motion.
     */
    public boolean onTrapdoor;
    public long lastOnTrapdoor;

    public void setOnTrapdoor(boolean value, long timestamp) {
        this.onTrapdoor = value;

        if (!value) return;
        this.lastOnTrapdoor = timestamp;
    }


    // TODO Lag compensation
    public boolean isOnTrapdoor() {
        return onTrapdoor;
    }

    /**
     * -- LIQUID --
     * Corresponds to the last time the user was in any sort of liquid. This checks for the presence of Water and
     * Lava liquids in the radius of box#size + 0.1. If collision is detected, this will be updated to the move packet
     * timestamp.
     */
    public boolean inLiquid, wasInLiquid;
    public long lastInLiquid;

    public void setInLiquid(boolean value, long timestamp) {
        this.wasInLiquid = this.inLiquid;
        this.inLiquid = value;

        if (!value) return;
        this.lastInLiquid = timestamp;
    }


    // TODO Lag compensation
    public boolean isInLiquid() {
        return inLiquid;
    }


    /**
     * -- LADDER --
     * Corresponds to the last interaction with a ladder. This checks if Block#air and Block#above are ladders or vines.
     * Vines have an identical behaviour to ladders however those allow direct collision with them since they are
     * Not occluding. In terms of motionY changes, they hold the same. I don't see the need to add them to a particular
     * Variable. For now it'll remain default
     *
     * @see NMSMaterial#LADDER
     */
    public boolean onLadder;
    public long lastOnLadder;

    public void setOnLadder(boolean value, long timestamp) {
        this.onLadder = value;

        if (!value) return;
        this.lastOnLadder = timestamp;
    }


    // TODO Lag compensation
    public boolean isOnLadder() {
        return onLadder;
    }


    /**
     * -- WEB --
     * Corresponds to the last collision with a Web block. Web blocks cause an interesting variance of the velocity
     * of a user. By default, they cancel out the motion of a processor yet make them sink. I need to document them
     * more.
     */
    public boolean inWeb;
    public long lastInWeb;

    public void setInWeb(boolean value, long timestamp) {
        this.inWeb = value;

        if (!value) return;
        this.lastInWeb = timestamp;
    }


    // TODO Lag compensation
    public boolean isInWeb() {
        return inWeb;
    }


    /**
     * -- SPRINTING --
     * Represents whether the user is sprinting or not. This is highly recommended to use above Player#isSprinting
     * As this represents the raw information before it's sent to Spigot. This is, hence, more accurate and not a
     * whole tick behind.
     */
    @Deprecated
    public boolean sprinting;

    public void setSprinting(boolean value, long timestamp) {
        this.sprinting = value;

        if (!value) return;
        // todo last sprint
    }

    // TODO Sprinting Grace
    @Deprecated
    public boolean isSprinting() {
        return sprinting;
    }

    /**
     * -- BED --
     * Indicates whether the user is on a bed or not. The bed, alike slabs, allows the user to have elevation
     * when colliding with it.
     */

    public boolean onBed;


    /**
     * -- FALLING --
     * Represents whether the processor is falling or not. This is calculated with if deltaY < 0 and the user is not
     * in either a liquid, either a web, either a stair, either on soulsand or anything which is not considered
     * as air. Basically deltaY < 0 && !onGround && !inLiquid
     */
    public boolean falling;

    /**
     * -- SLIME VELOCITY --
     * SlimeVelocity represents if the user is still experiencing the slime inverted velocity. This is initiated
     * when the user collides with slime and is disabled when the user falls down or has a static motionY.
     * TODO Compensate for static motionY or liquids or any of these things
     */
    public boolean slimeVelocity;

    /**
     * -- VEHICLE --
     * Represents whether the user is within a vehicle or not. This is calculated by whether or not a packet input
     * is sent by the data or not. This is not however aligned with sample game mechanics and yada yada yada.
     */
    public boolean isInVehicleFake, isInVehiclePacket;
    public long lastSteerPacket;


    public boolean hasVelocity;

    /**
     * -- STUCK --
     * Represents whenever the data is directly colliding invalidately with the ground. This can be a cause of
     * a bad teleport or any of the such. Hence, it is calculated by checking the bounding box collision inside the
     * data.
     *
     * @see BoundingBox#checkCollision(Player, Predicate)
     */
    public boolean stuck;


    /**
     * -- Invalid NMS Motion
     * When interacting with an entity, the motion is subject to change until the user ceases to sprint.
     * This can be solved by this very easy practice. 200 IQ moves
     */
    public boolean invalidNMSMotion;

        /*
          __  __ _
         |  \/  (_)
         | \  / |_ ___  ___
         | |\/| | / __|/ __|
         | |  | | \__ \ (__
         |_|  |_|_|___/\___|

         */

    // Bullshit rest
    public List<PlayerMovement> previousPlayerPositions = new EvictingArrayList<>(25);

    /**
     * Velocity is an important factor. By default, it holds the value 0.0. It corresponds to Motion
     * in Minecraft and will exclusively update on knockback/interaction. Weird.
     *
     * @see Velocity
     */
    public Velocity velocity, lastVelocity;
    public boolean processedVelocity;

    /**
     * Corresponds to the delta horizontal distance between two position packets.
     * The following is calculated by: sqrt(deltaX^2 + deltaZ^2)
     */
    public double deltaH, previousDeltaH;

    /**
     * Corresponds to the delta vertical distance between two position packets.
     * The following is calculated by: |y - previousY|
     */
    public double deltaV, previousDeltaV;

    public boolean moving;


        /*
           _____      _   _
          / ____|    | | | |
         | (___   ___| |_| |_ ___ _ __ ___
          \___ \ / _ \ __| __/ _ \ '__/ __|
          ____) |  __/ |_| ||  __/ |  \__ \
         |_____/ \___|\__|\__\___|_|  |___/

         */


    public void setOnSoulSandX(boolean x) {
        this.onSoulSand = x;
        if (x) lastOnSoulSand = System.currentTimeMillis();

    }

    public void setOnIceX(boolean x) {
        this.onIce = x;
        if (x) lastOnIce = System.currentTimeMillis();
    }

    public void setOnSlimeX(boolean x) {
        this.onSlime = x;
        this.slimeVelocity = x;
        if (x) this.lastSlime = System.currentTimeMillis();
        //System.out.println("slime=" + x);
    }

    public void setOnGroundX(boolean x) {
        this.wasOnGround = this.onGround;
        this.onGround = x;
        if (x) this.lastOnGround = System.currentTimeMillis();
    }

    public void setInTrapdoorX(boolean x) {
        this.inTrapdoor = x;
        if (x) this.lastInTrapdoor = System.currentTimeMillis();
    }

    public void setOnTrapdoorX(boolean x) {
        this.onTrapdoor = x;
        if (x) this.lastOnTrapdoor = System.currentTimeMillis();
    }

    public void setUnderBlockX(boolean x) {
        this.wasUnderBlock = this.underBlock;
        this.underBlock = x;
        if (x) this.lastUnderBlock = System.currentTimeMillis();
    }

    public void setOnStairsX(boolean x) {
        this.onStair = x;
        if (x) this.lastOnStairs = System.currentTimeMillis();
        //System.out.println("stairs=" + x);
    }

    public void setOnSlabX(boolean x) {
        this.onSlab = x;
        if (x) this.lastOnSlab = System.currentTimeMillis();
        //System.out.println("slab=" + x);
    }

    public void setInLiquidX(boolean x) {
        this.wasInLiquid = this.inLiquid;
        this.inLiquid = x;
        if (x) this.lastInLiquid = System.currentTimeMillis();
    }

    public void setOnLadderX(boolean x) {
        this.onLadder = x;
        if (x) this.lastOnLadder = System.currentTimeMillis();
    }

    public void setInWebX(boolean x) {
        this.inWeb = x;
        if (x) this.lastInWeb = System.currentTimeMillis();
    }

        /*

          _____       _
         |  __ \     | |
         | |  | | ___| |__  _   _  __ _
         | |  | |/ _ \ '_ \| | | |/ _` |
         | |__| |  __/ |_) | |_| | (_| |
         |_____/ \___|_.__/ \__,_|\__, |
                                   __/ |
                                  |___/

         */

    /**
     * The mouse filter is Minecraft NMS's mouse filter. It handles the smoothing when the cinematic camera
     * is toggled; However, due to the nature of the game, smoothing can happen more often than there are ticks
     * per seconds. That's annoying
     * TODO implement this in any sort of smoothed processor
     */
    public MouseFilter
            yawFilter = new MouseFilter(),
            pitchFilter = new MouseFilter();



    public Cache<Velocity, Long> velocities = CacheBuilder
            .newBuilder()
            .expireAfterWrite(2000L,TimeUnit.MILLISECONDS)
            .build();
    public List<Pair<SimplePosition, Double>> posNew = new EvictingArrayList<>(20);

    public double getHighestHorizontalVelocity() {
        return Math.sqrt(velocities.asMap().keySet().stream().mapToDouble(Velocity::getSquaredHorizontal).max().orElse(0.0));
    }

    public double getHighestVerticalVelocity() {
        return velocities.asMap().keySet().stream().mapToDouble(Velocity::getVertical).max().orElse(0.0);
    }

    // Get the exempt manager
    public ExemptManager getExemptManager() {
        return data.exemptManager;
    }


    public SimplePosition closestLocationToX(long now, short offset) {
        long time = now - offset;
        long closest = now;
        PlayerMovement closs = null;
        for (PlayerMovement pos : previousPlayerPositions) {
            if (Math.abs(pos.getTimestamp() - time) < Math.abs(closest - time)) {
                closest = pos.getTimestamp();
                closs = pos;
            }
        }
        if (closs == null) return this.location;
        return closs;
    }

    public synchronized PlayerPosition closestAndShortestLocationToX(long now, short offset, PlayerPosition position) {
        long time = now - offset;
        final long[] closest = {now};
        final PlayerPosition[] closs = {null};


        previousPlayerPositions.forEach(pos -> {
            if (Math.abs(pos.getTimestamp() - time) < Math.abs(closest[0] - time) && position.distanceXZ(pos) < 3) {
                closest[0] = pos.getTimestamp();
                closs[0] = pos;
            }
        });

        if (closs[0] == null) return this.location;
        return closs[0];
    }

    public List<PlayerMovement> possiblePosition(long packet, short offset) {

        // Packet = max
        // Time = min
        long time = packet - offset - 50; // 50 is processing time
        List<PlayerMovement> positions = new ArrayList<>();

        for (PlayerMovement pos : previousPlayerPositions) {
            long positionTime = pos.getTimestamp() - offset;
            if (positionTime > time && positionTime < packet) {
                positions.add(pos);
            }
        }
        return positions;
    }

    public PlayerRotation getRotationBeforePosition() {
        return rotation.getTimestamp() > lastLocation.getTimestamp() ? lastRotation : rotation;
    }

    public boolean hasJumped() {
        return System.currentTimeMillis() - this.lastJump < 100L;
    }

    public boolean hasAttacked() {
        return System.currentTimeMillis() - data.combat.lastAttack < 200L;
    }

    public void updateMovement(PlayerMovement movement) {
        this.lastMovement = this.movement;
        this.movement = movement;
    }

    public void updatePosition(PlayerPosition movement) {
        this.lastLocation = this.location;
        this.location = movement;
    }
}
