package edu.vanderbilt.hw3.controllers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import edu.vanderbilt.hw3.driver.Direction;
import edu.vanderbilt.hw3.models.MazeModel;
import edu.vanderbilt.hw3.models.PathModel;
import edu.vanderbilt.hw3.playerview.MazeView;
import edu.vanderbilt.hw3.playerview.PlayerView;

/**
 * This is the controller for playing back the path
 * for one maze.
 * 
 * @author Mason Wright
 */
public final class PlayerController {
    
    /**
     * Milliseconds of delay between frames during playback.
     */
    private static final int FRAME_DELAY_IN_MILLIS = 50;
    
    /**
     * Number of frames per move the sprite makes.
     */
    private static final int FRAMES_PER_MOVE = 10;
    
    /**
     * Starts at 0, goes to FRAMES_PER_MOVE * pathModel.getPath().size().
     */
    private int counter;
    
    /**
     * A flag used to stop a run in progress.
     * AtomicBoolean is thread-safe, which is needed because
     * the Event Thread will set the flag, and the executor
     * will read it.
     */
    private final AtomicBoolean canRun;

    /**
     * Used to schedule periodic calls to update the view
     * for a "moving picture" effect.
     */
    private final ScheduledExecutorService executor;
    
    /**
     * The view to be updated by this controller.
     */
    private PlayerView view;
    
    /**
     * The model of the maze for this controller.
     */
    private final MazeModel mazeModel;
    
    /**
     * The path to be walked by the sprite for this controller.
     */
    private final PathModel pathModel;
    
    /**
     * Current location of the sprite in the maze.
     */
    private Point currentSpriteLocation;
    
    /**
     * A list of the directions in which the sprite will
     * actually move, given the position of the walls
     * in the maze.
     */
    private final List<Direction> actualMoveDirections;
    
    /**
     * Notified when a run completes, so the MasterController
     * can check if the game has been won.
     */
    private MasterController masterController;
    
    /**
     * Constructor.
     * 
     * @param aMazeModel the maze layout for this controller
     * @param aPathModel the path to be walked by the sprite for this
     * controller
     */
    public PlayerController(
        final MazeModel aMazeModel,
        final PathModel aPathModel
    ) {
        this.mazeModel = aMazeModel;
        this.pathModel = aPathModel;
        
        this.canRun = new AtomicBoolean(false);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.counter = 0;
        this.actualMoveDirections = new ArrayList<Direction>();
    }
    
    /**
     * Set the master controller to be notified when the user
     * might have won.
     * @param controller the controller to notify
     */
    public void setMasterController(
        final MasterController controller
    ) {
        this.masterController = controller;
    }
    
    /**
     * Sets up this controller. Should be called only from
     * the Event Dispatch Thread.
     */
    public void init() {
        if (this.view == null) {
            throw new IllegalStateException();
        }
        
        this.view.setState(
            this.mazeModel.getNumRows(), 
            this.mazeModel.getNumCols(), 
            this.mazeModel.getEndRow(), 
            this.mazeModel.getEndCol(), 
            this.mazeModel.getVerticalWalls(), 
            this.mazeModel.getHorizontalWalls()
        );
        
        this.view.setSpriteLocation(getStartLocation());
        this.view.setStepLabel("");
    }
    
    /**
     * Get the starting location of the sprite.
     * @return the starting point of the sprite
     */
    private Point getStartLocation() {
        final double half = 0.5;
        double startX = (this.mazeModel.getStartCol() + half) 
            * MazeView.MY_WIDTH / this.mazeModel.getNumCols();
        double startY = (this.mazeModel.getStartRow() + half) 
            * MazeView.MY_HEIGHT / this.mazeModel.getNumRows();
        return new Point((int) startX, (int) startY);
    }
    
    /**
     * Sets the view to be updated by this controller.
     * 
     * @param aView the view to be managed by this controller
     */
    public void setPlayerView(final PlayerView aView) {
        this.view = aView;
    }
    
    /**
     * Return true if the MazeModel has been beaten since the
     * path was edited.
     * 
     * @return whether the MazeModel has been beaten since the
     * path was edited
     */
    public boolean isWon() {
        return this.mazeModel.isBeaten();
    }
    
    /**
     * Load the list of actual move directions, based on the
     * directions in which the sprite should attempt to move,
     * and the position of walls in the maze.
     */
    private void setupActualMoveDirections() {
        this.actualMoveDirections.clear();
        
        int spriteRow = this.mazeModel.getStartRow();
        int spriteCol = this.mazeModel.getStartCol();
        for (Direction currentDirection: this.pathModel.getPath()) {
            if (
                this.mazeModel.isPassable(
                    spriteRow, 
                    spriteCol, 
                    currentDirection
                )
            ) {
                this.actualMoveDirections.add(currentDirection);
                switch(currentDirection) {
                case UP:
                    spriteRow--;
                    break;
                case DOWN:
                    spriteRow++;
                    break;
                case LEFT:
                    spriteCol--;
                    break;
                case RIGHT:
                    spriteCol++;
                    break;
                case NONE:
                    break;
                default:
                    break;
                }
            } else {
                this.actualMoveDirections.add(Direction.NONE);
            }
        }
    }
    
