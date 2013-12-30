package sorcer.arithmetic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.context.Contexts;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class Arithmometer implements Arithmetic, Serializable, SorcerConstants {

	private static final long serialVersionUID = -6475144369447355477L;
	
	private final static Logger logger = Logger.getLogger(Arithmometer.class
			.getName());

	/**
	 * Implements the {@link Adder} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context add(Context context) throws RemoteException {
		return calculate(context, ADD);
	}

	/**
	 * Implements the {@link Subtractor} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context subtract(Context context)
			throws RemoteException {
		return calculate(context, SUBTRACT);
	}

	/**
	 * Implements the {@link Multiplier} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context multiply(Context context)
			throws RemoteException {
		return calculate(context, MULTIPLY);
	}

	/**
	 * Implements the {@link Divider} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteExceptionO
	 */
	public Context divide(Context context) throws RemoteException {
		return calculate(context, DIVIDE);
	}

	/**
	 * Calculates the result of arithmetic operation specified by a selector
	 * (add, subtract, multiply, or divide).
	 * 
	 * @param input
	 *            service context
	 * @param selector
	 *            a name of arithmetic operation
	 * @return
	 * @throws RemoteException
	 * @throws ContextException
	 * @throws UnknownHostException
	 */
	Context calculate(Context context, String selector)
			throws RemoteException {
		ArrayContext cxt = (ArrayContext) context;
		try {
			// get sorted list of inout values
			List inputs = cxt.getInValues();
			logger.info("inputs: \n" + inputs);
			List outpaths = Contexts.getOutPaths(cxt);
			logger.info("getOutputPaths: \n" + outpaths);

			double result = 0;
			if (selector.equals(ADD)) {
				result = 0;
				for (Object value : inputs)
					result += (Double) value;
			} else if (selector.equals(SUBTRACT)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result -= (Double) inputs.get(i);
			} else if (selector.equals(MULTIPLY)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result *= (Double) inputs.get(i);
			} else if (selector.equals(DIVIDE)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result /= (Double) inputs.get(i);
			}

			logger.info(selector + " result: \n" + result);

			String outputMessage = "calculated by " + getHostname();
			if (outpaths.size() == 1) {
				// put the result in the existing output path
				cxt.putValue((String) outpaths.get(0), result);
				cxt.putValue((String) outpaths.get(0) + CPS
						+ ArrayContext.DESCRIPTION, outputMessage);
			} else {
				// put the result for a new output path
				logger.info("max index; " + cxt.getMaxIndex());
				int oi = cxt.getMaxIndex() + 1;
				cxt.ov(oi, result);
				cxt.ovd(oi, outputMessage);
			}

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " calculate execption", ex);
		}
		return (Context) context;
	}

	/**
	 * Returns name of the local host.
	 * 
	 * @return local host name
	 * @throws UnknownHostException
	 */
	private String getHostname() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}

}
