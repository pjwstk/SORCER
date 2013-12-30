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

package sorcer.space.requestor;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ServiceJob;
import sorcer.core.exertion.ServiceTask;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Sorcer;
import sorcer.util.Log;

public class WorkerJobRequestor {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		System.setSecurityManager(new RMISecurityManager());
		// initialize system properties
		Sorcer.getEnvProperties();
		
		// get the queried provider name from the command line
		String pn1 = args[0];
		String pn2 = args[1];
		String pn3 = args[2];
		
		logger.info("Provider name1: " + pn1);
		logger.info("Provider name2: " + pn2);
		logger.info("Provider name3: " + pn3);

		Exertion result = new WorkerJobRequestor()
			.getExertion(pn1, pn2, pn3).exert(null);
		logger.info("Output context1: \n" + ((Job)result).exertionAt(0).getContext());
		logger.info("Output context2: \n" + ((Job)result).exertionAt(1).getContext());
		logger.info("Output context3: \n" + ((Job)result).exertionAt(2).getContext());
	}

	private Exertion getExertion(String pn1, String pn2, String pn3) throws Exception {
		String hostname = InetAddress.getLocalHost().getHostName();

		Context context1 = new ServiceContext("work1");
		context1.putValue("requstor/name", hostname);
		context1.putValue("requestor/operand/1", 1);
		context1.putValue("requestor/operand/2", 1);
		context1.putValue("to/provider/name", pn1);
		
		Context context2 = new ServiceContext("work2");
		context2.putValue("requstor/name", hostname);
		context2.putValue("requestor/operand/1", 2);
		context2.putValue("requestor/operand/2", 2);
		context2.putValue("to/provider/name", pn2);
		
		Context context3 = new ServiceContext("work3");
		context3.putValue("requstor/name", hostname);
		context3.putValue("requestor/operand/1", 3);
		context3.putValue("requestor/operand/2", 3);
		context3.putValue("to/provider/name", pn3);
		
		Signature signature1 = new ServiceSignature("doIt",
				sorcer.ex2.provider.Worker.class.getName(), pn1);
		Signature signature2 = new ServiceSignature("doIt",
				sorcer.ex2.provider.Worker.class.getName(), pn2);
		Signature signature3 = new ServiceSignature("doIt",
				sorcer.ex2.provider.Worker.class.getName(), pn3);
		
		Task task1 = new ServiceTask("work1", context1, signature1);
		Task task2 = new ServiceTask("work2", context2, signature2);
		Task task3 = new ServiceTask("work3", context3, signature3);
		Job job = new ServiceJob();
		job.addExertion(task1);
		job.addExertion(task2);
		job.addExertion(task3);
		return job;
	}
}
