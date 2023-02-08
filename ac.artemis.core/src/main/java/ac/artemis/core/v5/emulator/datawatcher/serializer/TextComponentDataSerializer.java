package ac.artemis.core.v5.emulator.datawatcher.serializer;

import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.emulator.moderna.ITextComponent;
import cc.ghast.packet.buffer.ProtocolByteBuf;

public class TextComponentDataSerializer extends AbstractDataSerializer<ITextComponent> {
    public TextComponentDataSerializer(PlayerData data) {
        super(data);
    }

    @Override
    public void write(ProtocolByteBuf buf, ITextComponent value) {
        buf.writeString(ITextComponent.Serializer.componentToJson(value));
    }

    @Override
    public ITextComponent read(ProtocolByteBuf buf) {
        return ITextComponent.Serializer.jsonToComponent(buf.readStringBuf(32767));
    }

    @Override
    public ITextComponent copyValue(ITextComponent value) {
        return value.createCopy();
    }
}
