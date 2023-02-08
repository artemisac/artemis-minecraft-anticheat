package ac.artemis.anticheat.api;

public class ArtemisServerClient {
    private static ArtemisAPI artemisAPI;

    public static ArtemisAPI getAPI() {
        return artemisAPI;
    }

    public static void setAPI(ArtemisAPI api) {
        if (artemisAPI != null && api != null)
            throw new IllegalStateException("API is already registered");
        ArtemisServerClient.artemisAPI = api;
    }
}
