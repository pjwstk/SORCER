package sorcer.arithmetic;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import sorcer.core.AdministratableProvider;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ProviderException;
import sorcer.core.proxy.Partnership;
import sorcer.core.proxy.RemotePartner;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;

public class PartnerAveragerImpl extends AveragerImpl implements RemotePartner,
		Partnership, Serializable {

	private static final long serialVersionUID = 3985768659269899314L;

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
	public Exertion service(Exertion exertion, Transaction transaction) throws RemoteException,
			ExertionException, TransactionException {
		return provider.service(exertion, null);
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

	/**
	 * Returns name of the local host.
	 * 
	 * @return local host name
	 * @throws UnknownHostException
	 */
	private String getHostname() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}

	public String getProviderName() throws RemoteException {
		return getClass().getName();
	}
}
