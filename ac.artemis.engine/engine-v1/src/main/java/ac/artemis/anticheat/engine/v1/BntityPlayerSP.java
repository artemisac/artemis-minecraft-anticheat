package ac.artemis.anticheat.engine.v1;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.block.Block;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.minecraft.block.BlockFace;

import java.util.HashMap;

public class BntityPlayerSP extends BntityPlayer {

    public BntityPlayerSP(PlayerData data) {
        super(data);
        prepareToSpawn();
        new HashMap<>(this.activePotionsMap).forEach((id, potion) -> removePotionEffect(id));
    }

    /**
     * Used to tell if the data pressed forward twice. If this is at 0 and it's
     * pressed (And they are allowed to sprint, aka enough food on the ground etc)
     * it sets this to 7. If it's pressed and it's greater than 0 enable sprinting.
     */
    protected int sprintToggleTimer;

    /**
     * Ticks left before sprinting is disabled. Also known as "Sprint Grace" on NCP
     */
    public int sprintingTicksLeft;

    /** the last sneaking state sent to the server */
    private boolean serverSneakState;

    /** the last sprinting state sent to the server */
    private boolean serverSprintState;

    private boolean hasValidHealth;

    @Override
    public void onUpdate() {
        int ticks = 1;

        save: {
            final boolean isFucked = data.getVersion().isOrAbove(ProtocolVersion.V1_9);
            final boolean flag = isFucked && !data.prediction.isGround() && !data.prediction.isLastGround();

            if (!flag)
                break save;

            final boolean descend = data.prediction.getLastY() > data.prediction.getY();

            if (!descend)
                break save;

            double deltaY = data.prediction.getY() - data.prediction.getLastY();
            double motionY = data.entity.getMotionY();


            while (Math.abs(motionY - deltaY) > 1E-4 && Math.abs(motionY) < 0.03D) {
                ticks++;

                motionY -= 0.08D;
                motionY *= 0.98D;
            }
        }

        //Bukkit.broadcastMessage("Iterating for " + ticks);
        for (int i = 0; i < ticks; i++) {
            if (isChunkLoaded()) {
                super.onUpdate();

                // Todo Packet Prediction. Could be hella interesting ngl. Serves as a bad packet too
            /*
            if (this.isRiding()) {
				this.sendQueue.addToSendQueue(
						new C03GPacketPlayer.C05GPacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
				this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward,
						this.movementInput.jump, this.movementInput.sneak));
			} else {
				this.onUpdateWalkingPlayer();
			}
             */
            }
        }

    }

    @Override
    protected void updateEntityActionState() {
        super.updateEntityActionState();
        this.moveStrafing = this.playerControls.getMoveStrafing();
        this.moveForward = this.playerControls.getMoveForward();
        this.jumping = this.playerControls.isJumping();
    }

