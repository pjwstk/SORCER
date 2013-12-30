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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

/***
 * This class is a distributed version of the standard Java Observable class. It
 * uses the exact same API as the Java class.
 * 
 * @see java.util.Observable
 ***/
public class Observable extends UnicastRemoteObject implements RemoteObservable {
	// the object is ready for observer notification
	private boolean changed = false;
	// the list of RemoteObserver objects observing this object
	private Vector observers = new Vector();

	/*********************** Constructors *********************/
	/**
	 * Constructs a new observable
	 */
	public Observable() throws RemoteException {
		super();
	}

	/*************** Attribute accessor methods **************/
	/**
	 * Marks the observable as unchanged.
	 */
	protected synchronized void clearChanged() {
		changed = false;
	}

	/**
	 * @return true if the observable is flagged as changed
	 */
	public synchronized boolean hasChanged() {
		return changed;
	}

	/**
	 * Marks the observable as changed.
	 */
	protected synchronized void setChanged() {
		changed = true;
	}

	/**
	 * @return the number of observers observing this object
	 */
	public synchronized int countObservers() {
		return observers.size();
	}

	/**
	 * Adds a RemoteObserver to the list of objects observing this object.
	 * 
	 * @param ob
	 *            the new RemoteObserver
	 */
	public synchronized void addObserver(RemoteObserver ob) {
		if (!observers.contains(ob)) {
			observers.addElement(ob);
		}
	}

	/**
	 * Removes the specified RemoteObserver from the list of objects being
	 * observed.
	 * 
	 * @param ob
	 *            the RemoteObserver to be removed
	 */
	public synchronized void deleteObserver(RemoteObserver ob) {
		if (observers.contains(ob)) {
			observers.removeElement(ob);
		}
	}

	/**
	 * Clears out the entire observer list.
	 */
	public synchronized void deleteObservers() {
		observers = new Vector();
	}

	/***************** Observer notification *****************/
	/**
	 * Assuming the object has been changed called, this method will notify its
	 * observers with null as an argument.
	 */
	public void notifyObservers() {
		performNotify(null);
	}

	/**
	 * Notifies observers of a change with the specified remote reference as an
	 * argument.
	 * 
	 * @param r
	 *            the remote object to pass to observers
	 */
	public void notifyObservers(Remote r) {
		performNotify(r);
	}

	/**
	 * Notifies observers of a change with the specified serializable object as
	 * an argument.
	 * 
	 * @param s
	 *            the serializable object to send to the observers
	 */
	public void notifyObservers(Serializable s) {
		performNotify(s);
	}

	// This performs actual observer notification.
	// It first copies the observers Vector into an array.
	// This allows it to move out of the synchronized block
	// to avoid dead locks and avoid holding up other threads
	// while it notifies observers across potentially nasty Internet
	// links. For example, if you synchronized the whole block,
	// processing could take several seconds while some calls time
	// out. T`his means another thread coming in and adding to
	// the observer list would wait for this block to release its
	// monitor. Yuck!
	public void performNotify(Object arg) {
		RemoteObserver[] obs;
		int count;

		synchronized (this) {
			if (!hasChanged()) {
				return;
			}
			count = observers.size();
			obs = new RemoteObserver[count];
			observers.copyInto(obs);
			clearChanged();
		}
		for (int i = 0; i < count; i++) {
			if (obs[i] != null) {
				try {
					obs[i].update(this, arg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
