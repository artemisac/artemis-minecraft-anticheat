package ac.artemis.core.v5.emulator.modal;

/**
 * @author Ghast
 * @since 03/02/2021
 * Warden © 2021
 */
public class Friction {
    private Keyboard keyboard;
    private float friction;

    public Friction(final Keyboard keyboard, final float friction) {
        this.keyboard = keyboard;
        this.friction = friction;
    }

    public float getMoveForward() {
        return keyboard.getMoveForward();
    }

    public float getMoveStrafe() {
        return keyboard.getMoveStrafe();
    }

    public boolean isJumping() {
        return keyboard.isJumping();
    }

    public float getFriction() {
        return friction;
    }

    public void setKeyboard(final Keyboard keyboard) {
        this.keyboard = keyboard;
    }

    public void setFriction(final float friction) {
        this.friction = friction;
    }
}
