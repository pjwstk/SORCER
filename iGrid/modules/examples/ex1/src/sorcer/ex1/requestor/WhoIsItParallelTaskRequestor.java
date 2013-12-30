package sorcer.ex1.requestor;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionCallable;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;

public class WhoIsItParallelTaskRequestor {

	private static Logger logger = Log.getTestLog();

	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system environment from configs/sorcer.env
		Sorcer.getEnvProperties();
		int tally = 3;
		if (args.length == 1)
			tally = new Integer(args[0]);
		
		Exertion task = null;
		ExertionCallable ec = null;
		List<Future<Exertion>> fList = new ArrayList<Future<Exertion>>(tally);
		ExecutorService pool = Executors.newFixedThreadPool(tally);
		WhoIsItParallelTaskRequestor req = new WhoIsItParallelTaskRequestor();
		long start = System.currentTimeMillis();
		for (int i = 0; i < tally; i++) {
			task = req.getExertion();
			((ServiceExertion)task).setName(task.getName() + "-" + i);
			ec = new ExertionCallable(task);
			logger.info("exertion submit: " + task.getName());
			Future<Exertion> future = pool.submit(ec);
			fList.add(future);
		}
		pool.shutdown();
		for (int i = 0; i < tally; i++) {
			logger.info("got back task executed in parallel: " + fList.get(i).get().getName());
		}
		long end = System.currentTimeMillis();
		System.out.println("Execution time for " + tally + " parallel tasks : " + (end - start) + " ms.");
	}

	private Exertion getExertion() throws Exception {
		String hostname;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message",  new RequestorMessage("SORCER"));
		context.putValue("requestor/hostname", hostname);
		
		NetSignature signature = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class);

		Task task = new NetTask("Who Is It?",  signature, context);
		return task;
	}
}
