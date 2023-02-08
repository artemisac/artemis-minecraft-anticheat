package ac.artemis.core.v4.check.debug.writer;

import ac.artemis.core.v4.check.debug.Debug;

import java.util.List;

public interface DebugWriter {
    String write(List<Debug<?>> debugKeys);
}
