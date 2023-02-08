package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.map.ServersideAttributeMap;
import ac.artemis.core.v4.emulator.attribute.impl.SharedMonsterAttributes;
import ac.artemis.anticheat.engine.v1.utils.EntityUtil;
import ac.artemis.core.v4.emulator.entity.utils.PlayerControls;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.anticheat.engine.v1.block.WrappedBlock;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.emulator.magic.Magic;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v4.utils.blocks.BlocksUtil;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.world.Location;

import java.util.*;

public class BntityLivingBase extends Bntity {

    public BntityLivingBase(PlayerData data) {
        super(data);
        //this.setAIMoveSpeed(data.getPlayer().getWalkSpeed() / 2.0F);
        this.applyEntityAttributes();
    }

    private ServersideAttributeMap attributeMap;
    public static final UUID SPRINTING_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    public static final AttributeModifier SPRINTING_SPEED_BOOST_MODIFIER = (new AttributeModifier(
            SPRINTING_SPEED_BOOST_MODIFIER_UUID, "Sprinting speed boost", Magic.SPRINT_MODIFIER, 2))
            .setSaved(false);
    protected final Map<Integer, PotionEffect> activePotionsMap = new HashMap<>();

    /**
     * used to check whether entity is jumping.
     */
    public float moveStrafing;
    public float moveForward;
    protected float randomYawVelocity;

    /** Whether the DataWatcher needs to be updated with the active potions */
    private boolean potionsNeedUpdate = true;

    /**
     * A factor used to determine how far this entity will move each tick if it is
     * jumping or falling.
     */
    public float jumpMovementFactor = Magic.JUMP_MOVE_FACTOR;

    /**
     * A factor used to determine how far this entity will move each tick if it is
     * walking on land. Adjusted by speed, and slipperiness of the current block.
     */
    protected float landMovementFactor;

    /**
     * Number of ticks since last jump
     */
    public int jumpTicks;
    protected float absorptionAmount;

    /** The new X position to be applied to the entity. */
    protected double newPosX;

    /** The new Y position to be applied to the entity. */
    protected double newPosY;
    protected double newPosZ;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationYaw;

    /** The new yaw rotation to be applied to the entity. */
    protected double newRotationPitch;

    public float health;
    public float maxHealth;

    public int maxHurtResistantTime = 20;

    /**
     * Damage taken in the last hit. Mobs are resistant to damage less than this for
     * a short time after taking damage.
     */
    protected float lastDamage;

    /**
     * The amount of time remaining this entity should act 'hurt'. (Visual
     * appearance of red tint)
     */
    public int hurtTime;

    /** What the hurt time was max set to last. */
    public int maxHurtTime;

    /** The yaw at which this entity was last attacked from. */
    public float attackedAtYaw;

    /** The most recent player that has attacked this entity */
    protected Player attackingPlayer;

    /**
     * Set to 60 when hit by the player or the player's wolf, then decrements. Used
     * to determine whether the entity should drop items on death.
     */
    protected int recentlyHit;

    /**
     * This gets set on entity death, but never used. Looks like a duplicate of
     * isDead
     */
    protected boolean dead;

    /** The age of this EntityLiving (used to determine when it dies) */
    protected int entityAge;

    public final PlayerControls playerControls = new PlayerControls((BntityPlayerXYZ) this);


    @Override
    protected void prepareToSpawn() {
        super.prepareToSpawn();
        this.motionX = this.motionY = this.motionZ = 0.0D;
    }

    @Override
    protected void onUpdate() {
        super.onUpdate();


        // NORMALLY THERE'S WEIRD FUCKING STATS THINGS HERE IN MCP
        this.onLivingUpdate();
        // TODO DO ALL WEIRD YAW STUFF

    }

