package action;

import shapes.Curve;
import drawing.DrawingPanel;

public final class AddCurve implements ReversibleAction {

    private final Curve curve;
    private final DrawingPanel drawingContext;
    private boolean isDone;
    
    public AddCurve(final Curve aCurve, final DrawingPanel aDrawingContext) {
        this.curve = aCurve;
        this.drawingContext = aDrawingContext;
        this.isDone = true;
    }
    
    @Override
    public void undo() {
        if (!isDone) {
            return;
        }
        
        this.drawingContext.removeCurve(this.curve, false); // not a new action
        this.isDone = false;
    }

    @Override
    public void redo() {
        if (isDone) {
            return;   
        }
        
        this.drawingContext.addCurve(this.curve, false); // not a new action
        this.isDone = true;
    }
}
