package ac.artemis.anticheat.bukkit.logging;

import ac.artemis.anticheat.api.alert.Alert;
import ac.artemis.anticheat.api.listener.InjectListener;
import ac.artemis.anticheat.api.listener.VerboseListener;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.anticheat.bukkit.logging.listener.ConsoleVerboseListener;
import ac.artemis.anticheat.bukkit.logging.listener.PlayerVerboseListener;
import ac.artemis.core.v5.threading.Threading;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.entity.Messager;
import ac.artemis.packet.minecraft.entity.impl.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class BukkitNotificationListener implements InjectListener, VerboseListener {
    private final Map<Messager, Pair<VerboseListener, StaffEnums.StaffAlerts>> toAlert = new HashMap<>();
    private final ExecutorService service = Threading.getOrStartService("artemis-notifications");

    public void start() {
        Artemis.v()
                .getApi()
                .getPlayerDataManager()
                .addInjectListener(this);
        Artemis.v()
                .getApi()
                .getAlertManager()
                .addVerboseListener(this);
    }

    public void end() {
        Artemis.v()
                .getApi()
                .getPlayerDataManager()
                .removeInjectListener(this);
        Artemis.v()
                .getApi()
                .getAlertManager()
                .removeVerboseListener(this);
    }
    @Override
    public void receive(Alert alert) {
        service.execute(() -> {
            this.toAlert.forEach((p, sf) -> {
                if (!sf.getY().isHighEnough(alert.getSeverity())) return;
                sf.getX().receive(alert);
            });
        });
    }

    @Override
    public void onInject(Player player) {
        checkAlerts(player);
    }

    @Override
    public void onDestroy(Player player) {
        toAlert.remove(player);
    }

    public void checkAlerts(Player p) {
        final boolean staff = p.hasPermission(ThemeManager.getCurrentTheme().getAlertsPermission());
        final boolean admin = p.hasPermission(ThemeManager.getCurrentTheme().getMainPermission()) || p.isOp();
        final PlayerData data = Artemis.v().getApi().getPlayerDataManager().getData(p);

        if (admin || staff) {
            final StaffEnums.StaffAlerts alerts = admin ? StaffEnums.StaffAlerts.VERBOSE :  StaffEnums.StaffAlerts.VERBOSE;
            data.staff.setStaffAlert(alerts);
            this.toAlert.put(p, new Pair<>(new PlayerVerboseListener(p), alerts));
        } else {
            this.toAlert.remove(p);
        }
    }

    public void setAlerts(Player p) {
        final PlayerData data = Artemis.v().getApi().getPlayerDataManager().getData(p);

        if (data.staff.getStaffAlert().isHighEnough(StaffEnums.StaffAlerts.ALERTS)) {
            this.toAlert.put(p, new Pair<>(new PlayerVerboseListener(p), data.staff.getStaffAlert()));
        } else {
            this.toAlert.remove(p);
        }
    }

    public void toggleConsoleAlerts(StaffEnums.StaffAlerts staffAlerts) {
        final Messager hash = Server.v().getConsoleSender();
        if (toAlert.containsKey(hash)) {
            this.toAlert.remove(hash);
        } else {
            this.toAlert.put(hash, new Pair<>(new ConsoleVerboseListener(), staffAlerts));
        }
    }
}
