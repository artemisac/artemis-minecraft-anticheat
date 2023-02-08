package ac.artemis.core.v4.utils.lists;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Ghast
 * @since 06-Mar-20
 */
public class EvictingLinkedList<T> extends LinkedList<T> {
    private int max;

    public EvictingLinkedList(int max) {
        this.max = max;
    }

    @Override
    public void addFirst(T t) {
        if (size() >= max) {
            removeLast();
        }
        super.addFirst(t);
    }

    @Override
    public void addLast(T t) {
        if (size() >= max) {
            removeFirst();
        }
        super.addLast(t);
    }

    @Override
    public boolean add(T t) {
        if (size() >= max) {
            removeFirst();
        }
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (size() >= max) {
            for (int i = 0; i < c.size(); i++) {
                removeFirst();
            }
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnfinishedFeatureException("EvictingLinkedList#addAll(int, Collection<? extends T>)");
    }

    @Override
    public void add(int index, T element) {
        if (size() >= max) {
            removeFirst();
        }
        super.add(index, element);
    }

    public boolean isFull() {
        return size() >= max;
    }
}
