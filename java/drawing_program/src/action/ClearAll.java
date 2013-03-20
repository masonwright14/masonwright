package action;

import java.util.List;

import drawing.DrawingPanel;

import shapes.Curve;

public class ClearAll implements ReversibleAction {

    private final List<Curve> curves;
    private final DrawingPanel drawingContext;
    private boolean isDone;

    public ClearAll( final List<Curve> curves, final DrawingPanel drawingContext ) {
        this.curves = curves;
        this.drawingContext = drawingContext;
        this.isDone = true;
    }
    
    
    @Override
    public void undo() {
        if ( ! isDone )
            return;
        
        this.drawingContext.setCurves( curves );
        this.isDone = false;
    }

    @Override
    public void redo() {
        if ( isDone )
            return;
        
        this.drawingContext.clearAll( false ); // not a new action
        this.isDone = true;
    }
}