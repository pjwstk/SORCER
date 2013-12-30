package sorcer.arithmetic.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.arithmetic.Arithmetic;
import sorcer.arithmetic.ArithmeticRemote;
import sorcer.arithmetic.Averager;
import sorcer.core.context.ArrayContext;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.proxy.Outer;
import sorcer.service.Service;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Sorcer;

public class CalculatorUI extends JPanel implements ActionListener {

	private static final long serialVersionUID = 3689977306529761391L;

	private JTextField input; // To accept user input and display result

	private JButton button0, // This button to pass 0 to panel
			button1, // This button to pass 1 to panel
			button2, // This button to pass 2 to panel
			button3, // This button to pass 3 to panel
			button4, // This button to pass 4 to panel
			button5, // This button to pass 5 to panel
			button6, // This button to pass 6 to panel
			button7, // This button to pass 7 to panel
			button8, // This button to pass 8 to panel
			button9, // This button to pass 9 to panel
			buttonDiv, // This button operate division
			buttonMul, // This button operate multiplication
			buttonAdd, // This button operate addition
			buttonSub, // This button opearte subtraction
			buttonAss, // This button return result
			buttonOpar, // This button call open parentheses
			buttonCpar, // This button call close parentheses
			buttonPoint, // This button to pass . to panel
			buttonClear; // This button clear panel

	private int flag, // Record the state after calculating
			errLevel; // State the error level

	private final static Logger logger = Logger.getLogger(ArithmeticUI.class
			.getName());

	private ServiceItem item;

	// SORCER provider or semismart proxy Service#service(Exertion)
	private Service provider;

	// used for remote calls via a proxy
	// implementing ArithemeticRemote
	private ArithmeticRemote server;

	// used for remote calls via a proxy
	// implementing Partnership
	private Averager partner;

	// used for local execution - smart proxies,
	// implementing local Arithmetic calls
	private Arithmetic smartProxy;

	// a flag indicating if the proxy is remote, service provider's, or extended
	// server (partner)
	private boolean isRemote, isProvider, isExtended;

	private ArrayContext context;

	private String selector;

