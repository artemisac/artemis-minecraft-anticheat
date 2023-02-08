package ac.artemis.anticheat.engine.v2.optimizer;

import lombok.Data;

@Data
public class OptimizedIterationResult {

    private byte[] forwardIteration;
    private byte[] strafeIteration;
    private boolean[] jumpIteration;
    private boolean[] sprintIteration;
    private boolean[] usingIteration;
    private boolean[] attackIteration;
    private boolean[] groundIteration;
    private boolean[] velocityIteration;
    private boolean[] worldCompensationIteration;
    private boolean[] missedFlyingIteration;

    public OptimizedIterationResult setForwardIteration(byte[] forwardIteration) {
        this.forwardIteration = forwardIteration;
        return this;
    }
    public OptimizedIterationResult setStrafeIteration(byte[] strafeIteration) {
        this.strafeIteration = strafeIteration;
        return this;
    }
    public OptimizedIterationResult setJumpIteration(boolean[] jumpIteration) {
        this.jumpIteration = jumpIteration;
        return this;
    }
    public OptimizedIterationResult setSprintIteration(boolean[] sprintIteration) {
        this.sprintIteration = sprintIteration;
        return this;
    }
    public OptimizedIterationResult setUsingIteration(boolean[] usingIteration) {
        this.usingIteration = usingIteration;
        return this;
    }
    public OptimizedIterationResult setAttackIteration(boolean[] attackIteration) {
        this.attackIteration = attackIteration;
        return this;
    }
    public OptimizedIterationResult setGroundIteration(boolean[] groundIteration) {
        this.groundIteration = groundIteration;
        return this;
    }
    public OptimizedIterationResult setVelocityIteration(boolean[] velocityIteration) {
        this.velocityIteration = velocityIteration;
        return this;
    }
    public OptimizedIterationResult setWorldCompensationIteration(boolean[] worldCompensationIteration) {
        this.worldCompensationIteration = worldCompensationIteration;
        return this;
    }
    public OptimizedIterationResult setMissedFlyingIteration(boolean[] missedFlyingIteration) {
        this.missedFlyingIteration = missedFlyingIteration;
        return this;
    }
}
