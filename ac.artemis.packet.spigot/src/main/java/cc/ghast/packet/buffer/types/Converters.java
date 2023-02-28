package cc.ghast.packet.buffer.types;

import cc.ghast.packet.buffer.types.exclusive.*;
import cc.ghast.packet.buffer.types.java.*;
import cc.ghast.packet.buffer.types.minecraft.*;

/**
 * @author Ghast
 * @since 09-May-20
 */
public class Converters {
    public static ByteConverter BYTE;
    public static BytesConverter BYTES;
    public static DoubleConverter DOUBLE;
    public static IntegerConverter INTEGER;
    public static LongConverter LONG;
    public static StringConverter STRING;

    public static VarIntConverter VAR_INT;
    public static VarLongConverter VAR_LONG;
    public static BytePoolConverter BYTE_POOL;
    public static StringPoolConverter STRING_POOL;

    public static UUIDConverter UUID;

    public static NBTCompoundConverter NBT;
    public static NMSCompoundTagConverter NMS_NBT;
    public static ItemConverter ITEM;
    public static ItemStackConverter ITEM_STACK;

    public static LongLocationConverter LOCATION_LONG;

    static {
        try {
            BYTE = new ByteConverter();
            BYTES = new BytesConverter();
            DOUBLE = new DoubleConverter();
            INTEGER = new IntegerConverter();
            LONG = new LongConverter();
            STRING = new StringConverter();
            VAR_INT = new VarIntConverter();
            VAR_LONG = new VarLongConverter();
            BYTE_POOL = new BytePoolConverter();
            STRING_POOL = new StringPoolConverter();
            UUID = new UUIDConverter();
            NBT = new NBTCompoundConverter();
            NMS_NBT = new NMSCompoundTagConverter();
            ITEM = new ItemConverter();
            ITEM_STACK = new ItemStackConverter();
            LOCATION_LONG = new LongLocationConverter();
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
