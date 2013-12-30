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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lookup.ServiceItem;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import sorcer.core.Grider;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.grid.provider.grider.GridDispatcherContextUtil;
import sorcer.core.grid.provider.grider.GridDispatcherRemote;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Service;
import sorcer.service.Signature;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;
import sorcer.util.SorcerUtil;

public class GriderDispatcherUI extends javax.swing.JFrame {

	private static final int FRAME_WIDTH = 600;

	private static final int FRAME_HEIGHT = 460;

	private Service dispatcher;

	private net.jini.core.event.RemoteEventListener listener;

	private boolean valid;

	private GriderDispatcherConfig c;

	private GriderDispatcherGeneralPanel generalPanel;

	private GriderDispatcherIncludePanel includePanel;

	private GriderDispatcherArgumentsPanel argPanel;

	private GriderDispatcherExecutionPanel execPanel;

	private GriderDispatcherContextPanel contextPanel;

	private JFileChooser jFC = new JFileChooser();

	private javax.swing.JTabbedPane applicationTabPane;

	private javax.swing.JProgressBar progressBar;

	private JTextArea outListTarea;

	private OutFrame outFrame;

	static private final Logger logger = Log.getProviderLog();

	public GriderDispatcherUI(Object obj) {
		super();
		getAccessibleContext().setAccessibleName("SGrid UI");

		if (obj instanceof ServiceItem) {
			dispatcher = (Service) ((ServiceItem) obj).service;
		} else
			dispatcher = (Service) obj;

		try {
			listener = new DispatcherListener(this).getListener();
		} catch (RemoteException e) {
			logger.throwing("GriderDispatcherUI",
					"Constructor GriderDispatcherUI", e);
		}

		initComponents();
		c = new GriderDispatcherConfig();
		progressBar.setVisible(false);
		outFrame = new OutFrame();

		generalPanel = new GriderDispatcherGeneralPanel();
		includePanel = new GriderDispatcherIncludePanel();
		argPanel = new GriderDispatcherArgumentsPanel();
		execPanel = new GriderDispatcherExecutionPanel();
		contextPanel = new GriderDispatcherContextPanel();

		applicationTabPane.addTab("General", generalPanel);
		applicationTabPane.addTab("Include", includePanel);
		applicationTabPane.addTab("Arguments", argPanel);
		applicationTabPane.addTab("Execution", execPanel);
		applicationTabPane.addTab("Context", contextPanel);

		createPopupMenu();
		setContentPane(applicationTabPane);
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		// pack();
	}

