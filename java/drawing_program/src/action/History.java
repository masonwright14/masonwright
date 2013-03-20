package action;

import util.DropOutStack;
import util.Stack;

public class History {
    
    private final DropOutStack<ReversibleAction> undoStack;
    private final Stack<ReversibleAction> redoStack;
    private static final int UNDO_STACK_SIZE = 10;
    
    public History() {
        this.undoStack = new DropOutStack<ReversibleAction>( UNDO_STACK_SIZE );
        this.redoStack = new Stack<ReversibleAction>();
    }
    
    public void addAction( final ReversibleAction action ) {
        this.redoStack.clear();
        this.undoStack.push( action );
    }
    
    public boolean canUndo() {
        return ! this.undoStack.isEmpty();
    }
    
    public boolean canRedo() {
        return ! this.redoStack.isEmpty();
    }
    
    public void undo() {
        if ( ! canUndo() )
            return;
        
        ReversibleAction toUndo = this.undoStack.pop();
        toUndo.undo();
        this.redoStack.push( toUndo );
    }
    
    public void redo() {
        if ( ! canRedo() )
            return;
        
        ReversibleAction toRedo = this.redoStack.pop();
        toRedo.redo();
        this.undoStack.push( toRedo );
    }
}