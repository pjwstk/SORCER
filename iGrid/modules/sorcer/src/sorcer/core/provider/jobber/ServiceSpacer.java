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
import java.util.HashSet;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.security.auth.Subject;

import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.id.UuidFactory;
import sorcer.core.Dispatcher;
import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.ControlContext;
import sorcer.core.dispatch.DispatcherException;
import sorcer.core.dispatch.ExertDispatcherFactory;
import sorcer.core.dispatch.SpaceTaskDispatcher;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.loki.member.LokiMemberUtil;
import sorcer.core.provider.ControlFlowManager;
import sorcer.core.provider.Provider;
import sorcer.core.provider.ProviderDelegate;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.provider.Spacer;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exec;
import sorcer.service.Executor;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

import com.sun.jini.start.LifeCycle;

/**
 * ServiceSpacer - The SORCER rendezvous service provider that provides
 * coordination for executing exertions using JavaSpace from which provides PULL
 * exertions to be executed.
 * 
 */
public class ServiceSpacer extends ServiceProvider implements Spacer, Executor, SorcerConstants {
	private static Logger logger;
	private LokiMemberUtil myMemberUtil;

	/**
	 * ServiceSpacer - Default constructor
	 * 
	 * @throws RemoteException
	 */
	public ServiceSpacer() throws RemoteException {
		myMemberUtil = new LokiMemberUtil(ServiceSpacer.class.getName());
	}

	/**
	 * ServiceSpacer - Constructor
	 * 
	 * @param args
	 * @param lifeCycle
	 * @throws RemoteException
	 * 
	 *             Require ctor for Jini 2 NonActivatableServiceDescriptor
	 */
	public ServiceSpacer(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		myMemberUtil = new LokiMemberUtil(ServiceSpacer.class.getName());
		initLogger();
	}

