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

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.swing.JFrame;

/**
 * CallbackHandler that handles usernames and passwords.
 * 
 * @author Mike SBoolewski
 */
public class UsernamePasswordCallbackHandler implements CallbackHandler {

	public static JFrame parentFrame = new JFrame();

	private String username;

	private char[] password;

	private char[] keyStorePassword;

	private char[] keyPassword;

	final static String USERNAME = "Username";
	final static String PASSWORD = "Password";
	final static String KEYSTORE_PASSWORD = "Key Store Password";
	final static String KEY_PASSWORD = "Key Password";

	public UsernamePasswordCallbackHandler() {
		// authenticate the user
		LoginDialog dialog = new LoginDialog(parentFrame, true);
		if (dialog.isDone()) {
			username = dialog.getUsername();
			password = dialog.getPassword();
			keyStorePassword = dialog.getKeyStorePassword();
			keyPassword = dialog.getKeyPassword();
		}
	}

	/**
	 * We need a stateful handler to return the username and password.
	 */
	public UsernamePasswordCallbackHandler(String username, char[] password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Handle each callback. One NameCallback and three PasswordCallbacks are
	 * used.
	 * 
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	public void handle(Callback[] callbacks)
			throws UnsupportedCallbackException {
		// Step through the callbacks
		if (callbacks.length != 4) {
			throw new UnsupportedCallbackException(null,
					"Unsupported number of Callbacks");
		}

		for (int i = 0; i < callbacks.length; i++) {
			Callback callback = callbacks[i];
			// Handle the callback based on the order of text fields in login
			// dialog.
			if ((callback instanceof NameCallback)
					&& ((NameCallback) callback).getPrompt() == UsernamePasswordCallbackHandler.USERNAME) {
				((NameCallback) callback).setName(username);
			} else if ((callback instanceof PasswordCallback)
					&& ((PasswordCallback) callback).getPrompt() == UsernamePasswordCallbackHandler.PASSWORD) {
				((PasswordCallback) callback).setPassword(password);
			} else if ((callback instanceof PasswordCallback)
					&& ((PasswordCallback) callback).getPrompt() == UsernamePasswordCallbackHandler.KEYSTORE_PASSWORD) {
				((PasswordCallback) callback).setPassword(password);
			} else if ((callback instanceof PasswordCallback)
					&& ((PasswordCallback) callback).getPrompt() == UsernamePasswordCallbackHandler.KEY_PASSWORD) {
				((PasswordCallback) callback).setPassword(password);
			} else {
				throw new UnsupportedCallbackException(callback,
						"Unsupported Callback Type");
			}
		}
	}

}
