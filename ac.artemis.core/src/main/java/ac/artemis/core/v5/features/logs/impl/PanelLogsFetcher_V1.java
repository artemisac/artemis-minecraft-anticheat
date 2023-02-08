package ac.artemis.core.v5.features.logs.impl;

import ac.artemis.anticheat.api.check.CheckInfo;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.logs.FetchedLog;
import ac.artemis.core.v5.features.logs.PanelLogsFetcher;
import ac.artemis.core.v5.features.panel.utils.PanelHTTPConnection;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PanelLogsFetcher_V1 implements PanelLogsFetcher, PanelHTTPConnection {
    private static final String BACKEND_URL = "https://panel.artemis.ac/api/v1/anticheat/viewer/logs";
    private static final Gson GSON = new Gson();

    @Override
    public List<FetchedLog> getLogs(final UUID uuid) {
        /*final String license = Artemis.v().getApi().getLicenseManager().getLicense();
        final UUID session = null; //Bootstrap.v().getSaxophone();
        final PublicKey keyPair = null; //Bootstrap.v().getServerKey();

        if (session == null) {
            return new ArrayList<>();
        }

        final Request logsRequest = new Request();
        logsRequest.setLicense(license);
        logsRequest.setSessionId(session);
        logsRequest.setPlayer(uuid);

        try {
            // Generate random symmetric cypher
            final AsymmetricCipher cipher = new AsymmetricCipher();

            final CryptoPacket encryptedLicense = cipher.encrypt(license.getBytes(), keyPair);
            final CryptoPacketConverter converter = new CryptoPacketConverter();

            logsRequest.setLicense(converter.convert(encryptedLicense));
        } catch (final Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        try {
            final byte[] response = attemptConnect(BACKEND_URL, GSON.toJson(logsRequest).getBytes());
            // Get the response
            final Response anticheatResponse = GSON.fromJson(new String(response), Response.class);

            if (anticheatResponse.getErrorMessage() != null && !anticheatResponse.getErrorMessage().equalsIgnoreCase("")) {
                throw new IllegalStateException("Failed to post correct logs: " + anticheatResponse.getErrorMessage());
            }

            return anticheatResponse.getLogs()
                    .entrySet()
                    .stream()
                    .map(e -> {
                        final CheckInfo information = Artemis.v().getApi().getCheckManager().getInfo(e.getKey());

                        return new FetchedLog(information, e.getValue());
                    })
                    .collect(Collectors.toList());

        } catch (final IllegalStateException e) {
            Chat.sendConsoleMessage("&7[&b&lArtemis&7] &cFailed to download download logs from backend! Please contact the developers");
            e.printStackTrace();
        }*/
        return new ArrayList<>();
    }

    @Data
    static class Request {
        @SerializedName("sessionId")
        private UUID sessionId;

        @SerializedName("license")
        private String license;

        @SerializedName("player")
        private UUID player;
    }

    @Data
    static class Response {
        @SerializedName("errorMessage")
        private String errorMessage;

        @SerializedName("player")
        private String player;

        @SerializedName("count")
        private int count;

        @SerializedName("logs")
        private Map<String, Long> logs;
    }
}
