package ac.artemis.checks.regular.v2.checks.impl.aura;

import ac.artemis.anticheat.api.check.type.Type;
import ac.artemis.core.v4.check.ArtemisCheck;
import ac.artemis.core.v4.check.PacketHandler;
import ac.artemis.core.v4.check.annotations.Check;
import ac.artemis.core.v4.check.annotations.Experimental;
import ac.artemis.core.v4.check.annotations.NMS;
import ac.artemis.core.v4.check.settings.CheckInformation;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.nms.NMSManager;
import ac.artemis.core.v5.utils.bounding.Vec3d;
import ac.artemis.core.v5.utils.raytrace.Point;
import cc.ghast.packet.wrapper.mc.PlayerEnums;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientBlockPlace;
import ac.artemis.packet.wrapper.client.*;
import cc.ghast.packet.wrapper.packet.play.client.GPacketPlayClientUseEntity;

@Check(type = Type.AURA, var = "NMS")
@NMS
@Experimental
public class AuraNMS extends ArtemisCheck implements PacketHandler {
    private boolean attack = false;
    private boolean interact = false;

    private int moves = 0, looks = 0, lastLooks = 0, lastLooksDelta = 0, row = 0, streak = 0;

    public AuraNMS(final PlayerData data, final CheckInformation info) {
        super(data, info);
    }

    @Override
    public void handle(final GPacket packet) {
        if (packet instanceof GPacketPlayClientUseEntity) {
            final GPacketPlayClientUseEntity wrapper = (GPacketPlayClientUseEntity) packet;

            if (wrapper.getType() == PlayerEnums.UseType.ATTACK) {
                this.attack = true;
            } else {
                this.interact = true;
            }

            if (attack) {
                final Vec3d motion = NMSManager.getInms().getMotion(data.getPlayer());
                final int delta = Math.abs(looks - lastLooks);

                final boolean invalidFirst = moves > 20 && looks > 2;
                final boolean invalidSecond = delta == lastLooksDelta && delta < 2;
                final boolean invalidThird = delta == lastLooksDelta && delta == 0;
                final boolean invalidFourth = moves > 1 && motion.getX() == 0.0 && motion.getZ() == 0.0;

                if (invalidFirst) {
                    this.log("action= 3");
                }

                if (invalidSecond) {
                    final boolean proper = !isTickFormatted(++row);

                    if (proper) {
                        this.log("action= 4," + " r=" + row);

                        this.row = 10;
                    }
                } else {
                    this.row = 0;
                }

                if (invalidThird) {
                    final boolean flag = ++streak > 3;

                    if (flag) {
                        this.log("action= 5," + " s=" + streak);
                    }
                } else {
                    streak = 0;
                }

                if (invalidFourth) {
                    // Todo Fix this false positive
                    //this.log("action= 5");
                }

                this.lastLooksDelta = delta;
                this.lastLooks = looks;
            }
        } else if (packet instanceof GPacketPlayClientBlockPlace) {
            final boolean invalid = attack && !interact;

            if (invalid) {
                this.log("action= 1");
            }
        } else if (packet instanceof PacketPlayClientFlying) {
            final PacketPlayClientFlying wrapper = (PacketPlayClientFlying) packet;

            if (wrapper.isLook()) {
                ++looks;
            }

            if (++moves > 20) {
                final boolean invalid = attack && looks > 1;

                if (invalid) {
                    // Todo Fix this false positive
                    //this.log("action= 2");
                }

                this.moves = looks = 0;
            }

            this.attack = interact = false;
        }
    }

    private boolean isTickFormatted(final int tick) {
        return tick <= 20;
    }
}
