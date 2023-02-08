package ac.artemis.core.v4.check.debug.writer;

import ac.artemis.anticheat.api.check.debug.DebugKey;
import ac.artemis.core.v4.check.debug.Debug;

import java.util.List;

public class StandardDebugWriter implements DebugWriter {
    @Override
    public String write(List<Debug<?>> debugKeys) {
        final StringBuilder builder = new StringBuilder();

        for (DebugKey<?> debugKey : debugKeys) {
            builder.append(debugKey.getName())
                    .append('=')
                    .append(debugKey.getValue())
                    .append(" ");
        }

        return builder.toString();
    }
}
