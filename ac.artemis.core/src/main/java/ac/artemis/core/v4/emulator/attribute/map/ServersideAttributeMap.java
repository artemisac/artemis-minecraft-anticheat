package ac.artemis.core.v4.emulator.attribute.map;

import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.ModifiableAttributeInstance;
import ac.artemis.core.v4.emulator.attribute.RangedAttribute;
import ac.artemis.core.v4.emulator.attribute.map.BaseAttributeMap;
import ac.artemis.core.v4.utils.lists.LowerStringMap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ServersideAttributeMap extends BaseAttributeMap
{
    private final Set<IAttributeInstance> attributeInstanceSet = Sets.newHashSet();
    protected final Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap();

    public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute)
    {
        return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
    }

    public ModifiableAttributeInstance getAttributeInstanceByName(String attributeName)
    {
        IAttributeInstance iattributeinstance = super.getAttributeInstanceByName(attributeName);

        if (iattributeinstance == null)
        {
            iattributeinstance = this.descriptionToAttributeInstanceMap.get(attributeName);
        }

        return (ModifiableAttributeInstance)iattributeinstance;
    }

    /**
     * Registers an attribute with this AttributeMap, returns a modifiable AttributeInstance associated with this map
     */
    public IAttributeInstance registerAttribute(IAttribute attribute) {
        IAttributeInstance iattributeinstance = super.registerAttribute(attribute);

        if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null) {
            this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), iattributeinstance);
        }

        return iattributeinstance;
    }

    protected IAttributeInstance newAttribute(IAttribute p_180376_1_) {
        return new ModifiableAttributeInstance(this, p_180376_1_);
    }

    public void func_180794_a(IAttributeInstance p_180794_1_)
    {
        if (p_180794_1_.getAttribute().getShouldWatch())
        {
            this.attributeInstanceSet.add(p_180794_1_);
        }

        for (IAttribute iattribute : this.field_180377_c.get(p_180794_1_.getAttribute()))
        {
            ModifiableAttributeInstance modifiableattributeinstance = this.getAttributeInstance(iattribute);

            if (modifiableattributeinstance != null)
            {
                modifiableattributeinstance.flagForUpdate();
            }
        }
    }

    public Set<IAttributeInstance> getAttributeInstanceSet()
    {
        return this.attributeInstanceSet;
    }

    public Collection<IAttributeInstance> getWatchedAttributes()
    {
        Set<IAttributeInstance> set = Sets.<IAttributeInstance>newHashSet();

        for (IAttributeInstance iattributeinstance : this.getAllAttributes())
        {
            if (iattributeinstance.getAttribute().getShouldWatch())
            {
                set.add(iattributeinstance);
            }
        }

        return set;
    }
}
