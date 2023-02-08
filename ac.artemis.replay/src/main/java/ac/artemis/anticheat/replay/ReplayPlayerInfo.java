package ac.artemis.anticheat.replay;

import java.util.UUID;

public interface ReplayPlayerInfo {
    UUID getUuid();
    String getName();
    String getDisplayName();
    String getSkinBase64();

    int getEntityId();

    double getSpawnX();
    double getSpawnY();
    double getSpawnZ();

    float getSpawnYaw();
    float getSpawnPitch();
}
