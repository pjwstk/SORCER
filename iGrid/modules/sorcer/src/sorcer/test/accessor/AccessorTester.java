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

package sorcer.test.accessor;

import java.rmi.RMISecurityManager;
import java.util.Properties;
import java.util.logging.Logger;

import net.jini.core.transaction.server.TransactionManager;
import net.jini.space.JavaSpace;
import sorcer.core.provider.Cataloger;
import sorcer.core.provider.Jobber;
import sorcer.service.Service;
import sorcer.service.Tasker;
import sorcer.util.AccessorException;
import sorcer.util.Log;
import sorcer.util.ProviderAccessor;
import sorcer.util.ProviderLookup;
import sorcer.util.QosProviderAccessor;
import sorcer.util.ServiceAccessor;
import sorcer.util.Sorcer;
import sorcer.util.SorcerUtil;

public class AccessorTester {

	private static Logger logger = Log.getTestLog();

	public static void main(String[] args) {
		System.setSecurityManager(new RMISecurityManager());
		// Test the Service and ProviderAccessor functionality
		logger.info("iGrid.home: " + System.getProperty("iGrid.home"));
		Properties props = Sorcer.getEnvProperties();
		logger.info("SORCER Environment configurtion\n" + props);

		AccessorTester tester = new AccessorTester();
		String testType = args[0];

		logger.info("testing: " + testType);
		tester.run(testType);
	}

	private void run(String test) {
		JavaSpace space;
		Cataloger catalog;
		Jobber jobber;
		TransactionManager tm;

		if (test.equals("space")) {
			// test ServiceAccessor for JavaSpace

			// space = (JavaSpace) ServiceAccessor.getService(null,
			// JavaSpace.class.getName());
			// logger.info("=================> ServiceAccessor space: " +
			// space);
			//
			space = ProviderAccessor.getSpace();
			logger.info("=================> ProviderAccessor get space: "
					+ space);

			// space = (JavaSpace)
			// ProviderLookup.getService(JavaSpace.class.getName());
			// logger.info("=================> ProviderLookup: " + space);
		}
		if (test.equals("transaction")) {
			// test ServiceAccessor for TransactionManager
			// tm = (TransactionManager) ServiceAccessor.getService(null,
			// TransactionManager.class.getName());
			// logger.info("=================> ServiceAccessor tm: " + tm);

			tm = ProviderAccessor.getTransactionManager();
			logger.info("=================> ProviderAccessor tm: " + tm);

			// tm = (TransactionManager) ProviderLookup
			// .getService(TransactionManager.class.getName());
			// logger.info("=================> ProviderLookup tm: " + tm);
		}
		if (test.equals("tasker")) {
			// test ServiceAccessor for sorcer.service.Tasker
			sorcer.service.Tasker tasker;
			tasker = (Tasker) ProviderAccessor
					.getProvider("sorcer.service.Tasker");
			logger.info("ProviderAccessor tasker: " + tasker);
			logger.info("ProviderAccessor tasker instanceof Tasker: "
					+ (tasker instanceof Tasker));
			logger.info("ProviderAccessor tasker interfaces: "
					+ SorcerUtil.arrayToString(tasker.getClass()
							.getInterfaces()));
		} else if (test.equals("service")) {
			// test ServiceAccessor
			catalog = (Cataloger) ServiceAccessor.getService(null,
					Cataloger.class.getName());
			logger.info("ServiceAccessor cataloger: " + catalog);

			// jobber = (Jobber) ServiceAccessor.getService(null,
			// Jobber.class.getName());
			// logger.info("ServiceAccessor jobber: " + jobber);
		} else if (test.equals("lookup")) {
			// test ServiceProvider finding delegated directly to
			// ServiceAccessor, returns references to SORCER Provider
			catalog = (Cataloger) ProviderAccessor.getService(null,
					Cataloger.class.getName());
			logger.info("ServiceProvider find cataloger: " + catalog);

			jobber = (Jobber) ProviderAccessor.getService(null, Jobber.class
					.getName());
			logger.info("ServiceProvider find jobber: " + jobber);
		} else if (test.equals("cataloger")) {
			// test ServiceProvider get providers, returns references
			// casted to the provider's custom interface
			try {
				catalog = (Cataloger) ProviderLookup
						.getProvider("sorcer.core.Cataloger");
				logger.info("ProviderLookup get catalog: " + catalog);
				catalog = null;

				catalog = ProviderAccessor.getCataloger();
				logger.info("ProviderAccessor get cataloger: " + catalog);
				catalog = null;
			} catch (AccessorException ex) {
				logger.throwing(AccessorTester.class.getName(), "run", ex);
			}
		} else if (test.equals("jobber")) {
			// test ServiceProvider get providers, returns references
			// casted to the provider's custom interface
			try {
				jobber = (Jobber) ProviderLookup
						.getProvider("sorcer.service.Jobber");
				logger.info(">>>>>>ProviderLookup get jobber: " + jobber);
				jobber = null;

				jobber = ProviderAccessor.getJobber();
				logger.info(">>>>>>ProviderAccessor get jobber: " + jobber);
				jobber = null;
				;
			} catch (AccessorException ex) {
				logger.throwing(AccessorTester.class.getName(), "run", ex);
			}
		} else if (test.equals("ex5")) {
			// test ServiceProvider get Arithmetic provider
			//Service provider = ProviderAccessor.getProvider(sig
			//		.getProviderName(), sig.getServiceType(), codebase);
			
			Service arithmetic = ProviderAccessor.getProvider("Worker1",
					"sorcer.ex5.provider.Worker1", null);
			logger.info("Worker1 in ex5: " + arithmetic);
		}
	}
}
