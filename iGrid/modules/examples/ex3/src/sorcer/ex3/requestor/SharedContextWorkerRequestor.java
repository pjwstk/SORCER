package sorcer.ex3.requestor;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.requestor.ServiceRequestor;
import sorcer.core.signature.NetSignature;
import sorcer.ex2.requestor.Works;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Sorcer;

@SuppressWarnings("rawtypes")
public class    SharedContextWorkerRequestor extends ServiceRequestor {
	ServiceContext context = new ServiceContext();
	
	@SuppressWarnings("unchecked")
	public Exertion getExertion(String... args) throws ExertionException {
		String requestorName = getProperty("requestor.name");
		String prefix1 = getProperty("value.prefix.1");
		String prefix2 = getProperty("value.prefix.2");
logger.info("ZZZZZZZZZZZ init prefix1: " + prefix1);
logger.info("ZZZZZZZZZZZ init prefix2: " + prefix2);

		// define requestor data
		Task batch = null;
		try {
			context.putValue("requestor/name", requestorName);
			
			context.putInValue(prefix1 + "/requestor/operand/1", 20);
			context.putInValue(prefix1 + "/requestor/operand/2", 80);
            context.putInValue(prefix1 + "/requestor/work", Works.work1);
            context.putOutValue("requestor/operand/1", Context.none, "par|"+ prefix1);
			
			context.putInValue(prefix2 + "/requestor/operand/1", 10);
			context.putInValue(prefix2 + "/requestor/operand/2", 50);
            context.putInValue(prefix2 + "/requestor/work", Works.work2);
			context.putOutValue("requestor/operand/2", Context.none, "par|"+ prefix2);
			
            context.putInValue("requestor/work", Works.work3);
			context.putOutValue("provider/result", Context.none);

			// define required services
			NetSignature signature1 = new NetSignature("doWork#" + prefix1,
					sorcer.ex2.provider.Worker.class, Sorcer.getActualName("Worker1"));
			signature1.setType(Signature.Type.PRE);
			NetSignature signature2 = new NetSignature("doWork#" + prefix2,
					sorcer.ex2.provider.Worker.class, Sorcer.getActualName("Worker2"));
			signature2.setType(Signature.Type.PRE);
			NetSignature signature3 = new NetSignature("doWork", 
					sorcer.ex2.provider.Worker.class, Sorcer.getActualName("Worker3"));

			// define tasks
			batch = new NetTask("batch work", signature1, context);
			batch.addSignature(signature2);
			batch.addSignature(signature3);
		} catch (Exception e) {
			throw new ExertionException("Failed to create exertion", e);
		}
		batch.setExecTimeRequested(true);

		return batch;
	}

}