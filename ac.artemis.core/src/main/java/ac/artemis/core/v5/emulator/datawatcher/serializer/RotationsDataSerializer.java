package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v5.emulator.utils.Rotations;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class RotationsDataSerializer extends AbstractDataSerializer<Rotations> {
    public RotationsDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Rotations value) {
        buf.writeFloat(value.getX());
        buf.writeFloat(value.getY());
        buf.writeFloat(value.getZ());
    }

    @Override
    public Rotations read(ProtocolByteBuf buf) {
        return new Rotations(buf.readFloat(), buf.readFloat(), buf.readFloat());
    }

    @Override
    public Rotations copyValue(Rotations value) {
        return value;
    }
}
