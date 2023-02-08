package ac.artemis.core.v4.dependency;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.v4.dependency.impl.*;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.file.JarFileDownloader;
import ac.artemis.core.v4.utils.file.JarFileLoader;
import lombok.Getter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ghast
 * @since 27-Apr-20
 */

@Getter
public class DependencyManager extends Manager {
    public DependencyManager(Artemis plugin) {
        super(plugin, "Dependency [Manager]");
    }

    private final List<AbstractDependency> dependencies = new ArrayList<>();
    private final JarFileDownloader jarFileDownloader = new JarFileDownloader();
    private final JarFileLoader jarFileLoader = new JarFileLoader(new URL[]{});

    public static boolean IS_DEV;

    @Override
    @SuppressWarnings("unchecked")
    public void init(InitializeAction initializeAction) {
        Chat.sendConsoleMessage(Chat.spacer3());
        Chat.sendConsoleMessage("&8&m-----------------------------------<&b Dependency &8&m>-----------------------------------");
        this.checkDir();

        final Class<? extends AbstractDependency>[] clazzes = new Class[]{
                GuavaDependency.class,
                ApacheMathsDependency.class,
                HikariDependency.class,
                MongoDependency.class,
                SunJNADependency.class,
                Slf4jDependency.class,
                OshiDependency.class,
                ApacheLangDependency.class,
                FastUtilDependency.class,
                CommonsDependency.class
        };

        for (Class<? extends AbstractDependency> clazz : clazzes) {
            checkLocalDependency(clazz);
        }

        dependencies.forEach(AbstractDependency::init);
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {

    }

    private void kill() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&c Failed to download dependency. Stopping Artemis");
        Server.v().getPluginManager().kill(plugin.getPlugin());
    }


    @SuppressWarnings("all")
    private void checkDir() {
        File file = new File(plugin.getPlugin().getDataFolder(), "libs");
        if (!file.exists()) {
            file.mkdir();
            file.getParentFile().mkdirs();
            file.getParentFile().mkdir();
        }
    }

    private boolean checkLocalDependency(Class<? extends AbstractDependency> clazz) {

        String clazzName = clazz.getAnnotation(Dependency.class).name();

        try {
            File file = new File(plugin.getPlugin().getDataFolder(), "libs/"
                    + clazzName + ".jar");
            if (!file.exists()) {
                throw new NoSuchMethodException();
            }
            //Loader.v().getBukkitPluginLoader().addDependency(file);
            dependencies.add(clazz.getConstructor(Artemis.class).newInstance(plugin));
            return true;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            Chat.sendConsoleMessage("&r[&c✘&r]&c Failed to load dependency " + clazzName + ", redownloading latest...");
            //e.printStackTrace();
        } catch (NullPointerException | NoClassDefFoundError e) {
            IS_DEV = true;
            Chat.sendConsoleMessage("&7[&6❕&7]&6 Loading in developer mode. Expecting " + clazzName + " in classpath.");
            return true;
        }
        downloadDependency(clazzName, clazz);
        return false;
    }

    private void downloadDependency(String clazzName, Class<? extends AbstractDependency> clazz) {
        Dependency dependency = clazz.getAnnotation(Dependency.class);
        jarFileDownloader.downloadFile(dependency.url(), new File(plugin.getPlugin().getDataFolder(), "libs/"
                + clazzName + ".jar").getAbsoluteFile());
        checkLocalDependency(clazz);
    }

    private boolean checkPluginDependency(String className, Class<? extends AbstractDependency> clazz) {
        try {
            Class.forName(className);
            //dependencies.add(clazz.getConstructor(Artemis.class).newInstance(plugin));
            return true;
        } catch (ClassNotFoundException e) {
            Chat.sendConsoleMessage("&7[&bArtemis&7]&6 " + className + " dependency not found, using default settings");
        } catch (/*IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException*/ Exception e) {
            Chat.sendConsoleMessage("&7[&bArtemis&7]&6 Failed to load dependency " + className + ", using default settings");
        }
        return false;
    }
}
