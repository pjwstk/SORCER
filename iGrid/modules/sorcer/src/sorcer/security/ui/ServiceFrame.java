/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.security.ui;

import javax.swing.JFrame;

/**
 * A generic service UI frame attched to SORCER providers
 * 
 * @author Mike Sobolewski
 */
abstract public class ServiceFrame extends JFrame {

	private static final int FRAME_WIDTH = 500;

	private static final int FRAME_HEIGHT = 450;

	/** Creates a new service UI frame */
	public ServiceFrame(final Object obj) {
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
	 * Returns the content panel for this frame.
	 * 
	 * @return a content panel
	 */
	abstract protected SecureContentPane getContentPane(final Object obj);

	/**
	 * Create the GUI frame and show it.
	 */
	protected void createFrame(Object serviceItem) {
		// Create and set up the window.
		setTitle("Service UI Tester");
		// closing is managed by a service browser
		// do not setDefaultCloseOperation
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		SecureContentPane pane = getContentPane(serviceItem);
		pane.setOpaque(true); // content panes must be opaque
		setContentPane(pane);

		// Display the window.
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setVisible(true);
	}
}
