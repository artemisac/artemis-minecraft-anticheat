package ac.artemis.core.v4.data;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.check.*;
import ac.artemis.core.v4.check.exempt.ExemptManager;
import ac.artemis.core.v4.check.manager.AbstractCheckManager;
import ac.artemis.core.v4.check.packet.PacketExcludable;
import ac.artemis.core.v4.data.holders.*;
import ac.artemis.core.v4.packet.PacketExecutor;
import ac.artemis.core.v4.processor.AbstractHandler;
import ac.artemis.core.v4.processor.collision.CollisionPreProcessor;
import ac.artemis.core.v4.processor.connection.ConnectionPostProcessor;
import ac.artemis.core.v4.processor.connection.ConnectionPreProcessor;
import ac.artemis.core.v4.processor.emulator.EmulatorPostProcessor;
import ac.artemis.core.v4.processor.emulator.EmulatorPreProcessor;
import ac.artemis.core.v4.processor.interact.InteractPostProcessor;
import ac.artemis.core.v4.processor.interact.InteractPreProcessor;
import ac.artemis.core.v4.processor.miscellaneous.MiscellaneousPreProcessor;
import ac.artemis.core.v4.processor.movement.MovementPreProcessor;
import ac.artemis.core.v4.processor.potion.PotionPostProcessor;
import ac.artemis.core.v4.processor.potion.PotionPreProcessor;
import ac.artemis.core.v4.processor.reach.ReachPostProcessor;
import ac.artemis.core.v4.processor.reach.ReachPreProcessor;
import ac.artemis.core.v4.processor.sensitivity.SensitivityPreProcessor;
import ac.artemis.core.v4.processor.teleport.TeleportPostProcessor;
import ac.artemis.core.v4.processor.teleport.TeleportPreProcessor;
import ac.artemis.core.v4.processor.velocity.VelocityPostProcessor;
import ac.artemis.core.v4.processor.velocity.VelocityPreProcessor;
import ac.artemis.core.v4.processor.world.WorldPreProcessor;
import ac.artemis.core.v4.utils.chat.Chat;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.position.PredictionPosition;
import ac.artemis.core.v4.utils.reach.ReachEntity;
import ac.artemis.core.v4.utils.reach.ReachModal;
import ac.artemis.core.v5.emulator.Emulator;
import ac.artemis.core.v5.emulator.EmulatorManager;
import ac.artemis.core.v5.features.safety.SafetyFeature;
import ac.artemis.core.v5.features.safety.SafetyFeatureFactory;
import ac.artemis.core.v5.features.setback.SetbackFeature;
import ac.artemis.core.v5.features.setback.SetbackFeatureFactory;
import ac.artemis.core.v5.features.setback.SetbackType;
import ac.artemis.core.v5.features.teleport.TeleportFeatureFactory;
import ac.artemis.core.v5.features.teleport.TeleportHandlerFeature;
import ac.artemis.core.v5.features.teleport.TeleportHandlerType;
import ac.artemis.core.v5.language.Lang;
import ac.artemis.core.v5.logging.model.Log;
import ac.artemis.packet.minecraft.Server;
import ac.artemis.packet.minecraft.entity.impl.Player;
import ac.artemis.packet.PacketManager;
import ac.artemis.packet.spigot.utils.ServerUtil;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientFlying;
import cc.ghast.packet.PacketAPI;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerEntityVelocity;
import cc.ghast.packet.wrapper.packet.play.server.GPacketPlayServerPosition;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ghast
 * @since 06-Mar-20
 */

@Getter
@Setter
public class PlayerData {

