package ac.artemis.anticheat.engine.v2;

import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.anticheat.engine.v2.runner.PredictionRunner;
import ac.artemis.anticheat.engine.v2.runner.impl.BruteforcePredictionRunnerV3;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;
import ac.artemis.core.v4.emulator.attribute.impl.SharedMonsterAttributes;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v4.emulator.attribute.map.ServersideAttributeMap;
import ac.artemis.core.v4.emulator.damage.DamageSource;
import ac.artemis.core.v4.emulator.datawatcher.DataWatcher;
import ac.artemis.core.v4.emulator.entity.utils.PlayerCapabilities;
import ac.artemis.core.v4.emulator.entity.utils.PlayerControls;
import ac.artemis.core.v4.emulator.magic.Magic;
import ac.artemis.core.v4.emulator.potion.Potion;
import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.emulator.attributes.*;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.collision.CollisionProvider;
import ac.artemis.core.v5.emulator.collision.impl.LegacyBoundingBoxProvider;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherFactory;
import ac.artemis.core.v5.emulator.datawatcher.DataWatcherReader;
import ac.artemis.core.v5.emulator.modal.Motion;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.core.v5.emulator.world.impl.CachedWorld;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.PotionEffectType;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.inventory.ItemStack;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.minecraft.MathHelper;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityTeleport;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerExplosion;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerUpdateAttributes;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class ArtemisData implements Emulator {
    private PlayerData player;
    private ArtemisWorld world;

    public static final UUID SPRINTING_SPEED_BOOST_MODIFIER_UUID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    public static final AttributeModifier SPRINTING_SPEED_BOOST_MODIFIER = (new AttributeModifier(
            SPRINTING_SPEED_BOOST_MODIFIER_UUID, "Sprinting speed boost", Magic.SPRINT_MODIFIER, 2))
            .setSaved(false);

    public ArtemisData(PlayerData player) {
        this.player = player;
        this.world = new CachedWorld(player.getPlayer().getWorld());
        this.runner = new BruteforcePredictionRunnerV3(this);
        initAttributes();
        this.setSize(0.6F, 1.8F);
    }
    public static final BoundingBox ZERO_AABB = new BoundingBox(0.0,  0.0,0.0,0.0,0.0,0.0);
    private BoundingBox entityBoundingBox = ZERO_AABB.cloneBB();
    private PredictionRunner runner;
    private CollisionProvider collisionProvider = new LegacyBoundingBoxProvider();

    private final PlayerCapabilities capabilities = new PlayerCapabilities();

    private double x;
    private double y;
    private double z;

    private double outputX;
    private double outputY;
    private double outputZ;

    public float width;
    public float height;

    private double serverX;
    private double serverY;
    private double serverZ;

    private double lastMotionX;
    private double lastMotionY;
    private double lastMotionZ;

    private double motionX;
    private double motionY;
    private double motionZ;

    private double maxMotionX;
    private double maxMotionY;
    private double maxMotionZ;

    private double maxDistance;

    private float moveForward;
    private float moveStrafe;

    private double distance;
    private int iteration;
    private int jumpTicks;

    private float fallDistance;

    private boolean collidedHorizontally, lastCollidedHorizontally;
    private boolean collidedVertically;
    private boolean collidedGround;
    private boolean collided;
    private boolean wasPos;

    private boolean sprintAttributeState;

    private DataWatcher dataWatcher = new DataWatcher(this);

    public PlayerMovement lastPositionPrevious;
    public PlayerMovement lastRotationPrevious;

    private PlayerControls playerControls = new PlayerControls(this);
    private AttributeMap attributeMap = new AttributeMap();
    private List<String> tags = new ArrayList<>();

    // LEGACY - TO REPLACE
    private ServersideAttributeMap modifierMap;
    private final Map<Integer, PotionEffect> activePotionsMap = new HashMap<>();

    private void initAttributes() {
        // Booleans
        attributeMap.put(EntityAttributes.SPRINT, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.LAST_SPRINT, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.SNEAK, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.CHUNK_LOADED, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.WEB, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.LADDER, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.NOCLIP, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.GROUND, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.LAST_GROUND, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.FLYING, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.JUMPING, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.LAVA, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.WATER, new StandardAttribute<>(false));
        attributeMap.put(EntityAttributes.COMPENSATE_WORLD, new StandardAttribute<>(false));

        // Floats
        attributeMap.put(EntityAttributes.ATTRIBUTE_SPEED, new StandardAttribute<>(player.getPlayer().getWalkSpeed() / 2.F));
        attributeMap.put(EntityAttributes.YAW, new StandardAttribute<>(player.getPrediction().getYaw()));

        // Doubles
        attributeMap.put(EntityAttributes.FALL_DISTANCE, new StandardAttribute<>(0.0D));

        // Entities
        attributeMap.put(EntityAttributes.ATTACKED, new StandardAttribute<>(null));

        // Ints
        attributeMap.put(EntityAttributes.JUMP_TICKS, new StandardAttribute<>(0));
    }

    public void onUpdate() {
        runner.run();
        this.updatePotionEffects();
    }

    @Override
    public float getStepHeight() {
        return 0.6F; // TODO: Can change with version be careful.
    }

    @Override
    public boolean isChunkLoaded() {
        return attributeMap.poll(EntityAttributes.CHUNK_LOADED);
    }

    @Override
    public boolean isFlying() {
        return attributeMap.poll(EntityAttributes.FLYING);
    }

    @Override
    public List<ac.artemis.packet.minecraft.block.Block> getGhostBlocks() {
        return Collections.emptyList();
    }

    @Override
    public int getJumpBoostAmplifier(Player player) {
        if (player.hasEffect(PotionEffectType.JUMP)) {
            return player
                    .getActivePotionEffects()
                    .stream()
                    .filter(effect -> effect.getType().equals(PotionEffectType.JUMP))
                    .findFirst()
                    .map(effect -> effect.getAmplifier() + 1)
                    .orElse(0);
        }
        return 0;
    }

    @Override
    public boolean isInWeb() {
        return attributeMap.poll(EntityAttributes.WEB);
    }

    @Override
    public PlayerData getData() {
        return player;
    }

    @Override
    public PlayerMovement toMovement() {
        // No pitch.
        return new PlayerMovement(
                player.getPlayer(),
                outputX,
                outputY,
                outputZ,
                getRotationYaw(),
                0f,
                System.currentTimeMillis()
        );
    }

    @Override
    public BoundingBox getEntityBoundingBox() {
        return entityBoundingBox;
    }

    @Override
    public void setEntityBoundingBox(BoundingBox entityBoundingBoxIn) {
        this.entityBoundingBox = entityBoundingBoxIn;
    }

    public boolean isSneaking() {
        return attributeMap.poll(EntityAttributes.SNEAK);
    }

    public void setSneaking(final boolean value) {
        attributeMap.get(EntityAttributes.SNEAK).set(value);
    }

    @Override
    public void attackTargetEntityWithCurrentItem(Entity entityIn) {

    }

    @Override
    public PlayerCapabilities getCapabilities() {
        return capabilities;
    }

    @Override
    public void setItemInUse(ItemStack stack, PlayerEnums.Hand hand, int duration) {

    }

    public boolean isSprinting() {
        return attributeMap.poll(EntityAttributes.SPRINT);
    }

    @Override
    public boolean isAttributeSprinting() {
        return sprintAttributeState;
    }

    @Override
    public boolean isUsingItem() {
        return false;
    }

    @Override
    public boolean isWasPos() {
        return wasPos;
    }

    @Override
    public void setWasPos(boolean value) {
        this.wasPos = value;
    }

    @Override
    public float getRotationPitch() {
        return 0;
    }

    public void setSprinting(final boolean value) {
        attributeMap.get(EntityAttributes.SPRINT).set(value);
    }

    public void setInWeb() {
        attributeMap.get(EntityAttributes.WEB).set(true);
    }

    @Override
    public float getRotationYaw() {
        return attributeMap.poll(EntityAttributes.YAW);
    }

    public void setYaw(final float yaw) {
        attributeMap.get(EntityAttributes.YAW).set(yaw);
    }

    public void setSprintAttribute(boolean clear) {
        sprintAttributeState = clear;
        getModifierMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).removeModifier(SPRINTING_SPEED_BOOST_MODIFIER);

        if (clear) {
            getModifierMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).applyModifier(SPRINTING_SPEED_BOOST_MODIFIER);
        }
    }

    public void setChunkLoaded(final boolean value) {
        attributeMap.get(EntityAttributes.CHUNK_LOADED).set(value);
    }

    public boolean isGround() {
        return attributeMap.poll(EntityAttributes.GROUND);
    }

    public void setGround(final boolean value) {
        attributeMap.get(EntityAttributes.GROUND).set(value);
    }

    public boolean isJumping() {
        return attributeMap.poll(EntityAttributes.JUMPING);
    }

    public void setJumping(final boolean value) {
        attributeMap.get(EntityAttributes.JUMPING).set(value);
    }

    public boolean getLadder() {
        final int x = MathHelper.floor_double(this.x);
        final int y = MathHelper.floor_double(this.getEntityBoundingBox().minY);
        final int z = MathHelper.floor_double(this.z);

        final Block block = world.getBlockAt(x, y, z);

        return block != null
                && (block.getMaterial().equals(NMSMaterial.LADDER)
                    || block.getMaterial().equals(NMSMaterial.VINE))
                && !player.getPlayer().getGameMode().equals(GameMode.SPECTATOR);
    }

    public void setAttackedEntity(final Entity entity) {
        attributeMap.get(EntityAttributes.ATTACKED).set(entity);
    }

    public Entity getAttackedEntity() {
        return attributeMap.poll(EntityAttributes.ATTACKED);
    }

    public void resetPositionToBB() {
        this.outputX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
        this.outputY = this.getEntityBoundingBox().minY;
        this.outputZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
    }

    public void setPosition(double x, double y, double z) {
        this.x = this.outputX = x;
        this.y = this.outputY = y;
        this.z = this.outputZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setEntityBoundingBox(new BoundingBox(x - (double) f, y, z - (double) f,
                x + (double) f, y + (double) f1, z + (double) f));
    }

    public void apply(final TransitionData data) {
        this.attributeMap = data.getAttributeMap();
        this.motionX = data.getMotionX();
        this.motionY = data.getMotionY();
        this.motionZ = data.getMotionZ();
        this.maxMotionX = data.getMaxMotionX();
        this.maxMotionY = data.getMaxMotionY();
        this.maxMotionZ = data.getMaxMotionZ();
        this.entityBoundingBox = data.getBoundingBox().cloneBB();
        this.x = this.outputX = data.getX();
        this.y = this.outputY = data.getY();
        this.z = this.outputZ = data.getZ();
        this.collided = data.isCollided();
        this.collidedGround = data.isCollidedGround();
        this.collidedHorizontally = data.isCollidedHorizontally();
        this.collidedVertically = data.isCollidedVertically();
        this.moveForward = data.getMoveForward();
        this.moveStrafe = data.getMoveStrafe();
        this.setLastSprinting(sprintAttributeState);
        this.setSprintAttribute(data.isSprintingAttribute());
    }

    public TransitionData snapshot() {
        return new TransitionData(player)
                .setMotionX(motionX)
                .setMotionY(motionY)
                .setMotionZ(motionZ)
                .setMaxMotionX(maxMotionX)
                .setMaxMotionY(maxMotionY)
                .setMaxMotionZ(maxMotionZ)
                .setBoundingBox(entityBoundingBox.cloneBB())
                .setX(x)
                .setY(y)
                .setZ(z)
                .setCollided(collided)
                .setCollidedGround(collidedGround)
                .setCollidedHorizontally(collidedHorizontally)
                .setCollidedVertically(collidedVertically)
                .setMoveForward(moveForward)
                .setMoveStrafe(moveStrafe)
                .setSprintingAttribute(sprintAttributeState)
                .setAttributeMap(attributeMap.copy());
    }

    @Override
    public void setServerPosition(double x, double y, double z) {
        this.serverX = x;
        this.serverY = y;
        this.serverZ = z;
    }

    @Override
    public boolean isLastSprinting() {
        return attributeMap.poll(EntityAttributes.LAST_SPRINT);
    }

    @Override
    public void setLastSprinting(boolean sprinting) {
        attributeMap.get(EntityAttributes.LAST_SPRINT).set(sprinting);
    }

    @Override
    public String readTags() {
        return String.join(" ", tags);
    }

    @Override
    public void handlePacket(GPacketPlayServerPosition packet) {
        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();
        float yaw = packet.getYaw();
        float pitch = packet.getPitch();

        if (packet.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.X)) {
            x += this.x;
        }
        else {
            this.motionX = 0.0D;
        }

        if (packet.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y)) {
            y += this.y;
        }
        else {
            this.motionY = 0.0D;
        }

        if (packet.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Z)) {
            z += this.z;
        }
        else {
            this.motionZ = 0.0D;
        }

        if (packet.getFlags().contains(GPacketPlayServerPosition.PlayerTeleportFlags.Y_ROT)) {
            yaw += this.getRotationYaw();
        }

        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setYaw(yaw);
    }

    public void setSize(float width, float height) {
        if (width != this.width || height != this.height) {
            this.width = width;
            this.height = height;
            this.setEntityBoundingBox(
                    new BoundingBox(this.getEntityBoundingBox().minX, this.getEntityBoundingBox().minY,
                            this.getEntityBoundingBox().minZ, this.getEntityBoundingBox().minX + (double) this.width,
                            this.getEntityBoundingBox().minY + (double) this.height,
                            this.getEntityBoundingBox().minZ + (double) this.width));

        }
    }

    @Override
    public void setJumpTicks(final int ticks) {
        this.attributeMap.get(EntityAttributes.JUMP_TICKS).set(ticks);
    }

    public ArtemisWorld getWorld() {
        return world;
    }

    @Override
    public PlayerEnums.Hand getItemInHand() {
        return null;
    }

    @Override
    public void clearItemInUse() {

    }

    @Override
    public void stopUsingItem() {

    }

    @Override
    public void setHealth(float health) {

    }

    @Override
    public void onDeath(DamageSource cause) {

    }

    @Override
    public void onItemUseFinish() {

    }

    public void resetWorld() {
        this.world = new CachedWorld(
                player.getPlayer().getWorld()
        );
    }

    @Override
    public void prepareToSpawn() {
        this.setSize(0.6F, 1.8F);
        clearActivePotions();
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        this.lastMotionX = motionX;
        this.lastMotionY = motionY;
        this.lastMotionZ = motionZ;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    @Override
    public void addPotionEffect(PotionEffect potioneffectIn) {
        if (this.isPotionApplicable(potioneffectIn)) {
            if (this.activePotionsMap.containsKey(potioneffectIn.getPotionID())) {
                this.activePotionsMap.get(potioneffectIn.getPotionID())
                        .combine(potioneffectIn);
                this.onChangedPotionEffect(
                        this.activePotionsMap.get(potioneffectIn.getPotionID()), true);
            } else {
                this.activePotionsMap.put(potioneffectIn.getPotionID(), potioneffectIn);
                this.onNewPotionEffect(potioneffectIn);
            }
        }
    }

    @Override
    public void removePotionEffect(int potionId) {
        PotionEffect potioneffect = (PotionEffect) this.activePotionsMap.remove(potionId);

        if (potioneffect != null) {
            this.onFinishedPotionEffect(potioneffect);
        }
    }

    @Override
    public void handlePacket(GPacketPlayServerEntityTeleport packet) {

    }

    @Override
    public void handlePacket(GPacketPlayServerUpdateAttributes packet) {
        // TODO: 8/25/2021 Handle attributes properly? Ghast's system overrides the movement speed. (super not cool btw)
        BaseAttributeMap baseattributemap = this.getModifierMap();

        for (GPacketPlayServerUpdateAttributes.Snapshot snapshot : packet.getAttributes()) {
            IAttributeInstance iattributeinstance = baseattributemap.getAttributeInstanceByName(snapshot.getLocalName());

            if (iattributeinstance == null) {
                iattributeinstance = baseattributemap.registerAttribute(new RangedAttribute(null,
                        snapshot.getLocalName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
            }



            iattributeinstance.setBaseValue(snapshot.getBaseValue());
            iattributeinstance.removeAllModifiers();

            for (AttributeModifier attributemodifier : snapshot.getModifiers().stream()
                    .map(e -> new AttributeModifier(e.getId(), e.getName(), e.getAmount(), e.getOperation()))
                    .collect(Collectors.toList())) {
                iattributeinstance.applyModifier(attributemodifier);
            }
        }
    }

    public IAttributeInstance getEntityAttribute(IAttribute attribute) {
        return this.getModifierMap().getAttributeInstance(attribute);
    }

    @Override
    public DataWatcher getDataWatcher() {
        return dataWatcher;
    }

    @Override
    public DataWatcherReader getDataWatcherFactory() {
        return new DataWatcherFactory().setData(getData()).build();
    }

    @Override
    public void handleExplosion(GPacketPlayServerExplosion packet) {
        this.motionX += packet.getMotionX();
        this.motionY += packet.getMotionY();
        this.motionZ += packet.getMotionZ();
    }

    @Override
    public PlayerMovement getLastPositionPrevious() {
        return lastPositionPrevious;
    }

    @Override
    public void setLastPositionPrevious(PlayerMovement playerMovementIn) {
        this.lastPositionPrevious = playerMovementIn;
    }

    @Override
    public PlayerMovement getLastRotationPrevious() {
        return lastRotationPrevious;
    }

    @Override
    public void setLastRotationPrevious(PlayerMovement playerMovementIn) {
        this.lastRotationPrevious = playerMovementIn;
    }

    @Override
    public boolean isPreviousGround() {
        return attributeMap.poll(EntityAttributes.LAST_GROUND);
    }

    @Override
    public void setPreviousGround(boolean previousGroundIn) {
        attributeMap.get(EntityAttributes.LAST_GROUND).set(previousGroundIn);
    }

    @Override
    public boolean isOnGround() {
        return attributeMap.poll(EntityAttributes.GROUND);
    }

    @Override
    public boolean isNoClip() {
        return attributeMap.poll(EntityAttributes.NOCLIP);
    }

    @Override
    public Point getPosition() {
        return new Point(x, y ,z);
    }

    public Point getMaxMotion() {
        return new Point(Math.abs(maxMotionX), Math.abs(maxMotionY), Math.abs(maxMotionZ));
    }

    public Point getVectorMotion() {
        return new Point(Math.abs(motionX), Math.abs(motionY), Math.abs(motionZ));
    }

    public Motion getMotion2() {
        return new Motion(motionX, motionY, motionZ);
    }

    public Velocity getMotion() {
        return new Velocity(motionX, motionY, motionZ);
    }

    @Override
    public PlayerControls getPlayerControls() {
        return playerControls;
    }

    @Override
    public boolean isInLava() {
        return attributeMap.poll(EntityAttributes.LAVA);
    }

    @Override
    public boolean isInWater() {
        return attributeMap.poll(EntityAttributes.WATER);
    }

    @Override
    public float getMoveStrafing() {
        return moveStrafe;
    }

    @Override
    public float getMoveForward() {
        return moveForward;
    }

    @Override
    public int getJumpTicks() {
        return attributeMap.poll(EntityAttributes.JUMP_TICKS);
    }

    @Override
    public double getAIMoveSpeed() {
        return this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
    }

    @Override
    public boolean isOnLadder() {
        return attributeMap.poll(EntityAttributes.LADDER);
    }

    @Override
    public boolean isPotionActive(Potion potionIn) {
        return false;
    }

    @Override
    public PotionEffect getActivePotionEffect(Potion potionIn) {
        return activePotionsMap.get(potionIn.id);
    }

    @Override
    public void setNoClip(boolean noClipIn) {
        // This method does nothing because the emulator does not use it.
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        this.setYaw(yaw);
        // this emulator doesn't need pitch
    }

    public List<BoundingBox> getCollidingBoxes(BoundingBox boundingBox, boolean compensate) {
        return collisionProvider.getBoundingBoxes(this, boundingBox);
    }

    public ServersideAttributeMap getModifierMap() {
        if (this.modifierMap == null) {
            this.modifierMap = new ServersideAttributeMap();
            applyEntityAttributes();
        }

        return this.modifierMap;
    }

    protected void applyEntityAttributes() {
        this.getModifierMap().registerAttribute(SharedMonsterAttributes.maxHealth);
        this.getModifierMap().registerAttribute(SharedMonsterAttributes.knockbackResistance);
        this.getModifierMap().registerAttribute(SharedMonsterAttributes.movementSpeed);
    }

    public <T> Attribute<T> getAttribute(AttributeKey attributeKey) {
        return attributeMap.poll(attributeKey);
    }

    public <T> void setAttribute(AttributeKey attributeKey, T value) {
        attributeMap.get(attributeKey).set(value);
    }

    // Todo - port all of this later

    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        return true;
    }

    /**
     * Remove the speified potion effect from this entity.
     */
    public void removePotionEffectClient(int potionId) {
        this.activePotionsMap.remove(potionId);
    }

    protected void onNewPotionEffect(PotionEffect id) {
        Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getModifierMap(),
                id.getAmplifier());
    }

    protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
        Potion.potionTypes[id.getPotionID()].removeAttributesModifiersFromEntity(this, this.getModifierMap(),
                id.getAmplifier());
        Potion.potionTypes[id.getPotionID()].applyAttributesModifiersToEntity(this, this.getModifierMap(),
                id.getAmplifier());
    }

    protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
        Potion.potionTypes[p_70688_1_.getPotionID()].removeAttributesModifiersFromEntity(this,
                this.getModifierMap(), p_70688_1_.getAmplifier());
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

    protected void updatePotionEffects() {
        for (Map.Entry<Integer, PotionEffect> entry : new HashSet<>(activePotionsMap.entrySet())) {
            final PotionEffect potioneffect = entry.getValue();
            if (!potioneffect.onUpdate(this)) {
                activePotionsMap.remove(entry.getKey());
                this.onFinishedPotionEffect(potioneffect);
            } else if (potioneffect.getDuration() % 600 == 0) {
                this.onChangedPotionEffect(potioneffect, false);
            }
        }
    }

    public boolean isPotionActive(int potionId) {
        return this.activePotionsMap.containsKey(potionId);
    }
}
