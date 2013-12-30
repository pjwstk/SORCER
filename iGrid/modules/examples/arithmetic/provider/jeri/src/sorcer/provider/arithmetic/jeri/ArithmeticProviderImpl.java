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

package sorcer.provider.arithmetic.jeri;

import static sorcer.eo.operator.path;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.arithmetic.Adder;
import sorcer.arithmetic.ArithmeticRemote;
import sorcer.arithmetic.Divider;
import sorcer.arithmetic.Multiplier;
import sorcer.arithmetic.Subtractor;
import sorcer.arithmetic.ui.ArithmeticFrame;
import sorcer.arithmetic.ui.ArithmeticFrameUI;
import sorcer.arithmetic.ui.ArithmeticUI;
import sorcer.arithmetic.ui.CalculatorUI;
import sorcer.core.context.ArrayContext;
import sorcer.core.context.PositionalContext;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.provider.ServiceTasker;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.ui.serviceui.UIFrameFactory;
import sorcer.util.Sorcer;

import com.sun.jini.start.LifeCycle;

public class ArithmeticProviderImpl extends ServiceTasker implements
		ArithmeticRemote {

	/**
	 * Constructs an instance of the SORCER arithmetic provider implementing
	 * ArithmeticRemote. This constructor is required by Jini 2 life cycle
	 * management.
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public ArithmeticProviderImpl(String[] args, LifeCycle lifeCycle)
			throws Exception {
		super(args, lifeCycle);
	}

	/**
	 * Implements the {@link Adder} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 * @throws ContextException 
	 */
	public Context add(Context context) throws RemoteException, ContextException {
		return calculate(context, ADD);
	}

	/**
	 * Implements the {@link Subtractor} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 * @throws ContextException 
	 */
	public Context subtract(Context context) throws RemoteException, ContextException {
		return calculate(context, SUBTRACT);
	}

	/**
	 * Implements the {@link Multiplier} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws RemoteException
	 * @throws ContextException 
	 */
	public Context multiply(Context context) throws RemoteException, ContextException {
		return calculate(context, MULTIPLY);
	}

	/**
	 * Implements the {@link Divider} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
	 * @throws ContextException 
	 * @throws RemoteExceptionO
	 */
	public Context divide(Context context) throws RemoteException, ContextException {
		return calculate(context, DIVIDE);
	}

	private Context calculate(Context context, String selector)
			throws RemoteException, ContextException {
		Context out = null;
		if (context instanceof ArrayContext) {
			out = calculateFromArrayContext(context, selector);
		} else {
			out = calculateFromPositionalContext(context, selector);
		}
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA out context:\n" + out);
		return out;

	}
	
	/**
	 * Calculates the result of arithmetic operation specified by a selector
	 * (add, subtract, multiply, or divide) from the instance of ArrayContext.
	 * 
	 * @param input
	 *            service context
	 * @param selector
	 *            a name of arithmetic operation
	 * @return
	 * @throws RemoteException
	 * @throws ContextException
	 * @throws UnknownHostException
	 */
	private Context calculateFromArrayContext(Context context, String selector)
			throws RemoteException {
		ArrayContext cxt = (ArrayContext) context;
		try {
			// get sorted list of input values
			List<Double> inputs = (List<Double>)cxt.getInValues();
			logger.info("inputs: \n" + inputs);
			List<String> outpaths = cxt.getOutPaths();
			logger.info("outpaths: \n" + outpaths);

			double result = 0;
			if (selector.equals(ADD)) {
				result = 0;
				for (Double value : inputs)
					result += value;
			} else if (selector.equals(SUBTRACT)) {
				result = inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result -= inputs.get(i);
			} else if (selector.equals(MULTIPLY)) {
				result = inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result *= inputs.get(i);
			} else if (selector.equals(DIVIDE)) {
				result = inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result /= inputs.get(i);
			}

			logger.info(selector + " result: \n" + result);

			String outputMessage = "calculated by " + getHostname();
			if (outpaths.size() == 1) {
				// put the result in the existing output path
				cxt.putValue(outpaths.get(0), result);
				cxt.putValue(path(outpaths.get(0), ArrayContext.DESCRIPTION), outputMessage);
			} else {
				// put the result for a new output path
				logger.info("max index; " + cxt.getMaxIndex());
				int oi = cxt.getMaxIndex() + 1;
				cxt.ov(oi, result);
				cxt.ovd(oi, outputMessage);
			}

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " calculate exception", ex);
		}
		return (Context) context;
	}

	/**
	 * Calculates the result of arithmetic operation specified by a selector
	 * (add, subtract, multiply, or divide) from the instance of ServiceContext.
	 * 
	 * @param input
	 *            service context
	 * @param selector
	 *            a name of arithmetic operation
	 * @return
	 * @throws RemoteException
	 * @throws ContextException
	 * @throws UnknownHostException
	 */
	private Context calculateFromPositionalContext(Context context, String selector)
			throws RemoteException, ContextException {
		PositionalContext cxt = (PositionalContext) context;
		try {
			// get sorted list of input values
			List<Double> inputs = (List<Double>)cxt.getInValues();
			logger.info("inputs: \n" + inputs);
			List<String> outpaths = cxt.getOutPaths();
			logger.info("outpaths: \n" + outpaths);

			double result = 0.0;
			if (selector.equals(ADD)) {
					result = inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result += inputs.get(i);
			} else if (selector.equals(SUBTRACT)) {
				if (inputs.size() > 2) {
					throw new ContextException("more than two arguments for subtraction");
				}
				result = (Double)cxt.getInValueAt(1);
				result -= (Double)cxt.getInValueAt(2);
			} else if (selector.equals(MULTIPLY)) {
				result = inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result *= inputs.get(i);
			} else if (selector.equals(DIVIDE)) {
				if (inputs.size() > 2)
					throw new ContextException("more than two arguments for division");
				result = (Double)cxt.getInValueAt(1);
				result /= (Double)cxt.getInValueAt(2);
			}

			logger.info(selector + " result: \n" + result);

			String outputMessage = "calculated by " + getHostname();
			
			String outPath = selectOutPath(outpaths);
			cxt.putValue(outPath, result);
			cxt.putValue(path(outPath, ArrayContext.DESCRIPTION), outputMessage);
		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " calculate exception", ex);
		}
		return (Context) context;
	}
	
	private String selectOutPath(List<String> paths) {
		String pathPrefix = "result";
		if (paths.size() == 1) {
			return paths.get(0);
		} else {
			for (String path : paths) {
				if (path.startsWith(pathPrefix)) {
					return path;
				}
			}
			return pathPrefix;
		}
	}
	
	/**
	 * Returns a service UI descriptor for a simple Arithmetic UI. The service
	 * UI allows for invoking and testing Arithmetic interface.
	 * 
	 * @see sorcer.core.provider.Provider#getMainUIDescriptor()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.ServiceProvider#getMainUIDescriptor()
	 */
	public UIDescriptor getMainUIDescriptor() {
		return getUIDescriptor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.ServiceProvider#getUIDescriptor()
	 */
	public static UIDescriptor getUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIComponentFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/arithmetic-ui.jar") }, ArithmeticUI.class
							.getName()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uiDesc;
	}

	/**
	 * Returns a UIDescriptor using a UIFrameFactory for a frame UI.
	 * 
	 * @return a service UIDesctiptor
	 */
	public static UIDescriptor getFrameUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIFrameFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/arithmetic-frame-ui.jar") },
							ArithmeticFrameUI.class.getName()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uiDesc;
	}

	/**
	 * Returns a UIDescriptor using a UIComponentFactory for a frame UI.
	 * 
	 * @return a service UIDesctiptor
	 */
	public static UIDescriptor getComponentUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					new UIComponentFactory(new URL[] { new URL(Sorcer
							.getWebsterUrl()
							+ "/arithmetic-component-ui.jar") },
							ArithmeticFrame.class.getName(), true));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uiDesc;
	}

	/**
	 * Returns a service UI descriptor for this service. Usually this method is
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
			logger.throwing(CalculatorUI.class.getName(),
					"getCalculatorDescriptor", ex);
		}
		return uiDesc;
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

	/**
	 * Returns expected service contexts by this provider. This
	 * information about service context formats for this provider can be used
	 * by requestors to define relevant domain specific data.
	 * 
	 * @see sorcer.core.provider.Provider#getMethodContexts()
	 */
	public Map<String, Context> getMethodContexts() throws RemoteException {
		Map<String, Context> hm = new HashMap<String, Context>();
		ArrayContext ic = new ArrayContext("arithmetic");
		try {
			ic.iv(1, 0);
			ic.ivc(1, "operand 1");

			ic.iv(2, 0);
			ic.ivc(2, "operand 2");
			ic.ivd(2, "two operands are provided for a binary arithmetic operator");

			ic.ov(3, 0);
			ic.ovc(3, "output for operator on values 1 and 2");

			hm.put("Arithmetic.*", ic);
		} catch (ContextException e) {
			logger.throwing(ArithmeticProviderImpl.class.getName(),
					"getMethodContexts", e);
		}
		logger.info("provider's defined context: " + ic);
		return hm;
	}
}