    /**
     * Plays back the path to be walked by the sprite in the view.
     */
    public void play() {
        if (this.view == null) {
            throw new IllegalStateException();
        }
        
        this.view.setPlayEnabled(false);
        this.view.setStopAndResetEnabled(true);
        
        this.canRun.set(true);
        this.pathModel.startReading(this);
        this.counter = 0;
        this.currentSpriteLocation = getStartLocation();
        setupActualMoveDirections();
        
        callNextStep();
    }
    
    /**
     * Get the direction in which the sprite is currently
     * moving, based on the list of actual move directions.
     * 
     * @return the direction in which the sprite is currently
     * moving
     */
    private Direction getSpriteDirection() {
        // stage is 0-based
        final int stage = getStage();
        if (stage >= this.pathModel.getPath().size()) {
            return Direction.NONE;
        }

        return this.actualMoveDirections.get(stage);
    }
    
    /**
     * Get the string that should be used in the current path lael.
     * @return the string to show in the path label
     */
    private String getLabelString() {
        final StringBuilder builder = new StringBuilder();
        int stage = getStage();
        if (stage >= this.pathModel.getPath().size()) {
            builder.append("done");
        } else {
            builder.append(stage + 1).append(". ");
            
            if (isPathBlocked()) {
                builder.append("[blocked]");
            } else {
                builder.append(getSpriteDirection().toString());
            }
        } 
        
        return builder.toString();
    }
    
    /**
     * Get the number (0-based) of the current stage, from 0-7,
     * while the sprite is running.
     * @return the number of the current stage
     */
    private int getStage() {
        return this.counter / FRAMES_PER_MOVE;
    }
    
    /**
     * Returns true if the current direction requested to move
     * is blocked by a wall.
     * 
     * @return whether the current direction to move is blocked
     */
    private boolean isPathBlocked() {
        final int stage = getStage();
        if (stage >= this.pathModel.getPath().size()) {
            // run has ended
            return false;
        }
       
        if (this.pathModel.getPath().get(stage) == Direction.NONE) {
            // the request is to stay still
            return false;
        }
        
        // return true if the actual direction is different from
        // the requested direction
        return this.actualMoveDirections.get(stage) 
            != this.pathModel.getPath().get(stage);
    }
    
    /**
     * Get the location to which the sprite should move
     * in the next frame.
     * 
     * @return the point where the sprite should move next
     */
    private Point getNextPoint() {
        int newX;
        int newY;
        int moveDistance = MazeView.CELL_SIZE / FRAMES_PER_MOVE;
        switch(getSpriteDirection()) {
        case UP:
            newX = this.currentSpriteLocation.x;
            newY = this.currentSpriteLocation.y - moveDistance;
            return new Point(newX, newY);
        case DOWN:
            newX = this.currentSpriteLocation.x;
            newY = this.currentSpriteLocation.y + moveDistance;
            return new Point(newX, newY);
        case LEFT:
            newX = this.currentSpriteLocation.x - moveDistance;
            newY = this.currentSpriteLocation.y;
            return new Point(newX, newY);
        case RIGHT:
            newX = this.currentSpriteLocation.x + moveDistance;
            newY = this.currentSpriteLocation.y;
            return new Point(newX, newY);
        case NONE:
            return this.currentSpriteLocation;
        default:
            throw new IllegalStateException();
        }
    }
    
    /**
     * Advance one step in the playback. Should
     * be called only by the executor.
     */
    void step() {
        if (!this.canRun.get()) {
            cleanUpAfterRun();
            return;
        }

        this.currentSpriteLocation = getNextPoint();
        this.view.setSpriteLocation(this.currentSpriteLocation);
        this.view.setStepLabel(getLabelString());
        this.counter++;
        
        if (getStage() >= this.pathModel.getPath().size()) {
            this.canRun.set(false);
            if (this.currentSpriteLocation.equals(this.view.getEndLocation())) {
                // succeeded in the maze
                this.mazeModel.setIsBeaten(true);
                this.view.setStatusLabel("Maze cleared");
            } else {
                // failed in the maze
                this.mazeModel.setIsBeaten(false);
                this.view.setStepLabel("");
            }
            
            this.masterController.checkIfGameOver();
        }
        
        callNextStep();
    }
    
    /**
     * Resets the "maze cleared" label.
     * Must be called only on the Event Dispatch Thread.
     */
    public void pathEdited() {
        this.mazeModel.setIsBeaten(false);
        this.view.setStatusLabel("");
    }
    
    /**
     * Calls the executor to run step again.
     */
    private void callNextStep() {
        this.executor.schedule(
            new Runnable() {
                @Override
                public void run() {
                    step();
                }
            }, 
            FRAME_DELAY_IN_MILLIS, 
            TimeUnit.MILLISECONDS
        ); 
    }
    
    /**
     * Reset the running variables to wait state.
     */
    private void cleanUpAfterRun() {
        this.view.setPlayEnabled(true);
        this.view.setStopAndResetEnabled(false);
        this.pathModel.stopReading(this);
        this.view.setSpriteLocation(getStartLocation());
        this.view.setStepLabel("");
    }
    
    /**
     * Stops playing back the path and returns the sprite to its
     * start position.
     */
    public void stopAndReset() {
        if (this.view == null) {
            throw new IllegalStateException();
        }
                
        this.canRun.set(false);        
    }
}
