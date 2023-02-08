package ac.artemis.core.v4.data.holders;

import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.position.Velocity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CombatHolder extends AbstractHolder {

    public Player lastOpponent;
    public double cps, lastCps;
    public boolean isAttack, isCinematic, isCinematic2, isProcessAttack;
    public long lastAttack, lastDamage, lastBowDamage, lastExplosion;
    public List<Velocity> velocities;

    public CombatHolder(final PlayerData data) {
        super(data);
    }
}
