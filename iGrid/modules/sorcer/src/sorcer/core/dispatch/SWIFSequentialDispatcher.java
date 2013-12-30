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

package sorcer.core.dispatch;

import java.rmi.RemoteException;
import java.util.Set;

import sorcer.core.SorcerConstants;
import sorcer.core.exertion.Jobs;
import sorcer.core.exertion.NetJob;
import sorcer.core.provider.Provider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;

public class SWIFSequentialDispatcher extends SWIFExertDispatcher implements
		SorcerConstants {

	public SWIFSequentialDispatcher(Job job, 
            Set<Context> sharedContext,
            boolean isSpawned, 
            Provider provider,
            ProvisionManager provisionManager) throws Throwable {
		super(job, sharedContext, isSpawned, provider, provisionManager);
	}

	public void dispatchExertions() throws ExertionException,
			SignatureException {
        checkAndDispatchExertions();
		try {
			inputXrts = Jobs.getInputExertions((Job)xrt);
			reconcileInputExertions(xrt);
		} catch (ContextException e) {
			throw new ExertionException(e);
		}

		collectResults();
	}

	public void collectResults() throws ExertionException, SignatureException {
		ServiceExertion exertion = null;
		// RemoteExertion result = null;
		for (int i = 0; i < inputXrts.size(); i++) {
			exertion = (ServiceExertion) inputXrts.get(i);
			if (isInterupted(exertion))
				return;
			exertion = (ServiceExertion) execExertion(exertion);
			if (exertion.getStatus() <= FAILED)// ||
				// exertion.getState()<=FAILED)
				xrt.setStatus(FAILED);
			else if (exertion.getStatus() == SUSPENDED
					|| (xrt.getControlContext()).isReview(exertion))
				xrt.setStatus(SUSPENDED);
		}

		if (isInterupted(masterXrt))
			return;
		if (masterXrt != null) {
			exertion = (ServiceExertion) execExertion(masterXrt);// executeMasterExertion();
			if (exertion.getStatus() <= FAILED) {
				state = FAILED;
				xrt.setStatus(FAILED);
			} else {
				state = DONE;
				xrt.setStatus(DONE);
			}
		} else
			state = DONE;

		dispatchers.remove(xrt.getId());
	}

	public NetJob resumeJob() throws RemoteException {
		return null;
	}

	public NetJob stepJob() throws RemoteException {
		return null;
	}

	public void setStatus(ServiceExertion ex, int status) {
		((ServiceExertion) ex).setStatus(status);
	}

}
