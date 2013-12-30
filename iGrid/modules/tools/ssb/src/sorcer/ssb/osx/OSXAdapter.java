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

package sorcer.ssb.osx;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class OSXAdapter extends ApplicationAdapter {

	private OSXAdapter(OSXApplication inApp) {
		mainApp = inApp;
	}

	public void handleAbout(ApplicationEvent ae) {
		if (mainApp != null) {
			ae.setHandled(true);
			mainApp.about();
		} else {
			throw new IllegalStateException(
					"handleAbout: MyApp instance detached from listener");
		}
	}

	public void handlePreferences(ApplicationEvent ae) {
		if (mainApp != null) {
			mainApp.preferences();
			ae.setHandled(true);
		} else {
			throw new IllegalStateException(
					"handlePreferences: MyApp instance detached from listener");
		}
	}

	public void handleQuit(ApplicationEvent ae) {
		if (mainApp != null) {
			ae.setHandled(false);
			mainApp.quit();
		} else {
			throw new IllegalStateException(
					"handleQuit: MyApp instance detached from listener");
		}
	}

	public static void registerMacOSXApplication(OSXApplication inApp) {
		if (theApplication == null)
			theApplication = new Application();
		if (theAdapter == null)
			theAdapter = new OSXAdapter(inApp);
		theApplication.addApplicationListener(theAdapter);
	}

	public static void enablePrefs(boolean enabled) {
		if (theApplication == null)
			theApplication = new Application();
		theApplication.setEnabledPreferencesMenu(enabled);
	}

	private static OSXAdapter theAdapter;
	private static Application theApplication;
	private OSXApplication mainApp;
}
