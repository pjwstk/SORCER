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

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.id.Uuid;

import net.jini.core.transaction.Transaction;

/**
   A non-blocking low-level lock interface.  Results are immediate.  Blocking
   semantics should be implemented higher up.

   @todo Maybe assist blocking by providing some kind of event infrastructure.
 */
public interface Locker extends Remote {
    /**
       Establish a permanent resource in this locker.  This resource will never
       be thrown away unless explicitly deleted.
     */
    public void newResource(String aClass, Comparable aKey,
                            Serializable aResource,
                            Uuid aClientId)
        throws RemoteException;

    /**
       <p>Delete a resource previously established via <code>newResource</code>.
       If there is a holder of the lock, it will find that it's lease
       becomes invalid at the next renewal request.  When this happens, it
       should abort as necessary.</p>

       <p>In most situations, rather than rely on detection via invalid lease
       request, a client should be explicitly informed of the impending
       deletion of this lock and allowed to shutdown cleanly first.</p>
     */
    public void removeResource(String aClass, Comparable anId, Uuid aClientId)
        throws RemoteException;

    /**
       Lock an identified lock.  This method will create a lock if one doesn't
       exist already.  Thus, in the case of a lock previously created via
       <code>newLock</code> this method will simply attempt to assert a lock.

       @return -1 if the lock fails, otherwise the duration of the lease
     */
    public LockStatus takeLock(String aClass, Comparable aKey, Transaction aTxn,
                               Uuid aClientId)
        throws RemoteException;

    public boolean releaseLock(String aClass, Comparable aKey, Transaction aTxn,
                               Uuid aClientId)
        throws RemoteException;

    /**
       Used by transaction participant implementation to check liveness.
       Just allows us to be a bit more friendly.
     */
    public void ping() throws RemoteException;
}