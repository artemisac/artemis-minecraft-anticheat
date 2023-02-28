package cc.ghast.packet.buffer.types.exclusive;

import ac.artemis.packet.protocol.ProtocolVersion;
import cc.ghast.packet.buffer.BufConverter;
import cc.ghast.packet.buffer.types.Converters;
import cc.ghast.packet.wrapper.codec.StringPool;
import cc.ghast.packet.wrapper.netty.MutableByteBuf;

import java.nio.charset.StandardCharsets;


public class StringPoolConverter extends BufConverter<StringPool> {
    public StringPoolConverter() {
        super("StringPool", StringPool.class);
    }

    @Override
    public void write(MutableByteBuf buffer, StringPool value) {
        byte[] abyte = value.getData().getBytes(StandardCharsets.UTF_8);
        if (abyte.length > 32767) {
            throw new IllegalStateException("String too big (was " + value.getData().length() + " bytes encoded, max " + 32767 + ")");
        } else {
            Converters.VAR_INT.write(buffer, abyte.length);
            buffer.writeBytes(abyte);
        }
    }

    @Override
    public StringPool read(MutableByteBuf buffer, ProtocolVersion version, Object... args) {
        if (args.length < 1) throw new IllegalStateException("The received string is supposed to have getX size");
        int max = (int) args[0];
        int length = Converters.VAR_INT.read(buffer, version);
        if (length > max * 4) {
            throw new IllegalStateException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + max * 4 + ")");
        }

        if (length < 0) {
            throw new IllegalStateException("The received encoded string buffer length is less than zero! Weird string!");
        }

        String s;

        if (version.isOrAbove(ProtocolVersion.V1_12)) {
            s = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
            buffer.readerIndex(buffer.readerIndex() + length);
        } else {
            s = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
            buffer.readerIndex(buffer.readerIndex() + length);
            //s = new String(buffer.readBytes(length), StandardCharsets.UTF_8);
        }

        if (s.length() > max) {
            throw new IllegalStateException("The received string length is longer than maximum allowed (" + length + " > " + max + ")");
        }

        return new StringPool(s, length);
    }
}
