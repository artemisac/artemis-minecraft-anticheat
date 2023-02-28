package cc.ghast.packet.buffer.types.java;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.exceptions.InvalidByteBufStructureException;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author Myles
 * @since 01-May-20
 */
public class StringConverter extends BufConverter<String> {

    public StringConverter() {
        super("String", String.class);
    }

    private static final int maxJavaCharUtf8Length = Character.toString(Character.MAX_VALUE)
            .getBytes(StandardCharsets.UTF_8).length;

    @Override
    public void write(MutableByteBuf buffer, String value) {
        if (value.length() >= Short.MAX_VALUE) {
            throw new InvalidByteBufStructureException(String.format("Cannot send string longer than Short.MAX_VALUE (got %s characters)", value.length()));
        }

        byte[] b = value.getBytes(StandardCharsets.UTF_8);
        Converters.VAR_INT.write(buffer, b.length);
        buffer.writeBytes(b);
    }

    @Override
    public String read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        int len = Converters.VAR_INT.read(buffer, version);

        if (len >= Short.MAX_VALUE * maxJavaCharUtf8Length) {
            throw new InvalidByteBufStructureException(String.format("Cannot receive string longer than Short.MAX_VALUE * " + maxJavaCharUtf8Length + " bytes (got %s bytes)", len));
        }

        String string = buffer.toString(buffer.readerIndex(), len, StandardCharsets.UTF_8);
        buffer.skipBytes(len);

        if (string.length() >= Short.MAX_VALUE) {
            throw new InvalidByteBufStructureException(String.format("Cannot receive string longer than Short.MAX_VALUE characters (got %s bytes)", string.length()));
        }

        return string;
    }
}
