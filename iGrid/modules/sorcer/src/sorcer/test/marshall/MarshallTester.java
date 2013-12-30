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

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.UUID;
import java.util.logging.Logger;

import jgapp.persist.ObjectLogger;
import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ExertionEnvelop;
import sorcer.core.exertion.ServiceExertion;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.util.Log;


/**
 * This example demonstrates the marshalling of objects to a file 
 */
public class MarshallTester {
	
	private static Logger log = Log.getTestLog(); //logger framework
	
	// set context node path (key), the key must be known (hashmap)
	private String inMessagePath = "message" + SorcerConstants.CPS + SorcerConstants.IN_VALUE;
	private String inHostPath = "message" + SorcerConstants.CPS + SorcerConstants.IN_VALUE + SorcerConstants.CPS + "host";
	private String outHostPath = "message" + SorcerConstants.CPS + SorcerConstants.OUT_VALUE + SorcerConstants.CPS + "host";
	private String outGreetingPath = "message" + SorcerConstants.CPS + SorcerConstants.OUT_VALUE + SorcerConstants.CPS + "greeting";
	
	/**
	 * Main method for testing purpose only.
	 */
	public static void main(String[] args) {

        MarshallTester tester = new MarshallTester();
		
		tester.run(); //execute the composite exertion via the space
	}
	
	
	/**
	 * The default constructor which sets the RMI Security Manager
	 */
	public MarshallTester() {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	
	/**
	 * This method will marshall the objects into the logs directory
	 * 
	 * @see Exertion
	 * @see ExertionEnvelop
	 */
	public void run() {
        
        Exertion exertionJob = createJob("*** Job1 ***");
        Exertion exertionTask = createTask("*** Task1 ***");
        
        ExertionEnvelop envelopJob = ExertionEnvelop.getTemplate(exertionJob);
        ExertionEnvelop envelopTask = ExertionEnvelop.getTemplate(exertionTask);
        
		try {
            log.info("Marshalling ServiceJob to file: ../logs/serializedJob.dat");
			ObjectLogger.persistMarshalled("../logs/serializedJob.dat", exertionJob);
            
            log.info("Marshalling ServiceTask to file: ../logs/serializedTask.dat");
            ObjectLogger.persistMarshalled("../logs/serializedTask.dat", exertionTask);
            
            log.info("Marshalling ExertionEnvelop Job to file: ../logs/serializedEEJob.dat");
            ObjectLogger.persistMarshalled("../logs/serializedEEJob.dat", envelopJob);
            
            log.info("Marshalling ExertionEnvelop Task to file: ../logs/serializedEETask.dat");
            ObjectLogger.persistMarshalled("../logs/serializedEETask.dat", envelopTask);  
		}
		
		catch (Exception e) {
			log.severe("Unable to marshall the Exertions " + e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method creates a composite Job.
	 * @return ServiceJob the composite servicejob
	 */
	public Job createJob(String message) {
		
		//create the ServiceJobs
		Job Job1 = new Job(message); //root job
		Job Job2 = new Job("*** Job 2 ***");
		Job Job3 = new Job("*** Job 3 ***");
		
		//create the ServiceTasks
		ServiceExertion task1 = createTask("*** Task 1 ***");
		ServiceExertion task2 = createTask("*** Task 2 ***");
		ServiceExertion task3 = createTask("*** Task 3 ***");
		ServiceExertion task4 = createTask("*** Task 4 ***");
		ServiceExertion task5 = createTask("*** Task 5 ***");
		ServiceExertion task6 = createTask("*** Task 6 ***");
		
		//add the exertion (task/jobs) into the main job
		Job1.addExertion(task1);
		Job1.addExertion(task2);
		Job1.addExertion(Job2);
		
		Job2.addExertion(task3);
		Job2.addExertion(task4);
		Job2.addExertion(Job3);
		
		Job3.addExertion(task5);
		Job3.addExertion(task6);
		
		return Job1;
	}
		
	/**
	 * This method creates a simple Exertion of type ServiceTask. A ServiceTask is
	 * composed of a ServiceMethod and a ServiceContext.
	 * 
	 * @param taskname The name of the ServiceTask
	 * @return ServiceTask The elementary exertion
	 * 
	 * @see ServiceContext
	 * @see NetSignature
	 * @see ContextRequestor
	 */
	public ServiceExertion createTask(String taskName) {
		//create the service data
		ServiceContext context = new ServiceContext("Message");
		
		//create the service method
		NetSignature method = new NetSignature("printMessage", "sorcer.simple.simpleProvider.SimpleProvider", "Simple Provider");
		
		//create the exertion which is a servicetask composed of a service method and service context
		ServiceExertion task = new ServiceExertion(taskName, "ServiceTask for Simple Provider" , new NetSignature[] { method });
		
		try {
						
			//insert the message into the service context
			Contexts.putInValue(context, inMessagePath, taskName);
			Contexts.putInValue(context, inHostPath, InetAddress.getLocalHost().getHostName());
			
			//The simpleProvider will write the result to this path
			Contexts.putOutValue(context, outHostPath, Context.EMPTY_LEAF);
			Contexts.putOutValue(context, outGreetingPath, Context.EMPTY_LEAF);
		}
		catch (Exception e) {
			log.severe("createTask exception: " + e);
			e.printStackTrace();
			
		}
		
		//set the exertions service context
		task.setConditionalContext(context);
		
		//assign a unique exertion ID
		task.setId(UUID.randomUUID().toString()); //universal uid creation across multiple VM
		
		return task;
	}
	
	
	/**
	 * Traverses the Job/Task hierarchy and prints the designated key in the ServiceContext
	 * @param exertion Either a ServiceTask or a ServiceJob
	 */
	public void printExertions(Exertion exertion) {
		
		if (exertion instanceof ServiceExertion) {
			ServiceExertion Task = (ServiceExertion)exertion;
			
			String requestorMessage = (String)Task.getContext().get(inMessagePath);
			String providerHost = (String)Task.getContext().get(outHostPath);
			String greetings = (String)Task.getContext().get(outGreetingPath);
			
			log.info(greetings + " " + providerHost + " got the message \"" + requestorMessage +"\"");
		}
		
		else if (exertion instanceof Job) {
			Job Job = (Job)exertion;
			
			for (int i = 0; i < Job.size(); i++) {
				printExertions(Job.exertionAt(i));
			}
		}
	}
	
	
	/**
	 * Simply printes the attributes of the ExertionEnvelop (Entry)
	 * @param ee ExertionEnvelop that will be describe or pritnted out
	 */
	public static void printExertionEnvelop(ExertionEnvelop ee) {
        synchronized (System.out) {
        	System.out.println("************************************************");
        	System.out.println("* ServiceType: " + ee.serviceType);
        	System.out.println("* ProviderName: " + ee.providerName);
        	System.out.println("* ExertionID: " + ee.exertionID);
        	System.out.println("* ParentID: " + ee.parentID);
        	System.out.println("* isJob: " + ee.isJob);
        	System.out.println("* State: " + ee.state);
        	System.out.println("************************************************\n\n");
        }
	}
}
