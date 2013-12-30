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

package sorcer.test.misc;

import java.util.logging.Logger;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

import sorcer.core.context.tuple.eval.VariableException;
import sorcer.util.Log;

public class  MiscTester {

	private static Logger logger = Log.getTestLog();

	/**
	 * @param args
	 * @throws ParException
	 */
	public static void main(String[] args) throws Exception {
		 MiscTester.test1();
	}

	public static void test1() throws IllegalAccessException {
		logger.info("\n\ttest1");

		Object val = 5.0;
		double stepSize = 1.0;
		
		logger.info("val.getClass() = " + val.getClass());
		logger.info("double.class = " + double.class);
		
		if (val.getClass() == Double.class)
			val = ((Double) val) + stepSize;
		else
			throw new IllegalAccessException();

		logger.info("val = " + val);

	}
	
}
