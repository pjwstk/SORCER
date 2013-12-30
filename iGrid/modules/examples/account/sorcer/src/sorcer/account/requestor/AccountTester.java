package sorcer.account.requestor;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.account.provider.SorcerAccount;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Jobber;
import sorcer.core.signature.NetSignature;
import sorcer.service.Job;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;

public class AccountTester implements SorcerConstants {

	private static Logger log = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		Job result = new AccountTester().test();
		log.info("result: \n" + result);
	}

	private Job test() throws Exception {
		Jobber jobber = ProviderAccessor.getJobber();
		return (jobber != null) ? (Job) jobber.service(getJob(), null) : null;
	}

	private Job getJob() throws Exception {
		NetTask task1 = getDepositTask();
		NetTask task2 = getWithdrawalTask();
		NetJob job = new NetJob("account");
		job.addExertion(task1);
		job.addExertion(task2);
		return job;
	}

	private NetTask getDepositTask() throws Exception {
		ServiceContext context = new ServiceContext(SorcerAccount.ACCOUNT);
		context.putValue(SorcerAccount.DEPOSIT + CPS + SorcerAccount.AMOUNT,
				100);
		context.putValue(SorcerAccount.BALANCE + CPS + SorcerAccount.AMOUNT, 0);
		NetSignature signature = new NetSignature("makeDeposit",
				SorcerAccount.class, "Account1");
		NetTask task = new NetTask("account-deposit", signature);
		task.setContext(context);
		return task;
	}

	private NetTask getWithdrawalTask() throws Exception {
		ServiceContext context = new ServiceContext(SorcerAccount.ACCOUNT);
		context.putValue(SorcerAccount.WITHDRAWAL + CPS + SorcerAccount.AMOUNT,
				100);
		context.putValue(SorcerAccount.BALANCE + CPS + SorcerAccount.AMOUNT, 0);
		NetSignature signature = new NetSignature("makeWithdrawal",
				SorcerAccount.class, "Account2");
		NetTask task = new NetTask("account-withdrawal", signature);
		task.setContext(context);
		return task;
	}
}
