package sorcer.arithmetic.requestor.cast;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import sorcer.arithmetic.Arithmetic;
import sorcer.core.context.ArrayContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class ArithmeticUnicastRequestor {

	public static Logger logger = Logger
			.getLogger(ArithmeticUnicastRequestor.class.getName());

	public static void main(String argv[]) {
		new ArithmeticUnicastRequestor();
	}

	public ArithmeticUnicastRequestor() {
		LookupLocator lookup = null;
		ServiceRegistrar registrar = null;
		Arithmetic arithmetic = null;

		try {
			lookup = new LookupLocator("jini://localhost");
		} catch (java.net.MalformedURLException e) {
			logger.severe("not able to get locator");
			logger.throwing(ArithmeticUnicastRequestor.class.getName(),
					"ArithmeticUnicastRequestor", e);
			System.exit(1);
		}

		System.setSecurityManager(new RMISecurityManager());

		try {
			registrar = lookup.getRegistrar();
		} catch (java.io.IOException e) {
			logger.severe("not able to find lookup service");
			logger.throwing(ArithmeticUnicastRequestor.class.getName(),
					"ArithmeticUnicastRequestor", e);
			System.exit(1);
		} catch (java.lang.ClassNotFoundException e) {
			logger.throwing(ArithmeticUnicastRequestor.class.getName(),
					"ArithmeticUnicastRequestor", e);
			System.exit(1);
		}

		Class[] classes = new Class[] { Arithmetic.class };
		ServiceTemplate template = new ServiceTemplate(null, classes, null);
		try {
			arithmetic = (Arithmetic) registrar.lookup(template);
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}

		if (arithmetic == null) {
			logger.info("Arithmetic proxy is null");
			System.exit(2);
		}
		// Use the service to submit a service context
		Context context = null;
		try {
			context = getContext();
			context = arithmetic.multiply(context);
			logger.info("output servcie context: " + context);
		} catch (Exception e) {
			logger.throwing(ArithmeticUnicastRequestor.class.getName(),
					"ArithmeticUnicastRequestor", e);
		}
		logger.info("result: " + context);
		// we are done
		System.exit(0);
	}

	private Context getContext() throws ContextException {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 10.0);
		context.ivc(1, "arg1");
		context.iv(2, 50.0);
		context.ov(3, 0.0);
		context.ovc(3, "result for multiplying values 1 and 2");

		return context;
	}
}
