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

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.DefaultListModel;

public class GriderDispatcherArgumentsPanel extends javax.swing.JPanel {

	public GriderDispatcherArgumentsPanel() {
		initComponents();
		para = new GriderDispatcherParameter();
		mylist = new DefaultListModel();
		callList.setModel(mylist);
	}

	private void initComponents() {
		callList = new javax.swing.JList();
		outputTextField = new javax.swing.JTextField(24);
		//argAddButton = new javax.swing.JButton();
		argTextField = new javax.swing.JTextField(24);
		inputTextField = new javax.swing.JTextField(24);
		inputLabel = new javax.swing.JLabel();
		argumentLabel = new javax.swing.JLabel();
		outputLabel = new javax.swing.JLabel();
		jSeparator2 = new javax.swing.JSeparator();
		jSeparator3 = new javax.swing.JSeparator();
		dInputLabel = new javax.swing.JLabel();
		dArgLabel = new javax.swing.JLabel();
		dOutputLabel = new javax.swing.JLabel();
		inLabel = new javax.swing.JLabel();
		argLabel = new javax.swing.JLabel();
		outLabel = new javax.swing.JLabel();
		listBoxMoveUp = new javax.swing.JButton();
		listBoxMoveDown = new javax.swing.JButton();
		deleteButton = new javax.swing.JButton();

		callList.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				callListMouseClicked(evt);
			}
		});

		argScrollPane = new javax.swing.JScrollPane(callList);
		//argScrollPane.setViewportView(callList);
		outputTextField.setToolTipText("Enter output here...");
		outputTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
					addArg();
				}
			}
		});