	/** Creates new CalulatorUI Component */
	public CalculatorUI(Object obj) {
		super();
		getAccessibleContext().setAccessibleName("Calculator");
		try {
			item = (ServiceItem) obj;
			logger.info("service class: " + item.service.getClass().getName()
					+ "\nservice object: " + item.service);

			if (item.service instanceof Provider) {
				provider = (Provider) item.service;
				isProvider = true;
			}

			if (item.service instanceof ArithmeticRemote) {
				server = (ArithmeticRemote) item.service;
				isRemote = true;
			} else if (item.service instanceof Arithmetic) {
				smartProxy = (Arithmetic) item.service;
				isRemote = false;
				if (smartProxy instanceof Outer) {
					Object proxy = ((Outer) smartProxy).getInner();
					if (proxy instanceof Provider) {
						isProvider = true;
						provider = (Provider) proxy;
					} else if (proxy instanceof Outer) {
						isExtended = true;
						partner = (Averager) proxy;
					}
				}

			}
			logger.info("isProvider: " + isProvider + ", provider: " + provider
					+ "\nisRemote: " + isRemote + ", server= " + server
					+ "\nsmartProxy: " + smartProxy + "\nisExtended: "
					+ partner);

			// Schedule a job for the event-dispatching thread:
			// creating this application's service UI.
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createUI();
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void createUI() {
		setBackground(Color.white); // Set white backgroud
		setLayout(null);
		setSize(205, 210);

		flag = 0;
		errLevel = 0;

		input = new JTextField();
		input.setBounds(20, 20, 165, 25);
		add(input);
		Insets bis = new Insets(0,0,0,0);
		button0 = new JButton("0");
		button0.setMargin(bis);
		button0.setBounds(20, 160, 25, 25);
		add(button0);
		button1 = new JButton("1");
		button1.setMargin(bis);
		button1.setBounds(20, 125, 25, 25);
		add(button1);
		button2 = new JButton("2");
		button2.setMargin(bis);
		button2.setBounds(55, 125, 25, 25);
		add(button2);
		button3 = new JButton("3");
		button3.setMargin(bis);
		button3.setBounds(90, 125, 25, 25);
		add(button3);
		button4 = new JButton("4");
		button4.setMargin(bis);
		button4.setBounds(20, 90, 25, 25);
		add(button4);
		button5 = new JButton("5");
		button5.setMargin(bis);
		button5.setBounds(55, 90, 25, 25);
		add(button5);
		button6 = new JButton("6");
		button6.setMargin(bis);
		button6.setBounds(90, 90, 25, 25);
		add(button6);
		button7 = new JButton("7");
		button7.setMargin(bis);
		button7.setBounds(20, 55, 25, 25);
		add(button7);
		button8 = new JButton("8");
		button8.setMargin(bis);
		button8.setBounds(55, 55, 25, 25);
		add(button8);
		button9 = new JButton("9");
		button9.setMargin(bis);
		button9.setBounds(90, 55, 25, 25);
		add(button9);
		buttonDiv = new JButton("/");
		buttonDiv.setMargin(bis);
		buttonDiv.setBounds(160, 90, 25, 25);
		add(buttonDiv);
		buttonMul = new JButton("*");
		buttonMul.setMargin(bis);
		buttonMul.setBounds(125, 90, 25, 25);
		add(buttonMul);
		buttonAdd = new JButton("+");
		buttonAdd.setMargin(bis);
		buttonAdd.setBounds(125, 55, 25, 25);
		add(buttonAdd);
		buttonSub = new JButton("-");
		buttonSub.setMargin(bis);
		buttonSub.setBounds(160, 55, 25, 25);
		add(buttonSub);
		buttonAss = new JButton("=");
		buttonAss.setMargin(bis);
		buttonAss.setBounds(90, 160, 25, 25);
		add(buttonAss);
		buttonOpar = new JButton("(");
		buttonOpar.setMargin(bis);
		buttonOpar.setBounds(125, 125, 25, 25);
		add(buttonOpar);
		buttonCpar = new JButton(")");
		buttonCpar.setMargin(bis);
		buttonCpar.setBounds(160, 125, 25, 25);
		add(buttonCpar);
		buttonPoint = new JButton(".");
		buttonPoint.setMargin(bis);
		buttonPoint.setBounds(55, 160, 25, 25);
		add(buttonPoint);
		buttonClear = new JButton("Clear");
		buttonClear.setMargin(bis);
		buttonClear.setBounds(125, 160, 60, 25);
		add(buttonClear);

		input.addActionListener(this);
		button0.addActionListener(this);
		button1.addActionListener(this);
		button2.addActionListener(this);
		button3.addActionListener(this);
		button4.addActionListener(this);
		button5.addActionListener(this);
		button6.addActionListener(this);
		button7.addActionListener(this);
		button8.addActionListener(this);
		button9.addActionListener(this);
		buttonDiv.addActionListener(this);
		buttonMul.addActionListener(this);
		buttonAdd.addActionListener(this);
		buttonSub.addActionListener(this);
		buttonAss.addActionListener(this);
		buttonOpar.addActionListener(this);
		buttonCpar.addActionListener(this);
		buttonPoint.addActionListener(this);
		buttonClear.addActionListener(this);
		
		validate();
	}

	/*
	 * Draws the outline of calculator.
	 */
	public void paintComponent(Graphics graphic) {
		super.paintComponent(graphic); //paint background
		
		graphic.setColor(Color.yellow);
		graphic.drawRoundRect(0, 0, 190, 195, 15, 15);
		graphic.setColor(Color.lightGray);
		graphic.drawRoundRect(1, 1, 191, 196, 15, 15);
		graphic.drawRoundRect(2, 2, 192, 197, 15, 15);
		graphic.drawRoundRect(3, 3, 193, 198, 15, 15);
		graphic.drawRoundRect(4, 4, 194, 199, 15, 15);
		graphic.setColor(Color.black);
		graphic.fillRoundRect(5, 5, 195, 200, 15, 15);
		graphic.setColor(Color.green);
		graphic.drawRoundRect(5, 5, 195, 200, 15, 15);
		graphic.setColor(Color.yellow);
		graphic.drawString("SORCER", 80, 200);
	}

	/*
	 * User interface event-handling.
	 */
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() instanceof JButton) {
			JButton clickedButton = (JButton) event.getSource();
			if (clickedButton == buttonClear) {
				clearPanel();
				flag = 0;
			} else if (clickedButton == buttonAss) {
				errLevel = 0;
				compute();
				flag = 1;
			} else {
				if (flag == 1) {
					clearPanel(); // clear panel after calculation
					flag = 0;
				}
				displayEx(clickedButton);
			}
		} else {
			errLevel = 0;
			compute();
			flag = 1;
		}
	}

