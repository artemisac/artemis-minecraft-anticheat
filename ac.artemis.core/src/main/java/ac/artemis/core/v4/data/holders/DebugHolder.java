package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.data.PlayerData;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebugHolder extends AbstractHolder {
    public DebugHolder(PlayerData data) {
        super(data);
    }
}
