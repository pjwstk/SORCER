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

import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.NetTask;
import sorcer.core.signature.NetSignature;
import sorcer.service.Context;
import sorcer.service.Signature;
import sorcer.service.Task;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Filter;
import sorcer.vfe.Var;
import sorcer.vfe.filter.ContextFilter;

/**
 * Example on how use the ExertionEvaluator with a task context
 */
public class VariableEvaluation10 {

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
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 Using Exertion Evaluator with with the provider from examples/ex1bp/bin/whoIsIt-prv-boot.xml");

		Task se = getTask();
		Var x = new Var("x");
		x.setEvaluator(se.getEvaluator());
		// no filtering, return the out context
		logger.info("1 <<<<<<<<<<<<<<<<<<<< variable x, value: " + x.getValue());
		
		ContextFilter contextFiter = new ContextFilter("provider/hostname");
		contextFiter.addPath("task/provider");
		x.setFilter(contextFiter);
		
		// filter out two paths in the new context
		logger.info("2 <<<<<<<<<<<<<<<<<<<< variable x, filtered value: " + x.getValue());
		
		// filter out path value
		contextFiter.setPath(1);
		logger.info("3 <<<<<<<<<<<<<<<<<<<< variable x, selected path value: " + x.getValue());
	}
	
	private static Task getTask() throws Exception {
		String hostname;
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostname = inetAddress.getHostName();

		Context context = new ServiceContext("Who Is It?");
		context.putValue("requestor/message", "Hi SORCER!");
		context.putValue("requestor/hostname", hostname);
		
		Signature signature = new NetSignature("getHostName",
				sorcer.ex1.WhoIsIt.class);

		Task task = new NetTask("Who Is It?", context, signature);
		return task;
	}

}
