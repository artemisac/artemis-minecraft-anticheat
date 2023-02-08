package ac.artemis.checks.regular.v2.checks.impl.velocity;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;

import static ac.artemis.packet.protocol.ProtocolVersion.*;


/**
 * @author Ghast
 * @since 13/09/2020
 * Artemis © 2020
 */
@Check(type = Type.VELOCITY, var = "Y", threshold = 20)
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
public class VelocityY extends ArtemisCheck implements PredictionHandler, PacketHandler {
    public VelocityY(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private boolean process;
    private int vb;

    @Override
    public void handle(GPacket packet) {
        final boolean process = packet instanceof GPacketPlayClientTransaction
                && data.movement.isProcessedVelocity();

        if (!process)
            return;

        this.process = true;
    }

    @Override
    public void handle(final PredictionPosition prediction) {
        final double distance = prediction.got().distanceY(prediction.expected());

        final boolean flag = distance > 1E-4;
        final boolean unsafe = data.combat.isProcessAttack()
                || data.entity.isJumping()
                || this.isExempt(
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
                        ExemptType.TELEPORT,
                        ExemptType.LIQUID_WALK,
                        ExemptType.COLLIDED_HORIZONTALLY,
                        ExemptType.WEB
                );

        flag: {
            // If we deem it unsafe to check due to motion modifiers, don't process the flag
            if (unsafe || !process) {
                break flag;
            }

            // If we do not flag, decrease the verbose
            if (!flag) {
                this.vb = Math.max(0, vb - 1);
                break flag;
            }

            // If our pre-increased verbose is not superior to our threshold, don't flag
            if (vb++ < 2) break flag;

            // Every condition is met, proceed with flagging
            this.log(
                    new Debug<>("distance", distance)
            );
        }

        this.process = false;
        this.debug("distance=" + distance + " flag=" + flag + " unsafe=" + unsafe + " process=" + process);
    }
}
