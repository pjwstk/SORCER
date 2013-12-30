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

import java.io.Serializable;

import javax.security.auth.callback.Callback;

import sorcer.scaf.card.Debug;

/**
 *<p>
 * CardPasswordCallback allows underlying security services the ability to
 * interact with a calling application to retrieve specific authentication data
 * such as passwords
 * 
 *<p>
 * CardPasswordCallback does not retrieve or display the information requested
 * by underlying security services. CardPasswordCallback simply provide the
 * means to pass such requests to applications, and for applications, if
 * appropriate, to return requested information back to the underlying security
 * services.
 * 
 * @see CardCallbackHandler
 * @author Saurabh Bhatla
 * 
 */
public class CardPasswordCallback implements Callback, Serializable {

	/**
	 * user password
	 */
	String password;

	/**
	 * Default Constructor
	 */
	public CardPasswordCallback() {

	}

	/**
	 * Returns user password
	 * 
	 *@return user password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets user password in Callback.
	 */
	public void setPassword(String pass) {
		password = pass;
	}

	/**
	 * Clears the password stored in Callback.
	 */
	public void clearPassword() {
		password = null;

	}

	/**
	 * Prints debug statements
	 * 
	 * @param String
	 *            to be displayed
	 */
	public void out(String str) {
		Debug.out("CardPasswordCallback >> " + str);
	}

}