	private void initLogger() {
		Handler h = null;
		try {
			logger = Logger.getLogger("local." + ServiceSpacer.class.getName()
					+ "." + getProviderName());
			h = new FileHandler(System.getProperty(IGRID_HOME)
					+ "/logs/remote/local-Spacer-" + delegate.getHostName()
					+ "-" + getProviderName() + "%g.log", 20000, 8, true);
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
		// By default it's ServiceSpacer associated with this exertion.
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

	public Exertion service(Exertion exertion) throws RemoteException,
			ExertionException {
		try {
			logger.entering(this.getClass().getName(), "service: " + exertion.getName());
			setServiceID(exertion);
			System.out.println("ServiceSpacer.service(): ************************************ exertion = " + exertion);
			// create an instance of the ExertionProcessor and call on the
			// process method, returns an Exertion
			return new ControlFlowManager(exertion, delegate, this)
					.process(threadManager);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExertionException();
		}
	}

	public Exertion execute(Exertion exertion) throws TransactionException,
			RemoteException {
		return execute(exertion, null);
	}

	public Exertion execute(Exertion exertion, Transaction txn)
			throws TransactionException, RemoteException {
		if (exertion.isJob())
			return doJob(exertion);
		else
			return doTask(exertion);
	}

	public Exertion doJob(Exertion job) {
		setServiceID(job);
		try {
			if (job.getControlContext().isMonitorable()
					&& !job.getControlContext().isWaitable()) {
				replaceNullExertionIDs(job);
				notifyViaEmail(job);
				new JobThread((Job) job, this).start();
				return job;
			} else {
				JobThread jobThread = new JobThread((Job) job, this);
				jobThread.start();
				jobThread.join();
				Job result = jobThread.getResult();
				logger.finest("Result: " + result);
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
		return ((ProviderDelegate) getDelegate()).getProviderConfig()
				.getProperty("provider.dataURL") + filename;
	}

	private String getDataFilename(String filename) {
		return ((ProviderDelegate) getDelegate()).getProviderConfig()
				.getDataDir() + "/" + filename;
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
		String notifyees = ((ControlContext) job.getContext()).getNotifyList();
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
		((ControlContext) job.getContext()).setFeedback(comment);
		if (job.getMasterExertion() != null
				&& ((ServiceExertion) job.getMasterExertion()).isTask()) {
			((ServiceExertion) (job.getMasterExertion())).getContext()
					.putValue(Context.JOB_COMMENTS, comment);

			Contexts.markOut(
					((ServiceExertion) (job.getMasterExertion())).getContext(),
					Context.JOB_COMMENTS);

		}
	}

	protected class JobThread extends Thread {

		// doJob method calls this internally
		private Job job;

		private Job result;

		private String jobID;

		private Provider provider;
		
		public JobThread(Job job, Provider provider) {
			this.job = job;
			this.provider = provider;
		}

		public void run() {
			logger.finest("*** JobThread Started ***");
			Dispatcher dispatcher = null;

			try {
				dispatcher = ExertDispatcherFactory.getFactory()
						.createDispatcher((NetJob) job,
								new HashSet<Context>(), false, myMemberUtil, provider);
				while (dispatcher.getState() != Exec.DONE
						&& dispatcher.getState() != Exec.FAILED
						&& dispatcher.getState() != Exec.SUSPENDED) {
					logger.fine("Dispatcher waiting for exertions... Sleeping for 250 milliseconds.");
					Thread.sleep(250);
				}
				logger.fine("Dispatcher State: " + dispatcher.getState());
			} catch (DispatcherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = (NetJob) dispatcher.getExertion();
			try {
				job.getControlContext().appendTrace(provider.getProviderName()  + " dispatcher: " 
						+ dispatcher.getClass().getName());
			} catch (RemoteException e) {
				// ignore it
			}
		}

		public Job getJob() {
			return job;
		}

		public Job getResult() throws ContextException {
			return result;
		}

		public String getJobID() {
			return jobID;
		}
	}

	protected class TaskThread extends Thread {

		// doJob method calls this internally
		private Task task;

		private Task result;

		private String taskID;
		
		private Provider provider;

		public TaskThread(Task task, Provider provider) {
			this.task = task;
			this.provider = provider;
		}

		public void run() {
			logger.finest("*** TaskThread Started ***");
			SpaceTaskDispatcher dispatcher = null;

			try {
				dispatcher = (SpaceTaskDispatcher) ExertDispatcherFactory
						.getFactory().createDispatcher((NetTask) task,
								new HashSet<Context>(), false, myMemberUtil, provider);
				try {
					task.getControlContext().appendTrace(provider.getProviderName() + " dispatcher: " 
							+ dispatcher.getClass().getName());
				} catch (RemoteException e) {
					//ignore it, local call
				}
				while (dispatcher.getState() != Exec.DONE
						&& dispatcher.getState() != Exec.FAILED
						&& dispatcher.getState() != Exec.SUSPENDED) {
					logger.fine("Dispatcher waiting for a space task... Sleeping for 250 milliseconds.");
					Thread.sleep(250);
				}
				logger.fine("Dispatcher State: " + dispatcher.getState());
			} catch (DispatcherException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = (NetTask) dispatcher.getExertion();
		}

		public Task getTask() {
			return task;
		}

		public Task getResult() throws ContextException {
			return result;
		}

		public String getTaskID() {
			return taskID;
		}
	}
	
	private void prepareToResume(Job job) {
		return;
	}

	private void prepareToStep(Job job) {
		Exertion e = null;
		for (int i = 0; i < job.size(); i++) {
			e = job.get(i);
			((ControlContext) job.getContext()).setReview(e, true);
			if (((ServiceExertion) e).isJob())
				prepareToStep((Job) e);
		}
		return;
	}

	public Exertion doTask(Exertion task) throws RemoteException {
		setServiceID(task);
		try {
			if (task.isMonitorable()
					&& !task.isWaitable()) {
				replaceNullExertionIDs(task);
				notifyViaEmail(task);
				new TaskThread((Task) task, this).start();
				return task;
			} else {
				TaskThread taskThread = new TaskThread((Task) task, this);
				taskThread.start();
				taskThread.join();
				Task result = taskThread.getResult();
				logger.finest("Spacer result: " + result);
				return result;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
		/*
		 * try { if (task.isScript()) { ServiceContext[] ctxs = task.contexts;
		 * String fn = (String)task.getValue(OUT_FILE); Vector outputs =
		 * task.contexts[0].getStartsWithValues(OUT_PATH);
		 * ctxs[0].putValue(OUT_FILE, getDataFilename(fn));
		 * ((SorcerScript)task.getMethod()).invokeMethod(ctxs);
		 * ctxs[0].putValue((String)outputs.firstElement(), getDataURL(fn));
		 * result = task; } else if (task.isRemote()) { Util.debug(this,
		 * "==============> EXECUTE TASK " + task.name); Util.debug(this,
		 * "processTask:contexts=" + Contexts.contextToString(task.contexts));
		 * task.taskID = new UID().toString(); Util.debug(this, "get provider
		 * for=" + task.providerName); Provider provider =
		 * Catalog.getCatalog().lookup(task.getAttributes());
		 * 
		 * if (provider == null) { String msg = null; //get the PROCESS Method
		 * and grab provider name + interface ServiceMethod method =
		 * task.getMethod(); msg = "No Provider available. Provider Name: " +
		 * method.providerName + " Provider interface: " + method.serviceType;
		 * System.err.println(msg);
		 * 
		 * result = task; task.putValue(OUT_VALUE, "no provider available");
		 * 
		 * 
		 * throw new ExertionException(msg, task); } else {
		 * task.setProvider(provider); result =
		 * (RemoteServiceTask)provider.service(task); Util.debug(this,
		 * "processTask ==============> EXECUTED TASK " + result.name);
		 * Util.debug(this, "processTask:contexts=" +
		 * Contexts.contextToString(result.contexts)); } } } catch
		 * (ExertionMethodException e) { e.printStackTrace(); } catch
		 * (ExertionException e) { e.printStackTrace(); }
		 */

}
