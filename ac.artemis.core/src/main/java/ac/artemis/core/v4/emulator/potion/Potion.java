package ac.artemis.core.v4.emulator.potion;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.impl.SharedMonsterAttributes;
import ac.artemis.core.v5.emulator.Emulator;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class Potion
{
    /** The array of potion types. */
    public static final Potion[] potionTypes = new Potion[32];
    public static final Potion field_180151_b = null;
    public static final Potion moveSpeed = (new Potion(1, false, 8171462))
            .setPotionName("potion.moveSpeed").setIconIndex(0, 0)
            .registerPotionAttributeModifier(SharedMonsterAttributes.movementSpeed,
                    "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2);
    public static final Potion moveSlowdown = (new Potion(2, true, 5926017))
            .setPotionName("potion.moveSlowdown").setIconIndex(1, 0)
            .registerPotionAttributeModifier(SharedMonsterAttributes.movementSpeed,
                    "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2);
    public static final Potion digSpeed = (new Potion(3, false, 14270531))
            .setPotionName("potion.digSpeed").setIconIndex(2, 0).setEffectiveness(1.5D);
    public static final Potion digSlowdown = (new Potion(4, true, 4866583))
            .setPotionName("potion.digSlowDown").setIconIndex(3, 0);
    public static final Potion damageBoost = (new PotionAttackDamage(5, false, 9643043))
            .setPotionName("potion.damageBoost").setIconIndex(4, 0)
            .registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage,
                    "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2);
    public static final Potion heal = (new PotionHealth(6, false, 16262179))
            .setPotionName("potion.heal");
    public static final Potion harm = (new PotionHealth(7, true, 4393481))
            .setPotionName("potion.harm");
    public static final Potion jump = (new Potion(8,false, 2293580))
            .setPotionName("potion.jump").setIconIndex(2, 1);
    public static final Potion confusion = (new Potion(9,true, 5578058))
            .setPotionName("potion.confusion").setIconIndex(3, 1).setEffectiveness(0.25D);

    /** The regeneration Potion object. */
    public static final Potion regeneration = (new Potion(10,false, 13458603))
            .setPotionName("potion.regeneration").setIconIndex(7, 0).setEffectiveness(0.25D);
    public static final Potion resistance = (new Potion(11, false, 10044730))
            .setPotionName("potion.resistance").setIconIndex(6, 1);

    /** The fire resistance Potion object. */
    public static final Potion fireResistance = (new Potion(12, false, 14981690))
            .setPotionName("potion.fireResistance").setIconIndex(7, 1);

    /** The water breathing Potion object. */
    public static final Potion waterBreathing = (new Potion(13, false, 3035801))
            .setPotionName("potion.waterBreathing").setIconIndex(0, 2);

    /** The invisibility Potion object. */
    public static final Potion invisibility = (new Potion(14, false, 8356754))
            .setPotionName("potion.invisibility").setIconIndex(0, 1);

    /** The blindness Potion object. */
    public static final Potion blindness = (new Potion(15, true, 2039587))
            .setPotionName("potion.blindness").setIconIndex(5, 1).setEffectiveness(0.25D);

    /** The night vision Potion object. */
    public static final Potion nightVision = (new Potion(16, false, 2039713))
            .setPotionName("potion.nightVision").setIconIndex(4, 1);

    /** The hunger Potion object. */
    public static final Potion hunger = (new Potion(17, true, 5797459))
            .setPotionName("potion.hunger").setIconIndex(1, 1);

    /** The weakness Potion object. */
    public static final Potion weakness = (new PotionAttackDamage(18, true, 4738376))
            .setPotionName("potion.weakness").setIconIndex(5, 0)
            .registerPotionAttributeModifier(SharedMonsterAttributes.attackDamage,
                    "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);

    /** The poison Potion object. */
    public static final Potion poison = (new Potion(19, true, 5149489))
            .setPotionName("potion.poison").setIconIndex(6, 0).setEffectiveness(0.25D);

    /** The wither Potion object. */
    public static final Potion wither = (new Potion(20,true, 3484199))
            .setPotionName("potion.wither").setIconIndex(1, 2).setEffectiveness(0.25D);

    /** The health boost Potion object. */
    public static final Potion healthBoost = (new PotionHealthBoost(21, false, 16284963))
            .setPotionName("potion.healthBoost").setIconIndex(2, 2)
            .registerPotionAttributeModifier(SharedMonsterAttributes.maxHealth,
                    "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);

    /** The absorption Potion object. */
    public static final Potion absorption = (new PotionAbsorption(22, false, 2445989))
            .setPotionName("potion.absorption").setIconIndex(2, 2);

    /** The saturation Potion object. */
    public static final Potion saturation = (new PotionHealth(23, false, 16262179))
            .setPotionName("potion.saturation");
    public static final Potion field_180153_z = null;
    public static final Potion field_180147_A = null;
    public static final Potion field_180148_B = null;
    public static final Potion field_180149_C = null;
    public static final Potion field_180143_D = null;
    public static final Potion field_180144_E = null;
    public static final Potion field_180145_F = null;
    public static final Potion field_180146_G = null;

    /** The Id of a Potion object. */
    public final int id;
    private final Map<IAttribute, AttributeModifier> attributeModifierMap = new WeakHashMap<>();

    /**
     * This field indicated if the effect is 'bad' - negative - for the entity.
     */
    private final boolean isBadEffect;

    /** Is the color of the liquid for this potion. */
    private final int liquidColor;

    /** The name of the Potion. */
    private String name = "";

    /** The index for the icon displayed when the potion effect is active. */
    private int statusIconIndex = -1;
    private double effectiveness;
    private boolean usable;

    protected Potion(int potionID, boolean badEffect, int potionColor)
    {
        this.id = potionID;
        potionTypes[potionID] = this;
        this.isBadEffect = badEffect;

        if (badEffect)
        {
            this.effectiveness = 0.5D;
        }
        else
        {
            this.effectiveness = 1.0D;
        }

        this.liquidColor = potionColor;
    }

    /**
     * Sets the index for the icon displayed in the player's inventory when the status is active.
     */
    protected Potion setIconIndex(int p_76399_1_, int p_76399_2_)
    {
        this.statusIconIndex = p_76399_1_ + p_76399_2_ * 8;
        return this;
    }

    /**
     * returns the ID of the potion
     */
    public int getId()
    {
        return this.id;
    }

    public void performEffect(Emulator entityLivingBaseIn, int p_76394_2_)
    {
        /*if (this.id == regeneration.id)
        {
            if (entityLivingBaseIn.getHealth() < entityLivingBaseIn.getMaxHealth())
            {
                //entityLivingBaseIn.heal(1.0F);
            }
        }
        else if (this.id == poison.id)
        {
            if (entityLivingBaseIn.getHealth() > 1.0F)
            {
                entityLivingBaseIn.attackEntityFrom(DamageSource.magic, 1.0F);
            }
        }
        else if (this.id == wither.id)
        {
            entityLivingBaseIn.attackEntityFrom(DamageSource.wither, 1.0F);
        }
        else if (this.id == hunger.id && entityLivingBaseIn.getData().getPlayer() != null)
        {
            //((BntityPlayer)entityLivingBaseIn).addExhaustion(0.025F * (float)(p_76394_2_ + 1));
        }
        else if (this.id == saturation.id && entityLivingBaseIn.getData().getPlayer() != null)
        {
            //((EntityPlayer)entityLivingBaseIn).getFoodStats().addStats(p_76394_2_ + 1, 1.0F);
        }
        else if ((this.id != heal.id || entityLivingBaseIn.isEntityUndead()) && (this.id != harm.id || !entityLivingBaseIn.isEntityUndead()))
        {
            if (this.id == harm.id && !entityLivingBaseIn.isEntityUndead() || this.id == heal.id && entityLivingBaseIn.isEntityUndead())
            {
                entityLivingBaseIn.attackEntityFrom(DamageSource.magic, (float)(6 << p_76394_2_));
            }
        }
        else
        {
            //entityLivingBaseIn.heal((float)Math.max(4 << p_76394_2_, 0));
        }*/
    }

    public void affectEntity(Entity p_180793_1_, Entity p_180793_2_, Emulator entityLivingBaseIn, int p_180793_4_, double p_180793_5_)
    {
        /*if ((this.id != heal.id || entityLivingBaseIn.isEntityUndead()) && (this.id != harm.id || !entityLivingBaseIn.isEntityUndead()))
        {
            if (this.id == harm.id && !entityLivingBaseIn.isEntityUndead() || this.id == heal.id && entityLivingBaseIn.isEntityUndead())
            {
                int j = (int)(p_180793_5_ * (double)(6 << p_180793_4_) + 0.5D);

                if (p_180793_1_ == null)
                {
                    entityLivingBaseIn.attackEntityFrom(DamageSource.magic, (float)j);
                }
                else
                {
                    entityLivingBaseIn.attackEntityFrom(DamageSource.causeIndirectMagicDamage(p_180793_1_, p_180793_2_), (float)j);
                }
            }
        }
        else
        {
            int i = (int)(p_180793_5_ * (double)(4 << p_180793_4_) + 0.5D);
            //entityLivingBaseIn.heal((float)i);
        }*/
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant()
    {
        return false;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean isReady(int p_76397_1_, int p_76397_2_)
    {
        if (this.id == regeneration.id)
        {
            int k = 50 >> p_76397_2_;
            return k > 0 ? p_76397_1_ % k == 0 : true;
        }
        else if (this.id == poison.id)
        {
            int j = 25 >> p_76397_2_;
            return j > 0 ? p_76397_1_ % j == 0 : true;
        }
        else if (this.id == wither.id)
        {
            int i = 40 >> p_76397_2_;
            return i > 0 ? p_76397_1_ % i == 0 : true;
        }
        else
        {
            return this.id == hunger.id;
        }
    }

    /**
     * Set the potion name.
     */
    public Potion setPotionName(String nameIn)
    {
        this.name = nameIn;
        return this;
    }

    /**
     * returns the name of the potion
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns true if the potion has a associated status icon to display in then inventory when active.
     */
    public boolean hasStatusIcon()
    {
        return this.statusIconIndex >= 0;
    }

    /**
     * Returns the index for the icon to display when the potion is active.
     */
    public int getStatusIconIndex()
    {
        return this.statusIconIndex;
    }

    /**
     * This method returns true if the potion effect is bad - negative - for the entity.
     */
    public boolean isBadEffect()
    {
        return this.isBadEffect;
    }

    protected Potion setEffectiveness(double effectivenessIn)
    {
        this.effectiveness = effectivenessIn;
        return this;
    }

    public double getEffectiveness()
    {
        return this.effectiveness;
    }

    public boolean isUsable()
    {
        return this.usable;
    }

    /**
     * Returns the color of the potion liquid.
     */
    public int getLiquidColor()
    {
        return this.liquidColor;
    }

    /**
     * Used by potions to register the attribute they modify.
     */
    public Potion registerPotionAttributeModifier(IAttribute p_111184_1_, String p_111184_2_, double p_111184_3_, int p_111184_5_)
    {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(p_111184_2_), this.getName(), p_111184_3_, p_111184_5_);
        this.attributeModifierMap.put(p_111184_1_, attributemodifier);
        return this;
    }

    public Map<IAttribute, AttributeModifier> getAttributeModifierMap()
    {
        return this.attributeModifierMap;
    }

    public void removeAttributesModifiersFromEntity(Emulator entityLivingBaseIn, BaseAttributeMap p_111187_2_, int amplifier)
    {
        for (Map.Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet())
        {
            IAttributeInstance iattributeinstance = p_111187_2_.getAttributeInstance((IAttribute)entry.getKey());

            if (iattributeinstance != null)
            {
                iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    public void applyAttributesModifiersToEntity(Emulator entityLivingBaseIn, BaseAttributeMap p_111185_2_, int amplifier)
    {
        for (Map.Entry<IAttribute, AttributeModifier> entry : this.attributeModifierMap.entrySet())
        {
            IAttributeInstance iattributeinstance = p_111185_2_.getAttributeInstance((IAttribute)entry.getKey());

            if (iattributeinstance != null)
            {
                AttributeModifier attributemodifier = (AttributeModifier)entry.getValue();
                iattributeinstance.removeModifier(attributemodifier);
                iattributeinstance.applyModifier(new AttributeModifier(attributemodifier.getID(), this.getName() + " " + amplifier, this.getAttributeModifierAmount(amplifier, attributemodifier), attributemodifier.getOperation()));
            }
        }
    }

    public double getAttributeModifierAmount(int p_111183_1_, AttributeModifier modifier)
    {
        return modifier.getAmount() * (double)(p_111183_1_ + 1);
    }
}
