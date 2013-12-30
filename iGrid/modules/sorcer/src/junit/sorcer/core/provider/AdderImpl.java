package junit.sorcer.core.provider;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public class AdderImpl implements Adder {
	private Arithmometer arithmometer = new Arithmometer();

	public Context add(Context context) throws RemoteException,
			ContextException {
		return arithmometer.add(context);
	}

}
