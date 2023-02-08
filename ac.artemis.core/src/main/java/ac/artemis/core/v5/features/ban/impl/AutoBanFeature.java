package ac.artemis.core.v5.features.ban.impl;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.config.Configuration;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.ban.BanFeature;
import ac.artemis.core.v5.logging.model.Ban;

public class AutoBanFeature implements BanFeature {
    @Override
    public void logProfile(final Ban ban) {
        Artemis.v().getApi().getAlertManager().getNotificationProvider().provide(ban);
        Artemis.v().getApi().getAlertManager().getNotificationProvider().push();

        if (ban.isCancelled())
            return;

        final Configuration settings = ConfigManager.getSettings();

        Server.v().getScheduler().runTask(() -> {
            Server.v().dispatchCommand(Server.v().getConsoleSender(),
                    Chat.translate(settings.getString("ban.command")
                            .replace("%artemis_ban_impl%", ban.getUsername())
                            .replace("%artemis_ban_id%", ban.getBanId())
                    )
            );
        });
    }
}
