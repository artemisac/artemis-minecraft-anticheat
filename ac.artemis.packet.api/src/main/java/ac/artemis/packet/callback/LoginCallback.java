package ac.artemis.packet.callback;

import ac.artemis.packet.profile.Profile;

public interface LoginCallback {
    void onLogin(final Profile profile);
}