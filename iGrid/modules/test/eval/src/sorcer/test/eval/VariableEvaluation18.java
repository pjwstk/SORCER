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

import static java.lang.System.out;
import static sorcer.vo.operator.args;
import static sorcer.vo.operator.designVar;
import static sorcer.vo.operator.expression;
import static sorcer.vo.operator.gaussianDistribution;
import static sorcer.vo.operator.linkedVar;
import static sorcer.vo.operator.invariant;
import static sorcer.vo.operator.randomVar;
import static sorcer.vo.operator.var;
import static sorcer.vo.operator.vars;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import sorcer.core.SorcerConstants;
import sorcer.service.EvaluationException;
import sorcer.util.Log;
import sorcer.util.Sorcer;
import sorcer.vfe.Var;
import sorcer.vfe.VarException;
import sorcer.vfe.Variability.Type;
import sorcer.vfe.randomness.Distribution;
import sorcer.vfe.util.VarList;

/**
 * Example how use the variable-oriented programming
 */
public class VariableEvaluation18 implements SorcerConstants {

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
           	case 3:  test3(); break;
            case 4:  test4(); break;
            case 5:  test5(); break;
//            case 6:  test6(); break;
//            case 7:  test7(); break;
//            case 8:  test8(); break;
        }
	}

	public static void test1() throws ParException, RemoteException,
	EvaluationException, InterruptedException {
logger
		.info("\n\t>>>>>>>>>>>>>>>>> test1 creating variables");

		Var<?> p1 = invariant("x1", 5.0);
		out.println("p1 type: " + p1.getType());
		out.println("p1 value type: " + p1.getValueType());
		out.println("p1 value: " + p1.getValue());

		Var<?> x1 = var("x1", Type.RANDOM);
		out.println("x1 type: " + x1.getType());
		out.println("x1 value type: " + x1.getValueType());
		out.println("x1 value: " + x1.getValue());

		Var<?> x2 = designVar("x2");
		out.println("x2 type: " + x2.getType());
		out.println("x2 value type: " + x2.getValueType());
		out.println("x2 value: " + x2.getValue());
		
		Var<?> x3 = designVar("x3", 11);
		out.println("x3 type: " + x3.getType());
		out.println("x3 value type: " + x3.getValueType());
		out.println("x3 value: " + x3.getValue());
		
		Var<Double> x4 = var("x4", 10.1);
		out.println("x4 type: " + x4.getType());
		out.println("x4 value type: " + x4.getValueType());
		out.println("x4 value: " + x4.getValue());

		Var<Long> x5 = designVar("x5", 10L);
		out.println("x5 type: " + x5.getType());
		out.println("x4 value type: " + x5.getValueType());
		out.println("x5 value: " + x5.getValue());
		
		Var<?> x6 = linkedVar("x6");
		out.println("x6 type: " + x6.getType());
		out.println("x6 value type: " + x6.getValueType());
		out.println("x6 value: " + x6.getValue());
		
		Var<Long> x7 = linkedVar("x7", 18L);
		out.println("x7 type: " + x7.getType());
		out.println("x7 value type: " + x7.getValueType());
		out.println("x7 value: " + x7.getValue());
		
		Var<?> x8 = var("x8");
		out.println("x8 type: " + x8.getType());
		out.println("x8 value type: " + x8.getValueType());
		out.println("x8 value: " + x8.getValue());
		
		Var<Integer> x9 = var("x9", 10);
		out.println("x9 type: " + x9.getType());
		out.println("x9 value type: " + x9.getValueType());
		out.println("x9 value: " + x9.getValue());
		
		Var<?> x10 = var("x10");
		out.println("x10 type: " + x10.getType());
		out.println("x10 value type: " + x10.getValueType());
		out.println("x10 value: " + x10.getValue());
		
		Var<Integer> x11 = var("x11", 10);
		out.println("x11 type: " + x11.getType());
		out.println("11 value type: " + x11.getValueType());
		out.println("x11 value: " + x11.getValue());
		
		Var<?> x12 = var("x12");
		out.println("x12 type: " + x12.getType());
		out.println("x12 value type: " + x12.getValueType());
		out.println("x12 value: " + x12.getValue());
		
		Distribution d = gaussianDistribution(0.0, 0.0, 0.1);
		Var<?> x13 = randomVar("x13", 10.0, d);
		out.println("x13 type: " + x13.getType());
		out.println("x13 value type: " + x13.getValueType());
		out.println("x13 value: " + x13.getValue());
		
		VarList vars = vars(p1, x1, x2, x3, x4, x5, x5, x7, x8, x9, x10, x11, x12, x13);
		out.println("vars: " + vars);
	}
	
	public static void test2() throws ParException, RemoteException,
			EvaluationException, InterruptedException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test2 variable-oriented programming");

		class Timer {

			public void iterate() throws InterruptedException,
					EvaluationException {

				for(int i = 0; i < 5; i++ ) {
					out.println(4 + 5);
					Thread.sleep(1000);
				}
			}
		}

		new Timer().iterate();
	}

	
	public static void test3() throws ParException, RemoteException,
			EvaluationException, InterruptedException {
		logger.info("\n\t>>>>>>>>>>>>>>>>> test3 variable-oriented programming");

		class Timer {
					
			public void iterate(Var<?> callback)
					throws InterruptedException, EvaluationException {
				
				for(int i = 0; i < 5; i++ ) {
					out.println(callback.getValue());
					Thread.sleep(1000);
				}
			}
		}
		
		Var<?> x1 = var("x1", expression("x2 + x3", 
				args(vars(var("x2", 6), var("x3", 7)))));
	
		new Timer().iterate(x1);
	}
	
	
	public static void test4() throws ParException, RemoteException,
			EvaluationException, InterruptedException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test4 variable-oriented programming");

		class Timer {

			public void iterate(Var<?> callback,  Var<Integer> times, Var<Long> millis)
					throws InterruptedException, EvaluationException {

				for(int i = 0; i < times.getValue(); i++ ) {
					out.println(callback.getValue());
					Thread.sleep(millis.getValue());
				}
			}
		}

		Var<Integer> times = var("times", 6);
		Var<Long> millis = var("millis", 3000L);
		Var<?> x1 = var("x1", expression("x2 + x3 * 2.0", 
				args(vars(var("x2", 6.0), var("x3", 7.0)))));

		new Timer().iterate(x1, times, millis);
	}
	
	
	public static void test5() throws ParException, RemoteException,
			EvaluationException, InterruptedException {
		logger
				.info("\n\t>>>>>>>>>>>>>>>>> test5 variable-oriented programming");

		class Timer {

			public void iterate(Var<?> callback,
					Var<Integer> times, Var<Long> millis)
					throws InterruptedException, EvaluationException {

				for (int i = 1; i <= times.getValue(); i++) {
					out.println(callback.getValue());
					Thread.sleep(millis.getValue());
					
					if (i == 5) {
						Var<?> v = callback.getArgVar("x2");
						v.setValue(12.0);
						times.setValue(10);
						millis.setValue(1000L);
					}
				}
			}
		}

		Var<Integer> times = var("times", 20);
		Var<Long> millis = var("millis", 3000L);
		Var<?> x1 = var("x1", expression("x2 + x3 * 2.0", 
				args(vars(var("x2", 6.0), var("x3", 7.0)))));

		new Timer().iterate(x1, times, millis);
	}
}
