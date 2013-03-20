package shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class Rectangle extends Curve {

	private final Point pointA;
	private final Point pointB;

	public Rectangle( final Point a, final Point b, final Color color ) {
	    super( color );
	    
		this.pointA = a;
		this.pointB = b;
	}

	public Point getPointA() {
		return this.pointA;
	}

	public Point getPointB() {
		return this.pointB;
	}
	
    @Override
    public void draw( final Graphics g ) {
        g.setColor( super.getColor() );
        
        g.drawLine( pointA.x, pointA.y, pointB.x, pointA.y ); 
        g.drawLine( pointA.x, pointB.y, pointB.x, pointB.y ); 
        g.drawLine( pointA.x, pointA.y, pointA.x, pointB.y ); 
        g.drawLine( pointB.x, pointA.y, pointB.x, pointB.y ); 
    }
}
