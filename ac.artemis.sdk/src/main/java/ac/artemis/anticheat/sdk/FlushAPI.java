package ac.artemis.anticheat.sdk;

public class FlushAPI {
    private static FlushManager api;

    public static FlushManager getApi() {
        return api;
    }

    public static void setApi(FlushManager provided) {
        assert api != null : "Flush API already internally registered";
        FlushAPI.api = provided;
    }
}
