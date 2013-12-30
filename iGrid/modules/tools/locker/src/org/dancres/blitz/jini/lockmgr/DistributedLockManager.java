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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.UnknownTransactionException;
import net.jini.core.transaction.server.ServerTransaction;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.io.MarshalledInstance;

import org.jgroups.ChannelException;
import org.jgroups.MessageListener;
import org.jgroups.blocks.LockNotGrantedException;
import org.jgroups.blocks.LockNotReleasedException;
import org.jgroups.blocks.TwoPhaseVotingListener;
import org.jgroups.blocks.VoteException;

/**
 * Distributed lock manager is responsible for maintaining the lock information
 * consistent on all participating nodes.
 * 
 * <p>A modified version of the original which provides support for leased 
 * locks, the holding of a resource against a given lock and state
 * replication.</p>
 *
 * @author Roman Rokytskyy (rrokytskyy@acm.org)
 * @author Dan Creswell (dan@dancres.org)
 */
public class DistributedLockManager implements TwoPhaseVotingListener,
                                               LockManager,
                                               MessageListener {

    /**
     * This parameter means that lock acquisition expires after 5 seconds.
     * If there were no "commit" operation on prepared lock, then it
     * is treated as expired and is removed from the prepared locks table.
     */
    private static long ACQUIRE_EXPIRATION = 5000;
    
    /**
     * This parameter is used during lock releasing. If group fails to release
     * the lock during the specified period of time, unlocking fails.
     */
    private static long VOTE_TIMEOUT = 10000;

	// list of all prepared locks
	private Map preparedLocks = new HashMap();

	// list of all prepared releases
	private Map preparedReleases = new HashMap();

	// list of locks on the node
	private Map heldLocks = new HashMap();

    private Map preparedNews = new HashMap();

    private Map preparedDeletes = new HashMap();

    // list of resources held on the node
    private Map heldResources = Collections.synchronizedMap(new HashMap());

	private TwoPhaseVotingAdapter votingAdapter;

	private Object id;

    protected Logger log = Logger.getLogger(DistributedLockManager.class.getName());


    /**
     * Create instance of this class.
     * 
     * @param voteChannel instance of {@link VotingAdapter} that will be used 
     * for voting purposes on the lock decrees. <tt>voteChannel()</tt> will
     * be wrapped by the instance of the {@link TwoPhaseVotingAdapter}.
     * 
     * @param id the unique identifier of this lock manager.
     * 
     * @todo check if the node with the same id is already in the group.
     */
    public DistributedLockManager(VotingAdapter voteChannel, Object id) {
        this(new TwoPhaseVotingAdapter(voteChannel), id);
    }

    /**
     *  Constructor for the DistributedLockManager_cl object.
     * 
     *  @param channel instance of {@link TwoPhaseVotingAdapter}
     *  that will be used for voting purposes on the lock decrees.
     * 
     *  @param id the unique identifier of this lock manager.
     * 
     *  @todo check if the node with the same id is already in the group.
     */
    public DistributedLockManager(TwoPhaseVotingAdapter channel, Object id) {
        this.id = id;
        this.votingAdapter = channel;
        this.votingAdapter.addListener(this);
        this.votingAdapter.setDelegate(this);
    }

    /***********************************************************************
     * Local operations
     ***********************************************************************/

    /**
       Inserts a resource locally.
     */
    private boolean localInsert(LockDecree aDecree) {
        removeExpired(aDecree);

        if (heldResources.get(aDecree.getKey()) != null)
            return false;

        if (heldLocks.get(aDecree.getKey()) != null)
            return false;

        heldResources.put(aDecree.getKey(), aDecree);
        return true;
    }

    private boolean localDelete(LockDecree aDecree) {
        removeExpired(aDecree);

        return ((heldResources.remove(aDecree.getKey())) != null);
    }

    /**
     * Performs local lock. This method also performs the clean-up of the lock
     * table, all expired locks are removed.
     */
    private boolean localLock(LockDecree lockDecree) {
        // remove expired locks
        removeExpired(lockDecree);

        LockDecree localLock =
            (LockDecree) heldLocks.get(lockDecree.getKey());

        if (localLock == null) {

            // promote lock into commited state
            lockDecree.commit();

            // Overridden: DCC - hold lock regardless, we want to replicate
            //
            // no lock exist, perform local lock, note:
            // we do not store locks that were requested by other manager.
            // if (lockDecree.managerId.equals(id))
            // System.out.println("LocalLock: " + lockDecree.getKey() + 
            //                    ", " + lockDecree);

            heldLocks.put(lockDecree.getKey(), lockDecree);

            // everything is fine :)
            return true;
        } else
        if (localLock.requester.equals(lockDecree.requester))
            // requester already owns the lock
            return true;
        else
            // lock does not belong to requester
            return false;

    }

    /**
     * Releases lock locally.
     * 
     * @param lockDecree instance of {@link LockDecree} describing the lock.
     */
    private boolean localRelease(LockDecree lockDecree) {
        // remove expired locks
        removeExpired(lockDecree);

        LockDecree localLock=
                (LockDecree) heldLocks.get(lockDecree.getKey());

        if(localLock == null) {
            // no lock exist
            return true;
        }
        else if(localLock.requester.equals(lockDecree.requester)) {
            // requester owns the lock, release the lock
            heldLocks.remove(lockDecree.getKey());
            return true;
        }
        else
        // lock does not belong to requester
            return false;
    }

    /***********************************************************************
     * can tests
     ***********************************************************************/

    private boolean canInsert(LockDecree aDecree) {
        removeExpired(aDecree);

        return ((heldResources.get(aDecree.getKey())) == null);
    }

    private boolean canDelete(LockDecree aDecree) {
        removeExpired(aDecree);

        return ((heldResources.get(aDecree.getKey())) != null);
    }

    /**
     * Returns <code>true</code> if the requested lock can be granted by the
     * current node.
     * 
     * @param decree instance of <code>LockDecree</code> containing information
     * about the lock.
     */
    private boolean canLock(LockDecree decree) {
        // clean expired locks
        // System.out.println("Remove");
        removeExpired(decree);
        // System.out.println("Remove done");

        /*
        Iterator myLocks = heldLocks.keySet().iterator();

        while (myLocks.hasNext()) {
            Object myKey = myLocks.next();

            System.out.println("Key: " + myKey);
            System.out.println(myKey.hashCode() + ", " + decree.getKey().hashCode());
            System.out.println(myKey.equals(decree.getKey()));
            System.out.println(((Comparable) myKey).compareTo(decree.getKey()));
        }
        */

        LockDecree lock = (LockDecree)heldLocks.get(decree.getKey());
        if (lock == null) {
            // System.out.println("No lock for: " + decree.getKey());
            return true;
        } else {
            // System.out.println("Compare requester: " + decree.requester + ", " +
            //                    lock.requester);
            return lock.requester.equals(decree.requester);
        }
    }

    /**
     * Returns <code>true</code> if the requested lock can be released by the
     * current node.
     * 
     * @param decree instance of {@link LockDecree} containing information
     * about the lock.
     */
    private boolean canRelease(LockDecree decree) {
        // clean expired locks
        removeExpired(decree);

        // we need to check only hold locks, because
        // prepared locks cannot contain the lock
        LockDecree lock = (LockDecree)heldLocks.get(decree.getKey());
        if (lock == null)
            // check if this holds...
            return true;
        else
            return lock.requester.equals(decree.requester);
    }

    /***********************************************************************
     * cleanup operations
     ***********************************************************************/

    /**
     * Removes expired locks.
     * 
     * @param decree instance of {@link LockDecree} describing the lock.
     */
    private void removeExpired(LockDecree decree) {
        // remove the invalid (expired) lock
        LockDecree localLock = (LockDecree)heldLocks.get(decree.getKey());
        if (localLock != null && !localLock.isValid()) {
            // System.out.println("Clear out dead lock");
            heldLocks.remove(localLock.getKey());
        }
    }

    /***********************************************************************
     * External interface
     ***********************************************************************/

    public Serializable getResource(Object lockId) {
        NewDecree myDecree = (NewDecree) heldResources.get(lockId);

        if (myDecree == null)
            return null;

        return myDecree.getResource();
    }

    /**
     * Locks an object with <code>lockId</code> on behalf of the specified
     * <code>owner</code>.
     * 
     * @param lockId <code>Object</code> representing the object to be locked.
     * @param owner object that requests the lock.
     * @param timeout time during which group members should decide
     * whether to grant a lock or not.
     * @param aTxn the transaction to acquire the lock under
     *
     * @throws LockNotGrantedException when the lock cannot be granted.
     * 
     * @throws IllegalArgumentException if lockId or owner are not serializable.
     * 
     * @throws ChannelException if something bad happened to underlying channel.
     *
     * @todo Do join!!!!
     */
    public void lock(Object lockId, Object owner, long timeout,
                     Transaction aTxn)
        throws LockNotGrantedException, ChannelException
    {

        boolean acquired = false;

        try {
            AcquireLockDecree myDecree = new AcquireLockDecree(lockId, owner,
                                                               id, aTxn);

            acquired = votingAdapter.vote(myDecree, timeout);

        } catch (Exception anE) {
            System.err.println("Server failed");
            anE.printStackTrace(System.err);
            throw new ChannelException("Failed", anE);
        }

        if (!acquired) {
            // System.err.println("Not acquired");
            throw new LockNotGrantedException("Lock cannot be granted.");
        } else {
            // System.err.println("Acquired");
        }
    }

    public void insert(Object lockId, long aTimeout, Serializable aResource)
        throws LockNotGrantedException, ChannelException {

        NewDecree myDecree = new NewDecree(lockId, id, aResource);

        boolean inserted =
            votingAdapter.vote(myDecree, aTimeout);

        if (!inserted)
            throw new LockNotGrantedException("Failed to insert resource");
    }

    public void delete(Object lockId, long aTimeout)
        throws LockNotReleasedException, ChannelException {

        DeleteDecree myDecree = new DeleteDecree(lockId, id);

        boolean deleted =
            votingAdapter.vote(myDecree, aTimeout);

        if (!deleted)
            throw new LockNotReleasedException("Failed to delete resource");
    }

    /**
     * Unlocks an object with <code>lockId</code> on behalf of the specified
     * <code>owner</code>.
     * @param lockId <code>long</code> representing the object to be unlocked.
     * @param owner object that releases the lock.
     *
     * @throws LockNotReleasedException when the lock cannot be released.
     * @throws IllegalArgumentException if lockId or owner are not serializable.
     */
    public void unlock(Object lockId, Object owner, Transaction aTxn)
        throws LockNotReleasedException, ChannelException
    {

        // System.err.println("Performing voting");

        boolean released = false;

        try {
            released =
                votingAdapter.vote(new ReleaseLockDecree(lockId,
                                                         owner, id, aTxn),
                                   VOTE_TIMEOUT);
        } catch (Exception anE) {
            System.err.println("Server failed");
            anE.printStackTrace(System.err);
            throw new ChannelException("Failed", anE);
        }
        
        if (!released)
            throw new LockNotReleasedException("Lock cannot be unlocked.");
    }

    /***********************************************************************
     * Voting operations
     ***********************************************************************/

    /**
     * Checks the list of prepared locks/unlocks to determine if we are in the
     * middle of the two-phase commit process for the lock acqusition/release.
     * Here we do not tolerate if the request comes from the same node on behalf
     * of the same owner.
     * 
     * @param preparedContainer either <code>preparedLocks</code> or
     * <code>preparedReleases</code> depending on the situation.
     * 
     * @param requestedDecree instance of <code>LockDecree</code> representing
     * the lock.
     */
    private boolean checkPrepared(Map preparedContainer, 
        LockDecree requestedDecree)
    {
        LockDecree preparedDecree =
            (LockDecree)preparedContainer.get(requestedDecree.getKey());

        // if prepared lock is not valid, remove it from the list
        if ((preparedDecree != null) && !preparedDecree.isValid()) {
            preparedContainer.remove(preparedDecree.getKey());

            preparedDecree = null;
        }

        if (preparedDecree != null) {
            if (requestedDecree.requester.equals(preparedDecree.requester))
                return true;
            else
                return false;
        } else
            // it was not prepared... sorry...
            return true;
    }

    /**
     * Prepare phase for the lock acquisition or release.
     * 
     * @param decree should be an instance <code>LockDecree</code>, if not,
     * we throw <code>VoteException</code> to be ignored by the
     * <code>VoteChannel</code>.
     * 
     * @return <code>true</code> when preparing the lock operation succeeds.
     * 
     * @throws VoteException if we should be ignored during voting.
     */
    public synchronized boolean prepare(Object decree) throws VoteException {
        // System.err.println("Processing decree: " + decree.getClass().getName());

        if (!(decree instanceof LockDecree))
            throw new VoteException("Uknown decree type. Ignore me.");
            
        if (decree instanceof AcquireLockDecree) {
            // System.err.println("In acquire");
            AcquireLockDecree acquireDecree = (AcquireLockDecree)decree;

			if (log.isLoggable(Level.FINER))
				log.fine("Preparing to acquire decree " + acquireDecree.lockId);
            
            // System.err.println("Checking prepared");

            if (!checkPrepared(preparedLocks, acquireDecree))
                // there is a prepared lock owned by third party
                return false;

            // System.err.println("Checking can lock");

            if (canLock(acquireDecree)) {
                preparedLocks.put(acquireDecree.getKey(), acquireDecree);
                // System.out.println("canLock said: " + canLock(acquireDecree));
                return true;
            } else
                // we are unable to aquire local lock
                return false;
        } else if (decree instanceof ReleaseLockDecree) {

            // System.out.println("In release");

            ReleaseLockDecree releaseDecree = (ReleaseLockDecree)decree;
            
			if (log.isLoggable(Level.FINER))
				log.fine("Preparing to release decree " + releaseDecree.lockId);

            // System.out.println("Check prepared");

            if (!checkPrepared(preparedReleases, releaseDecree))
                // there is a prepared release owned by third party
                return false;

            // System.out.println("Validating release");

            if (canRelease(releaseDecree)) {
                preparedReleases.put(releaseDecree.getKey(), releaseDecree);
                // we have local lock and the prepared lock
                // System.out.println("Passed");
                return true;
            } else {
                // System.out.println("Failed");
                // we were unable to aquire local lock
                return false;
            }
        } else if (decree instanceof NewDecree) {
            NewDecree resourceDecree = (NewDecree) decree;

            if(log.isLoggable(Level.FINER))
                log.fine("Preparing to newResource decree " +
                          resourceDecree.lockId);

            if (!checkPrepared(preparedLocks, resourceDecree)) {
                // System.out.println("Blocking lockprepare");
                return false;
            }

            if (!checkPrepared(preparedNews, resourceDecree)) {
                // System.out.println("Blocking resourceprepare");
                return false;
            }

            if (canInsert(resourceDecree)) {
                preparedNews.put(resourceDecree.getKey(), resourceDecree);
                // System.out.println("canLock said: " + canLock(resourceDecree));
                return true;
            } else {
                // System.out.println("cantInsert");
                // we are blocked by a lock
                return false;
            }
        } else if (decree instanceof DeleteDecree) {
            DeleteDecree resourceDecree = (DeleteDecree) decree;

            if(log.isLoggable(Level.FINER))
                log.fine("Preparing to deleteResource decree " +
                          resourceDecree.lockId);

            if (!checkPrepared(preparedLocks, resourceDecree))
                return false;

            if (!checkPrepared(preparedDeletes, resourceDecree))
                return false;

            if (canDelete(resourceDecree)) {
                preparedDeletes.put(resourceDecree.getKey(), resourceDecree);

                return true;
            } else
                return false;
        }

        // we should not be here
        return false;
    }

    /**
     * Commit phase for the lock acquisition or release.
     * 
     * @param decree should be an instance <code>LockDecree</code>, if not,
     * we throw <code>VoteException</code> to be ignored by the
     * <code>VoteChannel</code>.
     * 
     * @return <code>true</code> when commiting the lock operation succeeds.
     * 
     * @throws VoteException if we should be ignored during voting.
     */
    public synchronized boolean commit(Object decree) throws VoteException {
        if (!(decree instanceof LockDecree))
            throw new VoteException("Uknown decree type. Ignore me.");

        if (decree instanceof AcquireLockDecree) {

            // System.err.println("Commiting acquire");

			if (log.isLoggable(Level.FINER))
				log.fine("Committing decree acquisition "
						+ ((LockDecree) decree).lockId);

            // System.err.println("Checking prepared");

            if (!checkPrepared(preparedLocks, (LockDecree)decree))
                // there is a prepared lock owned by third party
                return false;

            if (localLock((LockDecree)decree)) {
                preparedLocks.remove(((LockDecree)decree).getKey());

                // System.err.println("Local lock succeeded");
                return true;
            } else
                return false;
        } else if (decree instanceof ReleaseLockDecree) {
            
			if (log.isLoggable(Level.FINER))
				log.fine("Committing decree release "
						+ ((LockDecree) decree).lockId);
            
            if (!checkPrepared(preparedReleases, (LockDecree)decree))
                // there is a prepared release owned by third party
                return false;

            // System.err.println("Looking to release");
            if (localRelease((LockDecree)decree)) {
                preparedReleases.remove(((LockDecree)decree).getKey());
                return true;
            } else
                return false;
        } else if (decree instanceof NewDecree) {

			if (log.isLoggable(Level.FINER))
				log.fine("Committing decree newResource "
						+ ((LockDecree) decree).lockId);
            
            if (!checkPrepared(preparedLocks, (LockDecree)decree))
                // there is a prepared lock owned by third party
                return false;

            if (!checkPrepared(preparedNews, (LockDecree)decree))
                // there is a prepared resource owned by third party
                return false;

            if (localInsert((LockDecree)decree)) {
                preparedNews.remove(((LockDecree)decree).getKey());
                return true;
            } else
                return false;
        } else if (decree instanceof DeleteDecree) {
        	if (log.isLoggable(Level.FINER))
                log.fine("Committing decree deleteResource " +
                          ((LockDecree)decree).lockId);

            if (!checkPrepared(preparedLocks, (LockDecree)decree))
                // there is a prepared lock owned by third party
                return false;

            if (!checkPrepared(preparedDeletes, (LockDecree)decree))
                // there is a prepared delete owned by third party
                return false;

            if (localDelete((LockDecree)decree)) {
                preparedDeletes.remove(((LockDecree)decree).getKey());
                return true;
            } else
                return false;
        }

        // we should not be here
        return false;
    }

    /**
     * Abort phase for the lock acquisition or release.
     * 
     * @param decree should be an instance <code>LockDecree</code>, if not,
     * we throw <code>VoteException</code> to be ignored by the
     * <code>VoteChannel</code>.
     * 
     * @throws VoteException if we should be ignored during voting.
     */
    public synchronized void abort(Object decree) throws VoteException {
        if (!(decree instanceof LockDecree))
            throw new VoteException("Uknown decree type. Ignore me.");

        if (decree instanceof AcquireLockDecree) {
            
        	if (log.isLoggable(Level.FINER))
                log.fine("Aborting decree acquisition " +
                          ((LockDecree)decree).lockId);
            
            if (!checkPrepared(preparedLocks, (LockDecree)decree))
                // there is a prepared lock owned by third party
                return;

            preparedLocks.remove(((LockDecree)decree).getKey());
        } else if (decree instanceof ReleaseLockDecree) {
            
        	if (log.isLoggable(Level.FINER))
                log.fine("Aborting decree release " +
                          ((LockDecree)decree).lockId);
            
            if (!checkPrepared(preparedReleases, (LockDecree)decree))
                // there is a prepared release owned by third party
                return;

            preparedReleases.remove(((LockDecree)decree).getKey());
        } else if (decree instanceof NewDecree) {
            
        	if (log.isLoggable(Level.FINER))
                log.fine("Aborting decree newResource " +
                          ((LockDecree)decree).lockId);
            
            if (!checkPrepared(preparedNews, (LockDecree)decree))
                // there is a prepared resource owned by third party
                return;

            preparedNews.remove(((LockDecree)decree).getKey());
        } else if (decree instanceof DeleteDecree) {
        	if (log.isLoggable(Level.FINER))
                log.fine("Aborting decree deleteResource " +
                          ((LockDecree)decree).lockId);

            if (!checkPrepared(preparedDeletes, (LockDecree)decree))
                // there is a prepared resource owned by third party
                return;

            preparedDeletes.remove(((LockDecree)decree).getKey());
        }
    }

    /***********************************************************************
     * State synchronization operations
     ***********************************************************************/

    public byte[] getState() {
        System.out.println("GetState:" + id);

        try {
            ByteArrayOutputStream myStream = new ByteArrayOutputStream();
            ObjectOutputStream myOOS = new ObjectOutputStream(myStream);
            
            myOOS.writeObject(preparedLocks);
            myOOS.writeObject(preparedReleases);
            myOOS.writeObject(heldLocks);
            myOOS.writeObject(preparedNews);
            myOOS.writeObject(preparedDeletes);
            myOOS.writeObject(heldResources);
            myOOS.close();

            return myStream.toByteArray();
        } catch (Exception anE) {
            System.err.println("Failed to perform getState");
            anE.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Receive the message. All messages are ignored.
     *
     * @param msg message to check.
     */
    public void receive(org.jgroups.Message msg) {
        // Do nothing
    }

    /**
     * Set the channel state. We do nothing here.
     */
    public void setState(byte[] state) {
        System.out.println("SetState:" + id);

        if (state == null) {
            System.out.println("Not found state");
            return;
        }

        try {
            ByteArrayInputStream myStream = new ByteArrayInputStream(state);
            ObjectInputStream myOIS = new ObjectInputStream(myStream);

            preparedLocks = (Map) myOIS.readObject();
            preparedReleases = (Map) myOIS.readObject();
            heldLocks = (Map) myOIS.readObject();
            preparedNews = (Map) myOIS.readObject();
            preparedDeletes = (Map) myOIS.readObject();
            heldResources = Collections.synchronizedMap((Map)
                                                        myOIS.readObject());

            myOIS.close();

            System.out.println("Got " + preparedLocks.size() +
                               " prepared locks");

            System.out.println("Got " + preparedReleases.size() +
                               " prepared releases");

            System.out.println("Got " + heldLocks.size() +
                               " held locks");

            System.out.println("Got " + preparedNews.size() +
                               " prepared news");

            System.out.println("Got " + preparedDeletes.size() +
                               " prepared deletes");

            System.out.println("Got " + heldResources.size() +
                               " held resources");

        } catch (Exception anE) {
            System.err.println("Failed to perform setState");
            anE.printStackTrace(System.err);
        }
    }

    /***********************************************************************
     * Decrees - one for each different external interace operation
     ***********************************************************************/
            
    /**
     * This class represents the lock
     */
    public static class LockDecree implements Serializable {

        protected Object lockId;
        protected Object requester;
        protected Object managerId;

        protected boolean commited;

        LockDecree(Object lockId, Object requester, Object managerId) {
            /*
            System.out.println("LockDecree: " + lockId + ", " +
                               requester + ", " + managerId);
            */
            this.lockId = lockId;
            this.requester = requester;
            this.managerId = managerId;
        }

        /**
         * Returns the key that should be used for Map lookup.
         */
        public Object getKey() { return lockId; }

        /**
         * This is a place-holder for future lock expiration code.
         */
        public boolean isValid() { return true; }

        public void commit() { this.commited = true; }


        /**
         * This is hashcode from the java.lang.Long class.
         */
        public int hashCode() {
            return lockId.hashCode();
        }

        public boolean equals(Object other) {

            if (other instanceof LockDecree) {
                return ((LockDecree)other).lockId.equals(this.lockId);
            } else {
                return false;
            }
        }
    }

    /**
     * This class represents the lock to be released.
     */
    public static class AcquireLockDecree extends LockDecree {
        private transient long acquireExpiryTime;

        AcquireLockDecree(Object lockId, Object requester,
                          Object managerId, Transaction aTxn) throws Exception {
            super(lockId, 
                  new TransactionalOwner(requester, (ServerTransaction) aTxn),
                  managerId);
            acquireExpiryTime = System.currentTimeMillis() + 
                ACQUIRE_EXPIRATION;
        }

        /**
         * Lock aquire decree is valid for a <code>ACQUIRE_EXPIRATION</code>
         * time after creation and if the lock is still valid (in the
         * future locks will be leased for a predefined period of time).
         *
         * @todo Check transaction status
         */
        public boolean isValid() {
            boolean result = super.isValid();

            if (result) {
                if (!commited)
                    result = (acquireExpiryTime > System.currentTimeMillis());
                else {
                    // Check transaction status!!!!
                    TransactionalOwner myOwner =
                        (TransactionalOwner) requester;

                    return myOwner.isValid();
                }
            }

            // System.out.println("isValid: " + result);

            return result;
        }

        /**
           Locks are replicated across nodes so we need to ensure that the
           leases (acquire and lock) are recomputed as they are transferred
           between JVMs.
         */
        private void writeObject(ObjectOutputStream stream)
            throws IOException {
            stream.defaultWriteObject();
            stream.writeLong(acquireExpiryTime - System.currentTimeMillis());
        }

        private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
           
            // Acquire time is always small so we don't do overflow
            long myAcquireRemainder = stream.readLong();
            acquireExpiryTime = System.currentTimeMillis() + myAcquireRemainder;
        }
    }

    
    /**
     * This class represents the lock to be released.
     */
    public static class ReleaseLockDecree extends LockDecree {
        ReleaseLockDecree(Object lockId, Object requester, Object managerId,
                          Transaction aTxn) throws Exception {
            super(lockId,
                  new TransactionalOwner(requester, (ServerTransaction) aTxn),
                  managerId);
        }
    }

    public static class NewDecree extends LockDecree {
        private Serializable theResource;

        NewDecree(Object lockId, Object managerId, Serializable aResource) {
            super(lockId, "DLM:internal", managerId);
            theResource = aResource;
        }

        public Serializable getResource() {
            return theResource;
        }
    }

    public static class DeleteDecree extends LockDecree {
        DeleteDecree(Object lockId, Object managerId) {
            super(lockId, "DLM:internal", managerId);
        }
    }

    /**
       <p>This class simply allows us to make the transaction part of the
       requester id which makes sense as the lock is owned by the transaction
       as much as the requester.  We hold both as a double check against
       multiple threads using the same transaction (you might expect that
       we'd just track the transaction).</p>

       <p>The most difficult part is coping with the fact the JGroups doesn't
       do code downloading when it serializes things backwards and forwards
       so we must wrap the <code>ServerTransaction</code> so that codebase is
       carried across and downloading is performed correctly</p>
     */
    public static class TransactionalOwner implements Serializable {
        private MarshalledInstance thePackedTxn;
        private Object theRequester;

        private transient ServerTransaction theTxn;

        TransactionalOwner(Object aRequester, ServerTransaction aTxn)
            throws Exception {
            theTxn = aTxn;

            if (aTxn != null)
                thePackedTxn = new MarshalledInstance(aTxn);

            theRequester = aRequester;
        }

        /**
           @todo Fix verification of integrity
         */
        private ServerTransaction getTxn() throws Exception {
            synchronized(this) {
                if ((theTxn == null) && (thePackedTxn != null))
                    theTxn = (ServerTransaction) thePackedTxn.get(false);

                return theTxn;
            }
        }

        public boolean equals(Object anObject) {
            if (anObject instanceof TransactionalOwner) {
                try {
                    TransactionalOwner myOther = (TransactionalOwner) anObject;

                    if ((getTxn() == null) || (myOther.getTxn() == null)) {
                        if (getTxn() == myOther.getTxn())
                            return theRequester.equals(myOther.theRequester);
                    } else {
                        if (getTxn().equals(myOther.getTxn())) {
                            return theRequester.equals(myOther.theRequester);
                        }
                    }
                } catch (Exception anE) {
                    System.err.println("Equals failed!");
                    anE.printStackTrace(System.err);
                }
            }

            return false;
        }

        public boolean isValid() {
            try {
                if (theTxn == null) {
                    System.err.println("isValid:forceTrue");
                    return true;
                }

                int myTxnState = theTxn.mgr.getState(theTxn.id);

                switch (myTxnState) {
                    case TransactionConstants.VOTING :
                    case TransactionConstants.ACTIVE :
                    case TransactionConstants.PREPARED : return true;

                    case TransactionConstants.COMMITTED :
                    case TransactionConstants.ABORTED : return false;

                    default : {
                        throw new IllegalStateException("Unexpected state from txn mgr: " + myTxnState);
                    }
                }

            } catch (RemoteException anRE) {
                if (anRE instanceof NoSuchObjectException)
                    // Object isn't coming back
                    return false;

                // Stay valid 'til we get stable state from TxnMgr
                //
                return true;
            } catch (UnknownTransactionException aUTE) {
                // Invalid - the transaction has gone
                //
                return false;
            }
        }
    }
}