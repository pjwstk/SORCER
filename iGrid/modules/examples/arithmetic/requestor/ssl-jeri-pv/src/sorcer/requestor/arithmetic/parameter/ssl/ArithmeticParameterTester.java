package sorcer.requestor.arithmetic.parameter.ssl;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Jobber;
import sorcer.core.signature.NetSignature;
import sorcer.service.Job;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;

/**
 * Testing parameter passing between tasks witin the same service job. Two
 * numbers are added by the first task, then two numbers are multiplied by the
 * second one. The results of the first task and the second task are passed on
 * to the third task that subtracts the result of task two from the result of
 * task one. The {@link sorcer.core.context.ServiceContext} class is used in this
 * test case.
 * 
 * @see ArithmeticICParameterTester
 * @author Mike Sobolewski
 */

public class ArithmeticParameterTester implements SorcerConstants {

	private static Logger log = Log.getTestLog();

	private String root = "arithmetic";
	
	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		Job result = new ArithmeticParameterTester().test();
		log.info("result: \n" + result);
		Log.initializeSecurityLoggers();
	}

	private Job test() throws Exception {
		Jobber jobber = ProviderAccessor.getJobber();
		Job job = getJob();
		return (jobber != null) ? (Job) jobber.service(getJob(), null) : null;
	}

	private Job getJob() throws Exception {
		NetTask task1 = getAddTask();
		NetTask task2 = getMultiplyTask();
		NetTask task3 = getSubtractTask();
		NetJob job = new NetJob("Arithmetic");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		// map ouput the result of second task as the first argument of task
		// three
		task2.getContext().map(root + "[3]" + CPS + VALUE, 
				root + "[0]" + CPS + VALUE, task3.getContext());
		// map the result of the first task as the second argument of task
		// three
		task1.getContext().map(root + "[3]" + CPS + VALUE, 
				root + "[1]" + CPS + VALUE, task3.getContext());
		// job.getControlConect().setMonitorEnabled(true);
		return job;
	}

	private NetTask getAddTask() throws Exception {
		ServiceContext context = new ServiceContext("array");
		context.putInValue(root + "[0]" + CPS + VALUE, 20);
		context.putInValue(root + "[1]" + CPS + VALUE, 80);
		// We know that the output is gonna be placed in this path
		context.putOutValue(root + "[3]" + CPS + VALUE, 0);
		NetSignature method = new NetSignature("add",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-add", method);
		task.setConditionalContext(context);
		return task;
	}

	private NetTask getMultiplyTask() throws Exception {
		ServiceContext context = new ServiceContext("array");
		context.putInValue(root + "[0]" + CPS + VALUE, 10);
		context.putInValue(root + "[1]" + CPS + VALUE, 50);
		// We know that the output is gonna be placed in this path
		context.putOutValue(root + "[3]" + CPS + VALUE, 0);
		NetSignature method = new NetSignature("multiply",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-multiply", method);
		task.setConditionalContext(context);
		return task;
	}

	private NetTask getSubtractTask() throws Exception {
		ServiceContext context = new ServiceContext("aritmetic-subtract");
		// We want to stick in the result of multiply in here
		context.putInValue(root + "[0]" + CPS + VALUE, 0);
		// We want to stick in the result of add in here
		context.putInValue(root + "[1]" + CPS + VALUE, 0);
		NetSignature method = new NetSignature("subtract",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-subtract",
				"processing results from two previous tasks", method);
		task.setConditionalContext(context);
		return task;
	}
}
