package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.utils.lists.EvictingLinkedList;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class StaffHolder extends AbstractHolder {
    public StaffHolder(PlayerData data) {
        super(data);

        canDebug = data.getPlayer().hasPermission("artemis.debug");
    }

    public boolean canDebug;
    public boolean isAlerts, packetListening, banned;
    public Cache<PlayerData, List<CheckInformation>> debug = CacheBuilder
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();
    public Cache<PlayerData, List<CheckInformation>> logDebug = CacheBuilder
            .newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();

    public Deque<String> log = new EvictingLinkedList<>(1000);
    public StaffEnums.StaffAlerts staffAlert = StaffEnums.StaffAlerts.NONE;

    public boolean isDebug(ArtemisCheck artemisCheck) {
        return this.isDebug(this.data, artemisCheck);
    }

    public boolean isDebug(PlayerData data, ArtemisCheck artemisCheck) {
        final List<CheckInformation> debugs = debug.getIfPresent(data);
        return debugs != null && debugs.contains(artemisCheck.getInfo());
    }

    public boolean isLogDebug(ArtemisCheck artemisCheck) {
        return this.isLogDebug(this.data, artemisCheck);
    }

    public boolean isLogDebug(PlayerData data, ArtemisCheck artemisCheck) {
        final List<CheckInformation> debugs = logDebug.getIfPresent(data);
        return debugs != null && debugs.contains(artemisCheck.getInfo());
    }

    public boolean isLogging() {
        return this.getLog().size() > 0;
    }

}
