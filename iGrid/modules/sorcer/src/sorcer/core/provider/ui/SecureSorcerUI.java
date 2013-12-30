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

package sorcer.core.provider.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.x500.X500PrivateCredential;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.security.AuthenticationPermission;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;
import sorcer.core.Auditor;
import sorcer.core.context.ServiceContext;
import sorcer.security.jaas.UsernamePasswordCallbackHandler;
import sorcer.security.util.SorcerPrincipal;
import sorcer.security.util.SecurityUtil;
import fi.hut.tcm.spki.SPKICertificate;

/**
 * Implementation of a secure service UI.
 * 
 * @author Unknown
 * @author Abhijit
 * @author Max Berger
 * 
 */
public class SecureSorcerUI extends JFrame implements ActionListener {
	protected class AuditThread implements Runnable {
		ServiceContext ctx = null;

		String mesg = null;

		javax.security.auth.Subject subject = null;

		public AuditThread(javax.security.auth.Subject subject, String msg) {
			try {
				if (auditor == null) {
					auditor = getAuditor();
				}
				ServiceContext ctx1 = new ServiceContext("Auditor" + this);
				if (subject != null) {
					ctx1.putValue("subject", subject);
				}
				ctx1.putValue("message", msg);
				this.subject = subject;
				this.ctx = ctx1;
				this.mesg = msg;
				System.out.println("ctx=" + ctx);
			} catch (Exception ex) {
				System.out.println("Error in AuditThread");
				ex.printStackTrace();
			}
			// auditor.audit(ctx);
		}

