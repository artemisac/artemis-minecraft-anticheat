package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.moderna.ITextComponent;
import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.util.Optional;

public class OptionalTextComponentDataSerializer extends AbstractDataSerializer<Optional<ITextComponent>> {
    public OptionalTextComponentDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, Optional<ITextComponent> value) {
        if (value.isPresent()) {
            buf.writeBoolean(true);
            buf.writeString(ITextComponent.Serializer.componentToJson(value.get()));
        } else {
            buf.writeBoolean(false);
        }

    }

    @Override
    public Optional<ITextComponent> read(ProtocolByteBuf buf) {
        return buf.readBoolean()
                ? Optional.of(ITextComponent.Serializer.jsonToComponent(buf.readStringBuf(32767)))
                : Optional.empty();
    }

    @Override
    public Optional<ITextComponent> copyValue(Optional<ITextComponent> value) {
        return value;
    }
}
