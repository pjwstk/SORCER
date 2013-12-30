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

package sorcer.core.provider.surrogate;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.jini.config.EmptyConfiguration;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import net.jini.lookup.JoinManager;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.surrogate.HostContext;
import net.jini.surrogate.Surrogate;
import net.jini.surrogate.ip.IPInterconnectContext;

import com.sun.jini.lookup.entry.BasicServiceType;
import com.sun.jini.start.NonActivatableServiceDescriptor;

/**
 * The SurrogateProvider for the SORCER environment. this serves as the base
 * class for all mobile device providers
 */
public class SurrogateProvider implements Surrogate {

	private static final long LEASE_DUR = 60 * 1000; // seconds

	private static final String PRODUCT = "Surrogate Service";
	private static final String MANUFACTURER = "SORCER LAB";
	private static final String VENDOR = MANUFACTURER;
	private static final String VERSION = "Madison 1.0";
	private static Entry[] SERVICE_ATTRS = new Entry[] {
			new ServiceInfo(PRODUCT, MANUFACTURER, VENDOR, VERSION, null, null),
			new BasicServiceType("Surrogate Service") };
	private HostContext hostContext;
	private IPInterconnectContext ipInterconnectContext;
	private long id;
	private Socket appSock;
	private DataInputStream appDis;
	private DataOutputStream appDos;
	private Socket monitorSock;
	private DataInputStream monitorDis;
	private DataOutputStream monitorDos;
	private String description; // a description about the device
	private ArrayList threads; // utility threads
	private String result; // instruction execution result
	private JoinManager joinManager;
	private ServiceID serviceID;
	private boolean deviceAlive;
	private boolean deactivated;
	private String providerDetails;

	private static final Logger logger = Logger
			.getLogger("sorcer.core.provider.SurrogateProvider");

	public SurrogateProvider() throws RemoteException {
		// do nothing
	}

