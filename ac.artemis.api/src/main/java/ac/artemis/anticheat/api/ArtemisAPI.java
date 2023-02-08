package ac.artemis.anticheat.api;

import ac.artemis.anticheat.api.alert.Severity;
import ac.artemis.anticheat.api.listener.PunishListener;
import ac.artemis.anticheat.api.listener.VerboseListener;
import ac.artemis.packet.protocol.ProtocolVersion;

import java.util.UUID;

public interface ArtemisAPI {
    ProtocolVersion getVersion(final UUID uuid);

    Severity getAlertType(final UUID uuid);

    void addVerboseListener(VerboseListener verboseListener);

    void removeVerboseListener(VerboseListener verboseListener);

    void clearVerboseListeners();

    void addBanListener(PunishListener verboseListener);

    void removeBanListener(PunishListener verboseListener);

    void clearBanListeners();
}
