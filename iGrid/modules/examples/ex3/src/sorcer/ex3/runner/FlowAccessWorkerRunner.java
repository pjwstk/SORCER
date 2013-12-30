package sorcer.ex3.runner;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.requestor.ServiceRequestor;
import sorcer.core.signature.NetSignature;
import sorcer.ex2.requestor.Works;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;
import sorcer.util.Sorcer;

@SuppressWarnings("rawtypes")
public class FlowAccessWorkerRunner extends ServiceRequestor {

	public Exertion getExertion(String... args) throws ExertionException {
		String requestorName = getProperty("requestor.name");
		String pn1, pn2, pn3;
        pn1 = Sorcer.getSuffixedName(getProperty("provider.name.1"));
        pn2 = Sorcer.getSuffixedName(getProperty("provider.name.2"));
        pn3 = Sorcer.getSuffixedName(getProperty("provider.name.3"));
        Job job = null;
        try {
            Context context1 = new ServiceContext("work1");
            context1.putValue("requestor/name", requestorName);
            context1.putValue("requestor/operand/1", 1);
            context1.putValue("requestor/operand/2", 1);
            context1.putValue("requestor/work", Works.work1);
            context1.putValue("to/provider/name", pn1);

            Context context2 = new ServiceContext("work2");
            context2.putValue("requestor/name", requestorName);
            context2.putValue("requestor/operand/1", 2);
            context2.putValue("requestor/operand/2", 2);
            context2.putValue("requestor/work", Works.work2);
            context2.putValue("to/provider/name", pn2);

            Context context3 = new ServiceContext("work3");
            context3.putValue("requestor/name", requestorName);
            context3.putValue("requestor/operand/1", 3);
            context3.putValue("requestor/operand/2", 3);
            context3.putValue("requestor/work", Works.work3);
            context3.putValue("to/provider/name", pn3);


			NetSignature signature1 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class, pn1);
			NetSignature signature2 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class, pn2);
			NetSignature signature3 = new NetSignature("doWork",
					sorcer.ex2.provider.Worker.class, pn3);

			Task task1 = new NetTask("work1", signature1, context1);
			task1.setExecTimeRequested(true);
			Task task2 = new NetTask("work2", signature2, context2);
			Task task3 = new NetTask("work3", signature3, context3);
			job = new NetJob("flow");
			job.setExecTimeRequested(true);
			job.addExertion(task1);
			job.addExertion(task2);
			job.addExertion(task3);
						
			// PUSH or PULL provider access
			boolean isPushAccess = getProperty("provider.access.type", "PUSH").equals("PUSH");
			if (isPushAccess)
				job.setAccessType(Access.PUSH);
			else
				job.setAccessType(Access.PULL);
			
			// Exertion control flow PARallel or SEQential
			boolean iSequential = getProperty("provider.control.flow", "SEQUENTIAL").equals("SEQUENTIAL");
			if (iSequential)
				job.setFlowType(Flow.SEQ);
			else
				job.setFlowType(Flow.PAR);
			
			logger.info("isPushAccess: " + isPushAccess + " iSequential: " + iSequential);
		} catch (Exception e) {
			throw new ExertionException("Failed to create exertion", e);
		}
		return job;
	}

}