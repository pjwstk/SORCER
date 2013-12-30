package sorcer.requestor.arithmetic.parameter.ssl;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.SORCER;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ServiceJob;
import sorcer.core.exertion.ServiceTask;
import sorcer.core.provider.util.ProviderAccessor;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Jobber;
import sorcer.service.Job;
import sorcer.util.Log;

/**
 * Testing parameter passing between tasks witin the same service job. Two
 * numbers are added by the first task, then two numbers are multiplied by the
 * second one. The results of the first task and the second task are passed on
 * to the third task that subtracts the result of task two from the result of
 * task one. The {@link sorcer.service.ServiceContext} class is used in this
 * test case.
 * 
 * @see ArithmeticICParameterTester
 * @author Mike Sobolewski
 */

public class ArithmeticParameterTester implements SORCER {

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
		ServiceTask task1 = getAddTask();
		ServiceTask task2 = getMultiplyTask();
		ServiceTask task3 = getSubtractTask();
		ServiceJob job = new ServiceJob("Arithmetic");
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

	private ServiceTask getAddTask() throws Exception {
		ServiceContext context = new ServiceContext("array");
		context.putInValue(root + "[0]" + CPS + VALUE, 20);
		context.putInValue(root + "[1]" + CPS + VALUE, 80);
		// We know that the output is gonna be placed in this path
		context.putOutValue(root + "[3]" + CPS + VALUE, 0);
		ServiceSignature method = new ServiceSignature("add",
				"sorcer.arithmetic.ArithmeticRemote");
		ServiceTask task = new ServiceTask("arithmethic-add", method);
		task.setContext(context);
		return task;
	}

	private ServiceTask getMultiplyTask() throws Exception {
		ServiceContext context = new ServiceContext("array");
		context.putInValue(root + "[0]" + CPS + VALUE, 10);
		context.putInValue(root + "[1]" + CPS + VALUE, 50);
		// We know that the output is gonna be placed in this path
		context.putOutValue(root + "[3]" + CPS + VALUE, 0);
		ServiceSignature method = new ServiceSignature("multiply",
				"sorcer.arithmetic.ArithmeticRemote");
		ServiceTask task = new ServiceTask("arithmethic-multiply", method);
		task.setContext(context);
		return task;
	}

	private ServiceTask getSubtractTask() throws Exception {
		ServiceContext context = new ServiceContext("aritmetic-subtract");
		// We want to stick in the result of multiply in here
		context.putInValue(root + "[0]" + CPS + VALUE, 0);
		// We want to stick in the result of add in here
		context.putInValue(root + "[1]" + CPS + VALUE, 0);
		ServiceSignature method = new ServiceSignature("subtract",
				"sorcer.arithmetic.ArithmeticRemote");
		ServiceTask task = new ServiceTask("arithmethic-subtract",
				"processing results from two previous tasks", method);
		task.setContext(context);
		return task;
	}
}
