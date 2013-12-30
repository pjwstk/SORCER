/**
 * 
 */
package PACKAGE_NAME_HERE;

import com.sun.jini.start.LifeCycle;

import sorcer.core.context.ServiceContext;
import sorcer.core.provider.ServiceProvider;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.rmi.RemoteException;

/**
 * @author AUTHOR_NAME_HERE
 *
 */
public class PROVIDER_IMPLEMENTATION_NAME_HERE extends ServiceProvider
       implements PROVIDER_INTERFACE_NAME_HERE {	
	
	public PROVIDER_IMPLEMENTATION_NAME_HERE(
			String[] args, LifeCycle lifeCycle) throws Exception {		
		super(args, lifeCycle);
	}

	/*
	 * 
	 */
	@Override
	public Context METHOD_NAME_HERE(Context inputs) throws ContextException, RemoteException {
		
		/* Package the response in a return context object */
		Context outputs = new ServiceContext();
		return outputs;
	}
}
