package edu.vanderbilt.hw3.playerview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import edu.vanderbilt.hw3.driver.Util;

/**
 * This class is the view for a maze, which lies within a player view.
 * 
 * @author Mason Wright
 */
public final class MazeView extends JPanel {

    /**
     * Used for serialization.
     */
    private static final long serialVersionUID = 9153408090164697130L;
    
    /**
     * Used in calculations.
     */
    private static final double HALF = 0.5;
    
    /**
     * Width of the view.
     */
    public static final int MY_WIDTH = 400;
    
    /**
     * Height of the view.
     */
    public static final int MY_HEIGHT = 400;
    
    /**
     * Width and height of each cell.
     */
    public static final int CELL_SIZE = 100;
    
    /**
     * Number of rows in the maze.
     */
    private int numRows;
    
    /**
     * Number of columns in the maze.
     */
    private int numCols;
    
    /**
     * Row of the goal cell, counting from top and
     * starting at 0.
     */
    private int endRow;
    
    /**
     * Column of the goal cell, counting from left and
     * starting at 0.
     */
    private int endCol;
    
    /**
     * The collection of vertical walls. Format:
     * [row from top][location in row from left]
     */
    private boolean[][] verticalWalls;
    
    /**
     * The collection of horizontal walls. Format:
     * [column from left][location in column from top]
     */
    private boolean [][] horizontalWalls;
    
    /**
     * The current position of the sprite.
     */
    private Point spriteLocation;
    
    /**
     * The width of the sprite.
     */
    private static final int SPRITE_WIDTH = 60;
    
    /**
     * Half the width of the sprite, provided for efficiency.
     */
    private static final int SPRITE_RADIUS = 30;
    
    /**
     * The width of the sprite's smile.
     */
    private static final int SMILE_WIDTH = 30;
    
    /**
     * Half the width of the smile, provided for efficiency.
     */
    private static final int SMILE_RADIUS = 15;
    
    /**
     * The vertical offset of the smile.
     */
    private static final int SMILE_OFFSET = 20;
    
    /**
     * The horizontal offset of the eyes.
     */
    private static final int EYE_X_OFFSET = 8;
    
    /**
     * The vertical offset of the eyes.
     */
    private static final int EYE_Y_OFFSET = 20;
    
    /**
     * The height of each eye.
     */
    private static final int EYE_HEIGHT = 10;

    /**
     * Constructor. Should be called only from
     * Event Dispatch Thread.
     */
    public MazeView() {
        super();
    }
    
    /**
     * Initialize the view. Should be called only from the
     * Event Dispatch Thread.
     */
    public void init() {
        this.setLayout(null);
        this.setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));
    }
    
    /**
     * Get the point where the sprite's end goal is located.
     * 
     * @return the Point location of the goal cell.
     */
    public Point getEndLocation() {
        final double half = 0.5;
        return new Point(
            (int) ((this.endCol + half) * MY_WIDTH / this.numCols), 
            (int) ((this.endRow + half) * MY_HEIGHT / this.numRows)
        );
    }
    
    @Override
    public void paintComponent(final Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0,  0, MY_WIDTH, MY_HEIGHT);
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, MY_WIDTH - 1, MY_HEIGHT - 1);
        
        if (this.numCols == 0 || this.numRows == 0) {
            return;
        }
        
        final int colWidth = MY_WIDTH / this.numCols;
        final int rowHeight = MY_HEIGHT / this.numRows;
        
        for (int row = 0; row < this.numRows; row++) {
            final double yTop = row * rowHeight;
            final double yBottom = (row + 1) * rowHeight;
            for (int index = 0; index < this.numCols + 1; index++) {
                final double xcor = index * colWidth;
                if (this.verticalWalls[row][index]) {
                    g.drawLine(
                        (int) xcor, 
                        (int) yTop, 
                        (int) xcor, 
                        (int) yBottom
                    );
                }
            }
        }
        
        for (int column = 0; column < this.numCols; column++) {
            final double xLeft = column * colWidth;
            final double xRight = (column + 1) * colWidth;
            for (int index = 0; index < this.numRows + 1; index++) {
                final double ycor = index * rowHeight;
                if (this.horizontalWalls[column][index]) {
                    g.drawLine(
                        (int) xLeft, 
                        (int) ycor, 
                        (int) xRight, 
                        (int) ycor
                    );
                }
            }
        }
        
        g.setColor(Color.GREEN);
        final int fontSize = 24;
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        final int xOffset = 25;
        final double endX = (this.endCol + HALF) * colWidth - xOffset;
        
        final int yOffset = 10;
        final double endY = 
            (this.endRow + HALF) * rowHeight + yOffset;
        g.drawString("End", (int) endX, (int) endY);
        
        drawSprite(g);
    }
    
    /**
     * Draw the sprite, in the shape of a smiley face.
     * @param g the graphics object that will draw the sprite
     */
    private void drawSprite(final Graphics g) {
        if (this.spriteLocation == null) {
            return;
        }
        
        g.setColor(Color.BLUE);
        
        final int circleDegrees = 360;
        
        // head outline
        g.drawArc(
            this.spriteLocation.x - SPRITE_RADIUS, 
            this.spriteLocation.y - SPRITE_RADIUS, 
            SPRITE_WIDTH, 
            SPRITE_WIDTH, 
            0, 
            circleDegrees
        );
        
        final int semicircleDegrees = 180;
        
        // smile
        g.drawArc(
            this.spriteLocation.x - SMILE_RADIUS,
            this.spriteLocation.y - SPRITE_RADIUS + SMILE_OFFSET,
            SMILE_WIDTH,
            SMILE_WIDTH,
            semicircleDegrees,
            semicircleDegrees
        );
        
        // left eye
        g.drawLine(
            this.spriteLocation.x - EYE_X_OFFSET, 
            this.spriteLocation.y - SPRITE_RADIUS + EYE_Y_OFFSET, 
            this.spriteLocation.x - EYE_X_OFFSET, 
            this.spriteLocation.y - SPRITE_RADIUS + EYE_Y_OFFSET + EYE_HEIGHT
        );
        
        // right eye
        g.drawLine(
            this.spriteLocation.x + EYE_X_OFFSET, 
            this.spriteLocation.y - SPRITE_RADIUS + EYE_Y_OFFSET, 
            this.spriteLocation.x + EYE_X_OFFSET, 
            this.spriteLocation.y - SPRITE_RADIUS + EYE_Y_OFFSET + EYE_HEIGHT
        );
    }
    
    /**
     * Set the location of the sprite.
     * @param location the new location of the sprite
     */
    public void setSpriteLocation(final Point location) {
        this.spriteLocation = location;
        repaint();
    }
    
    /**
     * Set the state of the maze. Used at startup.
     * 
     * @param aNumRows the number of rows in the maze
     * @param aNumCols the number of columns in the maze
     * @param aEndRow the goal row
     * @param aEndCol the goal column
     * @param aVerticalWalls positions of vertical walls
     * @param aHorizontalWalls positions of horizontal walls
     */
    public void setState(
        final int aNumRows,
        final int aNumCols,
        final int aEndRow,
        final int aEndCol,
        final boolean[][] aVerticalWalls,
        final boolean[][] aHorizontalWalls
    ) {
        this.numRows = aNumRows;
        this.numCols = aNumCols;
        this.endRow = aEndRow;
        this.endCol = aEndCol;
        this.verticalWalls = Util.copy2dArray(aVerticalWalls);
        this.horizontalWalls = Util.copy2dArray(aHorizontalWalls);
        repaint();
    }
}
