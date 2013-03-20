package edu.vanderbilt.hw3.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import edu.vanderbilt.hw3.driver.Direction;
import edu.vanderbilt.hw3.editorview.EditorView;
import edu.vanderbilt.hw3.models.PathModel;

/**
 * This class holds the logic behind the editor, which
 * edits the path that the sprite will take through a maze.
 * 
 * @author Mason Wright
 */
public final class EditorController {
    
    /**
     * Button text for the "none" direction.
     */
    private static final String NONE_STRING = "_";
    
    /**
     * Button text for the "left" direction.
     */
    private static final String LEFT_STRING = "Left";
    
    /**
     * Button text for the "right" direction.
     */
    private static final String RIGHT_STRING = "Right";
    
    /**
     * Button text for the "up" direction.
     */
    private static final String UP_STRING = "Up";
    
    /**
     * Button text for the "down" direction.
     */
    private static final String DOWN_STRING = "Down";
   
    /**
     * The view for this controller.
     */
    private EditorView view;
    
    /**
     * The model edited by this controller.
     */
    private final PathModel pathModel;
    
    /**
     * Must be notified when the model is edited.
     */
    private MasterController masterController;
    
    private boolean isMutualExclusionEnabled;
        
    /**
     * Constructor.
     * 
     * @param aPathModel the model to be edited by this controller
     */
    public EditorController(final PathModel aPathModel) {
        this.pathModel = aPathModel;
        this.isMutualExclusionEnabled = true;
    }
    
    /**
     * Set the master controller.
     * 
     * @param controller the master controller to notify when the path
     * is edited
     */
    public void setMasterController(final MasterController controller) {
        this.masterController = controller;
    }

    
    /**
     * Set the view to be updated by this controller.
     * 
     * @param aView the view to be updated by this controller
     */
    public void setEditorView(
        final EditorView aView
    ) {
        this.view = aView;
        updateView();
    }
    
    /**
     * Set whether mutual exclusion should be enabled for reading and
     * writing to the path model at the same time.
     * 
     * @param isEnabled whether mutual exclusion should be enabled
     */
    public void setMutualExclusionEnabled(final boolean isEnabled) {
        this.isMutualExclusionEnabled = isEnabled;
    }
    
    /**
     * Return the editor view.
     * 
     * @return the EditorView
     */
    EditorView getView() {
        return this.view;
    }
    
    /**
     * Return the path model.
     * 
     * @return the PathModel
     */
    PathModel getPathModel() {
        return this.pathModel;
    }
    
    /**
     * Change the state of the model, setting a direction for
     * the path to take at a certain index in the list of directions.
     * 
     * @param newText the new text of a direction button
     * @param index the index of the direction in the list
     */
    public void buttonClicked(
        final String newText, 
        final int index
    ) {
        if (this.pathModel.isAnyoneReading() && this.isMutualExclusionEnabled) {
            JOptionPane.showMessageDialog(
                null,
                "You can't edit while a model is running.",
                "Does not compute",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            Direction oldDirection = getDirectionFromString(newText);
            Direction newDirection = getNextDirection(oldDirection);
            
            if (this.masterController == null) {
                throw new IllegalStateException();
            }
            this.masterController.pathEdited();
            
            this.pathModel.setDirection(newDirection, index);
            updateView();
        }
    }
    
    /**
     * Updates the text on the direction buttons.
     */
    private void updateView() {
        List<Direction> pathAsDirections = this.pathModel.getPath();
        final List<String> pathAsStrings = new ArrayList<String>();
        for (Direction currentDirection: pathAsDirections) {
            pathAsStrings.add(getStringFromDirection(currentDirection));
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getView().setButtonLabels(pathAsStrings);   
            }
        });
    }
    
    /**
     * Returns the Direction associated with the given button text String.
     * 
     * @param string a button text String
     * @return the associated Direction
     */
    private Direction getDirectionFromString(final String string) {
        if (string.equals(NONE_STRING)) {
            return Direction.NONE;
        } else if (string.equals(LEFT_STRING)) {
            return Direction.LEFT;
        } else if (string.equals(RIGHT_STRING)) {
            return Direction.RIGHT;
        } else if (string.equals(UP_STRING)) {
            return Direction.UP;
        } else if (string.equals(DOWN_STRING)) {
            return Direction.DOWN;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * Returns the button text String associated with the given Direction.
     * 
     * @param direction a Direction
     * @return the associated button text String
     */
    String getStringFromDirection(final Direction direction) {
        switch (direction) {
        case NONE: return NONE_STRING;
        case LEFT: return LEFT_STRING;
        case RIGHT: return RIGHT_STRING;
        case UP: return UP_STRING;
        case DOWN: return DOWN_STRING;
        default:
            throw new IllegalStateException();
        }
    }
    
    /**
     * Returns the next direction to display as the user clicks a button
     * to cycle through the possible choices.
     * 
     * @param oldDirection the previous Direction selected
     * @return the next Direction in the cycle
     */
    private Direction getNextDirection(final Direction oldDirection) {
        switch (oldDirection) {
        case NONE: return Direction.LEFT;
        case LEFT: return Direction.RIGHT;
        case RIGHT: return Direction.UP;
        case UP: return Direction.DOWN;
        case DOWN: return Direction.NONE;
        default:
            throw new IllegalStateException();
        }
    }
}
