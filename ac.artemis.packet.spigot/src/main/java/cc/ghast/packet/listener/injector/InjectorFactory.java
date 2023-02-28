package cc.ghast.packet.listener.injector;

import ac.artemis.packet.protocol.ProtocolVersion;

/**
 * @author Ghast
 * @since 30/12/2020
 * ArtemisPacket © 2020
 */
public class InjectorFactory {

    private final ProtocolVersion serverVersion;

    public InjectorFactory(ProtocolVersion serverVersion) {
        this.serverVersion = serverVersion;
    }

    public Injector buildInjector() {
        if (serverVersion.isLegacy()) {
            return new InjectorLegacy();
        } else {
            return new InjectorModern();
        }
    }
}
