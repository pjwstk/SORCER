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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GridDispatcherCtxUI extends JFrame implements ActionListener {

	private JTextField winExecTfld, winBinFolderTfld, winBinFileTfld,
			winLibFolderTfld, winLibFileTfld, linuxBinFolderTfld,
			linuxBinFileTfld, solarisBinFolderTfld, solarisBinFileTfld,
			callercmdTfld, cmdTfld;
	private JLabel winExecLbl, winBinFolderLbl, winBinFileLbl, winLibFolderLbl,
			winLibFileLbl, linuxBinFolderLbl, linuxBinFileLbl,
			solarisBinFolderLbl, solarisBinFileLbl, callecmdLbl, cmdLbl;
	private JCheckBox isOverWritChkBx;
	private JButton okBtn, cancelBtn;

	// ----------------------------------BUILD GUI
	// BEGIN------------------------------------
	public GridDispatcherCtxUI() {// Object obj) {
		super();
		try {

			setTitle("Specify Executables");
			getContentPane().setLayout(
					new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			// getContentPane().setLayout(new GridLayout(4,1));
			// System.out.println(this.getClass()+":GridDispatcherCtxUI()::"+getContentPane().getLayout());
			getContentPane().add(getWinOSUI());
			getContentPane().add(getLinuxOSUI());
			getContentPane().add(getSolarisOSUI());
			getContentPane().add(getCmdUI());
			getContentPane().add(getBtnUI());
			// Display the window.
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
		if ("Save".equals(cmd)) {
		}
		if ("Cancel".equals(cmd)) {
			setVisible(false);
		}
	}

	private JPanel getLinuxOSUI() {
		JPanel tempPnl = new JPanel();
		JPanel pnl1, pnl2, pnl;
		pnl = new JPanel();
		pnl1 = new JPanel();
		pnl2 = new JPanel();
		tempPnl.setLayout(new GridLayout(3, 1));

		linuxBinFolderLbl = new JLabel("Linux Binary Folder Path: ",
				JLabel.RIGHT);
		linuxBinFileLbl = new JLabel("Linux Binary File: ", JLabel.RIGHT);
		linuxBinFolderTfld = new JTextField(15);
		linuxBinFileTfld = new JTextField(15);

		pnl.add(new JLabel("Linux Operating System"));
		pnl1.setLayout(new GridLayout(1, 2));
		pnl1.add(linuxBinFolderLbl);
		pnl1.add(linuxBinFolderTfld);

		pnl2.setLayout(new GridLayout(1, 2));
		pnl2.add(linuxBinFileLbl);
		pnl2.add(linuxBinFileTfld);

		tempPnl.add(pnl);
		tempPnl.add(pnl1);
		tempPnl.add(pnl2);

		return tempPnl;
	}

	private JPanel getWinOSUI() {
		JPanel pnl1, pnl2, pnl3, pnl4, pnl5, tempPnl, pnl;
		pnl = new JPanel();
		pnl1 = new JPanel();
		pnl2 = new JPanel();
		pnl3 = new JPanel();
		pnl4 = new JPanel();
		pnl5 = new JPanel();
		pnl.add(new JLabel("Windows Operating System"));
		tempPnl = new JPanel(new GridLayout(6, 1));
		// tempPnl = new JPanel();

		winExecLbl = new JLabel("Windows Executable File Type: ", JLabel.RIGHT);
		winBinFolderLbl = new JLabel("Windows Binary Folder path: ",
				JLabel.RIGHT);
		winBinFileLbl = new JLabel("Wiondows Binary File: ", JLabel.RIGHT);
		winLibFolderLbl = new JLabel("Windows Library Folder Path: ",
				JLabel.RIGHT);
		winLibFileLbl = new JLabel("Windows Library File: ", JLabel.RIGHT);

		winExecTfld = new JTextField(15);
		winBinFolderTfld = new JTextField(15);
		winBinFileTfld = new JTextField(15);
		winLibFolderTfld = new JTextField(15);
		winLibFileTfld = new JTextField(15);

		pnl1.setLayout(new GridLayout(1, 2));
		pnl1.add(winExecLbl);
		pnl1.add(winExecTfld);

		pnl2.setLayout(new GridLayout(1, 2));
		pnl2.add(winBinFolderLbl);
		pnl2.add(winBinFolderTfld);

		pnl3.setLayout(new GridLayout(1, 2));
		pnl3.add(winBinFileLbl);
		pnl3.add(winBinFileTfld);

		pnl4.setLayout(new GridLayout(1, 2));
		pnl4.add(winLibFolderLbl);
		pnl4.add(winLibFolderTfld);

		pnl5.setLayout(new GridLayout(1, 2));
		pnl5.add(winLibFileLbl);
		pnl5.add(winLibFileTfld);

		tempPnl.add(pnl);
		tempPnl.add(pnl1);
		tempPnl.add(pnl2);
		tempPnl.add(pnl3);
		tempPnl.add(pnl4);
		tempPnl.add(pnl5);

		return tempPnl;

	}

	private JPanel getSolarisOSUI() {
		JPanel tempPnl = new JPanel();
		JPanel pnl, pnl1, pnl2;

		pnl = new JPanel();
		pnl1 = new JPanel();
		pnl2 = new JPanel();
		tempPnl.setLayout(new GridLayout(3, 1));

		solarisBinFolderLbl = new JLabel("Solaris Binary Folder Path: ",
				JLabel.RIGHT);
		solarisBinFileLbl = new JLabel("Solaris Binary File: ", JLabel.RIGHT);
		solarisBinFolderTfld = new JTextField(15);
		solarisBinFileTfld = new JTextField(15);

		pnl.add(new JLabel("Solaris Operating System"));

		pnl1.setLayout(new GridLayout(1, 2));
		pnl1.add(solarisBinFolderLbl);
		pnl1.add(solarisBinFolderTfld);

		pnl2.setLayout(new GridLayout(1, 2));
		pnl2.add(solarisBinFileLbl);
		pnl2.add(solarisBinFileTfld);

		tempPnl.add(pnl);
		tempPnl.add(pnl1);
		tempPnl.add(pnl2);

		return tempPnl;
	}

	private JPanel getBtnUI() {
		JPanel tempPnl = new JPanel();
		JButton okBtn = new JButton("Save");
		JButton cancelBtn = new JButton("Cancel");
		okBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		tempPnl.add(okBtn);
		tempPnl.add(cancelBtn);
		return tempPnl;
	}

	private JPanel getCmdUI() {
		JPanel tempPnl = new JPanel();
		JPanel pnl1, pnl2;
		pnl1 = new JPanel();
		pnl2 = new JPanel();

		tempPnl.setLayout(new GridLayout(2, 1));
		cmdLbl = new JLabel("Execute Command: ", JLabel.RIGHT);
		cmdTfld = new JTextField(15);

		pnl1.add(new JLabel(""));// Add Nothing to give a space between this and
									// other Guis

		pnl2.setLayout(new GridLayout(1, 2));
		pnl2.add(cmdLbl);
		pnl2.add(cmdTfld);

		tempPnl.add(pnl1);
		tempPnl.add(pnl2);

		return tempPnl;
	}
}
