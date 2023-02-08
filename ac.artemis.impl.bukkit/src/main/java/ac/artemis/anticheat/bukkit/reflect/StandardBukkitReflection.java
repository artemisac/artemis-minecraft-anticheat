package ac.artemis.anticheat.bukkit.reflect;

import ac.artemis.packet.minecraft.console.ConsoleReader;
import ac.artemis.core.v5.reflect.BukkitReflection;
import ac.artemis.packet.minecraft.entity.impl.Player;
import cc.ghast.packet.reflections.FieldAccessor;
import cc.ghast.packet.reflections.MethodInvoker;
import cc.ghast.packet.reflections.Reflection;
import org.bukkit.Bukkit;

public class StandardBukkitReflection implements BukkitReflection {
    private final Class<?> craftServer = Reflection.getCraftBukkitClass("CraftServer");
    private final MethodInvoker reader = Reflection.getMethod(craftServer, "getReader");
    private final Class<?> craftPlayer = Reflection.getCraftBukkitClass("entity.CraftPlayer");
    private final MethodInvoker playerHandle = Reflection.getMethod(craftPlayer, "getHandle");
    private final Class<?> entityPlayer = Reflection.getMinecraftClass("EntityPlayer");
    private final FieldAccessor<Integer> playerIntField = Reflection.getField(entityPlayer, "ping", int.class);

    @Override
    public int getPing(Player player) {
        final Object entityPlayerInstance = playerHandle.invoke(player.v());
        return playerIntField.get(entityPlayerInstance);
    }

    @Override
    public ConsoleReader getReader() {
        return (ConsoleReader) reader.invoke(Bukkit.getServer());
    }
}
