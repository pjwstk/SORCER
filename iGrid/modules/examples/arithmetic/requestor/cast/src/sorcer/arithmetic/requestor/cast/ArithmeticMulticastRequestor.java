package sorcer.arithmetic.requestor.cast;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import sorcer.arithmetic.Arithmetic;
import sorcer.core.context.ArrayContext;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.Sorcer;

public class ArithmeticMulticastRequestor implements DiscoveryListener {

	public static Logger logger = Logger
			.getLogger(ArithmeticMulticastRequestor.class.getName());

	public static void main(String argv[]) {
		new ArithmeticMulticastRequestor();

		// stay around long enough to gte discovery responses
		try {
			Thread.currentThread().sleep(30000L);
		} catch (java.lang.InterruptedException e) {
			// do nothing
		}
	}

	public ArithmeticMulticastRequestor() {
		System.setSecurityManager(new RMISecurityManager());
		LookupDiscovery discover = null;
		try {
			discover = new LookupDiscovery(Sorcer.getLookupGroups());
		} catch (Exception e) {
			logger.severe("not able find lookup service");
			logger.throwing(ArithmeticMulticastRequestor.class.getName(),
					"MulticastRequestor", e);
			System.exit(1);
		}
		discover.addDiscoveryListener(this);
	}

	public void discovered(DiscoveryEvent evt) {

		ServiceRegistrar[] registrars = evt.getRegistrars();
		Class[] classes = new Class[] { Arithmetic.class };
		Arithmetic arithmetic = null;
		ServiceTemplate template = new ServiceTemplate(null, classes, null);

		for (int n = 0; n < registrars.length; n++) {
			logger.info("lookup service discovered");
			ServiceRegistrar registrar = registrars[n];
			try {
				arithmetic = (Arithmetic) registrar.lookup(template);
			} catch (java.rmi.RemoteException e) {
				logger.throwing(ArithmeticMulticastRequestor.class.getName(),
						"discovered", e);
				continue;
			}
			if (arithmetic == null) {
				logger.info("Arithmetic proxy is null");
				continue;
			}
			// Use the service to submit a service context
			Context context = null;
			try {
				context = getContext();
				context = arithmetic.add(context);
			} catch (Exception e) {
				logger.throwing(ArithmeticMulticastRequestor.class.getName(),
						"discovered", e);
			}
			logger.info("result: " + context);
			// we are done
			System.exit(0);
		}
	}

	private Context getContext() throws ContextException {
		ArrayContext context = new ArrayContext("arithmetic");
		context.iv(1, 20.0);
		context.ivc(1, "arg1");
		context.iv(2, 80.0);
		context.ivc(2, "arg2");
		context.ov(3, 0.0);
		context.ovc(3, "result for adding arg1 and arg2");

		return context;
	}

	public void discarded(DiscoveryEvent event) {
		logger.info("discovery event:  + event");
	}
}
