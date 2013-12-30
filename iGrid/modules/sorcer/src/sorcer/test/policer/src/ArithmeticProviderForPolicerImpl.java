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

package sorcer.test.policer.src;

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
import sorcer.arithmetic.Arithmetic;
import sorcer.arithmetic.ArithmeticRemote;
import sorcer.arithmetic.Divider;
import sorcer.arithmetic.Multiplier;
import sorcer.arithmetic.Subtractor;
import sorcer.core.SorcerConstants;
import sorcer.test.policer.src.ui.ArithmeticFrame;
import sorcer.test.policer.src.ui.ArithmeticFrameUI;
import sorcer.test.policer.src.ui.ArithmeticUI;
import sorcer.core.context.ArrayContext;
import sorcer.core.context.Contexts;
import sorcer.core.provider.ServiceProvider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.ui.serviceui.UIFrameFactory;
import sorcer.util.Sorcer;

import com.sun.jini.start.LifeCycle;

public class ArithmeticProviderForPolicerImpl extends ServiceProvider implements
		ArithmeticRemote, SorcerConstants {

	/**
	 * Constructs an instance of the SORCER arithmetic provider implenting
	 * ArithmeticRemote. This constructor is required by Jini 2 life cycle
	 * management.
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public ArithmeticProviderForPolicerImpl(String[] args, LifeCycle lifeCycle)
			throws Exception {
		super(args, lifeCycle);
		System.out.println("Daniela's provider");
	}

	/**
	 * Implements the {@link Adder} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an ouitput service context
	 * @throws RemoteException
	 */
	public Context add(Context context) throws RemoteException {
		return calculate(context, Arithmetic.ADD);
	}

	/**
	 * Implements the {@link Subtractor} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an ouitput service context
	 * @throws RemoteException
	 */
	public Context subtract(Context context)
			throws RemoteException {
		return calculate(context, SUBTRACT);
	}

	/**
	 * Implements the {@link Multiplier} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an ouitput service context
	 * @throws RemoteException
	 */
	public Context multiply(Context context)
			throws RemoteException {
		return calculate(context, MULTIPLY);
	}

	/**
	 * Implements the {@link Divider} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an ouitput service context
	 * @throws RemoteExceptionO
	 */
	public Context divide(Context context) throws RemoteException {
		return calculate(context, DIVIDE);
	}

	/**
	 * Calculates the result of arithmetic operation specified by a selector
	 * (add, subtract, multiply, or divide).
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
	private Context calculate(Context context, String selector)
			throws RemoteException {
		ArrayContext cxt = (ArrayContext) context;
		try {
			// get sorted list of inout values
			List inputs = cxt.getInValues();
			logger.info("inputs: \n" + inputs);
			List outpaths = Contexts.getOutPaths(cxt);
			logger.info("getOutputPaths: \n" + outpaths);

			double result = 0;
			if (selector.equals(ADD)) {
				result = 0;
				for (Object value : inputs)
					result += (Double) value;
			} else if (selector.equals(SUBTRACT)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result -= (Double) inputs.get(i);
			} else if (selector.equals(MULTIPLY)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result *= (Double) inputs.get(i);
			} else if (selector.equals(DIVIDE)) {
				result = (Double) inputs.get(0);
				for (int i = 1; i < inputs.size(); i++)
					result /= (Double) inputs.get(i);
			}

			logger.info(selector + " result: \n" + result);

			String outputMessage = "calculated by " + getHostname();
			if (outpaths.size() == 1) {
				// put the result in the existing output path
				cxt.putValue((String) outpaths.get(0), result);
				cxt.putValue((String) outpaths.get(0) + CPS
						+ ArrayContext.DESCRIPTION, outputMessage);
			} else {
				// put the result for a new output path
				logger.info("max index; " + cxt.getMaxIndex());
				int oi = cxt.getMaxIndex() + 1;
				cxt.ov(oi, result);
				cxt.ovd(oi, outputMessage);
			}

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			throw new RemoteException(selector + " calculate execption", ex);
		}
		return (Context) context;
	}

	/**
	 * Returns a service UI desctiptor for a simple Arithmetic UI. The service
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
							+ "/policer-test-ui.jar") }, ArithmeticUI.class
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
					new UIFrameFactory(new URL[] { new URL(Sorcer.getWebsterUrl()
							+ "/policer-test-frame-ui.jar") }, ArithmeticFrameUI.class
							.getName()));
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
							+ "/policer-test-component-ui.jar") }, ArithmeticFrame.class
							.getName(), true));
		} catch (Exception ex) {
			ex.printStackTrace();
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
	 * Returns service contexts to be registered with lookup services. This
	 * information about service context formats for this provider can be used
	 * by users and tools to define relevant service tasks and jobs easy.
	 * 
	 * @see sorcer.core.provider.Provider#getMethodContexts()
	 */
	public Map getMethodContexts() {
		Map hm = new HashMap();
		ArrayContext ic = new ArrayContext("arithmetic");
		try {
			ic.iv(1, 0);
			ic.ivc(1, "argument 1");

			ic.iv(2, 0);
			ic.ivc(2, "argument 2");
			ic
					.ivd(
							2,
							"two argument are provided for a binary aritmetic operator, \n however mutiple argument are accepted");

			ic.ov(3, 0);
			ic.ovc(5, "output for operations on values 1 and 2");

			hm.put("Arithmetic.*", ic);
		} catch (ContextException e) {
			logger.throwing(ArithmeticProviderForPolicerImpl.class.getName(),
					"getMethodContexts", e);
		}
		logger.info("provider registerd context: " + ic);
		return hm;
	}
}
