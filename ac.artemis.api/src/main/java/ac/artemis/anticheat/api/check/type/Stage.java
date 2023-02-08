package ac.artemis.anticheat.api.check.type;

/**
 * @author Ghast
 * @since 15-Mar-20
 */

public enum Stage {
    EXPERIMENTING(0, "Dev#1"),
    TESTING(1, "Exp#2"),
    FALSING(2, "Pipeline#3"),
    PRE_RELEASE(3, "Heuristic"),
    RELEASE(4, "Heuristic#2"),
    PERFECTED(5, "Bad%Invalid")
    ;

    private final int priority;
    private final String naming;

    Stage(int priority, String naming) {
        this.priority = priority;
        this.naming = naming;
    }

    public String getNaming() {
        return naming;
    }

    public boolean isAbove(Stage a) {
        return a.priority < priority;
    }

    // Is value
    public boolean isOrAbove(Stage a) {
        return a.priority <= priority;
    }

    public boolean isEqual(Stage a) {
        return a.priority == priority;
    }

    public boolean isBelow(Stage a) {
        return a.priority > priority;
    }

    public boolean isOrBelow(Stage a) {
        return a.priority >= priority;
    }
}
