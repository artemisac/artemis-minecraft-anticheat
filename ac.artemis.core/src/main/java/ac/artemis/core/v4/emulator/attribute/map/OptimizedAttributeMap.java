package ac.artemis.core.v4.emulator.attribute.map;

import ac.artemis.core.v4.emulator.attribute.AttributeModifier;
import ac.artemis.core.v4.emulator.attribute.IAttribute;
import ac.artemis.core.v4.emulator.attribute.IAttributeInstance;
import ac.artemis.core.v4.utils.lists.LowerStringMap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

/**
 * @author Ghast
 * @since 30/11/2020
 * Artemis © 2020
 */
public class OptimizedAttributeMap extends BaseAttributeMap {
    protected final Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap<>();

    @Override
    protected IAttributeInstance newAttribute(IAttribute iattribute) {
        return null;
    }

    @Override
    public IAttributeInstance getAttributeInstance(IAttribute attribute) {
        return super.getAttributeInstance(attribute);
    }

    @Override
    public IAttributeInstance getAttributeInstanceByName(String attributeName) {
        return super.getAttributeInstanceByName(attributeName);
    }

    @Override
    public IAttributeInstance registerAttribute(IAttribute attribute) {
        return super.registerAttribute(attribute);
    }

    @Override
    public Collection<IAttributeInstance> getAllAttributes() {
        return super.getAllAttributes();
    }

    @Override
    public void func_180794_a(IAttributeInstance p_180794_1_) {
        super.func_180794_a(p_180794_1_);
    }

    @Override
    public void removeAttributeModifiers(Multimap<String, AttributeModifier> p_111148_1_) {
        super.removeAttributeModifiers(p_111148_1_);
    }

    @Override
    public void applyAttributeModifiers(Multimap<String, AttributeModifier> p_111147_1_) {
        super.applyAttributeModifiers(p_111147_1_);
    }
}
