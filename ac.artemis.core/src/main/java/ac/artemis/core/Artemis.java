package ac.artemis.core;

import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.core.v4.api.APIManager;

public interface Artemis {
    /**
     * Loads Artemis from an existing plugin interface
     * @param plugin plugin
     */
    APIManager load(final Plugin plugin);

    /**
     * @return API Manager for Artemis
     */
    APIManager getApi();

    /**
     * @return Returns the Plugin implementation of the software
     */
    Plugin getPlugin();

    /**
     * Virtual wrapped server instance
     *
     * @return the server
     */
    static Artemis v() {
        return Artemis.ArtemisInstance.getInstance();
    }

    /**
     * Stored reference to the server to not break stuff / make stuff
     * messy.
     */
    class ArtemisInstance {
        private static Artemis artemis;

        /**
         * @return the instance
         */
        public static Artemis getInstance() {
            return artemis;
        }

        /**
         * Sets the Artemis instance.
         *
         * @param v the server
         */
        public static void setInstance(Artemis v) {
            artemis = v;
        }
    }
}
