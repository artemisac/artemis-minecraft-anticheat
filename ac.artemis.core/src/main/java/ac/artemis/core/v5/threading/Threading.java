package ac.artemis.core.v5.threading;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@UtilityClass
public class Threading {
    private final Map<String, ExecutorService> services = new WeakHashMap<>();
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
            .setNameFormat("artemis-core-%d")
            .setPriority(7)
            .setUncaughtExceptionHandler(new ThreadedExceptionHandler())
            .build();

    @Deprecated
    public ExecutorService startService(final String name) {
        if (services.containsKey(name)) {
            throw new IllegalStateException("Duplicate artemis thread (" + name + " is already registered!)");
        }

        final ExecutorService service = Executors.newSingleThreadExecutor(threadFactory);

        services.put(name, service);
        return service;
    }

    public void stopService(final String name) {
        final ExecutorService executorService = services.get(name);

        if (executorService == null)
            return;

        executorService.shutdownNow().forEach(Runnable::run);
        services.remove(name);
    }

    public ExecutorService getOrStartService(final String name) {
        if (services.containsKey(name)) {
            ExecutorService service = services.get(name);

            if (service.isTerminated()) {
                service = Executors.newSingleThreadExecutor(threadFactory);

                services.put(name, service);
            }

            return service;
        }

        return startService(name);
    }

    public void killAll() {
        for (final Map.Entry<String, ExecutorService> stringExecutorServiceEntry : new HashSet<>(services.entrySet())) {
            stopService(stringExecutorServiceEntry.getKey());
        }

        services.clear();
    }
}
