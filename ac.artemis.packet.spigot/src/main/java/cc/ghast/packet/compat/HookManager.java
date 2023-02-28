package cc.ghast.packet.compat;

import cc.ghast.packet.profile.ArtemisProfile;
import ac.artemis.packet.protocol.ProtocolDirection;
import cc.ghast.packet.utils.Chat;
import cc.ghast.packet.buffer.ProtocolByteBuf;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ghast
 * @since 28/12/2020
 * ArtemisPacket © 2020
 */

@Getter
public class HookManager {
    private final List<PacketModifier> modifiers = new ArrayList<>();
    private ViaHook viaVersionHook;

    public HookManager() {
        this.init();
    }

    public void init() {
        if (checkPluginDependency("com.viaversion.viaversion.api.Via")) {
            viaVersionHook = new ViaVersionHook();
            viaVersionHook.getVersion(UUID.randomUUID());
        } else if (checkPluginDependency("us.myles.ViaVersion.api.Via")) {
            viaVersionHook = new LegacyViaVersionHook();
            viaVersionHook.getVersion(UUID.randomUUID());
        } else {
            viaVersionHook = null;
        }
    }

    public void modifyAll(ArtemisProfile profile, ProtocolDirection direction, ProtocolByteBuf byteBuf, int packetId) {
        modifiers.forEach(e -> e.modify(profile, direction, byteBuf, packetId));
    }

    private boolean checkPluginDependency(String className) {
        try {
            Class.forName(className);
            //dependencies.add(clazz.getConstructor(Artemis.class).newInstance(plugin));
            return true;
        } catch (ClassNotFoundException e) {
            Chat.sendConsoleMessage("&7[&bArtemis&7]&6 " + className + " dependency not found, using default settings");
        } catch (Throwable e) {
            Chat.sendConsoleMessage("&7[&bArtemis&7]&6 Failed to load dependency " + className + ", using default settings");
        }
        return false;
    }
}
