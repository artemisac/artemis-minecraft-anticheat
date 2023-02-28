package ac.artemis.packet.generator;

import ac.artemis.packet.generator.comparison.PacketMapFinder;
import ac.artemis.packet.wrapper.PacketClass;
import ac.artemis.packet.generator.serialization.PacketSerializer;
import ac.artemis.packet.generator.util.ServerUtil;
import ac.artemis.packet.protocol.ProtocolState;
import ac.artemis.packet.protocol.format.WrittenEnumProtocol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public final class PacketApiGenerator extends JavaPlugin {

    @Override
    @SneakyThrows
    public void onEnable() {
        // Plugin startup logic
        final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().registerTypeAdapter(PacketClass.class,
                new PacketSerializer()).create();
        final File file = new File(this.getDataFolder(), ServerUtil.getGameVersion().getServerVersion() + ".json");

        if (!file.exists()) {
            file.getParentFile().mkdir();
            file.getParentFile().mkdirs();
        }

        JsonWriter writer = new JsonWriter(new FileWriter(file));
        writer.setSerializeNulls(true);
        writer.setIndent("  ");

        final WrittenEnumProtocol writtenEnumProtocol = new WrittenEnumProtocol(ServerUtil.getGameVersion(), new HashMap<>());
        writtenEnumProtocol.getFormatMap().put(ProtocolState.HANDSHAKE, PacketMapFinder.findMap(ProtocolState.HANDSHAKE));
        writtenEnumProtocol.getFormatMap().put(ProtocolState.PLAY, PacketMapFinder.findMap(ProtocolState.PLAY));
        writtenEnumProtocol.getFormatMap().put(ProtocolState.LOGIN, PacketMapFinder.findMap(ProtocolState.LOGIN));
        writtenEnumProtocol.getFormatMap().put(ProtocolState.STATUS, PacketMapFinder.findMap(ProtocolState.STATUS));

        gson.toJson(writtenEnumProtocol, WrittenEnumProtocol.class, writer);
        writer.close();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
