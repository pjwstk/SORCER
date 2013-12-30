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

import static sorcer.co.operator.entry;
import static sorcer.co.operator.map;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.ServiceEvaluator;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.evaluator.FiniteDifferenceEvaluator;
import sorcer.vfe.evaluator.JepEvaluator;

/**
 * Example how use the Variables and JepEvaluator with FiniteDifferenceEvaluator
 */
public class VariableEvaluation15 {

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
//            case 4:  test4(); break;
//            case 5:  test5(); break;
//            case 6:  test6(); break;
//            case 7:  test7(); break;
        }
	}

	public static void test1() throws ParException, RemoteException,
			EvaluationException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test1 Using FiniteDifferenceEvaluator");

		// expression v1 = x1 + x2
		Var v1 = new Var("v1");
		v1.setEvaluator(new JepEvaluator("x1 + 2*x2^2"));
		logger.info("v1: " + v1);

		ServiceEvaluator v1e = (ServiceEvaluator) v1.getEvaluator();
		logger.info("v1e: " + v1e.describe());
		Var x1 = new Var("x1", 1.0);
		Var x2 = new Var("x2", 2d);
		v1e.addArg(x1);
		v1e.addArg(x2);
		logger.info("v1e args: " + v1e.getArgs().getNames());

		logger.info("setup v1e: " + v1e.describe());
		
		logger.info("<<<<<<<<<<<<<<<<<<<< v1 value: " + v1.getValue());
		
		FiniteDifferenceEvaluator pde = new FiniteDifferenceEvaluator(v1e, map(entry("x1", 1.0), entry("x2", 2.0)), 0.1);
		logger.info("<<<<<<<<<<<<<<<<<<<<1 pde derivative wrt x1: " + pde.getDerivative(x1));
		logger.info("<<<<<<<<<<<<<<<<<<<<1 pde derivative wrt x2: " + pde.getDerivative(x2));
		
		pde = new FiniteDifferenceEvaluator(v1e, 0.1);
		logger.info("<<<<<<<<<<<<<<<<<<<<2 pde derivative wrt x1: " + pde.getDerivative(x1));
		logger.info("<<<<<<<<<<<<<<<<<<<<2 pde derivative wrt x2: " + pde.getDerivative(x2));
		
		pde = new FiniteDifferenceEvaluator(v1e, 0.1, "x1", "x2");
		logger.info("<<<<<<<<<<<<<<<<<<<<3 pde gradient: " + pde.getDerivative(x1));
		pde = new FiniteDifferenceEvaluator(v1e, 0.1, "x2");
		logger.info("<<<<<<<<<<<<<<<<<<<<3 pde gradient: " + pde.getDerivative(x2));
		
		pde = new FiniteDifferenceEvaluator(v1e, map(entry("x1", 10.0), entry("x2", 20.0)), 0.01);
		logger.info("<<<<<<<<<<<<<<<<<<<<4 pde derivative wrt x1: " + pde.getDerivative(x1));
		logger.info("<<<<<<<<<<<<<<<<<<<<4 pde derivative wrt x2: " + pde.getDerivative(x2));
	}

}
