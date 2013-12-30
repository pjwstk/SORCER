package junit.sorcer.core.exertion;

//import com.gargoylesoftware,base,testing,TestUtil;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.co.operator.entry;
import static sorcer.eo.operator.args;
import static sorcer.eo.operator.configuration;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.deploy;
import static sorcer.eo.operator.exceptions;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.output;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.print;
import static sorcer.eo.operator.put;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.trace;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.par;
import static sorcer.po.operator.pars;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import junit.sorcer.core.provider.Adder;
import junit.sorcer.core.provider.AdderImpl;
import junit.sorcer.core.provider.Multiply;

import org.junit.Test;

import sorcer.core.context.ServiceContext;
import sorcer.core.context.model.par.Par;
import sorcer.core.exertion.ObjectTask;
import sorcer.core.signature.ObjectSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Provision;
import sorcer.service.Strategy.Wait;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class TaskTest {
	private final static Logger logger = Logger.getLogger(TaskTest.class
			.getName());

	static {
		ServiceExertion.debug = true;
		System.setProperty("java.util.logging.config.file",
				Sorcer.getHome() + "/configs/sorcer.logging");
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "arithmetic-beans.jar" });
	}

	@Test
	public void freeArithmeticTaskTest() throws ExertionException, SignatureException, ContextException {
		//to test tracing of execution enable ServiceExertion.debug 		
		Exertion task = task("add",
				sig("add"),
				context(in("arg/x1"), in("arg/x2"),
						result("result/y")));
		
		logger.info("get task: " + task);
		logger.info("get context: " + context(task));
		
		Object val = value(task, in("arg/x1", 20.0), in("arg/x2", 80.0),
				strategy(sig("add", AdderImpl.class), Access.PUSH, Wait.YES));
		
		logger.info("get value: " + val);
		assertEquals("Wrong value for 100", val, 100.0);
	}
	
	@Test
	public void arithmeticTaskTest() throws ExertionException, SignatureException, ContextException, RemoteException {
		//to test tracing of execution enable ServiceExertion.debug 
		ServiceExertion.debug = true;
		
		Task task = task("add",
				sig("add", AdderImpl.class),
				context(in("arg/x1", 20.0), in("arg/x2", 80.0),
						result("result/y")));
		
		// EXERTING
		task = exert(task);
		logger.info("exerted: " + task);
		assertTrue("Wrong value for 100.0", (Double)get(task) == 100.0);
		print(exceptions(task));
		assertTrue(exceptions(task).size() == 0);
		print(trace(task));
		
		// EVALUATING
		put(task, entry("result/y", Context.none));
		print(task);
		
		double val = (Double)value(task);
		//logger.info("get value: " + val);
		assertTrue("Wrong value for 100.0", val == 100.0);
		//logger.info("exec trace: " + trace(task));
		//logger.info("trace  size: " + trace(task).size());
		//assertTrue(trace(task).size() == 1);
		logger.info("exceptions: " + exceptions(task));
		//assertTrue(exceptions(task).size() == 0);

//		val = (Double)get(task, "result/y");
//		//logger.info("get value: " + val);
//		assertTrue("Wrong value for 100.0", val == 100.0);
//		
//		task = exert(task);
//		val = (Double)get(context(task), "result/y");
//		//logger.info("get value: " + val);
//		assertTrue("Wrong value for 100.0", val == 100.0);
//		//assertTrue(trace(task).size() == 2);
//		//           assertTrue(exceptions(task).size() == 0);
//		
//		put(task, entry("arg/x1", 1.0), entry("arg/x2", 5.0));
//		val = (Double)value(task);
//		logger.info("evaluate: " + val);
//		assertTrue("Wrong value for 6.0", val == 6.0);
//				
//		val = (Double)value(task, entry("arg/x1", 2.0), entry("arg/x2", 10.0));
//		logger.info("evaluate: " + val);
//		assertTrue("Wrong value for 12.0", val == 12.0);
//		
//		logger.info("task context: " + context(task));
//		logger.info("get value: " + get(task));
//		assertTrue("Wrong value for 12.0", get(task).equals(12.0));
	}

	@Test
	public void exertObjectTaskTest() throws Exception {
		ServiceExertion.debug = true;
		ObjectTask objTask = new ObjectTask("t4", new ObjectSignature("multiply", Multiply.class, double[].class));
		ServiceContext cxt = new ServiceContext();
		Object arg = new double[] { 10.0, 50.0 };
		//cxt.setReturnPath("result/y").setArgs(new double[] {10.0, 50.0});
		cxt.setReturnPath("result/y").setArgs(arg);
		objTask.setContext(cxt);
		
		//logger.info("objTask value: " + value(objTask));
		assertEquals("Wrong value for 500.0", value(objTask), 500.0);
		
		ObjectTask objTask2 = (ObjectTask)task("t4", sig("multiply", new Multiply(), double[].class), 
				context(args(new double[] {10.0, 50.0}), result("result/y")));
		//logger.info("objTask2 value: " + value(objTask2));
		assertEquals("Wrong value for 500.0", value(objTask2), 500.0);
	}
	
	@Test
	public void t5_Test() throws Exception {
		Task t5 = task("t5",
				sig(invoker("x2 + x3", par("x2", 20.0), par("x3", 80.0))),
				context(result("result/y")));

		//logger.info("t5: " + value(t5));
		assertEquals("Wrong value for 100.0", value(t5), 100.0);
	}

	@Test
	public void t4_TaskTest() throws Exception {
		Task t4 = task("t4", sig("multiply", new Multiply(), double[].class),
				context(args(new double[] { 10.0, 50.0 }), result("result/y")));

		//logger.info("t4: " + value(t4));
		assertEquals("Wrong value for 500.0", value(t4), 500.0);
	}

	@Test
	public void t3_TaskTest() throws Exception {
		Par x3 = par("x3", invoker("x3-e", "x1 - x2", pars("x1", "x2")));
		Task t3 = task(
				"t3",
				sig(x3),
				context("subtract", in(path("x1"), 40.0),
						in(path("x2"), 10.0), result(path("result/y"))));
		
		//logger.info("t3: " + value(t3));
		assertEquals("Wrong value for 30.0", value(t3), 30.0);

	}
	
	@Test
	public void t3_Task2Test() throws Exception {
		// testing with getValueEndsWith for vars in the context with prefixed paths
		Par x3 = par("x3", invoker("x3-e", "x1 - x2", pars("x1", "x2")));
		Task t3 = task(
				"t3",
				sig(x3),
				context("subtract", in(path("arg/x1"), 40.0),
						in(path("arg/x2"), 10.0), result(path("result/y"))));

		//logger.info("t3: " + value(t3));
		assertEquals("Wrong value for 30.0", value(t3), 30.0);

	}
	
	@Test
	public void deployTest() throws Exception {
		Task t5 = task("f5",
			sig("add", Adder.class,
					deploy(configuration("bin/sorcer/test/arithmetic/configs/AdderProviderConfig.groovy"))),
				context("add", input("arg/x3", 20.0d), input("arg/x4", 80.0d),
							output("result/y")),
				strategy(Provision.YES));
		logger.info("t5 is provisionable: " + t5.isProvisionable());
		assertTrue(t5.isProvisionable());
	}
}
	
