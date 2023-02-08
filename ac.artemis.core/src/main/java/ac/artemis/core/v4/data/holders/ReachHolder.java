package ac.artemis.core.v4.data.holders;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class ReachHolder extends AbstractHolder {
    public ReachHolder(PlayerData data) {
        super(data);
    }

    private final Map<Integer, ReachEntity> entities = new HashMap<>();
    private boolean hasAttacked, hasClicked;

    private ReachEntity lastAttackedEntity;
    private Player target;
}