	public void activate(HostContext hostContext, Object interconnectContext)
			throws Exception {

		this.hostContext = hostContext;
		ipInterconnectContext = (IPInterconnectContext) interconnectContext;

		InetAddress addr = ipInterconnectContext.getAddress();
		byte[] initData = ipInterconnectContext.getInitializationData();
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
				initData));
		try {
			// first field is a long id
			id = dis.readLong();
			// second field is a short for application port
			appSock = new Socket(addr, dis.readUnsignedShort());
			appDis = new DataInputStream(appSock.getInputStream());
			appDos = new DataOutputStream(appSock.getOutputStream());
			// third field is a short for monitor port
			monitorSock = new Socket(addr, dis.readUnsignedShort());
			monitorDis = new DataInputStream(monitorSock.getInputStream());
			monitorDos = new DataOutputStream(monitorSock.getOutputStream());
			// fourth a String description
			description = dis.readUTF() + " *** device assigned id: " + id;
			providerDetails = dis.readUTF();
		} catch (IOException e) {
			abortActivation("Failed to activate surrogate: ", e);
			return;
		}

		// threads = new ArrayList();

		// // keep alive monitor thread
		// threads.add(new Thread() {
		// public void run()
		// {
		// while (!Thread.interrupted()) {
		// try {
		// long renewID = monitorDis.readLong();
		// if (renewID == id) { // valid keep alive reply
		// synchronized (SurrogateProvider.this) {
		// deviceAlive = true;
		// }
		// }
		// } catch (IOException e) {
		// abortActivation("Failed to read keep alive msg " +
		// "in surrogate: ", e);
		// break;
		// }
		// }
		// }
		// });

		// for (int i=0; i<threads.size(); i++) {
		// ((Thread)threads.get(i)).start();
		// }

		// ipInterconnectContext.setKeepAliveHandler(new KeepAliveHandler() {
		// public synchronized void keepAlive(long period)
		// throws Exception
		// {
		// boolean abort;
		// synchronized (SurrogateProvider.this) {
		// abort = !deviceAlive;
		// // will be set to true when receive next response
		// deviceAlive = false;
		// }
		// if (abort) {
		// // device failed to respond during past period
		// abortActivation("Device failed to respond during " +
		// "last keep alive period.\n" +
		// "Deactivating the surrogate: ", null);
		// return;
		// }
		// try {
		// monitorDos.writeLong(id);
		// monitorDos.writeLong(period);
		// monitorDos.flush();
		// } catch (IOException e) {
		// abortActivation("Failed to send keep alive msg in " +
		// "surrogate: ", e);
		// }
		// return;
		// }
		// });

		System.out.println(getClass().getName()
				+ ">>>>>> providerDetails, after recent changes = "
				+ providerDetails);

		String codebase = "http://neem.cs.ttu.edu:2030/classes/scheduler-ui.jar:http://neem.cs.ttu.edu:2030/classes/scheduler-dl.jar";
		String policy = "/research/sorcer/policy/policy";
		String classpath = "/projects/users/rmalladi/projects/iGrid/lib/scheduler.jar";
		String implClassName = "sorcer.provider.scheduler.SchedulerProviderImpl";
		String config = "/projects/users/rmalladi/projects/iGrid/config/scheduler-server.config";

		/*
		 * String codebase =
		 * "http://neem.cs.ttu.edu:2030/classes/scheduler-dl.jar"; String policy
		 * = "/research/sorcer/policy/policy"; String classpath =
		 * "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/lib/scheduler.jar"
		 * ; String implClassName =
		 * "sorcer.provider.scheduler.SchedulerProviderImpl"; String config =
		 * "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/config/server.config"
		 * ;
		 */

		// String codebase =
		// "http://neem.cs.ttu.edu:2030/classes/cataloger-dl.jar";
		// String policy = "/research/sorcer/policy/policy";
		// String classpath =
		// "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/core/provider/catalog/lib/cataloger.jar";
		// String implClassName =
		// "sorcer.core.provider.cataloger.CatalogerImpl";
		// String config =
		// "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/core/provider/catalog/config/server.config";

		// Class surrogateClass =
		// Class.forName("com.sun.jini.madison.boot.HostBoot");
		Class surrogateClass = Class
				.forName("com.sun.jini.madison.boot.ExportCL");
		// // System.setProperty("java.class.path",
		// "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/core/provider/catalog/lib/cataloger.jar");
		// Class surrogateClass = this.getClass();
		ClassLoader loader = surrogateClass.getClassLoader();
		// // ClassLoader loader = ClassLoader.getSystemClassLoader();
		// // loader.loadClass("sorcer.core.provider.cataloger.CatalogerImpl");

		Thread thisThread = Thread.currentThread();
		thisThread.setContextClassLoader(loader);

		// System.setProperty("java.security.policy",
		// "/research/sorcer/policy/policy");
		// System.setProperty("java.util.logging.config.file",
		// "/projects/users/malladi/www/projects/iGrid/sorcer/src/sorcer/core/provider/catalog/bin/catalogerlogging.properties");

		// // String[] options = new String[]{
		// System.getProperty("java.util.logging.config.file") };
		// Configuration myConfig = ConfigurationProvider.getInstance(null,
		// loader);

		// System.out.println(getClass().getName() +
		// ">>>>>>system property set again with Surrogate host's classloader" +
		// loader);

		System.out
				.println(getClass().getName()
						+ ">>>>>>loader tree starts for this.getClass(), did some changes\n");

		com.sun.jini.start.ClassLoaderUtil.displayClassLoaderTree(loader);

		System.out.println(getClass().getName() + ">>>>>>loader tree ends\n");

		NonActivatableServiceDescriptor desc = new NonActivatableServiceDescriptor(
				codebase, policy, classpath, implClassName,
				new String[] { config });
		try {
			NonActivatableServiceDescriptor.Created created = (NonActivatableServiceDescriptor.Created) desc
					.create(EmptyConfiguration.INSTANCE);
		} catch (NoSuchMethodException nsme) {
			System.out.println(getClass().getName() + ">>> Error");
			nsme.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// String codebase =
		// "http://neem.cs.ttu.edu:2030/classes/scheduler-dl.jar";
		// String policy = "/research/sorcer/policy/policy";
		// String classpath =
		// "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/lib/scheduler.jar";
		// String implClassName =
		// "sorcer.provider.scheduler.SchedulerProviderImpl";
		// String config =
		// "/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/config/server.config";

		// Class surrogateClass =
		// Class.forName("com.sun.jini.madison.boot.ExportCL");
		// ClassLoader loader = surrogateClass.getClassLoader();

		// Configuration myConfig = ConfigurationProvider.getInstance(null,
		// loader);
		// System.setProperty("java.security.policy",
		// "/research/sorcer/policy/policy");
		// System.setProperty("java.util.logging.config.file",
		// "/projects/users/malladi/www/projects/iGrid/sorcer/src/sorcer/provider/scheduler/schedulerlogging.properties");

		// System.out.println(getClass().getName() +
		// ">>>>>>system property set again with Surrogate host's classloader" +
		// loader);

		// NonActivatableServiceDescriptor desc =
		// new NonActivatableServiceDescriptor(codebase, policy, classpath,
		// implClassName, new String[]{config});
		// try {
		// NonActivatableServiceDescriptor.Created created =
		// (NonActivatableServiceDescriptor.Created) desc.create(myConfig);
		// } catch (Exception e) {
		// logger.log(Level.FINEST, "Instantiating the provider failed", e);
		// e.printStackTrace();
		// }

		// try {
		// Runtime runtime = Runtime.getRuntime();
		// Process process =
		// runtime.exec("/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/bin/scheduler.prv",
		// new String[]{},
		// new
		// File("/projects/users/malladi/www/projects/iGrid/sorcer/bin/sorcer/provider/scheduler/bin/"));
		// int status = process.waitFor();
		// } catch(Exception e) {
		// logger.log(Level.FINEST, "Instantiating the provider failed", e);
		// e.printStackTrace();
		// }

		// System.out.println(getClass().getName() + ">>>>>>Process Started");

		// // register this service with lookup services
		// SurrogateProxy proxy = new SurrogateProxy(description);
		// ServiceIDListener idListener = new ServiceIDListener() {
		// public void serviceIDNotify(ServiceID serviceID) {
		// SurrogateProvider.this.serviceID = serviceID;
		// }
		// };
		// try {
		// joinManager = new JoinManager(proxy, SERVICE_ATTRS, idListener,
		// hostContext.getDiscoveryManager(),
		// null);
		// } catch (IOException e) {
		// abortActivation("Can not join Jini federation, " +
		// "SurrogateProvider self deactivating... ", e);
		// }
	}

	// private Exporter getExporter() {
	// return new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
	// new BasicILFactory(), true, true);
	// }

	public void deactivate() {
		if (deactivated)
			return; // only need to deactivate once

		if (joinManager != null)
			joinManager.terminate();

		for (int i = 0; i < threads.size(); i++) {
			((Thread) threads.get(i)).interrupt();
		}
		if (appSock != null) {
			try {
				appSock.close();
			} catch (IOException e) {
			}
		}
		if (monitorSock != null) {
			try {
				monitorSock.close();
			} catch (IOException e) {
			}
		}

		deactivated = true;
		notifyAll();
	}

	private void abortActivation(String msg, Exception e) {
		if (System.getProperty("com.sun.jini.madison.debug") != null) {
			if (e != null)
				e.printStackTrace();
			System.err.println(msg + description);
		}
		hostContext.cancelActivation();
	}
}
