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

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import jgapp.util.Util;
import sorcer.core.context.node.ContextNode;
import sorcer.util.Sorcer;

public class ContextFileGetter {

	private static Logger logger = Logger.getLogger("sorcer.test.context");

	private ContextNode cn;

	public ContextFileGetter() {
		// init test data if needed here
		cn = new ContextNode("URL -test");
	}

	public static void main(String[] args) {
		// Test the file names of context nodes
		logger.entering(ContextFileGetter.class.getName(), "main");
		ContextFileGetter tester = new ContextFileGetter();
		tester.testEnv();
		tester.testContextNode();
	}

	private void testContextNode() {

		try {
			cn.setUrl(new URL("http://localhost:9000/apps/results/myResults.txt"));
			File file = cn.getFile();
			logger.info("context node file: " + file);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void testEnv() {
		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));
		Properties props = Sorcer.getEnvProperties();
		Util.logProperties(props, "SORCER Environment configurtion");

		logger.info("data.root.dir: "
				+ Sorcer.getProperty("data.root.dir"));

		logger.info("provider.data.dir: "
				+ Sorcer.getProperty("provider.data.dir"));

		logger.info("provider data server: " + Sorcer.getDataServerUrl());
	}
}