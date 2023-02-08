package ac.artemis.core.v4.dependency;

import ac.artemis.core.v4.dependency.annotations.Dependency;
import ac.artemis.core.Artemis;
import lombok.AllArgsConstructor;

/**
 * @author Ghast
 * @since 10-Nov-19
 * Ghast CC © 2019
 */
@AllArgsConstructor
public abstract class AbstractDependency {
    protected final Artemis artemis;
    protected final String name = this.getClass().getAnnotation(Dependency.class).name();
    protected final String version = this.getClass().getAnnotation(Dependency.class).version();

    public abstract void init();
}
