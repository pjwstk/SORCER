package sorcer.arithmetic.junit;

import static org.junit.Assert.assertEquals;
import static sorcer.co.operator.loop;
import static sorcer.eo.operator.add;
import static sorcer.eo.operator.block;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.strategy;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.arithmetic.provider.Adder;
import sorcer.arithmetic.provider.Averager;
import sorcer.core.SorcerConstants;
import sorcer.service.Block;
import sorcer.service.ServiceExertion;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Wait;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ArithmeticSpaceTest implements SorcerConstants {

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

	@Test
	public void arithmeticSpaceTaskTest() throws Exception {
		Task spaceTask = task(
				"space task",
				sig("add", Adder.class),
				context("add", in("arg/x1", 20.0),
						in("arg/x2", 80.0), result("result/${name}")),
				strategy(Access.PULL, Wait.YES));
		
		Block spaceBlock = block(loop(5), spaceTask);
		
		logger.info("block size1" + spaceBlock.size());
		
		Task  masterTask = task(
				"t5",
				sig("average", Averager.class),
				context(result("result/average")));
		
		add(spaceBlock, masterTask);
		
		spaceBlock = (Block)spaceBlock.exert();
		logger.info("block context" + context(spaceBlock));
		assertEquals(get(context(spaceBlock), "result/average"), 100.00);
	}
		
}
