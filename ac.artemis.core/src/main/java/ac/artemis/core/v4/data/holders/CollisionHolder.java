package ac.artemis.core.v4.data.holders;

import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.anticheat.api.material.NMSMaterial;
import ac.artemis.core.v4.data.PlayerData;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CollisionHolder extends AbstractHolder {
    public CollisionHolder(PlayerData data) {
        super(data);
    }

    /**
     * Represents whether the user has interacted with a boat previously and has indeed left the vehicle
     * properly. This method is to prevent falses with Motion and to not be used as a source of
     * potential bypasses/disablers
     */
    public boolean hasLeftVehicle;

    /**
     * Represents whether the user collides with a boat or not within a 1 block extra radius.
     * Useful for preventing false positives
     */
    public boolean collidesBoat;

    /**
     * Represents whether the user collides with an entity able to be pushed by such. 
     */
    public boolean collidesCollideable;

    public boolean groundCollide;

    public boolean wasGroundCollide;

    public Set<NMSMaterial> collidingBlocks0 = new HashSet<>();

    public Set<Material> collidingBlocks1 = new HashSet<>();

    public Set<Material> collidingBlocksY1 = new HashSet<>();
    public Set<NMSMaterial> collidingBlocksY1NMS = new HashSet<>();

    public Set<NMSMaterial> collidingMaterials = new HashSet<>();

    public Set<NMSMaterial> collidingMaterials1 = new HashSet<>();
}
