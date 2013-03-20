package drawing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;


public class TestDrive {
    
    private final static String TITLE = "Drawing test";
    private final static int FRAME_WIDTH = 500;
    private final static int FRAME_HEIGHT = 450;
    private final static int DRAWING_WIDTH = 400;
    private final static int DRAWING_HEIGHT = 400;
    
    public static void main( String[] args ) {        
        JFrame frame = new JFrame( TITLE );
        frame.setSize( FRAME_WIDTH, FRAME_HEIGHT );
        frame.setLayout( new BorderLayout() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        final DrawingPanel drawingPanel = new DrawingPanel( new Dimension( DRAWING_WIDTH, DRAWING_HEIGHT ) );
        
        frame.add( new DrawingToolbar( drawingPanel ), BorderLayout.WEST );
        frame.add( drawingPanel, BorderLayout.CENTER );
        drawingPanel.init();
        frame.pack();
        frame.setVisible( true );
    }
}