package coalitiongames;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public final class DropOutQueue<T> implements Queue<T> {
    
    private final List<T> backingList = new LinkedList<T>();
    
    private final int maxSize;
    
    public DropOutQueue(final int aMaxSize) {
        if (aMaxSize <= 0) {
            throw new IllegalArgumentException();
        }
        this.maxSize = aMaxSize;
    }
    
    @Override
    public void clear() {
        this.backingList.clear();
    }
    
    @Override
    public boolean contains(final Object arg0) {
        return this.backingList.contains(arg0);
    }
    
    @Override
    public boolean isEmpty() {
        return this.backingList.isEmpty();
    }
    
    @Override
    public int size() {
        return this.backingList.size();
    }

    @Override
    public boolean add(final T arg0) {
        if (arg0 == null) {
            return false;
        }
        
        this.backingList.add(arg0);
        if (this.backingList.size() > maxSize) {
            this.backingList.remove(0);
        }
        
        assert size() <= maxSize;
        
        return true;
    }
    
    @Override
    public T remove() {
        if (this.backingList.isEmpty()) {
            return null;
        }
        return this.backingList.remove(0);
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(final Collection<?> arg0) {
        throw new UnsupportedOperationException();

    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(final Object arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("hiding")
    @Override
    public <T> T[] toArray(final T[] arg0) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public T element() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(final T arg0) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T peek() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T poll() {
        throw new UnsupportedOperationException();
    }
}
