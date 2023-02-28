package ac.artemis.packet.spigot.wrappers;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.wrapper.Packet;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

@Getter
@Setter
public abstract class GPacket implements Packet {
    protected UUID uuid;
    protected ProtocolVersion version;
    protected final static ProtocolVersion gameVersion = ServerUtil.getGameVersion();
    protected final Predicate<ProtocolVersion>[] versionPredicate;
    protected final long timestamp;
    protected boolean cancelled;
    protected final String realName;

    public GPacket(String realName, UUID player, ProtocolVersion version, Predicate<ProtocolVersion>... versionPredicate) {
        this.realName = realName;
        this.versionPredicate = versionPredicate;
        this.uuid = player;
        this.version = version;
        this.timestamp = System.currentTimeMillis();
    }

    public GPacket(String realName, UUID player, ProtocolVersion version) {
        this(realName, player, version, new Predicate[0]);
    }


    public GPacket(String realName) {
        this(realName, null, null);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

}

