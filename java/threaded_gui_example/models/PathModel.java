package edu.vanderbilt.hw3.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.vanderbilt.hw3.driver.Direction;
import edu.vanderbilt.hw3.controllers.PlayerController;

/**
 * Holds the path in which a sprite will try to move.
 * 
 * @author Mason Wright
 */
public final class PathModel {

    /**
     * The list of directions the sprite will attempt to move.
     */
    private final List<Direction> path;
    
    /**
     * The number of steps the sprite might take.
     */
    private static final int PATH_LENGTH = 8;
    
    /**
     * The set of players that are playing back this path.
     */
    private Set<PlayerController> readers;
    
    /**
     * Constructor.
     */
    public PathModel() {
        this.path = new ArrayList<Direction>();
        for (int i = 0; i < PATH_LENGTH; i++) {
            this.path.add(Direction.NONE);
        }
        
        this.readers = new HashSet<PlayerController>();
    }
    
    /**
     * Notifies the model that a player is playing back the path.
     * The path cannot be edited while a player is reading from it.
     * 
     * @param playerController the player that is reading the path
     */
    public void startReading(final PlayerController playerController) {
        this.readers.add(playerController);
    }
    
    /**
     * Notifies the model that a player is no longer playing back the path.
     * The path can be edited if no player is reading from it.
     * 
     * @param playerController the player that has stopped reading the path
     */
    public void stopReading(final PlayerController playerController) {
        this.readers.remove(playerController);
    }
    
    /**
     * Returns true if any player is reading back the path.
     * If a player is using the path, the path cannot be edited.
     * 
     * @return whether any player is reading back the path
     */
    public boolean isAnyoneReading() {
        return !this.readers.isEmpty();
    }
    
    /**
     * Returns the list of directions for the sprite to try to move.
     * @return the list of directions for the sprite to try to move
     */
    public List<Direction> getPath() {
        return this.path;
    }
    
    /**
     * Returns the direction in which the sprite will try to move
     * at position "index".
     * 
     * @param index the index in the list of directions
     * @return the direction to try to move
     */
    public Direction getDirection(final int index) {
        if (index < 0 || index >= PATH_LENGTH) {
            throw new IllegalArgumentException();
        }
        
        return this.path.get(index);
    }
    
    /**
     * Sets the direction for the sprite to try to move at a given index.
     * @param direction the direction to try to move
     * @param index the index in the list of directions
     */
    public void setDirection(
        final Direction direction, 
        final int index
    ) {
        if (index < 0 || index >= PATH_LENGTH) {
            throw new IllegalArgumentException();
        }
        
        this.path.set(index, direction);
    }
}
