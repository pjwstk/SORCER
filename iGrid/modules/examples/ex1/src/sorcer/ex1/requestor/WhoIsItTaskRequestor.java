package sorcer.ex1.requestor;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Signature;
import sorcer.service.Strategy.Access;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;

public class WhoIsItTaskRequestor {

	private static Logger logger = Log.getTestLog();

	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system environment from configs/sorcer.env
		Sorcer.getEnvProperties();
		Access providerAccess = Access.PUSH;
		if (args.length == 1) {
			if (!args[0].equals(Access.PUSH))
				providerAccess = Access.PULL;
		}
		Exertion result = new WhoIsItTaskRequestor().getExertion(providerAccess)
				.exert(null);
		logger.info("Output context: \n" + result.getContext());
	}

	public Exertion getExertion(Access providerAccess) throws Exception {
		String hostname;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message", new RequestorMessage("friend"));
		context.putValue("requestor/hostname", hostname);
		
		NetSignature signature = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class);

		Task task = new NetTask("Who Is It?", signature, context);
		task.setAccess(providerAccess);
		return task;
	}
}
