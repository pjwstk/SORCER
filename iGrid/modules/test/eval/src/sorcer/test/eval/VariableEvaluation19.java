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

import static sorcer.co.operator.array;
import static sorcer.vo.operator.outputVars;
import static sorcer.vo.operator.designVars;
import static sorcer.vo.operator.responseModel;
import static sorcer.vo.operator.soaEvaluator;
import static sorcer.vo.operator.var;

import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.core.context.model.explore.Update;
import sorcer.core.context.model.var.ResponseModel;
import sorcer.service.ContextException;
import sorcer.service.Updatable;
import sorcer.util.Log;
import sorcer.vfe.evaluator.SoaEvaluator;

/**
 * Example how use the variable-oriented programming
 */
public class VariableEvaluation19 implements SorcerConstants {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {

		int test = new Integer(args[0]);
        switch (test) {
            case 1:  test1(); break;
            case 2:  test2(); break;
//           	case 3:  test3(); break;
//            case 4:  test4(); break;
//            case 5:  test5(); break;
//            case 6:  test6(); break;
//            case 7:  test7(); break;
//            case 8:  test8(); break;
        }
	}

	public static void test1() throws RemoteException,
	InterruptedException, ContextException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 creating SOA test2");

		ResponseModel rm = responseModel("SOA Model", 
				designVars("x1"),
				outputVars("y1"));
		
		//double[] calcPointF0, double valueF0, double[] gradientF0
		var(rm, "x1", soaEvaluator(array(1.0, 2.0, 3.0), 4.0,  array(11.0, 12.0, 13.0)));
		
		SoaEvaluator soaEvaluator = (SoaEvaluator)rm.getVar("x1").getEvaluator();
		
		System.out.println(">>>>>>>>>>>>> initial SOA Evaluator: " + soaEvaluator);
		
		((Updatable)soaEvaluator).update(array(array(1.1, 2.1, 3.1),  4.1,  array(11.1, 12.1, 13.1)));
			
		System.out.println(">>>>>>>>>>>>> udated SOA Evaluator: " + soaEvaluator);
	}
	
	
	public static void test2() throws RemoteException,
	InterruptedException, ContextException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test1 creating SOA test2");

		ResponseModel rm = responseModel("SOA Model", 
				designVars("x1"),
				outputVars("y1"));
		
		//double[] calcPointF0, double valueF0, double[] gradientF0
		var(rm, "x1", soaEvaluator(array(1.0, 2.0, 3.0), 4.0,  array(11.0, 12.0, 13.0)));
		
		SoaEvaluator soaEvaluator = (SoaEvaluator)rm.getVar("x1").getEvaluator();
		
		System.out.println(">>>>>>>>>>>>> initial SOA Evaluator: " + soaEvaluator);
		
		//((Updatable)soaEvaluator).update(array(1.1, 2.1, 3.1),  4.1,  array(11.1, 12.1, 13.1));
			
		rm.updateEvaluation(new Update("x1", array(1.1, 2.1, 3.1),  4.1,  array(11.1, 12.1, 13.1)));
		
		System.out.println(">>>>>>>>>>>>> udated SOA Evaluator: " + soaEvaluator);
	}
}
