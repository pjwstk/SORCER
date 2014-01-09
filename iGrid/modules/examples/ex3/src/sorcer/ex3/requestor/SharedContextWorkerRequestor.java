package sorcer.ex3.requestor;

import java.rmi.RemoteException;

import sorcer.core.context.SharedAssociativeContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.requestor.ServiceRequestor;
import sorcer.core.signature.NetSignature;
import sorcer.ex2.requestor.Works;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;
import sorcer.util.SorcerEnv;

public class SharedContextWorkerRequestor extends ServiceRequestor {
	SharedAssociativeContext context = new SharedAssociativeContext(SorcerEnv.getActualSpaceName());
	
	public Exertion getExertion(String... args) throws ExertionException {
		String requestorName = getProperty("requestor.name");
		String prefix1 = getProperty("value.prefix.1");
		String prefix2 = getProperty("value.prefix.2");
		String prefix3 = getProperty("value.prefix.3");
        // A workaround because prefixes don't exist in Works anymore
        prefix1="";
        prefix2="";
        prefix3="";

		// define requestor data
		Job job = null;
		try {
			context.putValue("requestor/name", requestorName);
			
			context.putValue("requestor/operand/" + prefix1 + "1", 1);
			context.putValue("requestor/operand/" + prefix1 + "2", 1);
            context.putValue("requestor/work", Works.work1);
            context.writeValue("provider/result/" + prefix1 + "0", Context.none);
			
			context.putValue("requestor/operand/" + prefix2 + "1", 1);
			context.putValue("requestor/operand/" + prefix2 + "2", 1);
            context.putValue("requestor/work", Works.work2);
			context.writeValue("provider/result/" + prefix2 + "0", Context.none);
			
			context.aliasValue("requestor/operand/" + prefix3 + "1", "provider/result/" + prefix1 + "0");
			context.aliasValue("requestor/operand/" + prefix3 + "2", "provider/result/" + prefix2 + "0");
            context.putValue("requestor/work", Works.work3);
			context.putValue("provider/result/" + prefix3 + "0", 0);

			// define required services
			NetSignature signature1 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class);
			NetSignature signature2 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class);
			NetSignature signature3 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class);

			// define tasks
			Task task1 = new NetTask("work1", signature1, context);
			Task task2 = new NetTask("work2", signature2, context);
			Task task3 = new NetTask("work3", signature3, context);

			// define a job
			job = new NetJob("shared");
			job.addExertion(task1);
			job.addExertion(task2);
			job.addExertion(task3);
		} catch (Exception e) {
			throw new ExertionException("Failed to create exertion", e);
		}
		// define a job control strategy
		// use the catalog to delegate the tasks
		job.setAccessType(Access.PULL);
		// either parallel or sequential
		job.setFlowType(Flow.PAR);
		// time the job execution
		job.setExecTimeRequested(true);

		return job;
	}

	public void postprocess() {
		try {
			context.unshare();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ContextException e) {
			e.printStackTrace();
		}
		logger.info("<<<<<<<<<< Ouput exertion: \n" + exertion);
	}
}