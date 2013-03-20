package edu.vanderbilt.hw3.playerview;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.hw3.controllers.PlayerController;

/**
 * The view for a maze and its controls.
 * 
 * @author Mason Wright
 */
public final class PlayerView extends JPanel implements ActionListener {
    
    /**
     * Used for serialization.
     */
    private static final long serialVersionUID = 6814699043422871322L;
    
    /**
     * The controller for this view.
     */
    private final transient PlayerController playerController;
    
    /**
     * The maze shown by this view.
     */
    private final MazeView mazeView;
    
    /**
     * The buttons for controlling this view.
     */
    private JPanel buttonPanel;
    
    /**
     * The play button.
     */
    private JButton playButton;
    
    /**
     * The stop button.
     */
    private JButton stopAndResetButton;
    
    /**
     * The string to show on the play button.
     */
    private static final String PLAY_STRING = "Play";
    
    /**
     * The string to show on the stop button.
     */
    private static final String STOP_AND_RESET_STRING = "Stop and Reset";
    
    /**
     * A label to indicate the direction of the current step.
     */
    private final JLabel stepLabel;
    
    /**
     * A label to indicate whether the maze has been beaten since
     * the path was edited.
     */
    private final JLabel statusLabel;
    
    /**
     * Constructor. Should be called only from the Event
     * Dispatch Thread.
     * 
     * @param aPlayerController the controller for this view
     */
    public PlayerView(final PlayerController aPlayerController) {
        this.playerController = aPlayerController;
        this.playerController.setPlayerView(this);
        this.stepLabel = new JLabel();
        this.statusLabel = new JLabel();
        this.mazeView = new MazeView();
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    
    /**
     * Initialize the view. Should be called only from
     * Event Dispatch Thread.
     */
    public void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        this.playerController.setPlayerView(this);
        
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(
            new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS)
        );
        
        this.playButton = new JButton(PLAY_STRING);
        this.playButton.addActionListener(this);
        this.buttonPanel.add(this.playButton);
        
        this.stopAndResetButton = new JButton(STOP_AND_RESET_STRING);
        this.stopAndResetButton.addActionListener(this);
        this.stopAndResetButton.setEnabled(false);
        this.buttonPanel.add(this.stopAndResetButton);     
        
        JPanel stepPanel = new JPanel();
        stepPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        stepPanel.add(this.stepLabel);
        stepPanel.setBackground(Color.WHITE);
        this.buttonPanel.add(stepPanel);
        
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(this.statusLabel);
        final int fontSize = 26;
        this.statusLabel.setFont(new Font("Sans Serif", Font.PLAIN, fontSize));
        this.statusLabel.setForeground(Color.GREEN);
        
        // this is just a small width used to be ignored
        final int preferredWidth = 100;
        
        // the height of 50 is a desired maximum height
        final int preferredHeight = 50;
        statusPanel.setPreferredSize(
            new Dimension(preferredWidth, preferredHeight)
        );
        
        this.mazeView.init();
        this.add(this.mazeView); 
        this.add(this.buttonPanel);
        this.add(statusPanel);
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
        this.mazeView.setState(
            aNumRows, 
            aNumCols, 
            aEndRow, 
            aEndCol, 
            aVerticalWalls, 
            aHorizontalWalls
        );
    }
    
    /**
     * Set the location of the sprite.
     * @param location the new location of the sprite
     */
     public void setSpriteLocation(final Point location) {
        this.mazeView.setSpriteLocation(location);
    }
     
     /**
      * Set the text of the status label.
      * Should be called only on the Event Dispatch Thread.
      * 
      * @param text the new text to use
      */
    public void setStatusLabel(final String text) {
        this.statusLabel.setText(text);
    }
     
     /**
      * Set the text of the step label.
      * Should be called only on the Event Dispatch Thread.
      * 
      * @param text the new text to use
      */
    public void setStepLabel(final String text) {
        this.stepLabel.setText(text);
    }
    
    /**
     * Get the Play button.
     * @return the Play button
     */
    JButton getPlayButton() {
        return this.playButton;
    }
    
    /**
     * Get the Stop and Reset button.
     * @return the Stop and Reset button
     */
    JButton getStopAndResetButton() {
        return this.stopAndResetButton;
    }
    
    /**
     * Set whether the Play button is enabled.
     * @param isEnabled if true, enable
     */
    public void setPlayEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               getPlayButton().setEnabled(isEnabled);
           }
        });
    }
    
    /**
     * Get the point location of the sprite's goal.
     * @return the point location of the sprite's goal
     */
    public Point getEndLocation() {
        return this.mazeView.getEndLocation();
    }
    
    /**
     * Set whether the Stop and Reset button is enabled.
     * @param isEnabled if true, enable
     */
    public void setStopAndResetEnabled(final boolean isEnabled) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getStopAndResetButton().setEnabled(isEnabled);
            }
         });
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        if (event.getSource().equals(this.playButton)) {
            this.playerController.play();
        } else if (event.getSource().equals(this.stopAndResetButton)) {
            this.playerController.stopAndReset();
        } else {
            throw new IllegalStateException();
        }
    }
}
