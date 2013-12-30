package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;

public class SubtractorImpl implements Subtractor {

	public Context subtract(Context context) throws RemoteException {
		return new SmartArithmometer().calculate(context, Arithmetic.SUBTRACT);
	}
}
