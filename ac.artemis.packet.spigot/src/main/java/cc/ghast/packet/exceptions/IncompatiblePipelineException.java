package cc.ghast.packet.exceptions;

public class IncompatiblePipelineException extends RuntimeException {
    public IncompatiblePipelineException(final Class<?> type) {
        super("Pipeline is not supposed to receive type " + type);
    }
}