package ac.artemis.checks.regular.v2.checks.impl.velocity;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v4.utils.position.Velocity;

import static ac.artemis.packet.protocol.ProtocolVersion.*;


/**
 * @author Ghast
 * @since 13/09/2020
 * Artemis © 2020
 */

@Check(type = Type.VELOCITY, var = "XZ", threshold = 20)
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
public class VelocityXZ extends ArtemisCheck implements PredictionHandler, VelocityHandler {
    public VelocityXZ(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean process;
    private double lastDistance;
    private int vb;

    @Override
    public void handle(Velocity velocity) {
        this.process = true;
    }

    @Override
    public void handle(final PredictionPosition prediction) {
        if (!process) return;
        final double distance = prediction.got().distanceSquareXZ(prediction.expected());

        final boolean flag = distance > 5E-4 && lastDistance > 5E-4;

        final boolean unsafe = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.WORLD,
                ExemptType.MOVEMENT,
                ExemptType.GAMEMODE,
                ExemptType.LIQUID,
                ExemptType.TELEPORT,
                ExemptType.LIQUID_WALK,
                ExemptType.COLLIDED_HORIZONTALLY
        );

        flag: {
            // If we deem it unsafe to check due to motion modifiers, don't process the flag
            if (unsafe) {
                break flag;
            }

            // If we do not flag, decrease the verbose
            if (!flag) {
                this.vb = Math.max(0, vb - 1);
                break flag;
            }

            // If our pre-increased verbose is not superior to our threshold, don't flag
            if (vb++ < 3) break flag;

            // Every condition is met, proceed with flagging
            this.log("distance=" + distance);
        }

        this.debug("distance=" + distance + " flag=" + flag + " unsafe=" + unsafe + " process=" + process);

        this.lastDistance = distance;
        this.process = false;
    }
}
