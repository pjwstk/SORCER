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
import sorcer.service.Job;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.service.Strategy.Access;
import sorcer.service.Strategy.Flow;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Var;
import sorcer.vfe.filter.ContextFilter;

/**
 * Example on how use the ExertionEvaluator with a job context
 */
public class VariableEvaluation12 {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		logger.info("provider.lookup.accessor: "
				+ Sorcer.getProperty("provider.lookup.accessor"));

		int test = new Integer(args[0]);
        switch (test) {
            case 1:  test1(); break;
//            case 2:  test2(); break;
//            case 3:  test3(); break;
        }
	}

	public static void test1() throws Exception {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 Using Exertion Evaluator for job with with the provider" +
				" from examples/ex1bp/bin/whoIsIt1-prv-boot.xml" +
				" and examples/ex1bp/bin/whoIsIt2-prv-boot.xml");

		Job je = getJob();
		Var x = new Var("x");
		x.setEvaluator(je.getEvaluator());
		
		// no filtering, return the out context
		logger.info("1 <<<<<<<<<<<<<<<<<<<< variable x, value: " + x.getValue());
		
		// create filter
		ContextFilter contextFiter = new ContextFilter("job[0]/task[1]/task[Who Is It?]/Who Is It?/provider/hostname");
		contextFiter.addPath("job[0]/task[2]/task[Who Is It?]/Who Is It?/provider/address");
		x.setFilter(contextFiter);
//		  [java] Context Name: null
//		  [java] Root = null
//		  [java] job[0]/task[1]/task[Who Is It?] = Link:"Who Is It?" null
//		  [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/provider/hostname = curunir.rb.rad-e.wpafb.af.mil
//		  [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		  [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/requestor/message = Hi XYZ!
//		  [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/task/provider = ABC@curunir.rb.rad-e.wpafb.af.mil:10.131.5.101
//		  [java] job[0]/task[2]/task[Who Is It?] = Link:"Who Is It?" null
//		  [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/provider/address = 10.131.5.101
//		  [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/address = 10.131.5.101
//		  [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/requestor/hostname = curunir.rb.rad-e.wpafb.af.mil
//		  [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/task/provider = XYZ@curunir.rb.rad-e.wpafb.af.mil:10.131.5.101
		
		// filter out two paths in the new context
		logger.info("2 <<<<<<<<<<<<<<<<<<<< variable x, filtered value: " + x.getValue());
//	     [java] Context Name: Filtered Context
//	     [java] Root = service/context
//	     [java] job[0]/task[1]/task[Who Is It?]/Who Is It?/provider/hostname = curunir.rb.rad-e.wpafb.af.mil
//	     [java] job[0]/task[2]/task[Who Is It?]/Who Is It?/provider/address = 10.131.5.101
		
		// filter out path value
		contextFiter.setPath(1);
		logger.info("3 <<<<<<<<<<<<<<<<<<<< variable x, selected path value: " + x.getValue());
		//   [java] INFO: 3 <<<<<<<<<<<<<<<<<<<< variable x, selected path value: 10.131.5.101
	}
	
	private static Job getJob() throws Exception {
			String providerName1 = "ABC"; 
			String providerName2 = "XYZ";		
			String hostname, ipAddress;
			InetAddress inetAddress = InetAddress.getLocalHost();
			hostname = inetAddress.getHostName();
			ipAddress = inetAddress.getHostAddress();

			Context context1 = new ServiceContext("Who Is It?");
			context1.putValue("requestor/message", new RequestorMessage(providerName1));
			context1.putValue("requestor/hostname", hostname);

			Context context2 = new ServiceContext("Who Is It?");
			context1.putValue("requestor/message", new RequestorMessage(providerName2));
			context2.putValue("requestor/hostname", hostname);
			context2.putValue("requestor/address", ipAddress);
			
			Signature signature1 = new NetSignature("getHostName",
					sorcer.ex1.WhoIsIt.class, providerName1);
			Signature signature2 = new NetSignature("getHostAddress",
					sorcer.ex1.WhoIsIt.class, providerName2);

			Task task1 = new NetTask("Who Is It?", context1, signature1);
			Task task2 = new NetTask("Who Is It?", context2, signature2);
			Job job = new NetJob();
			job.addExertion(task1);
			job.addExertion(task2);
			
			ControlContext cc = job.getControlContext();
			// PUSH or PULL provider access
			cc.setAccessType(Access.PULL);
			// Exertion control flow PAR or SEQ
			cc.setFlowType(Flow.PAR);
			
			return job;
		}

}
