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

import java.io.*;
import java.rmi.*;

import net.jini.discovery.*;
import net.jini.event.*;
import net.jini.core.lookup.*;
import net.jini.core.event.*;
import net.jini.core.lease.*;

public class MailboxListener implements DiscoveryListener {
	public EventMailbox embox = null;

	public MailboxListener() {
		System.out.println("beep.");
	}

	public void discovered(DiscoveryEvent ev) {
		System.out.println("MailboxListener::discovered().");
		ServiceRegistrar[] srs = ev.getRegistrars();

		for (int i = 0; i < srs.length; i++)
			this.embox = getMailbox(srs[i]);
		if (embox == null)
			System.out.println("MailboxListener::discovered() embox == null");

	}

	public EventMailbox getMailbox(ServiceRegistrar sr) {

		try {
			Class[] cls = new Class[] { EventMailbox.class };
			ServiceTemplate st = new ServiceTemplate(null, cls, null);

			return (EventMailbox) sr.lookup(st);
		} catch (RemoteException re) {
			System.out
					.println("MailboxListener::getMailbox() Remote Exception()");
			re.printStackTrace();
		}
		return null;
	}

	public void discarded(DiscoveryEvent ev) {
		System.out.println("MailboxListener::discarded()");
	}
}
