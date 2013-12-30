package sorcer.arithmetic.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import net.jini.core.lookup.ServiceItem;
import sorcer.core.provider.Provider;

/**
 * SORCER provider info UI
 */
public class ArithmeticFrame extends JFrame {

	private static final int FRAME_WIDTH = 500;

	private static final int FRAME_HEIGHT = 450;

	/** Creates an Arithmetic Tester frame */
	public ArithmeticFrame(Object obj) {
		super();
		getAccessibleContext().setAccessibleName("Arithmetic Tester");
		createFrame(obj);
	}

	/**
	 * Create the GUI frame and do mot make it visible and do not allow to close
	 * it.
	 */
	private void createFrame(Object serviceItem) {
		// Create and set up the window.
		setTitle("Arithmetic Service UI Tester");

		// Create and set up the content pane.
		ArithmeticUI panel = new ArithmeticUI(serviceItem);
		panel.setOpaque(true); // content panes must be opaque
		setContentPane(panel);
		addWindowListener(new WindowAdapter() {
			public void windoeClosing(WindowEvent e) {
				dispose();
			}
		});
		//center the frame on screen
        setLocationRelativeTo(null);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
	}
}
