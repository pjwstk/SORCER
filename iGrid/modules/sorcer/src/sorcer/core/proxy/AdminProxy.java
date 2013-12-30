/*
 * Copyright 2013 the original author or authors.
 * Copyright 2013 SorcerSoft.org.
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

package sorcer.core.proxy;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.core.constraint.MethodConstraints;
import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.id.ReferentUuid;
import net.jini.id.Uuid;
import sorcer.core.AdministratableProvider;
import sorcer.util.Log;
/**
 * A proxy for Service Provider admin
 * 
 * @author Mike Sobolewski
 */
public class AdminProxy implements AdministratableProvider, ReferentUuid, RemoteMethodControl, Serializable {

	private static final long serialVersionUID = -3315793943325549727L;
	
	protected final static Logger logger = Log.getTestLog();

	private AdministratableProvider provider;
			
	private Uuid referentUuid;
	
	public AdminProxy(AdministratableProvider provider, Uuid referentUuid) throws UnknownHostException {	
		this.provider = provider;
		this.referentUuid = referentUuid;
    }

	/* (non-Javadoc)
	 * @see com.sun.jini.admin.DestroyAdmin#destroy()
	 */
	@Override
	public void destroy() throws RemoteException {
		provider.destroy();
	}
	
	/* (non-Javadoc)
	 * @see net.jini.admin.Administrable#getAdmin()
	 */
	@Override
	public Object getAdmin() throws RemoteException {
		return  provider.getAdmin();
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#getLookupAttributes()
	 */
	@Override
	public Entry[] getLookupAttributes() throws RemoteException {
		return provider.getLookupAttributes();
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#addLookupAttributes(net.jini.core.entry.Entry[])
	 */
	@Override
	public void addLookupAttributes(Entry[] attrSets) throws RemoteException {
		provider.addLookupAttributes(attrSets);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#modifyLookupAttributes(net.jini.core.entry.Entry[], net.jini.core.entry.Entry[])
	 */
	@Override
	public void modifyLookupAttributes(Entry[] attrSetTemplates,
			Entry[] attrSets) throws RemoteException {
		provider.modifyLookupAttributes(attrSetTemplates, attrSets);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#getLookupGroups()
	 */
	@Override
	public String[] getLookupGroups() throws RemoteException {
		return provider.getLookupGroups();
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#addLookupGroups(java.lang.String[])
	 */
	@Override
	public void addLookupGroups(String[] groups) throws RemoteException {
		provider.addLookupGroups(groups);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#removeLookupGroups(java.lang.String[])
	 */
	@Override
	public void removeLookupGroups(String[] groups) throws RemoteException {
		provider.removeLookupGroups(groups);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#setLookupGroups(java.lang.String[])
	 */
	@Override
	public void setLookupGroups(String[] groups) throws RemoteException {
		provider.setLookupGroups(groups);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#getLookupLocators()
	 */
	@Override
	public LookupLocator[] getLookupLocators() throws RemoteException {
		return provider.getLookupLocators();
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#addLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	@Override
	public void addLookupLocators(LookupLocator[] locators)
			throws RemoteException {
		provider.addLookupLocators(locators);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#removeLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	@Override
	public void removeLookupLocators(LookupLocator[] locators)
			throws RemoteException {
		provider.removeLookupLocators(locators);
	}

	/* (non-Javadoc)
	 * @see net.jini.admin.JoinAdmin#setLookupLocators(net.jini.core.discovery.LookupLocator[])
	 */
	@Override
	public void setLookupLocators(LookupLocator[] locators)
			throws RemoteException {
		provider.setLookupLocators(locators);
	}
	
	/* (non-Javadoc)
	 * @see net.jini.core.constraint.RemoteMethodControl#setConstraints(net.jini.core.constraint.MethodConstraints)
	 */
	@Override
	public RemoteMethodControl setConstraints(MethodConstraints constraints) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.jini.core.constraint.RemoteMethodControl#getConstraints()
	 */
	@Override
	public MethodConstraints getConstraints() {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.jini.id.ReferentUuid#getReferentUuid()
	 */
	@Override
	public Uuid getReferentUuid() {
		return referentUuid;
	}
}
