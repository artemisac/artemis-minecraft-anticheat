package ac.artemis.core.v4.utils.position;

import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.minecraft.world.World;
import lombok.Getter;

/**
 * @author Ghast
 * @since 15-Mar-20
 */
@Getter
public class SimplePosition {
    public double x, y, z;
    public long timestamp = System.currentTimeMillis();

    public SimplePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location toLocation(World world) {
        return Minecraft.v().createLocation(world, this.x, this.y, this.z);
    }

    public Location toLocation(Player p) {
        return Minecraft.v().createLocation(p.getWorld(), this.x, this.y, this.z);
    }

    public SimplePosition clone() {
        return new SimplePosition(x, y, z);
    }
}
