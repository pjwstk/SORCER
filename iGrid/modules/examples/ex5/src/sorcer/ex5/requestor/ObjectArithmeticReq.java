package sorcer.ex5.requestor;

import static org.junit.Assert.assertEquals;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.core.SorcerConstants;
import sorcer.core.context.PositionalContext;
import sorcer.core.exertion.ObjectJob;
import sorcer.core.exertion.ObjectTask;
import sorcer.core.signature.ObjectSignature;
import sorcer.ex5.provider.AdderImpl;
import sorcer.ex5.provider.MultiplierImpl;
import sorcer.ex5.provider.SubtractorImpl;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectArithmeticReq implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(ObjectArithmeticReq.class.getName());

	static {
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "ex5-arithmetic-beans.jar",
				"sorcer-prv-dl.jar" });
		System.out.println("CLASSPATH :"
				+ System.getProperty("java.class.path"));
	}

	@Test
	public void exertTaskConcatenation() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();

		Exertion job = new ObjectJob("3tasks");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		
		// make the result of second task as the first argument of task
		// three
		task2.getContext().connect("out/value", "arg1/value", task3.getContext());
		// make the result of the first task as the second argument of task
		// three
		task1.getContext().connect("out/value", "arg2/value", task3.getContext());
		
		job = job.exert();

		logger.info("job context: " + ((Job)job).getJobContext());
		// result at the provider's default path"
		assertEquals(((Job)job).getJobValue("3tasks/subtract/result/value"), 400.0);
	}

	@Test
	public void exertJobHierachicalComposition() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();

		Job internal = new ObjectJob("2tasks");
		internal.addExertion(task2);
		internal.addExertion(task1);
		
		Exertion job = new ObjectJob("1job1task");
		job.addExertion(internal);
		job.addExertion(task3);
		
		// make the result of second task as the first argument of task
		// three
		task2.getContext().connect("out/value", "arg1/value", task3.getContext());
		// make the result of the first task as the second argument of task
		// three
		task1.getContext().connect("out/value", "arg2/value", task3.getContext());
		
		job = job.exert();

		logger.info("job context: " + ((Job)job).getJobContext());
		// result at the provider's default path"
		assertEquals(((Job)job).getJobValue("1job1task/subtract/result/value"), 400.0);
	}
	
	@Test
	public void exertJobStrategy() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();
		
		Job internal = new ObjectJob("2tasks");
		internal.addExertion(task2);
		internal.addExertion(task1);
		internal.getControlContext().setFlowType(Flow.PAR);
		internal.getControlContext().setAccessType(Access.PUSH);

		Exertion job = new ObjectJob("1job1task");
		job.addExertion(internal);
		job.addExertion(task3);
		internal.getControlContext().setFlowType(Flow.SEQ);
		internal.getControlContext().setAccessType(Access.PUSH);
		
		// make the result of second task as the first argument of task
		// three
		task2.getContext().connect("out/value", "arg1/value", task3.getContext());
		// make the result of the first task as the second argument of task
		// three
		task1.getContext().connect("out/value", "arg2/value", task3.getContext());
		
		job = job.exert();

		logger.info("job context: " + ((Job)job).getJobContext());
		// result at the provider's default path"
		assertEquals(((Job)job).getJobValue("1job1task/subtract/result/value"), 400.0);
	}
	
	private static Task getAddTask() throws Exception {
		Context context = new PositionalContext("add");
		context.putInValue("arg1/value", 20.0);
		context.putInValue("arg2/value", 80.0);
		// We know that the output is gonna be placed in this path
		context.putOutValue("out/value", 0.0);
		Signature method = new ObjectSignature("add", AdderImpl.class);
		Task task = new ObjectTask("add", method);
		task.setContext(context);
		return task;
	}

	private static Task getMultiplyTask() throws Exception {
		Context context = new PositionalContext("multiply");
		context.putInValue("arg1/value", 10.0);
		context.putInValue("arg2/value", 50.0);
		// We know that the output is gonna be placed in this path
		context.putOutValue("out/value", 0.0);
		Signature method = new ObjectSignature("multiply", MultiplierImpl.class);
		Task task = new ObjectTask("multiply", method);
		task.setContext(context);
		return task;
	}

	private static Task getSubtractTask() throws Exception {
		PositionalContext context = new PositionalContext("subtract");
		// We want to stick in the result of multiply in here
		context.putInValueAt("arg1/value", 0.0, 1);
		// We want to stick in the result of add in here
		context.putInValueAt("arg2/value", 0.0, 2);
		Signature method = new ObjectSignature("subtract", SubtractorImpl.class);
		Task task = new ObjectTask("subtract",
				"processing results from two previouseky executed tasks", method);
		task.setContext(context);
		return task;
	}

	public static Job getObjectArithmeticJob() throws Exception {
		Task task1 = getAddTask();
		Task task2 = getMultiplyTask();
		Task task3 = getSubtractTask();

		Job internal = new ObjectJob("2tasks");
		internal.addExertion(task2);
		internal.addExertion(task1);
		
		Job job = new ObjectJob("1job1task");
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
}
