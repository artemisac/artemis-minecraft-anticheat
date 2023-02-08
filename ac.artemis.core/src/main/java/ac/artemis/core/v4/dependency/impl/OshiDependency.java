package ac.artemis.core.v4.dependency.impl;

import ac.artemis.core.Artemis;
import ac.artemis.core.v4.dependency.AbstractDependency;
import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.v4.utils.chat.Chat;

/**
 * @author Ghast
 * @since 10-May-20
 */

@Dependency(
        name = "OshiCore",
        version = "4.5.0",
        url = "https://repo1.maven.org/maven2/com/github/oshi/oshi-core/5.2.0/oshi-core-5.2.0.jar",
        download = true
)
public class OshiDependency extends AbstractDependency {
    public OshiDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Oshi initialized");
    }
}
