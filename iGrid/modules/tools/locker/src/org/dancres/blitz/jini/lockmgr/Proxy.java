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

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.WeakHashMap;

import net.jini.admin.Administrable;
import net.jini.core.transaction.Transaction;
import net.jini.id.ReferentUuid;
import net.jini.id.ReferentUuids;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import net.jini.io.MarshalledInstance;

import com.sun.jini.admin.DestroyAdmin;

/**
   <p>Proxy doesn't actually embed a stub.  Instead, it knows where to locate
   the back-end lock services from LUS and can then aggregate them together to
   gain resilience.</p>
 */
class Proxy implements MutualExclusion, AdministratableProvider, Serializable, ReferentUuid {

    private Locker theStub;
    private Uuid theUuid;
    private transient Map theUuids;

    Proxy(Locker aStub, Uuid aUuid) {
        theStub = aStub;
        theUuid = aUuid;
    }

    public Uuid getReferentUuid() {
        return theUuid;
    }

    /**
       Establish a permanent lock in this locker.  This lock will never
       be thrown away unless explicitly deleted.
     */
    public void newResource(String aClass, Comparable anId,
                            Serializable aResource)
        throws RemoteException {
        
        Uuid myLockerId = getLockerId();

        try {
            MarshalledInstance myPackedResource =
                new MarshalledInstance(aResource);

            theStub.newResource(aClass, anId, myPackedResource, myLockerId);
        } catch (IOException anIOE) {
            throw new RemoteException("Failed to marshall resource", anIOE);
        }
    }

    public void removeResource(String aClass, Comparable anId)
        throws RemoteException {

        Uuid myLockerId = getLockerId();

        theStub.removeResource(aClass, anId, myLockerId);
    }

    /**
       Lock an identified lock.  This method will create a lock if one doesn't
       exist already.  Thus, in the case of a lock previously created via
       <code>newLock</code> this method will simply attempt to assert a lock.

       @param User-defined identifier for debug purposes (not yet handled)

       @todo Fix <code>myResource.get</code> when/if we do integrity
       @todo Support anIdentifier
     */
    public LockResult getLock(String aClass, Comparable anId, Transaction aTxn,
                              Serializable anIdentifier)
        throws RemoteException {

        if (aTxn == null)
            throw new IllegalArgumentException("You must pass a transaction");

        Uuid myLockerId = getLockerId();

        // System.err.println("In proxy: " + theStub);

        LockStatus myStatus = theStub.takeLock(aClass, anId, aTxn,
                                               myLockerId);

        // System.err.println("Got status");

        if (!myStatus.didSucceed()) {
            // System.err.println("Status says failed");
            return new LockResult(null, null);
        }

        MarshalledInstance myResource =
            (MarshalledInstance) myStatus.getResource();

        if (myResource != null) {
            try {
                return new LockResult(new LockImpl((Serializable) myResource.get(false)), null);
            } catch (ClassNotFoundException aCNFE) {
                throw new RemoteException("Failed to unpack resource", aCNFE);
            } catch (IOException anIOE) {
                throw new RemoteException("Failed to unpack resource", anIOE);
            }
        } else {
            return new LockResult(new LockImpl(null), null);
        }
    }

    private Uuid getLockerId() {
        synchronized(this) {
            if (theUuids == null)
                theUuids = new WeakHashMap();

            Thread myThread = Thread.currentThread();

            Uuid myUuid = (Uuid) theUuids.get(myThread);

            if (myUuid == null) {
                myUuid = UuidFactory.generate();
                theUuids.put(myThread, myUuid);
            }

            return myUuid;
        }
    }

    public boolean equals(Object anObject) {
        return ReferentUuids.compare(this, anObject);
    }

    public int hashCode() {
        return theUuid.hashCode();
    }

	/* (non-Javadoc)
	 * @see com.sun.jini.admin.DestroyAdmin#destroy()
	 */
	@Override
	public void destroy() throws RemoteException {
		((DestroyAdmin)theStub).destroy();
		
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	@Override
	public Object getAdmin() throws RemoteException {
		return ((Administrable)theStub).getAdmin();
	}
}