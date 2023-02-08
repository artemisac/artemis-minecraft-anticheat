package ac.artemis.anticheat.replay;

import java.util.Collection;

public interface ReplayPlayerRepository {
    int getEntityId(final ReplayPlayerInfo entity);

    ReplayPlayerInfo getEntity(final int id);

    Collection<ReplayPlayerInfo> getEntities();
}
