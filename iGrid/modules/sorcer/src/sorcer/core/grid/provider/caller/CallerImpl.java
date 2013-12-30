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

package sorcer.core.grid.provider.caller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URI;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.security.Policy;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import sorcer.core.FileStorer;
import sorcer.core.Policer;
import sorcer.core.SorcerConstants;
import sorcer.core.exertion.NetTask;
import sorcer.core.provider.ServiceProvider;
import sorcer.core.signature.NetSignature;
import sorcer.security.util.SorcerPrincipal;
import sorcer.service.Context;
import sorcer.service.ContextException;
import sorcer.service.SignatureException;
import sorcer.util.DocumentDescriptor;
import sorcer.util.ProviderAccessor;
import sorcer.util.ServerUtil;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;
import sorcer.util.rmi.InputStreamAdapter;

import com.sun.jini.start.LifeCycle;

public class CallerImpl extends ServiceProvider implements sorcer.core.Caller,
		SorcerConstants {

	private String[] commands;

	private String[] envps;

	private String[] arguments;

	private String[] inputs;

	private URI[] srcUri;

	private URI[] binUri;

	private URI[] libUri;

	private URI[] inUri;

	private File workingDir;

	private String[] loadLib;

	private String compileCmd;

	private FileStorer fs = null;

	private SorcerPrincipal principal1 = null;
	
	private Policy originalPolicy;
	private Policy newPolicy;

	static SorcerPrincipal principal = new SorcerPrincipal();
	static {
		principal.setId("101");
		principal.setName("algerm");
		principal.setRole("root");
		principal.setAccessClass(4);
		principal.setExportControl(false);
	}
	
	private final static Logger logger = Logger.getLogger(CallerImpl.class
			.getName());
	
	public CallerImpl() throws Exception {
		// do nothing
	}

	public CallerImpl(String[] args, LifeCycle lifeCycle) throws Exception {
		super(args, lifeCycle);
		
		originalPolicy = Policy.getPolicy();
		newPolicy = getPolicy();
		
		Policy.setPolicy(newPolicy);
		logger.info("NEW POLICY SET");
	}
	
	private Policy getPolicy() throws SignatureException {
		if (Sorcer.getProperty("sorcer.policer.mandatory").equals("true")) {
			//logger.info("Inside Caller constructor");
			Policer policer = (Policer) ProviderAccessor.getProvider(null, Policer.class);
			NetTask task = null;
			// specify method selector and service type
			NetSignature method;
			method = new NetSignature(
				Policer.POLICY, Policer.class);
			// specify task name, description and methods
			task = new NetTask(Policer.POLICY, Policer.POLICY
					+ " the policy object of a specific provider", method);
		
//			if (policer != null) {
//				try {
//					// assign service context for the task
//					PolicerContext context = new PolicerContext();
//					context.setProviderName(getProviderName());
//					task.setContext(context);
//					ServiceTask returnedTask = null;
//								
//					returnedTask = (ServiceTask) policer.service(task, null);
//					PolicerContext returnedContext = (PolicerContext) returnedTask.getContext();
//					final Policy newPolicy = returnedContext.getPolicyObject();
//					return newPolicy;
//					
//				} catch(Exception e) {
//					e.printStackTrace();
//				}
//			} else {
//				logger.info("No Policer in the network !!!");
//			}
		} else {
			logger.info("sorcer.policer.mandatory property in sorcer.env is false");
		}	
		return null;
	}

	private Hashtable processJavaArgs(Context context) {

		// fileMap contains the mapping of output URIs to downloaded local files
		// fileMap also contains the arguments for the command with key
		// "JavaArgs".
		Hashtable fileMap = new Hashtable();
		String[] params;
		try {
			params = SorcerUtil.tokenize(CallerUtil.getArgs(context)[0], " ");

			String workingDir = CallerUtil.getWorkingDir(context);
			String fsFolder, fsFile;
			Hashtable uriHT;

			boolean useFS;

			if ("true".equals(CallerUtil.getUseFS(context)))
				useFS = true;
			else
				useFS = false;

			Vector inputVector = CallerUtil.getInputVector(context);
			Hashtable inputFileMap = CallerUtil.getInputFileMap(context);

			StringBuffer sb = new StringBuffer(" ");
			String s = "";
			URI uri[] = new URI[(params.length - 1) / 2]; // since each URI is
			// identified with it's
			// previous param

			for (int i = 1, uriCount = 0; i < params.length; i++) {
				// System.out.println(" Params[" + i + "] contains ======> " +
				// params[i]);

				if (params[i].endsWith("-fin") || params[i].endsWith("-fout")) {
					s = params[i].substring(0, params[i].lastIndexOf("-"));
					if (s != null || s != "") {
						sb.append(s);
						sb.append(" ");
						i++;

						if (params[i - 1].endsWith("-fin"))
							if (useFS) {

								fsFolder = params[i].substring(0, params[i]
										.lastIndexOf("/"));
								fsFile = params[i].substring(params[i]
										.lastIndexOf("/"));
								uriHT = new Hashtable();
								uriHT.put("folder", fsFolder);
								uriHT.put("file", fsFile);
								uri[uriCount] = SorcerUtil.getURI(
										"sorcer.core.FileStorer",
										getProperty("*"), uriHT);
								// uri[uriCount] = new URI(params[i]);
								// System.out.println(" URI array contains
								// ======> " + uri[uriCount]);
							} else {

								String fname = params[i];
								int fIndex = ((Integer) inputFileMap.get(fname))
										.intValue();
								Vector fVector = (Vector) inputVector
										.elementAt(fIndex);
								writeVectorToFile(fVector, uriCount);
							}
						sb.append("file").append(uriCount).append(" ");

						if (params[i].endsWith("-fout"))
							fileMap.put("file"
									+ (new Integer(uriCount)).toString(),
									params[i]);

						uriCount++;
					}
				} else
					sb.append(params[i]).append(" ");
			}

			fileMap.put("JavaArgs", sb.toString());

			if (useFS) {
				if (uri != null && uri.length > 0) {
					// System.out.println(" URI array lenght
					// =====================>"
					// + uri.length);

					for (int i = 0; i < uri.length; i++)
						System.out.println(" URI array contains ======> "
								+ uri[i]);

					downloadFiles(uri, workingDir);
				}
			}
		} catch (ContextException e1) {
			e1.printStackTrace();
		}
		return fileMap;
	}

	private void writeVectorToFile(Vector input, int fileIndex) {
		try {
			FileOutputStream fos = new FileOutputStream("file" + fileIndex);
			PrintStream ps = new PrintStream(fos);
			int i = 0;

			for (i = 0; i < input.size(); i++)
				ps.println(input.elementAt(i));
		}

		catch (FileNotFoundException fe) {
			System.out.println("Output file not found");
		}

		catch (IOException ioe) {
			System.out.println("IO exception");
		}
	}

	public Context execute(Context context) {
		//logger.info("Current policy: " + Policy.getPolicy());
		
		String[] result = null;

		try {
			getInputs(context);
			if (CallerUtil.isJava(context)) {
				// Processing for java command
				logger.info("Caller recieved java call for processing............");
				//arguments = CallerUtil.getArgs(context);
				String className = SorcerUtil.tokenize(arguments[0], " ")[0];
				logger.info("Arguments[0] ================> "
						+ arguments[0]);
				logger.info("className ===============> " + className);
				Hashtable outputFileMap = processJavaArgs(context);
				String args[] = SorcerUtil.tokenize((String) outputFileMap
						.get("JavaArgs"), " ");
				;
				// args[] =
				// Util.getTokens((String)outputFileMap.get("JavaArgs"), " ");
				logger.info("The args in JavaArgs from HT are : ==========> "
								+ (String) outputFileMap.get("JavaArgs"));
				logger.info("The args in JavaArgs from args[0] are : ==========> "
								+ args);
				outputFileMap.remove("JavaArgs");
				// logic for getting each argument to be passed to the java
				// class
				// arg[0] is the java class to be invoked
				// if an arg ends with -fin or -fout,
				// then the next argument represents a file in FileStore.
				// The file needs to be downloaded and saved in the local dir.
				// A mapping needs to be maintained for the files.
				// The args need to be modified to remove the '-fin' and '-fout'
				// The java class is invoked in a new thread in the same JVM and
				// the modified args are passed to it.

				// ------------------ How to get all
				// Inputs................????????????????????????

				ExecuteJava executeJava = new ExecuteJava(className, args,
						outputFileMap);
				Thread javaThread = new Thread(executeJava);
				javaThread.start();

				while (!executeJava.done)
					;

				result = setJavaResults(executeJava.outputFileName);
				CallerUtil.setOutputVector(context, executeJava.outputVector);
				CallerUtil.setOutputFileMap(context, executeJava.fileMap);

			} if (Sorcer.getProperty("sorcer.policer.mandatory").equals("true")) {
				// Added by Daniela Inclezan
				logger.info("ARGS: "+ arguments + "commands[0]:" + commands[0]);
				logger.info("******CONTEXT: " + context);
				
				// Processing for system command
				result = new String[1];
				
				String os = CallerUtil.getOS();

				/***************************************************************
				 * logic for executing the commands
				 **************************************************************/
				
				Runtime runtime = Runtime.getRuntime();

				if (libUri != null) {
					String str = CallerUtil.getWorkingDir(context);

					if ((str == null) || (" ".equals(str)))
						str = ".";

					for (int index = 0; index < libUri.length; index++) {
						Hashtable ht = ServerUtil
								.getParameters(libUri[index]);
						String filename = (String) ht.get("file");
						String folder = (String) ht.get("folder");
					}
				}

				// check if linux or solaris you have ./<cmd>
				// ------------------------------------FOR
				// JAVA----------------------------
				if ("java/".equalsIgnoreCase(os)) {

				}
				// ------------------------------------------------------------------------
				else {
					String comd = CallerUtil.getCmds(context)[0];
					logger.info("~~~~~~~~~~~CallerUtil cmds: " + CallerUtil.getCmds(context)[0]);
					String cmd;
					if ((("linux/".equalsIgnoreCase(os)) || ("solaris/"
							.equalsIgnoreCase(os)))
							&& (!(comd.startsWith("./"))))
						cmd = "./" + comd.toString();
					else
						cmd = comd.toString();
				
					System.out.println(">>>>>>>>>>> CallerImpl::execute() Before exec() cmd = "
									+ cmd + "\n>>>>>>>> os = " + os);
					
					try {						
						//added by Daniela Inclezan
						String argum = CallerUtil.getArgs(context)[0];
						
						String ins = cmd + " " + argum;
						String[] input = ins.split("\\s");
						
						//String cmdstr[] = {cmd, "-k", arguments[i], "-h", arguments[i+1]};
						workingDir = new File("/home/grad2/dincleza/iGrid/modules/sorcer/src/sorcer/core/grid/provider/caller/");
						Process process = runtime.exec(input, envps, workingDir);
						int status = process.waitFor();

						DataInputStream in = new DataInputStream(process
								.getInputStream());

						String line = null;
						StringBuffer str = new StringBuffer();

						while ((line = in.readLine()) != null) {
							str.append(line + "\n");
						}

						if (str != null)
							result[0] = str.toString();									
					} catch (AccessControlException ex) {
						logger.severe("!!!!!!!!!!ACCESS DENIED!!!!!!!!!!!! " + ex);
						result[0] = "ACCESS DENIED !!!!";
					}
				}
					
				
			} else {
				// Processing for system command
				result = new String[commands.length];
				String cmd[] = new String[commands.length];
				String os = CallerUtil.getOS();

				/***************************************************************
				 * logic for executing the commands
				 **************************************************************/

				for (int i = 0; i < commands.length; i++) {

					// PrintWriter execLog = new PrintWriter (new
					// FileOutputStream( commands[i] + ".log"));

					StringBuffer command = new StringBuffer(commands[i]);

					if (arguments != null)
						for (int index = 0; index < arguments.length; index++)
							command.append(" " + arguments[index]);

					Runtime runtime = Runtime.getRuntime();

					if (libUri != null) {
						String str = CallerUtil.getWorkingDir(context);

						if ((str == null) || (" ".equals(str)))
							str = ".";

						for (int index = 0; index < libUri.length; index++) {
							Hashtable ht = ServerUtil
									.getParameters(libUri[index]);
							String filename = (String) ht.get("file");
							String folder = (String) ht.get("folder");

							// runtime.load(str + File.separator + folder +
							// File.separator + filename);
						}
					}

					if (loadLib != null) {
						for (int index = 0; index < loadLib.length; index++) {
							// runtime.loadLibrary(loadLib[i]);
						}
					}

					// check if linux or solaris you have ./<cmd>
					// ------------------------------------FOR
					// JAVA----------------------------
					if ("java/".equalsIgnoreCase(os)) {

					}
					// ------------------------------------------------------------------------
					else {
						if ((("linux/".equalsIgnoreCase(os)) || ("solaris/"
								.equalsIgnoreCase(os)))
								&& (!(commands[i].startsWith("./"))))
							cmd[i] = "./" + command.toString();
						else
							cmd[i] = command.toString();
					
						System.out
								.println(">>>>>>>>>>> CallerImpl::execute() Before exec() cmd = "
										+ cmd[i] + "\n>>>>>>>> os = " + os);
						
						try {						
							//added by Daniela Inclezan - TODO: remove hardcoded parts
							logger.info("envps: " + envps + " workingDir: " + workingDir);
							String cmd1[] = {"proth2", "-k", "6", "-h", "3"};
							workingDir = new File("/home/grad2/dincleza/iGrid/modules/sorcer/src/sorcer/core/grid/provider/caller/");
							logger.info("Modified: envps: " + envps + " workingDir: " + workingDir);
							Process process = runtime.exec(cmd1, envps, workingDir);
							//////////////
						
						
							//Process process = runtime.exec(cmd[i], envps,
							//		workingDir);

							int status = process.waitFor();

							DataInputStream in = new DataInputStream(process
									.getInputStream());

							String line = null;
							StringBuffer str = new StringBuffer();

							while ((line = in.readLine()) != null) {
								str.append(line + "\n");
								// execLog.println(line);
								// execLog.flush();
							}

							//logger.info("str: " + str);
							if (str != null)
								result[i] = str.toString();
												
							// execLog.close();
						} catch (AccessControlException ex) {
							logger.severe("!!!!!!!!!!ACCESS DENIED!!!!!!!!!!!! " + ex);
							result[0] = "ACCESS DENIED !!!!";
						}
					}
				}
			}
			//logger.info("Final result[0]: " + result[0]);
			updateContext(context, result);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		catch (sorcer.service.ContextException ce) {
			ce.printStackTrace();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		} catch (AccessControlException ex) {
			//Policy.setPolicy(originalPolicy);
			logger.severe("!!!!!!!!!!ACCESS DENIED!!!!!!!!!!!! " + ex);
			String[] res = {"ACCESS DENIED !!!!"};
			//logger.info("Final res[0]: " + res[0]);
			CallerUtil.setCallOutput(context, res);
		}

		//Policy.setPolicy(originalPolicy);
		return context;
	}

	private String[] setJavaResults(String outputFile) {
		String lineOutput = "";
		StringBuffer output = new StringBuffer(" ");
		;
		String[] result = new String[1];

		try {
			FileReader fr = new FileReader(outputFile);
			BufferedReader br = new BufferedReader(fr);

			while ((lineOutput = br.readLine()) != null)
				output.append(lineOutput).append("\n");

			fr.close();
		}

		catch (FileNotFoundException fe) {
			System.out.println("Output file not found");
		}

		catch (IOException ioe) {
			System.out.println("IO exception");
		}

		result[0] = output.toString();

		return result;
		// updateContext(context,result);
	}

	private void getInputs(Context context) throws RemoteException,
			ContextException {

		// three cases: Here we need to check only for src
		// or bin as call is mandatory i.e you can specify
		// 1. src and call
		// 2. bin and call
		// 3. call

		// System.out.println("\n*** in getInputs(context)... ***\n");
		// System.out.println("\n*** printing context... ***\n" + context +
		// "\n");
		if (CallerUtil.isJava(context)) {
			CallerUtil.setJava();
			getJavaInputs(context);
			return;
		}

		String filename = null;

		/*
		 * if (context.getPrincipal() != null) { principal1 =
		 * context.getPrincipal(); } else {
		 * System.out.println("++++++++++++++++++++++ NULL PRINCIPAL
		 * ++++++++++++++++++++"); }
		 */

		if ("linux".equalsIgnoreCase(System.getProperty("os.name"))) {
			System.out.println(">>>>>>>>>>>>>Linux");
			CallerUtil.setLinux();
		}

		else {
			if ("SunOS".equalsIgnoreCase(System.getProperty("os.name"))) {
				System.out.println(">>>>>>>>>>>>>Solaris");
				CallerUtil.setSolaris();
			} else {
				System.out.println(">>>>>>>>>>>>>Windows");
				CallerUtil.setWindows();
			}
		}

		if (CallerUtil.isSrc(context)) {
			// System.out.println("\n*** isSrc... ***\n");
			srcUri = CallerUtil.getSrcURIs(context);

			if (srcUri != null)
				downloadFiles(context, srcUri);

			libUri = CallerUtil.getLibURIs(context);

			if (libUri != null)
				downloadFiles(context, libUri);

			loadLib = CallerUtil.getLoadLib(context);

			compileCmd = CallerUtil.getCompileCmd(context);

			compileSources(context, srcUri);
		}

		else { // it is bin or call
			// System.out.println("\n*** isBin... ***\n");
			if (CallerUtil.isBin(context)) {
				binUri = CallerUtil.getBinURIs(context);

				if (binUri != null) {
					System.out.println("downloading bin files");
					downloadFiles(context, binUri);
					//logger.info("after downloading");
					
					// System.out.println("*** got the file! ***\n");

					// if it is linux or unix, make the bin file executable
					// and also convert dos file to unix/linux format.
					if (("linux"
							.equalsIgnoreCase(System.getProperty("os.name")))
							|| ("SunOS".equalsIgnoreCase(System
									.getProperty("os.name")))) {
						try {

							System.out.println("\n*** OS is linux/unix ***\n");

							String str = CallerUtil.getWorkingDir(context);
							if ((str == null) || (" ".equals(str)))
								str = ".";

							for (int index = 0; index < binUri.length; index++) {
								Hashtable ht = ServerUtil
										.getParameters(binUri[index]);
								filename = (String) ht.get("file");
								// Permisison.......=============");
								Runtime runtime = Runtime.getRuntime();

								if (("SunOS".equalsIgnoreCase(System
										.getProperty("os.name"))))
									runtime.exec("dos2unix " + str
											+ File.separator + filename + " "
											+ str + File.separator + filename);

								// System.out.println("\n*** chaning permission
								// on file: " + filename + " ***\n");
								Process process = runtime.exec("chmod 775 "
										+ str + File.separator + filename);

								int status = process.waitFor();

								// ***Abhijit; At present this is written as if
								// principal!=null so tht the developers who are
								// not using security are able to work smoothly
								// , however later this needs to be taken off
								// and if the principal is null, the call shall
								// fail
								/*
								 * if (principal != null) { Subject client = new
								 * Subject(); final String finalFile = filename;
								 * client.getPrincipals().add(new
								 * X500Principal(principal1.getName())); try {
								 * Subject.doAs(client, new
								 * PrivilegedExceptionAction(){ public Object
								 * run() throws Exception { try{ //Just check
								 * the permission, dont do Subject.doAs()
								 * AccessController.checkPermission(new
								 * FilePermission("./"+finalFile, "execute"));
								 * return null; } catch (AccessControlException
								 * ex){ System.out.println("Exception Occured in
								 * File Permission"); ex.printStackTrace();
								 * throw new RemoteException("Execute Access
								 * Denied to the Subject"); } //catch
								 * (PrivilegedActionException ex){ // //} } }); }
								 * catch (PrivilegedActionException ex) {
								 * Util.debug(this, "PrivilegedActionException
								 * in Caller"); ex.printStackTrace(); }
								 * 
								 * //catch(Exception ex){ //Util.debug(this,
								 * "Exception in Caller");
								 * //ex.printStackTrace();
								 * //System.out.println("Message3 =
								 * "+ex.getMessage()); //String[] string =
								 * {ex.getMessage()}; //updateContext(context,
								 * string); //System.out.println("Context
								 * updated to = " + context); //return; //} }
								 */

							}
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}

						catch (java.lang.InterruptedException ie) {
							ie.printStackTrace();
						}
					}
				}

				libUri = CallerUtil.getLibURIs(context);

				if (libUri != null)
					downloadFiles(context, libUri);

				loadLib = CallerUtil.getLoadLib(context);
			}
		}
		// *** Abhijit:: May be the execue permission of the file must also be
		// checked here before the integrity is checked

		// System.out.print("\n*** Checking filename ... ***\n");
		if (filename == null) {
			System.out.println("File not yet initialized");
			Hashtable ht = ServerUtil.getParameters(binUri[0]);
			filename = (String) ht.get("file");
			System.out.println("filename now = " + filename);
		}

		/* commented by Daniela Inclezan because different MD5 were computed every time
		try {
			String hash2 = SecurityUtil.computeMD5(filename);
			String hash1 = "bc16aa201586867d5c49fe37042eab6";
						
			System.out
					.println("******* ------ Checking Integrity ------ *******"
							+ hash2);
			SecurityUtil.isModified(hash1, hash2);
		}

		catch (WrongMessageDigestException ex) {
			System.out.println("Exception Occured in File Integrity");
			ex.printStackTrace();
			System.out.println("Message2 = " + ex.getMessage());
			String[] string = { ex.getMessage() };
			updateContext(context, string);
			throw new RemoteException("Problem with File Integrity");
		}

		catch (java.security.NoSuchAlgorithmException nsae) {
			System.out.println("Exception Occured in File Integrity");
			nsae.printStackTrace();
			System.out.println("Message2 = " + nsae.getMessage());
			String[] string = { nsae.getMessage() };
			updateContext(context, string);
			throw new RemoteException("Problem with Hash Algorithm");
			// return;
		} catch (NullPointerException npe) {
			System.out.println("Exception Occured in File Integrity");
			npe.printStackTrace();
			System.out.println("Message2 = " + npe.getMessage());
			String[] string = { npe.getMessage() };
			updateContext(context, string);
			throw new RemoteException("Problem with computeDigest");
			// return;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("Problem with computeDigest");
		}
		*/

		commands = CallerUtil.getCmds(context);
		
		// logging info added by Daniela Inclezan
		//logger.info("Commands as passed in the context: ");
		for (int i=0; i<commands.length; i++) {
			//logger.info(commands[i] + " ");
		}
		
		
		if (commands == null)
			throw new ContextException("No command Provided");
		
		String str = CallerUtil.getWorkingDir(context);
		//logger.info("Working dir string: " + str);

		if (str != null)
			workingDir = getWorkingDir(str);
		else
			workingDir = null;

		envps = CallerUtil.getEnvp(context);
		arguments = CallerUtil.getArgs(context);
		// use of inputs is not known so far, if it is set read it.
		// not used anywhere in code.
		inputs = CallerUtil.getInputs(context);

	}

	private void getJavaInputs(Context context) throws RemoteException {
		try {
			arguments = SorcerUtil.tokenize((CallerUtil.getArgs(context))[0], " ");

			inputs = CallerUtil.getInputs(context);
		} catch (ContextException e) {
			e.printStackTrace();
		}
		// add code to check if some of the arguments are files.
		// if args are files they need to be downloaded.
		return;
	}

	private void updateContext(Context context, String[] result)
			throws RemoteException {
		try {
			// append the machine name, time taken to execute
			InetAddress inetAddress = InetAddress.getLocalHost();
			String hostName = inetAddress.getHostName();

			CallerUtil.setCallOutput(context, result);
			System.out.println(context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException("execute method failed", e);
		}
	}

	public String getProviderName() {
		return "SORCER Caller";
	}

	public boolean isValidTask() {
		return true;
	}

	private File getWorkingDir(String dir) {
		String operatingSystem = CallerUtil.OS;

		if (("linux".equalsIgnoreCase(operatingSystem))
				|| ("SunOS".equalsIgnoreCase(operatingSystem)))
			dir = validatePath("\\", dir);
		else
			dir = validatePath("/", dir);

		return new File(dir);
	}

	private String validatePath(String searchString, String dir) {
		return dir;
	}

	private boolean isDownloadRequired(Context ctx, URI uri) {
		String filename = null, path = null;
		try {
			if (CallerUtil.isOverWrite(ctx))
				return true;

			Hashtable ht = ServerUtil.getParameters(uri);
			String folder = (String) ht.get("folder");
			filename = (String) ht.get("file");
			path = CallerUtil.getWorkingDir(ctx);

			if (path == null)
				path = ".";

		} catch (ContextException e) {
			e.printStackTrace();
		}
		
		if ((new File(path + File.separator + filename)).exists()) {
			return false;
		} else {
			return true;
		}
	}

	private void downloadFiles(Context context, URI[] uri) {
		DocumentDescriptor docDesc = null;
		Class type = null;

		try {
			// System.out.println("\n*** in downloadFiles(ServiceContext,
			// uri)... ***\n");
			for (int i = 0; i < uri.length; i++) {
				// check whether download is required
				if (!isDownloadRequired(context, uri[i]))
					continue;
				
				//logger.info("after checking that download is required");

//				if (fs == null) {
//					try {
//						type = Sorcer.getProperty("filestore.type");
//					}
//					catch (Exception e) {
//						//ignore the exception
//					}
//					
//					if (type != null) {
//						fs = (FileStorer) ProviderAccessor.getProvider(null, type);
//					}
//					else {
//						fs = (FileStorer) ProviderAccessor.getProvider(null, DocumentFileStorer.class);
//						
//					}		
//				}

				docDesc = new DocumentDescriptor();
				docDesc.setPrincipal(principal);

//				GAppACL acl = new GAppACL(principal, "ACL Protected");
//				try {
//					acl.addGroupPermissions(principal, "group1", new String[] {
//							GAppACL.VIEW, GAppACL.ADD }, true);
//				} catch (Exception e) {
//				}
//
//				docDesc.setACL(acl);

				// get the folder name and filename
				Hashtable ht = ServerUtil.getParameters(uri[i]);

				docDesc.setFolderPath((String) ht.get("folder"));
				String filename = (String) ht.get("file");

				docDesc.setDocumentName(filename);

				docDesc = fs.getInputDescriptor(docDesc);

				String path = CallerUtil.getWorkingDir(context);

				if (path == null)
					path = ".";

				FileOutputStream fos = new FileOutputStream(new File(path + File.separator + filename));
				byte[] buffer = new byte[8192];
				int count = -1;
				
				try {
					do {
						count = docDesc.in.read(buffer);
						if (count > 0)
						fos.write(buffer, 0, count);
					}
					while(count > -1);
				}
				catch (EOFException e) {
					//TODO fix java.io.EOFException
				}
				
				fos.close();
				docDesc.in.close();
				
				//((InputStream) docDesc.in).read(new File(path + File.separator + filename));
			}
		} catch (java.rmi.RemoteException re) {
			re.printStackTrace();
		} catch (java.io.IOException ie) {
			ie.printStackTrace();
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}

	private void downloadFiles(URI[] uri, String workingDir) {
		DocumentDescriptor docDesc = null;
		StringBuffer localFile;
		String type = null;

		try {
			for (int i = 0; i < uri.length; i++) {
//				if (fs == null) {
//					try {
//						type = Sorcer.getProperty("filestore.type");
//					}
//					catch (Exception e) {
//						//ignore the exception
//					}
//					
//					if (type != null) {
//						fs = (FileStorer) ProviderAccessor.getProvider(null, type);
//					}
//					else {
//						fs = (FileStorer) ProviderAccessor.getProvider(null, DocumentFileStorer.class.getName());
//						
//					}					
//				}

				localFile = new StringBuffer("file");

				docDesc = new DocumentDescriptor();
				docDesc.setPrincipal(principal);

//				GAppACL acl = new GAppACL(principal, "ACL Protected");
//				try {
//					acl.addGroupPermissions(principal, "group1", new String[] {
//							GAppACL.VIEW, GAppACL.ADD }, true);
//				} catch (Exception e) {
//				}

//				docDesc.setACL(acl);

				// get the folder name and filename
				Hashtable ht = ServerUtil.getParameters(uri[i]);

				docDesc.setFolderPath((String) ht.get("folder"));
				String filename = (String) ht.get("file");

				docDesc.setDocumentName(filename);

				docDesc = fs.getInputDescriptor(docDesc);

				if (workingDir == null)
					workingDir = ".";

				((InputStreamAdapter) docDesc.in).read(new File(workingDir
						+ File.separator + localFile.append(i)));
			}
		} catch (java.rmi.RemoteException re) {
			re.printStackTrace();
		} catch (java.io.IOException ie) {
			ie.printStackTrace();
		} 
	}

	private void compileSources(Context context, URI[] srcUri) {
		try {
			if ((srcUri != null) && (compileCmd != null)) {
				for (int i = 0; i < srcUri.length; i++) {
					Hashtable ht = ServerUtil.getParameters(srcUri[i]);

					String filename = (String) ht.get("file");
					String folder = (String) ht.get("folder");

					String path;

					path = CallerUtil.getWorkingDir(context);

					if (path == null)
						path = ".";

					Runtime runtime = Runtime.getRuntime();

					if (libUri != null) {
						String str = CallerUtil.getWorkingDir(context);
						if ((str == null) || (" ".equals(str)))
							str = ".";

						for (int index = 0; index < libUri.length; index++) {
							Hashtable hashTable = ServerUtil
									.getParameters(libUri[index]);
							String file = (String) hashTable.get("file");
							String folderName = (String) hashTable
									.get("folder");

							runtime.load(str + File.separator + folder
									+ File.separator + filename);
						}
					}

					if (loadLib != null) {
						for (int index = 0; index < loadLib.length; index++) {
							runtime.loadLibrary(loadLib[i]);
						}
					}

					Process process = runtime.exec(compileCmd + " " + path
							+ folder + filename);
				}
			}
		}

		catch (java.io.IOException ioe) {
			ioe.printStackTrace();
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
}

class ExecuteJava implements Runnable {
	String mainClass;

	String args[];

	Hashtable outputFileMap;

	public boolean done = false;

	// public String outputFileName = "Output.log" + (new
	// java.util.Date()).toString();
	public String outputFileName = "Output.log";

	public Vector outputVector = new Vector(1, 1);

	public Hashtable fileMap = new Hashtable();

	public ExecuteJava(String mainClass, String[] args, Hashtable outputFileMap) {
		this.mainClass = mainClass;
		this.args = args;
		this.outputFileMap = outputFileMap;
	}

	public void start() {

	}

	private Vector parseInputFile(String fileName) {
		Vector v = new Vector(1, 1);
		BufferedReader in = null;
		System.out.println(" Reading the local file ");
		try {
			in = new BufferedReader(new FileReader(fileName));
			String args;

			while ((args = in.readLine()) != null) {
				System.out.println(" Read in Line:: " + args);
				v.addElement(args);
			}
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
		return v;
	}

	public void run() {
		Class[] argTypes = new Class[1];
		argTypes[0] = String[].class;

		PrintStream ps = null;
		PrintStream op = null;
		PrintStream err = null;
		FileOutputStream fos = null;
		FileReader fr = null;
		// ----------------------------------------------------------------------------------------
		// ----------------------------------------------------------------------------------------

		// Saving the original Output PrintStream
		op = System.out;
		err = System.err;

		// Creating the output log file
		try {
			fos = new FileOutputStream(outputFileName);
			ps = new PrintStream(fos);
		} catch (Exception e1) {
			System.out.println("Logging file not created!");
		}
		// Setting the System Output to the log file
		System.setOut(ps);
		System.setErr(ps);

		try {
			java.lang.reflect.Method mainMethod = Class.forName(mainClass)
					.getDeclaredMethod("main", argTypes);
			Object[] argListForInvokedMain = new Object[1];
			String arguments[] = new String[1];
			argListForInvokedMain[0] = args;
			// Place whatever args you want to pass into other class's main
			// here.
			mainMethod.invoke(null,
			// This is the instance on which you invoke
					// the method; since main is static, you can pass null in.
					argListForInvokedMain);

			Vector opkeys = SorcerUtil.getKeys(outputFileMap);
			// Vector localFile = new Vector(1,1);;
			for (int j = 0; j < opkeys.size(); j++) {
				outputVector.add(parseInputFile((String) opkeys.elementAt(j)));
				fileMap.put(outputFileMap.get((String) opkeys.elementAt(j)),
						new Integer(j));
			}

		} catch (ClassNotFoundException ex) {
			System.out.println("Class " + mainClass
					+ " not found in classpath.");
		} catch (NoSuchMethodException ex) {
			System.out.println("Class " + mainClass
					+ " does not define public static void main(String[])");
		} catch (InvocationTargetException ex) {
			System.out.println("Exception while executing " + mainClass + ":"
					+ ex.getTargetException());
		} catch (IllegalAccessException ex) {
			System.out.println("main(String[]) in class " + mainClass
					+ " is not public");
		} finally {

			try {
				fos.flush();
				fos.close();
			}

			catch (Exception e2) {
				System.out.println("Error closing log file!");
			}

			finally {
				System.setOut(op);
				System.setErr(err);
				done = true;
			}
		}
	}
}
