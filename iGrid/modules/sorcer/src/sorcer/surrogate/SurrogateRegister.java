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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;

import net.jini.surrogate.ip.Constants;

public class SurrogateRegister {

	private static final int MAX_MULTICAST_PKT_SIZE = 512;
	private static final int DEFAULT_MULTICAST_TTL = 2;
	private static final int REQUEST_INTERVAL = 1000 * 5;

	// multicast group on which host announcements are expected
	private InetAddress announcementAddress;
	private int announcementPort; // port for host announcements
	private InetAddress requestAddress; // multicast group for host requests
	private int requestPort; // port for host requests

	private static final int MAJOR_PROTOCOL_VERSION = 1;
	private static final int MINOR_PROTOCOL_VERSION = 1;
	private SurrogateHandler handler;
	private InetAddress deviceAddress;
	private HostPacketListener hostAnnouncementListener;
	private HostRequestSender hostRequestSender;
	private HostPacketListener hostResponseListener;
	private Random random;
	private ArrayList hosts; // discovered active surrogate hosts
	private ArrayList surrogates; // SurrogateRecs
	// max number of requests send out for one surrogate registration
	private int maxHostRequests;
	private boolean terminated;

	private Hashtable registeredHosts;

	public SurrogateRegister(SurrogateHandler handler)
			throws UnknownHostException, IOException {
		this.handler = handler;

		announcementAddress = InetAddress.getByName("224.0.1.174");

		announcementPort = 1927;
		if (announcementPort < 0 || announcementPort > 65535) {
			throw new IOException("Invalid host announcement port "
					+ "property: " + announcementPort);
		}

		requestAddress = InetAddress.getByName("224.0.1.173");

		requestPort = 1928;
		if (requestPort < 0 || requestPort > 65535)
			throw new IOException("Invalid host request port property: "
					+ requestPort);

		deviceAddress = InetAddress.getLocalHost();

		random = new Random();
		hosts = new ArrayList();
		surrogates = new ArrayList();

		DatagramSocket respSock = new DatagramSocket();
		hostResponseListener = new HostPacketListener(respSock);
		hostResponseListener.start();

		hostRequestSender = new HostRequestSender(hostResponseListener
				.getPort());
		hostRequestSender.start();

		MulticastSocket mSocket = new MulticastSocket(announcementPort);
		mSocket.joinGroup(announcementAddress);
		setInterface(mSocket);
		hostAnnouncementListener = new HostPacketListener(mSocket);
		hostAnnouncementListener.start();

		terminated = false;

		registeredHosts = new Hashtable();
	}

	// -----------------------------------
	// public methods
	// -----------------------------------

	public boolean isRegistered(HostRec hostRec, String surrogate) {
		Iterator iterator = registeredHosts.keySet().iterator();

		while (iterator.hasNext()) {
			HostRec key = (HostRec) iterator.next();
			String value = (String) registeredHosts.get(key);

			if (key.equals(hostRec) && value.equals(surrogate)) {
				return true;
			}
		}

		return false;
	}

	public void register(long id, String jarFile, String desc, int appPort,
			int monitorPort, String initData) {
		SurrogateRec surRec = new SurrogateRec(id, jarFile, desc, appPort,
				monitorPort, initData);
		boolean alreadyRegistered = false;

		while (hosts.size() > 0) {
			HostRec hostRec;
			synchronized (this) {
				int index = random.nextInt(hosts.size());
				hostRec = (HostRec) hosts.get(index);
				if (System.currentTimeMillis() - hostRec.lastAnnTime > 120000) { // if
																					// not
																					// received
																					// new
																					// announcement
																					// in
					// the past 2 minutes, discard the host
					hosts.remove(index);
					continue;
				}

				if (isRegistered(hostRec, initData)) { // if surrogate is
														// already registered to
														// this host
					hosts.remove(index);
					alreadyRegistered = true;
					continue;
				}

			}
			try {
				registerSurrogate(hostRec, surRec);
				handler.registrationDone(surRec.id);
				registeredHosts.put(hostRec, initData);
				return;
			} catch (IOException e) {
				synchronized (this) {
					int index = hosts.indexOf(hostRec);
					if (index >= 0)
						hosts.remove(index);
				}
				continue; // try next host
			}
		}

		if (alreadyRegistered) {
			return;
		}
		// no active host available, wait for next host announcement
		synchronized (this) {
			surrogates.add(surRec);
		}
	}

	public synchronized void terminate() {
		if (terminated)
			return; // only need to be terminated once

		hostAnnouncementListener.interrupt();
		hostRequestSender.interrupt();
		hostResponseListener.interrupt();
		surrogates.clear();
		hosts.clear();
		terminated = true;
	}

	// -----------------------------------
	// private methods
	// -----------------------------------

	/**
	 * Set the network interface to use for multicast, if the default is
	 * overridden.
	 */
	private static void setInterface(MulticastSocket sock) throws IOException {
		try {
			String iface = System.getProperty("com.sun.jini.madison."
					+ "examples.devices.ip." + "discovery.interface");
			if (iface != null)
				sock.setInterface(InetAddress.getByName(iface));
		} catch (SecurityException e) {
		}
	}

