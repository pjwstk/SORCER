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

package sorcer.surrogate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class SurrogateHandler {

	private long nextID;
	private final SurrogateRegister surrogateRegister;
	private final SurrogateMonitor surrogateMonitor;
	private final HashMap surrogates;
	private boolean terminated;
	private String initData;

	public SurrogateHandler() throws IOException {
		nextID = new Random().nextLong();
		surrogateRegister = new SurrogateRegister(this);
		surrogateMonitor = new SurrogateMonitor(this);
		surrogates = new HashMap();
		terminated = false;
	}

	// -----------------------------------
	// public methods
	// -----------------------------------

	public void register(String jarFile, String desc, int appPort,
			String initData) {
		this.initData = initData;
		long newID;
		synchronized (this) {
			if (terminated)
				return;
			newID = nextID++;
			surrogates.put(new Long(newID), new SurrogateRec(jarFile, desc,
					appPort));
		}
		System.out.println(getClass().getName()
				+ ">>>>>>>>before surrogateRegister.register()");
		surrogateRegister.register(newID, jarFile, desc, appPort,
				surrogateMonitor.getPort(), initData);
	}

	// called from the register
	public void registrationDone(long id) {
		boolean idValid;
		synchronized (this) {
			if (terminated)
				return;
			idValid = surrogates.get(new Long(id)) != null;
		}
		if (idValid)
			surrogateMonitor.register(id);
	}

	// called from the monitor
	public void surrogateUnreachable(long id) {
		SurrogateRec rec;
		long newID = 0; // will be re-assigned if will be used
		synchronized (this) {
			if (terminated)
				return;
			// forget the failed surrogate, re-register one with a new id
			rec = (SurrogateRec) surrogates.remove(new Long(id));
			if (rec != null) {
				if (System.getProperty("com.sun.jini.madison.debug") != null) {
					System.err.println("Surrogate unreachable: "
							+ rec.description);
				}
				newID = nextID++;
				surrogates.put(new Long(newID), rec);
			}
		}
		if (rec != null) {
			// re-register the surrogate
			if (System.getProperty("com.sun.jini.madison.debug") != null) {
				System.err.println("Trying to re-register surrogate: "
						+ rec.description + "...");
			}
			surrogateRegister.register(newID, rec.jarFile, rec.description,
					rec.appPort, surrogateMonitor.getPort(), initData);
		}
	}

	public void terminate() {
		synchronized (this) {
			if (terminated)
				return; // only need to be terminated once
			surrogates.clear();
			terminated = true;
		}
		surrogateRegister.terminate();
		surrogateMonitor.terminate();
	}

	// -----------------------------------
	// private inner classes
	// -----------------------------------

	private class SurrogateRec {

		public String jarFile;
		public String description;
		public int appPort;

		SurrogateRec(String jarFile, String description, int appPort) {
			this.jarFile = jarFile;
			this.description = description;
			this.appPort = appPort;
		}
	}
}
