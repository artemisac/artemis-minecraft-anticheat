package ac.artemis.core.v5.emulator;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.blocks.BlockUtil;
import ac.artemis.core.v5.emulator.attributes.AttributeKey;
import ac.artemis.core.v5.emulator.attributes.AttributeMap;
import ac.artemis.core.v5.emulator.attributes.EntityAttributes;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.emulator.tags.Tags;
import ac.artemis.core.v5.emulator.utils.OutputAction;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.nms.MathHelper;
import lombok.Data;

import java.util.*;

@Data
public class TransitionData {
    private final PlayerData data;

    public TransitionData(PlayerData data) {
        this.data = data;

        //System.out.println(":((((");
    }

    private double x;
    private double y;
    private double z;

    private double targetX;
    private double targetY;
    private double targetZ;

    private BoundingBox boundingBox;

    private float moveForward;
    private float moveStrafe;

    private float slipperiness;
    private float friction;

    private double motionX;
    private double motionY;
    private double motionZ;

    private double maxMotionX;
    private double maxMotionY;
    private double maxMotionZ;

    private boolean collidedHorizontally, lastCollidedHorizontally;
    private boolean collidedVertically;
    private boolean collidedGround;
    private boolean collided;

    private boolean sprintingAttribute;

    private AttributeMap attributeMap;

    private boolean dumbFix;

    private final List<OutputAction> actionList = new ArrayList<>();
    private final Set<Tags> tags = new HashSet<>();

    public TransitionData addAction(final OutputAction... actions) {
        actionList.addAll(Arrays.asList(actions));
        return this;
    }

    public TransitionData addAction(final Collection<OutputAction> actions) {
        actionList.addAll(actions);
        return this;
    }

    public TransitionData addTag(final Tags... tags) {
        this.tags.addAll(Arrays.asList(tags));
        return this;
    }

    public <T> TransitionData push(final AttributeKey key, T value) {
        attributeMap.get(key).set(value);
        return this;
    }

    public float getFrictionAtBB(double remove) {
        final int x = MathHelper.floor((boundingBox.getMinX() + boundingBox.getMaxX()) / 2.0D);
        final int y = MathHelper.floor((MathHelper.floor(boundingBox.getMinY()) - remove));
        final int z = MathHelper.floor((boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2.0D);

        final Block block = data.getEntity().getWorld().getBlockAt(x, y, z);

        return block == null || block.getMaterial() == null
                ? 0.6F
                : BlockUtil.getSlipperiness(block.getMaterial());
    }

    public <T> T poll(AttributeKey key) {
        return attributeMap.poll(key);
    }

    public boolean isGround() {
        return poll(EntityAttributes.GROUND);
    }

    public boolean isSprinting() {
        return poll(EntityAttributes.SPRINT);
    }

    public boolean isSneaking() {
        return poll(EntityAttributes.SNEAK);
    }

    public boolean isWeb() {
        return poll(EntityAttributes.WEB);
    }

    public boolean isInWater() {
        return attributeMap.poll(EntityAttributes.WATER);
    }

    public boolean isInLava() {
        return attributeMap.poll(EntityAttributes.LAVA);
    }
    
    public boolean isNoClip(Emulator data) {
        // String comparison for safe 1.7 porting **in case** you decide to switch the version
        return data.getData().getPlayer().getGameMode().name().equalsIgnoreCase("SPECTATOR");
    }

    public float getStepHeight() {
        // Hardcoded to save performance, reference in here in case you want to add it
        // To attributes
        return 0.6F;
    }

    public float getYaw() {
        return attributeMap.poll(EntityAttributes.YAW);
    }

    public Point getResult() {
        return new Point(x, y, z);
    }

    public TransitionData setSlipperiness(float slipperiness) {
        this.slipperiness = slipperiness;
        return this;
    }

    public TransitionData setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public TransitionData setX(double x) {
        this.x = x;
        return this;
    }

    public TransitionData setY(double y) {
        this.y = y;
        return this;
    }

    public TransitionData setZ(double z) {
        this.z = z;
        return this;
    }

    public TransitionData setMoveForward(float moveForward) {
        this.moveForward = moveForward;
        return this;
    }

    public TransitionData setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
        return this;
    }

    public TransitionData setTargetX(double targetX) {
        this.targetX = targetX;
        return this;
    }

    public TransitionData setTargetY(double targetY) {
        this.targetY = targetY;
        return this;
    }

    public TransitionData setTargetZ(double targetZ) {
        this.targetZ = targetZ;
        return this;
    }

    public TransitionData setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    public TransitionData setMotionX(double motionX) {
        this.motionX = motionX;
        return this;
    }

    public TransitionData setMotionY(double motionY) {
        this.motionY = motionY;
        return this;
    }

    public TransitionData setMotionZ(double motionZ) {
        this.motionZ = motionZ;
        return this;
    }

    public TransitionData setMaxMotionX(double motionX) {
        this.maxMotionX = motionX;
        return this;
    }

    public TransitionData setMaxMotionY(double motionY) {
        this.maxMotionY = motionY;
        return this;
    }

    public TransitionData setMaxMotionZ(double motionZ) {
        this.maxMotionZ = motionZ;
        return this;
    }

    public TransitionData setAttributeMap(AttributeMap attributeMap) {
        this.attributeMap = attributeMap;
        return this;
    }

    public TransitionData setCollidedHorizontally(boolean collidedHorizontally) {
        this.collidedHorizontally = collidedHorizontally;
        return this;
    }

    public TransitionData setLastCollidedHorizontally(boolean lastCollidedHorizontally) {
        this.lastCollidedHorizontally = lastCollidedHorizontally;
        return this;
    }

    public TransitionData setCollidedVertically(boolean collidedVertically) {
        this.collidedVertically = collidedVertically;
        return this;
    }

    public TransitionData setCollidedGround(boolean collidedGround) {
        this.collidedGround = collidedGround;
        return this;
    }

    public TransitionData setCollided(boolean collided) {
        this.collided = collided;
        return this;
    }

    public TransitionData setSprintingAttribute(boolean sprintingAttribute) {
        this.sprintingAttribute = sprintingAttribute;
        return this;
    }
}
