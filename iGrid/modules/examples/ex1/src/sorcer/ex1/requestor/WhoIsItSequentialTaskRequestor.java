package sorcer.ex1.requestor;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;

public class WhoIsItSequentialTaskRequestor {

	private static Logger logger = Log.getTestLog();

	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system environment from configs/sorcer.env
		Sorcer.getEnvProperties();
		
		int tally = 3;
		if (args.length == 2)
			tally = new Integer(args[1]);
		// create a service task and execute it 'tally' times
		Exertion task = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < tally; i++) {
			task = new WhoIsItSequentialTaskRequestor().getExertion();
			((ServiceExertion)task).setName(task.getName() + "-" + i);
			task = task.exert(null);
			logger.info("got sequentially executed task: " + task.getName());
		}
		long end = System.currentTimeMillis();
		System.out.println("Execution time for " + tally + " sequential tasks : " + (end - start) + " ms.");
	}

	public Exertion getExertion() throws Exception {
		String hostname;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message", new RequestorMessage("SORCER"));
		context.putValue("requestor/hostname", hostname);
		
		NetSignature signature = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class);

		Task task = new NetTask("Who Is It?", signature, context);
		return task;
	}
}
