package ac.artemis.packet.spigot.protocol;

import ac.artemis.packet.PacketGenerator;
import ac.artemis.packet.PacketManager;
import ac.artemis.packet.protocol.ProtocolVersion;
import ac.artemis.packet.protocol.format.WrittenEnumProtocol;
import ac.artemis.packet.spigot.serialization.PacketSerializer;
import ac.artemis.packet.spigot.utils.access.Accessor;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.wrapper.PacketClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProtocolRepository extends Accessor {
    public ProtocolRepository(Plugin plugin) {
        super(plugin);
    }

    @Override
    public void create() {
        final Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(PacketClass.class, new PacketSerializer())
                .create();


        for (ProtocolVersion value : ProtocolVersion.values()) {
            final String name = value.getServerVersion() + ".json";
            final InputStream bufferedInputStream = this.getClass().getClassLoader().getResourceAsStream(name);

            if (bufferedInputStream == null) continue;

            final JsonReader jsonReader = new JsonReader(new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8));
            final WrittenEnumProtocol writtenEnumProtocol = gson.fromJson(jsonReader, WrittenEnumProtocol.class);

            if (writtenEnumProtocol == null)
                continue;

            PacketManager.getApi().addProtocol(value, writtenEnumProtocol);

            final PacketGenerator generator = new ProtocolGeneratorFactory()
                    .setProtocol(writtenEnumProtocol)
                    .setPlugin(plugin)
                    .build();

            PacketManager.getApi().addGenerator(value, generator);

            ServerUtil.sendConsoleMessage("&a&lSuccessfully &radded protocol of version &b&l" + value);
        }
    }

    @Override
    public void dispose() {

    }
}
