package sorcer.ex1;

import java.rmi.Remote;
import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public interface WhoIsIt extends Remote {

	Context getHostName(Context context) throws RemoteException,
			ContextException;

	Context getHostAddress(Context context) throws RemoteException,
			ContextException;

	Context getCanonicalHostName(Context context) throws RemoteException,
			ContextException;

	Context getTimestamp(Context context) throws RemoteException,
			ContextException;
}
