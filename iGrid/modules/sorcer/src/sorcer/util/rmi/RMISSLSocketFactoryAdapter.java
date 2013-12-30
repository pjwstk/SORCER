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

package sorcer.util.rmi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * RMIClientSocket factory yielding an SSL socket.
 */

public class RMISSLSocketFactoryAdapter implements RMIClientSocketFactory,
		RMIServerSocketFactory, Serializable {
	private SSLSocketFactory csf = (SSLSocketFactory) SSLSocketFactory
			.getDefault();
	private transient SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory
			.getDefault();

	/**
	 * @param target
	 *            Hostname of desired target
	 * @param port
	 *            Port number at target
	 * @exception IOException
	 *                on any network or I/O error
	 */
	public Socket createSocket(String target, int port) throws IOException {
		System.out.println("createSocket");
		SSLSocket s = (SSLSocket) csf.createSocket(target, port);
		System.out.println("got Socket");
		s.setEnabledCipherSuites(csf.getSupportedCipherSuites());
		return s;
	}

	/**
	 * @param port
	 *            Port number to listen at
	 * @exception IOException
	 *                on any network or I/O error
	 */
	public ServerSocket createServerSocket(int port) throws IOException {
		System.out.println("createServerSocket");
		SSLServerSocket s = (SSLServerSocket) ssf.createServerSocket(port);
		System.out.println("got ServerSocket");
		s.setEnabledCipherSuites(ssf.getSupportedCipherSuites());
		return s;
	}

	/** @return true iff objects are equal */
	public boolean equals(Object that) {
		// possibly a bit weak
		// return that instanceof RMISSLSocketFactoryAdapter;
		// better
		return that != null && that.getClass() == this.getClass();
	}
}

class SSLSocketFactoryAdapterTest extends Thread {
	ServerSocket serverSocket;

	SSLSocketFactoryAdapterTest(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
		setDaemon(true);
	}

	public void run() {
		for (;;) {
			try {
				Socket s = serverSocket.accept();
				System.out.println("Got a client on " + s);
				DataInputStream in = new DataInputStream(s.getInputStream());
				String request = in.readUTF();
				System.out.println("request=" + request);
				DataOutputStream out = new DataOutputStream(s.getOutputStream());
				out.writeUTF(request);
				out.flush();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}

	// test program
	public static void main(String[] args) throws Exception {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : 1097;
		RMISSLSocketFactoryAdapter pfsa = new RMISSLSocketFactoryAdapter();
		new SSLSocketFactoryAdapterTest(pfsa.createServerSocket(port)).start();
		Socket s = pfsa.createSocket("localhost", port);
		System.out.println("Connected to server via " + s);
		DataOutputStream out = new DataOutputStream(s.getOutputStream());
		out.writeUTF("Hello SSL!");
		out.flush();
		DataInputStream in = new DataInputStream(s.getInputStream());
		String reply = in.readUTF();
		System.out.println("reply=" + reply);
		s.close();
	}
}
