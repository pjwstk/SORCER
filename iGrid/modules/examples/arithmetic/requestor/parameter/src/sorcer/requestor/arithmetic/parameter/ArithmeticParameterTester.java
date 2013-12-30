package sorcer.requestor.arithmetic.parameter;

import static sorcer.eo.operator.path;

import java.rmi.RMISecurityManager;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.PositionalContext;
import sorcer.core.context.ControlContext.ThrowableTrace;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.util.Log;

/**
 * Testing parameter passing between tasks within the same service job. Two
 * numbers are added by the first task, then two numbers are multiplied by the
 * second one. The results of the first task and the second task are passed on
 * to the third task that subtracts the result of task two from the result of
 * task one. The {@link sorcer.core.context.PositionalContext} is used for
 * requestor's data in this test.
 * 
 * @see ArithmeticICParameterTester
 * @author Mike Sobolewski
 */

public class ArithmeticParameterTester implements SorcerConstants {

	private static Logger log = Log.getTestLog();

	private String arg = "arg";
	private String out = "result";

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		Exertion exertion = new ArithmeticParameterTester().getJob();
		Exertion result = exertion.exert(null);
		// log.info("result: \n" + result);
		// Show result the data contexts of component tasks
		// log.info("result: \n" + result.getExertions().get(0).getContext());
		// log.info("result: \n" + result.getExertions().get(1).getContext());
		// log.info("result: \n" + result.getExertions().get(2).getContext());

		List<ThrowableTrace> exceptions = result.getExceptions();
		log.info("exceptions: \n" + exceptions);
		
		if (exceptions.size() == 0)
			log.info("result: \n" + result.getContext());
		else
			log.info("exceptions: \n" + exceptions);
	}

	private Job getJob() throws Exception {
		NetTask task1 = getAddTask();
		NetTask task2 = getMultiplyTask();
		NetTask task3 = getSubtractTask();

		NetJob job = new NetJob("subtract(add,multiply)");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		// map output the result of second task as the first argument of task
		// three
		task2.getContext().connect(path(out, "[3]", VALUE),
				path(arg, "[0]", VALUE), task3.getContext());
		// map the result of the first task as the second argument of task
		// three
		task1.getContext().connect(path(out, "[3]", VALUE),
				path(arg, "[1]", VALUE), task3.getContext());
		// job.getControlConect().setMonitorEnabled(true);
		return job;
	}

	private NetTask getAddTask() throws Exception {
		PositionalContext context = new PositionalContext("add");
		context.putInValueAt(path(arg, "[0]", VALUE), 20.0, 1);
		context.putInValueAt(path(arg, "[1]", VALUE), 80.0, 2);
		// We know that the output is gonna be placed in this path
		context.putOutValue(path(out, "[3]", VALUE), 0);
		NetSignature method = new NetSignature("add",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("add", method);
		task.setContext(context);
		return task;
	}

	private NetTask getMultiplyTask() throws Exception {
		PositionalContext context = new PositionalContext("multiply");
		context.putInValueAt(path(arg, "[0]", VALUE), 10.0, 1);
		context.putInValueAt(arg + "[1]" + CPS + VALUE, 50.0, 2);
		// We know that the output is gonna be placed in this path
		context.putOutValue(path(out, "[3]", VALUE), 0);
		NetSignature method = new NetSignature("multiply",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("multiply", method);
		task.setContext(context);
		return task;
	}

	private NetTask getSubtractTask() throws Exception {
		PositionalContext context = new PositionalContext("subtract");
		// We want to stick in the result of multiply in here
		context.putInValueAt(path(arg, "[0]", VALUE), 0.0, 1);
		// We want to stick in the result of add in here
		context.putInValueAt(path(arg, "[1]", VALUE), 0.0, 2);
		NetSignature method = new NetSignature("subtract",
				sorcer.arithmetic.ArithmeticRemote.class);
		NetTask task = new NetTask("subtract",
				"processing results from two previous tasks", method);
		task.setContext(context);
		return task;
	}
}
