package ac.artemis.checks.regular.v2.checks.impl.omnisprint;

import ac.artemis.core.v4.check.TeleportHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.position.ComplexPositionCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v4.utils.position.ModifiableFlyingLocation;
import ac.artemis.core.v4.utils.position.PlayerPosition;
import ac.artemis.core.v4.utils.position.PlayerRotation;

/**
 * @author Ghast
 * @since 02-Apr-20
 */
@Setback
@Check(type = Type.OMNISPRINT, var = "Simple")
public class OmniSprintSimple extends ComplexPositionCheck implements TeleportHandler {
    public OmniSprintSimple(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private int buff;

    @Override
    public void handlePosition(PlayerPosition from, PlayerPosition to) {
        final boolean exempt = this.isExempt(
                ExemptType.JOIN,
                ExemptType.SLIME,
                ExemptType.LIQUID_WALK,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.GAMEMODE,
                ExemptType.TELEPORT
        );

        if (isNull(CheckType.ROTATION) || data.user.isOnCooldown() || exempt)
            return;

        final PlayerRotation toRot = data.movement.getRotation();
		
		if (from.distanceXZ(to) < 0.01) return;
		
		// Position Angle
		// ------------------------------------------------------
		// Theory behind this is as the yaw is oriented on a Z+ coordinate, we get a degree angle
		// compared to the Z+/Yaw axis. This gives us an accurate point to compare to yaw. 
		// 
		// The value returned from the following should be 135. 
		// 0.0------------- +x
		// |  -1,1
		// |    \
		// |     \
		// |     -2,2
		// -z
		// More can be read about this here: 
		// https://stackoverflow.com/questions/2676719/calculating-the-angle-between-the-line-defined-by-two-points
		// ------------------------------------------------------
        final double angle = MathUtil.angleOf(from.getZ(), from.getX(), to.getZ(), to.getX());
		
		// Yaw
		// ------------------------------------------------------
		// Good old normalization. The getYaw() function returns yaw % 360; This converts the yaw from 
		// a I believe Euler angle to a Degrees angle. This can be done better. 
		// ------------------------------------------------------
        final double yaw = toRot.getYaw() >= 0 ? toRot.getYaw() : toRot.getYaw() + 360;
		
		// Delta
		// ------------------------------------------------------
		// We calculate the delta with a round of 5 (result will be a multiple of 5) due to precision
		// Issues and mostly this working best. There are other alternatives. This could greatly
		// Be improved upon. 
		// The delta raw returns a 0to360 angle, the regular delta returns a 0to180 angle. No clue why
		// ------------------------------------------------------
        final double angleDeltaRaw = Math.round(MathUtil.getDistanceBetweenAngles360Raw(yaw, angle) / 5) * 5;
        final double angleDelta = Math.round(MathUtil.getDistanceBetweenAngles360(yaw, angle) / 5) * 5;


        float moveStrafing = 0.0F;
        float moveForward = 0.0F;

		// Move Forward
		// ------------------------------------------------------
		// By the following theory, if the angle is in a range of 0 and 90 (distance from yaw, remember
		// the angleDelta returns a distance from both sides so both sides are calculated with this). If
		// The distance is minimal, it's safe to say it's going forward. Otherwise, it's going backwards. 
		// If none are found, it's going on the sides, which we then ignore and leave moveForward to be 
		// it's default value.
		// ------------------------------------------------------
        if (angleDelta >= 0 && angleDelta < 90) moveForward++;
        else if (angleDelta > 90 && angleDelta <= 180) moveForward--;
		
		// Move Strafing
		// ------------------------------------------------------
		// This uses the raw angles and checks if the player is strafing to a side or not. Using this in
		// a regular prediction check will cause issues. However, thanks to the buffer and the fact this 
		// is simply an omnisprint check, we don't have to worry about false positives. The moveForward 
		// is the only to be concerned about if ever you seek to fix a false positive.
		// ------------------------------------------------------
        if (angleDeltaRaw > 0 && angleDeltaRaw < 180) moveStrafing--;
        else if (angleDeltaRaw > 180 && angleDeltaRaw < 360) moveStrafing++;

        // If the player is onGround + is sprinting yet is not allowed to sprint, flag him.
		final boolean env = this.getPacket().isOnGround() && data.entity.isSprinting() && data.user.isSprinting();
		final boolean flag = (moveForward < 0) || (moveForward == 0 && moveStrafing != 0);

        if (env && flag) {
            if (buff++ > 2) {
                log("offset=" + angleDelta + " moveForward=" + moveForward + " moveStrafing=" + moveStrafing);
            }
        } else {
            buff = 0;
        }
    }

    @Override
    public void handle(ModifiableFlyingLocation confirmedLocation) {
        this.buff = 0;
    }
}
