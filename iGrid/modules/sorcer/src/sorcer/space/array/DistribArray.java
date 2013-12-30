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

package sorcer.space.array;

import java.rmi.RemoteException;

import net.jini.core.entry.UnusableEntryException;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionException;
import net.jini.space.JavaSpace;

public class DistribArray {
	private JavaSpace space;
	private String name;

	public DistribArray(JavaSpace space, String name) {
		this.space = space;
		this.name = name;
	}

	/*
	 * public static void main(String[] args) { JavaSpace space =
	 * SpaceAccessor.getSpace();
	 * 
	 * try { String name = "duke's array"; DistribArray array = new
	 * DistribArray(space, name);
	 * 
	 * array.create();
	 * 
	 * for (int i = 0; i < 10; i++) { array.append(new Integer(i)); }
	 * 
	 * System.out.println("Size of array is " + array.size());
	 * 
	 * for (int i = 0; i < 10; i++) { Integer elem =
	 * (Integer)array.readElement(i); System.out.println("Elem " + i + " is " +
	 * elem); } } catch (Exception e) { e.printStackTrace(); } }
	 */

	public void create() throws RemoteException, TransactionException,
			UnusableEntryException, InterruptedException {
		Start start = new Start();
		start.name = name;
		start.position = new Integer(0);

		End end = new End();
		end.name = name;
		end.position = new Integer(0);

		Start startTemplate = new Start();
		startTemplate.name = name;

		End endTemplate = new End();
		endTemplate.name = name;

		Start starte = (Start) space.readIfExists(startTemplate, null,
				Long.MAX_VALUE);
		End ende = (End) space.readIfExists(endTemplate, null, Long.MAX_VALUE);

		if ((starte == null) || (ende == null)) {
			space.write(start, null, Lease.FOREVER);
			space.write(end, null, Lease.FOREVER);
		}
	}

	public int append(Object obj, Transaction txn) throws RemoteException,
			TransactionException, UnusableEntryException, InterruptedException {
		End template = new End();
		template.name = name;

		End end = (End) space.take(template, txn, 0);
		int position = end.increment();
		space.write(end, null, Lease.FOREVER);

		Element element = new Element(name, position, obj);
		space.write(element, txn, Lease.FOREVER);

		return position;
	}

	public int append(Object obj) throws RemoteException, TransactionException,
			UnusableEntryException, InterruptedException {
		return append(obj, null);
	}

	public int size() throws RemoteException, TransactionException,
			UnusableEntryException, InterruptedException {
		Start startTemplate = new Start();
		startTemplate.name = name;

		End endTemplate = new End();
		endTemplate.name = name;

		Start start = (Start) space.read(startTemplate, null, Long.MAX_VALUE);
		End end = (End) space.read(endTemplate, null, Long.MAX_VALUE);

		return (end.position.intValue() - start.position.intValue());
	}

	public Object readElement(int pos) throws RemoteException,
			TransactionException, UnusableEntryException, InterruptedException {
		Element template = new Element(name, pos, null);

		Element element = (Element) space.read(template, null, Long.MAX_VALUE);
		return element.data;
	}

	public Object readElementbyData(Object data) throws RemoteException,
			TransactionException, UnusableEntryException, InterruptedException {
		Element template = new Element(name, data);

		Element element = (Element) space.read(template, null, Long.MAX_VALUE);
		return element.data;
	}

}
