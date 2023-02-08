package ac.artemis.core.v4.check.exempt;

import ac.artemis.core.v4.check.exempt.type.ExemptType;
import ac.artemis.core.v4.data.PlayerData;

import java.util.Arrays;
import java.util.function.Function;

public class ExemptManager {
    private final PlayerData playerData;

    public ExemptManager(final PlayerData playerData) {
        this.playerData = playerData;
    }

    public boolean isExempt(final ExemptType exemptType) {
        return exemptType.getFunction().apply(playerData);
    }

    public boolean isExempt(final Function<PlayerData, Boolean> customExempt) {
        return customExempt.apply(playerData);
    }

    public boolean isExempt(final ExemptType... exemptTypes) {
        return Arrays.stream(exemptTypes).anyMatch(exemptType -> exemptType.getFunction().apply(playerData));
    }
}
