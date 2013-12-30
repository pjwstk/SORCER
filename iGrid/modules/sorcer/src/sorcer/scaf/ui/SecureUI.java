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

package sorcer.scaf.ui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.WindowConstants;

import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceItem;
import net.jini.security.AuthenticationPermission;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;
import sorcer.scaf.auth.CardCallbackHandler;
import sorcer.scaf.auth.CardLoginModule;

/**
 *<p>
 * This is the facade class that is exposed by SCAF to be extended by any
 * application that needs to use SCAF to provide authentication and
 * authorization in SORCER. It gets the proxy object downloaded from the
 * provider and loads the configuration files to be used to make communication
 * channel with the provider. It then checks all the constraints that are
 * specified in the configuration file and throws an exception if any of the
 * constraint is not met.
 *<p>
 * It uses prepare-minimal.config configuration file which requires Integerity
 * and Confidentiality constraints to be statisfied. It uses SSL channel to
 * provide integrity and confidentiality.
 *<p>
 * An inner class is used to present the user with the a frame that takes the
 * user password for the card. A subject is created using proxy certificates key
 * pair, which is signed by private key from the card. A user is gives three
 * chances to authenticate himself after which SCAF terminates.
 * 
 * 
 * @author Saurabh Bhatla
 * @see CardLoginModule
 *@see LogonFrame
 */
public class SecureUI extends JFrame {
	/**
	 *Configuration used to set constraints
	 */
	protected net.jini.config.Configuration config;
	/**
	 *Currently logged in subject
	 */
	protected Subject loggedSubject;
	/**
	 * Proxy received after verification
	 */
	protected Object preparedProxy;

	/**
	 * Returns subject that was created after successfull authentication
	 * 
	 * @return loggedSubject
	 */
	public Subject getSubject() {
		return loggedSubject;
	}

	/*
	 * public Permission[] getGrants(Class cl, Principal[] principals) { return
	 * null; }
	 * 
	 * public void grant(Class cl, Principal[] principals, Permission[]
	 * permissions) {
	 * 
	 * }
	 * 
	 * public boolean grantSupported() { return false; }
	 */

