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

package sorcer.core.grid.provider.dispatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.security.AccessController;
import java.security.Principal;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.transaction.InvalidTransactionException;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.export.ServerContext;
import net.jini.io.context.ClientSubject;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.LookupCache;
import net.jini.lookup.ServiceDiscoveryManager;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.core.FileStorer;
import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.core.grid.provider.caller.CallerUtil;
import sorcer.core.provider.Jobber;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.provider.autonomic.provisioner.AutonomicProvisioner;
import sorcer.core.provider.autonomic.provisioner.ProvisionerException;
import sorcer.core.signature.NetSignature;
import sorcer.security.util.SorcerPrincipal;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.ServiceExertion;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.ui.serviceui.UIFrameFactory;
import sorcer.util.DocumentDescriptor;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;
import sorcer.util.Util;
import sorcer.util.rmi.InputStreamAdapter;
import sorcer.util.rmi.OutputStreamProxy;

import com.sun.jini.start.LifeCycle;

public class GridDispatcherProviderImpl extends ServiceProvider
    implements GridDispatcherRemote, SorcerConstants {

    private static Disco disco;
    private static ServiceContext ctx;
    //*** Abhijit:: SorcerProvider shall have a client field for this pupose
    Subject client = null;	
    //***Abhijit:: These methods should be provided in SorcerProvider.java later
    public void setPrincipal(Exertion ex, Subject subj){
	
    }
    public void setPrincipal(Exertion ex, Principal p){
	System.out.println(this+"Setting the 88888888 Exertion Principal 888888");
	ex.setPrincipal(new SorcerPrincipal(p.getName()));
    }
    public void setPrincipal(Context ctx, Principal p){
	System.out.println(this+"Setting the 00000000 Context Principal 0000000");
	ctx.setPrincipal(new SorcerPrincipal(p.getName()));
    }
    

    
    public GridDispatcherProviderImpl() throws RemoteException {
	disco = new Disco();
    }
    
       //require ctor for Jini 2 NonActivatableServiceDescriptor
    public GridDispatcherProviderImpl(String [] args,LifeCycle lifeCycle)
	throws Exception{
	super(args,lifeCycle);
	disco = new Disco();
    }

    public FileStorer getFileStorer() throws RemoteException {
	return disco.getFileStorer();
    }
    public Context computePrime(ProviderContext dispatcherCtx) throws RemoteException{
	System.out.println("Abhijit:: >>>> Inside computePrime(ProviderContext)");
	return computePrime((Context)dispatcherCtx);
    }

    //public static Subject client = null;
    
    public Context computePrime(Context dispatcherCtx) 
	throws RemoteException {	
	//*** Added by Abhijit
	/*
	  System.out.println("Abhijit:: ********Call to computePrime received*********");
	  try{
	  System.out.println("Starting Client Subject");
	  
	  Set principals = client.getPrincipals();
	  Iterator iterator = principals.iterator();
	  while (iterator.hasNext()){
	  Principal principal = (Principal) iterator.next();
	  System.out.println("Abhijit:: WHOA! I got the subject! I got the subject!!"+ principal.getName());
	  /*
	  if(principal.getName()!="Abhijit"){
	  return null;
	  }
	  }	    
	  
	  }catch(Exception ex){
	  System.out.println("Exception occured in ClientSubject");
	  ex.printStackTrace();
	  }
	*/
	//***
	try{
	    ClientSubject cs = (ClientSubject)ServerContext.getServerContextElement(ClientSubject.class);
	    client = null;
	    if(cs!=null){
		client=cs.getClientSubject();
		System.out.println("YES! GOT THE CS = "+cs+", the client subject is "+cs.getClientSubject());
	    }else{
		System.out.println("OUCH!! CLIENT SUBJECT NOT OBTAINED");
	    }	
	    if(client==null){
		System.out.println("ClIENT SUBJECT SET TO NULL");
	    }
	}catch(Exception ex){
	    System.out.println("Exception in creating clientSubject");
	    ex.printStackTrace();
	}
	System.out.println("+++++++++++++++++++++++ Printing Subject Public Credentials +++++++++++++");
	Set publicCs = client.getPublicCredentials();
	Iterator iterator = publicCs.iterator();
	int count =0 ;
	while(iterator.hasNext()){
	    System.out.println("///// - " + count++);
	    System.out.println("--"+iterator.next());
	}
	System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
		    
	if (ctx == null) 	initContextTemplate();

	try {
	    
	    //doMagic();
	    
	    String notify = GridDispatcherContextUtil.getNotify(dispatcherCtx);
	    int jobSize = GridDispatcherContextUtil.getJobSize(dispatcherCtx);
	    
	    String inputFile  = GridDispatcherContextUtil.getInputFile(dispatcherCtx);
	    String outputFile = GridDispatcherContextUtil.getOutputFile(dispatcherCtx);
	    String[] inputValues = GridDispatcherContextUtil.getInputValues(dispatcherCtx);
	    RemoteEventListener callback = GridDispatcherContextUtil.getCallback(dispatcherCtx);
	    
	    if (inputFile != null && !"".equals(inputFile))	    
		new JobsDispatcher(inputFile, 
				   jobSize, 
				   notify,
				   new DispatcherResult(outputFile),client).start();
	    else if (inputValues != null && !( "".equals(outputFile) || 
					       outputFile==null)  ) {  
		new JobsDispatcher( inputValues, 
				    jobSize,
				    notify,
				    new DispatcherResult(outputFile), client).start();
	    }
	    else if (inputValues != null)
		new JobsDispatcher( inputValues, 
				    jobSize,
				    notify,
				    new DispatcherResult(callback), client).start();
	    else 
		GridDispatcherContextUtil.setException(dispatcherCtx, 
					      "No Inputs for the dispatcher provider");
	    
	    return dispatcherCtx;
	}catch (Exception e) {
	    e.printStackTrace();
	    throw new RemoteException(e.getMessage());
	}
    }
    
    private void doMagic() throws RemoteException {
	
	AutonomicProvisioner auto = disco.getProvisioner();
	try {
	    if (auto!=null)
		auto.deploy("sorcer.core.Caller");

	}catch (ProvisionerException pe) { pe.printStackTrace(); } 
    }

    public void init() throws RemoteException{
	super.init();
    }

    private ServiceContext initContextTemplate() {
	ctx = new ServiceContext("dispatcher", "dispatcher");
	//setPrincipal(ctx, );
	if (client!=null)
	    setPrincipal(ctx,((Principal)client.getPrincipals().iterator().next()));	
	ctx.setId(new UID().toString());
	Hashtable params = new Hashtable();		
	CallerUtil.setBin(ctx);
	
	//----------For windows--------------------------
	CallerUtil.setWindows();			
	params.put("folder", getProperty("caller.program.win.bin.folder"));
	params.put("file", getProperty("caller.program.win.bin.file"));
	
	URI binURI = SorcerUtil.getURI("sorcer.core.FileStorer",
				       getProperty("provider.fs.name"),
				       params);
	CallerUtil.setBinURIs(ctx, new URI[] { binURI });	
	//Now for the libraries
	params.put("folder", getProperty("caller.program.win.lib.folder"));
	params.put("file", getProperty("caller.program.win.lib.file0"));
	URI libURI0 = SorcerUtil.getURI("sorcer.core.FileStorer",
				       getProperty("provider.fs.name"),
				       params);
	
	CallerUtil.setLibURIs(ctx, new URI[] { libURI0}); 
	
	//----------For Linux--------------------------
	CallerUtil.setLinux();			
	params.put("folder", getProperty("caller.program.linux.bin.folder"));
	params.put("file", getProperty("caller.program.linux.bin.file"));
	
	binURI = SorcerUtil.getURI("sorcer.core.FileStorer",
				       getProperty("provider.fs.name"),
				       params);
	CallerUtil.setBinURIs(ctx, new URI[] { binURI });	
	
	
	//----------For Solaris--------------------------
	CallerUtil.setSolaris();			
	params.put("folder", getProperty("caller.program.solaris.bin.folder"));
	params.put("file", getProperty("caller.program.solaris.bin.file"));
	
	binURI = SorcerUtil.getURI("sorcer.core.FileStorer",
				       getProperty("provider.fs.name"),
				       params);
	CallerUtil.setBinURIs(ctx, new URI[] { binURI });	
	
	
	ctx.setDomainName("Abhijit"); ctx.setSubdomainName("Domain");
	
	CallerUtil.setCmds(ctx, new String[] {getProperty("caller.call.cmd")} );
	//	CallerUtil.setArgs(ctx, new String[] {"-h 
	//Testing
	//CallerUtil.setCallOutput(ctx, new String[] { "OUTPUT OF CALL"});
	return ctx;
    }

    
    // public Hashtable getMethodContexts() {
    //Hashtable mc = new Hashtable();
    //ServiceContext sc;
    //try {
    //    sc = (ServiceContext)Util.clone(ctx);
    //	}catch (Exception e) { e.printStackTrace(); return null; }
    //mc.put("computePrime", sc);
    //return mc;
    //}
    
    public UIDescriptor getMainUIDescriptor() {
	System.out.println(this.getClass()+">>> Abhijit:: Inside DispatcherImplm's mainUIDescriptor");
	UIDescriptor uiDesc = null;
	try {
	    System.out.println(this.getClass()+"Abhijit::>> publishin gthe UI");
	    uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
				     new UIFrameFactory(new URL[]{new URL(Sorcer.getWebsterUrl()+"dispatcher-ui.jar")},
							"sorcer.provider.grid.dispatcher.GridDispatcherUI"));
	}catch(Exception ex) { ex.printStackTrace(); }
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
	    principal.setId("363");
	    principal.setName("sobol");
	    principal.setRole("root");
	    principal.setAccessClass(4);
	    principal.setExportControl(false);
	    return principal;
	}


	public void done(Context ctx) {
	    String[] result = CallerUtil.getCallOutput(ctx);
	    if (result == null) {
		return;
	    }
	    try {
		if (resultFile!=null)
		    writeOutputToFile(result);
		else if (callback != null) {
		    for (int i=0; i < result.length; i++) 
			callback.notify(new RemoteEvent(result[i], 0, 0, null));
		}
	    }catch (Exception uee) {
		uee.printStackTrace();
	    }
	}

	private void undoMagic() {
	  try {
	
	      AutonomicProvisioner auto = disco.getProvisioner();
	      if (auto!=null)
		  auto.undeploy("sorcer.core.Caller");
	  }catch (Exception forgetIt) {forgetIt.printStackTrace(); }
	}
	
	private void initiateAppendFileServer() {
	    try {
		//Upload the output file
		FileStorer fs = GridDispatcherProviderImpl.disco.getFileStorer();
		String folderName = resultFile.substring(0, resultFile.lastIndexOf("/"));
		String fileName   = resultFile.substring(resultFile.lastIndexOf("/")+1);
		appendDesc = new DocumentDescriptor();
		appendDesc.setPrincipal(getPrincipal());
		appendDesc.setFolderPath(folderName);
		appendDesc.setDocumentName(fileName);		
		File dummy = new File(fileName);
		dummy.createNewFile();
		appendDesc = fs.getOutputDescriptor(appendDesc);
		((OutputStreamProxy)appendDesc.out).write(dummy);

		//now prepare for appending to the file
		appendDesc = fs.getAppendDescriptor(appendDesc);
		outputURL = appendDesc.fileURL;
	    } catch (Exception e) { e.printStackTrace(); }
	}
	
	
	private void writeOutputToFile(String[] result) throws RemoteException, IOException {
	    try {
		for (int i=0; i<result.length; i++)
		    ((OutputStreamProxy)appendDesc.out).write(result[i].getBytes());
		((OutputStreamProxy)appendDesc.out).write("\n".getBytes());
	    } catch (RemoteException re) {
		re.printStackTrace();
		try {
		    //now prepare for appending to the file
		    FileStorer fs = disco.getFileStorer();
		    appendDesc = fs.getAppendDescriptor(appendDesc);
		    for (int i=0; i<result.length; i++)
			((OutputStreamProxy)appendDesc.out).write(result[i].getBytes());
		    ((OutputStreamProxy)appendDesc.out).write("\n".getBytes());
		}catch (Exception e) {
		    e.printStackTrace();
		    return;
		}
	    }
	}
	

	public void doFinally() {
	    if (resultFile != null) {
		try {
		    writeOutputToFile(new String[] { "_DONE_" } );
		    (appendDesc.out).close();
		} catch(IOException e) { e.printStackTrace(); }
	    }
	    else 
		try {
		    callback.notify(new RemoteEvent("_DONE_", 0, 0, null));
		}catch (Exception uee) {
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
	private Subject client;
	
	public JobsDispatcher(String inputFile, 
			      int jobSize, String notify,
			      DispatcherResult result) {
	    this.inputFile = inputFile;
	    this.jobSize = jobSize;
	    this.result = result;
	    this.notify = notify;
	    activeJobs = 0;
	}

	public JobsDispatcher(String[] inputValues, 
			      int jobSize, String notify,
			      DispatcherResult result) {
	    this.inputValues = inputValues;
	    this.jobSize = jobSize;
	    this.result = result;
	    this.notify = notify;
	    activeJobs = 0;
	}

	public JobsDispatcher(String inputFile, 
			      int jobSize, String notify,
			      DispatcherResult result, Subject client) {
	    this.inputFile = inputFile;
	    this.jobSize = jobSize;
	    this.result = result;
	    this.notify = notify;
	    this.client = client;
	    activeJobs = 0;
	}
	
	public JobsDispatcher(String[] inputValues, 
			      int jobSize, String notify,
			      DispatcherResult result, Subject client) {
	    this.inputValues = inputValues;
	    this.jobSize = jobSize;
	    this.result = result;
	    this.notify = notify;
	    this.client = client;
	    activeJobs = 0;
	}

	
	public void run() {

	    if (inputFile!=null)
		inputValues = (String[])parseInputFile();	    
	    
	    StringBuffer sb = new StringBuffer();
	    for (int i=0; i<inputValues.length; i++) {
		StringTokenizer st = new StringTokenizer(inputValues[i]);
		sb.delete(0, sb.length());				
		if (st.hasMoreTokens()) sb.append(" -k ").append(st.nextToken());
		if (st.hasMoreTokens()) sb.append(" -h ").append(st.nextToken());
		inputValues[i] = sb.toString();
	    }
	    
	    int fromIndex = 0;
	    int toIndex = 0;
	    do {
		toIndex = fromIndex + jobSize - 1;
		new JobDispatcher(inputValues, 
				  fromIndex, toIndex,
				  notify, this, client).start();
		fromIndex = toIndex+1;
		activeJobs++;
	    } while (fromIndex <= inputValues.length-1);	    
	}

	
	public String[] parseInputFile() {
	    Vector v = new Vector();
	    BufferedReader in = null;
	    
	    try {
		String fileName = downloadFile();
		in = new BufferedReader(new FileReader(fileName));
		String args;
		while ( (args=in.readLine()) != null )
		    v.add(args);
				
	    } catch (Exception e) {
		e.printStackTrace();
		return null;
	    } finally {
		try {  if (in!=null) in.close(); }catch(Exception e) { }
	    }
		    
	    return (String[])(v.toArray(new String[v.size()])) ;
	}
	
	private static SorcerPrincipal getPrincipal() {
	    SorcerPrincipal principal = new SorcerPrincipal();
	    principal.setId("183");
	    principal.setName("malladi");
	    principal.setRole("root");
	    principal.setAccessClass(4);
	    principal.setExportControl(false);
	    return principal;
	}
	
	
	//Downloads the file from file store and 
	//returns the name of the file.
	private String downloadFile() throws RemoteException, IOException {
	    
	    String folderName = inputFile.substring(0, inputFile.lastIndexOf("/"));
	    String fileName   = inputFile.substring(inputFile.lastIndexOf("/")+1);
	    System.out.println("Docnloading file path="+folderName+" fileName="+fileName);
	    DocumentDescriptor docDesc = new DocumentDescriptor();	    
	    docDesc.setPrincipal(getPrincipal());
	    docDesc.setFolderPath(folderName);
	    docDesc.setDocumentName(fileName);
	    
	    FileStorer fs = GridDispatcherProviderImpl.disco.getFileStorer();

	    try {
		docDesc = fs.getInputDescriptor(docDesc);
		((InputStreamAdapter)docDesc.in).read(new File(fileName));
	    }catch (InvalidTransactionException ite) {
		ite.printStackTrace();
		throw new RemoteException("InvalidTransactionFromServer: "+ ite.getMessage());
	    }
	    return fileName;	    
	}
	 
	
	public void done(Job resultJob) {
	    activeJobs--;
	    for (int i=0; i<resultJob.size(); i++) 
		result.done(((ServiceExertion)resultJob.exertionAt(i)).getContext());
	    
	    if (activeJobs==0)
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
		sdm = new ServiceDiscoveryManager(disco, new LeaseRenewalManager());
		lCache1 = sdm.createLookupCache(new ServiceTemplate(null, new Class[] { sorcer.service.Service.class} , null),
						null,  null);    
		lCache2 = sdm.createLookupCache(new ServiceTemplate(null, 
								    new Class[] { sorcer.core.provider.autonomic.provisioner.AutonomicProvisioner.class }, null), 
						null, null );
	    }catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	public Jobber getJobber() {
	    int tries = 0;
	    while (tries < MAX_TRIES) {
		ServiceItem[] items =  (lCache1.lookup(null, Integer.MAX_VALUE));
		for (int i=0; i<items.length; i++)
		    if ( items[i].service!=null &&
			 items[i].service instanceof Jobber ) {
			print(tries, items[i].service); 
			System.out.println("GOT JOBBER SERVICE - SERVICE ID="+items[i].serviceID);
			return (Jobber)items[i].service;
		    }
		tries++;
		try {Thread.sleep(SLEEP_TIME);}catch(Exception e) { }
	    }
	    print(tries, null);
	    return null;
	}
     
 	public AutonomicProvisioner getProvisioner() {
 	    int tries = 0;
 	    while (tries < MAX_TRIES) {
 		ServiceItem[] items =  (lCache2.lookup(null, Integer.MAX_VALUE));
 		for (int i=0; i<items.length; i++)
 		    if ( items[i].service!=null &&
 			 items[i].service instanceof AutonomicProvisioner ) {
 			print(tries, items[i].service); 
 			return (AutonomicProvisioner)items[i].service;}
 		tries++;
 		try {Thread.sleep(SLEEP_TIME);}catch(Exception e) { }
 	    }
 	    print(tries, null);
 	    return null;
 	}
	
	public FileStorer getFileStorer() {
	    int tries = 0;
	    while (tries < MAX_TRIES) {
		ServiceItem[] items =  (lCache1.lookup(null, Integer.MAX_VALUE));
		for (int i=0; i<items.length; i++)
		    if ( items[i].service!=null &&
			 items[i].service instanceof FileStorer ){ print(tries, items[i].service); return (FileStorer)items[i].service;}
		tries++;
		try {Thread.sleep(SLEEP_TIME);}catch(Exception e) { }
	    }
	    print(tries, null);
	    return null;
	}
	
	public void print(int i, Object obj) {
	    System.out.println("tries = "+i+" object="+obj);
	}
    }
    
    
    public static final class JobDispatcher extends Thread {
	
	private String[] inputValues;
	private int fromIndex;
	private int toIndex;
	private JobsDispatcher disp;
	private String notify;
	private Subject client;

	public JobDispatcher(String[] inputValues,
			     int fromIndex,
			     int toIndex,
			     String notify,
			     JobsDispatcher disp) {
	    
	    this.inputValues = inputValues;
	    this.fromIndex = fromIndex;
	    this.toIndex = toIndex;
	    this.notify = notify;
	    this.disp = disp;
	}

	public JobDispatcher(String[] inputValues,
			     int fromIndex,
			     int toIndex,
			     String notify,
			     JobsDispatcher disp, Subject client) {
	    
	    this.inputValues = inputValues;
	    this.fromIndex = fromIndex;
	    this.toIndex = toIndex;
	    this.notify = notify;
	    this.disp = disp;
	    this.client = client;
	}
	
	public void run() {
	    if (toIndex >= inputValues.length) 
		toIndex = inputValues.length-1;
	    
	    Job job = new Job("dispatcher"+fromIndex+"-"+toIndex);
	    Job result;
	    if(client!=null)
		//setPrincipal(job,((Principal)client.getPrincipals().iterator().next()));
		job.setPrincipal(new SorcerPrincipal(((Principal)client.getPrincipals().iterator().next()).getName()));;
	    //job.setPrincipal(new GAppPrincipal("((ABHIJIT))"));
	    job.setId(new UID().toString());
	    job.getContext().setExecTimeRequested(true);
	    job.getContext().isMonitorEnabled(false);
	    //There are access types - CATLAOG, SPACE and DIRECT
	    //There are Job Strategies - SEQUENTIAL, PARALLEL
	    //Not sure if CATLAOG and DIRECT work with PARALLEL, they should though
	    //job.getCC(). setJobStrategyAccess(job.getCC().SPACE);
	    job.getContext(). setAccessType(job.getContext().CATALOG);
	    //job.getCC(). setJobStrategy(job.getCC().PARALLEL);
	    job.getContext(). setFlowType(job.getContext().SEQUENTIAL);
	    //job.getCC(). setJobStrategy(job.getCC().DIRECT);
	    job.getContext().setNotifyList(notify);
	    
	    ServiceExertion task;
	    
	    for (int i=fromIndex; i<=toIndex; i++) {
		if (!"".equals(inputValues[i])) {
		    job.addExertion(task = getTask(inputValues[i], i));
		    job.getContext().setNotifyList(task, notify);
		    job.getContext().setExecTimeRequested(task, true);
		}
	    }
	    
	    try {
		System.out.println("-------------------calling disco.getJobber--------");
		Jobber jobber = disco.getJobber();
		System.out.println("Jobber Obtained --> "+jobber);
		System.out.println(">>>>>>>>>>>>>>>>>>.Sending job ="+job);
		Subject server = Subject.getSubject(AccessController.getContext());
		System.out.println(this+"Abhijit:::>>> Subject obtained before calling on jobber = "+server);		
		//***Abhijit - change this to more generic implementation - JUST for trial
		//System.out.println("Abhijit::>>>>> PREAPRING PROXY");
		//jobber = (Jobber)preparer.prepareProxy(jobber);
		//System.out.println("Abhijit::>>>>>--------- PREAPRING PROXY --------- DONE");
		//**The above code to be put as more generic implementation				

		if (job.size()!=0){
		    System.out.println("Inside job.size()!=0 statement");
		    if(client!=null){
			//System.out.println("Abhijit::>>----------- calling as a CLIENT Subject.doAs()------client = "+client);
			final Job finalJob = job;
			final Jobber finalJobber = jobber;		       
			result = (Job)jobber.service(job);
			/*
			result = (ServiceJob)Subject.doAs(client,
							  new PrivilegedExceptionAction() {
							      public Object run() throws Exception {
								  try{
								      //System.out.println("The present Subject is"+Subject.getSubject(AccessController.getContext()));
								      //AccessController.checkPermission(new MethodPermission(finalM.getName()));
								      return (ServiceJob)finalJobber.service(finalJob);
								  }catch (AccessControlException ex){
								      System.out.println("Exception Occured in Permission");
								      ex.printStackTrace();
								      throw new AccessControlException("Access Denied to the method");
								  }
							      }
							  });		    
			*/
			
		    }else{
			System.out.println("Abhijit::>>----------- calling WITHOUT a client subject ------");
			result = (Job)jobber.service(job);
		    }
		    System.out.println("--------result found --------+");
		    //else System.out.println("--------result = NULL");
		}
		else{
		    System.out.println("Inside the else of job.size!=0 statement");
		    result = job;
		}
		//Testing
		//result = job;
		disp.done(result);
	    }catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	public ServiceExertion getTask(String inputValue, int index) {
	    Context sc;
	    
	    try {
		sc = (Context)Util.clone(ctx);
	    }catch (Exception e) { e.printStackTrace(); return null; }
	    
	    CallerUtil.setArgs(sc, new String[]{inputValue});
	    CallerUtil.setOutputURL(sc, disp.getOutputURL());
	    
	    NetSignature method = new NetSignature("execute", 
						     "sorcer.core.Caller",
						     null);
						     
	    ServiceExertion task = new ServiceExertion("dispatcher-"+index, "Dispatcher Task-"+index,
					       new NetSignature[] {method});
	    task.setConditionalContext(sc);
	    if(client!=null)
		task.setPrincipal(new SorcerPrincipal(((Principal)client.getPrincipals().iterator().next()).getName()));;
		//setPrincipal(task,((Principal)client.getPrincipals().iterator().next()));
	    //task.setPrincipal(new GAppPrincipal("-----*ABHIJIT*-----"));
	    task.setId (new UID().toString() + task.getName());
	    return task;	    
	}
    }
		
}
