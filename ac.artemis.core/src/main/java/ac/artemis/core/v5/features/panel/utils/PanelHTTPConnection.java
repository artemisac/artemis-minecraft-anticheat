package ac.artemis.core.v5.features.panel.utils;

import lombok.SneakyThrows;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public interface PanelHTTPConnection {
    @SneakyThrows
    default byte[] attemptConnect(final String session, final byte[] request) {
        final URL url = new URL(session);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.setRequestMethod("POST");
        conn.addRequestProperty("User-Agent", "Artemis/Client");
        conn.addRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        final OutputStream out = conn.getOutputStream();
        out.write(request);
        out.flush();
        out.close();

        if (conn.getResponseCode() == 403) {
            throw new IllegalStateException("Access forbidden");
        } else if (conn.getResponseCode() != 200) {
            throw new IllegalStateException("An error occurred while downloading file: " + conn.getResponseCode());
        }

        final InputStream in = conn.getInputStream();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        int total;
        while ((total = in.read(buffer)) > 0) {
            baos.write(buffer, 0, total);
        }
        return baos.toByteArray();
    }
}
