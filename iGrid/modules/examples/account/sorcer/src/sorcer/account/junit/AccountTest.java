package sorcer.account.junit;

import static sorcer.eo.operator.args;
import static sorcer.eo.operator.context;
import static sorcer.eo.operator.exert;
import static sorcer.eo.operator.get;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.jobContext;
import static sorcer.eo.operator.parameterTypes;
import static sorcer.eo.operator.result;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;
import static sorcer.eo.operator.value;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.account.provider.Account;
import sorcer.account.provider.Money;
import sorcer.account.provider.ServiceAccount;
import sorcer.core.SorcerConstants;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Task;
import sorcer.util.Sorcer;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings("unchecked")
public class AccountTest implements SorcerConstants {

	private final static Logger logger = Logger
			.getLogger(AccountTest.class.getName());

	static {
		ServiceExertion.debug = true;
		System.setProperty("java.security.policy", Sorcer.getHome()
				+ "/configs/policy.all");
		System.setSecurityManager(new RMISecurityManager());
		Sorcer.setCodeBase(new String[] { "jeri-account-dl.jar" });
		System.out.println("CLASSPATH :" + System.getProperty("java.class.path"));
		System.setProperty("java.protocol.handler.pkgs", "sorcer.util.url|org.rioproject.url");
	}
	
	@Test
	public void accout1BalanceTest() throws Exception {
		Task t1 = task("t1",
				sig("getBalance", ServiceAccount.class, "Account1"),
				context("balance1", result("balance/amount")));

		logger.info("t1 value: " + value(t1));
	}
	
	@Test
	public void accout1DepositTest() throws Exception {
		Task t2 = task("t2",
				sig("makeDeposit", ServiceAccount.class, "Account1"),
				context("deposit", in("deposit/amount", new Money(10000))));
		t2 = exert(t2);
		logger.info("t1 context: " + context(t2));
		logger.info("t1 value: " + get(t2, "deposit/balance/amount"));
	}
	
	@Test
	public void accout2BalanceTest() throws Exception {
		Task t3 = task("t3",
				sig("getBalance", ServiceAccount.class, "Account2"),
				context("balance2", result("balance/amount")));
		logger.info("t3 value: " + value(t3));
	}
	
	@Test
	public void accout2WithdrawalTest() throws Exception {
		Task t4 = task("t4",
				sig("makeWithdrawal", ServiceAccount.class, "Account2"),
				context("withdrawl", in("withdrawal/amount", new Money(10000))));
		
		t4 = exert(t4);
		logger.info("t3 context: " + context(t4));
		logger.info("t3 value: " + get(t4, "withdrawal/balance/amount"));
	}
	
	@Test
	public void transferJobTest() throws Exception {
		Task t1 = task("t1",
				sig("getBalance", ServiceAccount.class, "Account1"));
		
		Task t2 = task("t2",
				sig("makeDeposit", ServiceAccount.class, "Account1"),
				context("deposit", in("deposit/amount", new Money(10000))));
		
		Task t3 = task("t3",
				sig("getBalance", ServiceAccount.class, "Account2"));
		
		Task t4 = task("t4",
				sig("makeWithdrawal", ServiceAccount.class, "Account2"),
				context("withdrawl", in("withdrawal/amount", new Money(10000)), 
						result("balance/amount")));
		
		Job tj = job("tj", t1, t2, t3, t4);
		tj = exert(tj);
		logger.info("job transfer context: " + jobContext(tj));
	}
	
	@Test
	public void parmetricBalanceTest() throws Exception {
		Task balance = task(
				"balance",
				sig("getBalance", Account.class, "Account1"),
					result("balance/amount"));

		logger.info("Account1 balance: " + value(balance));
	}
	
	@Test
	public void parmetricDepositTest() throws Exception {
		Task deposit = task(
				"deposit",
				sig("makeDeposit", Account.class, "Account1"),
				context(parameterTypes(Money.class), args(new Money(10000)),
					result("balance/amount")));

		deposit = exert(deposit);
		logger.info("t1 context: " + context(deposit));
		
		Task balance = task(
				"balance",
				sig("getBalance", Account.class, "Account1"),
					result("balance/amount"));
		
		logger.info("Account1 balance: " + value(balance));
	}

}
