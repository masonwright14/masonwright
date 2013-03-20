package edu.vanderbilt.hw3.editorview;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import edu.vanderbilt.hw3.controllers.EditorController;

/**
 * This view displays buttons for editing the path to be taken
 * by the sprite in each maze.
 * 
 * @author Mason Wright
 */
public final class EditorView extends JPanel implements ActionListener {

    /**
     * Used for serialization.
     */
    private static final long serialVersionUID = -6812093569697898976L;
    
    /**
     * The controller that handles user actions in the view.
     */
    private final transient EditorController editorController;
    
    /**
     * The width of the panel.
     */
    private static final int MY_WIDTH = 800;
    
    /**
     * The height of the panel.
     */
    private static final int MY_HEIGHT = 80;
    
    /**
     * The number of buttons to display.
     */
    private static final int NUM_BUTTONS = 8;
    
    /**
     * The list of buttons, from left to right.
     */
    private final List<JButton> buttons;
    
    /**
     * A panel that will hold the buttons for controlling the editor.
     */
    private final JPanel buttonPanel;
    
    /**
     * Constructor. Should be called only on the Event Dispatch Thread.
     * 
     * @param aEditorController the controller that will handle
     * user actions in this view
     */
    public EditorView(final EditorController aEditorController) {
        this.editorController = aEditorController;
        this.buttons = new ArrayList<JButton>();
        this.buttonPanel = new JPanel();
        this.setBorder(BorderFactory.createRaisedBevelBorder());
    }
    
    /**
     * Get the editor controller.
     * 
     * @return the editor controller
     */
    EditorController getEditorController() {
        return this.editorController;
    }
    
    /**
     * Sets up the view. Should be called only on the Event Dispatch Thread.
     */
    public void init() {
        this.editorController.setEditorView(this);
        
        this.setPreferredSize(new Dimension(MY_WIDTH, MY_HEIGHT));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel instructionsPanel = new JPanel();
        JLabel instructionsLabel = new JLabel("Smiley Face Path Editor");
        final int fontSize = 20;
        instructionsLabel.setFont(new Font("Sans Serif", Font.PLAIN, fontSize));
        instructionsPanel.add(instructionsLabel);
        JToggleButton mutualExclusionButton = new JToggleButton("Disable ME");
        mutualExclusionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                JToggleButton button = (JToggleButton) event.getSource();
                final boolean isSelected = button.isSelected();
                if (isSelected) {
                    button.setText("Enable ME");
                } else {
                    button.setText("Disable ME");
                }
                getEditorController().setMutualExclusionEnabled(!isSelected);
            } 
        });
        instructionsPanel.add(mutualExclusionButton);
        this.add(instructionsPanel);        
        setupButtonPanel();
        this.add(this.buttonPanel);
    }
    
    /**
     * Sets up the buttons for this view.
     * Should be called only on the Event Dispatch Thread.
     */
    private void setupButtonPanel() {
        this.buttonPanel.setLayout(
            new BoxLayout(this.buttonPanel, BoxLayout.X_AXIS)
        );
        for (int i = 0; i < NUM_BUTTONS; i++) {
            this.buttons.add(new JButton());
            this.buttonPanel.add(new JLabel("" + (i + 1) + "."));
            this.buttonPanel.add(this.buttons.get(i));
            this.buttonPanel.add(Box.createHorizontalGlue());
            this.buttons.get(i).addActionListener(this);
        }
    }
    
    
    /**
     * Get the list of JButtons.
     * 
     * @return the list of JButtons
     */
    List<JButton> getButtons() {
        return this.buttons;
    }
    
    /**
     * Sets the text of each button to the text String
     * associated with the appropriate state from the input
     * list.
     * 
     * @param labels the labels on buttons from left to right
     */
    public void setButtonLabels(final List<String> labels) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < labels.size(); i++) {
                    getButtons().get(i).setText(labels.get(i));
                }
            }
        });
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        JButton button = (JButton) event.getSource();
        final int buttonIndex = this.buttons.indexOf(button);
        final String buttonText = button.getText();
        
        this.editorController.buttonClicked(
            buttonText,
            buttonIndex
        );
    }
}
