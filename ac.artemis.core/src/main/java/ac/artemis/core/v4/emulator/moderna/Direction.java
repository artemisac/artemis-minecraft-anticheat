package ac.artemis.core.v4.emulator.moderna;

import ac.artemis.packet.minecraft.entity.Entity;
import ac.artemis.core.v5.utils.bounding.BlockPos;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import cc.ghast.packet.nms.MathHelper;
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Direction {
   DOWN(0, 1, -1, "down", Direction.AxisDirection.NEGATIVE, Direction.Axis.Y, new NaivePoint(0, -1, 0)),
   UP(1, 0, -1, "up", Direction.AxisDirection.POSITIVE, Direction.Axis.Y, new NaivePoint(0, 1, 0)),
   NORTH(2, 3, 2, "north", Direction.AxisDirection.NEGATIVE, Direction.Axis.Z, new NaivePoint(0, 0, -1)),
   SOUTH(3, 2, 0, "south", Direction.AxisDirection.POSITIVE, Direction.Axis.Z, new NaivePoint(0, 0, 1)),
   WEST(4, 5, 1, "west", Direction.AxisDirection.NEGATIVE, Direction.Axis.X, new NaivePoint(-1, 0, 0)),
   EAST(5, 4, 3, "east", Direction.AxisDirection.POSITIVE, Direction.Axis.X, new NaivePoint(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final Direction.Axis axis;
   private final Direction.AxisDirection axisDirection;
   private final NaivePoint directionVec;
   private static final Direction[] VALUES = values();
   private static final Map<String, Direction> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(Direction::getName2, (direction) -> {
      return direction;
   }));
   private static final Direction[] BY_INDEX = Arrays.stream(VALUES).sorted(Comparator.comparingInt((direction) -> {
      return direction.index;
   })).toArray((size) -> {
      return new Direction[size];
   });
   private static final Direction[] BY_HORIZONTAL_INDEX = Arrays.stream(VALUES).filter((direction) -> {
      return direction.getAxis().isHorizontal();
   }).sorted(Comparator.comparingInt((direction) -> {
      return direction.horizontalIndex;
   })).toArray((size) -> {
      return new Direction[size];
   });
   private static final Long2ObjectMap<Direction> BY_LONG = Arrays.stream(VALUES).collect(Collectors.toMap((direction) -> {
      return (new BlockPos(direction.getDirectionVec())).toLong();
   }, (direction) -> {
      return direction;
   }, (direction1, direction2) -> {
      throw new IllegalArgumentException("Duplicate keys");
   }, Long2ObjectOpenHashMap::new));

   private Direction(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, Direction.AxisDirection axisDirectionIn,
                     Direction.Axis axisIn, NaivePoint directionVecIn) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   public static Direction[] getFacingDirections(Entity entityIn) {
      float f = entityIn.getLocation().getPitch() * ((float)Math.PI / 180F);
      float f1 = -entityIn.getLocation().getYaw() * ((float)Math.PI / 180F);
      float f2 = MathHelper.sin(f);
      float f3 = MathHelper.cos(f);
      float f4 = MathHelper.sin(f1);
      float f5 = MathHelper.cos(f1);
      boolean flag = f4 > 0.0F;
      boolean flag1 = f2 < 0.0F;
      boolean flag2 = f5 > 0.0F;
      float f6 = flag ? f4 : -f4;
      float f7 = flag1 ? -f2 : f2;
      float f8 = flag2 ? f5 : -f5;
      float f9 = f6 * f3;
      float f10 = f8 * f3;
      Direction direction = flag ? EAST : WEST;
      Direction direction1 = flag1 ? UP : DOWN;
      Direction direction2 = flag2 ? SOUTH : NORTH;
      if (f6 > f8) {
         if (f7 > f9) {
            return compose(direction1, direction, direction2);
         } else {
            return f10 > f7 ? compose(direction, direction2, direction1) : compose(direction, direction1, direction2);
         }
      } else if (f7 > f10) {
         return compose(direction1, direction2, direction);
      } else {
         return f9 > f7 ? compose(direction2, direction, direction1) : compose(direction2, direction1, direction);
      }
   }

   private static Direction[] compose(Direction first, Direction second, Direction third) {
      return new Direction[]{first, second, third, third.getOpposite(), second.getOpposite(), first.getOpposite()};
   }

   /*public static Direction rotateFace(Matrix4f matrixIn, Direction directionIn) {
      NaivePoint NaivePoint = directionIn.getDirectionVec();
      Vector4f vector4f = new Vector4f((float)NaivePoint.getX(), (float)NaivePoint.getY(), (float)NaivePoint.getZ(), 0.0F);
      vector4f.transform(matrixIn);
      return getFacingFromVector(vector4f.getX(), vector4f.getY(), vector4f.getZ());
   }*/

   public Quaternion getRotation() {
      Quaternion quaternion = Vector3f.XP.rotationDegrees(90.0F);
      switch(this) {
      case DOWN:
         return Vector3f.XP.rotationDegrees(180.0F);
      case UP:
         return Quaternion.ONE.copy();
      case NORTH:
         quaternion.multiply(Vector3f.ZP.rotationDegrees(180.0F));
         return quaternion;
      case SOUTH:
         return quaternion;
      case WEST:
         quaternion.multiply(Vector3f.ZP.rotationDegrees(90.0F));
         return quaternion;
      case EAST:
      default:
         quaternion.multiply(Vector3f.ZP.rotationDegrees(-90.0F));
         return quaternion;
      }
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public Direction.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public Direction getOpposite() {
      return byIndex(this.opposite);
   }

   public Direction rotateY() {
      switch(this) {
      case NORTH:
         return EAST;
      case SOUTH:
         return WEST;
      case WEST:
         return NORTH;
      case EAST:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   public Direction rotateYCCW() {
      switch(this) {
      case NORTH:
         return WEST;
      case SOUTH:
         return EAST;
      case WEST:
         return SOUTH;
      case EAST:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getXOffset() {
      return this.directionVec.getX();
   }

   public int getYOffset() {
      return this.directionVec.getY();
   }

   public int getZOffset() {
      return this.directionVec.getZ();
   }

   public Vector3f toVector3f() {
      return new Vector3f((float)this.getXOffset(), (float)this.getYOffset(), (float)this.getZOffset());
   }

   public String getName2() {
      return this.name;
   }

   public Direction.Axis getAxis() {
      return this.axis;
   }

   public static Direction byName(String name) {
      return name == null ? null : NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
   }

   public static Direction byIndex(int index) {
      return BY_INDEX[Math.abs(index % BY_INDEX.length)];
   }

   public static Direction byHorizontalIndex(int horizontalIndexIn) {
      return BY_HORIZONTAL_INDEX[Math.abs(horizontalIndexIn % BY_HORIZONTAL_INDEX.length)];
   }

   /*public static Direction byLong(int x, int y, int z) {
      return BY_LONG.get(BlockPos.pack(x, y, z));
   }*/

   public static Direction fromAngle(double angle) {
      return byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
   }

   public static Direction getFacingFromAxisDirection(Direction.Axis axisIn, Direction.AxisDirection axisDirectionIn) {
      switch(axisIn) {
      case X:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? EAST : WEST;
      case Y:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? UP : DOWN;
      case Z:
      default:
         return axisDirectionIn == Direction.AxisDirection.POSITIVE ? SOUTH : NORTH;
      }
   }

   public float getHorizontalAngle() {
      return (float)((this.horizontalIndex & 3) * 90);
   }

   /*public static Direction getRandomDirection(Random rand) {
      return Util.getRandomObject(VALUES, rand);
   }*/

   public static Direction getFacingFromVector(double x, double y, double z) {
      return getFacingFromVector((float)x, (float)y, (float)z);
   }

   public static Direction getFacingFromVector(float x, float y, float z) {
      Direction direction = NORTH;
      float f = Float.MIN_VALUE;

      for(Direction direction1 : VALUES) {
         float f1 = x * (float)direction1.directionVec.getX() + y * (float)direction1.directionVec.getY() + z * (float)direction1.directionVec.getZ();
         if (f1 > f) {
            f = f1;
            direction = direction1;
         }
      }

      return direction;
   }

   public String toString() {
      return this.name;
   }

   public String getString() {
      return this.name;
   }

   public static Direction getFacingFromAxis(Direction.AxisDirection axisDirectionIn, Direction.Axis axisIn) {
      for(Direction direction : VALUES) {
         if (direction.getAxisDirection() == axisDirectionIn && direction.getAxis() == axisIn) {
            return direction;
         }
      }

      throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
   }

   public NaivePoint getDirectionVec() {
      return this.directionVec;
   }

   public boolean hasOrientation(float degrees) {
      float f = degrees * ((float)Math.PI / 180F);
      float f1 = -MathHelper.sin(f);
      float f2 = MathHelper.cos(f);
      return (float)this.directionVec.getX() * f1 + (float)this.directionVec.getZ() * f2 > 0.0F;
   }

   public static enum Axis implements Predicate<Direction> {
      X("x") {
         public int getCoordinate(int x, int y, int z) {
            return x;
         }

         public double getCoordinate(double x, double y, double z) {
            return x;
         }
      },
      Y("y") {
         public int getCoordinate(int x, int y, int z) {
            return y;
         }

         public double getCoordinate(double x, double y, double z) {
            return y;
         }
      },
      Z("z") {
         public int getCoordinate(int x, int y, int z) {
            return z;
         }

         public double getCoordinate(double x, double y, double z) {
            return z;
         }
      };

      private static final Direction.Axis[] VALUES = values();
      private static final Map<String, Direction.Axis> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(Direction.Axis::getName2, (axis) -> {
         return axis;
      }));
      private final String name;

      private Axis(String nameIn) {
         this.name = nameIn;
      }

      public static Direction.Axis byName(String name) {
         return NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this == Y;
      }

      public boolean isHorizontal() {
         return this == X || this == Z;
      }

      public String toString() {
         return this.name;
      }


      public boolean test(Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis() == this;
      }

      public Direction.Plane getPlane() {
         switch(this) {
         case X:
         case Z:
            return Direction.Plane.HORIZONTAL;
         case Y:
            return Direction.Plane.VERTICAL;
         default:
            throw new Error("Someone's been tampering with the universe!");
         }
      }

      public String getString() {
         return this.name;
      }

      public abstract int getCoordinate(int x, int y, int z);

      public abstract double getCoordinate(double x, double y, double z);
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }

      public Direction.AxisDirection inverted() {
         return this == POSITIVE ? NEGATIVE : POSITIVE;
      }
   }

   public static enum Plane implements Iterable<Direction>, Predicate<Direction> {
      HORIZONTAL(new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}, new Direction.Axis[]{Direction.Axis.X, Direction.Axis.Z}),
      VERTICAL(new Direction[]{Direction.UP, Direction.DOWN}, new Direction.Axis[]{Direction.Axis.Y});

      private final Direction[] facingValues;
      private final Direction.Axis[] axisValues;

      private Plane(Direction[] facingValuesIn, Direction.Axis[] axisValuesIn) {
         this.facingValues = facingValuesIn;
         this.axisValues = axisValuesIn;
      }


      public boolean test(Direction p_test_1_) {
         return p_test_1_ != null && p_test_1_.getAxis().getPlane() == this;
      }

      public Iterator<Direction> iterator() {
         return Iterators.forArray(this.facingValues);
      }

      public Stream<Direction> getDirectionValues() {
         return Arrays.stream(this.facingValues);
      }
   }
}
