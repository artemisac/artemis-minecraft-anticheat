package ac.artemis.core.v5;

import ac.artemis.anticheat.api.ArtemisAPI;
import ac.artemis.anticheat.api.alert.Severity;
import ac.artemis.anticheat.api.listener.PunishListener;
import ac.artemis.anticheat.api.listener.VerboseListener;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.PacketAPI;
import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.*;

public class ArtemisSpigotAPI implements ArtemisAPI {
    private final Set<VerboseListener> verboseListenerMap = new HashSet<>();
    private final Set<PunishListener> punishListeners = new HashSet<>();

    @Override
    public ProtocolVersion getVersion(final UUID uuid) {
        return PacketAPI.getVersion(uuid);
    }

    @Override
    public Severity getAlertType(final UUID uuid) {
        final PlayerData data = Artemis.v().getApi().getPlayerDataManager().getData(uuid);

        if (data == null) return Severity.NONE;

        switch (data.staff.getStaffAlert()) {
            case EXPERIMENTAL_VERBOSE:
            case VERBOSE_SELF:
            case NONE:
                return Severity.NONE;
            case ALERTS:
                return Severity.VIOLATION;
            default:
                return Severity.VERBOSE;
        }
    }

    @Override
    public void addVerboseListener(final VerboseListener verboseListener) {
        this.verboseListenerMap.add(verboseListener);
        Artemis.v().getApi().getAlertManager().getVerboseListeners().add(verboseListener);
    }

    @Override
    public void removeVerboseListener(final VerboseListener verboseListener) {
        this.verboseListenerMap.remove(verboseListener);
        Artemis.v().getApi().getAlertManager().getVerboseListeners().remove(verboseListener);
    }

    @Override
    public void clearVerboseListeners() {
        for (final VerboseListener value : verboseListenerMap) {
            Artemis.v().getApi().getAlertManager().getVerboseListeners().remove(value);
        }

        this.verboseListenerMap.clear();
    }

    @Override
    public void addBanListener(final PunishListener punishListener) {
        this.punishListeners.add(punishListener);
        Artemis.v().getApi().getAlertManager().getPunishListeners().add(punishListener);
    }

    @Override
    public void removeBanListener(final PunishListener punishListener) {
        Artemis.v().getApi().getAlertManager().getPunishListeners().remove(punishListener);
        this.punishListeners.remove(punishListener);
    }

    @Override
    public void clearBanListeners() {
        for (final PunishListener punishListener : punishListeners) {
            Artemis.v().getApi().getAlertManager().getPunishListeners().remove(punishListener);
        }

        this.punishListeners.clear();
    }
}
