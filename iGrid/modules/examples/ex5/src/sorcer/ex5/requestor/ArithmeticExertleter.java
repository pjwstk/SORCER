package sorcer.ex5.requestor;

import static org.junit.Assert.assertEquals;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.SorcerConstants;
import sorcer.core.context.PositionalContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Evaluation;
import sorcer.service.Invocation;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes" })
public class ArithmeticExertleter implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(NetArithmeticReq.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ex5-arithmetic-beans.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	@Test
	public void evaluateArithmeticExertleter() throws Exception {
		
		NetSignature signature = new NetSignature("getValue", Evaluation.class);
		Task task = new NetTask("eval", signature);
		Task result = (Task)task.exert();		
		Context out = (Context)result.getReturnValue();

		logger.info("1job1task/subtract/result/value: " 
				+ out.getValue("1job1task/subtract/result/value"));
		assertEquals(out.getValue("1job1task/subtract/result/value"), 1210.0);
	}
	
	@Test
	public void exertArithmeticExertleter() throws Exception {
		
		Context addContext = new PositionalContext("add");
		addContext.putInValue("arg1/value", 90.0);
		addContext.putInValue("arg2/value", 110.0);
		
		Context multiplyContext = new PositionalContext("multiply");
		multiplyContext.putInValue("arg1/value", 10.0);
		multiplyContext.putInValue("arg2/value", 70.0);

		ServiceContext invokeContext = new ServiceContext("invoke");
		invokeContext.putLink("add", addContext, "");
		invokeContext.putLink("multiply", multiplyContext, "");
		
		NetSignature signature = new NetSignature("invoke", Invocation.class);
		
		Task task = new NetTask("invoke", signature, invokeContext);
		Task result = (Task)task.exert();		
		Context out = result.getContext();
//		logger.info("result context: " + out);

		logger.info("1job1task/subtract/result/value: " + out.getValue("1job1task/subtract/result/value"));
		assertEquals(out.getValue("1job1task/subtract/result/value"), 500.0);
	}

	@Test
	public void exertArithmeticExertleterWithSubcontexts() throws Exception {
		
		Context addContext = new PositionalContext("add");
		addContext.putInValue("arg1/value", 80.0);
		
		Context multiplyContext = new PositionalContext("multiply");
		multiplyContext.putInValue("arg1/value", 20.0);

		ServiceContext invokeContext = new ServiceContext("invoke");
		invokeContext.putLink("add", addContext, "");
		invokeContext.putLink("multiply", multiplyContext, "");
		
		NetSignature signature = new NetSignature("invoke", Invocation.class);
		
		Task task = new NetTask("invoke", signature, invokeContext);
		Task result = (Task)task.exert();		
		Context out = result.getContext();
//		logger.info("result context: " + out);

		logger.info("1job1task/subtract/result/value: " + out.getValue("1job1task/subtract/result/value"));
		assertEquals(out.getValue("1job1task/subtract/result/value"), 1210.0);
	}
}
