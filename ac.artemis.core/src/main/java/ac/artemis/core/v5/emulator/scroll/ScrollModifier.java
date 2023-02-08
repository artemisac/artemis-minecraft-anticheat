package ac.artemis.core.v5.emulator.scroll;

import java.util.UUID;

public interface ScrollModifier {
    ScrollModifier id(final UUID uuid);

    ScrollModifier name(final String name);

    ScrollModifier operation(final int operation);

    ScrollModifier amount(final double amount);

    UUID getId();

    String getName();

    int getOperation();

    double getAmount();
}
