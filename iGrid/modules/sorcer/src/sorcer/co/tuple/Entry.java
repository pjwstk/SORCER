/*
 * Copyright 2012 the original author or authors.
 * Copyright 2012 SorcerSoft.org.
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

package sorcer.co.tuple;

import java.net.URL;
import java.rmi.RemoteException;

import sorcer.core.context.model.var.FidelityInfo;
import sorcer.service.Arg;
import sorcer.service.Context;
import sorcer.service.Evaluation;
import sorcer.service.EvaluationException;

@SuppressWarnings("unchecked")
public class Entry<T> extends Tuple2<String, T> implements Arg, Evaluation<T>, Comparable {
	private static final long serialVersionUID = 5168783170981015779L;
	
	public int index;
	public boolean isPersistant = false;
	public URL datastoreURL;
	public FidelityInfo fidelity;
	
	public Entry() {
	}

	public Entry(String path) {
		_1 = path;
	}
	
	public Entry(String path, T value) {
		T v = value;
		if (v == null)
			v = (T)Context.none;

		_1 = path;
		this._2 = v;
	}
	
	public Entry(String path, T value, int index) {
		T v = value;
		if (v == null)
			v = (T)Context.none;

		_1 = path;
		this._2 = v;
		this.index = index;
	}

	public int index() {
		return index;
	}

	/* (non-Javadoc)
	 * @see sorcer.service.Evaluation#asis()
	 */
	@Override
	public T asis() throws EvaluationException, RemoteException {
		return _2;
	}

	/* (non-Javadoc)
	 * @see sorcer.service.Evaluation#getValue(sorcer.service.Arg[])
	 */
	@Override
	public T getValue(Arg... entries) throws EvaluationException,
			RemoteException {
		return _2;
	}

	/* (non-Javadoc)
	 * @see sorcer.service.Evaluation#substitute(sorcer.service.Arg[])
	 */
	@Override
	public Evaluation<T> substitute(Arg... entries)
			throws EvaluationException, RemoteException {
		for (Arg a : entries) {
			if (a.getName().equals(getName()) && a instanceof Entry) {
				_2 = ((Entry<T>)a).value();
			}
		}
		return this;
	}
	
	@Override
	public int hashCode() {
		int hash = _1.length() + 1;
		return hash = hash * 31 + _1.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if ((object instanceof Entry<?>)
				&& ((Entry<T>) object)._1.equals(_1))
			return true;
		else
			return false;
	}
	@Override
	public String toString() {
		return "[" + _1 + ":" + _2 + ":" + index + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Object o) {
		if (o == null)
			throw new NullPointerException();
		if (o instanceof Entry<?>)
			return _1.compareTo(((Entry<?>) o).getName());
		else
			return -1;
	}

}