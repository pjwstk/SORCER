package sorcer.ex5.junit;

import static org.junit.Assert.assertEquals;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exerter;
import sorcer.service.Job;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes" })
public class ArithmeticExerterTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ArithmeticExerterTest.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ex5-arithmetic-beans.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	@Test
	public void exertExerter() throws Exception {
		Job exertion = ArithmeticNetTest.getJobInJobNetArithmeticJob();
		Task task = new NetTask("exert", new NetSignature("exert",
				Exerter.class),
				new ServiceContext(exertion));
		Task result = (Task) task.exert();
		// logger.info("result: " + result);
		// logger.info("return value: " + result.getReturnValue());
	
		Context out = (Context) result.getContext();
//		logger.info("out context: " + out);
		logger.info("1job1task/subtract/result/value: "
				+ out.getValue(
						"1job1task/subtract/result/value"));
		assertEquals(
				out.getValue("1job1task/subtract/result/value"),
				400.0);
	}
}
