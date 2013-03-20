package edu.vanderbilt.hw3.driver;

/**
 * Represents a direction in which the sprite can move
 * in one step.
 * 
 * @author Mason Wright
 */
public enum Direction {
    /**
     * Sprite tries to move one space left.
     */
    LEFT, 
    
    /**
     * Sprite tries to move one space right.
     */
    RIGHT, 
    
    /**
     * Sprite tries to move one space up.
     */
    UP, 
    
    /**
     * Sprite tries to move one space down.
     */
    DOWN, 
    
    /**
     * Sprite does not try to move.
     */
    NONE
}
