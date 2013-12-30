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
import java.io.OutputStream;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * HTTPSServer
 * 
 * A very simple HTTPS Server, using the JSSE. It returns a success message over
 * HTTPS.
 * 
 * This version has been altered to require client authentication.
 * 
 * In order to run this example, you will need to set the following System
 * properties on the command-line:
 * 
 * javax.net.ssl.keyStore javax.net.ssl.keyStorePassword
 * javax.net.ssl.trustStore javax.net.ssl.trustStorePassword
 * 
 * Here's an example (enter all on one line):
 * 
 * java -Djavax.net.ssl.keyStore=.keystore
 * -Djavax.net.ssl.keyStorePassword=password
 * -Djavax.net.ssl.trustStore=.truststore
 * -Djavax.net.ssl.trustStorePassword=password HTTPSServer
 * 
 */
public class HTTPSServerWithAuth {

	public static void main(String[] args) throws IOException {

		// First we need a SocketFactory that will create
		// SSL server sockets.
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory
				.getDefault();
		SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(8443);

		// Require client authentication
		ss.setNeedClientAuth(true);

		// Keep on accepting connections forever
		while (true) {
			try {
				Socket s = ss.accept();

				// Get the input and output streams. These will be
				// encrypted transparently.
				OutputStream out = s.getOutputStream();
				BufferedReader in = new BufferedReader(new InputStreamReader(s
						.getInputStream()));

				// Read through the input from the client,
				// and display it to the screen.
				String line = null;
				while (((line = in.readLine()) != null) && (!("".equals(line)))) {
					System.out.println(line);
				}
				System.out.println("");

				// Construct a response
				StringBuffer buffer = new StringBuffer();
				buffer.append("<HTML>\n");
				buffer.append("<HEAD><TITLE>HTTPS Server</TITLE></HEAD>\n");
				buffer.append("<BODY>\n");
				buffer.append("<H1>Success!</H1>\n");
				buffer.append("</BODY>\n");
				buffer.append("</HTML>\n");

				// HTTP requires a content-length.
				String string = buffer.toString();
				byte[] data = string.getBytes();
				out.write("HTTP/1.0 200 OK\n".getBytes());
				out.write(new String("Content-Length: " + data.length + "\n")
						.getBytes());
				out.write("Content-Type: text/html\n\n".getBytes());
				out.write(data);
				out.flush();

				// Close the streams and socket.
				out.close();
				in.close();
				s.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
