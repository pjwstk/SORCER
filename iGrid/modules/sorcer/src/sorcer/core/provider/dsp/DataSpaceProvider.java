/*
 * Copyright 2012 the original author or authors.
 * Copyright 2012 SorcerSoft.org.
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

package sorcer.core.provider.dsp;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import sorcer.core.provider.DataspaceStorer;
import sorcer.core.provider.ServiceProvider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.url.sos.Handler;

import com.sun.jini.start.LifeCycle;

@SuppressWarnings("rawtypes")
public class DataSpaceProvider extends ServiceProvider implements DataspaceStorer {

	static {
		Handler.register();
	}
	
	public DataSpaceProvider() throws RemoteException {
		// do nothing
	}

	/**
	 * Constructs an instance of the SORCER Object Store implementing
	 * EvaluationRemote. This constructor is required by Jini 2 life cycle
	 * management.
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public DataSpaceProvider(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		//setupDataspace();
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#retrieve(sorcer.service.Context)
	 */
	@Override
	public Context contextRetrieve(Context context) throws RemoteException,
			ContextException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextStore(sorcer.service.Context)
	 */
	@Override
	public Context contextStore(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextUpdate(sorcer.service.Context)
	 */
	@Override
	public Context contextUpdate(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#delete(sorcer.service.Context)
	 */
	@Override
	public Context contextDelete(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextClear(sorcer.service.Context)
	 */
	@Override
	public Context contextClear(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextList(sorcer.service.Context)
	 */
	@Override
	public Context contextList(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextSize(sorcer.service.Context)
	 */
	@Override
	public Context contextSize(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see sorcer.core.StorageManagement#contextRecords(sorcer.service.Context)
	 */
	@Override
	public Context contextRecords(Context context) throws RemoteException,
			ContextException, MalformedURLException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
