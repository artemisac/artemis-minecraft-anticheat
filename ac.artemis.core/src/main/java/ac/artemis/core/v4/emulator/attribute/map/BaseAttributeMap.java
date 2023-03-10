package ac.artemis.core.v4.emulator.attribute.map;

import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.utils.lists.LowerStringMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseAttributeMap
{
    protected final Map<IAttribute, IAttributeInstance> attributes = new HashMap<>();
    protected final Map<String, IAttributeInstance> attributesByName = new LowerStringMap();
    protected final Multimap<IAttribute, IAttribute> field_180377_c = HashMultimap.<IAttribute, IAttribute>create();

    public IAttributeInstance getAttributeInstance(IAttribute attribute)
    {
        return (IAttributeInstance) this.attributes.get(attribute);
    }

    public IAttributeInstance getAttributeInstanceByName(String attributeName)
    {
        return (IAttributeInstance)this.attributesByName.get(attributeName);
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute attribute)
    {
        if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName()))
        {
            throw new IllegalArgumentException("Attribute is already registered!");
        }
        else
        {
            IAttributeInstance iattributeinstance = this.newAttribute(attribute);
            this.attributesByName.put(attribute.getAttributeUnlocalizedName(), iattributeinstance);
            this.attributes.put(attribute, iattributeinstance);

            for (IAttribute iattribute = attribute.func_180372_d(); iattribute != null; iattribute = iattribute.func_180372_d())
            {
                this.field_180377_c.put(iattribute, attribute);
            }

            return iattributeinstance;
        }
    }

    protected abstract IAttributeInstance newAttribute(IAttribute iattribute);

    public Collection<IAttributeInstance> getAllAttributes()
    {
        return this.attributesByName.values();
    }

    public void func_180794_a(IAttributeInstance p_180794_1_)
    {
    }

    public void removeAttributeModifiers(Multimap<String, AttributeModifier> p_111148_1_)
    {
        for (Entry<String, AttributeModifier> entry : p_111148_1_.entries())
        {
            IAttributeInstance iattributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (iattributeinstance != null)
            {
                iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    public void applyAttributeModifiers(Multimap<String, AttributeModifier> p_111147_1_)
    {
        for (Entry<String, AttributeModifier> entry : p_111147_1_.entries())
        {
            IAttributeInstance iattributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (iattributeinstance != null)
            {
                iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
                iattributeinstance.applyModifier((AttributeModifier)entry.getValue());
            }
        }
    }
}
