package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.moderna.Direction;
import ac.artemis.core.v5.emulator.pose.Pose;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class DirectionDataSerializer extends AbstractDataSerializer<Direction> {
    public DirectionDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Direction value) {
        buf.writeVarInt(value.ordinal());
    }

    @Override
    public Direction read(ProtocolByteBuf buf) {
        return Direction.values()[buf.readVarInt()];
    }

    @Override
    public Direction copyValue(Direction value) {
        return value;
    }
}
