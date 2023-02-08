package ac.artemis.core.v4.dependency.impl;

import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.dependency.AbstractDependency;
import ac.artemis.core.v4.utils.chat.Chat;

@Dependency(
        name = "Guava",
        version = "29.0-jre",
        url = "https://repo1.maven.org/maven2/com/google/guava/guava/29.0-jre/guava-29.0-jre.jar",
        download = true
)
public class GuavaDependency extends AbstractDependency {

    public GuavaDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Guava initialized");
    }
}
