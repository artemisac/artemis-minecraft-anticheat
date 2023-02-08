package ac.artemis.core.v5.features.panel.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.APIManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.panel.PanelBanner;
import ac.artemis.core.v5.logging.model.Ban;
import ac.artemis.core.v5.features.panel.utils.PanelHTTPConnection;
import ac.artemis.core.v5.threading.Threading;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class PanelBanner_V1 implements PanelBanner, PanelHTTPConnection {
    private static final String backendUrl = "https://panel.artemis.ac/api/v1/anticheat/ban";
    private static final Gson gson = new Gson();
    private static final ExecutorService SERIALIZE_SERVER = Threading.getOrStartService("backend-notification");

    private final File dumpDirectory;

    public PanelBanner_V1(final File dumpDirectory) {
        this.dumpDirectory = dumpDirectory;
    }

    @Override
    public void log(final Ban ban) {
        SERIALIZE_SERVER.execute(() -> {
            if (true /*Bootstrap.v() == null*/) {
                if (APIManager.onLoader) {
                    Server.v().getPluginManager().kill(Artemis.v().getPlugin());
                    return;
                }

                final File output = new File(dumpDirectory, ban.getUsername() + "_" + System.currentTimeMillis() + ".json");

                try {
                    final FileOutputStream stream = new FileOutputStream(output);
                    IOUtils.write(gson.toJson(ban), stream);
                    stream.close();
                    Chat.sendConsoleMessage("&7[&bArtemis&7]&a Saved ban to " + output.getName());
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

                final BanRequest logsRequest = new BanRequest(session, license, ban);

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

                try {
                    final byte[] response = attemptConnect(backendUrl, gson.toJson(logsRequest).getBytes());
                    // Get the response
                    final AnticheatResponse anticheatResponse = gson.fromJson(new String(response), AnticheatResponse.class);

                    if (anticheatResponse.getErrorMessage() != null && !anticheatResponse.getErrorMessage().equalsIgnoreCase("")) {
                        throw new IllegalStateException("Failed to post correct logs: " + anticheatResponse.getErrorMessage());
                    }

                    Chat.sendConsoleMessage("&7[&bArtemis&7]&a Saved ban to Artemis Backend under license ****-****-*****-" + license.substring(license.length() - 6));
                } catch (final IllegalStateException e) {
                    Chat.sendConsoleMessage("&7[&b&lArtemis&7] &cFailed to upload logs to backend! Please contact the developers");
                }*/
            }
        });
    }

    @Data
    private static class BanRequest {
        @SerializedName("sessionId")
        private UUID sessionId;

        @SerializedName("license")
        private String license;

        @SerializedName("banId")
        private String banId;

        @SerializedName("username")
        private String username;

        @SerializedName("uuid")
        private UUID uuid;

        @SerializedName("timestamp")
        private long timestamp;

        public BanRequest(final UUID sessionId, final String license, final Ban ban) {
            this.sessionId = sessionId;
            this.license = license;
            this.banId = ban.getBanId();
            this.username = ban.getUsername();
            this.uuid = ban.getUuid();
            this.timestamp = ban.getTimestamp();
        }
    }
}
