package ac.artemis.core.v5.emulator.attributes;
/**
 * @author Ghast
 * @since 13/02/2021
 * Artemis © 2021
 */
public class EntityAttributes {
    /*
     * Booleans
     */
    public static final AttributeKey CHUNK_LOADED = new AttributeKey("chunk-load");
    public static final AttributeKey JUMPING = new AttributeKey("jumping");
    public static final AttributeKey GROUND = new AttributeKey("ground");
    public static final AttributeKey LAST_GROUND = new AttributeKey("last-ground");
    public static final AttributeKey SPRINT = new AttributeKey("sprint");
    public static final AttributeKey LAST_SPRINT = new AttributeKey("sprint");
    public static final AttributeKey SNEAK = new AttributeKey("sneak");
    public static final AttributeKey FLYING = new AttributeKey("flying");
    public static final AttributeKey WATER = new AttributeKey("water");
    public static final AttributeKey LAVA = new AttributeKey("lava");
    public static final AttributeKey LADDER = new AttributeKey("ladder");
    public static final AttributeKey WEB = new AttributeKey("web");
    public static final AttributeKey COMPENSATE_WORLD = new AttributeKey("world");
    public static final AttributeKey NOCLIP = new AttributeKey("noclip");

    /*
     * Doubles
     */
    public static final AttributeKey FALL_DISTANCE = new AttributeKey("fall");

    /*
     * Floats
     */
    public static final AttributeKey ATTRIBUTE_SPEED = new AttributeKey("speed");
    public static final AttributeKey YAW = new AttributeKey("yaw");
    /*
     * Entity
     */
    public static final AttributeKey ATTACKED = new AttributeKey("attacked");

    /*
     * Ints
     */
    public static final AttributeKey JUMP_TICKS = new AttributeKey("jump-ticks");
}
