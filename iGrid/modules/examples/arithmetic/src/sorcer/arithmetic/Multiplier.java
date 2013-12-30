package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public interface Multiplier {

	public Context multiply(Context context) throws RemoteException,
			ContextException;
}
