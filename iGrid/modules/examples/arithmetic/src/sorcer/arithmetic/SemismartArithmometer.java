package sorcer.arithmetic;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.transaction.Transaction;
import sorcer.core.AdministratableProvider;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ProviderException;
import sorcer.core.proxy.Partner;
import sorcer.core.proxy.Partnership;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.ServiceExertion;

public class SemismartArithmometer extends Arithmometer implements
		Serializable, Partner, Partnership {

	private static final long serialVersionUID = 6606124760247284084L;

	private final static Logger logger = Logger
			.getLogger(SemismartArithmometer.class.getName());

	protected Provider provider;

	private AdministratableProvider admin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.OuterProxy#setProxy(java.rmi.Remote)
	 */
	public void setInner(Object innerProxy) throws ProviderException {
		provider = (Provider) innerProxy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.OuterProxy#getProxy()
	 */
	public Remote getInner() throws RemoteException {
		return (Remote) provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.base.Service#service(sorcer.base.Exertion)
	 */
	public Exertion service(Exertion exertion) throws RemoteException,
			ExertionException {
		// execute task locally
		Context result = null;
		String selector = exertion.getProcessSignature().getSelector();
		if (selector.equals(ADD))
			result = add(exertion.getContext());
		else if ((selector.equals(SUBTRACT)))
			result = subtract(exertion.getContext());
		else if ((selector.equals(MULTIPLY)))
			result = multiply(exertion.getContext());
		else if ((selector.equals(DIVIDE)))
			result = divide(exertion.getContext());

		((ServiceExertion) exertion).setContext(result);
		return exertion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.base.Service#service(sorcer.base.Exertion)
	 */
	public Exertion service(Exertion exertion, Transaction transaction)
			throws RemoteException, ExertionException {
		return service(exertion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	public Object getAdmin() throws RemoteException {
		return admin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sorcer.core.provider.proxy.SmartProxy#setAdmin(java.lang.Object)
	 */
	public void setAdmin(Object adminProxy) {
		admin = (AdministratableProvider) adminProxy;
	}

	public String getProviderName() throws RemoteException {
		return getClass().getName();
	}
}
