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

package sorcer.core.grid.provider.grider;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 *  The Checker class is responsible for the functions that validate textboxes.  This basically involved obtaining a textbox
 *  and a string containing all the characters not allowed (or which must exist within the string).
 *
 *  The Checker class also contains a function to verify that a string is a valid email address.  This does not include
 *  any form of domain name checking or verification past being in a valid format. (so 34234987@988989r87987w.commu will be fine)
 *  
 *  The fonts are described in the bottom, and textboxes are set as red when they are invalid.  This does not update the 
 *  verification fields (whether or not something in the context is valid), it only checks the strings.
 */
public class GridDispatcherArgUI extends JFrame implements ActionListener {

	public JTextField addArgsTfld, addInTfld, addOutTfld;
	public JComboBox locationCbx, hostnameCbx, opSysCbx, callPrvCbx;
	public JTextArea argsTarea, inTarea, outTarea;
	public JCheckBox argsChkBx, inChkBx, outChkBx;

	private JButton okBtn, cancelBtn, upBtn, downBtn, deleteBtn;
	private JPanel argsPnl, inPnl, outPnl;

	// ----------------------------------BUILD GUI
	// BEGIN------------------------------------
	public GridDispatcherArgUI() {// Object obj) {
		super();
		try {
			JPanel mainPnl = new JPanel();// Main Panel on which all panels to
			// be added!!
			setTitle("Specify Arguments and Input files");
			// Display the window.
			// getContentPane().setLayout(new GridLayout(2,1));
			getContentPane().setLayout(new BorderLayout());
			mainPnl.add(getCenterPaneUI());
			mainPnl.add(getRightPaneUI());
			getContentPane().add(mainPnl, BorderLayout.CENTER);
			getContentPane().add(getOkCancelUI(), BorderLayout.SOUTH);
			pack();
			this.setResizable(false);
			setVisible(true);
		} catch (Exception e) {
			System.out.println("Exception in Constructor");
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent ae) {
		String cmd = ae.getActionCommand();
		String addtext = new String();
		System.out.println("cmd=" + cmd);
		if ("ArgumentsAdd".equals(cmd)) {
			if (argsChkBx.isSelected()) {
				try {
					// argsTarea.setEditable(false);
					addtext = addArgsTfld.getText();
					if ("".equals(addtext)) {
						throw new NullPointerException("Text Field Null");
					}
					int lines = argsTarea.getLineCount();
					int offset = 0;
					System.out.println("--Lines =" + lines + ",offset="
							+ offset);
					if (lines > 1) {
						System.out.println("Lines>1");
						offset = argsTarea.getLineEndOffset(lines - 2);
					}
					System.out.println("Lines =" + lines + ",offset=" + offset);
					if (offset >= 0)
						argsTarea.insert(addtext + "\n", offset);
					// else if(lines==0)argsTarea.insert(addtext+"\n",lines);
				} catch (Exception ne) {
					ne.printStackTrace();
				}
			} else {
				try {
					addtext = addArgsTfld.getText();
					if ("".equals(addtext)) {
						throw new NullPointerException("Text Field Null");
					}
					argsTarea.append(addtext + "\n");
					int lines = argsTarea.getLineCount();
					int offset = argsTarea.getLineEndOffset(lines - 1);
					System.out.println("Lines =" + lines + ",offset=" + offset);
					// argsTarea.insert(addtext+"\n",lines);
				} catch (Exception ne) {
					ne.printStackTrace();
				}
			}
		}
		if ("InputsAdd".equals(cmd)) {
		}
		if ("OutputsAdd".equals(cmd)) {
		}
		if ("Argumentsup".equals(cmd)) {
		}
		if ("Argumentsdown".equals(cmd)) {
		}
		if ("Argumentsdelete".equals(cmd)) {
		}
		if ("Inputsup".equals(cmd)) {
		}
		if ("Inputsdown".equals(cmd)) {
		}
		if ("Inputsdelete".equals(cmd)) {
		}
		if ("Outputsup".equals(cmd)) {
		}
		if ("Outputsdown".equals(cmd)) {
		}
		if ("Outputsdelete".equals(cmd)) {
		}
		if ("Save".equals(cmd)) {
		}
		if ("Cancel".equals(cmd)) {
			this.setVisible(false);
		}
	}

	private void getBtnsUI() {
	}

	private JPanel getCenterPaneUI() {
		System.out.println("getCenerPaneUI........");
		JPanel tempPnl = new JPanel();
		tempPnl.add(getArgsInOutUI("Arguments"));
		tempPnl.add(getArgsInOutUI("Inputs"));
		tempPnl.add(getArgsInOutUI("Outputs"));
		return tempPnl;
	}

	private JPanel getRightPaneUI() {
		JPanel tempPnl = new JPanel();
		tempPnl.setLayout(new GridLayout(3, 1));
		tempPnl.add(getAddUI("Arguments"));
		tempPnl.add(getAddUI("Inputs"));
		tempPnl.add(getAddUI("Outputs"));
		return tempPnl;
	}

	private JPanel getArgsInOutUI(String label) {
		System.out.println("getArgsInOutUI::label=" + label);
		JPanel tempPnl = new JPanel();
		JLabel jLbl = new JLabel(label);
		tempPnl.setLayout(new BorderLayout());
		tempPnl.add(jLbl, BorderLayout.NORTH);

		if (label.equals("Arguments")) {
			argsTarea = new JTextArea(10, 1);
			tempPnl.add(new JScrollPane(argsTarea), BorderLayout.CENTER);
			tempPnl.add(getUpDownDeleteUI(label), BorderLayout.SOUTH);
		} else if (label.equals("Inputs")) {
			inTarea = new JTextArea(10, 1);
			tempPnl.add(new JScrollPane(inTarea), BorderLayout.CENTER);
			tempPnl.add(getUpDownDeleteUI(label), BorderLayout.SOUTH);
		} else if (label.equals("Outputs")) {
			outTarea = new JTextArea(10, 1);
			tempPnl.add(new JScrollPane(outTarea), BorderLayout.CENTER);
			tempPnl.add(getUpDownDeleteUI(label), BorderLayout.SOUTH);
		}
		return tempPnl;
	}

	private JPanel getAddUI(String label) {
		JPanel tempPnl = new JPanel();
		tempPnl.setLayout(new GridLayout(2, 1));
		JLabel jLbl = new JLabel(label);
		// JButton okBtn;
		JPanel pnl1 = new JPanel();
		JPanel pnl2 = new JPanel();
		if (label.equals("Arguments")) {
			addArgsTfld = new JTextField(10);
			JButton okBtn = new JButton("Add");
			okBtn.setActionCommand(label + "Add");
			okBtn.addActionListener(this);
			argsChkBx = new JCheckBox("Above", true);
			pnl2.add(jLbl);
			pnl2.add(addArgsTfld);
			pnl1.add(argsChkBx);
			pnl1.add(okBtn);
			tempPnl.add(pnl2);
			tempPnl.add(pnl1);
		} else if (label.equals("Inputs")) {
			addInTfld = new JTextField(10);
			JButton okBtn = new JButton("Add");
			okBtn.setActionCommand(label + "add");
			okBtn.addActionListener(this);
			inChkBx = new JCheckBox("Above", true);
			pnl2.add(jLbl);
			pnl2.add(addInTfld);
			pnl1.add(inChkBx);
			pnl1.add(okBtn);
			tempPnl.add(pnl2);
			tempPnl.add(pnl1);
		} else if (label.equals("Outputs")) {
			addOutTfld = new JTextField(10);
			JButton okBtn = new JButton("Add");
			okBtn.setActionCommand(label + "add");
			okBtn.addActionListener(this);
			outChkBx = new JCheckBox("Above", true);
			pnl2.add(jLbl);
			pnl2.add(addOutTfld);
			pnl1.add(outChkBx);
			pnl1.add(okBtn);
			tempPnl.add(pnl2);
			tempPnl.add(pnl1);
		}
		return tempPnl;
	}

	private JPanel getUpDownDeleteUI(String label) {
		// Action a;
		// a.NAME=label;
		JPanel tempPnl = new JPanel();
		// tempPnl.setLayout(new GridLayout(2,1));
		upBtn = new JButton("Up");
		downBtn = new JButton("Down");
		deleteBtn = new JButton("Delete");
		upBtn.setActionCommand(label + "up");
		downBtn.setActionCommand(label + "down");
		deleteBtn.setActionCommand(label + "delete");

		upBtn.addActionListener(this);
		downBtn.addActionListener(this);
		deleteBtn.addActionListener(this);
		JPanel pnl1 = new JPanel();
		pnl1.add(upBtn);
		pnl1.add(downBtn);
		pnl1.add(deleteBtn);
		tempPnl.add(pnl1);
		// tempPnl.add((new JPanel()).add(deleteBtn));
		return tempPnl;
	}

	private JPanel getOkCancelUI() {
		JPanel tempPnl = new JPanel();
		JButton okBtn = new JButton("Save");
		JButton cancelBtn = new JButton("Cancel");
		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		tempPnl.add(okBtn);
		tempPnl.add(cancelBtn);
		return tempPnl;
	}

}
