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
        name = "Mongo",
        version = "3.12.3",
        url = "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.3/mongo-java-driver-3.12.3.jar",
        download = true
)
public class MongoDependency extends AbstractDependency {
    public MongoDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Mongo initialized");
    }
}
