package ac.artemis.core.v5.sdk.flush.handler;

import ac.artemis.packet.minecraft.Server;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.data.PlayerData;
import ac.artemis.core.v4.packet.PacketExecutor;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v5.sdk.flush.FlushSdkFeature;
import ac.artemis.core.v5.sdk.flush.FlushSdkHandler;
import ac.artemis.core.v5.sdk.flush.component.standard.FlushNonSdkList;
import ac.artemis.core.v5.sdk.flush.component.standard.FlushNonSdkListener;
import ac.artemis.core.v5.sdk.flush.impl.StandardTickingFlushFeature;
import ac.artemis.core.v5.utils.pledge.ReflectionUtil;
import cc.ghast.packet.reflections.ReflectUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

public class FlushSdkHandlerStandard implements FlushSdkHandler {
    private static final Collection<String> TICKABLE_CLASS_NAMES = Arrays.asList("IUpdatePlayerListBox", "ITickable", "Runnable");
    private Field hookedField;
    private FlushNonSdkListener listener;

    @Getter
    private Set<FlushSdkFeature> features;

    @Override
    public void init() throws IllegalAccessException {
        listener = new FlushNonSdkListener(this);

        Server.v().getScheduler().runTaskTimerAsynchronously(new Runnable() {
            @Override
            public void run() {
                for (Pair<PlayerData, PacketExecutor> value : Artemis.v().getApi().getPlayerDataManager()
                        .getPlayerDataMap().values()) {
                    listener.onPreFlush(value.getX());
                }
            }
        }, 0,0);

        this.injectPledge();

        features = new HashSet<>(Collections.singleton(
                new StandardTickingFlushFeature()
        ));
    }



    @Override
    public void disinit() throws IllegalAccessException {
        listener = null;

        this.ejectPledge();
    }

    
    public void injectPledge() throws IllegalAccessException {
        Object server = ReflectUtil.MINECRAFT_SERVER;
        Class<?> serverClass = ReflectUtil.MINECRAFT_SERVER_CLAZZ;

        // Inject our hooked list for end of tick
        for (Field field : serverClass.getDeclaredFields()) {
            if (field.getType().equals(List.class)) {
                // Check if type parameters match one of the tickable class names used throughout different versions
                Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                if (!TICKABLE_CLASS_NAMES.contains(genericType.getSimpleName())) {
                    continue;
                }

                field.setAccessible(true);

                // Use a list wrapper to check when the size method is called
                FlushNonSdkList<?> wrapper = new FlushNonSdkList<Object>((List) field.get(server), new Runnable() {
                    @Override
                    public void run() {
                        for (Pair<PlayerData, PacketExecutor> value : Artemis.v().getApi().getPlayerDataManager()
                                .getPlayerDataMap().values()) {
                            listener.onPostFlush(value.getX());
                        }
                    }
                });

                ReflectionUtil.setUnsafe(server, field, wrapper);
                this.hookedField = field;
                break;
            }
        }
    }

    
    public void ejectPledge() throws IllegalAccessException {
        if (this.hookedField != null) {
            Object server = ReflectUtil.MINECRAFT_SERVER;

            FlushNonSdkList<?> hookedListWrapper = (FlushNonSdkList<?>) this.hookedField.get(server);

            ReflectionUtil.setUnsafe(server, this.hookedField, hookedListWrapper.getBase());
            this.hookedField = null;
        }
    }
}
