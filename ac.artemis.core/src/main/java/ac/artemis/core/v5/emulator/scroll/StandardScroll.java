package ac.artemis.core.v5.emulator.scroll;

import com.google.common.collect.Lists;

import java.util.*;

/**
 * @author Ghast
 * @since 12/02/2021
 * Artemis © 2021
 */
public class StandardScroll implements Scroll {
    private final ScrollTemplate scrollTemplate;
    private double base;
    private double cachedValue;
    
    // Storage
    private final Map<Integer, Set<ScrollModifier>> mapByOperation = new HashMap<>();
    private final Map<String, Set<ScrollModifier>> mapByName = new HashMap<>();
    private final Map<UUID, ScrollModifier> mapByUUID = new HashMap<>();

    private boolean needsUpdate = true;

    public StandardScroll(final ScrollTemplate scrollTemplate, final double base) {
        this.scrollTemplate = scrollTemplate;
        this.base = base;
    }

    @Override
    public double getBase() {
        return base;
    }

    @Override
    public void setBase(final double t) {
        this.base = t;

        flagForUpdate();
    }

    @Override
    public Collection<ScrollModifier> getModifiersByOperation(final int operation) {
        return this.mapByOperation.get(operation);
    }

    @Override
    public Collection<ScrollModifier> getAllThroughOperations() {
        final Set<ScrollModifier> set = new HashSet<>();

        for (int i = 0; i < 3; ++i) {
            set.addAll(this.getModifiersByOperation(i));
        }

        return set;
    }

    @Override
    public boolean hasModifier(final ScrollModifier modifier) {
        return this.mapByUUID.get(modifier.getId()) != null;
    }

    @Override
    public ScrollModifier getModifier(final UUID uuid) {
        return this.mapByUUID.get(uuid);
    }

    @Override
    public void applyModifier(final ScrollModifier modifier) {
        if (this.getModifier(modifier.getId()) != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        
        else {
            Set<ScrollModifier> set = this.mapByName.get(modifier.getName());

            if (set == null) {
                set = new HashSet<>();
                this.mapByName.put(modifier.getName(), set);
            }

            this.mapByOperation.get(modifier.getOperation()).add(modifier);
            set.add(modifier);
            this.mapByUUID.put(modifier.getId(), modifier);
        }

        flagForUpdate();
    }

    @Override
    public void removeModifier(final ScrollModifier modifier) {
        for (int i = 0; i < 3; ++i) {
            final Set<ScrollModifier> set = this.mapByOperation.get(i);
            set.remove(modifier);
        }

        final Set<ScrollModifier> set1 = this.mapByName.get(modifier.getName());

        if (set1 != null) {
            set1.remove(modifier);

            if (set1.isEmpty()) {
                this.mapByName.remove(modifier.getName());
            }
        }

        this.mapByUUID.remove(modifier.getId());
        
        flagForUpdate();
    }

    @Override
    public void removeAllModifiers() {
        final Collection<ScrollModifier> collection = this.getAllThroughOperations();

        if (collection != null) {
            for (final ScrollModifier attributemodifier : Lists.newArrayList(collection)) {
                this.removeModifier(attributemodifier);
            }
        }
        
        flagForUpdate();
    }

    @Override
    public double getAttributeValue() {
        if (this.needsUpdate) {
            this.cachedValue = this.computeValue();
            this.needsUpdate = false;
        }

        return this.cachedValue;
    }
    

    public void flagForUpdate() {
        this.needsUpdate = true;
    }

    private double computeValue() {
        double d0 = this.getBase();

        for (final ScrollModifier modifier : this.getModifiersByOperation(0)) {
            d0 += modifier.getAmount();
        }

        double d1 = d0;

        for (final ScrollModifier modifier : this.getModifiersByOperation(1)) {
            d1 += d0 * modifier.getAmount();
        }

        for (final ScrollModifier modifier : this.getModifiersByOperation(2)) {
            d1 *= 1.0D + modifier.getAmount();
        }

        return this.scrollTemplate.clampValue(d1);
    }
}
