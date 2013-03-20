package shapes;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Curve {

    private Color color;
    
    public Curve( final Color color ) {
        this.color = color;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public void setColor( final Color newColor ) {
        this.color = newColor;
    }
    
    /**
     * @param g  
     */
    public void draw( final Graphics g ) {
        // do nothing
    }
}