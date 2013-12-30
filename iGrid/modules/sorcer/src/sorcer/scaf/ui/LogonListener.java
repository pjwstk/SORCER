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

import javax.security.auth.Subject;

/**
 * <p>
 * Listener that is required to be implemented by application classes that need
 * to use SCAF. It is used as channel to pass information from the SCAF
 * framework classes to application that is using SCAF.
 *<p>
 * Card password would be required by application classes to sign ServiceTask
 * and generate SignedServiceTask
 *<p>
 * Subject would be used by application classes to be sent in the
 * ServiceContext. This is subject holds the name of the user retrieved from the
 * card.
 * 
 * @author Saurabh Bhatla
 */
public interface LogonListener {
	/**
	 * Signalled when logon is done successfully.
	 * 
	 * @param password
	 *            of the user that was taken by SCAF and is required to be sent
	 *            to the application
	 *@param subject
	 *            that contains user name read from card
	 */
	public void logonDone(String password, Subject subject);
}
