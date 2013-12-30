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

package sorcer.core.dispatch;

import java.rmi.RemoteException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.ServiceFactory;

import sorcer.core.Dispatcher;
import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.dispatch.ExertDispatcher.DispatchThread;
import sorcer.core.exertion.Jobs;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.Provider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;

abstract public class SWIFExertDispatcher extends ExertDispatcher
		implements SorcerConstants {

	public SWIFExertDispatcher(Job job, 
            Set<Context> sharedContext,
            boolean isSpawned, 
            Provider provider,
            ProvisionManager provisionManager) {
		super(job, sharedContext, isSpawned, provider, provisionManager);
		DispatchThread gthread = new DispatchThread();
		gthread.start();
		try {
			gthread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			state = FAILED;
		}
	}

	protected void preExecExertion(Exertion exertion) throws ExertionException,
	SignatureException {
		// If Job, new dispatcher will update inputs for it's Exertion
		logger.info("__________UPDATING INPUTS_______________");
		// in catalog dispatchers, if it is job, then new dispatcher is spawned
		// and the
		// shared contexts are passed.So the new dispatcher will update inputs
		// of tasks inside
		// the jobExertion But in space, all inputs to a new job are to be
		// updated before
		// dropping.
		try {
			if (((ServiceExertion) exertion).isTask()) {
				updateInputs(exertion);
			}
		} catch (Exception e) {
			throw new ExertionException(e);
		}
		((ServiceExertion) exertion).startExecTime();
		((ServiceExertion) exertion).setStatus(RUNNING);
	}

	// Parallel
	protected ExertionThread runExertion(ServiceExertion ex) {
		ExertionThread eThread = new ExertionThread(ex, this);
		eThread.start();
		return eThread;
	}

	// Sequential
	protected ServiceExertion execExertion(Exertion ex)
			throws SignatureException, ExertionException {
		ServiceExertion exi = (ServiceExertion) ex;
		// set subject before task goes out.
		exi.setSubject(subject);
		preExecExertion(ex);
		Exertion result = null;
		try {
			result = (exi.isTask()) ? ((Exertion) execTask((NetTask) ex))
					: ((ServiceExertion) execJob((NetJob) ex));
		} catch (Exception e) {
			exi.reportException(e);
			result = (Exertion) exi;
			((ServiceExertion) result).setStatus(FAILED);
		}
		// set subject after result is recieved.
		((ServiceExertion) result).setSubject(subject);
		postExecExertion((Exertion) exi, result);
		return (ServiceExertion) result;
	}

	protected void postExecExertion(Exertion ex, Exertion result)
			throws SignatureException, ExertionException {
		((ServiceExertion) result).stopExecTime();
		ServiceExertion ser = (ServiceExertion) result;
		((NetJob)xrt).setExertionAt(result, ((ServiceExertion) ex).getIndex());
		if (ser.getStatus() > FAILED && ser.getStatus() != SUSPENDED) {
			ser.setStatus(DONE);
			// update all outputs to sharedcontext only for tasks.For jobs,
			// spawned dispatcher does it.
			try {
				if (((ServiceExertion) result).isTask()) {
					collectOutputs(result);
				}	
				notifyExertionExecution(ex, result);
			} catch (ContextException e) {
				throw new ExertionException(e);
			}
		}
	}

	// Made private so that other classes just calls execExertion and not
	// execTask
	private NetTask execTask(NetTask task) throws ExertionException,
			SignatureException {

		String QNameService = null;
		String QNamePort = null;

		String BODY_NAMESPACE_VALUE = null;
		String ENCODING_STYLE_PROPERTY = null;
		String NS_XSD = null;
		String URI_ENCODING = null;
		String TARGET_ENDPOINT_ADDRESS = null;
		String IN_TYPE = null;
		String RETURN_TYPE = null;
		String METHOD_NAME = null;
		String INPUT_PARAM = null;

		try {
			Context ctx = task.getContext();
			QNameService = (String) ctx.getValue("QNameService");
			QNamePort = (String) ctx.getValue("QNamePort");

			BODY_NAMESPACE_VALUE = (String) ctx
					.getValue("BODY_NAMESPACE_VALUE");
			ENCODING_STYLE_PROPERTY = (String) ctx
					.getValue("ENCODING_STYLE_PROPERTY");
			NS_XSD = (String) ctx.getValue("NS_XSD");
			URI_ENCODING = (String) ctx.getValue("URI_ENCODING");
			TARGET_ENDPOINT_ADDRESS = (String) ctx
					.getValue("TARGET_ENDPOINT_ADDRESS");
			IN_TYPE = (String) ctx.getValue("IN_TYPE");
			RETURN_TYPE = (String) ctx.getValue("RETURN_TYPE");
			METHOD_NAME = (String) ctx.getValue("METHOD_NAME");
			INPUT_PARAM = (String) ctx.getValue("INPUT_PARAM");
			try {
				task.getControlContext().appendTrace(provider.getProviderName() 
						+ " dispatcher: " + getClass().getName());
			} catch (RemoteException e) {
				// ignore it, local call
			}
			QName name = new QName(QNameService);

			ServiceFactory factory = ServiceFactory.newInstance();
			Service service = factory.createService(new QName(QNameService));

			QName port = new QName(QNamePort);

			Call rpcCall = service.createCall(port);

			rpcCall.setTargetEndpointAddress(TARGET_ENDPOINT_ADDRESS);

			rpcCall
					.setProperty(Call.SOAPACTION_USE_PROPERTY,
							new Boolean(true));
			rpcCall.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
			rpcCall.setProperty(ENCODING_STYLE_PROPERTY, URI_ENCODING);
			rpcCall.setProperty(Call.OPERATION_STYLE_PROPERTY, "rpc");

			QName QNAME_RETURN_TYPE = new QName(NS_XSD, RETURN_TYPE);
			rpcCall.setReturnType(QNAME_RETURN_TYPE);

			rpcCall.setOperationName(new QName(BODY_NAMESPACE_VALUE,
					METHOD_NAME));

			QName QNAME_IN_TYPE = new QName(NS_XSD, IN_TYPE);
			rpcCall.addParameter("arg0", QNAME_IN_TYPE, ParameterMode.IN);

			String[] params = { INPUT_PARAM };

			String result = (String) rpcCall.invoke(params);

			Contexts.putOutValue(ctx, OUT_VALUE + 0, result);

		} catch (ServiceException se) {
			System.err.println("Service Exception");
		} catch (ContextException ce) {
			System.err.println("Context Exception");
		} catch (RemoteException re) {
			re.printStackTrace();
			System.err.println("dispatcher execution failed for task: " + task);
			throw new ExertionException("Remote Exception while executing task");
		} catch (Exception tme) { // (ExertionMethodException tme) {
			tme.printStackTrace();
			System.err.println("dispatcher execution failed for method: "
					+ task.getProcessSignature());
		}

		return task;
	}

	// Made private so that other classes just calls execExertion and not
	// execJob
	private NetJob execJob(NetJob serviceJob)
			throws DispatcherException, InterruptedException {
		Dispatcher dispatcher = null;
		runningExertionIDs.addElement(serviceJob.getId());
		try {
			dispatcher = ExertDispatcherFactory.getFactory()
					.createDispatcher(serviceJob, sharedContexts, true, provider);
			// Util.debug(this, "execJob:got dispatcher=" + dispatcher,
			// "dispatch");

			// wait until serviceJob is done by dispatcher
			while (dispatcher.getState() != DONE
					&& dispatcher.getState() != FAILED) {
				Thread.sleep(250);
			}
			return (NetJob) dispatcher.getExertion();
		} catch (DispatcherException de) {
			de.printStackTrace();
			throw de;
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			throw ie;
		}
	}

	private NetTask execScript(NetTask task) throws SignatureException {
		return null;
	}

	protected class ExertionThread extends Thread {
		private ServiceExertion ex;

		private ServiceExertion result;

		private ExertDispatcher dispatcher;

		public ExertionThread(ServiceExertion exertion,
				ExertDispatcher dispatcher) {
			ex = exertion;
			this.dispatcher = dispatcher;
			if (isMonitored)
				dispatchers.put(xrt.getId(), dispatcher);
		}

		public void run() {
			try {
				result = execExertion(ex);
			} catch (ExertionException ee) {
				ee.printStackTrace();
				result = ex;
				((ServiceExertion) result).setStatus(FAILED);
			} catch (SignatureException eme) {
				eme.printStackTrace();
				result = ex;
				((ServiceExertion) result).setStatus(FAILED);
			}
		}

		public ServiceExertion getExertion() {
			return ex;
		}

		public ServiceExertion getResult() {
			return result;
		}

	}

}
