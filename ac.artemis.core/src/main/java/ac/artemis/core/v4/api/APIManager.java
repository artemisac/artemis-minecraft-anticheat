package ac.artemis.core.v4.api;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.ban.BanManager;
import ac.artemis.core.v4.check.CheckManager;
import ac.artemis.core.v4.dependency.DependencyManager;
import ac.artemis.core.v4.lag.LagManager;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.notification.NotificationManager;
import ac.artemis.core.v4.tick.TickManager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.hashing.HashUtil;
import ac.artemis.core.v4.packet.PacketManager;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.loader.LoaderManager;
import ac.artemis.core.v4.license.LicenseManager;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.data.PlayerDataManager;
import ac.artemis.core.v4.timings.TimingsManager;
import ac.artemis.core.v4.utils.maths.MathUtil;
import ac.artemis.core.v5.emulator.EmulatorManager;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.core.v5.language.LanguageManager;
import ac.artemis.core.v5.threading.Threading;
import ac.artemis.core.v5.utils.TimeUtil;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static ac.artemis.core.v4.utils.http.HTTPRequest.HTTP_SERVICE;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
@Getter
public class APIManager extends Manager {

    // MANAGERS
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;
    private ThemeManager themeManager;
    private NMSManager nmsManager;
    private NotificationManager alertManager;
    private BanManager banManager;
    private TimingsManager timingsManager;
    private DependencyManager dependencyManager;
    private LanguageManager languageManager;
    private TickManager tickManager;
    private LagManager lagManager;
    private LoaderManager loaderManager;
    private PacketManager packetManager;
    private LicenseManager licenseManager;
    private CheckManager checkManager;
    private EmulatorManager emulatorManager;
    private final List<Manager> managers = new ArrayList<>();

    // MISC
    private ExecutorService httpPool;
    private ExecutorService service;
    private final List<ExecutorService> threads = new ArrayList<>();

    public static final boolean onLoader = false;

    public APIManager(Artemis plugin) {
        super(plugin, "API [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) throws NoSuchAlgorithmException, IOException {
        Chat.sendLogo();
        // CONCURRENCY
        this.service = Threading.getOrStartService("artemis-startup-service");
        this.httpPool = Threading.getOrStartService("artemis-connection-service");;

        this.dependencyManager = new DependencyManager(plugin);
        this.dependencyManager.init(initializeAction);

        this.timingsManager = new TimingsManager(plugin);
        this.lagManager = new LagManager(plugin);
        this.configManager = new ConfigManager(plugin);
        this.languageManager = new LanguageManager(plugin);
        this.banManager = new BanManager(plugin);
        this.themeManager = new ThemeManager(plugin);
        this.playerDataManager = new PlayerDataManager(plugin);
        this.nmsManager = new NMSManager(plugin);
        this.loaderManager = new LoaderManager(plugin);
        this.packetManager = new PacketManager(plugin);
        this.licenseManager = new LicenseManager(plugin);
        this.tickManager = new TickManager(plugin);
        this.alertManager = new NotificationManager(plugin);
        this.checkManager = new CheckManager(plugin);
        this.emulatorManager = new EmulatorManager(plugin);

        this.managers.addAll(Arrays.asList(
                timingsManager,
                configManager,
                languageManager)
        );

        for (Manager manager : managers) {
            manager.init(InitializeAction.START);
        }

        final List<Manager> postBoot = Arrays.asList(
                loaderManager,
                themeManager,
                playerDataManager,
                nmsManager,
                emulatorManager,
                //storageManager,
                banManager,
                licenseManager,
                packetManager,
                tickManager,
                alertManager,
                checkManager
        );
        // And finally add 'em all
        this.managers.addAll(postBoot);


        Chat.sendConsoleMessage(Chat.spacer3());
        Chat.sendConsoleMessage(Lang.MSG_CONSOLE_BOOT_SPACER);

        for (Manager manager : postBoot) {
            final double before = TimeUtil.milliTimeWithAD();
            manager.init(InitializeAction.START);
            final double now = MathUtil.roundToPlace(TimeUtil.milliTimeWithAD() - before, 2);

            final String updated = Lang.MSG_CONSOLE_BOOT_MANAGER_ENABLE
                    .replace("%name%", manager.getName())
                    .replace("%time%", Double.toString(now));

            Chat.sendConsoleMessage("&r[&a✓&r] " + updated);
        }

        Chat.sendConsoleMessage(Chat.spacer());

        this.threads.addAll(Arrays.asList(service, httpPool));

        HTTP_SERVICE.execute(() -> {
            try {
                HashUtil.sendHashes(InitializeAction.START, licenseManager.getLicense());
            } catch (NullPointerException e) {
                e.printStackTrace();
                if (onLoader) {
                    disinit(ShutdownAction.STOP);
                    Server.v().getPluginManager().kill(plugin.getPlugin());
                }
            } catch (Exception e) {
                disinit(ShutdownAction.STOP);
                Server.v().getPluginManager().kill(plugin.getPlugin());
            }
        });
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        for (Manager manager : managers) {
            final double before = TimeUtil.milliTimeWithAD();
            manager.disinit(ShutdownAction.STOP);
            final double now = MathUtil.roundToPlace(TimeUtil.milliTimeWithAD() - before, 2);

            final String updated = Lang.MSG_CONSOLE_BOOT_MANAGER_DISABLE
                    .replace("%name%", manager.getName())
                    .replace("%time%", Double.toString(now));

            Chat.sendConsoleMessage("&r[&a✓&r] " + updated);
        }

        Threading.killAll();
        this.dependencyManager.disinit(ShutdownAction.STOP);
    }

    @Deprecated
    public synchronized void injectManager(Class<? extends Manager> clazz) {
        try {
            Manager manager = clazz.getConstructor(Artemis.class).newInstance(plugin);
            manager.init(InitializeAction.RELOAD);
            Chat.sendConsoleMessage("&7[&b&lArtemis&7] &aEnabled &b" + manager.getClass().getSimpleName() + "&a!");
            managers.add(manager);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void injectManager(final Manager manager) {
        managers.add(manager);
    }

}
