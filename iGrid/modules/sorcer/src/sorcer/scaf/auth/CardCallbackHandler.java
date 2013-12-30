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

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import sorcer.scaf.card.Debug;

/**
 *<p>
 * <code>CardCallbackHandler</code> implements CallbackHandler and passes it to
 * underlying security services so that they may interact with the application
 * to retrieve specific authentication data, such as card passwords.
 * 
 *<p>
 * Underlying security services make requests for different types of information
 * by passing individual Callbacks to the <code>CardCallbackHandler</code>. The
 * <code>CardCallbackHandler</code>
 * 
 * implementation decides how to retrieve information depending on the Callbacks
 * passed to it. In SCAF the underlying service needs a password to authenticate
 * a user on card, it uses a <code>CardPasswordCallback</code>.
 * 
 * @see CardPasswordCallback
 *@author Saurabh Bhatla
 */
public class CardCallbackHandler implements CallbackHandler {

	/**
	 * user password
	 */
	String password;

	/**
	 * Constructor that initializes CallbackHandlet with password.
	 * 
	 * @param password
	 *            user password presented to the system
	 */

	public CardCallbackHandler(String password) {
		this.password = password;
		out("Password is: " + password);
	}

	/**
	 * Invoke an array of Callbacks.
	 * 
	 * @param callbacks
	 *            an array of Callback objects which contain the information
	 *            requested by an underlying security service to be retrieved or
	 *            displayed.
	 * @exception java.io.IOException
	 *                if an input or output error occurs.
	 * @exception UnsupportedCallbackException
	 *                if the implementation of this method does not support one
	 *                or more of the Callbacks specified in the callbacks
	 *                parameter.
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof CardPasswordCallback) {
				// prompt the user for sensitive information
				CardPasswordCallback pc = (CardPasswordCallback) callbacks[i];
				pc.setPassword(password);
			} else {
				throw new UnsupportedCallbackException(callbacks[i],
						"Unrecognized Callback");
			}
		}
	}

	/**
	 * Prints debug statements
	 * 
	 * @param string
	 *            to be displayed
	 */
	public void out(String str) {
		Debug.out("SmartCardCallbackHandler>> " + str);
	}

}
