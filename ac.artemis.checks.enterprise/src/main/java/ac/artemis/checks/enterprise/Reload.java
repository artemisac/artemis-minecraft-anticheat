package ac.artemis.checks.enterprise;

import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.anticheat.api.check.CheckReload;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.packet.minecraft.Server;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Ghast
 * @since 20-May-20
 */
public class Reload extends TimerTask implements CheckReload {

    @SneakyThrows
    public Reload() {
        this.watchService = ConfigManager.getChecks().getFile().getParentFile().toPath().getFileSystem().newWatchService();
        ConfigManager.getChecks().getFile().getParentFile().toPath().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        new Timer().scheduleAtFixedRate(this, 0, 1000);
    }

    private final WatchService watchService;
    private long lastReload;

    @Override
    public void run() {
        final WatchKey key = watchService.poll();
        if (key != null && key.isValid() && TimeUtil.hasExpired(lastReload, 5)) {
            reloadAssets();
        }
    }

    @SneakyThrows
    public void reloadAssets() {
        lastReload = System.currentTimeMillis();
        Artemis.v().getApi().getConfigManager().init(InitializeAction.RELOAD);
        for (Field field : ArtemisEnterprise.class.getDeclaredFields()) {
            if (!CheckInformation.class.isAssignableFrom(field.getType())) continue;
            field.setAccessible(true);
            CheckInformation checkInformation = (CheckInformation) field.get(null);
            checkInformation.init();
        }
        Chat.sendConsoleMessage("&7[&bArtemis&7] Updated &benterprise&7 checks.yml");
        String permission = ThemeManager.getCurrentTheme().getVerbosePermission();
        Server.v().getOnlinePlayers()
                .parallelStream()
                .forEach(p -> {
            if (p.hasPermission(permission)) {
                p.sendMessage(Chat.translate("&7[&bArtemis&7] &aReloaded enterprise checks.yml!"));
            }
        });
    }

        @Override
        public List<CheckInfo> getChecks() {
            final List<CheckInfo> checkInfos = new ArrayList<>();
            for (Field field : ArtemisEnterprise.class.getDeclaredFields()) {
                if (!CheckInfo.class.isAssignableFrom(field.getType())) continue;
            field.setAccessible(true);
            try {
                CheckInformation checkInformation = (CheckInformation) field.get(null);
                checkInfos.add(checkInformation);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

        }
        return checkInfos;
    }
}
