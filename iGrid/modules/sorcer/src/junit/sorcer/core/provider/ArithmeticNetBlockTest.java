package junit.sorcer.core.provider;

import static org.junit.Assert.assertEquals;
import static sorcer.co.operator.entry;
import static sorcer.eo.operator.alt;
import static sorcer.eo.operator.block;
import static sorcer.eo.operator.condition;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.loop;
import static sorcer.eo.operator.opt;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;
import static sorcer.po.operator.invoker;
import static sorcer.po.operator.par;
import static sorcer.po.operator.pars;

import java.io.File;
import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import sorcer.core.SorcerConstants;
import sorcer.service.Block;
import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings("unchecked")
public class ArithmeticNetBlockTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ArithmeticNoNetTest.class.getName());
	
	static {
		ServiceExertion.debug = true;
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ju-arithmetic-beans.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
		System.setProperty("java.protocol.handler.pkgs", "sorcer.util.url|org.rioproject.url");
	}
	
	@BeforeClass
	public static void setUpOnce() throws IOException, InterruptedException,
			ExertionException, ContextException {
		String antBuild = Sorcer.getHome()
				+ "/modules/sorcer/src/junit/sorcer/core/provider/bin/boot-all-spawn.xml";
		File antFile = new File(antBuild);
		exert(task("all-arithmetic", antFile));
	}

//	@AfterClass
//	public static void cleanup() throws RemoteException, InterruptedException,
//			SignatureException {
//		Sorcer.destroyNode(null, Adder.class);
//	}
	
	@Test
	public void contextAltTest() throws Exception {
		Task t4 = task("t4", sig("multiply", Multiplier.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						result("block/result")));

		Task t5 = task("t5", sig("add", Adder.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						result("block/result")));
		
		Block block = block("block", context(entry("y1", 100), entry("y2", 200)),
				alt(opt(condition("{ y1, y2 -> y1 > y2 }", "y1", "y2"), t4), 
					opt(condition("{ y1, y2 -> y1 <= y2 }", "y1", "y2"), t5)));
		
		block = exert(block);
		logger.info("block context: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 100.00);

		block = exert(block, entry("y1", 200.0), entry("y2", 100.0));
		logger.info("block context: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 500.0);
	}
	
	@Test
	public void taskAltBlockTest() throws Exception {
		Task t3 = task("t3",  sig("subtract", SubtractorImpl.class), 
				context("subtract", in("arg/t4"), in("arg/t5"),
						result("block/result")));

		Task t4 = task("t4", sig("multiply", Multiplier.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						result("arg/t4")));

		Task t5 = task("t5", sig("add", Adder.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						result("arg/t5")));
		
		Task t6 = task("t6", sig("average", Averager.class), 
				context("average", in("arg/t4"), in("arg/t5"),
						result("block/result")));
		
		Block block = block("block", t4, t5, alt(
				opt(condition("{ t4, t5 -> t4 > t5 }", "t4", "t5"), t3), 
				opt(condition("{ t4, t5 -> t4 <= t5 }", "t4", "t5"), t6)));

		block = exert(block);
		logger.info("block context 1: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 400.00);
		
		block = exert(block, entry("block/t5/arg/x1", 200.0), entry("block/t5/arg/x2", 800.0));
		logger.info("block context 2: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 750.00);
	}
	
	@Test
	public void optBlockTest() throws Exception {
		Task t4 = task("t4", sig("multiply", Multiplier.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						result("out")));
		
		Task t5 = task("t5", sig("add", Adder.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						result("out")));
		
		Block block = block("block", t4,
				opt(condition("{ out -> out > 600 }", "out"), t5));
		
		block = exert(block);
		logger.info("block context 1: " + context(block));
//		logger.info("result: " + value(context(block), "out"));
		assertEquals(value(context(block), "out"), 500.0);
		
		block = exert(block, entry("block/t4/arg/x1", 200.0), entry("block/t4/arg/x2", 800.0));
		logger.info("block context 2: " + context(block));
//		logger.info("result: " + value(context(block), "out"));
		assertEquals(value(context(block), "out"), 100.0);
	}
	
	@Test
	public void varBlockTest() throws Exception {
		Task t4 = task("t4", sig("multiply", Multiplier.class), 
				context("multiply", in("arg/x1", 10.0), in("arg/x2", 50.0),
						result("block/result")));

		Task t5 = task("t5", sig("add", Adder.class), 
				context("add", in("arg/x1", 20.0), in("arg/x2", 80.0),
						result("block/result")));
				 
		Block block = block("block", context(entry("x1", 4), entry("x2", 5)),
				task(par("y", invoker("x1 * x2", pars("x1", "x2")))), 
				alt("altx", opt(condition("{ y -> y > 50 }", "y"), t4), 
				    opt(condition("{ y -> y <= 50 }", "y"), t5)));
				
		block = exert(block);
		logger.info("block context: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 100.00);
		
		block = exert(block, entry("block/x1", 10.0), entry("block/x2", 6.0));
		logger.info("block context: " + context(block));
//		logger.info("result: " + value(context(block), "block/result"));
		assertEquals(value(context(block), "block/result"), 500.00);
	}
	
	@Test
	public void loopBlockTest() throws Exception {
		Block block = block("block", context(entry("x1", 10.0), entry("x2", 20.0), entry("z", 100.0)),
				loop(condition("{ x1, x2, z -> x1 + x2 < z }", "x1", "x2", "z"), 
						task(par("x1", invoker("x1 + 3", pars("x1"))))));
		
		block = exert(block);
		logger.info("block context: " + context(block));
//		logger.info("result: " + value(context(block), "x1"));
		assertEquals(value(context(block), "x1"), 82.00);
	}
}
