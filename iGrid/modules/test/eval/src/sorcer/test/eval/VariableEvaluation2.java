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
import java.util.logging.Logger;

import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.MethodEvaluator;

/**
 * Example on how use the MethodEvaluator
 */
public class VariableEvaluation2 {

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
		case 3: test3(); break;
		case 4: test4(); break;
		// case 5: test5(); break;
		}
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 Using Method Evaluator");

		// expression R = -5x1 - 5x2
		Var x1 = new Var("x1", 1.0);
		Var x2 = new Var("x2", 2.0);

		// class name specified
		MethodEvaluator me = new MethodEvaluator("sorcer.test.eval.Response",
				"calculateVars");
		me.addArg(x1).addArg(x2);
		logger.info("evaluator variables: " + me.getVarParameters());

		Var R = new Var("R");
		R.setEvaluator(me);
		logger.info("varaiable R" + R);
		logger.info("setup evaluator me: " + me.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: R value: " + R.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: R value: " + R.getValue());
	}

	public static void test2() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 Using Method Evaluator with Java API");

		// expression R = -5x1 - 5x2
		Var x1 = new Var("x1", 1.0);
		Var x2 = new Var("x2", 1.0);
		// class name specified
		MethodEvaluator me = new MethodEvaluator("sorcer.test.eval.Response");
		// Java API, no variables as dependents
		me.setArgs("calculate", new Class[] { double.class, double.class },
				new Object[] { 1.0, 1.0 });

		Var R = new Var("R");
		R.setEvaluator(me);
		logger.info("R" + R);
		logger.info("setup me: " + me.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: R value: " + R.getValue());
		logger.info("<<<<<<<<<<<<<<<<<<<< second: R value: " + R.getValue());
	}

	public static void test3() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test3 Using Method Evaluator with resetting value");

		// expression R = -5x1 - 5x2
		Var x1 = new Var("x1", 1.0);
		Var x2 = new Var("x2", 1.0);
		// class name specified
		MethodEvaluator me = new MethodEvaluator("sorcer.test.eval.Response",
				"calculateVars");
		me.addArg(x1).addArg(x2);
		// Java API
		// me.setMethod("calculate", new Class[] { double.class, double.class },
		// new Object[] { 1.0, 1.0 });

		Var R = new Var("R");
		R.setEvaluator(me);
		logger.info("R" + R);
		logger.info("setup me: " + me.describe());

		logger.info("<<<<<<<<<<<<<<<<<<<< first: R value: " + R.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info("<<<<<<<<<<<<<<<<<<<< second: R value: " + R.getValue());
	}

	public static void test4() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test4 Using Method Evaluator object in the constructor");

		// expression R = -5x1 - 5x2
		Var x1 = new Var("x1", 1.0);
		Var x2 = new Var("x2", 1.0);
		// class name specified
		MethodEvaluator me = new MethodEvaluator( new sorcer.test.eval.Response());
		me.setSelector("calculateVars");
		me.addArg(x1).addArg(x2);
		
		Var R = new Var("R");
		R.setEvaluator(me);
		
		logger.info("<<<<<<<<<<<<<<<<<<<< first: R value: " + R.getValue());
		// evaluate execution this time
		x1.setValue(10.0);
		logger.info("<<<<<<<<<<<<<<<<<<<< second: R value: " + R.getValue());

	}
}
