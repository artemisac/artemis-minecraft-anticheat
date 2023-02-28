package cc.ghast.packet.nms.payload;

public class ArtemisDigestException extends RuntimeException {
    public ArtemisDigestException() {
    }

    public ArtemisDigestException(String message) {
        super(message);
    }

    public ArtemisDigestException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArtemisDigestException(Throwable cause) {
        super(cause);
    }

    public ArtemisDigestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
