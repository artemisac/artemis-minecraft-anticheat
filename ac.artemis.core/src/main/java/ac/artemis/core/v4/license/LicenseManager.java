package ac.artemis.core.v4.license;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.file.LicensePatcher;
import ac.artemis.core.v4.utils.hashing.HashUtil;
import ac.artemis.core.v4.utils.maths.OptifineUtil;
import ac.artemis.core.v4.utils.random.SafeRandom;
import lombok.Getter;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Random;

import static ac.artemis.core.v4.api.APIManager.onLoader;
import static ac.artemis.core.v4.utils.http.HTTPRequest.HTTP_SERVICE;


@Getter
public class LicenseManager extends Manager {
    public LicenseManager(Artemis plugin) {
        super(plugin, "Panel [Manager]");
    }

    private String license;
    private static final Random random = new Random();

    @Override
    public void init(InitializeAction initializeAction) {
        this.initLicense();
        Server.v().getScheduler().runTaskTimerAsynchronously(() -> {
            final Runnable runnable;
            switch (random.nextInt(4)) {
                case 1: runnable = () -> HashUtil.sendHashes(InitializeAction.START, license); break;
                case 2: runnable = () -> OptifineUtil.checkOptifine(InitializeAction.START, license); break;
                default: runnable = () -> LicensePatcher.initChecks(InitializeAction.START, license);
            }

            HTTP_SERVICE.execute(() -> {
                try {
                    runnable.run();
                } catch (NullPointerException e) {
                    if (onLoader) {
                        e.printStackTrace();
                        disinit(ShutdownAction.STOP);
                        Server.v().getPluginManager().kill(plugin.getPlugin());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    disinit(ShutdownAction.STOP);
                    Server.v().getPluginManager().kill(plugin.getPlugin());
                }
            });
        }, 0, 1000 * 60);
    }

    private void initLicense() {
        //final Loader plugin = Loader.v();

        if (true) {
            Chat.sendConsoleMessage("&r[&a✓&r] &7Launching session manager in developer mode...");

            try {
                Class<?> dev = Class.forName("ac.artemis.anticheat.ArtemisDev");
                Field license = dev.getDeclaredField("LICENSE");
                this.license = (String) license.get(null);
            } catch (Exception e) {
                Chat.sendConsoleMessage("&7[&bArtemis&7] &cFailed to establish session. Please restart or contact a" +
                        "developer with the following error code: 0x01 (SESSION_DEV_FAILURE)");
                Server.v().getPluginManager().kill(this.plugin.getPlugin());
                return;
            }
            return;
        }

        /*File file = new File(plugin.getDataFolder(), "license.txt");
        if (!file.exists()) {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdir();
            } else {
                file.getAbsoluteFile().getParentFile().mkdir();
            }
            plugin.saveResource("license.txt", true);
        }
        try {
            FileReader bufferedInputStream = new FileReader(file);
            BufferedReader reader = new BufferedReader(bufferedInputStream);
            this.license = reader.readLine();
            if (license == null) {
                System.out.println("Error whilst initializing the plugin: license not found");
                Server.v().getPluginManager().kill(this.plugin.getPlugin());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }
}
