package sorcer.ex1.requestor;

import java.net.InetAddress;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.requestor.ServiceRequestor;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Log;

public class WhoIsItTaskRunner extends ServiceRequestor {

	private static Logger logger = Log.getTestLog();

	public Exertion getExertion(String... args) throws ExertionException {
		String hostname = null;
		String providerName = null;
		// define requestor data
		if (args.length == 2)
			providerName = args[1];
		Task task = null;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostname = inetAddress.getHostName();

			Context context = new ServiceContext("Who Is It?");
			context.putValue("requestor/message", new RequestorMessage("Unknown"));
			context.putValue("requestor/hostname", hostname);
			// if service provider name is given use it in the signature
			NetSignature signature = new NetSignature("getHostName",
					sorcer.ex1.WhoIsIt.class, providerName != null ? providerName : null);

			task = new NetTask("Who Is It?", signature, context);
			ServiceExertion.debug = true;
		} catch (Exception e) {
			throw new ExertionException("Failed to create exertion", e);
		}
		return task;
	}

}