package sorcer.arithmetic.junit;

import static org.junit.Assert.assertEquals;
import static sorcer.eo.operator.classpath;
import static sorcer.eo.operator.codebase;
import static sorcer.eo.operator.configuration;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.deploy;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.idle;
import static sorcer.eo.operator.implementation;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.jobContext;
import static sorcer.eo.operator.maintain;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.output;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;

import java.rmi.RMISecurityManager;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.arithmetic.provider.Adder;
import sorcer.arithmetic.provider.Arithmetic;
import sorcer.arithmetic.provider.Multiplier;
import sorcer.arithmetic.provider.Subtractor;
import sorcer.core.SorcerConstants;
import sorcer.core.deploy.Deployment;
import sorcer.core.provider.ServiceTasker;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Strategy.Provision;
import sorcer.service.Strategy.Wait;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
public class ArithmeticNetTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ArithmeticNetTest.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "arithmetic-dl.jar",
				"sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :"
				+ System.getProperty("java.class.path"));
	}
	
	@Test
	public void netTaskTest() throws Exception {

		Task t5 = task(
				"t5",
				sig("add", Adder.class),
				context("add", in("arg, x1", 20.0), in("arg, x2", 80.0),
						result("result, y")));
		t5 = exert(t5);
		// logger.info("t5 context: " + context(t5));
		// logger.info("t5 value: " + get(t5));
		assertEquals("Wrong value for 100.0", value(t5), 100.0);
	}

	@Test
	public void multiServiceProviderTest() throws Exception {

		Task t5 = task(
				"t5",
				sig("add", Arithmetic.class),
				context("add", in("arg, x1", 20.0), in("arg, x2", 80.0),
						result("result, y")));

		t5 = exert(t5);
		// logger.info("t5 context: " + context(t5));
		// logger.info("t5 value: " + get(t5));
		assertEquals("Wrong value for 100.0", value(t5), 100.0);
	}

	@Test
	public void spaceTaskTest() throws Exception {
		Task t5 = task(
				"t5",
				sig("add", Adder.class),
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						out("result/y")), strategy(Access.PULL, Wait.YES));

		t5 = exert(t5);
		logger.info("t5 context: " + context(t5));
		logger.info("t5 value: " + get(t5, "result/y"));
		assertEquals("Wrong value for 100.0", get(t5, "result/y"), 100.0);
	}

	@Test
	public void pushParJobTest() throws Exception {
		Job job = createJob(Flow.PAR, Access.PUSH);
		job = exert(job);
		// logger.info("job j1: " + job);
		// logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		// logger.info("job j1 value @ j1/t3/result/y = " + get(job,
		// "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}

	@Test
	public void pushSeqJobTest() throws Exception {
		Job job = createJob(Flow.SEQ, Access.PUSH);
		job = exert(job);
		// logger.info("job j1: " + job);
		// logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		// logger.info("job j1 value @ j1/t3/result/y = " + get(job,
		// "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}

	@Test
	public void pullParJobTest() throws Exception {
		Job job = createJob(Flow.PAR, Access.PULL);
		job = exert(job);
		// logger.info("job j1: " + job);
		// logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		// logger.info("job j1 value @ j1/t3/result/y = " + get(job,
		// "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}

	public void pullSeqJobTest() throws Exception {
		Job job = createJob(Flow.SEQ, Access.PULL);
		job = exert(job);
		// logger.info("job j1: " + job);
		// logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		// logger.info("job j1 value @ j1/t3/result/y = " + get(job,
		// "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}

	// two level job composition with PULL and PAR execution
	private Job createJob(Flow flow, Access access) throws Exception {
		Task t3 = task(
				"t3",
				sig("subtract", Subtractor.class),
				context("subtract", in("arg/x1", null), in("arg/x2", null),
						out("result/y", null)));
		Task t4 = task("t4",
				sig("multiply", Multiplier.class),
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						out("result/y", null)));
		Task t5 = task("t5",
				sig("add", Adder.class),
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						out("result/y", null)));

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		Job j1 = job("j1", // sig("service", Jobber.class),
				job("j2", t4, t5, strategy(flow, access)), t3,
				pipe(out(t4, "result/y"), in(t3, "arg/x1")),
				pipe(out(t5, "result/y"), in(t3, "arg/x2")));

		return j1;
	}

	@Test
	public void createProvisionedJob() throws Exception {
		Task f4 = task(
				"f4",
				sig("multiply", Multiplier.class,
					deploy(implementation(ServiceTasker.class.getName()),
						classpath("arithmetic-beans.jar"),
						codebase("arithmetic-dl.jar"),
						configuration("bin/examples/ex6/configs/multiplier-prv.config"),
						maintain(1),
						idle("30m"))),
				context("multiply", input("arg/x1", 10.0d),
						input("arg/x2", 50.0d), out("result/y1", null)));

		Task f5 = task(
				"f5",
				sig("add", Adder.class,
					deploy(classpath("arithmetic-beans.jar"),
						codebase("arithmetic-dl.jar"),
						configuration("bin/examples/ex6/configs/adder-prv.config"),
						idle(1000*60*30))),
				context("add", input("arg/x3", 20.0d), input("arg/x4", 80.0d),
						output("result/y2", null)));

		Task f3 = task(
				"f3",
				sig("subtract", Subtractor.class,
					deploy(classpath("arithmetic-beans.jar"),
						codebase("arithmetic-dl.jar"),
						configuration("bin/examples/ex6/configs/subtractor-prv.config"))),
				context("subtract", input("arg/x5", null),
						input("arg/x6", null), output("result/y3", null)));

		// job("f1", job("f2", f4, f5), f3,
		// job("f1", job("f2", f4, f5, strategy(Flow.PAR, Access.PULL)), f3,
		Job f1 = job("f1", job("f2", f4, f5), f3, strategy(Provision.NO),
				pipe(out(f4, "result/y1"), input(f3, "arg/x5")),
				pipe(out(f5, "result/y2"), input(f3, "arg/x6")));

		List<Signature> allSigs = f1.getAllSignatures();
		List<Deployment> allDeployments = f1.getAllDeployments();
		logger.info("allSigs: " + allSigs);
		logger.info("allDeployments: " + allDeployments);

		assertEquals(allSigs.size(), 3);
		assertEquals(allDeployments.size(), 3);
	}

}