//		argAddButton.setText("Add");
//		argAddButton.setToolTipText("Add parameters to context...");
//		argAddButton.addActionListener(new java.awt.event.ActionListener() {
//			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				addArg();
//			}
//		});

		argTextField.setToolTipText("Enter argument here...");
		argTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
					addArg();
				}
			}
		});

		inputTextField.setToolTipText("Enter input here...");
		inputTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
					addArg();
				}
			}
		});

		inputLabel.setText("Input:");
		argumentLabel.setText("Argument:");
		outputLabel.setText("Output:");
		jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

		dInputLabel.setText("Input:");
		dInputLabel.setEnabled(false);

		dArgLabel.setText("Argument:");
		dArgLabel.setEnabled(false);

		dOutputLabel.setText("Output:");
		dOutputLabel.setEnabled(false);

		inLabel.setText("<none>");
		inLabel.setEnabled(false);

		argLabel.setText("<none>");
		argLabel.setEnabled(false);

		outLabel.setText("<none>");
		outLabel.setEnabled(false);

		listBoxMoveUp.setText("Up");
		listBoxMoveUp.setToolTipText("Move selection up...");
		listBoxMoveUp.setActionCommand("up");
		listBoxMoveUp.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ListBoxMoveUpMouseClicked(evt);
			}
		});

		listBoxMoveDown.setText("Down");
		listBoxMoveDown.setToolTipText("Move selection down...");
		listBoxMoveDown.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				ListBoxMoveDownMouseClicked(evt);
			}
		});

		//deleteButton.setText("<html><font color=\"red\"><B>X</B></font>");
		deleteButton.setText("X");
		deleteButton.setForeground(Color.red);
		deleteButton.setToolTipText("Delete selection from context...");
		deleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				DeleteButtonMouseClicked(evt);
			}
		});

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
			.add(layout.createSequentialGroup()
				.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
					.add(layout.createSequentialGroup()
					.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING,false)
						.add(argScrollPane, 0,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
						.add(layout.createSequentialGroup()
							.add(listBoxMoveUp,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,51,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
							.add(listBoxMoveDown).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,Short.MAX_VALUE)
							.add(deleteButton))) //org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,30,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
							.add(jSeparator3,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,21,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
							.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(dInputLabel)
								.add(dArgLabel)
								.add(layout.createSequentialGroup()
									.add(10,10,10)
									.add(argLabel))
									.add(dOutputLabel)
									.add(layout.createSequentialGroup()
									.add(10,10,10)
									.add(outLabel))
									.add(layout.createSequentialGroup()
										.add(10,10,10)
										.add(inLabel))))
										.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
											.add(org.jdesktop.layout.GroupLayout.LEADING,layout.createSequentialGroup()
											.addContainerGap()
											.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
												.add(inputLabel)
												.add(outputLabel)
												.add(argumentLabel))
												.add(14,14,14)
												.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
													.add(org.jdesktop.layout.GroupLayout.TRAILING,argTextField)
													.add(org.jdesktop.layout.GroupLayout.TRAILING,outputTextField)
													.add(inputTextField,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,420,Short.MAX_VALUE))
													.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
													//.add(argAddButton))
													.add(org.jdesktop.layout.GroupLayout.LEADING,jSeparator2,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,579,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
													.addContainerGap(587, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout.createSequentialGroup()
					.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
						.add(org.jdesktop.layout.GroupLayout.TRAILING,jSeparator3,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,276,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.add(org.jdesktop.layout.GroupLayout.TRAILING,layout.createSequentialGroup()
							.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
								.add(argScrollPane,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,247,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(layout.createSequentialGroup()
								.addContainerGap()
								.add(dInputLabel)
								.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(inLabel)
																										.add(52,52,52)
																										.add(dArgLabel)
																										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(argLabel)
																										.add(52,52,52)
																										.add(dOutputLabel)
																										.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(outLabel)))
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
																						.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
																						.add(listBoxMoveUp)
																						.add(listBoxMoveDown))
																						.add(deleteButton))
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
										.add(
												jSeparator2,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												10,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(
												layout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																layout
																		.createSequentialGroup()
																		.add(6,6,6)
																		.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
																		.add(inputLabel)
																		.add(inputTextField,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
																		.add(argumentLabel).add(argTextField,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
																			.add(outputTextField,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																			.add(outputLabel)))
																			.add(layout.createSequentialGroup()
																		.addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
//																		.add(
//																				argAddButton,
//																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
//																				org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
//																				org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap()))));
	}

	private void DeleteButtonMouseClicked(java.awt.event.MouseEvent evt) {
		int idx = callList.getSelectedIndex();

		// remove from visible list
		mylist.remove(callList.getSelectedIndex());

		// remove from actual vector
		para.removeIn(idx);
		para.removeOut(idx);
		para.removeArg(idx);
		try {
			if (idx > 0) {
				callList.setSelectedIndex(idx - 1);
			} else {
				callList.setSelectedIndex(0);
			}

			if (mylist.size() > 0) {
				inLabel.setEnabled(true);
				dInputLabel.setEnabled(true);
				argLabel.setEnabled(true);
				dArgLabel.setEnabled(true);
				outLabel.setEnabled(true);
				dOutputLabel.setEnabled(true);
				inLabel.setText(para.getIn().elementAt(
						callList.getSelectedIndex()).toString());
				argLabel.setText(para.getArg().elementAt(
						callList.getSelectedIndex()).toString());
				outLabel.setText(para.getOut().elementAt(
						callList.getSelectedIndex()).toString());
			} else {
				inLabel.setEnabled(false);
				dInputLabel.setEnabled(false);
				argLabel.setEnabled(false);
				dArgLabel.setEnabled(false);
				outLabel.setEnabled(false);
				dOutputLabel.setEnabled(false);
				inLabel.setText("<none>");
				argLabel.setText("<none>");
				outLabel.setText("<none>");
			}
		} catch (Exception e) {
		}
	}

	private void ListBoxMoveDownMouseClicked(java.awt.event.MouseEvent evt) {
		int idx = callList.getSelectedIndex();
		if (idx != mylist.size() - 1) {
			String temp = (String) mylist.get(idx + 1);
			mylist.set(idx + 1, mylist.get(idx));
			mylist.set(idx, temp);
			callList.setSelectedIndex(idx + 1);
		}

		inLabel.setEnabled(true);
		dInputLabel.setEnabled(true);
		argLabel.setEnabled(true);
		dArgLabel.setEnabled(true);
		outLabel.setEnabled(true);
		dOutputLabel.setEnabled(true);

		inLabel.setText(para.getIn().elementAt(callList.getSelectedIndex())
				.toString());
		argLabel.setText(para.getArg().elementAt(callList.getSelectedIndex())
				.toString());
		outLabel.setText(para.getOut().elementAt(callList.getSelectedIndex())
				.toString());
	}

	private void ListBoxMoveUpMouseClicked(java.awt.event.MouseEvent evt) {
		int idx = callList.getSelectedIndex();
		if (idx != 0) {
			String temp = (String) mylist.get(idx - 1);
			mylist.set(idx - 1, mylist.get(idx));
			mylist.set(idx, temp);
			callList.setSelectedIndex(idx - 1);
		}

		inLabel.setEnabled(true);
		dInputLabel.setEnabled(true);
		argLabel.setEnabled(true);
		dArgLabel.setEnabled(true);
		outLabel.setEnabled(true);
		dOutputLabel.setEnabled(true);

		inLabel.setText(para.getIn().elementAt(callList.getSelectedIndex())
				.toString());
		argLabel.setText(para.getArg().elementAt(callList.getSelectedIndex())
				.toString());
		outLabel.setText(para.getOut().elementAt(callList.getSelectedIndex())
				.toString());
	}

	private void callListMouseClicked(java.awt.event.MouseEvent evt) {
		if (mylist.size() > 0) {
			inLabel.setEnabled(true);
			dInputLabel.setEnabled(true);
			argLabel.setEnabled(true);
			dArgLabel.setEnabled(true);
			outLabel.setEnabled(true);
			dOutputLabel.setEnabled(true);

			inLabel.setText(para.getIn().elementAt(callList.getSelectedIndex())
					.toString());
			argLabel.setText(para.getArg().elementAt(
					callList.getSelectedIndex()).toString());
			outLabel.setText(para.getOut().elementAt(
					callList.getSelectedIndex()).toString());
		}
	}

	public GriderDispatcherParameter getParameter() {
		return para;
	}

	public void setParamter(GriderDispatcherParameter p) {
		para.setEqual(p);
		mylist.clear();
		int i;
		for (i = 0; i < para.getIn().size(); i++)
			mylist.addElement((String) para.getIn().elementAt(i)
					+ (String) para.getArg().elementAt(i)
					+ (String) para.getOut().elementAt(i));
	}

	public void addArg() {
		int index = callList.getModel().getSize();
		String name = inputTextField.getText() + argTextField.getText()
				+ outputTextField.getText();

		para.addIn(inputTextField.getText());
		inputTextField.setText("");
		para.addArg(argTextField.getText());
		argTextField.setText("");
		para.addOut(outputTextField.getText());
		outputTextField.setText("");

		mylist.add(index, name);
	}

	public void clear() {
		para.clear();
		mylist.clear();
	}

	/* VARIABLE */
	private GriderDispatcherParameter para;

	private DefaultListModel mylist;

	// Variables declaration - do not modify
	private javax.swing.JButton argAddButton;

	private javax.swing.JScrollPane argScrollPane;

	private javax.swing.JTextField argTextField;

	private javax.swing.JLabel argumentLabel;

	private javax.swing.JList callList;

	private javax.swing.JButton deleteButton;

	private javax.swing.JLabel inputLabel;

	private javax.swing.JTextField inputTextField;

	private javax.swing.JButton listBoxMoveDown;

	private javax.swing.JButton listBoxMoveUp;

	private javax.swing.JLabel outputLabel;

	private javax.swing.JTextField outputTextField;

	private javax.swing.JLabel argLabel;

	private javax.swing.JLabel dArgLabel;

	private javax.swing.JLabel dInputLabel;

	private javax.swing.JLabel dOutputLabel;

	private javax.swing.JLabel inLabel;

	private javax.swing.JSeparator jSeparator2;

	private javax.swing.JSeparator jSeparator3;

	private javax.swing.JLabel outLabel;
	// End of variables declaration
}