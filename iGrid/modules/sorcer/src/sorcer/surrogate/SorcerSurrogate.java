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

package sorcer.surrogate;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sorcer.core.xml.StaxParser;
import sorcer.core.xml.XMLElement;
import sorcer.security.jaas.UsernamePasswordCallbackHandler;

/**
 * SorcerSurrogate.java is a generic surrogate dealing with 1. Receiving request
 * regarding registration of Surrogate from Mobile Clients 2. registering
 * Surrogate with Surrogate Host
 */
public class SorcerSurrogate extends HttpServlet {

	private String query;
	private String username;
	private char[] password;
	private Subject subject;

	private Hashtable elements;

	private String intermediateFile = null;

	private static final int DEPLOY_SURROGATE = 1;
	private static final int UNDEPLOY_SURROGATE = 2;
	private static final String DELIMITER = "##";

	private String propertyFilePath;

	private void loadProperties(HttpServletResponse res) {
		// load the properties form properties file
		Properties props = new Properties();
		File file;

		FileInputStream fin;
		String str;

		try {
			fin = new FileInputStream(propertyFilePath + File.separator
					+ "surrogate.properties");
			props.load(fin);
			fin.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			try {
				res.sendError(res.SC_BAD_REQUEST,
						"Unable to read Property file");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		str = props.getProperty("surrogate.task.filename", "task.xml");
		intermediateFile = str;
	}

	private void writeToFile(String filename, String data) {
		PrintWriter pw = null;

		try {
			pw = new PrintWriter(new FileOutputStream(filename));

			pw.write(data);
			pw.flush();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} finally {
			pw.close();
		}
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		propertyFilePath = config.getInitParameter("propertyFilePath");
		if (propertyFilePath == null)
			propertyFilePath = ".";

		System.out.println(getClass().getName() + ">>>>>>>> initParam = "
				+ propertyFilePath);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		System.out.println(getClass().getName() + ">>>>>>>> reached doPost()");

		ServletInputStream in = req.getInputStream();
		DataInputStream din = new DataInputStream(in);

		int length;
		length = req.getContentLength();

		if (length <= 0) {
			System.out.println("some problem with length");
			length = 1024;
		}

		byte[] data = new byte[length];
		if ((din.read(data) == -1)) {
			System.out.println("some problem with actual data");
			res.sendError(res.SC_BAD_REQUEST, "Unable to read parameters");
			return;
		}

		System.out.println(getClass().getName()
				+ ">>>>>>>> before reading parameters");

		String request = new String(data);
		int index = request.indexOf("<?xml");
		if (index != -1)
			request = request.substring(index);

		index = request.indexOf("\n0");
		if (index != -1)
			request = request.substring(0, index);

		System.out.println(getClass().getName() + ">>>>>>>> request = "
				+ request);

		try {
			parseRequest(res, request);

			System.out.println(getClass().getName() + ">>>>>>>> query = "
					+ query);

			if (query != null) {
				int idx = query.indexOf("##");

				if (idx == -1) {
					System.out.println(" Bad Request");
					res.sendError(res.SC_BAD_REQUEST,
							"Unable to read parameters");
				}

				String initData = query.substring(0, idx);
				String requestUrl = query.substring(idx + 2, query.length());

				if (verifyInitData(initData) && verifyURL(requestUrl)) {
					System.out.println(getClass().getName()
							+ ">>>>>>>> before invoking registerSurrogate()");
					registerSurrogate(initData, requestUrl);

					res.setContentType("text/html");
					res.setContentLength("Surrogate Registered".length());
					res.setStatus(res.SC_OK);

					OutputStream out = res.getOutputStream();
					out.write("Surrogate Registered".getBytes());
					out.flush();
					out.close();
				} else {
					System.out.println(" Bad Request");
					res.sendError(res.SC_BAD_REQUEST,
							"Either Cmd# or URL is messed up");
				}
			} else {
				System.out.println(" Bad Request");
				res.sendError(res.SC_BAD_REQUEST,
						"No query provided in the request");
			}
		} catch (BadRequestException bre) {
			System.out.println(" Bad Request");
			bre.printStackTrace();
			res.sendError(res.SC_BAD_REQUEST, "Bad Request Submitted");
		} catch (InvalidUserException iue) {
			System.out.println(" Invalid username or password");
			iue.printStackTrace();
			res.sendError(res.SC_BAD_REQUEST,
					"Either Username or password is incorrect");
		}
	}

	private void verifyUser() throws FailedLoginException, LoginException {
		username = (String) ((XMLElement) (elements.get("username"))).getData();
		password = ((String) ((XMLElement) (elements.get("password")))
				.getData()).toCharArray();

		System.out.println(">>>>>>Username" + username);
		System.out.println(">>>>>>password" + password);

		System.setProperty("portal.server",
				"http://www.sorcer.cs.ttu.edu:2036/sorcer/servlet/controller");

		Configuration cfg = new Configuration() {

			public AppConfigurationEntry[] getAppConfigurationEntry(
					String applicationName) {
				Hashtable options = new Hashtable();
				String key = "jgapp.jaas.PasswordLoginModule";
				String value = "required";
				options.put(key, value);

				return new AppConfigurationEntry[] { new AppConfigurationEntry(
						"jgapp.jaas.PasswordLoginModule",
						AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
						options) };
			}

			public void refresh() {
			}
		};

		Configuration.setConfiguration(cfg);

		LoginContext loginContext = new LoginContext("anyName",
				new UsernamePasswordCallbackHandler(username, password));

		loginContext.login();
		subject = loginContext.getSubject();
	}

	private void parseRequest(HttpServletResponse res, String request)
			throws BadRequestException, InvalidUserException {
		// try {
		if ("".equals(request) || request == null)
			throw new BadRequestException("Request not properly formed");

		request = request.trim();

		loadProperties(res);
		System.out.println("after load properties");
		writeToFile(intermediateFile, request);
		System.out.println("after writeToFile");

		// Parser Stuff
		StaxParser parser = new StaxParser(intermediateFile);

		elements = parser.parse();

		// verifyUser();

		int i = 0;

		XMLElement element = (XMLElement) elements.get("in-value" + i);
		if (element != null)
			query = element.getData().toString();
		// } catch(FailedLoginException fle) {
		// throw new InvalidUserException("Username or password did not match");
		// } catch(LoginException le) {
		// throw new InvalidUserException("Username or password didnot match");
		// }
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		System.out.println(getClass().getName() + ">>>>>>>> reached doGet()");
		doPost(req, res);
	}

	private boolean verifyInitData(String initData) {
		if (initData != null && !"".equals(initData))
			return true;

		return false;
	}

	private boolean verifyURL(String url) {
		if (url != null && !"".equals(url))
			return true;

		return false;
	}

	// private void registerSurrogate(String cmd, String url) throws IOException
	// {
	// URLConnection sock = null;
	// try {
	// System.out.println(getClass().getName() +
	// ">>>>>>>> reached registerSurrogate() jar-url = " + url);
	// // generate initialization data
	// ByteArrayOutputStream bos = new ByteArrayOutputStream();
	// DataOutputStream dos = new DataOutputStream(bos);
	// dos.writeLong(1100);
	// dos.writeShort(1927);
	// dos.writeShort(1927);
	// dos.writeUTF("Description");
	// dos.flush();
	// byte[] initData = bos.toByteArray();

	// // sock = new Socket(InetAddress.getByName(url));

	// URL servlet = new URL("http://neem.cs.ttu.edu:3006/surrogate/adapter");
	// sock = servlet.openConnection();

	// sock.setDoInput(true);
	// sock.setDoOutput(true);

	// // Don't use a cached version of URL connection.
	// sock.setUseCaches (false);
	// sock.setDefaultUseCaches (false);

	// sock.setRequestProperty("Content-Type", "text/html");

	// System.out.println("Registering surrogate: " +
	// "Description");

	// dos = new DataOutputStream(sock.getOutputStream());
	// dos.writeShort(1);
	// dos.writeShort(2);

	// boolean isURL = false;
	// int index = url.indexOf(':');
	// if (index > 0) { // could be a URL
	// String protocol = url.substring(0, index);
	// if (protocol.equalsIgnoreCase("http") ||
	// protocol.equalsIgnoreCase("ftp") ||
	// protocol.equalsIgnoreCase("tftp"))
	// {
	// isURL = true;
	// }
	// }

	// if (isURL) { // URL with supported protocols
	// dos.writeShort(url.length());
	// dos.writeBytes(url);
	// dos.writeInt(initData.length);
	// dos.write(initData, 0, initData.length);
	// dos.writeInt(0);
	// } else {
	// dos.writeShort(0); // set the length of surrogate URL as 0
	// dos.writeInt(initData.length);
	// dos.write(initData, 0, initData.length);
	// // write the JAR file out
	// File jarFile = new File(url);
	// FileInputStream fis = new FileInputStream(jarFile);
	// dos.writeInt((int)jarFile.length());
	// byte[] buff = new byte[1024];
	// int len = fis.read(buff);
	// while (len > 0) {
	// dos.write(buff, 0, len);
	// len = fis.read(buff);
	// }
	// }
	// dos.flush();

	// System.out.println(getClass().getName() +
	// ">>>>>>>> reached registerSurrogate(), before sock.getInputStream()");
	// DataInputStream in = new DataInputStream(sock.getInputStream());
	// } catch(java.net.MalformedURLException mue) {
	// mue.printStackTrace();
	// } finally{
	// // try {
	// // if (sock != null)
	// // sock.close();
	// // } catch(IOException e) {}
	// }
	// }

	private void registerSurrogate(String initData, String url)
			throws IOException {
		try {
			SurrogateHandler handler = new SurrogateHandler();
			SurrogateListener listener = new SurrogateListener();

			listener.start();
			System.out.println(getClass().getName()
					+ ">>>>>>>> before handler.register()");
			handler.register(url, url, listener.getPort(), initData);
		} catch (IOException e) {
			System.err.println("Failed to instantiate DeploySurrogate.");
			throw e;
		}
	}

	private class SurrogateListener extends Thread {
		ServerSocket srvSock;

		SurrogateListener() throws IOException {
			super("Surrogate listener");
			srvSock = new ServerSocket(0);
		}

		public int getPort() {
			return srvSock.getLocalPort();
		}

		public synchronized void interrupt() {
			if (isInterrupted())
				return; // only need to be interrupted once
			try {
				// ServerSocket.close() doesn't work on all platforms.
				new Socket(InetAddress.getLocalHost(), srvSock.getLocalPort())
						.close();
				srvSock.close();
			} catch (IOException e) {
			}
			super.interrupt();
		}

		public synchronized boolean isInterrupted() { // synchronized version
			return super.isInterrupted();
		}

		public void run() {
			while (!isInterrupted()) {
				try {
					Socket sock = srvSock.accept();
					if (isInterrupted())
						return;
					new TaskExecutor(sock).start();
				} catch (IOException e) {
					Thread.yield(); // give interrupt() a chance to finish
				}
			}
		}
	}

	private class TaskExecutor extends Thread {

		private DataInputStream dis;
		private DataOutputStream dos;
		private final Socket sock;

		TaskExecutor(Socket sock) {
			super("Task execution thread");
			setDaemon(true);
			this.sock = sock;
			try {
				dis = new DataInputStream(sock.getInputStream());
				dos = new DataOutputStream(sock.getOutputStream());
			} catch (IOException e) {
				if (System.getProperty("com.sun.jini.madison.debug") != null) {
					e.printStackTrace();
					System.err.println("Failed to get streams of socket: "
							+ sock);
				}
				interrupt();
			}
		}

		public synchronized void interrupt() {
			if (isInterrupted())
				return; // only need to be interrupted once
			try {
				sock.close();
			} catch (IOException e) {
			}
			super.interrupt();
		}

		public void run() {
			while (!isInterrupted()) {
				String instruction = null;
				long id = 0;
				try {
					id = dis.readLong();
					instruction = dis.readUTF();
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null) {
						e.printStackTrace();
						System.err.println("Failure while reading "
								+ "instruction from socket: " + sock);
					}
					// interrupt();
					return;
				}
				try {
					dos.writeUTF("Done: " + instruction + " *** "
							+ "form surrogate: " + id); // write ack back
					dos.flush();
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null) {
						e.printStackTrace();
						System.err.println("Failure while writing back "
								+ "result on socket: " + sock
								+ "\nfor surrogate: " + id);
					}
					interrupt();
					return;
				}
			}
		}
	}
}
