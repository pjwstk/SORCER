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
package sorcer.core.provider.jobber;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.security.auth.Subject;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.id.UuidFactory;
import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ControlContext;
import sorcer.core.dispatch.JobThread;
import sorcer.core.exertion.NetJob;
import sorcer.core.provider.ControlFlowManager;
import sorcer.core.provider.Jobber;
import sorcer.core.provider.ProviderDelegate;
import sorcer.core.provider.ServiceProvider;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Executor;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

import com.sun.jini.start.LifeCycle;

/**
 * ServiceJobber - The SORCER rendezvous service provider that provides
 * coordination for executing exertions using directly (PUSH) service providers.
 * 
 */
public class ServiceJobber extends ServiceProvider implements Jobber, Executor, SorcerConstants {
	private Logger logger = Logger.getLogger(ServiceJobber.class.getName());

	public ServiceJobber() throws RemoteException {
		// do nothing
	}

	// require constructor for Jini 2 NonActivatableServiceDescriptor
	public ServiceJobber(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		initLogger();
	}
	
	private void initLogger() {
		Handler h = null;
		try {
			logger = Logger.getLogger("local." + ServiceJobber.class.getName() + "."
					+ getProviderName());
			h = new FileHandler(System.getProperty(IGRID_HOME)
					+ "/logs/remote/local-Jobber-" + delegate.getHostName() + "-" + getProviderName()
					+ "%g.log", 20000, 8, true);
			if (h != null) {
				h.setFormatter(new SimpleFormatter());
				logger.addHandler(h);
			}
			logger.setUseParentHandlers(false);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setServiceID(Exertion ex) {
		// By default it's ServiceJobber associated with servlet.
		// Not service.
		try {
			if (getProviderID() != null) {
				logger.finest(getProviderID().getLeastSignificantBits() + ":"
						+ getProviderID().getMostSignificantBits());
				((ServiceExertion) ex).setLsbId(getProviderID()
						.getLeastSignificantBits());
				((ServiceExertion) ex).setMsbId(getProviderID()
						.getMostSignificantBits());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public Exertion service(Exertion exertion) throws RemoteException, ExertionException {
		logger.entering(this.getClass().getName(), "service: " + exertion.getName());
		try {
			// Jobber overrides SorcerProvider.service method here
			setServiceID(exertion);
			// Create an instance of the ExertionProcessor and call on the
			// process method, returns an Exertion
			return new ControlFlowManager(exertion, delegate, this).process(threadManager);

		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new ExertionException();
		}
	}

	public Exertion execute(Exertion exertion) throws RemoteException,
			TransactionException, ExertionException {
		return execute(exertion, null);
	}
	
	public Exertion execute(Exertion exertion, Transaction txn)
			throws TransactionException, ExertionException, RemoteException {
		//logger.info("*********************************************ServiceJobber.exert(), exertion = " + exertion);
		Exertion ex = doJob(exertion, txn);
		//logger.info("*********************************************ServiceJobber.exert(), ex = " + ex);

		return ex;
	}

	public Exertion doJob(Exertion job) {
		return doJob(job, null);
	}
	
	public Exertion doJob(Exertion job, Transaction txn) {
		//logger.info("*********************************************ServiceJobber.doJob(), job = " + job);
		setServiceID(job);
		try {
			if (((ServiceExertion)job).getControlContext().isMonitorable()
					&& !(((NetJob)job).getControlContext()).isWaitable()) {
				replaceNullExertionIDs(job);
				notifyViaEmail(job);
				new JobThread((Job) job, this).start();
				return job;
			} else {
				JobThread jobThread = new JobThread((Job) job, this);
				jobThread.start();
				jobThread.join();
				Job result = jobThread.getResult();
				logger.finest("<==== Result: " + result);
				return result;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	// public Exertion stopJob(String , Subject subject)
	// throws RemoteException, ExertionException, ExertionMethodException {
	// RemoteServiceJob job = getJob(jobID, subject);
	// //If job has serviceID then call stop on the provider with serviceID
	// if (job.getServiceID()!=null &&
	// !job.getServiceID().equals(getProviderID())) {
	// Provider provider =
	// ServiceProviderAccessor.getProvider(job.getServiceID());
	// if (provider == null)
	// throw new ExertionException("Jobber with serviceID ="+job.getServiceID()
	// +" Jobber Name ="+job.getJobberName()+" down!");
	// else
	// return provider.stopJob(jobID, subject);
	// }

	// //else assume the Jobber called on is current one.
	// ExertionDispatcher dispatcher = getDispatcher(jobID);
	// if (dispatcher == null) {
	// throw new ExertionException("No job with id "+jobID+" found in Jobber ");
	// //RemoteServiceJob job = getPersistedJob(jobID ,subject);
	// //return job;
	// //return cleanIfCorrupted(job);
	// }
	// else {
	// if (isAuthorized(subject,"STOPSERVICE",jobID)) {
	// if (job.getStatus()!=RUNNING || job.getState()!=RUNNING)
	// throw new ExertionException("Job with id="+jobID+" is not Running!");
	// return dispatcher.stopJob();
	// }
	// else
	// throw new ExertionException("Access Denied to step Job id ="+jobID+"
	// subject="+subject);
	// }
	// }

	// public Exertion suspendJob(String jobID,Subject subject)
	// throws RemoteException, ExertionException, ExertionMethodException {

	// ExertionDispatcher dispatcher = getDispatcher(jobID);
	// if (dispatcher == null) {
	// throw new ExertionException("No job with id "+jobID+" found in Jobber ");
	// //RemoteServiceJob job = getPersistedJob(jobID ,subject);
	// //return job;
	// //return cleanIfCorrupted(job);
	// }
	// else {
	// if (isAuthorized(subject,"SUSPENDJOB",jobID))
	// return dispatcher.suspendJob();
	// else
	// throw new ExertionException("Access Denied to step Job id ="+jobID+"
	// subject="+subject);
	// }
	// }

	// public Exertion resumeJob(String jobID,Subject subject)
	// throws RemoteException, ExertionException, ExertionMethodException {
	// RemoteServiceJob job = null;
	// if (isAuthorized(subject,"RESUMEJOB",jobID)) {
	// job = getJob(jobID, subject);
	// if (job.getStatus()==RUNNING || job.getState()==RUNNING)
	// throw new ExertionException("Job with id="+jobID+" already Running!");
	// prepareToResume(job);
	// return doJob(job);
	// }
	// els
	// throw new ExertionException("Access Denied to step Job id ="+jobID+"
	// subject="+subject);
	// }

	// public Exertion stepJob(String jobID,Subject subject)
	// throws RemoteException, ExertionException, ExertionMethodException {
	// RemoteServiceJob job = null;
	// if (isAuthorized(subject,"STEPJOB",jobID)) {
	// job = getJob(jobID, subject);
	// if (job.getStatus()==RUNNING || job.getState()==RUNNING)
	// throw new ExertionException("Job with id="+jobID+" already Running!");
	// prepareToStep(job);
	// return doJob(job);
	// }
	// else
	// throw new ExertionException("Access Denied to step Job id ="+jobID+"
	// subject="+subject);
	// }

	private String getDataURL(String filename) {
		return ((ProviderDelegate) getDelegate()).getProviderConfig().getProperty(
				"provider.dataURL")
				+ filename;
	}

	private String getDataFilename(String filename) {
		return ((ProviderDelegate) getDelegate()).getProviderConfig().getDataDir() + "/"
				+ filename;
	}

	/** {@inheritDoc} */
	public boolean isAuthorized(Subject subject, Signature signature) {
		return true;
	}


	private void replaceNullExertionIDs(Exertion ex) {
		if (ex != null && ((ServiceExertion) ex).getId() == null) {
			((ServiceExertion) ex)
					.setId(UuidFactory.generate());
			if (((ServiceExertion) ex).isJob()) {
				for (int i = 0; i < ((Job) ex).size(); i++)
					replaceNullExertionIDs(((Job) ex).get(i));
			}
		}
	}

	private void notifyViaEmail(Exertion ex) throws ContextException {
		if (ex == null || ((ServiceExertion) ex).isTask())
			return;
		Job job = (Job) ex;
		Vector recipents = null;
		String notifyees = ((ControlContext) ((NetJob)job).getControlContext())
				.getNotifyList();
		if (notifyees != null) {
			String[] list = SorcerUtil.tokenize(notifyees, MAIL_SEP);
			recipents = new Vector(list.length);
			for (int i = 0; i < list.length; i++)
				recipents.addElement(list[i]);
		}
		String to = "", admin = Sorcer.getProperty("sorcer.admin");
		if (recipents == null) {
			if (admin != null) {
				recipents = new Vector();
				recipents.addElement(admin);
			}
		} else if (admin != null && !recipents.contains(admin))
			recipents.addElement(admin);

		if (recipents == null)
			to = to + "No e-mail notifications will be sent for this job.";
		else {
			to = to + "e-mail notification will be sent to\n";
			for (int i = 0; i < recipents.size(); i++)
				to = to + "  " + recipents.elementAt(i) + "\n";
		}
		String comment = "Your job '" + job.getName()
				+ "' has been submitted.\n" + to;
		((ControlContext) ((NetJob)job).getControlContext()).setFeedback(comment);
		if (job.getMasterExertion() != null
				&& ((ServiceExertion) job.getMasterExertion()).isTask()) {
			((ServiceExertion) (job.getMasterExertion())).getContext()
					.putValue(Context.JOB_COMMENTS, comment);

			Contexts.markOut(((ServiceExertion) (job.getMasterExertion()))
					.getContext(), Context.JOB_COMMENTS);

		}
	}

	private void prepareToResume(Job job) {

	}

	private void prepareToStep(Job job) {
		Exertion e = null;
		for (int i = 0; i < job.size(); i++) {
			e = job.get(i);
			(((NetJob)job).getControlContext()).setReview(e, true);
			if (((ServiceExertion) e).isJob())
				prepareToStep((Job) e);
		}
	}

}
