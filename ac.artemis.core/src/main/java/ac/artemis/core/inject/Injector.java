package ac.artemis.core.inject;

import ac.artemis.core.v4.utils.chat.Chat;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Injector {
    public Injectable inject(final Class<?> clazz) {
        if (!Injectable.class.isAssignableFrom(clazz))
            throw new IllegalStateException("Injectable artemis class has to implement Injectable");
        try {
            final Injectable injectable = (Injectable) clazz.newInstance();
            injectable.begin();
            return injectable;
        } catch (InstantiationException e) {
            Chat.sendConsoleMessage("&4Fatal issue with Artemis! Error code &cOxA0-01");
            e.printStackTrace();
            Chat.sendConsoleMessage(Chat.spacer());
        } catch (IllegalAccessException e) {
            Chat.sendConsoleMessage("&4Fatal issue with Artemis! Error code &cOxA0-02");
            e.printStackTrace();
            Chat.sendConsoleMessage(Chat.spacer());
        }

        return null;
    }
}
