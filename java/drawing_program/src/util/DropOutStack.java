package util;

import java.util.ArrayList;
import java.util.List;

public class DropOutStack<T> {

    private final List<T> contents;
    private final int maxSize;
    
    public DropOutStack( final int maxSize ) {        
        if ( maxSize < 1 )
            throw new IllegalArgumentException();
        
        this.maxSize = maxSize;
        this.contents = new ArrayList<T>();
    }
    
    public int maxSize() {
        return this.maxSize;
    }
    
    public boolean isEmpty() {
        return this.contents.isEmpty();
    }
    
    public int size() {
        return this.contents.size();
    }
    
    public void clear() {
        this.contents.clear();
    }
    
    public void push( final T toAdd ) {
        this.contents.add( toAdd );
        
        // drop bottom element, if over-filled
        if ( this.contents.size() > this.maxSize ) {
            this.contents.remove( 0 );
        }
    }
    
    public T pop() {
        if ( this.contents.isEmpty() )
            return null;
        
        return this.contents.remove( this.contents.size() - 1 );
    }
}