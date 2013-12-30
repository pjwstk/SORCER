package sorcer.ex5.provider;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public interface Divider {

	public Context divide(Context context) throws RemoteException,
			ContextException;
}
