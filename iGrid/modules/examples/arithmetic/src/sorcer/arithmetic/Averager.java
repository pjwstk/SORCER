package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;

public interface Averager {
	public Context average(Context context)
			throws RemoteException;
}
