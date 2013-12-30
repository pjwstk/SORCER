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

package sorcer.test.context;

import java.rmi.RMISecurityManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ControlContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.dispatch.ExertDispatcher;
import sorcer.core.exertion.Jobs;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Jobber;
import sorcer.core.provider.Provider;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.util.Log;

/**
 * This is a tester for the Contexter.mapOutput method
 */
public class MapOutputTester {

	private static Logger log = Log.getTestLog(); // logger framework

	// set context node path (key), the key must be known (hashmap)
	private String outMessage = "message" + SorcerConstants.CPS
			+ SorcerConstants.IN_VALUE + SorcerConstants.CPS + "msg";

	private String inMessage = "message" + SorcerConstants.CPS
			+ SorcerConstants.OUT_VALUE + SorcerConstants.CPS + "msg";

	/**
	 * Main method for testing purpose only.
	 */
	public static void main(String[] args) {

		MapOutputTester client = new MapOutputTester();

		client.run(); // execute the job
	}

	/**
	 * The default constructor which sets the RMI Security Manager
	 */
	public MapOutputTester() {

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
	}

	/**
	 * This method shows how to use SORCER's S2S framework. The ServiceJob is
	 * executed on the Jobber provider, which basically delegates the tasks in
	 * the job to the appropreate providers.
	 * 
	 * @see Provider
	 * @see Jobber
	 * @see Exertion
	 * @see Job
	 */
	public void run() {
		ServiceExertion task1 = null;
		ServiceExertion task2 = null;
		ServiceExertion task3 = null;

		try {
			NetJob job = new NetJob("*** Job 1 ***"); // create
			// service
			// job

			// create the three tasks
			task1 = createTask("*** Task 1 ***");
			task2 = createTask("*** Task 2 ***");
			task3 = createTask("*** Task 3 ***");

			// add the three task into the job
			job.addExertion(task1);
			job.addExertion(task2);
			job.addExertion(task3);

			ControlContext cc = (ControlContext) job.getContext();

			// set the ServiceJob attributes
			cc.setAccessType(Access.PUSH); // use the
			// catalog/LUS
			// to delegate
			// the tasks
			cc.setFlowType(Flow.SEQ); // either parallel
			// or sequential
			job.setId(UUID.randomUUID().toString()); // set the exertion id
			cc.setExecTimeRequested(true); // time the job execution
			cc.setMonitorable(false); // job can be monitored
			cc.setWaitable(true);

			try {
				// clear input node of task2 and task3
				Contexts.putOutValue(task1.getContext(), outMessage,
						"*MapOutput Works!*");
				Contexts.putInValue(task2.getContext(), inMessage,
						Context.EMPTY_LEAF);
				Contexts.putInValue(task3.getContext(), inMessage,
						Context.EMPTY_LEAF);

				// mapoutput of task2's sc data node(inMessage) to task1's sc
				// data node (outMessage)
				Contexts.map(outMessage, task1.getContext(), inMessage, task2
						.getContext());

				// mapoutput of task3's sc data node(inMessage) to task2's sc
				// data node (outMessage)
				Contexts.map(inMessage, task2.getContext(), inMessage, task3
						.getContext());
			} catch (Exception e) {
				log.severe("Error in mapoutput" + e);
				e.printStackTrace();
			}

			log.info("*** Task2 CP after mapoutput: "
					+ task2.getContext().getMetaattributeValue(inMessage,
							Context.CONTEXT_PARAMETER));
			log.info("*** Task3 CP after mapoutput: "
					+ task3.getContext().getMetaattributeValue(inMessage,
							Context.CONTEXT_PARAMETER));

			// test the dispatcher
			new MapOutputTester.TestDispatcher(job, new HashSet(), false);

			log
					.info("********************** Printing Exertion **************************");

			task1 = (ServiceExertion) job.exertionAt(0);
			task2 = (ServiceExertion) job.exertionAt(1);
			task3 = (ServiceExertion) job.exertionAt(2);

			log.info("*** Task 1 *** mapoutput message: "
					+ task1.getContext().getValue(outMessage));
			log.info("*** Task 2 *** got the message: "
					+ task2.getContext().getValue(inMessage));
			log.info("*** Task 3 *** got the message: "
					+ task3.getContext().getValue(inMessage));
		} catch (Exception e) {
			e.printStackTrace();
		}

		catch (Throwable t) {

		}

	}

	/**
	 * This method creates a simple Exertion of type ServiceTask. An exertion is
	 * composed of a service method and a service context.
	 * 
	 * @param message
	 *            the string stored in the service data which will be printed by
	 *            the provider's service routing
	 * @return ServiceTask The type of exertion returned by the service routing
	 * @throws SignatureException 
	 * @see ServiceContext
	 * @see NetSignature
	 * @see ContextRequestor
	 */
	public ServiceExertion createTask(String message) throws SignatureException {
		// create the service data
		ServiceContext context = new ServiceContext("Message");

		// create the service method
		NetSignature method = new NetSignature("printMessage",
				sorcer.simple.simpleProvider.SimpleProvider.class,
				"Simple Provider");

		// create the exertion which is a servicetask composed of a service
		// method and service context
		NetTask task = new NetTask("printMessage", method);

		try {

			// insert the message into the service context
			Contexts.putInValue(context, outMessage, Context.EMPTY_LEAF);
			Contexts.putOutValue(context, inMessage, Context.EMPTY_LEAF);
		} catch (Exception e) {
			log.severe("createTask exception: " + e);
			e.printStackTrace();

		}

		// set the exertions service context
		task.setContext(context);

		// assign a unique exertion ID
		task.setId(UUID.randomUUID().toString()); // universal uid creation
		// across multiple VM

		return task;
	}