	/**
	 * Default Constructor. It uses prepare-minimal.config configuration file
	 * which requires Integerity and Confidentiality constraints to be
	 * statisfied.
	 * 
	 * @param proxy
	 *            object received from lookup service
	 */
	public SecureUI(Object obj) {

		super();

		try {

			URL clientCnfLocn = this.getClass().getResource(
					"/sorcer/provider/bboard/config/preparer-minimal.config");
			// debug("this.getClass().getResource(\"config/preparer-minimal.config\"): "+clientCnfLocn);
			String a[] = new String[1];
			a[0] = clientCnfLocn.toString();
			config = ConfigurationProvider.getInstance(a, this.getClass()
					.getClassLoader());
			if (config == null) {
				out("URL was = " + clientCnfLocn);
				// debug("config is null, Configuration.getInstance did not work");

			} else {
				out("config is not null - "
						+ config.getEntry("client.ServiceUIProxyPreparer",
								"preparer", ProxyPreparer.class,
								new BasicProxyPreparer()).toString());
			}

			final ClassLoader cl = this.getClass().getClassLoader();
			Thread.currentThread().setContextClassLoader(cl);
			init();

			prepareProxy(obj);
			// authenticate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Initializes SCAF
	 */
	public void init() {
		// Set the classloader and everything so that further classes can be
		// downloaded
		URL url = this.getClass().getResource(
				"/sorcer/provider/bboard/config/jaas.config");

		String resourceLocation = url.toString();

		System.setProperty("java.security.auth.login.config", resourceLocation);
		System.setProperty("java.rmi.server.codebase", resourceLocation);

		// resourceLocation =
		// (this.getClass().getResource("/sorcer/provider/bboard/config/keystore.client")).toString();
		// String trustStoreURL =
		// "file:"+System.getProperty("user.home").replace(
		// File.separatorChar, '\\') +
		// '\\' + "truststore.client";
		// out("\r\r trustStoreURL="+trustStoreURL);
		// System.setProperty("javax.net.ssl.trustStore", resourceLocation);
	}

	/**
	 * Prepares the proxy object by performing all validation that would be used
	 * to communicate with the provider
	 * 
	 * @param object
	 *            proxy received from lookup service
	 */
	protected void prepareProxy(Object obj) {
		ProxyPreparer preparer = null;
		try {
			preparer = (ProxyPreparer) config.getEntry(
					"client.ServiceUIProxyPreparer", "preparer",
					ProxyPreparer.class, new BasicProxyPreparer());
		} catch (net.jini.config.ConfigurationException ce) {
			// debug(" Configuration Exception Occured");
			System.out.println("-------------------............-------->");
			ce.printStackTrace();
			preparer = new BasicProxyPreparer();
		}
		try {
			/*
			 * java.security.Policy policy = java.security.Policy.getPolicy();
			 * if(policy==null) { //debug("------- The Policy Object Was Null");
			 * //Abhijit:: If security is found null we can implement a
			 * DynamicPolicyProvider } else { PermissionCollection permissions =
			 * policy.getPermissions(this.getClass().getProtectionDomain());
			 * //debug("THE PERMISSIONS =\r "+ permissions.toString());
			 * //Abhijit:: If security is not null, we can still set the new
			 * Policy to DynamicPolicyProvider }
			 * 
			 * net.jini.security.policy.DynamicPolicyProvider policy1 = new
			 * net.jini.security.policy.DynamicPolicyProvider();
			 *//*
				 * policy1.grant(this.getClass(), new Principal[]{new
				 * X500Principal("Abhijit".getBytes())}, new
				 * Permission[]{net.jini.security.AuthenticationPermission});
				 */
			/*
			 * java.security.Policy.setPolicy(policy1);
			 * java.security.Policy.getPolicy().refresh(); PermissionCollection
			 * permissions =
			 * policy1.getPermissions(this.getClass().getProtectionDomain());
			 * //debug("THE PERMISSIONS =\r "+ permissions.toString());
			 */
			ServiceItem item = (ServiceItem) obj;
			preparedProxy = preparer.prepareProxy(item.service);
		} catch (Exception ex) {
			// debug("Possible Remote Exception/ Security Exception in Authentication Permission");
			System.out.println("--------------------------->");

			ex.printStackTrace();
		}
	}

	/**
	 * Returns LogonFrame that will be used to take password for card
	 * 
	 * @param LogonListener
	 *            which will be notified when logon is done
	 */
	protected void getLogonFrame(LogonListener listener) {
		LoginFrame lFrame = new LoginFrame("Sorcer Authentication Frame",
				listener);
		lFrame.show();
		// authUI.pack();
	}

	/**
	 * Returns PreparedProxy
	 * 
	 * @return prepared proxy
	 */
	protected Object getPreparedProxy() {
		return preparedProxy;
	}

	/**
	 *Sets permission for the prinicipal that was logged in
	 */
	protected void setPermission() {
		String targetName = ((Principal) (loggedSubject.getPrincipals()
				.iterator().next())).getName();
		// debug("Set the Permission for = "+targetName);
		AuthenticationPermission p = new AuthenticationPermission(targetName,
				"connect");
	}

	/**
	 * 
	 *<p>
	 * An inner class of SecureUI which is used to present the user with the a
	 * frame that takes the user password for the card. A subject is created
	 * using proxy certificates key pair, which is signed by private key from
	 * the card. A user is gives three chances to authenticate himself after
	 * which SCAF terminates.
	 * 
	 * 
	 * @author Saurabh Bhatla
	 * @see CardLoginModule
	 */

	class LoginFrame extends JFrame implements ActionListener {

		private JLabel passwd_label;
		private JPanel login_panel;

		private JPasswordField passwd_textfield;
		private JButton submit_button;
		private JButton reset_button;
		// public JavaCard card;
		/**
		 * User password
		 */
		public String password;
		/**
		 * Listener that will be notified after logon is done
		 */
		public LogonListener listener;

		/**
		 *Constructor to create LogonFrame
		 * 
		 * @param title
		 *            of the Logon Form
		 *@param listener
		 *            that will be notified when the logon is done
		 */
		public LoginFrame(String title, LogonListener listener) {
			// final ClassLoader cl=this.getClass().getClassLoader();
			// Thread.currentThread().setContextClassLoader(cl);
			// card =new JavaCard();
			this.listener = listener;
			setTitle(title);
			setLocation(new Point(250, 250));
			initComponents();
		}

		/**
		 *Returns password entered by user
		 * 
		 * @return password entered
		 */
		public String getPassword() {
			return password;
		}

		/**
		 * Sets password entered by user
		 * 
		 * @param password
		 *            entered
		 */
		public void setPassword(String pass) {
			password = pass;
		}

		/**
		 * Initializes the graphical user interface components
		 */
		public void initComponents() {

			passwd_label = new JLabel();

			passwd_textfield = new JPasswordField();
			login_panel = new JPanel(null);
			reset_button = new JButton();
			submit_button = new JButton();

			getContentPane().setLayout(new FlowLayout());

			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setTitle("Sorcer Bulletin Board");
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent evt) {
					exitForm(evt);
				}
			});

			passwd_label.setText("Password :");
			login_panel.add(passwd_label);
			passwd_label.setBounds(60, 130, 70, 15);

			passwd_textfield.setEchoChar('*');
			passwd_textfield.setActionCommand("submit");
			passwd_textfield.addActionListener(this);
			login_panel.add(passwd_textfield);
			passwd_textfield.setBounds(150, 130, 100, 19);

			submit_button.setText("Submit");
			login_panel.add(submit_button);
			submit_button.setBounds(100, 170, 75, 25);
			submit_button.setActionCommand("submit");
			submit_button.addActionListener(this);

			reset_button.setText("Reset");
			login_panel.add(reset_button);
			reset_button.setBounds(200, 170, 68, 25);
			reset_button.setActionCommand("reset");
			reset_button.addActionListener(this);

			login_panel.setPreferredSize(new Dimension(370, 330));
			getContentPane().add(login_panel);

			pack();
		}

