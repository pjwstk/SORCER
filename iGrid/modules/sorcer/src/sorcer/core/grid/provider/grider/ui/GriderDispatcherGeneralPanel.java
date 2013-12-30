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

public class GriderDispatcherGeneralPanel extends javax.swing.JPanel {
	public GriderDispatcherGeneralPanel() {
		initComponents();
		c = new GriderDispatcherChecker();
	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		ProjNameLabel = new javax.swing.JLabel();
		ProgNameTextField = new javax.swing.JTextField(32);
		JobSizeLabel = new javax.swing.JLabel();
		JobSizeTextField = new javax.swing.JTextField(32);
		NotifyLabel = new javax.swing.JLabel();
		DefaultSGridCheckBox = new javax.swing.JCheckBox();
		jSeparator1 = new javax.swing.JSeparator();
		HostLabel = new javax.swing.JLabel();
		NodeLabel = new javax.swing.JLabel();
		LocationLabel = new javax.swing.JLabel();
		LocationComboBox = new javax.swing.JComboBox();
		HostComboBox = new javax.swing.JComboBox();
		NodeComboBox = new javax.swing.JComboBox();
		NotifyTextField = new javax.swing.JTextField(32);
		ModeLabel = new javax.swing.JLabel();
		ModeComboBox = new javax.swing.JComboBox();

		ProjNameLabel.setText("Program Name:");
		ProgNameTextField.setToolTipText("Enter program name here...");
		ProgNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				ProgNameTextFieldKeyReleased(evt);
			}
		});

		JobSizeLabel.setText("Job Size:");
		JobSizeTextField.setToolTipText("Enter job size here...");
		JobSizeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				JobSizeTextFieldKeyReleased(evt);
			}
		});

		NotifyLabel.setText("Notify:");

		DefaultSGridCheckBox.setSelected(true);
		DefaultSGridCheckBox.setText("Use Dynamic SGrid Settings");
		DefaultSGridCheckBox
				.setToolTipText("Select to use dynamic SGrid settings...");
		DefaultSGridCheckBox.setBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 0, 0, 0));
		DefaultSGridCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		DefaultSGridCheckBox
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						DefaultSGridCheckBoxActionPerformed(evt);
					}
				});

		// HostLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		HostLabel.setText("Host:");
		HostLabel.setEnabled(false);

		// NodeLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		NodeLabel.setText("Node Name:");
		NodeLabel.setEnabled(false);

		// LocationLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		LocationLabel.setText("Location:");
		LocationLabel.setEnabled(false);

		// LocationComboBox.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		LocationComboBox.setToolTipText("Select location here...");
		LocationComboBox.setEnabled(false);
		LocationComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				LocationComboBoxKeyReleased(evt);
			}
		});

		// HostComboBox.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		HostComboBox.setToolTipText("Select host here...");
		HostComboBox.setEnabled(false);
		HostComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				HostComboBoxKeyReleased(evt);
			}
		});

		// NodeComboBox.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		NodeComboBox.setToolTipText("Select node name here...");
		NodeComboBox.setEnabled(false);
		NodeComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				NodeComboBoxKeyReleased(evt);
			}
		});

		// NotifyTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		NotifyTextField.setToolTipText("Enter notify email address here...");
		NotifyTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				NotifyTextFieldKeyReleased(evt);
			}
		});

		// ModeLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		ModeLabel.setText("Mode:");
		ModeLabel.setEnabled(false);

		// ModeComboBox.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		ModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Viewer", "Silenus", "Document Manager" }));
		ModeComboBox.setEnabled(false);

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
										.add(
												layout
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
																								jSeparator1,
																								0,
																								600,
																								Short.MAX_VALUE)
																						.add(
																								DefaultSGridCheckBox)
																						.add(
																								layout
																										.createSequentialGroup()
																										.add(
																												layout
																														.createParallelGroup(
																																org.jdesktop.layout.GroupLayout.LEADING)
																														.add(
																																ProjNameLabel)
																														.add(
																																JobSizeLabel)
																														.add(
																																NotifyLabel))
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												layout
																														.createParallelGroup(
																																org.jdesktop.layout.GroupLayout.LEADING,
																																false)
																														.add(
																																org.jdesktop.layout.GroupLayout.TRAILING,
																																JobSizeTextField)
																														.add(
																																org.jdesktop.layout.GroupLayout.TRAILING,
																																ProgNameTextField,
																																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																														.add(
																																NotifyTextField)))))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				45,
																				45,
																				45)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								LocationLabel)
																						.add(
																								HostLabel)
																						.add(
																								NodeLabel)
																						.add(
																								ModeLabel))
																		.add(
																				20,
																				20,
																				20)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								ModeComboBox,
																								0,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								LocationComboBox,
																								0,
																								200,
																								Short.MAX_VALUE)
																						.add(
																								HostComboBox,
																								0,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)
																						.add(
																								NodeComboBox,
																								0,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.addContainerGap()
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								ProjNameLabel)
																						.add(
																								ProgNameTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								JobSizeLabel)
																						.add(
																								JobSizeTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.add(
																				42,
																				42,
																				42)
																		.add(
																				jSeparator1,
																				0,
																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				DefaultSGridCheckBox))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				67,
																				67,
																				67)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								NotifyLabel)
																						.add(
																								NotifyTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
										.add(27, 27, 27)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																LocationComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(LocationLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(HostLabel)
														.add(
																HostComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(
																NodeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
														.add(NodeLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(ModeLabel)
														.add(
																ModeComboBox,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
	}// </editor-fold>

	private void ProgNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		ProgName = c.check_against(Prog_name, ProgNameTextField);
		if (ProgNameTextField.getText().length() == 0) {
			ProgName = false;
		}
	}

	private void JobSizeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		JobSize = c.check_for(J_size, JobSizeTextField);
		if (JobSizeTextField.getText().length() == 0) {
			JobSize = false;
		}
	}

	private void NotifyTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		Notify = c.check_email(Not_name, NotifyTextField);
		if (NotifyTextField.getText().length() == 0) {
			Notify = false;
		}
	}

	private void DefaultSGridCheckBoxActionPerformed(
			java.awt.event.ActionEvent evt) {
		setDefaultFields(!DefaultSGridCheckBox.isSelected());
	}

	private void LocationComboBoxKeyReleased(java.awt.event.KeyEvent evt) {/* Location */
	}

	private void HostComboBoxKeyReleased(java.awt.event.KeyEvent evt) {/* Host */
	}

	private void NodeComboBoxKeyReleased(java.awt.event.KeyEvent evt) {/*
																		 * Node
																		 * Name
																		 */
	}

	/* GETS */
	public String getProgramName() {
		return ProgNameTextField.getText();
	}

	public String getJobSize() {
		return JobSizeTextField.getText();
	}

	public String getNotify() {
		return NotifyTextField.getText();
	}

	public String getLocationValue() {
		return "";
	}

	public String getHost() {
		return "";
	}

	public String getNodeName() {
		return "";
	}

	public int getMode() {
		return ModeComboBox.getSelectedIndex();
	}

	/* SETS */
	public void setProgramName(String t) {
		ProgNameTextField.setText(t);
	}

	public void setJobSize(String t) {
		JobSizeTextField.setText(t);
	}

	public void setNotify(String t) {
		NotifyTextField.setText(t);
	}

	public void setLocationValue(int i) {
	}

	public void setHost(int i) {
	}

	public void setNodeName(int i) {
	}

	public void setMode(int i) {
		ModeComboBox.setSelectedIndex(i);
	}

	public void setDefaultFields(boolean b) {
		DefaultSGridCheckBox.setSelected(!b);
		LocationLabel.setEnabled(b);
		HostLabel.setEnabled(b);
		NodeLabel.setEnabled(b);
		ModeLabel.setEnabled(b);
		LocationComboBox.setEnabled(b);
		HostComboBox.setEnabled(b);
		NodeComboBox.setEnabled(b);
		ModeComboBox.setEnabled(b);
	}

	/* OTHERS */
	public void clear() {
		setProgramName("");
		setJobSize("");
		setNotify("");
		setLocationValue(0);
		setHost(0);
		setNodeName(0);
		setDefaultFields(false);
	}

	public void checkAll() {
		ProgName = c.check_against(Prog_name, ProgNameTextField);
		JobSize = c.check_for(J_size, JobSizeTextField);
		Notify = c.check_email(Not_name, NotifyTextField);

		if (ProgNameTextField.getText().length() == 0) {
			ProgName = false;
		}
		if (JobSizeTextField.getText().length() == 0) {
			JobSize = false;
		}
		if (NotifyTextField.getText().length() == 0) {
			Notify = false;
		}
	}

	public String getVString() {
		StringBuilder temp = new StringBuilder();
		if (!ProgName) {
			temp.append(" - Program Name\n");
		}
		if (!JobSize) {
			temp.append(" - Job Size\n");
		}
		if (!Notify) {
			temp.append(" - Notify\n");
		}
		return temp.toString();
	}

	/* VARIABLES */
	private GriderDispatcherChecker c;

	private boolean ProgName = false;
	private boolean JobSize = false;
	private boolean Notify = false;

	private String Prog_name = "`~!@#$%^&*()+=-,. ;:<>{}[]/\\'";
	private String J_size = "1234567890";
	private String Not_name = "`~!#$%^&*()+=-, \";:<>{}[]/\\|";

	// Variables declaration - do not modify
	private javax.swing.JCheckBox DefaultSGridCheckBox;
	private javax.swing.JComboBox HostComboBox;
	private javax.swing.JLabel HostLabel;
	private javax.swing.JLabel JobSizeLabel;
	private javax.swing.JTextField JobSizeTextField;
	private javax.swing.JComboBox LocationComboBox;
	private javax.swing.JLabel LocationLabel;
	private javax.swing.JComboBox ModeComboBox;
	private javax.swing.JLabel ModeLabel;
	private javax.swing.JComboBox NodeComboBox;
	private javax.swing.JLabel NodeLabel;
	private javax.swing.JLabel NotifyLabel;
	private javax.swing.JTextField NotifyTextField;
	private javax.swing.JTextField ProgNameTextField;
	private javax.swing.JLabel ProjNameLabel;
	private javax.swing.JSeparator jSeparator1;
	// End of variables declaration
}