    /**
     * Called frequently so the entity can update its state every tick as required.
     * For example, zombies and skeletons use this to react to sunlight and start to
     * burn.
     */
    public void onLivingUpdate() {

        // Todo Sprinting Grace

        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0) {
                //this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }

        float f = 0.8F;


        this.playerControls.tick();
        this.setSprintingSilent(playerControls.isSprint());

        final boolean flag = this.playerControls.jumping;
        final boolean flag1 = this.sneaking;
        final boolean flag2 = this.playerControls.moveForward >= f;
        // Todo item usage control
        if (this.playerControls.using && !this.isRiding()) {
            this.playerControls.moveStrafing *= 0.2F;
            this.playerControls.moveForward *= 0.2F;
            this.sprintToggleTimer = 0;
        }

        // Push out block velocity
        pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
                this.posZ + (double) this.width * 0.35D);
        pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
                this.posZ - (double) this.width * 0.35D);
        pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
                this.posZ - (double) this.width * 0.35D);
        pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D,
                this.posZ + (double) this.width * 0.35D);

        final boolean flag3 = (float) data.getPlayer().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
        // Todo Spring toogle timer

        // Todo Rest of the method

        /*if (this.onGround && !flag1 && !flag2 && this.playerControls.moveForward >= f && !this.isSprinting() && flag3
                && !this.isUsingItem() && !this.isPotionActive(Potion.blindness)) {
            if (this.sprintToggleTimer <= 0 && !this.playerControls.isSprint()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && this.playerControls.moveForward >= f && flag3 && !this.isUsingItem()
                && !this.isPotionActive(Potion.blindness) && this.playerControls.isSprint()) {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (this.moveForward < f || this.isCollidedHorizontally || !flag3)) {
            this.setSprinting(false);
        }


        // -- Start Artemis Mega Edit --
        /*if (sprinting != playerControls.isSprint()) {
            this.setSprinting(playerControls.isSprint());
        }*/



        /*if (this.capabilities.allowFlying) {
            if (this.isSpectatorMode()) {
                if (!this.capabilities.isFlying) {
                    this.capabilities.isFlying = true;
                }
            } else if (!flag && this.jumping) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.flyToggleTimer = 0;
                }
            }
        }*/

        if (this.isFlying()) {
            if (data.prediction.getLastY() > data.prediction.getY()) {
                this.motionY -= capabilities.getFlySpeed() * 3.0F;
            }

            else if (data.prediction.getLastY() < data.prediction.getY()) {
                this.motionY += capabilities.getFlySpeed() * 3.0F;
            }
        }

        super.onLivingUpdate();

        // Todo abilities
    }

    private boolean pushOutOfBlocks(double x, double y, double z) {
        if (noClip) {
            return false;
        } else {
            Location blockpos = Minecraft.v().createLocation(data.getPlayer().getWorld(), x, y, z);

            double d0 = x - (double) blockpos.getBlockX();
            double d1 = z - (double) blockpos.getBlockZ();

            Block block = BlockUtil.getBlockAsync(blockpos);
            if (block == null) return false;

            if (!this.isOpenBlockSpace(block)) {

                int i = -1;
                double d2 = 9999.0D;

                if (this.isOpenBlockSpace(BlockUtil.getBlockAsync(blockpos.add(BlockFace.WEST.getModX(),
                        BlockFace.WEST.getModY(), BlockFace.WEST.getModZ()))) && d0 < d2) {
                    d2 = d0;
                    i = 0;
                }

                if (this.isOpenBlockSpace(BlockUtil.getBlockAsync(blockpos.add(BlockFace.EAST.getModX(),
                        BlockFace.EAST.getModY(), BlockFace.EAST.getModZ()))) && 1.0D - d0 < d2) {
                    d2 = 1.0D - d0;
                    i = 1;
                }

                if (this.isOpenBlockSpace(BlockUtil.getBlockAsync(blockpos.add(BlockFace.NORTH.getModX(),
                        BlockFace.NORTH.getModY(), BlockFace.NORTH.getModZ()))) && d1 < d2) {
                    d2 = d1;
                    i = 4;
                }

                if (this.isOpenBlockSpace(BlockUtil.getBlockAsync(blockpos.add(BlockFace.SOUTH.getModX(),
                        BlockFace.SOUTH.getModY(), BlockFace.SOUTH.getModZ()))) && 1.0D - d1 < d2) {
                    d2 = 1.0D - d1;
                    i = 5;
                }

                float f = 0.1F;

                if (i == 0) {
                    //System.out.println("Modified motion -X");
                    this.motionX = -f;
                }

                if (i == 1) {
                    //System.out.println("Modified motion X");
                    this.motionX = f;
                }

                if (i == 4) {
                    //System.out.println("Modified motion -Z");
                    this.motionZ = -f;
                }

                if (i == 5) {
                    //System.out.println("Modified motion Z");
                    this.motionZ = f;
                }
            }
        }
        return false;
    }

    /**
     * Updates health locally.
     */
    public void setPlayerSPHealth(float health) {
        if (this.hasValidHealth) {
            float f = this.getHealth() - health;

            if (f <= 0.0F) {
                this.setHealth(health);

                if (f < 0.0F) {
                    this.hurtResistantTime = this.maxHurtResistantTime / 2;
                }
            } else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    /**
     * Set sprinting switch for Entity.
     */
    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from
     * the armor first and then health second with the reduced value. Args:
     * damageAmount
     */
    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    /**
     * Returns true if the block at the given BlockPos and the block above it are
     * NOT full cubes.
     */
    private boolean isOpenBlockSpace(Block pos) {
        if (pos == null) return true;
        final Material mid = pos.getType();
        final Material top = pos.getRelative(BlockFace.UP).getType();
        return !isNormalCube(mid) && !isNormalCube(top);
    }

    public boolean isNormalCube(Material material) {
        return material.isBlock() && material.isOccluding() && material.isSolid();
    }

    private boolean isSpectatorMode(){
        return data.getPlayer().getGameMode().name().equalsIgnoreCase("SPECTATOR");
    }
}
