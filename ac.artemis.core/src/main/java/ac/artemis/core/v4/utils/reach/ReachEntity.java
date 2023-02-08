package ac.artemis.core.v4.utils.reach;

import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Ghast
 * @since 14/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */

/**
 * @author Ghast
 * @since 14/07/2020
 * Ghast Holdings LLC / Artemis © 2020
 */

@Data
public class ReachEntity {

    public int entity;

    public int serverPosX;
    public int serverPosY;
    public int serverPosZ;

    public boolean confirming;
    public Point nextReach = null;
    public double multiplier;

    public List<ReachPosition> reachPositions = new CopyOnWriteArrayList<>();

    public static final BoundingBox ZERO_AABB = new BoundingBox(0.0,0.0,0.0,0.0,0.0,0.0);

    public ReachEntity(int entity, int posX, int posY, int posZ, double multiplier) {
        this.entity = entity;

        this.serverPosX = posX;
        this.serverPosY = posY;
        this.serverPosZ = posZ;
        this.multiplier = multiplier;

        final ReachPosition reachPosition = new ReachPosition();
        reachPosition.setPosition(
                serverPosX / multiplier,
                serverPosY / multiplier,
                serverPosZ / multiplier
        );

        this.reachPositions.add(reachPosition);
    }

    public void tick() {
        for (ReachPosition reachPosition : reachPositions) {
            reachPosition.onLivingUpdate();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReachPosition {
        private int otherPlayerMPPosRotationIncrements;
        private double otherPlayerMPX;
        private double otherPlayerMPY;
        private double otherPlayerMPZ;

        public double posX;
        public double posY;
        public double posZ;

        public double lastPosX;
        public double lastPosY;
        public double lastPosZ;

        public BoundingBox entityBoundingBox;
        public BoundingBox lastEntityBoundingBox;

        public boolean skip;

        public void onLivingUpdate() {

            if (this.otherPlayerMPPosRotationIncrements > 0) {
                double d0 = this.posX + (this.otherPlayerMPX - this.posX) / (double) this.otherPlayerMPPosRotationIncrements;
                double d1 = this.posY + (this.otherPlayerMPY - this.posY) / (double) this.otherPlayerMPPosRotationIncrements;
                double d2 = this.posZ + (this.otherPlayerMPZ - this.posZ) / (double) this.otherPlayerMPPosRotationIncrements;

                --this.otherPlayerMPPosRotationIncrements;
                this.setPosition(d0, d1, d2);
            } else {
                this.setPosition(posX, posY, posZ);
            }
        }

        public void setPositionAndRotation2(double x, double y, double z, int posRotationIncrements) {
            this.otherPlayerMPX = x;
            this.otherPlayerMPY = y;
            this.otherPlayerMPZ = z;
            this.otherPlayerMPPosRotationIncrements = posRotationIncrements;
        }

        /**
         * Sets the x,y,z of the entity from the given parameters. Also seems to set up
         * a bounding box.
         */
        public void setPosition(double x, double y, double z) {
            lastPosX = posX;
            lastPosY = posY;
            lastPosZ = posZ;

            this.posX = x;
            this.posY = y;
            this.posZ = z;

            float f = 0.6F / 2.0F;
            float f1 = 1.8F;

            this.setLastEntityBoundingBox(this.entityBoundingBox);
            this.setEntityBoundingBox(new BoundingBox(x - (double) f, y, z - (double) f, x + (double) f, y + (double) f1, z + (double) f));
        }

        public double getMoveDist() {
            return entityBoundingBox.distance(lastEntityBoundingBox);
        }

        public ReachPosition clone() {
            return new ReachPosition(
                    otherPlayerMPPosRotationIncrements,
                    otherPlayerMPX,
                    otherPlayerMPY,
                    otherPlayerMPZ,

                    posX,
                    posY,
                    posZ,

                    lastPosX,
                    lastPosY,
                    lastPosZ,

                    entityBoundingBox,
                    lastEntityBoundingBox,
                    skip
            );
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ReachPosition that = (ReachPosition) o;
            return that.hashCode() == hashCode();
        }

        @Override
        public int hashCode() {
            final int precision = ConfigManager.getReach().getV1_precision();
            return Objects.hash(
                    otherPlayerMPPosRotationIncrements,
                    MathUtil.roundToPlace(otherPlayerMPX, precision),
                    MathUtil.roundToPlace(otherPlayerMPY, precision),
                    MathUtil.roundToPlace(otherPlayerMPZ, precision),
                    MathUtil.roundToPlace(posX, 2),
                    MathUtil.roundToPlace(posY, 2),
                    MathUtil.roundToPlace(posZ, 2)
            );
        }
    }
}
