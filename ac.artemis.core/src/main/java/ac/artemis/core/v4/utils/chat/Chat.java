package ac.artemis.core.v4.utils.chat;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.plugin.Plugin;
import ac.artemis.core.Artemis;

import java.util.List;

/**
 * @author Ghast
 * @since 14-Mar-20
 */
public class Chat {
    public static String translate(String s) {
        if (s == null) return "";
        char[] b = s.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static String[] translate(String[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = translate(s[i]);
        }
        return s;
    }

    public static List<String> translate(List<String> s) {
        for (int i = 0; i < s.size(); i++) {
            s.set(i, translate(s.get(i)));
        }
        return s;
    }

    public static String[] translateAndConvert(List<String> s) {
        String[] var = new String[s.size()];
        for (int i = 0; i < s.size(); i++) {
            var[i] = translate(s.get(i));
        }
        return var;
    }

    public static String firstCap(String str) {
        return str != null && str.length() != 0 ? Character.toTitleCase(str.charAt(0)) + str.substring(1) : str;
    }

    public static void sendConsoleMessage(String s) {
        Server.v().getConsoleSender().sendMessage(translate(s));
    }

    public static void sendConsoleMessage(String... s) {
        for (String s1 : s) {
            Server.v().getConsoleSender().sendMessage(translate(s1));

        }
    }

    public static void sendLogo() {
        String[] logo = new String[] {
                "   /$$$$$$              /$$                             /$$          \n",
                "  /$$__  $$            | $$                            |__/          \n",
                " | $$  \\ $$  /$$$$$$  /$$$$$$    /$$$$$$  /$$$$$$/$$$$  /$$  /$$$$$$$\n",
                " | $$$$$$$$ /$$__  $$|_  $$_/   /$$__  $$| $$_  $$_  $$| $$ /$$_____/\n",
                " | $$__  $$| $$  \\__/  | $$    | $$$$$$$$| $$ \\ $$ \\ $$| $$|  $$$$$$ \n",
                " | $$  | $$| $$        | $$ /$$| $$_____/| $$ | $$ | $$| $$ \\____  $$\n",
                " | $$  | $$| $$        |  $$$$/|  $$$$$$$| $$ | $$ | $$| $$ /$$$$$$$/\n",
                " |__/  |__/|__/         \\___/   \\_______/|__/ |__/ |__/|__/|_______/ \n",
                "                                                                    \n"
        };

        final StringBuilder builder2 = new StringBuilder("\n \n \n");
        for (String s : logo) {
            builder2.append("&b")
                    .append("                     ")
                    .append(s);
        }

        sendConsoleMessage(builder2.toString());
        sendConsoleMessage(" ");

        Plugin artemis = Artemis.v().getPlugin();
        sendConsoleMessage(spacer() + spacer() + spacer());
        StringBuilder builder = new StringBuilder();
        artemis.getAuthors().forEach(author -> builder.append(author).append(", "));
        sendConsoleMessage("    &b&lVersion&7:&f " + artemis.getVersion() + "   " + "&b&lAuthors&7:&f " + builder);
    }

    public static String spacer() {
        return translate("&8&m----------------------------");
    }

    public static String spacer3() {
        return translate("&8&m------------------------------------------------------------------------------------");
    }
}
