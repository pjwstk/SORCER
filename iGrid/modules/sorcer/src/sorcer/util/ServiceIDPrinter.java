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

package sorcer.util;

import net.jini.core.lookup.ServiceID;
import net.jini.lookup.ServiceIDListener;
import sorcer.core.SorcerConstants;

public class ServiceIDPrinter implements ServiceIDListener, SorcerConstants {

	public ServiceIDPrinter() {
		// do nothing
	}

	public void serviceIDNotify(ServiceID sid) {
		System.out.println("Service has been assigned service ID: "
				+ sid.toString());
		try {
			ObjectLogger.persist(Sorcer.getProperty("sorcer.env.idFile"), sid);
			System.out.println("Written to persistent storage.");
		} catch (Exception e) {
			System.out
					.println("Cannot write service ID to persistent storage.");
			System.exit(1);
		}
	}
}
