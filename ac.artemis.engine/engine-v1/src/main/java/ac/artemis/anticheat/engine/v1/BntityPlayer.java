package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.impl.SharedMonsterAttributes;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.anticheat.engine.v1.utils.EntityUtil;
import ac.artemis.core.v4.emulator.entity.utils.ItemUtil;
import ac.artemis.core.v4.emulator.entity.utils.PlayerCapabilities;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.magic.Magic;
import ac.artemis.packet.minecraft.EnchantType;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.LivingEntity;
import ac.artemis.packet.minecraft.entity.impl.Arrow;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.nms.MathHelper;
import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import lombok.Getter;

@Getter
public class BntityPlayer extends BntityLivingBase {

    public BntityPlayer(PlayerData data) {
        super(data);
        this.capabilities = new PlayerCapabilities();
    }

    /**
     * This is the item that is in use when the player is holding down the
     * useItemButton (e.g., bow, food, sword)
     */
    private ItemStack itemInUse;
    private PlayerEnums.Hand itemInHand;

    /**
     * This field starts off equal to getMaxItemUseDuration and is decremented on
     * each tick
     */
    private int itemInUseCount;

    private long lastItemInUse;

    public int flyToggleTimer;

    private Entity lastAttack;

    @Override
    public void prepareToSpawn() {
        this.setSize(0.6F, 1.8F);
        super.prepareToSpawn();
        //capabilities.setFlySpeed(data.getPlayer().getFlySpeed());
        //capabilities.setWalkSpeed(data.getPlayer().getWalkSpeed());
        //capabilities.setAllowFlying(data.getPlayer().getAllowFlight());
        //capabilities.setCreativeMode(data.getPlayer().getGameMode().equals(GameMode.CREATIVE));
    }

    @Override
    protected void onLivingUpdate() {
        // Todo add fly toggle timer and stuff

        if (this.flyToggleTimer > 0) {
            --this.flyToggleTimer;
        }

        super.onLivingUpdate();

        // Movement updater
        IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        //iattributeinstance.setBaseValue(this.capabilities.getWalkSpeed());

        /*
         * Why! It's the same thing! Why dear lord!
         * Well, turns out such a small difference can cause a slight offset! Hurray! So instead, it's
         * more favorable to just replicate the MCP fuckery in order to stay consistent with the client
         * at a 0,0 offset. Don't blame me, blame the cunts who remade the game for no reason.
         */
        if (this instanceof BntityPlayerXYZ_1_15) {
            this.jumpMovementFactor = 0.02F;
            if (this.isSprinting()) {
                this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + 0.005999999865889549D);
            }
        } else {
            this.jumpMovementFactor = Magic.JUMP_MOVE_FACTOR;

            if (this.isSprinting()) {
                this.jumpMovementFactor = (float) ((double) this.jumpMovementFactor + (double) Magic.JUMP_MOVE_FACTOR * 0.3D);
            }
        }

