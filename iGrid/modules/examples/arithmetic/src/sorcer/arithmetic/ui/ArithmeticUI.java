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

package sorcer.arithmetic.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.arithmetic.Adder;
import sorcer.arithmetic.Arithmetic;
import sorcer.arithmetic.ArithmeticRemote;
import sorcer.arithmetic.Averager;
import sorcer.arithmetic.Divider;
import sorcer.arithmetic.Multiplier;
import sorcer.arithmetic.Subtractor;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.proxy.Outer;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.Service;
import sorcer.service.SignatureException;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

/**
 * Component Service UI for SORCER Arithmetic - an example service
 */
public class ArithmeticUI extends JPanel implements SorcerConstants {

	private final static Logger logger = Logger.getLogger(ArithmeticUI.class
			.getName());

	private ServiceItem item;

	// SORCER provider or semismart proxy Service#service(Exertion)
	private Service provider;

	// used for remote calls via a proxy
	// implementing ArithemeticRemote
	private ArithmeticRemote server;

	// used for remote calls via a proxy
	// implementing Partnership
	private Averager partner;

	// used for local execution - smart proxies,
	// implementing local Arithmetic calls
	private Arithmetic smartProxy;

	// a flag indicating if the proxy is remote, service provider's, or extended
	// server (partner)
	private boolean isRemote, isProvider, isExtended;

	private ArrayContext context;

	private String selector;

	private JTextField inField;

	private JTextArea outText;

