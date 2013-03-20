package util;

import java.util.ArrayList;
import java.util.List;

public class Stack<T> {
    
    private final List<T> contents;
    
    public Stack() {        
        this.contents = new ArrayList<T>();
    }
    
    public int size() {
        return this.contents.size();
    }
    
    public boolean isEmpty() {
        return this.contents.isEmpty();
    }
    
    public void clear() {
        this.contents.clear();
    }
    
    public void push( final T toAdd ) {
        this.contents.add( toAdd );
    }
    
    public T pop() {
        if ( this.contents.isEmpty() )
            return null;
        
        return this.contents.remove( this.contents.size() - 1 );
    }
}
