package ac.artemis.packet.minecraft.entity;

import ac.artemis.packet.minecraft.Wrapped;

public interface Messager extends Wrapped {
    void sendMessage(final String message);
}
