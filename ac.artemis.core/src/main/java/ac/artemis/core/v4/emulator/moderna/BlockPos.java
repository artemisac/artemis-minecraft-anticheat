package ac.artemis.core.v4.emulator.moderna;

import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v5.utils.raytrace.NaivePoint;
import ac.artemis.core.v5.utils.raytrace.Point;
import com.google.common.collect.AbstractIterator;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.Validate;

public class BlockPos extends NaivePoint {
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int NUM_X_BITS = 1 + ModernaMathHelper.log2(ModernaMathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int NUM_Z_BITS = NUM_X_BITS;
   private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
   private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
   private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
   private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
   private static final int INVERSE_START_BITS_Z = NUM_Y_BITS;
   private static final int INVERSE_START_BITS_X = NUM_Y_BITS + NUM_Z_BITS;

   public BlockPos(int x, int y, int z) {
      super(x, y, z);
   }

   public BlockPos(double x, double y, double z) {
      super(x, y, z);
   }

   public BlockPos(Point vec) {
      this(vec.getX(), vec.getY(), vec.getZ());
   }

   public BlockPos(NaivePoint source) {
      this(source.getX(), source.getY(), source.getZ());
   }

   public static long offset(long pos, Direction direction) {
      return offset(pos, direction.getXOffset(), direction.getYOffset(), direction.getZOffset());
   }

   public static long offset(long pos, int dx, int dy, int dz) {
      return pack(unpackX(pos) + dx, unpackY(pos) + dy, unpackZ(pos) + dz);
   }

   public static int unpackX(long packedPos) {
      return (int)(packedPos << 64 - INVERSE_START_BITS_X - NUM_X_BITS >> 64 - NUM_X_BITS);
   }

   public static int unpackY(long packedPos) {
      return (int)(packedPos << 64 - NUM_Y_BITS >> 64 - NUM_Y_BITS);
   }

   public static int unpackZ(long packedPos) {
      return (int)(packedPos << 64 - INVERSE_START_BITS_Z - NUM_Z_BITS >> 64 - NUM_Z_BITS);
   }

   public static BlockPos fromLong(long packedPos) {
      return new BlockPos(unpackX(packedPos), unpackY(packedPos), unpackZ(packedPos));
   }

   public long toLong() {
      return pack(this.getX(), this.getY(), this.getZ());
   }

   public static long pack(int x, int y, int z) {
      long i = 0L;
      i = i | ((long)x & X_MASK) << INVERSE_START_BITS_X;
      i = i | ((long)y & Y_MASK) << 0;
      return i | ((long)z & Z_MASK) << INVERSE_START_BITS_Z;
   }

   public static long atSectionBottomY(long packedPos) {
      return packedPos & -16L;
   }

   public BlockPos add(double x, double y, double z) {
      return x == 0.0D && y == 0.0D && z == 0.0D ? this : new BlockPos((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
   }

   public BlockPos add(int x, int y, int z) {
      return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
   }

   public BlockPos add(NaivePoint vec) {
      return this.add(vec.getX(), vec.getY(), vec.getZ());
   }

   public BlockPos subtract(NaivePoint vec) {
      return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
   }

   public BlockPos up() {
      return this.offset(Direction.UP);
   }

   public BlockPos up(int n) {
      return this.offset(Direction.UP, n);
   }

   public BlockPos down() {
      return this.offset(Direction.DOWN);
   }

   public BlockPos down(int n) {
      return this.offset(Direction.DOWN, n);
   }

   public BlockPos north() {
      return this.offset(Direction.NORTH);
   }

   public BlockPos north(int n) {
      return this.offset(Direction.NORTH, n);
   }

   public BlockPos south() {
      return this.offset(Direction.SOUTH);
   }

   public BlockPos south(int n) {
      return this.offset(Direction.SOUTH, n);
   }

   public BlockPos west() {
      return this.offset(Direction.WEST);
   }

   public BlockPos west(int n) {
      return this.offset(Direction.WEST, n);
   }

   public BlockPos east() {
      return this.offset(Direction.EAST);
   }

   public BlockPos east(int n) {
      return this.offset(Direction.EAST, n);
   }

   public BlockPos offset(Direction facing) {
      return new BlockPos(this.getX() + facing.getXOffset(), this.getY() + facing.getYOffset(), this.getZ() + facing.getZOffset());
   }

   public BlockPos offset(Direction facing, int n) {
      return n == 0 ? this : new BlockPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
   }

   public BlockPos func_241872_a(Direction.Axis p_241872_1_, int p_241872_2_) {
      if (p_241872_2_ == 0) {
         return this;
      } else {
         int i = p_241872_1_ == Direction.Axis.X ? p_241872_2_ : 0;
         int j = p_241872_1_ == Direction.Axis.Y ? p_241872_2_ : 0;
         int k = p_241872_1_ == Direction.Axis.Z ? p_241872_2_ : 0;
         return new BlockPos(this.getX() + i, this.getY() + j, this.getZ() + k);
      }
   }

   /*public BlockPos rotate(Rotation rotationIn) {
      switch(rotationIn) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }*/

   public BlockPos crossProduct(NaivePoint vec) {
      return new BlockPos(this.getY() * vec.getZ() - this.getZ() * vec.getY(), this.getZ() * vec.getX() - this.getX() * vec.getZ(), this.getX() * vec.getY() - this.getY() * vec.getX());
   }

   public BlockPos toImmutable() {
      return this;
   }

   public BlockPos.Mutable toMutable() {
      return new BlockPos.Mutable(this.getX(), this.getY(), this.getZ());
   }

   public static Iterable<BlockPos> getRandomPositions(Random rand, int amount, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      int i = maxX - minX + 1;
      int j = maxY - minY + 1;
      int k = maxZ - minZ + 1;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final BlockPos.Mutable pos = new BlockPos.Mutable();
            int remainingAmount = amount;

            protected BlockPos computeNext() {
               if (this.remainingAmount <= 0) {
                  return this.endOfData();
               } else {
                  BlockPos blockpos = this.pos.setPos(minX + rand.nextInt(i), minY + rand.nextInt(j), minZ + rand.nextInt(k));
                  --this.remainingAmount;
                  return blockpos;
               }
            }
         };
      };
   }

   public static Iterable<BlockPos> getProximitySortedBoxPositionsIterator(BlockPos pos, int xWidth, int yHeight, int zWidth) {
      int i = xWidth + yHeight + zWidth;
      int j = pos.getX();
      int k = pos.getY();
      int l = pos.getZ();
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final BlockPos.Mutable coordinateIterator = new BlockPos.Mutable();
            private int field_239604_i_;
            private int field_239605_j_;
            private int field_239606_k_;
            private int field_239607_l_;
            private int field_239608_m_;
            private boolean field_239609_n_;

            protected BlockPos computeNext() {
               if (this.field_239609_n_) {
                  this.field_239609_n_ = false;
                  this.coordinateIterator.setZ(l - (this.coordinateIterator.getZ() - l));
                  return this.coordinateIterator;
               } else {
                  BlockPos blockpos;
                  for(blockpos = null; blockpos == null; ++this.field_239608_m_) {
                     if (this.field_239608_m_ > this.field_239606_k_) {
                        ++this.field_239607_l_;
                        if (this.field_239607_l_ > this.field_239605_j_) {
                           ++this.field_239604_i_;
                           if (this.field_239604_i_ > i) {
                              return this.endOfData();
                           }

                           this.field_239605_j_ = Math.min(xWidth, this.field_239604_i_);
                           this.field_239607_l_ = -this.field_239605_j_;
                        }

                        this.field_239606_k_ = Math.min(yHeight, this.field_239604_i_ - Math.abs(this.field_239607_l_));
                        this.field_239608_m_ = -this.field_239606_k_;
                     }

                     int i1 = this.field_239607_l_;
                     int j1 = this.field_239608_m_;
                     int k1 = this.field_239604_i_ - Math.abs(i1) - Math.abs(j1);
                     if (k1 <= zWidth) {
                        this.field_239609_n_ = k1 != 0;
                        blockpos = this.coordinateIterator.setPos(j + i1, k + j1, l + k1);
                     }
                  }

                  return blockpos;
               }
            }
         };
      };
   }

   public static Optional<BlockPos> getClosestMatchingPosition(BlockPos pos, int width, int height, Predicate<BlockPos> posFilter) {
      return getProximitySortedBoxPositions(pos, width, height, width).filter(posFilter).findFirst();
   }

   public static Stream<BlockPos> getProximitySortedBoxPositions(BlockPos pos, int xWidth, int yHeight, int zWidth) {
      return StreamSupport.stream(getProximitySortedBoxPositionsIterator(pos, xWidth, yHeight, zWidth).spliterator(), false);
   }

   public static Iterable<BlockPos> getAllInBoxMutable(BlockPos firstPos, BlockPos secondPos) {
      return getAllInBoxMutable(Math.min(firstPos.getX(), secondPos.getX()), Math.min(firstPos.getY(), secondPos.getY()), Math.min(firstPos.getZ(), secondPos.getZ()), Math.max(firstPos.getX(), secondPos.getX()), Math.max(firstPos.getY(), secondPos.getY()), Math.max(firstPos.getZ(), secondPos.getZ()));
   }

   public static Stream<BlockPos> getAllInBox(BlockPos firstPos, BlockPos secondPos) {
      return StreamSupport.stream(getAllInBoxMutable(firstPos, secondPos).spliterator(), false);
   }

   /*public static Stream<BlockPos> getAllInBox(BoundingBox box) {
      return getAllInBox(Math.min(box.minX, box.maxX),
              Math.min(box.minY, box.maxY),
              Math.min(box.minZ, box.maxZ),
              Math.max(box.minX, box.maxX),
              Math.max(box.minY, box.maxY),
              Math.max(box.minZ, box.maxZ));
   }*/

   public static Stream<BlockPos> getAllInBox(BoundingBox aabb) {
      return getAllInBox(ModernaMathHelper.floor(aabb.minX), ModernaMathHelper.floor(aabb.minY), ModernaMathHelper.floor(aabb.minZ), ModernaMathHelper.floor(aabb.maxX), ModernaMathHelper.floor(aabb.maxY), ModernaMathHelper.floor(aabb.maxZ));
   }

   public static Stream<BlockPos> getAllInBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      return StreamSupport.stream(getAllInBoxMutable(minX, minY, minZ, maxX, maxY, maxZ).spliterator(), false);
   }

   public static Iterable<BlockPos> getAllInBoxMutable(int x1, int y1, int z1, int x2, int y2, int z2) {
      int i = x2 - x1 + 1;
      int j = y2 - y1 + 1;
      int k = z2 - z1 + 1;
      int l = i * j * k;
      return () -> {
         return new AbstractIterator<BlockPos>() {
            private final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            private int totalAmount;

            protected BlockPos computeNext() {
               if (this.totalAmount == l) {
                  return this.endOfData();
               } else {
                  int i1 = this.totalAmount % i;
                  int j1 = this.totalAmount / i;
                  int k1 = j1 % j;
                  int l1 = j1 / j;
                  ++this.totalAmount;
                  return this.mutablePos.setPos(x1 + i1, y1 + k1, z1 + l1);
               }
            }
         };
      };
   }

   public static Iterable<BlockPos.Mutable> func_243514_a(BlockPos p_243514_0_, int p_243514_1_, Direction p_243514_2_, Direction p_243514_3_) {
      Validate.validState(p_243514_2_.getAxis() != p_243514_3_.getAxis(), "The two directions cannot be on the same axis");
      return () -> {
         return new AbstractIterator<BlockPos.Mutable>() {
            private final Direction[] field_243520_e = new Direction[]{p_243514_2_, p_243514_3_, p_243514_2_.getOpposite(), p_243514_3_.getOpposite()};
            private final BlockPos.Mutable field_243521_f = p_243514_0_.toMutable().move(p_243514_3_);
            private final int field_243522_g = 4 * p_243514_1_;
            private int field_243523_h = -1;
            private int field_243524_i;
            private int field_243525_j;
            private int field_243526_k = this.field_243521_f.getX();
            private int field_243527_l = this.field_243521_f.getY();
            private int field_243528_m = this.field_243521_f.getZ();

            protected BlockPos.Mutable computeNext() {
               this.field_243521_f.setPos(this.field_243526_k, this.field_243527_l, this.field_243528_m).move(this.field_243520_e[(this.field_243523_h + 4) % 4]);
               this.field_243526_k = this.field_243521_f.getX();
               this.field_243527_l = this.field_243521_f.getY();
               this.field_243528_m = this.field_243521_f.getZ();
               if (this.field_243525_j >= this.field_243524_i) {
                  if (this.field_243523_h >= this.field_243522_g) {
                     return this.endOfData();
                  }

                  ++this.field_243523_h;
                  this.field_243525_j = 0;
                  this.field_243524_i = this.field_243523_h / 2 + 1;
               }

               ++this.field_243525_j;
               return this.field_243521_f;
            }
         };
      };
   }

   public static class Mutable extends BlockPos {
      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(int x_, int y_, int z_) {
         super(x_, y_, z_);
      }

      public Mutable(double x, double y, double z) {
         this(ModernaMathHelper.floor(x), ModernaMathHelper.floor(y), ModernaMathHelper.floor(z));
      }

      public BlockPos add(double x, double y, double z) {
         return super.add(x, y, z).toImmutable();
      }

      public BlockPos add(int x, int y, int z) {
         return super.add(x, y, z).toImmutable();
      }

      public BlockPos offset(Direction facing, int n) {
         return super.offset(facing, n).toImmutable();
      }

      public BlockPos func_241872_a(Direction.Axis p_241872_1_, int p_241872_2_) {
         return super.func_241872_a(p_241872_1_, p_241872_2_).toImmutable();
      }

      /*public BlockPos rotate(Rotation rotationIn) {
         return super.rotate(rotationIn).toImmutable();
      }*/

      public BlockPos.Mutable setPos(int xIn, int yIn, int zIn) {
         this.setX(xIn);
         this.setY(yIn);
         this.setZ(zIn);
         return this;
      }

      public BlockPos.Mutable setPos(double xIn, double yIn, double zIn) {
         return this.setPos(ModernaMathHelper.floor(xIn), ModernaMathHelper.floor(yIn), ModernaMathHelper.floor(zIn));
      }

      public BlockPos.Mutable setPos(NaivePoint vec) {
         return this.setPos(vec.getX(), vec.getY(), vec.getZ());
      }

      public BlockPos.Mutable setPos(long packedPos) {
         return this.setPos(unpackX(packedPos), unpackY(packedPos), unpackZ(packedPos));
      }

      public BlockPos.Mutable setPos(AxisRotation rotation, int x, int y, int z) {
         return this.setPos(rotation.getCoordinate(x, y, z, Direction.Axis.X), rotation.getCoordinate(x, y, z, Direction.Axis.Y), rotation.getCoordinate(x, y, z, Direction.Axis.Z));
      }

      public BlockPos.Mutable setAndMove(NaivePoint pos, Direction direction) {
         return this.setPos(pos.getX() + direction.getXOffset(), pos.getY() + direction.getYOffset(), pos.getZ() + direction.getZOffset());
      }

      public BlockPos.Mutable setAndOffset(NaivePoint pos, int offsetX, int offsetY, int offsetZ) {
         return this.setPos(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ);
      }

      public BlockPos.Mutable move(Direction facing) {
         return this.move(facing, 1);
      }

      public BlockPos.Mutable move(Direction facing, int n) {
         return this.setPos(this.getX() + facing.getXOffset() * n, this.getY() + facing.getYOffset() * n, this.getZ() + facing.getZOffset() * n);
      }

      public BlockPos.Mutable move(int xIn, int yIn, int zIn) {
         return this.setPos(this.getX() + xIn, this.getY() + yIn, this.getZ() + zIn);
      }

      public BlockPos.Mutable func_243531_h(NaivePoint p_243531_1_) {
         return this.setPos(this.getX() + p_243531_1_.getX(), this.getY() + p_243531_1_.getY(), this.getZ() + p_243531_1_.getZ());
      }

      public BlockPos.Mutable clampAxisCoordinate(Direction.Axis axis, int min, int max) {
         switch(axis) {
         case X:
            return this.setPos(ModernaMathHelper.clamp(this.getX(), min, max), this.getY(), this.getZ());
         case Y:
            return this.setPos(this.getX(), ModernaMathHelper.clamp(this.getY(), min, max), this.getZ());
         case Z:
            return this.setPos(this.getX(), this.getY(), ModernaMathHelper.clamp(this.getZ(), min, max));
         default:
            throw new IllegalStateException("Unable to clamp axis " + axis);
         }
      }

      public void setX(int xIn) {
         super.setX(xIn);
      }

      public void setY(int yIn) {
         super.setY(yIn);
      }

      public void setZ(int zIn) {
         super.setZ(zIn);
      }

      public BlockPos toImmutable() {
         return new BlockPos(this);
      }
   }
}
