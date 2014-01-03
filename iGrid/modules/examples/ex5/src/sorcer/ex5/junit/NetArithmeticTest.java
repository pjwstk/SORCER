package sorcer.ex5.junit;

import static org.junit.Assert.assertEquals;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.SorcerConstants;
import sorcer.core.context.PositionalContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.exertion.ObjectJob;
import sorcer.core.signature.NetSignature;
import sorcer.ex5.provider.Adder;
import sorcer.ex5.provider.Multiplier;
import sorcer.ex5.provider.Subtractor;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes" })
public class NetArithmeticTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(NetArithmeticTest.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ex5-arithmetic-beans.jar",  "sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
	}
	
	@Test
	public void exertTaskHierachy() throws Exception {
		Job job = getJobInJobNetArithmeticJob();
		Job result = (NetJob) job.exert();
		logger.info("result context: " + result.getComponentContext("3tasks/subtract"));
		logger.info("job context: " + result.getJobContext());
		assertEquals(result.getValue("1job1task/subtract/result/value"), 400.0);
	}
	
	@Test
	public void exertTaskConcatenation() throws Exception {
		Job job = getTaskedNetJob();	
		NetJob result = (NetJob)job.exert();
		logger.info("result context: "  + result.getComponentContext("3tasks/subtract"));
		logger.info("job context: " + result.getJobContext());
		assertEquals(result.getValue("3tasks/subtract/result/value"), 400.0);
	}

	public static Job getJobInJobNetArithmeticJob() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();

		Job internal = new NetJob("2tasks");
		internal.addExertion(task2);
		internal.addExertion(task1);
		
		Job job = new NetJob("1job1task");
		job.addExertion(internal);
		job.addExertion(task3);
		
		// make the result of second task as the first argument of task
		// three
		task2.getContext().connect("out/value", "arg1/value", task3.getContext());
		// make the result of the first task as the second argument of task
		// three
		task1.getContext().connect("out/value", "arg2/value", task3.getContext());
		
		return job;
	}
	
	public static Job getTaskedNetJob() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();

		Job job = new NetJob("3tasks");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		
		// make the result of second task as the first argument of task
		// three
		task2.getContext().connect("out/value", "arg1/value", task3.getContext());
		// make the result of the first task as the second argument of task
		// three
		task1.getContext().connect("out/value", "arg2/value", task3.getContext());
		
		return job;
	}
	private static Task getAddTask() throws Exception {
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

	private static Task getMultiplyTask() throws Exception {
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

	private static Task getSubtractTask() throws Exception {
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
}
