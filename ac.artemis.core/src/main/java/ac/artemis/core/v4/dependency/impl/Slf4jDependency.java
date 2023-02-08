package ac.artemis.core.v4.dependency.impl;

import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.Artemis;
import ac.artemis.core.v4.dependency.AbstractDependency;
import ac.artemis.core.v4.utils.chat.Chat;

@Dependency(
        name = "Slf4jSimple",
        version = "2.0.0-alpha1",
        url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.0-alpha1/slf4j-simple-2.0.0-alpha1.jar",
        download = true
)
public class Slf4jDependency extends AbstractDependency {
    public Slf4jDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Slf4j-Simple initialized");
    }

}
