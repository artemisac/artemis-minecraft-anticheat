package ac.artemis.core.v4.check.templates.emulator;

import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PredictionHandler;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;

public abstract class PredictionCheck  extends ArtemisCheck implements PredictionHandler {
    public PredictionCheck(PlayerData data, CheckInformation info) {
        super(data, info);
    }

}
