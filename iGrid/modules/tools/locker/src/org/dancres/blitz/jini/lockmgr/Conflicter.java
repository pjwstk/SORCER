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

/**
 * @todo Fix this to use a transaction
 */
public class Conflicter {
	public void exec() throws Exception {
		Lookup myFinder = new Lookup(MutualExclusion.class);

		MutualExclusion myLocker = (MutualExclusion) myFinder.getService();

		myFinder = new Lookup(TransactionManager.class);

		TransactionManager myMgr = (TransactionManager) myFinder.getService();

		Transaction.Created myTxnC = TransactionFactory.create(myMgr, 100000);
		Transaction myTxn = myTxnC.transaction;

		LockResult myLock = myLocker.getLock("rhubarb", new Integer(55), myTxn,
				"conflicter");

		if (myLock.didSucceed())
			System.err.println("Argh, shouldn't get lock");
		else
			System.out.println("Couldn't get lock");

		myTxn.abort();
	}

	public static void main(String args[]) {
		try {
			if (System.getSecurityManager() == null)
				System.setSecurityManager(new RMISecurityManager());

			new Conflicter().exec();
		} catch (Exception anE) {
			System.err.println("Whoops");
			anE.printStackTrace(System.err);
		}
	}
}