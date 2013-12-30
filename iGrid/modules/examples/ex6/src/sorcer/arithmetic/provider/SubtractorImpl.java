package sorcer.arithmetic.provider;

import java.rmi.RemoteException;

import sorcer.service.Context;
import sorcer.service.ContextException;

public class SubtractorImpl implements Subtractor {
	Arithmometer arithmometer = new Arithmometer();

	public Context subtract(Context context) throws RemoteException,
			ContextException {
		return arithmometer.subtract(context);
	}
}
