package ac.artemis.core.v4.emulator.attribute.impl;

import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;

public class SharedMonsterAttributes {
    public static final IAttribute maxHealth = (new RangedAttribute((IAttribute)null, "generic.maxHealth",
            20.0D, 0.0D, 1024.0D)).setDescription("Max Health").setShouldWatch(true);
    public static final IAttribute followRange = (new RangedAttribute((IAttribute)null, "generic.followRange",
            32.0D, 0.0D, 2048.0D)).setDescription("Follow Range");
    public static final IAttribute knockbackResistance = (new RangedAttribute((IAttribute)null, "generic.knockbackResistance",
            0.0D, 0.0D, 1.0D)).setDescription("Knockback Resistance");
    public static final IAttribute movementSpeed = (new RangedAttribute((IAttribute)null, "generic.movementSpeed",
            0.699999988079071D, 0.0D, 1024.0D)).setDescription("Movement Speed").setShouldWatch(true);
    public static final IAttribute attackDamage = new RangedAttribute((IAttribute)null, "generic.attackDamage",
            2.0D, 0.0D, 2048.0D);
}
