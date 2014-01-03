package sorcer.arithmetic.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static sorcer.eo.operator.classpath;
import static sorcer.eo.operator.codebase;
import static sorcer.eo.operator.configuration;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.deploy;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.exertion;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.idle;
import static sorcer.eo.operator.implementation;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.input;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.jobContext;
import static sorcer.eo.operator.link;
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

import org.junit.Ignore;
import org.junit.Test;

import sorcer.arithmetic.provider.Adder;
import sorcer.arithmetic.provider.Averager;
import sorcer.arithmetic.provider.Multiplier;
import sorcer.arithmetic.provider.Subtractor;
import sorcer.core.SorcerConstants;
import sorcer.core.context.PositionalContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.deploy.Deployment;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.ServiceTasker;
import sorcer.core.signature.NetSignature;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Context;
import sorcer.service.Contexter;
import sorcer.service.Evaluation;
import sorcer.service.Exerter;
import sorcer.service.Exertion;
import sorcer.service.Invocation;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Strategy.Monitor;
import sorcer.service.Strategy.Provision;
import sorcer.service.Strategy.Wait;
import sorcer.service.Task;
import sorcer.util.ProviderAccessor;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArithmeticNetTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ArithmeticNetTest.class.getName());

	static {
		ServiceExertion.debug = true;
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ex6-arithmetic-beans.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
		System.setProperty("java.protocol.handler.pkgs", "sorcer.util.url|org.rioproject.url");
	}

//	@BeforeClass 
//	public static void setUpOnce() throws IOException, InterruptedException {
//		String antBuild = Sorcer.getHome()
//				+ "/modules/sorcer/src/junit/sorcer/core/provider/bin/boot-all-spawn.xml";
//		File antFile = new File(antBuild);
//		Project project = new Project();
//		project.init();
//		ProjectHelper.configureProject(project, antFile);
//		project.executeTarget("all-arithmetic");
//		Thread.sleep(2000);
//	}
	
//	@AfterClass 
//	public static void cleanup() throws RemoteException, InterruptedException {
//		Sorcer.destroyNode(null, Adder.class);
//	}
	
