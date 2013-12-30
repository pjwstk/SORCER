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

package sorcer.test.eval;

import java.net.InetAddress;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import sorcer.core.context.ControlContext;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetJob;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.ex1.requestor.RequestorMessage;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.util.Log;
import sorcer.util.Sorcer;

/**
 * Example on how to use the ContextFilter with Jobs
 */
public class VariableEvaluation11 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		logger.info("provider.lookup.accessor: "
				+ Sorcer.getProperty("provider.lookup.accessor"));

		int test = new Integer(args[0]);
		switch (test) {
		case 1: test1(); break;
		case 2: test2(); break;
		// case 3: test3(); break;
		// case 4: test4(); break;
		}
	}

	public static void test1() throws Exception {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1: job context for ContextFilter");
		Job job = (Job) getExertion();

		Context jc = job.getContext();
		System.out.println("job context1:\n" + job.getExertion(0).getContext());
		System.out.println("job context2:\n" + job.getExertion(1).getContext());
		System.out.println("job context:\n" + jc);

		System.out.println(">>>>>>> task[1]/Who Is It?/requestor/hostname:"
				+ jc.getValue("task[1]/Who Is It?/requestor/hostname"));
		System.out.println(">>>>>>> task[2]/Who Is It?/requestor/address:"
				+ jc.getValue("task[2]/Who Is It?/requestor/address"));
		
//		     [java] job context1:
//		     [java] Context Name: Who Is It?
//		     [java] Root = Who Is It?
//		     [java] requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] requestor/message = Hi provider2!
//		     [java] job context2:
//		     [java] Context Name: Who Is It?
//		     [java] Root = Who Is It?
//		     [java] requestor/address = 10.131.5.101
//		     [java] requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job context:
//		     [java] Context Name: XYZ
//		     [java] Root = XYZ
//		     [java] job[0]/task[1]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/message = Hi provider2!
//		     [java] job[0]/task[2]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/address = 10.131.5.101
//		     [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] >>>>>>> task[1]/Who Is It?/requestor/hostname:null
//		     [java] >>>>>>> task[2]/Who Is It?/requestor/address:null
	}
	
	public static void test2() throws Exception {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1: complex job context for ContextFilter");
		Job job = (Job) getExertion();
		Job job2 = (Job) getExertion();
		job2.setName("component job");
		job.addExertion(job2);
		
	
		System.out.println("job context1:\n" + job.getExertion(0).getContext());
		System.out.println("job context2:\n" + job.getExertion(1).getContext());
		
		Context jc = job.getContext();
		System.out.println("job context:\n" + jc);

		System.out.println(">>>>>>> job[0]/job[3]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname:"
				+ jc.getValue("job[0]/job[3]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname"));
		System.out.println(">>>>>>> job[0]/job[3]/task[2]/task[Who Is It?]/Who Is It?/requestor/address:"
				+ jc.getValue("job[0]/job[3]/task[2]/task[Who Is It?]/Who Is It?/requestor/address"));
		
//		 	 [java] job context1:
//		     [java] Context Name: Who Is It?
//		     [java] Root = Who Is It?
//		     [java] requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] requestor/message = Hi provider2!
//		     [java] job context2:
//		     [java] Context Name: Who Is It?
//		     [java] Root = Who Is It?
//		     [java] requestor/address = 10.131.5.101
//		     [java] requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job context:
//		     [java] Context Name: XYZ
//		     [java] Root = XYZ
//		     [java] job[0]/job[3]/task[1]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/job[3]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job[0]/job[3]/task[1]/task[Who Is It?]/Who Is It?/requestor/message = Hi provider2!
//		     [java] job[0]/job[3]/task[2]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/job[3]/task[2]/task[Who Is It?]/Who Is It?/requestor/address = 10.131.5.101
//		     [java] job[0]/job[3]/task[2]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job[0]/task[1]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/message = Hi provider2!
//		     [java] job[0]/task[2]/task[Who Is It?] = Link:"Who Is It?" null
//		     [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/address = 10.131.5.101
//		     [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		     [java] >>>>>>> job[0]/job[3]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname:curunir.rb.rad-e.wpafb.af.mil
//		     [java] >>>>>>> job[0]/job[3]/task[2]/task[Who Is It?]/Who Is It?/requestor/address:10.131.5.101
	}
	
	private static Exertion getExertion() throws Exception {
		Job job = null;
		Context context1 = null;
		Context context2 = null;
		String providerName1 = "provider1";
		String providerName2 = "provider2";
		String hostname, ipAddress;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();
		ipAddress = inetAddress.getHostAddress();

		context1 = new ServiceContext("Who Is It?");
		context1.putValue("requestor/message", new RequestorMessage(
				providerName1));
		context1.putValue("requestor/hostname", hostname);

		context2 = new ServiceContext("Who Is It?");
		context1.putValue("requestor/message", new RequestorMessage(
				providerName2));
		context2.putValue("requestor/hostname", hostname);
		context2.putValue("requestor/address", ipAddress);

		Signature signature1 = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class, providerName1);
		Signature signature2 = new NetSignature("getHostAddress",
				sorcer.ex1.WhoIsIt.class, providerName2);

		Task task1 = new NetTask("Who Is It?", context1, signature1);
		Task task2 = new NetTask("Who Is It?", context2, signature2);
		job = new NetJob("XYZ");
		job.addExertion(task1);
		job.addExertion(task2);

		ControlContext cc = job.getControlContext();
		// PUSH or PULL provider access
		cc.setAccessType(Access.PULL);
		// Exertion control flow PARALLEL or SEQUENTIAL
		cc.setFlowType(Flow.PAR);
		return job;

	}
}
