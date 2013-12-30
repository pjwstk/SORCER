package sorcer.ex1.requestor.bean;

import java.net.InetAddress;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.requestor.ServiceRequestor;
import sorcer.core.signature.NetSignature;
import sorcer.ex1.requestor.RequestorMessage;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.Task;

public class WhoIsItBeanRequestor2 extends ServiceRequestor {

	public Exertion getExertion(String... args) throws ExertionException {
		String hostname, ipAddress;
		InetAddress inetAddress;
		String providerName = null;
		Context context = null;
		NetSignature signature = null;
		// define requestor data
		if (args.length == 2)
			providerName = args[1];
		logger.info("providerName: " + providerName);
		Task task = null;
		try {
			inetAddress = InetAddress.getLocalHost();

			hostname = inetAddress.getHostName();
			ipAddress = inetAddress.getHostAddress();

			context = new ServiceContext("Who Is It?");
			context.putValue("requestor/message", new RequestorMessage(
					"WhoIsIt Bean"));
			context.putValue("requestor/hostname", hostname);
			context.putValue("requestor/address", ipAddress);

			signature = new NetSignature("getHostName",
					sorcer.ex1.WhoIsIt.class, providerName != null ? providerName : null);
			
			task = new NetTask("Who Is It?", signature, context);
		} catch (Exception e) {
			throw new ExertionException("Failed to create exertion", e);
		}
		return task;
	}

	@Override
	public void postprocess(String... args) throws ContextException {
		logger.info("<<<<<<<<<< Exceptions: \n" + exertion.getExceptions());
		logger.info("<<<<<<<<<< Trace list: \n" + exertion.getControlContext().getTrace());
		logger.info("<<<<<<<<<< Ouput context: \n" + exertion.getContext());
	}
	
}
