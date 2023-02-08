package ac.artemis.core.v5.emulator.datawatcher;

import cc.ghast.packet.buffer.ProtocolByteBuf;

import java.util.List;

public interface DataWatcherReader {
    List<WatchableObject> readWatchedListFromPacketBuffer(ProtocolByteBuf buffer);
}
