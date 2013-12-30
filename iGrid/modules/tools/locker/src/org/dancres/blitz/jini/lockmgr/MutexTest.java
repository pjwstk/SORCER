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

import java.rmi.RMISecurityManager;

import net.jini.core.transaction.*;
import net.jini.core.transaction.server.TransactionManager;

public class MutexTest {
	public void exec() throws Exception {
		/*
		 * Find an instance of the MutualExclusion service
		 */
		Lookup myFinder = new Lookup(MutualExclusion.class);

		MutualExclusion myLocker = (MutualExclusion) myFinder.getService();

		myFinder = new Lookup(TransactionManager.class);

		TransactionManager myMgr = (TransactionManager) myFinder.getService();

		/*
		 * Create a transaction under which we will first acquire the lock and
		 * then alter appropriate state.
		 */
		Transaction.Created myTxnC = TransactionFactory.create(myMgr, 100000);
		Transaction myTxn = myTxnC.transaction;

		/*
		 * Grab a lock under the passed transaction.
		 * 
		 * The transaction should then be used for all state modifications
		 * across services after which the transaction should be committed. The
		 * lock will be released automatically at that point.
		 * 
		 * If things go awry, abort the transaction to clear out state and
		 * automatically free the lock.
		 */

		System.err.println("Try lock");

		LockResult myLock = myLocker.getLock("rhubarb", new Integer(55), myTxn,
				"MutexTest");

		if (!myLock.didSucceed()) {
			myTxn.abort();
			throw new RuntimeException("Didn't get lock");
		}

		System.out.println("Got lock id 55: " + myLock);

		/*
		 * Let the lock go
		 */
		myTxn.commit();

		/*
		 * Insert a resource which will be made accessible to anyone that
		 * acquires the lock identified by [rhubarb,Integer(66)]
		 */
		myLocker.newResource("rhubarb", new Integer(66), new Integer(25));

		myTxnC = TransactionFactory.create(myMgr, 100000);
		myTxn = myTxnC.transaction;

		myLock = myLocker.getLock("rhubarb", new Integer(66), myTxn,
				"MutexTest");

		if ((myLock.getLock().getResource() == null)
				|| (!myLock.getLock().getResource().equals(new Integer(25)))) {
			myTxn.abort();
			throw new RuntimeException("Didn't get resource: "
					+ myLock.getLock().getResource());
		}
		System.out.println("Got lock id 66 resource=25: " + myLock);
		myTxn.commit();

		/*
		 * Delete the resource. Any future locks will not see the resource
		 * anymore.
		 */
		myLocker.removeResource("rhubarb", new Integer(66));

		myTxnC = TransactionFactory.create(myMgr, 100000);
		myTxn = myTxnC.transaction;

		myLock = myLocker.getLock("rhubarb", new Integer(66), myTxn,
				"MutexTest");

		if (myLock.getLock().getResource() != null) {
			myTxn.abort();
			throw new RuntimeException("Did get a resource: "
					+ myLock.getLock().getResource());
		}
		System.out.println("Got lock for removed id 66: " + myLock);
		myTxn.commit();

		myTxnC = TransactionFactory.create(myMgr, 100000);
		myTxn = myTxnC.transaction;

		myLock = myLocker.getLock("rhubarb", new Integer(55), myTxn,
				"MutextTest");

		if (!myLock.didSucceed()) {
			myTxn.abort();
			throw new RuntimeException("Didn't get lock");
		}
		System.out.println("Got lock id 55: " + myLock);
		System.out.println("Waiting for conflict");

		Thread.sleep(30000);

		System.out.println("Releasing");

		myTxn.commit();
	}

	public static void main(String args[]) {
		try {
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new RMISecurityManager());

			new MutexTest().exec();
		} catch (Exception anE) {
			System.err.println("Whoops");
			anE.printStackTrace(System.err);
		}
	}
}