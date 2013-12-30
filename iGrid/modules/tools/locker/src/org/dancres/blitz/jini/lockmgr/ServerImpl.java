/*
 Copyright 2005 Dan Creswell (dan@dancres.org)

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 License for the specific language governing permissions and limitations under
 the License.
 */

package org.dancres.blitz.jini.lockmgr;

import java.io.Serializable;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceID;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.export.Exporter;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.lookup.JoinManager;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.blocks.LockNotGrantedException;
import org.jgroups.blocks.LockNotReleasedException;

import com.sun.jini.start.LifeCycle;

/**
 * @todo Support for configuring multicast etc
 */
public class ServerImpl implements Locker, AdministratableProvider {
	public static final String NETWORK_CONFIG_ENTRY_NAME = "networkConfig";
	
	public final Logger logger = Logger.getLogger(getClass().getName());
	
	public static final String IS_STANDALONE = "standalone";

	private Proxy theProxy;

	private Locker theStub;

	private Exporter theExporter;

	private JoinManager theJoinManager;

	private ServiceID theServiceId;

	private JChannel theChannel;

	private VotingAdapter theVotingAdapter;

	private LockManager theLockMgr;
	
	private boolean standalone = true;
	
	private long theCrashCount = System.currentTimeMillis();

	/**
	 * @todo Make this configurable later including channel
	 */
	private JiniConfig theConfig;

	public ServerImpl(String[] anArgs, LifeCycle aLifecycle) throws Exception {

		if (anArgs.length > 0)
			ConfigurationFactory.setup(anArgs);

		if (System.getSecurityManager() == null) {
			System.err.println("Setting RMISecurityManager");
			System.setSecurityManager(new RMISecurityManager());
		} else {
			System.err.println("Manager already set: "
					+ System.getSecurityManager());
		}

		theConfig = (JiniConfig) ConfigurationFactory
				.getEntry(NETWORK_CONFIG_ENTRY_NAME, JiniConfig.class,
						JiniConfig.DEFAULT);
		standalone = (Boolean) ConfigurationFactory.getEntry(
				IS_STANDALONE, boolean.class, true);
		String SERVER_PROTOCOL_STACK = ""
				+ "UDP(mcast_addr="
				+ theConfig.getMcastAddr()
				+ ";mcast_port="
				+ theConfig.getMcastPort()
				+ ";ip_ttl="
				+ theConfig.getMcastTTL()
				+ ";"
				+ "mcast_send_buf_size=150000;mcast_recv_buf_size=80000)"
				+ ":PING(timeout=500;num_initial_members=2)"
				+ ":FD"
				+ ":VERIFY_SUSPECT(timeout=1500)"
				+ ":pbcast.NAKACK(gc_lag=50;retransmit_timeout=300,600,1200,2400,4800)"
				+ ":UNICAST(timeout=5000)"
				+ ":pbcast.STABLE(desired_avg_gossip=200)"
				+ ":FRAG(frag_size=4096)"
				+ ":pbcast.GMS(join_timeout=5000;join_retry_timeout=1000;"
				+ "shun=false;print_local_addr=false)"
				+ ":pbcast.STATE_TRANSFER(down_thread=false)";

		theExporter = theConfig.getExporter();

		theStub = (Locker) theExporter.export(this);

		Class[] myInterfaces = theStub.getClass().getInterfaces();

		for (int i = 0; i < myInterfaces.length; i++) {
			System.err.println("Stub supports: " + myInterfaces[i].getName());
		}

		Uuid myUuid = UuidFactory.generate();
		theServiceId = new ServiceID(myUuid.getMostSignificantBits(),
				myUuid.getLeastSignificantBits());

		theProxy = new Proxy(theStub, myUuid);

		theChannel = new JChannel(SERVER_PROTOCOL_STACK);
		theChannel.setOpt(Channel.GET_STATE_EVENTS, Boolean.TRUE);
		theChannel.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);
		theChannel.setOpt(Channel.AUTO_GETSTATE, Boolean.TRUE);
		theVotingAdapter = new VotingAdapter(theChannel);
		theChannel.connect(theConfig.getLockGroup());

		System.out.println("Waiting for channel connection");

