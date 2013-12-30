package sorcer.service;

/*
 * Copyright 2013 the original author or authors.
 * Copyright 2013 SorcerSoft.org.
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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sorcer.co.tuple.Entry;
import sorcer.core.context.model.par.Par;
import sorcer.core.context.model.par.ParException;

/**
 * @author Mike Sobolewski
 */
@SuppressWarnings("rawtypes")
public class ArgSet extends TreeSet<Arg> {
	
	private static final long serialVersionUID = -4662755904016297879L;
	
	public ArgSet() {
		super();
	}

	public ArgSet(ArgList argList) {
		addAll(argList);
	}
	
	public ArgSet(Set<Par> argSet) {
		addAll(argSet);
	}

	
	public ArgSet(ArgList...  argLists) {
		for (ArgList vl : argLists) {
			addAll(vl);
		}
	}
	
	public ArgSet(Arg...  args) {
		for (Arg v : args) {
			add(v);
		}
	}
	
	public Arg getArg(String parName) throws ParException {
		for (Arg v : this) {
			if (v.getName().equals(parName))
				return v;
		}
		return null;
	}
	
	public void setValue(String parName, Object value)
			throws EvaluationException {
		Arg par = null;
		for (Arg p : this) {
			if (p.getName().equals(parName)) {
				par = p;
				if (par instanceof Setter)
					try {
						((Setter)par).setValue(value);
					} catch (RemoteException e) {
						throw new EvaluationException(e);
					}
				break;
			}
		}
		if (par == null)
			throw new ParException("No such Par in the list: " + parName);
	}
	
	public ArgList selectArgs(List<String>... parnames) {
		List<String> allParNames = new ArrayList<String>();
		for (List<String> nl : parnames) {
			allParNames.addAll(nl);
		}
		ArgList out = new ArgList();
		for (Arg v : this) {
			if (allParNames.contains(v.getName())) {
				out.add(v);
			}
		}
		return out;
	}
	
	public ArgSet selectArgs(String... parnames) {
		List<String> vnames = Arrays.asList(parnames);
		ArgSet out = new ArgSet();
		for (Arg v : this) {
			if (vnames.contains(v.getName())) {
				out.add(v);
			}
		}
		return out;
	}

	@Override
	public boolean contains(Object obj) {
		if (!(obj instanceof Par<?>))
			return false;
		else {
			for (Arg v : this) {
				if (v.getName().equals(((Par<?>)obj).getName()))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean remove(Object obj) {
		if (obj == null || !(obj instanceof Par<?>)) {
			return false;
		} else {
			for (Arg v : this) {
				if (v.getName().equals(((Par<?>) obj).getName())) {
					super.remove(v);
					return true;
				}
			}
		}
		return false;
	}
	
	 public List<String> getNames() {
		 List<String> names = new ArrayList<String>(size());
		 Iterator<Arg> i = iterator();
		 while (i.hasNext()) {
			 names.add(i.next().getName());
		 }
		 return names;
	 }
	 
	 public List<Object> getValues() throws EvaluationException, RemoteException {
		 List<Object> values = new ArrayList<Object>(size());
		 Iterator<Arg> i = iterator();
		 while (i.hasNext()) {
			 Object val = i.next();
			 if (val instanceof Evaluation) {
				 values.add(((Evaluation)val).getValue());
			 } else
				 values.add(null);
		 }
		 return values;
	 }
	 
	 public Arg[] toArray() {
		 Arg[] va = new Arg[size()];
		 return toArray(va);
	 }
			
	 public ArgList toList() {
		 ArgList vl = new ArgList(size());
		 for (Arg v : this)
			 vl.add(v);
		 return vl;
	 }

	public static ArgSet asSet(Map map) {
		ArgSet as = new ArgSet();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry) it.next();
			as.add(new Entry((String) e.getKey(), e.getValue()));
		}
		return as;
	}

	public static ArgSet asSet(ArgList list) {
		return new ArgSet(list);
	}

	public static ArgList asList(Arg[] array) {
		ArgList vl = new ArgList(array.length);
		for (Arg v : array)
			vl.add(v);
		return vl;
	}
	 
	public static ArgSet asSet(Arg[] array) {
		ArgSet vl = new ArgSet();
		for (Arg v : array)
			vl.add(v);
		return vl;
	}

	public void clearArgs() throws EvaluationException {
		for (Arg p : this) {
			try {
				if (p instanceof Setter)
					((Setter) p).setValue(null);
			} catch (RemoteException e) {
				throw new EvaluationException(e);
			}
		}
	}
}
