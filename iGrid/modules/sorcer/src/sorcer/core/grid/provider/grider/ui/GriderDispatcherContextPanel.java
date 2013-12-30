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

public class GriderDispatcherContextPanel extends javax.swing.JPanel {
	public GriderDispatcherContextPanel() {
		initComponents();
	}

	private void initComponents() {
		configTextArea = new javax.swing.JTextArea();
		configTextArea.setColumns(44);
		configTextArea.setRows(20);
		configTextArea.setEditable(false);
		configScrollPane = new javax.swing.JScrollPane(configTextArea);
		// configScrollPane.setViewportView(configTextArea);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						configScrollPane, 0,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				org.jdesktop.layout.GroupLayout.LEADING).add(
				layout.createSequentialGroup().addContainerGap().add(
						configScrollPane, 0,
						org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE).addContainerGap()));
	}// </editor-fold>

	/* GETS */
	public String getConfigTextArea() {
		return configTextArea.getText();
	}

	/* SETS */
	public void setConfigTextArea(String t) {
		configTextArea.setText(t);
	}

	// Variables declaration - do not modify
	private javax.swing.JScrollPane configScrollPane;

	private javax.swing.JTextArea configTextArea;
	// End of variables declaration
}