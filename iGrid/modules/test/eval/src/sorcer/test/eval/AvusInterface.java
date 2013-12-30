/* **************************************************************************
 * Name:        AvusInterface.java
 *
 * Created:     RM Kolonay 16 June 2005
 *
 * Revision history:
 *
 *
 * Copyright (C) 2005 Air Force Research Laboratory Air Vehicles Directorate
 * *************************************************************************/
package sorcer.test.eval;

import java.rmi.RemoteException;

import sorcer.service.Context;

/**
 * Generic Interface for Astros Execution providers.
 * 
 * @author R. M. Kolonay
 * @version %I%, %G%
 * @since JDK1.4
 */
public interface AvusInterface {

	public Context executeAvus(Context context)
			throws RemoteException;
}
