package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.entity.Console;
import org.bukkit.command.ConsoleCommandSender;

public class BukkitConsole extends AbstractWrapper<ConsoleCommandSender> implements Console {
    public BukkitConsole(ConsoleCommandSender wrapper) {
        super(wrapper);
    }

    @Override
    public void sendMessage(String s) {
        wrapper.sendMessage(s);
    }
}
