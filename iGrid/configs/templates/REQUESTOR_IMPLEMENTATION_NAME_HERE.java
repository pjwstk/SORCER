/**
 * 
 */
package PACKAGE_NAME_HERE;

import java.lang.reflect.Method;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Enumeration;

import net.jini.core.transaction.TransactionException;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ServiceTask;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.SignatureException;
import sorcer.service.Task;

/**
 * @author AUTHOR_NAME_HERE
 *
 */
public class REQUESTOR_IMPLEMENTATION_NAME_HERE {
	/**
	 * For exertion oriented programming, need to specify:<br/>
	 * 1. Context object(s) for data sharing<br/>
	 * 2. Signature object(s) for specifying interface, method, and provider
	 *    (optionally) to use<br/>
	 * 3. Task/Job object(s) for carrying out exertion oriented programming
	 *    ...attach context and signature object(s) to task/job object(s) 
	 *    as is appropriate<br/>
	 * @throws ContextException 
	 * @throws ExertionException 
	 * @throws TransactionException 
	 * @throws RemoteException 
	 */	
	 public static void main(String[] args) throws ContextException, 
	                                               RemoteException, 
	                                               TransactionException, 
	                                               ExertionException{
		 /**
		  * Specify security manager
		  */
		 if (System.getSecurityManager() == null) {
			 System.setSecurityManager(new RMISecurityManager());
		 }
		 
		 /**
		  * Specify context object
		  */
		  Context cntx = new ServiceContext();
		  /* Will need to adjust paths and objects based on contract with
		   * provider's input Context object. Hopefully, interface will be
		   * marked with appropriate paths and object class types 
		   * 
		   * Use of putInValue will prepend DA_IN to path*/
		  cntx.putValue("/path/where/value/has/to/be/inserted", 
	                    "Serializable Object");
		  
		  /**
		   * Specify signature object
		   */
		  Signature sig = new ServiceSignature();
		  sig.setServiceType(
				  PROVIDER_INTERFACE_NAME_HERE.class);
		  /* setName is really misnamed. It should probably be setMethod(...)
		   * but this doesn't exist. setName does not name the signature 
		   * object, but assigns to this.selector, the name of the method 
		   * to use from the interface specified with setServiceType 
		   * 
		   * For demonstration purposes, just show how to use reflection
		   * to get the first method of the interface. You may want to hard
		   * code a name, use a reflection search method, etc. */
		  Method[] mthds = 
			      PROVIDER_INTERFACE_NAME_HERE.class.getMethods();
		  if (mthds.length > 0) {
			  try {
				((ServiceSignature)sig).setSelector(mthds[0].getName());
			} catch (SignatureException e) {
				e.printStackTrace();
			}  
		  }		  
		  /* Default is to find any provider supporting the interface 
		   * specified by setServiceType (*). You may want to modify this
		   * value */
		  sig.setProviderName("*");
		  
		  /**
		   * Specify task/job object, assign context and signature
		   */
		  Task tsk = new ServiceTask();
		  tsk.setContext(cntx);
		  tsk.addSignature(sig);
		  
		  /**
		   * Carry out an exertion operation
		   */
		  Exertion exertion = tsk.exert(null);
		  
		  /**
		   * Post-process context object that is returned by provider
		   */
		  Context returnedContext = exertion.getContext();		
		  
		  /* Will probably use .getValue(path) instead */
		  if (returnedContext instanceof ServiceContext) {
			  ServiceContext rtSrvContext = (ServiceContext)returnedContext;
			  Enumeration<?> enValues = rtSrvContext.contextValues();
	          while (enValues.hasMoreElements()) {
	            Object object = (Object) enValues.nextElement();
	            System.out.println("Object Class: " + object.getClass().getName());
	            System.out.println("Object toString():" + object.toString());
	      	}
		  }
		  else {
			  System.out.println("Returned Context:" + 
					             returnedContext.toString());
		  }
	 }

}
