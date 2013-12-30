package sorcer.test.policer.src.ui;

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
import javax.swing.JFileChooser;

import java.io.*;

import jgapp.util.Util;
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
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.Service;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Sorcer;

/**
 * Componet Service UI for SORCER Arithmetic - an example service
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

	/** Creates new Arithetic UI Component */
	public ArithmeticUI(Object obj) {
		super();
		
		System.out.println("Daniela's test");
		
		getAccessibleContext().setAccessibleName("Policer Tester: Arithmetic Provider");
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void createUI() {
		setLayout(new BorderLayout());
		
		JPanel entryPanel = new JPanel(new BorderLayout());
		entryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JLabel lbl = new JLabel(
				"Enter floating-point values (space separated):");
			
		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		
		inField = new JTextField(40);
		inField.setActionCommand("context");
		inField.addActionListener(new BtnActionListener());
		inputPanel.add(inField, BorderLayout.WEST);
		
		JButton btn;
		btn = new JButton("Browse ...");
		btn.setActionCommand("browse");
		btn.addActionListener(new BtnActionListener());
		inputPanel.add(btn, BorderLayout.EAST);
		
		entryPanel.add(lbl, BorderLayout.NORTH);
		entryPanel.add(inputPanel, BorderLayout.SOUTH);
		
		outText = new JTextArea(20, 40);
		JScrollPane scroller = new JScrollPane(outText);
		scroller.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new BoxLayout(cmdPanel, BoxLayout.X_AXIS));
		cmdPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		btn = new JButton("Clear");
		btn.setActionCommand("clear");
		btn.addActionListener(new BtnActionListener());
		cmdPanel.add(btn);

		cmdPanel.add(Box.createHorizontalGlue());

		btn = new JButton("Add");
		btn.setActionCommand("add");
		btn.addActionListener(new BtnActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Subtract");
		btn.setActionCommand("subtract");
		btn.addActionListener(new BtnActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Divide");
		btn.setActionCommand("divide");
		btn.addActionListener(new BtnActionListener());
		cmdPanel.add(btn);

		btn = new JButton("Multiply");
		btn.setActionCommand("multiply");
		btn.addActionListener(new BtnActionListener());
		cmdPanel.add(btn);

		if (isExtended) {
			btn = new JButton("Average");
			btn.setActionCommand("average");
			btn.addActionListener(new BtnActionListener());
			cmdPanel.add(btn);
		}

		add(entryPanel, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);
		add(cmdPanel, BorderLayout.SOUTH);
	}

	private ArrayContext createServiceContext(String userLine) {
		context = new ArrayContext("arithmetic");
		String data[] = Util.getTokens(userLine, " ");
		try {
			for (int i = 0; i < data.length; i++) {
				context.iv(i, new Double(data[i]));
				context.ivc(i, "user input");
			}
			logger.info("input service context: " + context);
			outText.setText("");
			outText.setText("" + context);
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
			NetSignature method = new NetSignature(
					selector, "sorcer.arithmetic.ArithmeticRemote");
			// specify task name, description and methods
			task = new NetTask(selector, selector
					+ " multiple float-point numbers", method);
			// assign service context for the task
			task.setConditionalContext(context);
		} catch (ContextException e) {
			e.printStackTrace();
		}
		return task;
	}

	class BtnActionListener implements ActionListener {
		public BtnActionListener() {
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
				outText.setText("Input Service Conext\n"
						+ createServiceContext(inField.getText()));
				return;
			} else if (selector.equals("browse")) {
				// TODO open a filedialog window and select the input file
				JFileChooser fc = new JFileChooser();
			    
			    // Show open dialog; this method does not return until the dialog is closed
			    fc.showOpenDialog(inField.getParent().getParent());
			    File selFile = fc.getSelectedFile();
			    
			    try {
			    	FileReader fr = new FileReader(selFile);
			    	BufferedReader br = new BufferedReader(fr);

			    	try {
			    		inField.setText(br.readLine());
			    	} catch (IOException e) {
			    		inField.setText("File cannot be read");
			    	}
 			    } catch (FileNotFoundException e) {
			    	inField.setText("File not found");
			    }
			}
			try {
				NetTask task = null;
				if (isRemote && server != null)
					task = (NetTask) serverService(createTask(inField
							.getText()));
				else if (isProvider && isRemote)
					task = (NetTask) ((Service) provider)
							.service(createTask(inField.getText()), null);
				else if (smartProxy != null)
					task = (NetTask) smartService(createTask(inField
							.getText()));
				else if (isExtended) {
					task = createTask(inField.getText());
					Context cxt = partner.average(task.getContext());
					task.setConditionalContext(cxt);
				} else {
					task = (NetTask) beanService(createTask(inField
							.getText()));
				}

				outText.append("Output Service Context\n" + task.getContext());
			} catch (Exception ex) {
				logger.throwing(ArithmeticUI.class.getName(),
						"actionPerformed", ex);
			}
		}
	}

	/**
	 * Invokes operations of the ArithemeticRemote interface on the remote
	 * server that is not of the Provder type.
	 * 
	 * @param exertion
	 *            a service task for the unform S2S case
	 * @return service task
	 * @throws RemoteException
	 * @throws ExertionException
	 */
	public Exertion serverService(Exertion exertion) throws RemoteException,
			ExertionException {
		// execute task directly using RemoteArithmetic
		Context result = null;
		String selector = exertion.getProcessSignature().getName();
		if (selector.equals(Arithmetic.ADD))
			result = server.add(exertion.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = server.subtract(exertion.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = server.multiply(exertion.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = server.divide(exertion.getContext());

		logger.info("serverService:result: " + result);
		((ServiceExertion) exertion).setConditionalContext(result);
		return exertion;
	}

	/**
	 * Invokes local operations of the Arithemetic interface of samrt proxy
	 * 
	 * @param exertion
	 *            a service task for the unform S2S case
	 * @return service task
	 * @throws RemoteException
	 * @throws ExertionException
	 */
	public NetTask smartService(NetTask task) throws RemoteException,
			ExertionException {
		// execute task locally
		Context result = null;
		String selector = task.getProcessSignature().getName();
		if (selector.equals(Arithmetic.ADD))
			result = smartProxy.add(task.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = smartProxy.subtract(task.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = smartProxy.multiply(task.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = smartProxy.divide(task.getContext());

		logger.info("smartService:result: " + result);
		task.setConditionalContext(result);
		return task;
	}

	public Exertion beanService(Exertion exertion) throws RemoteException,
			ExertionException {
		// execute task locally
		Context result = null;
		String selector = exertion.getProcessSignature().getName();
		if (selector.equals(Arithmetic.ADD))
			result = ((Adder) provider).add(exertion.getContext());
		else if ((selector.equals(Arithmetic.SUBTRACT)))
			result = ((Subtractor) provider).subtract(exertion.getContext());
		else if ((selector.equals(Arithmetic.MULTIPLY)))
			result = ((Multiplier) provider).multiply(exertion.getContext());
		else if ((selector.equals(Arithmetic.DIVIDE)))
			result = ((Divider) provider).divide(exertion.getContext());

		logger.info("beanService:result: " + result);
		((ServiceExertion) exertion).setConditionalContext(result);
		return exertion;
	}

	/**
	 * Returns a service UI descriptorfor this service. Usally this method is
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
