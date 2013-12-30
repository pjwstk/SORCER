package sorcer.ex1.requestor.bean;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.ex1.requestor.RequestorMessage;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Log;

public class WhoIsItBeanClient2 {

	private static Logger logger = Log.getTestLog();
	private static String providerName;
	
	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		if (args.length == 1)
			providerName = args[0];
		logger.info("providerName: " + providerName);
		
		Exertion result = new WhoIsItBeanClient2()
			.getExertion().exert(null);
		logger.info("<<<<<<<<<< Exceptions: \n" + result.getExceptions());
		logger.info("<<<<<<<<<< Trace list: \n" + result.getControlContext().getTrace());
		logger.info("<<<<<<<<<< Result: \n" + result);
	}

	private Exertion getExertion() throws Exception {
		String hostname, ipAddress;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();
		ipAddress = inetAddress.getHostAddress();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message", new RequestorMessage(
				"WhoIsIt Bean"));
		context.putValue("requestor/hostname", hostname);
		context.putValue("requestor/address", ipAddress);

		NetSignature signature = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class, providerName != null ? providerName : null);

		Task task = new NetTask("Who Is It?",signature, context);
		return task;
	}
}
