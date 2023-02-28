package cc.ghast.packet.reflections;

import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.spigot.utils.ServerUtil;
import cc.ghast.packet.protocol.EnumProtocol;
import ac.artemis.packet.protocol.ProtocolDirection;
import cc.ghast.packet.wrapper.nbt.WrappedItem;
import cc.ghast.packet.wrapper.netty.MutableByteBufOutputStream;
import cc.ghast.packet.wrapper.netty.input.NettyUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataOutput;
import java.net.SocketAddress;
import java.util.*;

/**
 * @author Ghast
 * @since 17/08/2020
 * Artemis © 2020
 */

public class ReflectUtil {

    /**
     * Util designated to help with some getX object stuff. Jeez 1.7 is getX pain :/
     */
    private static NettyUtil NETTY_UTIL;

    /*
        Minecraft Server field
     */
    public static final Class<?> MINECRAFT_SERVER_CLAZZ = Reflection.getMinecraftClass("MinecraftServer");
    public static Class<?> CRAFT_SERVER_CLAZZ;
    public static FieldAccessor<?> MINECRAFT_SERVER_FIELD;
    public static Object MINECRAFT_SERVER;

    /*
        Minecraft Connection Field
     */

    public static Class<?> SERVER_CONNECTION_CLAZZ;
    public static FieldAccessor<?> SERVER_CONNECTION_FIELD;
    public static Object SERVER_CONNECTION;
    public static FieldAccessor<List> CHANNEL_FUTURES_FIELD;

    public static Object getChannelFuture() {
        return CHANNEL_FUTURES_FIELD.get(SERVER_CONNECTION).get(0);
    }

    /*
        Minecraft Manager Field
     */
    private static Class<?> NETWORK_MANAGER_CLAZZ;
    private static FieldAccessor<List> NETWORK_MANAGERS_FIELD;
    private static FieldAccessor<?> CHANNEL_FIELD;
    /*
        Socket Field
     */
    private static FieldAccessor<SocketAddress> ADDRESS_FIELD;


    /*
        Enum Protocol Class
     */
    private static Class<?> ENUM_PROTOCOL_CLAZZ;
    private static Object[] ENUM_PROTOCOLS;
    private static FieldAccessor<Map> PACKET_MAP_FIELD;

    /*
        Enum Direction Class
     */
    private static Class<?> ENUM_DIRECTION_CLAZZ;

    // ServerBound = [0] -> To server
    // ClientBound = [1] -> To client
    private static Object[] DIRECTIONS;

    private static FieldAccessor<Integer> ENUM_DIRECTION_ORDINAL_FIELD;

    public static Object getChannel(UUID id, String address){
        List futures = NETWORK_MANAGERS_FIELD.get(SERVER_CONNECTION);

        Object future = futures.stream().filter(ch -> {
            SocketAddress address1 = (SocketAddress) ADDRESS_FIELD.get(ch);
            String parsed = parseAddress(address1);
            return address.equalsIgnoreCase(parsed);
        }).findFirst().orElse(null);

        if (future != null) return CHANNEL_FIELD.get(future);
        return null;
    }

    public static List getChannels(){
        return (List) NETWORK_MANAGERS_FIELD.get(SERVER_CONNECTION);
    }

    public static String parseAddress(SocketAddress address) {
        return address.toString().split("/")[1].split(":")[0];
    }

