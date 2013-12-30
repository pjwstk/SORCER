package sorcer.arithmetic.junit;

import static org.junit.Assert.assertEquals;
import static sorcer.co.operator.from;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.jobContext;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.type;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.set;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import junit.sorcer.core.provider.MultiplierImpl;
import junit.sorcer.core.provider.SubtractorImpl;

import org.junit.Test;

import sorcer.arithmetic.provider.AdderBuilder;
import sorcer.arithmetic.provider.AdderImpl;
import sorcer.arithmetic.provider.ArithmeticImpl;
import sorcer.core.SorcerConstants;
import sorcer.core.context.model.par.ParModel;
import sorcer.core.provider.jobber.ServiceJobber;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Signature.Direction;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArithmeticNoNetTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ArithmeticNoNetTest.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ju-arithmetic-beans.jar",
				"sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :"
				+ System.getProperty("java.class.path"));
	}


	@Test
	public void adderTaskTest() throws Exception {
		Task t5 = task(
				"t5",
				sig("add", AdderImpl.class),
				context("add", in("arg, x1", 20.0), in("arg, x2", 80.0),
						result("result, y")));

		t5 = exert(t5);
		logger.info("t5 context: " + context(t5));
		logger.info("t5 value: " + get(t5));
		assertEquals("Wrong value for 100.0", get(t5), 100.0);
	}

	@Test
	public void arithmeticMultiServiceTest() throws Exception {
		Task t5 = task(
				"t5",
				sig("add", ArithmeticImpl.class),
				context("add", in("arg, x1", 20.0), in("arg, x2", 80.0),
						result("result, y")));

		assertEquals("Wrong value for 100.0", value(t5), 100.0);
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
		
		batch3 = exert(batch3);
		//logger.info("task result/y: " + get(batch3, "result/y"));
		assertEquals("Wrong value for 400.0", get(batch3, "result/y"), 400.0);
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
	
	
	@Test
	public void exertJobPushParTest() throws Exception {
		Job job = createJob(Flow.PAR, Access.PUSH);
		job = exert(job);
		//logger.info("job j1: " + job);
		//logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		//logger.info("job j1 value @ j1/t3/result/y = " + get(job, "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}
	
	@Test
	public void exertJobPushSeqTest() throws Exception {
		Job job = createJob(Flow.SEQ, Access.PUSH);
		job = exert(job);
		//logger.info("job j1: " + job);
		//logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		//logger.info("job j1 value @ j1/t3/result/y = " + get(job, "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}
	
	// two level job composition with PULL and PAR execution
	private Job createJob(Flow flow, Access access) throws Exception {
		Task t3 = task("t3", sig("subtract", SubtractorImpl.class), 
				context("subtract", in("arg/x1", null), in("arg/x2", null),
						out("result/y", null)));

		Task t4 = task("t4", sig("multiply", MultiplierImpl.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						out("result/y", null)));

		Task t5 = task("t5", sig("add", AdderImpl.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						out("result/y", null)));
		
		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		//Job job = job("j1",
		Job job = job("j1", sig("service", ServiceJobber.class), 
				//job("j2", t4, t5),
				job("j2", t4, t5, sig("service", ServiceJobber.class), 
						strategy(flow, access)), 
				t3,
				pipe(out(t4, "result/y"), in(t3, "arg/x1")),
				pipe(out(t5, "result/y"), in(t3, "arg/x2")));
				
		return job;
	}
	
	@Test
	public void addModelTest() throws Exception {
		ParModel pm = AdderBuilder.getAdderModel();
		logger.info("x value: " + value(pm, "x"));
		logger.info("y value: " + value(pm, "y"));

		logger.info("adder value: " + value(pm, "add"));
		assertEquals(value(pm, "add"), 30.0);
		set(pm, "x", 20.0);
		assertEquals(value(pm, "add"), 40.0);
	}
}
