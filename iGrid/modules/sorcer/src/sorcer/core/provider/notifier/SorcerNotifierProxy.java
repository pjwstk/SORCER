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

package sorcer.core.provider.notifier;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import sorcer.core.SorcerNotifierProtocol;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.ServiceProvider;

class SorcerNotifierProxy extends ServiceProvider implements Serializable,
		SorcerNotifierProtocol, Remote {

	public SorcerNotifierProxy() throws RemoteException {
	}

	public Integer register(RemoteEventListener listener, Object handback,
			Integer regFor, String userId, Vector sessionJobs, String sessionID)
			throws RemoteException {
		return new Integer("1");
	}

	public Integer register(RemoteEventListener listener, Integer regFor,
			String userId, Vector sessionJobs, String sessionID)
			throws RemoteException {
		return new Integer("2");
	}

	public Integer register(RemoteEventListener listener, Integer regFor,
			String userId, Vector sessionJobs) throws RemoteException {
		return new Integer("2");
	}

	public void deleteListener(Integer id, Integer regFor)
			throws RemoteException {
		return;
	}

	public void notify(RemoteEvent ev) throws RemoteException {
		return;
	}

	public boolean isValidTask(NetTask task) {
		return true;
	}

	public boolean isValidTask() {
		return true;
	}

	public void appendJobToSession(String ownerID, String jobID,
			int sessionType, String sessionID) {
	}

}