    /**
     * PlayerData initialization relies on two factors: An active ChannelInjector and a CheckManager.
     *
     * @param player Bukkit processor required for initialization.
     * @see AbstractCheckManager
     */
    public PlayerData(final Player player, final PacketExecutor executor) {
        // Initialize Player
        this.player = player;
        this.executor = executor;
        this.playerID = player.getUniqueId();

        // Data values
        this.movement = new MovementHolder(this);
        this.user = new UserHolder(this);
        this.staff = new StaffHolder(this);
        this.combat = new CombatHolder(this);
        this.timing = new TimingHolder(this);
        this.collision = new CollisionHolder(this);
        this.connection = new ConnectionHolder(this);
        this.sensitivity = new SensitivityHolder(this);
        this.prediction = new PredictionHolder(this);
        this.reach = new ReachHolder(this);
        this.world = new WorldHolder(this);

        try {
            this.version = PacketAPI.getVersion(player.getUniqueId());
            this.entity = EmulatorManager.getProvider()
                    .getFactory()
                    .setData(this)
                    .build();
        } catch (final NullPointerException e) {
            this.version = ServerUtil.getGameVersion();
            this.entity = EmulatorManager.getProvider()
                    .getFactory()
                    .setData(this)
                    .build();
            Chat.sendConsoleMessage("&4Fatal issue with Artemis! Error code &cOxA4-01");
            e.printStackTrace();
            Chat.sendConsoleMessage(Chat.spacer());
        }

        Chat.sendConsoleMessage(Lang.MSG_CONSOLE_JOIN_VERSION
                .replace("%player%", this.getPlayer().getName())
                .replace("%version%", version.name())
        );

        // Initialize handlers
        this.preHandlers = Arrays.asList(
                new ConnectionPreProcessor(this),
                new PotionPreProcessor(this),
                new MovementPreProcessor(this),
                new InteractPreProcessor(this),
                new MiscellaneousPreProcessor(this),
                new CollisionPreProcessor(this),
                new VelocityPreProcessor(this),
                new TeleportPreProcessor(this),
                new EmulatorPreProcessor(this),
                new ReachPreProcessor(this),
                new WorldPreProcessor(this),
                new SensitivityPreProcessor(this)
        );

        this.postHandlers = Arrays.asList(
                new ConnectionPostProcessor(this),
                new VelocityPostProcessor(this),
                new TeleportPostProcessor(this),
                new PotionPostProcessor(this),
                new InteractPostProcessor(this),
                new EmulatorPostProcessor(this),
                new ReachPostProcessor(this)
        );

        this.teleportHandler = new TeleportFeatureFactory()
                .setType(TeleportHandlerType.BRUTEFORCE)
                .build();

        this.setbackHandler = new SetbackFeatureFactory()
                .setType(SetbackType.SMART)
                .build();

        this.safetyFeature = new SafetyFeatureFactory()
                .build();

        // Initialize the checkmanager
        try {
            // Get the CheckManager injected in Artemis.
            for (final Class<? extends AbstractCheckManager> checkManager :
                    Artemis.v().getApi().getPlayerDataManager().getCheckManager()) {
                final Constructor<? extends AbstractCheckManager> constructor = checkManager.getConstructor(PlayerData.class);

                // Create new instance
                this.getCheckManager().add(constructor.newInstance(this));
            }
        } catch (final IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            Server.v().getScheduler().runTask(() -> player.kickPlayer("Failed to load processor. Please contact the Anticheat developers"));
            e.printStackTrace();
            return;
        }

        /*if (Artemis.INSTANCE.getApi().getDependencyManager().getViaVersionDependency() != null) {
            this.version = ProtocolVersion.getVersion(Artemis.INSTANCE.getApi().getDependencyManager()
                    .getViaVersionDependency().getVersion(player).getVersion());
        }*/


        PacketManager.getApi().sendPacket(player.getUniqueId(), new GPacketPlayServerEntityVelocity(player.getEntityId(), 0.D, 0.D, 0.D));

        // Basic things
        this.user.setJoin(System.currentTimeMillis());
        this.movement.setRespawnTicks(100);
        this.tickTimer = new Timer();
        this.exemptManager = new ExemptManager(this);

        //Bukkit.getPluginManager().callEvent(new ArtemisDataCreateEvent(this));
    }

    // Player Info

