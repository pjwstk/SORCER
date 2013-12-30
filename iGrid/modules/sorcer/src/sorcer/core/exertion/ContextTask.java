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

package sorcer.core.exertion;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.ContextManagement;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Signature;
import sorcer.util.Log;

/**
* The SORCER context task extending the basic task implementation {@link Task}.
* 
* @author Mike Sobolewski
*/
public class ContextTask extends NetTask {
	
	private final static Logger logger = Log.getTestLog();
	
	static final long serialVersionUID = -1182397620651528862L;
	
	public ContextTask(Class serviceType) {
		name = ContextManagement.CONTEXT_REQUEST_PATH;
		// if process signature is null the provider should execute pre and
		// postprocess signatures
		try {
			signatures.add(new NetSignature(SorcerConstants.SELF_SERVICE,
					serviceType));
			NetSignature ss = new NetSignature("getContext",
					ContextManagement.class);
			ss.setType(Signature.Type.POST);
			signatures.add(ss);
			Context cxt = new ServiceContext(
					ContextManagement.CONTEXT_REQUEST_PATH);

			cxt.putValue(ContextManagement.CONTEXT_REQUEST_PATH,
					SorcerConstants.NULL);
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
	
	public Context getProviderContext() throws RemoteException,
			ContextException {
		Context cxt = null;
		try {
			cxt = (Context) exert(null).getContext().getValue(
					ContextManagement.CONTEXT_REQUEST_PATH);
		} catch (Exception e) {
			throw new ContextException(e);
		}
		logger.info("provider context: " + cxt);
		return cxt;
	}

}
