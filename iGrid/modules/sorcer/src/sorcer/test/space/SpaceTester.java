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

package sorcer.test.space;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.UUID;
import java.util.logging.Logger;

import net.jini.core.entry.Entry;
import net.jini.space.JavaSpace;
import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ControlContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ExertionEnvelop;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;


/**
 * This example demonstrates how to communicate to any SORCER provider using 
 * an Exertion Space (JavaSpaces). 
 * 
 * The exertion is wrap by an ExertionEnvelop which implements Entry.
 * 
 * @author Michael Alger
 * @version 1.0
 */
public class SpaceTester {
	
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

        SpaceTester client = new SpaceTester();
		
		client.serviceJobExecutionSpace(); //execute the composite exertion via the space
	}
	
	
	/**
	 * The default constructor which sets the RMI Security Manager
	 */
	public SpaceTester() {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}
	
	
	/**
	 * This method executes the exertion using the Exertion space (Javaspaces).
	 * 
	 * @see JavaSpace
	 * @see Exertion
	 * @see Entry
	 * @see ExertionEnvelop
	 */
	public void serviceJobExecutionSpace() {
        Exertion exertion = null;
        ExertionEnvelop envelop = null;
        
		try {
			JavaSpace space = ProviderAccessor.getSpace(); //retrieve the JavaSpace service proxy
			
			if (space == null) {
				log.severe("Cannot find javaspaces...");
                System.exit(-1);
			}
			
			log.info("Writing 10 Jobs and 10 Tasks into the Space with 60 secs lease time...");
            log.info("check JavaSpace Sevice Browser ...");
            
            for (int i = 0; i < 10; i++) { //drop 10 jobs and 10 tasks into Space
                exertion = createSpaceJob("*** Job" + i + " ***"); //creates a job 
                envelop = ExertionEnvelop.getTemplate(exertion); //wraps the exertion for space usage  
                space.write(envelop, null, 60000); //write the envelop into the space with lease of 1 min
              
                exertion = createTask("*** Task" + i + " ***"); //creates the task
                envelop = ExertionEnvelop.getTemplate(exertion); //wraps the exertion for space usage
                space.write(envelop, null, 60000); //write the envelop into the space with lease of 1 min
            }
            
            /*
			log.info("Sleeping for 15 secs, check space browser...\n");
			Thread.currentThread().sleep(10000); //wait for 15sec before taking the result
			
			//returns a take template from the given exertion, state = DONE (4)
			ExertionEnvelop template = ExertionEnvelop.getTakeTemplate(exertion); 
			//template.providerName = null;
			
		    log.info("ExertionEnvelop - Template for take");
		    printExertionEnvelop(template);
		    
		    log.info("Take ExertionEnvelop from space...\n");
		    ExertionEnvelop resultEnvelop = (ExertionEnvelop)space.take(template, null, Lease.FOREVER);
		    
		    log.info("Result ExertionEnvelop");
		    printExertionEnvelop(resultEnvelop); //print the result envelop attributes
		    
		    printExertions(resultEnvelop.exertion);	//prints the Exertion in the envelop
            */
		}
		catch (Exception e) {
			log.severe("Space execution error: " + e);
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This method creates a composite Job.
	 * @return ServiceJob the composite servicejob
	 */
	public Job createSpaceJob(String message) {
		
		//create the ServiceJobs
		Job Job1 = createJob(message); //root job
		
		//create the ServiceTasks
		ServiceExertion task1 = createTask("*** Task 1 ***");
		ServiceExertion task2 = createTask("*** Task 2 ***");
		ServiceExertion task3 = createTask("*** Task 3 ***");
		
		//add the exertion (task/jobs) into the main job
		Job1.addExertion(task1);
		Job1.addExertion(task2);
        Job1.addExertion(task3);
		
		return Job1;
	}
	
	
	/**
	 * Method which creates a single job with the set attributes
	 * @param message the name of the ServiceJob
	 * @return ServiceJob the composite servicejob
	 */
	public Job createJob(String message) {
		
		NetJob job = new NetJob(message);
		ControlContext cc = (ControlContext)job.getContext();
		//set the ServiceJob attributes
		cc.setAccessType(ControlContext.PULL); //use the catalog to delegate the tasks
		cc.setFlowType(ControlContext.SEQUENTIAL); //either parallel or sequential
		job.setId(UUID.randomUUID().toString()); //set the exertion id
		cc.setExecTimeRequested(true); //time the job execution
		cc.isMonitorEnabled(false); //job can be monitored
		cc.setWaitable(true);
		
		return job;
	}
	
	
	/**
	 * This method creates a simple Exertion of type ServiceTask. A ServiceTask is
	 * composed of a service method and a service context.
	 * 
	 * @param message the string stored in the service data which will be printed by the provider's service routing
	 * @return ServiceTask The type of exertion returned by the service routing
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
		NetTask task = new NetTask(taskName, "ServiceTask for Simple Provider", method);
		
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
		task.setContext(context);
		
		//assign a unique exertion ID
		task.setId(UUID.randomUUID().toString()); //universal uid creation across multiple VM
		
		return task;
	}
	
	
	/**
	 * Traverses the Job/Task hierarchy and prints the designated key
	 * @param exertion Either a ServiceTask or a ServiceJob
	 */
	public void printExertions(Exertion exertion) {
		
		if (exertion instanceof ServiceExertion) {
			ServiceExertion Task = (ServiceExertion)exertion;
			String greetings = null;
			String providerHost = null;
			
			String requestorMessage = null;
			try {
				requestorMessage = (String)Task.getContext().getValue(inMessagePath);
				providerHost = (String)Task.getContext().getValue(outHostPath);
				greetings = (String)Task.getContext().getValue(outGreetingPath);
			} catch (ContextException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
