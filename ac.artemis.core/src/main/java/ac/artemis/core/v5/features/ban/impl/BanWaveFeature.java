package ac.artemis.core.v5.features.ban.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.ban.BanFeature;
import ac.artemis.core.v5.logging.model.Ban;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BanWaveFeature implements BanFeature {
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private final File banWave = new File(Artemis.v().getPlugin().getDataFolder(), "data/banwave.json");
    private BanWaveFile banWaveFile;
    private WatchService watchService;
    private Timer timer;

    public BanWaveFeature() {
        try {
            /*
             * Here we implement the load thread to ensure the file is loaded. If the file is not found,
             * we try creating it.
             */
            tryLoad();

            /*
             * Initialize a watch service which will monitor the file to ensure it can be dynamically
             * modified to prevent any removable bans and so and forth. To ensure this system can be
             * compatible with the removable ban system, we will have to implement some sort of remove
             * listener to this.
             */
            this.watchService = banWave.getParentFile().toPath().getFileSystem().newWatchService();
            this.banWave.getParentFile().toPath().register(watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY
            );

            /*
             * Here we attempt creating a new timer task which will pretty much be running the loop
             * for saving, checking the banwave and executing the bans. Pretty efficient system imo
             * considering it's running on a timer thread hence not impacting server data
             */
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkLoop();
                }
            }, 1000, 1000);


            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                this.trySave();
                this.timer.cancel();
            }));

        } catch (final Throwable e) {
            e.printStackTrace();
            Chat.sendConsoleMessage("&c&lFatal error when initializing Artemis! Please contact the developer!");
            Server.v().getPluginManager().kill(Artemis.v().getPlugin());
        }
    }

    private void trySave() {
        try {
            /*
             * Here we initialize a file writer which will proceed to save everything to
             */
            final Writer writer = new FileWriter(banWave);
            gson.toJson(banWaveFile, BanWaveFile.class, writer);
            writer.close();
        } catch (final Exception e) {
            Chat.sendConsoleMessage("&c&lFatal error when saving Artemis banwave! Please contact the developer! (err: 0xBWF-TS)");
            e.printStackTrace();
            Server.v().getPluginManager().kill(Artemis.v().getPlugin());
        }
    }

    private void tryLoad() {
        try {
            if (!banWave.exists()) {
                if (banWave.getParentFile() != null) {
                    banWave.getParentFile().mkdir();
                } else {
                    banWave.getAbsoluteFile().getParentFile().mkdir();
                }
                final FileOutputStream outputStream = new FileOutputStream(banWave);
                IOUtils.write(gson.toJson(new BanWaveFile(), BanWaveFile.class), outputStream);
                outputStream.close();
                //Chat.sendConsoleMessage("&7[&bArtemis&7]&a Built the banwave data handler successfully!");
            }

            final JsonReader reader = new JsonReader(new FileReader(banWave));
            banWaveFile = gson.fromJson(reader, BanWaveFile.class);
            reader.close();
            //Chat.sendConsoleMessage("&7[&bArtemis&7]&a Loaded the banwave data handler successfully!");
        } catch (final Exception e) {
            Chat.sendConsoleMessage("&c&lFatal error when initializing Artemis! Please contact the developer! (Error 0xBWF:TryLoad)");
            e.printStackTrace();
        }
    }


    private void checkLoop() {
        final WatchKey watchKey = watchService.poll();

        reload: {
            final boolean invalid = watchKey == null || watchKey.pollEvents().size() == 0;
            if (invalid)
                break reload;

            tryLoad();
        }

        wave: {
            final boolean invalid = banWaveFile != null
                    && banWaveFile.getLastWave() + banWaveFile.getDelay() < System.currentTimeMillis();

            if (!invalid)
                break wave;

            final Configuration settings = ConfigManager.getSettings();
            if (banWaveFile.getBans().size() > 0) {
                for (final Ban ban : banWaveFile.getBans()) {
                    Server.v().getScheduler().runTask(() -> {
                        Server.v().dispatchCommand(Server.v().getConsoleSender(),
                                Chat.translate(settings.getString("ban.command")
                                        .replace("%artemis_ban_impl%", ban.getUsername())
                                        .replace("%artemis_ban_id%", ban.getBanId())
                                )
                        );
                    });
                }

                banWaveFile.getBans().clear();
                Chat.sendConsoleMessage("&7[&bArtemis&7]&a Finished executing ban-wave task!");
            }
            banWaveFile.setLastWave(System.currentTimeMillis());
            trySave();
        }
    }

    @Override
    public void logProfile(final Ban ban) {
        Artemis.v().getApi().getAlertManager().getNotificationProvider().provide(ban);
        Artemis.v().getApi().getAlertManager().getNotificationProvider().push();

        if (ban.isCancelled())
            return;

        banWaveFile.getBans().add(ban);
        trySave();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class BanWaveFile {
        @SerializedName("delay")
        private long delay = 3600000L;

        @SerializedName("lastWave")
        private long lastWave = 0L;

        @SerializedName("bans")
        private List<Ban> bans = new ArrayList<>();
    }
}
