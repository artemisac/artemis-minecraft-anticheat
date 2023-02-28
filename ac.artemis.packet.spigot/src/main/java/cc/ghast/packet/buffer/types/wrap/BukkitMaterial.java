package cc.ghast.packet.buffer.types.wrap;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.material.Material;

public class BukkitMaterial extends AbstractWrapper<org.bukkit.Material> implements Material {
    public BukkitMaterial(org.bukkit.Material wrapper) {
        super(wrapper);
    }

    @Override
    public String name() {
        return wrapper.name();
    }

    @Override
    public boolean isBlock() {
        return wrapper.isBlock();
    }

    @Override
    public boolean isOccluding() {
        return wrapper.isOccluding();
    }

    @Override
    public int getMaxDurability() {
        return wrapper.getMaxDurability();
    }

    @Override
    public boolean isLegacy() {
        return false;
    }

    @Override
    public boolean isAir() {
        return wrapper == org.bukkit.Material.AIR;
    }

    @Override
    public boolean isEdible() {
        return wrapper.isEdible();
    }

    @Override
    public boolean isSolid() {
        return wrapper.isSolid();
    }

    @Override
    public int getId() {
        return wrapper.getId();
    }
}
