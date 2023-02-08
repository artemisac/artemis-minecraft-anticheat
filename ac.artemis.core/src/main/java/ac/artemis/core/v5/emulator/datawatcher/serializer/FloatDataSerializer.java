package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class FloatDataSerializer extends AbstractDataSerializer<Float> {
    public FloatDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Float value) {
        buf.writeFloat(value);
    }

    @Override
    public Float read(ProtocolByteBuf buf) {
        return buf.readFloat();
    }

    @Override
    public Float copyValue(Float value) {
        return value;
    }
}