    public static Map<ProtocolDirection, Map<Integer, Class<? extends GPacket>>> getPacketMap(EnumProtocol id) {
        // Create the map
        Map<ProtocolDirection, Map<Integer, Class<? extends GPacket>>> map = new HashMap<>();

        // Get the map from the id to match the Spigot enum protocol
        Object enumProtocol = ENUM_PROTOCOLS[id.getOrdinal()];

        // For every direction, we'll seek to getting all the values from it's map
        for (int i = 0; i < ProtocolDirection.values().length; i++) {

            ProtocolDirection direction = ProtocolDirection.values()[i];

            // Create getX new map where we'll store the values
            Map<Integer, Class<? extends GPacket>> packetMap = new HashMap<>();

            // Get the map from the packet map
            Map map1 = PACKET_MAP_FIELD.get(enumProtocol);

            if (ServerUtil.getGameVersion().isOrAbove(ProtocolVersion.V1_15)) {
                try {
                    System.out.println(";) " + i);
                    Object interest = map1.get(DIRECTIONS[i]);

                    if (interest == null){
                        map.put(direction, packetMap);
                        continue;
                    }

                    FieldAccessor<Map> fieldAccessor = Reflection.getField(interest.getClass(), Map.class, 0);
                    Map iterate = (Map) fieldAccessor.get(interest);

                    // For every value iterated, get the integer and the clazz and match the name
                    iterate.forEach((clazz, packetId) -> {
                        // Grab the packet ID
                        final int packet = (int) packetId;

                        // Grab the class
                        final Class claz = (Class) clazz;

                        // Convert name to string. This won't unfortunately work with obfuscated spigots. If
                        // You do obfuscate your spigots and rename the packets, it isn't my problem anymore.
                        // This API already supports for itself to be obfuscated. Don't be too needy >:(
                        String packetName = claz.getSimpleName();

                        // Add it to the map
                        packetMap.put(packet, id.getPacketClass(direction, packetName));
                    });

                    // Put the packet map in itself
                    map.put(direction, packetMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                Map interest = (Map) map1.get(DIRECTIONS[i]);

                // Map can be nullable. Just skip if it is
                if (interest == null){
                    map.put(direction, packetMap);
                    continue;
                }

                // For every value iterated, get the integer and the clazz and match the name
                interest.forEach((packetId, clazz) -> {
                    // Grab the packet ID
                    int packet = (int) packetId;

                    // Grab the class
                    Class claz = (Class) clazz;

                    // Convert name to string. This won't unfortunately work with obfuscated spigots. If
                    // You do obfuscate your spigots and rename the packets, it isn't my problem anymore.
                    // This API already supports for itself to be obfuscated. Don't be too needy >:(
                    String packetName = claz.getSimpleName();

                    // Add it to the map
                    packetMap.put(packet, id.getPacketClass(direction, packetName));
                });

                // Put the packet map in itself
                map.put(direction, packetMap);
            }

        }

        // Return the map
        return map;
    }

    /*
        NBT READING/WRITING
     */

    private static Class<?> NBT_READ_LIMITER_CLAZZ;
    private static ConstructorInvoker NBT_READ_LIMITER_CONSTRUCTOR;

    private static Class<?> NBT_TOOLS_CLAZZ;

    private static Class<?> NBT_COMPOUND_CLAZZ;
    private static MethodInvoker NBT_COMPOUND_READ_FROM_BYTEBUF;

    /**
     * Allows users to read compound tags directly from the buffer
     * @param stream ByteBufStream
     * @return NMS object of the compound tag
     */
    public static Object getCompoundTag(Object stream) {
        // Instantiate getX new limiter
        Object threadLimiter = NBT_READ_LIMITER_CONSTRUCTOR.invoke(2097152L);
        // Invoke the object
        Object tag = null;

        try {
            tag = NBT_COMPOUND_READ_FROM_BYTEBUF.invoke(null, stream, threadLimiter);
        } catch (Exception e){
            // ignore
        }
        return tag;
    }

    private static MethodInvoker WRITE_NBT_COMPOUND_TO_BYTEBUF;

    public static void writeCompoundTag(Object compoundTag, MutableByteBufOutputStream stream){
        WRITE_NBT_COMPOUND_TO_BYTEBUF.invoke(null, compoundTag, stream);
    }

    private static Class<?> CRAFT_ITEM_CLAZZ;
    private static Class<?> ITEM_NMS_CLAZZ;
    private static Class<?> ITEM_TYPE_CLAZZ;

    private static MethodInvoker GET_NBT_TAG_FROM_ITEMSTACK_METHOD;

    private static FieldAccessor<?> GET_HANDLE_ITEM;

    public static Object getCompoundTagFromItem(ItemStack stack) {
        Object nms = GET_HANDLE_ITEM.get(stack);
        return GET_NBT_TAG_FROM_ITEMSTACK_METHOD.invoke(stack);
    }

    private static MethodInvoker GET_ITEM_FROM_ID_METHOD;
    private static ConstructorInvoker ITEM_NMS_CONSTRUCTOR;
    private static MethodInvoker SET_DATA_METHOD;
    private static MethodInvoker AS_BUKKIT_COPY_METHOD;

    public static ItemStack getItemFromWrapper(WrappedItem item){
        Object id = GET_ITEM_FROM_ID_METHOD.invoke(null, item.getId());
        Object nmsItem;

        if (ServerUtil.getGameVersion().isAbove(ProtocolVersion.V1_14)) {
            nmsItem = ITEM_NMS_CONSTRUCTOR.invoke(id, item.getAmount());
        } else {
            nmsItem = ITEM_NMS_CONSTRUCTOR.invoke(id, item.getAmount(), item.getData());
        }
        try {
            SET_DATA_METHOD.invoke(nmsItem, item.getTag());
        } catch (Exception e) {
            // ignored
        }
        return (ItemStack) AS_BUKKIT_COPY_METHOD.invoke(null, nmsItem);
    }

    /*
        Player
     */

    private static Class<?> CRAFT_PLAYER_CLAZZ;
    private static Class<?> NMS_PLAYER_CLAZZ;
    private static MethodInvoker GET_HANDLE_METHOD;
    private static FieldAccessor<Integer> PING_FIELD;

    public static int getPing(Player player) {
        Object nmsPlayer = GET_HANDLE_METHOD.invoke(player);
        return PING_FIELD.get(nmsPlayer);
    }


    @SneakyThrows
    public static void init() {
        NETTY_UTIL = NettyUtil.getInstance();
        CRAFT_SERVER_CLAZZ = Reflection.getCraftBukkitClass("CraftServer");
        MINECRAFT_SERVER_FIELD = Reflection.getField(CRAFT_SERVER_CLAZZ, MINECRAFT_SERVER_CLAZZ, 0);
        MINECRAFT_SERVER = MINECRAFT_SERVER_FIELD.get(Bukkit.getServer());

        SERVER_CONNECTION_CLAZZ = Reflection.getMinecraftClass("ServerConnection");
        SERVER_CONNECTION_FIELD = Reflection.getField(MINECRAFT_SERVER_CLAZZ, SERVER_CONNECTION_CLAZZ, 0);
        SERVER_CONNECTION = SERVER_CONNECTION_FIELD.get(MINECRAFT_SERVER);
        CHANNEL_FUTURES_FIELD = Reflection.getField(SERVER_CONNECTION_CLAZZ, List.class, 0);

        NETWORK_MANAGER_CLAZZ = Reflection.getMinecraftClass("NetworkManager");
        NETWORK_MANAGERS_FIELD = Reflection.getField(SERVER_CONNECTION_CLAZZ, List.class, 1);

        try {
            CHANNEL_FIELD = Reflection.getField(NETWORK_MANAGER_CLAZZ, "channel", 0);
        } catch (Exception e) {

        }

        ADDRESS_FIELD = Reflection.getField(NETWORK_MANAGER_CLAZZ, SocketAddress.class, 0);

        ENUM_PROTOCOL_CLAZZ = Reflection.getMinecraftClass("EnumProtocol");
        ENUM_PROTOCOLS = ENUM_PROTOCOL_CLAZZ.getEnumConstants();
        PACKET_MAP_FIELD = Reflection.getField(ENUM_PROTOCOL_CLAZZ, Map.class, 1);

        try {
            ENUM_DIRECTION_CLAZZ = Reflection.getMinecraftClass("EnumProtocolDirection");
            DIRECTIONS = ENUM_DIRECTION_CLAZZ.getEnumConstants();
        } catch (Exception e) {

        }

        NBT_READ_LIMITER_CLAZZ = Reflection.getMinecraftClass("NBTReadLimiter");
        NBT_READ_LIMITER_CONSTRUCTOR = Reflection.getConstructor(NBT_READ_LIMITER_CLAZZ, long.class);
        NBT_TOOLS_CLAZZ = Reflection.getMinecraftClass("NBTCompressedStreamTools");
        NBT_COMPOUND_CLAZZ = Reflection.getMinecraftClass("NBTTagCompound");
        NBT_COMPOUND_READ_FROM_BYTEBUF = Reflection.getMethod(NBT_TOOLS_CLAZZ, NBT_COMPOUND_CLAZZ,
                0, DataInput.class, NBT_READ_LIMITER_CLAZZ);

        WRITE_NBT_COMPOUND_TO_BYTEBUF = Reflection.getMethod(NBT_TOOLS_CLAZZ, void.class,
                0, NBT_COMPOUND_CLAZZ, DataOutput.class);
        CRAFT_ITEM_CLAZZ = Reflection.getCraftBukkitClass("inventory.CraftItemStack");
        ITEM_NMS_CLAZZ = Reflection.getMinecraftClass("ItemStack");
        ITEM_TYPE_CLAZZ = Reflection.getMinecraftClass("Item");
        GET_NBT_TAG_FROM_ITEMSTACK_METHOD = Reflection.getMethod(ITEM_NMS_CLAZZ, "getTag");
        GET_HANDLE_ITEM = Reflection.getField(CRAFT_ITEM_CLAZZ, "handle", ITEM_NMS_CLAZZ);
        GET_ITEM_FROM_ID_METHOD = Reflection.getMethod(ITEM_TYPE_CLAZZ, "getById", int.class);

        if (ServerUtil.getGameVersion().isAbove(ProtocolVersion.V1_14)) {
            ITEM_NMS_CONSTRUCTOR = Reflection.getConstructor(ITEM_NMS_CLAZZ,
                    Reflection.getMinecraftClass("IMaterial"), int.class);
        } else {
            ITEM_NMS_CONSTRUCTOR = Reflection.getConstructor(ITEM_NMS_CLAZZ, ITEM_TYPE_CLAZZ, int.class, int.class);
        }

        SET_DATA_METHOD = Reflection.getMethod(ITEM_NMS_CLAZZ, void.class, 0, NBT_COMPOUND_CLAZZ);
        AS_BUKKIT_COPY_METHOD = Reflection.getMethod(CRAFT_ITEM_CLAZZ, "asBukkitCopy", ITEM_NMS_CLAZZ);

        CRAFT_PLAYER_CLAZZ = Reflection.getCraftBukkitClass("entity.CraftPlayer");
        NMS_PLAYER_CLAZZ = Reflection.getMinecraftClass("EntityPlayer");
        GET_HANDLE_METHOD = Reflection.getMethod(CRAFT_PLAYER_CLAZZ, "getHandle");
        PING_FIELD = Reflection.getField(NMS_PLAYER_CLAZZ, "ping", int.class);
    }
    static {
        init();
    }
}
