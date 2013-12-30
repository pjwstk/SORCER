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

package sorcer.security.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.lookup.ServiceItem;
import net.jini.security.AuthenticationPermission;
import net.jini.security.BasicProxyPreparer;
import net.jini.security.ProxyPreparer;
import net.jini.security.policy.DynamicPolicyProvider;
import sorcer.core.Auditor;
import sorcer.util.Log;

/**
 * Implementation of a secure service UI pane. Can be to be used in association
 * with {@link SecureFrame).
 * 
 * @author Mike Sobolewski
 */
abstract public class SecureContentPane extends JPanel {

	final static private Logger logger = Log.getSecurityLog();

	final static public String COMPONENT_NAME = SecureContentPane.class
			.getName();

	public final static String JAAS_CONFIG = "configs/service-ui.login";

	public final static String JINI_CONFIG = "configs/service-ui.config";

	protected LoginContext loginContext;

	protected net.jini.config.Configuration config;

	protected boolean allowDelegation = false;

	protected Auditor auditor;

	protected JCheckBox chbx;

	protected boolean debug = true;

	protected JPasswordField getPasswdfld;

	protected JTextField getUsrnamefld;

	protected boolean gotSubject = false;

	protected Principal loggedPrincipal = null;

	// used in subclasses after being prepared
	protected Object preparedProxy;

	public SecureContentPane(final Object serviceItem)
			throws ConfigurationException, LoginException, IOException {

		Log.initializeSecurityLoggers();

		URL serviceUiConfigUrl = getClass().getResource(JINI_CONFIG);
		logger.info("SecureContentPane>>configs/service-ui.confg: "
				+ serviceUiConfigUrl);
		logger.info("configs/service-ui.confg expectec in: " + getClass());
		if (serviceUiConfigUrl != null) {
			String args[] = new String[] { serviceUiConfigUrl.toString() };
			config = ConfigurationProvider.getInstance(args, getClass()
					.getClassLoader());
		}

		if (config != null) {
			URL url = getClass().getResource(JAAS_CONFIG);
			String jassConfigPath = url.toString();
			logger.info("SecureContentPane>>configs/service-ui.login: "
					+ jassConfigPath);
			System.setProperty("java.security.auth.login.config",
					jassConfigPath);
			loginContext = (LoginContext) config.getEntry(COMPONENT_NAME,
					"loginContext", LoginContext.class, null);
			logger.info("SecureContentPane>>loginContext: " + loginContext);
			loginContext.login();
			try {
				final Subject subject = loginContext.getSubject();
				if (subject == null)
					throw new ConfigurationException(
							"SecureContentPane>>Not able to create Subject");
				try {
					Subject.doAsPrivileged(subject,
							new PrivilegedExceptionAction() {
								public Object run() throws IOException,
										ConfigurationException {
									init(serviceItem);
									return subject;
								}
							}, null); // AccessControlContext
				} catch (PrivilegedActionException e) {
					throw e.getCause();
				}
			} catch (IOException e) {
				throw e;
			} catch (ConfigurationException e) {
				throw e;
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable e) {
				throw (Error) e;
			}
		}
	}

	private void init(Object serviceItem) {
		prepareProxy(serviceItem);

		if (preparedProxy != null) {
			logger.info("Service UI prepared proxy:" + preparedProxy);
			// Schedule a job for the event-dispatching thread:
			// creating this application's service UI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JPanel contentPane = createContentPane();
					if (contentPane != SecureContentPane.this) {
						setLayout(new BorderLayout());
						add(contentPane, BorderLayout.CENTER);
					}
				}
			});
		}
		// inspect class loader tree
		// com.sun.jini.start.ClassLoaderUtil.displayContextClassLoaderTree();
		// com.sun.jini.start.ClassLoaderUtil.displayClassLoaderTree(provider
		// .getClass().getClassLoader());
	}

	/**
	 * Creates a proper Service UI content pane.
	 * 
	 * @param serviceItem
	 */
	abstract protected JPanel createContentPane();

	protected Object getPreparedProxy() {
		return preparedProxy;
	}

	protected void prepareProxy(Object obj) {
		ProxyPreparer preparer = null;
		if (config == null) {
			logger.severe("NO configuration for the requestor");
			return;
		}

		try {
			preparer = (ProxyPreparer) config.getEntry(COMPONENT_NAME,
					"preparer", ProxyPreparer.class, new BasicProxyPreparer());
		} catch (net.jini.config.ConfigurationException ce) {
			ce.printStackTrace();
			preparer = new BasicProxyPreparer();
		}
		try {
			java.security.Policy policy = java.security.Policy.getPolicy();
			if (policy != null) {
				PermissionCollection permissions = policy.getPermissions(this
						.getClass().getProtectionDomain());
				logger.info("Existing policy permissions =\n"
						+ permissions.toString());

				DynamicPolicyProvider dpolicy = new DynamicPolicyProvider();

				// Subject subject =
				// Subject.getSubject(AccessController.getContext());

				Set principals = loginContext.getSubject().getPrincipals();
				Principal[] ps = new Principal[principals.size()];
				Iterator it = principals.iterator();
				Permission[] aps = new Permission[ps.length];
				int i = 0;
				while (it.hasNext()) {
					ps[i] = (Principal) it.next();
					aps[i] = new AuthenticationPermission(
							"javax.security.auth.x500.X500Principal. \"CN="
									+ (ps[i]).getName() + "\"",
							"accept,connect,delegate");
					i++;
				}
				dpolicy.grant(this.getClass(), ps, aps);
				java.security.Policy.setPolicy(dpolicy);
				java.security.Policy.getPolicy().refresh();
				PermissionCollection dpermissions = dpolicy.getPermissions(this
						.getClass().getProtectionDomain());
				logger.info("New dynamic policy permissions =\n"
						+ dpermissions.toString());
			}

			ServiceItem item = (ServiceItem) obj;
			preparedProxy = preparer.prepareProxy(item.service);
			logger.info(">>>Preparred proxy: " + preparedProxy
					+ "\nwith preparer:" + preparer);
		} catch (Exception ex) {
			logger.info("prepareProxy>>>Service proxy not prepared");
			ex.printStackTrace();
			logger.throwing(getClass().getName(), "prepareProxy", ex);
		}
	}

	protected PermissionCollection getAuthPermissions() {
		String targetName;
		PermissionCollection pc = new Permissions();
		Iterator i = loginContext.getSubject().getPrincipals().iterator();
		while (i.hasNext()) {
			targetName = ((Principal) i.next()).getName();
			pc.add(new AuthenticationPermission(targetName, "connect"));
		}
		return pc;
	}
}
