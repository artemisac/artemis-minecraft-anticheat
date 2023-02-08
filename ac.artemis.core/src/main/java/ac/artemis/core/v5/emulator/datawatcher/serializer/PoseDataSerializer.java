package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.pose.Pose;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class PoseDataSerializer extends AbstractDataSerializer<Pose> {
    public PoseDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Pose value) {
        buf.writeVarInt(value.ordinal());
    }

    @Override
    public Pose read(ProtocolByteBuf buf) {
        return Pose.values()[buf.readVarInt()];
    }

    @Override
    public Pose copyValue(Pose value) {
        return value;
    }
}
