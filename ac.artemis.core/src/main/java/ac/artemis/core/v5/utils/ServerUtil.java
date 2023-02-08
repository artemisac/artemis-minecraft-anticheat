package ac.artemis.core.v5.utils;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.packet.minecraft.world.World;
import ac.artemis.core.v4.utils.chat.Chat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServerUtil {

    public void dispatchCommand(final String command) {
        Server.v().dispatchCommand(Server.v().getConsoleSender(), Chat.translate(command));
    }

    public void broadcast(final String message) {
        Server.v().broadcast(Chat.translate(message));
    }

    public void debug(final Object message) {
        Server.v().broadcast(Chat.translate("&c[Debug] &7" + message));
    }

    public void log(final String message) {
        System.out.println("ARTEMIS LOGGER > " + message);
    }

    public void console(final String s) {
        Server.v().getConsoleSender().sendMessage("&7[&bArtemis&7] &b" + Chat.translate(s));
    }

    public Entity getEntity(final int id) {
        for (final World world : Server.v().getWorlds()) {
            for (final Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) {
                    return entity;
                }
            }
        }
        return null;
    }

    public Entity getEntity(final World world, final int id) {
        if (world != null) {
            for (final Entity entity : world.getEntities()) {
                if (entity.getEntityId() == id) {
                    return entity;
                }
            }
        }
        return null;
    }
}
