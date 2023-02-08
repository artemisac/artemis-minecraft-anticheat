package ac.artemis.anticheat.api.check.type;

/**
 * @author Ghast
 * @since 15-Oct-19
 * Ghast CC © 2019
 */
public enum Type {
    AURA(Category.COMBAT),
    AIM(Category.COMBAT),
    REACH(Category.COMBAT),
    AUTOCLICKER(Category.COMBAT),
    SPEED(Category.MOVEMENT),
    FLY(Category.MOVEMENT),
    BADPACKETS(Category.MISC),
    OMNISPRINT(Category.MOVEMENT),
    INVENTORYWALK(Category.MOVEMENT),
    NOFALL(Category.MOVEMENT),
    SCAFFOLD(Category.MOVEMENT),
    PINGSPOOF(Category.EXPLOIT),
    TIMER(Category.PLAYER),
    VELOCITY(Category.COMBAT),
    JESUS(Category.MOVEMENT),
    CRASHER(Category.MISC),
    HITBOX(Category.COMBAT),
    BLINK(Category.MOVEMENT),
    SAFEWALK(Category.MOVEMENT),
    REGEN(Category.PLAYER),
    FASTLADDER(Category.MOVEMENT),
    PHASE(Category.MOVEMENT),
    PREDICTION(Category.MOVEMENT),
    DISABLER(Category.EXPLOIT),
    PROTOCOL(Category.MISC),
    ENTITYSPEED(Category.MISC),
    HEURISTICS(Category.MISC),
    UNKNOWN(Category.MISC);

    private final Category category;
    private final String correctName;

    Type(Category category) {
        this.category = category;
        this.correctName = name().length() != 0 ? Character.toTitleCase(name().charAt(0)) + name().substring(1) : name();;
    }

    public Category getCategory() {
        return category;
    }

    public String getCorrectName() {
        return correctName;
    }
}
