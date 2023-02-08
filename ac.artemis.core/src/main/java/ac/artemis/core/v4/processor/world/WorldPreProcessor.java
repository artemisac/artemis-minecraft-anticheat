package ac.artemis.core.v4.processor.world;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerBlockChange;

@Deprecated
public class WorldPreProcessor extends AbstractHandler {
    public WorldPreProcessor(PlayerData data) {
        super("World [0x01] @ Deprecated", data);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayServerBlockChange) {
            final GPacketPlayServerBlockChange pos = (GPacketPlayServerBlockChange) packet;

            /*data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    if (pos.getBlockId() == 0) {
                        data.entity.getGhostBlocks().stream()
                                .filter(e -> e.getLocation().getBlockX() == pos.getPosition().getX()
                                        && e.getLocation().getBlockY() == pos.getPosition().getY()
                                        && e.getLocation().getBlockZ() == pos.getPosition().getZ())
                                .findFirst()
                                .ifPresent(block -> data.entity.getGhostBlocks().remove(block));
                    }

                    /*data.world.addBlock(new ItemStack(Material.BEDROCK), new NaivePoint(
                            pos.getPosition().getX(),
                            pos.getPosition().getY(),
                            pos.getPosition().getZ())
                    );
                }
            });*/
        }
    }
}
