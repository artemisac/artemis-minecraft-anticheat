package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.collision.impl.LegacyBoundingBoxProvider;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherReader;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.emulator.world.impl.CachedWorld;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v4.emulator.datawatcher.DataWatcher;
import ac.artemis.anticheat.engine.v1.utils.EntityUtil;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.PlayerEnums;
import ac.artemis.anticheat.engine.v1.block.WrappedBlock;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.emulator.entity.utils.PlayerCapabilities;
import ac.artemis.core.v4.nms.minecraft.INMS;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.blocks.BlocksUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import ac.artemis.core.v5.emulator.collision.CollisionProvider;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherFactory;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.Vehicle;
import ac.artemis.packet.minecraft.entity.impl.ArmorStand;
import ac.artemis.packet.minecraft.entity.impl.Minecart;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import lombok.Getter;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class Bntity {

    public Bntity(PlayerData data) {
        this.data = data;
        this.entityBoundingBox = ZERO_AABB;
        this.dataWatcher = new DataWatcher((Emulator) this);
        this.dataWatcherFactory = new DataWatcherFactory().setData(data).build();
        this.dataWatcher.addObject(0, (byte) 0);
        this.dataWatcher.addObject(1, (short) 300);
        this.dataWatcher.addObject(3, (byte) 0);
        this.dataWatcher.addObject(2, "");
        this.dataWatcher.addObject(4, (byte) 0);
        this.world = new CachedWorld(data.getPlayer().getWorld());
    }

    @Getter
    protected final List<Block> ghostBlocks = new ArrayList<>();

    protected ArtemisWorld world;

    @Getter
    protected final List<String> tags = new ArrayList<>();


    public PlayerData data;

    public static final BoundingBox ZERO_AABB = new BoundingBox(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static final CollisionProvider COLLISION_PROVIDER = new LegacyBoundingBoxProvider();

    protected final DataWatcher dataWatcher;
    protected final DataWatcherReader dataWatcherFactory;

    /**
     * Represents the velocity of a data. This is important when processing processor. It will be the main element which
     * will impact how a position is changed.
     */
    public double motionX, motionY, motionZ;
    protected double maxMotionX, maxMotionY, maxMotionZ;
    public double lastMotionX, lastMotionY, lastMotionZ;

    public double posX, posY, posZ;

    public double prevPosX;
    public double prevPosY;
    public double prevPosZ;

    private double maxDistance;

    /** Entity rotation Pitch */
    public float prevRotationYaw;
    public float prevRotationPitch;

    public float rotationYaw, rotationPitch;

    public boolean isAirBorne;

    public boolean wasGround, previousGround;

    public boolean noClip;

    public boolean lastSprinting;

    public boolean sneaking;

    public boolean jumping;

    public boolean onGround;

    /**
     * True if after a move this entity has collided with something either
     * vertically or horizontally
     */
    public boolean isCollided;
    public boolean velocityChanged;
    public boolean inWeb;

    /**
     * gets set by setEntityDead, so this must be the flag whether an Entity is dead
     * (inactive may be better term)
     */
    public boolean isDead;

    protected boolean firstUpdate;

    /**
     * Whether this entity is currently inside of water (if it handles water
     * movement that is)
     */
    public boolean inWater;

    public int fire;

    /** The distance walked multiplied by 0.6 */
    public float fallDistance;

    /**
     * How high this entity can step up when running into a block to try to get over
     * it (currently make note the entity will always step up this amount and not
     * just the amount needed)
     */
    public float stepHeight;

    public boolean isCollidedHorizontally, isCollidedVertically;

    /** How wide this entity is considered to be */
    public float width;

    /** How high this entity is considered to be */
    public float height;

    public boolean wasPos;

    public int newPosRotationIncrements;

    public BoundingBox entityBoundingBox;

    public Entity riddingEntity;

    public PlayerMovement lastPositionPrevious;
    public PlayerMovement lastRotationPrevious;

    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    public PlayerCapabilities capabilities;

    public PlayerEnums.AirType airType;

    /**
     * Remaining time an entity will be "immune" to further damage after being hurt.
     */
    public int hurtResistantTime;

    public boolean isChunkLoaded() {
        PlayerPosition from = data.prediction.getPosition();
        if (from == null) return false;
        World world = from.getWorld();
        return world.isChunkLoaded((int) Math.floor(from.getX()) >> 4, (int) Math.floor(from.getZ()) >> 4);
    }


    protected void prepareToSpawn() {
        while (this.posY > 0.0D && this.posY < 256.0D) {
            this.setPosition(this.posX, this.posY, this.posZ);

            if (NMSManager.getInms().getCollidingBoxes(data.getPlayer(),
                    this.getEntityBoundingBox(), ghostBlocks).isEmpty()) {
                break;
            }

            ++this.posY;
        }

        this.motionX = this.motionY = this.motionZ = 0.0D;
        this.rotationPitch = 0.0F;
        this.stepHeight = 0.6F;
        this.capabilities.isFlying = data.getPlayer().isFlying();
    }

    protected void onUpdate() {
        onEntityUpdate();
    }

    public void onEntityUpdate() {
        // Todo Dunno what goes in here ngl
        this.handleWaterMovement();

        /*if (this.isInLava()) {
            //this.setOnFireFromLava();
        //*/
    }

    /**
     * Called when a player attacks an entity. If this returns true the attack will
     * not happen.
     */
    public boolean hitByEntity(Entity entityIn) {
        return entityIn instanceof ArmorStand && this.attackEntityFromHanging(DamageSource.causePlayerDamage(entityIn), 0.0F);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFromHanging(DamageSource source, float amount) {
        return !this.isEntityInvulnerable(source);
    }

    /**
     * Tries to moves the entity by the passed in displacement. Args: x, y, z
     */
    protected void moveEntity(double x, double y, double z) {
        if (noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.resetPositionToBB();
        } else {
            INMS inms = NMSManager.getInms();

            double offsetX = x;
            double offsetY = y;
            double offsetZ = z;

            if (this.inWeb) {
                this.inWeb = false;
                x *= 0.25D;
                y *= 0.05000000074505806D;
                z *= 0.25D;
                this.motionX = 0.0D;
                this.motionY = 0.0D;
                this.motionZ = 0.0D;
                this.tag("Web");
            }


            boolean flag = this.onGround && this.isSneaking();

            if (flag) {
                final double offset = 0.05D;

                for (; x != 0.0D && inms.getCollidingBoxes(data.getPlayer(),
                        this.getEntityBoundingBox().offset(x, -1.0D, 0.0D), ghostBlocks).isEmpty();
                     offsetX = x) {
                    if (x < offset && x >= -offset) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= offset;
                    } else {
                        x += offset;
                    }
                }

                for (; z != 0.0D && inms.getCollidingBoxes(data.getPlayer(),
                        this.getEntityBoundingBox().offset(0.0D, -1.0D, z), ghostBlocks).isEmpty();
                     offsetZ = z) {
                    if (z < offset && z >= -offset) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= offset;
                    } else {
                        z += offset;
                    }
                }

                for (; x != 0.0D && z != 0.0D && inms.getCollidingBoxes(data.getPlayer(),
                        this.getEntityBoundingBox().offset(x, -1.0D, z), ghostBlocks).isEmpty();
                     offsetZ = z) {
                    if (x < offset && x >= -offset) {
                        x = 0.0D;
                    } else if (x > 0.0D) {
                        x -= offset;
                    } else {
                        x += offset;
                    }

                    offsetX = x;

                    if (z < offset && z >= -offset) {
                        z = 0.0D;
                    } else if (z > 0.0D) {
                        z -= offset;
                    } else {
                        z += offset;
                    }
                }
            }

            List<BoundingBox> list1 = inms.getCollidingBoxes(data.getPlayer(),
                    this.getEntityBoundingBox().addCoord(x, y, z), ghostBlocks);

            final BoundingBox axisalignedbb = this.getEntityBoundingBox().cloneBB();

            // Y
            for (BoundingBox axisalignedbb1 : list1) {
                y = axisalignedbb1.calculateYOffset(this.getEntityBoundingBox(), y);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

            // X
            for (BoundingBox axisalignedbb2 : list1) {
                x = axisalignedbb2.calculateXOffset(this.getEntityBoundingBox(), x);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));

            // Z
            for (BoundingBox axisalignedbb13 : list1) {
                z = axisalignedbb13.calculateZOffset(this.getEntityBoundingBox(), z);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));

            final boolean sneaking = this.onGround || offsetY != y && offsetY < 0.0D;

            if (this.stepHeight > 0.0F && sneaking && (offsetX != x || offsetZ != z)) {
                double copyX = x;
                double copyY = y;
                double copyZ = z;

                final BoundingBox backupBB = this.getEntityBoundingBox().cloneBB();
                this.setEntityBoundingBox(axisalignedbb);

                y = this.stepHeight;

                final List<BoundingBox> list = NMSManager.getInms().getCollidingBoxes(data.getPlayer(),
                        this.getEntityBoundingBox().addCoord(offsetX, y, offsetZ), ghostBlocks);

                BoundingBox sneakedBoundingBox = this.getEntityBoundingBox();
                final BoundingBox axisalignedbb5 = sneakedBoundingBox.addCoord(offsetX, 0.0D, offsetZ);
                double modifyingY = y;

                for (BoundingBox boundingBox : list) {
                    modifyingY = boundingBox.calculateYOffset(axisalignedbb5, modifyingY);
                }

                sneakedBoundingBox = sneakedBoundingBox.offset(0.0D, modifyingY, 0.0D);
                double modifyingX = offsetX;

                for (BoundingBox boundingBox : list) {
                    modifyingX = boundingBox.calculateXOffset(sneakedBoundingBox, modifyingX);
                }

                sneakedBoundingBox = sneakedBoundingBox.offset(modifyingX, 0.0D, 0.0D);
                double modifyingZ = offsetZ;

                for (BoundingBox boundingBox : list) {
                    modifyingZ = boundingBox.calculateZOffset(sneakedBoundingBox, modifyingZ);
                }

                sneakedBoundingBox = sneakedBoundingBox.offset(0.0D, 0.0D, modifyingZ);


                BoundingBox sneakedBoundingBox2 = this.getEntityBoundingBox();
                double modifyingY2 = y;

                for (BoundingBox boundingBox : list) {
                    modifyingY2 = boundingBox.calculateYOffset(sneakedBoundingBox2, modifyingY2);
                }
                sneakedBoundingBox2 = sneakedBoundingBox2.offset(0.0D, modifyingY2, 0.0D);


                double modifyingX2 = offsetX;
                for (BoundingBox boundingBox : list) {
                    modifyingX2 = boundingBox.calculateXOffset(sneakedBoundingBox2, modifyingX2);
                }
                sneakedBoundingBox2 = sneakedBoundingBox2.offset(modifyingX2, 0.0D, 0.0D);


                double modifyingZ2 = offsetZ;
                for (BoundingBox boundingBox : list) {
                    modifyingZ2 = boundingBox.calculateZOffset(sneakedBoundingBox2, modifyingZ2);
                }
                sneakedBoundingBox2 = sneakedBoundingBox2.offset(0.0D, 0.0D, modifyingZ2);


                double deltaXZ = modifyingX * modifyingX + modifyingZ * modifyingZ;
                double deltaXZ2 = modifyingX2 * modifyingX2 + modifyingZ2 * modifyingZ2;

                if (deltaXZ > deltaXZ2) {
                    x = modifyingX;
                    z = modifyingZ;
                    y = -modifyingY;
                    this.setEntityBoundingBox(sneakedBoundingBox);
                } else {
                    x = modifyingX2;
                    z = modifyingZ2;
                    y = -modifyingY2;
                    this.setEntityBoundingBox(sneakedBoundingBox2);
                }

                for (BoundingBox boundingBox : list) {
                    y = boundingBox.calculateYOffset(this.getEntityBoundingBox(), y);
                }

                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));

                if (copyX * copyX + copyZ * copyZ >= x * x + z * z) {
                    x = copyX;
                    y = copyY;
                    z = copyZ;
                    this.setEntityBoundingBox(backupBB);
                }
            }


            // Update collided horizontally
            this.resetPositionToBB();

            this.isCollidedHorizontally = offsetX != x || offsetZ != z;
            this.isCollidedVertically = offsetY != y;
            this.onGround = this.isCollidedVertically && offsetY < 0.0D;
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;


            final int floorX = MathHelper.floor_double(this.posX);
            final int floorY = MathHelper.floor_double(this.posY - 0.20000000298023224D);
            final int floorZ = MathHelper.floor_double(this.posZ);

            final Location blockPos = Minecraft.v().createLocation(data.getPlayer().getWorld(), floorX, floorY, floorZ);

            ac.artemis.core.v5.emulator.block.Block block = world.getBlockAt(floorX, floorY, floorZ);

            if (block != null && block.getMaterial().equals(NMSMaterial.AIR)) {
                final NaivePoint relative = block.getLocation().getRelative(BlockFace.DOWN);
                ac.artemis.core.v5.emulator.block.Block below = world.getBlockAt(relative.getX(), relative.getY(), relative.getZ());

                final NMSMaterial material = below.getMaterial();

                if (material.equals(NMSMaterial.OAK_FENCE)
                        || material.equals(NMSMaterial.COBBLESTONE_WALL)
                        || material.equals(NMSMaterial.OAK_FENCE_GATE)) {
                    block = below;
                    blockPos.add(0, -1, 0);
                }
            }
            WrappedBlock block1 = null;

            if (block != null) {
                block1 = WrappedBlock.getBlock(block.getMaterial());

                this.updateFallState(y, this.onGround, block1, blockPos);
                //check.debug("d4=" + d4 + " y=" + y);
                if (offsetY != y) {
                    //check.debug("reeeeeeeeee");
                    block1.onLanded(this);
                    this.tag("CY");
                }
            }


            if (offsetX != x) {
                //check.debug("d3=" + d3 + " x=" + x);
                this.motionX = 0.0D;
                this.tag("CX");
            }

            if (offsetZ != z) {
                //check.debug("d5=" + d5 + " z=" + z);
                this.motionZ = 0.0D;
                this.tag("CZ");
            }

            if (this.canTriggerWalking() && !flag && this.riddingEntity == null && block1 != null) {
                if (this.onGround) {
                    block1.onEntityCollidedWithBlock(this, blockPos);
                    this.tag("BC");
                }
            }

            this.doBlockCollisions();
            // TODO Handle the rest afterwards
        }
    }

    protected void updateFallState(double y, boolean onGroundIn, WrappedBlock blockIn, Location pos) {
        if (onGroundIn) {
            if (this.fallDistance > 0.0F) {
                if (blockIn != null) {
                    blockIn.onFallenUpon(this, pos, this.fallDistance);
                } else {
                    this.fall(this.fallDistance, 1.0F);
                }

                this.fallDistance = 0.0F;
            }
        } else if (y < 0.0D) {
            this.fallDistance = (float) ((double) this.fallDistance - y);
        }
    }

    public void fall(float distance, float damageMultiplier) {

        // Todo Entity
        /*if (this.riddenByEntity != null) {
            this.riddenByEntity.fall(distance, damageMultiplier);
        }*/
    }

    protected void doBlockCollisions() {
        NaivePoint blockpos = new NaivePoint(
                this.getEntityBoundingBox().minX + 0.001D,
                this.getEntityBoundingBox().minY + 0.001D,
                this.getEntityBoundingBox().minZ + 0.001D
        );

        NaivePoint blockpos1 = new NaivePoint(
                this.getEntityBoundingBox().maxX - 0.001D,
                this.getEntityBoundingBox().maxY - 0.001D,
                this.getEntityBoundingBox().maxZ - 0.001D
        );

        if (EntityUtil.isAreaLoaded(data.getPlayer().getWorld(), blockpos, blockpos1)) {
            for (int x = blockpos.getX(); x <= blockpos1.getX(); ++x) {
                for (int y = blockpos.getY(); y <= blockpos1.getY(); ++y) {
                    for (int z = blockpos.getZ(); z <= blockpos1.getZ(); ++z) {
                        final Location blockpos2 = Minecraft.v().createLocation(data.getPlayer().getWorld(), x, y, z);
                        final ac.artemis.core.v5.emulator.block.Block block = world.getBlockAt( x, y, z);

                        if (block == null) continue;
                        WrappedBlock.getBlock(block.getMaterial())
                                .onEntityCollidedWithBlockState(this, blockpos2);
                    }
                }
            }
        }
    }

    public boolean isPushedByWater() {
        return !this.capabilities.isFlying;
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity
     * to the entity
     */
    public boolean handleWaterMovement() {
        if (world != null && EntityUtil.handleMaterialAcceleration(data.getPlayer().getWorld(),
                this.getEntityBoundingBox()
                        .cloneBB()
                        .expand(0.0D, -0.4000000059604645D, 0.0D)
                        .shrink(0.001D, 0.001D, 0.001D),
                NMSMaterial.WATER, this)) {

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.fire = 0;
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    public boolean isInWater() {
        return NMSManager.getInms()
                .getCollidingBlocks(
                    this.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D),
                        data.getPlayer().getWorld())
                .stream()
                .map(NMSMaterial::matchXMaterial)
                .collect(Collectors.toSet())
                .contains(NMSMaterial.WATER);
    }

    public boolean isInLava() {
        return NMSManager.getInms()
                .getCollidingBlocks(
                    this.getEntityBoundingBox().expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D),
                                data.getPlayer().getWorld())
                .stream()
                .map(NMSMaterial::matchXMaterial)
                .collect(Collectors.toSet())
                .contains(NMSMaterial.LAVA);
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return !this.capabilities.isFlying;
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other.
     * Args: entity
     */
    public void applyEntityCollision(Entity entityIn) {
        if ((!(entityIn instanceof Vehicle) || ((Vehicle) entityIn).getPassenger() != this.getData().getPlayer())
                /*&& entityIn.getVehicle() != this.getData().getPlayer()*/) {
            if (!EntityUtil.isNoClip(entityIn) && !this.noClip) {
                double d0 = this.posX - entityIn.getLocation().getX();
                double d1 = this.posX - entityIn.getLocation().getZ();
                double d2 = MathHelper.abs_max(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = (double) MathHelper.sqrt_double(d2);
                    d0 = d0 / d2;
                    d1 = d1 / d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 = d0 * d3;
                    d1 = d1 * d3;
                    d0 = d0 * 0.05000000074505806D;
                    d1 = d1 * 0.05000000074505806D;
                    d0 = d0 * (double) (1.0F);
                    d1 = d1 * (double) (1.0F);

                    this.addVelocity(d0, 0.0D, d1);

                    /*if (entityIn.riddenByEntity == null) {
                        entityIn.addVelocity(d0, 0.0D, d1);
                    }*/
                }
            }
        }
    }

    /**
     * Adds to the current velocity of the entity. Args: x, y, z
     */
    public void addVelocity(double x, double y, double z) {
        this.motionX += x;
        this.motionY += y;
        this.motionZ += z;
        this.isAirBorne = true;
    }

    /**
     * sets the players height back to normal after doing things like sleeping and
     * dieing
     */
    protected void resetHeight() {

    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting) {
        // Nada
        this.setFlag(3, sprinting);
    }

    /**
     * Set sprinting switch for Entity.
     */
    public boolean isSprinting() {
        // Nada
        return this.getFlag(3);
    }

    public void setVelocity(double x, double y, double z) {
        this.lastMotionX = motionX;
        this.lastMotionY = motionY;
        this.lastMotionZ = motionZ;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public void setVelocity(Velocity velocity) {
        this.setVelocity(velocity.getX(), velocity.getY(), velocity.getZ());
    }

    /**
     * Sets the x,y,z of the entity from the given parameters. Also seems to set up
     * a bounding box.
     */
    public void setPosition(double x, double y, double z) {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setEntityBoundingBox(new BoundingBox(x - (double) f, y, z - (double) f,
                x + (double) f, y + (double) f1, z + (double) f));
    }

    public Point getPosition() {
        return new Point(posX, posY, posZ);
    }

    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements,
                                        boolean p_180426_10_) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
        List<BoundingBox> list = NMSManager.getInms().getCollidingBoxes(data.getPlayer(),
                this.getEntityBoundingBox().contract(0.03125D, 0.0D, 0.03125D), ghostBlocks);

        if (!list.isEmpty()) {
            double d0 = 0.0D;

            for (BoundingBox axisalignedbb : list) {
                if (axisalignedbb.maxY > d0) {
                    d0 = axisalignedbb.maxY;
                }
            }

            y = y + (d0 - this.getEntityBoundingBox().minY);
            this.setPosition(x, y, z);
        }
    }

    public void setRotation(float yaw, float pitch) {
        this.rotationYaw = yaw;
        this.rotationPitch = pitch;
    }

    public boolean isFlying() {
        return capabilities.isFlying();
    }

    public BoundingBox getEntityBoundingBox() {
        return entityBoundingBox;
    }

    public void setEntityBoundingBox(BoundingBox entityBoundingBox) {
        this.entityBoundingBox = entityBoundingBox;
    }

    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    /**
     * Returns true if the entity is riding another entity, used by render to rotate
     * the legs to be in 'sit' position for players.
     */
    public boolean isRiding() {
        return this.riddingEntity != null;
    }

    /**
     * Resets the entity's position to the center (planar) and bottom (vertical)
     * points of its bounding box.
     */
    protected void resetPositionToBB() {
        this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
        this.posY = this.getEntityBoundingBox().minY;
        this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
    }

    /**
     * Sets the Entity inside a web block.
     */
    public void setInWeb() {
        this.inWeb = true;
        this.fallDistance = 0.0F;
    }

    /**
     * Sets the width and height of the entity. Args: width, height
     */
    public void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            float f = this.width;
            this.width = width;
            this.height = height;
            this.setEntityBoundingBox(
                    new BoundingBox(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY,
                            this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double) this.width,
                            this.getEntityBoundingBox().minY + (double) this.height,
                            this.getEntityBoundingBox().minZ + (double) this.width));

            if (this.width > f && !this.firstUpdate) {
                this.moveEntity((double) (f - this.width), 0.0D, (double) (f - this.width));
            }
        }
    }

    /**
     * Sets that this entity has been attacked.
     */
    protected void setBeenAttacked() {
        this.velocityChanged = true;
    }

    /**
     * Checks if the offset position from the entity's current position is inside of
     * liquid. Args: x, y, z
     */
    public boolean isOffsetPositionInLiquid(double x, double y, double z) {
        BoundingBox axisalignedbb = this.getEntityBoundingBox().offset(x, y, z);
        return this.isLiquidPresentInAABB(axisalignedbb);
    }

    /**
     * Determines if a liquid is present within the specified AxisAlignedBB.
     */
    private boolean isLiquidPresentInAABB(BoundingBox bb) {
        Set<Material> materials = NMSManager.getInms().getCollidingBlocks(bb, data.getPlayer().getWorld());
        return materials.contains(NMSMaterial.WATER.parseMaterial());
    }

    /**
     * returns true if this entity is by a ladder, false otherwise
     */
    public boolean isOnLadder() {
        final int i = MathHelper.floor_double(this.posX);
        final int j = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        final int k = MathHelper.floor_double(this.posZ);
        final ac.artemis.core.v5.emulator.block.Block block = world.getBlockAt(i, j, k);
        //if (block != null) //System.out.println(block.getType());
        return block != null && (
                (block.getMaterial()).equals(NMSMaterial.LADDER)
                || block.getMaterial().equals(NMSMaterial.VINE))
                && !data.getPlayer().getGameMode().equals(GameMode.SPECTATOR);
    }

    /**
     * Sets the entity's position and rotation.
     */
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.prevPosX = this.posX = x;
        this.prevPosY = this.posY = y;
        this.prevPosZ = this.posZ = z;
        this.prevRotationYaw = this.rotationYaw = yaw;
        this.prevRotationPitch = this.rotationPitch = pitch;
        double d0 = (double) (this.prevRotationYaw - yaw);

        if (d0 < -180.0D) {
            this.prevRotationYaw += 360.0F;
        }

        if (d0 >= 180.0D) {
            this.prevRotationYaw -= 360.0F;
        }

        this.setPosition(this.posX, this.posY, this.posZ);
        this.setRotation(yaw, pitch);
    }

    public boolean isEntityInvulnerable(DamageSource source) {
        return this.capabilities.isDisableDamage() && source != DamageSource.outOfWorld
                && !(source.getEntity() instanceof Player && ((Player) source.getEntity()).getGameMode().equals(GameMode.CREATIVE));
    }

    public boolean isEntityUndead(){
        return false;
    }

    protected boolean isMovementBlocked() {
        return data.getPlayer().getHealth() <= 0.0d;
    }

    /**
     * Returns true if the flag is active for the entity. Known flags: 0) is burning; 1) is sneaking; 2) is riding
     * something; 3) is sprinting; 4) is eating
     */
    protected boolean getFlag(int flag) {
        return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
    }

    /**
     * Enable or disable a entity flag, see getEntityFlag to read the know flags.
     */
    protected void setFlag(int flag, boolean set) {
        byte b0 = this.dataWatcher.getWatchableObjectByte(0);

        if (set) {
            this.dataWatcher.updateObject(0, (byte) (b0 | 1 << flag));
        } else {
            this.dataWatcher.updateObject(0, (byte) (b0 & ~(1 << flag)));
        }
    }

    protected void tag(String string) {
        this.tags.add(string);
    }

    public String readTags() {
        return Arrays.toString(tags.toArray());
    }

    public float getFriction() {
        int blockX = MathHelper.floor_double(this.posX);
        int blockY = MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1;
        int blockZ = MathHelper.floor_double(this.posZ);

        if (!data.getPlayer().getWorld().isChunkLoaded(blockX >> 4, blockZ >> 4)) return 0.60F;

        return BlocksUtil.getSlipperiness(data.getPlayer().getWorld().getBlockAt(blockX, blockY, blockZ));
    }

    public Velocity getMotion() {
        return new Velocity(motionX, motionY, motionZ);
    }

    public void resetWorld() {
        this.world = new CachedWorld(data.getPlayer().getWorld());
    }

    public PlayerMovement toMovement(){
        return new PlayerMovement(data.getPlayer(), this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, System.currentTimeMillis());
    }
}