	/*
	 * Clears input TextField.
	 */
	private void clearPanel() {
		input.setText("");
	}

	/*
	 * Displays the whole expression that the user enters.
	 */
	private void displayEx(JButton clickedButton) {
		String tmpstr;
		tmpstr = input.getText();
		if (clickedButton == button0)
			input.setText(tmpstr + "0");
		else if (clickedButton == button1)
			input.setText(tmpstr + "1");
		else if (clickedButton == button2)
			input.setText(tmpstr + "2");
		else if (clickedButton == button3)
			input.setText(tmpstr + "3");
		else if (clickedButton == button4)
			input.setText(tmpstr + "4");
		else if (clickedButton == button5)
			input.setText(tmpstr + "5");
		else if (clickedButton == button6)
			input.setText(tmpstr + "6");
		else if (clickedButton == button7)
			input.setText(tmpstr + "7");
		else if (clickedButton == button8)
			input.setText(tmpstr + "8");
		else if (clickedButton == button9)
			input.setText(tmpstr + "9");
		else if (clickedButton == buttonDiv)
			input.setText(tmpstr + "/");
		else if (clickedButton == buttonMul)
			input.setText(tmpstr + "*");
		else if (clickedButton == buttonAdd)
			input.setText(tmpstr + "+");
		else if (clickedButton == buttonSub)
			input.setText(tmpstr + "-");
		else if (clickedButton == buttonOpar)
			input.setText(tmpstr + "(");
		else if (clickedButton == buttonCpar)
			input.setText(tmpstr + ")");
		else if (clickedButton == buttonPoint)
			input.setText(tmpstr + ".");
	}

	/*
	 * Calculates the expression and dispays the result, when the user hit
	 * return key or "=" button.
	 */
	private void compute() {
		String tmpStr, Result = "";
		char ch;
		int i, noOfChar, noOfPar = 0;

		tmpStr = input.getText();
		noOfChar = tmpStr.length();

		// Expression brief checking
		for (i = 0; i < noOfChar; i++) {
			ch = tmpStr.charAt(i);
			if (ch == ')')
				noOfPar--;
			if (noOfPar < 0)
				errLevel = 1;
			if (ch == '(')
				noOfPar++;
			if (ch < '(' || ch > '9' || ch == ',')
				errLevel = 2;
			if (ch == '.' && (i + 1 < tmpStr.length()))
				for (int j = i + 1; (j < tmpStr.length())
						&& ((Character.isDigit(tmpStr.charAt(j))) || ((tmpStr
								.charAt(j))) == '.'); j++)
					if (tmpStr.charAt(j) == '.')
						errLevel = 3;
			// If an operand has more than one point return error
		}// End of expression brief checking

		if (noOfPar != 0)
			errLevel = 1; // If open and close parentheses do not match return
		// error

		if (errLevel != 0)
			errMessage(errLevel); // An error perform to prompt error message
		else
			Result = calculate(tmpStr); // No error perform to calculate
		// expression
		if (errLevel != 0)
			errMessage(errLevel); // An error perform to prompt error message
		else
			input.setText(Result); // No error show result
	}

