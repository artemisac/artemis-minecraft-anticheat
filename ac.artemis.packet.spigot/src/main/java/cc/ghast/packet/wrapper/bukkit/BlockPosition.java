package cc.ghast.packet.wrapper.bukkit;

import cc.ghast.packet.nms.MathHelper;
import lombok.Getter;

@Getter
public class BlockPosition {
    public static final int c = 1 + MathHelper.c(MathHelper.b(30000000));
    public static final int d = BlockPosition.c;
    public static final int e = 64 - BlockPosition.c - BlockPosition.d;
    public static final int f = 0 + BlockPosition.d;
    public static final int g = BlockPosition.f + BlockPosition.e;
    public static final long h = (1L << BlockPosition.c) - 1L;
    public static final long i = (1L << BlockPosition.e) - 1L;
    public static final long j = (1L << BlockPosition.d) - 1L;


    private int x, y, z;

    public BlockPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


}
