package ac.artemis.anticheat.api.check;

public interface CheckInit {
    void init();
    void disinit();
    CheckReload getReload();
}
