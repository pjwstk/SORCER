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

import net.jini.core.transaction.UnknownTransactionException;

import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.server.TransactionParticipant;
import net.jini.core.transaction.server.TransactionConstants;
import net.jini.core.transaction.server.ServerTransaction;

import net.jini.id.Uuid;

public class TxnParticipantImpl implements TransactionParticipant,
                                           Serializable {

    private Locker theStub;

    private String theClass;
    private Comparable theKey;
    private Uuid theClientId;

    TxnParticipantImpl(Locker aStub, String aClass, Comparable aKey,
                       Uuid aClientId) {

        theStub = aStub;
        theClass = aClass;
        theKey = aKey;
        theClientId = aClientId;
    }

    public int prepare(TransactionManager mgr, long id)
        throws UnknownTransactionException, RemoteException {

        // Make sure the remote end is still alive
        theStub.ping();

        return TransactionConstants.PREPARED;
    }

    public void commit(TransactionManager mgr, long id)
        throws UnknownTransactionException, RemoteException {

        releaseLock(mgr, id);
    }

    public void abort(TransactionManager mgr, long id)
        throws UnknownTransactionException, RemoteException {

        releaseLock(mgr, id);
    }

    public int prepareAndCommit(TransactionManager mgr, long id)
        throws UnknownTransactionException, RemoteException {

        releaseLock(mgr, id);

        return TransactionConstants.COMMITTED;
    }

    private void releaseLock(TransactionManager aMgr, long anId) {
        try {
            theStub.releaseLock(theClass, theKey,
                                new ServerTransaction(aMgr, anId),
                                theClientId);
        } catch (RemoteException anRE) {
            /*
              Being pessimistic - if remote failure occurs we may never
              see the stub active again.  The backends will figure out that
              the transaction was finalized and cleanup accordingly but we need
              to make sure the caller considers that we've succeeded so we
              avoid throwing this exception.
            */
        }
    }

    public boolean equals(Object anObject) {
        if (anObject instanceof TxnParticipantImpl) {
            TxnParticipantImpl myOther =
                (TxnParticipantImpl) anObject;

            return (theStub.equals(myOther.theStub));
        }

        return false;
    }
}