		/**
		 *Exits the Application
		 * 
		 * @param WindowEvent
		 *            which is generated when user exists form
		 */
		public void exitForm(WindowEvent evt) {
			// card.shutdown();
			// System.exit(0);
		}

		/**
		 * Signalled when user submits the form.
		 *<p>
		 * Perform validation on the length of password. It should be eight
		 * characters long.
		 * 
		 * @param ActionEvent
		 *            which is generated when submit button is pressed
		 */
		public void actionPerformed(ActionEvent event) {

			if ("reset".equals(event.getActionCommand())) {

				passwd_textfield.setText("");
			} else {
				password = new String(passwd_textfield.getPassword());
				if (!(password).trim().equals("") && !(password.length() < 8)) {

					/*
					 * try { //Semaphore semaphore=new Semaphore();
					 * semaphore.put(); } catch(Exception e) {
					 * System.out.println("Exception in actionPerformed() "+e);
					 * }
					 */
					this.hide();
					authenticate();
					// out("--------------GOT THE PASSWORD------------");
				} else {
					JOptionPane.showMessageDialog(this,
							"Length of Password should be 8 characters.",
							"Warning", JOptionPane.WARNING_MESSAGE);
					passwd_textfield.setText("");
				}
			}
		}

		private void authenticate() {
			LoginContext lc = null;
			try {
				Thread.currentThread().setContextClassLoader(
						this.getClass().getClassLoader());
				lc = new LoginContext("Card", new CardCallbackHandler(password));
			} catch (LoginException le) {
				System.err.println("Cannot create LoginContext.	"
						+ le.getMessage());
				System.exit(-1);
			} catch (SecurityException se) {
				System.err.println("Cannot create LoginContext. "
						+ se.getMessage());
				System.exit(-1);
			}
			// the user has 3attempts to authenticate successfully
			int i;

			try {
				// attempt authentication
				lc.login();

				loggedSubject = lc.getSubject();
				out("Logged Subject is: " + loggedSubject);
				listener.logonDone(password, loggedSubject);
				// break;
			} catch (LoginException le) {
				System.err.println(".......Authentication failed:");
				System.err.println(" " + le.getMessage());
				le.printStackTrace();
				try {
					Thread.currentThread().sleep(3000);
				} catch (Exception e) {
					// ignore
				}
				JOptionPane.showMessageDialog(this,
						"Incorrect Password, Enter Again.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				this.passwd_textfield.setText("");
				this.show();
			} catch (Exception e) {
				out("Exception in authenticate:" + e); // ignore
			}

			System.out.println("Authentication succeeded!");
			// System.exit(0);
		}

	}

	/**
	 * Prints debug statements
	 * 
	 * @param string
	 *            to be displayed
	 */
	public void out(String str) {
		sorcer.scaf.card.Debug.out("SecureUI>> " + str);
	}
}
