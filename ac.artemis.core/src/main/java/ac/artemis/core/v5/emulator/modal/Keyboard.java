package ac.artemis.core.v5.emulator.modal;
/**
 * @author Ghast
 * @since 03/02/2021
 * Warden © 2021
 */
public class Keyboard {

    private float moveForward;
    private float moveStrafe;

    private boolean jumping;

    public Keyboard(final float moveForward, final float moveStrafe, final boolean jumping) {
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
        this.jumping = jumping;
    }

    public float getMoveForward() {
        return moveForward;
    }

    public void setMoveForward(final float moveForward) {
        this.moveForward = moveForward;
    }

    public float getMoveStrafe() {
        return moveStrafe;
    }

    public void setMoveStrafe(final float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(final boolean jumping) {
        this.jumping = jumping;
    }

    public Keyboard copy() {
        return new Keyboard(moveForward, moveStrafe, jumping);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Keyboard keyboard = (Keyboard) o;

        if (Float.compare(keyboard.moveForward, moveForward) != 0) return false;
        if (Float.compare(keyboard.moveStrafe, moveStrafe) != 0) return false;

        return jumping == keyboard.jumping;
    }

    @Override
    public int hashCode() {
        int result = (moveForward != +0.0f ? Float.floatToIntBits(moveForward) : 0);

        result = 31 * result + (moveStrafe != +0.0f ? Float.floatToIntBits(moveStrafe) : 0);
        result = 31 * result + (jumping ? 1 : 0);

        return result;
    }
}
