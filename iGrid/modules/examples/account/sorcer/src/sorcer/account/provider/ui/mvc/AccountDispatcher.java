package sorcer.account.provider.ui.mvc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.account.provider.Account;

public class AccountDispatcher implements ActionListener {

	private final static Logger logger = Logger
			.getLogger("sorcer.provider.account.ui.mvc");

	private AccountModel model;

	private AccountView view;

	private Account account;

	public AccountDispatcher(AccountModel model, AccountView view,
			Account account) {
		this.model = model;
		this.view = view;
		this.account = account;
	}

	public void getBalance() {
		try {
			model.setBalance(account.getBalance());
		} catch (RemoteException e) {
			logger.info("Error occurred while getting account balance");
			logger.throwing(getClass().getName(), "getBalance", e);
		}
	}

	private void makeWithdrawl() {
		try {
			model.setWithdrawalAmount(view.getWithdrawalAmount());
			account.makeWithdrawal(model.getWithdrawalAmount());
			getBalance();
		} catch (Exception exception) {
			System.out.println("Couldn't talk to account. Error was \n "
					+ exception);
			exception.printStackTrace();
		}
	}

	private void makeDeposit() {
		try {
			model.setDepositAmount(view.getDepositAmount());
			account.makeDeposit(model.getDepositAmount());
			getBalance();
		} catch (Exception exception) {
			System.out.println("Couldn't talk to account. Error was \n "
					+ exception);
			exception.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		logger.info("actionPerformed>>action: " + action);
		if (action == AccountModel.DEPOSIT)
			makeDeposit();
		else if (action == AccountModel.WITHDRAW)
			makeWithdrawl();
	}
}
