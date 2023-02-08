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
        name = "Fastutil",
        version = "8.2.3",
        url = "https://repo1.maven.org/maven2/it/unimi/dsi/fastutil/8.2.3/fastutil-8.2.3.jar",
        download = true
)
public class FastUtilDependency extends AbstractDependency {

    public FastUtilDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b FastUtil 8.2.3 initialized");
    }
}
