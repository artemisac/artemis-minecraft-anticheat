package ac.artemis.core.v4.emulator.attribute;

public interface IAttribute
{
    String getAttributeUnlocalizedName();

    double clampValue(double p_111109_1_);

    double getDefaultValue();

    boolean getShouldWatch();

    IAttribute func_180372_d();
}