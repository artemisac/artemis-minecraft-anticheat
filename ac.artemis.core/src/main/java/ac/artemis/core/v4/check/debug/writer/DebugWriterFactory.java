package ac.artemis.core.v4.check.debug.writer;

public class DebugWriterFactory {
    public DebugWriter build() {
        return new StandardDebugWriter();
    }
}
