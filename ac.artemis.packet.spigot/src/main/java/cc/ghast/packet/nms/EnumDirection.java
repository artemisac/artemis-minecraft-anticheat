package cc.ghast.packet.nms;

import cc.ghast.packet.wrapper.bukkit.BlockPosition;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Spliterator;
import java.util.function.Consumer;

public enum EnumDirection {

    DOWN(0, 1, -1, "down", EnumDirection.EnumAxisDirection.NEGATIVE,
            EnumDirection.EnumAxis.Y, new BlockPosition(0, -1, 0)),
    UP(1, 0, -1, "up", EnumDirection.EnumAxisDirection.POSITIVE,
            EnumDirection.EnumAxis.Y, new BlockPosition(0, 1, 0)),
    NORTH(2, 3, 2, "north", EnumDirection.EnumAxisDirection.NEGATIVE,
            EnumDirection.EnumAxis.Z, new BlockPosition(0, 0, -1)),
    SOUTH(3, 2, 0, "south", EnumDirection.EnumAxisDirection.POSITIVE,
            EnumDirection.EnumAxis.Z, new BlockPosition(0, 0, 1)),
    WEST(4, 5, 1, "west", EnumDirection.EnumAxisDirection.NEGATIVE,
            EnumDirection.EnumAxis.X, new BlockPosition(-1, 0, 0)),
    EAST(5, 4, 3, "east", EnumDirection.EnumAxisDirection.POSITIVE,
            EnumDirection.EnumAxis.X, new BlockPosition(1, 0, 0)),
    ;

    private static final EnumDirection[] faces = new EnumDirection[6];
    private static final EnumDirection[] orient = new EnumDirection[4];
    private static final Map<String, EnumDirection> p = Maps.newHashMap();

    static {
        EnumDirection[] aenumdirection = values();
        int i = aenumdirection.length;

        for (EnumDirection enumdirection : aenumdirection) {
            EnumDirection.faces[enumdirection.x] = enumdirection;
            if (enumdirection.k().c()) {
                EnumDirection.orient[enumdirection.z] = enumdirection;
            }

            EnumDirection.p.put(enumdirection.j().toLowerCase(), enumdirection);
        }

    }

    private final int x;
    private final int y;
    private final int z;
    private final String j;
    private final EnumDirection.EnumAxis axis;
    private final EnumDirection.EnumAxisDirection axisDirection;
    private final BlockPosition blockPosition;

    EnumDirection(int x, int y, int z, String s, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection,
                  EnumDirection.EnumAxis enumdirection_enumaxis, BlockPosition baseblockposition) {
        this.x = x;
        this.z = z;
        this.y = y;
        this.j = s;
        this.axis = enumdirection_enumaxis;
        this.axisDirection = enumdirection_enumaxisdirection;
        this.blockPosition = baseblockposition;
    }

    public static EnumDirection fromType1(int i) {
        return EnumDirection.faces[MathHelper.a(i % EnumDirection.faces.length)];
    }

    public static EnumDirection fromType2(int i) {
        return EnumDirection.orient[MathHelper.a(i % EnumDirection.orient.length)];
    }

    public static EnumDirection fromAngle(double d0) {
        return fromType2(MathHelper.floor(d0 / 90.0D + 0.5D) & 3);
    }

    public static EnumDirection getX(Random random) {
        return values()[random.nextInt(values().length)];
    }