	public static void main(String args[]) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				Object obj = ProviderAccessor.getProvider(Grider.class);
				new GriderDispatcherUI(obj).setVisible(true);
			}
		});
	}

	private void initComponents() {
		applicationTabPane = new JTabbedPane();
		progressBar = new JProgressBar();

		JToolBar toolBar = new JToolBar();
		JLabel progressLabel = new JLabel();
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenuItem newMenuItem = new JMenuItem();
		JMenuItem loadMenuItem = new JMenuItem();
		JMenuItem clearMenuItem = new JMenuItem();
		JMenuItem saveMenuItem = new JMenuItem();
		JMenuItem exitMenuItem = new JMenuItem();
		JMenu actionMenu = new JMenu();
		JMenuItem verifyMenuItem = new JMenuItem();
		JMenuItem submitMenuItem = new JMenuItem();
		JMenu navigateMenu = new JMenu();
		JMenuItem generalMenuItem = new JMenuItem();
		JMenuItem includeMenuItem = new JMenuItem();
		JMenuItem argumentsMenuItem = new JMenuItem();
		JMenuItem executionMenuItem = new JMenuItem();
		JMenuItem contextMenuItem = new JMenuItem();
		JMenuItem helpMenu = new JMenu();
		JMenuItem javaDocMenuItem = new JMenuItem();
		JMenuItem aboutMenuItem = new JMenuItem();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		setTitle("SGrid Service UI");
		// setResizable(false);
		toolBar.setFloatable(false);
		toolBar.add(progressLabel);
		toolBar.add(progressBar);

		fileMenu.setText("File");
		newMenuItem.setText("New");
		newMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clear();
			}
		});
		fileMenu.add(newMenuItem);

		loadMenuItem.setText("Load");
		loadMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				load();
			}
		});
		fileMenu.add(loadMenuItem);

		clearMenuItem.setText("Clear");
		clearMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clear();
			}
		});
		fileMenu.add(clearMenuItem);

		saveMenuItem.setText("Save");
		saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				save();
			}
		});
		fileMenu.add(saveMenuItem);

		exitMenuItem.setText("Exit");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dispose();
			}
		});
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);

		actionMenu.setText("Action");
		verifyMenuItem.setText("Verify");
		verifyMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				verify();
			}
		});
		actionMenu.add(verifyMenuItem);

		submitMenuItem.setText("Submit");
		submitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				submit();
			}
		});
		actionMenu.add(submitMenuItem);
		menuBar.add(actionMenu);

		navigateMenu.setText("Navigate");
		generalMenuItem.setText("General");
		generalMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				applicationTabPane.setSelectedIndex(0);
			}
		});
		navigateMenu.add(generalMenuItem);

		includeMenuItem.setText("Include");
		includeMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				applicationTabPane.setSelectedIndex(1);
			}
		});
		navigateMenu.add(includeMenuItem);

		argumentsMenuItem.setText("Arguments");
		argumentsMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						applicationTabPane.setSelectedIndex(2);
					}
				});
		navigateMenu.add(argumentsMenuItem);

		executionMenuItem.setText("Execution");
		executionMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						applicationTabPane.setSelectedIndex(3);
					}
				});
		navigateMenu.add(executionMenuItem);

		contextMenuItem.setText("Context");
		contextMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				commitAllChanges();
				contextPanel.setConfigTextArea(c.view());
				applicationTabPane.setSelectedIndex(4);
			}
		});
		navigateMenu.add(contextMenuItem);

		menuBar.add(navigateMenu);

		helpMenu.setText("Help");
		javaDocMenuItem.setText("Java Doc");
		javaDocMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// TO DO
			}
		});
		helpMenu.add(javaDocMenuItem);

		aboutMenuItem.setText("About");
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// TO DO
			}
		});
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		setJMenuBar(menuBar);
		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
	}

	public void createPopupMenu() {
		// Create the popup menu.
		JMenuItem popupLoadMenuItem = new JMenuItem();
		JMenuItem popupSaveMenuItem = new JMenuItem();
		JMenuItem popupClearMenuItem = new JMenuItem();

		JMenuItem popupVerifyMenuItem = new JMenuItem();
		JMenuItem popupSubmitMenuItem = new JMenuItem();

		popupLoadMenuItem.setText("Load");
		popupLoadMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						load();
					}
				});

		popupSaveMenuItem.setText("Save");
		popupSaveMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						save();
					}
				});

		popupClearMenuItem.setText("Clear");
		popupClearMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						clear();
					}
				});

		popupVerifyMenuItem.setText("Verify");
		popupVerifyMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						verify();
					}
				});

		popupSubmitMenuItem.setText("Submit");
		popupSubmitMenuItem
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						submit();
					}
				});

		JPopupMenu popup = new JPopupMenu();
		popup.add(popupLoadMenuItem);
		popup.add(popupSaveMenuItem);
		popup.addSeparator();
		popup.add(popupClearMenuItem);
		popup.addSeparator();
		popup.add(popupVerifyMenuItem);
		popup.add(popupSubmitMenuItem);

		// Add listener to the text area so the popup menu can come up.
		MouseListener popupListener = new PopupListener(popup);
		applicationTabPane.addMouseListener(popupListener);
	}

	class PopupListener extends MouseAdapter {
		JPopupMenu popup;

		PopupListener(JPopupMenu popupMenu) {
			popup = popupMenu;
		}

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private void save() {
		commitAllChanges();

		jFC.setApproveButtonText("Save");
		try {
			jFC.showOpenDialog(this);
			String fileName = jFC.getSelectedFile().getAbsolutePath();

			GriderDispatcherFileAccess gdtfs = new GriderDispatcherFileAccess();
			gdtfs.saveConfig(fileName, c);
		} catch (Exception ex) {
		}
	}

	private void load() {
		jFC.setApproveButtonText("Load");
		try {
			jFC.showOpenDialog(this);
			String fileName = jFC.getSelectedFile().getAbsolutePath();

			GriderDispatcherFileAccess f = new GriderDispatcherFileAccess();
			c = f.load(fileName);
			dumpConfig();
			checkAll();
		} catch (Exception ex) {
		}
	}

	private void dumpConfig() {
		// Set the general panel values
		generalPanel.setProgramName(c.ca.getProgname());
		generalPanel.setJobSize(c.ca.getJobsize());
		generalPanel.setNotify(c.ca.getNotify());

		// Set the execution panel values
		execPanel.setExecCommand(c.ca.getExecutecommand());
		execPanel.setLinux(c.prog.getLinux());
		execPanel.setLinTab(c.prog.getLinux());
		execPanel.setLinBinFile(c.prog.getLinbinFile());
		execPanel.setLinBinPath(c.prog.getLinbinPath());
		execPanel.setSolaris(c.prog.getSolaris());
		execPanel.setSolTab(c.prog.getSolaris());
		execPanel.setSolBinFile(c.prog.getSolbinFile());
		execPanel.setSolBinPath(c.prog.getSolbinPath());
		execPanel.setWindows(c.prog.getWindows());
		execPanel.setWinTab(c.prog.getWindows());
		execPanel.setWinBinFile(c.prog.getWinbinFile());
		execPanel.setWinBinPath(c.prog.getWinbinPath());
		execPanel.setWinExecFileType(c.prog.getWinexecFiletype());
		execPanel.setWinLibFile(c.prog.getWinlibFile());
		execPanel.setWinLibPath(c.prog.getWinlibPath());

		// Set the argument panel values
		argPanel.setParamter(c.ca.para);

		// Set the include panel values
		includePanel.setInclude(c.ca.getInclude());

		// Set the context panel values
		contextPanel.setConfigTextArea(c.view());
	}

	private void clear() {
		applicationTabPane.setSelectedIndex(0);
		generalPanel.clear();
		includePanel.clear();
		argPanel.clear();
		execPanel.clear();

		commitAllChanges();
	}

	private void verify() {
		commitAllChanges();
		checkAll();

		JOptionPane.showMessageDialog(this, getVString());
	}

	private void submit() {
		commitAllChanges();
		checkAll();

		if (valid) {
			String temp = "";
			temp += "All fields are valid\nThe following context will be submitted:\n";
			// /*
			JOptionPane.showMessageDialog(this, temp + context().toString());

			Context resultantContext = context();
			try {
				NetSignature signature = new NetSignature("computePrime",
						GridDispatcherRemote.class);
				NetTask task = new NetTask("computePrime",
						"computePrime", signature);
				task.setContext(resultantContext);

				resultantContext = dispatcher.service(task, null).getContext();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Remote Call Failed");
			}
			// */
		} else {
			JOptionPane.showMessageDialog(this, getVString());
		}
	}

	private void checkAll() {
		generalPanel.checkAll();
		execPanel.checkAll();

		String temp = generalPanel.getVString() + execPanel.getVString();
		if (temp.length() > 0) {
			valid = false;
		} else {
			valid = true;
		}
	}

	private String getVString() {
		String temp = "";
		String t = "";
		temp += generalPanel.getVString();
		temp += execPanel.getVString();

		if (temp.length() > 0) {
			valid = false;
			t += "The following fields are invalid:\n" + temp;
		} else {
			valid = true;
			t += "All fields are valid.";
		}

		return t;
	}

	private void commitAllChanges() {
		commitArgumentsPanel();
		commitExecutionPanel();
		commitGeneralPanel();
		commitIncludePanel();
	}

	private void commitArgumentsPanel() {
		c.ca.para.setEqual(argPanel.getParameter());
	}

	private void commitExecutionPanel() {
		c.ca.setExecutecommand(execPanel.getExecCommand());
		c.prog.setLinbinFile(execPanel.getLinBinFile());
		c.prog.setLinbinPath(execPanel.getLinBinPath());
		c.prog.setSolbinFile(execPanel.getSolBinFile());
		c.prog.setSolbinPath(execPanel.getSolBinPath());
		c.prog.setWinbinFile(execPanel.getWinBinFile());
		c.prog.setWinbinPath(execPanel.getWinBinPath());
		c.prog.setWinexecFiletype(execPanel.getWinExecFileType());
		c.prog.setWinlibFile(execPanel.getWinLibFile());
		c.prog.setWinlibPath(execPanel.getWinLibPath());
		c.prog.setWindows(execPanel.getWindows());
		c.prog.setLinux(execPanel.getLinux());
		c.prog.setSolaris(execPanel.getSolaris());
	}

	private void commitGeneralPanel() {
		c.ca.setHost(generalPanel.getHost());
		c.ca.setJobsize(generalPanel.getJobSize());
		c.ca.setLocation(generalPanel.getLocationValue());
		c.ca.setNodename(generalPanel.getNodeName());
		c.ca.setNotify(generalPanel.getNotify());
		c.ca.setProgname(generalPanel.getProgramName());
	}

	private void commitIncludePanel() {
		c.ca.setInclude(includePanel.getInclude());
	}

	private Context context() {
		Context ctx = new ServiceContext();
		try {
			// General Panel Content
			GridDispatcherContextUtil.setProgramName(ctx, generalPanel
					.getProgramName());
			GridDispatcherContextUtil
					.setJobSize(ctx, generalPanel.getJobSize());
			GridDispatcherContextUtil.setNotify(ctx, generalPanel.getNotify());
			// GridDispatcherContextUtil.setLocation(ctx,generalPanel.getLocationValue());
			// GridDispatcherContextUtil.setHost(ctx,generalPanel.getHost());
			// GridDispatcherContextUtil.setNodeName(ctx,generalPanel.getNodeName());

			// Input Panel Content
			String[] paramValues = SorcerUtil.tokenize(includePanel
					.getInclude(), "\n");
			GridDispatcherContextUtil.setInputValues(ctx, paramValues);
			listener.notify(null);
			GridDispatcherContextUtil.setCallback(ctx, listener);

			// Arguments Panel Content

			// Execution Panel Content
			// GridDispatcherContextUtil.setWinExecFileType(ctx,execPanel.getWinExecFileType());
			GridDispatcherContextUtil.setWinBinPath(ctx, execPanel
					.getWinBinPath());
			GridDispatcherContextUtil.setWinBinFile(ctx, execPanel
					.getWinBinFile());
			GridDispatcherContextUtil.setWinLibPath(ctx, execPanel
					.getWinLibPath());
			GridDispatcherContextUtil.setWinLibFile(ctx, execPanel
					.getWinLibFile());
			GridDispatcherContextUtil.setSolBinPath(ctx, execPanel
					.getSolBinPath());
			GridDispatcherContextUtil.setSolBinFile(ctx, execPanel
					.getSolBinFile());
			GridDispatcherContextUtil.setLinBinPath(ctx, execPanel
					.getLinBinPath());
			GridDispatcherContextUtil.setLinBinFile(ctx, execPanel
					.getLinBinFile());
			GridDispatcherContextUtil.setExecCom(ctx, execPanel
					.getExecCommand());
		} catch (ContextException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (UnknownEventException e) {
			e.printStackTrace();
		}
		return ctx;
	}

	public void recievedOutput(String output) {
		outListTarea.append("\n");
		outListTarea.append(output);
		outFrame.pack();
		outFrame.setVisible(true);
	}

	class OutFrame extends JFrame implements ActionListener {
		public OutFrame() {
			super();
			try {
				setTitle("Output List");
				outListTarea = new JTextArea(40, 40);
				getContentPane().setLayout(new BorderLayout());
				JButton okBtn = new JButton("Cancel");
				JButton cancelBtn = new JButton("Done");
				cancelBtn.addActionListener(this);
				okBtn.addActionListener(this);
				outListTarea.setEditable(false);
				JPanel tmpPnl = new JPanel();
				tmpPnl.add(cancelBtn);
				getContentPane().add(new JScrollPane(outListTarea),
						BorderLayout.CENTER);
				getContentPane().add(tmpPnl, BorderLayout.SOUTH);
				pack();
			} catch (Exception e) {
				System.out.println("Failed to construct OutFrame");
				e.printStackTrace();
			}
		}

		public void actionPerformed(ActionEvent ae) {
			String cmd = ae.getActionCommand();
			if ("Done".equals(cmd)) {
				setVisible(false);
			}
			if ("Ok".equals(cmd)) {
				setVisible(false);
			}
		}
	}

	public static final class DispatcherListener implements
			RemoteEventListener, Serializable, Remote {
		public transient GriderDispatcherUI ui;

		public DispatcherListener() {
		}

		public DispatcherListener(GriderDispatcherUI pui) {
			ui = pui;
		}

		public RemoteEventListener getListener() throws RemoteException {
			BasicJeriExporter exp = new BasicJeriExporter(TcpServerEndpoint
					.getInstance(0), new BasicILFactory(), true, true);
			return (RemoteEventListener) exp.export(this);
		}

		public void notify(RemoteEvent event) throws RemoteException {
			if (event == null) {
				System.out
						.println("GridDispatcherUI.java:notify(RemoteEvent)::"
								+ "Remote Event is Null");
				return;
			}
			try {
				System.out
						.println("GridDispatcherUI.java:notify(RemoteEvent)::"
								+ "Remote Event obtained");
				ui.recievedOutput((String) event.getSource());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RemoteException("Exception occured in Output", e);
			}
		}
	}
}