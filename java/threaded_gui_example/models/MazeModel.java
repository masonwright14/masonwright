package edu.vanderbilt.hw3.models;

import edu.vanderbilt.hw3.driver.Direction;
import edu.vanderbilt.hw3.driver.Util;

/**
 * This class holds the layout of a maze.
 * 
 * @author Mason Wright
 */
public final class MazeModel {

    /**
     * How many rows in the maze.
     */
    private final int numRows;
    
    /**
     * How many columns in the maze.
     */
    private final int numCols;
    
    /**
     * Which row the sprite starts in.
     */
    private final int startRow;
    
    /**
     * Which column the sprite starts in.
     */
    private final int startCol;
    
    /**
     * Which row the sprite should reach.
     */
    private final int endRow;
    
    /**
     * Which column the sprite should reach.
     */
    private final int endCol;
    
    /**
     * Whether there is a vertical wall at each position in the maze.
     * The array format is [row from top][position from left].
     */
    private final boolean[][] verticalWalls;
    
    /**
     * Whether there is a horizonal wall at each position in the maze.
     * The array format is [column from left][position from top].
     */
    private final boolean [][] horizontalWalls;
    
    /**
     * True if the maze has been beaten since the last time the
     * path was edited. This variable should be reset to false
     * any time the path is edited. The maze is "beaten" only
     * when a path is actually played back and the sprite is
     * on the goal cell at the END of the run.
     */
    private boolean isBeaten;
        
    /**
     * Constructor.
     * 
     * @param aNumRows how many rows in the maze
     * @param aNumCols how many columns in the maze
     * @param aStartRow sprite's starting row
     * @param aStartCol sprite's starting column
     * @param aEndRow sprite's goal row
     * @param aEndCol sprite's goal column
     * @param aVerticalWalls positions of vertical walls: 
     * [row from top][position from left]
     * @param aHorizontalWalls positions of horizontal walls: 
     * [column from left][position from top]
     */
    public MazeModel(
        final int aNumRows,
        final int aNumCols,
        final int aStartRow,
        final int aStartCol,
        final int aEndRow,
        final int aEndCol,
        final boolean[][] aVerticalWalls,
        final boolean[][] aHorizontalWalls
    ) { 
        this.numRows = aNumRows;
        this.numCols = aNumCols;
        this.startRow = aStartRow;
        this.startCol = aStartCol;
        this.endRow = aEndRow;
        this.endCol = aEndCol;
        this.verticalWalls = Util.copy2dArray(aVerticalWalls);
        this.horizontalWalls = Util.copy2dArray(aHorizontalWalls);
        this.isBeaten = false;
    }
    
    /**
     * Return whether the maze has been beaten since the path
     * was last edited.
     * 
     * @return whether the maze has been beaten since the path
     * was last edited 
     */
    public boolean isBeaten() {
        return this.isBeaten;
    }
    
    /**
     * Set whether the maze has been beaten since
     * the path was last edited.
     * 
     * @param aIsBeaten the new value to use
     */
    public void setIsBeaten(final boolean aIsBeaten) {
        this.isBeaten = aIsBeaten;
    }

    /**
     * Get number of rows in the maze.
     * @return number of rows in the maze
     */
    public int getNumRows() {
        return this.numRows;
    }

    /**
     * Get number of columns in the maze.
     * @return number of columns in the maze
     */
    public int getNumCols() {
        return this.numCols;
    }

    /**
     * Get sprite's starting row.
     * @return sprite's starting row
     */
    public int getStartRow() {
        return this.startRow;
    }

    /**
     * Get sprite's starting column.
     * @return sprite's starting column
     */
    public int getStartCol() {
        return this.startCol;
    }

    /**
     * Get sprite's goal row.
     * @return sprite's goal row
     */
    public int getEndRow() {
        return this.endRow;
    }

    /**
     * Get sprite's goal column.
     * @return sprite's goal column
     */
    public int getEndCol() {
        return this.endCol;
    }

    /**
     * Get the collection of vertical walls in the maze.
     * Format: [row from top][position from left].
     * @return the collection of vertical walls
     */
    public boolean[][] getVerticalWalls() {
        return Util.copy2dArray(this.verticalWalls);
    }

    /**
     * Get the collection of horizontal walls in the maze.
     * Format: [column from left][position from top].
     * @return the collection of horizontal walls
     */
    public boolean[][] getHorizontalWalls() {
        return Util.copy2dArray(this.horizontalWalls);
    }
    
    /**
     * Returns true if there is a vertical wall in row Q, at location R.
     * @param row counting from the top, starting at 0
     * @param position counting left to right, starting to left
     * of leftmost column, starting at 0
     * @return whether there is a vertical wall at this location
     */
    public boolean isVerticalWall(final int row, final int position) {
        if (
            row >= this.verticalWalls.length 
            || position >= this.verticalWalls[row].length
        ) {
            throw new IllegalArgumentException();
        }
        
        return this.verticalWalls[row][position];
    }
    
    /**
     * Returns true if there is a horizontal wall in column Q, at location R.
     * @param column counting from the left, starting at 0
     * @param position counting top to bottom, starting above the top column,
     * starting at 0
     * @return whether there is a horizontal wall at this location
     */
    public boolean isHorizontalWall(final int column, final int position) {
        if (
            column >= this.horizontalWalls.length 
            || position >= this.horizontalWalls[column].length
        ) {
            throw new IllegalArgumentException();
        }
        
        return this.horizontalWalls[column][position];
    }
    
    /**
     * Returns true if the sprite can move in the given direction,
     * starting from the given position in the maze.
     * 
     * @param oldRow the sprite's current row
     * @param oldColumn the sprite's current column
     * @param direction the direction in which the sprite might move
     * @return whether the sprite can move in the given direction
     */
    public boolean isPassable(
        final int oldRow, 
        final int oldColumn, 
        final Direction direction
    ) {
        if (direction == Direction.UP) {
            return oldRow != 0 && !isHorizontalWall(oldColumn, oldRow);
        } else if (direction == Direction.DOWN) {
            return 
                oldRow != this.numRows - 1 
                && !isHorizontalWall(oldColumn, oldRow + 1);
        } else if (direction == Direction.LEFT) {
            return oldColumn != 0 && !isVerticalWall(oldRow, oldColumn);
        } else if (direction == Direction.RIGHT) {
            return 
                oldColumn != this.numCols - 1 
                && !isVerticalWall(oldRow, oldColumn + 1);
        } else if (direction == Direction.NONE) {
             return true;
        } else {
            throw new IllegalStateException();
        }
    }
}
