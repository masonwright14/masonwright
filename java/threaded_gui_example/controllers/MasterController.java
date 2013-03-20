package edu.vanderbilt.hw3.controllers;

import javax.swing.SwingUtilities;

import edu.vanderbilt.hw3.masterview.MasterView;
import edu.vanderbilt.hw3.models.MazeModel;
import edu.vanderbilt.hw3.models.PathModel;

/**
 * This class controls the overall project. It is mostly
 * concerned with initializing the other controllers.
 * 
 * @author Mason Wright
 */
public final class MasterController {

    /**
     * The view for the project as a whole.
     */
    private final MasterView masterView;
    
    /**
     * The controller for the path editor.
     */
    private final EditorController editorController;
    
    /**
     * The controller for the maze on the left.
     */
    private final PlayerController leftPlayerController;
    
    /**
     * The controller for the maze on the right.
     */
    private final PlayerController rightPlayerController;
    
    /**
     * The path to be walked by the sprite in each maze.
     */
    private final PathModel pathModel;
    
    /**
     * Get the master view.
     * @return the master view
     */
    MasterView getMasterView() {
        return this.masterView;
    }
    
    /**
     * Constructor.
     */
    public MasterController() {
        this.pathModel = new PathModel();
        this.editorController = new EditorController(this.pathModel);
        
        boolean[][] leftVertWalls = 
            {
                {true, false, false, false, true},
                {true, true, true, true, true},
                {true, true, true, true, true},
                {true, false, false, false, true}
            };
        boolean[][]leftHorizWalls =
            {
                {true, false, false, false, true},
                {true, false, false, true, true},
                {true, true, false, false, true},
                {true, false, false, true, true}
            };
        
        final int rows = 4;
        final int columns = 4;
        final int leftEndRow = 2;
        final int leftEndColumn = 3;
        MazeModel leftMazeModel = 
            new MazeModel(
                rows, // number of rows
                columns, // number of columns
                1, // start row
                1, // start column
                leftEndRow, // end row
                leftEndColumn, // end column
                leftVertWalls, 
                leftHorizWalls
            );
        this.leftPlayerController = 
            new PlayerController(leftMazeModel, this.pathModel);
        
        boolean[][] rightVertWalls = 
            {
                {true, false, false, false, true},
                {true, true, true, true, true},
                {true, true, true, true, true},
                {true, false, false, false, true}
            };
        boolean[][]rightHorizWalls =
            {
                {true, false, false, true, true},
                {true, false, false, false, true},
                {true, true, false, false, true},
                {true, false, false, false, true}
            };
        MazeModel rightMazeModel = 
            new MazeModel(
                rows, // number of rows
                columns, // number of columns
                2, // start row
                1, // start column
                1, // end row
                0, // end column
                rightVertWalls, 
                rightHorizWalls
            );
        this.rightPlayerController = 
            new PlayerController(rightMazeModel, this.pathModel);
        
        this.editorController.setMasterController(this);
        
        this.masterView = new MasterView(
            this.editorController,
            this.leftPlayerController,
            this.rightPlayerController
        );
    }
    
    /**
     * Get the editor controller.
     * @return the editor controller
     */
    EditorController getEditorController() {
        return this.editorController;
    }
    
    /**
     * Get the left player controller.
     * @return the left player controller
     */
    PlayerController getLeftPlayerController() {
        return this.leftPlayerController;
    }
    
    /**
     * Get the right player controller.
     * @return the right player controller
     */
    PlayerController getRightPlayerController() {
        return this.rightPlayerController;
    }
    
    /**
     * Called by a player controller when a run finishes.
     * If both mazes have been completed since the model
     * was edited, displays an informational dialog
     * box with congratulations.
     */
    public void checkIfGameOver() {
        if (
            this.leftPlayerController.isWon()
            && this.rightPlayerController.isWon()
        ) {
            this.masterView.showVictoryMessage();
        }
    }
    
    /**
     * Called by the player controller when a path is edited,
     * so the player controllers will be notified.
     * 
     * Should be called only on the Event Dispatch Thread.
     */
    public void pathEdited() {
        this.leftPlayerController.pathEdited();
        this.rightPlayerController.pathEdited();
    }
    
    /**
     * Initializes the views and controllers.
     */
    public void startUp() {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getMasterView().init();
               getLeftPlayerController().
                   setMasterController(MasterController.this);
               getRightPlayerController().
                   setMasterController(MasterController.this);
               getLeftPlayerController().init();
               getRightPlayerController().init();
               getMasterView().show();
           }
        });
    }
}
