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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Captures the classic memento pattern for Java. The memento pattern decouples
 * a business object from its state so that systems like the lightweight
 * persistence engine can manage storage and retrieval of an object's state to
 * and from a data store.
 */
public class Memento implements Serializable {
	/**
	 * The bitmask meaning an attribute should not be saved.
	 */
	static public final int NOSAVE = (Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT);

	static public final String NULL = "null";

	/**
	 * Determines whether or not a given field should be saved. A field should
	 * not be saved if it is final, static, or transient.
	 * 
	 * @param f
	 *            the field to be tested
	 * @return true if the field should be saved
	 */
	static public boolean isSaved(Field f) {
		int mod = f.getModifiers();

		if ((mod & Memento.NOSAVE) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The values representing the state of the object behind this memento.
	 * 
	 * @serial
	 */
	private Hashtable values = new Hashtable();

	/**
	 * Constructs a new, empty memento.
	 */
	public Memento() {
		super();
	}

	/**
	 * Constructs a memento representing the state of the specified object.
	 * 
	 * @param ob
	 *            the object to be represented
	 */
	public Memento(Object ob) {
		super();
		{
			Class cls = ob.getClass();

			while (!cls.equals(Object.class)) {
				Field[] attrs = cls.getDeclaredFields();
				Hashtable map = new Hashtable();

				values.put(cls, map);
				for (int i = 0; i < attrs.length; i++) {
					if (isSaved(attrs[i])) {
						try {
							if (attrs[i].get(ob) != null)
								map.put(attrs[i].getName(), attrs[i].get(ob));
							else
								map.put(attrs[i].getName(), NULL);
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
				}
				cls = cls.getSuperclass();
			}
		}
	}

	/**
	 * Provides the value for the attribute of the specified class.
	 * 
	 * @param cls
	 *            the class in which the attribute is declared
	 * @param attr
	 *            the name of the attribute
	 * @return the value of the attribute
	 */
	public Object get(Class cls, String attr) {
		Hashtable map;

		if (!values.containsKey(cls)) {
			return null;
		}
		map = (Hashtable) values.get(cls);
		if (map.get(attr).equals(NULL))
			return null;

		return map.get(attr);
	}

	/**
	 * Maps the values currently in the memento to the object in question.
	 * 
	 * @param ob
	 *            the object who should be assigned values from the memento
	 * @throws java.lang.NoSuchFieldException
	 *             the object in question does not have a field for one of the
	 *             memento values
	 */
	public void map(Object ob) throws NoSuchFieldException {
		Enumeration keys = values.keys();

		while (keys.hasMoreElements()) {
			Class cls = (Class) keys.nextElement();
			Hashtable vals = (Hashtable) values.get(cls);
			Enumeration attrs = vals.keys();

			while (attrs.hasMoreElements()) {
				String attr = (String) attrs.nextElement();
				Object val = vals.get(attr);
				Field f = cls.getDeclaredField(attr);

				try {
					if (!val.equals(NULL))
						f.set(ob, val);
					else
						f.set(ob, null);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Places the specified value into the memento based on the field's
	 * declaring class and name.
	 * 
	 * @param cls
	 *            the class in which the field is declared
	 * @param attr
	 *            the name of the attribute
	 * @param val
	 *            the value being stored
	 */
	public void put(Class cls, String attr, Object val) {
		Hashtable map;

		if (values.containsKey(cls)) {
			map = (Hashtable) values.get(cls);
		} else {
			map = new Hashtable();
			values.put(cls, map);
		}
		map.put(attr, val);
	}
}
