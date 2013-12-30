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

package sorcer.core;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import sorcer.service.ExertionException;

public interface Policer extends AdministratableProvider, Remote {

	// This is a marker interface for this provider.
	// This Provider gets any task and executes the method on the
	// provider. Note that any requestor who needs his task to be processed
	// by this provider must also provide the self executing method
	// or the agent along with the task

	public static final String INSERT_DATABASE_ACTION = "insert";
	public static final String UPDATE_DATABASE_ACTION = "update";
	public static final String DELETE_DATABASE_ACTION = "delete";

	public static final Boolean TRUE_DATABASE_ACTION_SUCCESS = true;
	public static final Boolean FALSE_DATABASE_ACTION_SUCCESS = false;

	public static final String PROVIDER_POLICY_LIST = "getProviderPolicyList";
	public static final String POLICY = "getPolicy";
	public static final String DATABASE_ACTION = "performDatabaseAction";
	public static final String POLICY_ENTRIES_FOR_PROVIDER = "getListOfPolicyEntriesForProvider";
	public static final String REPLICATE_TRANSACTION = "replicateTransaction";

	public static final long TIMEOUT = 15000;// 60000; // 60,000 millis = 60 s =
												// 1 min
	public static final long DELAY = 30000;// 60000; // 60,000 millis = 60 s = 1
											// min
	public static final long DELTA = 10000; // 10,000 millis = 10 s

	/*
	 * returns an array with some little information (provider name, provider
	 * Id, date of the last update and owner) about all the policies in the
	 * database
	 */
	public ArrayList getProviderPolicyList() throws ExertionException,
			RemoteException;

	/*
	 * retrieve information for one provider from the database and construct a
	 * Policy object from that information
	 */
	public Policy getPolicy(String providerName, String providerID)
			throws ExertionException, RemoteException;

	/*
	 * perform an insert / update / delete of policy from the database
	 */
	public Boolean performDatabaseAction(String providerName,
			String providerID, String providerInterface,
			String providerKeystore, String databaseAction, Vector policyEntries)
			throws ExertionException, RemoteException;

	/*
	 * returns a list of DatabasePolicyEntries for all the entries for a
	 * specific provider; as well, returns the interface and keystore for that
	 * provider
	 */
	public List getListOfPolicyEntriesForProvider(String providerName,
			String providerID) throws ExertionException, RemoteException;
}
