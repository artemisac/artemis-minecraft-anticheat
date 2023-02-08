package ac.artemis.checks.enterprise.heuristics.modal;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.ReachHandler;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v4.utils.reach.ReachModal;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.Data;

public abstract class AbstractAngleHeuristicCheck extends ArtemisCheck implements ReachHandler {

    public AbstractAngleHeuristicCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public final void handle(ReachModal current, ReachEntity opponent) {
        /*
         * Here in this scenario we seriously do NOT want to be flagging game-mode creative
         * as it simply gains more reach. On a regular MC player, such has 3 block of entity
         * reach. However, when you switch to game-mode creative, your reach is extended to
         * 4.5. And as I simply cannot be asked to add the math condition for something useless,
         * I exempt ti.
         */
        if (this.isExempt(ExemptType.GAMEMODE)) {
            return;
        }

        /*
         * Here we obtain the information required by the Minecraft protocol to interact with
         * the raytracing. Our first component is the eye position of the player.
         */
        final Point eyePos = current.getEyePos();

        if (current.getHitVec() == null || opponent == null || current.getType() != ReachModal.Type.HIT)
            return;

        final BoundingBox bb = current.getBoundingBox().cloneBB();
        final Point hit = current.getHitVec();

        final double deltaBBX = eyePos.getX() - bb.middleX();
        final double deltaBBZ = eyePos.getZ() - bb.middleZ();

        final double deltaHITX = eyePos.getX() - hit.getX();
        final double deltaHITZ = eyePos.getZ() - hit.getZ();

        final double deltaX = data.prediction.getDeltaX();
        final double deltaZ = data.prediction.getDeltaZ();
        final double deltaXZ = deltaX + deltaZ;

        final Point move = new Point(deltaBBX, 0, deltaBBZ);
        final Point look = new Point(deltaHITX, 0, deltaHITZ);

        /*
         * Find the angle (radian) angle of the move. We're not done! Since reach has to be accounted for (and since
         * hacked clients literally never take it into account), we have to properly scale it. We know the angle is
         * a factor between 0 and 2PI (it'll never be more than 3 as it's simply impossible to have opposing vectors
         * for hit).
         */
        final double angle = look.angle(move);

        /*
         * Here we scale the angle to find the distance between the two points based on the reach. The mathematical
         * formula for this is a*R=a'.
         */
        final double scaledReach = current.getDistance() * angle;

        final AngleModal angleModal = new AngleModal();
        angleModal.setAngle(angle);
        angleModal.setScaledAngle(scaledReach);
        angleModal.setDeltaXZ(deltaXZ);

        this.handle(angleModal);
    }

    public abstract void handle(final AngleModal modal);

    @Data
    protected static class AngleModal {
        private double angle;
        private double scaledAngle;
        private double deltaXZ;
    }
}
