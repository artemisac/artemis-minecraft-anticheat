package ac.artemis.core.v4.data.holders;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.Velocity;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Getter;
import lombok.Setter;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Ghast
 * @since 23/02/2021
 * Artemis © 2021
 */

@Getter
@Setter
public class PredictionHolder extends AbstractHolder {
    public PredictionHolder(PlayerData data) {
        super(data);
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    private double lastX;
    private double lastY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;

    private double deltaX;
    private double deltaY;
    private double deltaZ;

    private double lastDeltaX;
    private double lastDeltaY;
    private double lastDeltaZ;

    private boolean ground;
    private boolean lastGround;

    private boolean pos;
    private boolean lastPos;
    private boolean lastLastPos;

    private Entity vehicle;

    private int slot = data.getPlayer().getInventory().getHeldItemSlot();

    private boolean confirmingVelocity;
    private int velocityTicks;

    private long lastFlying;
    private double predictionTime;

    private int iteration;
    private double distanceX;
    private double distanceY;
    private double distanceZ;
    private boolean sprinting;
    private float friction;


    private Deque<Velocity> queuedVelocity = new LinkedList<>();

    private final Deque<Entity> queuedAttacks = new LinkedList<>();
    private final Deque<Velocity> queuedVelocities = new LinkedList<>();

    public Point getLocation() {
        return new Point(x, y, z);
    }

    public Point getLastLocation() {
        return new Point(lastX, lastY, lastZ);
    }

    public BoundingBox getLazyBox() {
        final float width = 0.6F / 2.0F;
        final float height = 1.8F;

        return new BoundingBox(x - (double) width, y, z - (double) width,
                x + (double) width, y + (double) height, z + (double) width);
    }

    public BoundingBox getLastLazyBox() {
        final float width = 0.6F / 2.0F;
        final float height = 1.8F;

        return new BoundingBox(
                lastX - (double) width,
                lastY,
                lastZ - (double) width,
                lastX + (double) width,
                lastY + (double) height,
                lastZ + (double) width
        );
    }

    public PlayerPosition getPosition() {
        return new PlayerPosition(data.getPlayer(), x, y, z, System.currentTimeMillis());
    }

    public PlayerPosition getLastPosition() {
        return new PlayerPosition(data.getPlayer(), lastX, lastY, lastZ, System.currentTimeMillis());
    }

    public PlayerMovement getMovement() {
        return new PlayerMovement(
                data.getPlayer(),
                x,
                y,
                z,
                yaw,
                pitch,
                System.currentTimeMillis()
        );
    }

    public PlayerMovement getLastMovement() {
        return new PlayerMovement(data.getPlayer(), lastX, lastY, lastZ, lastYaw, lastPitch, System.currentTimeMillis());
    }

    public float getEyeHeight() {
        float eyeHeight = 1.62F;

        if (data.getPlayer().isSleeping()) eyeHeight = 0.2F;
        if (data.getEntity().isSneaking()) eyeHeight -= 0.08F;

        return eyeHeight;
    }

    public boolean isInVehicle() {
        return vehicle != null;
    }
}
