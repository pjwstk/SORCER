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
package sorcer.scaf.auth;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import javax.security.auth.x500.X500PrivateCredential;
import javax.swing.JOptionPane;

import sorcer.scaf.card.CardFactory;
import sorcer.scaf.card.JavaCard;
import sun.security.util.DerOutputStream;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

/**
 * <p>
 * CardLoginModule authenticates users with a password. This LoginModule checks
 * the user password using card supplied If user successfully authenticates
 * itself, a principal with the user's user name is added to the Subject. This
 * LoginModule has the debug option. If set to true in the login Configuration,
 * debug messages will be output to the output stream, System.out.
 * 
 * @see CardCallbackHandler
 * @see CardPasswordCallback
 * @author Saurabh Bhatla
 */
public class CardLoginModule implements LoginModule {
	// initial state
	/**
	 * Subject that is created when the user is logged in
	 */
	private Subject subject;
	/**
	 * Handler that handles all Callbacks specified
	 */
	private CallbackHandler callbackHandler;
	/**
	 * Map containing the shared state of the system
	 */
	private Map sharedState;
	/**
	 * Map containing login options
	 */
	private Map options;
	/**
	 * configurable option for debugging
	 */

	private boolean debug = false;
	// the authentication status
	/**
	 *the authentication status
	 */
	private boolean succeeded = false;
	/**
	 *the authentication status
	 */
	private boolean commitSucceeded = false;
	// username and password
	/**
	 * username from certificate data
	 */
	private String username;
	/**
	 *password
	 */
	private String password;
	/**
	 * JavaCard object to work with java card
	 */
	private JavaCard card;

	/**
	 * Certificate Path
	 */
	private java.security.cert.CertPath certP = null;
	/**
	 * User's Private Credentials
	 */
	private X500PrivateCredential privateCredential;
	/**
	 * Principal that goes with subject
	 */
	private javax.security.auth.x500.X500Principal principal;
	/**
	 * Default principal
	 */
	private javax.security.auth.x500.X500Principal default_principal;

