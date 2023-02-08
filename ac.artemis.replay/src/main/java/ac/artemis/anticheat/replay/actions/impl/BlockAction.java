package ac.artemis.anticheat.replay.actions.impl;

import ac.artemis.anticheat.replay.ReplayBlock;
import ac.artemis.anticheat.replay.actions.Action;
import ac.artemis.anticheat.replay.actions.ActionData;
import ac.artemis.anticheat.replay.actions.ActionType;

import java.util.Collection;

@ActionData(ActionType.BLOCK)
public interface BlockAction extends Action {
    Collection<ReplayBlock> getBlocks();
}
