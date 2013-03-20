package drawing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class DrawingToolbar extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = -8272354443867339365L;
	private DrawingPanel drawingPanel;
	
	
    private static final String CLEAR_ALL_STRING = "Clear all";
	private static final String PENCIL_STRING = "Pencil";
	private static final String RECTANGLE_STRING = "Rectangle";
	
    private static final String BLACK_STRING = "Black";
    private static final String RED_STRING = "Red";
    private static final String GREEN_STRING = "Green";
    private static final String BLUE_STRING = "Blue";
    
    private static final String UNDO_BUTTON_STRING = "Undo";
    private static final String REDO_BUTTON_STRING = "Redo";
    
    private final JButton undoButton;
    private final JButton redoButton;
    
	
	public DrawingToolbar( final DrawingPanel drawingPanel ) {
	    super();
	    
	    this.setLayout( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
	    
        this.drawingPanel = drawingPanel;
        this.drawingPanel.setDrawingMode( DrawingMode.PENCIL );
        this.drawingPanel.setColor( Color.BLACK );

        {
            JButton clearAllButton = new JButton( CLEAR_ALL_STRING );
            this.add( clearAllButton );
            clearAllButton.addActionListener( this );
        }
	    
        {
            ButtonGroup drawingModeGroup = new ButtonGroup();

        	JRadioButton pencilButton = getRadioButton( PENCIL_STRING, drawingModeGroup );
        	pencilButton.setSelected( true );
        	
        	getRadioButton( RECTANGLE_STRING, drawingModeGroup );
        }	    

        {
            ButtonGroup colorGroup = new ButtonGroup();
            JRadioButton black = getRadioButton( BLACK_STRING, colorGroup );
            black.setSelected( true );
            
            getRadioButton( RED_STRING, colorGroup );
            getRadioButton( GREEN_STRING, colorGroup );
            getRadioButton( BLUE_STRING, colorGroup );
        }
        
        {
            this.undoButton = new JButton( UNDO_BUTTON_STRING );
            this.add( this.undoButton );
            this.undoButton.addActionListener( this );
            
            this.redoButton = new JButton( REDO_BUTTON_STRING );
            this.add( this.redoButton );
            this.redoButton.addActionListener( this );
        }
	}
	
	private JRadioButton getRadioButton( final String name, final ButtonGroup group ) {
	    JRadioButton result = new JRadioButton( name );
	    result.setActionCommand( name );
	    result.addActionListener( this );
	    this.add( result );
	    group.add( result );
	    return result;
	}
	

	@Override
	public void actionPerformed( final ActionEvent e ) {
		if ( e.getActionCommand().equals( PENCIL_STRING ) ) {
		    this.drawingPanel.setDrawingMode( DrawingMode.PENCIL );
		}
		else if ( e.getActionCommand().equals( RECTANGLE_STRING ) ) {
		    this.drawingPanel.setDrawingMode( DrawingMode.RECTANGLE );
		}
		else if ( e.getActionCommand().equals( CLEAR_ALL_STRING ) ) {
		    this.drawingPanel.clearAll( true ); // this is a new clearAll() action
		}
		else if ( e.getActionCommand().equals( BLACK_STRING ) ) {
            this.drawingPanel.setColor( Color.BLACK );
        }
        else if ( e.getActionCommand().equals( RED_STRING ) ) {
            this.drawingPanel.setColor( Color.RED );
        }
        else if ( e.getActionCommand().equals( GREEN_STRING ) ) {
            this.drawingPanel.setColor( Color.GREEN );
        }
        else if ( e.getActionCommand().equals( BLUE_STRING ) ) {
            this.drawingPanel.setColor( Color.BLUE );
        }
        else if ( e.getActionCommand().equals( UNDO_BUTTON_STRING ) ) {
            this.drawingPanel.undo();
        }
        else if ( e.getActionCommand().equals( REDO_BUTTON_STRING ) ) {
            this.drawingPanel.redo();
        }
	}
}
