package ac.artemis.checks.enterprise.velocity;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.VelocityHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v4.utils.position.Velocity;

import java.util.Arrays;


/**
 * @author Ghast
 * @since 13/09/2020
 * Artemis © 2020
 */

@Check(type = Type.VELOCITY, var = "XYZ", threshold = 20)
@ClientVersion
public class VelocityXYZ extends ArtemisCheck implements PredictionHandler, VelocityHandler {
    public VelocityXYZ(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean process;
    private double lastDistance;
    private int vb;
    private int ticks;

    @Override
    public void handle(Velocity velocity) {
        this.process = true;
    }

    @Override
    public void handle(final PredictionPosition prediction) {
        if (!process)
            return;

        if (data.prediction.isGround() || ticks > 4) {
            this.ticks = 0;
            this.process = false;
            return;
        }

        final double distance = prediction.got().distanceSquare(prediction.expected());

        final boolean flag = distance > (ticks > 0 ? 5E-4 : 5E-6);

        final boolean unsafe = this.isExempt(
                ExemptType.FLIGHT,
                ExemptType.VEHICLE,
                ExemptType.VOID,
                ExemptType.JOIN,
                ExemptType.MOVEMENT,
                ExemptType.WORLD,
                ExemptType.GAMEMODE,
                ExemptType.LIQUID,
                ExemptType.TELEPORT,
                ExemptType.LIQUID_WALK,
                ExemptType.COLLIDED_HORIZONTALLY,
                ExemptType.WEB,
                ExemptType.ZERO_ZERO_THREE
        );

        flag: {
            // If we deem it unsafe to check due to motion modifiers, don't process the flag
            if (unsafe) {
                this.ticks = 0;
                this.process = false;
                break flag;
            }

            // If we do not flag, decrease the verbose
            if (!flag) {
                this.vb = Math.max(0, vb - 1);
                this.ticks++;
                break flag;
            }

            this.ticks = 0;
            this.process = false;

            // If our pre-increased verbose is not superior to our threshold, don't flag
            if (++vb < 3) break flag;

            // Every condition is met, proceed with flagging
            this.log(new Debug<>("exempt", Arrays.toString(exemptTypes())));
        }

        this.lastDistance = distance;

        if (data.prediction.isConfirmingVelocity()) {
            this.ticks = 0;
            this.process = false;
        }

        this.debug("distance=" + distance + " flag=" + flag + " unsafe=" + unsafe + " process=" + process);
    }
}
