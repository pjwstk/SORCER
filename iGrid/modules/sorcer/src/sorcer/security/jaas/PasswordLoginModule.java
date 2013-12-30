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

package sorcer.security.jaas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import sorcer.security.util.SorcerPrincipal;
import sorcer.util.Log;
import sorcer.util.SorcerUtil;
import sun.security.x509.X509CertImpl;

/**
 * Login module that checks a username and password. It uses user.home key store
 * for creating X500Principal ans based on username/password authentication a
 * GAppPrincipal.
 */
public class PasswordLoginModule implements LoginModule {
	private Subject subject;

	private CallbackHandler callbackHandler;

	private boolean loginSucceeded = false;

	private boolean commitSucceeded = false;

	private String username;

	private char[] password;

	private char[] keyStorePassword;

	private char[] keyPassword;

	private SorcerPrincipal principal;

	private X500Principal x500Principal;

	private KeyStore keyStore;

	private static Logger logger = Log.getSecurityLog();

	/**
	 * Initialize this login module.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		loginSucceeded = false;
		commitSucceeded = false;
		username = null;
		clearPasswords();
		if (callbackHandler == null) {
			this.callbackHandler = new UsernamePasswordCallbackHandler();
		}
		logger.info("Using callbackHandler: " + callbackHandler);
		// We do not support shared state or options so far.
	}

	/**
	 * Attempt to log a user in using a GApp protocol proxy.
	 */
	public boolean login() throws LoginException {

		if (callbackHandler == null) {
			throw new LoginException("No CallbackHandler defined");
		}

		// create four callbacks: one for username, one for user password,
		// and two for key store and key passwords
		Callback[] callbacks = new Callback[4];
		callbacks[0] = new NameCallback(
				UsernamePasswordCallbackHandler.USERNAME);
		callbacks[1] = new PasswordCallback(
				UsernamePasswordCallbackHandler.PASSWORD, false);
		callbacks[2] = new PasswordCallback(
				UsernamePasswordCallbackHandler.KEYSTORE_PASSWORD, false);
		callbacks[3] = new PasswordCallback(
				UsernamePasswordCallbackHandler.KEY_PASSWORD, false);

		try {
			// Call the callback handler to fill out information
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();

			char[] tempPassword = ((PasswordCallback) callbacks[1])
					.getPassword();

			if (username == null || tempPassword == null)
				return false;

			password = new char[tempPassword.length];
			System.arraycopy(tempPassword, 0, password, 0, tempPassword.length);
			// Clear out the password in the callback
			((PasswordCallback) callbacks[1]).clearPassword();

			char[] tempKeyStorePassword = ((PasswordCallback) callbacks[2])
					.getPassword();
			if (tempKeyStorePassword == null)
				keyPassword = password;
			else {
				keyStorePassword = new char[tempPassword.length];
				System.arraycopy(tempKeyStorePassword, 0, keyStorePassword, 0,
						tempKeyStorePassword.length);
				// Clear out the password in the callback
				((PasswordCallback) callbacks[2]).clearPassword();
			}

			char[] tempKeyPassword = ((PasswordCallback) callbacks[3])
					.getPassword();
			if (tempKeyPassword == null)
				keyPassword = keyStorePassword;
			else {
				keyPassword = new char[tempPassword.length];
				System.arraycopy(tempKeyPassword, 0, keyPassword, 0,
						tempKeyPassword.length);
				// Clear out the password in the callback
				((PasswordCallback) callbacks[3]).clearPassword();
			}
		} catch (IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException(uce.toString());
		}

		logger.info("login>>username: " + username);
		logger.info("login>>password: " + SorcerUtil.arrayToString(password));
		logger.info("login>>keyStorePassword: "
				+ SorcerUtil.arrayToString(keyStorePassword));
		logger.info("login>>keyPassword: "
				+ SorcerUtil.arrayToString(keyPassword));

		// Now we need to check for the validity of the username and password.
		String keyStoreURL = null;
		try {
			// keyStoreURL = "file:///" +
			// Util.urlEncode(System.getProperty("user.home"))
			keyStoreURL = "file:///" + System.getProperty("user.home")
					+ File.separatorChar + "req.keystore";

			// load Keystore
			keyStore = null;
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream in = (new URL(keyStoreURL)).openStream();
			keyStore.load(in, keyStorePassword);
			in.close();

			Certificate cert = keyStore.getCertificate(username);
			logger.info("XXXXX Certificate for: " + username + " is: " + cert);
			X509Certificate certImpl = new X509CertImpl(cert.getEncoded());
			x500Principal = certImpl.getSubjectX500Principal();
			logger.info("XXXXX X509 Certificate for: " + username + " is: "
					+ x500Principal);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (x500Principal == null) {
			// Authentication failed. Clean up state and throw exception.
			logger.info("Could not login, provided keystore: " + keyStoreURL
					+ "\n does not contain entry =" + username);
			loginSucceeded = false;
			username = null;
			clearPasswords();
		} else {
			// username and password are correct.
			loginSucceeded = true;
		}
		return loginSucceeded;
	}

	private void getPrincipalCredentials(String alias, char[] keyStorePassword,
			char[] keyPassword) {
		if (subject == null) {
			logger.info("getPrincipalCredentials>>Subject is null");
			return;
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
				ArrayList certList = new ArrayList();
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
		X500PrivateCredential privateCredential = null;
		X509Certificate certificate = (X509Certificate) fromKeyStore[0];
		Key key = null;
		try {
			key = keyStore.getKey(alias, keyPassword);
			privateCredential = new X500PrivateCredential(certificate,
					(PrivateKey) key, alias);
		} catch (KeyStoreException kse) {
			logger.throwing(getClass().getName(), "getPrincipalCredentials",
					kse);
		} catch (NoSuchAlgorithmException nsae) {
			logger.throwing(getClass().getName(), "getPrincipalCredentials",
					nsae);
		} catch (UnrecoverableKeyException uke) {
			logger.throwing(getClass().getName(), "getPrincipalCredentials",
					uke);
		}

		if (subject.isReadOnly()) {
			logger.info("getPrincipalCredentials>>subject is readonly");
		} else {
			subject.getPublicCredentials().add(certP);
			subject.getPrivateCredentials().add(privateCredential);
		}

		Iterator iterator;

		// // Add the SPKI certificate to public credentials
		// SPKICertificate cert = SecurityUtil.getSPKICert(new
		// KeyPair(publicKey,
		// (PrivateKey)key), publicKey, true, new fi.hut.tcm.spki.impl.Tag());
		// subject.getPublicCredentials().add(cert);

		// net.jini.security.policy.DynamicPolicyProvider dp =
		// (net.jini.security.policy.DynamicPolicyProvider) java.security.Policy
		// .getPolicy();
		// dp.grant(this.getClass(), new Principal[] { x500Principal },
		// new Permission[] { new AuthenticationPermission(
		// "javax.security.auth.x500.X500Principal \"*\"",
		// "delegate") });

		// list all principals
		Set principals = subject.getPrincipals();
		iterator = principals.iterator();
		Principal p = null;
		while (iterator.hasNext()) {
			p = (Principal) iterator.next();
			logger.info("The Subject principal is = " + principal.getName());
		}

		// list all private keys
		Set privateCredentials = subject.getPrivateCredentials();
		PrivateKey privateKey = null;
		iterator = privateCredentials.iterator();
		while (iterator.hasNext()) {
			privateKey = ((X500PrivateCredential) iterator.next())
					.getPrivateKey();
			logger.info("The Subject privateKey is = " + privateKey);
		}

		// list all public keys
		Set publicCredentials = subject.getPublicCredentials();
		iterator = publicCredentials.iterator();
		Object credential;
		while (iterator.hasNext()) {
			credential = iterator.next();
			logger.info("The Subject public credential is = " + credential);
		}
	}

	/**
	 * This is called if all logins succeeded.
	 */
	public boolean commit() throws LoginException {
		if (loginSucceeded == false) {
			return false;
		}
		// Login succeeded, so create a Principal and add it to the Subject.
		principal = new SorcerPrincipal(username);

		if (!(subject.getPrincipals().contains(principal))) {
			subject.getPrincipals().add(principal);
		}

		if (x500Principal != null
				&& !(subject.getPrincipals().contains(x500Principal))) {
			subject.getPrincipals().add(x500Principal);
		}
		// If we wanted our Subject to contain our credentials,
		getPrincipalCredentials(principal.getName(), keyStorePassword,
				keyPassword);

		// Clear out the username and password.
		username = null;
		clearPasswords();
		commitSucceeded = true;
		return true;
	}

	/**
	 * Called if overall login failed.
	 */
	public boolean abort() throws LoginException {
		// If login failed, return false;
		if (loginSucceeded == false) {
			return false;
		} else if (loginSucceeded == true && commitSucceeded == false) {
			// Our login succeeded, but others failed.
			loginSucceeded = false;
			username = null;
			clearPasswords();
			principal = null;
		} else {
			// We committed, but someone else's failed.
			logout();
		}
		return true;
	}

	/**
	 * Logout the user.
	 */
	public boolean logout() throws LoginException {
		// Need to remove the principal from the Subject.
		subject.getPrincipals().remove(principal);
		loginSucceeded = false;
		commitSucceeded = false;
		username = null;
		clearPasswords();
		principal = null;
		return true;
	}

	/**
	 * Clear out the password.
	 */
	private void clearPasswords() {
		if (password != null) {
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';

			password = null;
		}

		if (keyStorePassword != null) {
			for (int i = 0; i < keyStorePassword.length; i++)
				keyStorePassword[i] = ' ';

			keyStorePassword = null;
		}

		if (password != null) {
			for (int i = 0; i < keyPassword.length; i++)
				keyPassword[i] = ' ';

			keyPassword = null;
		}
	}
}
