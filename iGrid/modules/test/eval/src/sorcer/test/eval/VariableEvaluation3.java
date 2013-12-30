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

import net.jini.config.ConfigurationException;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.ExpressionEvaluator;

/**
 * Example on how to initializing a variable from configuration file
 */
public class VariableEvaluation3 {

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
		}
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException, ConfigurationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Initializing a variable from configuration file: evalutator");

		// expression e1 = x1 + x2
		Var x1 = new Var("x1");
		x1.init();
		logger.info("x1: " + x1);
		logger.info("x1>>evauator: " + x1.getEvaluator());
		logger.info("x1>>dependents: " + ((ExpressionEvaluator) x1.getEvaluator()).getVariables());

		logger.info("<<<<<<<<<<<<<<<<<<<< x1 value: " + x1.getValue());
	}

	public static void test2() throws ParException, RemoteException,
			EvaluationException, ConfigurationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 Initializing a variable from configuration file: evaluator and filter");

		// expression x4 = x1 + x2
		Var x4 = new Var("x4");
		x4.init();
		logger.info("x4: " + x4);
		logger.info("x4>>evauator: " + x4.getEvaluator());
		logger.info("x4>>dependents: "
				+ ((ExpressionEvaluator) x4.getEvaluator()).getVariables());

		logger.info("<<<<<<<<<<<<<<<<<<<< x4 value: " + x4.getValue());
	}

}