		while (!theChannel.isConnected()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException anIE) {
			}
		}

		System.out.println("Connected: " + theChannel + ", "
				+ theChannel.getAddress());

		theLockMgr = new DistributedLockManager(theVotingAdapter, myUuid);

		System.out.println("Channel connected");

		theChannel.getState(null, 0);

		System.out.println("Syncd state");

		theJoinManager = new JoinManager(theProxy, theConfig.getJiniAttrs(),
				theServiceId, theConfig.getLDM(), null,
				ConfigurationFactory.getConfig());

		System.out.println("Advertising Proxy");
	}

	/**
	 * Establish a permanent resource in this locker. This resource will never
	 * be thrown away unless explicitly deleted.
	 * 
	 * @param aResource
	 *            a Serializable object to be associated with the lock and made
	 *            available to future lockers.
	 */
	public void newResource(String aClass, Comparable anId,
			Serializable aResource, Uuid aClientId) throws RemoteException {

		try {
			theLockMgr.insert(new LockKey(aClass, anId),
					theConfig.getOpTimeout(), aResource);
		} catch (ChannelException aCE) {
			throw new RemoteException("Comms failure", aCE);
		} catch (LockNotGrantedException anLNGE) {
			if (theChannel.isConnected())
				throw new RemoteException(
						"Couldn't insert - are you holding a lock?");
			else
				throw new RemoteException("Paritioned from cooperative", anLNGE);
		}
	}

	public void removeResource(String aClass, Comparable anId, Uuid aClientId)
			throws RemoteException {

		try {
			theLockMgr.delete(new LockKey(aClass, anId),
					theConfig.getOpTimeout());
		} catch (ChannelException aCE) {
			throw new RemoteException("Comms failure", aCE);
		} catch (LockNotReleasedException anLNGE) {
			if (theChannel.isConnected())
				throw new RemoteException(
						"Couldn't remove - are you holding a lock?");
			else
				throw new RemoteException("Paritioned from cooperative", anLNGE);
		}
	}

	/**
	 * Lock an identified lock. This method will create a lock if one doesn't
	 * exist already. Thus, in the case of a lock previously created via
	 * <code>newLock</code> this method will simply attempt to assert a lock.
	 * 
	 * @todo Make wait timeout configurable
	 * @todo Make requester a combo of client id and transaction
	 * @todo Modify valid check to use transaction in requester to establish the
	 *       fact
	 */
	public LockStatus takeLock(String aClass, Comparable anId,
			Transaction aTxn, Uuid aClientId) throws RemoteException {

		try {
			/*
			 * System.out.println("Request lock: " + aClass + ", " + anId + ", "
			 * + aLeaseTime + ", " + aClientId);
			 */

			LockKey myKey = new LockKey(aClass, anId);

			theLockMgr.lock(myKey, aClientId, theConfig.getOpTimeout(), aTxn);

			// System.err.println("Joining txn");

			if (aTxn != null) {
				/*
				 * Do join - we don't need to join until we've asserted the lock
				 * because if we fail here, the lock will be removed at some
				 * future point because it won't be valid (we'll abort the
				 * transaction by signalling to the user they've failed to
				 * acquire the lock).
				 */
				try {
					ServerTransaction myTxn = (ServerTransaction) aTxn;

					TransactionParticipant myParticipant = new TxnParticipantImpl(
							theStub, aClass, anId, aClientId);

					myTxn.mgr.join(myTxn.id, myParticipant, theCrashCount);

				} catch (Exception anE) {
					// System.err.println("Join failed");
					anE.printStackTrace(System.err);
					return LockStatus.FAILED;
				}
			}

			// System.err.println("Returning status");
			return new LockStatus(theLockMgr.getResource(myKey));

		} catch (ChannelException aCE) {
			throw new RemoteException("Comms failure", aCE);
		} catch (LockNotGrantedException anLNGE) {
			if (theChannel.isConnected())
				return LockStatus.FAILED;
			else
				throw new RemoteException("Paritioned from cooperative", anLNGE);
		}
	}

	public boolean releaseLock(String aClass, Comparable aKey,
			Transaction aTxn, Uuid aClientId) throws RemoteException {

		try {
			theLockMgr.unlock(new LockKey(aClass, aKey), aClientId, aTxn);
			return true;
		} catch (ChannelException aCE) {
			throw new RemoteException("Comms failure", aCE);
		} catch (LockNotReleasedException anLNRE) {
			throw new RemoteException(
					"Lock issue - did you ever have the lock?", anLNRE);
		}
	}

	public void destroy() throws java.rmi.RemoteException {
		theJoinManager.terminate();
		theExporter.unexport(true);
		new Thread(new DestroyThread()).start();
	}

	public void ping() throws RemoteException {
	}

	public Object getAdmin() throws java.rmi.RemoteException {
		return (AdministratableProvider)theProxy;
	  }
	
	/**
	 * This method spawns a separate thread to destroy this provider after 2
	 * sec, should make a reasonable attempt to let this remote call return
	 * successfully.
	 */
	private class DestroyThread implements Runnable {
		public void run() {
			try {
				// allow for remaining cleanup
				Thread.sleep(2000);
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				if (standalone) {
					System.out.println("Destoyed Locker Service");
					System.exit(0);
				}
			}
		}
	}
}