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

package sorcer.core.grid.provider.grider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.security.auth.Subject;

import net.jini.config.ConfigurationException;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.id.UuidFactory;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import net.jini.lookup.ui.factory.JFrameFactory;
import sorcer.core.Caller;
import sorcer.core.FileStorer;
import sorcer.core.Grider;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ControlContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.grid.provider.caller.CallerUtil;
import sorcer.core.grid.provider.grider.ui.GriderDispatcherUI;
import sorcer.core.provider.Jobber;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.signature.NetSignature;
import sorcer.security.util.SorcerPrincipal;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Service;
import sorcer.service.ServiceExertion;
import sorcer.service.SignatureException;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.ui.serviceui.UIFrameFactory;
import sorcer.util.AccessorException;
import sorcer.util.DocumentDescriptor;
import sorcer.util.ProviderAccessor;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;
import sorcer.util.rmi.InputStreamAdapter;
import sorcer.util.rmi.OutputStreamProxy;

import com.sun.jini.start.LifeCycle;

public class GridDispatcherProviderImpl extends ServiceProvider implements
		Grider, SorcerConstants {

	private static Disco disco;

	private static ServiceContext ctx;

	// *** Abhijit:: SorcerProvider shall have a client field for this pupose
	Subject client = null;

	// ***Abhijit:: These methods should be provided in SorcerProvider.java
	// later
	public void setPrincipal(Exertion ex, Subject subj) {

	}

	public void setPrincipal(Exertion ex, Principal p) {
		logger.finest(this + "Setting the 88888888 Exertion Principal 888888");
		((ServiceExertion) ex).setPrincipal(new SorcerPrincipal(p.getName()));
	}

	public void setPrincipal(Context ctx, Principal p) {
		logger.finest(this + "Setting the 00000000 Context Principal 0000000");
		ctx.setPrincipal(new SorcerPrincipal(p.getName()));
	}

	public GridDispatcherProviderImpl() throws RemoteException {
		disco = new Disco();
	}

	// require ctor for Jini 2 NonActivatableServiceDescriptor
	public GridDispatcherProviderImpl(String[] args, LifeCycle lifeCycle)
			throws Exception {
		super(args, lifeCycle);
		disco = new Disco();
	}

	public FileStorer getFileStorer() throws RemoteException {
		return disco.getFileStorer();
	}

	// public static Subject client = null;
	public Context computePrime(Context dispatcherCtx) throws RemoteException {
		// *** Added by Abhijit
		/*
		 * logger.finest("Abhijit:: ********Call to computePrime
		 * received*********"); try{ logger.finest("Starting Client Subject");
		 * 
		 * Set principals = client.getPrincipals(); Iterator iterator =
		 * principals.iterator(); while (iterator.hasNext()){ Principal
		 * principal = (Principal) iterator.next(); logger.finest("Abhijit::
		 * WHOA! I got the subject! I got the subject!!"+ principal.getName());
		 * /* if(principal.getName()!="Abhijit"){ return null; } }
		 * 
		 * }catch(Exception ex){ logger.finest("Exception occured in
		 * ClientSubject"); ex.printStackTrace(); }
		 */
		// ***
		/*
		 * try{ ClientSubject cs =
		 * (ClientSubject)ServerContext.getServerContextElement
		 * (ClientSubject.class); client = null; if(cs!=null){
		 * client=cs.getClientSubject();
		 * logger.finest("YES! GOT THE CS = "+cs+", the client subject is
		 * "+cs.getClientSubject()); }else{ logger.finest("OUCH!! CLIENT SUBJECT
		 * NOT OBTAINED"); } if(client==null){ logger.finest("ClIENT SUBJECT SET
		 * TO NULL"); } }catch(Exception ex){ logger.finest("Exception in
		 * creating clientSubject"); ex.printStackTrace(); }
		 * logger.finest("+++++++++++++++++++++++ Printing Subject Public
		 * Credentials +++++++++++++"); Set publicCs =
		 * client.getPublicCredentials(); Iterator iterator =
		 * publicCs.iterator(); int count =0 ; while(iterator.hasNext()){
		 * logger.finest("///// - " + count++);
		 * logger.finest("--"+iterator.next()); }
		 * logger.finest("+++++++++++++++++++++++++++++++++++++++++++");
		 */

		if (ctx == null)
			initContextTemplate(dispatcherCtx);

		try {

			// doMagic();

			String notify = GridDispatcherContextUtil.getNotify(dispatcherCtx);
			int jobSize = GridDispatcherContextUtil.getJobSize(dispatcherCtx);

			String inputFile = GridDispatcherContextUtil
					.getInputFile(dispatcherCtx);
			String outputFile = GridDispatcherContextUtil
					.getOutputFile(dispatcherCtx);
			String[] inputValues = GridDispatcherContextUtil
					.getInputValues(dispatcherCtx);
			RemoteEventListener callback = GridDispatcherContextUtil
					.getCallback(dispatcherCtx);

			if (inputFile != null && !"".equals(inputFile)) {
				new JobsDispatcher(inputFile, jobSize, notify,
						new DispatcherResult(outputFile), client).start();
			}

			else if (inputValues != null
					&& !("".equals(outputFile) || outputFile == null)) {
				new JobsDispatcher(inputValues, jobSize, notify,
						new DispatcherResult(outputFile), client).start();
			}

			else if (inputValues != null) {
				new JobsDispatcher(inputValues, jobSize, notify,
						new DispatcherResult(callback), client).start();
			}

			else {
				GridDispatcherContextUtil.setException(dispatcherCtx,
						"No Inputs for the dispatcher provider");
			}

			return dispatcherCtx;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
	}

	public void init() throws RemoteException, ConfigurationException {
		init();
	}

	private ServiceContext initContextTemplate(Context context) {
		ctx = new ServiceContext("dispatcher", "dispatcher");
		// setPrincipal(ctx, );
		// if (client!=null)
		// setPrincipal(ctx,((Principal)client.getPrincipals().iterator().next()));

		String fileStoreName = "*"; // no option in the ServiceUI atm

		ctx.setId(UuidFactory.generate());
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		try {
			CallerUtil.setBin(ctx);

			// ----------For windows--------------------------
			CallerUtil.setWindows();
			params.put("folder", context
					.getValue("caller/program/win/bin/folder"));
			params.put("file", context.getValue("caller/program/win/bin/file"));

			URI binURI = SorcerUtil.getURI(FileStorer.class.getName(),
					fileStoreName, params);
			CallerUtil.setBinURIs(ctx, new URI[] { binURI });

			// Now for the libraries
			params.put("folder", context
					.getValue("caller/program/win/lib/folder"));
			params.put("file", context.getValue("caller/program/win/lib/file"));

			URI libURI0 = SorcerUtil.getURI(FileStorer.class.getName(),
					fileStoreName, params);
			CallerUtil.setLibURIs(ctx, new URI[] { libURI0 });

			// ----------For Linux--------------------------
			CallerUtil.setLinux();
			params.put("folder", context
					.getValue("caller/program/linux/bin/folder"));
			params.put("file", context
					.getValue("caller/program/linux/bin/file"));

			binURI = SorcerUtil.getURI(FileStorer.class.getName(),
					fileStoreName, params);
			CallerUtil.setBinURIs(ctx, new URI[] { binURI });

			// ----------For Solaris--------------------------
			CallerUtil.setSolaris();
			params.put("folder", context
					.getValue("caller/program/solaris/bin/folder"));
			params.put("file", context
					.getValue("caller/program/solaris/bin/file"));

			binURI = SorcerUtil.getURI(FileStorer.class.getName(),
					fileStoreName, params);
			CallerUtil.setBinURIs(ctx, new URI[] { binURI });

			ctx.setDomainName("algerm");
			ctx.setSubdomainName("Domain");

			CallerUtil.setCmds(ctx, new String[] { (String) context
					.getValue("caller/call/cmd") });
			// CallerUtil.setArgs(ctx, new String[] {"-h
			// Testing
			// CallerUtil.setCallOutput(ctx, new String[] { "OUTPUT OF CALL"});

		} catch (ContextException e) {
			e.printStackTrace();
		}
		return ctx;
	}

	// public Hashtable getMethodContexts() {
	// Hashtable mc = new Hashtable();
	// ServiceContext sc;
	// try {
	// sc = (ServiceContext)Util.clone(ctx);
	// }catch (Exception e) { e.printStackTrace(); return null; }
	// mc.put("computePrime", sc);
	// return mc;
	// }

	public UIDescriptor getMainUIDescriptor() {
		UIDescriptor uiDesc = null;
		try {
			uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
					(JFrameFactory) new UIFrameFactory(new URL[] { new URL(
							Sorcer.getWebsterUrl() + "/grider-ui.jar") },
							GriderDispatcherUI.class.getName()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return uiDesc;
	}

	public boolean isValidTask() {
		return true;
	}

	public static final class DispatcherResult {

		private String resultFile = null;

		private RemoteEventListener callback;

		private DocumentDescriptor appendDesc;

		public URL outputURL = null;

		public DispatcherResult(String resultFile) {
			this.resultFile = resultFile;
			initiateAppendFileServer();
		}

		public DispatcherResult(RemoteEventListener callback) {
			this.callback = callback;
		}

		private static SorcerPrincipal getPrincipal() {
			SorcerPrincipal principal = new SorcerPrincipal();
			principal.setId("101");
			principal.setName("algerm");
			principal.setRole("root");
			principal.setAccessClass(4);
			principal.setExportControl(false);
			return principal;
		}

		public void done(Context ctx) {
			String[] result;
			try {
				result = CallerUtil.getCallOutput(ctx);

				if (result == null) {
					return;
				}
				if (resultFile != null)
					writeOutputToFile(result);
				else if (callback != null) {
					for (int i = 0; i < result.length; i++)
						callback.notify(new RemoteEvent(result[i], 0, 0, null));
				}
			} catch (Exception uee) {
				uee.printStackTrace();
			}
		}

//		private void undoMagic() {
//			try {
//				AutonomicProvisioner auto = disco.getProvisioner();
//				if (auto != null)
//					auto.undeploy(Caller.class.getName());
//			} catch (Exception forgetIt) {
//				forgetIt.printStackTrace();
//			}
//		}

		private void initiateAppendFileServer() {
			try {
				// Upload the output file
				FileStorer fs = GridDispatcherProviderImpl.disco
						.getFileStorer();
				String folderName = resultFile.substring(0, resultFile
						.lastIndexOf("/"));
				String fileName = resultFile.substring(resultFile
						.lastIndexOf("/") + 1);
				appendDesc = new DocumentDescriptor();
				appendDesc.setPrincipal(getPrincipal());
				appendDesc.setFolderPath(folderName);
				appendDesc.setDocumentName(fileName);
				File dummy = new File(fileName);
				dummy.createNewFile();
				appendDesc = fs.getOutputDescriptor(appendDesc);
				((OutputStreamProxy) appendDesc.out).write(dummy);

				// now prepare for appending to the file
				appendDesc = fs.getAppendDescriptor(appendDesc);
				outputURL = appendDesc.fileURL;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void writeOutputToFile(String[] result) throws RemoteException,
				IOException {
			try {
				for (int i = 0; i < result.length; i++)
					((OutputStreamProxy) appendDesc.out).write(result[i]
							.getBytes());
				((OutputStreamProxy) appendDesc.out).write("\n".getBytes());
			} catch (RemoteException re) {
				re.printStackTrace();
				try {
					// now prepare for appending to the file
					FileStorer fs = disco.getFileStorer();
					appendDesc = fs.getAppendDescriptor(appendDesc);
					for (int i = 0; i < result.length; i++)
						((OutputStreamProxy) appendDesc.out).write(result[i]
								.getBytes());
					((OutputStreamProxy) appendDesc.out).write("\n".getBytes());
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}

		public void doFinally() {
			if (resultFile != null) {
				try {
					writeOutputToFile(new String[] { "_DONE_" });
					(appendDesc.out).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else
				try {
					callback.notify(new RemoteEvent("_DONE_", 0, 0, null));
				} catch (Exception uee) {
					uee.printStackTrace();
				}
			// undoMagic();
		}
	}

	public static final class JobsDispatcher extends Thread {

		String inputFile;

		int jobSize;

		DispatcherResult result;

		String[] inputValues;

		int activeJobs;

		String notify;

		private Subject client = null;

		public JobsDispatcher(String inputFile, int jobSize, String notify,
				DispatcherResult result) {
			this.inputFile = inputFile;
			this.jobSize = jobSize;
			this.result = result;
			this.notify = notify;
			activeJobs = 0;
		}

		public JobsDispatcher(String[] inputValues, int jobSize, String notify,
				DispatcherResult result) {
			this.inputValues = inputValues;
			this.jobSize = jobSize;
			this.result = result;
			this.notify = notify;
			activeJobs = 0;
		}

		public JobsDispatcher(String inputFile, int jobSize, String notify,
				DispatcherResult result, Subject client) {
			this.inputFile = inputFile;
			this.jobSize = jobSize;
			this.result = result;
			this.notify = notify;
			this.client = client;
			activeJobs = 0;
		}

		public JobsDispatcher(String[] inputValues, int jobSize, String notify,
				DispatcherResult result, Subject client) {
			this.inputValues = inputValues;
			this.jobSize = jobSize;
			this.result = result;
			this.notify = notify;
			this.client = client;
			activeJobs = 0;
		}

		public void run() {

			if (inputFile != null)
				inputValues = (String[]) parseInputFile();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < inputValues.length; i++) {
				StringTokenizer st = new StringTokenizer(inputValues[i]);
				sb.delete(0, sb.length());
				if (st.hasMoreTokens())
					sb.append(" -k ").append(st.nextToken());
				if (st.hasMoreTokens())
					sb.append(" -h ").append(st.nextToken());
				inputValues[i] = sb.toString();
			}

			int fromIndex = 0;
			int toIndex = 0;
			do {
				toIndex = fromIndex + jobSize - 1;
				new JobDispatcher(inputValues, fromIndex, toIndex, notify, this)
						.start();
				fromIndex = toIndex + 1;
				activeJobs++;
			} while (fromIndex <= inputValues.length - 1);
		}

		public String[] parseInputFile() {
			Vector v = new Vector();
			BufferedReader in = null;

			try {
				String fileName = downloadFile();
				in = new BufferedReader(new FileReader(fileName));
				String args;
				while ((args = in.readLine()) != null)
					v.add(args);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (Exception e) {
				}
			}

			return (String[]) (v.toArray(new String[v.size()]));
		}

		private static SorcerPrincipal getPrincipal() {
			SorcerPrincipal principal = new SorcerPrincipal();
			principal.setId("101");
			principal.setName("algerm");
			principal.setRole("root");
			principal.setAccessClass(4);
			principal.setExportControl(false);
			return principal;
		}

		// Downloads the file from file store and
		// returns the name of the file.
		private String downloadFile() throws RemoteException, IOException {

			String folderName = inputFile.substring(0, inputFile
					.lastIndexOf("/"));
			String fileName = inputFile
					.substring(inputFile.lastIndexOf("/") + 1);
			logger.finest("Docnloading file path=" + folderName + " fileName="
					+ fileName);
			DocumentDescriptor docDesc = new DocumentDescriptor();
			docDesc.setPrincipal(getPrincipal());
			docDesc.setFolderPath(folderName);
			docDesc.setDocumentName(fileName);

			FileStorer fs = GridDispatcherProviderImpl.disco.getFileStorer();
			docDesc = fs.getInputDescriptor(docDesc);
			((InputStreamAdapter) docDesc.in).read(new File(fileName));

			return fileName;
		}

		public void done(Job resultJob) throws ContextException {
			activeJobs--;
			for (int i = 0; i < resultJob.size(); i++)
				result.done(((ServiceExertion) resultJob.get(i))
						.getContext());

			if (activeJobs == 0)
				result.doFinally();
		}

		public URL getOutputURL() {
			return result.outputURL;
		}
	}

	public static class Disco {

		ServiceDiscoveryManager sdm;

		LookupCache lCache1;

		LookupCache lCache2;

		private static final int MAX_TRIES = 100;

		private static final int SLEEP_TIME = 100;

		public Disco() {
			try {
				LookupDiscovery disco = new LookupDiscovery(Sorcer.getLookupGroups());
				sdm = new ServiceDiscoveryManager(disco,
						new LeaseRenewalManager());
				lCache1 = sdm.createLookupCache(new ServiceTemplate(null,
						new Class[] { sorcer.service.Service.class }, null),
						null, null);
//				lCache2 = sdm
//						.createLookupCache(
//								new ServiceTemplate(
//										null,
//										new Class[] { sorcer.core.provider.autonomic.provisioner.AutonomicProvisioner.class },
//										null), null, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Jobber getJobber() {
			int tries = 0;
			while (tries < MAX_TRIES) {
				ServiceItem[] items = (lCache1.lookup(null, Integer.MAX_VALUE));

				for (int i = 0; i < items.length; i++)
					if (items[i].service != null
							&& items[i].service instanceof Jobber) {
						print(tries, items[i].service);
						logger.finest("GOT JOBBER SERVICE - SERVICE ID="
								+ items[i].serviceID);
						return (Jobber) items[i].service;
					}
				tries++;
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
				}
			}
			print(tries, null);
			return null;
		}

//		public AutonomicProvisioner getProvisioner() {
//			int tries = 0;
//			while (tries < MAX_TRIES) {
//				ServiceItem[] items = (lCache2.lookup(null, Integer.MAX_VALUE));
//				for (int i = 0; i < items.length; i++)
//					if (items[i].service != null
//							&& items[i].service instanceof AutonomicProvisioner) {
//						print(tries, items[i].service);
//						return (AutonomicProvisioner) items[i].service;
//					}
//				tries++;
//				try {
//					Thread.sleep(SLEEP_TIME);
//				} catch (Exception e) {
//				}
//			}
//			print(tries, null);
//			return null;
//		}

		public FileStorer getFileStorer() {
			int tries = 0;
			while (tries < MAX_TRIES) {
				ServiceItem[] items = (lCache1.lookup(null, Integer.MAX_VALUE));
				for (int i = 0; i < items.length; i++)
					try {
						if (items[i].service != null
								&& Class.forName(
										Sorcer.getProperty("filestore.type"))
										.isInstance(items[i].service)) {
							print(tries, items[i].service);
							return (FileStorer) items[i].service;
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				tries++;
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (Exception e) {
				}
			}
			print(tries, null);
			return null;
		}

		public void print(int i, Object obj) {
			logger.finest("tries = " + i + " object=" + obj);
		}
	}

	public static final class JobDispatcher extends Thread {

		private String[] inputValues;

		private int fromIndex;

		private int toIndex;

		private JobsDispatcher disp;

		private String notify;

		private Subject client;

		public JobDispatcher(String[] inputValues, int fromIndex, int toIndex,
				String notify, JobsDispatcher disp) {

			this.inputValues = inputValues;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.notify = notify;
			this.disp = disp;
		}

		public JobDispatcher(String[] inputValues, int fromIndex, int toIndex,
				String notify, JobsDispatcher disp, Subject client) {

			this.inputValues = inputValues;
			this.fromIndex = fromIndex;
			this.toIndex = toIndex;
			this.notify = notify;
			this.disp = disp;
			this.client = client;
		}

		public void run() {
			if (toIndex >= inputValues.length)
				toIndex = inputValues.length - 1;

			Job job = new NetJob("dispatcher" + fromIndex + "-" + toIndex);
			
			Job result;
			// if(client!=null)
			// setPrincipal(job,((Principal)client.getPrincipals().iterator().next()));
			// job.setPrincipal(new
			// GAppPrincipal(((Principal)client.getPrincipals().iterator().next()).getName()));;
			// job.setPrincipal(new GAppPrincipal("((ABHIJIT))"));
			ControlContext cc = (ControlContext) job.getContext();
			job.setId(UuidFactory.generate());
			cc.setExecTimeRequested(true);
			cc.setMonitorable(false);
			// There are access types - CATLAOG, SPACE and DIRECT
			// There are Job Strategies - SEQUENTIAL, PARALLEL
			// Not sure if CATLAOG and DIRECT work with PARALLEL, they should
			// though
			// job.getCC(). setJobStrategyAccess(job.getCC().SPACE);
			cc.setAccessType(Access.PUSH);
			// job.getCC(). setJobStrategy(job.getCC().PARALLEL);
			cc.setFlowType(Flow.SEQ);
			// job.getCC(). setJobStrategy(job.getCC().DIRECT);
			cc.setNotifyList(notify);

			ServiceExertion task = null;

			for (int i = fromIndex; i <= toIndex; i++) {
				if (!"".equals(inputValues[i])) {
					try {
						job.addExertion(task = getTask(inputValues[i], i));
					} catch (Exception e) {
						e.printStackTrace();
					}
					cc.setNotifyList(task, notify);
					cc.setExecTimeRequested(task, true);
				}
			}

			try {
				// logger.finest("-------------------calling
				// disco.getJobber--------");
				// commented by Daniela Inclezan
				// Jobber jobber = disco.getJobber();
				Jobber jobber = getJobber();
				logger.finest("Jobber Obtained --> ");
				// logger.finest(">>>>>>>>>>>>>>>>>>.Sending job ="+job);
				// Subject server =
				// Subject.getSubject(AccessController.getContext());
				// logger.finest(this+"Abhijit:::>>> Subject obtained
				// before calling on jobber = "+server);
				// ***Abhijit - change this to more generic implementation -
				// JUST for trial
				// logger.finest("Abhijit::>>>>> PREAPRING PROXY");
				// jobber = (Jobber)preparer.prepareProxy(jobber);
				// logger.finest("Abhijit::>>>>>--------- PREAPRING PROXY
				// --------- DONE");
				// **The above code to be put as more generic implementation

				if (job.size() != 0) {
					System.out
							.println("\n*** Inside job.size()!=0 statement\n");
					// if(client!=null){
					// logger.finest("Abhijit::>>----------- calling as a
					// CLIENT Subject.doAs()------client = "+client);
					final Job finalJob = job;
					final Jobber finalJobber = jobber;
					result = (Job) ((Service)jobber).service(job, null);
					/*
					 * result = (ServiceJob)Subject.doAs(client, new
					 * PrivilegedExceptionAction() { public Object run() throws
					 * Exception { try{ //logger.finest("The present Subject
					 * is"+Subject.getSubject(AccessController.getContext()));
					 * //AccessController.checkPermission(new
					 * MethodPermission(finalM.getName())); return
					 * (ServiceJob)finalJobber.service(finalJob); }catch
					 * (AccessControlException ex){ logger.finest("Exception
					 * Occured in Permission"); ex.printStackTrace(); throw new
					 * AccessControlException("Access Denied to the method"); }
					 * } });
					 */

					// }else{
					// logger.finest("Abhijit::>>----------- calling
					// WITHOUT a client subject ------");
					// result = (ServiceJob)jobber.service(job);
					// }
					logger.finest("--------result found --------+");
					// else logger.finest("--------result = NULL");
				} else {
					System.out
							.println("Inside the else of job.size!=0 statement");
					result = job;
				}
				// Testing
				// result = job;
				disp.done(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// method created by Daniela Inclezan
		public Jobber getJobber() {
			logger.info("get Jobber");
			try {
				Jobber jobber = ProviderAccessor.getJobber();
				return jobber;
			} catch (AccessorException e) {
				e.printStackTrace();
				return null;
			}

			/*
			 * int tries = 0; int MAX_TRIES = 20; int SLEEP_TIME = 2000; while
			 * (tries < MAX_TRIES) { //ServiceTemplate st = new
			 * ServiceTemplate(null, new Class[] { Jobber.class }, null);
			 * //ServiceItem[] items = ServiceAccessor.getServiceItems(st, null,
			 * Env.getGroups()); ProviderAccessor.getJobber();
			 * logger.info("Found " + items.length + " proxies");
			 * 
			 * for (int i = 0; i < items.length; i++) if (items[i].service !=
			 * null && items[i].service instanceof Jobber) {
			 * logger.info("number of tries: " + tries + " item: " +
			 * items[i].service);
			 * logger.finest("GOT JOBBER SERVICE - SERVICE ID=" +
			 * items[i].serviceID); return (Jobber) items[i].service; } tries++;
			 * try { Thread.sleep(SLEEP_TIME); } catch (Exception e) { } }
			 * return null;
			 */
		}

		public ServiceExertion getTask(String inputValue, int index) throws SignatureException {
			Context sc;
			NetSignature method;
			try {
				sc = (Context) SorcerUtil.clone(ctx);
				CallerUtil.setArgs(sc, new String[] { inputValue });
				CallerUtil.setOutputURL(sc, disp.getOutputURL());
				method = new NetSignature("execute", Caller.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			NetTask task = new NetTask("dispatcher-" + index,
					"Dispatcher Task-" + index, method);
			task.setContext(sc);
			// if(client!=null)
			// task.setPrincipal(new
			// GAppPrincipal(((Principal)client.getPrincipals().iterator().next()).getName()));;
			// setPrincipal(task,((Principal)client.getPrincipals().iterator().next()));
			// task.setPrincipal(new GAppPrincipal("-----*ABHIJIT*-----"));
			task.setId(UuidFactory.generate());
			return task;
		}
	}
}