//	@Test
//	public void getCatalogTest() throws Exception {
//		Cataloger catalog = ProviderAccessor.getCataloger();
//		System.out.println("Cataloger: " + catalog);
//		String[] pnames = catalog.getProviderList();
//		logger.info("cataloger pnames: " + pnames);
//	}
	
	private Task getAddTask() throws Exception {
		Context context = new PositionalContext("add");
		context.putInValue("arg1/value", 20.0);
		context.putInValue("arg2/value", 80.0);
		// We know that the output is gonna be placed in this path
		context.putOutValue("out/value", 0);
		Signature method = new NetSignature("add", Adder.class);
		Task task = new NetTask("add", method);
		task.setContext(context);
		return task;
	}

	private Task getMultiplyTask() throws Exception {
		Context context = new PositionalContext("multiply");
		context.putInValue("arg1/value", 10.0);
		context.putInValue("arg2/value", 50.0);
		// We know that the output is gonna be placed in this path
		context.putOutValue("out/value", 0);
		Signature method = new NetSignature("multiply", Multiplier.class);
		Task task = new NetTask("multiply", method);
		task.setContext(context);
		return task;
	}

	private Task getSubtractTask() throws Exception {
		PositionalContext context = new PositionalContext("subtract");
		// We want to stick in the result of multiply in here
		context.putInValueAt("arg1/value", 0.0, 1);
		// We want to stick in the result of add in here
		context.putInValueAt("arg2/value", 0.0, 2);
		Signature method = new NetSignature("subtract", Subtractor.class);
		Task task = new NetTask("subtract",
				"processing results from two previous tasks", method);
		task.setContext(context);
		return task;
	}
	
	@Test
	public void arithmeticProviderTest() throws Exception {
		
		Task t5 = task(
				"t5",
				sig("add", Adder.class),
				context("add", in("arg, x1", 20.0),
						in("arg, x2", 80.0), result("result, y")));
		t5 = exert(t5);
		//logger.info("t5 context: " + context(t5));
		//logger.info("t5 value: " + get(t5));
		assertEquals("Wrong value for 100.0", value(t5), 100.0);
	}
	
	@Test
	public void averagerProviderTest() throws Exception {
		
		Task t5 = task(
				"t5",
				sig("average", Averager.class),
				context("average", in("arg, x1", 20.0),
						in("arg, x2", 80.0), result("result, y")));
		t5 = exert(t5);
		logger.info("t5 context: " + context(t5));
		logger.info("t5 value: " + get(t5));
		assertEquals("Wrong value for 50.0", value(t5), 50.0);
	}
	
	@Test
	public void arithmeticSpaceTaskTest() throws Exception {
		Task t5 = task(
				"t5",
				sig("add", Adder.class),
				context("add", in("arg/x1", 20.0),
						in("arg/x2", 80.0), out("result/y")),
				strategy(Access.PULL, Wait.YES));

		t5 = exert(t5);
		logger.info("t5 context: " + context(t5));
		logger.info("t5 value: " + get(t5, "result/y"));
		assertEquals("Wrong value for 100.0", get(t5, "result/y"), 100.0);
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
		logger.info("job j1: " + job);
		job = exert(job);
		logger.info("job j1: " + job);
		//logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		//logger.info("job j1 value @ j1/t3/result/y = " + get(job, "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}
	
	@Test
	public void exertJobPullParTest() throws Exception {
		Job job = createJob(Flow.PAR, Access.PULL);
		job = exert(job);
		//logger.info("job j1: " + job);
		//logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		//logger.info("job j1 value @ j1/t3/result/y = " + get(job, "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}
	
	@Test
	public void exertJobPullSeqTest() throws Exception {
		Job job = createJob(Flow.SEQ, Access.PULL);
		job = exert(job);
		//logger.info("job j1: " + job);
		//logger.info("job j1 job context: " + context(job));
		logger.info("job j1 job context: " + jobContext(job));
		//logger.info("job j1 value @ j1/t3/result/y = " + get(job, "j1/t3/result/y"));
		assertEquals(get(job, "j1/t3/result/y"), 400.00);
	}
	
	@Test
	public void exerterTest() throws Exception {
	Task f5 = task(
			"f5",
			sig("add", Adder.class),
			context("add", in("arg/x1", 20.0),
					in("arg/x2", 80.0), out("result/y", null)),
			strategy(Monitor.NO, Wait.YES));
	
	Exertion out = null;
//	long start = System.currentTimeMillis();
	Exerter exerter = ProviderAccessor.getExerter();
//	logger.info("got exerter: " + exerter);

	out = exerter.exert(f5);
//	long end = System.currentTimeMillis();
	
//	logger.info("task f5 context: " + context(out));
//	logger.info("task f5 result/y: " + get(context(out), "result/y"));
	assertEquals(get(out, "result/y"), 100.00);
	}
	
	@Test
	public void arithmeticEolExertleter() throws Exception {
		// get the current value of the exertlet
		Task task = task("eval", sig("getValue", Evaluation.class, "Arithmetic Exertleter"));
		logger.info("j1/t3/result/y: " + value(task, "j1/t3/result/y"));
		assertEquals(value(task, "j1/t3/result/y"), 400.0);
	
		// change both the contexts completely
		Context multiplyContext = context("multiply", in("arg/x1", 10.0), in("arg/x2", 70.0));
		Context addContext = context("add", in("arg/x1", 90.0), in("arg/x2", 110.0));
		Context invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		task = task("invoke", sig("invoke", Invocation.class, "Arithmetic Exertleter"), invokeContext);
		logger.info("j1/t3/result/y: " + value(task, "j1/t3/result/y"));
		assertEquals(value(task, "j1/t3/result/y"), 500.0);
	
		// change partially the contexts
		multiplyContext = context("multiply", in("arg/x1", 20.0));
		addContext = context("add", in("arg/x1", 80.0));
		invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		task = task("invoke", sig("invoke", Invocation.class, "Arithmetic Exertleter"), invokeContext);
//		logger.info("j1/t3/result/y: " + value(task, "j1/t3/result/y"));
		assertEquals(value(task, "j1/t3/result/y"), 1210.0);		
				
		// reverse the state of the exertleter
		multiplyContext = context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0));
		addContext = context("add", in("arg/x1", 80.0), in("arg/x2", 20.0));
		invokeContext = context("invoke");
		link(invokeContext, "t4", multiplyContext);
		link(invokeContext, "t5", addContext);
		task = task("invoke", sig("invoke", Invocation.class, "Arithmetic Exertleter"), invokeContext);
//		logger.info("j1/t3/result/y: " + value(task, "j1/t3/result/y"));
		assertEquals(value(task, "j1/t3/result/y"), 400.0);
	}
	
	@Test
	public void arithmeticApiExertleter() throws Exception {
		// get the current value of the exertlet
		NetSignature signature = new NetSignature("getValue", Evaluation.class, 
				Sorcer.getActualName("Arithmetic Exertleter"));
		Task task = new NetTask("eval", signature);
		Task result = (Task)task.exert();		
		Context out = (Context)result.getReturnValue();
		logger.info("result: " + result);
//		logger.info("j1/t3/result/y: " + out.getValue("j1/t3/result/y"));
		assertEquals(out.getValue("j1/t3/result/y"), 400.0);
		
		
		// change both the contexts completely
		Context addContext = new PositionalContext("add");
		addContext.putInValue("arg/x1", 90.0);
		addContext.putInValue("arg/x2", 110.0);
		
		Context multiplyContext = new PositionalContext("multiply");
		multiplyContext.putInValue("arg/x1", 10.0);
		multiplyContext.putInValue("arg/x2", 70.0);

		ServiceContext invokeContext = new ServiceContext("invoke");
		invokeContext.putLink("t5", addContext, "");
		invokeContext.putLink("t4", multiplyContext, "");
		
		signature = new NetSignature("invoke", Invocation.class, 
				Sorcer.getActualName("Arithmetic Exertleter"));
		
		task = new NetTask("invoke", signature, invokeContext);
		result = (Task)task.exert();	
		logger.info("result context: " + result);
		out = result.getContext();
		logger.info("result context: " + out);
//		logger.info("j1/t3/result/y: " + out.getValue("j1/t3/result/y"));
//		assertEquals(out.getValue("j1/t3/result/y"), 500.0);

		
		// change partially the contexts
		addContext = new PositionalContext("add");
		addContext.putInValue("arg/x1", 80.0);
		
		multiplyContext = new PositionalContext("multiply");
		multiplyContext.putInValue("arg/x1", 20.0);

		invokeContext = new ServiceContext("invoke");
		invokeContext.putLink("t5", addContext, "");
		invokeContext.putLink("t4", multiplyContext, "");
		
		signature = new NetSignature("invoke", Invocation.class,
				Sorcer.getActualName("Arithmetic Exertleter"));
		
		task = new NetTask("invoke", signature, invokeContext);
		result = (Task)task.exert();		
		out = result.getContext();
//		logger.info("result context: " + out);
//		logger.info("j1/t3/result/y: " + out.getValue("j1/t3/result/y"));
		assertEquals(out.getValue("j1/t3/result/y"), 1210.0);
	
		
		// reverse the state of the exertleter
		addContext = new PositionalContext("t5");
		addContext.putInValue("arg/x1", 20.0);
		addContext.putInValue("arg/x2", 80.0);
		multiplyContext = new PositionalContext("t4");
		multiplyContext.putInValue("arg/x1", 10.0);
		multiplyContext.putInValue("arg/x2", 50.0);

		invokeContext = new ServiceContext("invoke");
		invokeContext.putLink("t5", addContext, "");
		invokeContext.putLink("t4", multiplyContext, "");
		
		signature = new NetSignature("invoke", Invocation.class, 
				Sorcer.getActualName("Arithmetic Exertleter"));
		
		task = new NetTask("invoke", signature, invokeContext);
		result = (Task)task.exert();		
		out = result.getContext();
//		logger.info("result context: " + out);
//		logger.info("j1/t3/result/y: " + out.getValue("j1/t3/result/y"));
		assertEquals(out.getValue("j1/t3/result/y"), 400.0);
	}
	
	// two level job composition with PULL and PAR execution
	private static Job createJob(Flow flow, Access access) throws Exception {
		Task t3 = task("t3", sig("subtract", Subtractor.class), 
				context("subtract", in("arg/x1", null), in("arg/x2", null),
						out("result/y", null)));

		Task t4 = task("t4", sig("multiply", Multiplier.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						out("result/y", null)));

		Task t5 = task("t5", sig("add", Adder.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						out("result/y", null)));

		// Service Composition j1(j2(t4(x1, x2), t5(x1, x2)), t3(x1, x2))
		//Job job = job("j1",
		Job job = job("j1", //sig("service", RemoteJobber.class), 
				//job("j2", t4, t5),
				job("j2", t4, t5, strategy(flow, access)), 
				t3,
				pipe(out(t4, "result/y"), in(t3, "arg/x1")),
				pipe(out(t5, "result/y"), in(t3, "arg/x2")));
				
		return job;
	}
		
	public static Context createContext() throws Exception {
		return context("add", input("arg/x1", 20.0), input("arg/x2", 80.0));
	}
	
	public static Exertion createJob() throws Exception {
		return createJob(Flow.SEQ, Access.PUSH);
	}
	
	@Test
	public void contexterTest() throws Exception {
		Task cxtt = task("addContext", sig("getContext", Contexter.class, "Add Contexter"),
				context("add", input("arg/x1"), input("arg/x2")));
		 
		Context result = context(exert(cxtt));
//		logger.info("contexter context 1: " + result);
		
		assertEquals(get(result, "arg/x1"), 20.0);
		assertEquals(get(result, "arg/x2"), 80.0);
	
		cxtt = task("appendContext", sig("appendContext", Contexter.class, "Add Contexter"),
				context("add", input("arg/x1", 200.0), input("arg/x2", 800.0)));

		result = context(exert(cxtt));
//		logger.info("contexter context 2: " + result);

		cxtt = task("addContext", sig("getContext", Contexter.class, "Add Contexter"),
				context("add", input("arg/x1"), input("arg/x2")));

		result = context(exert(cxtt));
//		logger.info("contexter context 3: " + result);
		
		assertEquals(get(result, "arg/x1"), 200.0);
		assertEquals(get(result, "arg/x2"), 800.0);
		
		// reset the contexter
		cxtt = task("appendContext", sig("appendContext", Contexter.class, "Add Contexter"),
				context("add", input("arg/x1", 20.0), input("arg/x2", 80.0)));

		result = context(exert(cxtt));
//		logger.info("contexter context 4: " + result);
		assertEquals(get(result, "arg/x1"), 20.0);
		assertEquals(get(result, "arg/x2"), 80.0);
	}
	
	@Test
	public void netContexterTaskTest() throws Exception {
		Task t5 = task("t5", sig("add", Adder.class), 
					sig("getContext", Contexter.class, "Add Contexter", Signature.APD),
					context("add", in("arg/x1"), in("arg/x2"),
						result("result/y")));

		Context result =  context(exert(t5));
//		logger.info("contexter context: " + result);
		assertEquals(get(result, "result/y"), 100.0);
	}
	
	public Job createProvisionedJob() throws Exception {
		Task f4 = task(
				"f4",
				sig("multiply", Multiplier.class,
					deploy(implementation(ServiceTasker.class.getName()),
						classpath("ex6-arithmetic-beans.jar"),
						codebase("ex6-arithmetic-dl.jar"),
						configuration("bin/examples/ex6/configs/multiplier-prv.config"),
						maintain(1),
						idle("3h"))),
				context("multiply", input("arg/x1", 10.0d),
						input("arg/x2", 50.0d), out("result/y1", null)));

		Task f5 = task(
				"f5",
				sig("add", Adder.class,
					deploy(classpath("arithmetic-beans.jar"),
						codebase("arithmetic-dl.jar"),
						configuration("bin/examples/ex6/configs/adder-prv.config"),
						idle(60*3))),
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
		
		return f1;
	}

	@Test
	public void testProvisionedJob() throws Exception {
		Job f1 = createProvisionedJob();
		List<Signature> allSigs = f1.getAllSignatures();
//		logger.info("all sigs size: " + allSigs.size());
		assertEquals(allSigs.size(), 5);
		allSigs = f1.getAllTaskSignatures();
//		logger.info("all net sigs size: " + allSigs.size());
		assertEquals(allSigs.size(), 3);
		List<NetSignature> netSigs = f1.getAllNetTaskSignatures();
//		logger.info("all net sigs size: " + allSigs.size());
		assertEquals(netSigs.size(), 3);

		List<Deployment> allDeployments = f1.getAllDeployments();
//		logger.info("allDeployments: " + allDeployments);
//		logger.info("allDeployments size: " + allDeployments.size());
		assertEquals(allDeployments.size(), 3);

		int f4Idle = ((ServiceSignature) ((Task) exertion(f1, "f1/f2/f4"))
				.getProcessSignature()).getDeployment().getIdle();
		int f5Idle = ((ServiceSignature) ((Task) exertion(f1, "f1/f2/f5"))
				.getProcessSignature()).getDeployment().getIdle();
//		logger.info("f4 idle: " + f4Idle);
//		logger.info("f5 idle: " + f5Idle);
		assertEquals(f4Idle, f5Idle);
	}
	
	@Test
	public void exertionDeploymentIdTest() throws Exception {
		Job job = createProvisionedJob();
		String did =  job.getDeploymentId();
		logger.info("job deploy id: " + did);
		assertEquals(did, "a65d7a4ccafe6c2cf7ff20e207ba7e8a");
	}
	
	@Ignore
	@Test
	public void asyncTaskTest() throws Exception {
		//TODO get result from monitor
		task("t5",
				sig("add", Adder.class),
				context("add", input("arg/x1", 20.0), input("arg/x2", 80.0),
						output("result/y", null)), strategy(Monitor.YES, Wait.NO));
	}
	
	@Ignore
	@Test
	public void deployTest() throws Exception {
		// works only with Rio support
		Task t5 = task("f5",
			sig("add", Adder.class,
					deploy(configuration("bin/sorcer/test/arithmetic/configs/AdderProviderConfig.groovy"))),
				context("add", input("arg/x3", 20.0d), input("arg/x4", 80.0d),
							result("result/y")),
				strategy(Provision.YES));
//		logger.info("t5 is provisionable: " + t5.isProvisionable());
		assertTrue(t5.isProvisionable());
//		logger.info("t5 value: " + value(t5));
		assertEquals(value(t5), 100.0);
	}
}
