package ac.artemis.core.v4.config;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v5.config.impl.PredictionConfiguration;
import ac.artemis.core.v5.config.impl.ReachConfiguration;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
@Getter
public class ConfigManager extends Manager {
    @Getter
    private static Configuration checks;
    @Getter
    private static Configuration settings;
    @Getter
    private static PredictionConfiguration prediction;
    @Getter
    private static ReachConfiguration reach;

    @Getter
    private static boolean debugger;

    public ConfigManager(Artemis plugin) {
        super(plugin,"Config [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        settings = Server.v().getConfig("settings.yml");
        checks = Server.v().getConfig("checks.yml");
        prediction = new PredictionConfiguration("prediction.yml");
        prediction.init();
        reach = new ReachConfiguration("reach.yml");
        reach.init();

        loadDataFolder();

        if (settings.getString("security.encryption-key").equalsIgnoreCase("GENERATE_ME")) {
            initAESKey();
        }

        debugger = ConfigManager.getSettings().getBoolean("general.debugger");
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        prediction.disinit();
        reach.disinit();
    }


    @SuppressWarnings("all")
    private void loadDataFolder() {
        File file = new File(plugin.getPlugin().getDataFolder(), "data");
        if (file.exists()) {
            file.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    private void initAESKey() {
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
        // Get base64 encoded version of the key
        String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        getSettings().set("security.encryption-key", encodedKey);
    }
}
