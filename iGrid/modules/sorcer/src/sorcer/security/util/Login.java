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

package sorcer.security.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import sorcer.security.jaas.UsernamePasswordCallbackHandler;

public class Login {

	public static Subject login() {
		LoginContext loginContext = null;
		String username;
		char[] password;
		try {
			LineNumberReader stdinReader = new LineNumberReader(
					new BufferedReader(new InputStreamReader(System.in)));

			System.out.print("\nLogin:");
			username = stdinReader.readLine().trim();

			System.out.print("\nPassword: ");
			// password = stdinReader.readLine().trim().toCharArray();
			password = readPassword(stdinReader);

			loginContext = new LoginContext("SORCERLogin",
					new UsernamePasswordCallbackHandler(username, password));

			loginContext.login();
			// System.out.println("\nRequestor login succeeded");

		} catch (LoginException le) {
			System.out.println("\nRequestor login failed");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// Now we're logged in, so we can get the current subject.
		return loginContext.getSubject();
	}

	private static char[] readPassword(BufferedReader in) {
		Eraser eraser = new Eraser(System.out);
		eraser.start();
		char[] password = null;
		try {
			password = in.readLine().toCharArray();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		eraser.interrupt();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
		return password;
	}
}

class Eraser extends Thread {
	PrintStream out;

	boolean finish = false;

	public Eraser(PrintStream out) {
		this.out = out;
	}

	public void run() {
		out.print("\010 ");
		while (!finish) {
			out.print("\010 ");
			try {
				sleep(3);
			} catch (InterruptedException inte) {
				finish = true;
			}
		}
	}
}
