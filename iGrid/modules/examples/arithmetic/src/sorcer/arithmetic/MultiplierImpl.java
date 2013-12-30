package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;

public class MultiplierImpl implements Multiplier {

	public Context multiply(Context context) throws RemoteException {
		return new SmartArithmometer().calculate(context, Arithmetic.MULTIPLY);
	}
}
