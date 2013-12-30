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

package sorcer.core.grid.provider.grider.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.JFileChooser;

public class GriderDispatcherIncludePanel extends javax.swing.JPanel {

	/* VARIABLES */
	private BufferedReader ifs;

	private JFileChooser jFC = new JFileChooser();

	// Variables declaration - do not modify
	private javax.swing.JLabel fileNameLabel;

	private javax.swing.JScrollPane includeScrollPane;

	private javax.swing.JTextArea includeTextArea;

	private javax.swing.JButton loadIncludeButton;

	private javax.swing.JLabel dFileLabel;

	// End of variables declaration

	public GriderDispatcherIncludePanel() {
		initComponents();
	}

	private void initComponents() {
		includeTextArea = new javax.swing.JTextArea();
		loadIncludeButton = new javax.swing.JButton();
		fileNameLabel = new javax.swing.JLabel();
		dFileLabel = new javax.swing.JLabel();

		includeTextArea.setColumns(40);
		includeTextArea.setRows(20);
		includeScrollPane = new javax.swing.JScrollPane(includeTextArea);
		// includeScrollPane.setViewportView(includeTextArea);

		loadIncludeButton.setText("Load");
		loadIncludeButton.setToolTipText("Click here to load file...");
		loadIncludeButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						loadIncludeButtonActionPerformed(evt);
					}
				});

		fileNameLabel.setText("File Name:");

		// dFileLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		dFileLabel.setText("<none>");
		dFileLabel.setToolTipText("Included file's name...");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																includeScrollPane,
																0,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				loadIncludeButton)
																		.add(
																				60,
																				60,
																				60)
																		.add(
																				fileNameLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				dFileLabel)))
										.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				org.jdesktop.layout.GroupLayout.TRAILING,
				layout.createSequentialGroup().addContainerGap().add(
						includeScrollPane, 0,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addPreferredGap(
						org.jdesktop.layout.LayoutStyle.RELATED).add(
						layout.createParallelGroup(
								org.jdesktop.layout.GroupLayout.BASELINE).add(
								loadIncludeButton).add(fileNameLabel).add(
								dFileLabel))));
	}

	private void loadIncludeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		jFC.showOpenDialog(this);
		Vector v = new Vector();
		String fileName = null;

		try {
			fileName = jFC.getSelectedFile().getAbsolutePath();
			dFileLabel.setText(jFC.getSelectedFile().getName());

			ifs = new BufferedReader(new FileReader(fileName));
			String args;
			while ((args = ifs.readLine()) != null) {
				v.add(args);
			}
		} catch (Exception e) {
		}

		for (int i = 0; i < v.size(); i++) {
			includeTextArea.append(v.elementAt(i).toString());
			includeTextArea.append("\n");
		}
	}

	/* GETS */
	public String getInclude() {
		return includeTextArea.getText();
	}

	/* SETS */
	public void setInclude(String t) {
		includeTextArea.setText(t);
	}

	/* OTHERS */
	public void clear() {
		setInclude("");
	}
}