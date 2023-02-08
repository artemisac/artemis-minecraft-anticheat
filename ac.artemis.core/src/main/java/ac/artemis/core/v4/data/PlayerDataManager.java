package ac.artemis.core.v4.data;


import ac.artemis.anticheat.api.listener.InjectListener;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.api.Manager;
import ac.artemis.core.v4.check.TickHandler;
import ac.artemis.core.v4.check.manager.AbstractCheckManager;
import ac.artemis.core.v4.data.utils.StaffEnums;
import ac.artemis.core.v4.packet.PacketExecutor;
import ac.artemis.core.v4.theme.ThemeManager;
import ac.artemis.core.v4.utils.action.InitializeAction;
import ac.artemis.core.v4.utils.action.ShutdownAction;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.graphing.Pair;
import ac.artemis.core.v5.threading.Threading;
import ac.artemis.packet.PacketManager;
import cc.ghast.packet.PacketAPI;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ghast
 * @since 19-Mar-20
 */

@Getter
public class PlayerDataManager extends Manager {

    private final Map<UUID, Pair<PlayerData, PacketExecutor>> playerDataMap = new ConcurrentHashMap<>();
    private final List<Class<? extends AbstractCheckManager>> checkManager = new ArrayList<>();
    private ExecutorService service;
    private Deque<PacketExecutor> executors = new LinkedList<>();
    private Timer timer;
    private int count;
    private AtomicInteger tick = new AtomicInteger();
    private final List<InjectListener> injectListeners = new ArrayList<>();


    public PlayerDataManager(Artemis plugin) {
        super(plugin, "PlayerData [Manager]");
    }

    @Override
    public void init(InitializeAction initializeAction) {
        this.service = Threading.getOrStartService("artemis-data-service");
        plugin.getApi().getThreads().add(service);
        injectPlayers(Server.v().getOnlinePlayers());
        // TODO: Fix this
        this.service.execute(this::decreaseVerbose);
        this.service.execute(this::tickChecks);
    }

    @Override
    public void disinit(ShutdownAction shutdownAction) {
        this.timer.cancel();
        uninjectPlayers();
        this.service.shutdown();
        System.gc();
    }

    public PlayerData getData(Player player) {
        Pair<PlayerData, PacketExecutor> pair = playerDataMap.getOrDefault(player.getUniqueId(), null);
        if (pair == null) return null;
        return pair.getX();
    }

    public PlayerData getData(UUID player) {
        Pair<PlayerData, PacketExecutor> pair = playerDataMap.get(player);
        if (pair == null) return null;
        return pair.getX();
    }

    public Pair<PlayerData, PacketExecutor> getDataAndExecutor(UUID player) {
        return playerDataMap.get(player);
    }

    private void decreaseVerbose() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final int ticc = tick.get();
                playerDataMap.forEach((uuid, pair) -> {
                    pair.getX().getCheckManager().forEach(cm ->
                            cm.getChecks().forEach((clazz, check) -> {
                                if (ticc % check.getInfo().getDecay() == 0)
                                    check.decrease(1.0f);
                            })
                    );

                    if (pair.getX().staff.getLog().size() > 0) {
                        pair.getX().staff.getLog().pop();
                    }
                });