    protected void onLivingUpdate() {
        if (this.jumpTicks > 0) {
            this.jumpTicks--;
        }

        if (this.newPosRotationIncrements > 0) {
            double d0 = this.posX + (this.newPosX - this.posX) / (double) this.newPosRotationIncrements;
            double d1 = this.posY + (this.newPosY - this.posY) / (double) this.newPosRotationIncrements;
            double d2 = this.posZ + (this.newPosZ - this.posZ) / (double) this.newPosRotationIncrements;
            double d3 = MathHelper.wrapAngleTo180_double(this.newRotationYaw - (double) this.rotationYaw);
            this.rotationYaw = (float) ((double) this.rotationYaw + d3 / (double) this.newPosRotationIncrements);
            this.rotationPitch = (float) ((double) this.rotationPitch
                    + (this.newRotationPitch - (double) this.rotationPitch) / (double) this.newPosRotationIncrements);
            --this.newPosRotationIncrements;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        // Turns out this isn't valid lol
        // Actually it is NOT
        /*this.motionX *= 0.98D;
        this.motionY *= 0.98D;
        this.motionZ *= 0.98D;
        */

        // Round the motion if it's too minimal. This is useful for not having tiny ass numbers.
        if (Math.abs(this.motionX) < Magic.MINIMUM_MOTION) {
            this.motionX = 0.0D;
        }

        if (Math.abs(this.motionY) < Magic.MINIMUM_MOTION) {
            this.motionY = 0.0D;
        }

        if (Math.abs(this.motionZ) < Magic.MINIMUM_MOTION) {
            this.motionZ = 0.0D;
        }

        // Basically if data is dead, just reset these characteristics
        if (this.isMovementBlocked()) {
            this.jumping = false;
            this.moveStrafing = 0.0f;
            this.moveForward = 0.0f;
            this.randomYawVelocity = 0.0f;
        } else {
            this.updateEntityActionState();
        }

        // BIG MISTAKE WAS HERE OOPS

        if (this.jumping) {
            // Todo better Water, Lava and jump handling
            final boolean condition = this instanceof BntityPlayerXYZ_1_15
                    ? this.jumpTicks == 0
                    : data.prediction.isLastGround() && this.jumpTicks == 0;

            if (inWater) {
                this.updateAITick();
            } else if (collidesLava()) {
                this.handleJumpLava();
            } else if (condition) {
                this.jump();
                this.jumpTicks = 10;
            }

            this.tag("Jump");
        } else {
            this.jumpTicks = 0;
        }
        // Move modification
        this.moveForward *= Magic.MOVE_BIND_MODIFIER;
        this.moveStrafing *= Magic.MOVE_BIND_MODIFIER;

        this.randomYawVelocity *= 0.9F;
        this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
        //this.collideWithNearbyEntities();
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = NMSManager.getInms().getEntitiesInAABBexcluding(this.getData().getPlayer(),
                this.getEntityBoundingBox().expand(0.20000000298023224D, 0.0D, 0.20000000298023224D),
                EntityUtil::canBePushed);

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                this.applyEntityCollision(entity);
            }
        }
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    public void jump() {

        // Increase the motion by the magic value!
        this.motionY = Magic.JUMP_UPWARDS_MOTION;

        if (data.getPlayer().hasEffect(PotionEffectType.JUMP)) {
            // TODO Do the potion thingy instead of relying on this old hag's shit
            this.motionY += ((float) (getJumpBoostAmplifier(data.getPlayer()))) * 0.1F;
        }

        if (this.playerControls.isSprint()) {
            float f = this.rotationYaw * Magic.MOTION_XZ_YAW_JUMP_MODIFIER;
            this.motionX -= MathHelper.sin(f) * Magic.MOTION_XZ_JUMP_MODIFIER;
            this.motionZ += MathHelper.cos(f) * Magic.MOTION_XZ_JUMP_MODIFIER;
        }

        this.isAirBorne = true;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        boolean flag1 = ((BntityPlayer) this).capabilities.disableDamage;

        /*if (this.isEntityAlive()) {
            if (this.isInsideOfMaterial(Material.water)) {
                if (!this.canBreatheUnderwater() && !this.isPotionActive(Potion.waterBreathing.id) && !flag1) {
                    this.setAir(this.decreaseAirSupply(this.getAir()));

                    if (this.getAir() == -20) {
                        this.setAir(0);

                        for (int i = 0; i < 8; ++i) {
                            float f = this.rand.nextFloat() - this.rand.nextFloat();
                            float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                            float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                            this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (double) f,
                                    this.posY + (double) f1, this.posZ + (double) f2, this.motionX, this.motionY,
                                    this.motionZ, new int[0]);
                        }

                        this.attackEntityFrom(DamageSource.drown, 2.0F);
                    }
                }

                if (!this.worldObj.isRemote && this.isRiding() && this.ridingEntity instanceof EntityLivingBase) {
                    this.mountEntity((Entity) null);
                }
            } else {
                this.setAir(300);
            }
        }*/

        /*if (this.isEntityAlive() && this.isWet()) {
            this.extinguish();
        }*/


        if (this.hurtTime > 0) {
            --this.hurtTime;
        }

        if (this.hurtResistantTime > 0) {
            --this.hurtResistantTime;
        }

        if (this.getHealth() <= 0.0F) {
            //this.onDeathUpdate();
        }

        if (this.recentlyHit > 0) {
            --this.recentlyHit;
        } else {
            this.attackingPlayer = null;
        }

        /*if (this.lastAttacker != null && !this.lastAttacker.isEntityAlive()) {
            this.lastAttacker = null;
        }

        if (this.entityLivingToAttack != null) {
            if (!this.entityLivingToAttack.isEntityAlive()) {
                this.setRevengeTarget((EntityLivingBase) null);
            } else if (this.ticksExisted - this.revengeTimer > 100) {
                this.setRevengeTarget((EntityLivingBase) null);
            }
        }*/

        this.updatePotionEffects();
    }



