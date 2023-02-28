package ac.artemis.packet;

public class PacketManager {
    private static PacketAPI api;

    public static PacketAPI getApi() {
        if (api == null) {
            throw new IllegalStateException("Packet API hasn't been set!");
        }
        return api;
    }

    public static void setApi(PacketAPI papi) {
        if (api != null) {
            throw new IllegalStateException("Packet API has already been set!");
        }
        PacketManager.api = papi;
    }
}
