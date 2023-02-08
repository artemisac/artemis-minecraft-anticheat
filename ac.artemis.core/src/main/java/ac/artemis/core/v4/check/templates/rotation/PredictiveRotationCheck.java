package ac.artemis.core.v4.check.templates.rotation;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.enums.CheckType;
import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.modal.TrueAimRotation;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;

public abstract class PredictiveRotationCheck extends ArtemisCheck implements PacketHandler {
    public PredictiveRotationCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    public GPacket packet;

    @Override
    public void handle(final GPacket packet) {
        if (data == null || isExempt(ExemptType.GAMEMODE, ExemptType.TELEPORT, ExemptType.JOIN, ExemptType.NOT_COMBAT, ExemptType.RESPAWN))
            return;
        if (packet instanceof PacketPlayClientFlying && ((PacketPlayClientFlying) packet).isLook() && !isNull(CheckType.ROTATION)) {
            this.packet = packet;



            handle(new TrueAimRotation(data.sensitivity.getDeltaX(), data.sensitivity.getDeltaY()));
        }
    }

    public abstract void handle(final TrueAimRotation rotation);
}
