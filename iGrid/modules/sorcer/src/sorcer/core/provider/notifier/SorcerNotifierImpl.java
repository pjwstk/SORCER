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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import sorcer.core.SorcerConstants;
import sorcer.core.SorcerNotifierProtocol;
import sorcer.core.exertion.NetTask;
import sorcer.core.misc.MsgRef;
import sorcer.core.provider.ServiceProvider;

public class SorcerNotifierImpl extends ServiceProvider implements
		RemoteEventListener, SorcerNotifierProtocol, SorcerConstants {

	/* Failure Events go here */
	private Vector failureEvents = new Vector();
	private Vector failureUIDs = new Vector();
	private Vector failureHandbacks = new Vector();
	private Vector failureJobIDs = new Vector();
	private Vector failureSessionIDs = new Vector();

	/* Exception Events go here */
	private Vector exceptionEvents = new Vector();
	private Vector exceptionUIDs = new Vector();
	private Vector exceptionHandbacks = new Vector();
	private Vector exceptionJobIDs = new Vector();
	private Vector exceptionSessionIDs = new Vector();

	/* Information Events go here */
	private Vector informationEvents = new Vector();
	private Vector informationUIDs = new Vector();
	private Vector informationHandbacks = new Vector();
	private Vector informationJobIDs = new Vector();
	private Vector informationSessionIDs = new Vector();

	/* Warning Events go here */
	private Vector warningEvents = new Vector();
	private Vector warningUIDs = new Vector();
	private Vector warningHandbacks = new Vector();
	private Vector warningJobIDs = new Vector();
	private Vector warningSessionIDs = new Vector();

	// String dbUrl = Env.getProperty("applicationServer.url.sorcer");
	String dbUrl = "jdbc:oracle:thin:@mondrian.cs.ttu.edu:1521:QASORCER";

	public SorcerNotifierImpl() throws RemoteException {
	};

	public boolean isValidTask() {
		return true;
	}

	public Integer register(RemoteEventListener listener, Object handback,
			Integer regFor, String userId, Vector sessionJobs, String sessionID)
			throws RemoteException {
		int spot;

		System.out.println(getClass().getName() + "::register() listener: "
				+ listener + " handback: " + handback + " regFor: " + regFor
				+ " userId: " + userId + " sessionJobs: " + sessionJobs
				+ " sessionID: " + sessionID);

		switch (regFor.intValue()) {

		case NOTIFY_FAILURE: {
			System.out.println(getClass().getName()
					+ "::register() register for NOTIFY_FAILURE");
			failureEvents.add(listener);
			spot = failureEvents.indexOf(listener);
			failureHandbacks.insertElementAt(handback, spot);
			failureUIDs.insertElementAt(userId, spot);
			failureJobIDs.insertElementAt(sessionJobs, spot);
			failureSessionIDs.insertElementAt(sessionID, spot);
			break;
		}
		case NOTIFY_EXCEPTION: {
			System.out.println(getClass().getName()
					+ "::register() register for NOTIFY_EXCEPTION");
			exceptionEvents.add(listener);
			spot = exceptionEvents.indexOf(listener);
			exceptionHandbacks.insertElementAt(handback, spot);
			exceptionUIDs.insertElementAt(userId, spot);
			exceptionJobIDs.insertElementAt(sessionJobs, spot);
			exceptionSessionIDs.insertElementAt(sessionID, spot);
			break;
		}
		case NOTIFY_INFORMATION: {
			System.out.println(getClass().getName()
					+ "::register() register for NOTIFY_INFORMATION");
			informationEvents.add(listener);
			spot = informationEvents.indexOf(listener);
			informationHandbacks.insertElementAt(handback, spot);
			informationUIDs.insertElementAt(userId, spot);
			informationJobIDs.insertElementAt(sessionJobs, spot);
			informationSessionIDs.insertElementAt(sessionID, spot);
			break;
		}

		case NOTIFY_WARNING: {
			System.out.println(getClass().getName()
					+ "::register() register for NOTIFY_WARNING");
			warningEvents.add(listener);
			spot = warningEvents.indexOf(listener);
			warningHandbacks.insertElementAt(handback, spot);
			warningUIDs.insertElementAt(userId, spot);
			warningJobIDs.insertElementAt(sessionJobs, spot);
			warningSessionIDs.insertElementAt(sessionID, spot);
			break;
		}

		}

		// dumpState();
		return new Integer(userId);
	}

	public Integer register(RemoteEventListener listener, Integer regFor,
			String userId, Vector sessionJobs, String sessionID)
			throws RemoteException {
		return register(listener, null, regFor, userId, sessionJobs, sessionID);
	}

	public Integer register(RemoteEventListener listener, Integer regFor,
			String userId, Vector sessionJobs) throws RemoteException {
		// insert null and pass the buck
		return register(listener, null, regFor, userId, sessionJobs, null);
	}

	public void deleteListener(Integer id, Integer regFor)
			throws RemoteException {

		switch (regFor.intValue()) {

		case NOTIFY_FAILURE: {
			failureEvents.removeElementAt(failureUIDs.indexOf(id));
			failureHandbacks.removeElementAt(failureUIDs.indexOf(id));
			failureJobIDs.removeElementAt(failureUIDs.indexOf(id));
			failureSessionIDs.removeElementAt(failureUIDs.indexOf(id));
			failureUIDs.removeElement(id);
		}
		case NOTIFY_EXCEPTION: {
			exceptionEvents.removeElementAt(exceptionUIDs.indexOf(id));
			exceptionHandbacks.removeElementAt(exceptionUIDs.indexOf(id));
			exceptionJobIDs.removeElementAt(exceptionUIDs.indexOf(id));
			exceptionSessionIDs.removeElementAt(failureUIDs.indexOf(id));
			exceptionUIDs.removeElement(id);
		}
		case NOTIFY_INFORMATION: {
			informationEvents.removeElementAt(informationUIDs.indexOf(id));
			informationHandbacks.removeElementAt(informationUIDs.indexOf(id));
			informationJobIDs.removeElementAt(informationUIDs.indexOf(id));
			informationSessionIDs.removeElementAt(failureUIDs.indexOf(id));
			informationUIDs.removeElement(id);
		}

		case NOTIFY_WARNING: {
			warningEvents.removeElementAt(warningUIDs.indexOf(id));
			warningHandbacks.removeElementAt(warningUIDs.indexOf(id));
			warningJobIDs.removeElementAt(warningUIDs.indexOf(id));
			warningSessionIDs.removeElementAt(failureUIDs.indexOf(id));
			warningUIDs.removeElement(id);
		}

		}

		return;
	}

	/* Provider */
	/*
	 * public String getName() throws RemoteException { return "SorcerNotifier";
	 * }
	 * 
	 * public Remote[] getAttributes() throws RemoteException { Entry [] e = {
	 * new Name(getName())}; return e; }
	 */

	/* remote event listener */
	public void notify(RemoteEvent ev) throws RemoteException {
		try {
			// System.out.println(getClass().getName() + "::notify() ev:" + ev);

			MsgRef msg = (MsgRef) ev.getSource();

			msg.setMsgID(getNextSeqID("FIP_MESSAGE_SEQ"));
			// set the owner and job ids for the other stuff not to have null
			// problemos
			if (msg.getJobID() == null)
				msg.setJobID(getTaskParentJobID(msg.getTaskID()));
			if (msg.getOwnerID() == null)
				msg.setOwnerID(getJobOwner(msg.getJobID()));

			storeMsgToDB(msg);

			switch (msg.getMsgType().intValue()) {

			case NOTIFY_FAILURE: {
				System.out.println(getClass().getName()
						+ "::notify() NOTIFY_FAILURE.");
				failureNotify(ev);
				break;
			}
			case NOTIFY_EXCEPTION: {
				System.out.println(getClass().getName()
						+ "::notify() NOTIFY_EXCEPTION.");
				exceptionNotify(ev);
				break;
			}
			case NOTIFY_INFORMATION: {
				System.out.println(getClass().getName()
						+ "::notify() NOTIFY_INFORMATION.");
				informationNotify(ev);
				break;
			}
			case NOTIFY_WARNING: {
				System.out.println(getClass().getName()
						+ "::notify() NOTIFY_WARNING.");
				warningNotify(ev);
				break;
			}
			}

		} catch (UnknownEventException ex) {
			System.err
					.println("SorcerNotifierImpl::notify(RemoteEvent ev) UnknownEventException.");
		}
		return;
	}

	protected void failureNotify(RemoteEvent ev) throws RemoteException,
			UnknownEventException {
		MsgRef msg = (MsgRef) ev.getSource();
		String destUID = msg.getOwnerID();
		RemoteEventListener rel;
		boolean found = false;
		int i;

		try {

			for (i = 0; i < failureUIDs.size(); i++) {
				// if(destUID.equals(failureUIDs.elementAt(i)) &&
				// this.isJobIDinSession(msg.getJobID(),
				// (Vector)failureJobIDs.elementAt(i))){
				if (msg.getSessionID().equals(failureSessionIDs.elementAt(i))) {
					rel = (RemoteEventListener) failureEvents.elementAt(i);
					rel.notify(ev);
					found = true;
				}
			}

			if (!found)
				System.err
						.println("SorcerNotifierImpl::failureNotify(RemoteEvent ev) destination user not found");
			/*
			 * else{ rel = (RemoteEventListener) failureEvents.elementAt(i);
			 * rel.notify(ev); }
			 */
		} catch (Exception e1) {
			System.err
					.println("SorcerNotifierImpl::failureNotify() caught an Exception.");
		}

		return;

	}

	protected void exceptionNotify(RemoteEvent ev) {
		MsgRef msg = (MsgRef) ev.getSource();
		String destUID = msg.getOwnerID();
		RemoteEventListener rel;
		boolean found = false;
		int i;

		try {

			for (i = 0; i < exceptionUIDs.size(); i++) {
				// if(destUID.equals(exceptionUIDs.elementAt(i)) &&
				// this.isJobIDinSession(msg.getJobID(),(Vector)
				// exceptionJobIDs.elementAt(i))){
				if (msg.getSessionID().equals(exceptionSessionIDs.elementAt(i))) {
					rel = (RemoteEventListener) exceptionEvents.elementAt(i);
					rel.notify(ev);
					found = true;
				}
			}

			if (!found)
				System.err
						.println("SorcerNotifierImpl::exceptionNotify(RemoteEvent ev) destination user not found");
			/*
			 * else{ rel = (RemoteEventListener) exceptionEvents.elementAt(i);
			 * rel.notify(ev); }
			 */
		} catch (Exception e1) {
			System.err
					.println("SorcerNotifierImpl::exceptionNotify() caught an Exception.");
		}
		return;
	}

	protected void informationNotify(RemoteEvent ev) {
		MsgRef msg = (MsgRef) ev.getSource();
		String destUID = msg.getOwnerID();
		RemoteEventListener rel;
		boolean found = false;
		int i;

		try {

			for (i = 0; i < informationUIDs.size(); i++) {
				// System.out.println(getClass().getName() +
				// "::informationNotify() informationUIDs[" + i + "]: " +
				// informationUIDs.elementAt(i));
				// if(destUID.equals(informationUIDs.elementAt(i)) &&
				// this.isJobIDinSession(msg.getJobID(),
				// (Vector)informationJobIDs.elementAt(i))){
				if (msg.getSessionID().equals(
						informationSessionIDs.elementAt(i))) {
					rel = (RemoteEventListener) informationEvents.elementAt(i);
					rel.notify(ev);
					found = true;
				}
			}

			if (!found)
				System.err
						.println("SorcerNotifierImpl::informationNotify(RemoteEvent ev) destination user not found");
			/*
			 * else{ rel = (RemoteEventListener) informationEvents.elementAt(i);
			 * rel.notify(ev); }
			 */
		} catch (Exception e1) {
			System.err
					.println("SorcerNotifierImpl::informationNotify() caught an Exception.");
			e1.printStackTrace();
		}
		return;
	}

	protected void warningNotify(RemoteEvent ev) {
		MsgRef msg = (MsgRef) ev.getSource();
		String destUID = msg.getOwnerID();
		RemoteEventListener rel;
		boolean found = false;
		int i;

		try {

			for (i = 0; i < warningUIDs.size(); i++) {
				// if(destUID.equals(warningUIDs.elementAt(i)) &&
				// this.isJobIDinSession(msg.getJobID(),
				// (Vector)warningJobIDs.elementAt(i))){
				if (msg.getSessionID().equals(warningSessionIDs.elementAt(i))) {
					rel = (RemoteEventListener) warningEvents.elementAt(i);
					rel.notify(ev);
					found = true;
				}
			}

			if (!found)
				System.err
						.println("SorcerNotifierImpl::warningNotify(RemoteEvent ev) destination user not found");
			/*
			 * else{ System.err.println(getClass().getName() +
			 * "::warningNotify() found user, notifying."); rel =
			 * (RemoteEventListener) warningEvents.elementAt(i); rel.notify(ev);
			 * }
			 */
		} catch (Exception e1) {
			System.err
					.println("SorcerNotifierImpl::warningNotify() caught an Exception.");
		}
		return;
	}

	private boolean isJobIDinSession(String jobID, Vector jobIDList) {
		try {
			for (int i = 0; i < jobIDList.size(); i++) {
				// System.out.println(getClass().getName() +
				// "::isJobIDinSession() jobID:"+ jobID +
				// " jobIDList[" + i + "]:" + jobIDList.elementAt(i));
				if (jobID.equals(jobIDList.elementAt(i)))
					return true;
			}
		} catch (Exception e) {
			System.out.println(getClass().getName()
					+ "::isJobIDinSession() caught Exception.");
			e.printStackTrace();
		}
		return false;
	}

	protected void makeMsgPersistant() {
		System.err
				.println("SorcerNotifierImpl::makeMsgPersistant() NOT IMPLEMENETED HERE.");
		return;
	}

	public boolean isValidTask(NetTask task) {
		return true;
	}

	protected void storeMsgToDB(MsgRef mr) {
		Connection conn = null;
		Statement stmt = null;
		try {
			int res = -1;
			ResultSet rs = null;
			conn = getConnection();
			stmt = conn.createStatement();

			StringBuffer query = new StringBuffer(
					"INSERT INTO FIP_MESSAGE (Message_Seq_Id, Eng_Task_Seq_Id, Job_Seq_Id, Msg_Type, Content, Creation_Date, Session_Seq_Id, Source) ");
			query.append("VALUES(");
			query.append(mr.getMsgID());
			query.append(", ");
			query.append(mr.getTaskID());
			query.append(", ");
			query.append(mr.getJobID());
			query.append(", ");
			query.append(mr.getMsgType());
			query.append(", '");
			query.append(mr.getMsgData());
			query.append(mr.getSessionID());
			query.append(", '");
			query.append(mr.getSource());
			query.append("')");

			System.out.println(getClass().getName()
					+ "::storeMsgToDB() queryx:" + query.toString());
			res = stmt.executeUpdate(query.toString());
			// System.out.println(getClass().getName() +
			// "::storeMsgToDB() inserted " + res + " records.");
			stmt.close();
			conn.close();

		} catch (SQLException sqle) {
			System.out.println(getClass().getName()
					+ "::storeMsgToDB() caught SQLException.");
			sqle.printStackTrace();
			try {
				stmt.close();
				conn.close();
			} catch (SQLException sqle2) {
				System.out.println(getClass().getName()
						+ "::storeMsgToDB() Couldn't close DB copnnection.");
			}

		}
	}

	public void run() {
		try {
			while (true)
				Thread.sleep(10000000);
		} catch (InterruptedException ie) {
			System.err
					.println("SorcerNotifierImpl::run()InterruptedException.");
		}
	}

	protected Uuid getTaskParentJobID(Uuid taskID) {
		Uuid res = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			ResultSet rs = null;
			conn = getConnection();
			stmt = conn.createStatement();
			StringBuffer query = new StringBuffer(
					"SELECT Job_Seq_Id FROM FIP_ENG_TASK WHERE Eng_Task_Seq_Id = ");
			query.append(taskID);
			System.out.println(getClass().getName()
					+ "::getTaskParentJobID() query:" + query);

			rs = stmt.executeQuery(query.toString());

			rs.next();
			res = UuidFactory.create(rs.getString(1));
			stmt.close();
			conn.close();

		} catch (SQLException sqle) {
			System.out.println(getClass().getName()
					+ "::getTaskParentJobID() caught SQLException.");
			sqle.printStackTrace();
			try {
				stmt.close();
				conn.close();
			} catch (SQLException sqle2) {
				System.out
						.println(getClass().getName()
								+ "::getTaskParentJobID() Couldn't close DB copnnection.");
			}
		}
		return res;
	}

	protected String getJobOwner(Uuid jobID) {
		String res = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			ResultSet rs = null;

			conn = getConnection();
			stmt = conn.createStatement();
			StringBuffer query = new StringBuffer(
					"SELECT Owner_Id FROM FIP_JOB WHERE Job_Seq_Id = ");
			query.append(jobID);
			System.out.println(getClass().getName() + "::getJobOwner() query:"
					+ query);

			rs = stmt.executeQuery(query.toString());

			rs.next();
			res = rs.getString(1);
			stmt.close();
			conn.close();

		} catch (SQLException sqle) {
			System.out.println(getClass().getName()
					+ "::getJobOwner() caught SQLException.");
			sqle.printStackTrace();
			try {
				stmt.close();
				conn.close();
			} catch (SQLException sqle2) {
				System.out.println(getClass().getName()
						+ "::getJobOwner() Couldn't close DB copnnection.");
			}
		}
		return res;
	}

	public void appendJobToSession(String ownerID, String jobID,
			int sessionType, String sessionID) throws RemoteException {

		System.out.println(getClass().getName()
				+ "::appendJobToSession() ownerID: " + ownerID + " jobID: "
				+ jobID + " sessionType: " + sessionType + " SessionID:"
				+ sessionID);

		boolean found = false;
		Vector jobIDs = null;
		int i = 0;

		if (sessionType == NOTIFY_FAILURE) {
			for (i = 0; i < failureUIDs.size(); i++) {
				if (ownerID.equals((String) failureUIDs.elementAt(i))
						&& sessionID.equals((String) failureSessionIDs
								.elementAt(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				jobIDs = (Vector) failureJobIDs.elementAt(i);
				if (jobIDs == null) {
					failureUIDs.insertElementAt(new Vector(), i);
					jobIDs = (Vector) failureUIDs.elementAt(i);
				}
				jobIDs.add(jobID);
			} else {
				System.out
						.println(getClass().getName()
								+ "::appendJobToSession() NOTIFY_FAILURE user not registered for failures.");
			}
		} else if (sessionType == NOTIFY_WARNING) {

			for (i = 0; i < warningUIDs.size(); i++) {
				if (ownerID.equals((String) warningUIDs.elementAt(i))
						&& sessionID.equals((String) warningSessionIDs
								.elementAt(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				jobIDs = (Vector) warningJobIDs.elementAt(i);
				if (jobIDs == null) {
					warningUIDs.insertElementAt(new Vector(), i);
					jobIDs = (Vector) warningUIDs.elementAt(i);
				}
				jobIDs.add(jobID);
			} else {
				System.out
						.println(getClass().getName()
								+ "::appendJobToSession() NOTIFY_WARNING user not registered for warnings.");
			}
		} else if (sessionType == NOTIFY_EXCEPTION) {

			for (i = 0; i < exceptionUIDs.size(); i++) {
				if (ownerID.equals((String) exceptionUIDs.elementAt(i))
						&& sessionID.equals((String) exceptionSessionIDs
								.elementAt(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				jobIDs = (Vector) exceptionJobIDs.elementAt(i);
				if (jobIDs == null) {
					exceptionUIDs.insertElementAt(new Vector(), i);
					jobIDs = (Vector) exceptionUIDs.elementAt(i);
				}
				jobIDs.add(jobID);
			} else {
				System.out
						.println(getClass().getName()
								+ "::appendJobToSession() NOTIFY_EXCEPTION user not registered for exceptions.");
			}
		} else if (sessionType == NOTIFY_INFORMATION) {
			for (i = 0; i < informationUIDs.size(); i++) {
				if (ownerID.equals((String) informationUIDs.elementAt(i))
						&& sessionID.equals((String) informationSessionIDs
								.elementAt(i))) {
					found = true;
					break;
				}
			}
			if (found) {
				jobIDs = (Vector) informationJobIDs.elementAt(i);
				if (jobIDs == null) {
					informationUIDs.insertElementAt(new Vector(), i);
					jobIDs = (Vector) informationUIDs.elementAt(i);
				}
				jobIDs.add(jobID);
			} else {
				System.out
						.println(getClass().getName()
								+ "::appendJobToSession() NOTIFY_information user not registered for information.");
			}
		} else
			System.out.println(getClass().getName()
					+ "::appendJobToSession() bad sessionType specified.");

	}

	String getNextSeqID(String sequenceName) {
		// get teh next value of a sequence
		String seqNextVal = null;
		;
		ResultSet rs = null;
		String query = new String("SELECT " + sequenceName
				+ ".NEXTVAL FROM DUAL");
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery(query.toString());

			// System.out.println(getClass().getName() +
			// "::getNextSeqID() query:" + query);
			rs = stmt.executeQuery(query.toString());

			rs.next();
			seqNextVal = rs.getString(1);
			// System.out.println(getClass().getName() +
			// "::getNextSeqID() seqNextVal: " + seqNextVal);
			stmt.close();
			conn.close();

		} catch (SQLException sqle) {
			System.out.println(getClass().getName()
					+ "::getNextSeqID() caught SQLException.");
			sqle.printStackTrace();
			try {
				stmt.close();
				conn.close();
			} catch (SQLException sqle2) {
				System.out.println(getClass().getName()
						+ "::getNextSeqID() Couldn't close DB copnnection.");
			}
		}
		return seqNextVal;
	}

	private Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			return DriverManager.getConnection(dbUrl, "sorcer", "1sorcer1");

		} catch (ClassNotFoundException cnfe) {
			System.out.println(getClass().getName()
					+ "::getConnection() caught ClassNotFoundException.");
			cnfe.printStackTrace();
		} catch (SQLException sqle) {
			System.out.println(getClass().getName()
					+ "::getJobOwner() caught SQLException.");
			sqle.printStackTrace();
		}
		return null;
	}

	private void dumpVect(Vector v) {
		System.out.println("vector length: " + v.size());
		for (int i = 0; i < v.size(); i++) {
			if (v.elementAt(i) != null)
				System.out.println("[" + i + "]" + (v.elementAt(i)).toString());
			else
				System.out.println("[" + i + "] NULL");
		}
	}

	private void dumpState() {
		/* Failure Events go here */

		System.out.println(getClass().getName() + "::dumpState() a failure:");
		System.out.println(getClass().getName() + "::dumpState() Events:");
		dumpVect(failureEvents);
		System.out.println(getClass().getName() + "::dumpState() UIDs.");
		dumpVect(failureUIDs);
		System.out.println(getClass().getName() + "::dumpState() Handbacks");
		dumpVect(failureHandbacks);
		System.out.println(getClass().getName() + "::dumpState() JobIDs");
		dumpVect(failureJobIDs);
		System.out.println(getClass().getName() + "::dumpState() sessionIDs");
		dumpVect(failureSessionIDs);

		System.out.println(getClass().getName() + "::dumpState() exception:");
		System.out.println(getClass().getName() + "::dumpState() Events:");
		dumpVect(exceptionEvents);
		System.out.println(getClass().getName() + "::dumpState() UIDs.");
		dumpVect(exceptionUIDs);
		System.out.println(getClass().getName() + "::dumpState() Handbacks");
		dumpVect(exceptionHandbacks);
		System.out.println(getClass().getName() + "::dumpState() JobIDs");
		dumpVect(exceptionJobIDs);
		System.out.println(getClass().getName() + "::dumpState() sessionIDs");
		dumpVect(exceptionSessionIDs);

		System.out.println(getClass().getName() + "::dumpState() information:");
		System.out.println(getClass().getName() + "::dumpState() Events:");
		dumpVect(informationEvents);
		System.out.println(getClass().getName() + "::dumpState() UIDs.");
		dumpVect(informationUIDs);
		System.out.println(getClass().getName() + "::dumpState() Handbacks");
		dumpVect(informationHandbacks);
		System.out.println(getClass().getName() + "::dumpState() JobIDs");
		dumpVect(informationJobIDs);
		System.out.println(getClass().getName() + "::dumpState() sessionIDs");
		dumpVect(informationSessionIDs);

		System.out.println(getClass().getName() + "::dumpState() warning:");
		System.out.println(getClass().getName() + "::dumpState() Events:");
		dumpVect(warningEvents);
		System.out.println(getClass().getName() + "::dumpState() UIDs.");
		dumpVect(warningUIDs);
		System.out.println(getClass().getName() + "::dumpState() Handbacks");
		dumpVect(warningHandbacks);
		System.out.println(getClass().getName() + "::dumpState() JobIDs");
		dumpVect(warningJobIDs);
		System.out.println(getClass().getName() + "::dumpState() sessionIDs");
		dumpVect(warningSessionIDs);

	}

}
