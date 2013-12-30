package junit.sorcer.core.exertion;

//import com.gargoylesoftware,base,testing,TestUtil;
import static org.junit.Assert.assertEquals;
import static sorcer.eo.operator.args;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.par;
import static sorcer.po.operator.pars;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import junit.sorcer.core.provider.Multiply;
import junit.sorcer.core.provider.SubtractorImpl;

import org.junit.Before;
import org.junit.Test;

import sorcer.core.context.model.par.Par;
import sorcer.core.provider.jobber.ServiceJobber;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */

public class HybridJobTest {
	private final static Logger logger = Logger.getLogger(HybridJobTest.class
			.getName());

	static {
		System.setProperty("java.util.logging.config.file",
				Sorcer.getHome() + "/configs/sorcer.logging");
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "arithmetic-beans.jar" });
	}

	@Before
	public void setUp() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void exertHybridJob1Test() throws Exception {
		Exertion job = createJob1();
		//logger.info("created job: " + job);

		job = exert(job);
		logger.info("job context: " + context(job));

		logger.info("value at j1/t3/result/y: " + get(job, "j1/t3/result/y"));
		logger.info("value at t3, result/y: " + get(job, "t3", "result/y"));

		// absolute path
		assertEquals("Wrong value for 400.0", get(job, "j1/t3/result/y"), 400.0);
		//local t3 path
		assertEquals("Wrong value for 400.0", get(job, "t3", "result/y"), 400.0);
	}

	// two level job composition
	private Exertion createJob1() throws Exception {
		Par<?> x3 = par("x3", invoker("x3-e", "x1 - x2", pars("x1", "x2")));
		Task t3 = task("t3", sig(x3),
				context("subtract", in(path("arg/x1"), null),
						in(path("arg/x2"), null), result("result/y")));

		// Task t4 = task("t4", sig("multiply", Multiplier.class),
		// context("multiply", in(path("arg/x1"), 10.0), in("path(arg/x2"), 50.0),
		// out(path("result/y"), null)));

		Task t4 = task("t4", sig("multiply", new Multiply(), double[].class),
				context("multiply", args(new double[] { 10.0, 50.0 }), result("result/y")));
		// name("t4", t4);

		// Task t5 = task("t5", sig("add", Adder.class),
		// context("add", in(path("arg/x1"), 20.0), in(path("arg/x2"), 80.0),
		// out(path("result/y"), null)));

		Task t5 = task("t5",
				sig(invoker("x2 + x3", par("x2", 20.0), par("x3", 80.0))),
				context("add", result("result/y")));
		// name("t5", t5);

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		// Job j1= job("j1", job("j2", t4, t5, strategy(Flow.PARALLEL,
		// Access.PULL)), t3,
		
		// intra Jobber
		Job job = job("j1", sig("execute", ServiceJobber.class), 
				job("j2", sig("execute", ServiceJobber.class), t4, t5), 
				t3,
				pipe(out(t4, path("result/y")), in(t3, path("arg/x1"))),
				pipe(out(t5, path("result/y")), in(t3, path("arg/x2"))));
		
//		// net Jobber
//		Job job = job("j1", job("j2", t4, t5), t3,
//				pipe(out(t4, path("result/y")), in(t3, path("arg/x1"))),
//				pipe(out(t5, path("result/y")), in(t3, path("arg/x2"))));
//
		return job;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void exertHybridJob2Test() throws Exception {
		Job job = createJob2();
		//logger.info("created job: " + job);

		job = exert(job);
		//logger.info("job context: " + jobContext(job));
		
		//logger.info("value at j1/t3/result/y: " + value(job, "j1/t3/result/y"));
		//logger.info("value at t3, result/y: " + value(job, "t3", "result/y"));

		// absolute path
		assertEquals("Wrong value for 400.0", get(job, "j1/t3/result/y"), 400.0);
		//local t3 path
		assertEquals("Wrong value for 400.0", get(job, "t3", "result/y"), 400.0);
	}

	// two level job composition
	private Job createJob2() throws Exception {
		// to avoid spelling errors in test cases define instance variables
		String arg = "arg", result = "result";
		String x1 = "x1", x2 = "x2", y = "y";

		Task t3 = task(
				"t3",
				//sig("subtract", Subtractor.class),
				sig("subtract", SubtractorImpl.class),
				context("subtract", in(path(arg, x1), null),
						in(path(arg, x2), null), result("result/y")));

		// Task t4 = task("t4", sig("multiply", Multiplier.class),
		// context("multiply", in(path(arg, x1), 10.0), in(path(arg, x2), 50.0),
		// out(path(result, y), null)));

		Task t4 = task("t4", sig("multiply", new Multiply(), double[].class),
				context("multiply", args(new double[] { 10.0, 50.0 }), result("result/y")));
		// name("t4", t4);

		// Task t5 = task("t5", sig("add", Adder.class),
		// context("add", in(path(arg, x1), 20.0), in(path(arg, x2), 80.0),
		// out(path(result, y), null)));

		Task t5 = task("t5",
				sig(invoker("x2 + x3", par("x2", 20.0), par("x3", 80.0))),
				context("add", result("result/y")));
		// name("t5", t5);

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		// Job j1= job("j1", job("j2", t4, t5, strategy(Flow.PARALLEL,
		// Access.PULL)), t3,
		
		//ObjectJob
		Job job = job("j1", sig("execute", ServiceJobber.class), 
				job(sig("execute", ServiceJobber.class), "j2", t4, t5), t3,
				pipe(out(t4, path(result, y)), in(t3, path(arg, x1))),
				pipe(out(t5, path(result, y)), in(t3, path(arg, x2))));
		
		// NetJob
//		Job job = job("j1", job("j2", t4, t5), t3,
//				pipe(out(t4, path(result, y)), in(t3, path(arg, x1))),
//				pipe(out(t5, path(result, y)), in(t3, path(arg, x2))));

		return job;
	}
}