	/**
	 * Initialize this LoginModule.
	 * 
	 * @param subject
	 *            the Subject to be authenticated.
	 * @param callbackHandler
	 *            a CallbackHandler for communicating with the end user
	 *            (prompting for user names and passwords, for example).
	 * @param sharedState
	 *            shared LoginModule state.
	 * @param options
	 *            options specified in the login Configuration for this
	 *            particular LoginModule.
	 */
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;
		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
		try {
			card = (JavaCard) CardFactory.getCard(CardFactory.JAVA_CARD);
		} catch (Exception e) {
			out("Exception in intialize(): " + e);
		}
	}

	/**
	 * Authenticate the user by getting the password from
	 * <code>CardCallbackHandler</code>.
	 * 
	 * @return true in all cases since this LoginModule should not be ignored.
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 * @exception LoginException
	 *                if this LoginModule is unable to perform the
	 *                authentication.
	 */
	public boolean login() throws LoginException {
		out("IN LOGIN------------------------->");
		// prompt for a user name and password
		if (callbackHandler == null)
			throw new LoginException("Error: no CallbackHandler available "
					+ "to garner authentication information from the user");
		Callback[] callbacks = new Callback[1];
		callbacks[0] = new CardPasswordCallback();
		while (true) {
			try {
				callbackHandler.handle(callbacks);

				// callbackHandler.handle(callbacks);

				password = ((CardPasswordCallback) callbacks[0]).getPassword();
				out("PASSWORD IS: " + password);

				((CardPasswordCallback) callbacks[0]).clearPassword();
			} catch (java.io.IOException ioe) {
				throw new LoginException(ioe.toString());
			} catch (UnsupportedCallbackException uce) {
				throw new LoginException(
						"Error: "
								+ uce.getCallback().toString()
								+ " not available to garner authentication information from the user");
			}
			// print debugging information

			// verify the username/password

			boolean passwordCorrect = false;

			if (!card.getCardIn()) {
				JOptionPane.showMessageDialog(null, "Please Insert Card.",
						"Warning", JOptionPane.WARNING_MESSAGE);
				continue;
			}

			if (card.verifyPin(password)) {
				// authentication succeeded!!!
				passwordCorrect = true;
				out("\t\tAuthentication succeeded");
				succeeded = true;
				X509Certificate userCert = getCert();
				username = userCert.getSubjectDN().getName();
				out("User Is: " + username);
				return true;
			} else {
				// authentication failed -- clean out state
				out("\t\tAuthentication failed");
				succeeded = false;

				password = null;
				throw new FailedLoginException("Password Incorrect");

			}
		}
	}

	/**
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded). If this LoginModule's own authentication attempt
	 * succeeded (checked by retrieving the private state saved by the login
	 * method), then this method associates a principal, private credentials and
	 * public credentials with the Subject located in the LoginModule. If this
	 * LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 * 
	 * @exception LoginException
	 *                if the commit fails.
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {
			// add a Principal (authenticated identity)
			// to the Subject
			// assume the user we authenticated is the SamplePrincipal
			// userPrincipal = new SamplePrincipal(username);
			// if (!subject.getPrincipals().contains(userPrincipal))
			// subject.getPrincipals().add(userPrincipal);

			// out("\t\tAdded SamplePrincipal to Subject");

			// in any case, clean out state
			// username = null;
			// password = null;
			internalCommit();
			commitSucceeded = true;
			return true;
		}

	}

	/**
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed). If this LoginModule's own authentication
	 * attempt succeeded (checked by retrieving the private state saved by the
	 * login and commit methods), then this method cleans up any state that was
	 * originally saved.
	 * 
	 * @exception LoginException
	 *                if the abort fails.
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			if (password != null) {
				password = null;
			}
			principal = null;
			default_principal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}

		return true;
	}

	/**
	 * Logout the user. This method removes the principal that was added by the
	 * commit method.
	 * 
	 * @exception LoginException
	 *                if the logout fails.
	 * @return true in all cases since this LoginModule should not be ignored.
	 */
	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(principal);
		subject.getPrincipals().remove(default_principal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			password = null;
		}
		principal = null;
		default_principal = null;
		return true;
	}

	/**
	 * Retrieves the user's certificate from smart card.
	 * 
	 * @return user certificate
	 */
	public X509Certificate getCert() {
		try {
			byte[] cert = card.getCertificate(password);

			if (cert == null)
				return null;

			CertificateFactory certificatefactory = CertificateFactory
					.getInstance("X.509");
			Certificate certificate = certificatefactory
					.generateCertificate(new ByteArrayInputStream(cert));

			X509Certificate userCert = (X509Certificate) certificate;

			// out("Before: "+HexString.hexify(userCert.getPublicKey().getEncoded())
			// );
			return userCert;
		} catch (Exception e) {
			out("Exception in getCert() " + e);
			return null;
		}
	}

	/**
	 * Used by commit to sign proxy certificate and retrieve credentials from
	 * smart card.
	 * 
	 */
	public void internalCommit() {
		try {
			URL locn = this.getClass().getResource(
					"/sorcer/provider/bboard/config/keystore.client");
			String keystoreFile = "keystore.client";
			String caAlias = "DDDDDD";
			String certToSignAlias = "default";
			String newAlias = "mycert";
			final int VALIDITY = 365;

			// Begin by getting a password and reading in the keystore
			char[] password1 = new String("saurabh").toCharArray();
			out("Cert (" + certToSignAlias + ") password: ");
			char[] certPassword = new String("default").toCharArray();

			X509Certificate userCert = getCert();
			RSAPublicKey key = (RSAPublicKey) userCert.getPublicKey();
			out("------------>" + key);

			byte[] encoded = userCert.getEncoded();
			X509CertImpl caCertImpl = new X509CertImpl(encoded);
			X509CertInfo caCertInfo = (X509CertInfo) caCertImpl
					.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);

			X500Name issuer = (X500Name) caCertInfo.get(X509CertInfo.SUBJECT
					+ "." + CertificateIssuerName.DN_NAME);

			out("X500Name: " + issuer);

			// Read in the keystore
			FileInputStream input = new FileInputStream(keystoreFile);
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(input, password1);
			input.close();

			// Get the cert to be signed
			java.security.cert.Certificate cert = keyStore
					.getCertificate(certToSignAlias);
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(
					certToSignAlias, certPassword);
			encoded = cert.getEncoded();
			X509CertImpl certImpl = new X509CertImpl(encoded);
			X509CertInfo certInfo = (X509CertInfo) certImpl
					.get(X509CertImpl.NAME + "." + X509CertImpl.INFO);

			Date firstDate = new Date();
			out("-------------------->" + firstDate.getTime());
			out("-------------------->" + firstDate.getTime());
			out("-------------------->" + firstDate.getTime());
			Date lastDate = new Date(firstDate.getTime() + VALIDITY * 24 * 60
					* 60 * 1000L);
			CertificateValidity interval = new CertificateValidity(firstDate,
					lastDate);

			certInfo.set(X509CertInfo.VALIDITY, interval);

			// Make a new serial number
			certInfo.set(X509CertInfo.SERIAL_NUMBER,
					new CertificateSerialNumber(
							(int) (firstDate.getTime() / 1000)));

			// Set the issuer
			certInfo.set(X509CertInfo.ISSUER + "."
					+ CertificateSubjectName.DN_NAME, issuer);

			AlgorithmId algorithm = new AlgorithmId(
					AlgorithmId.md5WithRSAEncryption_oid);
			certInfo.set(CertificateAlgorithmId.NAME + "."
					+ CertificateAlgorithmId.ALGORITHM, algorithm);

			AlgorithmId algId = AlgorithmId.get("MD5withRSA");
			DerOutputStream deroutputstream = new DerOutputStream();
			DerOutputStream deroutputstream1 = new DerOutputStream();
			certInfo.encode(deroutputstream1);
			byte abyte0[] = deroutputstream1.toByteArray();
			algId.encode(deroutputstream1);

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(abyte0);
			byte[] hash = md.digest();

			byte[] signature = card.sign(hash, password);

			deroutputstream1.putBitString(signature);
			deroutputstream.write((byte) 48, deroutputstream1);
			byte signedCert[] = deroutputstream.toByteArray();

			X509CertImpl newCert = new X509CertImpl(signedCert);

			keyStore.setKeyEntry(newAlias, privateKey, certPassword,
					new java.security.cert.Certificate[] { newCert });

			// Store the keystore
			FileOutputStream output = new FileOutputStream(keystoreFile);
			keyStore.store(output, password1);
			output.close();

			FileOutputStream output1 = new FileOutputStream("mm.crt");
			output1.write(userCert.getEncoded());

			Certificate[] fromKeyStore;

			fromKeyStore = keyStore.getCertificateChain("mycert");
			if (fromKeyStore == null || fromKeyStore.length == 0
					|| !(fromKeyStore[0] instanceof X509Certificate)) {
				out("Error, NO CERTIFICATE COULD BE FOUND IN KEYSTORE");
			} else {
				java.util.LinkedList certList = new java.util.LinkedList();
				for (int i = 0; i < fromKeyStore.length; i++) {
					certList.add(fromKeyStore[i]);
				}
				CertificateFactory certF = CertificateFactory
						.getInstance("X.509");
				certP = certF.generateCertPath(certList);
			}
			String keyStoreAlias = "mycert";

			X509Certificate certificate = (X509Certificate) fromKeyStore[0];
			default_principal = new javax.security.auth.x500.X500Principal(
					certificate.getSubjectDN().getName());
			principal = new javax.security.auth.x500.X500Principal(userCert
					.getSubjectDN().getName());
			Key privateKey1 = keyStore.getKey(keyStoreAlias, certPassword);
			if (privateKey1 == null || !(privateKey1 instanceof PrivateKey)) {
				throw new FailedLoginException(
						"Unable to recover key from keystore");
			}

			privateCredential = new X500PrivateCredential(certificate,
					(PrivateKey) privateKey1, keyStoreAlias);
			out("principal=" + principal + "\n certificate="
					+ privateCredential.getCertificate() + "\n alias ="
					+ privateCredential.getAlias());

			if (subject.isReadOnly()) {
				out("Your subject is readonly ----------------------");
			} else {
				subject.getPrincipals().add(principal);
				subject.getPrincipals().add(default_principal);
				subject.getPublicCredentials().add(certP);
				subject.getPrivateCredentials().add(privateCredential);
				out("CREDENTIALS ADDED SUCCESSFULLY TO THE SUBJECT");
				out("The subject = " + subject);
			}

		} catch (Exception e) {
			out("Exception in internalCommit: " + e);
		}

	}

	/**
	 * Prints debug statements
	 * 
	 * @param String
	 *            to be displayed
	 */

	public void out(String str) {
		sorcer.scaf.card.Debug.out("CardLoginModule >> " + str);
	}
}
