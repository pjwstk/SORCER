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

import java.net.URI;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import sorcer.core.SorcerConstants;
import sorcer.core.context.ServiceContext;
import sorcer.service.Context;
import sorcer.service.ContextException;

/**
 * Caller Utilities complement ServiceContext to be used with Caller providers
 * to manipulate thier data.
 * 
 * @author Ravi Malladi, Sekhar Soorianarayanan
 */

public class CallerUtil {

	final static String EXECTYPE = "caller/program/exec type";

	final static String ISOVERWRITE = "caller/program/exec type/isOverWrite";

	/** ***** PROGRAM + OS + BIN/LIB/LOAD_LIB/SRC/COMPILER*** */
	final static String PROGRAM = "caller/program/";

	final static String WINDOWS = "win/";

	final static String LINUX = "linux/";

	final static String SOLARIS = "solaris/";

	final static String JAVA = "java/";

	final static String BIN = "bin";

	final static String LIB = "lib";

	final static String CALL = "call";

	final static String LOAD_LIB = "load lib";

	final static String SRC = "src";

	final static String COMPILER = "src/compiler";

	/** **************************************************************** */

	final static String COMMAND = "caller/call/command";

	final static String ARG = "caller/call/arg";

	final static String DIR = "caller/call/dir";

	final static String ENVP = "caller/call/parameters/envp";

	final static String IN = "caller/call/parameters/in";

	final static String OUT = "caller/call/parameters/out";

	final static String CALL_OUTPUT = sorcer.core.SorcerConstants.OUT_VALUE
			+ "/call_output";

	final static String INFO = "caller/call/other info";

	final static String OUTPUT_URL = SorcerConstants.OUT_VALUE + "/output_URL";

	static final String INPUT_VECTOR = "caller/input/vector";

	static final String OUTPUT_VECTOR = "caller/output/vector";

	static final String INPUT_FILEMAP = "caller/input/filemap";

	static final String OUTPUT_FILEMAP = "caller/output/filemap";

	static final String USE_FS = "caller/usage/FS";

	// either WIN/LINUX/UNIX
	static String OS;

	public CallerUtil() {
		// do nothing
	}

	public static String getUseFS(Context ctx) {
		return (String) ((ServiceContext) ctx).get(USE_FS);
	}

	public static void setUseFS(Context ctx, boolean usefs) throws ContextException {
		if (usefs)
			ctx.putValue(USE_FS, "true");
		else
			ctx.putValue(USE_FS, "false");
	}

	public static Vector getInputVector(Context ctx) throws ContextException {
		return (Vector) ctx.getValue(INPUT_VECTOR);
	}

	public static void setInputVector(Context ctx, Vector inputVector) throws ContextException {
		ctx.putValue(INPUT_VECTOR, inputVector);
	}

	public static Hashtable getInputFileMap(Context ctx)
			throws ContextException {
		return (Hashtable) ctx.getValue(INPUT_FILEMAP);
	}

	public static void setInputFileMap(Context ctx,
			Hashtable inputFileMap) throws ContextException {
		ctx.putValue(INPUT_FILEMAP, inputFileMap);
	}

	public static Hashtable getOutputFileMap(Context ctx)
			throws ContextException {
		return (Hashtable) ctx.getValue(INPUT_FILEMAP);
	}

	public static void setOutputFileMap(Context ctx,
			Hashtable inputFileMap) throws ContextException {
		ctx.putValue(OUTPUT_FILEMAP, inputFileMap);
	}

	public static Vector getOutputVector(Context ctx) throws ContextException {
		return (Vector) ctx.getValue(OUTPUT_VECTOR);
	}

	public static void setOutputVector(Context ctx, Vector outputVector) throws ContextException {
		ctx.putValue(OUTPUT_VECTOR, outputVector);
	}

	static {
		OS = System.getProperty("os.name") + "/";
	}

	public static void setWindows() {
		OS = WINDOWS;
	}

