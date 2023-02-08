package ac.artemis.checks.enterprise.aim;

import ac.artemis.anticheat.api.check.type.Stage;
import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.check.templates.rotation.PredictiveRotationCheck;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.utils.modal.TrueAimRotation;

@Check(type = Type.AIM, var = "Test", bannable = false)
@Experimental(stage = Stage.EXPERIMENTING)
public class AimRetard extends PredictiveRotationCheck {
    public AimRetard(PlayerData data, CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final TrueAimRotation rotation) {

    }
}
