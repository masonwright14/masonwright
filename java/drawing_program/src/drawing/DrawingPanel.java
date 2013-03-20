package drawing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import action.AddCurve;
import action.ClearAll;
import action.History;

import shapes.Curve;
import shapes.Path;
import shapes.Rectangle;
import util.Util;

public class DrawingPanel extends JPanel {

    private static final long serialVersionUID = 8755076423596729184L;
    private boolean mouseWasDown;
    private final List<Curve> curves;
    private Curve tempCurve;
    private MyMouseListener listener;
    private Point startPoint;
    private BufferedImage buffer;
    private Graphics2D bufferG2;
    private DrawingMode drawingMode;
    private Dimension myDimensions;
    private Color color;
    private final History history;

    public DrawingPanel( final Dimension dimensions ) {
        super();
        
        this.setPreferredSize( dimensions );
        this.myDimensions = dimensions;
        this.curves = new ArrayList<Curve>();
        this.history = new History();
    }
    
    public void init() {        
        this.mouseWasDown = false;
        
        this.listener = new MyMouseListener();
        this.addMouseListener( listener );
        this.addMouseMotionListener( listener );
        
        this.buffer = new BufferedImage( 
            ( int ) myDimensions.getWidth(), 
            ( int ) myDimensions.getWidth(), 
            BufferedImage.TYPE_INT_ARGB 
        );
        this.bufferG2 = ( Graphics2D ) this.buffer.getGraphics();
        
        repaint();
    }
    
    public void clearAll( final boolean isNewAction ) {
        if ( isNewAction ) {
            this.history.addAction( new ClearAll( Util.cloneList( this.curves ), this ) );
        }
        
        this.curves.clear();
        
        repaint();
    }
    
    public void setCurves( final List<Curve> newCurves ) {
        this.curves.clear();
        this.curves.addAll( newCurves );
        repaint();
    }
    
    public void setColor( final Color color ) {
        this.color = color;
    }
    
    public void setDrawingMode( final DrawingMode drawingMode ) {
        this.drawingMode = drawingMode;
        updateCursor();
    }
    
    public boolean canUndo() {
        return this.history.canUndo();
    }
    
    public boolean canRedo() {
        return this.history.canRedo();
    }
    
    public void undo() {
        this.history.undo();
    }
    
    public void redo() {
        this.history.redo();
    }
    
    public void addCurve( final Curve toAdd, final boolean isNewAction ) {        
        if ( isNewAction ) {
            this.history.addAction( new AddCurve( toAdd, this ) );
        }
        
        this.curves.add( toAdd );
        repaint();
    }
    
    public void removeCurve( final Curve target, final boolean isNewAction ) {
        if ( this.curves.contains( target ) )
            this.curves.remove( target );
        
        if ( isNewAction ) {
            // TODO: generate new ReversibleAction
        }
        
        repaint();
    }
    
    private void updateCursor() {
        
        switch ( this.drawingMode ) {
        case PENCIL:
            try {
                Image cursorImage = ImageIO.read( new File( "pencil.png" ) );
                Cursor pencilCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                    cursorImage,
                    new Point( 6, 28 ),
                    "pencil"
                );
                this.setCursor( pencilCursor );
                
            } catch ( IOException e ) {
                e.printStackTrace();
            }  
            break;
        case RECTANGLE:
            this.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
            break;
        }
    }

    @Override
    public void update( final Graphics g ) {
        paint( g );
    }
    
    public void updateDrawing() {
        if ( this.drawingMode == DrawingMode.PENCIL ) {
            if ( listener.isMouseDown() ) {
                // if mouse was down, continue existing Path
                if ( this.mouseWasDown ) {
                    Path currentPath = ( Path ) this.curves.get( this.curves.size() - 1 );
                    currentPath.addPoint( 
                        new Point( listener.mouseX(), listener.mouseY() )
                    );
                }
                // if the mouse was not down, start a  new Path
                else {
                    addCurve( 
                        new Path( 
                            new Point( listener.mouseX(), listener.mouseY() ), 
                            new Point( listener.pMouseX(), listener.pMouseY() ),
                            this.color
                        ),
                        true // new action
                    );   
                }
            }
        }
        else if ( this.drawingMode == DrawingMode.RECTANGLE ) {
            if ( listener.isMouseDown() ) {
                if ( this.mouseWasDown ){ // continue old rectangle if not new click
                    this.tempCurve = new Rectangle( 
                        new Point( this.startPoint.x, this.startPoint.y ),
                        new Point( listener.mouseX(), listener.mouseY() ),
                        this.color
                    );
                }
                else { // start rectangle if new click
                    this.startPoint = new Point( listener.mouseX(), listener.mouseY() );
                }
            }
            else {
                if ( this.mouseWasDown ) {
                    addCurve( 
                        new Rectangle( 
                            new Point( this.startPoint.x, this.startPoint.y ),
                            new Point( listener.mouseX(), listener.mouseY() ),
                            this.color
                        ),
                        true // new action
                    );    
                }
                
                this.tempCurve = null;
            }
        }
        
        this.mouseWasDown = listener.isMouseDown();
    }
    
    @Override
    public void paint( final Graphics g ) {
        this.bufferG2.setColor( Color.WHITE );
        this.bufferG2.fillRect( 
            0, 
            0, 
            ( int ) this.myDimensions.getWidth(), 
            ( int ) this.myDimensions.getHeight() 
        );
                        
        for ( Curve currentPath: this.curves ) {
            currentPath.draw( this.bufferG2 );
        }
        
        if ( this.tempCurve != null ) {
            this.tempCurve.draw( this.bufferG2 );
        }
                
        g.drawImage( 
            this.buffer, 
            0, 
            0, 
            this 
        );
    }
    
    
    
    private class MyMouseListener extends MouseInputAdapter {
        
        private boolean isMouseDown;
        private Point mouseLocation;
        private Point lastLocation;
        
        public MyMouseListener() {
            super();
            
            this.isMouseDown = false;
        }
        
        @Override
        public void mousePressed( final MouseEvent aEvent ) {
            this.isMouseDown = true;
            this.lastLocation = aEvent.getPoint();
            this.mouseLocation = aEvent.getPoint();
            updateDrawing();
            repaint();
        }
        
        @Override
        public void mouseReleased( final MouseEvent aEvent ) {     
            this.isMouseDown = false;
            updateLocation( aEvent.getPoint() );
            updateDrawing();
            repaint();
        }
        
        @Override
        public void mouseMoved( final MouseEvent aEvent ) {           
            updateLocation( aEvent.getPoint() );
        }
        
        @Override
        public void mouseDragged( final MouseEvent aEvent ) {           
            updateLocation( aEvent.getPoint() );
            updateDrawing();
            repaint();
        }
        
        public boolean isMouseDown() {
            return this.isMouseDown;
        }
        
        @SuppressWarnings("unused")
        public Point mouseLoc() {
            return new Point( this.mouseLocation );
        }
        
        public int mouseX() {
            return this.mouseLocation.x;
        }
        
        public int mouseY() {
            return this.mouseLocation.y;
        }
        
        @SuppressWarnings("unused")
        public Point pMouseLoc() {
            return this.lastLocation;
        }
        
        public int pMouseX() {
            return this.lastLocation.x;
        }
        
        public int pMouseY() {
            return this.lastLocation.y;
        }
        
        private void updateLocation( final Point p ) {
            if ( this.mouseLocation == null ) {
                this.lastLocation = p;
            }
            else {
                this.lastLocation = this.mouseLocation;
            }
            
            this.mouseLocation = p;
        }
    }
}
