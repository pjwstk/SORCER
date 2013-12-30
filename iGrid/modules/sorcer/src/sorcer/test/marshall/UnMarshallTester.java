/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

package sorcer.test.marshall;

import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import jgapp.persist.ObjectLogger;
import sorcer.core.exertion.ExertionEnvelop;
import sorcer.core.exertion.ServiceExertion;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.util.Log;


/**
 * This example demonstrates how to unmarshall the objects from a file
 * 
 * This example is good to determine the necessary classes to be included in your download jar file (codebase)
 * in order to successfully unmarshall the object(s) on the recieving end. 
 */
public class UnMarshallTester {
	
	private static Logger log = Log.getTestLog(); //logger framework
	
   /**
	 * Main method for testing purpose only.
	 */
	public static void main(String[] args) {

        UnMarshallTester client = new UnMarshallTester();
		
		client.run(); //execute the composite exertion via the space
	}
	
	
	/**
	 * The default constructor which sets the RMI Security Manager
	 */
	public UnMarshallTester() {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	
	/**
	 * This method executes the exertion using the Exertion space (Javaspaces).
	 */
	public void run() {
        
        Exertion exertion1 = null;
        Exertion exertion2 = null;
        ExertionEnvelop envelopJob = null;
        ExertionEnvelop envelopTask = null;
        
		try {
            //unmarshall the objects from the file
            exertion1 = (Exertion) ObjectLogger.restoreMarshalled("../logs/serializedJob.dat");
			exertion2 = (Exertion) ObjectLogger.restoreMarshalled("../logs/serializedTask.dat");
			envelopJob = (ExertionEnvelop) ObjectLogger.restoreMarshalled("../logs/serializedEEJob.dat");
            envelopTask = (ExertionEnvelop) ObjectLogger.restoreMarshalled("../logs/serializedEETask.dat");
            
            log.info("**** Exertion1 - ServiceJob ****");
            log.info("ExertionName: " + exertion1.getName());
            log.info("ProviderName: " + exertion1.getProviderName());
            log.info("ExertionID: " + exertion1.getId());
            
            log.info("**** Exertion2 - ServiceTask ****");
            log.info("ExertionName: " + exertion2.getName());
            log.info("ProviderName: " + exertion2.getProviderName());
            log.info("ExertionID: " + exertion2.getId());
            
            log.info("**** ExertionEnvelop - Job ****");
            log.info("EnvelopServiceType: " + envelopJob.serviceType);
            log.info("EnvelopID: " + envelopJob.exertionID);
            log.info("isJob: " + envelopJob.isJob);
            
            log.info("**** ExertionEnvelop - Task ****");
            log.info("EnvelopServiceType: " + envelopTask.serviceType);
            log.info("EnvelopID: " + envelopTask.exertionID);
            log.info("isTask: " + envelopTask.isJob);
           
            //test the objects
            Job job = (Job) exertion1;
            ServiceExertion task = (ServiceExertion) exertion2;
            NetSignature method = (NetSignature) task.getProcessSignature();
            Context context = (Context) task.sc();
            task = (ServiceExertion) job.exertionAt(0);
            ServiceExertion task1 = (ServiceExertion) ( (Job) envelopJob.exertion).exertionAt(0);
            task1.sc();
		}
		
		catch (Exception e) {
			log.severe("Unable to unmarshall the objects... " + e);
			e.printStackTrace();
		}
	}
}
