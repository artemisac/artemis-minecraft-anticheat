package ac.artemis.core.v5.emulator.world.mcp;

import lombok.Getter;

@Getter
public enum Dimension {
    NETHER(-1),
    OVERWORLD(0),
    END(1);

    private final int id;

    Dimension(int id) {
        this.id = id;
    }
}