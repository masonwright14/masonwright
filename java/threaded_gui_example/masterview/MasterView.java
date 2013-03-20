package edu.vanderbilt.hw3.masterview;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import edu.vanderbilt.hw3.controllers.EditorController;
import edu.vanderbilt.hw3.controllers.PlayerController;
import edu.vanderbilt.hw3.editorview.EditorView;
import edu.vanderbilt.hw3.playerview.PlayerView;

/**
 * An overall view that displays the sub-view for each part of the program.
 * 
 * @author Mason Wright
 */
public final class MasterView {

    /**
     * The view for the path editor.
     */
    private EditorView editorView;
    
    /**
     * The view for the left maze.
     */
    private PlayerView leftPlayerView;
    
    /**
     * The view for the right maze.
     */
    private PlayerView rightPlayerView;
    
    /**
     * The frame that holds the overall view.
     */
    private JFrame frame;
    
    /**
     * The title String.
     */
    private static final String TITLE = "Ariadne's Thread";
    
    /**
     * Constructor. May be called from off the Event Dispatch
     * Thread.
     * 
     * @param aEditorController the controller for the path editor
     * @param aLeftPlayerController the controller for the left maze
     * @param aRightPlayerController the controller for the right maze
     */
    public MasterView(
        final EditorController aEditorController,
        final PlayerController aLeftPlayerController,
        final PlayerController aRightPlayerController
    ) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setEditorView(new EditorView(aEditorController));
                setLeftPlayerView(new PlayerView(aLeftPlayerController));
                setRightPlayerView(new PlayerView(aRightPlayerController));
            }
        });

    }
    
    /**
     * Set the editor view.
     * 
     * @param view the editor view
     */
    void setEditorView(final EditorView view) {
        this.editorView = view;
    }
    
    /**
     * Set the left player view.
     * 
     * @param view the player view
     */
    void setLeftPlayerView(final PlayerView view) {
        this.leftPlayerView = view;
    }
    
    /**
     * Set the right player view.
     * 
     * @param view the player view
     */
    void setRightPlayerView(final PlayerView view) {
        this.rightPlayerView = view;
    }
    
    /**
     * Set up the master view and its slave views. Must be called
     * from the Event Dispatch Thread.
     */
    public void init() {
        if (
            this.editorView == null
            || this.leftPlayerView == null
            || this.rightPlayerView == null
        ) {
            throw new IllegalStateException();
        }
        
        this.frame = new JFrame(TITLE);
        this.frame.getContentPane().setLayout(
            new BoxLayout(this.frame.getContentPane(), BoxLayout.Y_AXIS)
        );
        
        this.editorView.init();
        this.frame.getContentPane().add(this.editorView);
        
        this.leftPlayerView.init();        
        this.rightPlayerView.init();
        
        final JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.X_AXIS));
        playerPanel.add(this.leftPlayerView);
        playerPanel.add(this.rightPlayerView);
        
        this.frame.getContentPane().add(playerPanel);
        
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
        
        
    }
    
    /**
     * Show a dialog box with instructions.
     */
    public void showInstructions() {
        SwingUtilities.invokeLater(new Runnable() {
            private static final String INSTRUCTIONS_TEXT =
                "Edit the path the smiley face will try to take.\n"
                + "Then press play on both mazes.\n"
                + "You win when the smiley face completes both mazes,\n"
                + "without an edit to the path in between!";
                
            @Override
            public void run() {
                JOptionPane.showMessageDialog(
                    null,
                    INSTRUCTIONS_TEXT,
                    "How to play",
                    JOptionPane.INFORMATION_MESSAGE
                ); 
            }
        });
    }
    
    /**
     * Show a dialog box to congratulate the user.
     */
    public void showVictoryMessage() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(
                    null,
                    "You win! Both mazes complete.",
                    "Hooray!",
                    JOptionPane.INFORMATION_MESSAGE
                ); 
            }
        });
    }
    
    /**
     * Show the GUI. Called after init.
     */
    public void show() {
        SwingUtilities.invokeLater(new Runnable() {
           @Override
           public void run() {
               doShow();
           }
        });
    }
    
    /**
     * Actually show the GUI. Should run on the Event Dispatch Thread.
     */
    void doShow() {
        if (this.frame == null) {
            throw new IllegalStateException();
        }
        
        this.frame.pack();
        this.frame.setVisible(true);
        showInstructions();
    }
}
