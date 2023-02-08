package ac.artemis.core.v5.utils;

import ac.artemis.packet.minecraft.EnchantType;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v5.utils.block.BlockUtil;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EntityUtil {
    public int getDepthStrider(final Player player){
        if (ServerUtil.getGameVersion().isBelow(ProtocolVersion.V1_8))
            return 0;
        if (player.getInventory() == null)
            return 0;
        if (player.getInventory().getBoots() == null)
            return 0;
        if (!player.getInventory().getBoots().hasEnchant(EnchantType.DEPTH_STRIDER))
            return 0;

        return player.getInventory().getBoots().getEnchantLevel(EnchantType.DEPTH_STRIDER);
    }

    public boolean isAreaLoaded(final World world, final NaivePoint min, final NaivePoint max){
        return isAreaLoaded(world, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public boolean isAreaLoaded(final World world, int xStart, final int yStart, int zStart, int xEnd, final int yEnd, int zEnd) {
        if (yEnd >= 0 && yStart < 256) {
            xStart = xStart >> 4;
            zStart = zStart >> 4;
            xEnd = xEnd >> 4;
            zEnd = zEnd >> 4;

            for (int i = xStart; i <= xEnd; ++i) {
                for (int j = zStart; j <= zEnd; ++j) {
                    if (!BlockUtil.isLoadedShifted(world, i, j)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