	/*
	 * Implements the expression
	 */
	private String calculate(String expression) {
		String result = expression, f_operand, r_operand;
		char cha;
		int index, f_index, r_index, no_of_cha = result.length(), no_of_pare = 0, pare_match = 0, op_index = 0, cp_index = 0;

		if (errLevel == 0) {
			// Checking Parentheses
			for (index = 0; index < no_of_cha; index++) {
				cha = result.charAt(index);

				if (cha == '(') {
					if (pare_match == 0)
						op_index = index;
					pare_match++;
					no_of_pare++;
				}

				if (cha == ')') {
					pare_match--;
					if (pare_match == 0)
						cp_index = index;
				}
			}// End of checking Parentheses

			if (op_index + 1 == cp_index)
				errLevel = 3;

			// Recursive Calculate, when parentheses existed
			if (errLevel == 0 && no_of_pare > 0) {
				if ((op_index == 0) && (cp_index == (no_of_cha - 1))
						&& (op_index != cp_index))
					result = calculate(result.substring(op_index + 1, cp_index));
				else if (op_index == 0 && cp_index > 0) {
					if ((Character.isDigit(result.charAt(cp_index + 1))))
						errLevel = 3;
					else {
						result = calculate(result.substring(op_index + 1,
								cp_index))
								+ result.substring(cp_index + 1);
						no_of_pare--;
						while (no_of_pare != 0) {
							result = calculate(result);
							no_of_pare--;
						}
					}
				} else if ((op_index > 0) && (cp_index > 0)
						&& (cp_index != no_of_cha - 1)) {
					if ((Character.isDigit(result.charAt(cp_index + 1)))
							|| (Character.isDigit(result.charAt(op_index - 1))))
						errLevel = 3;
					else {
						result = result.substring(0, op_index)
								+ calculate(result.substring(op_index + 1,
										cp_index))
								+ result.substring(cp_index + 1);
						no_of_pare--;
						while (no_of_pare != 0) {
							result = calculate(result);
							no_of_pare--;
						}
					}
				} else if (cp_index == no_of_cha - 1 && op_index > 0) {
					if ((Character.isDigit(result.charAt(op_index - 1))))
						errLevel = 3;
					else {
						result = result.substring(0, op_index)
								+ calculate(result.substring(op_index + 1,
										cp_index));
						no_of_pare--;
						while (no_of_pare != 0) {
							result = calculate(result);
							no_of_pare--;
						}
					}
				}
			}// End of recursive Calculate statement

			// Implement algorithm
			if (no_of_pare == 0 && errLevel == 0) {
				if ((!(Character.isDigit(result.charAt(0))) && (result
						.charAt(0) != '-'))
						|| !(Character.isDigit(result
								.charAt(result.length() - 1))))
					errLevel = 3;

				// Implement multiply and divide first
				for (index = 0; index < result.length() && (errLevel == 0); index++) {
					cha = result.charAt(index);

					if (cha == '*' || cha == '/') {
						if (!(Character.isDigit(result.charAt(index - 1)))
								|| (!(Character.isDigit(result
										.charAt(index + 1))) && (result
										.charAt(index + 1) != '-')))
							errLevel = 3;
						if (result.charAt(index + 1) == '-')
							if (!(Character.isDigit(result.charAt(index + 2))))
								errLevel = 3;
						if (errLevel == 0) {
							f_index = index - 1;

							if (f_index > 2)
								if (((result.charAt(f_index - 1)) == '-')
										&& ((result.charAt(f_index - 2)) == 'E'))
									f_index = f_index - 2;

							while ((f_index > 0)
									&& ((Character.isDigit(result
											.charAt(f_index - 1)))
											|| ((result.charAt(f_index - 1)) == '.') || ((result
											.charAt(f_index - 1)) == 'E'))) {
								f_index--;
							}
							if (f_index == 1)
								if ((result.charAt(f_index - 1)) == '-')
									f_index--;
							if (f_index > 2)
								if (((result.charAt(f_index - 1)) == '-')
										&& !(Character.isDigit(result
												.charAt(f_index - 2))))
									f_index--;
							f_operand = result.substring(f_index, index);

							r_index = index + 1;
							while ((r_index < result.length() - 1)
									&& ((Character.isDigit(result
											.charAt(r_index + 1)))
											|| ((result.charAt(r_index + 1)) == '.') || ((result
											.charAt(r_index + 1)) == 'E'))) {
								r_index++;
								if (r_index < result.length() - 2)
									if (((result.charAt(r_index)) == 'E')
											&& ((result.charAt(r_index + 1)) == '-'))
										r_index++;
							}
							r_operand = result
									.substring(index + 1, r_index + 1);

							if ((f_index != 0)
									&& (r_index != result.length() - 1)) {
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'N')
									errLevel = 4; // If an answer is not a
								// number return error
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'I')
									errLevel = 5; // If an answer is Infinity
								// return error

								result = result.substring(0, f_index)
										+ algorithm(cha, f_operand, r_operand)
										+ result.substring(r_index + 1);
								index = 0;
							} else if ((f_index == 0)
									&& (r_index == result.length() - 1)) {
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'N')
									errLevel = 4; // If an answer is not a
								// number return error
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'I')
									errLevel = 5; // If an answer is Infinity
								// return error

								result = algorithm(cha, f_operand, r_operand);
							} else if (f_index == 0) {
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'N')
									errLevel = 4; // If an answer is not a
								// number return error
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'I')
									errLevel = 5; // If an answer is Infinity
								// return error

								result = algorithm(cha, f_operand, r_operand)
										+ result.substring(r_index + 1);
								index = 0;
							} else if (r_index == result.length() - 1) {
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'N')
									errLevel = 4; // If an answer is not a
								// number return error
								if (algorithm(cha, f_operand, r_operand)
										.charAt(0) == 'I')
									errLevel = 5; // If an answer is Infinity
								// return error

