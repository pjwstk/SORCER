package sorcer.requestor.arithmetic.ssl.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.jini.config.ConfigurationException;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.signature.NetSignature;
import sorcer.security.ui.ContextRestorationWrapper;
import sorcer.security.ui.SecureContentPane;
import sorcer.service.ContextException;
import sorcer.service.Service;
import sorcer.service.SignatureException;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

/**
 * Componet Service UI for SORCER Arithmetic - an example service
 */
public class SecureArithmeticUI extends SecureContentPane implements SorcerConstants {

	private static final long serialVersionUID = -8924924634525391844L;

	private final static Logger logger = Log.getTestLog();

	private ArrayContext context;

	private String selector;

	private JTextField inField;

	private JTextArea outText;

	/**
	 * Creates new Arithetic UI Component
	 * 
	 * @throws IOException
	 * @throws ConfigurationException
	 * @throws LoginException
	 */
	public SecureArithmeticUI(Object obj) throws LoginException,
			ConfigurationException, IOException {
		super(obj);
		if (preparedProxy != null) {
			getAccessibleContext()
					.setAccessibleName("Secure Arithmetic Tester");
		}
		// inspect class loader tree
		// com.sun.jini.start.ClassLoaderUtil.displayContextClassLoaderTree();
		// com.sun.jini.start.ClassLoaderUtil.displayClassLoaderTree(provider
		// .getClass().getClassLoader());
	}

	protected JPanel createContentPane() {
		setLayout(new BorderLayout());
		JPanel entryPanel = new JPanel(new BorderLayout());
		entryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel lbl = new JLabel(
				"Enter  (space separated):");
		entryPanel.add(lbl, BorderLayout.NORTH);
		inField = new JTextField(40);
		inField.setActionCommand("context");
		inField.addActionListener(new BtnActionListener());
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
		btn.addActionListener(ContextRestorationWrapper
				.wrap(new BtnActionListener()));
		cmdPanel.add(btn);

		add(entryPanel, BorderLayout.NORTH);
		add(scroller, BorderLayout.CENTER);
		add(cmdPanel, BorderLayout.SOUTH);

		return this;
	}

	private ArrayContext createServiceContext(String userLine) {
		context = new ArrayContext("arithmetic");
		String data[] = SorcerUtil.getTokens(userLine, " ");
		try {
			for (int i = 0; i < data.length; i++) {
				context.iv(i, new Double(data[i]));
				context.ivc(i, "user input");
			}
			logger.info("input service context: " + context);
			outText.setText("");
			outText.setText("" + context);
		} catch (NumberFormatException nfe) {
			logger.throwing(getClass().getName(), "createServiceContext", nfe);
		} catch (ContextException ce) {
			logger.throwing(getClass().getName(), "createServiceContext", ce);
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
			logger.throwing(getClass().getName(), "createTask", e);
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
			}
			NetTask task = null;
			try {
				task = createTask(inField.getText());
				logger.info(">>>>>>>input task: " + task);
				task = (NetTask) ((Service) preparedProxy).service(task,
						null);
				logger.info(">>>>>>>output task: " + task);
			} catch (Exception ex) {
				logger.throwing(SecureArithmeticUI.class.getName(),
						"actionPerformed", ex);
			}
			outText.append("Output Service Context\n" + task.getContext());
		}
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
							+ "/ssl-arithmetic-ui.jar") },
							SecureArithmeticUI.class.getName()));
		} catch (Exception ex) {
			logger.throwing(SecureArithmeticUI.class.getName(),
					"getUIDescriptor", ex);
		}
		return uiDesc;
	}
}
