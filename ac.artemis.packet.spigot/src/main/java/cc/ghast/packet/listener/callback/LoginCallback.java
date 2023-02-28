package cc.ghast.packet.listener.callback;

import cc.ghast.packet.profile.ArtemisProfile;

public interface LoginCallback {
    void onLogin(final ArtemisProfile profile);
}
