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
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

public class SurrogateMonitor {

	/** Default keep alive period, in milliseconds. */
	private long reregisterInterval;

	private SurrogateHandler handler;
	private InetAddress deviceAddress;
	private HashMap surrogates;
	private TreeMap surrogatesByTime;
	private SurrogateKeepAliveExpirationThread keepAliveExpirationThread;
	private HashMap threads; // keep alive msg processing threads
	private SurrogateKeepAliveMsgListener listener;
	private boolean terminated;

	public SurrogateMonitor(SurrogateHandler handler)
			throws UnknownHostException, IOException {
		this.handler = handler;
		deviceAddress = InetAddress.getLocalHost();
		surrogates = new HashMap();
		surrogatesByTime = new TreeMap();

		reregisterInterval = 1000L * 60;
		String prop = System.getProperty("com.sun.jini.madison.examples."
				+ "devices.ip.reregisterInterval");
		if (prop != null && prop.trim().length() > 0)
			reregisterInterval = Long.parseLong(prop.trim());

		keepAliveExpirationThread = new SurrogateKeepAliveExpirationThread();
		keepAliveExpirationThread.start();
		threads = new HashMap();
		listener = new SurrogateKeepAliveMsgListener();
		listener.start();
		terminated = false;
	}

	// -----------------------------------
	// public methods
	// -----------------------------------

	public void register(long id) {
		long expiration = System.currentTimeMillis() + reregisterInterval;
		SurrogateRec rec = new SurrogateRec(id, expiration);
		synchronized (this) {
			if (terminated || surrogates.get(new Long(id)) != null)
				return;
			surrogates.put(new Long(id), rec);
			surrogatesByTime.put(rec, rec);
			notifyAll();
		}
	}

	public int getPort() {
		return listener.getPort();
	}

	public synchronized void terminate() {
		if (terminated)
			return; // only need to be terminated once
		keepAliveExpirationThread.interrupt();
		listener.interrupt();
		for (Iterator iter = threads.values().iterator(); iter.hasNext();)
			((Thread) iter.next()).interrupt();
		threads.clear();
		surrogates.clear();
		surrogatesByTime.clear();
		terminated = true;
	}

	// -----------------------------------
	// private inner classes
	// -----------------------------------

	/** Surrogate keep alive expiration thread code */
	private class SurrogateKeepAliveExpirationThread extends Thread {

		/** Create a daemon thread */
		public SurrogateKeepAliveExpirationThread() {
			super("surrogate keep alive expiration thread");
			setDaemon(true);
		}

		public void run() {
			while (!isInterrupted()) {
				long now = System.currentTimeMillis();
				long minExpiration = Long.MAX_VALUE;
				while (true) {
					SurrogateRec rec = null;
					synchronized (SurrogateMonitor.this) {
						try {
							rec = (SurrogateRec) surrogatesByTime.firstKey();
							minExpiration = rec.expiration;
						} catch (NoSuchElementException e) {
							// empty list, wait until new surrogate registered
							minExpiration = Long.MAX_VALUE;
						}
						if (minExpiration > now) {
							try {
								SurrogateMonitor.this.wait(minExpiration - now);
							} catch (InterruptedException e) {
								return;
							}
							break;
						} else if (rec != null) {
							surrogates.remove(new Long(rec.id));
							surrogatesByTime.remove(rec);
						}
					}
					if (rec != null)
						handler.surrogateUnreachable(rec.id);
				}
			}
		}
	}

	private class SurrogateKeepAliveMsgListener extends Thread {

		private ServerSocket srvSocket;

		SurrogateKeepAliveMsgListener() throws IOException {
			super("keep alive message listener");
			setDaemon(true);
			srvSocket = new ServerSocket(0);
		}

		public int getPort() {
			return srvSocket.getLocalPort();
		}

		public synchronized void interrupt() {
			if (isInterrupted())
				return; // only need to be interrupted once
			try {
				// ServerSocket.close() doesn't work on all platforms.
				new Socket(deviceAddress, getPort()).close();
				srvSocket.close();
			} catch (IOException e) {
			}
			super.interrupt();
		}

		public void run() {
			while (!isInterrupted()) {
				try {
					Socket sock = srvSocket.accept();
					synchronized (SurrogateMonitor.this) {
						if (isInterrupted())
							break;
						Thread thread = new KeepAliveMsgProcessThread(sock);
						thread.start();
						threads.put(thread, thread);
					}
				} catch (IOException e) { /* ignore with impunity */
				}
			}
		}
	}

	private class KeepAliveMsgProcessThread extends Thread {

		private DataInputStream dis;
		private DataOutputStream dos;
		private final Socket sock;

		KeepAliveMsgProcessThread(Socket sock) {
			super("surrogate keep alive msg process thread");
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

		public void interrupt() {
			synchronized (this) {
				if (isInterrupted())
					return; // only need to be interrupted once
				try {
					sock.close();
				} catch (IOException e) {
				}
				super.interrupt();
			}
		}

		public synchronized boolean isInterrupted() {
			return super.isInterrupted();
		}

		public void run() {
			while (!isInterrupted()) {
				long id;
				long keepAlivePeriod = 0;
				try {
					id = dis.readLong(); // surrogate id
					keepAlivePeriod = dis.readLong();
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null) {
						e.printStackTrace();
						System.err.println("Failure while reading keep "
								+ "alive msg from socket: " + sock);
					}
					interrupt();
					return;
				}
				SurrogateRec rec = null;
				synchronized (SurrogateMonitor.this) {
					rec = (SurrogateRec) surrogates.get(new Long(id));
				}
				if (rec == null) {
					interrupt();
					return; // unknown surrogate or expired already
				}
				if (System.getProperty("com.sun.jini.madison.debug") != null) {
					System.err.println("Received keep alive message for "
							+ "surrogate: " + id + " at: "
							+ new Date(System.currentTimeMillis()));
				}
				synchronized (SurrogateMonitor.this) {
					surrogatesByTime.remove(rec);
					rec.expiration = System.currentTimeMillis()
							+ keepAlivePeriod;
					surrogatesByTime.put(rec, rec);
					SurrogateMonitor.this.notifyAll();
				}
				try {
					dos.writeLong(id); // write ack back
					dos.flush();
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null) {
						e.printStackTrace();
						;
						System.err.println("ACK failed for keep alive "
								+ "message from socket: " + sock);
					}
					interrupt();
					return;
				}
			}
		}
	}

	private class SurrogateRec implements Comparable {
		public long id;
		public long expiration;

		SurrogateRec(long id, long expiration) {
			this.id = id;
			this.expiration = expiration;
		}

		public int compareTo(Object o) {
			// has to be instance of SurrogateRec
			SurrogateRec rec = (SurrogateRec) o;
			if (expiration < rec.expiration)
				return -1;
			else if (expiration == rec.expiration)
				return 0;
			else
				return 1;
		}
	}
}
