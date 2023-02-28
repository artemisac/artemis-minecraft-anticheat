package net.minecraft.util.io.netty.channel;

import lombok.experimental.UtilityClass;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelFuture;

/**
 * Class designed to circumvent the access verifier in OpenJDK
 * See https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/master/src/java.base/share/classes/sun/invoke/util/VerifyAccess.java
 *
 * By accessing it via this specific package, we completely circumvent anything related to netty's bullshit access
 */

@UtilityClass
public class LegacyNettyProxy {

    public ChannelFuture writeAndFlush(final Channel channel, final Object object) {
        return channel.writeAndFlush(object);
    }
}
