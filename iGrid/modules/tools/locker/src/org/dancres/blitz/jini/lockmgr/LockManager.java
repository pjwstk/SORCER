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

import net.jini.core.transaction.Transaction;

import org.jgroups.ChannelException;

import org.jgroups.blocks.LockNotGrantedException;
import org.jgroups.blocks.LockNotReleasedException;

/**
 * <code>LockManager</code> represents generic lock manager that allows
 * obtaining and releasing locks on objects.
 * 
 * @author Roman Rokytskyy (rrokytskyy@acm.org)
 */
public interface LockManager {
    
    /**
     * Obtain lock on <code>obj</code> for specified <code>owner</code>.
     * Implementation should try to obtain lock few times within the
     * specified timeout.
     *
     * @param obj obj to lock, usually not full object but object's ID.
     * @param owner object identifying entity that will own the lock.
     * @param timeout maximum time that we grant to obtain a lock.
     * @param aTxn the transaction to take this lock under
     *
     * @throws LockNotGrantedException if lock is not granted within
     * specified period.
     *
     * @throws ClassCastException if <code>obj</code> and/or
     * <code>owner</code> is not of type that implementation expects to get
     * (for example, when distributed lock manager obtains non-serializable
     * <code>obj</code> or <code>owner</code>).
     * 
     * @throws ChannelException if something bad happened to communication
     * channel.
     */
    void lock(Object obj, Object owner, long timeout, Transaction aTxn)
        throws LockNotGrantedException, ChannelException;

    /**
       <p>Store a resource against a particular lock id for later recovery
       via <code>getResource</code>.

       <p>Note insert will fail if a lock is currently held or
       is about to be asserted against that id.</p>
     */
    void insert(Object lockId, long aTimeout,
                Serializable aResource)
        throws LockNotGrantedException, ChannelException;

    /**
       <p>Delete a resource held against a particular lock id.</p>

       <p>This operation will fail whilst a lock is actively held against
       the lock id.</p>
     */
    void delete(Object lockId, long aTimeout)
        throws LockNotReleasedException, ChannelException;

    Serializable getResource(Object lockId);

    /**
     * Release lock on <code>obj</code> owned by specified <code>owner</code>.
     *
     * @param obj obj to lock, usually not full object but object's ID.
     * @param owner object identifying entity that will own the lock.
     *
     * @throws LockOwnerMismatchException if lock is owned by another object.
     *
     * @throws ClassCastException if <code>obj</code> and/or
     * <code>owner</code> is not of type that implementation expects to get
     * (for example, when distributed lock manager obtains non-serializable
     * <code>obj</code> or <code>owner</code>).
     * 
     * @throws ChannelException if something bad happened to communication
     * channel.
     */
    void unlock(Object obj, Object owner, Transaction aTxn)
        throws LockNotReleasedException, ChannelException;
}