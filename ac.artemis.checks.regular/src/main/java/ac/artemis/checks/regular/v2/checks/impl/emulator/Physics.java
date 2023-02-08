package ac.artemis.checks.regular.v2.checks.impl.emulator;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.annotations.NMS;
import ac.artemis.core.v4.check.annotations.Setting;
import ac.artemis.core.v4.check.enums.CheckSettings;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.settings.CheckSetting;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v5.emulator.Emulator;
import lombok.Getter;

import java.util.Arrays;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

@Check(type = Type.PREDICTION, var = "Protocol")
@Getter
@NMS
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
public class Physics extends ArtemisCheck implements PredictionHandler {

    // YOU HAD THE MAGIC VALUE WRONG YOU *beeps*
    private static final float FRICTION = 0.91f, BASE = 1.f, MAGICF = 0.16277136F;

    private final Emulator entity = data.entity;

    private float buffer, bufferGround, bufferDeltaY, bufferDeltaXZ, bufferStrafe;
    private int skip;

    @Setting(type = CheckSettings.MAX_DELTA, defaultValue = "0.08")
    private final CheckSetting deltaXYZ = info.getSetting(CheckSettings.MAX_DELTA);

    public Physics(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final PredictionPosition prediction) {
        // Compile this for logs
        final String motion = String.format(
                " motion: x=%.4f y=%.4f z=%.4f yaw=%.4f jumping=%s sprinting=%s sneaking=%s using=%s ladder=%s collideH=%s time=%.4f",
                data.entity.getMotionX(),
                data.entity.getMotionY(),
                data.entity.getMotionZ(),
                data.entity.getRotationYaw(),
                data.entity.isJumping(),
                data.entity.isSprinting(),
                data.entity.isSneaking(),
                data.entity.isUsingItem(),
                data.entity.isOnLadder(),
                data.entity.isCollidedHorizontally(),
                data.prediction.getPredictionTime()
        );

        this.debug("[Artemis | Motion] " + motion);

        final boolean nu11 = this.isNull(CheckType.POSITION, CheckType.MOVEMENT, CheckType.ROTATION);

        if (nu11) {
            debug("Data is null :(");
            return;
        }

        final boolean exempt = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.MOVEMENT,
                ExemptType.COLLIDE_ENTITY,
                ExemptType.LIQUID,
                ExemptType.FLIGHT,
                ExemptType.LIQUID_WALK,
                ExemptType.SLIME,
                ExemptType.RESPAWN,
                ExemptType.ITEM,
                ExemptType.WEB,
                ExemptType.LADDER
        );

        // Set the distances.
        final double receivedDistance = prediction.was().distanceXZ(prediction.got());
        final double distance = prediction.was().distanceXZ(prediction.expected());

        final double distanceXYZ = prediction.differenceSquared();
        final double distanceXZ = prediction.got().distanceXZ(prediction.expected());
        final double distanceY = prediction.got().distanceY(prediction.expected());

        /*debug("strafe=" + entity.getMoveStrafing() + " forward=" + entity.getMoveForward());
        debug("distanceXYZ=" + distanceXYZ + " xzDelta=" + distanceXZ + " yDelta=" + distanceY);
        debug("fallDistance=" + entity.getFallDistance() + " moveSpeed=" + entity.getAIMoveSpeed());
        *//*debug("attributes=" + Arrays.toString(entity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed)
                .func_111122_c().stream().map(e -> e.getName() + ":" + e.getAmount()).toArray()));*//*
        debug("water=" + entity.isInWater() + " lava=" + entity.isInLava() + " dist=" + distanceXYZ);
        debug("[!] expected=" + distance + " received=" + receivedDistance + " ground=" + data.entity.isOnGround());*/

        debug("expected=" + distance + " received=" + receivedDistance);
        debug("eX=" + prediction.expected().getX() + " eY=" + prediction.expected().getY() + " eZ=" + prediction.expected().getZ());

        if (exempt) {
            debug("Is exempted: " + Arrays.toString(exemptTypes()));
            return;
        }

        if (distance > 16) {
            return;
        }

        if (!prediction.isPredictSmaller()) {
            return;
        }


        /*
         * <=> Motion XYZ <=>
         * -------------------------------------------
         * Theory behind this is that the movement predicted of a player, combined with the
         * calculation of the motion, should not be superior to about 0.08 (estimated value,
         * Could be lower. The probability of it falsing is of about 10%, which is directly
         * Compensated by a verbose system based on 4 required consecutive infractions. The
         * Probability of such happening is of 0.0001. 5 infractions is 0.00001 etc...
         * It's relatively safe to say this is pretty safe and hopefully, with tweaks in the
         * Future, it's stability will only improve.
         * --------------------------------------------
         */
        xyz: {
            // Flag if the user is beyond the verbose
            final boolean onGroundFlag = distanceXYZ > 1E-3D
                    && data.movement.isOnGroundCollision()
                    && distanceY == 0.D;
            final boolean flag = distanceXYZ > 5E-3D
                    && distanceY < 1E-4D
                    && distanceY > -1E-4D;

            if (flag || onGroundFlag) {

                // Lock the max buffer at 20. Todo make this configurable
                this.buffer = buffer > 40 ? 40 : buffer + 1;

                if (this.skip-- > 0) return;

                // This rarely false's due to how high the threshold is
                if (buffer++ > 25){
                    log("Movement", " expected=" + distance + " received=" + receivedDistance + " delta=" + distanceXYZ);
                }
                break xyz;
            }

            // This is a nice way than math.max because cba to call a method. This is better for obfuscation
            // too. This will de-increment by 0.5 if the player hasn't caused an infraction
            this.buffer = buffer > 0 ? buffer - 2 : 0;
        }


        y: {
            final boolean flag = distanceY > 1;

            if (flag) {
                if (bufferDeltaY++ > 2){
                    log("Fly", "yDelta=" + distanceY + motion);
                }
                break y;
            }
            this.bufferDeltaY = bufferDeltaY > 0.0F ? bufferDeltaY - 0.5F : 0.0F;
        }

        ground: {
            final boolean flag = data.user.isOnFakeGround() != entity.isOnGround();

            if (!flag){
                this.bufferGround = 0.0F;
                break ground;
            }

            if (bufferGround++ < 15) {
                break ground;
            }

            //this.log("Ground", "server=" + entity.isOnGround() + " client=" + data.user.isOnFakeGround());
        }


    }
}
