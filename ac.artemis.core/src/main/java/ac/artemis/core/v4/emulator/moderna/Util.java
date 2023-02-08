package ac.artemis.core.v4.emulator.moderna;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {
   private static final AtomicInteger NEXT_SERVER_WORKER_ID = new AtomicInteger(1);
   public static LongSupplier nanoTimeSupplier = System::nanoTime;
   public static final UUID DUMMY_UUID = new UUID(0L, 0L);

   public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> toMapCollector() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static long milliTime() {
      return nanoTime() / 1000000L;
   }

   public static double milliTimeWithAD() {
      return nanoTime() / 1000000D;
   }

   public static long nanoTime() {
      return nanoTimeSupplier.getAsLong();
   }

   public static long millisecondsSinceEpoch() {
      return Instant.now().toEpochMilli();
   }




   private static void shutdownService(ExecutorService p_240985_0_) {
      p_240985_0_.shutdown();

      boolean flag;
      try {
         flag = p_240985_0_.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException interruptedexception) {
         flag = false;
      }

      if (!flag) {
         p_240985_0_.shutdownNow();
      }

   }

   public static <T> CompletableFuture<T> completedExceptionallyFuture(Throwable throwableIn) {
      CompletableFuture<T> completablefuture = new CompletableFuture<>();
      completablefuture.completeExceptionally(throwableIn);
      return completablefuture;
   }

   public static void toRuntimeException(Throwable throwableIn) {
      throw throwableIn instanceof RuntimeException ? (RuntimeException)throwableIn : new RuntimeException(throwableIn);
   }


   public static Util.OS getOSType() {
      String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      if (s.contains("win")) {
         return Util.OS.WINDOWS;
      } else if (s.contains("mac")) {
         return Util.OS.OSX;
      } else if (s.contains("solaris")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("sunos")) {
         return Util.OS.SOLARIS;
      } else if (s.contains("linux")) {
         return Util.OS.LINUX;
      } else {
         return s.contains("unix") ? Util.OS.LINUX : Util.OS.UNKNOWN;
      }
   }

   public static Stream<String> getJvmFlags() {
      RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
      return runtimemxbean.getInputArguments().stream().filter((p_211566_0_) -> {
         return p_211566_0_.startsWith("-X");
      });
   }

   public static <T> T getLast(List<T> listIn) {
      return listIn.get(listIn.size() - 1);
   }

   public static <T> T getElementAfter(Iterable<T> iterable,  T element) {
      Iterator<T> iterator = iterable.iterator();
      T t = iterator.next();
      if (element != null) {
         T t1 = t;

         while(t1 != element) {
            if (iterator.hasNext()) {
               t1 = iterator.next();
            }
         }

         if (iterator.hasNext()) {
            return iterator.next();
         }
      }

      return t;
   }

   public static <T> T getElementBefore(Iterable<T> iterable,  T current) {
      Iterator<T> iterator = iterable.iterator();

      T t;
      T t1;
      for(t = null; iterator.hasNext(); t = t1) {
         t1 = iterator.next();
         if (t1 == current) {
            if (t == null) {
               t = (T)(iterator.hasNext() ? Iterators.getLast(iterator) : current);
            }
            break;
         }
      }

      return t;
   }

   public static <T> T make(Supplier<T> supplier) {
      return supplier.get();
   }

   public static <T> T make(T object, Consumer<T> consumer) {
      consumer.accept(object);
      return object;
   }

   public static <V> CompletableFuture<List<V>> gather(List<? extends CompletableFuture<? extends V>> futuresIn) {
      List<V> list = Lists.newArrayListWithCapacity(futuresIn.size());
      CompletableFuture<?>[] completablefuture = new CompletableFuture[futuresIn.size()];
      CompletableFuture<Void> completablefuture1 = new CompletableFuture<>();
      futuresIn.forEach((p_215083_3_) -> {
         int i = list.size();
         list.add((V)null);
         completablefuture[i] = p_215083_3_.whenComplete((p_215085_3_, p_215085_4_) -> {
            if (p_215085_4_ != null) {
               completablefuture1.completeExceptionally(p_215085_4_);
            } else {
               list.set(i, p_215085_3_);
            }

         });
      });
      return CompletableFuture.allOf(completablefuture).applyToEither(completablefuture1, (p_215089_1_) -> {
         return list;
      });
   }

   public static <T> Optional<T> acceptOrElse(Optional<T> opt, Consumer<T> consumer, Runnable orElse) {
      if (opt.isPresent()) {
         consumer.accept(opt.get());
      } else {
         orElse.run();
      }

      return opt;
   }

   public static Runnable namedRunnable(Runnable runnableIn, Supplier<String> supplierIn) {
      return runnableIn;
   }


   public static String getMessage(Throwable throwableIn) {
      if (throwableIn.getCause() != null) {
         return getMessage(throwableIn.getCause());
      } else {
         return throwableIn.getMessage() != null ? throwableIn.getMessage() : throwableIn.toString();
      }
   }

   public static <T> T getRandomObject(T[] selections, Random rand) {
      return selections[rand.nextInt(selections.length)];
   }

   public static int getRandomInt(int[] selections, Random rand) {
      return selections[rand.nextInt(selections.length)];
   }

   private static BooleanSupplier func_244363_a(final Path p_244363_0_, final Path p_244363_1_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.move(p_244363_0_, p_244363_1_);
               return true;
            } catch (IOException ioexception) {
               ioexception.printStackTrace();
               return false;
            }
         }

         public String toString() {
            return "rename " + p_244363_0_ + " to " + p_244363_1_;
         }
      };
   }

   private static BooleanSupplier func_244362_a(final Path p_244362_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            try {
               Files.deleteIfExists(p_244362_0_);
               return true;
            } catch (IOException ioexception) {
               ioexception.printStackTrace();
               return false;
            }
         }

         public String toString() {
            return "delete old " + p_244362_0_;
         }
      };
   }

   private static BooleanSupplier func_244366_b(final Path p_244366_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return !Files.exists(p_244366_0_);
         }

         public String toString() {
            return "verify that " + p_244366_0_ + " is deleted";
         }
      };
   }

   private static BooleanSupplier func_244367_c(final Path p_244367_0_) {
      return new BooleanSupplier() {
         public boolean getAsBoolean() {
            return Files.isRegularFile(p_244367_0_);
         }

         public String toString() {
            return "verify that " + p_244367_0_ + " is present";
         }
      };
   }

   private static boolean func_244365_a(BooleanSupplier... p_244365_0_) {
      for(BooleanSupplier booleansupplier : p_244365_0_) {
         if (!booleansupplier.getAsBoolean()) {
            System.err.println("Failed to execute " + booleansupplier.toString());
            return false;
         }
      }

      return true;
   }

   private static boolean func_244359_a(int p_244359_0_, String p_244359_1_, BooleanSupplier... p_244359_2_) {
      for(int i = 0; i < p_244359_0_; ++i) {
         if (func_244365_a(p_244359_2_)) {
            return true;
         }

         System.err.printf("Failed to %s, retrying %d/%d%n", p_244359_1_, i, p_244359_0_);
      }

      System.err.printf("Failed to %s, aborting, progress might be lost", p_244359_1_);
      return false;
   }

   public static void backupThenUpdate(File current, File latest, File oldBackup) {
      func_244364_a(current.toPath(), latest.toPath(), oldBackup.toPath());
   }

   public static void func_244364_a(Path p_244364_0_, Path p_244364_1_, Path p_244364_2_) {
      int i = 10;
      if (!Files.exists(p_244364_0_) || func_244359_a(10, "create backup " + p_244364_2_, func_244362_a(p_244364_2_), func_244363_a(p_244364_0_, p_244364_2_), func_244367_c(p_244364_2_))) {
         if (func_244359_a(10, "remove old " + p_244364_0_, func_244362_a(p_244364_0_), func_244366_b(p_244364_0_))) {
            if (!func_244359_a(10, "replace " + p_244364_0_ + " with " + p_244364_1_, func_244363_a(p_244364_1_, p_244364_0_), func_244367_c(p_244364_0_))) {
               func_244359_a(10, "restore " + p_244364_0_ + " from " + p_244364_2_, func_244363_a(p_244364_2_, p_244364_0_), func_244367_c(p_244364_0_));
            }

         }
      }
   }

   public static int func_240980_a_(String p_240980_0_, int p_240980_1_, int p_240980_2_) {
      int i = p_240980_0_.length();
      if (p_240980_2_ >= 0) {
         for(int j = 0; p_240980_1_ < i && j < p_240980_2_; ++j) {
            if (Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_++)) && p_240980_1_ < i && Character.isLowSurrogate(p_240980_0_.charAt(p_240980_1_))) {
               ++p_240980_1_;
            }
         }
      } else {
         for(int k = p_240980_2_; p_240980_1_ > 0 && k < 0; ++k) {
            --p_240980_1_;
            if (Character.isLowSurrogate(p_240980_0_.charAt(p_240980_1_)) && p_240980_1_ > 0 && Character.isHighSurrogate(p_240980_0_.charAt(p_240980_1_ - 1))) {
               --p_240980_1_;
            }
         }
      }

      return p_240980_1_;
   }

   public static Consumer<String> func_240982_a_(String prefix, Consumer<String> p_240982_1_) {
      return (p_240986_2_) -> {
         p_240982_1_.accept(prefix + p_240986_2_);
      };
   }

   public static void func_240984_a_(Path p_240984_0_, Path p_240984_1_, Path p_240984_2_) throws IOException {
      Path path = p_240984_0_.relativize(p_240984_2_);
      Path path1 = p_240984_1_.resolve(path);
      Files.copy(p_240984_2_, path1);
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         protected String[] getOpenCommandLine(URL url) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
         }
      },
      OSX {
         protected String[] getOpenCommandLine(URL url) {
            return new String[]{"open", url.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

   }
}
