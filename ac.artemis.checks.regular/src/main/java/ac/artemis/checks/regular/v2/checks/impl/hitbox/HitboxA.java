package ac.artemis.checks.regular.v2.checks.impl.hitbox;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.packet.minecraft.GameMode;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.entity.impl.Player;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

/**
 * @author Ghast
 * @since 04-Apr-20
 */

@Check(type = Type.HITBOX, var = "A", threshold = 3)
public class HitboxA  extends ArtemisCheck implements PacketHandler {
    public HitboxA(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            final Entity entity = NMSManager
                    .getInms()
                    .getEntity(data.getPlayer().getWorld(), wrapper.getEntityId());

            final boolean invalid = !wrapper.getType().equals(PlayerEnums.UseType.INTERACT_AT)
                    || !(entity instanceof Player)
                    || ((Player) entity).getGameMode().equals(GameMode.CREATIVE);

            if (invalid) {
                return;
            }

            final Vector3D vec = wrapper.getBody().get();
            final double x = vec.getX();
            final double y = vec.getY();
            final double z = vec.getZ();

            if (Math.max(Math.abs(x), Math.abs(z)) > 0.401 || Math.abs(y) > 1.91) {
                this.log("x= " + x + " y=" + y + " z=" + z);
            }

            this.debug("x= " + x + " y=" + y + " z=" + z);
        }
    }
}
