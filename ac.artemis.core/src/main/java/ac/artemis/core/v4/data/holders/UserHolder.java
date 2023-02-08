package ac.artemis.core.v4.data.holders;

import ac.artemis.core.v4.emulator.potion.PotionEffect;
import ac.artemis.core.v5.utils.bounding.BoundingBox;
import ac.artemis.core.v4.utils.position.PlayerMovement;
import ac.artemis.core.v4.utils.time.TimeUtil;
import ac.artemis.core.v4.data.PlayerData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Getter
@Setter
public class UserHolder extends AbstractHolder {
    public UserHolder(PlayerData data) {
        super(data);
    }

    public Map<Long, Long> keepAlives = new WeakHashMap<>();
    public BoundingBox box;
    public double transactionPing = -1, offset;
    public int differencial, keepAlivePing;
    public long lastFlyingPacket, lastKeepAlive, lastLag, ping, lastPlace, lastUnderBlock, longInTimePassed, lastSentKeepAlive,
            lastDig, currentTime, lastTp, deltaFly, join, lastHealSatiated, lastSetback, lastGround, lastFakeGround;
    public PlayerMovement lastLegitLocation;
    public boolean placed, tpUnknown, onGround, onFakeGround, onGroundAir, liquid, digging,
            fakeDigging, inventoryOpen, underBlock, aboveLiquid, onLilyPad, sprinting, sneaking, usingItem, usingPotion, isSetback;
    public String customClientPayload = "";
    public int maxPingTicks = 1000;

    private long lastRespawn;

    private List<PotionEffect> effects = new ArrayList<>();
    private PotionEffect lastAddEffect;
    private PotionEffect lastRemoveEffect;
    private boolean effectAddProcessed;
    private boolean effectRemoveProcessed;

    public void setSetBackX() {
        this.isSetback = true;
    }

    public boolean isLagging() {
        long now = System.currentTimeMillis();
        return now - data.movement.getLastDelayedMovePacket() < 220L || data.movement.getTeleportTicks() > 0;
    }

    public boolean isOnCooldown() {
        return data.movement.getDeathTicks() != 0
                || data.movement.getRespawnTicks() != 0
                || data.movement.getTeleportTicks() != 0
                || !TimeUtil.hasExpired(getJoin(), 2)
                || !TimeUtil.hasExpired(getLastRespawn(), 1)
                ;
    }

    public boolean keepAliveExists(long id) {
        return this.keepAlives.containsKey(id);
    }

    public long getKeepAliveTime(long id) {
        long time = this.keepAlives.get(id);
        this.keepAlives.remove(id);
        return time;
    }

    public void addKeepAliveTime(long id) {
        this.keepAlives.put(id, System.currentTimeMillis());
    }
}