    /**
     * Player ID defines the immutable identifier of a Player. This is used temporarily to access in a more swift wau
     * the processor.getUniqueId() method without directly calling the processor. In short, this is useless. We still
     * nonetheless use it as a shortcut.
     */
    private final UUID playerID;

    /**
     * The processor corresponds to a Bukkit processor. This
     */
    private final Player player;

    public Emulator entity;

    // Handlers
    /**
     * List of all handlers called. This is simply to handleRotation all sorts of processor without making a mess of everything.
     * TODO Make handlers have specific packets to be listened to to prevent redundant packet calls, without causing
     * TODO the iteration to have a superior time than the original
     */
    private final List<AbstractHandler> preHandlers;
    private final List<AbstractHandler> postHandlers;

    // Logging

    /**
     * Verboses represent the logged processor which is yet to be confirmed. For a check to have a valid violation it has
     * to meet certain imposed requirements. This is to prevent occasional false positives as well as laggy times.
     * This is super useful as an FYI.
     */
    private final Map<ArtemisCheck, Log> verboses = new ConcurrentHashMap<>();

    /**
     * This is pretty straight forward. It logs all violations by a processor. The reason this is not a List is just to
     * filter easily the logs from each checks. I'm hesitating on implementing this directly into the checks. It would
     * 100% improve the speed of the execution. It's still to think about since Verus does that and I don't wish to
     * directly copy their system.
     *
     * @see Log
     * @see ArtemisCheck
     */
    private final Map<ArtemisCheck, Log> violations = new ConcurrentHashMap<>();
    private final PacketExecutor executor;

    // Data holders
    public final MovementHolder movement;
    public final UserHolder user;
    public final StaffHolder staff;
    public final CombatHolder combat;
    public final TimingHolder timing;
    public final CollisionHolder collision;
    public final ConnectionHolder connection;
    public final SensitivityHolder sensitivity;
    public final PredictionHolder prediction;
    public final ReachHolder reach;
    public final WorldHolder world;

    // Features
    private final TeleportHandlerFeature teleportHandler;
    private final SetbackFeature setbackHandler;
    private final SafetyFeature safetyFeature;

    // CheckManager
    public final List<AbstractCheckManager> checkManager = new ArrayList<>();

    public List<ArtemisCheck> getChecks() {
        final List<ArtemisCheck> checks = new ArrayList<>();
        checkManager.stream().map(AbstractCheckManager::getChecks).forEach(e -> checks.addAll(e.values()));
        return checks;
    }

    // ExemptManager
    public ExemptManager exemptManager;

    public Timer tickTimer;

    /**
     * Will return all verboses found. This excludes violations.
     * TODO Return violations too
     *
     * @return Map of the Check's name (ChecktypeValue in Camel Case, eg: AuraMovement) and the Log's processor
     * @see Log
     * @see ArtemisCheck
     */
    public Map<String, Log> getLogs() {
        final Map<String, Log> logs = new WeakHashMap<>();

        violations.forEach((check, log) -> {
            logs.put(check.getInfo().getType().toString() + check.getInfo().getVar(), log);
        });
        return logs;
    }

    private ac.artemis.packet.protocol.ProtocolVersion version;


    public int getTotalFlags() {
        final int[] flag = {0};
        violations.forEach((check, log) -> {
            flag[0] += Math.round(log.getCount());
        });
        return flag[0];
    }


    public synchronized void handleFastProcess(final GPacket packet) {
        for (final AbstractCheckManager abstractCheckManager : checkManager) {
            abstractCheckManager.getChecks()
                    .values()
                    .stream()
                    .filter(e -> FastProcessHandler.class.isAssignableFrom(e.getClass()))
                    .forEach(e -> ((FastProcessHandler) e).fastHandle(packet));
        }

        this.preHandlers.stream()
                .filter(e -> FastProcessHandler.class.isAssignableFrom(e.getClass()))
                .forEach(e -> ((FastProcessHandler) e).fastHandle(packet));
    }

