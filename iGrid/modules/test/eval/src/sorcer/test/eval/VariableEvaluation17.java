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

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.Contexts;
import sorcer.core.context.PositionalContext;
import sorcer.core.context.ServiceContext;
import sorcer.service.ContextException;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.VarException;

/**
 * Example how use the Positioning Context
 */
public class VariableEvaluation17 implements SorcerConstants {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		logger.info("provider.lookup.accessor: "
				+ Sorcer.getProperty("provider.lookup.accessor"));

		int test = new Integer(args[0]);
        switch (test) {
            case 1:  test1(); break;
            case 2:  test2(); break;
//            case 3:  test3(); break;
//            case 4:  test4(); break;
//            case 5:  test5(); break;
//            case 6:  test6(); break;
//            case 7:  test7(); break;
//            case 8:  test8(); break;
        }
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Operand positioning in context");

		ServiceContext context = new ServiceContext("array");
		String root = "arithmetic";
		try {
			Contexts.putInValueAt(context, root + "[0]" + CPS + VALUE, 20, 1);

			Contexts.putInValueAt(context, root + "[1]" + CPS + VALUE, 80, 2);
			
			Contexts.putValueAt(context, root + "[10]" + CPS + VALUE, 100, 1);
			
//			Contexts.putInValue(context, root + "[0]" + CPS + VALUE, 20);
//
//			Contexts.putInValue(context, root + "[1]" + CPS + VALUE, 80);
			
			context.putOutValue(root + "[3]" + CPS + VALUE, 0);

			System.out.println("context: \n" + context);
			
			Enumeration e1 = context.inPaths();
			
			while (e1.hasMoreElements()) {
				System.out.println("next in path: " + e1.nextElement());
			}
			
			Enumeration e2 = context.outPaths();
			
			while (e2.hasMoreElements()) {
				System.out.println("next out path: " + e2.nextElement());
			}
			
//			
//			List ml = context.getMarkedValues("");
			
			List inputPaths = Contexts.getInPaths(context);
			List outPaths = Contexts.getOutPaths(context);
						
			System.out.println("getInputPaths: \n" + inputPaths);
			System.out.println("getOutputPaths: \n" + outPaths);

			for (int i = 0; i < inputPaths.size(); i++) {

				logger.info("in value " + i + ". "
						+ context.getValue((String) inputPaths.get(i)));

			}
			System.out.println("in value 1: "
					+ Contexts.getInValueAt(context, 1));
			System.out.println("in value 2: "
					+ Contexts.getInValueAt(context, 2));
			
			System.out.println("value at 10: "
					+ Contexts.getValueAt(context, 10));
			
			System.out.println("all values at 1: "
					+ Arrays.toString(Contexts.getValuesAt(context, 1)));
			
		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
	
	public static void test2() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Operand positioning in context");

		PositionalContext context = new PositionalContext("array");
		String root = "arithmetic";
		try {
			context.putInValueAt(root + "[0]" + CPS + VALUE, 20, 1);

			context.putInValueAt(root + "[1]" + CPS + VALUE, 80, 2);

			context.putValueAt(root + "[10]" + CPS + VALUE, 100, 1);

			// Contexts.putInValue(context, root + "[0]" + CPS + VALUE, 20);
			//
			// Contexts.putInValue(context, root + "[1]" + CPS + VALUE, 80);

			context.putOutValue(root + "[3]" + CPS + VALUE, 0);

			System.out.println("context: \n" + context);

			Enumeration e1 = context.inPaths();

			while (e1.hasMoreElements()) {
				System.out.println("next in path: "
						+ e1.nextElement());
			}

			Enumeration e2 = context.outPaths();

			while (e2.hasMoreElements()) {
				System.out.println("next out path: "
						+ e2.nextElement());
			}

			//	
			// List ml = context.getMarkedValues("");

			List inputPaths = Contexts.getInPaths(context);
			List outPaths = Contexts.getOutPaths(context);

			System.out
					.println("getInputPaths: \n" + inputPaths);
			System.out.println("getOutputPaths: \n" + outPaths);

			for (int i = 0; i < inputPaths.size(); i++) {

				logger.info("in value " + i + ". "
						+ context.getValue((String) inputPaths.get(i)));

			}
			System.out.println("in value 1: "
					+ context.getInValueAt(1));
			System.out.println("in value 2: "
					+ context.getInValueAt(2));

			System.out.println("value at 10: "
					+ context.getValueAt(10));

			System.out.println("all values at 1: "
					+ context.getValuesAt(1));

		} catch (ContextException e) {
			e.printStackTrace();
		}
	}
}
