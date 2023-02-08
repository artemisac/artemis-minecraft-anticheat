package ac.artemis.anticheat.replay.actions.impl;

import ac.artemis.anticheat.replay.actions.ActionData;
import ac.artemis.anticheat.replay.actions.ActionType;
import ac.artemis.anticheat.replay.actions.EntityAction;

@ActionData(ActionType.MOVEMENT)
public interface MovementAction extends EntityAction {
    double getDeltaX();
    double getDeltaY();
    double getDeltaZ();
}
