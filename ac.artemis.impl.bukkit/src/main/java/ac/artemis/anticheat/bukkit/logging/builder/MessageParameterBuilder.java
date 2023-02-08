package ac.artemis.anticheat.bukkit.logging.builder;

public class MessageParameterBuilder {
    private static HoverableMessageParameter hoverableMessageParameter;

    public static MessageParameter buildHoverableParameter() {
        if (hoverableMessageParameter == null) {
            hoverableMessageParameter = new HoverableMessageParameter();
        }

        return hoverableMessageParameter;
    }
}
