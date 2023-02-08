package ac.artemis.core.v4.processor.miscellaneous;

import ac.artemis.core.v4.check.FastProcessHandler;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.data.holders.ConnectionHolder;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.utils.function.PacketAction;
import cc.ghast.packet.PacketAPI;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientClientCommand;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientWindowClose;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientHeldItemSlot;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientTransaction;
import cc.ghast.packet.wrapper.packet.play.server.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class MiscellaneousPreProcessor extends AbstractHandler implements FastProcessHandler {

    public MiscellaneousPreProcessor(PlayerData data) {
        super("Misc [0x01]", data);
    }

    @Override
    public void handle(final GPacket packet) {
        long now = System.currentTimeMillis();
        if (packet instanceof GPacketPlayClientHeldItemSlot) {
            data.user.usingItem = false;
        }

        else if (packet instanceof GPacketPlayClientClientCommand) {
            final GPacketPlayClientClientCommand cmd = (GPacketPlayClientClientCommand) packet;
            if (cmd.getCommand().equals(PlayerEnums.ClientCommand.PERFORM_RESPAWN)) {
                data.user.setLastRespawn(packet.getTimestamp());
                data.prediction.setVehicle(null);
            }
        }

        else if (packet instanceof GPacketPlayClientTransaction) {
            final GPacketPlayClientTransaction trx = (GPacketPlayClientTransaction) packet;
            if (!trx.isAccepted()) {
                //data.user.setInventoryOpen(true);
            }
        }

        else if (packet instanceof GPacketPlayClientWindowClose) {
            data.user.setInventoryOpen(false);
        }



        /*else if (packet instanceof GPacketPlayServerEntityTeleport) {
            final GPacketPlayServerEntityTeleport tp = (GPacketPlayServerEntityTeleport) packet;

            if (data.prediction.getVehicle() != null &&
                    (tp.getEntityId() == data.getPlayer().getEntityId() || tp.getEntityId() == data.prediction.getVehicle().getEntityId())) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.setVehicle(null);
                    }
                });
            }
        }*/

        /*else if (packet instanceof GPacketPlayServerPosition) {
            if (data.prediction.getVehicle() != null) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.setVehicle(null);
                    }
                });
            }
        }*/

        data.timing.miscTiming.addTime(now, System.currentTimeMillis());
    }

    @Override
    public void fastHandle(GPacket packet) {
        if (packet instanceof GPacketPlayServerRespawn) {
            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    data.user.setLastRespawn(packet.getTimestamp());
                    data.user.setInventoryOpen(false);
                    data.prediction.setVehicle(null);
                }
            });
        }

        else if (packet instanceof GPacketPlayServerWindowClose) {
            data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                @Override
                public void pre() {
                    data.user.setInventoryOpen(false);
                }
            });
        }

        else if (packet instanceof GPacketPlayServerEntityAttach) {
            final GPacketPlayServerEntityAttach attach = (GPacketPlayServerEntityAttach) packet;

            if (attach.getEntityId() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunctionAndTick(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.setVehicle(NMSManager.getInms().getEntity(data.getPlayer().getWorld(), attach.getVehicleId()));
                        if (data.prediction.getVehicle() == null) {
                            data.resetMotion();
                        }
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerEntityDestroy) {
            final GPacketPlayServerEntityDestroy dtr = (GPacketPlayServerEntityDestroy) packet;

            final boolean flag = data.prediction.getVehicle() != null
                    && Arrays.stream(dtr.getEntities()).anyMatch(e -> data.prediction.getVehicle().getEntityId() == e);
            if (flag) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.setVehicle(null);
                        data.resetMotion();
                    }
                });
            }
        }

        else if (packet instanceof GPacketPlayServerBed) {
            final GPacketPlayServerBed bed = (GPacketPlayServerBed) packet;

            if (bed.getPlayerID() == data.getPlayer().getEntityId()) {
                data.connection.confirmFunction(ConnectionHolder.ConfirmationType.TRANSACTION, new PacketAction() {
                    @Override
                    public void pre() {
                        data.prediction.setVehicle(null);
                        data.resetMotion();
                    }
                });
            }
        }
    }
}
