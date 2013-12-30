package rmi.account.client;

import rmi.account.*;
import rmi.account.data.*;
import java.rmi.*;
import java.rmi.server.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BankClientFrame extends ExitingFrame {
    private JTextField accountNameField;
    private JTextField balanceTextField;
    private JTextField withdrawalTextField;
    private JTextField depositTextField;
    private Account account;
    static private String host = "localhost";
    static private String port = "1099";

    public BankClientFrame() {}
        
    public BankClientFrame(String serverName, String serverPort) {
        host = serverName;
        port = serverPort;
    }

    protected void buildGUI() {
        JPanel contentPane = new JPanel(new BorderLayout());

        contentPane.add(buildActionPanel(), BorderLayout.CENTER);
        contentPane.add(buildBalancePanel(), BorderLayout.SOUTH);
        setContentPane(contentPane);
        setSize(250, 100);
    }

    private void resetBalanceField() {
        try {
            Money balance = account.getBalance();

            balanceTextField.setText("Balance: " + balance.toString());
        } catch (Exception e) {
            System.out.println("Error occurred while getting account balance\n" + e);
        }
    }

    private JPanel buildActionPanel() {
        JPanel actionPanel = new JPanel(new GridLayout(3, 3));

        actionPanel.add(new JLabel("Account Name:"));
        accountNameField = new JTextField();
        actionPanel.add(accountNameField);
        JButton getBalanceButton = new JButton("Get Balance");

        getBalanceButton.addActionListener(new GetBalanceAction());
        actionPanel.add(getBalanceButton);
        actionPanel.add(new JLabel("Withdraw"));
        withdrawalTextField = new JTextField();
        actionPanel.add(withdrawalTextField);
        JButton withdrawalButton = new JButton("Do it");

        withdrawalButton.addActionListener(new WithdrawAction());
        actionPanel.add(withdrawalButton);
        actionPanel.add(new JLabel("Deposit"));
        depositTextField = new JTextField();
        actionPanel.add(depositTextField);
        JButton depositButton = new JButton("Do it");

        depositButton.addActionListener(new DepositAction());
        actionPanel.add(depositButton);
        return actionPanel;
    }

    private JPanel buildBalancePanel() {
        JPanel balancePanel = new JPanel(new GridLayout(1, 2));

        balancePanel.add(new JLabel("Current Balance:"));
        balanceTextField = new JTextField();
        balanceTextField.setEnabled(false);
        balancePanel.add(balanceTextField);
        return balancePanel;
    }

    private void getAccount() {
        try {
            account = (Account) Naming.lookup("rmi://" + host + ':' + port + '/' + accountNameField.getText());
        } catch (Exception e) {
            System.out.println("Couldn't find account " + accountNameField.getText() + ". Error was \n " + e);
            e.printStackTrace();
        }
        return;
    }

    private void releaseAccount() {
        account = null;
    }

    private Money readTextField(JTextField moneyField) {
        try {
            Float floatValue = new Float(moneyField.getText());
            float actualValue = floatValue.floatValue();
            int cents = (int) (actualValue * 100);

            return new Money(cents);
        } catch (Exception e) {
            System.out.println("Field doesn't contain a valid value");
        }
        return null;
    }

    private class GetBalanceAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                getAccount();
                resetBalanceField();
                releaseAccount();
            } catch (Exception exception) {
                System.out.println("Couldn't talk to account. Error was \n " + exception);
                exception.printStackTrace();
            }
        }
    }


    private class WithdrawAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                getAccount();
                Money withdrawalAmount = readTextField(withdrawalTextField);

                account.makeWithdrawal(withdrawalAmount);
                withdrawalTextField.setText("");
                resetBalanceField();
                releaseAccount();
            } catch (Exception exception) {
                System.out.println("Couldn't talk to account. Error was \n " + exception);
                exception.printStackTrace();
            }
        }
    }


    private class DepositAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                getAccount();
                Money depositAmount = readTextField(depositTextField);
                account.makeDeposit(depositAmount);
                depositTextField.setText("");
                resetBalanceField();
                releaseAccount();
            } catch (Exception exception) {
                System.out.println("Couldn't talk to account. Error was \n " + exception);
                exception.printStackTrace();
            }
        }
    }
}
