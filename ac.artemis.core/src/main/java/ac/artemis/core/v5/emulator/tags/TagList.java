package ac.artemis.core.v5.emulator.tags;

import java.util.ArrayList;
import java.util.Arrays;

public class TagList extends ArrayList<Tags> {
    @Override
    public String toString() {
        return Arrays.toString(this.toArray());
    }
}
