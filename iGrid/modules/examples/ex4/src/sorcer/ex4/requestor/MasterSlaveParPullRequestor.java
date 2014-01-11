package sorcer.ex4.requestor;

import sorcer.core.context.ServiceContext;
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
import sorcer.service.SignatureException;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MasterSlaveParPullRequestor extends ServiceRequestor {

	/* (non-Javadoc)
	 * @see sorcer.core.requestor.ExertionRunner#getExertion(java.lang.String[])
	 */
	@Override
	public Exertion getExertion(String... args) 
		throws ExertionException, ContextException, SignatureException {
		String requestorName = getProperty("requestor.name");

        // define requestors data
        Context context1 = new ServiceContext("work1");
        context1.putValue("requestor/name", requestorName);
        context1.putValue("requestor/operand/1", 20);
        context1.putValue("requestor/operand/2", 30);
        context1.putValue("requestor/work", Works.work1);
        context1.putOutValue("provider/result", null);

        Context context2 = new ServiceContext("work2");
        context2.putValue("requestor/name", requestorName);
        context2.putValue("requestor/operand/1", 10);
        context2.putValue("requestor/operand/2", 12);
        context2.putValue("requestor/work", Works.work2);
        context2.putOutValue("provider/result", null);

        Context context3 = new ServiceContext("work3");
        context3.putValue("requestor/name", requestorName);
        context3.putInValue("requestor/operand/1", 80);
        context3.putInValue("requestor/operand/2", 60);
        context3.putValue("requestor/work", Works.work3);
        context3.putOutValue("provider/result", null);

        Context context4 = new ServiceContext("work4");
        context4.putValue("requestor/name", requestorName);
        context4.putInValue("requestor/operand/1", null);
        context4.putInValue("requestor/operand/2", null);
        context4.putInValue("requestor/operand/3", null);
        context4.putOutValue("provider/result", null);
        context4.putValue("requestor/work", Works.work4);

        // pass the parameters from one dataContext to the next dataContext
        //mapped parameter should be marked via in, out, or inout paths
        context1.map("provider/result", "requestor/operand/1", context4);
        context2.map("provider/result", "requestor/operand/2", context4);
        context3.map("provider/result", "requestor/operand/3", context4);

        // define required services
        NetSignature signature1 = new NetSignature("doWork",
                sorcer.ex2.provider.Worker.class);
        NetSignature signature2 = new NetSignature("doWork",
                sorcer.ex2.provider.Worker.class);
        NetSignature signature3 = new NetSignature("doWork",
                sorcer.ex2.provider.Worker.class);
        NetSignature signature4 = new NetSignature("doWork",
                sorcer.ex2.provider.Worker.class);
        // define tasks
        Task task1 = new NetTask("work1", signature1, context1);
        Task task2 = new NetTask("work2", signature2, context2);
        Task task3 = new NetTask("work3", signature3, context3);
        Task task4 = new NetTask("work4", signature4, context4);

        // define a job
        Job job = new NetJob("master/slave PAR/PULL");
        job.addExertion(task1);
        job.addExertion(task2);
        job.addExertion(task3);
        job.addExertion(task4);

        // define a job control strategy
        // use the catalog to delegate the tasks
        // define a job control strategy
        // use the catalog to delegate the tasks
        job.setAccessType(Access.PULL);
        // either parallel or sequential
        job.setFlowType(Flow.PAR);
        // time the job execution
        job.setExecTimeRequested(true);
        // job can be monitored
        job.setMonitored(false);
        // wait for results or do it asynchronously
        job.setMasterExertion(task4);
        
		return job;
	}

	public void postprocess(String... args) throws ExertionException, ContextException {
		super.postprocess(args);
		logger.info("<<<<<<<<<< Exex time: " + exertion.getControlContext().getExecTime());		
	}
	
}