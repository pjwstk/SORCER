package sorcer.account.provider.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.account.provider.Account;
import sorcer.account.provider.Money;
import sorcer.core.provider.ServiceProvider;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Sorcer;

public class AccountUI extends JPanel {

	private static final long serialVersionUID = -3171243785170712405L;

	private JTextField balanceTextField;

	private JTextField withdrawalTextField;

	private JTextField depositTextField;

	private Account account;

	private ServiceItem item;

	private final static Logger logger = Logger.getLogger(AccountUI.class
			.getName());

	public AccountUI(Object provider) {
		super();
		getAccessibleContext().setAccessibleName("Account Tester");
		item = (ServiceItem) provider;

		if (item.service instanceof Account) {
			account = (Account) item.service;
			createUI();
		}

	}

	protected void createUI() {
		setLayout(new BorderLayout());
		add(buildAccountPanel(), BorderLayout.CENTER);
		resetBalanceField();
	}

	private void resetBalanceField() {
		try {
			Money balance = account.getBalance();
			balanceTextField.setText(balance.value());
		} catch (Exception e) {
			logger.info("Error occurred while getting account balance");
			logger.throwing(getClass().getName(), "resetBalanceField", e);
		}
	}

	private JPanel buildAccountPanel() {
		JPanel panel = new JPanel();
		JPanel actionPanel = new JPanel(new GridLayout(3, 3));

		actionPanel.add(new JLabel("Current Balance:"));
		balanceTextField = new JTextField();
		balanceTextField.setEnabled(false);
		actionPanel.add(balanceTextField);
		actionPanel.add(new JLabel(" cents"));

		actionPanel.add(new JLabel("$ Withdraw"));
		withdrawalTextField = new JTextField();
		actionPanel.add(withdrawalTextField);
		JButton withdrawalButton = new JButton("Do it");
		withdrawalButton.addActionListener(new WithdrawAction());
		actionPanel.add(withdrawalButton);
		
		actionPanel.add(new JLabel("$ Deposit"));
		depositTextField = new JTextField();
		actionPanel.add(depositTextField);
		JButton depositButton = new JButton("Do it");
		depositButton.addActionListener(new DepositAction());
		actionPanel.add(depositButton);

		panel.add(actionPanel);
		return panel;
	}

	private Money readTextField(JTextField moneyField) {
		try {
			Float floatValue = new Float(moneyField.getText());
			float actualValue = floatValue.floatValue();
			int cents = (int) (actualValue * 100);

			return new Money(cents);
		} catch (Exception e) {
			logger.info("Field doesn't contain a valid value");
		}
		return null;
	}

	private class WithdrawAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Money withdrawalAmount = readTextField(withdrawalTextField);
				account.makeWithdrawal(withdrawalAmount);
				withdrawalTextField.setText("");
				resetBalanceField();
			} catch (Exception exception) {
				logger.info("Couldn't talk to account. Error was" + exception);
				logger.throwing(getClass().getName(), "actionPerformed",
						exception);
			}
		}
	}

	private class DepositAction implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			try {
				Money depositAmount = readTextField(depositTextField);
				account.makeDeposit(depositAmount);
				depositTextField.setText("");
				resetBalanceField();
			} catch (Exception exception) {
				logger.info("Couldn't talk to account. Error was \n"
						+ exception);
				logger.throwing(getClass().getName(), "actionPerformed",
						exception);
			}
		}
	}

	/**
	 * Returns a service UI descriptorfor this service. Usally this method is
	 * used as an entry in provider configuration files when smart proxies are
	 * deployed with a standard off the shelf {@link ServiceProvider}.
	 * 
	 * @return service UI descriptor
	 */
	public static UIDescriptor getUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIComponentFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/accout-ui.jar") }, AccountUI.class.getName()));
		} catch (Exception ex) {
			logger.throwing(AccountUI.class.getName(), "getUIDescriptor", ex);
		}
		return uiDesc;
	}
}
