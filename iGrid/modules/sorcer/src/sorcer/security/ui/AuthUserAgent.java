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

import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;

abstract public class AuthUserAgent {

	private LoginContext loginContext;

	AuthUserAgent(String argv[]) throws ConfigurationException, LoginException,
			IOException {
		final Configuration config = ConfigurationProvider.getInstance(argv,
				getClass().getClassLoader());

		loginContext = (LoginContext) config.getEntry(
				SecureContentPane.COMPONENT_NAME, "loginContext",
				LoginContext.class, null);

		if (loginContext == null) {
			init(config);
		} else {
			loginContext.login();
			try {
				try {
					Subject.doAsPrivileged(loginContext.getSubject(),
							new PrivilegedExceptionAction() {
								public Object run() throws IOException,
										ConfigurationException {
									init(config);
									return null;
								}
							}, null);
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

	/**
	 * Created GUI and discover an instance of Service or create exertion.
	 * 
	 * @param config
	 * @throws ConfigurationException
	 */
	abstract protected void init(Configuration config)
			throws ConfigurationException;

}
