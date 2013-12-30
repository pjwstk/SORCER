package sorcer.arithmetic.provider;

import java.rmi.RemoteException;

import sorcer.core.context.ContextManagementAdapter;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class MultiplierImpl implements Multiplier {
	Arithmometer arithmometer = new Arithmometer();

	public Context multiply(Context context) throws RemoteException,
			ContextException {
		return arithmometer.multiply(context); 
	}

}
