package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class VarIntDataSerializer extends AbstractDataSerializer<Integer> {
    public VarIntDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Integer value) {
        buf.writeVarInt(value);
    }

    @Override
    public Integer read(ProtocolByteBuf buf) {
        return buf.readVarInt();
    }

    @Override
    public Integer copyValue(Integer value) {
        return value;
    }
}
