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

package sorcer.test.context;

import java.rmi.RemoteException;

import sorcer.service.EvaluationException;
import sorcer.vfe.Var;

public class ResponseVariable2 {

	public double R2;
	public String name = "Mike";
	
	/** This method demonstrates the use of the MethodEvaluator class with a
	 * signature of two double types
	 * @param x1
	 * @param x2
	 * @return double
	 */
	public double calculate(double x1, double x2) {
		R2 = -5*x1 -5*x2;
		return R2;
	}

	/** This method demonstrates the use of the MethodEvaluator class with 
	 * no forma parameters
	 * @return double
	 */
	public double calculate() {
		double x1 = 1.0, x2 = 1.0;
		R2 = -5*x1 -5*x2;
		return R2;
	}
	
	/** This method demonstrates the use of the MethodEvaluator class using the 
	 * default signature 
	 * @param x1
	 * @param x2
	 * @return double
	 */
	public double calculateVars(Var x1, Var x2) {
		try {
			R2 = -5*(Double)x1.getValue() -5*(Double)x2.getValue();
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
		return R2;
	}
	
	public double getR2() {
		return R2;
	}
}
