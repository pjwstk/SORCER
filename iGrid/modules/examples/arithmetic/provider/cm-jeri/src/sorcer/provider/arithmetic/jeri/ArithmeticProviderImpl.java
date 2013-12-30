package sorcer.provider.arithmetic.jeri;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.Enumeration;
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
import sorcer.arithmetic.ui.ArithmeticFrame;
import sorcer.arithmetic.ui.ArithmeticFrameUI;
import sorcer.arithmetic.ui.ArithmeticUI;
import sorcer.arithmetic.ui.CalculatorUI;
import sorcer.core.SORCER;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.context.eval.ContextNode;

import sorcer.core.provider.ServiceProvider;
import sorcer.core.provider.ServiceTasker;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.ui.context.ContextBrowser;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.ui.serviceui.UIFrameFactory;
import sorcer.util.Sorcer;
import sorcer.util.IndexedContext;

import com.sun.jini.start.LifeCycle;

public class ArithmeticProviderImpl extends ServiceTasker implements
		ArithmeticRemote, SORCER {

	public ArithmeticProviderImpl() throws Exception {
		// do nothing
	}
	
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
	 */
	public Context add(Context context) throws RemoteException {
		return calculate(context, Arithmetic.ADD);
	}

	/**
	 * Implements the {@link Subtractor} interface.
	 * 
	 * @param context
	 *            input context for this operation
	 * @return an output service context
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
	 * @return an output service context
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
	 * @return an output service context
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
			
		
		try {
			// get sorted list of inout values
			List inputs;
			List outpaths;
			if(context instanceof IndexedContext)
			{
				IndexedContext cxt= (IndexedContext) context;
				inputs = cxt.getInputValues();
				outpaths = Contexts.listOutputPaths(cxt);
			}
			else
			{
				ServiceContext cxt2=(ServiceContext) context;
				//Enumeration enumer=cxt2.getInputPaths();
				
				
				inputs = Contexts.listInputPaths(cxt2);
				for(int i=0;i<inputs.size();i++)
				{
					//ContextNode test=new ContextBrowser();
					if(cxt2.getValue((String)inputs.get(i)) instanceof ContextNode)
						inputs.set(i, new Double(((ContextNode)cxt2.getValue((String)inputs.get(i))).getData(cxt2).toString()));
					else
						inputs.set(i, cxt2.getValue((String)inputs.get(i)));
				}
				outpaths = Contexts.listOutputPaths(cxt2);
			}
			
			logger.info("inputs: \n" + inputs);
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
			
			if(context instanceof IndexedContext)
			{
				IndexedContext cxt= (IndexedContext) context;
				if (outpaths.size() == 1) {
					// put the result in the existing output path
					cxt.putValue((String) outpaths.get(0), result);
					cxt.putValue((String) outpaths.get(0) + CPS
							+ IndexedContext.DESCRIPTION, outputMessage);
				} else {
					// put the result for a new output path
					logger.info("max index; " + cxt.getMaxIndex());
					int oi = cxt.getMaxIndex() + 1;
					cxt.ov(oi, result);
					cxt.ovd(oi, outputMessage);
				}
			}
			else
			{
				ServiceContext cxt2=(ServiceContext) context;
				//Enumeration enumer=cxt2.getInputPaths();
				if (outpaths.size() == 1) {
					// put the result in the existing output path
					cxt2.putValue((String) outpaths.get(0), result);
					cxt2.putValue((String) outpaths.get(0) + CPS
							+ IndexedContext.DESCRIPTION, outputMessage);
				} else {
					// put the result for a new output path
					
					cxt2.putValue("output"+selector, result);
					cxt2.putValue("output"+selector + CPS
							+ IndexedContext.DESCRIPTION, outputMessage);
				}
				
				
			}
			

		} catch (Exception ex) {
			// ContextException, UnknownHostException
			System.out.println("error is "+ex.getMessage());
			//throw new RemoteException(selector + " calculate exception", ex);
			return new ServiceContext("Failed with "+ex.getMessage());
		}
		return (Context) context;
	}

	/**
	 * Returns a service UI descriptor for a simple Arithmetic UI. The service
	 * UI allows for invoking and testing Arithmetic interface.
	 * 
	 * @see sorcer.core.Provider#getMainUIDescriptor()
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
					new UIComponentFactory(new URL[] { new URL(Env
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
					new UIFrameFactory(new URL[] { new URL(Env.getWebsterUrl()
							+ "/arithmetic-frame-ui.jar") }, ArithmeticFrameUI.class
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
					new UIComponentFactory(new URL[] { new URL(Env
							.getWebsterUrl()
							+ "/arithmetic-component-ui.jar") }, ArithmeticFrame.class
							.getName(), true));
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
					new UIComponentFactory(new URL[] { new URL(Env
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
	 * @see sorcer.core.Provider#getMethodContexts()
	 */
	public Map getMethodContexts() throws RemoteException {
		Map hm = new HashMap();
		IndexedContext ic = new IndexedContext("arithmetic");
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
			logger.throwing(ArithmeticProviderImpl.class.getName(),
					"getMethodContexts", e);
		}
		logger.info("provider registerd context: " + ic);
		return hm;
	}
}
