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

import java.util.logging.Logger;

import sorcer.util.Log;

import com.sun.jna.ptr.IntByReference;


public class JnaEvaluation {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) throws Exception {
		IntByReference ibr1 = new IntByReference(2000);
		logger.info("ibr1=" + ibr1.toString());
	}
	
}