	/**
	 * a test dispatcher for testing the Contexter.mapOutput
	 */
	public class TestDispatcher extends ExertDispatcher {

		/**
		 * @param job
		 * @param sharedContexts
		 * @param isSpawned
		 * @throws Throwable
		 */
		public TestDispatcher(NetJob job, Set sharedContexts,
				boolean isSpawned) throws Throwable {
			super(job, sharedContexts, isSpawned, null);

			DispatchThread gthread = new DispatchThread();
			gthread.start();
			try {
				gthread.join();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				state = FAILED;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see sorcer.core.dispatch.ExertionDispatcher#generateExertions()
		 */
		@Override
		public void dispatchExertions() throws ExertionException, SignatureException {
			inputXrts = Jobs.getInputExertions((NetJob)xrt);
			reconcileInputExertions(xrt);
			collectResults();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see sorcer.core.dispatch.ExertionDispatcher#collectResults()
		 */
		@Override
		public void collectResults() throws ExertionException, SignatureException {

			ServiceExertion exertion = null;
			// RemoteExertion result = null;
			for (int i = 0; i < inputXrts.size(); i++) {
				exertion = (ServiceExertion) inputXrts.elementAt(i);
				if (isInterupted(exertion))
					return;

				if (exertion.getStatus() <= FAILED)// ||
					// exertion.getState()<=FAILED)
					xrt.setStatus(FAILED);
				else if (exertion.getStatus() == SUSPENDED
						|| ((ControlContext) xrt.getContext())
								.isReview(exertion))
					xrt.setStatus(SUSPENDED);
			}

			if (isInterupted(masterXrt))
				return;
			if (masterXrt != null) {
				exertion = execExertion((ServiceExertion) masterXrt);// executeMasterExertion();
				if (exertion.getStatus() <= FAILED) {
					state = FAILED;
					xrt.setStatus(FAILED);
				} else {
					state = DONE;
					xrt.setStatus(DONE);
				}
			} else
				state = DONE;

			dispatchers.remove(xrt.getId());
		}

		protected ServiceExertion execExertion(ServiceExertion ex)
				throws SignatureException,
				ExertionException {
			preExecExertion(ex);
			ServiceExertion result = null;
			try {
				if (((ServiceExertion) ex).isTask()) {
					result = ex;
				}

				else {
					result = ex;
				}
			} catch (Exception e) {
				System.out.println("\n*** Failed Exertion ***");
			}
			postExecExertion(ex, result);
			return result;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * sorcer.core.dispatch.ExertionDispatcher#preExecExertion(sorcer.core
		 * .exertion.RemoteExertion)
		 */
		@Override
		protected void preExecExertion(Exertion exertion)
				throws ExertionException,
				SignatureException {

			if (((ServiceExertion) exertion).isTask()) {
				updateInputs(exertion);
			}
			((ServiceExertion)exertion).startExecTime();
			((ServiceExertion) exertion).setStatus(RUNNING);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * sorcer.core.dispatch.ExertionDispatcher#postExecExertion(sorcer.core
		 * .exertion.RemoteExertion, sorcer.core.exertion.RemoteExertion)
		 */
		@Override
		protected void postExecExertion(Exertion ex, Exertion result)
				throws ExertionException,
				SignatureException {
			((ServiceExertion) result).stopExecTime();
			ServiceExertion re = (ServiceExertion) result;
			((NetJob)xrt).setExertionAt(result, ((ServiceExertion) ex).getIndex());
			if (re.getStatus() > FAILED && re.getStatus() != SUSPENDED) {
				re.setStatus(DONE);
				if (((ControlContext) xrt.getContext())
						.isNodeReferencePreserved())
					try {
						Jobs.preserveNodeReferences(ex, result);
					} catch (ContextException ce) {
						ce.printStackTrace();
						throw new ExertionException("ContextException caught: "
								+ ce.getMessage());
					}
				// update all outputs to shared context only for tasks.For jobs,
				// spawned dispatcher does it.
				if (((ServiceExertion) result).isTask()) {
					collectOutputs(result);
				}
				((ServiceExertion)xrt).stopExecTime();
				notifyExertionExecution(ex, result);
			}

		}

		protected class DispatchThread extends Thread {
			public void run() {
				try {
					dispatchExertions();
				} catch (ExertionException ee) {
					ee.printStackTrace();
				} catch (SignatureException eme) {
					eme.printStackTrace();
				}
			}
		}
	}

}
