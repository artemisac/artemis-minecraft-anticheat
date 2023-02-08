package ac.artemis.core.v5.emulator.block;


import ac.artemis.core.v5.emulator.TransitionData;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import lombok.Data;
import ac.artemis.core.v5.emulator.attributes.Attribute;
import ac.artemis.core.v5.emulator.attributes.AttributeKey;
import ac.artemis.core.v5.emulator.attributes.StandardAttribute;
import ac.artemis.core.v5.emulator.world.ArtemisWorld;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v5.utils.bounding.EnumFacing;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ghast
 * @since 12/02/2021
 * Artemis © 2021
 */

@Data
public class Block implements ac.artemis.core.v5.emulator.block.BlockProvider, ac.artemis.core.v5.emulator.block.BukkitData, CollisionLandable {
    private final NMSMaterial material;
    protected NaivePoint location;
    protected EnumFacing direction;
    private final Map<AttributeKey, Attribute<?>> data = new HashMap<>();

    public Block(final NMSMaterial material, final NaivePoint location, final EnumFacing direction) {
        this.material = material;
        this.location = location;
        this.direction = direction;
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public List<BoundingBox> getBoundingBox(final ArtemisWorld world) {
        return Collections.singletonList(getFromPoint(location, 0.F,0.F, 0.F, 1.D, 1.D, 1.D));
    }

    @Override
    public void onLanded(final TransitionData emulator) {
        emulator.setMotionY(0.0D);
    }

    @Override
    public <T> void addAttribute(final AttributeKey key, final Attribute<T> attribute) {
        this.data.put(key, attribute);
    }

    @Override
    public <T> void removeAttribute(final AttributeKey attributeKey) {
        this.data.remove(attributeKey);
    }

    @Override
    public <T> Attribute<T> getAttribute(final AttributeKey attributeKey) {
        if (data.containsKey(attributeKey)) {
            return (Attribute<T>) this.data.get(attributeKey);
        } else {
            final Attribute<T> value = new StandardAttribute<>(null);
            this.data.put(attributeKey, value);
            return value;
        }
    }

    @Override
    public void readData(final int data) {

    }

    @Override
    public int writeData() {
        return 0;
    }

    public static BoundingBox getFromPoint(final NaivePoint point, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        return new BoundingBox(point.getX() + f, point.getY() + f1, point.getZ() + f2,
                point.getX() + f3, point.getY() + f4, point.getZ() + f5);
    }

    public static BoundingBox getFromPoint(final NaivePoint point, final double f, final double f1, final double f2, final double f3, final double f4, final double f5) {
        return new BoundingBox(point.getX() + f, point.getY() + f1, point.getZ() + f2,
                point.getX() + f3, point.getY() + f4, point.getZ() + f5);
    }

    @Override
    public String toString() {
        return "Block{" +
                "material=" + material +
                '}';
    }
}
