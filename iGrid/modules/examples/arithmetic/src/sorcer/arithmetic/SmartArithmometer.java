package sorcer.arithmetic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.AdministratableProvider;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ArrayContext;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ProviderException;
import sorcer.core.proxy.Outer;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;

public class SmartArithmometer extends SemismartArithmometer implements
		Averager, Serializable {

	private static final long serialVersionUID = -8572140251918426722L;

	private final static Logger logger = Logger
			.getLogger(SmartArithmometer.class.getName());

	private AveragerRemote averager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.arithmetic.Averager#average(sorcer.base.ServiceContext)
	 * 
	 * This a local call on the SmartArithmometer object that in turn makes a
	 * remote call on its partner's proxy.
	 */
	public Context average(Context context)
			throws RemoteException {
		return averager.average(context);
	}

	/**
	 * If the publishing provider has a partner's proxy, use it for extending
	 * functionalty for {@link AveragerRemote#average(Context)}.
	 * 
	 * @see sorcer.core.proxy.Outer#setProxy(java.rmi.Remote)
	 */
	public void setInner(Object innerProxy) throws ProviderException {
		try {
			if (innerProxy instanceof AveragerRemote)
				averager = (AveragerRemote) innerProxy;

			if (innerProxy instanceof Outer)
				provider = (Provider) ((Outer) innerProxy).getInner();
		} catch (RemoteException e) {
			throw new ProviderException(
					"Not able to set innet proxy for AveragerRemote");
		}
	}
}
