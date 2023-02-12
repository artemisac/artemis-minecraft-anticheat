package ac.artemis.core.v4.utils.hashing;

import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.http.HTTPRequest;
import ac.artemis.core.v4.utils.item.CheckRequest;
import lombok.SneakyThrows;

import java.net.URL;
import java.security.PublicKey;
import java.util.UUID;

/**
 * @author Ghast
 * @since 03-Apr-20
 */
public class HashUtil {

    public static String encodeUUID(UUID uuid) {
        return encode(uuid.toString() + System.currentTimeMillis());
    }

    public static String encode(String header) {
        char[] code = new char[32];
        for (int i = 0; i < header.length(); i++) {
            code[i % code.length] = (char) ((int) code[i % code.length] ^ (int) header.charAt(i));
        }
        return new String(code);
    }



    public static void sendHashes(InitializeAction action, String license) {

    }
}
