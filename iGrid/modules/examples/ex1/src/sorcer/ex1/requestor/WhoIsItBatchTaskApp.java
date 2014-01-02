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
import sorcer.service.Task;
import sorcer.service.Signature.Type;
import sorcer.util.Log;
import sorcer.util.Sorcer;

@SuppressWarnings("rawtypes")
public class WhoIsItBatchTaskApp {

	private static Logger logger = Log.getTestLog();

	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system environment from configs/sorcer.env
		Sorcer.getEnvProperties();
		// get the queried provider name
		String providerName = Sorcer.getActualName(args[0]);
		logger.info("Who is provider \"" + providerName + "\"?");
		
		Exertion result = new WhoIsItBatchTaskApp().getExertion(providerName)
				.exert();
		logger.info("Exceptions: \n" + result.getExceptions());
		logger.info("Trace: \n" + result.getTrace());
		logger.info("Ouptut context: \n" + result.getContext());
	}
	
	public Exertion getExertion(String providername) throws Exception {
		String hostname, ipAddress;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();
		ipAddress = inetAddress.getHostAddress();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message", new RequestorMessage("SORCER"));
		context.putValue("requestor/hostname", hostname);
		context.putValue("requestor/address", ipAddress);
		
		Signature signature1 = new NetSignature("getHostAddress",
				sorcer.ex1.WhoIsIt.class, providername, Type.PRE);
		Signature signature2 = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class, providername, Type.SRV);
		Signature signature3 = new NetSignature("getCanonicalHostName",
				sorcer.ex1.WhoIsIt.class, providername, Type.POST);
		Signature signature4 = new NetSignature("getTimestamp",
				sorcer.ex1.WhoIsIt.class, providername, Type.POST);
		
		Task task = new NetTask("Who Is It?",  new Signature[] { signature1, signature2, signature3, signature4 }, context);

		return task;
	}
}
