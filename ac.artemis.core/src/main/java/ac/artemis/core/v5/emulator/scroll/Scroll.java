package ac.artemis.core.v5.emulator.scroll;

import java.util.Collection;
import java.util.UUID;

public interface Scroll {
    double getBase();

    void setBase(final double t);
    
    Collection<ScrollModifier> getModifiersByOperation(final int operation);

    Collection<ScrollModifier> getAllThroughOperations();

    boolean hasModifier(final ScrollModifier modifier);

    ScrollModifier getModifier(final UUID uuid);

    void applyModifier(final ScrollModifier modifier);

    void removeModifier(final ScrollModifier modifier);

    void removeAllModifiers();

    double getAttributeValue();
}