        this.setAIMoveSpeed((float) iattributeinstance.getAttributeValue());
    }

    @Override
    protected void moveEntityWithHeading(float strafe, float forward) {
        if (this.isFlying() && this.riddingEntity == null) {
            double d3 = this.motionY;
            float f = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed() * (float) (this.isSprinting() ? 2 : 1);
            super.moveEntityWithHeading(strafe, forward);
            this.motionY = d3 * 0.6D;
            this.jumpMovementFactor = f;
        } else {
            super.moveEntityWithHeading(strafe, forward);
        }
    }

    @Override
    protected void onUpdate() {

        this.noClip = this.isSpectator();

        if (this.isSpectator()) {
            this.onGround = false;
        }

        if (this.itemInUse != null) {
            if (--this.itemInUseCount <= 0) {
                //this.onItemUseFinish();
            }

            try {
                final boolean main = itemInHand == null || itemInHand.equals(PlayerEnums.Hand.MAIN_HAND);
                final ac.artemis.packet.minecraft.inventory.ItemStack stack;

                if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                    stack = main
                            ? data.getPlayer().getInventory().getItem(data.prediction.getSlot())
                            : data.getPlayer().getInventory().getItemInOffHand();
                } else {
                    stack = data.getPlayer().getInventory().getItem(data.prediction.getSlot());
                }

                if (stack != null && !stack.v().equals(itemInUse)) {
                    //System.out.println("Stopped using item! current:" + stack + " current:" + itemInUse);
                    this.clearItemInUse();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }

        // TODO Sleep timer + all of the methods in EntityPlayer#onUpdate
        super.onUpdate();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.10000000149011612D);
    }

    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword) by
     * holding down the useItemButton
     */
    public boolean isUsingItem() {
        return this.itemInUse != null;
    }

    /**
     * gets the duration for how long the current itemInUse has been in use
     */
    public int getItemInUseDuration() {
        return this.isUsingItem() ? ItemUtil.getItemUseByItem(data.entity, itemInUse) - this.itemInUseCount : 0;
    }

    public void stopUsingItem() {
        if (this.itemInUse != null) {
            //this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
        }

        this.clearItemInUse();
    }

    public void clearItemInUse() {
        this.itemInUse = null;
        this.itemInUseCount = 0;

        this.lastItemInUse = System.currentTimeMillis();

        //if (!this.worldObj.isRemote) {
            //this.setEating(false);
        //}
    }

    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int
     * maxItemUseDuration
     */
    public void setItemInUse(ItemStack stack, PlayerEnums.Hand hand, int duration) {
        if (stack != null && duration > 0) {
            this.itemInUse = stack;
            this.itemInUseCount = duration;
            this.itemInHand = hand;

            /*if (!this.worldObj.isRemote) {
                this.setEating(true);
            }*/
        }
        this.lastItemInUse = System.currentTimeMillis();
    }

    /**
     * Used for when item use count runs out, ie: eating completed
     */
    public void onItemUseFinish() {
        if (this.itemInUse != null) {
            int i = this.itemInUse.getAmount();
            //ItemUtil.finishItem(this.itemInUse, this);
            //ItemStack itemstack = this.itemInUse.onItemUseFinish(this.worldObj, this);

            /*if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i) {
                this.inventory.mainInventory[this.inventory.currentItem] = itemstack;

                if (itemstack.stackSize == 0) {
                    this.inventory.mainInventory[this.inventory.currentItem] = null;
                }
            }*/

            this.clearItemInUse();
        }
    }

    /*public boolean isBlocking() {
        return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == EnumAction.BLOCK;
    }*/

    /**
     * Attacks for the player the targeted entity with the currently equipped item.
     * The equipped item has hitEntity called on it. Args: targetEntity
     */

    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (EntityUtil.canAttack(targetEntity)) {
            if (!this.hitByEntity(targetEntity)) {
                float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                //float f1 = NMSManager.getInms().getEnchantModifier(this.getHeldItem().v(), targetEntity);


                if (this.getHeldItem() != null && this.getHeldItem().hasEnchant(EnchantType.KNOCKBACK)) {
                    i += this.getHeldItem().getEnchantLevel(EnchantType.KNOCKBACK);
                }

                if (this.playerControls.isSprint()) {
                    ++i;
                }

                if (f > 0.0F/* || f1 > 0.0F*/) {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater()
                            && !this.isPotionActive(Potion.blindness) && this.riddingEntity == null
                            && targetEntity instanceof LivingEntity;

                    if (flag && f > 0.0F) {
                        f *= 1.5F;
                    }

                    //f = f + f1;
                    boolean flag1 = false;
                    /*int j = EnchantmentHelper.getFireAspectModifier(this);

                    if (targetEntity instanceof LivingEntity && j > 0 && !targetEntity.isBurning()) {
                        flag1 = true;
                        targetEntity.setFire(1);
                    }*/

                    /*double d0 = targetEntity.motionX;
                    double d1 = targetEntity.motionY;
                    double d2 = targetEntity.motionZ;*/
                    //boolean flag2 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage(this), f);

                    //if (!(targetEntity instanceof Damageable)) return;

                    //Damageable living = (Damageable) targetEntity;
                    boolean flag2 = EntityUtil.canDamage(targetEntity);
                    if (flag2) {
                        if (i > 0) {
                            this.lastMotionX = motionX;
                            this.lastMotionY = motionY;
                            this.lastMotionZ = motionZ;
                            this.motionX *= 0.6D;
                            this.motionZ *= 0.6D;
                            //this.setSprinting(false);
                        }


                        /*if (flag) {
                            // CRIT
                            this.onCriticalHit(targetEntity);
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(targetEntity);
                        }

                        this.setLastAttacker(targetEntity);*/


                        /*if (itemstack != null && entity instanceof EntityLivingBase) {
                            itemstack.hitEntity((EntityLivingBase) entity, this);

                            if (itemstack.stackSize <= 0) {
                                this.destroyCurrentEquippedItem();
                            }
                        }

                        this.addExhaustion(0.3F);*/
                    }
                }
            }
        }
    }

    public ac.artemis.packet.minecraft.inventory.ItemStack getHeldItem() {
        try {
            final boolean main = itemInHand == null || itemInHand.equals(PlayerEnums.Hand.MAIN_HAND);
            final ac.artemis.packet.minecraft.inventory.ItemStack stack;

            if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                stack = main
                        ? data.getPlayer().getInventory().getItem(data.prediction.getSlot())
                        : data.getPlayer().getInventory().getItemInOffHand();
            } else {
                stack = data.getPlayer().getInventory().getItem(data.prediction.getSlot());
            }
            return stack;
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }

    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable(source)) {
            return false;
        } else if (this.capabilities.disableDamage && !source.canHarmInCreative()) {
            return false;
        } else {
            this.entityAge = 0;

            if (this.getHealth() <= 0.0F) {
                return false;
            } else {
                /*if (this.isPlayerSleeping()) {
                    this.wakeUpPlayer(true, true, false);
                }*/

                if (source.isDifficultyScaled()) {
                    /*switch (data.getPlayer().getWorld().getDifficulty()) {
                        case Difficulty.PEACEFUL:
                            amount = 0.0F;
                            break;
                        case Difficulty.EASY:
                            amount = amount / 2.0F + 1.0F;
                            break;
                        case Difficulty.HARD:
                            amount = amount * 3.0F / 2.0F;
                            break;
                    }*/
                }

                if (amount == 0.0F) {
                    return false;
                } else {
                    Entity entity = source.getEntity();

                    /*if (entity instanceof Arrow && ((Arrow) entity).getShooter() != null) {
                        //entity = ((Arrow) entity).getShooter();
                    }*/

                    return super.attackEntityFrom(source, amount);
                }
            }
        }
    }

    /**
     * Called when the mob's health reaches 0.
     */
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        this.setSize(0.2F, 0.2F);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612D;

        this.motionX = (double) (-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F)
                * 0.1F);
        this.motionZ = (double) (-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float) Math.PI / 180.0F)
                * 0.1F);
    }

    @Override
    public void fall(float distance, float damageMultiplier) {
        if (!this.capabilities.allowFlying) {
            super.fall(distance, damageMultiplier);
        }
    }

    /**
     * the movespeed used for the new AI system
     * @return
     */
    @Override
    public double getAIMoveSpeed() {
        return this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }

    public boolean isPushedByWater() {
        return !this.capabilities.isFlying;
    }

    public boolean isSpectator(){
        return data.getPlayer().getGameMode().name().equalsIgnoreCase("SPECTATOR");
    }
}
