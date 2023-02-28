package ac.artemis.packet.spigot.protocol;

import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.protocol.format.WrittenEnumProtocol;
import ac.artemis.packet.spigot.protocol.generator.ProtocolGeneratorDirect;
import ac.artemis.packet.spigot.utils.factory.Factory;
import org.bukkit.plugin.Plugin;

public class ProtocolGeneratorFactory implements Factory<PacketGenerator> {
    private Plugin plugin;
    private WrittenEnumProtocol protocol;

    public ProtocolGeneratorFactory setPlugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public ProtocolGeneratorFactory setProtocol(WrittenEnumProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    @Override
    public PacketGenerator build() {
        assert plugin != null : "Plugin is null!";
        assert protocol != null : "Protocol is null";

        return new ProtocolGeneratorDirect(plugin, protocol);
    }
}