    public synchronized void handlePacket(final GPacket packet) {
        PredictionPosition position = null;

        if (packet instanceof PacketPlayClientFlying) {
            position = new PredictionPosition(prediction.getLastMovement(), prediction.getMovement(), entity.toMovement());
        }

        for (final AbstractCheckManager checkManager : this.checkManager) {
            for (final Map.Entry<Class<? extends ArtemisCheck>, ArtemisCheck> entry
                    : checkManager.getChecks().entrySet()) {
                final Class<? extends ArtemisCheck> clazz = entry.getKey();
                final ArtemisCheck check = entry.getValue();

                packet: {
                    final boolean validPacketCheck = check instanceof PacketHandler && check.canCheck();
                    final boolean notExcluded = (!PacketExcludable.class.isAssignableFrom(clazz)
                            || ((PacketExcludable) check).isCompatible(packet));

                    if (!validPacketCheck || !notExcluded) break packet;

                    ((PacketHandler) check).handle(packet);
                }

                prediction: {
                    // Make sure there was a prediction result.
                    if (position == null) break prediction;

                    // Do the gay checking ghast does.
                    if (position.was() == null || position.got() == null || position.expected() == null) break prediction;

                    // Make sure the check is a prediction check.
                    if (!(check instanceof PredictionHandler) || !check.canCheck()) break prediction;

                    ((PredictionHandler) check).handle(position);
                }
            }
        }

        this.setbackHandler.tick(this, this.user.isSetback(), false);
    }

    public synchronized void handleReach(final ReachModal current, final ReachEntity opponent){
        // Execute all checks
        checkManager.forEach(cm -> cm.getChecks()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof ReachHandler && entry.getValue().canCheck())
                .forEach(entry -> {
                    try {
                        final ReachHandler handler = (ReachHandler) entry.getValue();
                        handler.handle(current, opponent);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
        ));
    }

    public final synchronized void handleClick(final PlayerMovement current, final ReachEntity opponent){
        // Execute all checks
        checkManager.forEach(cm -> cm.getChecks()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof ClickHandler && entry.getValue().canCheck())
                .forEach(entry -> {
                            try {
                                final ClickHandler handler = (ClickHandler) entry.getValue();
                                handler.handle(current, opponent);
                            } catch (final Exception e) {
                                e.printStackTrace();
                            }
                }
        ));
    }

    public void handlePreHandlers(final GPacket packet) {
        final long now = System.currentTimeMillis();

        for (final AbstractHandler preHandler : preHandlers) {
            try {
                preHandler.handle(packet);
            } catch (final Exception ex){
                ex.printStackTrace();
            }
        }
        timing.handlerPreTiming.addTime(now, System.currentTimeMillis());
    }

    public void handlePostHandlers(final GPacket packet) {
        if (packet instanceof GPacketPlayServerPosition) {
            final GPacketPlayServerPosition tp = (GPacketPlayServerPosition) packet;
            this.teleportHandler.queueTeleport(tp);
        }

        postHandlers.forEach(e -> {
            try {
                e.handle(packet);
            } catch (final Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public Log getVerbose(final ArtemisCheck check) {
        return verboses.getOrDefault(check, null);
    }

    public void resetMotion() {
        try {
            PacketManager.getApi().sendPacket(
                    this.getPlayer().getUniqueId(),
                    new GPacketPlayServerEntityVelocity(
                            this.getPlayer().getEntityId(),
                            0.D,
                            0.D,
                            0.D
                    )
            );
        } catch (final IllegalStateException e) {
            ServerUtil.sendConsoleMessage("&c&lWARNING&r! &bKicking player of username &r" + this.getPlayer().getName() + "&b due to broken pipeline&r!");
            monke();
        }
    }

    public void monke() {
        this.monke("Timed out");
    }

    public void monke(final String reason) {
        Server.v().getScheduler().runTask(() -> this.getPlayer().kickPlayer(reason));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PlayerData data = (PlayerData) o;

        return playerID.equals(data.playerID);
    }

    @Override
    public int hashCode() {
        return playerID.hashCode();
    }
}
