package ac.artemis.core.v4.packet;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.core.v5.threading.Threading;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.server.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

/**
 * @author Ghast
 * @since 23/10/2020
 * Artemis © 2020
 */
public class PacketExecutor {
    /**
     * Certain packets must absolutely not be processed asynchronously. However,
     * since we have our lil prediction engine. It's heavy...
     *
     * So! Here's a dumb solution. We process it on the netty thread. Seems
     * annoying but truly it ain't. It's just slightly more intensive. Nothing
     * that can impact connection speed
     */
    private static final Set<Class<?>> fastProcessable = new HashSet<>(Arrays.asList(
            GPacketPlayServerKeepAlive.class,
            GPacketPlayServerKickDisconnect.class,

            GPacketPlayServerEntity.class,
            GPacketPlayServerEntity.GPacketPlayServerEntityLook.class,
            GPacketPlayServerEntity.GPacketPlayServerRelEntityMove.class,
            GPacketPlayServerEntity.GPacketPlayServerRelEntityMoveLook.class,

            GPacketPlayServerEntityVelocity.class,
            GPacketPlayServerEntityMetadata.class,
            GPacketPlayServerEntityStatus.class,
            GPacketPlayServerEntityEffect.class,
            GPacketPlayServerEntityEffectRemove.class,
            GPacketPlayServerEntityTeleport.class,
            GPacketPlayServerEntityDestroy.class,

            GPacketPlayServerBlockChange.class,
            GPacketPlayServerBlockChangeMulti.class,

            GPacketPlayServerAbilities.class,
            GPacketPlayServerRespawn.class,
            GPacketPlayServerWindowOpen.class,
            GPacketPlayServerUpdateAttributes.class,
            GPacketPlayServerExplosion.class,
            GPacketPlayServerWindowClose.class,
            GPacketPlayServerEntityAttach.class,
            GPacketPlayServerBed.class
    ));

    private static int count = 0;
    private final ExecutorService service;
    private final List<PlayerData> players;

    public PacketExecutor() {
        count++;
        this.service = Threading.getOrStartService("artemis-data-thread-" + count);
        this.players = new CopyOnWriteArrayList<>();
    }

    public void addPlayer(PlayerData data) {
        this.players.add(data);
    }

    public void removePlayer(PlayerData data) {
        this.players.remove(data);
    }

    public int size() {
        return players.size();
    }

    public void executePacket(PlayerData data, GPacket packet) {
        if (data.getSafetyFeature().check(packet)) {
            Chat.sendConsoleMessage(Lang.MSG_CONSOLE_KICK_CRASH.replace("%player%", data.getPlayer().getName()));
            data.monke();
            return;
        }

        if (fastProcessable.contains(packet.getClass()))
            data.handleFastProcess(packet);

        this.service.execute(() -> {
            if (data.getTeleportHandler().isTeleport(data, packet)) return;

            data.handlePreHandlers(packet);
            data.handlePacket(packet);
            data.handlePostHandlers(packet);
            data.timing.packetTiming.addTime(packet.getTimestamp(), System.currentTimeMillis());
        });
    }

    public void run(Runnable runnable) {
        this.service.execute(runnable);
    }
}
