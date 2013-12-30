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

import net.jini.core.event.RemoteEventListener;
import sorcer.core.context.ServiceContext;
import sorcer.security.util.SorcerPrincipal;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class GridDispatcherContextUtil {
	static final String CALLBACK = "dispatcher/input/callback";

	public static RemoteEventListener getCallback(Context ctx)
			throws ContextException {
		return (RemoteEventListener) ctx.getValue(CALLBACK);
	}

	public static void setCallback(Context ctx, RemoteEventListener rel)
			throws ContextException {
		ctx.putValue(CALLBACK, rel);
	}

	static final String EXCEPTION = "dispatcher/output/exception";

	public static String getException(Context ctx) throws ContextException {
		return (String) ctx.getValue(EXCEPTION);
	}

	public static void setException(Context ctx, String values)
			throws ContextException {
		ctx.putValue(EXCEPTION, values);
	}

	static final String EXEC_COM = "caller/call/cmd";

	public static void setExecCom(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(EXEC_COM, temp);
	}

	static final String EXEC_TYPE = "";

	public static void setExecType(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(EXEC_TYPE, temp);
	}

	static final String HOST = "";

	public static void setHost(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(HOST, temp);
	}

	static final String IN_FILE = "dispatcher/input/file";

	public static String getInputFile(Context ctx) throws ContextException {
		return (String) ctx.getValue(IN_FILE);
	}

	public static void setInputFile(Context ctx, String file)
			throws ContextException {
		ctx.putValue(IN_FILE, file);
	}

	static final String IN_VALUES = "dispatcher/input/values";

	public static String[] getInputValues(Context ctx) throws ContextException {
		return (String[]) ctx.getValue(IN_VALUES);
	}

	public static void setInputValues(Context ctx, String[] values)
			throws ContextException {
		ctx.putValue(IN_VALUES, values);
	}

	static final String JOB_SIZE = "dispatcher/input/jobsize";

	static final int DEFAULT_JOB_SIZE = 5;

	public static int getJobSize(Context ctx) throws ContextException {
		String jSize;
		jSize = (String) ctx.getValue(JOB_SIZE);

		try {
			return Integer.parseInt(jSize);
		} catch (Exception e) {
			return DEFAULT_JOB_SIZE;
		}
	}

	public static void setJobSize(Context ctx, String size)
			throws ContextException {
		ctx.putValue(JOB_SIZE, size);
	}

	static final String L_BIN_PATH = "caller/program/linux/bin/folder";

	public static void setLinBinPath(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(L_BIN_PATH, temp);
	}

	static final String L_BIN_FILE = "caller/program/linux/bin/file";

	public static void setLinBinFile(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(L_BIN_FILE, temp);
	}

	static final String LOCATION = "";

	public static void setLocation(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(LOCATION, temp);
	}

	static final String NODE_NAME = "";

	public static void setNodeName(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(NODE_NAME, temp);
	}

	static final String NOTIFY = "dispatcher/input/notify";

	public static String getNotify(Context ctx) throws ContextException {
		return (String) ctx.getValue(NOTIFY);
	}

	public static void setNotify(Context ctx, String notify)
			throws ContextException {
		ctx.putValue(NOTIFY, notify);
	}

	static final String OUT_FILE = "dispatcher/output/file";

	public static String getOutputFile(Context ctx) throws ContextException {
		return (String) ctx.getValue(OUT_FILE);
	}

	public static void setOutputFile(Context ctx, String file)
			throws ContextException {
		ctx.putValue(OUT_FILE, file);
	}

	static SorcerPrincipal principal = new SorcerPrincipal();
	static {
		principal.setId("101");
		principal.setName("algerm");
		principal.setRole("root");
		principal.setAccessClass(4);
		principal.setExportControl(false);
	}

	public static void setProgramName(Context ctx, String ProgramName)
			throws ContextException {
		ctx.setName(ProgramName);
	}

	static final String S_BIN_PATH = "caller/program/solaris/bin/folder";

	public static void setSolBinPath(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(S_BIN_PATH, temp);
	}

	static final String S_BIN_FILE = "caller/program/solaris/bin/file";

	public static void setSolBinFile(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(S_BIN_FILE, temp);
	}

	static final String W_EXEC_FILE_TYPE = "";

	public static void setWinExecFileType(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(W_EXEC_FILE_TYPE, temp);
	}

	static final String W_BIN_PATH = "caller/program/win/bin/folder";

	public static void setWinBinPath(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(W_BIN_PATH, temp);
	}

	static final String W_BIN_FILE = "caller/program/win/bin/file";

	public static void setWinBinFile(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(W_BIN_FILE, temp);
	}

	static final String W_LIB_PATH = "caller/program/win/lib/folder";

	public static void setWinLibPath(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(W_LIB_PATH, temp);
	}

	static final String W_LIB_FILE = "caller/program/win/lib/file";

	public static void setWinLibFile(Context ctx, String temp)
			throws ContextException {
		ctx.putValue(W_LIB_FILE, temp);
	}
}