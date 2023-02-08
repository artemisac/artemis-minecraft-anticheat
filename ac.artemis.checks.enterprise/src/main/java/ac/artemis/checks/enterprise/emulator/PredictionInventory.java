package ac.artemis.checks.enterprise.emulator;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.ClientVersion;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Setback;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientWindowClick;

import static ac.artemis.packet.protocol.ProtocolVersion.*;

/**
 * @author Ghast
 * @since 18/10/2020
 * Artemis © 2020
 *
 * This check is an odd one as it's quite sensitive yet quite fascinating in itself. Basing on
 * whether the user is inside an inventory (discounting the fact of the twitch gui which we need
 * to add a proper setback for), we can flag for any inventory clicks whilst moving key-binds.
 * This method is significantly more stable than simply checking if the inventory is open and
 * most often will flag any inv-move/auto-pot.
 */
@Check(type = Type.PREDICTION, var = "Inventory")
@ClientVersion(version = {V1_7, V1_7_10, V1_8, V1_8_5, V1_8_9, V1_9, V1_9_1, V1_9_2, V1_9_4, V1_10, V1_10_2, V1_11, V1_12, V1_12_1, V1_12_2})
@Setback
public class PredictionInventory extends ArtemisCheck implements PacketHandler {
    public PredictionInventory(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    private float buffer;

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientWindowClick) {
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
                    ExemptType.PLACING,
                    ExemptType.TELEPORT,
                    ExemptType.LIQUID_WALK
            );

            final boolean flag = this.getData().user.isInventoryOpen()
                    && (data.entity.getMoveForward() > 0.F || data.entity.getMoveStrafing() > 0.F);


            flag: {
                if (exempt)
                    break flag;

                if (!flag) {
                    this.buffer = 0.F;
                    break flag;
                }

                if (buffer++ < 3F) {
                    break flag;
                }

                this.log(
                        new Debug<>("inv", data.user.isInventoryOpen()),
                        new Debug<>("forward", data.entity.getMoveForward()),
                        new Debug<>("strafe", data.entity.getMoveStrafing()),
                        new Debug<>("buffer", buffer)
                );
            }

            this.debug("inv=%s moveForward=%.4f moveStrafe=%.4f buffer=%.4f",
                    data.user.isInventoryOpen(),
                    data.entity.getMoveForward(),
                    data.entity.getMoveStrafing(),
                    buffer
            );
        }
    }
}
