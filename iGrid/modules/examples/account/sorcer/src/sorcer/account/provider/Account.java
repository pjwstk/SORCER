package sorcer.account.provider;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Account extends Remote {

	public Money getBalance() throws RemoteException;

	public void makeDeposit(Money amount) throws RemoteException,
			NegativeAmountException;

	public void makeWithdrawal(Money amount) throws RemoteException,
			OverdraftException, NegativeAmountException;
}