                if (tick.get() == Integer.MAX_VALUE) {
                    tick.set(-1);
                }
                tick.incrementAndGet();
            }
        }, 2000, 2000);
    }

    private void tickChecks() {
        Server.v().getScheduler().scheduleAsyncRepeatingTask(() -> {
            playerDataMap.forEach((uuid, pair) -> {
                pair.getX().getCheckManager().forEach(cm -> cm.getChecks()
                        .entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() instanceof TickHandler && entry.getValue().canCheck())
                        .forEach(entry -> {
                            TickHandler handler = (TickHandler) entry.getValue();
                            handler.tick();
                        })
                );
            });
        }, 0, 1);
    }

    public void injectPlayers(Collection<? extends Player> players) {
        for (Player player : players) {
            if (playerDataMap.containsKey(player.getUniqueId()))
                continue;

            this.injectPlayer(player.getUniqueId());
        }
    }

    public void injectPlayer(UUID uuid) {
        final long time = System.currentTimeMillis();

        if (uuid == null) {
            return;
        }

        if (!PacketAPI.isInjected(uuid)) {
            return;
        }

        final Player player = Server.v().getPlayer(uuid);
        // MAKE SURE WE ARE NOT GETTING A FAKE PLAYER
        if (player != null && !player.hasMetadata("NPC") && !player.hasMetadata("fake")) {

            PacketExecutor executor = checkExecutors();

            // RUNNABLE
            Runnable t = () -> {
                // CREATE THE DATA
                PlayerData data = new PlayerData(player, executor);
                executor.addPlayer(data);
                // SET THE LOGIN TIME
                data.user.setLongInTimePassed(System.currentTimeMillis());

                // PUT IT IN THE MAP
                playerDataMap.put(player.getUniqueId(), new Pair<>(data, executor));

                // SET ALERTS FOR STAFF BECAUSE FAWKING RETARDS
                for (InjectListener injectListener : injectListeners) {
                    injectListener.onInject(player);
                }

                final long delay = (System.currentTimeMillis() - time);

                if (player.hasPermission(ThemeManager.getCurrentTheme().getAlertsPermission())) {
                    player.sendMessage(Chat.translate(ThemeManager.getCurrentTheme().getJoinMessage()
                            .replace("%time%", Long.toString(delay))));
                }
                Server.v().getOnlinePlayers().forEach(p -> {
                    if (p.hasPermission(ThemeManager.getCurrentTheme().getMainPermission()) && p.getUniqueId() != player.getUniqueId()
                            && getData(p) != null
                            && getData(p).staff.getStaffAlert().isHighEnough(StaffEnums.StaffAlerts.EXPERIMENTAL_VERBOSE)) {
                        p.sendMessage(Chat.translate("&8[&bArtemis&8] &aSuccessfully " + player.getName()
                                + "'s processor in " + delay + " ms"));
                    }
                });
                Chat.sendConsoleMessage("&8[&bArtemis&8] &aSuccessfully injected into player &r"
                        + player.getName() + "&b of UUID &r" + uuid + "&b in &r" + delay + "&bms");

                plugin.getApi()
                        .getTimingsManager()
                        .getJoinTiming()
                        .addTime(delay, System.currentTimeMillis());
            };

            // EXECUTE!
            //service.submit(t);
            this.service.execute(t);
            this.count++;
        }
    }

    public void uninjectPlayer(Player player) {
        if (playerDataMap.containsKey(player.getUniqueId())) {
            // REMOVE PLAYER FROM PROTOCOL
            PacketManager.getApi().disinject(player.getUniqueId());

            // KILL THE THREAD
            //playerDataMap.get(impl.getUniqueId()).getY().shutdown();
            for (InjectListener injectListener : injectListeners) {
                injectListener.onDestroy(player);
            }

            // REMOVE FROM MAP
            Pair<PlayerData, PacketExecutor> data = getDataAndExecutor(player.getUniqueId());
            if (data.getY() != null)
                data.getY().removePlayer(data.getX());
            this.playerDataMap.remove(player.getUniqueId());
        }
    }

    public PacketExecutor checkExecutors() {
        PacketExecutor executor = this.executors.stream().min(Comparator.comparingInt(PacketExecutor::size)).orElse(null);
        if (executor == null || executor.size() >= 15) {
            executor = new PacketExecutor();
            this.executors.add(executor);
        }

        return executor;
    }

    public void uninjectOfflinePlayer(UUID player) {
        // KILL THE THREAD
        //playerDataMap.get(impl).getY().shutdown();
        // REMOVE FROM MAP
        playerDataMap.remove(player);
    }

    public void uninjectPlayers() {
        playerDataMap.forEach((uuid, pair) -> {
            if (Server.v().getPlayer(uuid) != null) uninjectPlayer(Server.v().getPlayer(uuid));
            else uninjectOfflinePlayer(uuid);
        });
    }

    public void addInjectListener(final InjectListener injectListener) {
        injectListeners.add(injectListener);
    }

    public void removeInjectListener(final InjectListener injectListener) {
        injectListeners.remove(injectListener);
    }

    public void clearInjectListeners() {
        injectListeners.clear();
    }

}
