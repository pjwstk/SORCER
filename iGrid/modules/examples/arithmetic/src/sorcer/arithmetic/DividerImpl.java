package sorcer.arithmetic;

import java.rmi.RemoteException;

import sorcer.service.Context;

public class DividerImpl implements Divider {

	public Context divide(Context context) throws RemoteException {
		return new SmartArithmometer().calculate(context, Arithmetic.DIVIDE);
	}
}
