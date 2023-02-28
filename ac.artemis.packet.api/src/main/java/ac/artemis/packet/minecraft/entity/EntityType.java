package ac.artemis.packet.minecraft.entity;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {
    DROPPED_ITEM("Item", 1, false),
    EXPERIENCE_ORB("XPOrb",  2),
    LEASH_HITCH("LeashKnot", 8),
    PAINTING("Painting", 9),
    ARROW("Arrow", 10),
    SNOWBALL("Snowball", 11),
    FIREBALL("Fireball", 12),
    SMALL_FIREBALL("SmallFireball", 13),
    ENDER_PEARL("ThrownEnderpearl", 14),
    ENDER_SIGNAL("EyeOfEnderSignal", 15),
    THROWN_EXP_BOTTLE("ThrownExpBottle", 17),
    ITEM_FRAME("ItemFrame", 18),
    WITHER_SKULL("WitherSkull", 19),
    PRIMED_TNT("PrimedTnt", 20),
    FALLING_BLOCK("FallingSand", 21, false),
    FIREWORK("FireworksRocketEntity", 22, false),
    ARMOR_STAND("ArmorStand", 30, false),
    MINECART_COMMAND("MinecartCommandBlock", 40),
    BOAT("Boat", 41),
    MINECART("MinecartRideable", 42),
    MINECART_CHEST("MinecartChest", 43),
    MINECART_FURNACE("MinecartFurnace", 44),
    MINECART_TNT("MinecartTNT", 45),
    MINECART_HOPPER("MinecartHopper", 46),
    MINECART_MOB_SPAWNER("MinecartMobSpawner", 47),
    CREEPER("Creeper", 50),
    SKELETON("Skeleton", 51),
    SPIDER("Spider", 52),
    GIANT("Giant", 53),
    ZOMBIE("Zombie", 54),
    SLIME("Slime", 55),
    GHAST("Ghast", 56),
    PIG_ZOMBIE("PigZombie", 57),
    ENDERMAN("Enderman", 58),
    CAVE_SPIDER("CaveSpider", 59),
    SILVERFISH("Silverfish", 60),
    BLAZE("Blaze", 61),
    MAGMA_CUBE("LavaSlime", 62),
    ENDER_DRAGON("EnderDragon", 63),
    WITHER("WitherBoss", 64),
    BAT("Bat", 65),
    WITCH("Witch", 66),
    ENDERMITE("Endermite", 67),
    GUARDIAN("Guardian", 68),
    PIG("Pig", 90),
    SHEEP("Sheep", 91),
    COW("Cow", 92),
    CHICKEN("Chicken", 93),
    SQUID("Squid", 94),
    WOLF("Wolf", 95),
    MUSHROOM_COW("MushroomCow", 96),
    SNOWMAN("SnowMan", 97),
    OCELOT("Ozelot", 98),
    IRON_GOLEM("VillagerGolem", 99),
    HORSE("EntityHorse", 100),
    RABBIT("Rabbit", 101),
    VILLAGER("Villager", 120),
    ENDER_CRYSTAL("EnderCrystal", 200),
    SPLASH_POTION((String)null, -1, false),
    EGG((String)null, -1, false),
    FISHING_HOOK((String)null, -1, false),
    LIGHTNING((String)null, -1, false),
    WEATHER((String)null, -1, false),
    PLAYER((String)null, -1, false),
    COMPLEX_PART((String)null, -1, false),
    UNKNOWN((String)null, -1, false);

    private String name;
    private short typeId;
    private boolean independent;
    private boolean living;
    private static final Map<String, EntityType> NAME_MAP = new HashMap();
    private static final Map<Short, EntityType> ID_MAP = new HashMap();

    static {
        EntityType[] var0;
        int var1 = (var0 = values()).length;

        for(int var2 = 0; var2 < var1; ++var2) {
            EntityType type = var0[var2];
            if (type.name != null) {
                NAME_MAP.put(type.name.toLowerCase(), type);
            }

            if (type.typeId > 0) {
                ID_MAP.put(type.typeId, type);
            }
        }

    }

    EntityType(String name, int typeId) {
        this(name,typeId, true);
    }

    EntityType(String name, int typeId, boolean independent) {
        this.name = name;
        this.typeId = (short)typeId;
        this.independent = independent;

    }

    /** @deprecated */
    @Deprecated
    public String getName() {
        return this.name;
    }

    /** @deprecated */
    @Deprecated
    public short getTypeId() {
        return this.typeId;
    }

    /** @deprecated */
    @Deprecated
    public static EntityType fromName(String name) {
        return name == null ? null : (EntityType)NAME_MAP.get(name.toLowerCase());
    }

    /** @deprecated */
    @Deprecated
    public static EntityType fromId(int id) {
        return id > 32767 ? null : (EntityType)ID_MAP.get((short)id);
    }

    public boolean isSpawnable() {
        return this.independent;
    }

    public boolean isAlive() {
        return this.living;
    }
}
