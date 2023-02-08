package ac.artemis.anticheat.bukkit;

import ac.artemis.packet.minecraft.AbstractWrapper;
import ac.artemis.packet.minecraft.entity.Messager;
import org.bukkit.command.CommandSender;

public class BukkitMessager extends AbstractWrapper<CommandSender> implements Messager {
    public BukkitMessager(CommandSender wrapper) {
        super(wrapper);
    }

    @Override
    public void sendMessage(String s) {
        wrapper.sendMessage(s);
    }
}
