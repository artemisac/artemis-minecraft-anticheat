package ac.artemis.core.v5.emulator.scroll;

public interface ScrollTemplate {
    ScrollTemplate name(final String name);

    ScrollTemplate minimum(final double minimum);

    ScrollTemplate maximum(final double maximum);

    ScrollTemplate defaultValue(final double defaultValue);

    String getAttributeUnlocalizedName();

    double clampValue(double value);

    double getDefaultValue();
}
