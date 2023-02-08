package ac.artemis.core.v4.encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class IpTransformer {
    public static String serialize(String s, String key) throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

            byte[] encrypted = cipher.doFinal(s.getBytes());

            return Arrays.toString(Base64.getEncoder().encode(encrypted));
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }

    public static String deserialize(String s, String key) throws IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(key);
            SecretKeySpec skeySpec = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(s));

            return Arrays.toString(decrypted);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
            throw e;
        }
    }
}
