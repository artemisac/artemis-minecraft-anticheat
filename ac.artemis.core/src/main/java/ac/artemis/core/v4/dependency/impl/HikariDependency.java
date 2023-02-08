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
        name = "Hikari",
        version = "3.4.2",
        url = "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/3.4.2/HikariCP-3.4.2.jar",
        download = true
)
public class HikariDependency extends AbstractDependency {

    public HikariDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Hikari initialized");
    }
}
