package cc.ghast.packet.wrapper.packet.play.server;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.protocol.PacketLink;
import ac.artemis.packet.wrapper.server.PacketPlayServerPosition;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import ac.artemis.packet.spigot.wrappers.GPacket;
import cc.ghast.packet.wrapper.packet.ReadableBuffer;
import cc.ghast.packet.wrapper.packet.WriteableBuffer;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@PacketLink(PacketPlayServerPosition.class)
public class GPacketPlayServerPosition extends GPacket implements PacketPlayServerPosition, ReadableBuffer, WriteableBuffer {
    public GPacketPlayServerPosition(UUID player, ProtocolVersion version) {
        super("PacketPlayOutPosition", player, version);
    }

    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Set<PlayerTeleportFlags> flags;
    private Optional<Integer> confirmId;

    @Override
    public void read(ProtocolByteBuf byteBuf) {
        this.x = byteBuf.readDouble();
        this.y = byteBuf.readDouble();
        this.z = byteBuf.readDouble();
        this.yaw = byteBuf.readFloat();
        this.pitch = byteBuf.readFloat();
        this.flags = PlayerTeleportFlags.readFlags(byteBuf.readUnsignedByte());
    }

    @Override
    public void write(ProtocolByteBuf byteBuf) {
        byteBuf.writeDouble(x);
        byteBuf.writeDouble(y);
        byteBuf.writeDouble(z);
        byteBuf.writeFloat(yaw);
        byteBuf.writeFloat(pitch);
        byteBuf.writeByte(PlayerTeleportFlags.writeFlags(flags));
    }

    @Getter
    public enum PlayerTeleportFlags {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int bit;

        PlayerTeleportFlags(int bit)
        {
            this.bit = bit;
        }

        private int operand()
        {
            return 1 << this.bit;
        }

        private boolean matches(int flag) {
            return (flag & this.operand()) == this.operand();
        }

        public static Set<PlayerTeleportFlags> readFlags(int flag) {
            final Set<PlayerTeleportFlags> set = EnumSet.noneOf(PlayerTeleportFlags.class);

            for (PlayerTeleportFlags enumFlag : values()) {
                if (enumFlag.matches(flag)) {
                    set.add(enumFlag);
                }
            }

            return set;
        }

        public static int writeFlags(Set<PlayerTeleportFlags> var0) {
            int var1 = 0;

            PlayerTeleportFlags var3;
            for(Iterator<PlayerTeleportFlags> var2 = var0.iterator(); var2.hasNext(); var1 |= var3.operand()) {
                var3 = var2.next();
            }

            return var1;
        }
    }
}
