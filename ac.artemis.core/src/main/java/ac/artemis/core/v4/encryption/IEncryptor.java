package ac.artemis.core.v4.encryption;

/**
 * @author Ghast
 * @since 27-Apr-20
 */
public interface IEncryptor {
    String serialize(String value, String key);

    String deserialize(String encryptedValue, String key);

    String generateKey(String seed);
}
