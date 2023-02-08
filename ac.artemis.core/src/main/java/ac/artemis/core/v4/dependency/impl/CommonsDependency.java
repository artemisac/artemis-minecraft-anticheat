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
        name = "Commons",
        version = "2.6",
        url = "https://repo1.maven.org/maven2/commons-io/commons-io/2.6/commons-io-2.6.jar",
        download = true
)
public class CommonsDependency extends AbstractDependency {
    public CommonsDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Commons IO initialized");
    }
}
