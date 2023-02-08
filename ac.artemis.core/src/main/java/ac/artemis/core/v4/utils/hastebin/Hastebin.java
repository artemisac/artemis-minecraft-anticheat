package ac.artemis.core.v4.utils.hastebin;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.http.HTTPRequest;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URL;

public class Hastebin {
    private static final String endpoint = "https://paste.artemis.ac/documents";

    public static String paste(final String[] s, Player player) {
        Response response = new Response();

        HTTPRequest.HTTP_SERVICE.execute(() -> {
            try {
                String res = sendData(s);
                response.setString(res);

                if (res == null) {
                    player.sendMessage(Chat.translate("&7[&6&lOUTPUT&7] &6Error when pasting to Hastebin. Check console"));
                    return;
                }
                player.sendMessage(Chat.translate("&7[&6&lOUTPUT&7] &6Pasted hastebin at: " + response.getString()));

            } catch (IOException e) {
                player.sendMessage(Chat.translate("&7[&6&lOUTPUT&7] &6Error when pasting to Hastebin. Check console"));
                e.printStackTrace();
                response.setString(null);
            }
        });
        return response.getString();
    }

    private static String sendData(final String[] s) throws IOException {
        StringBuilder payload = new StringBuilder();
        for (String string : s) {
            payload.append("\n").append(string);
        }
        payload.append("\n -- end --");
        HTTPRequest request = new HTTPRequest(new URL(endpoint));
        request.setTimeout(10000);
        request.setUseragent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.109 Safari/537.37");
        request.setPostData(payload.toString());
        String[] res;
        try {
            res = request.read();
            String reply = res[0];
            JsonParser parser = new JsonParser();
            return "https://paste.artemis.ac/" + parser.parse(reply).getAsJsonObject().get("key").getAsString();
        } catch (Exception e) {
            System.out.println("[!]---> Unhandled HTTP request exception");

        }
        return null;
    }
}