	public static void setLinux() {
		OS = LINUX;
	}

	public static void setSolaris() {
		OS = SOLARIS;
	}

	public static void setJava() {
		OS = JAVA;
	}

	public static String getOS() {
		return (String) OS;
	}

	public static boolean isOverWrite(Context ctx) throws ContextException {
		if (ctx != null)
			return "true".equals(ctx.getValue(ISOVERWRITE));
		else
			return false;
	}

	public static void setIsOverWrite(Context ctx) throws ContextException {
		if (ctx != null)
			ctx.putValue(ISOVERWRITE, "true");
	}

	public static String[] getCallOutput(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(CALL_OUTPUT);

		return (String[]) null;
	}

	public static void setCallOutput(Context ctx, String[] output) {
		if (ctx != null) {
			if (output == null)
				ctx.remove(CALL_OUTPUT);
			else {
				// ctx.putValue(CALL_OUTPUT, output);
				try {
					ctx.putValue(CALL_OUTPUT, output);
				} catch (sorcer.service.ContextException ce) {
					ce.printStackTrace();
				}
			}
		}
	}

	public static String[] getInputs(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(IN);
		return (String[]) null;
	}

	public static void setInputs(Context ctx, String[] sorcerURIs) throws ContextException {
		if (ctx != null) {
			if (sorcerURIs == null)
				ctx.remove(IN);
			else
				ctx.putValue(IN, sorcerURIs);
		}
	}

	public static void setOutputs(Context ctx, String[] sorcerURIs) throws ContextException {
		if (ctx != null) {
			if (sorcerURIs == null)
				ctx.remove(OUT);
			else
				ctx.putValue(OUT, sorcerURIs);
		}
	}

	public static void setOutputURL(Context ctx, URL url) throws ContextException {
		if (url == null)
			ctx.remove(OUTPUT_URL);
		else
			ctx.putValue(OUTPUT_URL, url);
		try {
			ctx.mark(OUTPUT_URL, Context.CONTEXT_PARAMETER
					+ Context.DA_OUT + SorcerConstants.APS + SorcerConstants.APS);
		} catch (Exception e) {
		}
	}

	public static String[] getOutputs(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(OUT);

		return (String[]) null;
	}

	public static boolean isSrc(Context ctx) throws ContextException {
		if (ctx != null) {
			Object obj;
			obj = ctx.getValue(EXECTYPE);
			if ((obj != null) && (obj.equals("src")))
				return true;
		}

		return false;
	}

	public static void setSrc(Context ctx) throws ContextException {
		if (ctx != null) {
			ctx.putValue(EXECTYPE, "src");
		}
	}

	public static boolean isJava(Context ctx) throws ContextException {
		if (ctx != null) {
			Object obj;
			obj = ctx.getValue(EXECTYPE);

			if ((obj != null) && (obj.equals("bytecode"))) {
				// OS = JAVA;
				return true;
			}
		}

		return false;
	}

	public static void setJava(Context ctx) throws ContextException {
		if (ctx != null) {
			ctx.putValue(EXECTYPE, "bytecode");
		}
	}

	public static boolean isBin(Context ctx) throws ContextException {
		if (ctx != null) {
			Object obj;
			obj = ctx.getValue(EXECTYPE);

			if ((obj != null) && (obj.equals("bin")))
				return true;
		}

		return false;
	}

	public static void setBin(Context ctx) throws ContextException {
		if (ctx != null) {
			ctx.putValue(EXECTYPE, "bin");
		}
	}

	public static boolean isCall(Context ctx) throws ContextException {
		if (ctx != null) {
			Object obj;
			obj = ctx.getValue(EXECTYPE);

			if ((obj != null) && (obj.equals("call")))
				return true;
		}

		return false;
	}

	public static void setCall(Context ctx) throws ContextException {
		if (ctx != null) {
			ctx.putValue(EXECTYPE, "call");
		}
	}