		public Auditor getAuditor() {
			final int MAX_TRIES = 100;
			ServiceDiscoveryManager sdm = null;
			LookupCache lCache1 = null;
			final int SLEEP_TIME = 100;
			try {

				LookupDiscovery disco = new LookupDiscovery(
						LookupDiscovery.ALL_GROUPS);
				sdm = new ServiceDiscoveryManager(disco,
						new LeaseRenewalManager());
				lCache1 = sdm.createLookupCache(new ServiceTemplate(null,
						new Class[] { sorcer.service.Service.class }, null),
						null, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
			int tries = 0;
			while (tries < MAX_TRIES) {
				ServiceItem[] items = (lCache1.lookup(null, Integer.MAX_VALUE));
				for (int i = 0; i < items.length; i++) {
					if (items[i].service != null
							&& items[i].service instanceof Auditor) {
						System.out.println(tries + "" + items[i].service);
						System.out.println("GOT Auditor  SERVICE - SERVICE ID="
								+ items[i].serviceID);
						return (Auditor) items[i].service;
					}
				}
				tries++;
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
				}
			}
			// Util.debug(this, tries+"null");
			return null;
		}

		public void run() {
			try {
				auditor.audit(ctx);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	class AuthUI extends JFrame implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		JDialog jdialog;

		public AuthUI() {
			// this.super();
			System.out.println("Inside the AUTHUI constructor");
			setTitle("Login");
			// getContentPane().setLayout(new GridLayout(2,1));
			// getContentPane().add(getMainUI());
			// getContentPane().add(getBtnUI());
			jdialog = new JDialog(this, true);
			jdialog.getContentPane().setLayout(new GridLayout(2, 1));
			jdialog.getContentPane().add(getMainUI());
			JPanel pnl1 = new JPanel();
			pnl1.setLayout(new GridLayout(2, 1));
			pnl1.add(getDelegationUI());
			pnl1.add(getBtnUI());
			// jdialog.getContentPane().add(getDelegationUI());
			// jdialog.getContentPane().add(getBtnUI());
			jdialog.getContentPane().add(pnl1);
			jdialog.setTitle("Login");
			// this.start();
			// debug(this+"\r\r\r++++++++++++++++++++++++++++CALLING
			// SHOW()+++++++++++++++++++++++++++++++++++\r\r\r");
			show1();
		}

		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if ("Login".equals(cmd)) {
				String username = getUsrnamefld.getText();
				char[] password = getPasswdfld.getPassword();
				System.out.println("Login  Command" + "\n.." + username
						+ new String(password));
				try {
					System.out
							.println("Setting the current class laoder to the class's loader. from..."
									+ Thread.currentThread()
											.getContextClassLoader().toString());
					Thread.currentThread().setContextClassLoader(
							this.getClass().getClassLoader());
					System.out.println("The current class loader set to ..."
							+ Thread.currentThread().getContextClassLoader()
									.toString());

					LoginContext loginContext = new LoginContext("GAppLogin",
							new UsernamePasswordCallbackHandler(username,
									password));
					System.out.println();
					System.out.println();
					System.out.println("The class loader for this class = "
							+ this.getClass().getClassLoader().toString());
					System.out.println("The current class loader thread = "
							+ Thread.currentThread().getContextClassLoader()
									.toString());
					System.out.println();
					System.out.println();
					System.out.println("System class Loader:"
							+ (this.getClass().getClassLoader()
									.getSystemClassLoader()).toString());

					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();

					System.out.println();
					System.out.println();
					// System.out.println("Class Loader for LoginContext class"+
					// loginContext.getClassLoader().toString());
					System.out
							.println("Calling the LoginContext.login() ................................................");

					loginContext.login();
					System.out.println("----------------------");
					loggedSubject = loginContext.getSubject();
					System.out.println(loggedSubject);
					Set principals = loggedSubject.getPrincipals();
					Iterator iterator = principals.iterator();
					Principal principal = null;
					while (iterator.hasNext()) {
						principal = (SorcerPrincipal) iterator.next();
						debug("The principal is = " + principal.getName());
					}
					// Abhijit:: For the time being just pass both the password
					// Abhijit:: Later user shall have an option of specifing a
					// password for the key an keystore
					getKeyStoreSubject(username, new String(password),
							new String(password));

					// Create a SPKI Certificate with the user as issuer

					// -- Get the public key of the user
					Set publicCredentials = loggedSubject
							.getPublicCredentials();
					iterator = publicCredentials.iterator();
					int counter = 0;
					PublicKey publicKey = null;
					while (iterator.hasNext()) {
						if (counter < 1) {
							System.out
									.println("++++++++++++++++++++++++++++++ Before Public key ++++++++++++++++++++++++++++++");
							publicKey = ((X509Certificate) ((java.security.cert.CertPath) iterator
									.next()).getCertificates().get(0))
									.getPublicKey();
							System.out
									.println("++++++++++++++++++++++++++++++ After Public key ++++++++++++++++++++++++++++++");
						} else {
							break;
						}
					}
					// -- Get the private key of the user
					Set privateCredentials = loggedSubject
							.getPrivateCredentials();
					iterator = privateCredentials.iterator();
					PrivateKey privateKey = null;
					while (iterator.hasNext()) {
						privateKey = ((X500PrivateCredential) iterator.next())
								.getPrivateKey();
					}
					// -------
					// Add the spki certificate to private credentials
					SPKICertificate cert = SecurityUtil.getSPKICert(
							new KeyPair(publicKey, privateKey), publicKey,
							true, new fi.hut.tcm.spki.impl.Tag());
					System.out.println("==" + cert);
					// LinkedList certList = new LinkedList();
					// certList.add(cert);
					// SPKICertificateFactory certF =
					// SPKICertificateFactory.getInstance("X.509");
					// certP =
					// certF.generateCertPath(certList);
					loggedSubject.getPublicCredentials().add(cert);

					System.out.println("##################################");
					System.out.println("loggedSubject = " + loggedSubject);

					// SPKI Certificate created and put as user public
					// credentials
					// debug("\r\r\r\r\r\r\r\n\n\n\n----------------------------------GRANTING
					// POLICY ----------------------------------");
					net.jini.security.policy.DynamicPolicyProvider policy1 = (net.jini.security.policy.DynamicPolicyProvider) java.security.Policy
							.getPolicy();
					policy1
							.grant(
									this.getClass(),
									new Principal[] { loggedPrincipal },
									new Permission[] { new AuthenticationPermission(
											"javax.security.auth.x500.X500Principal  \"*\"",
											"delegate") });
					if (chbx.isSelected()) {
						allowDelegation = true;
					}
					if (loggedSubject != null) {
						// debug(this+"(((((((((((((((((((((((((((((((((((((((((((((
						// NOT
						// NULL)))))))))))))))))))))))))))))))))))))))))))))");
						hide();
						dispose();
					}

				}
				// catch LoginException and print out a dialogbox indicating
				// wrong usename and password
				catch (Exception ex) {
					System.out
							.println("Error in the Login Context Login Method 12");
					ex.printStackTrace();
					JOptionPane.showMessageDialog(this,
							"Login Failed!, Try Again");
				}
			}

			if ("Cancel".equals(cmd)) {
				System.out.println("Cancel Command");
				dispose();
			}
		}

		public void show1() {
			// debug(this+" ISNIDE SHOW ");
			jdialog.pack();
			jdialog.show();
		}

		private JPanel getBtnUI() {
			System.out.println("Inside getBtnUI");
			JPanel tempPnl = new JPanel();
			loginBtn = new JButton("Login");
			cancelBtn = new JButton("Cancel");
			loginBtn.addActionListener(this);
			cancelBtn.addActionListener(this);
			tempPnl.add(loginBtn);
			tempPnl.add(cancelBtn);
			return tempPnl;
		}

		/*
		 * public synchronized void run(){ show(); pack();
		 * //requestFocusInWindow(); toFront(); }
		 */
		private JPanel getDelegationUI() {
			JPanel pnl = new JPanel();
			chbx = new JCheckBox("Allow Delegation", true);

			pnl.add(chbx);
			return pnl;
		}

		private void getKeyStoreSubject(String username, String password,
				String keyStorePassword) {
			String keyStoreURL = "file:"
					+ System.getProperty("user.home").replace(
							File.separatorChar, '\\') + '\\'
					+ "keystore.client";
			String trustStoreURL = "file:"
					+ System.getProperty("user.home").replace(
							File.separatorChar, '\\') + '\\'
					+ "truststore.client";
			// debug("getKeystoreSubject():: The URL for keystore file is =
			// "+keyStoreURL);
			String alias = username;
			// Subject subject = loggedSubject;
			if (loggedSubject == null) {
				// debug("getKeyStoreSubject:: loggedSubject was null");
				return;
			}
			KeyStore keyStore = null;

			// load Keystore
			try {
				keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
				InputStream in = (new URL(keyStoreURL)).openStream();
				keyStore.load(in, keyStorePassword.toCharArray());
				in.close();
			} catch (Exception ex) {
				System.out.println("Exception Occured in loading keystore");
				ex.printStackTrace();
			}
			// get certificate from keystore
			java.security.cert.Certificate[] fromKeyStore = null;
			java.security.cert.CertPath certP = null;

			try {
				fromKeyStore = keyStore.getCertificateChain(alias);
				if (fromKeyStore == null || fromKeyStore.length == 0
						|| !(fromKeyStore[0] instanceof X509Certificate)) {
					throw new FailedLoginException(
							"Unable to find X.509 certificate chain in keystore");
				} else {
					LinkedList certList = new LinkedList();
					for (int i = 0; i < fromKeyStore.length; i++) {
						certList.add(fromKeyStore[i]);
					}
					CertificateFactory certF = CertificateFactory
							.getInstance("X.509");
					certP = certF.generateCertPath(certList);
				}
			} catch (Exception ex) {
				// debug("problem in getting Certificate chain");
				ex.printStackTrace();
			}
			// get Principal and keys
			javax.security.auth.x500.X500Principal principal = null;
			javax.security.auth.x500.X500PrivateCredential privateCredential = null;
			try {
				X509Certificate certificate = (X509Certificate) fromKeyStore[0];
				principal = new javax.security.auth.x500.X500Principal(
						certificate.getSubjectDN().getName());
				Key privateKey = keyStore.getKey(alias, password.toCharArray()); // shall
				// be
				// changed
				// to
				// private
				// key
				// password
				if (privateKey == null || !(privateKey instanceof PrivateKey)) {
					throw new FailedLoginException(
							"Unable to recover key from keystore");
				}

				privateCredential = new X500PrivateCredential(certificate,
						(PrivateKey) privateKey, alias);
			} catch (Exception ex) {
				debug("problem in obtaining certificate and keys"
						+ ex.toString());
				ex.printStackTrace();
			}
			if (loggedSubject.isReadOnly()) {
				debug("subject is readonly ----------------------");
			} else {
				loggedPrincipal = principal;
				loggedSubject.getPrincipals().add(principal);
				loggedSubject.getPublicCredentials().add(certP);
				loggedSubject.getPrivateCredentials().add(privateCredential);
				// debug("CREDENTIALS ADDED SUCCESSFULLY TO THE SUBJECT");
				debug("The subject = " + principal);
			}
		}

		private JPanel getMainUI() {
			try {
				System.out.println("Inside getMainUI");
				JPanel tempPnl = new JPanel();
				tempPnl.setLayout(new GridLayout(2, 1));
				JPanel tmp1, tmp2;
				tmp1 = new JPanel();
				// getUsrnamefld = new JTextField(20);
				JLabel usrLbl = new JLabel("Username: ", SwingConstants.RIGHT);
				// tmp1.setLayout();
				tmp1.add(usrLbl);
				tmp1.add(getUsrnamefld);

				tmp2 = new JPanel();
				// getPasswdfld = new JPasswordField(20);
				// getPasswdfld.setEchoChar('*');
				JLabel passLbl = new JLabel("Password: ", SwingConstants.RIGHT);
				tmp2.add(passLbl);
				tmp2.add(getPasswdfld);

				tempPnl.add(tmp1);
				tempPnl.add(tmp2);
				return tempPnl;
			} catch (Exception ex) {
				// debug("error in mainUI");
				ex.printStackTrace();
				return null;
			}
		}
	}

	/**
	 * Required because JFrame is serializable.
	 */
	private static final long serialVersionUID = 1L;

	protected boolean allowDelegation = false;

	protected Auditor auditor;

	protected JCheckBox chbx;

	protected net.jini.config.Configuration config;

	protected boolean debug = true;

	protected JPasswordField getPasswdfld;

	protected JTextField getUsrnamefld;

	protected boolean gotSubject = false;

	protected Principal loggedPrincipal = null;

	protected javax.security.auth.Subject loggedSubject;

	protected JButton loginBtn, cancelBtn;

	protected Object preparedProxy;

	public SecureSorcerUI() {
		super();
		instantiate();
	}

	public SecureSorcerUI(Object obj) {
		super();
		loggedSubject = null;
		try {
			// auditor = AuditorUtil.getAuditor();
			// debug(" ------ In the 1st Constructor");
			URL clientCnfLocn = this.getClass().getResource(
					"config/preparer-minimal.config");
			// debug("this.getClass().getResource(\"config/preparer-minimal.config\"):
			// "+clientCnfLocn);
			String a[] = new String[1];
			a[0] = clientCnfLocn.toString();
			config = ConfigurationProvider.getInstance(a, this.getClass()
					.getClassLoader());
			if (config == null) {
				// debug("URL was = "+clientCnfLocn);
				// debug("config is null, Configuration.getInstance did not
				// work");
			} else {
				// debug("config is not null - "+
				// config.getEntry("client.ServiceUIProxyPreparer", "preparer",
				// ProxyPreparer.class, new BasicProxyPreparer()).toString());
			}
			// SecureSorcerUI();
			instantiate();
			// getAuthUI();
			prepareProxy(obj);
			getAuthUI();
			// Thread.sleep(1000);

		} catch (Exception ex) {
			// debug("Exception in 1st Constructor");
			ex.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
	}

	public Permission[] getGrants(Class cl, Principal[] principals) {
		return null;
	}

	public void grant(Class cl, Principal[] principals, Permission[] permissions) {

	}

	public boolean grantSupported() {
		return false;
	}

	public void instantiate() {
		// debug("Inside Instantiate");
		getUsrnamefld = new JTextField(20);
		// debug("Instantiated getUsernameField");
		JLabel usrLbl = new JLabel("Username: ", SwingConstants.RIGHT);
		getPasswdfld = new JPasswordField(22);
		// debug("Instantiated getPaswordField");
		getPasswdfld.setEchoChar('*');
		JLabel passLbl = new JLabel("Password: ", SwingConstants.RIGHT);

		// Set the classloader and everything so that further classes can be
		// downloaded

		URL url = this.getClass().getResource("config/jaas.config");
		/**
		 * need to read this file name from the jar file. Shouldnt be hardcoded
		 * *
		 */

		String resourceLocation = url.toString();
		// System.out.println("Resource for the config file:" +
		// this.getClass().getClassLoader().getResource("jaas.config"));
		System.setProperty("java.security.auth.login.config", resourceLocation);
		System.out.println("java.rmi.server.codebase " + resourceLocation);
		System.setProperty("java.rmi.server.codebase", resourceLocation);
		resourceLocation = (this.getClass()
				.getResource("config/keystore.client")).toString();
		String trustStoreURL = "file:"
				+ System.getProperty("user.home").replace(File.separatorChar,
						'\\') + '\\' + "truststore.client";
		// debug("\r\r trustStoreURL="+trustStoreURL);
		System.setProperty("javax.net.ssl.trustStore", resourceLocation);
		// System.out.println("java keystore = "+ resourceLocation);
		// System.setProperty("javax.net.ssl.keyStore", resourceLocation);

	}

	protected void debug(String msg) {
		if (debug) {
			System.out.println(this.getClass() + ": " + msg);
			/*
			 * try{ javax.security.auth.Subject subject =
			 * javax.security.auth.Subject
			 * .getSubject(AccessController.getContext()); Thread t = new
			 * Thread(new AuditThread(subject, msg)); t.start(); }catch
			 * (Exception ex){ ex.printStackTrace(); }
			 */

		}
	}

	protected void getAuthUI() throws Exception {

		AuthUI authUI = new AuthUI();
		// authUI.show();
		// authUI.pack();
		// Thread.currentThread().yield();

		// Thread t = new Thread(authUI);
		// t.start();
		// t.join();
		/*
		 * while(true){ if(loggedSubject!=null) return; Thread.sleep(10000); }
		 */
		/*
		 * AuthUI authUI = new AuthUI(); while(loggedSubject==null){
		 * debug("Showing Auth UI"); authUI.show(); authUI.pack(); }
		 */
	}

	protected Object getPreparedProxy() {
		return preparedProxy;
	}

	protected void prepareProxy(Object obj) {
		ProxyPreparer preparer = null;
		try {
			preparer = (ProxyPreparer) config.getEntry(
					"client.ServiceUIProxyPreparer", "preparer",
					ProxyPreparer.class, new BasicProxyPreparer());
		} catch (net.jini.config.ConfigurationException ce) {
			debug(" Configuration Exception Occured" + ce.toString());
			ce.printStackTrace();
			preparer = new BasicProxyPreparer();
		}
		try {
			java.security.Policy policy = java.security.Policy.getPolicy();
			if (policy == null) {
				debug("------- The Policy Object Was Null");
				// Abhijit:: If security is found null we can implement a
				// DynamicPolicyProvider
			} else {
				PermissionCollection permissions = policy.getPermissions(this
						.getClass().getProtectionDomain());
				debug("THE PERMISSIONS =\r " + permissions.toString());
				// Abhijit:: If security is not null, we can still set the new
				// Policy to DynamicPolicyProvider
			}
			net.jini.security.policy.DynamicPolicyProvider policy1 = new net.jini.security.policy.DynamicPolicyProvider();
			/*
			 * policy1.grant(this.getClass(), new Principal[]{new
			 * X500Principal("Abhijit".getBytes())}, new
			 * Permission[]{net.jini.security.AuthenticationPermission});
			 */
			java.security.Policy.setPolicy(policy1);
			java.security.Policy.getPolicy().refresh();
			PermissionCollection permissions = policy1.getPermissions(this
					.getClass().getProtectionDomain());
			// debug("THE PERMISSIONS =\r "+ permissions.toString());

			ServiceItem item = (ServiceItem) obj;
			/**
			 * Make more generic proxy Preparer, rather than calling
			 * item.service here
			 */
			preparedProxy = preparer.prepareProxy(item.service);
		} catch (Exception ex) {
			debug("Possible Remote Exception/ Security Exception in Authentication Permission"
					+ ex.toString());
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Warning! Service doesnt security constraints\n"
							+ ex.toString());
		}
	}

	protected void setPermission() {
		String targetName = ((loggedSubject.getPrincipals().iterator().next()))
				.getName();
		// debug("Set the Permission for = "+targetName);
		AuthenticationPermission p = new AuthenticationPermission(targetName,
				"connect");
	}
}
