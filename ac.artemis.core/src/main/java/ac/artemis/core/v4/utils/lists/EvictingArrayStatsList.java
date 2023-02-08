package ac.artemis.core.v4.utils.lists;

import java.util.Collection;

/**
 * @author Ghast
 * @since 09-May-20
 */

// TODO MAKE THIS
public class EvictingArrayStatsList extends EvictingArrayList<Double> {
    public EvictingArrayStatsList(int max) {
        super(max);
    }

    @Override
    public boolean add(Double element) {
        return super.add(element);
    }

    @Override
    public void add(int index, Double element) {
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends Double> c) {
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
        return super.addAll(index, c);
    }
}
