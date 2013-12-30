/**
 * 
 */
package PACKAGE_NAME_HERE;

import sorcer.core.SorcerConstants;
import sorcer.service.Context;
import sorcer.service.ContextException;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author AUTHOR_NAME_HERE
 *
 */
public interface PROVIDER_INTERFACE_NAME_HERE extends Remote, SorcerConstants, Serializable {
	public Context METHOD_NAME_HERE(Context in) throws ContextException, RemoteException;
}
