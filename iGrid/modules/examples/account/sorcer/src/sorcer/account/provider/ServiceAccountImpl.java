package sorcer.account.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import sorcer.core.SorcerConstants;
import sorcer.core.provider.Provider;
import sorcer.core.proxy.Partnership;
import sorcer.core.proxy.RemotePartner;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.util.Log;

public class ServiceAccountImpl implements Account, ServiceAccount, SorcerConstants {

	private static Logger logger = Log.getTestLog();

	private Money balance;

	public Context getBalance(Context context) throws RemoteException {
		return process(context, ServiceAccount.BALANCE);
	}

	public Context makeDeposit(Context context) throws RemoteException {
		return process(context, ServiceAccount.DEPOSIT);
	}

	public Context makeWithdrawal(Context context) throws RemoteException {
		return process(context, ServiceAccount.WITHDRAWAL);
	}

	private Context process(Context context, String selector)
			throws RemoteException {
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

			logger.info(selector + " result: \n" + result);
			String outputMessage = "processed by " + getHostname();
			context.putValue(
					ServiceAccount.BALANCE + CPS + ServiceAccount.AMOUNT, result);
			context.putValue(ServiceAccount.COMMENT, outputMessage);

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " process execption", ex);
		}
		return context;
	}

	public ServiceAccountImpl(Money startingBalance) throws RemoteException {
		balance = startingBalance;
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

	private Provider partner;

	private Administrable admin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.proxy.Partnership#getPartner()
	 */
	public Remote getInner() throws RemoteException {
		return (Remote) partner;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.base.Service#service(sorcer.base.Exertion)
	 */
	public Exertion service(Exertion exertion, Transaction transaction)
			throws RemoteException, ExertionException, TransactionException {
		return partner.service(exertion, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	public Object getAdmin() throws RemoteException {
		return admin;
	}

	public void setInner(Object provider) {
		partner = (Provider) provider;
	}

	public void setAdmin(Object admin) {
		this.admin = (Administrable) admin;
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
