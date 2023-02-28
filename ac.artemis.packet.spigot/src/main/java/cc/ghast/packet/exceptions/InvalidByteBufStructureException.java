package cc.ghast.packet.exceptions;

public class InvalidByteBufStructureException extends RuntimeException {
    public InvalidByteBufStructureException(Throwable cause) {
        super(cause);
    }

    public InvalidByteBufStructureException(String message) {
        super(message);
    }
}
