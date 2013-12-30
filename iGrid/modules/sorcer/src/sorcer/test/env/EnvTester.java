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

package sorcer.test.env;

import java.util.Properties;
import java.util.logging.Logger;

import sorcer.util.Log;
import sorcer.util.Sorcer;

public class EnvTester {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) {
		// Test the absolute path of file in the SORCER data repository
		EnvTester tester = new EnvTester();
		tester.run();
	}

	private void run() {

		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));

		logger.info("WAIT_FOR: " + Sorcer.getLookupWaitTime());
		Properties props = Sorcer.getEnvProperties();
		logger.info("SORCER Environment configurtion");

		Sorcer.updateFromProperties(System.getProperties());
		logger.info("SORCER Environment configurtion with system properties\n"
				+ props);

		logger.info("sorcer.jobber.name: "
				+ Sorcer.getProperty("sorcer.jobber.name"));

		logger.info("provider.webster.port: "
				+ Sorcer.getProperty("provider.webster.port"));

		// Accessing service node types
		String fiperTp3Type = Sorcer.getProperty("fiper.tp3.txt");
		logger.info("fiper.tp3.txt: " + fiperTp3Type);
		String fiperTp3ModelType = Sorcer.getProperty("fiper.tp3.model");
		logger.info("fiper.tp3.model: " + fiperTp3ModelType);
		String fiperTp3ModelDataType = Sorcer
				.getProperty("fiper.tp3.modeldata");
		logger.info("fiper.tp3.modeldata: " + fiperTp3ModelDataType);
	}
}