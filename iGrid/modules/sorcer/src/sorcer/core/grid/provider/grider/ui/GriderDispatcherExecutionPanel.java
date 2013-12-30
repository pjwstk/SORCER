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

public class GriderDispatcherExecutionPanel extends javax.swing.JPanel {
	public GriderDispatcherExecutionPanel() {
		initComponents();
		c = new GriderDispatcherChecker();
	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		OSTabPane = new javax.swing.JTabbedPane();
		WinPanel = new javax.swing.JPanel();
		WinExecTextField = new javax.swing.JTextField();
		WinBinPathTextField = new javax.swing.JTextField(32);
		WinBinFileTextField = new javax.swing.JTextField(32);
		WinLibPathTextField = new javax.swing.JTextField(32);
		WinLibFileTextField = new javax.swing.JTextField(32);
		WEFTLabel = new javax.swing.JLabel();
		WBFPLabel = new javax.swing.JLabel();
		WBFLabel = new javax.swing.JLabel();
		WLFPLabel = new javax.swing.JLabel();
		WLFLabel = new javax.swing.JLabel();
		WindowsCheckBox = new javax.swing.JCheckBox();
		SolPanel = new javax.swing.JPanel();
		SBFPLabel = new javax.swing.JLabel();
		SBFLabel = new javax.swing.JLabel();
		SolBinPathTextField = new javax.swing.JTextField(32);
		SolBinFileTextField = new javax.swing.JTextField(32);
		SolarisCheckBox = new javax.swing.JCheckBox();
		LinPanel = new javax.swing.JPanel();
		LBFPLabel = new javax.swing.JLabel();
		LBFLabel = new javax.swing.JLabel();
		LinBinPathTextField = new javax.swing.JTextField(32);
		LinBinFileTextField = new javax.swing.JTextField(32);
		LinuxCheckBox = new javax.swing.JCheckBox();
		ExecComTextField = new javax.swing.JTextField(50);
		ExecCommandLabel = new javax.swing.JLabel();
		BinaryCheckBox = new javax.swing.JCheckBox();
		ASCIICheckBox = new javax.swing.JCheckBox();
		SourceCheckBox = new javax.swing.JCheckBox();

		WinPanel.setEnabled(false);

		WinBinPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				WinBinPathTextFieldKeyReleased(evt);
			}
		});

		WinBinFileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				WinBinFileTextFieldKeyReleased(evt);
			}
		});

		WinLibPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				WinLibPathTextFieldKeyReleased(evt);
			}
		});

		WinLibFileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				WinLibFileTextFieldKeyReleased(evt);
			}
		});

		WEFTLabel.setText("Windows Executable File Type:");

		WBFPLabel.setText("Windows Binary Folder Path:");

		WBFLabel.setText("Windows Binary File:");

		WLFPLabel.setText("Windows Library Folder Path:");

		WLFLabel.setText("Windows Library File:");

		WindowsCheckBox.setSelected(true);
		WindowsCheckBox.setText("Windows Enabled");
		WindowsCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		WindowsCheckBox
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		WindowsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		WindowsCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				WindowsCheckBoxActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout WinPanelLayout = new org.jdesktop.layout.GroupLayout(
				WinPanel);
		WinPanel.setLayout(WinPanelLayout);
		WinPanelLayout
				.setHorizontalGroup(WinPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								WinPanelLayout
										.createSequentialGroup()
										.add(26, 26, 26)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(WBFPLabel).add(
																WBFLabel).add(
																WLFPLabel).add(
																WLFLabel).add(
																WEFTLabel))
										.add(24, 24, 24)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING,
																false)
														.add(
																WinBinFileTextField)
														.add(
																WinBinPathTextField)
														.add(
																WinExecTextField,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																300,
																Short.MAX_VALUE)
														.add(
																WinLibPathTextField)
														.add(
																WinLibFileTextField))
										.addContainerGap(35, Short.MAX_VALUE))
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								WinPanelLayout.createSequentialGroup()
										.addContainerGap(466, Short.MAX_VALUE)
										.add(WindowsCheckBox).addContainerGap()));
		WinPanelLayout
				.setVerticalGroup(WinPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								WinPanelLayout
										.createSequentialGroup()
										.add(25, 25, 25)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(WEFTLabel)
														.add(
																WinExecTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.add(9, 9, 9)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(WBFPLabel)
														.add(
																WinBinPathTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(WBFLabel)
														.add(
																WinBinFileTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(WLFPLabel)
														.add(
																WinLibPathTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												WinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(WLFLabel)
														.add(
																WinLibFileTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												114, Short.MAX_VALUE).add(
												WindowsCheckBox)
										.addContainerGap()));
		OSTabPane.addTab("Windows OS", WinPanel);

		// SBFPLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		SBFPLabel.setText("Solaris Binary Folder Path:");

		// SBFLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		SBFLabel.setText("Solaris Binary File:");

		// SolBinPathTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		SolBinPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				SolBinPathTextFieldKeyReleased(evt);
			}
		});

		// SolBinFileTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		SolBinFileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				SolBinFileTextFieldKeyReleased(evt);
			}
		});

		SolarisCheckBox.setSelected(true);
		SolarisCheckBox.setText("Solaris Enabled");
		// SolarisCheckBox.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		SolarisCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(
				0, 0, 0, 0));
		SolarisCheckBox
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		SolarisCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		SolarisCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SolarisCheckBoxActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout SolPanelLayout = new org.jdesktop.layout.GroupLayout(
				SolPanel);
		SolPanel.setLayout(SolPanelLayout);
		SolPanelLayout
				.setHorizontalGroup(SolPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								SolPanelLayout
										.createSequentialGroup()
										.add(27, 27, 27)
										.add(
												SolPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(SBFPLabel).add(
																SBFLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												SolPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING,
																false)
														.add(
																SolBinPathTextField)
														.add(
																SolBinFileTextField,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																316,
																Short.MAX_VALUE))
										.addContainerGap(83, Short.MAX_VALUE))
						.add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								SolPanelLayout.createSequentialGroup()
										.addContainerGap(478, Short.MAX_VALUE)
										.add(SolarisCheckBox).addContainerGap()));
		SolPanelLayout
				.setVerticalGroup(SolPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								SolPanelLayout
										.createSequentialGroup()
										.add(27, 27, 27)
										.add(
												SolPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(SBFPLabel)
														.add(
																SolBinPathTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												SolPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(SBFLabel)
														.add(
																SolBinFileTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												199, Short.MAX_VALUE).add(
												SolarisCheckBox)
										.addContainerGap()));
		OSTabPane.addTab("Solaris", SolPanel);

		// LBFPLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		LBFPLabel.setText("Linux Binary Folder Path:");

		// LBFLabel.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		LBFLabel.setText("Linux Binary File:");

		// LinBinPathTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		LinBinPathTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				LinBinPathTextFieldKeyReleased(evt);
			}
		});

		// LinBinFileTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		LinBinFileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				LinBinFileTextFieldKeyReleased(evt);
			}
		});

		LinuxCheckBox.setSelected(true);
		LinuxCheckBox.setText("Linux Enabled");
		// LinuxCheckBox.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		LinuxCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		LinuxCheckBox
				.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
		LinuxCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		LinuxCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				LinuxCheckBoxActionPerformed(evt);
			}
		});

		org.jdesktop.layout.GroupLayout LinPanelLayout = new org.jdesktop.layout.GroupLayout(
				LinPanel);
		LinPanel.setLayout(LinPanelLayout);
		LinPanelLayout
				.setHorizontalGroup(LinPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								LinPanelLayout
										.createSequentialGroup()
										.add(26, 26, 26)
										.add(
												LinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(LBFLabel).add(
																LBFPLabel))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												LinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.TRAILING,
																false)
														.add(
																LinBinPathTextField)
														.add(
																LinBinFileTextField,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																323,
																Short.MAX_VALUE))
										.add(85, 85, 85)).add(
								org.jdesktop.layout.GroupLayout.TRAILING,
								LinPanelLayout.createSequentialGroup()
										.addContainerGap(484, Short.MAX_VALUE)
										.add(LinuxCheckBox).addContainerGap()));
		LinPanelLayout
				.setVerticalGroup(LinPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								LinPanelLayout
										.createSequentialGroup()
										.add(27, 27, 27)
										.add(
												LinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(LBFPLabel)
														.add(
																LinBinPathTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												LinPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(LBFLabel)
														.add(
																LinBinFileTextField,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED,
												199, Short.MAX_VALUE).add(
												LinuxCheckBox)
										.addContainerGap()));
		OSTabPane.addTab("Linux", LinPanel);

		// ExecComTextField.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		ExecComTextField.setToolTipText("Enter execution command here...");

		// ExecCommandLabel.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		ExecCommandLabel.setText("Execution Command:");

		BinaryCheckBox.setText("Binary File");
		// BinaryCheckBox.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		BinaryCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		BinaryCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		BinaryCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				BinaryCheckBoxMouseClicked(evt);
			}
		});

		ASCIICheckBox.setText("ASCII File");
		// ASCIICheckBox.setFont(new java.awt.Font("Palatino Linotype", 0, 12));
		ASCIICheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		ASCIICheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		ASCIICheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ASCIICheckBoxMouseClicked(evt);
			}
		});

		SourceCheckBox.setText("Source File");
		// SourceCheckBox.setFont(new java.awt.Font("Palatino Linotype", 0,
		// 12));
		SourceCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
				0, 0, 0));
		SourceCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
		SourceCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				SourceCheckBoxMouseClicked(evt);
			}
		});

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
																				ExecCommandLabel)
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING,
																								false)
																						.add(
																								layout
																										.createSequentialGroup()
																										.add(
																												ASCIICheckBox)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												BinaryCheckBox)
																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												SourceCheckBox))
																						.add(
																								ExecComTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
														.add(
																OSTabPane,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																582,
																org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								layout
										.createSequentialGroup()
										.add(
												OSTabPane,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												330, Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createParallelGroup(
																				org.jdesktop.layout.GroupLayout.BASELINE)
																		.add(
																				ASCIICheckBox)
																		.add(
																				BinaryCheckBox)
																		.add(
																				SourceCheckBox))
														.add(
																layout
																		.createSequentialGroup()
																		.add(
																				21,
																				21,
																				21)
																		.add(
																				layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(
																								ExecComTextField,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																						.add(
																								ExecCommandLabel))))
										.addContainerGap()));
	}// </editor-fold>

	private void LinuxCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		setLinTab(LinuxCheckBox.isSelected());
	}

	private void SolarisCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		setSolTab(SolarisCheckBox.isSelected());
	}

	private void WindowsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
		setWinTab(WindowsCheckBox.isSelected());
	}

	private void LinBinFileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		LinbinFle = c.check_against(Not_name, LinBinFileTextField);
		if (LinBinFileTextField.getText().length() == 0) {
			LinbinFle = false;
		}
	}

	private void LinBinPathTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		LinbinPth = c.check_against(unix_path, LinBinPathTextField);
		if (LinBinPathTextField.getText().length() == 0) {
			LinbinPth = false;
		}
	}

	private void SolBinFileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		SolbinFle = c.check_against(Not_name, SolBinFileTextField);
		if (SolBinFileTextField.getText().length() == 0) {
			SolbinFle = false;
		}
	}

	private void SolBinPathTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		SolbinPth = c.check_against(unix_path, SolBinPathTextField);
		if (SolBinPathTextField.getText().length() == 0) {
			SolbinPth = false;
		}
	}

	private void WinLibFileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		WinlibFle = c.check_against(Not_name, WinLibFileTextField);
		if (WinLibFileTextField.getText().length() == 0) {
			WinlibFle = false;
		}
	}

	private void WinLibPathTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		WinlibPth = c.check_against(win_path, WinLibPathTextField);
		if (WinLibPathTextField.getText().length() == 0) {
			WinlibPth = false;
		}
	}

	private void WinBinFileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		WinbinFle = c.check_against(Not_name, WinBinFileTextField);
		if (WinBinFileTextField.getText().length() == 0) {
			WinbinFle = false;
		}
	}

	private void WinBinPathTextFieldKeyReleased(java.awt.event.KeyEvent evt) {
		WinbinPth = c.check_against(win_path, WinBinPathTextField);
		if (WinBinPathTextField.getText().length() == 0) {
			WinbinPth = false;
		}
	}

	private void SourceCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {
		if (SourceCheckBox.isSelected()) {
			BinaryCheckBox.setSelected(false);
			ASCIICheckBox.setSelected(false);
		}
	}

	private void BinaryCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {
		if (BinaryCheckBox.isSelected()) {
			SourceCheckBox.setSelected(false);
			ASCIICheckBox.setSelected(false);
		}
	}

	private void ASCIICheckBoxMouseClicked(java.awt.event.MouseEvent evt) {
		if (ASCIICheckBox.isSelected()) {
			SourceCheckBox.setSelected(false);
			BinaryCheckBox.setSelected(false);
		}
	}

	/* GETS */
	public String getWinExecFileType() {
		return WinExecTextField.getText();
	}

	public String getWinBinPath() {
		return WinBinPathTextField.getText();
	}

	public String getWinBinFile() {
		return WinBinFileTextField.getText();
	}

	public String getWinLibPath() {
		return WinLibPathTextField.getText();
	}

	public String getWinLibFile() {
		return WinLibFileTextField.getText();
	}

	public String getLinBinPath() {
		return LinBinPathTextField.getText();
	}

	public String getLinBinFile() {
		return LinBinFileTextField.getText();
	}

	public String getSolBinPath() {
		return SolBinPathTextField.getText();
	}

	public String getSolBinFile() {
		return SolBinFileTextField.getText();
	}

	public boolean getASCII() {
		return ASCIICheckBox.isSelected();
	}

	public boolean getBinary() {
		return BinaryCheckBox.isSelected();
	}

	public boolean getSource() {
		return SourceCheckBox.isSelected();
	}

	public String getExecCommand() {
		return ExecComTextField.getText();
	}

	public boolean getWindows() {
		return WindowsCheckBox.isSelected();
	}

	public boolean getLinux() {
		return LinuxCheckBox.isSelected();
	}

	public boolean getSolaris() {
		return SolarisCheckBox.isSelected();
	}

	/* SETS */
	public void setWinExecFileType(String t) {
		WinExecTextField.setText(t);
	}

	public void setWinBinPath(String t) {
		WinBinPathTextField.setText(t);
	}

	public void setWinBinFile(String t) {
		WinBinFileTextField.setText(t);
	}

	public void setWinLibPath(String t) {
		WinLibPathTextField.setText(t);
	}

	public void setWinLibFile(String t) {
		WinLibFileTextField.setText(t);
	}

	public void setLinBinPath(String t) {
		LinBinPathTextField.setText(t);
	}

	public void setLinBinFile(String t) {
		LinBinFileTextField.setText(t);
	}

	public void setSolBinPath(String t) {
		SolBinPathTextField.setText(t);
	}

	public void setSolBinFile(String t) {
		SolBinFileTextField.setText(t);
	}

	public void setASCII(boolean b) {
		ASCIICheckBox.setSelected(b);
	}

	public void setBinary(boolean b) {
		BinaryCheckBox.setSelected(b);
	}

	public void setSource(boolean b) {
		SourceCheckBox.setSelected(b);
	}

	public void setExecCommand(String t) {
		ExecComTextField.setText(t);
	}

	public void setWindows(boolean b) {
		WindowsCheckBox.setSelected(b);
	}

	public void setLinux(boolean b) {
		LinuxCheckBox.setSelected(b);
	}

	public void setSolaris(boolean b) {
		SolarisCheckBox.setSelected(b);
	}

	public void setWinTab(boolean b) {
		WBFLabel.setEnabled(b);
		WBFPLabel.setEnabled(b);
		WEFTLabel.setEnabled(b);
		WLFLabel.setEnabled(b);
		WLFPLabel.setEnabled(b);
		WinBinFileTextField.setEnabled(b);
		WinBinPathTextField.setEnabled(b);
		WinExecTextField.setEnabled(b);
		WinLibFileTextField.setEnabled(b);
		WinLibPathTextField.setEnabled(b);
	}

	public void setSolTab(boolean b) {
		SBFLabel.setEnabled(b);
		SBFPLabel.setEnabled(b);
		SolBinFileTextField.setEnabled(b);
		SolBinPathTextField.setEnabled(b);
	}

	public void setLinTab(boolean b) {
		LBFLabel.setEnabled(b);
		LBFPLabel.setEnabled(b);
		LinBinFileTextField.setEnabled(b);
		LinBinPathTextField.setEnabled(b);
	}

	/* OTHERS */
	public void clear() {
		setWindows(true);
		setWinTab(true);
		setWinExecFileType("");
		setWinBinPath("");
		setWinBinFile("");
		setWinLibPath("");
		setWinLibFile("");
		setLinux(true);
		setLinTab(true);
		setLinBinPath("");
		setLinBinFile("");
		setSolaris(true);
		setSolTab(true);
		setSolBinPath("");
		setSolBinFile("");
		setASCII(false);
		setBinary(false);
		setSource(false);
		setExecCommand("");
	}

	public void checkAll() {
		LinbinFle = c.check_against(Not_name, LinBinFileTextField);
		LinbinPth = c.check_against(unix_path, LinBinPathTextField);
		SolbinFle = c.check_against(Not_name, SolBinFileTextField);
		SolbinPth = c.check_against(unix_path, SolBinPathTextField);
		WinlibFle = c.check_against(Not_name, WinLibFileTextField);
		WinlibPth = c.check_against(win_path, WinLibPathTextField);
		WinbinFle = c.check_against(Not_name, WinBinFileTextField);
		WinbinPth = c.check_against(win_path, WinBinPathTextField);

		if (LinBinFileTextField.getText().length() == 0) {
			LinbinFle = false;
		}
		if (LinBinPathTextField.getText().length() == 0) {
			LinbinPth = false;
		}
		if (SolBinFileTextField.getText().length() == 0) {
			SolbinFle = false;
		}
		if (SolBinPathTextField.getText().length() == 0) {
			SolbinPth = false;
		}
		if (WinLibFileTextField.getText().length() == 0) {
			WinlibFle = false;
		}
		if (WinLibPathTextField.getText().length() == 0) {
			WinlibPth = false;
		}
		if (WinBinFileTextField.getText().length() == 0) {
			WinbinFle = false;
		}
		if (WinBinPathTextField.getText().length() == 0) {
			WinbinPth = false;
		}
	}

	public String getVString() {
		StringBuilder temp = new StringBuilder();
		if (WindowsCheckBox.isSelected()) {
			if (!WinbinPth) {
				temp.append(" - Windows Binary Path\n");
			}
			if (!WinbinFle) {
				temp.append(" - Windows Binary File\n");
			}
			if (!WinlibPth) {
				temp.append(" - Windows Library Path\n");
			}
			if (!WinlibFle) {
				temp.append(" - Windows Library File\n");
			}
		}
		if (SolarisCheckBox.isSelected()) {
			if (!SolbinPth) {
				temp.append(" - Solaris Binary Path\n");
			}
			if (!SolbinFle) {
				temp.append(" - Solaris Binary File\n");
			}
		}
		if (LinuxCheckBox.isSelected()) {
			if (!LinbinPth) {
				temp.append(" - Linux Binary Path\n");
			}
			if (!LinbinFle) {
				temp.append(" - Linux Binary File\n");
			}
		}
		return temp.toString();
	}

	/* VARIABLES */
	GriderDispatcherChecker c;

	private boolean WinbinPth = false;
	private boolean WinbinFle = false;
	private boolean WinlibPth = false;
	private boolean WinlibFle = false;
	private boolean SolbinPth = false;
	private boolean SolbinFle = false;
	private boolean LinbinPth = false;
	private boolean LinbinFle = false;

	private String Not_name = "`~!#$%^&*()+=-, \";:<>{}[]/\\|";
	private String win_path = "`~!#$%^&*()+=-,\";:<>{}[]|";
	private String unix_path = "`~!#$%^&*()+=-, \";:<>{}[]|";

	// Variables declaration - do not modify
	private javax.swing.JCheckBox ASCIICheckBox;
	private javax.swing.JCheckBox BinaryCheckBox;
	private javax.swing.JTextField ExecComTextField;
	private javax.swing.JLabel ExecCommandLabel;
	private javax.swing.JLabel LBFLabel;
	private javax.swing.JLabel LBFPLabel;
	private javax.swing.JTextField LinBinFileTextField;
	private javax.swing.JTextField LinBinPathTextField;
	private javax.swing.JPanel LinPanel;
	private javax.swing.JCheckBox LinuxCheckBox;
	private javax.swing.JTabbedPane OSTabPane;
	private javax.swing.JLabel SBFLabel;
	private javax.swing.JLabel SBFPLabel;
	private javax.swing.JTextField SolBinFileTextField;
	private javax.swing.JTextField SolBinPathTextField;
	private javax.swing.JPanel SolPanel;
	private javax.swing.JCheckBox SolarisCheckBox;
	private javax.swing.JCheckBox SourceCheckBox;
	private javax.swing.JLabel WBFLabel;
	private javax.swing.JLabel WBFPLabel;
	private javax.swing.JLabel WEFTLabel;
	private javax.swing.JLabel WLFLabel;
	private javax.swing.JLabel WLFPLabel;
	private javax.swing.JTextField WinBinFileTextField;
	private javax.swing.JTextField WinBinPathTextField;
	private javax.swing.JTextField WinExecTextField;
	private javax.swing.JTextField WinLibFileTextField;
	private javax.swing.JTextField WinLibPathTextField;
	private javax.swing.JPanel WinPanel;
	private javax.swing.JCheckBox WindowsCheckBox;
	// End of variables declaration
}