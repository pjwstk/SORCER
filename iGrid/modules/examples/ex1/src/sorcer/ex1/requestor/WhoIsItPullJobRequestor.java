package sorcer.ex1.requestor;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;

public class WhoIsItPullJobRequestor {

	private static Logger logger = Log.getTestLog();
	
	public static void main(String... args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system environment from configs/sorcer.env
		Sorcer.getEnvProperties();
		ServiceExertion.debug = true;
		// get the queried provider name
		String providerName1 = args[0];
		String providerName2 = args[1];
		String jobberName = args[2];
		
		logger.info("Who is \"" + providerName1 + "\"?");
		logger.info("Who is \"" + providerName2 + "\"?");
		
		NetJob ex = (NetJob)new WhoIsItPullJobRequestor().getExertion(providerName1, providerName2);
		Exertion result = ex.exert(null, jobberName);
		
		ServiceExertion.debug = true;
		logger.info("Job exceptions job: \n" + result.getExceptions());
		logger.info("Output job: \n" + result);
		logger.info("Output context1: \n" + result.getContext("Who Is It1?"));
		logger.info("Output context2: \n" + result.getContext("Who Is It2?"));
	}

	private Exertion getExertion(String providerName1, String providerName2) throws Exception {
		String hostname, ipAddress;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();
		ipAddress = inetAddress.getHostAddress();

		Context context1 = new ServiceContext("Who Is It?");
		context1.putValue("requestor/message", new RequestorMessage(providerName1));
		context1.putValue("requestor/hostname", hostname);

		Context context2 = new ServiceContext("Who Is It?");
		context2.putValue("requestor/message", new RequestorMessage(providerName2));
		context2.putValue("requestor/hostname", hostname);
		context2.putValue("requestor/address", ipAddress);
		
		NetSignature signature1 = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class, providerName1);
		NetSignature signature2 = new NetSignature("getHostAddress",
				sorcer.ex1.WhoIsIt.class, providerName2);

		Task task1 = new NetTask("Who Is It1?", signature1, context1);
		Task task2 = new NetTask("Who Is It2?", signature2, context2);
		Job job = new NetJob();
		job.addExertion(task1);
		job.addExertion(task2);
	
		// PUSH or PULL provider access
		job.setAccess(Access.PULL);
		// Exertion control flow PARALLEL or SEQUENTIAL
		job.setFlow(Flow.PAR);
		
		return job;
	}
}
