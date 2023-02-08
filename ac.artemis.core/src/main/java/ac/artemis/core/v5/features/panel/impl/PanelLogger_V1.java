package ac.artemis.core.v5.features.panel.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.APIManager;
import ac.artemis.core.v5.features.panel.PanelLogger;
import ac.artemis.core.v5.logging.model.Log;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.panel.utils.PanelHTTPConnection;
import ac.artemis.core.v5.threading.Threading;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PanelLogger_V1 implements PanelLogger, PanelHTTPConnection {
    private static final String backendUrl = "https://panel.artemis.ac/api/v1/anticheat/logs";
    private static final Gson gson = new GsonBuilder()
            .serializeSpecialFloatingPointValues()
            .serializeNulls()
            .create();
    private static final ExecutorService SERIALIZE_SERVER = Threading.getOrStartService("backend-notification");

    private final File dumpDirectory;

    public PanelLogger_V1(final File dumpDirectory) {
        this.dumpDirectory = dumpDirectory;
    }

    @Override
    public void log(final List<Log> logList) {
        final List<Log> logs = new ArrayList<>(logList);
        SERIALIZE_SERVER.execute(() -> {
            if (true/*Bootstrap.v() == null*/) {
                if (APIManager.onLoader) {
                    Server.v().getPluginManager().kill(Artemis.v().getPlugin());
                    return;
                }

                final File output = new File(dumpDirectory, System.currentTimeMillis() + ".json");

                try {
                    final FileOutputStream stream = new FileOutputStream(output);
                    IOUtils.write(gson.toJson(logs), stream);
                    stream.close();
                    Chat.sendConsoleMessage("&7[&bArtemis&7]&a Saved " + logs.size() + " logs to " + output.getName());
                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }

            else {
                /*final String license = Artemis.v().getApi().getLicenseManager().getLicense();
                final UUID session = Bootstrap.v().getSaxophone();
                final PublicKey keyPair = Bootstrap.v().getServerKey();

                if (session == null) {
                    Server.v().getPluginManager().kill(Artemis.v().getPlugin());
                    return;
                }

                final LogsRequest logsRequest = new LogsRequest(session, license, logs);

                try {
                    // Generate random symmetric cypher
                    final AsymmetricCipher cipher = new AsymmetricCipher();

                    final CryptoPacket encryptedLicense = cipher.encrypt(license.getBytes(), keyPair);
                    final CryptoPacketConverter converter = new CryptoPacketConverter();

                    logsRequest.setLicense(converter.convert(encryptedLicense));

                } catch (final Exception e) {
                    e.printStackTrace();
                    return;
                }

                final byte[] response = attemptConnect(backendUrl, gson.toJson(logsRequest).getBytes());

                // Get the response
                final AnticheatResponse anticheatResponse = gson.fromJson(new String(response), AnticheatResponse.class);

                if (anticheatResponse.getErrorMessage() != null && !anticheatResponse.getErrorMessage().equalsIgnoreCase("")) {
                    throw new IllegalStateException("Failed to post correct logs: " + anticheatResponse.getErrorMessage());
                }

                Chat.sendConsoleMessage("&7[&bArtemis&7]&a Uploaded " + logs.size() + " logs to Artemis Backend under license ****-****-*****-" + license.substring(license.length() - 6));*/
            }
        });
    }



    @Data
    private static class LogsRequest  {
        @SerializedName("sessionId")
        private UUID sessionId;

        @SerializedName("license")
        private String license;

        @SerializedName("logs")
        private final List<Log> logs;

        public LogsRequest(final UUID sessionId, final String license, final List<Log> logs) {
            this.sessionId = sessionId;
            this.license = license;
            this.logs = logs;
        }
    }
}
