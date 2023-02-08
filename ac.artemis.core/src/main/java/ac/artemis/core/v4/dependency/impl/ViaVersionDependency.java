package ac.artemis.core.v4.dependency.impl;


import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.dependency.AbstractDependency;
import ac.artemis.core.v4.utils.chat.Chat;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

/**
 * @author Ghast
 * @since 10-Nov-19
 * Ghast CC © 2019
 */
@Dependency(
        name = "ViaVersion",
        version = "2.1.3",
        url = "https://repo.viaversion.com/us/myles/viaversion/2.2.3-SNAPSHOT/viaversion-2.2.3-SNAPSHOT.jar"
)
public class ViaVersionDependency extends AbstractDependency {

    public ViaAPI plugin;

    public ViaVersionDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        try {
            plugin = Via.getAPI();
        } catch (Exception e) {
            Chat.sendConsoleMessage("&8[&4WARNING8]&c ViaVersion not detected!");
            Server.v().getPluginManager().kill(Artemis.v().getPlugin());
        }
    }
}
