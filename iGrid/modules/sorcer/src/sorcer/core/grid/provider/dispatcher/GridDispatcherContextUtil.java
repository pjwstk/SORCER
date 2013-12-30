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

import net.jini.core.event.RemoteEventListener;
import sorcer.core.context.ServiceContext;
import sorcer.security.util.SorcerPrincipal;
import sorcer.service.Context;
import sorcer.service.ContextException;

public class GridDispatcherContextUtil {

	static SorcerPrincipal principal = new SorcerPrincipal();
	static {
		principal.setId("183");
		principal.setName("malladi");
		principal.setRole("root");
		principal.setAccessClass(4);
		principal.setExportControl(false);
	}

	static final String IN_VALUES = "dispatcher/input/values";
	static final String IN_FILE = "dispatcher/input/file";
	static final String JOB_SIZE = "dispatcher/input/jobsize";
	static final String CALLBACK = "dispatcher/input/callback";
	static final String NOTIFY = "dispatcher/input/notify";

	static final String OUT_FILE = "dispatcher/output/file";
	static final String EXCEPTION = "dispatcher/output/exception";

	static final int DEFAULT_JOB_SIZE = 5;

	public static String getOutputFile(Context ctx) throws ContextException {
		return (String) ctx.getValue(OUT_FILE);
	}

	public static void setOutputFile(Context ctx, String file)
			throws ContextException {
		ctx.putValue(OUT_FILE, file);
		((ServiceContext) ctx).selfModified();
	}

	public static RemoteEventListener getCallback(Context ctx)
			throws ContextException {
		return (RemoteEventListener) ctx.getValue(CALLBACK);
	}

	public static void setCallback(Context ctx, RemoteEventListener rel)
			throws ContextException {
		ctx.putValue(CALLBACK, rel);
		((ServiceContext) ctx).selfModified();
	}

	public static String getNotify(Context ctx) throws ContextException {
		return (String) ctx.getValue(NOTIFY);
	}

	public static void setNotify(Context ctx, String notify)
			throws ContextException {
		ctx.putValue(NOTIFY, notify);
		((ServiceContext) ctx).selfModified();
	}

	public static String getInputFile(Context ctx) throws ContextException {
		return (String) ctx.getValue(IN_FILE);
	}

	public static void setInputFile(Context ctx, String file)
			throws ContextException {
		ctx.putValue(IN_FILE, file);
		((ServiceContext) ctx).selfModified();
	}

	public static String[] getInputValues(Context ctx) throws ContextException {
		return (String[]) ctx.getValue(IN_VALUES);
	}

	public static void setInputValues(Context ctx, String[] values)
			throws ContextException {
		ctx.putValue(IN_VALUES, values);
		((ServiceContext) ctx).selfModified();
	}

	public static int getJobSize(Context ctx) throws ContextException {
		String jSize = (String) ctx.getValue(JOB_SIZE);

		try {
			return Integer.parseInt(jSize);
		} catch (Exception e) {
			return DEFAULT_JOB_SIZE;
		}
	}

	public static void setJobSize(Context ctx, String size)
			throws ContextException {
		ctx.putValue(JOB_SIZE, size);
		((ServiceContext) ctx).selfModified();
	}

	public static String getException(Context ctx) throws ContextException {
		return (String) ctx.getValue(EXCEPTION);

	}

	public static void setException(Context ctx, String values)
			throws ContextException {
		ctx.putValue(EXCEPTION, values);
		((ServiceContext) ctx).selfModified();
	}
}
