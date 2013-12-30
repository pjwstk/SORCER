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

import java.rmi.RemoteException;

import net.jini.core.transaction.Transaction;

/**
   <p>Proxy interface for the locker service.  Running more than one node in
   the same locker group provides fault-tolerance/resilience where locks
   and resources are replicated around the group.</p>

   <p>Locks are stateless in that they are allocated on request and exist
   only as long as the lock is held ensuring that any other lock attempts are
   blocked until release.</p>

   <p>However, one may associate a resource with a particular lock such
   that the resource is made available to a client which acquires the lock.
   </p>

   <p>Basic usage is:</p>

   <ol>
   <li>Locate MutualExclusion instance</li>
   <li>Locate TransactionManager instance</li>
   <li>Use TransactionFactory to obtain transaction</li>
   <li>Request lock under transaction</li>
   <li>Perform work with appropriate services under transaction</li>
   <li>Commit transaction to make changes and release lock</li>
   <li>If something goes awry, abort the transaction to free the lock</li>
   </ol>
 */
public interface MutualExclusion {
    /**
       Create a lockable resource.
     */
    public void newResource(String aClass, Comparable anId,
                            Serializable aResource)
        throws RemoteException;

    public void removeResource(String aClass, Comparable anId)
        throws RemoteException;

    /**
       Request a lock using the specified identifier.  If there is a resource
       associated with the passed identifier, it will be made available to the
       caller if the lock is successfully acquired.

       @param aClass the class of the lock e.g. "entry" or "service" or
       whatever.  This allows for logical grouping of locks which supports,
       for example, partitioning across or within applications.
       @param anId a unique identifier within the class for this lock
       @param aTxn the txn to associate the lock with.  Lock will be held
       for as long as transaction is active and released at commit/abort.
       @param anIdentifier is a application level identifier that the caller
       may wish to associate with the lock - not yet supported
       @return <code>LockResult</code> indicating lock status
     */
    public LockResult getLock(String aClass, Comparable anId, Transaction aTxn,
                              Serializable anIdentifier)
        throws RemoteException;
}