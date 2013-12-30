package sorcer.arithmetic.ui;

import javax.swing.JFrame;

import net.jini.core.lookup.ServiceItem;
import sorcer.core.provider.Provider;

/**
 * SORCER provider info UI
 */
public class ArithmeticFrameUI extends JFrame {

	private static final int FRAME_WIDTH = 500;

	private static final int FRAME_HEIGHT = 450;
	
    /** Creates new CatalogerUI */
	public ArithmeticFrameUI(final Object obj) {
		super();
		// Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createFrame(obj);
            }
        });
		
	}

    /**
	 * Create the GUI frame and show it.
	 */
    private void createFrame(Object serviceItem) {
        // Create and set up the window.
        setTitle("Arithmetic Service UI Tester");
        // closing is managed by a service browser
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        ArithmeticUI panel = new ArithmeticUI(serviceItem);
        panel.setOpaque(true); // content panes must be opaque
        setContentPane(panel);
        
        // Display the window.
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        //center the frame on screen
        setLocationRelativeTo(null);
        //pack();
        setVisible(true);
    }
}
