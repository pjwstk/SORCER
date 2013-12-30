package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;

public class AdderImpl implements Adder {

	public Context add(Context context) throws RemoteException {
		return new SmartArithmometer().calculate(context, Arithmetic.ADD);
	}
}
