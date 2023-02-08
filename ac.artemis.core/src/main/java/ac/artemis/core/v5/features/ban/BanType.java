package ac.artemis.core.v5.features.ban;

import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.features.ban.impl.AutoBanFeature;
import ac.artemis.core.v5.features.ban.impl.BanWaveFeature;
import ac.artemis.core.v5.features.ban.impl.NoneBanFeature;
import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public enum BanType {
    NONE(new NoneBanFeature(), "none", "default", "nothing", "nada", "nunca"),
    AUTOBAN(new AutoBanFeature(), "autoban", "ban", "punish", "instaban"),
    BANWAVE(new BanWaveFeature(), "banwave", "judgement", "jday", "banw", "judgementday", "banday") ,
    INCREMENT(new NoneBanFeature(), "increment", "lenient");

    private final BanFeature banFeature;
    private final String[] aliases;

    BanType(BanFeature banFeature, String... args) {
        this.banFeature = banFeature;
        this.aliases = args;
    }

    public BanFeature getBanFeature() {
        return banFeature;
    }
}
