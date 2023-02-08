package ac.artemis.core.v5.features.ban.impl;

import ac.artemis.core.Artemis;
import ac.artemis.core.v5.features.ban.BanFeature;
import ac.artemis.core.v5.logging.model.Ban;

public class NoneBanFeature implements BanFeature {
    @Override
    public void logProfile(final Ban ban) {
        ban.setBanId(ban.getBanId() + "-" + "none");

        Artemis.v().getApi().getAlertManager().getNotificationProvider().provide(ban);
        Artemis.v().getApi().getAlertManager().getNotificationProvider().push();
    }
}
