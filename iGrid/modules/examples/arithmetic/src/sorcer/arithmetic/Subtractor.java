package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public interface Subtractor {

	public Context subtract(Context context) throws RemoteException,
			ContextException;
}
