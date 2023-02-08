package ac.artemis.core.v5.emulator;

import ac.artemis.core.inject.Injector;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.config.ConfigManager;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v5.utils.interf.Factory;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Ghast
 * @since 03/02/2021
 * Artemis © 2021
 */
public class EmulatorManager extends Manager {
    public EmulatorManager(Artemis plugin) {
        super(plugin, "Emulator");
    }

    private final Set<EmulatorProvider> providerSet = new HashSet<>();

    @Getter
    @Setter
    private static EmulatorProvider provider;

    @Override
    public void init(InitializeAction initializeAction) {
        try {
            Injector.inject(Class.forName("ac.artemis.anticheat.engine.v1.EngineInjector"));
            Injector.inject(Class.forName("ac.artemis.anticheat.engine.v2.EngineInjector"));
        } catch (Exception e) {
            Chat.sendConsoleMessage("&4Fatal issue with Artemis! Error code &cOxA1-01");
            e.printStackTrace();
            Chat.sendConsoleMessage(Chat.spacer());
        }

        this.set(ConfigManager.getPrediction().getVersion());
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        providerSet.clear();
        provider = null;
    }

    public void inject(final EmulatorProvider provider) {
        this.providerSet.add(provider);
    }

    public void set(String name) {
        /* Quicky set */
        name = name.toLowerCase(Locale.ROOT);

        /* Iterate and find the most compatible emulator provider */
        for (EmulatorProvider emulatorProvider : providerSet) {
            if (emulatorProvider.getNames().contains(name)) {
                provider = emulatorProvider;
                return;
            }
        }

        Chat.sendConsoleMessage("&aFatal issue with Artemis! Tried to bootstrap unknown engine &c" + name);
    }

    @Getter
    public static class EmulatorProvider {
        private Set<String> names;
        private EmulatorFactory factory;

        public EmulatorProvider setNames(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
            return this;
        }

        public EmulatorProvider setFactory(EmulatorFactory factory) {
            this.factory = factory;
            return this;
        }
    }

    public abstract static class EmulatorFactory implements Factory<Emulator> {
        protected PlayerData data;

        public EmulatorFactory setData(PlayerData data) {
            this.data = data;
            return this;
        }
    }
}
