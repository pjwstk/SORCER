/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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
package sorcer.core.provider;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.jini.core.constraint.RemoteMethodControl;
import net.jini.core.lookup.ServiceItem;
import net.jini.export.ProxyAccessor;
import net.jini.security.proxytrust.ServerProxyTrust;
import sorcer.core.AdministratableProvider;
import sorcer.core.RemoteContextManagement;
import sorcer.core.proxy.Partner;
import sorcer.core.proxy.Partnership;
import sorcer.service.DynamicAccessor;
import sorcer.service.Service;
import sorcer.service.Signature;
import sorcer.service.SignatureException;

import com.sun.jini.start.LifeCycle;



 public class StaticServiceProvider extends ServiceProvider implements
		AdministratableProvider, ProxyAccessor, ServerProxyTrust,
		RemoteMethodControl, LifeCycle, Partner, Partnership, RemoteContextManagement {
	// RemoteMethodControl is needed to enable Proxy Constraints
	 
	private static DynamicAccessor accessor;
		
	static {
		accessor = new StaticProviderAccessor();
//		ServiceAccessor
//				.setAccessor(new StaticProviderAccessor());
	}

	// all providers in the same shared JVM
	private static Map<String, ServiceProvider> providers = new HashMap<String, ServiceProvider>();	
	
	protected StaticServiceProvider() throws RemoteException {
		providers.put(getProviderName(), this);
		delegate.provider = this;
	}

	/**
	 * Required constructor for Jini 2 NonActivatableServiceDescriptors
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws Exception
	 */
	public StaticServiceProvider(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
	}

	public static class StaticProviderAccessor implements DynamicAccessor {

		public StaticProviderAccessor() {
			
		}
		
		/* (non-Javadoc)
		 * @see sorcer.service.DynamicAccessor#getService(sorcer.service.Signature)
		 */
		@Override
		public Service getService(Signature signature)
				throws SignatureException {
			Service provider = providers.get(signature.getProviderName());
			if (provider == null) {
				Collection<ServiceProvider> servicers = providers.values();
				for (ServiceProvider p : servicers) {
					Class<?>[] interfaces = p.getClass().getInterfaces();
					for (Class<?> c : interfaces) {
					if (c==signature.getServiceType())
						provider = p;
					}
				}
			}
			return provider;
		}

		/* (non-Javadoc)
		 * @see sorcer.service.DynamicAccessor#getServiceItem(sorcer.service.Signature)
		 */
		@Override
		public ServiceItem getServiceItem(Signature signature)
				throws SignatureException {
			return null;
		}
		
	}

	public static Service getProvider(Signature signature) throws SignatureException {
		return accessor.getService(signature);
	}
}
