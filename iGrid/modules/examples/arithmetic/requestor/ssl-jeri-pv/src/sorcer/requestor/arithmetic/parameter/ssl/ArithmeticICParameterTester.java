package sorcer.requestor.arithmetic.parameter.ssl;

import java.rmi.RMISecurityManager;
import java.security.PrivilegedExceptionAction;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.util.Log;

/**
 * Testing parameter passing between tasks witin the same service job. Two
 * numbers are added by the first task, then two numbers are multiplied by the
 * second one. The results of the first task and the second task are passed on
 * to the third task that subtracts the result of task two from the result of
 * task one. The {@link sorcer.core.context.ArrayContext} class is used in this test
 * case.
 * 
 * @see ArithmeticParameterTester
 * @author Mike Sobolewski
 */

public class ArithmeticICParameterTester implements SorcerConstants {

	private static Logger log = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		Job result = new ArithmeticICParameterTester().test();
		log.info("result: \n" + result);
		Log.initializeSecurityLoggers();
	}

	private Job test() throws Exception {
		LoginContext loginContext = new LoginContext("sorcer.requestor.arithmetic.parameter.ssl.ArithmeticParameterTester");
        loginContext.login();

        Subject.doAsPrivileged(loginContext.getSubject(),
                               new PrivilegedExceptionAction() {
                                   public Object run() throws Exception {
                                	   return (Job)getJob().exert(null);
                                   }
                               },
                               null);
        return null;
		//return getJob().exert(null);
		
        //Jobber jobber = ProviderAccessor.getJobber();
        //return (jobber != null) ? (Job) jobber.service(getJob(), null) : null;
	}

	private Job getJob() throws Exception {
		NetTask task1 = getAddTask();
		NetTask task2 = getMultiplyTask();
		NetTask task3 = getSubtractTask();
		NetJob job = new NetJob("SSL Arithmetic");
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		// map the result of second task as the first argument of task
		// three
		log.info("context 2: " + task2.getContext());
		log.info("context 3: " + task3.getContext());
		task2.getContext().map(ArrayContext.ovp(3), ArrayContext.ivp(1),
				task3.getContext());
		// map the result of the first task as the second argument of task
		// three
		task1.getContext().map(ArrayContext.ovp(3), ArrayContext.ivp(2),
				task3.getContext());
		// job.getCC().setMonitorEnabled(true);
		return job;
	}

	private NetTask getAddTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 20.0);
		context.ivc(1, "arg1");
		context.iv(2, 80.0);
		context.ivc(2, "arg2");
		context.ov(3, 0.0);
		context.ovc(3, "result for adding arg1 and arg2");

		NetSignature method = new NetSignature("add",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-add", method);
		task.setConditionalContext(context);
		return task;
	}

	private NetTask getMultiplyTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 10.0);
		context.ivc(1, "arg1");
		context.iv(2, 50.0);
		context.ov(3, 0.0);
		context.ovc(3, "result for multiplying values 1 and 2");

		NetSignature method = new NetSignature("multiply",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-multiply", method);
		task.setConditionalContext(context);
		return task;
	}

	private NetTask getSubtractTask() throws Exception {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 0.0);
		context.ivc(1, "arg1: result of task 2");
		context.iv(2, 0.0);
		context.ivc(2, "arg2: result of task 1");
		context.ov(3, 0.0);
		context.ovc(3, "result for subtacting arg1 and arg2");

		NetSignature method = new NetSignature("subtract",
				"sorcer.arithmetic.ArithmeticRemote");
		NetTask task = new NetTask("arithmethic-subtract", method);
		task.setConditionalContext(context);
		return task;
	}
}
