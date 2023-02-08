package ac.artemis.checks.regular.v2.checks.impl.scaffold;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.debug.Debug;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.block.Block;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.minecraft.Minecraft;
import ac.artemis.packet.minecraft.material.Material;
import ac.artemis.packet.minecraft.world.Location;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.nms.EnumDirection;
import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientFlying;

import java.util.Optional;

@Check(type = Type.SCAFFOLD)
public final class ScaffoldA extends ArtemisCheck implements PacketHandler, PacketExcludable {

    private int ticks;

    public ScaffoldA(final PlayerData data, final CheckInformation info) {
        super(data, info);

        this.setCompatiblePackets(GPacketPlayClientBlockPlace.class, GPacketPlayClientFlying.class);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientBlockPlace && this.ticks < 3) {
            final GPacketPlayClientBlockPlace wrapper = ((GPacketPlayClientBlockPlace) packet);
            final BlockPosition position = wrapper.getPosition();

            if (position.getX() == -1 && position.getZ() == -1) return;

            ++this.ticks;

            final Block block = data.entity.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
            if (block == null) return;

            final Optional<EnumDirection> optional = wrapper.getDirection();

            optional.ifPresent(direction -> {
                final Material type = block.getMaterial().getMaterial();

                if (type.isSolid() && type.isOccluding()) {
                    final double x = data.entity.getPosition().getX();
                    final double y = data.entity.getPosition().getY();
                    final double z = data.entity.getPosition().getZ();

                    if ((y - block.getLocation().getY()) > 1.0 - 1E-6) {
                        final Location eyeLocation = Minecraft.v().createLocation(data.getPlayer().getWorld(),
                                x, y + data.getPlayer().getEyeHeight(), z
                        );

                        if (!this.interactedCorrectly(block.getLocation(), eyeLocation, direction)) {
                            this.log(new Debug<>("face", direction));
                        }
                    }
                }
            });
        }

        else {
            this.ticks = 0;
        }
    }

    private boolean interactedCorrectly(final NaivePoint blockLoc, final Location playerLoc, final EnumDirection face) {
        switch (face) {
            case UP: {
                //final double limit = blockLoc.getY() + 0.03;
                return true;/*playerLoc.getY() > limit;*/
            }
            case DOWN: {
                final double limit = blockLoc.getY() - 0.03;
                return playerLoc.getY() < limit;
            }
            case WEST: {
                final double limit = blockLoc.getX() + 0.03;
                return limit > playerLoc.getX();
            }
            case EAST: {
                final double limit = blockLoc.getX() + 1 - 0.03;
                return playerLoc.getX() > limit;
            }
            case NORTH: {
                final double limit = blockLoc.getZ() + 0.03;
                return playerLoc.getZ() < limit;
            }
            case SOUTH: {
                final double limit = blockLoc.getZ() + 1 - 0.03;
                return playerLoc.getZ() > limit;
            }

            default: return true;
        }
    }
}
