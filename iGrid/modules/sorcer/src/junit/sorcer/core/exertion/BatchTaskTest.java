package junit.sorcer.core.exertion;

//import com.gargoylesoftware,base,testing,TestUtil;
import static org.junit.Assert.assertEquals;
import static sorcer.co.operator.from;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.type;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.pars;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import junit.sorcer.core.provider.AdderImpl;
import junit.sorcer.core.provider.MultiplierImpl;
import junit.sorcer.core.provider.SubtractorImpl;

import org.junit.Test;

import sorcer.service.Signature;
import sorcer.service.Signature.Direction;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */

public class BatchTaskTest {
	private final static Logger logger = Logger.getLogger(TaskTest.class
			.getName());

	static {
		System.setProperty("java.util.logging.config.file",
				Sorcer.getHome() + "/configs/sorcer.logging");
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "arithmetic-beans.jar" });
	}

	@Test
	public void batchTask1aTest() throws Exception {
		// batch for the composition f1(f2(f3((x1, x2), f4(x1, x2)), f5(x1, x2))
		// testing with getValueEndsWith for vars in the context with prefixed paths
		Task batch1 = task(
				"batch1",
				type(sig(invoker("x1 * x2", pars("x1", "x2")), result("x5")), Signature.PRE),
				type(sig(invoker("x3 + x4", pars("x3","x4")), result("x6")), Signature.PRE),
				type(sig(invoker("x5 - x6", pars("x5", "x6")), result("result/y")), Signature.SRV),
				context(in("arg/x1", 10.0), in("arg/x2", 50.0), 
						in("arg/x3", 20.0), in("arg/x4", 80.0)));
		
		logger.info("task batch1: " + batch1.getClass());

		//logger.info("task t: " + value(batch1));
		assertEquals("Wrong value for 400.0", value(batch1), 400.0);
	}
	
	@Test
	public void batchTask1bTest() throws Exception {
		// batch for the composition f1(f2(f3((x1, x2), f4(x1, x2)), f5(x1, x2))
		// testing with getValueEndsWith for vars in the context with prefixed paths
		Task batch1 = task(
				"batch1",
				sig(invoker("x1 * x2", pars("x1", "x2")), result("x5")),
				sig(invoker("x3 + x4", pars("x3","x4")), result("x6")),
				sig(invoker("x5 - x6", pars("x5", "x6")), result("result/y")),
				context(in("arg/x1", 10.0), in("arg/x2", 50.0), 
						in("arg/x3", 20.0), in("arg/x4", 80.0)));
		
		//logger.info("task t: " + value(batch1));
		assertEquals("Wrong value for 400.0", value(batch1), 400.0);
	}
	
	@Test
	public void batchTask2Test() throws Exception {
		// PREPROCESS, POSTPROCESS with SERVICE signatures and with invoker, evaluation tasks
		Task batch2 = task(
				"batch2",
				type(sig(invoker("x1 * x2", pars("x1", "x2")), result("x5")), Signature.PRE),
				type(sig(invoker("x4 + x3", pars("x3","x4")), result("x6")), Signature.PRE),
				sig(invoker("x5 - x6", pars("x5", "x6")), result("result/y", Direction.IN)),
				type(sig("add", AdderImpl.class, result("result/z")), Signature.POST), 
				context(in("arg/x1", 10.0), in("arg/x2", 50.0), 
						in("arg/x3", 20.0), in("arg/x4", 80.0)));
		
		batch2 = exert(batch2);
		//logger.info("task result/y: " + get(batch2, "result/y"));
		assertEquals("Wrong value for 400.0", get(batch2, "result/y"), 400.0);

		// sums up all inputs and the return value of y: [400.0, 80.0, 20.0, 50.0, 10.0]]
		//logger.info("task result/z: " + get(batch2, "result/z"));
		assertEquals("Wrong value for 560.0", get(batch2, "result/z"), 560.0);
	}
	
	@Test
	public void batchTask3Test() throws Exception {
		// batch for the composition f1(f2(f3((x1, x2), f4(x1, x2)), f5(x1, x2))
		// shared context with named paths
		Task batch3 = task("batch3",
				type(sig("multiply", MultiplierImpl.class, result("subtract/x1", Direction.IN)), Signature.PRE),
				type(sig("add", AdderImpl.class, result("subtract/x2", Direction.IN)), Signature.PRE),
				sig("subtract", SubtractorImpl.class, result("result/y", from("subtract/x1", "subtract/x2"))),
				context(in("multiply/x1", 10.0), in("multiply/x2", 50.0), 
						in("add/x1", 20.0), in("add/x2", 80.0)));
		
		logger.info("task getSignatures:" + batch3.getSignatures());
				
		batch3 = exert(batch3);
//		//logger.info("task result/y: " + get(batch3, "result/y"));
//		assertEquals("Wrong value for 400.0", get(batch3, "result/y"), 400.0);
	}
	
	
	@Test
	public void batchTask4Test() throws Exception {
		// batch for the composition f1(f2(f3((x1, x2), f4(x1, x2)), f5(x1, x2))
		// shared context with prefixed paths
		Task batch3 = task("batch3",
				type(sig("multiply#op1", MultiplierImpl.class, result("op3/x1", Direction.IN)), Signature.PRE),
				type(sig("add#op2", AdderImpl.class, result("op3/x2", Direction.IN)), Signature.PRE),
				sig("subtract", SubtractorImpl.class, result("result/y", from("op3/x1", "op3/x2"))),
				context(in("op1/x1", 10.0), in("op1/x2", 50.0), 
						in("op2/x1", 20.0), in("op2/x2", 80.0)));
		
		batch3 = exert(batch3);
		//logger.info("task result/y: " + get(batch3, "result/y"));
		assertEquals("Wrong value for 400.0", get(batch3, "result/y"), 400.0);
	}
}
	
