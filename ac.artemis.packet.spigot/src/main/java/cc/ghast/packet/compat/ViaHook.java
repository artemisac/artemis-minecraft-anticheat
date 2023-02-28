package cc.ghast.packet.compat;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public interface ViaHook {
    int getVersion(UUID uuid);

    ByteBuf transformPacket(UUID uuid, ByteBuf buf, int id);

    ByteBuf transformPacketSend(UUID uuid, ByteBuf buf, int id);
}
