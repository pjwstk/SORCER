package sorcer.arithmetic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.context.Contexts;
import sorcer.service.Context;

public class AveragerImpl implements AveragerRemote, Serializable {

	private static final long serialVersionUID = 3985768659269899314L;
	
	private final static Logger logger = Logger.getLogger(Averager.class
			.getName());

	/**
	 * Implements the {@link Averager} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 */
	public Context average(Context context)
			throws RemoteException {
		ArrayContext cxt = (ArrayContext) context;
		try {
			// get sorted list of inout values
			List inputs = cxt.getInValues();
			logger.info("inputs: \n" + inputs);
			List outpaths = Contexts.getOutPaths(cxt);
			logger.info("getOutputPaths: \n" + outpaths);

			double result = 0;
			for (Object value : inputs)
				result += (Double) value;
			
			result = result / inputs.size();

			logger.info(Arithmetic.AVERAGE + " result: \n" + result);

			String outputMessage = "calculated by " + getHostname();
			if (outpaths.size() == 1) {
				// put the result in the existing output path
				cxt.putValue((String) outpaths.get(0), result);
				cxt.putValue((String) outpaths.get(0) + SorcerConstants.CPS
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
			throw new RemoteException(Arithmetic.AVERAGE
					+ " calculate execption", ex);
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
