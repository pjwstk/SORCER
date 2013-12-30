/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sorcer.security.jaas;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import sorcer.util.Log;

class LoginDialog extends JDialog implements ActionListener {

	private String username;

	private char[] password;

	private char[] keyStorePassword;

	private char[] keyPassword;

	private boolean done = false;

	private JTextField usernameText;

	private JPasswordField passwordField, keyStorePasswordField,
			keyPasswordField;

	public LoginDialog(JFrame frame, boolean modal) {
		super(frame, "SORCER Login", modal);
		JFrame.setDefaultLookAndFeelDecorated(true);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new BorderLayout());
		loginPanel.setOpaque(true); // content panes must be opaque
		setContentPane(loginPanel);

		JPanel passwdPnl = new JPanel(new GridLayout(4, 2));
		passwdPnl.add(new JLabel(UsernamePasswordCallbackHandler.USERNAME));
		passwdPnl.add(usernameText = new JTextField(14));
		passwdPnl.add(new JLabel(UsernamePasswordCallbackHandler.PASSWORD));
		passwdPnl.add(passwordField = new JPasswordField(14));
		passwdPnl.add(new JLabel(
				UsernamePasswordCallbackHandler.KEYSTORE_PASSWORD));
		passwdPnl.add(keyStorePasswordField = new JPasswordField(14));
		passwdPnl.add(new JLabel(UsernamePasswordCallbackHandler.KEY_PASSWORD));
		passwdPnl.add(keyPasswordField = new JPasswordField(14));

		passwordField
				.setActionCommand(UsernamePasswordCallbackHandler.PASSWORD);
		passwordField.setEchoChar('*');

		keyStorePasswordField
				.setActionCommand(UsernamePasswordCallbackHandler.KEYSTORE_PASSWORD);
		keyStorePasswordField.setEchoChar('*');

		keyPasswordField
				.setActionCommand(UsernamePasswordCallbackHandler.KEY_PASSWORD);
		keyPasswordField.setEchoChar('*');

		JPanel buttonPanel = new JPanel();

		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPanel.add(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPanel.add(cancelButton);

		loginPanel.add(passwdPnl, BorderLayout.CENTER);
		loginPanel.add(buttonPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				username = null;
				password = null;
				dispose();
			}
		});
		pack();
		setLocationRelativeTo(frame);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand() == "OK") {
			username = usernameText.getText();
			password = passwordField.getPassword();
			keyStorePassword = keyStorePasswordField.getPassword();
			keyPassword = keyPasswordField.getPassword();
			done = true;
		} else {
			username = null;
			password = null;
			done = false;
		}
		setVisible(false);
	}

	public String getUsername() {
		return username;
	}

	public char[] getPassword() {
		return password;
	}

	public char[] getKeyStorePassword() {
		return keyStorePassword;
	}

	public char[] getKeyPassword() {
		return keyPassword;
	}

	public boolean isDone() {
		return done;
	}
}