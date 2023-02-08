package ac.artemis.core.v5.emulator.scroll;

import ac.artemis.core.v5.utils.minecraft.MathHelper;

public class StandardScrollTemplate implements ScrollTemplate {
    private String name;
    private double minimum;
    private double maximum;
    private double defaultValue;

    public StandardScrollTemplate name(final String name) {
        this.name = name;
        return this;
    }

    public StandardScrollTemplate minimum(final double minimum) {
        this.minimum = minimum;
        return this;
    }

    public StandardScrollTemplate maximum(final double maximum) {
        this.maximum = maximum;
        return this;
    }

    public StandardScrollTemplate defaultValue(final double defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public String getAttributeUnlocalizedName() {
        return name;
    }

    @Override
    public double clampValue(final double value) {
        return MathHelper.clamp_double(value, minimum, maximum);
    }

    @Override
    public double getDefaultValue() {
        return defaultValue;
    }
}
