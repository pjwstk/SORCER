package sorcer.account.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.provider.ServiceTasker;
import sorcer.service.Context;
import sorcer.util.Log;
import sorcer.util.Sorcer;

import com.sun.jini.start.LifeCycle;

@SuppressWarnings("rawtypes")
public class AccountProvider extends ServiceTasker implements Account,
		SorcerAccount, SorcerConstants {

	private static Logger logger = Log.getTestLog();

	private Money balance;

	/**
	 * Constructs an instance of the SORCER account provider implementing
	 * SorcerAccount and Account. This constructor is required by Jini 2 life
	 * cycle management.
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public AccountProvider(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		String cents = getProperty("provider.balance");
		balance = new Money(Integer.parseInt(cents));
	}

	public Context getBalance(Context context) throws RemoteException {
		return process(context, SorcerAccount.BALANCE);
	}

	public Context makeDeposit(Context context) throws RemoteException {
		return process(context, SorcerAccount.DEPOSIT);
	}

	public Context makeWithdrawal(Context context) throws RemoteException {
		return process(context, SorcerAccount.WITHDRAWAL);
	}

	private Context process(Context context, String selector)
			throws RemoteException {
		try {
			logger.info("input context: \n" + context);

			Money result = null, amount = null;
			if (selector.equals(SorcerAccount.BALANCE)) {
				result = getBalance();
			} else if (selector.equals(SorcerAccount.DEPOSIT)) {
				amount = (Money) context.getValue(SorcerAccount.DEPOSIT + CPS
						+ SorcerAccount.AMOUNT);
				makeDeposit(amount);
				result = getBalance();
			} else if (selector.equals(SorcerAccount.WITHDRAWAL)) {
				amount = (Money) context.getValue(SorcerAccount.WITHDRAWAL
						+ CPS + SorcerAccount.AMOUNT);
				makeWithdrawal(amount);
				result = getBalance();
			}
			// set return value
			if (context.getReturnPath() != null) {
				context.setReturnValue(result);
			}
			logger.info(selector + " result: \n" + result);
			String outputMessage = "processed by " + getHostname();
			context.putValue(selector + CPS +
					SorcerAccount.BALANCE + CPS + SorcerAccount.AMOUNT, result);
			context.putValue(SorcerAccount.COMMENT, outputMessage);

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " process execption", ex);
		}
		return context;
	}

	public Money getBalance() throws RemoteException {
		String cents = Sorcer.getProperty("provider.balance");
		return balance;
	}

	public void makeDeposit(Money amount) throws RemoteException,
			NegativeAmountException {
		checkForNegativeAmount(amount);
		balance.add(amount);
		return;
	}

	public void makeWithdrawal(Money amount) throws RemoteException,
			OverdraftException, NegativeAmountException {
		checkForNegativeAmount(amount);
		checkForOverdraft(amount);
		balance.subtract(amount);
		return;
	}

	private void checkForNegativeAmount(Money amount)
			throws NegativeAmountException {
		int cents = amount.getCents();

		if (0 > cents) {
			throw new NegativeAmountException();
		}
	}

	private void checkForOverdraft(Money amount) throws OverdraftException {
		if (amount.greaterThan(balance)) {
			throw new OverdraftException(false);
		}
		return;
	}

	/**
	 * Returns name of the local host.
	 * 
	 * @return local host name
	 * @throws UnknownHostException
	 */
	private String getHostname() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}
}
