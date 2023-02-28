package cc.ghast.packet.wrapper.bukkit;

import lombok.Getter;

@Getter
public enum BlockFace {
    Y_MINUS(0),
    Y_PLUS(1),
    Z_MINUS(2),
    Z_PLUS(3),
    X_MINUS(4),
    X_PLUS(5),
    INVALID(6)
    ;

    private final int id;

    BlockFace(int id) {
        this.id = id;
    }
}
