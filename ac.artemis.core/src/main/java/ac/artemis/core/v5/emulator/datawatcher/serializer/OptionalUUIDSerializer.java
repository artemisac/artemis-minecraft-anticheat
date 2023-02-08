package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.util.Optional;
import java.util.UUID;

public class OptionalUUIDSerializer extends AbstractDataSerializer<Optional<UUID>> {
    public OptionalUUIDSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Optional<UUID> value) {
        if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeUUID(value.get());
        } else {
            buf.writeBoolean(false);
        }
    }

    @Override
    public Optional<UUID> read(ProtocolByteBuf buf) {
        return buf.readBoolean() ? Optional.of(buf.readUUID()) : Optional.empty();
    }

    @Override
    public Optional<UUID> copyValue(Optional<UUID> value) {
        return value;
    }
}
