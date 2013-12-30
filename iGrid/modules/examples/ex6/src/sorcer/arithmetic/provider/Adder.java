package sorcer.arithmetic.provider;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.MonitorException;

public interface Adder {

	public Context add(Context context) throws RemoteException,
			ContextException, MonitorException;
}
