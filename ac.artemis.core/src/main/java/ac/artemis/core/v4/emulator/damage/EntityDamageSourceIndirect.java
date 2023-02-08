package ac.artemis.core.v4.emulator.damage;

import ac.artemis.packet.minecraft.entity.Entity;

public class EntityDamageSourceIndirect extends EntityDamageSource {
    private final Entity indirectEntity;

    public EntityDamageSourceIndirect(String p_i1568_1_, Entity p_i1568_2_, Entity indirectEntityIn) {
        super(p_i1568_1_, p_i1568_2_);
        this.indirectEntity = indirectEntityIn;
    }

    public Entity getSourceOfDamage()
    {
        return this.damageSourceEntity;
    }

    public Entity getEntity()
    {
        return this.indirectEntity;
    }


}
