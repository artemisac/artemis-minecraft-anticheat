package cc.ghast.packet.wrapper.packet.play.client;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.client.v1_8.PacketPlayClientUseEntity;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import cc.ghast.packet.wrapper.bukkit.Vector3D;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Optional;
import java.util.UUID;

@Getter
@PacketLink(PacketPlayClientUseEntity.class)
public class GPacketPlayClientUseEntity extends GPacket implements PacketPlayClientUseEntity, ReadableBuffer {
    public GPacketPlayClientUseEntity(UUID player, ProtocolVersion version) {
        super("PacketPlayInUseEntity", player, version);
    }

    private int entityId;
    private PlayerEnums.UseType type;
    private Optional<Vector3D> body;
    private PlayerEnums.Hand hand;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.entityId = byteBuf.readVarInt();
        this.type = PlayerEnums.UseType.values()[byteBuf.readVarInt()];
        if (type.equals(PlayerEnums.UseType.INTERACT_AT)) {
            float targetX = byteBuf.readFloat();
            float targetY = byteBuf.readFloat();
            float targetZ = byteBuf.readFloat();
            this.body = Optional.of(new Vector3D(targetX, targetY, targetZ));
        } else {
            this.body = Optional.empty();
        }

        if (this.version.isOrBelow(ProtocolVersion.V1_9)) {
            this.hand = PlayerEnums.Hand.MAIN_HAND;
        }

        else {
            if (type.equals(PlayerEnums.UseType.INTERACT) || type.equals(PlayerEnums.UseType.INTERACT_AT))
                this.hand = PlayerEnums.Hand.values()[byteBuf.readVarInt()];
            else
                this.hand = PlayerEnums.Hand.MAIN_HAND;
        }
    }

    public Entity getEntity() {
        return Bukkit.getPlayer(uuid).getWorld().getLivingEntities().parallelStream().filter(e-> e.getEntityId() == entityId).findFirst().orElse(null);
    }


}
