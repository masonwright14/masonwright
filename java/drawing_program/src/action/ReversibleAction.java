package action;

public interface ReversibleAction {
    
    /**
     * Undo the effect of the action, and enter the undone state. If the action
     * is in the undone state, this method does nothing.
     */
    public void undo();
    
    /**
     * Re-do the effect of the action, and enter the done state. If the action is in the done state,
     * this method does nothing.
     */
    public void redo();
}