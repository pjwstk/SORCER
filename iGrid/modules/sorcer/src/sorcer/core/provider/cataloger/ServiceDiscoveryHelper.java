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
package sorcer.core.provider.cataloger;

import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.LookupCache;
import net.jini.lease.LeaseRenewalManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.lookup.ServiceItemFilter;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.ServiceDiscoveryListener;
import net.jini.lookup.ServiceDiscoveryEvent;
import net.jini.core.entry.Entry;
import net.jini.lookup.entry.Name;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;


public class ServiceDiscoveryHelper{

 	private LeaseRenewalManager _lrm=new LeaseRenewalManager();
 	private Object _lock=new Object();
 	private Object _proxy;
 	 
	/**
     * Locates the first matching service via multicast discovery
     * @param serviceClass The class object representing the interface of the service
     * @param waitTime How to wait for the service to be discovered
     * @throws IOException
     * @throws InterruptedException
     * @return  */    
    public static Object getService(Class serviceClass,long waitTime)
        throws java.io.IOException,InterruptedException{
            
        return getService(serviceClass,null,waitTime);    
    }
    /**
     * Locates the first matching service via multicast discovery
     * @param serviceClass The class object representing the interface of the service
     * @param serviceName The Name attribute of the service
     * @throws IOException
     * @throws InterruptedException
     * @return  */    
    public static Object getService(Class serviceClass,String serviceName,long waitTime)
        throws java.io.IOException,InterruptedException{
    
        ServiceDiscoveryHelper sdh=new ServiceDiscoveryHelper();
        return sdh.getServiceImpl(serviceClass,serviceName,waitTime);
    }
     private Object getServiceImpl(Class serviceClass,String serviceName,long waitTime)
        throws java.io.IOException,InterruptedException{
        
                
         Class [] types=new Class[]{serviceClass};        
         Entry [] entry=null;
         
         if(serviceName!=null){
            entry=new Entry[]{new Name(serviceName)};
        }

         ServiceTemplate template=new ServiceTemplate(null,types,entry);
         
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        //Example of how to use ServiceDisovery Manager
        ServiceItemFilter sif=new ServiceItemFilter(){
            public boolean check(ServiceItem item){
                System.out.println("check() "+item.service);
                return true;
            }
        };
        //ServiceDiscoveryListener is called by the ServiceDiscoveryManager
        ServiceDiscoveryListener sdl=new ServiceDiscoveryListener(){
            public void serviceAdded(ServiceDiscoveryEvent event) {
                System.out.println("serviceAdded "+event);
                
                ServiceItem si=event.getPostEventServiceItem();
                
                System.out.println("serviceItem= "+si);
                if(si.service!=null){
                	
                	synchronized(_lock){
                		_proxy=si.service;
                		_lock.notifyAll();
                	}
                }
            }
            public void serviceChanged(ServiceDiscoveryEvent event) {
                System.out.println("serviceChanged "+event);
            }
            public void serviceRemoved(ServiceDiscoveryEvent event) {
                System.out.println("serviceRemoved "+event);
                
            }
        };
        //Setup the SDM
        LookupDiscovery ldm=new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        ServiceDiscoveryManager sdm=new ServiceDiscoveryManager(ldm,  _lrm );
        LookupCache cache=sdm.createLookupCache(template,sif,sdl);
        
       synchronized(_lock){
            _lock.wait(waitTime);               
       }
       
		cache.terminate();
		sdm.terminate();
		ldm.terminate();
		if(_proxy==null){
			throw new InterruptedException("Service not found within wait time");
		}
		return _proxy;   
    }
}
