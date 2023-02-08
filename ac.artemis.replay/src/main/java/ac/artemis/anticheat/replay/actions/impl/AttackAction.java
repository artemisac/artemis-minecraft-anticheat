package ac.artemis.anticheat.replay.actions.impl;

import ac.artemis.anticheat.replay.actions.ActionData;
import ac.artemis.anticheat.replay.actions.ActionType;
import ac.artemis.anticheat.replay.actions.EntityAction;

@ActionData(ActionType.ATTACK)
public interface AttackAction extends EntityAction {
}
