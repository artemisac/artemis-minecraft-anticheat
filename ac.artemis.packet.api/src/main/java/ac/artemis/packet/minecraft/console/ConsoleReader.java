package ac.artemis.packet.minecraft.console;

public interface ConsoleReader {
    String readLine(final String prompt, final Character mask);
}
