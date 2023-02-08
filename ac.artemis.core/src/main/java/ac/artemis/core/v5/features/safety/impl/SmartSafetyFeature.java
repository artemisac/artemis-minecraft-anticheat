package ac.artemis.core.v5.features.safety.impl;

import ac.artemis.core.v5.features.safety.SafetyFeature;
import ac.artemis.packet.spigot.wrappers.GPacket;
import ac.artemis.packet.wrapper.client.PacketPlayClientLook;
import ac.artemis.packet.wrapper.client.PacketPlayClientPosition;

import java.util.function.Function;

public class SmartSafetyFeature implements SafetyFeature {
    @Override
    public boolean check(final GPacket packet) {
        for (final Checks value : Checks.values()) {
            if (value.check(packet)) return true;
        }
        return false;
    }


    enum Checks {
        FARLANDS(p -> {
            if (p instanceof PacketPlayClientPosition) {
                final PacketPlayClientPosition fly = (PacketPlayClientPosition) p;

                if (Math.abs(fly.getX()) >= Integer.MAX_VALUE) return true;
                if (Math.abs(fly.getY()) >= Integer.MAX_VALUE) return true;
                return Math.abs(fly.getZ()) >= Integer.MAX_VALUE;
            }

            return false;
        }),

        GAYAIM(p -> {
            if (p instanceof PacketPlayClientLook) {
                final PacketPlayClientLook fly = (PacketPlayClientLook) p;

                if (Math.abs(fly.getYaw()) >= Float.MAX_VALUE) return true;
                return Math.abs(fly.getPitch()) >= Float.MAX_VALUE;
            }

            return false;
        })
        ;

        private final Function<GPacket, Boolean> function;

        Checks(final Function<GPacket, Boolean> function) {
            this.function = function;
        }

        public boolean check(final GPacket packet) {
            return function.apply(packet);
        }
    }
}