    public static EnumDirection getX(EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, EnumDirection.EnumAxis enumdirection_enumaxis) {
        EnumDirection[] aenumdirection = values();
        int i = aenumdirection.length;

        for (EnumDirection enumdirection : aenumdirection) {
            if (enumdirection.getAxisDirection() == enumdirection_enumaxisdirection && enumdirection.k() == enumdirection_enumaxis) {
                return enumdirection;
            }
        }

        throw new IllegalArgumentException("No such direction: " + enumdirection_enumaxisdirection + " " + enumdirection_enumaxis);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public EnumDirection.EnumAxisDirection getAxisDirection() {
        return this.axisDirection;
    }

    public EnumDirection opposite() {
        return fromType1(this.z);
    }

    public EnumDirection getDirectionY() {
        switch (EnumDirection.SyntheticClass_1.b[this.ordinal()]) {
            case 1:
                return EnumDirection.EAST;

            case 2:
                return EnumDirection.SOUTH;

            case 3:
                return EnumDirection.WEST;

            case 4:
                return EnumDirection.NORTH;

            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    public EnumDirection getDirectionCCW() {
        switch (EnumDirection.SyntheticClass_1.b[this.ordinal()]) {
            case 1:
                return EnumDirection.WEST;

            case 2:
                return EnumDirection.NORTH;

            case 3:
                return EnumDirection.EAST;

            case 4:
                return EnumDirection.SOUTH;

            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getAdjacentX() {
        return this.axis == EnumDirection.EnumAxis.X ? this.axisDirection.a() : 0;
    }

    public int getAdjacentY() {
        return this.axis == EnumDirection.EnumAxis.Y ? this.axisDirection.a() : 0;
    }

    public int getAdjacentZ() {
        return this.axis == EnumDirection.EnumAxis.Z ? this.axisDirection.a() : 0;
    }

    public String j() {
        return this.j;
    }

    public EnumDirection.EnumAxis k() {
        return this.axis;
    }

    public String toString() {
        return this.j;
    }

    public String getName() {
        return this.j;
    }

    public enum EnumDirectionLimit implements Predicate<EnumDirection>, Iterable<EnumDirection> {

        HORIZONTAL, VERTICAL;

        EnumDirectionLimit() {
        }

        public EnumDirection[] a() {
            switch (EnumDirection.SyntheticClass_1.c[this.ordinal()]) {
                case 1:
                    return new EnumDirection[]{EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

                case 2:
                    return new EnumDirection[]{EnumDirection.UP, EnumDirection.DOWN};

                default:
                    throw new Error("Someone\'s been tampering with the universe!");
            }
        }

        public EnumDirection a(Random random) {
            EnumDirection[] aenumdirection = this.a();

            return aenumdirection[random.nextInt(aenumdirection.length)];
        }

        public boolean a(EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k().d() == this;
        }

        public Iterator<EnumDirection> iterator() {
            return Iterators.forArray(this.a());
        }

        @Override
        public void forEach(Consumer<? super EnumDirection> action) {
            for (EnumDirection enumDirection : a()) {
                action.accept(enumDirection);
            }
        }

        @Override
        public Spliterator<EnumDirection> spliterator() {
            return null;
        }


        @Override
        public boolean apply(EnumDirection enumDirection) {
            return this.a(enumDirection);
        }
    }

    public enum EnumAxisDirection {

        POSITIVE(1, "Towards positive"), NEGATIVE(-1, "Towards negative");

        private final int c;
        private final String d;

        EnumAxisDirection(int i, String s) {
            this.c = i;
            this.d = s;
        }

        public int a() {
            return this.c;
        }

        public String toString() {
            return this.d;
        }
    }

    public enum EnumAxis implements Predicate<EnumDirection> {

        X("x", EnumDirection.EnumDirectionLimit.HORIZONTAL),
        Y("y", EnumDirection.EnumDirectionLimit.VERTICAL),
        Z("z", EnumDirection.EnumDirectionLimit.HORIZONTAL);

        private static final Map<String, EnumDirection.EnumAxis> d = Maps.newHashMap();

        static {
            EnumDirection.EnumAxis[] aenumdirection_enumaxis = values();
            int i = aenumdirection_enumaxis.length;

            for (EnumAxis enumdirection_enumaxis : aenumdirection_enumaxis) {
                EnumAxis.d.put(enumdirection_enumaxis.a().toLowerCase(), enumdirection_enumaxis);
            }

        }

        private final String e;
        private final EnumDirection.EnumDirectionLimit f;

        EnumAxis(String s, EnumDirection.EnumDirectionLimit enumdirection_enumdirectionlimit) {
            this.e = s;
            this.f = enumdirection_enumdirectionlimit;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this.f == EnumDirection.EnumDirectionLimit.VERTICAL;
        }

        public boolean c() {
            return this.f == EnumDirection.EnumDirectionLimit.HORIZONTAL;
        }

        public String toString() {
            return this.e;
        }

        public boolean a(EnumDirection enumdirection) {
            return enumdirection != null && enumdirection.k() == this;
        }

        public EnumDirection.EnumDirectionLimit d() {
            return this.f;
        }

        public String getName() {
            return this.e;
        }

        @Override
        public boolean apply(EnumDirection enumDirection) {
            return this.a(enumDirection);
        }
    }

    static class SyntheticClass_1 {

        static final int[] a;
        static final int[] b;
        static final int[] c = new int[EnumDirection.EnumDirectionLimit.values().length];

        static {
            try {
                EnumDirection.SyntheticClass_1.c[EnumDirection.EnumDirectionLimit.HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.c[EnumDirection.EnumDirectionLimit.VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror1) {
                ;
            }

            b = new int[EnumDirection.values().length];

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.NORTH.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror2) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.EAST.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror3) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.SOUTH.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror4) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.WEST.ordinal()] = 4;
            } catch (NoSuchFieldError nosuchfielderror5) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.UP.ordinal()] = 5;
            } catch (NoSuchFieldError nosuchfielderror6) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.b[EnumDirection.DOWN.ordinal()] = 6;
            } catch (NoSuchFieldError nosuchfielderror7) {
                ;
            }

            a = new int[EnumDirection.EnumAxis.values().length];

            try {
                EnumDirection.SyntheticClass_1.a[EnumDirection.EnumAxis.X.ordinal()] = 1;
            } catch (NoSuchFieldError nosuchfielderror8) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.a[EnumDirection.EnumAxis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError nosuchfielderror9) {
                ;
            }

            try {
                EnumDirection.SyntheticClass_1.a[EnumDirection.EnumAxis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError nosuchfielderror10) {
                ;
            }

        }
    }
}
