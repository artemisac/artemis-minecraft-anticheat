package cc.ghast.packet.compat;

import cc.ghast.packet.protocol.EnumProtocolCurrent;
import com.viaversion.viaversion.ViaVersionPlugin;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.State;
import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;

import java.util.UUID;

/**
 * @author Ghast
 * @since 15/09/2020
 * ArtemisPacket © 2020
 */
public class ViaVersionHook implements ViaHook {

    private final ViaVersionPlugin api = ViaVersionPlugin.getInstance();

    public int getVersion(UUID uuid) {
        return api.getApi().isInjected(uuid) ? api.getApi().getPlayerVersion(uuid) : -1;
    }

    @SneakyThrows
    public ByteBuf transformPacket(UUID uuid, ByteBuf buf, int id) {
        if (uuid == null
                || Via.getManager().getConnectionManager().getConnectedClients() == null
                || !Via.getManager().getConnectionManager().getConnectedClients().containsKey(uuid)) {
            return buf;
        }

        final UserConnection connection = Via.getManager().getConnectionManager().getConnectedClient(uuid);

        if (connection == null) {
            return buf;
        }

        buf.retain();
        buf.resetReaderIndex();

        try {
            connection.transformServerbound(buf, e -> new IllegalStateException("ViaVersion failed to convert packet (type: " + id + ")", e.getCause()));
        } catch (Throwable e) {
            // do literally nothing
            buf = null;
        }
        return buf;
    }

    @SneakyThrows
    public ByteBuf transformPacketSend(UUID uuid, ByteBuf buf, int id) {
        if (uuid == null
                || Via.getManager().getConnectionManager().getConnectedClients() == null
                || !Via.getManager().getConnectionManager().getConnectedClients().containsKey(uuid)) {
            return buf;
        }

        final UserConnection connection = Via.getManager().getConnectionManager().getConnectedClient(uuid);

        if (connection == null) {
            return buf;
        }

        buf.resetReaderIndex();

        try {
            connection.transformClientbound(buf, e -> new IllegalStateException("ViaVersion failed to convert packet (type: " + id + ")", e.getCause()));
        } catch (IllegalStateException e) {
            // do literally nothing
            buf = null;
        }
        return buf;
    }

    private State getState(EnumProtocolCurrent protocol) {
        switch (protocol) {
            case STATUS: return State.STATUS;
            case LOGIN: return State.LOGIN;
            case HANDSHAKE: return State.HANDSHAKE;
        }
        return State.PLAY;
    }
}