	public static String getWorkingDir(Context ctx) throws ContextException {
		if (ctx != null) {
			Object obj;
			obj = ctx.getValue(DIR);

			if (obj != null)
				return obj.toString();
		}

		return null;
	}

	public static void setWorkingDir(Context ctx, String dir) throws ContextException {
		if (ctx != null) {
			if (dir == null)
				ctx.remove(DIR);
			else
				ctx.putValue(DIR, dir);
		}
	}

	public static String[] getArgs(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(ARG);

		return (String[]) null;
	}

	public static void setArgs(Context ctx, String[] arguments) throws ContextException {
		if (ctx != null) {
			if (arguments == null)
				ctx.remove(ARG);
			else
				ctx.putValue(ARG, arguments);
		}
	}

	public static String[] getCmds(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(COMMAND);

		return (String[]) null;
	}

	public static void setCmds(Context ctx, String[] commands) throws ContextException {
		if (ctx != null) {
			if (commands == null)
				ctx.remove(COMMAND);
			else
				ctx.putValue(COMMAND, commands);
		}
	}

	public static String[] getEnvp(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(ENVP);

		return (String[]) null;
	}

	public static void setEnvp(Context ctx, String[] envp) throws ContextException {
		if (ctx != null) {
			if (envp == null)
				ctx.remove(ENVP);
			else
				ctx.putValue(ENVP, envp);
		}
	}

	public static URI[] getSrcURIs(Context ctx) throws ContextException {
		if (ctx != null)
			return (URI[]) ctx.getValue(PROGRAM + OS + SRC);

		return (URI[]) null;
	}

	public static void setSrcURIs(Context ctx, URI[] srcURI) throws ContextException {
		if (ctx != null) {
			if (srcURI == null)
				ctx.remove(PROGRAM + OS + SRC);
			else
				ctx.putValue(PROGRAM + OS + SRC, srcURI);
		}
	}

	public static URI[] getBinURIs(Context ctx) throws ContextException {
		if (ctx != null)
			return (URI[]) ctx.getValue(PROGRAM + OS + BIN);

		return (URI[]) null;
	}

	public static void setBinURIs(Context ctx, URI[] binURI) throws ContextException {
		if (ctx != null) {
			if (binURI == null)
				ctx.remove(PROGRAM + OS + BIN);
			else
				ctx.putValue(PROGRAM + OS + BIN, binURI);
		}
	}

	public static URI[] getLibURIs(Context ctx) throws ContextException {
		if (ctx != null)
			return (URI[]) ctx.getValue(PROGRAM + OS + LIB);

		return (URI[]) null;
	}

	public static void setLibURIs(Context ctx, URI[] libURI) throws ContextException {
		if (ctx != null) {
			if (libURI == null)
				ctx.remove(PROGRAM + OS + LIB);
			else
				ctx.putValue(PROGRAM + OS + LIB, libURI);
		}
	}

	public static String getCompileCmd(Context ctx) throws ContextException {
		if (ctx != null)
				return (String) ctx.getValue(PROGRAM + OS + COMPILER);
		return (String) null;
	}

	public static void setCompileCmd(Context ctx, String cmd) throws ContextException {
		if (ctx != null) {
			if (cmd == null)
				ctx.remove(PROGRAM + OS + COMPILER);
			else
				ctx.putValue(PROGRAM + OS + COMPILER, cmd);
		}
	}

	public static String[] getLoadLib(Context ctx) throws ContextException {
		if (ctx != null)
			return (String[]) ctx.getValue(PROGRAM + OS + LOAD_LIB);
		
		return (String[]) null;
	}

	public static void setLoadLib(Context ctx, String loadLib[]) throws ContextException {
		if (ctx != null) {
			if (loadLib == null)
				ctx.remove(PROGRAM + OS + LOAD_LIB);
			else
				ctx.putValue(PROGRAM + OS + LOAD_LIB, loadLib);
		}
	}
}
