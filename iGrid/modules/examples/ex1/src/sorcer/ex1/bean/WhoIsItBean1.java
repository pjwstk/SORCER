package sorcer.ex1.bean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import sorcer.core.provider.Provider;
import sorcer.core.provider.ServiceProvider;
import sorcer.ex1.Message;
import sorcer.ex1.WhoIsIt;
import sorcer.ex1.provider.ProviderMessage;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.SorcerUtil;

public class WhoIsItBean1 implements WhoIsIt {

	private ServiceProvider provider;
	
	public void init(Provider provider) {
		this.provider = (ServiceProvider)provider;
	}
	
	public Context getHostName(Context context) throws RemoteException,
			ContextException {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			context.putValue("provider/hostname", hostname);
			context.putValue("provider/message", "Hello "
					+ context.getValue("requestor/address") + "!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return context;
	}

	public Context getHostAddress(Context context) throws RemoteException,
			ContextException {
		String ipAddress;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			context.putValue("provider/address", ipAddress);
			context.putValue("provider/message", "Hello "
					+ context.getValue("requestor/address") + "!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
