package cc.ghast.packet.utils;

/**
 * @author Ghast
 * @since 15/08/2020
 * Artemis © 2020
 */
public class HookUtil {

    private static boolean plib;

    static {
        try {
            Class.forName("com.comphenix.protocol.ProtocolLib");
            plib = true;
        } catch (Exception e){
            plib = false;
        }
    }

    public static String getHookBehind() {
        if (plib) {
            return "protocol_lib_decoder";
        }

        return "decoder";
    }

    public static String getHookOutbound() {
        return "encoder";
    }

    public static String getHookForward() {
        return "encoder";
    }


}
