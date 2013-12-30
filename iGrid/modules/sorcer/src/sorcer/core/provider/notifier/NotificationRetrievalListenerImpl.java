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

package sorcer.core.provider.notifier;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import net.jini.core.event.RemoteEvent;
import sorcer.core.SorcerConstants;
import sorcer.core.misc.MsgRef;

public class NotificationRetrievalListenerImpl extends UnicastRemoteObject
		implements NotificationRetrievalListenerProtocol, SorcerConstants {

	private Vector msgData = new Vector();
	private int msgIndex = 0;

	public NotificationRetrievalListenerImpl() throws RemoteException {
	};

	public void notify(RemoteEvent ev) throws RemoteException {
		try {
			Vector curr = new Vector(MSG_SOURCE + 1);

			if (ev.getSource() == null) {
				System.out.println(getClass().getName()
						+ "::notify() ev.getSource() == null.");
				return;
			}
			MsgRef mr = (MsgRef) ev.getSource();
			// System.out.println(getClass().getName() + "::notify() mr: "
			// + mr + "data: " + mr.getMsgData() + " index:" + msgIndex);

			// curr.insertElementAt(new String(mr.getOwnerID()), USER_ID);
			// System.out.println(getClass().getName() +
			// "::notify() after UID msg id from mref:" +
			// mr.getMsgID());

			curr.insertElementAt(new String(mr.getMsgID()), MSG_ID);
			// System.out.println(getClass().getName() +
			// "::notify() after MSG_ID");

			curr.insertElementAt(new String(mr.getMsgType().toString()),
					MSG_TYPE);
			curr.insertElementAt(mr.getJobID(), JOB_ID);
			curr.insertElementAt(mr.getTaskID(), TASK_ID);
			curr.insertElementAt(new String(mr.getMsgData()), MSG_CONTENT);
			curr.insertElementAt(new String(mr.getSource()), MSG_SOURCE);

			System.out.println(getClass().getName() + "::notify() msgs # "
					+ msgIndex +
					// " USER_ID : " + (String) curr.elementAt(USER_ID) +
					" MSG_ID : " + (String) curr.elementAt(MSG_ID)); // +
			// " MSG_TYPE : " + (String) curr.elementAt(MSG_TYPE) +
			// " JOB_ID : " + (String) curr.elementAt(JOB_ID) +
			// " TASK_ID : " + (String) curr.elementAt(TASK_ID) +
			// " MSG_CONTENT : " + (String) curr.elementAt(MSG_CONTENT) +
			// " MSG_SOURCE : " + (String) curr.elementAt(MSG_SOURCE));

			msgData.insertElementAt(curr, msgIndex);
			// System.out.println(getClass().getName() +
			// "::notify() increase msgIndex and DONE.");
			msgIndex++;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Vector getMsgData() {
		Vector v = (Vector) msgData.clone();
		msgData.removeAllElements();
		msgIndex = 0;
		return v;
	}

	public int getMsgCount() {
		System.out.println(getClass().getName() + "::getMsgCount() msgIndex:"
				+ msgIndex);
		return msgIndex;
	}
}
