package ac.artemis.core.v4.check.debug;

import ac.artemis.anticheat.api.check.debug.DebugKey;
import com.google.gson.annotations.SerializedName;

public class Debug<T> implements DebugKey<T> {
    @SerializedName("key")
    private final String key;

    @SerializedName("value")
    private final T value;

    public Debug(String key, T value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getName() {
        return key;
    }

    @Override
    public T getValue() {
        return value;
    }
}