								result = result.substring(0, f_index)
										+ algorithm(cha, f_operand, r_operand);
							}
						}
					}
				}// End of implement multiply and divide

				// Implement add and subtract
				for (index = 0; index < result.length() && (errLevel == 0); index++) {
					if (index == 0 && result.charAt(index) == '-')
						index = 1;

					if (index > 0)
						if (((result.charAt(index)) == 'E')
								&& ((result.charAt(index + 1)) == '-'))
							index = index + 2;

					cha = result.charAt(index);

					if (cha == '+' || cha == '-') {
						if (!(Character.isDigit(result.charAt(index - 1)))
								|| (!(Character.isDigit(result
										.charAt(index + 1))) && (result
										.charAt(index + 1) != '-')))
							errLevel = 3;
						if (result.charAt(index + 1) == '-')
							if (!(Character.isDigit(result.charAt(index + 2))))
								errLevel = 3;
						if (errLevel == 0) {
							f_index = 0;
							f_operand = result.substring(f_index, index);

							r_index = index + 1;
							while ((r_index < result.length() - 1)
									&& ((Character.isDigit(result
											.charAt(r_index + 1)))
											|| ((result.charAt(r_index + 1)) == '.') || ((result
											.charAt(r_index + 1)) == 'E'))) {
								r_index++;
								if (r_index < result.length() - 2)
									if (((result.charAt(r_index)) == 'E')
											&& ((result.charAt(r_index + 1)) == '-'))
										r_index++;
							}
							r_operand = result
									.substring(index + 1, r_index + 1);
							result = algorithm(cha, f_operand, r_operand)
									+ result.substring(r_index + 1);
							index = 0;
						}
					}
				}// End of implement add and subtract

			}// End of implement algorithm

		}
		return result;
	}

	/*
	 * Implements the simple expression.
	 */
	private String algorithm(char operator, String fOperand, String rOperand) {
		Double fw, rw;
		double f, r, ans = 0;
		String res;

		fw = new Double(fOperand);
		rw = new Double(rOperand);
		f = fw.doubleValue();
		r = rw.doubleValue();

		if (operator == '+')
			ans = f + r;
		if (operator == '-')
			ans = f - r;
		if (operator == '*')
			ans = f * r;
		if (operator == '/')
			ans = f / r;

		res = Double.toString(ans);

		return res;
	}

	/*
	 * Prompts error message.
	 */
	private void errMessage(int level) {
		switch (level) {
		case 1:
			input.setText("Parentheses do not match");
			break;
		case 2:
			input.setText("Invalid input");
			break;
		case 3:
			input.setText("Invalid expression");
			break;
		case 4:
			input.setText("Not a number");
			break;
		case 5:
			input.setText("Infinity");
			break;
		default:
			input.setText("Unknow error");
			break;
		}
	}
	
	/**
	 * Returns a service UI descriptorfor this service. Usally this method is
	 * used as an entry in provider configuration files when smart proxies are
	 * deployed with a standard off the shelf {@link ServiceProvider}.
	 * 
	 * @return service UI descriptor
	 */
	public static UIDescriptor getCalculatorDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIComponentFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/calculator-ui.jar") }, CalculatorUI.class
							.getName()));
		} catch (Exception ex) {
			logger
					.throwing(CalculatorUI.class.getName(), "getCalculatorDescriptor",
							ex);
		}
		return uiDesc;
	}
}
