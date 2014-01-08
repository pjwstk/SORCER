package sorcer.account.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.provider.ServiceTasker;
import sorcer.service.Context;
import sorcer.util.Log;

import com.sun.jini.start.LifeCycle;

@SuppressWarnings("rawtypes")
public class AccountProvider extends ServiceTasker implements Account,
		ServiceAccount, SorcerConstants {

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

	public Context getBalance(Context context) throws RemoteException,
			AccountException {
		return process(context, ServiceAccount.BALANCE);
	}

	public Context makeDeposit(Context context) throws RemoteException,
			AccountException {
		return process(context, ServiceAccount.DEPOSIT);
	}

	public Context makeWithdrawal(Context context) throws RemoteException,
			AccountException {
		return process(context, ServiceAccount.WITHDRAWAL);
	}

	private Context process(Context context, String selector)
			throws RemoteException, AccountException {
		try {
			logger.info("input context: \n" + context);

			Money result = null, amount = null;
			if (selector.equals(ServiceAccount.BALANCE)) {
				result = getBalance();
			} else if (selector.equals(ServiceAccount.DEPOSIT)) {
				amount = (Money) context.getValue(ServiceAccount.DEPOSIT + CPS
						+ ServiceAccount.AMOUNT);
				makeDeposit(amount);
				result = getBalance();
			} else if (selector.equals(ServiceAccount.WITHDRAWAL)) {
				amount = (Money) context.getValue(ServiceAccount.WITHDRAWAL
						+ CPS + ServiceAccount.AMOUNT);
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
					ServiceAccount.BALANCE + CPS + ServiceAccount.AMOUNT, result);
			context.putValue(ServiceAccount.COMMENT, outputMessage);

		} catch (Exception ex) {
			throw new AccountException(ex);
		}
		return context;
	}

	public Money getBalance() throws RemoteException {
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