	/** Creates new Arithmetic UI Component */
	public ArithmeticUI(Object obj) {
		super();
		getAccessibleContext().setAccessibleName("Arithmetic Tester");
		try {
			item = (ServiceItem) obj;
			logger.info("service class: " + item.service.getClass().getName()
					+ "\nservice object: " + item.service);

			if (item.service instanceof Provider) {
				provider = (Provider) item.service;
				isProvider = true;
			}

			if (item.service instanceof ArithmeticRemote) {
				server = (ArithmeticRemote) item.service;
				isRemote = true;
			} else if (item.service instanceof Arithmetic) {
				smartProxy = (Arithmetic) item.service;
				isRemote = false;
				if (smartProxy instanceof Outer) {
					Object proxy = ((Outer) smartProxy).getInner();
					if (proxy instanceof Provider) {
						isProvider = true;
						provider = (Provider) proxy;
					} else if (proxy instanceof Outer) {
						isExtended = true;
						partner = (Averager) proxy;
					}
				}

			}
			logger.info("isProvider: " + isProvider + ", provider: " + provider
					+ "\nisRemote: " + isRemote + ", server= " + server
					+ "\nsmartProxy: " + smartProxy + "\nisExtended: "
					+ partner);

			// Schedule a job for the event-dispatching thread:
			// creating this application's service UI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createUI();
				}
			});

			// inspect class loader tree
			// com.sun.jini.start.ClassLoaderUtil.displayContextClassLoaderTree();
			// com.sun.jini.start.ClassLoaderUtil.displayClassLoaderTree(provider
			// .getClass().getClassLoader());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void createUI() {
		setLayout(new BorderLayout());
		JPanel entryPanel = new JPanel(new BorderLayout());
		entryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel lbl = new JLabel(
				"Enter numeric values (space separated):");
		entryPanel.add(lbl, BorderLayout.NORTH);
		inField = new JTextField(40);
		inField.setActionCommand("context");
		inField.addActionListener(new ArithmeticActionListener());
		entryPanel.add(inField, BorderLayout.SOUTH);
		outText = new JTextArea(20, 40);
		JScrollPane scroller = new JScrollPane(outText);
		scroller.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		JButton btn;
		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));
		cmdPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		btn = new JButton("Clear");
		btn.setActionCommand("clear");
		btn.addActionListener(new ArithmeticActionListener());
		cmdPanel.add(btn);

		cmdPanel.add(Box.createHorizontalGlue());

		btn = new JButton("Add");
		btn.setActionCommand("add");
		btn.addActionListener(new ArithmeticActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Subtract");
		btn.setActionCommand("subtract");
		btn.addActionListener(new ArithmeticActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Divide");
		btn.setActionCommand("divide");
		btn.addActionListener(new ArithmeticActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Multiply");
		btn.setActionCommand("multiply");
		btn.addActionListener(new ArithmeticActionListener());
		cmdPanel.add(btn);

		if (isExtended) {
			btn = new JButton("Average");
			btn.setActionCommand("average");
			btn.addActionListener(new ArithmeticActionListener());
			cmdPanel.add(btn);
		}

		add(entryPanel, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);
		add(cmdPanel, BorderLayout.SOUTH);
	}

	private ArrayContext createServiceContext(String userLine) {
		context = new ArrayContext("arithmetic");
		String data[] = SorcerUtil.getTokens(userLine, " ");
		try {
			for (int i = 0; i < data.length; i++) {
				context.iv(i, new Double(data[i]));
				context.ivc(i, "user input");
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ContextException e) {
			e.printStackTrace();
		}
		return context;
	}

	private NetTask createTask(String userLine) {
		NetTask task = null;
		if (context == null)
			createServiceContext(userLine);
		try {
			context.d("testing operation: " + selector);

			// specify method selector and service type
			NetSignature method = new NetSignature(selector,
					sorcer.arithmetic.ArithmeticRemote.class);
			// specify task name, description and methods
			task = new NetTask(selector, selector
					+ " multiple float-point numbers", method);
			// assign service context for the task
			task.setContext(context);
		} catch (ContextException e) {
			e.printStackTrace();
		} 
		return task;
	}

	class ArithmeticActionListener implements ActionListener {
		
		public ArithmeticActionListener() {
			super();
		}

		public void actionPerformed(ActionEvent ae) {
			selector = ae.getActionCommand();
			if (selector.equals("clear")) {
				inField.setText("");
				outText.setText("");
				context = null;
				return;
			} else if (selector.equals("context")) {
				outText.setText("");
				outText.append("Input Data Context");
				outText.append(createServiceContext(inField.getText()).toString());
				return;
			}
			try {
				NetTask task = createTask(inField.getText());	
				Context inContext = task.getContext();
				if (isRemote && server != null)	
					task = (NetTask) serverService(task);
				else if (isProvider && isRemote)
					task = (NetTask) ((Service) provider).service(task, null);
				else if (smartProxy != null)
					task = (NetTask) smartService(task);
				else if (isExtended) {
					Context cxt = partner.average(task.getContext());
					task.setContext(cxt);
				} else {
					task = (NetTask) beanService(task);
				}
	
				outText.setText("Input Data Context");
				outText.append(inContext.toString());
				outText.append("Output Data Context");
				outText.append(task.getContext().toString());
			} catch (Exception ex) {
				logger.throwing(ArithmeticUI.class.getName(),
						"actionPerformed", ex);
			}
		}
	}

	/**
	 * Invokes operations of the ArithemeticRemote interface on the remote
	 * server that is not of the Provider type.
	 * 
	 * @param exertion
	 *            a service task for the uniform S2S case
	 * @return service task
	 * @throws RemoteException
	 * @throws ExertionException
	 * @throws ContextException 
	 */
	public Exertion serverService(Exertion exertion) throws RemoteException,
			ExertionException, ContextException {
		// execute task directly using RemoteArithmetic
		Context result = null;
		String selector = exertion.getProcessSignature().getSelector();
		if (selector.equals(Arithmetic.ADD))
			result = server.add(exertion.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = server.subtract(exertion.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = server.multiply(exertion.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = server.divide(exertion.getContext());

		logger.info("serverService:result: " + result);
		((ServiceExertion) exertion).setContext(result);
		return exertion;
	}

	/**
	 * Invokes local operations of the Arithmetic interface of smart proxy
	 * 
	 * @param exertion
	 *            a service task for the uniform S2S case
	 * @return service task
	 * @throws RemoteException
	 * @throws ExertionException
	 * @throws ContextException 
	 */
	public NetTask smartService(NetTask task) throws RemoteException,
			ExertionException, ContextException {
		// execute task locally
		Context result = null;
		String selector = task.getProcessSignature().getSelector();
		if (selector.equals(Arithmetic.ADD))
			result = smartProxy.add(task.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = smartProxy.subtract(task.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = smartProxy.multiply(task.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = smartProxy.divide(task.getContext());

		logger.info("smartService:result: " + result);
		task.setContext(result);
		return task;
	}

	public Exertion beanService(Exertion exertion) throws RemoteException,
			ExertionException, ContextException {
		// execute task locally
		Context result = null;
		String selector = exertion.getProcessSignature().getSelector();
		if (selector.equals(Arithmetic.ADD))
			result = ((Adder) provider).add(exertion.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = ((Subtractor) provider).subtract(exertion.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = ((Multiplier) provider).multiply(exertion.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = ((Divider) provider).divide(exertion.getContext());

		logger.info("beanService:result: " + result);
		((ServiceExertion) exertion).setContext(result);
		return exertion;
	}

	/**
	 * Returns a service UI descriptor for this service. Usually this method is
	 * used as an entry in provider configuration files when smart proxies are
	 * deployed with a standard off the shelf {@link ServiceProvider}.
	 * 
	 * @return service UI descriptor
	 */
	public static UIDescriptor getUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIComponentFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/arithmetic-ui.jar") }, ArithmeticUI.class
							.getName()));
		} catch (Exception ex) {
			logger
					.throwing(ArithmeticUI.class.getName(), "getUIDescriptor",
							ex);
		}
		return uiDesc;
	}
}
