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

package sorcer.util.dbac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sorcer.util.Stopwatch;

// This class will attempt to remake the connection with 
// the applications server every sleepTime seconds (by default).
public class RemakeConnection extends Thread {

	boolean serverState = false;
	Socket serverSocket;
	DataInputStream inStream = null;
	DataOutputStream outStream = null;
	String hostname;
	int port, sleepTime = 3000;
	boolean inStreamOn = true;

	// Takes hostname, port of current connection
	public RemakeConnection(String hostname, int port) {
		this(hostname, port, true);

	}

	// Takes hostname, port of current connection and boolean inStreamOn
	public RemakeConnection(String hostname, int port, boolean inStreamOn) {
		this.hostname = hostname;
		this.port = port;
		this.inStreamOn = inStreamOn;
		start();
	}

	// Allows the user to also set the delay time
	public RemakeConnection(String hostname, int port, int sleepTime) {
		this(hostname, port);
		this.sleepTime = sleepTime;
	}

	// The run method
	// This is the thread body that is attempting every sleepTime seconds
	// to remake the connection.
	public void run() {
		Stopwatch stopwatch = null;
		// if (Util.isDebugged) stopwatch = new Stopwatch();
		// Attempt to remake the connection
		while (!serverState) {
			System.err
					.println("RemakeConnection>>run: attempting to reconnect...");
			// Attempt to connect to a port
			try {
				// Open the connection to the server
				serverSocket = new Socket(hostname, port);

				// Attempt to open a client InputStream
				if (inStreamOn)
					inStream = new DataInputStream(serverSocket
							.getInputStream());
				outStream = new DataOutputStream(serverSocket.getOutputStream());
				// if we made it here, then state is ok
				serverState = true;
			} catch (IOException e) {
				serverSocket = null;
				System.err
						.println("RemakeConnection>>run: Still not able to connect");
			}

			// Still here, let's wait awhile (sleepTime)
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // end while
		// Util.debug(this, "RemakeConnection:run: " + stopwatch.get());
	}
}
