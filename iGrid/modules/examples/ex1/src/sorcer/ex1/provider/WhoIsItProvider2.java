package sorcer.ex1.provider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.provider.ServiceTasker;
import sorcer.ex1.Message;
import sorcer.ex1.WhoIsIt;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.util.SorcerUtil;

import com.sun.jini.start.LifeCycle;

public class WhoIsItProvider2 extends ServiceTasker implements WhoIsIt {

	public WhoIsItProvider2(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
	}

	/* (non-Javadoc)
	 * @see sorcer.ex1.provider.WhoIsIt#getHostName(sorcer.service.Context)
	 */
	@Override
	public Context getHostName(Context context) throws RemoteException,
			ContextException {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
			context.putValue("provider/hostname", hostname);
			String rhn = (String) context.getValue("requestor/hostname");
			Message msg = (Message) context.getValue("requestor/message");
			context.putValue("provider/message", new ProviderMessage(msg
					.getMessage(), getProviderName(), rhn));
			
			Thread.sleep(1000);
			context.reportException(new RuntimeException("getHostName: slept for 1 sec"));
			context.appendTrace(getClass().getName() + ":" + getProviderName());
			
			Logger cl = getContextLogger();
			if (cl != null)
				cl.info("executed getHostName: " + context);
			
			Logger pl = getProviderLogger();
			if (pl != null)
				pl.info("executed getHostName: " + context);
			
			Logger rl = getRemoteLogger();
			if (rl != null)
				rl.info(">>>>>>>>>>>> out context: " + context);
		} catch (UnknownHostException e1) {
			context.reportException(e1);
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			context.reportException(e2);
			e2.printStackTrace();
		}
		return context;
	}
	
	/* (non-Javadoc)
	 * @see sorcer.ex1.provider.WhoIsIt#getHostAddress(sorcer.service.Context)
	 */
	@Override
	public Context getHostAddress(Context context) throws RemoteException,
			ContextException {
		String ipAddress;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
			context.putValue("provider/address", ipAddress);
			String rhn = (String) context.getValue("requestor/hostname");
			Message rmsg = (Message) context.getValue("requestor/message");
			context.putValue("provider/message", new ProviderMessage(rmsg
					.getMessage(), getProviderName(), rhn));
			
			Thread.sleep(1000);
			context.reportException(new RuntimeException("getHostAddress: slept for 1 sec"));
			context.appendTrace(getClass().getName() + ":" + getProviderName());
			
			Logger cl = getContextLogger();
			if (cl != null)
				cl.info("executed getHostAddress: " + context);
			
			Logger pl = getProviderLogger();
			if (pl != null)
				pl.info("executed getHostName: " + context);
			
			Logger rl = getRemoteLogger();
			if (rl != null)
				rl.info(">>>>>>>>>>>> out context: " + context);
		} catch (UnknownHostException e1) {
			context.reportException(e1);
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			context.reportException(e2);
			e2.printStackTrace();
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
			context.putValue("provider/message",
					new ProviderMessage(rmsg.getMessage(), getProviderName(),
							rhn));
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
