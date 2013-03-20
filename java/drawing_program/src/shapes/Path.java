package shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import util.Util;




public class Path extends Curve {

    private final List<Point> points;
    
    public Path( final List<Point> aPoints, final Color color ) {
        super( color );
        
        this.points = Util.cloneList( aPoints );
    }
    
    public Path( final Point pointA, final Point pointB, final Color color ) {
        super( color );
        
        this.points = new ArrayList<Point>();
        this.points.add( pointA );
        this.points.add( pointB );
    }
    
    
    public void addPoint( final Point toAdd ) {
        // don't add a point if it's the same as the last point in the list (redundant)
        if ( this.points.get( this.points.size() - 1 ).equals( toAdd ) )
            return;
        
        this.points.add( toAdd );
    }
    
    
    @Override
    public void draw( final Graphics g ) {
        g.setColor( super.getColor() );
        
        for ( int i = 0; i < this.points.size() - 1; i++ ) {
            Point firstPoint = this.points.get( i );
            Point nextPoint = this.points.get( i + 1 );
            g.drawLine( firstPoint.x, firstPoint.y, nextPoint.x, nextPoint.y ); 
        }
    }
}