    /**
     * Moves the entity based on the specified heading. Args: strafe, forward
     */
    protected void moveEntityWithHeading(float strafe, float forward) {
        // If the chunk is loaded
        final boolean fuckup = this instanceof BntityPlayerXYZ_1_15;

        // If the user does not collide with water nor is flying
        if (!inWater || this.isFlying()) {

            // If the user does not collide with lava nor is flying
            if (!this.collidesLava() || this.isFlying()) {

                // Grab the magic friction value, equivalent of 0.91F
                float friction = Magic.FRICTION;

                // These values are directly from NMS, quite useful most the time, these have to be improved nonetheless
                // Check if the user was on ground before as we're a tick behind since we're predicting the position
                // Apply the block slipperiness to the friction
                final double remove = fuckup ? 0.5000001D : 1.D;

                final float slipperiness = BlocksUtil.getSlipperiness(BlockUtil.getBlockAsync(
                        Minecraft.v().createLocation(
                                data.getPlayer().getWorld(),
                                cc.ghast.packet.nms.MathHelper.floor(this.posX),
                                cc.ghast.packet.nms.MathHelper.floor(this.getEntityBoundingBox().minY) - remove,
                                cc.ghast.packet.nms.MathHelper.floor(this.posZ)
                        )
                ));

                if (onGround) {
                    friction *= slipperiness;
                }

                // This is the odd value "f" is in the formula.
                final float drag = fuckup
                        ? 0.21600002F / (slipperiness * slipperiness * slipperiness)
                        : 0.16277136F / (friction * friction * friction);

                // Shifted friction
                float acceleration;

                // If the player is on the ground, take his speed and his drag
                if (this.onGround) {
                    acceleration = (float) (this.getAIMoveSpeed() * drag);
                } else {
                    // Else, use his air speed called jump movement factor
                    acceleration = this.jumpMovementFactor;
                }

                // Move the entity's motion
                // ARTEMIS 1.13 PATCH!
                this.moveFlying(strafe, forward, acceleration);


                // This bit is entirely taken from NMS. It'll make the motionY of a data static if such user
                // Is colliding with a ladder.
                if (this.isOnLadder()) {
                    float f6 = Magic.MOTION_H_MAX_LADDER;
                    this.motionX = MathHelper.clamp_double(this.motionX, -f6, f6);
                    this.motionZ = MathHelper.clamp_double(this.motionZ, -f6, f6);
                    this.fallDistance = 0.0F;

                    if (this.motionY < -Magic.MOTION_Y_MAX_LADDER) {
                        this.motionY = -Magic.MOTION_Y_MAX_LADDER;
                    }

                    boolean flag = this.sneaking;

                    if (flag && this.motionY < 0.0D) {
                        this.motionY = 0.0D;
                    }
                    this.tag("PrLad");
                }

                this.maxMotionX = motionX;
                this.maxMotionY = motionY;
                this.maxMotionZ = motionZ;

                // Moves the entity based on it's motion
                this.moveEntity(motionX, motionY, motionZ);

                if (this.isCollidedHorizontally && this.isOnLadder()) {
                    this.motionY = 0.2D;
                    this.tag("PoLad");
                }

                // If the chunk is unloaded, begin to sink data down the abyss to send in packets I guess?
                if (!isChunkLoaded()) {
                    this.motionY = this.posY > 0 ? -0.1D : 0.0D;
                    this.tag("Chunk");
                }

                // Decrease the natural motion
                else {
                    this.motionY -= Magic.MOTION_Y_DECREASE;
                }

                // Natural update of motion for next position
                this.motionY *= Magic.FRICTION_Y;
                this.motionX *= friction;
                this.motionZ *= friction;
            }

            // LAVA COLLISION / FLYING
            else {
                final double y = this.posY;
                // Move with a low friction
                this.moveFlying(strafe, forward, 0.02F);
                this.moveEntity(motionX, motionY, motionZ);
                this.motionX *= Magic.MOTION_LAVA_MULTIPLIER;
                this.motionY *= Magic.MOTION_LAVA_MULTIPLIER;
                this.motionZ *= Magic.MOTION_LAVA_MULTIPLIER;
                this.motionY -= Magic.MOTION_V_LIQUID_SUBTRACT;

                // TODO Liquid offset and motionY modifier: https://pastebin.com/wntK1Z8b
                if (this.isCollidedHorizontally && !this.isOffsetPositionInLiquid(this.motionX,
                        this.motionY + 0.6000000238418579D - this.posY + y, this.motionZ)) {
                    this.motionY = 0.30000001192092896D;
                }
            }
        }
        // LIQUID COLLISION / FLYING
        else {
            double d0 = this.posY;
            float f1 = 0.8F;
            float f2 = 0.02F;
            float f3 = (float) ac.artemis.core.v5.utils.EntityUtil.getDepthStrider(data.getPlayer());

            if (f3 > 3.0F) {
                f3 = 3.0F;
            }

            if (!onGround) {
                f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
                f1 += (0.54600006F - f1) * f3 / 3.0F;
                f2 += (this.getAIMoveSpeed() - f2) * f3 / 3.0F;
            }

            this.moveFlying(strafe, forward, f2);
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= f1;
            this.motionY *= Magic.MOTION_V_WATER_MULTIPLIER;
            this.motionZ *= f1;
            this.motionY -= Magic.MOTION_V_LIQUID_SUBTRACT;

            // Todo Implement liquid offset calculation for MotionY, as seen below
            if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX,
                    this.motionY + 0.6000000238418579D - this.posY + d0, this.motionZ)) {
                this.motionY = 0.30000001192092896D;
                this.tag("W-Off");
            }
        }

        // Todo LimbSwing?
    }

    protected void updatePotionEffects() {
        for (Map.Entry<Integer, PotionEffect> entry : new HashSet<>(activePotionsMap.entrySet())) {
            final PotionEffect potioneffect = entry.getValue();
            if (!potioneffect.onUpdate((Emulator) this)) {
                activePotionsMap.remove(entry.getKey());
                this.onFinishedPotionEffect(potioneffect);
            } else if (potioneffect.getDuration() % 600 == 0) {
                this.onChangedPotionEffect(potioneffect, false);
            }
        }
        if (this.potionsNeedUpdate) {
            //this.updatePotionMetadata();

            this.potionsNeedUpdate = false;
        }
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (iattributeinstance.getModifier(SPRINTING_SPEED_BOOST_MODIFIER_UUID) != null) {
            iattributeinstance.removeModifier(SPRINTING_SPEED_BOOST_MODIFIER);
        }

        if (sprinting) {
            iattributeinstance.applyModifier(SPRINTING_SPEED_BOOST_MODIFIER);
        }
    }

    /**
     * Set sprinting switch for Entity.
     */
    public void setSprintingSilent(boolean sprinting) {
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);

        if (iattributeinstance.getModifier(SPRINTING_SPEED_BOOST_MODIFIER_UUID) != null) {
            iattributeinstance.removeModifier(SPRINTING_SPEED_BOOST_MODIFIER);
        }

        if (sprinting) {
            iattributeinstance.applyModifier(SPRINTING_SPEED_BOOST_MODIFIER);
        }
    }



    protected void updateEntityActionState() {

        // Get the basic positions a tick behind
        /*PlayerPosition fromPos = data.movement.lastLocation;
        PlayerPosition toPos = data.movement.location;

        move: {
            this.moveForward = 0.0F;
            this.moveStrafing = 0.0F;

            if (!data.movement.isMoving() || !wasPos) return;

            int[] vars = MoveUtil.getMoveForwardIteration(this, data.movement.movement.toPoint());
            int forward = vars[0];
            int strafe = vars[1];
            int jump = vars[2];

            this.jumping = jump == 1;
            this.moveForward = forward;
            this.moveStrafing = strafe;

            if (!isSneaking()) break move;

            this.moveForward = (float)((double)this.moveForward * 0.3D);
            this.moveStrafing = (float)((double)this.moveStrafing * 0.3D);
        }*/
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F) {
                return false;
            } else if (source.isFireDamage() && this.isPotionActive(Potion.fireResistance)) {
                return false;
            } else {
               /* if ((source == DamageSource.anvil || source == DamageSource.fallingBlock)
                        && this.getEquipmentInSlot(4) != null) {
                    this.getEquipmentInSlot(4).damageItem((int) (amount * 4.0F + this.rand.nextFloat() * amount * 2.0F),
                            this);
                    amount *= 0.75F;
                }*/

                boolean flag = true;

                if ((float) this.hurtResistantTime > (float) this.maxHurtResistantTime / 2.0F) {
                    if (amount <= this.lastDamage) {
                        return false;
                    }

                    this.damageEntity(source, amount - this.lastDamage);
                    this.lastDamage = amount;
                    flag = false;
                } else {
                    this.lastDamage = amount;
                    this.hurtResistantTime = this.maxHurtResistantTime;
                    this.damageEntity(source, amount);
                    this.hurtTime = this.maxHurtTime = 10;
                }

                this.attackedAtYaw = 0.0F;
                Entity entity = source.getEntity();

                if (entity != null) {
                    //this.setRevengeTarget((LivingEntity) entity);

                    if (entity instanceof Player) {
                        this.recentlyHit = 100;
                        this.attackingPlayer = (Player) entity;
                    } /*else if (entity instanceof Wolf) {
                        Wolf entitywolf = (Wolf) entity;

                        if (entitywolf.isTamed()) {
                            this.recentlyHit = 100;
                            this.attackingPlayer = null;
                        }
                    }*/
                }

                if (flag) {

                    // Todo the fuck is this
                    //this.worldObj.setEntityState(this, (byte) 2);

                    if (source != DamageSource.drown) {
                        this.setBeenAttacked();
                    }

                    if (entity != null) {
                        double d1 = entity.getLocation().getX() - this.posX;
                        double d0;

                        for (d0 = entity.getLocation().getZ() - this.posZ; d1 * d1
                                + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.attackedAtYaw = (float) (MathHelper.func_181159_b(d0, d1) * 180.0D / Math.PI
                                - (double) this.rotationYaw);
                        this.knockBack(entity, amount, d1, d0);
                    } else {
                        this.attackedAtYaw = (float) ((int) (Math.random() * 2.0D) * 180);
                    }
                }

                if (this.getHealth() <= 0.0F) {
                    this.onDeath(source);
                }
                return true;
            }
        }
    }



    public float getFriction(PlayerMovement loc) {
        int blockX = MathHelper.floor_double(loc.getX());
        int blockY = MathHelper.floor_double(loc.getY() - 1);
        int blockZ = MathHelper.floor_double(loc.getZ());

        if (BlockUtil.getBlockAsync(loc.getWorld(), blockX, blockY, blockZ) == null) return 0.60F;

        return BlocksUtil.getSlipperiness(loc.getWorld().getBlockAt(blockX, blockY, blockZ));
    }

    /**
     * adds a PotionEffect to the entity
     */
    public void addPotionEffect(PotionEffect potioneffectIn) {
        if (this.isPotionApplicable(potioneffectIn)) {
            if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
                ((PotionEffect) this.activePotionsMap.get(potioneffectIn.getPotionID()))
                        .combine(potioneffectIn);
                this.onChangedPotionEffect(
                        (PotionEffect) this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
            } else {
                this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
        }
    }

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return potioneffectIn.getPotionID() == Potion.moveSpeed.getId() || potioneffectIn.getPotionID() == Potion.moveSlowdown.getId();
    }

    /**
     * Remove the speified potion effect from this entity.
     */
    public void removePotionEffectClient(int potionId) {
        this.activePotionsMap.remove(potionId);
    }

    /**
     * Remove the specified potion effect from this entity.
     */
    public void removePotionEffect(int potionId) {
        PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.remove(potionId);

        if (potioneffect != null) {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    protected void onNewPotionEffect(PotionEffect id) {
        this.potionsNeedUpdate = true;

        Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity((Emulator) this, this.getAttributeMap(),
                id.getAmplifier());
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
        this.potionsNeedUpdate = true;

        Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity((Emulator) this, this.getAttributeMap(),
                id.getAmplifier());
        Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity((Emulator) this, this.getAttributeMap(),
                id.getAmplifier());
    }

    protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
        this.potionsNeedUpdate = true;

        Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity((Emulator) this,
                this.getAttributeMap(), p_70688_1_.getAmplifier());
    }

    public void clearActivePotions() {
        Iterator<Integer> iterator = this.activePotionsMap.keySet().iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.get(integer);
            iterator.remove();
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    public boolean isPotionActive(int potionId) {
        return this.activePotionsMap.containsKey(potionId);
    }

    public boolean isPotionActive(Potion potionIn) {
        return this.activePotionsMap.containsKey(potionIn.id);
    }

    /**
     * returns the PotionEffect for the supplied Potion if it is active, null
     * otherwise.
     */
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return (PotionEffect) this.activePotionsMap.get(potionIn.id);
    }

    /**
     * Clears potion metadata values if the entity has no potion effects. Otherwise,
     * updates potion effect color, ambience, and invisibility metadata values
     */
    protected void updatePotionMetadata() {
        /*9if (this.activePotionsMap.isEmpty()) {
            this.resetPotionEffectMetadata();
            this.setInvisible(false);
        } else {
            int i = PotionHelper.calcPotionLiquidColor(this.activePotionsMap.values());
            this.dataWatcher.updateObject(8,
                    Byte.valueOf((byte) (PotionHelper.getAreAmbient(this.activePotionsMap.values()) ? 1 : 0)));
            this.dataWatcher.updateObject(7, Integer.valueOf(i));
            this.setInvisible(this.isPotionActive(Potion.invisibility.id));
        }*/
    }


    /**
     * Taken directly from NMS. This is what updates properly the MotionXZ of a data
     *
     * @param strafe   Horizontal (to the data) movement based on the Euler Axis
     * @param forward  Vertical (to the data) movement based on the Euler Axis
     * @param friction Friction of the current data
     */
    protected void moveFlying(float strafe, float forward, float friction) {
        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;
            float f1 = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
            float f2 = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);
            this.motionX += strafe * f2 - forward * f1;
            this.motionZ += forward * f2 + strafe * f1;
        }
    }

    @Override
    protected void updateFallState(double y, boolean onGroundIn, WrappedBlock blockIn, Location pos) {
        if (!this.inWater) {
            this.handleWaterMovement();
        }
        super.updateFallState(y, onGroundIn, blockIn, pos);
    }

    /**
     * knocks back this entity
     */
    public void knockBack(Entity entityIn, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
        //KNOCK BACK

        // /!\ Important Notice /!\
        // --------------------------------------
        // The following lines of code have been subtracted from the code:
        //
        // f (this.rand.nextDouble() >= this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance)
        //				.getAttributeValue()) {
        //
        // This causes a 1/2 chance of this being invalid. This is only applicable to special entities such as Zombies.
        // Following such, it is safe to ignore it safely. To be investigated for +1.9
        // --------------------------------------
        this.isAirBorne = true;
        float f = MathHelper.sqrt_double(p_70653_3_ * p_70653_3_ + p_70653_5_ * p_70653_5_);
        float f1 = 0.4F;
        this.motionX /= 2.0D;
        this.motionY /= 2.0D;
        this.motionZ /= 2.0D;
        this.motionX -= p_70653_3_ / (double) f * (double) f1;
        this.motionY += (double) f1;
        this.motionZ -= p_70653_5_ / (double) f * (double) f1;

        if (this.motionY > Magic.MOTION_Y_KB_LIMIT) {
            this.motionY = Magic.MOTION_Y_KB_LIMIT;
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause) {
        Entity entity = cause.getEntity();
        /*EntityLivingBase entitylivingbase = this.func_94060_bK();

        if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.addToPlayerScore(this, this.scoreValue);
        }

        if (entity != null) {
            entity.onKillEntity(this);
        }*/

        this.dead = true;
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from
     * the armor first and then health second with the reduced value. Args:
     * damageAmount
     */
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {

    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        super.fall(distance, damageMultiplier);

        PotionEffect potioneffect = this.getActivePotionEffect(Potion.jump);
        float f = potioneffect != null ? (float) (potioneffect.getAmplifier() + 1) : 0.0F;
        int i = MathHelper.ceiling_float_int((distance - 3.0F - f) * damageMultiplier);

        if (i > 0) {
            this.attackEntityFrom(DamageSource.fall, (float) i);
        }
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getAttributeMap().getAttributeInstance(attribute);
    }

    public ServersideAttributeMap getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new ServersideAttributeMap();
        }

        return this.attributeMap;
    }

    protected void applyEntityAttributes() {
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
    }

    /**
     * main AI tick function, replaces updateEntityActionState
     */
    protected void updateAITick() {
        this.motionY += Magic.AI_TICK_MODIFIER;
    }

    protected void handleJumpLava() {
        this.motionY += Magic.LAVA_TICK_MODIFIER;
    }

    public boolean collidesWater() {
        // Todo Water better collision handler
        PlayerPosition from = data.movement.getLastLocation();
        return NMSManager.getInms().getCollidingBlocks(entityBoundingBox, data.getPlayer().getWorld())
                .stream().anyMatch(e -> e.equals(NMSMaterial.WATER.getMaterial()));
    }

    public boolean collidesLava() {
        // Todo Lava better collision handler
        PlayerPosition from = data.movement.getLastLocation();
        return NMSManager.getInms().getCollidingBlocks(entityBoundingBox, data.getPlayer().getWorld())
                .stream().anyMatch(e -> e.equals(NMSMaterial.WATER.getMaterial()));
    }

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements,
                                        boolean p_180426_10_) {
        this.newPosX = x;
        this.newPosY = y;
        this.newPosZ = z;
        this.newRotationYaw = (double) yaw;
        this.newRotationPitch = (double) pitch;
        this.newPosRotationIncrements = posRotationIncrements;
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    public double getAIMoveSpeed() {
        return this.landMovementFactor;
    }

    public void setAIMoveSpeed(float speed) {
        this.landMovementFactor = speed;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
    }

    public float getAbsorptionAmount() {
        return absorptionAmount;
    }

    public void setAbsorptionAmount(float absorptionAmount) {
        this.absorptionAmount = absorptionAmount;
    }

    protected int getSpeedBoostAmplifier(Player player) {
        if (player.hasEffect(PotionEffectType.SPEED)) {
            return player.getActivePotionEffects().stream().filter(effect -> effect.getType().equals(PotionEffectType.SPEED))
                    .findFirst().map(effect -> effect.getAmplifier() + 1).orElse(0);
        }
        return 0;
    }

    public int getJumpBoostAmplifier(Player player) {
        if (player.hasEffect(PotionEffectType.JUMP)) {
            return player.getActivePotionEffects().stream().filter(effect -> effect.getType().equals(PotionEffectType.JUMP))
                    .findFirst().map(effect -> effect.getAmplifier() + 1).orElse(0);
        }
        return 0;
    }
}
