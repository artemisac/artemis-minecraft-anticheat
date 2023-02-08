package ac.artemis.anticheat.engine.v2.runner;

import ac.artemis.core.v4.data.PlayerData;
import lombok.Getter;

@Getter
public class BruteforceKey {
    private final PlayerData data;

    private int forward, strafe;
    private boolean jump, sprint, using, attack, ground, velocity, world, missedFlying;
    private boolean dumbFix;

    public BruteforceKey(final PlayerData data) {
        this.data = data;
    }

    public BruteforceKey setForward(final int forward) {
        this.forward = forward;
        return this;
    }

    public BruteforceKey setStrafe(final int strafe) {
        this.strafe = strafe;
        return this;
    }

    public BruteforceKey setJump(final boolean jump) {
        this.jump = jump;
        return this;
    }

    public BruteforceKey setSprint(final boolean sprint) {
        this.sprint = sprint;
        return this;
    }

    public BruteforceKey setUsing(final boolean using) {
        this.using = using;
        return this;
    }

    public BruteforceKey setAttack(final boolean attack) {
        this.attack = attack;
        return this;
    }

    public BruteforceKey setGround(final boolean ground) {
        this.ground = ground;
        return this;
    }

    public BruteforceKey setVelocity(final boolean velocity) {
        this.velocity = velocity;
        return this;
    }

    public BruteforceKey setWorld(final boolean world) {
        this.world = world;
        return this;
    }

    public BruteforceKey setMissedFlying(final boolean missedFlying) {
        this.missedFlying = missedFlying;
        return this;
    }

    public BruteforceKey setDumbFix(boolean dumbFix) {
        this.dumbFix = dumbFix;
        return this;
    }

    public BruteforceKey copy() {
        return new BruteforceKey(data)
                .setForward(forward)
                .setStrafe(strafe)
                .setJump(jump)
                .setSprint(sprint)
                .setUsing(using)
                .setAttack(attack)
                .setGround(ground)
                .setVelocity(velocity)
                .setWorld(world)
                .setMissedFlying(missedFlying)
                .setDumbFix(dumbFix);
    }
}