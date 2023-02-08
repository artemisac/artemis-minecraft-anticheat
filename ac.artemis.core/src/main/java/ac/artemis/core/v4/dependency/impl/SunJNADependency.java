package ac.artemis.core.v4.dependency.impl;

import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.dependency.AbstractDependency;
import ac.artemis.core.v4.utils.chat.Chat;

/**
 * @author Ghast
 * @since 10-May-20
 */

@Dependency(
        name = "Sun-JNA",
        version = "5.5.0",
        url = "https://repo1.maven.org/maven2/net/java/dev/jna/jna/5.5.0/jna-5.5.0.jar",
        download = true
)
public class SunJNADependency extends AbstractDependency {
    public SunJNADependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b JNA initialized");
    }
}