	private DatagramPacket marshalOutgoingHostRequest(InetAddress hostAddr,
			int reqPort, int respPort) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeShort(Constants.HOST_REQUEST);
		dos.writeShort(MAJOR_PROTOCOL_VERSION);
		dos.writeShort(MINOR_PROTOCOL_VERSION);
		String addr = deviceAddress.getHostAddress();
		dos.writeShort(addr.length());
		dos.writeBytes(addr);
		dos.writeShort(respPort);
		dos.flush();
		byte[] message = bos.toByteArray();
		if (message.length > MAX_MULTICAST_PKT_SIZE)
			throw new IllegalArgumentException("device address marshals "
					+ "too large");
		return new DatagramPacket(message, message.length, hostAddr, reqPort);
	}

	private void registerSurrogate(HostRec hostRec, SurrogateRec surRec)
			throws IOException {
		System.out.println(getClass().getName()
				+ ">>>>>>Entered registeredSurrogate()");
		Socket sock = null;
		try {
			// generate initialization data
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeLong(surRec.id);
			dos.writeShort(surRec.appPort);
			dos.writeShort(surRec.monitorPort);
			dos.writeUTF(surRec.description);
			dos.writeUTF(surRec.initData);
			dos.flush();
			byte[] initData = bos.toByteArray();

			sock = new Socket(hostRec.resp.getHostAddress(), hostRec.resp
					.getTCPPort());
			if (System.getProperty("com.sun.jini.madison.debug") != null) {
				System.err.println("Registering surrogate: "
						+ surRec.description);
			}
			dos = new DataOutputStream(sock.getOutputStream());
			dos.writeShort(MAJOR_PROTOCOL_VERSION);
			dos.writeShort(MINOR_PROTOCOL_VERSION);

			boolean isURL = false;
			int index = surRec.jarFile.indexOf(':');
			if (index > 0) { // could be a URL
				String protocol = surRec.jarFile.substring(0, index);
				if (protocol.equalsIgnoreCase("http")
						|| protocol.equalsIgnoreCase("ftp")
						|| protocol.equalsIgnoreCase("tftp")) {
					isURL = true;
				}
			}

			if (isURL) { // URL with supported protocols
				dos.writeShort(surRec.jarFile.length());
				dos.writeBytes(surRec.jarFile);
				dos.writeInt(initData.length);
				dos.write(initData, 0, initData.length);
				dos.writeInt(0);
			} else {
				dos.writeShort(0); // set the length of surrogate URL as 0
				dos.writeInt(initData.length);
				dos.write(initData, 0, initData.length);
				// write the JAR file out
				File jarFile = new File(surRec.jarFile);
				FileInputStream fis = new FileInputStream(jarFile);
				dos.writeInt((int) jarFile.length());
				byte[] buff = new byte[1024];
				int len = fis.read(buff);
				while (len > 0) {
					dos.write(buff, 0, len);
					len = fis.read(buff);
				}
			}
			dos.flush();
		} finally {
			try {
				if (sock != null)
					sock.close();
			} catch (IOException e) {
			}
		}
	}

	// -----------------------------------
	// private nested classes
	// -----------------------------------

	private class HostPacketListener extends Thread {

		private final DatagramSocket sock;

		HostPacketListener(DatagramSocket sock) {
			super("Host packet listener " + sock);
			setDaemon(true);
			this.sock = sock;
		}

		public int getPort() {
			return sock.getLocalPort();
		}

		public synchronized void interrupt() {
			if (isInterrupted())
				return; // only need to be interrupted once
			sock.close();
			super.interrupt();
		}

		public void run() {
			byte[] buf = new byte[MAX_MULTICAST_PKT_SIZE];
			DatagramPacket pkt = new DatagramPacket(buf, buf.length);

			while (true) {
				if (isInterrupted())
					break;
				pkt.setLength(buf.length);
				try {
					sock.receive(pkt);
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null) {
						e.printStackTrace();
						System.err.println("Host announcement/response "
								+ "receive failure, exiting"
								+ "HostPacketListener thread "
								+ "listening on: " + sock + ":"
								+ sock.getLocalPort());
					}
					break;
				}
				if (isInterrupted())
					break;
				if (System.getProperty("com.sun.jini.madison.debug") != null) {
					System.err
							.println("Received host announcement/response "
									+ "from: " + pkt.getAddress() + ":"
									+ pkt.getPort());
				}
				IncomingHostResponse resp = null;
				try {
					resp = new IncomingHostResponse(pkt);
				} catch (IOException e) {
					if (System.getProperty("com.sun.jini.madison.debug") != null
							&& !e.getMessage().equals("Wrong type.")) {
						e.printStackTrace();
						System.err.println("Failed to parse the received "
								+ "host announcement/response!");
					}
					continue;
				}
				HostRec hostRec = new HostRec(resp, System.currentTimeMillis());
				ArrayList surs = null;
				synchronized (SurrogateRegister.this) {
					int index = hosts.indexOf(hostRec);
					if (index < 0) {
						hosts.add(hostRec);
					} else {
						hosts.set(index, hostRec);
					}
					surs = surrogates;
					surrogates = new ArrayList();
				}
				// register all surrogates to the newly discovered host
				for (int i = 0; i < surs.size(); i++) {
					SurrogateRec surRec = (SurrogateRec) surs.get(i);
					try {
						registerSurrogate(hostRec, surRec);
						handler.registrationDone(surRec.id);
					} catch (IOException e) {
						synchronized (SurrogateRegister.this) {
							surrogates.add(surRec); // queue for next announce
						}
					}
				}
			}
		}
	}

	private class IncomingHostResponse {
		private InetAddress hostAddress;
		private int tcpPort;
		private int udpPort;

		/** Parse a response message from a host. */
		IncomingHostResponse(DatagramPacket pkt) throws IOException {
			byte[] bytes = pkt.getData();
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					bytes));

			short type = dis.readShort();
			if ((type != Constants.HOST_RESPONSE)
					&& (type != Constants.HOST_ANNOUNCEMENT)) {
				throw new IOException("Wrong type.");
			}
			short majorVersion = dis.readShort();
			if (majorVersion != MAJOR_PROTOCOL_VERSION)
				throw new IOException("unsupported major protocol version: "
						+ majorVersion);

			short minorVersion = dis.readShort();
			if (minorVersion < MINOR_PROTOCOL_VERSION)
				throw new IOException("unsupported minor protocol version: "
						+ minorVersion);

			// host address
			int addrLen = dis.readUnsignedShort();
			if (addrLen < 1 || (12 + addrLen) > pkt.getLength()) // check size
				throw new IOException("Malformed host announcement received.");
			if (addrLen > 0) {
				byte[] addrData = new byte[addrLen];
				dis.readFully(addrData);
				hostAddress = InetAddress.getByName(new String(addrData));
			} else {
				if (System.getProperty("com.sun.jini.madison.debug") != null)
					System.err.println("Host response with no host address!");
				throw new IOException("Host response with no host address!");
			}
			tcpPort = dis.readUnsignedShort();
			udpPort = dis.readUnsignedShort();
			dis.close();
		}

		public InetAddress getHostAddress() {
			return hostAddress;
		}

		public int getTCPPort() {
			return tcpPort;
		}

		public int getUDPPort() {
			return udpPort;
		}

		public boolean equals(Object o) {
			if (!(o instanceof IncomingHostResponse))
				return false;
			IncomingHostResponse resp = (IncomingHostResponse) o;
			return hostAddress.equals(resp.hostAddress)
					&& tcpPort == resp.tcpPort && udpPort == resp.udpPort;
		}
	}

	private class HostRequestSender extends Thread {

		private MulticastSocket msock;
		private final DatagramPacket reqPkt;

		HostRequestSender(int respPort) throws IOException {
			msock = new MulticastSocket(requestPort);
			msock.joinGroup(requestAddress);
			int ttl = Integer.getInteger(
					"com.sun.jini.madison.examples.devices.ip."
							+ "Multicast.TTL", DEFAULT_MULTICAST_TTL)
					.intValue();
			msock.setTimeToLive(ttl);
			setInterface(msock);
			reqPkt = marshalOutgoingHostRequest(requestAddress, requestPort,
					respPort);
		}

		public synchronized void interrupt() {
			if (isInterrupted())
				return; // only need to be interrupted once
			msock.close();
			super.interrupt();
		}

		public void run() {
			int maxHostRequests = Integer. // default to 3
					getInteger(
							"com.sun.jini.madison.examples.devices.ip."
									+ "maxHostRequests", 3).intValue();

			System.out.println(getClass().getName() + ">>>> maxHostRequests = "
					+ maxHostRequests);
			for (int i = 0; i < maxHostRequests; i++) {
				try {
					msock.send(reqPkt);
				} catch (IOException e) {
				}
				if (isInterrupted())
					break;
				try {
					Thread.sleep(REQUEST_INTERVAL);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	private class HostRec {

		public IncomingHostResponse resp;
		public long lastAnnTime;

		HostRec(IncomingHostResponse resp, long lastAnnTime) {
			this.resp = resp;
			this.lastAnnTime = lastAnnTime;
		}

		public boolean equals(Object o) {
			if (!(o instanceof HostRec))
				return false;
			return resp.equals(((HostRec) o).resp);
		}
	}

	private class SurrogateRec {

		public long id;
		public String jarFile;
		public int appPort;
		public int monitorPort;
		public String description;
		public String initData;

		SurrogateRec(long id, String jarFile, String desc, int appPort,
				int monitorPort, String initData) {
			this.id = id;
			this.jarFile = jarFile;
			this.description = desc;
			this.appPort = appPort;
			this.monitorPort = monitorPort;
			this.initData = initData;
		}
	}
}
