package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class StringDataSerializer extends AbstractDataSerializer<String> {
    public StringDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, String value) {
        buf.writeString(value);
    }

    @Override
    public String read(ProtocolByteBuf buf) {
        return buf.readStringBuf(32767);
    }

    @Override
    public String copyValue(String value) {
        return value;
    }
}
