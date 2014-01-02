package sorcer.ex1.bean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.provider.Provider;
import sorcer.core.provider.ServiceProvider;
import sorcer.ex1.Message;
import sorcer.ex1.WhoIsIt;
import sorcer.ex1.provider.ProviderMessage;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.SorcerUtil;

@SuppressWarnings("rawtypes")
public class WhoIsItBean2 implements WhoIsIt {

	private ServiceProvider provider;
	private Logger logger;
	
	public void init(Provider provider) {
		this.provider = (ServiceProvider)provider;
		try {
			logger = provider.getLogger();
		} catch (RemoteException e) {
			// ignore it, local call
		}
	}
	
	public Context getHostName(Context context) throws RemoteException,
			ContextException {
		String hostname;
		logger.entering(WhoIsItBean2.class.getName(), "getHostName");
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			context.putValue("provider/hostname", hostname);
			String rhn = (String) context.getValue("requestor/hostname");
			Message rmsg = (Message) context.getValue("requestor/message");
			context.putValue("provider/message", new ProviderMessage(rmsg
					.getMessage(), provider.getProviderName(), rhn));
			
			Thread.sleep(2000);
			context.reportException(new RuntimeException("Slept for 2 sec"));
			context.appendTrace(getClass().getName() + ":" + provider.getProviderName());
		
			logger.info("executed getHostName: " + context);

		} catch (UnknownHostException e1) {
			throw new ContextException("getHostAddress", e1);
		} catch (InterruptedException e2) {
			throw new ContextException("getHostAddress", e2);
		}
		return context;
	}

	public Context getHostAddress(Context context) throws RemoteException,
			ContextException {
		String ipAddress;
		logger.entering(WhoIsItBean2.class.getName(), "getHostName");
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			context.putValue("provider/address", ipAddress);
			String rhn = (String) context.getValue("requestor/hostname");
			Message rmsg = (Message) context.getValue("requestor/message");
			context.putValue("provider/message", new ProviderMessage(rmsg
					.getMessage(), provider.getProviderName(), rhn));
			
			Thread.sleep(2000);
			context.reportException(new RuntimeException("Slept for 2 sec"));
			context.appendTrace(getClass().getName() + ":" + provider.getProviderName());
			
			logger.info("executed getHostAddress: " + context);

		} catch (UnknownHostException e1) {
			throw new ContextException("getHostAddress", e1);
		} catch (InterruptedException e2) {
			throw new ContextException("getHostAddress", e2);
		}
		return context;
	}

	/* (non-Javadoc)
	 * @see sorcer.ex1.provider.WhoIsIt#getCanonicalHostName(sorcer.service.Context)
	 */
	public Context getCanonicalHostName(Context context)
			throws RemoteException, ContextException {
		String fqname;
		try {
			fqname = InetAddress.getLocalHost().getCanonicalHostName();
			context.putValue("provider/fqname", fqname);
			String rhn = (String) context.getValue("requestor/hostname");
			Message rmsg = (Message) context.getValue("requestor/message");
			context.putValue("provider/message", new ProviderMessage(rmsg
					.getMessage(), provider.getProviderName(), rhn));
		} catch (UnknownHostException e1) {
			context.reportException(e1);
			e1.printStackTrace();
		}
		return context;
	}

	/* (non-Javadoc)
	 * @see sorcer.ex1.provider.WhoIsIt#getTimestamp(sorcer.service.Context)
	 */
	@Override
	public Context getTimestamp(Context context) throws RemoteException,
			ContextException {
		context.putValue("provider/timestamp", SorcerUtil.getDateTime());
		return context;
	}
}
