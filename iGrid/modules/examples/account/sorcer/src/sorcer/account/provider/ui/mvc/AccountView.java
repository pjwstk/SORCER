package sorcer.account.provider.ui.mvc;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
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

public class AccountView extends JPanel implements Observer {
	
	private static final long serialVersionUID = -3812646466769297683L;

	private JTextField balanceTextField;

	private JTextField withdrawalTextField;

	private JTextField depositTextField;

	private AccountModel model;

	private AccountDispatcher dispatcher;

	private final static Logger logger = Logger
			.getLogger("sorcer.provider.account.ui.mvc");

	public AccountView(Object provider) {
		super();
		getAccessibleContext().setAccessibleName("AccountView Tester");
		ServiceItem item = (ServiceItem) provider;

		if (item.service instanceof Account) {
			Account account = (Account) item.service;
			model = new AccountModel();
			dispatcher = new AccountDispatcher(model, this, account);
			createView();
			model.addObserver(this);
			dispatcher.getBalance();
		}
	}

	protected void createView() {
		setLayout(new BorderLayout());
		add(buildAccountPanel(), BorderLayout.CENTER);
	}

	private JPanel buildAccountPanel() {
		JPanel panel = new JPanel();
		JPanel actionPanel = new JPanel(new GridLayout(3, 3));

		actionPanel.add(new JLabel("Current Balance"));
		balanceTextField = new JTextField();
		balanceTextField.setEnabled(false);
		actionPanel.add(balanceTextField);
		actionPanel.add(new JLabel(" cents"));

		actionPanel.add(new JLabel(AccountModel.WITHDRAW));
		withdrawalTextField = new JTextField();
		actionPanel.add(withdrawalTextField);
		JButton withdrawalButton = new JButton("Do it");
		withdrawalButton.setActionCommand(AccountModel.WITHDRAW);
		withdrawalButton.addActionListener(dispatcher);
		actionPanel.add(withdrawalButton);

		actionPanel.add(new JLabel(AccountModel.DEPOSIT));
		depositTextField = new JTextField();
		actionPanel.add(depositTextField);
		JButton depositButton = new JButton("Do it");
		depositButton.setActionCommand(AccountModel.DEPOSIT);
		depositButton.addActionListener(dispatcher);
		actionPanel.add(depositButton);

		panel.add(actionPanel);
		return panel;
	}

	public Money getDepositAmount() {
		return readTextField(depositTextField);
	}

	public Money getWithdrawalAmount() {
		return readTextField(withdrawalTextField);
	}

	public void clearDepositAmount() {
		depositTextField.setText("");
	}

	public void clearWithdrawalAmount() {
		withdrawalTextField.setText("");
	}

	public void displayBalance() {
		Money balance = model.getBalance();
		balanceTextField.setText(balance.value());
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

	public void update(Observable o, Object arg) {
		logger.info("update>>arg: " + arg);
		if (arg != null) {
			if (arg.equals(AccountModel.DEPOSIT))
				clearDepositAmount();
			else if (arg.equals(AccountModel.WITHDRAW))
				clearWithdrawalAmount();
			else if (arg.equals(AccountModel.BALANCE))
				displayBalance();;
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
							+ "/accout-mvc-ui.jar") }, AccountView.class
							.getName()));
		} catch (Exception ex) {
			logger.throwing(AccountView.class.getName(), "getUIDescriptor", ex);
		}
		return uiDesc;
	}

}
