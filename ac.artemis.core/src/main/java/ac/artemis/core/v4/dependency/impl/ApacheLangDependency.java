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
        name = "ApacheLang",
        version = "3.11",
        url = "https://repo1.maven.org/maven2/org/apache/commons/commons-lang3/3.11/commons-lang3-3.11.jar",
        download = true
)
public class ApacheLangDependency extends AbstractDependency {

    public ApacheLangDependency(Artemis artemis) {
        super(artemis);
    }

    @Override
    public void init() {
        Chat.sendConsoleMessage("&7[&bArtemis&7]&b Apache Lang 3.0 initialized");
    }
}
